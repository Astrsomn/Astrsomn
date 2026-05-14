import {computed, ref} from 'vue'

export function useBuilderPage() {
    const leftCollapsed = ref(false)
    const compactNode = true
    const initialZoomMode: 'fit-compact' | 'normal' = 'fit-compact'

    const layoutColumns = computed(() => (leftCollapsed.value ? '0px minmax(680px, 1fr) 320px' : '360px minmax(680px, 1fr) 320px'))
    const layoutColumnsSmall = computed(() => (leftCollapsed.value ? '0px minmax(520px, 1fr) 300px' : '320px minmax(520px, 1fr) 300px'))

    return {
        leftCollapsed,
        compactNode,
        initialZoomMode,
        layoutColumns,
        layoutColumnsSmall
    }
}

