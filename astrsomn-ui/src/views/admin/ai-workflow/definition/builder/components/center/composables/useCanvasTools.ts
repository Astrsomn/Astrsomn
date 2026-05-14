import {computed, ref} from 'vue'

type UseCanvasToolsParams = {
    canUndo: () => boolean
    canRedo: () => boolean
    onClearSelection: () => void
    onUndo: () => void
    onRedo: () => void
}

export function useCanvasTools(params: UseCanvasToolsParams) {
    const interactionMode = ref<'box' | 'pan'>('box')

    const setInteractionMode = (mode: 'box' | 'pan') => {
        interactionMode.value = mode
    }

    const clearSelection = () => {
        params.onClearSelection()
    }

    const undo = () => {
        if (!params.canUndo()) return
        params.onUndo()
    }

    const redo = () => {
        if (!params.canRedo()) return
        params.onRedo()
    }

    const canUndo = computed(() => params.canUndo())
    const canRedo = computed(() => params.canRedo())

    return {
        interactionMode,
        canUndo,
        canRedo,
        setInteractionMode,
        clearSelection,
        undo,
        redo
    }
}
