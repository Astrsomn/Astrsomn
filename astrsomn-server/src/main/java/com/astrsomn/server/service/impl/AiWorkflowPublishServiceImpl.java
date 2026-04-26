package com.astrsomn.server.service.impl;

import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.server.service.AiWorkflowPublishService;
import com.astrsomn.workflow.core.domain.dto.runtime.AstFlowPublishRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class AiWorkflowPublishServiceImpl implements AiWorkflowPublishService {

    @Override
    public BaseResponse<String> publish(AstFlowPublishRequestDTO request) {
        // TODO M1: 发布流程草稿 -> DSL 校验 -> 编译 -> 版本冻结 -> 切换 latest
        return BaseResponse.fail("TODO: publish not implemented yet", null);
    }
}
