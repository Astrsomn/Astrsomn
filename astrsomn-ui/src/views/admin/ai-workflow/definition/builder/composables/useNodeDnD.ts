import { NODE_DRAG_MIME } from '../constants'
import type { WorkflowNodeType } from '../types'

export function useNodeDnD() {
  const startDrag = (ev: DragEvent, nodeType: WorkflowNodeType) => {
    if (!ev.dataTransfer) return
    ev.dataTransfer.setData(NODE_DRAG_MIME, nodeType)
    ev.dataTransfer.effectAllowed = 'copy'
  }

  const parseDropType = (ev: DragEvent): WorkflowNodeType | null => {
    const raw = ev.dataTransfer?.getData(NODE_DRAG_MIME)
    if (!raw) return null
    if (raw === 'start' || raw === 'llm' || raw === 'tool' || raw === 'condition' || raw === 'end') {
      return raw
    }
    return null
  }

  return {
    startDrag,
    parseDropType
  }
}
