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

  const onConnect = (connection: Connection) => {
    const next = addEdge(
      {
        ...connection,
        type: 'smoothstep',
        markerEnd: MarkerType.ArrowClosed
      },
      edges.value as Edge[]
    ) as WorkflowEdge[]
    edges.value = next
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
    updateSelectedNode,
    updateSelectedEdge,
    onConnect,
    loadGraph,
    toGraphJson
  }
}
