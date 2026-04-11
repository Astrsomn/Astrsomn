package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.vecdriver.AiVecDriverCreateRequestDTO;
import org.astrsomn.core.common.dto.vecdriver.AiVecDriverQueryRequestDTO;
import org.astrsomn.core.common.dto.vecdriver.AiVecDriverResponseDTO;
import org.astrsomn.core.common.dto.vecdriver.AiVecDriverUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiVecDriverEntity;
import org.astrsomn.core.mapper.AiVecDriverMapper;
import org.astrsomn.server.service.AiVecDriverService;
import org.springframework.stereotype.Service;

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
        if (updateById(entity)) {
            return BaseResponse.success("更新成功");
        } else {
            return BaseResponse.fail("更新失败");
        }
    }

    @Override
    public PageResponse<AiVecDriverResponseDTO> queryPage(BasePageRequest<AiVecDriverQueryRequestDTO> request) {
        Page<AiVecDriverEntity> page = new Page<>(request.getPageNo(), request.getPageSize());
        LambdaQueryWrapper<AiVecDriverEntity> wrapper = new LambdaQueryWrapper<>();
        AiVecDriverQueryRequestDTO query = request.getParam();
        if (query != null) {
            if (query.getDriverName() != null && !query.getDriverName().isEmpty()) {
                wrapper.like(AiVecDriverEntity::getDriverName, query.getDriverName());
            }
            if (query.getProvider() != null && !query.getProvider().isEmpty()) {
                wrapper.eq(AiVecDriverEntity::getProvider, query.getProvider());
            }

        }
        page(page, wrapper);
        PageResponse<AiVecDriverResponseDTO> response = new PageResponse<>();
        response.setTotal(page.getTotal());
        response.setList(page.getRecords().stream().map(item -> {
            AiVecDriverResponseDTO dto = new AiVecDriverResponseDTO();
            dto.setId(item.getId());
            dto.setDriverName(item.getDriverName());
            dto.setProvider(item.getProvider());

            dto.setParams(item.getParams());
            dto.setCreateTime(item.getCreateTime());
            dto.setUpdateTime(item.getUpdateTime());
            return dto;
        }).collect(java.util.stream.Collectors.toList()));
        return response;
    }

    @Override
    public BaseResponse<AiVecDriverResponseDTO> detail(Long id) {
        AiVecDriverEntity entity = getById(id);
        if (entity == null) {
            return BaseResponse.fail("驱动不存在");
        }
        AiVecDriverResponseDTO dto = new AiVecDriverResponseDTO();
        dto.setId(entity.getId());
        dto.setDriverName(entity.getDriverName());
        dto.setProvider(entity.getProvider());

        dto.setParams(entity.getParams());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return BaseResponse.success(dto);
    }
}
