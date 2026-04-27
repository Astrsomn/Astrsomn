export type ChatSessionItem = {
  id?: number | string
  memoryKey: string
  title: string
  preview: string
  updateTime?: string
  createTime?: string
  messageCount?: number
  raw?: Record<string, unknown>
}
