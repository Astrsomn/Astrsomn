import {ref} from 'vue'

/**
 * 向量中心 Sidebar 共享状态
 * 数据状态（sources/stores/docs/...）由 useVectorCenterState 提供
 * 这里只保存与全局 AdminSidebar 协作所需的状态
 */
const sidebarCollapsed = ref(false)

export function useVectorCenterSidebarState() {
  return {
    sidebarCollapsed,
  }
}
