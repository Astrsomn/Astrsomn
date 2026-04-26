import type { Edge, Node, XYPosition } from '@vue-flow/core'

export type WorkflowNodeType =
  | 'start'
  | 'end'
  | 'parallel'
  | 'llm'
  | 'knowledge'
  | 'vision'
  | 'condition'
  | 'iterator'
  | 'template'
  | 'http'
  | 'code'
  | 'search'
  | 'human-audit'
  | 'input-form'

export type WorkflowValueType = 'String' | 'Number' | 'Boolean' | 'Array' | 'Object' | 'Any'

export type NodePort = {
  handleId: string
  valueType: WorkflowValueType
  sourceNode?: string
  value?: unknown
}

export type NodeConfigField = {
  key: string
  label: string
  component: 'input' | 'textarea' | 'number' | 'select' | 'switch'
  required?: boolean
  placeholder?: string
  options?: Array<{ label: string; value: string }>
  defaultValue?: unknown
}

export type WorkflowNodeData = {
  label: string
  description?: string
  category?: string
  nodeTypeTitle?: string
  config?: Record<string, unknown>
  inputs?: NodePort[]
  outputs?: NodePort[]
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

export type CanvasContextTarget = 'node' | 'edge' | 'pane'

export type CanvasContextMenuPayload = {
  target: CanvasContextTarget
  x: number
  y: number
  nodeId?: string
  edgeId?: string
}

export type PaletteContextMenuPayload = {
  x: number
  y: number
  type: WorkflowNodeType
  label: string
  description: string
}

export type WorkflowNodeDefinition = {
  type: WorkflowNodeType
  category: 'control' | 'ai' | 'logic' | 'tool' | 'interaction'
  title: string
  description: string
  inputs: NodePort[]
  outputs: NodePort[]
  configSchema: NodeConfigField[]
}
