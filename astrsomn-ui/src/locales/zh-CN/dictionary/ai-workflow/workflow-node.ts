/**
 * 工作流节点类型枚举 - 与后端 AstWorkflowEnum.NodeTypeEnum 对齐（中文文案）
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const workflowNodeTypeLabels = {
    START: '开始节点',
    LLM: 'LLM节点',
    TOOL: '工具节点',
    CONDITION: '条件节点',
    HUMAN: '人工节点',
    END: '结束节点'
} as const

export const aiWorkflowNodeTypeDictionary = createEnumDictionary({
    id: 'ai-workflow.nodeType',
    labels: workflowNodeTypeLabels,
    order: ['START', 'LLM', 'TOOL', 'CONDITION', 'HUMAN', 'END']
})

const workflowExecutionStatusLabels = {
    RUNNING: '运行中',
    SUSPENDED: '挂起',
    COMPLETED: '已完成',
    FAILED: '失败'
} as const

export const aiWorkflowExecutionStatusDictionary = createEnumDictionary({
    id: 'ai-workflow.executionStatus',
    labels: workflowExecutionStatusLabels,
    order: ['RUNNING', 'SUSPENDED', 'COMPLETED', 'FAILED']
})

const humanTaskStatusLabels = {
    PENDING: '待处理',
    APPROVED: '已通过',
    REJECTED: '已拒绝'
} as const

export const aiWorkflowHumanTaskStatusDictionary = createEnumDictionary({
    id: 'ai-workflow.humanTaskStatus',
    labels: humanTaskStatusLabels,
    order: ['PENDING', 'APPROVED', 'REJECTED']
})
