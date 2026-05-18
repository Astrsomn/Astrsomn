/**
 * 工作流节点类型枚举 - 与后端 AstWorkflowEnum.NodeTypeEnum 对齐（英文文案）
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const workflowNodeTypeLabels = {
    START: 'Start Node',
    LLM: 'LLM Node',
    TOOL: 'Tool Node',
    CONDITION: 'Condition Node',
    HUMAN: 'Human Node',
    END: 'End Node'
} as const

export const aiWorkflowNodeTypeDictionary = createEnumDictionary({
    id: 'ai-workflow.nodeType',
    labels: workflowNodeTypeLabels,
    order: ['START', 'LLM', 'TOOL', 'CONDITION', 'HUMAN', 'END']
})

const workflowExecutionStatusLabels = {
    RUNNING: 'Running',
    SUSPENDED: 'Suspended',
    COMPLETED: 'Completed',
    FAILED: 'Failed'
} as const

export const aiWorkflowExecutionStatusDictionary = createEnumDictionary({
    id: 'ai-workflow.executionStatus',
    labels: workflowExecutionStatusLabels,
    order: ['RUNNING', 'SUSPENDED', 'COMPLETED', 'FAILED']
})

const humanTaskStatusLabels = {
    PENDING: 'Pending',
    APPROVED: 'Approved',
    REJECTED: 'Rejected'
} as const

export const aiWorkflowHumanTaskStatusDictionary = createEnumDictionary({
    id: 'ai-workflow.humanTaskStatus',
    labels: humanTaskStatusLabels,
    order: ['PENDING', 'APPROVED', 'REJECTED']
})
