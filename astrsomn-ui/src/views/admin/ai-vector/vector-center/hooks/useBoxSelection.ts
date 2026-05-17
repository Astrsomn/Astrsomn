import {type Ref, ref} from 'vue'

export interface BoxSelectionOptions {
  containerRef: Ref<HTMLElement | null>
  itemSelector?: string
}

export function useBoxSelection(options: BoxSelectionOptions) {
  const {containerRef, itemSelector = '[data-select-id]'} = options

  const selectedIds = ref<Set<string>>(new Set())
  const isDragging = ref(false)
  const selectionRect = ref<{ x: number; y: number; w: number; h: number } | null>(null)

  let startX = 0
  let startY = 0
  let dragStarted = false

  const clearSelection = () => {
    selectedIds.value = new Set()
  }

  const selectAll = (ids: string[]) => {
    selectedIds.value = new Set(ids)
  }

  const toggleSelect = (id: string, ctrlKey: boolean) => {
    const next = new Set(selectedIds.value)
    if (ctrlKey) {
      if (next.has(id)) {
        next.delete(id)
      } else {
        next.add(id)
      }
    } else {
      next.clear()
      next.add(id)
    }
    selectedIds.value = next
  }

  const selectRange = (ids: string[]) => {
    const next = new Set(selectedIds.value)
    for (const id of ids) {
      next.add(id)
    }
    selectedIds.value = next
  }

  const getIntersectingIds = (): string[] => {
    const rect = selectionRect.value
    const container = containerRef.value
    if (!rect || !container) return []

    const containerRect = container.getBoundingClientRect()
    const selLeft = containerRect.left + rect.x
    const selTop = containerRect.top + rect.y
    const selRight = selLeft + rect.w
    const selBottom = selTop + rect.h

    const items = container.querySelectorAll(itemSelector)
    const ids: string[] = []
    for (const el of items) {
      const elRect = el.getBoundingClientRect()
      const intersects = !(
        selRight < elRect.left ||
        selLeft > elRect.right ||
        selBottom < elRect.top ||
        selTop > elRect.bottom
      )
      if (intersects) {
        const id = el.getAttribute('data-select-id')
        if (id) ids.push(id)
      }
    }
    return ids
  }

  const onMouseDown = (e: MouseEvent) => {
    // Only start box selection on left click on the container itself (not on cards)
    if (e.button !== 0) return
    const target = e.target as HTMLElement
    if (target.closest(itemSelector)) return

    const container = containerRef.value
    if (!container) return

    const rect = container.getBoundingClientRect()
    startX = e.clientX - rect.left
    startY = e.clientY - rect.top
    dragStarted = false
    isDragging.value = true
    selectionRect.value = null

    document.addEventListener('mousemove', onMouseMove)
    document.addEventListener('mouseup', onMouseUp)
  }

  const onMouseMove = (e: MouseEvent) => {
    if (!isDragging.value) return
    const container = containerRef.value
    if (!container) return

    const rect = container.getBoundingClientRect()
    const currentX = e.clientX - rect.left
    const currentY = e.clientY - rect.top

    const dx = Math.abs(currentX - startX)
    const dy = Math.abs(currentY - startY)

    // Only start drawing after moving at least 5px
    if (!dragStarted && (dx < 5 && dy < 5)) return
    dragStarted = true

    selectionRect.value = {
      x: Math.min(startX, currentX),
      y: Math.min(startY, currentY),
      w: dx,
      h: dy
    }
  }

  const onMouseUp = () => {
    document.removeEventListener('mousemove', onMouseMove)
    document.removeEventListener('mouseup', onMouseUp)

    if (dragStarted) {
      const ids = getIntersectingIds()
      if (ids.length > 0) {
        selectRange(ids)
      }
    }

    isDragging.value = false
    selectionRect.value = null
    dragStarted = false
  }

  const isSelected = (id: string) => selectedIds.value.has(id)

  return {
    selectedIds,
    isDragging,
    selectionRect,
    clearSelection,
    selectAll,
    toggleSelect,
    selectRange,
    isSelected,
    onMouseDown
  }
}
