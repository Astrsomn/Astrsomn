package com.astrsomn.workflow.starter.config;

import com.astrsomn.starter.config.AstrsomnProperties;
import com.astrsomn.workflow.core.runtime.spi.AstFlowDomainEventPublisher;
import com.astrsomn.workflow.core.runtime.spi.AstFlowExecutionStateMachine;
import com.astrsomn.workflow.core.runtime.spi.AstFlowNodeExecutor;
import com.astrsomn.workflow.core.runtime.spi.AstFlowNodeExecutorRegistry;
import com.astrsomn.workflow.core.runtime.spi.AstFlowPlanResolver;
import com.astrsomn.workflow.core.runtime.spi.AstFlowRuntimeEngine;
import com.astrsomn.workflow.core.runtime.spi.policy.AstFlowRateLimitPolicy;
import com.astrsomn.workflow.core.runtime.spi.policy.AstFlowRetryPolicy;
import com.astrsomn.workflow.core.runtime.spi.policy.AstFlowTimeoutPolicy;
import com.astrsomn.workflow.starter.runtime.engine.DefaultAstFlowRuntimeEngine;
import com.astrsomn.workflow.starter.runtime.event.LoggingAstFlowDomainEventPublisher;
import com.astrsomn.workflow.starter.runtime.executor.ConditionNodeExecutor;
import com.astrsomn.workflow.starter.runtime.executor.EndNodeExecutor;
import com.astrsomn.workflow.starter.runtime.executor.HumanNodeExecutor;
import com.astrsomn.workflow.starter.runtime.executor.LlmNodeExecutor;
import com.astrsomn.workflow.starter.runtime.executor.NoopNodeExecutor;
import com.astrsomn.workflow.starter.runtime.executor.StartNodeExecutor;
import com.astrsomn.workflow.starter.runtime.executor.ToolNodeExecutor;
import com.astrsomn.workflow.starter.runtime.plan.DefaultAstFlowPlanResolver;
import com.astrsomn.workflow.starter.runtime.policy.NoopAstFlowRateLimitPolicy;
import com.astrsomn.workflow.starter.runtime.policy.NoopAstFlowRetryPolicy;
import com.astrsomn.workflow.starter.runtime.policy.NoopAstFlowTimeoutPolicy;
import com.astrsomn.workflow.starter.runtime.registry.DefaultAstFlowNodeExecutorRegistry;
import com.astrsomn.workflow.starter.runtime.state.DefaultAstFlowExecutionStateMachine;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Workflow starter 的补充自动配置：
 * 在不改业务方配置的情况下，将 workflow entity 包自动并入 typeAliasesPackage。
 */
@AutoConfiguration
@MapperScan("com.astrsomn.workflow.starter.mapper")
@ConditionalOnClass(AstrsomnProperties.class)
@ConditionalOnProperty(prefix = "astrsomn.data-base", name = "database-type")
@AutoConfigureBefore(name = "com.astrsomn.starter.config.AstrsomnAutoConfiguration")
public class AstrsomnWorkflowAutoConfiguration {

    private static final String WORKFLOW_ENTITY_PACKAGE = "com.astrsomn.workflow.core.domain.entity";

    @Bean
    public BeanPostProcessor astrsomnWorkflowMybatisDefaultsPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                if (!(bean instanceof AstrsomnProperties properties)) {
                    return bean;
                }

                AstrsomnProperties.MybatisPlus mybatisPlus = properties.getMybatisPlus();
                if (mybatisPlus == null) {
                    return bean;
                }

                String additionalTypeAliasesPackage = mybatisPlus.getAdditionalTypeAliasesPackage();
                if (additionalTypeAliasesPackage == null || additionalTypeAliasesPackage.isBlank()) {
                    mybatisPlus.setAdditionalTypeAliasesPackage(WORKFLOW_ENTITY_PACKAGE);
                    return bean;
                }

                if (!additionalTypeAliasesPackage.contains(WORKFLOW_ENTITY_PACKAGE)) {
                    mybatisPlus.setAdditionalTypeAliasesPackage(
                            additionalTypeAliasesPackage + "," + WORKFLOW_ENTITY_PACKAGE
                    );
                }
                return bean;
            }
        };
    }

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
