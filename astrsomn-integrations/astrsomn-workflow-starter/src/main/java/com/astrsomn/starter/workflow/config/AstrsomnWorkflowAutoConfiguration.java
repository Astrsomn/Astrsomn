package com.astrsomn.starter.workflow.config;

import com.astrsomn.api.workflow.runtime.spi.*;
import com.astrsomn.api.workflow.runtime.spi.policy.AstFlowRateLimitPolicy;
import com.astrsomn.api.workflow.runtime.spi.policy.AstFlowRetryPolicy;
import com.astrsomn.api.workflow.runtime.spi.policy.AstFlowTimeoutPolicy;
import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.astrsomn.starter.workflow.runtime.engine.DefaultAstFlowRuntimeEngine;
import com.astrsomn.starter.workflow.runtime.event.LoggingAstFlowDomainEventPublisher;
import com.astrsomn.starter.workflow.runtime.executor.*;
import com.astrsomn.starter.workflow.runtime.plan.DefaultAstFlowPlanResolver;
import com.astrsomn.starter.workflow.runtime.policy.NoopAstFlowRateLimitPolicy;
import com.astrsomn.starter.workflow.runtime.policy.NoopAstFlowRetryPolicy;
import com.astrsomn.starter.workflow.runtime.policy.NoopAstFlowTimeoutPolicy;
import com.astrsomn.starter.workflow.runtime.registry.DefaultAstFlowNodeExecutorRegistry;
import com.astrsomn.starter.workflow.runtime.state.DefaultAstFlowExecutionStateMachine;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

import java.util.List;


@AutoConfiguration
@ConditionalOnClass(AstrsomnProperties.class)
@ConditionalOnProperty(name = "astrsomn.datasource.url")
@Conditional(EnableAstroWorkflowCondition.class)
@AutoConfigureBefore(name = "com.astrsomn.starter.runtime.config.AstrsomnAutoConfiguration")
public class AstrsomnWorkflowAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AstFlowExecutionStateMachine astFlowExecutionStateMachine() {
        return new DefaultAstFlowExecutionStateMachine();
    }

    @Bean
    @ConditionalOnMissingBean
    public AstFlowPlanResolver astFlowPlanResolver() {
        return new DefaultAstFlowPlanResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public AstFlowDomainEventPublisher astFlowDomainEventPublisher() {
        return new LoggingAstFlowDomainEventPublisher();
    }

    @Bean
    @ConditionalOnMissingBean
    public AstFlowRetryPolicy astFlowRetryPolicy() {
        return new NoopAstFlowRetryPolicy();
    }

    @Bean
    @ConditionalOnMissingBean
    public AstFlowTimeoutPolicy astFlowTimeoutPolicy() {
        return new NoopAstFlowTimeoutPolicy();
    }

    @Bean
    @ConditionalOnMissingBean
    public AstFlowRateLimitPolicy astFlowRateLimitPolicy() {
        return new NoopAstFlowRateLimitPolicy();
    }

    @Bean
    public AstFlowNodeExecutor startNodeExecutor() {
        return new StartNodeExecutor();
    }

    @Bean
    public AstFlowNodeExecutor endNodeExecutor() {
        return new EndNodeExecutor();
    }

    @Bean
    public AstFlowNodeExecutor llmNodeExecutor() {
        return new LlmNodeExecutor();
    }

    @Bean
    public AstFlowNodeExecutor toolNodeExecutor() {
        return new ToolNodeExecutor();
    }

    @Bean
    public AstFlowNodeExecutor conditionNodeExecutor() {
        return new ConditionNodeExecutor();
    }

    @Bean
    public AstFlowNodeExecutor humanNodeExecutor() {
        return new HumanNodeExecutor();
    }

    @Bean
    public AstFlowNodeExecutor noopNodeExecutor() {
        return new NoopNodeExecutor();
    }

    @Bean
    @ConditionalOnMissingBean
    public AstFlowNodeExecutorRegistry astFlowNodeExecutorRegistry(List<AstFlowNodeExecutor> nodeExecutors) {
        return new DefaultAstFlowNodeExecutorRegistry(nodeExecutors);
    }

    @Bean
    @ConditionalOnMissingBean
    public AstFlowRuntimeEngine astFlowRuntimeEngine(AstFlowPlanResolver planResolver,
                                                     AstFlowNodeExecutorRegistry nodeExecutorRegistry,
                                                     AstFlowExecutionStateMachine stateMachine,
                                                     AstFlowDomainEventPublisher eventPublisher,
                                                     AstFlowRateLimitPolicy rateLimitPolicy) {
        return new DefaultAstFlowRuntimeEngine(planResolver, nodeExecutorRegistry, stateMachine, eventPublisher, rateLimitPolicy);
    }
}
