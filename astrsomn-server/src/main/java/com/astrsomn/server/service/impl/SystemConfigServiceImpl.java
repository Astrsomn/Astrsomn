package com.astrsomn.server.service.impl;

import com.astrsomn.system.dto.config.SystemConfigCreateRequestDTO;
import com.astrsomn.system.dto.config.SystemConfigQueryRequestDTO;
import com.astrsomn.system.dto.config.SystemConfigResponseDTO;
import com.astrsomn.system.dto.config.SystemConfigUpdateRequestDTO;
import com.astrsomn.system.entity.SystemConfigEntity;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.system.exception.SystemConfigErrorEnum;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.mapper.SystemConfigMapper;
import com.astrsomn.server.service.SystemConfigService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfigEntity>
        implements SystemConfigService {


    @Override
    public BaseResponse<String> create(SystemConfigCreateRequestDTO request) {
        SystemConfigEntity entity = new SystemConfigEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(SystemConfigErrorEnum.CONFIG_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) {
            throw new BusinessException(SystemConfigErrorEnum.CONFIG_DELETE_FAILED);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<SystemConfigResponseDTO> detail(Long id) {
        SystemConfigEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(SystemConfigErrorEnum.CONFIG_NOT_FOUND);
        }
        SystemConfigResponseDTO responseDTO = new SystemConfigResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(SystemConfigUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(SystemConfigErrorEnum.CONFIG_PARAM_ERROR);
        }
        SystemConfigEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(SystemConfigErrorEnum.CONFIG_NOT_FOUND);
        }
        SystemConfigEntity entity = new SystemConfigEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(SystemConfigErrorEnum.CONFIG_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<SystemConfigResponseDTO> queryPage(BasePageRequest<SystemConfigQueryRequestDTO> request) {
        IPage<SystemConfigResponseDTO> page = PageUtils.buildPage(request);
        SystemConfigQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new SystemConfigQueryRequestDTO();
        }
        IPage<SystemConfigResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }
}
