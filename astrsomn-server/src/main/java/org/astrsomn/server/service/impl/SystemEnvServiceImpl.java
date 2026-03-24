package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.env.SystemEnvCreateRequestDTO;
import org.astrsomn.core.common.dto.env.SystemEnvQueryRequestDTO;
import org.astrsomn.core.common.dto.env.SystemEnvUpdateRequestDTO;
import org.astrsomn.core.common.dto.env.SystemEnvResponseDTO;
import org.astrsomn.core.common.entity.SystemEnvEntity;
import org.astrsomn.core.mapper.SystemEnvMapper;
import org.astrsomn.server.service.SystemEnvService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class SystemEnvServiceImpl extends ServiceImpl<SystemEnvMapper, SystemEnvEntity> implements SystemEnvService {
    @Override
    public BaseResponse<String> create(SystemEnvCreateRequestDTO request) {
        SystemEnvEntity entity = new SystemEnvEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = save(entity);
        return result ? BaseResponse.success("创建成功") : BaseResponse.fail("创建失败", null);
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        return result ? BaseResponse.success("删除成功") : BaseResponse.fail("删除失败", null);
    }

    @Override
    public BaseResponse<SystemEnvResponseDTO> detail(Long id) {
        SystemEnvEntity entity = getById(id);
        if (entity == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        SystemEnvResponseDTO responseDTO = new SystemEnvResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(SystemEnvUpdateRequestDTO request) {
        SystemEnvEntity entity = new SystemEnvEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        return result ? BaseResponse.success("更新成功") : BaseResponse.fail("更新失败", null);
    }

    @Override
    public PageResponse<SystemEnvResponseDTO> queryPage(BasePageRequest<SystemEnvQueryRequestDTO> request) {
        IPage<SystemEnvResponseDTO> page = request.buildPage();
        IPage<SystemEnvResponseDTO> result = baseMapper.queryPage(page, request.getParam());
        return PageResponse.buildResponse(result);
    }
}
