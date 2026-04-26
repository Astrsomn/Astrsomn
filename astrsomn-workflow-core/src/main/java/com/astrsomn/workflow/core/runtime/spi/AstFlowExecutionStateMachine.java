package com.astrsomn.workflow.core.runtime.spi;

import com.astrsomn.workflow.core.runtime.constant.AstFlowInstanceEventEnum;
import com.astrsomn.workflow.core.runtime.constant.AstFlowInstanceStateEnum;

public interface AstFlowExecutionStateMachine {

    AstFlowInstanceStateEnum transit(AstFlowInstanceStateEnum currentState, AstFlowInstanceEventEnum event);
}
