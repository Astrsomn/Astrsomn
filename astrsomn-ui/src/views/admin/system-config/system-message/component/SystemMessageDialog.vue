<template>
  <AstModal
    :destroy-on-close="true"
    :open="open"
    :width="960"
    body-height="720px"
    main-padding="20px"
    max-body-height="85vh"
    wrap-class-name="msg-center-modal"
    @update:open="onOpenChange"
  >
    <template #header-title>{{ t.dialog.title }}</template>

    <template v-if="page.total > 0" #footer>
      <AstPagination
        :current="page.pageNum"
        :page-size="page.pageSize"
        :show-size-changer="true"
        :total="page.total"
        @change="onPageChange"
      />
    </template>

    <div class="msg-dialog">
      <a-tabs
        v-model:activeKey="activeTab"
        class="msg-tabs"
        size="small"
        @change="onTabChange"
      >
        <a-tab-pane
          v-for="opt in readStatusTabOptions"
          :key="opt.value"
          :tab="opt.label"
        />
      </a-tabs>

      <div class="msg-toolbar">
        <a-select
          v-model:value="filter.messageLevel"
          :options="levelOptions"
          :placeholder="t.dialog.filterLevel"
          allow-clear
          class="msg-toolbar-select"
          size="middle"
          @change="onFilterChange"
        />
        <span class="msg-toolbar-spacer"/>

        <div class="msg-toolbar-actions">
          <a-tooltip :title="t.dialog.btnRefresh">
            <a-button :loading="loading" shape="circle" type="text" @click="fetchList">
              <template #icon><reload-outlined/></template>
            </a-button>
          </a-tooltip>
          <a-tooltip :title="t.dialog.btnMarkAllRead">
            <a-button
              :disabled="!hasUnreadOnPage"
              shape="circle"
              type="text"
              @click="markAllReadOnPage"
            >
              <template #icon><check-circle-outlined/></template>
            </a-button>
          </a-tooltip>
          <a-button type="link" @click="openFullList">{{ t.dialog.btnOpenList }}</a-button>
        </div>
      </div>

      <a-spin :spinning="loading" class="msg-spin">
        <div v-if="list.length === 0" class="msg-empty-wrapper">
          <a-empty :description="unreadOnly && unreadCount > 0 ? t.dialog.emptyText : t.dialog.emptyUnread" />
        </div>

        <ul v-else class="msg-list">
          <li
            v-for="item in list"
            :key="String(item.id)"
            :class="['msg-item', `msg-level-${item.messageLevel?.toLowerCase()}`, { 'msg-item-unread': item.readStatus !== 'READ' }]"
            @click="openDetail(item)"
          >
            <div class="msg-item-head">
              <a-tag :color="levelColor(item.messageLevel)" class="msg-level-tag">
                {{ levelLabel(item.messageLevel) }}
              </a-tag>
              <a-tag class="msg-type-tag">{{ typeLabel(item.messageType) }}</a-tag>
              <span class="msg-title">{{ item.title || '—' }}</span>
              <span v-if="item.readStatus !== 'READ'" class="msg-unread-dot"/>
            </div>

            <div v-if="item.content" class="msg-content">{{ item.content }}</div>

            <div class="msg-meta">
              <div class="msg-meta-info">
                <span v-if="item.source" class="msg-meta-badge">
                  <span class="badge-label">{{ sysMsg.source }}</span>
                  <span class="badge-value">{{ item.source }}</span>
                </span>
                <span v-if="item.envCode" class="msg-meta-badge env">
                  <span class="badge-label">{{ sysMsg.env }}</span>
                  <span class="badge-value">{{ item.envCode }}</span>
                </span>
                <span class="msg-time">{{ item.createTime || '' }}</span>
              </div>

              <div class="msg-actions" @click.stop>
                <a-button
                  v-if="item.readStatus !== 'READ'"
                  size="small"
                  type="link"
                  @click="markAsRead(item)"
                >
                  {{ t.dialog.btnMarkRead }}
                </a-button>
                <a-popconfirm
                  :title="t.dialog.confirmDelete"
                  cancel-text="No"
                  ok-text="Yes"
                  @confirm="() => handleDelete(item)"
                >
                  <a-button danger size="small" type="link">
                    {{ t.dialog.btnDelete }}
                  </a-button>
                </a-popconfirm>
              </div>
            </div>
          </li>
        </ul>
      </a-spin>
    </div>

    <AstModal
      :open="detailVisible"
      :width="800"
      body-height="auto"
      main-padding="24px"
      @cancel="detailVisible = false"
      @update:open="(v: boolean) => (detailVisible = v)"
    >
      <template #header-title>{{ t.dialog.detailTitle }}</template>
      <a-descriptions v-if="currentItem" :column="1" bordered class="msg-detail-desc" size="middle">
        <a-descriptions-item :label="t.dialog.detailFieldTitle">
          <span class="detail-title-text">{{ currentItem.title || '—' }}</span>
        </a-descriptions-item>
        <a-descriptions-item :label="t.dialog.detailFieldType">
          {{ typeLabel(currentItem.messageType) }}
        </a-descriptions-item>
        <a-descriptions-item :label="t.dialog.detailFieldLevel">
          <a-tag :color="levelColor(currentItem.messageLevel)">{{ levelLabel(currentItem.messageLevel) }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item :label="t.dialog.detailFieldContent">
          <pre class="msg-detail-pre">{{ currentItem.content || '—' }}</pre>
        </a-descriptions-item>
        <a-descriptions-item v-if="currentItem.source" :label="t.dialog.detailFieldSource">
          <a-tag color="blue">{{ currentItem.source }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item v-if="currentItem.refKey" :label="t.dialog.detailFieldRefKey">
          <code>{{ currentItem.refKey }}</code>
        </a-descriptions-item>
        <a-descriptions-item v-if="currentItem.errorCode" :label="t.dialog.detailFieldErrorCode">
          <a-tag color="error">{{ currentItem.errorCode }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item v-if="currentItem.envCode" :label="t.dialog.detailFieldEnvCode">
          <a-tag color="orange">{{ currentItem.envCode }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item :label="t.dialog.detailFieldCreateTime">
          {{ currentItem.createTime || '—' }}
        </a-descriptions-item>
      </a-descriptions>
    </AstModal>
  </AstModal>
</template>

<script lang="ts" setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { message as antMessage } from 'ant-design-vue'
import { CheckCircleOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { type SystemMessage, systemMessageApi } from '@/api/systemMessage'
import { getDictionary } from '@/locales/dictionary/registry'
import { usePageTranslation } from '@/locales/pages'
import { WORKSPACE_ENV_STORAGE_KEY } from '@/constants/workspaceEnv'
import AstModal from '@/components/home/AstModal.vue'
import AstPagination from '@/components/home/AstPagination.vue'

interface Props {
  open: boolean
}

const props = defineProps<Props>()
const emit = defineEmits<{
  (e: 'update:open', value: boolean): void
  (e: 'unread-count-change', count: number): void
}>()

const router = useRouter()
const tCommon = usePageTranslation('common')
const t = usePageTranslation('system-message')
const sysMsg = computed(() => tCommon.value.systemMessage)

const typeDict = getDictionary('system.message.type')
const levelDict = getDictionary('system.message.level')
const readStatusDict = getDictionary('system.message.readStatus')

const typeLabel = (v?: string) => typeDict.getLabel(v) ?? v ?? '—'
const levelLabel = (v?: string) => levelDict.getLabel(v) ?? v ?? '—'
const levelColor = (v?: string) => {
  if (v === 'SUCCESS') return 'success'
  if (v === 'WARN') return 'warning'
  if (v === 'ERROR') return 'error'
  return 'processing'
}

const loading = ref(false)
const list = ref<SystemMessage[]>([])
const page = reactive({ pageNum: 1, pageSize: 8, total: 0 })
const filter = reactive<{ readStatus?: string; messageLevel?: string }>({
  readStatus: 'UNREAD'
})

// ---- tab options ----
const readStatusTabOptions = computed(() => [
  ...readStatusDict.order.map((key: string) => ({
    label: readStatusDict.getLabel(key) ?? key,
    value: key,
  })),
  { label: tCommon.value.statusSwitch.all, value: '' },
])

const activeTab = computed({
  get: () => filter.readStatus ?? '',
  set: (val: string) => {
    filter.readStatus = val || undefined
  },
})

const levelOptions = computed(() => levelDict.order.map(key => ({
  label: levelDict.getLabel(key) ?? key,
  value: key
})))

const unreadOnly = computed(() => filter.readStatus === 'UNREAD')
const hasUnreadOnPage = computed(() => list.value.some(i => i.readStatus !== 'READ'))
const unreadCount = computed(() => page.total > 0 && unreadOnly.value ? 0 : list.value.filter(i => i.readStatus !== 'READ').length)

watch(() => props.open, (val) => {
  if (val) {
    void fetchList()
  }
})

watch(unreadCount, (val) => {
  emit('unread-count-change', val)
}, { immediate: false })

const onOpenChange = (val: boolean) => emit('update:open', val)

const onFilterChange = () => {
  page.pageNum = 1
  void fetchList()
}

const onTabChange = () => {
  page.pageNum = 1
  void fetchList()
}

const onPageChange = (pageNum: number, pageSize: number) => {
  page.pageNum = pageNum
  page.pageSize = pageSize
  void fetchList()
}

const fetchList = async () => {
  loading.value = true
  try {
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        messageLevel: filter.messageLevel || undefined,
        readStatus: filter.readStatus || undefined
      }
    }
    const resp = await systemMessageApi.queryPage(payload)
    list.value = resp.list || []
    page.total = resp.total || 0
  } catch (e) {
    antMessage.error(t.value.dialog.loadFailed)
  } finally {
    loading.value = false
  }
}

const detailVisible = ref(false)
const currentItem = ref<SystemMessage | null>(null)

const openDetail = (item: SystemMessage) => {
  currentItem.value = item
  detailVisible.value = true
  if (item.id != null && item.readStatus !== 'READ') {
    void markAsRead(item, true)
  }
}

const markAsRead = async (item: SystemMessage, silent = false) => {
  if (item.id == null) return
  try {
    await systemMessageApi.update({ id: item.id, readStatus: 'READ' })
    item.readStatus = 'READ'
    if (!silent) {
      antMessage.success(t.value.dialog.markReadSuccess)
    }
  } catch (e) {
    if (!silent) {
      antMessage.error(t.value.dialog.loadFailed)
    }
  }
}

const handleDelete = async (item: SystemMessage) => {
  if (item.id == null) return
  try {
    const msg = await systemMessageApi.delete([item.id])
    antMessage.success(msg || t.value.dialog.deleteSuccess)
    await fetchList()
  } catch (e) {
    antMessage.error(t.value.dialog.loadFailed)
  }
}

const markAllReadOnPage = async () => {
  const unreadItems = list.value.filter(i => i.id != null && i.readStatus !== 'READ')
  if (unreadItems.length === 0) return
  try {
    await Promise.all(unreadItems.map(i => systemMessageApi.update({ id: i.id, readStatus: 'READ' })))
    antMessage.success(t.value.dialog.markAllReadSuccess)
    await fetchList()
  } catch (e) {
    antMessage.error(t.value.dialog.loadFailed)
  }
}

const openFullList = () => {
  emit('update:open', false)
  void router.push({ name: 'AdminSystemMessage' })
}

// ========== SSE ==========
let source: EventSource | null = null
let reconnectTimer: ReturnType<typeof setTimeout> | null = null
let reconnectAttempt = 0

const shouldConnectSse = () => {
  if (typeof window === 'undefined') return false
  return !!localStorage.getItem('token')
}

const cleanupSse = () => {
  if (source) {
    source.close()
    source = null
  }
}

const clearReconnect = () => {
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }
}

const connectSse = () => {
  clearReconnect()
  cleanupSse()
  if (!shouldConnectSse()) return
  const token = localStorage.getItem('token') || ''
  const envCode = localStorage.getItem(WORKSPACE_ENV_STORAGE_KEY) || ''
  const query = new URLSearchParams({ token })
  if (envCode) query.set('envCode', envCode)
  try {
    source = new EventSource(`/v1/astro/sse/system-message?${query.toString()}`)
  } catch (e) {
    scheduleReconnect()
    return
  }
  source.addEventListener('SYSTEM_MESSAGE', () => {
    if (props.open) {
      void fetchList()
    } else {
      emit('unread-count-change', unreadCount.value + 1)
    }
  })
  source.onerror = () => {
    cleanupSse()
    scheduleReconnect()
  }
  source.onopen = () => {
    reconnectAttempt = 0
  }
}

const scheduleReconnect = () => {
  if (!shouldConnectSse()) return
  reconnectAttempt += 1
  const backoff = Math.min(20000, 1000 * 2 ** Math.min(reconnectAttempt, 5))
  reconnectTimer = setTimeout(() => {
    if (shouldConnectSse()) connectSse()
  }, backoff)
}

onMounted(() => {
  connectSse()
})

onBeforeUnmount(() => {
  clearReconnect()
  cleanupSse()
})
</script>

<style scoped>
/* ---- root: fills AstModal's .fsm-main ---- */
.msg-dialog {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  overflow: hidden;
}

/* ---- tabs ---- */
.msg-tabs {
  flex-shrink: 0;
}

.msg-tabs :deep(.ant-tabs-nav) {
  margin-bottom: 0;
}

/* ---- toolbar ---- */
.msg-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid var(--ant-color-border-split, #f0f0f0);
  flex-shrink: 0;
}

.msg-toolbar-select {
  width: 140px;
}

.msg-toolbar-spacer {
  flex: 1;
}

.msg-toolbar-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* ---- spinner & list area ---- */
.msg-spin {
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.msg-spin :deep(.ant-spin-container) {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* empty */
.msg-empty-wrapper {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* list */
.msg-list {
  list-style: none;
  margin: 0;
  padding: 4px 2px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

/* ---- card ---- */
.msg-item {
  position: relative;
  border: 1px solid var(--ant-color-border-secondary, #f0f0f0);
  border-radius: 8px;
  padding: 14px 16px;
  background: var(--ant-color-bg-container, #ffffff);
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  flex-shrink: 0;
}

.msg-item:hover {
  border-color: var(--ant-color-primary-hover, #40a9ff);
  box-shadow: 0 4px 12px var(--ant-color-box-shadow-secondary, rgba(0, 0, 0, 0.05));
  transform: translateY(-1px);
}

.msg-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: transparent;
  transition: background-color 0.2s;
}
.msg-item-unread::before {
  background: var(--ant-color-primary, #1890ff);
}
.msg-item-unread.msg-level-error::before {
  background: var(--ant-color-error, #ff4d4f);
}
.msg-item-unread.msg-level-warn::before {
  background: var(--ant-color-warning, #faad14);
}
.msg-item-unread.msg-level-success::before {
  background: var(--ant-color-success, #52c41a);
}

.msg-item-unread {
  background: var(--ant-color-primary-bg, #e6f7ff);
  border-color: var(--ant-color-primary-border, #91d5ff);
}

.msg-item-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.msg-level-tag, .msg-type-tag {
  margin-inline-end: 0 !important;
  border-radius: 4px;
}

.msg-title {
  flex: 1;
  font-size: 14px;
  font-weight: 600;
  color: var(--ant-color-text-heading, rgba(0, 0, 0, 0.85));
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.msg-unread-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--ant-color-primary, #1890ff);
  flex-shrink: 0;
  box-shadow: 0 0 0 2px var(--ant-color-primary-bg-hover, rgba(24, 144, 255, 0.2));
}

.msg-content {
  font-size: 13px;
  color: var(--ant-color-text-secondary, rgba(0, 0, 0, 0.45));
  line-height: 1.5;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}

.msg-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 24px;
}

.msg-meta-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: var(--ant-color-text-description, rgba(0, 0, 0, 0.45));
}

.msg-meta-badge {
  display: inline-flex;
  align-items: center;
  background: var(--ant-color-fill-alter, #fafafa);
  border: 1px solid var(--ant-color-border-secondary, #f0f0f0);
  border-radius: 4px;
  overflow: hidden;
}

.msg-meta-badge .badge-label {
  background: var(--ant-color-fill-content, #f5f5f5);
  padding: 1px 6px;
  color: var(--ant-color-text-muted, rgba(0, 0, 0, 0.45));
}

.msg-meta-badge .badge-value {
  padding: 1px 6px;
  font-weight: 500;
  color: var(--ant-color-text, rgba(0, 0, 0, 0.65));
}

.msg-time {
  color: var(--ant-color-text-placeholder, rgba(0, 0, 0, 0.25));
}

.msg-actions {
  display: flex;
  gap: 4px;
  opacity: 0;
  transform: translateX(10px);
  transition: all 0.2s ease-in-out;
}

.msg-item:hover .msg-actions {
  opacity: 1;
  transform: translateX(0);
}

/* ---- detail modal ---- */
.msg-detail-desc {
  margin-top: 16px;
}

.detail-title-text {
  font-weight: 600;
  color: var(--ant-color-text-heading);
}

.msg-detail-pre {
  margin: 0;
  font-family: var(--ant-font-family-code, monospace);
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 13px;
  color: var(--ant-color-text);
  background: var(--ant-color-fill-alter, #fafafa);
  border: 1px solid var(--ant-color-border-secondary, #f0f0f0);
  padding: 12px;
  border-radius: 6px;
  max-height: 240px;
  overflow-y: auto;
}

code {
  font-family: var(--ant-font-family-code, monospace);
  background: var(--ant-color-fill-alter, #fafafa);
  padding: 2px 6px;
  border-radius: 4px;
  border: 1px solid var(--ant-color-border-secondary, #f0f0f0);
}
</style>
