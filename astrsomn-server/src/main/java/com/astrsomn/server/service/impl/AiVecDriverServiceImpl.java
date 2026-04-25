package com.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.astrsomn.core.common.base.BasePageRequest;
import com.astrsomn.core.common.base.BaseResponse;
import com.astrsomn.core.common.base.PageResponse;
import com.astrsomn.core.common.dto.vecdriver.AiVecDriverCreateRequestDTO;
import com.astrsomn.core.common.dto.vecdriver.AiVecDriverQueryRequestDTO;
import com.astrsomn.core.common.dto.vecdriver.AiVecDriverResponseDTO;
import com.astrsomn.core.common.dto.vecdriver.AiVecDriverUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiVecDriverEntity;
import com.astrsomn.starter.mapper.AiVecDriverMapper;
import com.astrsomn.server.service.AiVecDriverService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AiVecDriverServiceImpl extends ServiceImpl<AiVecDriverMapper, AiVecDriverEntity> implements AiVecDriverService {

    @Override
    public BaseResponse<String> create(AiVecDriverCreateRequestDTO request) {
        AiVecDriverEntity entity = new AiVecDriverEntity();
        entity.setDriverName(request.getDriverName());
        entity.setProvider(request.getProvider());

        entity.setParams(request.getParams());
        if (save(entity)) {
            return BaseResponse.success("创建成功");
        } else {
            return BaseResponse.fail("创建失败");
        }
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (removeByIds(java.util.Arrays.asList(ids))) {
            return BaseResponse.success("删除成功");
        } else {
            return BaseResponse.fail("删除失败");
        }
    }

    @Override
    public BaseResponse<String> update(AiVecDriverUpdateRequestDTO request) {
        AiVecDriverEntity entity = getById(request.getId());
        if (entity == null) {
            return BaseResponse.fail("驱动不存在");
        }
        entity.setDriverName(request.getDriverName());
        entity.setProvider(request.getProvider());

        entity.setParams(request.getParams());
        entity.setStatus(request.getStatus());
        if (updateById(entity)) {
            return BaseResponse.success("更新成功");
        } else {
            return BaseResponse.fail("更新失败");
        }
    }

    @Override
    public PageResponse<AiVecDriverResponseDTO> queryPage(BasePageRequest<AiVecDriverQueryRequestDTO> request) {
        IPage<AiVecDriverResponseDTO> page = request.buildPage();
        AiVecDriverQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiVecDriverQueryRequestDTO();
        }
        IPage<AiVecDriverResponseDTO> result = baseMapper.queryPage(page, param);
        return PageResponse.buildResponse(result);
    }

    @Override
    public BaseResponse<List<AiVecDriverResponseDTO>> listForSelect(String status) {
        LambdaQueryWrapper<AiVecDriverEntity> w = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            w.eq(AiVecDriverEntity::getStatus, status);
        }
        w.orderByAsc(AiVecDriverEntity::getDriverName);
        List<AiVecDriverResponseDTO> list = list(w).stream().map(this::toResponseDto).collect(Collectors.toList());
        return BaseResponse.success(list);
    }

    @Override
    public BaseResponse<AiVecDriverResponseDTO> detail(Long id) {
        AiVecDriverEntity entity = getById(id);
        if (entity == null) {
            return BaseResponse.fail("驱动不存在");
        }
        return BaseResponse.success(toResponseDto(entity));
    }

    private AiVecDriverResponseDTO toResponseDto(AiVecDriverEntity item) {
        AiVecDriverResponseDTO dto = new AiVecDriverResponseDTO();
        dto.setId(item.getId());
        dto.setDriverName(item.getDriverName());
        dto.setProvider(item.getProvider());
        dto.setParams(item.getParams());
        dto.setCreateTime(item.getCreateTime());
        dto.setUpdateTime(item.getUpdateTime());
        return dto;
    }
}
