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
import org.astrsomn.core.exception.base.BusinessException;
import org.astrsomn.core.exception.constant.AiMcpErrorEnum;
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
        if (!result) {
            throw new BusinessException(AiMcpErrorEnum.MCP_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) {
            throw new BusinessException(AiMcpErrorEnum.MCP_DELETE_FAILED);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<AiMcpResponseDTO> detail(Long id) {
        AiMcpEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(AiMcpErrorEnum.MCP_NOT_FOUND);
        }
        AiMcpResponseDTO responseDTO = new AiMcpResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(AiMcpUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AiMcpErrorEnum.MCP_PARAM_ERROR);
        }
        AiMcpEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AiMcpErrorEnum.MCP_NOT_FOUND);
        }
        AiMcpEntity entity = new AiMcpEntity();
        BeanUtils.copyProperties(request, entity);
        bizResourceKeyAssignHelper.assignMcpKeyIfBlank(entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AiMcpErrorEnum.MCP_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
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
