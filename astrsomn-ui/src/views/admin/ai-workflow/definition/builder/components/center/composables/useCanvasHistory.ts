import {computed, ref} from 'vue'
import type {CanvasGraphState} from '../../../domain/types'

type HistoryOptions = {
    capacity?: number
}

const cloneState = (snapshot: CanvasGraphState): CanvasGraphState => {
    return {
        nodes: JSON.parse(JSON.stringify(snapshot.nodes)),
        edges: JSON.parse(JSON.stringify(snapshot.edges)),
        canvasConfig: JSON.parse(JSON.stringify(snapshot.canvasConfig))
    }
}

export function useCanvasHistory(options: HistoryOptions = {}) {
    const capacity = Math.max(20, options.capacity ?? 80)
    const past = ref<CanvasGraphState[]>([])
    const present = ref<CanvasGraphState | null>(null)
    const future = ref<CanvasGraphState[]>([])
    let pushTimer: number | null = null

    const canUndo = computed(() => past.value.length > 0 && !!present.value)
    const canRedo = computed(() => future.value.length > 0 && !!present.value)

    const clearQueuedPush = () => {
        if (pushTimer != null) {
            window.clearTimeout(pushTimer)
            pushTimer = null
        }
    }

    const resetHistory = (snapshot: CanvasGraphState) => {
        clearQueuedPush()
        present.value = cloneState(snapshot)
        past.value = []
        future.value = []
    }

    const replacePresent = (snapshot: CanvasGraphState) => {
        present.value = cloneState(snapshot)
    }

    const pushSnapshot = (snapshot: CanvasGraphState) => {
        if (!present.value) {
            resetHistory(snapshot)
            return
        }
        past.value.push(cloneState(present.value))
        if (past.value.length > capacity) {
            past.value.splice(0, past.value.length - capacity)
        }
        present.value = cloneState(snapshot)
        future.value = []
    }

    const queuePushSnapshot = (snapshot: CanvasGraphState) => {
        clearQueuedPush()
        pushTimer = window.setTimeout(() => {
            pushSnapshot(snapshot)
            pushTimer = null
        }, 0)
    }

    const undo = () => {
        if (!canUndo.value || !present.value) return null
        const previous = past.value.pop()
        if (!previous) return null
        future.value.unshift(cloneState(present.value))
        present.value = cloneState(previous)
        return cloneState(present.value)
    }

    const redo = () => {
        if (!canRedo.value || !present.value) return null
        const next = future.value.shift()
        if (!next) return null
        past.value.push(cloneState(present.value))
        present.value = cloneState(next)
        return cloneState(present.value)
    }

    return {
        canUndo,
        canRedo,
        resetHistory,
        replacePresent,
        pushSnapshot,
        queuePushSnapshot,
        undo,
        redo
    }
}
