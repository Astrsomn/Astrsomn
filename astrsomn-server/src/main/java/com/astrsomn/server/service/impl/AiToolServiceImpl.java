package com.astrsomn.server.service.impl;

import com.astrsomn.api.runtime.common.dto.tool.AiToolCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.tool.AiToolQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.tool.AiToolResponseDTO;
import com.astrsomn.api.runtime.common.dto.tool.AiToolUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiToolEntity;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.runtime.exception.AiToolErrorEnum;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.mapper.AiToolMapper;
import com.astrsomn.server.service.AiToolService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiToolServiceImpl extends ServiceImpl<AiToolMapper, AiToolEntity> implements AiToolService {



    @Override
    public BaseResponse<String> create(AiToolCreateRequestDTO request) {
        AiToolEntity entity = new AiToolEntity();
        BeanUtils.copyProperties(request, entity);

        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AiToolErrorEnum.TOOL_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) {
            throw new BusinessException(AiToolErrorEnum.TOOL_DELETE_FAILED);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<AiToolResponseDTO> detail(Long id) {
        AiToolEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(AiToolErrorEnum.TOOL_NOT_FOUND);
        }
        AiToolResponseDTO responseDTO = new AiToolResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(AiToolUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AiToolErrorEnum.TOOL_PARAM_ERROR);
        }
        AiToolEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AiToolErrorEnum.TOOL_NOT_FOUND);
        }
        AiToolEntity entity = new AiToolEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AiToolErrorEnum.TOOL_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<AiToolResponseDTO> queryPage(BasePageRequest<AiToolQueryRequestDTO> request) {
        IPage<AiToolResponseDTO> page = PageUtils.buildPage(request);
        AiToolQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiToolQueryRequestDTO();
        }
        IPage<AiToolResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }
}
