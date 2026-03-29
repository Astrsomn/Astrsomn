package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.config.SystemConfigCreateRequestDTO;
import org.astrsomn.core.common.dto.config.SystemConfigQueryRequestDTO;
import org.astrsomn.core.common.dto.config.SystemConfigResponseDTO;
import org.astrsomn.core.common.dto.config.SystemConfigUpdateRequestDTO;
import org.astrsomn.core.common.entity.SystemConfigEntity;
import org.astrsomn.core.mapper.SystemConfigMapper;
import org.astrsomn.server.service.SystemConfigService;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfigEntity>
        implements SystemConfigService {

    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(SystemConfigCreateRequestDTO request) {
        SystemConfigEntity entity = new SystemConfigEntity();
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
    public BaseResponse<SystemConfigResponseDTO> detail(Long id) {
        SystemConfigEntity entity = getById(id);
        if (entity == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        SystemConfigResponseDTO responseDTO = new SystemConfigResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(SystemConfigUpdateRequestDTO request) {
        SystemConfigEntity entity = new SystemConfigEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        return result ? BaseResponse.success("更新成功") : BaseResponse.fail("更新失败", null);
    }

    @Override
    public PageResponse<SystemConfigResponseDTO> queryPage(BasePageRequest<SystemConfigQueryRequestDTO> request) {
        IPage<SystemConfigResponseDTO> page = request.buildPage();
        SystemConfigQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new SystemConfigQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<SystemConfigResponseDTO> result = baseMapper.queryPage(page, param);
        return PageResponse.buildResponse(result);
    }
}
