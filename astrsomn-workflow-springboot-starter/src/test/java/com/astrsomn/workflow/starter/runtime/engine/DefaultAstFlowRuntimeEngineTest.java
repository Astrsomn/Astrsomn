package com.astrsomn.workflow.starter.runtime.engine;

import com.astrsomn.workflow.core.domain.dto.runtime.AstFlowTestRunRequestDTO;
import com.astrsomn.workflow.core.domain.dto.runtime.AstFlowTestRunResponseDTO;
import com.astrsomn.workflow.core.runtime.spi.AstFlowNodeExecutor;
import com.astrsomn.workflow.core.runtime.spi.AstFlowNodeExecutorRegistry;
import com.astrsomn.workflow.starter.runtime.event.LoggingAstFlowDomainEventPublisher;
import com.astrsomn.workflow.starter.runtime.executor.EndNodeExecutor;
import com.astrsomn.workflow.starter.runtime.executor.LlmNodeExecutor;
import com.astrsomn.workflow.starter.runtime.executor.StartNodeExecutor;
import com.astrsomn.workflow.starter.runtime.plan.DefaultAstFlowPlanResolver;
import com.astrsomn.workflow.starter.runtime.policy.NoopAstFlowRateLimitPolicy;
import com.astrsomn.workflow.starter.runtime.registry.DefaultAstFlowNodeExecutorRegistry;
import com.astrsomn.workflow.starter.runtime.state.DefaultAstFlowExecutionStateMachine;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DefaultAstFlowRuntimeEngineTest {

    @Test
    public void shouldCompleteStartToEndFlow() {
        AstFlowNodeExecutorRegistry registry = new DefaultAstFlowNodeExecutorRegistry(defaultExecutors());
        DefaultAstFlowRuntimeEngine engine = new DefaultAstFlowRuntimeEngine(
                new DefaultAstFlowPlanResolver(),
                registry,
                new DefaultAstFlowExecutionStateMachine(),
                new LoggingAstFlowDomainEventPublisher(),
                new NoopAstFlowRateLimitPolicy()
        );

        AstFlowTestRunRequestDTO request = new AstFlowTestRunRequestDTO();
        request.setWorkflowKey("demo");
        request.setUserMessage("hello");
        AstFlowTestRunResponseDTO response = engine.testRun(request);

        Assert.assertEquals("COMPLETED", response.getStatus());
        Assert.assertEquals("end", response.getLastNodeId());
        Assert.assertNotNull(response.getNodeTraces());
        Assert.assertFalse(response.getNodeTraces().isEmpty());
    }

    private List<AstFlowNodeExecutor> defaultExecutors() {
        return List.of(new StartNodeExecutor(), new LlmNodeExecutor(), new EndNodeExecutor());
    }
}
