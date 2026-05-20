package com.astrsomn.server.service.ai;

import com.astrsomn.api.runtime.common.dto.model.AiModelCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.model.AiModelQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.model.AiModelResponseDTO;
import com.astrsomn.api.runtime.common.dto.model.AiModelUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiInstanceEntity;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.runtime.exception.AiModelErrorEnum;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.mapper.AiModelMapper;
import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.astrsomn.starter.runtime.context.EnvRuntime;
import com.astrsomn.starter.runtime.mapper.AstAiInstanceMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiModelServiceImpl extends ServiceImpl<AiModelMapper, AiModelEntity> implements AiModelService {

    private static final String DEFAULT_YES = "Y";
    private static final String DEFAULT_NO = "N";
    private static final String INSTANCE_KEY_PREFIX = "INS-";
    private static final String RANDOM_KEY_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int RANDOM_KEY_LENGTH = 8;
    private static final int INSTANCE_KEY_MAX_RETRY = 10;

    private final AstrsomnProperties astrsomnProperties;
    private final AstAiInstanceMapper aiInstanceMapper;


    @Override
    public BaseResponse<String> delete(long[] longIds) {
        List<Long> ids = Arrays.stream(longIds).boxed().toList();
        List<AiModelEntity> models = listByIds(ids);

        boolean result = removeByIds(ids);
        if (!result) {
            throw new BusinessException(AiModelErrorEnum.MODEL_DELETE_FAILED);
        }
        return BaseResponse.success("success");
    }

    @Override
    public BaseResponse<String> generateInstances(long[] modelIds) {
        if (modelIds == null || modelIds.length == 0) {
            return BaseResponse.success("成功生成0个实例");
        }
        List<Long> ids = Arrays.stream(modelIds).boxed().toList();
        List<AiModelEntity> models = listByIds(ids);
        if (models.isEmpty()) {
            return BaseResponse.success("成功生成0个实例");
        }
        Map<Long, AiModelEntity> modelMap = models.stream()
                .filter(model -> model.getId() != null)
                .collect(Collectors.toMap(AiModelEntity::getId, Function.identity(), (left, right) -> left));
        int created = 0;
        for (Long modelId : ids) {
            AiModelEntity model = modelMap.get(modelId);
            if (model == null || StringUtils.isBlank(model.getModelKey())) {
                continue;
            }
            AiInstanceEntity instance = new AiInstanceEntity();
            instance.setInstanceName(StringUtils.isBlank(model.getModelName()) ? model.getModelKey() : model.getModelName());
            instance.setInstanceKey(generateUniqueInstanceKey(model.getEnvCode()));
            instance.setModelKey(model.getModelKey());
            instance.setStatus("enabled");
            instance.setEnvCode(model.getEnvCode());
            int inserted = aiInstanceMapper.insert(instance);
            if (inserted <= 0) {
                throw new BusinessException(AiModelErrorEnum.MODEL_CREATE_FAILED);
            }
            created++;
        }
        return BaseResponse.success("成功生成" + created + "个实例");
    }

    @Override
    public PageResponse<AiModelResponseDTO> queryPage(BasePageRequest<AiModelQueryRequestDTO> request) {
        IPage<AiModelResponseDTO> page = PageUtils.buildPage(request);
        AiModelQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiModelQueryRequestDTO();
        }
        IPage<AiModelResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }

    @Override
    public BaseResponse<AiModelResponseDTO> detail(Long longId) {
        AiModelResponseDTO responseDTO = baseMapper.selectModelWithReferenceStatus(longId);
        if (responseDTO == null) {
            throw new BusinessException(AiModelErrorEnum.MODEL_NOT_FOUND);
        }
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> updateModel(AiModelUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AiModelErrorEnum.MODEL_PARAM_ERROR);
        }
        AiModelEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AiModelErrorEnum.MODEL_NOT_FOUND);
        }
        AiModelEntity entity = new AiModelEntity();
        BeanUtils.copyProperties(request, entity);
        if (StringUtils.isBlank(entity.getModelType())) {
            entity.setModelType(existing.getModelType());
        }
        if (StringUtils.isBlank(entity.getEnvCode())) {
            entity.setEnvCode(StringUtils.isBlank(existing.getEnvCode())
                    ? EnvRuntime.resolveEffectiveEnvCode(astrsomnProperties)
                    : existing.getEnvCode());
        }
        if (entity.getIsDefault() == null) {
            entity.setIsDefault(DEFAULT_NO);
        }


        if (aiInstanceMapper.selectCount(
                new LambdaQueryWrapper<AiInstanceEntity>()
                        .eq(AiInstanceEntity::getModelKey, existing.getModelKey().trim())
                        .eq(AiInstanceEntity::getEnvCode, existing.getEnvCode().trim())) > 0) {
            entity.setModelKey(existing.getModelKey());
        }

        ensureSingleDefaultModelPerType(entity.getEnvCode(), entity.getModelType(), entity.getIsDefault(), entity.getId());
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AiModelErrorEnum.MODEL_UPDATE_FAILED);
        }
        return BaseResponse.success("success");
    }

    @Override
    public BaseResponse<String> create(AiModelCreateRequestDTO request) {
        AiModelEntity entity = new AiModelEntity();
        BeanUtils.copyProperties(request, entity);
        if (StringUtils.isBlank(entity.getEnvCode())) {
            entity.setEnvCode(EnvRuntime.resolveEffectiveEnvCode(astrsomnProperties));
        }
        if (entity.getIsDefault() == null) {
            entity.setIsDefault(DEFAULT_NO);
        }

        ensureSingleDefaultModelPerType(entity.getEnvCode(), entity.getModelType(), entity.getIsDefault(), null);

        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AiModelErrorEnum.MODEL_CREATE_FAILED);
        }
        return BaseResponse.success("success");
    }

    private String generateUniqueInstanceKey(String envCode) {
        for (int i = 0; i < INSTANCE_KEY_MAX_RETRY; i++) {
            String candidate = INSTANCE_KEY_PREFIX + randomUpperString(RANDOM_KEY_LENGTH);
            if (!existsInstanceKeyInEnv(candidate, envCode)) {
                return candidate;
            }
        }
        throw new BusinessException(AiModelErrorEnum.MODEL_CREATE_FAILED);
    }

    private String randomUpperString(int length) {
        StringBuilder builder = new StringBuilder(length);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            int idx = random.nextInt(RANDOM_KEY_CHARS.length());
            builder.append(RANDOM_KEY_CHARS.charAt(idx));
        }
        return builder.toString();
    }

    private boolean existsInstanceKeyInEnv(String instanceKey, String envCode) {
        LambdaQueryWrapper<AiInstanceEntity> wrapper = new LambdaQueryWrapper<AiInstanceEntity>()
                .eq(AiInstanceEntity::getInstanceKey, instanceKey);
        if (StringUtils.isBlank(envCode)) {
            wrapper.and(w -> w.isNull(AiInstanceEntity::getEnvCode).or().eq(AiInstanceEntity::getEnvCode, ""));
        } else {
            wrapper.eq(AiInstanceEntity::getEnvCode, envCode.trim());
        }
        return aiInstanceMapper.selectCount(wrapper) > 0;
    }

    private void ensureSingleDefaultModelPerType(String envCode, String modelType, String isDefault, Long excludeId) {
        if (!Objects.equals(isDefault, DEFAULT_YES)) {
            return;
        }
        if (StringUtils.isBlank(modelType)) {
            throw new BusinessException(AiModelErrorEnum.MODEL_PARAM_ERROR, "默认模型必须指定模型类型");
        }
        if (StringUtils.isBlank(envCode)) {
            throw new BusinessException(AiModelErrorEnum.MODEL_PARAM_ERROR, "默认模型必须指定环境编码");
        }
        LambdaUpdateWrapper<AiModelEntity> clearDefaultWrapper = new LambdaUpdateWrapper<AiModelEntity>()
                .eq(AiModelEntity::getEnvCode, envCode.trim())
                .eq(AiModelEntity::getModelType, modelType.trim())
                .eq(AiModelEntity::getIsDefault, DEFAULT_YES)
                .set(AiModelEntity::getIsDefault, DEFAULT_NO);
        if (excludeId != null) {
            clearDefaultWrapper.ne(AiModelEntity::getId, excludeId);
        }
        update(clearDefaultWrapper);
    }


}
