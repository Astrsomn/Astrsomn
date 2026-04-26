import type { WorkflowGraph, WorkflowNodeType, NodePaletteGroup } from './types'

export const NODE_DRAG_MIME = 'application/astrsomn-workflow-node'

const nodeDisplayMap: Record<WorkflowNodeType, { label: string; description: string }> = {
  start: { label: '开始', description: '流程起点' },
  llm: { label: 'LLM', description: '大模型调用节点' },
  tool: { label: 'Tool', description: '外部工具调用节点' },
  condition: { label: '条件分支', description: '根据条件进行分支' },
  end: { label: '结束', description: '流程终点' }
}

export const paletteGroups: NodePaletteGroup[] = [
  {
    key: 'base',
    title: '基础节点',
    items: [toPaletteItem('start'), toPaletteItem('end')]
  },
  {
    key: 'execute',
    title: '执行节点',
    items: [toPaletteItem('llm'), toPaletteItem('tool')]
  },
  {
    key: 'logic',
    title: '逻辑节点',
    items: [toPaletteItem('condition')]
  }
]

export const defaultGraph: WorkflowGraph = {
  nodes: [
    {
      id: 'start-1',
      type: 'start',
      position: { x: 120, y: 180 },
      data: { label: '开始', description: '工作流入口' }
    },
    {
      id: 'end-1',
      type: 'end',
      position: { x: 420, y: 180 },
      data: { label: '结束', description: '工作流出口' }
    }
  ],
  edges: [
    {
      id: 'edge-start-1-end-1',
      source: 'start-1',
      target: 'end-1',
      label: '默认路径'
    }
  ],
  viewport: { x: 0, y: 0, zoom: 1 }
}

export function createNodeData(type: WorkflowNodeType) {
  const display = nodeDisplayMap[type]
  return {
    label: display.label,
    description: display.description,
    config: {}
  }
}

function toPaletteItem(type: WorkflowNodeType) {
  return {
    type,
    label: nodeDisplayMap[type].label,
    description: nodeDisplayMap[type].description
  }
}
