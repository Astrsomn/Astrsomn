/**
 * 工作流实例状态枚举 - 与后端 AstFlowInstanceStateEnum 对齐（中文文案）
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const workflowInstanceStateLabels = {
    CREATED: '已创建',
    RUNNING: '运行中',
    SUSPENDED: '已挂起',
    COMPLETED: '已完成',
    FAILED: '失败'
} as const

export const aiWorkflowInstanceStateDictionary = createEnumDictionary({
    id: 'ai-workflow.instance.state',
    labels: workflowInstanceStateLabels,
    order: ['CREATED', 'RUNNING', 'SUSPENDED', 'COMPLETED', 'FAILED']
})

const workflowNodeStateLabels = {
    READY: '就绪',
    RUNNING: '运行中',
    SUCCEEDED: '成功',
    FAILED: '失败',
    WAITING_HUMAN: '等待人工',
    SKIPPED: '已跳过'
} as const

export const aiWorkflowNodeStateDictionary = createEnumDictionary({
    id: 'ai-workflow.node.state',
    labels: workflowNodeStateLabels,
    order: ['READY', 'RUNNING', 'SUCCEEDED', 'FAILED', 'WAITING_HUMAN', 'SKIPPED']
})
