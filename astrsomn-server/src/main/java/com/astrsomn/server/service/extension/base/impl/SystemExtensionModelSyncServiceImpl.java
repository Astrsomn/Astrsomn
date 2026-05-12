package com.astrsomn.server.service.extension.base.impl;

import com.astrsomn.starter.runtime.mapper.AstAiInstanceMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.constant.SystemExtensionEnum;
import com.astrsomn.api.runtime.common.dto.extension.ExtensionModelLoadPreviewDTO;
import com.astrsomn.api.runtime.common.dto.extension.ExtensionModelSyncPreviewRowDTO;
import com.astrsomn.api.runtime.common.dto.extension.ExtensionModelUnloadPreviewDTO;
import com.astrsomn.api.runtime.common.entity.AiInstanceEntity;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.astrsomn.api.runtime.common.entity.SystemExtensionEntity;
import com.astrsomn.api.runtime.common.langchain.extension.model.ModelProviderHandler;
import com.astrsomn.common.utils.CollectionUtils;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.api.runtime.exception.SystemExtensionModelSyncErrorEnum;
import com.astrsomn.starter.runtime.mapper.AstAiModelMapper;
import com.astrsomn.server.service.AiModelService;
import com.astrsomn.server.service.extension.base.SystemExtensionModelSyncService;
import com.astrsomn.server.service.extension.base.SystemExtensionService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.astrsomn.starter.runtime.context.EnvRuntime;
import com.astrsomn.starter.runtime.langchain.factory.AstroModelFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SystemExtensionModelSyncServiceImpl implements SystemExtensionModelSyncService {

    private final SystemExtensionService systemExtensionService;
    private final AstroModelFactory astroModelFactory;
    private final AiModelService aiModelService;
    private final AstAiModelMapper aiModelMapper;
    private final AstAiInstanceMapper aiInstanceMapper;
    private final QueryEnvParamHelper queryEnvParamHelper;
    private final AstrsomnProperties astrsomnProperties;

    private record ProviderEnv(String extensionCode, String envCode) {}

    private record LoadSyncContext(String extensionCode, String envCode, ModelProviderHandler handler) {}

    @Override
    public BaseResponse<String> loadModels(Long extensionId, String modelKeys) {
        LoadSyncContext ctx = resolveLoadSyncContext(extensionId);

        List<AiModelEntity> available = ctx.handler().getAvailableModels();
        if (CollectionUtils.isEmpty(available)) {
            return BaseResponse.success("厂商未返回可用模型清单");
        }

        // 解析选中的模型键
        Set<String> selectedModelKeys = new HashSet<>();
        if (StringUtils.isNotBlank(modelKeys)) {
            selectedModelKeys.addAll(Arrays.asList(modelKeys.split(",")));
        }

        int added = 0;
        int skipped = 0;
        for (AiModelEntity src : available) {
            String modelKey = StringUtils.trimToNull(src.getModelKey());
            if (modelKey == null) {
                skipped++;
                continue;
            }
            
            // 如果指定了模型键，且当前模型不在选中列表中，则跳过
            if (!selectedModelKeys.isEmpty() && !selectedModelKeys.contains(modelKey)) {
                continue;
            }
            
            String rowProvider = StringUtils.trimToNull(src.getExtensionCode());
            if (rowProvider == null) {
                rowProvider = ctx.extensionCode();
            }

            long exists = aiModelMapper.selectCount(
                    new LambdaQueryWrapper<AiModelEntity>()
                            .eq(AiModelEntity::getModelKey, modelKey)
                            .eq(AiModelEntity::getExtensionCode, rowProvider)
                            .eq(AiModelEntity::getEnvCode, ctx.envCode())
                            .eq(AiModelEntity::getDeleted, false));
            if (exists > 0) {
                skipped++;
                continue;
            }

            AiModelEntity dto = new AiModelEntity();
            BeanUtils.copyProperties(src, dto);
            dto.setId(null);
            dto.setModelKey(modelKey);
            dto.setExtensionCode(rowProvider);
            dto.setEnvCode(ctx.envCode());
            dto.setStatus(AiModelEnum.StatusEnum.ENABLED.getCode());
            boolean result = aiModelService.save(dto);

            if (result) {
                added++;
            } else {
                skipped++;
            }
        }

        return BaseResponse.success(String.format("加载完成：新增 %d 条，跳过 %d 条。", added, skipped));
    }

    @Override
    public BaseResponse<String> unloadModels(Long extensionId, String modelKeys) {
        ProviderEnv pe = resolveExtensionProviderEnv(extensionId);

        List<AiModelEntity> rows = aiModelMapper.selectList(
                new LambdaQueryWrapper<AiModelEntity>()
                        .eq(AiModelEntity::getExtensionCode, pe.extensionCode())
                        .eq(AiModelEntity::getEnvCode, pe.envCode())
                        .eq(AiModelEntity::getDeleted, false));

        // 解析选中的模型键
        Set<String> selectedModelKeys = new HashSet<>();
        if (StringUtils.isNotBlank(modelKeys)) {
            selectedModelKeys.addAll(Arrays.asList(modelKeys.split(",")));
        }

        int removed = 0;
        int skipped = 0;
        List<String> blockedKeys = new ArrayList<>();
        for (AiModelEntity row : rows) {
            if (row.getId() == null) {
                continue;
            }
            String mk = StringUtils.trimToNull(row.getModelKey());
            if (mk == null) {
                skipped++;
                continue;
            }
            
            // 如果指定了模型键，且当前模型不在选中列表中，则跳过
            if (!selectedModelKeys.isEmpty() && !selectedModelKeys.contains(mk)) {
                continue;
            }
            
            if (isModelKeyReferencedByInstance(mk, pe.envCode())) {
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

    @Override
    public BaseResponse<ExtensionModelLoadPreviewDTO> previewLoadModels(Long extensionId) {
        LoadSyncContext ctx = resolveLoadSyncContext(extensionId);

        ExtensionModelLoadPreviewDTO dto = new ExtensionModelLoadPreviewDTO();
        List<AiModelEntity> available = ctx.handler().getAvailableModels();
        if (available == null || available.isEmpty()) {
            return BaseResponse.success(dto);
        }

        for (AiModelEntity src : available) {
            String modelKey = StringUtils.trimToNull(src.getModelKey());
            if (modelKey == null) {
                dto.setSkippedInvalidCount(dto.getSkippedInvalidCount() + 1);
                continue;
            }
            String rowProvider = StringUtils.trimToNull(src.getExtensionCode());
            if (rowProvider == null) {
                rowProvider = ctx.extensionCode();
            }

            long exists = aiModelMapper.selectCount(
                    new LambdaQueryWrapper<AiModelEntity>()
                            .eq(AiModelEntity::getModelKey, modelKey)
                            .eq(AiModelEntity::getExtensionCode, rowProvider)
                            .eq(AiModelEntity::getEnvCode, ctx.envCode())
                            .eq(AiModelEntity::getDeleted, false));
            ExtensionModelSyncPreviewRowDTO row = toPreviewRow(src, rowProvider);
            if (exists > 0) {
                dto.getSkippedExisting().add(row);
            } else {
                dto.getToCreate().add(row);
            }
        }
        return BaseResponse.success(dto);
    }

    @Override
    public BaseResponse<ExtensionModelUnloadPreviewDTO> previewUnloadModels(Long extensionId) {
        ProviderEnv pe = resolveExtensionProviderEnv(extensionId);

        ExtensionModelUnloadPreviewDTO dto = new ExtensionModelUnloadPreviewDTO();
        List<AiModelEntity> rows = aiModelMapper.selectList(
                new LambdaQueryWrapper<AiModelEntity>()
                        .eq(AiModelEntity::getExtensionCode, pe.extensionCode())
                        .eq(AiModelEntity::getEnvCode, pe.envCode())
                        .eq(AiModelEntity::getDeleted, false));

        for (AiModelEntity row : rows) {
            if (row.getId() == null) {
                continue;
            }
            ExtensionModelSyncPreviewRowDTO previewRow = toPreviewRow(row, pe.extensionCode());
            String mk = StringUtils.trimToNull(row.getModelKey());
            if (mk != null && isModelKeyReferencedByInstance(mk, pe.envCode())) {
                dto.getKeptReferenced().add(previewRow);
            } else {
                dto.getToRemove().add(previewRow);
            }
        }
        return BaseResponse.success(dto);
    }

    private ProviderEnv resolveExtensionProviderEnv(Long extensionId) {
        if (extensionId == null) {
            throw new BusinessException(SystemExtensionModelSyncErrorEnum.SYNC_PARAM_ERROR);
        }
        SystemExtensionEntity ext = systemExtensionService.getById(extensionId);
        if (ext == null) {
            throw new BusinessException(SystemExtensionModelSyncErrorEnum.EXTENSION_NOT_FOUND);
        }
        if (!SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER.getCode().equals(ext.getType())) {
            throw new BusinessException(SystemExtensionModelSyncErrorEnum.SYNC_PARAM_ERROR);
        }

        String extensionCode = resolveExtensionCode(ext);
        if (extensionCode == null) {
            throw new BusinessException(SystemExtensionModelSyncErrorEnum.SYNC_PARAM_ERROR);
        }
        if (findProviderEnum(extensionCode).isEmpty()) {
            throw new BusinessException(SystemExtensionModelSyncErrorEnum.SYNC_PARAM_ERROR);
        }

        String envCode = effectiveEnvCode();
        if (StringUtils.isBlank(envCode)) {
            throw new BusinessException(SystemExtensionModelSyncErrorEnum.SYNC_PARAM_ERROR);
        }

        return new ProviderEnv(extensionCode, envCode);
    }

    private LoadSyncContext resolveLoadSyncContext(Long extensionId) {
        ProviderEnv pe = resolveExtensionProviderEnv(extensionId);

        Optional<ModelProviderHandler> handlerOpt = astroModelFactory.getHandler(pe.extensionCode());
        if (!handlerOpt.isPresent()) {
            throw new BusinessException(SystemExtensionModelSyncErrorEnum.SYNC_FAILED, "当前运行时未加载该厂商的 ModelProviderHandler（SPI）: " + pe.extensionCode());
        }
        return new LoadSyncContext(pe.extensionCode(), pe.envCode(), handlerOpt.get());
    }

    private static ExtensionModelSyncPreviewRowDTO toPreviewRow(AiModelEntity src, String fallbackProvider) {
        ExtensionModelSyncPreviewRowDTO row = new ExtensionModelSyncPreviewRowDTO();
        row.setModelKey(src.getModelKey());
        row.setModelName(src.getModelName());
        row.setModelType(src.getModelType());
        String p = StringUtils.trimToNull(src.getExtensionCode());
        row.setProvider(p != null ? p : fallbackProvider);
        return row;
    }

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
