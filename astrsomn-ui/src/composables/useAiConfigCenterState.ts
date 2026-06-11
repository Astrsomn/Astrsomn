import {ref} from 'vue'

/**
 * AI 配置中心共享状态
 * 用于在全局 AdminSidebar 与 AI 配置中心 Index.vue 之间共享数据
 */
export interface AiConfigProvider {
  id: string | number
  key: string
  name: string
  description: string
  avatar: string
}

const selectedProvider = ref<AiConfigProvider | null>(null)
const sidebarCollapsed = ref(false)

export function useAiConfigCenterState() {
  return {
    selectedProvider,
    sidebarCollapsed,
  }
}
