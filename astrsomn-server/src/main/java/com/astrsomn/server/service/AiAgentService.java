package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.core.common.base.BasePageRequest;
import com.astrsomn.core.common.base.BaseResponse;
import com.astrsomn.core.common.base.PageResponse;
import com.astrsomn.core.common.dto.agent.AiAgentCreateRequestDTO;
import com.astrsomn.core.common.dto.agent.AiAgentQueryRequestDTO;
import com.astrsomn.core.common.dto.agent.AiAgentResponseDTO;
import com.astrsomn.core.common.dto.agent.AiAgentUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiAgentEntity;

public interface AiAgentService extends IService<AiAgentEntity> {
    BaseResponse<String> create(AiAgentCreateRequestDTO request);

    BaseResponse<String> delete(long[] longIds);

    BaseResponse<AiAgentResponseDTO> detail(Long longId);

    BaseResponse<String> updateAgent(AiAgentUpdateRequestDTO request);

    PageResponse<AiAgentResponseDTO> queryPage(BasePageRequest<AiAgentQueryRequestDTO> request);
}
