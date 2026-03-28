package org.astrsomn.starter.workflow;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 注册工作流与 LangChain 集成 Bean（定义加载、编排器、TASK 节点助手回调）。
 */
@AutoConfiguration
@ComponentScan(basePackageClasses = WorkflowIntegrationAutoConfiguration.class)
public class WorkflowIntegrationAutoConfiguration {
}
