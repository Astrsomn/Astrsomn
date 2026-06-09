import {ref} from 'vue'
import type {ChatSessionItem} from '@/views/chat-index/utils/types'

/**
 * Chat 侧边栏共享状态
 * - sessionItems / sessionLoading / currentMemoryKey / sidebarCollapsed 由 chat-index 写入
 * - AdminSidebar 通过该 composable 读取并在 AstSidebar 中渲染
 *
 * 由于 open / create / delete 这些动作需要联动 chat-index 内部的消息、草稿、滚动等状态，
 * 这里采用 handler slot 模式：chat-index 在挂载时注册处理器，AdminSidebar 触发时回调。
 */
const sessionItems = ref<ChatSessionItem[]>([])
const sessionLoading = ref(false)
const currentMemoryKey = ref('')
const sidebarCollapsed = ref(false)

let onOpenHandler: ((memoryKey: string) => void | Promise<void>) | null = null
let onCreateHandler: (() => void) | null = null
let onDeleteHandler: ((session: ChatSessionItem) => void) | null = null

export function useChatSidebarState() {
  return {
    sessionItems,
    sessionLoading,
    currentMemoryKey,
    sidebarCollapsed,
    registerHandlers(handlers: {
      open?: (memoryKey: string) => void | Promise<void>
      create?: () => void
      delete?: (session: ChatSessionItem) => void
    }) {
      onOpenHandler = handlers.open || null
      onCreateHandler = handlers.create || null
      onDeleteHandler = handlers.delete || null
    },
    handleOpen(memoryKey: string) {
      return onOpenHandler?.(memoryKey)
    },
    handleCreate() {
      onCreateHandler?.()
    },
    handleDelete(session: ChatSessionItem) {
      onDeleteHandler?.(session)
    },
  }
}
