package com.astrsomn.server.service;

import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowPublishRequestDTO;

public interface AiWorkflowPublishService {

    BaseResponse<String> publish(AstFlowPublishRequestDTO request);
}
