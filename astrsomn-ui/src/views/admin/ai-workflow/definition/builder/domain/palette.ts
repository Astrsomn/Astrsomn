import { nodeDefinitionMap } from './node-definitions'
import type { CanvasPaletteIconItem, NodePaletteGroup, WorkflowNodeType } from './types'

function toPaletteItem(type: WorkflowNodeType) {
  const definition = nodeDefinitionMap[type]
  return {
    type,
    label: definition.title,
    description: definition.description
  }
}

export const paletteGroups: NodePaletteGroup[] = [
  {
    key: 'base',
    title: '基础/入口',
    items: [toPaletteItem('start')]
  },
  {
    key: 'ai',
    title: 'AI 核心',
    items: [toPaletteItem('llm')]
  },
  {
    key: 'retrieval',
    title: '知识检索',
    items: [toPaletteItem('retrieval')]
  },
  {
    key: 'logic',
    title: '逻辑控制',
    items: [toPaletteItem('if-else'), toPaletteItem('intent-classifier'), toPaletteItem('parallel')]
  },
  {
    key: 'data',
    title: '数据加工',
    items: [toPaletteItem('code'), toPaletteItem('merge')]
  },
  {
    key: 'ext',
    title: '外部扩展',
    items: [toPaletteItem('http'), toPaletteItem('tools')]
  },
  {
    key: 'end',
    title: '结束/输出',
    items: [toPaletteItem('end')]
  }
]

export const canvasPaletteIcons: CanvasPaletteIconItem[] = [
  {
    key: 'control',
    label: '基础',
    description: '开始与结束节点',
    children: [toPaletteItem('start'), toPaletteItem('end')]
  },
  {
    key: 'ai',
    label: 'AI',
    description: 'LLM 与知识检索节点',
    children: [toPaletteItem('llm'), toPaletteItem('retrieval')]
  },
  {
    key: 'logic',
    label: '逻辑',
    description: '条件分支、意图分类与并行控制',
    children: [toPaletteItem('if-else'), toPaletteItem('intent-classifier'), toPaletteItem('parallel')]
  },
  {
    key: 'tool',
    label: '加工',
    description: '代码执行与变量聚合',
    children: [toPaletteItem('code'), toPaletteItem('merge')]
  },
  {
    key: 'interaction',
    label: '扩展',
    description: 'HTTP 与工具调用节点',
    children: [toPaletteItem('http'), toPaletteItem('tools')]
  }
]

