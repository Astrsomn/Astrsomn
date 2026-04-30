package com.astrsomn.server.service;

import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.workflow.core.domain.dto.runtime.AstFlowPublishRequestDTO;

public interface AiWorkflowPublishService {

    BaseResponse<String> publish(AstFlowPublishRequestDTO request);
}
