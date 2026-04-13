package org.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.agent.AiAgentCreateRequestDTO;
import org.astrsomn.core.common.dto.agent.AiAgentQueryRequestDTO;
import org.astrsomn.core.common.dto.agent.AiAgentResponseDTO;
import org.astrsomn.core.common.dto.agent.AiAgentUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiAgentEntity;

public interface AiAgentService extends IService<AiAgentEntity> {
    BaseResponse<String> create(AiAgentCreateRequestDTO request);

    BaseResponse<String> delete(long[] longIds);

    BaseResponse<AiAgentResponseDTO> detail(Long longId);

    BaseResponse<String> updateAgent(AiAgentUpdateRequestDTO request);

    PageResponse<AiAgentResponseDTO> queryPage(BasePageRequest<AiAgentQueryRequestDTO> request);
}
