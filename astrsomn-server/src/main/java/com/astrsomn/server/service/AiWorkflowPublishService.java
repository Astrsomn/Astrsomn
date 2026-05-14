package com.astrsomn.server.service;

import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowPublishRequestDTO;
import com.astrsomn.common.base.BaseResponse;

public interface AiWorkflowPublishService {

    BaseResponse<String> publish(AstFlowPublishRequestDTO request);
}
