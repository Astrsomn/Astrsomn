package org.astrsomn.workflow.starter.autoconfigure;

import org.astrsomn.workflow.core.engine.DefaultWorkflowEngine;
import org.astrsomn.workflow.core.engine.WorkflowEngine;
import org.astrsomn.workflow.core.spi.ConditionEvaluator;
import org.astrsomn.workflow.core.spi.LoopContinuationPolicy;
import org.astrsomn.workflow.core.spi.NodeHandlerRegistry;
import org.astrsomn.workflow.core.spi.impl.DefaultLoopContinuationPolicy;
import org.astrsomn.workflow.starter.WorkflowRuntimeFactory;
import org.astrsomn.workflow.starter.spel.SpelConditionEvaluator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackageClasses = WorkflowRuntimeFactory.class)
public class WorkflowAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public NodeHandlerRegistry nodeHandlerRegistry() {
        return new NodeHandlerRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConditionEvaluator conditionEvaluator() {
        return new SpelConditionEvaluator();
    }

    @Bean
    @ConditionalOnMissingBean
    public LoopContinuationPolicy loopContinuationPolicy() {
        return new DefaultLoopContinuationPolicy();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultWorkflowEngine defaultWorkflowEngine(NodeHandlerRegistry nodeHandlerRegistry,
                                                       ConditionEvaluator conditionEvaluator,
                                                       LoopContinuationPolicy loopContinuationPolicy) {
        return new DefaultWorkflowEngine(nodeHandlerRegistry, conditionEvaluator, loopContinuationPolicy,
                10_000, null);
    }

    @Bean
    @ConditionalOnMissingBean
    public WorkflowEngine workflowEngine(DefaultWorkflowEngine defaultWorkflowEngine) {
        return defaultWorkflowEngine;
    }
}
