package com.astrsomn.server.service.impl;
import com.astrsomn.core.common.utils.PageConverter;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.agent.AiAgentCreateRequestDTO;
import com.astrsomn.core.common.dto.agent.AiAgentQueryRequestDTO;
import com.astrsomn.core.common.dto.agent.AiAgentResponseDTO;
import com.astrsomn.core.common.dto.agent.AiAgentUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiAgentEntity;
import com.astrsomn.commn.base.BusinessException;
import com.astrsomn.core.exception.AiAgentErrorEnum;
import com.astrsomn.starter.mapper.AiAgentMapper;
import com.astrsomn.server.service.AiAgentService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.astrsomn.core.common.utils.PageUtils;
@Service
@RequiredArgsConstructor
public class AiAgentServiceImpl extends ServiceImpl<AiAgentMapper, AiAgentEntity> implements AiAgentService {


    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AiAgentCreateRequestDTO request) {
        AiAgentEntity aiAgent = new AiAgentEntity();
        BeanUtils.copyProperties(request, aiAgent);

        boolean result = save(aiAgent);
        if (!result) {
            throw new BusinessException(AiAgentErrorEnum.AGENT_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) {
            throw new BusinessException(AiAgentErrorEnum.AGENT_DELETE_FAILED);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<AiAgentResponseDTO> detail(Long longId) {
        AiAgentEntity aiAgent = getById(longId);
        if (aiAgent == null) {
            throw new BusinessException(AiAgentErrorEnum.AGENT_NOT_FOUND);
        }
        AiAgentResponseDTO responseDTO = new AiAgentResponseDTO();
        BeanUtils.copyProperties(aiAgent, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> updateAgent(AiAgentUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AiAgentErrorEnum.AGENT_PARAM_ERROR);
        }
        AiAgentEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AiAgentErrorEnum.AGENT_NOT_FOUND);
        }
        AiAgentEntity aiAgent = new AiAgentEntity();
        BeanUtils.copyProperties(request, aiAgent);

        boolean result = updateById(aiAgent);
        if (!result) {
            throw new BusinessException(AiAgentErrorEnum.AGENT_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<AiAgentResponseDTO> queryPage(BasePageRequest<AiAgentQueryRequestDTO> request) {
        IPage<AiAgentResponseDTO> page = PageUtils.buildPage(request);
        AiAgentQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiAgentQueryRequestDTO();
        }

        IPage<AiAgentResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }
}
