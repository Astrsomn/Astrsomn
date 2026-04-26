import type { Edge, Node, XYPosition } from '@vue-flow/core'

export type WorkflowNodeType = 'start' | 'llm' | 'tool' | 'condition' | 'end'

export type WorkflowNodeData = {
  label: string
  description?: string
  config?: Record<string, unknown>
}

export type WorkflowNode = Node<WorkflowNodeData>
export type WorkflowEdge = Edge

export type WorkflowMeta = {
  id?: number | string
  workflowName: string
  workflowKey: string
  description: string
}

export type WorkflowGraph = {
  nodes: WorkflowNode[]
  edges: WorkflowEdge[]
  viewport?: {
    x: number
    y: number
    zoom: number
  }
  meta?: Partial<WorkflowMeta>
}

export type NodePaletteItem = {
  type: WorkflowNodeType
  label: string
  description: string
}

export type NodePaletteGroup = {
  key: string
  title: string
  items: NodePaletteItem[]
}

export type NodeDropPayload = {
  type: WorkflowNodeType
  position: XYPosition
}
