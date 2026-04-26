/**
 * TODO 这是类型中心。定义了整个工作流编辑器的数据契约：节点类型、端口类型、节点数据、边数据、图结构、菜单 payload 等。
 *      其他 3 个文件都依赖这里的类型，保证调用时有统一约束。
 */

import type { XYPosition } from '@vue-flow/core'

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

export type WorkflowNode = {
  id: string
  type?: WorkflowNodeType | string
  position: XYPosition
  data: WorkflowNodeData
  selected?: boolean
  [key: string]: unknown
}
export type WorkflowEdge = {
  id?: string
  source: string
  target: string
  sourceHandle?: string | null
  targetHandle?: string | null
  label?: string
  type?: string
  [key: string]: unknown
}

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

export type LeftViewMode = 'workflow-list' | 'model-chat'

export type WorkflowListItem = {
  id: string
  workflowName: string
  workflowKey?: string
  description?: string
}

export type CanvasPaletteIconItem = {
  key: string
  label: string
  description: string
  children: NodePaletteItem[]
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
