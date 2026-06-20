package com.astrsomn.starter.workflow.runtime.engine;

import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowTestRunRequestDTO;
import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowTestRunResponseDTO;
import com.astrsomn.api.workflow.runtime.spi.AstFlowNodeExecutor;
import com.astrsomn.api.workflow.runtime.spi.AstFlowNodeExecutorRegistry;
import com.astrsomn.starter.workflow.runtime.event.LoggingAstFlowDomainEventPublisher;
import com.astrsomn.starter.workflow.runtime.executor.EndNodeExecutor;
import com.astrsomn.starter.workflow.runtime.executor.LlmNodeExecutor;
import com.astrsomn.starter.workflow.runtime.executor.StartNodeExecutor;
import com.astrsomn.starter.workflow.runtime.plan.DefaultAstFlowPlanResolver;
import com.astrsomn.starter.workflow.runtime.policy.NoopAstFlowRateLimitPolicy;
import com.astrsomn.starter.workflow.runtime.registry.DefaultAstFlowNodeExecutorRegistry;
import com.astrsomn.starter.workflow.runtime.state.DefaultAstFlowExecutionStateMachine;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

@Ignore("Test skipped")
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
