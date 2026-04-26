package com.astrsomn.workflow.starter.runtime.state;

import com.astrsomn.workflow.core.runtime.constant.AstFlowInstanceEventEnum;
import com.astrsomn.workflow.core.runtime.constant.AstFlowInstanceStateEnum;
import org.junit.Assert;
import org.junit.Test;

public class DefaultAstFlowExecutionStateMachineTest {

    private final DefaultAstFlowExecutionStateMachine stateMachine = new DefaultAstFlowExecutionStateMachine();

    @Test
    public void shouldTransitForValidEvents() {
        AstFlowInstanceStateEnum running = stateMachine.transit(AstFlowInstanceStateEnum.CREATED, AstFlowInstanceEventEnum.START);
        AstFlowInstanceStateEnum completed = stateMachine.transit(running, AstFlowInstanceEventEnum.FINISH);
        Assert.assertEquals(AstFlowInstanceStateEnum.RUNNING, running);
        Assert.assertEquals(AstFlowInstanceStateEnum.COMPLETED, completed);
    }

    @Test
    public void shouldThrowForInvalidTransition() {
        try {
            stateMachine.transit(AstFlowInstanceStateEnum.CREATED, AstFlowInstanceEventEnum.FINISH);
            Assert.fail("Should throw IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertTrue(expected.getMessage().contains("Illegal state transition"));
        }
    }
}
