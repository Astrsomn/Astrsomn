import type { WorkflowGraph } from './types'

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

