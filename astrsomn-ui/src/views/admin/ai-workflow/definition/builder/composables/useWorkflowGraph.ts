/**
 * TODO 这是核心状态与行为层（最核心）：
 *      管理响应式状态：nodes、edges、viewport、选中节点/边
 *      图编辑动作：增删节点、删边、复制节点、批量禁用/删除、更新节点/边属性
 *      连线规则：防自连、防重复、条件节点 true/false 约束、并行分支数量约束
 *      校验与序列化：validateGraph、loadGraph、toGraphJson
 *      可以把它看作 builder 的“store + service”（只是用 composable 实现）。
 */

import { computed, ref } from 'vue'
import { addEdge, MarkerType, type Connection, type Edge } from '@vue-flow/core'
import { defaultGraph } from '../domain/graph-default'
import { validateConnection, validateGraphState } from '../domain/graph-rules'
import { parseGraphJson, stringifyGraphJson } from '../domain/graph-serializer'
import { createNodeData } from '../domain/node-data-factory'
import type { WorkflowEdge, WorkflowGraph, WorkflowNode, WorkflowNodeData, WorkflowNodeType } from '../domain/types.ts'

const ensureNodeData = (node: WorkflowNode): WorkflowNodeData => {
  return {
    ...(node.data ?? {}),
    label: node.data?.label ?? node.id
  }
}

export function useWorkflowGraph() {
  const nodes = ref<WorkflowNode[]>([...defaultGraph.nodes])
  const edges = ref<WorkflowEdge[]>([...defaultGraph.edges])
  const viewport = ref(defaultGraph.viewport ?? { x: 0, y: 0, zoom: 1 })
  const selectedNodeId = ref<string>()
  const selectedEdgeId = ref<string>()
  const nodeCounter = ref(nodes.value.length + 1)

  const selectedNode = computed((): WorkflowNode | undefined => {
    const targetId = selectedNodeId.value
    if (!targetId) return undefined
    const nodeList = nodes.value as unknown as WorkflowNode[]
    for (const node of nodeList) {
      if ((node as { id: string }).id === targetId) return node
    }
    return undefined
  })
  const selectedEdge = computed((): WorkflowEdge | undefined => {
    const targetId = selectedEdgeId.value
    if (!targetId) return undefined
    const edgeList = edges.value as unknown as WorkflowEdge[]
    for (const edge of edgeList) {
      if ((edge as { id?: string }).id === targetId) return edge
    }
    return undefined
  })

  const createNode = (nodeType: WorkflowNodeType, position: { x: number; y: number }) => {
    const id = `${nodeType}-${nodeCounter.value++}`
    const node: WorkflowNode = {
      id,
      type: nodeType,
      position,
      data: createNodeData(nodeType)
    }
    nodes.value = ([...(nodes.value as unknown[]), node] as unknown) as WorkflowNode[]
    selectedNodeId.value = id
    selectedEdgeId.value = undefined
  }

  const removeSelection = () => {
    if (selectedNodeId.value) {
      const targetNodeId = selectedNodeId.value
      nodes.value = (nodes.value as unknown as WorkflowNode[]).filter((node) => node.id !== targetNodeId)
      edges.value = edges.value.filter((edge) => edge.source !== targetNodeId && edge.target !== targetNodeId)
      selectedNodeId.value = undefined
    } else if (selectedEdgeId.value) {
      const targetEdgeId = selectedEdgeId.value
      edges.value = edges.value.filter((edge) => edge.id !== targetEdgeId)
      selectedEdgeId.value = undefined
    }
  }

  const removeNodeById = (nodeId: string) => {
    nodes.value = (nodes.value as unknown as WorkflowNode[]).filter((node) => node.id !== nodeId)
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
    nodes.value = (nodes.value as unknown as WorkflowNode[]).map((node) => {
      if (node.id !== nodeId) return node
      const current = Boolean(node.data?.config?.disabled)
      const nextDisabled = !current
      const baseData = ensureNodeData(node)
      return {
        ...node,
        data: {
          ...baseData,
          config: {
            ...(baseData.config ?? {}),
            disabled: nextDisabled
          }
        }
      }
    })
  }

  const batchRemoveNodes = (nodeIds: string[]) => {
    const nodeSet = new Set(nodeIds)
    nodes.value = (nodes.value as unknown as WorkflowNode[]).filter((node) => !nodeSet.has(node.id))
    edges.value = edges.value.filter((edge) => !nodeSet.has(edge.source) && !nodeSet.has(edge.target))
    if (selectedNodeId.value && nodeSet.has(selectedNodeId.value)) selectedNodeId.value = undefined
  }

  const batchDisableNodes = (nodeIds: string[]) => {
    const nodeSet = new Set(nodeIds)
    nodes.value = (nodes.value as unknown as WorkflowNode[]).map((node) => {
      if (!nodeSet.has(node.id)) return node
      const baseData = ensureNodeData(node)
      return {
        ...node,
        data: {
          ...baseData,
          config: {
            ...(baseData.config ?? {}),
            disabled: true
          }
        }
      }
    })
  }

  const updateSelectedNode = (payload: { label?: string; description?: string; config?: Record<string, unknown> }) => {
    if (!selectedNodeId.value) return
    nodes.value = (nodes.value as unknown as WorkflowNode[]).map((node) => {
      if (node.id !== selectedNodeId.value) return node
      const baseData = ensureNodeData(node)
      return {
        ...node,
        data: {
          ...baseData,
          ...(payload.label != null ? { label: payload.label } : {}),
          ...(payload.description != null ? { description: payload.description } : {}),
          ...(payload.config != null ? { config: { ...(baseData.config ?? {}), ...payload.config } } : {})
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
    const result = validateConnection(connection, nodes.value, edges.value)
    if (!result.ok) return result

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
    return validateGraphState(nodes.value, edges.value)
  }

  const loadGraph = (graphJson?: string) => {
    const parsed = parseGraphJson(graphJson)
    if (!parsed) return
    if (Array.isArray(parsed.nodes)) nodes.value = parsed.nodes
    if (Array.isArray(parsed.edges)) edges.value = parsed.edges
    if (parsed.viewport) viewport.value = parsed.viewport
    nodeCounter.value = nodes.value.length + 1
    selectedNodeId.value = undefined
    selectedEdgeId.value = undefined
  }

  const toGraphJson = (meta?: WorkflowGraph['meta']) => {
    return stringifyGraphJson({
      nodes: nodes.value,
      edges: edges.value,
      viewport: viewport.value,
      meta
    })
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
