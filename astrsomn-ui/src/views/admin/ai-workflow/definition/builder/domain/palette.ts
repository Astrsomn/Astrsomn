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
    key: 'control',
    title: '控制类节点',
    items: [toPaletteItem('start'), toPaletteItem('end'), toPaletteItem('parallel')]
  },
  {
    key: 'ai',
    title: 'AI 核心类节点',
    items: [toPaletteItem('llm'), toPaletteItem('knowledge'), toPaletteItem('vision')]
  },
  {
    key: 'logic',
    title: '逻辑处理类节点',
    items: [toPaletteItem('condition'), toPaletteItem('iterator'), toPaletteItem('template')]
  },
  {
    key: 'tool',
    title: '工具与扩展节点',
    items: [toPaletteItem('http'), toPaletteItem('code'), toPaletteItem('search')]
  },
  {
    key: 'interaction',
    title: '前端交互节点',
    items: [toPaletteItem('human-audit'), toPaletteItem('input-form')]
  }
]

export const canvasPaletteIcons: CanvasPaletteIconItem[] = [
  {
    key: 'control',
    label: '控制',
    description: '开始、结束、并行等流程控制节点',
    children: [toPaletteItem('start'), toPaletteItem('end'), toPaletteItem('parallel')]
  },
  {
    key: 'ai',
    label: 'AI',
    description: 'LLM、知识库与视觉识别节点',
    children: [toPaletteItem('llm'), toPaletteItem('knowledge'), toPaletteItem('vision')]
  },
  {
    key: 'logic',
    label: '逻辑',
    description: '条件、迭代、模板拼接节点',
    children: [toPaletteItem('condition'), toPaletteItem('iterator'), toPaletteItem('template')]
  },
  {
    key: 'tool',
    label: '工具',
    description: 'HTTP、代码执行与搜索节点',
    children: [toPaletteItem('http'), toPaletteItem('code'), toPaletteItem('search')]
  },
  {
    key: 'interaction',
    label: '交互',
    description: '人工审核和输入增强节点',
    children: [toPaletteItem('human-audit'), toPaletteItem('input-form')]
  }
]

