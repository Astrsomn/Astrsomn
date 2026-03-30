package org.astrsomn.starter.workflow;

import lombok.RequiredArgsConstructor;
import org.astrsomn.workflow.core.context.WorkflowContext;
import org.astrsomn.workflow.core.context.WorkflowExecutionResult;
import org.astrsomn.workflow.core.engine.WorkflowEngine;
import org.astrsomn.workflow.core.model.WorkflowDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

/**
 * 从库加载已发布定义并执行。需同时引入 {@code astrsomn-workflow-spring-boot-starter} 以提供 {@link WorkflowEngine} Bean。
 */
@Service
@RequiredArgsConstructor
@ConditionalOnBean(WorkflowEngine.class)
public class WorkflowChatOrchestrator {

    private final WorkflowDefinitionService workflowDefinitionService;
    private final WorkflowEngine workflowEngine;

    /**
     * @param workflowKey 对应 {@code AI_WORKFLOW.WORKFLOW_KEY}，且 {@code STATUS=PUBLISHED}
     * @param context     建议至少放入 {@code userMessage}、{@code memoryKey}（与 TASK 节点 promptVar/memoryVar 一致）
     */
    public WorkflowExecutionResult runPublishedWorkflow(String workflowKey, WorkflowContext context) {
        WorkflowDefinition def = workflowDefinitionService.loadPublished(workflowKey);
        return workflowEngine.execute(def, context);
    }
}
