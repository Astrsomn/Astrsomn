package com.astrsomn.starter.workflow.runtime.state;

import com.astrsomn.api.workflow.runtime.constant.AstFlowInstanceEventEnum;
import com.astrsomn.api.workflow.runtime.constant.AstFlowInstanceStateEnum;
import com.astrsomn.api.workflow.runtime.spi.AstFlowExecutionStateMachine;

import java.util.EnumMap;
import java.util.Map;

public class DefaultAstFlowExecutionStateMachine implements AstFlowExecutionStateMachine {

    private final Map<AstFlowInstanceStateEnum, Map<AstFlowInstanceEventEnum, AstFlowInstanceStateEnum>> transitions =
            new EnumMap<>(AstFlowInstanceStateEnum.class);

    public DefaultAstFlowExecutionStateMachine() {
        register(AstFlowInstanceStateEnum.CREATED, AstFlowInstanceEventEnum.START, AstFlowInstanceStateEnum.RUNNING);
        register(AstFlowInstanceStateEnum.RUNNING, AstFlowInstanceEventEnum.NODE_SUSPENDED, AstFlowInstanceStateEnum.SUSPENDED);
        register(AstFlowInstanceStateEnum.RUNNING, AstFlowInstanceEventEnum.NODE_FAILED, AstFlowInstanceStateEnum.FAILED);
        register(AstFlowInstanceStateEnum.RUNNING, AstFlowInstanceEventEnum.FINISH, AstFlowInstanceStateEnum.COMPLETED);
        register(AstFlowInstanceStateEnum.SUSPENDED, AstFlowInstanceEventEnum.RESUME, AstFlowInstanceStateEnum.RUNNING);
    }

    private void register(AstFlowInstanceStateEnum from, AstFlowInstanceEventEnum event, AstFlowInstanceStateEnum to) {
        transitions.computeIfAbsent(from, key -> new EnumMap<>(AstFlowInstanceEventEnum.class)).put(event, to);
    }

    @Override
    public AstFlowInstanceStateEnum transit(AstFlowInstanceStateEnum currentState, AstFlowInstanceEventEnum event) {
        AstFlowInstanceStateEnum next = transitions.getOrDefault(currentState, Map.of()).get(event);
        if (next == null) {
            throw new IllegalStateException("Illegal state transition, currentState=" + currentState + ", event=" + event);
        }
        return next;
    }
}
