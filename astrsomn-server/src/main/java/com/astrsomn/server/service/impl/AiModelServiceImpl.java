package com.astrsomn.server.service.impl;
import com.astrsomn.core.common.utils.PageConverter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.model.AiModelCreateRequestDTO;
import com.astrsomn.core.common.dto.model.AiModelQueryRequestDTO;
import com.astrsomn.core.common.dto.model.AiModelResponseDTO;
import com.astrsomn.core.common.dto.model.AiModelUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiInstanceEntity;
import com.astrsomn.core.common.entity.AiModelEntity;
import com.astrsomn.commn.utils.StringUtils;
import com.astrsomn.commn.base.BusinessException;
import com.astrsomn.core.exception.AiModelErrorEnum;
import com.astrsomn.starter.mapper.AiInstanceMapper;
import com.astrsomn.starter.mapper.AiModelMapper;
import com.astrsomn.server.service.AiModelService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.starter.config.AstrsomnProperties;
import com.astrsomn.starter.context.EnvRuntime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.astrsomn.core.common.utils.PageUtils;
@Service
@RequiredArgsConstructor
public class AiModelServiceImpl extends ServiceImpl<AiModelMapper, AiModelEntity> implements AiModelService {

    private static final String INSTANCE_KEY_PREFIX = "INS-";
    private static final String RANDOM_KEY_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int RANDOM_KEY_LENGTH = 8;
    private static final int INSTANCE_KEY_MAX_RETRY = 10;

    private final AstrsomnProperties astrsomnProperties;
    private final AiInstanceMapper aiInstanceMapper;
    private final QueryEnvParamHelper queryEnvParamHelper;


    @Override
    public BaseResponse<String> delete(long[] longIds) {
        boolean result = removeByIds(Arrays.stream(longIds).boxed().toList());
        if (!result) {
            throw new BusinessException(AiModelErrorEnum.MODEL_DELETE_FAILED);
        }
        return BaseResponse.success("删除成功");
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
        queryEnvParamHelper.stampEffectiveEnv(param);
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
        if (StringUtils.isBlank(entity.getEnvCode())) {
            entity.setEnvCode(EnvRuntime.resolveEffectiveEnvCode(astrsomnProperties));
        }


        if (aiInstanceMapper.selectCount(
                new LambdaQueryWrapper<AiInstanceEntity>()
                        .eq(AiInstanceEntity::getModelKey, existing.getModelKey().trim())
                        .eq(AiInstanceEntity::getEnvCode, existing.getEnvCode().trim())) > 0) {
            entity.setModelKey(existing.getModelKey());
        }
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AiModelErrorEnum.MODEL_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public BaseResponse<String> create(AiModelCreateRequestDTO request) {
        AiModelEntity entity = new AiModelEntity();
        BeanUtils.copyProperties(request, entity);
        if (StringUtils.isBlank(entity.getEnvCode())) {
            entity.setEnvCode(EnvRuntime.resolveEffectiveEnvCode(astrsomnProperties));
        }

        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AiModelErrorEnum.MODEL_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
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



}
