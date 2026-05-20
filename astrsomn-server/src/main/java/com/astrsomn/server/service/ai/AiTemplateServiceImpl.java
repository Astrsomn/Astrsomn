package com.astrsomn.server.service.ai;

import com.astrsomn.api.runtime.common.dto.template.AiTemplateCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.template.AiTemplateQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.template.AiTemplateResponseDTO;
import com.astrsomn.api.runtime.common.dto.template.AiTemplateUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiTemplateEntity;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.runtime.exception.AiTemplateErrorEnum;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.mapper.AiTemplateMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiTemplateServiceImpl extends ServiceImpl<AiTemplateMapper, AiTemplateEntity> implements AiTemplateService {


    @Override
    public BaseResponse<String> create(AiTemplateCreateRequestDTO request) {
        AiTemplateEntity entity = new AiTemplateEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AiTemplateErrorEnum.TEMPLATE_CREATE_FAILED);
        }
        return BaseResponse.success("success");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) {
            throw new BusinessException(AiTemplateErrorEnum.TEMPLATE_DELETE_FAILED);
        }
        return BaseResponse.success("success");
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
        return BaseResponse.success("success");
    }

    @Override
    public PageResponse<AiTemplateResponseDTO> queryPage(BasePageRequest<AiTemplateQueryRequestDTO> request) {
        IPage<AiTemplateResponseDTO> page = PageUtils.buildPage(request);
        AiTemplateQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiTemplateQueryRequestDTO();
        }
        IPage<AiTemplateResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }
}
