<template>
  <AdminPageShell
    title="AI 对话管理"
    description="管理 AI 对话记录，按 memoryKey 聚合展示，右侧查看详细对话内容。"
    empty-text="暂无对话记录。"
  >
    <div class="conversation-page">
      <AdminListToolbar>
        <template #left>
          <AstrsomnSearchPill
            v-model="query.memoryKey"
            placeholder="搜索 Memory Key"
            button-label="搜索"
            layout="toolbar"
            @search="fetchList"
          />

          <AstrsomnStateSwitch
            v-model="query.status"
            @change="fetchList"
            :options="[
              { label: '全部', value: undefined, color: '#1676fd', icon: CheckCircleOutlined },
              { label: '启用', value: 'enabled', color: '#10b981', icon: CheckCircleOutlined },
              { label: '禁用', value: 'disabled', color: '#f43f5e', icon: StopOutlined }
            ]"
          />
        </template>

        <template #right>
          <AstrsomnSegmentedButton :buttons="toolbarSegmentButtons" />
        </template>
      </AdminListToolbar>

      <div class="conversation-container">
        <!-- 左侧列表区域 -->
        <div class="left-panel">
          <AstrsomnOverview
            :list-length="list.length"
            :selected-count="selectedRowKeys.length"
            :all-current-selected="allCurrentSelected"
            :part-current-selected="partCurrentSelected"
            :show-actions="list.length > 0"
            :summary-text="`当前页 ${list.length} 条对话组，已选 ${selectedRowKeys.length} 条。`"
            @toggle-select-all="toggleSelectAllCurrentPage"
          />

          <a-spin :spinning="loading">
            <div class="conversation-cards">
              <ConversationCard
                v-for="item in list"
                :key="item.memoryKey || item.id"
                :conversation="item"
                :conversation-count="1"
                :latest-time="item.updateTime || item.createTime || ''"
                :is-selected="selectedRowKeys.includes(item.memoryKey || '')"
                @select="(memoryKey) => handleCardSelect(memoryKey)"
                @recover="(memoryKey) => handleRecoverConversation(memoryKey)"
                @delete="(memoryKey) => handleDeleteOne(memoryKey)"
              />
            </div>

            <div v-if="list.length === 0" class="empty-wrap">
              <a-empty description="暂无匹配的对话组" />
            </div>

            <div class="pagination-wrap">
              <a-pagination
                :current="page.pageNum"
                :page-size="page.pageSize"
                :total="page.total"
                :show-size-changer="false"
                @change="onPageChange"
              />
            </div>
          </a-spin>
        </div>

        <!-- 右侧对话内容区域 -->
        <div class="right-panel">
          <div v-if="selectedConversation" class="conversation-detail">
            <div class="detail-header">
              <h3>对话详情</h3>
              <div class="detail-info">
                <span class="info-item">Memory Key: {{ selectedConversation.memoryKey }}</span>
                <span class="info-item">创建时间: {{ formatTime(selectedConversation.createTime) }}</span>
                <span class="info-item">状态: 
                  <span class="status-pill" :class="`status-pill-${String(selectedConversation.status || '').toLowerCase()}`">
                    {{ getStatusLabel(selectedConversation.status) }}
                  </span>
                </span>
              </div>
            </div>
            
            <div class="conversation-content">
              <div v-if="parsedConversation" class="chat-messages">
                <div
                  v-for="(message, index) in parsedConversation"
                  :key="index"
                  class="chat-message"
                  :class="message.role === 'user' ? 'user-message' : 'ai-message'"
                >
                  <div class="message-role">{{ message.role === 'user' ? '用户' : 'AI' }}</div>
                  <div class="message-content">{{ message.content }}</div>
                  <div class="message-time">{{ formatTime(message.timestamp) }}</div>
                </div>
              </div>
              <div v-else class="empty-content">
                <a-empty description="暂无对话内容" />
              </div>
            </div>
          </div>
          <div v-else class="empty-detail">
            <a-empty description="请选择一个对话组查看详情" />
          </div>
        </div>
      </div>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  CheckCircleOutlined,
  CopyOutlined,
  DeleteOutlined,
  ReloadOutlined,
  StopOutlined
} from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AdminListToolbar from '@/components/home/AdminListToolbar.vue'
import AstrsomnOverview from '@/components/home/AstrsomnOverview.vue'
import AstrsomnStateSwitch from '@/components/home/AstrsomnStateSwitch.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import ConversationCard from './ConversationCard.vue'
import { aiConversationApi, type AiConversation, type PageResponse } from '@/api/aiConversation'

type QueryState = {
  memoryKey?: string
  status?: string
  createUser?: string
  startTime?: string
  endTime?: string
}

type ChatMessage = {
  role: 'user' | 'ai'
  content: string
  timestamp: string
}

const normalizeText = (value?: string, fallback = '—') => {
  const text = String(value || '').trim()
  return text || fallback
}

const getStatusLabel = (status?: string) => {
  const statusMap: Record<string, string> = {
    enabled: '启用',
    disabled: '禁用'
  }
  return statusMap[String(status || '').toLowerCase()] || normalizeText(status)
}

const formatTime = (time?: string) => {
  if (!time) return '—'
  try {
    const date = new Date(time)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch {
    return time
  }
}

const copyMemoryKey = async (value?: string) => {
  const text = String(value || '').trim()
  if (!text) {
    message.warning('当前没有可复制的 Memory Key')
    return
  }

  try {
    await navigator.clipboard.writeText(text)
    message.success('Memory Key 已复制')
  } catch (error) {
    message.error('复制失败，请手动复制')
  }
}

const query = reactive<QueryState>({})
const loading = ref(false)
const list = ref<AiConversation[]>([])
const selectedRowKeys = ref<Array<string>>([])

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const selectedConversation = ref<AiConversation | null>(null)
const selectedConversationList = ref<AiConversation[]>([])

const currentPageIds = computed(() =>
  list.value
    .map((item) => item.memoryKey)
    .filter((id): id is string => id !== undefined && id !== null)
)

const allCurrentSelected = computed(() => {
  return currentPageIds.value.length > 0 && currentPageIds.value.every((id) => selectedRowKeys.value.includes(id))
})

const partCurrentSelected = computed(() => {
  if (currentPageIds.value.length === 0) return false
  const count = currentPageIds.value.filter((id) => selectedRowKeys.value.includes(id)).length
  return count > 0 && count < currentPageIds.value.length
})

const toggleSelectAllCurrentPage = (checked: boolean) => {
  if (checked) {
    selectedRowKeys.value = Array.from(new Set([...selectedRowKeys.value, ...currentPageIds.value]))
    return
  }
  selectedRowKeys.value = selectedRowKeys.value.filter((id) => !currentPageIds.value.includes(id))
}

const handleCardSelect = (memoryKey: string) => {
  const index = selectedRowKeys.value.indexOf(memoryKey)
  if (index > -1) {
    selectedRowKeys.value.splice(index, 1)
  } else {
    selectedRowKeys.value.push(memoryKey)
  }
}

const handleRecoverConversation = async (memoryKey: string) => {
  loading.value = true
  try {
    const conversations = await aiConversationApi.recoverByMemoryKey(memoryKey)
    if (conversations && conversations.length > 0) {
      // 取最新的一条作为主要信息
      selectedConversation.value = conversations[conversations.length - 1]
      // 同时保存整个对话列表，用于显示完整的对话历史
      selectedConversationList.value = conversations
    }
  } catch (error) {
    console.error('获取对话详情失败:', error)
    message.error('获取对话详情失败')
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  query.memoryKey = undefined
  query.status = undefined
  query.createUser = undefined
  query.startTime = undefined
  query.endTime = undefined
  page.pageNum = 1
  selectedRowKeys.value = []
  selectedConversation.value = null
  void fetchList()
}

const toolbarSegmentButtons = computed<SegmentedButton[]>(() => [
  {
    label: '批量删除',
    type: 'danger',
    plain: true,
    icon: DeleteOutlined,
    disabled: selectedRowKeys.value.length === 0,
    onClick: () => {
      const n = selectedRowKeys.value.length
      if (n === 0) return
      Modal.confirm({
        title: `确定删除选中的 ${n} 个对话组吗？`,
        onOk: () => handleBatchDelete()
      })
    }
  },
  {
    label: '重置',
    type: 'primary',
    plain: true,
    icon: ReloadOutlined,
    onClick: resetFilters
  }
])

const parsedConversation = computed<ChatMessage[] | null>(() => {
  if (!selectedConversationList.value || selectedConversationList.value.length === 0) return null
  
  return selectedConversationList.value.map(item => ({
    role: item.role === 'user' ? 'user' : 'ai',
    content: item.content || '',
    timestamp: item.updateTime || item.createTime || new Date().toISOString()
  }))
})

const fetchList = async () => {
  loading.value = true
  try {
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        memoryKey: query.memoryKey || undefined,
        status: query.status || undefined,
        createUser: query.createUser || undefined,
        startTime: query.startTime || undefined,
        endTime: query.endTime || undefined
      }
    }

    const resp: PageResponse<AiConversation> = await aiConversationApi.queryGroups(payload)
    list.value = resp.list || []
    page.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

const onPageChange = (p: number) => {
  page.pageNum = p
  void fetchList()
}

const handleRowClick = async (record: GroupedConversation) => {
  loading.value = true
  try {
    const conversation = await aiConversationApi.recoverByMemoryKey(record.memoryKey)
    selectedConversation.value = conversation
  } catch (error) {
    console.error('获取对话详情失败:', error)
    message.error('获取对话详情失败')
  } finally {
    loading.value = false
  }
}

const handleDeleteOne = async (memoryKey: string) => {
  const conversations = list.value.filter(c => c.memoryKey === memoryKey)
  const ids = conversations.map(c => c.id).filter((id): id is number | string => id !== undefined && id !== null)
  
  if (ids.length === 0) return
  const msg = await aiConversationApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = selectedRowKeys.value.filter(key => key !== memoryKey)
  if (selectedConversation.value?.memoryKey === memoryKey) {
    selectedConversation.value = null
  }
  void fetchList()
}

const handleBatchDelete = async () => {
  const memoryKeys = [...selectedRowKeys.value]
  if (memoryKeys.length === 0) return
  
  const ids: Array<number | string> = []
  list.value.forEach(c => {
    if (memoryKeys.includes(c.memoryKey || '')) {
      if (c.id) ids.push(c.id)
    }
  })
  
  if (ids.length === 0) return
  const msg = await aiConversationApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = []
  selectedConversation.value = null
  void fetchList()
}

void fetchList()
</script>

<style scoped>
.conversation-page {
  padding: 20px;
}

.conversation-container {
  display: flex;
  gap: 20px;
  margin-top: 20px;
  height: calc(100vh - 240px);
}

.left-panel {
  flex: 0.618;
  min-width: 400px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.conversation-cards {
  flex: 1;
  overflow-y: auto;
  padding-right: 8px;
}

.conversation-cards::-webkit-scrollbar {
  width: 6px;
}

.conversation-cards::-webkit-scrollbar-track {
  background: var(--bg-surface);
  border-radius: 3px;
}

.conversation-cards::-webkit-scrollbar-thumb {
  background: var(--border-default);
  border-radius: 3px;
}

.conversation-cards::-webkit-scrollbar-thumb:hover {
  background: var(--text-tertiary);
}

.right-panel {
  flex: 1;
  min-width: 400px;
  border: 1px solid var(--border-default);
  border-radius: var(--radius-sm);
  background: var(--bg-card);
  display: flex;
  flex-direction: column;
}

.empty-wrap {
  display: flex;
  justify-content: center;
  padding: 32px 0;
}

.pagination-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
  flex-wrap: wrap;
}

.copyable-key {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  max-width: 100%;
}

.copy-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary, #6b7280);
}

.copy-btn:hover,
.copy-btn:focus {
  color: var(--primary);
  background: color-mix(in srgb, var(--primary) 8%, white) !important;
}

.mono-chip {
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  display: inline-flex;
  align-items: center;
  max-width: 100%;
  padding: 4px 10px;
  border-radius: 999px;
  background: color-mix(in srgb, var(--bg-surface) 80%, white);
  color: var(--text-primary, #111827);
}

.count-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 28px;
  height: 28px;
  padding: 0 8px;
  border-radius: 999px;
  background: color-mix(in srgb, var(--primary) 10%, var(--bg-card));
  color: var(--primary);
  font-size: 12px;
  font-weight: 600;
}

.time-text {
  font-size: 12px;
  color: var(--text-secondary, #6b7280);
}

.status-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.status-pill-enabled {
  color: #166534;
  background: #f0fdf4;
}

.status-pill-disabled {
  color: #9a3412;
  background: #fff7ed;
}

.empty-detail {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.conversation-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.detail-header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-default);
  background: var(--bg-surface);
}

.detail-header h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.detail-info {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 13px;
  color: var(--text-secondary);
}

.info-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.conversation-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: var(--bg-card);
}

.chat-messages {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.chat-message {
  max-width: 80%;
  padding: 12px 16px;
  border-radius: var(--radius-sm);
  position: relative;
}

.user-message {
  align-self: flex-start;
  background: color-mix(in srgb, var(--primary) 5%, var(--bg-card));
  border: 1px solid color-mix(in srgb, var(--primary) 15%, var(--border-default));
}

.ai-message {
  align-self: flex-end;
  background: color-mix(in srgb, var(--bg-surface) 90%, white);
  border: 1px solid var(--border-default);
}

.message-role {
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 4px;
  color: var(--text-secondary);
}

.message-content {
  font-size: 14px;
  line-height: 1.5;
  color: var(--text-primary);
  margin-bottom: 8px;
  word-break: break-word;
}

.message-time {
  font-size: 11px;
  color: var(--text-tertiary);
  text-align: right;
}

.empty-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

@media (max-width: 1200px) {
  .conversation-container {
    flex-direction: column;
    height: auto;
  }
  
  .left-panel,
  .right-panel {
    flex: none;
    min-width: unset;
    width: 100%;
  }
  
  .right-panel {
    min-height: 400px;
  }
}

@media (max-width: 720px) {
  .conversation-page {
    padding: 12px;
  }
  
  .detail-info {
    flex-direction: column;
    gap: 8px;
  }
  
  .chat-message {
    max-width: 95%;
  }
}
</style>