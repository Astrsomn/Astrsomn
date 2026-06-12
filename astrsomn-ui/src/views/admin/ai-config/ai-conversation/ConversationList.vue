<template>
  <AstPageShell :empty-text="t.list.emptyText">
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
      </AstListToolbar>

      <AstDataView
          :columns="columns"
          :data-source="list"
          :loading="loading"
          :row-key="'id'"
          :scroll="{ y: 'calc(100vh - 320px)' }"
          mode="table"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'sessionStatus'">
            <a-tag :color="record.sessionStatus === 'enabled' ? 'green' : 'red'">
              {{ record.sessionStatus === 'enabled' ? t.list.statusEnabled : t.list.statusDisabled }}
            </a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'action'">
            <div class="action-cell">
              <a-tooltip :title="t.card.recover">
                <a-button size="small" type="text" @click="handleView(record)">
                  <template #icon><EyeOutlined/></template>
                </a-button>
              </a-tooltip>
              <a-tooltip :title="t.card.delete">
                <a-button size="small" type="text" @click="handleDelete(record)">
                  <template #icon><DeleteOutlined/></template>
                </a-button>
              </a-tooltip>
            </div>
          </template>
        </template>
      </AstDataView>

      <div v-if="page.total > 0" class="conversation-pagination">
        <AstPagination
            :current="page.pageNum"
            :page-size="page.pageSize"
            :show-size-changer="false"
            :total="page.total"
            @change="onPageChange"
        />
      </div>

      <!-- 查看对话详情 Drawer -->
      <a-drawer
          :open="drawerOpen"
          :title="t.detail.title"
          :width="640"
          @close="drawerOpen = false"
      >
        <div v-if="drawerLoading" class="drawer-loading">
          <a-spin/>
        </div>
        <div v-else-if="conversationMessages.length" class="chat-messages">
          <div
              v-for="(msg, index) in conversationMessages"
              :key="index"
              :class="msg.role"
              class="message-row"
          >
            <div class="avatar-box">
              <div v-if="msg.role === 'ai'" class="avatar ai-avatar">
                <svg height="22" viewBox="0 0 24 24" width="22">
                  <path d="M12 2L4.5 20.29L5.21 21L12 18L18.79 21L19.5 20.29L12 2Z" fill="currentColor"/>
                </svg>
              </div>
              <div v-else class="avatar user-avatar">
                <span>ME</span>
              </div>
            </div>
            <div class="message-body">
              <div v-if="msg.role === 'ai'" class="ai-card">
                <div class="answer-section">
                  <div class="markdown-renderer">{{ msg.content }}</div>
                </div>
              </div>
              <div v-else class="user-card">
                <div class="user-bubble">
                  <div class="user-html">{{ msg.content }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="empty-content">
          <a-empty :description="t.detail.emptyContent"/>
        </div>
      </a-drawer>
    </div>
  </AstPageShell>
</template>

<script lang="ts" setup>
import {computed, reactive, ref} from 'vue'
import {message, Modal} from 'ant-design-vue'
import {
  CheckCircleOutlined,
  DeleteOutlined,
  EyeOutlined,
  StopOutlined
} from '@ant-design/icons-vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import AstListToolbar from '@/components/home/AstListToolbar.vue'
import AstStatusSwitch from '@/components/home/AstStatusSwitch.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstDataView from '@/components/home/AstDataView.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import {type AiConversation, aiConversationApi} from '@/api/aiConversation'
import {type AiChatSession, aiChatSessionApi, type PageResponse} from '@/api/aiChatSession'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-conversation')

type QueryState = {
  memoryKey?: string
  status?: string
}

const query = reactive<QueryState>({})
const loading = ref(false)
const list = ref<AiChatSession[]>([])

const page = reactive({
  pageNum: 1,
  pageSize: 18,
  total: 0
})

const columns = computed(() => [
  {title: 'Memory Key', dataIndex: 'memoryKey', width: 200, copyable: true, ellipsis: true},
  {title: '会话标题', dataIndex: 'sessionTitle', width: 180, ellipsis: true},
  {title: '最后消息', dataIndex: 'lastMessagePreview', ellipsis: true},
  {title: '消息数', dataIndex: 'messageCount', width: 80, align: 'center' as const},
  {title: t.value.detail.status, dataIndex: 'sessionStatus', width: 90},
  {title: t.value.detail.createTime, dataIndex: 'createTime', dateFormat: true, width: 160},
  {title: '更新时间', dataIndex: 'updateTime', dateFormat: true, width: 160},
  {title: '', dataIndex: 'action', width: 100, fixed: 'right' as const},
])

const drawerOpen = ref(false)
const drawerLoading = ref(false)
const conversationMessages = ref<{ role: 'user' | 'ai'; content: string }[]>([])

const handleView = async (record: AiChatSession) => {
  if (!record.memoryKey) return
  drawerOpen.value = true
  drawerLoading.value = true
  conversationMessages.value = []
  try {
    const conversations = await aiConversationApi.recoverByMemoryKey(record.memoryKey)
    if (conversations && conversations.length > 0) {
      conversationMessages.value = conversations.map(item => ({
        role: item.role === 'user' ? 'user' as const : 'ai' as const,
        content: item.content || ''
      }))
    }
  } catch (error) {
    console.error(t.value.list.fetchDetailFailed + ':', error)
    message.error(t.value.list.fetchDetailFailed)
  } finally {
    drawerLoading.value = false
  }
}

const handleDelete = (record: AiChatSession) => {
  if (!record.id) return
  Modal.confirm({
    title: t.value.card.deleteConfirmTitle,
    content: t.value.card.deleteConfirmContent,
    okType: 'danger',
    onOk: async () => {
      try {
        const msg = await aiChatSessionApi.delete([record.id!])
        message.success(msg)
        await fetchList()
      } catch (e: unknown) {
        const err = e as { message?: string }
        message.error(err?.message || '删除失败')
      }
    }
  })
}

const fetchList = async () => {
  loading.value = true
  try {
    const resp: PageResponse<AiChatSession> = await aiChatSessionApi.queryPage({
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        memoryKey: query.memoryKey || undefined,
        sessionStatus: query.status || undefined
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

void fetchList()
</script>

<style scoped>
.conversation-page {
  padding: 20px;
}

.action-cell {
  display: flex;
  align-items: center;
  gap: 4px;
}

.conversation-pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* ── Drawer 内对话样式 ── */
.drawer-loading {
  display: flex;
  justify-content: center;
  padding: 80px 0;
}

.chat-messages {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin: 12px 0;
  max-width: 100%;
}

.message-row.user {
  flex-direction: row-reverse;
}

.avatar-box {
  flex-shrink: 0;
  padding-top: 2px;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ai-avatar {
  background: var(--chat-ai-avatar-bg, #eff6ff);
  border: 1px solid var(--chat-ai-avatar-border, #bfdbfe);
  color: var(--chat-ai-avatar-icon, #3b82f6);
}

.user-avatar {
  background: var(--chat-user-avatar-bg, #6366f1);
  border: 1px solid var(--chat-user-avatar-border, #4f46e5);
  color: var(--chat-user-avatar-text, #fff);
  font-size: 10px;
  font-weight: 800;
}

.message-body {
  flex: 1;
  min-width: 0;
  max-width: calc(100% - 48px);
}

.ai-card {
  border-radius: 16px;
  border: 1px solid var(--chat-ai-card-border, #e5e7eb);
  background: var(--chat-ai-card-bg, #f9fafb);
  overflow: hidden;
}

.answer-section {
  background: var(--chat-answer-bg, #fff);
}

.markdown-renderer {
  padding: 16px 18px 12px;
  font-size: 15px;
  line-height: 1.75;
  color: var(--chat-markdown-text, #1f2937);
  word-break: break-word;
}

.user-card {
  display: flex;
  justify-content: flex-end;
}

.user-bubble {
  max-width: min(100%, 640px);
  padding: 12px 16px;
  border-radius: 16px 4px 16px 16px;
  background: linear-gradient(135deg, #6366f1, #3b82f6);
  color: #fff;
  box-shadow: 0 4px 14px rgba(99, 102, 241, 0.28);
}

.user-html {
  font-size: 15px;
  line-height: 1.65;
  word-break: break-word;
}

.empty-content {
  display: flex;
  justify-content: center;
  padding: 80px 0;
}

@media (max-width: 720px) {
  .conversation-page {
    padding: 12px;
  }
}
</style>
