import { NODE_DRAG_MIME } from '../constants'
import type { WorkflowNodeType } from '../types'

const workflowNodeTypes: WorkflowNodeType[] = [
  'start',
  'end',
  'parallel',
  'llm',
  'knowledge',
  'vision',
  'condition',
  'iterator',
  'template',
  'http',
  'code',
  'search',
  'human-audit',
  'input-form'
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
