package com.astrsomn.api.workflow.runtime.spi;

import com.astrsomn.api.workflow.runtime.constant.AstFlowInstanceEventEnum;
import com.astrsomn.api.workflow.runtime.constant.AstFlowInstanceStateEnum;

public interface AstFlowExecutionStateMachine {

    AstFlowInstanceStateEnum transit(AstFlowInstanceStateEnum currentState, AstFlowInstanceEventEnum event);
}