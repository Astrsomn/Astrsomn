package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.dto.extension.ExtensionModelLoadPreviewDTO;
import org.astrsomn.core.common.dto.extension.ExtensionModelSyncPreviewRowDTO;
import org.astrsomn.core.common.dto.extension.ExtensionModelUnloadPreviewDTO;
import org.astrsomn.core.common.dto.model.AiModelCreateRequestDTO;
import org.astrsomn.core.common.entity.AiInstanceEntity;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.entity.SystemExtensionEntity;
import org.astrsomn.core.common.langchain.extension.ModelProviderHandler;
import org.astrsomn.core.common.util.CollectionUtils;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.exception.base.BusinessException;
import org.astrsomn.core.exception.constant.SystemExtensionModelSyncErrorEnum;
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

import java.util.*;

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

    private record ProviderEnv(String providerCode, String envCode) {}

    private record LoadSyncContext(String providerCode, String envCode, ModelProviderHandler handler) {}

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
            
            String rowProvider = StringUtils.trimToNull(src.getProvider());
            if (rowProvider == null) {
                rowProvider = ctx.providerCode();
            }

            long exists = aiModelMapper.selectCount(
                    new LambdaQueryWrapper<AiModelEntity>()
                            .eq(AiModelEntity::getModelKey, modelKey)
                            .eq(AiModelEntity::getProvider, rowProvider)
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
            dto.setProvider(rowProvider);
            dto.setEnvCode(ctx.envCode());

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
                        .eq(AiModelEntity::getProvider, pe.providerCode())
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
            String rowProvider = StringUtils.trimToNull(src.getProvider());
            if (rowProvider == null) {
                rowProvider = ctx.providerCode();
            }

            long exists = aiModelMapper.selectCount(
                    new LambdaQueryWrapper<AiModelEntity>()
                            .eq(AiModelEntity::getModelKey, modelKey)
                            .eq(AiModelEntity::getProvider, rowProvider)
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
                        .eq(AiModelEntity::getProvider, pe.providerCode())
                        .eq(AiModelEntity::getEnvCode, pe.envCode())
                        .eq(AiModelEntity::getDeleted, false));

        for (AiModelEntity row : rows) {
            if (row.getId() == null) {
                continue;
            }
            ExtensionModelSyncPreviewRowDTO previewRow = toPreviewRow(row, pe.providerCode());
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

        String providerCode = resolveProviderCode(ext);
        if (providerCode == null) {
            throw new BusinessException(SystemExtensionModelSyncErrorEnum.SYNC_PARAM_ERROR);
        }
        if (findProviderEnum(providerCode).isEmpty()) {
            throw new BusinessException(SystemExtensionModelSyncErrorEnum.SYNC_PARAM_ERROR);
        }

        String envCode = effectiveEnvCode();
        if (StringUtils.isBlank(envCode)) {
            throw new BusinessException(SystemExtensionModelSyncErrorEnum.SYNC_PARAM_ERROR);
        }

        return new ProviderEnv(providerCode, envCode);
    }

    private LoadSyncContext resolveLoadSyncContext(Long extensionId) {
        ProviderEnv pe = resolveExtensionProviderEnv(extensionId);

        Optional<ModelProviderHandler> handlerOpt = astroModelFactory.getHandler(pe.providerCode());
        if (!handlerOpt.isPresent()) {
            throw new BusinessException(SystemExtensionModelSyncErrorEnum.SYNC_FAILED, "当前运行时未加载该厂商的 ModelProviderHandler（SPI）: " + pe.providerCode());
        }
        return new LoadSyncContext(pe.providerCode(), pe.envCode(), handlerOpt.get());
    }

    private static ExtensionModelSyncPreviewRowDTO toPreviewRow(AiModelEntity src, String fallbackProvider) {
        ExtensionModelSyncPreviewRowDTO row = new ExtensionModelSyncPreviewRowDTO();
        row.setModelKey(src.getModelKey());
        row.setModelName(src.getModelName());
        row.setModelType(src.getModelType());
        String p = StringUtils.trimToNull(src.getProvider());
        row.setProvider(p != null ? p : fallbackProvider);
        return row;
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
