import {computed, ref} from 'vue'

export function useSidebarCollapse(options?: {
  defaultCollapsed?: boolean
  collapsedWidth?: number
  expandedWidth?: number
}) {
  const collapsed = ref(options?.defaultCollapsed ?? false)
  const collapsedWidth = options?.collapsedWidth ?? 64
  const expandedWidth = options?.expandedWidth ?? 320

  const toggleCollapsed = () => {
    collapsed.value = !collapsed.value
  }

  const sidebarWidth = computed(() => collapsed.value ? collapsedWidth : expandedWidth)

  return {collapsed, toggleCollapsed, sidebarWidth, collapsedWidth, expandedWidth}
}
