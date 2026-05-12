package com.astrsomn.server.service.impl;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.server.mapper.SystemEnvMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.runtime.common.dto.env.SystemEnvCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.env.SystemEnvQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.env.SystemEnvResponseDTO;
import com.astrsomn.api.runtime.common.dto.env.SystemEnvUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.SystemEnvEntity;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.api.runtime.exception.SystemEnvErrorEnum;
import com.astrsomn.starter.runtime.mapper.AstSystemEnvMapper;
import com.astrsomn.server.service.SystemEnvService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.astrsomn.api.runtime.common.utils.PageUtils;
@Service
public class SystemEnvServiceImpl extends ServiceImpl<SystemEnvMapper, SystemEnvEntity> implements SystemEnvService {
    @Override
    public BaseResponse<String> create(SystemEnvCreateRequestDTO request) {
        SystemEnvEntity entity = new SystemEnvEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(SystemEnvErrorEnum.ENV_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) {
            throw new BusinessException(SystemEnvErrorEnum.ENV_DELETE_FAILED);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<SystemEnvResponseDTO> detail(Long id) {
        SystemEnvEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(SystemEnvErrorEnum.ENV_NOT_FOUND);
        }
        SystemEnvResponseDTO responseDTO = new SystemEnvResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(SystemEnvUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(SystemEnvErrorEnum.ENV_PARAM_ERROR);
        }
        SystemEnvEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(SystemEnvErrorEnum.ENV_NOT_FOUND);
        }
        SystemEnvEntity entity = new SystemEnvEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(SystemEnvErrorEnum.ENV_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<SystemEnvResponseDTO> queryPage(BasePageRequest<SystemEnvQueryRequestDTO> request) {
        IPage<SystemEnvResponseDTO> page = PageUtils.buildPage(request);
        IPage<SystemEnvResponseDTO> result = baseMapper.queryPage(page, request.getParam());
        return PageConverter.toResponse(result);
    }
}
