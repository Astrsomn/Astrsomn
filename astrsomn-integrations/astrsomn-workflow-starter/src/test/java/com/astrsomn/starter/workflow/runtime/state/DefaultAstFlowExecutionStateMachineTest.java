package com.astrsomn.starter.workflow.runtime.state;

import com.astrsomn.api.workflow.runtime.constant.AstFlowInstanceEventEnum;
import com.astrsomn.api.workflow.runtime.constant.AstFlowInstanceStateEnum;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Test skipped")
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
