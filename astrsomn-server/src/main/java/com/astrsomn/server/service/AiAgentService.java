package com.astrsomn.server.service;

import com.astrsomn.api.runtime.common.dto.agent.AiAgentCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.agent.AiAgentQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.agent.AiAgentResponseDTO;
import com.astrsomn.api.runtime.common.dto.agent.AiAgentUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiAgentEntity;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiAgentService extends IService<AiAgentEntity> {
    BaseResponse<String> create(AiAgentCreateRequestDTO request);

    BaseResponse<String> delete(long[] longIds);

    BaseResponse<AiAgentResponseDTO> detail(Long longId);

    BaseResponse<String> updateAgent(AiAgentUpdateRequestDTO request);

    PageResponse<AiAgentResponseDTO> queryPage(BasePageRequest<AiAgentQueryRequestDTO> request);

    BaseResponse<String> createFullAgent(AiAgentCreateRequestDTO request);
}
