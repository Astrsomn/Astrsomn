package com.astrsomn.server.service.ai.extension;

import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.entity.AiInstanceEntity;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.utils.StringUtils;

import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.astrsomn.starter.runtime.context.EnvRuntime;
import com.astrsomn.starter.runtime.mapper.AstAiInstanceMapper;
import com.astrsomn.starter.runtime.mapper.AstAiModelMapper;
import com.astrsomn.starter.runtime.system.mapper.AstSystemExtensionMapper;
import com.astrsomn.system.constant.SystemExtensionEnum;
import com.astrsomn.system.entity.SystemExtensionEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class SystemExtensionModelGuard {

    private final AstSystemExtensionMapper astSystemExtensionMapper;
    private final AstAiModelMapper aiModelMapper;
    private final AstAiInstanceMapper aiInstanceMapper;

    private final AstrsomnProperties astrsomnProperties;

    private static String resolveExtensionCode(SystemExtensionEntity ext) {
        String fromCol = StringUtils.trimToNull(ext.getExtensionCode());
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


    public BaseResponse<String> disableAllModelsForExtension(Long extensionId) {
        BaseResponse<ProviderEnv> resolved = resolveProviderEnv(extensionId);
        if (!resolved.isSuccess() || resolved.getData() == null) {
            return BaseResponse.fail(resolved.getMessage(), null);
        }
        ProviderEnv pe = resolved.getData();

        LambdaUpdateWrapper<AiModelEntity> uw = new LambdaUpdateWrapper<AiModelEntity>()
                .eq(AiModelEntity::getExtensionCode, pe.extensionCode())
                .eq(AiModelEntity::getEnvCode, pe.envCode())
                .eq(AiModelEntity::getDeleted, false)
                .set(AiModelEntity::getStatus, AiModelEnum.StatusEnum.DISABLED.getCode());

        int rows = aiModelMapper.update(null, uw);
        return BaseResponse.success(String.format("已将 %d 条模型状态设为停用（disabled）。", rows));
    }


    public BaseResponse<Void> assertNoAiModelsForProviderExtension(Long extensionId) {
        SystemExtensionEntity ext = astSystemExtensionMapper.selectById(extensionId);
        if (ext == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        if (!SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER.getCode().equals(ext.getType())) {
            return BaseResponse.success(null);
        }

        String extensionCode = resolveExtensionCode(ext);
        if (StringUtils.isBlank(extensionCode)) {
            return BaseResponse.fail("无法解析 extensionCode / extensionKey，无法校验模型表", null);
        }
        String envCode = effectiveEnvCode();
        if (StringUtils.isBlank(envCode)) {
            return BaseResponse.fail("无法解析当前环境 envCode", null);
        }

        Long count =
                aiModelMapper.selectCount(
                        new LambdaQueryWrapper<AiModelEntity>()
                                .eq(AiModelEntity::getExtensionCode, extensionCode.trim())
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


    public BaseResponse<Void> assertNoInstancesUseProviderModels(Long extensionId) {
        SystemExtensionEntity ext = astSystemExtensionMapper.selectById(extensionId);
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
                        .eq(AiModelEntity::getExtensionCode, pe.extensionCode())
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
        SystemExtensionEntity ext = astSystemExtensionMapper.selectById(extensionId);
        if (ext == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        if (!SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER.getCode().equals(ext.getType())) {
            return BaseResponse.fail("仅模型类扩展支持该操作", null);
        }

        String extensionCode = resolveExtensionCode(ext);
        if (extensionCode == null) {
            return BaseResponse.fail("无法解析 extensionCode / extensionKey", null);
        }
        if (findProviderEnum(extensionCode).isEmpty()) {
            return BaseResponse.fail("非法的厂商代码: " + extensionCode, null);
        }

        String envCode = effectiveEnvCode();
        if (StringUtils.isBlank(envCode)) {
            return BaseResponse.fail("无法解析当前环境 envCode", null);
        }

        return BaseResponse.success(new ProviderEnv(extensionCode, envCode));
    }

    private String effectiveEnvCode() {

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

    private record ProviderEnv(String extensionCode, String envCode) {
    }
}
