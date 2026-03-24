package org.astrsomn.server.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.entity.AiAgentEntity;
import org.astrsomn.core.mapper.AiAgentMapper;
import org.astrsomn.server.dto.request.AiAgentCreateRequestDTO;
import org.astrsomn.server.dto.request.AiAgentQueryRequestDTO;
import org.astrsomn.server.dto.request.AiAgentUpdateRequestDTO;
import org.astrsomn.server.dto.response.AiAgentResponseDTO;
import org.astrsomn.server.service.AiAgentService;
import org.springframework.stereotype.Service;

@Service
public class AiAgentServiceImpl extends ServiceImpl<AiAgentMapper, AiAgentEntity> implements AiAgentService {
    @Override
    public BaseResponse<String> create(AiAgentCreateRequestDTO request) {
        return null;
    }

    @Override
    public BaseResponse<String> delete(long[] longIds) {
        return null;
    }

    @Override
    public BaseResponse<AiAgentResponseDTO> detail(Long longId) {
        return null;
    }

    @Override
    public BaseResponse<String> updateAgent(AiAgentUpdateRequestDTO request) {
        return null;
    }

    @Override
    public PageResponse<AiAgentResponseDTO> queryPage(BasePageRequest<AiAgentQueryRequestDTO> request) {
        return null;
    }
}
