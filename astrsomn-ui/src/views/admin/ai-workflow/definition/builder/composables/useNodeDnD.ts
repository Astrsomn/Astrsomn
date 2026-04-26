/**
 * TODO 这是拖拽桥接层（DnD 小工具）：
    startDrag：开始拖拽时把节点类型写进 dataTransfer
    parseDropType：在 drop 时读取并校验类型是否合法
    它不管业务状态，只负责拖拽数据在“面板 ↔ 画布”间传递。
 */


import { NODE_DRAG_MIME } from '../domain/dnd-constants'
import type { WorkflowNodeType } from '../domain/types.ts'

const workflowNodeTypes: WorkflowNodeType[] = [
  'start',
  'end',
  'parallel',
  'llm',
  'retrieval',
  'if-else',
  'intent-classifier',
  'merge',
  'http',
  'code',
  'tools'
]

export function useNodeDnD() {
  const startDrag = (ev: DragEvent, nodeType: WorkflowNodeType) => {
    if (!ev.dataTransfer) return
    ev.dataTransfer.setData(NODE_DRAG_MIME, nodeType)
    ev.dataTransfer.effectAllowed = 'copy'
  }

  const parseDropType = (ev: DragEvent): WorkflowNodeType | null => {
    const raw = ev.dataTransfer?.getData(NODE_DRAG_MIME)
    if (!raw) return null
    if (workflowNodeTypes.includes(raw as WorkflowNodeType)) {
      return raw as WorkflowNodeType
    }
    return null
  }

  return {
    startDrag,
    parseDropType
  }
}
