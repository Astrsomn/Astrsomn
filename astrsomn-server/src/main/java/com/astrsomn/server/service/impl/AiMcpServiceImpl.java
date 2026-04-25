package com.astrsomn.server.service.impl;
import com.astrsomn.core.common.utils.PageConverter;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.mcp.AiMcpCreateRequestDTO;
import com.astrsomn.core.common.dto.mcp.AiMcpQueryRequestDTO;
import com.astrsomn.core.common.dto.mcp.AiMcpResponseDTO;
import com.astrsomn.core.common.dto.mcp.AiMcpUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiMcpEntity;
import com.astrsomn.commn.base.BusinessException;
import com.astrsomn.core.exception.AiMcpErrorEnum;
import com.astrsomn.starter.mapper.AiMcpMapper;
import com.astrsomn.server.service.AiMcpService;
import com.astrsomn.server.service.support.BizResourceKeyAssignHelper;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.astrsomn.core.common.utils.PageUtils;
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
        IPage<AiMcpResponseDTO> page = PageUtils.buildPage(request);
        AiMcpQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiMcpQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiMcpResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }
}
