package com.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import com.astrsomn.core.common.base.BasePageRequest;
import com.astrsomn.core.common.base.BaseResponse;
import com.astrsomn.core.common.base.PageResponse;
import com.astrsomn.core.common.dto.template.AiTemplateCreateRequestDTO;
import com.astrsomn.core.common.dto.template.AiTemplateQueryRequestDTO;
import com.astrsomn.core.common.dto.template.AiTemplateResponseDTO;
import com.astrsomn.core.common.dto.template.AiTemplateUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiTemplateEntity;
import com.astrsomn.core.exception.base.BusinessException;
import com.astrsomn.core.exception.constant.AiTemplateErrorEnum;
import com.astrsomn.starter.mapper.AiTemplateMapper;
import com.astrsomn.server.service.AiTemplateService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiTemplateServiceImpl extends ServiceImpl<AiTemplateMapper, AiTemplateEntity> implements AiTemplateService {

    private final QueryEnvParamHelper queryEnvParamHelper;
    @Override
    public BaseResponse<String> create(AiTemplateCreateRequestDTO request) {
        AiTemplateEntity entity = new AiTemplateEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AiTemplateErrorEnum.TEMPLATE_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) {
            throw new BusinessException(AiTemplateErrorEnum.TEMPLATE_DELETE_FAILED);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<AiTemplateResponseDTO> detail(Long id) {
        AiTemplateEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(AiTemplateErrorEnum.TEMPLATE_NOT_FOUND);
        }
        AiTemplateResponseDTO responseDTO = new AiTemplateResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(AiTemplateUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AiTemplateErrorEnum.TEMPLATE_PARAM_ERROR);
        }
        AiTemplateEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AiTemplateErrorEnum.TEMPLATE_NOT_FOUND);
        }
        AiTemplateEntity entity = new AiTemplateEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AiTemplateErrorEnum.TEMPLATE_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<AiTemplateResponseDTO> queryPage(BasePageRequest<AiTemplateQueryRequestDTO> request) {
        IPage<AiTemplateResponseDTO> page = request.buildPage();
        AiTemplateQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiTemplateQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiTemplateResponseDTO> result = baseMapper.queryPage(page, param);
        return PageResponse.buildResponse(result);
    }
}
