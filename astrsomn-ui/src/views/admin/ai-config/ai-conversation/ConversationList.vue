<template>
  <AstPageShell

      :empty-text="t.list.emptyText"
  >
    <div class="conversation-page">
      <AstListToolbar>
        <template #left>
          <AstSearchInput
              v-model="query.memoryKey"
              :button-label="t.list.searchButton"
              layout="toolbar"
              :placeholder="t.list.searchPlaceholder"
              @search="fetchList"
          />

          <AstStatusSwitch
              v-model="query.status"
              :options="[
              { label: t.list.statusAll, value: undefined, color: '#1676fd', icon: CheckCircleOutlined },
              { label: t.list.statusEnabled, value: 'enabled', color: '#10b981', icon: CheckCircleOutlined },
              { label: t.list.statusDisabled, value: 'disabled', color: '#f43f5e', icon: StopOutlined }
            ]"
              @change="fetchList"
          />
        </template>

        <template #right>
          <AstegmentedButton :buttons="toolbarSegmentButtons"/>
        </template>
      </AstListToolbar>

      <div class="conversation-container">
        <ConversationListPanel
            v-model:selectedRowKeys="selectedRowKeys"
            :list="list"
            :loading="loading"
            :page="page"
            @delete="handleDeleteByMemoryKey"
            @pageChange="onPageChange"
            @recover="handleRecoverConversation"
        />

        <ConversationDetailPanel
            :selectedConversation="selectedConversation"
            :selectedConversationList="selectedConversationList"
        />
      </div>
    </div>
  </AstPageShell>
</template>

<script lang="ts" setup>
import {computed, reactive, ref} from 'vue'
import {message, Modal} from 'ant-design-vue'
import {CheckCircleOutlined, DeleteOutlined, ReloadOutlined, StopOutlined} from '@ant-design/icons-vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import AstListToolbar from '@/components/home/AstListToolbar.vue'
import AstStatusSwitch from '@/components/home/AstStatusSwitch.vue'
import AstegmentedButton, {type SegmentedButton} from '@/components/home/AstegmentedButton.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import ConversationListPanel from './component/ConversationListPanel.vue'
import ConversationDetailPanel from './component/ConversationDetailPanel.vue'
import {type AiConversation, aiConversationApi} from '@/api/aiConversation'
import {type AiChatSession, aiChatSessionApi, type PageResponse} from '@/api/aiChatSession'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-conversation')

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

      selectedConversation.value = conversations[conversations.length - 1]

      selectedConversationList.value = conversations
    }
  } catch (error) {
    console.error(t.value.list.fetchDetailFailed + ':', error)
    message.error(t.value.list.fetchDetailFailed)
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
    label: t.value.list.batchDelete,
    type: 'danger',
    plain: true,
    icon: DeleteOutlined,
    disabled: selectedRowKeys.value.length === 0,
    onClick: () => {
      const n = selectedRowKeys.value.length
      if (n === 0) return
      Modal.confirm({
        title: t.value.list.batchDeleteConfirm.replace('{n}', String(n)),
        onOk: () => handleBatchDelete()
      })
    }
  },
  {
    label: t.value.list.reset,
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
