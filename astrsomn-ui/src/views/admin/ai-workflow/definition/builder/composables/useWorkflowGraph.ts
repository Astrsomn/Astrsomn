import { computed, ref } from 'vue'
import { addEdge, MarkerType, type Connection, type Edge, type Node } from '@vue-flow/core'
import { createNodeData, defaultGraph } from '../constants'
import type { WorkflowEdge, WorkflowGraph, WorkflowNode, WorkflowNodeType } from '../types'

export function useWorkflowGraph() {
  const nodes = ref<WorkflowNode[]>([...defaultGraph.nodes])
  const edges = ref<WorkflowEdge[]>([...defaultGraph.edges])
  const viewport = ref(defaultGraph.viewport ?? { x: 0, y: 0, zoom: 1 })
  const selectedNodeId = ref<string>()
  const selectedEdgeId = ref<string>()
  const nodeCounter = ref(nodes.value.length + 1)

  const selectedNode = computed(() => nodes.value.find((node) => node.id === selectedNodeId.value))
  const selectedEdge = computed(() => edges.value.find((edge) => edge.id === selectedEdgeId.value))

  const createNode = (nodeType: WorkflowNodeType, position: { x: number; y: number }) => {
    const id = `${nodeType}-${nodeCounter.value++}`
    const node: Node = {
      id,
      type: nodeType,
      position,
      data: createNodeData(nodeType)
    }
    nodes.value = [...nodes.value, node as WorkflowNode]
    selectedNodeId.value = id
    selectedEdgeId.value = undefined
  }

  const removeSelection = () => {
    if (selectedNodeId.value) {
      const targetNodeId = selectedNodeId.value
      nodes.value = nodes.value.filter((node) => node.id !== targetNodeId)
      edges.value = edges.value.filter((edge) => edge.source !== targetNodeId && edge.target !== targetNodeId)
      selectedNodeId.value = undefined
    } else if (selectedEdgeId.value) {
      const targetEdgeId = selectedEdgeId.value
      edges.value = edges.value.filter((edge) => edge.id !== targetEdgeId)
      selectedEdgeId.value = undefined
    }
  }

  const removeNodeById = (nodeId: string) => {
    nodes.value = nodes.value.filter((node) => node.id !== nodeId)
    edges.value = edges.value.filter((edge) => edge.source !== nodeId && edge.target !== nodeId)
    if (selectedNodeId.value === nodeId) selectedNodeId.value = undefined
  }

  const removeEdgeById = (edgeId: string) => {
    edges.value = edges.value.filter((edge) => edge.id !== edgeId)
    if (selectedEdgeId.value === edgeId) selectedEdgeId.value = undefined
  }

  const duplicateNodeById = (nodeId: string) => {
    const original = nodes.value.find((node) => node.id === nodeId)
    if (!original) return undefined
    const nextId = `${original.type}-${nodeCounter.value++}`
    const clone: WorkflowNode = {
      ...original,
      id: nextId,
      position: {
        x: original.position.x + 36,
        y: original.position.y + 36
      },
      selected: false,
      data: {
        ...(original.data || {}),
        label: `${original.data?.label || nextId}-副本`
      }
    } as WorkflowNode
    nodes.value = [...nodes.value, clone]
    selectedNodeId.value = nextId
    selectedEdgeId.value = undefined
    return nextId
  }

  const toggleNodeDisabled = (nodeId: string) => {
    nodes.value = nodes.value.map((node) => {
      if (node.id !== nodeId) return node
      const current = Boolean(node.data?.config?.disabled)
      const nextDisabled = !current
      return {
        ...node,
        data: {
          ...node.data,
          config: {
            ...(node.data?.config ?? {}),
            disabled: nextDisabled
          }
        }
      }
    })
  }

  const batchRemoveNodes = (nodeIds: string[]) => {
    const nodeSet = new Set(nodeIds)
    nodes.value = nodes.value.filter((node) => !nodeSet.has(node.id))
    edges.value = edges.value.filter((edge) => !nodeSet.has(edge.source) && !nodeSet.has(edge.target))
    if (selectedNodeId.value && nodeSet.has(selectedNodeId.value)) selectedNodeId.value = undefined
  }

  const batchDisableNodes = (nodeIds: string[]) => {
    const nodeSet = new Set(nodeIds)
    nodes.value = nodes.value.map((node) => {
      if (!nodeSet.has(node.id)) return node
      return {
        ...node,
        data: {
          ...node.data,
          config: {
            ...(node.data?.config ?? {}),
            disabled: true
          }
        }
      }
    })
  }

  const updateSelectedNode = (payload: { label?: string; description?: string; config?: Record<string, unknown> }) => {
    if (!selectedNodeId.value) return
    nodes.value = nodes.value.map((node) => {
      if (node.id !== selectedNodeId.value) return node
      return {
        ...node,
        data: {
          ...node.data,
          ...(payload.label != null ? { label: payload.label } : {}),
          ...(payload.description != null ? { description: payload.description } : {}),
          ...(payload.config != null ? { config: { ...(node.data?.config ?? {}), ...payload.config } } : {})
        }
      }
    })
  }

  const updateSelectedEdge = (payload: { label?: string }) => {
    if (!selectedEdgeId.value) return
    edges.value = edges.value.map((edge) => {
      if (edge.id !== selectedEdgeId.value) return edge
      return {
        ...edge,
        ...(payload.label != null ? { label: payload.label } : {})
      }
    })
  }

  const setEdgeLabelById = (edgeId: string, label: string) => {
    edges.value = edges.value.map((edge) => (edge.id === edgeId ? { ...edge, label } : edge))
  }

  const updateEdgeStyleById = (edgeId: string, edgeType: 'smoothstep' | 'straight') => {
    edges.value = edges.value.map((edge) => (edge.id === edgeId ? { ...edge, type: edgeType } : edge))
  }

  const onConnect = (connection: Connection) => {
    if (!connection.source || !connection.target) {
      return { ok: false, message: '连线缺少源节点或目标节点' }
    }
    if (connection.source === connection.target) {
      return { ok: false, message: '不支持节点自连接' }
    }

    const duplicated = edges.value.some(
      (edge) =>
        edge.source === connection.source &&
        edge.target === connection.target &&
        (edge.sourceHandle || '') === (connection.sourceHandle || '') &&
        (edge.targetHandle || '') === (connection.targetHandle || '')
    )
    if (duplicated) {
      return { ok: false, message: '相同连线已存在' }
    }

    const sourceNode = nodes.value.find((node) => node.id === connection.source)
    if (sourceNode?.type === 'condition') {
      if (!connection.sourceHandle || (connection.sourceHandle !== 'true' && connection.sourceHandle !== 'false')) {
        return { ok: false, message: '条件节点必须从 true/false 分支发出连线' }
      }
    }

    if (sourceNode?.type === 'parallel') {
      const branchCount = Number(sourceNode.data?.config?.branchCount ?? 2)
      const outgoing = edges.value.filter((edge) => edge.source === sourceNode.id).length
      if (outgoing >= branchCount) {
        return { ok: false, message: `并行节点最多允许 ${branchCount} 条分支` }
      }
    }

    const next = addEdge(
      {
        ...connection,
        type: 'smoothstep',
        markerEnd: MarkerType.ArrowClosed
      },
      edges.value as Edge[]
    ) as WorkflowEdge[]
    edges.value = next
    return { ok: true as const }
  }

  const validateGraph = () => {
    const errors: string[] = []
    const startCount = nodes.value.filter((node) => node.type === 'start').length
    const endCount = nodes.value.filter((node) => node.type === 'end').length

    if (startCount !== 1) errors.push('流程必须且仅能有一个开始节点')
    if (endCount < 1) errors.push('流程至少需要一个结束节点')

    nodes.value.forEach((node) => {
      if (node.type === 'condition') {
        const outgoing = edges.value.filter((edge) => edge.source === node.id)
        const hasTrue = outgoing.some((edge) => edge.sourceHandle === 'true')
        const hasFalse = outgoing.some((edge) => edge.sourceHandle === 'false')
        if (!hasTrue || !hasFalse) {
          errors.push(`条件节点 ${node.data?.label || node.id} 需要 true/false 两条分支`)
        }
      }
      if (node.type === 'parallel') {
        const outgoingCount = edges.value.filter((edge) => edge.source === node.id).length
        const branchCount = Number(node.data?.config?.branchCount ?? 2)
        if (outgoingCount < branchCount) {
          errors.push(`并行节点 ${node.data?.label || node.id} 分支不足，期望 ${branchCount} 条`)
        }
      }
    })

    return { ok: errors.length === 0, errors }
  }

  const loadGraph = (graphJson?: string) => {
    if (!graphJson) return
    try {
      const parsed = JSON.parse(graphJson) as WorkflowGraph
      if (Array.isArray(parsed.nodes)) nodes.value = parsed.nodes
      if (Array.isArray(parsed.edges)) edges.value = parsed.edges
      if (parsed.viewport) viewport.value = parsed.viewport
      nodeCounter.value = nodes.value.length + 1
      selectedNodeId.value = undefined
      selectedEdgeId.value = undefined
    } catch {
      // ignore invalid graph content for initial scaffold stage
    }
  }

  const toGraphJson = (meta?: WorkflowGraph['meta']) => {
    return JSON.stringify(
      {
        nodes: nodes.value,
        edges: edges.value,
        viewport: viewport.value,
        meta: meta ?? {}
      },
      null,
      2
    )
  }

  return {
    nodes,
    edges,
    viewport,
    selectedNodeId,
    selectedEdgeId,
    selectedNode,
    selectedEdge,
    createNode,
    removeSelection,
    removeNodeById,
    removeEdgeById,
    duplicateNodeById,
    toggleNodeDisabled,
    batchRemoveNodes,
    batchDisableNodes,
    updateSelectedNode,
    updateSelectedEdge,
    setEdgeLabelById,
    updateEdgeStyleById,
    onConnect,
    validateGraph,
    loadGraph,
    toGraphJson
  }
}
