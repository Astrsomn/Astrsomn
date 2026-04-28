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
        <ConversationListPanel
          :loading="loading"
          :list="list"
          v-model:selectedRowKeys="selectedRowKeys"
          :page="page"
          @recover="handleRecoverConversation"
          @delete="handleDeleteByMemoryKey"
          @pageChange="onPageChange"
        />

        <!-- 右侧对话内容区域 -->
        <ConversationDetailPanel
          :selectedConversation="selectedConversation"
          :selectedConversationList="selectedConversationList"
        />
      </div>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  CheckCircleOutlined,
  DeleteOutlined,
  ReloadOutlined,
  StopOutlined
} from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AdminListToolbar from '@/components/home/AdminListToolbar.vue'
import AstrsomnStateSwitch from '@/components/home/AstrsomnStateSwitch.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import ConversationListPanel from './ConversationListPanel.vue'
import ConversationDetailPanel from './ConversationDetailPanel.vue'
import { aiConversationApi, type AiConversation } from '@/api/aiConversation'
import { aiChatSessionApi, type AiChatSession, type PageResponse } from '@/api/aiChatSession'

type QueryState = {
  memoryKey?: string
  status?: string
  createUser?: string
  startTime?: string
  endTime?: string
}

const query = reactive<QueryState>({})
const loading = ref(false)
const list = ref<AiChatSession[]>([])
const selectedRowKeys = ref<Array<string>>([])

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const selectedConversation = ref<AiConversation | null>(null)
const selectedConversationList = ref<AiConversation[]>([])

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

    const resp: PageResponse<AiChatSession> = await aiChatSessionApi.queryPage({
      pageNo: payload.pageNo,
      pageSize: payload.pageSize,
      param: {
        memoryKey: payload.param.memoryKey,
        sessionStatus: payload.param.status
      }
    })
    list.value = resp.list || []
    page.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

const onPageChange = (p: number, size: number) => {
  page.pageNum = p
  page.pageSize = size
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
  const msg = await aiChatSessionApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = []
  selectedConversation.value = null
  void fetchList()
}

const handleDeleteByMemoryKey = async (memoryKey: string) => {
  const ids: Array<number | string> = list.value
    .filter((item) => item.memoryKey === memoryKey && item.id !== undefined && item.id !== null)
    .map((item) => item.id as number | string)
  if (ids.length === 0) return
  const msg = await aiChatSessionApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = selectedRowKeys.value.filter((key) => key !== memoryKey)
  if (selectedConversation.value?.memoryKey === memoryKey) {
    selectedConversation.value = null
    selectedConversationList.value = []
  }
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

@media (max-width: 1200px) {
  .conversation-container {
    flex-direction: column;
    height: auto;
  }
}

@media (max-width: 720px) {
  .conversation-page {
    padding: 12px;
  }
}
</style>