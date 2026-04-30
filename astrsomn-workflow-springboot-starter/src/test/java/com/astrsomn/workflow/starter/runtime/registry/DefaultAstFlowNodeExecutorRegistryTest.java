package com.astrsomn.workflow.starter.runtime.registry;

import com.astrsomn.workflow.core.runtime.context.AstFlowNodeExecutionContext;
import com.astrsomn.workflow.core.runtime.model.AstFlowNodeExecuteResult;
import com.astrsomn.workflow.core.runtime.spi.AstFlowNodeExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DefaultAstFlowNodeExecutorRegistryTest {

    @Test
    public void shouldThrowWhenDuplicateNodeTypeExists() {
        AstFlowNodeExecutor executor1 = new StubNodeExecutor("LLM");
        AstFlowNodeExecutor executor2 = new StubNodeExecutor("LLM");
        try {
            new DefaultAstFlowNodeExecutorRegistry(List.of(executor1, executor2));
            Assert.fail("Should throw IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertTrue(expected.getMessage().contains("Duplicate node executor type"));
        }
    }

    @Test
    public void shouldResolveExecutor() {
        AstFlowNodeExecutor executor = new StubNodeExecutor("END");
        DefaultAstFlowNodeExecutorRegistry registry = new DefaultAstFlowNodeExecutorRegistry(List.of(executor));
        Assert.assertSame(executor, registry.resolve("END"));
    }

    private record StubNodeExecutor(String type) implements AstFlowNodeExecutor {
        @Override
        public AstFlowNodeExecuteResult execute(AstFlowNodeExecutionContext context) {
            return null;
        }
    }
}
