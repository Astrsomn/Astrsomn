package com.astrsomn.workflow.starter.config;

import com.astrsomn.starter.config.AstrsomnProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

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
}
