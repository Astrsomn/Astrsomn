/**
 * 工作流实例状态枚举 - 与后端 AstFlowInstanceStateEnum 对齐（英文文案）
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const workflowInstanceStateLabels = {
    CREATED: 'Created',
    RUNNING: 'Running',
    SUSPENDED: 'Suspended',
    COMPLETED: 'Completed',
    FAILED: 'Failed'
} as const

export const aiWorkflowInstanceStateDictionary = createEnumDictionary({
    id: 'ai-workflow.instance.state',
    labels: workflowInstanceStateLabels,
    order: ['CREATED', 'RUNNING', 'SUSPENDED', 'COMPLETED', 'FAILED']
})

const workflowNodeStateLabels = {
    READY: 'Ready',
    RUNNING: 'Running',
    SUCCEEDED: 'Succeeded',
    FAILED: 'Failed',
    WAITING_HUMAN: 'Waiting for Human',
    SKIPPED: 'Skipped'
} as const

export const aiWorkflowNodeStateDictionary = createEnumDictionary({
    id: 'ai-workflow.node.state',
    labels: workflowNodeStateLabels,
    order: ['READY', 'RUNNING', 'SUCCEEDED', 'FAILED', 'WAITING_HUMAN', 'SKIPPED']
})
