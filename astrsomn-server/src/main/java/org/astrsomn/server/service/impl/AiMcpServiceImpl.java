package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.mcp.AiMcpCreateRequestDTO;
import org.astrsomn.core.common.dto.mcp.AiMcpQueryRequestDTO;
import org.astrsomn.core.common.dto.mcp.AiMcpUpdateRequestDTO;
import org.astrsomn.core.common.dto.mcp.AiMcpResponseDTO;
import org.astrsomn.core.common.entity.AiMcpEntity;
import org.astrsomn.core.mapper.AiMcpMapper;
import org.astrsomn.server.service.AiMcpService;
import org.astrsomn.server.service.support.BizResourceKeyAssignHelper;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiMcpServiceImpl extends ServiceImpl<AiMcpMapper, AiMcpEntity> implements AiMcpService {

    private final BizResourceKeyAssignHelper bizResourceKeyAssignHelper;
    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AiMcpCreateRequestDTO request) {
        AiMcpEntity entity = new AiMcpEntity();
        BeanUtils.copyProperties(request, entity);
        bizResourceKeyAssignHelper.assignMcpKeyIfBlank(entity);
        boolean result = save(entity);
        return result ? BaseResponse.success("创建成功") : BaseResponse.fail("创建失败", null);
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        return result ? BaseResponse.success("删除成功") : BaseResponse.fail("删除失败", null);
    }

    @Override
    public BaseResponse<AiMcpResponseDTO> detail(Long id) {
        AiMcpEntity entity = getById(id);
        if (entity == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        AiMcpResponseDTO responseDTO = new AiMcpResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(AiMcpUpdateRequestDTO request) {
        AiMcpEntity entity = new AiMcpEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        return result ? BaseResponse.success("更新成功") : BaseResponse.fail("更新失败", null);
    }

    @Override
    public PageResponse<AiMcpResponseDTO> queryPage(BasePageRequest<AiMcpQueryRequestDTO> request) {
        IPage<AiMcpResponseDTO> page = request.buildPage();
        AiMcpQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiMcpQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiMcpResponseDTO> result = baseMapper.queryPage(page, param);
        return PageResponse.buildResponse(result);
    }
}
