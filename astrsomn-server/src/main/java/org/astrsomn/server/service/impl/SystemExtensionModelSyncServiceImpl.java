package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.dto.model.AiModelCreateRequestDTO;
import org.astrsomn.core.common.entity.AiInstanceEntity;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.entity.SystemExtensionEntity;
import org.astrsomn.core.common.langchain.extension.ModelProviderHandler;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.mapper.AiInstanceMapper;
import org.astrsomn.core.mapper.AiModelMapper;
import org.astrsomn.server.service.AiModelService;
import org.astrsomn.server.service.SystemExtensionModelSyncService;
import org.astrsomn.server.service.SystemExtensionService;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.astrsomn.starter.context.EnvRuntime;
import org.astrsomn.starter.langchain.factory.AstroModelFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SystemExtensionModelSyncServiceImpl implements SystemExtensionModelSyncService {

    private final SystemExtensionService systemExtensionService;
    private final AstroModelFactory astroModelFactory;
    private final AiModelService aiModelService;
    private final AiModelMapper aiModelMapper;
    private final AiInstanceMapper aiInstanceMapper;
    private final QueryEnvParamHelper queryEnvParamHelper;
    private final AstrsomnProperties astrsomnProperties;

    @Override
    public BaseResponse<String> loadModels(Long extensionId) {
        SystemExtensionEntity ext = systemExtensionService.getById(extensionId);
        if (ext == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        if (!SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER.getCode().equals(ext.getType())) {
            return BaseResponse.fail("仅模型类扩展支持加载模型", null);
        }

        String providerCode = resolveProviderCode(ext);
        if (providerCode == null) {
            return BaseResponse.fail("无法解析 providerCode / extensionKey", null);
        }
        if (findProviderEnum(providerCode).isEmpty()) {
            return BaseResponse.fail("非法的厂商代码: " + providerCode, null);
        }

        Optional<ModelProviderHandler> handlerOpt = astroModelFactory.getHandler(providerCode);
        if (handlerOpt.isEmpty()) {
            return BaseResponse.fail("当前运行时未加载该厂商的 ModelProviderHandler（SPI）: " + providerCode, null);
        }

        String envCode = effectiveEnvCode();
        if (StringUtils.isBlank(envCode)) {
            return BaseResponse.fail("无法解析当前环境 envCode", null);
        }

        List<AiModelEntity> available = handlerOpt.get().getAvailableModels();
        if (available == null || available.isEmpty()) {
            return BaseResponse.success("厂商未返回可用模型清单");
        }

        int added = 0;
        int skipped = 0;
        for (AiModelEntity src : available) {
            String modelKey = StringUtils.trimToNull(src.getModelKey());
            if (modelKey == null) {
                skipped++;
                continue;
            }
            String rowProvider = StringUtils.trimToNull(src.getProvider());
            if (rowProvider == null) {
                rowProvider = providerCode;
            }

            long exists = aiModelMapper.selectCount(
                    new LambdaQueryWrapper<AiModelEntity>()
                            .eq(AiModelEntity::getModelKey, modelKey)
                            .eq(AiModelEntity::getProvider, rowProvider)
                            .eq(AiModelEntity::getEnvCode, envCode)
                            .eq(AiModelEntity::getDeleted, false));
            if (exists > 0) {
                skipped++;
                continue;
            }

            AiModelCreateRequestDTO dto = new AiModelCreateRequestDTO();
            BeanUtils.copyProperties(src, dto);
            dto.setId(null);
            dto.setModelKey(modelKey);
            dto.setProvider(rowProvider);
            dto.setEnvCode(envCode);

            BaseResponse<String> created = aiModelService.create(dto);
            if (created.isSuccess()) {
                added++;
            } else {
                skipped++;
            }
        }

        return BaseResponse.success(String.format("加载完成：新增 %d 条，跳过 %d 条。", added, skipped));
    }

    @Override
    public BaseResponse<String> unloadModels(Long extensionId) {
        SystemExtensionEntity ext = systemExtensionService.getById(extensionId);
        if (ext == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        if (!SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER.getCode().equals(ext.getType())) {
            return BaseResponse.fail("仅模型类扩展支持卸载模型", null);
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

        List<AiModelEntity> rows = aiModelMapper.selectList(
                new LambdaQueryWrapper<AiModelEntity>()
                        .eq(AiModelEntity::getProvider, providerCode)
                        .eq(AiModelEntity::getEnvCode, envCode)
                        .eq(AiModelEntity::getDeleted, false));

        int removed = 0;
        int skipped = 0;
        List<String> blockedKeys = new ArrayList<>();
        for (AiModelEntity row : rows) {
            if (row.getId() == null) {
                continue;
            }
            String mk = StringUtils.trimToNull(row.getModelKey());
            if (mk != null && isModelKeyReferencedByInstance(mk, envCode)) {
                skipped++;
                blockedKeys.add(mk);
                continue;
            }
            boolean ok = aiModelService.removeById(row.getId());
            if (ok) {
                removed++;
            } else {
                skipped++;
            }
        }

        String msg = String.format("卸载完成：删除 %d 条，跳过 %d 条。", removed, skipped);
        if (!blockedKeys.isEmpty()) {
            msg += " 以下模型仍被实例引用未删除: " + String.join(", ", blockedKeys);
        }
        return BaseResponse.success(msg);
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
