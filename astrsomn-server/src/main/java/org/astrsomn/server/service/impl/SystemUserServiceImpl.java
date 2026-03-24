package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.user.SystemUserCreateRequestDTO;
import org.astrsomn.core.common.dto.user.SystemUserQueryRequestDTO;
import org.astrsomn.core.common.dto.user.SystemUserUpdateRequestDTO;
import org.astrsomn.core.common.dto.user.SystemUserResponseDTO;
import org.astrsomn.core.common.entity.SystemUserEntity;
import org.astrsomn.core.mapper.SystemUserMapper;
import org.astrsomn.server.service.SystemUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUserEntity> implements SystemUserService {
    @Override
    public BaseResponse<String> create(SystemUserCreateRequestDTO request) {
        SystemUserEntity entity = new SystemUserEntity();
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
    public BaseResponse<SystemUserResponseDTO> detail(Long id) {
        SystemUserEntity entity = getById(id);
        if (entity == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        SystemUserResponseDTO responseDTO = new SystemUserResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(SystemUserUpdateRequestDTO request) {
        SystemUserEntity entity = new SystemUserEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        return result ? BaseResponse.success("更新成功") : BaseResponse.fail("更新失败", null);
    }

    @Override
    public PageResponse<SystemUserResponseDTO> queryPage(BasePageRequest<SystemUserQueryRequestDTO> request) {
        IPage<SystemUserResponseDTO> page = request.buildPage();
        IPage<SystemUserResponseDTO> result = baseMapper.queryPage(page, request.getParam());
        return PageResponse.buildResponse(result);
    }
}
