import type {Connection} from '@vue-flow/core'
import type {WorkflowEdge, WorkflowNode} from './types'

export const validateConnection = (connection: Connection, nodes: WorkflowNode[], edges: WorkflowEdge[]) => {
    if (!connection.source || !connection.target) {
        return {ok: false, message: '连线缺少源节点或目标节点' as const}
    }
    if (connection.source === connection.target) {
        return {ok: false, message: '不支持节点自连接' as const}
    }

    const duplicated = edges.some(
        (edge) =>
            edge.source === connection.source &&
            edge.target === connection.target &&
            (edge.sourceHandle || '') === (connection.sourceHandle || '') &&
            (edge.targetHandle || '') === (connection.targetHandle || '')
    )
    if (duplicated) {
        return {ok: false, message: '相同连线已存在' as const}
    }

    const sourceNode = nodes.find((node) => node.id === connection.source)
    if (sourceNode?.type === 'if-else') {
        if (!connection.sourceHandle || (connection.sourceHandle !== 'true' && connection.sourceHandle !== 'false')) {
            return {ok: false, message: '条件节点必须从 true/false 分支发出连线' as const}
        }
    }

    if (sourceNode?.type === 'parallel') {
        const branchCount = Number(sourceNode.data?.config?.branchCount ?? 2)
        const outgoing = edges.filter((edge) => edge.source === sourceNode.id).length
        if (outgoing >= branchCount) {
            return {ok: false, message: `并行节点最多允许 ${branchCount} 条分支` as const}
        }
    }

    return {ok: true as const}
}

export const validateGraphState = (nodes: WorkflowNode[], edges: WorkflowEdge[]) => {
    const errors: string[] = []
    const startCount = nodes.filter((node) => node.type === 'start').length
    const endCount = nodes.filter((node) => node.type === 'end').length

    if (startCount !== 1) errors.push('流程必须且仅能有一个开始节点')
    if (endCount < 1) errors.push('流程至少需要一个结束节点')

    nodes.forEach((node) => {
        if (node.type === 'if-else') {
            const outgoing = edges.filter((edge) => edge.source === node.id)
            const hasTrue = outgoing.some((edge) => edge.sourceHandle === 'true')
            const hasFalse = outgoing.some((edge) => edge.sourceHandle === 'false')
            if (!hasTrue || !hasFalse) {
                errors.push(`条件节点 ${node.data?.label || node.id} 需要 true/false 两条分支`)
            }
        }
        if (node.type === 'parallel') {
            const outgoingCount = edges.filter((edge) => edge.source === node.id).length
            const branchCount = Number(node.data?.config?.branchCount ?? 2)
            if (outgoingCount < branchCount) {
                errors.push(`并行节点 ${node.data?.label || node.id} 分支不足，期望 ${branchCount} 条`)
            }
        }
    })

    return {ok: errors.length === 0, errors}
}

