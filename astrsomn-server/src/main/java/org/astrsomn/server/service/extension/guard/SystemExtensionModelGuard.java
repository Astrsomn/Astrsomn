package org.astrsomn.server.service.extension.guard;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.entity.AiInstanceEntity;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.entity.SystemExtensionEntity;
import org.astrsomn.core.common.utils.StringUtils;
import org.astrsomn.core.mapper.AiInstanceMapper;
import org.astrsomn.core.mapper.AiModelMapper;
import org.astrsomn.core.mapper.SystemExtensionMapper;
import org.astrsomn.server.service.extension.base.SystemExtensionService;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.astrsomn.starter.context.EnvRuntime;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 模型类扩展：停用厂商下全部模型、卸载前校验实例引用（避免与 {@link SystemExtensionService} 循环依赖）。
 */
@Component
@RequiredArgsConstructor
public class SystemExtensionModelGuard {

    private final SystemExtensionMapper systemExtensionMapper;
    private final AiModelMapper aiModelMapper;
    private final AiInstanceMapper aiInstanceMapper;
    private final QueryEnvParamHelper queryEnvParamHelper;
    private final AstrsomnProperties astrsomnProperties;

    private record ProviderEnv(String providerCode, String envCode) {}

    /**
     * 将当前环境下该扩展对应厂商的全部模型状态设为 disabled。
     */
    public BaseResponse<String> disableAllModelsForExtension(Long extensionId) {
        BaseResponse<ProviderEnv> resolved = resolveProviderEnv(extensionId);
        if (!resolved.isSuccess() || resolved.getData() == null) {
            return BaseResponse.fail(resolved.getMessage(), null);
        }
        ProviderEnv pe = resolved.getData();

        LambdaUpdateWrapper<AiModelEntity> uw = new LambdaUpdateWrapper<AiModelEntity>()
                .eq(AiModelEntity::getProvider, pe.providerCode())
                .eq(AiModelEntity::getEnvCode, pe.envCode())
                .eq(AiModelEntity::getDeleted, false)
                .set(AiModelEntity::getStatus, AiModelEnum.StatusEnum.DISABLED.getCode());

        int rows = aiModelMapper.update(null, uw);
        return BaseResponse.success(String.format("已将 %d 条模型状态设为停用（disabled）。", rows));
    }

    /**
     * 模型类扩展卸载插件前：当前环境下 {@code AI_MODEL} 仍存在该厂商记录则拒绝（需先「卸载模型」清空表内数据）。
     */
    public BaseResponse<Void> assertNoAiModelsForProviderExtension(Long extensionId) {
        SystemExtensionEntity ext = systemExtensionMapper.selectById(extensionId);
        if (ext == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        if (!SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER.getCode().equals(ext.getType())) {
            return BaseResponse.success(null);
        }

        String providerCode = resolveProviderCode(ext);
        if (StringUtils.isBlank(providerCode)) {
            return BaseResponse.fail("无法解析 providerCode / extensionKey，无法校验模型表", null);
        }
        String envCode = effectiveEnvCode();
        if (StringUtils.isBlank(envCode)) {
            return BaseResponse.fail("无法解析当前环境 envCode", null);
        }

        Long count =
                aiModelMapper.selectCount(
                        new LambdaQueryWrapper<AiModelEntity>()
                                .eq(AiModelEntity::getProvider, providerCode.trim())
                                .eq(AiModelEntity::getEnvCode, envCode.trim())
                                .eq(AiModelEntity::getDeleted, false));
        if (count != null && count > 0) {
            return BaseResponse.fail(
                    "当前环境下仍存在该厂商的 AI 模型数据（共 "
                            + count
                            + " 条），请先通过「卸载模型」清空后再卸载插件。",
                    null);
        }
        return BaseResponse.success(null);
    }

    /**
     * 模型类扩展卸载插件前：若有实例引用该厂商任一模型则拒绝。
     */
    public BaseResponse<Void> assertNoInstancesUseProviderModels(Long extensionId) {
        SystemExtensionEntity ext = systemExtensionMapper.selectById(extensionId);
        if (ext == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        if (!SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER.getCode().equals(ext.getType())) {
            return BaseResponse.success(null);
        }

        BaseResponse<ProviderEnv> resolved = resolveProviderEnv(extensionId);
        if (!resolved.isSuccess() || resolved.getData() == null) {
            return BaseResponse.fail(resolved.getMessage(), null);
        }
        ProviderEnv pe = resolved.getData();

        List<AiModelEntity> models = aiModelMapper.selectList(
                new LambdaQueryWrapper<AiModelEntity>()
                        .eq(AiModelEntity::getProvider, pe.providerCode())
                        .eq(AiModelEntity::getEnvCode, pe.envCode())
                        .eq(AiModelEntity::getDeleted, false));

        List<String> blocked = new ArrayList<>();
        for (AiModelEntity m : models) {
            String mk = StringUtils.trimToNull(m.getModelKey());
            if (mk != null && isModelKeyReferencedByInstance(mk, pe.envCode())) {
                blocked.add(mk);
            }
        }
        if (!blocked.isEmpty()) {
            return BaseResponse.fail(
                    "存在 AI 实例正在使用以下模型，无法卸载插件: " + String.join(", ", blocked), null);
        }
        return BaseResponse.success(null);
    }

    private BaseResponse<ProviderEnv> resolveProviderEnv(Long extensionId) {
        if (extensionId == null) {
            return BaseResponse.fail("扩展 ID 不能为空", null);
        }
        SystemExtensionEntity ext = systemExtensionMapper.selectById(extensionId);
        if (ext == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        if (!SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER.getCode().equals(ext.getType())) {
            return BaseResponse.fail("仅模型类扩展支持该操作", null);
        }

        String providerCode = resolveProviderCode(ext);
        if (providerCode == null) {
            return BaseResponse.fail("无法解析 providerCode / extensionKey", null);
        }
        if (findProviderEnum(providerCode).isEmpty()) {
            return BaseResponse.fail("非法的厂商代码: " + providerCode, null);
        }

        String envCode = effectiveEnvCode();
        if (StringUtils.isBlank(envCode)) {
            return BaseResponse.fail("无法解析当前环境 envCode", null);
        }

        return BaseResponse.success(new ProviderEnv(providerCode, envCode));
    }

    private static String resolveProviderCode(SystemExtensionEntity ext) {
        String fromCol = StringUtils.trimToNull(ext.getProviderCode());
        if (fromCol != null) {
            return fromCol;
        }
        return StringUtils.trimToNull(ext.getExtensionKey());
    }

    private static Optional<AiModelEnum.ProviderEnum> findProviderEnum(String code) {
        for (AiModelEnum.ProviderEnum e : AiModelEnum.ProviderEnum.values()) {
            if (e.getCode().equals(code)) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }

    private String effectiveEnvCode() {
        String stamped = queryEnvParamHelper.effectiveEnvCode();
        if (StringUtils.isNotBlank(stamped)) {
            return stamped.trim();
        }
        return StringUtils.trimToNull(EnvRuntime.resolveEffectiveEnvCode(astrsomnProperties));
    }

    private boolean isModelKeyReferencedByInstance(String modelKey, String envCode) {
        if (StringUtils.isBlank(modelKey) || StringUtils.isBlank(envCode)) {
            return false;
        }
        return aiInstanceMapper.selectCount(
                        new LambdaQueryWrapper<AiInstanceEntity>()
                                .eq(AiInstanceEntity::getModelKey, modelKey.trim())
                                .eq(AiInstanceEntity::getEnvCode, envCode.trim()))
                > 0;
    }
}

