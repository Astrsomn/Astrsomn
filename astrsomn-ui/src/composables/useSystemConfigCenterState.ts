import {ref} from 'vue'

/**
 * 系统配置中心共享状态
 * 用于在全局 AdminSidebar 与系统配置中心 Index.vue 之间共享数据
 */
const activeView = ref<string>('all')
const sidebarCollapsed = ref(false)

export function useSystemConfigCenterState() {
  return {
    activeView,
    sidebarCollapsed,
  }
}
