package com.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import com.astrsomn.core.common.base.BasePageRequest;
import com.astrsomn.core.common.base.BaseResponse;
import com.astrsomn.core.common.base.PageResponse;
import com.astrsomn.core.common.dto.model.AiModelCreateRequestDTO;
import com.astrsomn.core.common.dto.model.AiModelQueryRequestDTO;
import com.astrsomn.core.common.dto.model.AiModelResponseDTO;
import com.astrsomn.core.common.dto.model.AiModelUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiInstanceEntity;
import com.astrsomn.core.common.entity.AiModelEntity;
import com.astrsomn.core.common.utils.StringUtils;
import com.astrsomn.core.exception.base.BusinessException;
import com.astrsomn.core.exception.constant.AiModelErrorEnum;
import com.astrsomn.starter.mapper.AiInstanceMapper;
import com.astrsomn.starter.mapper.AiModelMapper;
import com.astrsomn.server.service.AiModelService;
import com.astrsomn.server.service.support.BizResourceKeyAssignHelper;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.starter.config.AstrsomnProperties;
import com.astrsomn.starter.context.EnvRuntime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiModelServiceImpl extends ServiceImpl<AiModelMapper, AiModelEntity> implements AiModelService {

    private final AstrsomnProperties astrsomnProperties;
    private final AiInstanceMapper aiInstanceMapper;
    private final QueryEnvParamHelper queryEnvParamHelper;
    private final BizResourceKeyAssignHelper bizResourceKeyAssignHelper;

    @Override
    public BaseResponse<String> delete(long[] longIds) {
        boolean result = removeByIds(Arrays.stream(longIds).boxed().toList());
        if (!result) {
            throw new BusinessException(AiModelErrorEnum.MODEL_DELETE_FAILED);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public PageResponse<AiModelResponseDTO> queryPage(BasePageRequest<AiModelQueryRequestDTO> request) {
        IPage<AiModelResponseDTO> page = request.buildPage();
        AiModelQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiModelQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiModelResponseDTO> result = baseMapper.queryPage(page, param);
        return PageResponse.buildResponse(result);
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
        bizResourceKeyAssignHelper.assignModelKeyIfBlank(entity);

        if (aiInstanceMapper.selectCount(
                new LambdaQueryWrapper<AiInstanceEntity>()
                        .eq(AiInstanceEntity::getModelKey, existing.getModelKey().trim())
                        .eq(AiInstanceEntity::getEnvCode, existing.getEnvCode().trim())) > 0) {
            entity.setModelKey(existing.getModelKey());
        } else {
            bizResourceKeyAssignHelper.assignModelKeyIfBlank(entity);
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
        bizResourceKeyAssignHelper.assignModelKeyIfBlank(entity);
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AiModelErrorEnum.MODEL_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }



}
