<template>
  <AstrsomnPageShell
    title="向量源"
    description="配置向量数据库连接信息，支持 Milvus、Pinecone、DashVector 等多种向量引擎。"
    empty-text="暂无向量源配置。"
  >
    <div class="vecsource-page">
      <AstrsomnListToolbar>
        <template #left>
          <AstrsomnSearchPill
            v-model="query.name"
            placeholder="搜索向量源名称"
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
      </AstrsomnListToolbar>

      <AstrsomnOverview
        :list-length="list.length"
        :selected-count="selectedRowKeys.length"
        :all-current-selected="allCurrentSelected"
        :part-current-selected="partCurrentSelected"
        :show-actions="list.length > 0"
        :summary-text="`当前页 ${list.length} 条向量源，已选 ${selectedRowKeys.length} 条。`"
        @toggle-select-all="toggleSelectAllCurrentPage"
      />

      <a-table
        :columns="columns"
        :data-source="list"
        :pagination="false"
        row-key="id"
        :row-selection="rowSelection"
        :scroll="{ x: 1280 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'source'">
            <div class="cell-stack">
              <span class="cell-title">{{ record.name || '未命名向量源' }}</span>
              <span class="cell-subtitle multiline-2">{{ getProviderLabel(record.provider) }}</span>
            </div>
          </template>
          <template v-else-if="column.key === 'connection'">
            <div class="cell-stack">
              <span class="cell-title mono-text-inline">{{ getConnectionInfo(record) }}</span>
              <div class="detail-pills">
                <span v-if="record.databaseName" class="detail-pill">
                  DB: {{ record.databaseName }}
                </span>
                <span v-if="record.username" class="detail-pill">
                  用户: {{ record.username }}
                </span>
              </div>
            </div>
          </template>
          <template v-else-if="column.key === 'provider'">
            <span class="type-pill" :class="`type-pill-${String(record.provider || '').toLowerCase()}`">
              {{ getProviderLabel(record.provider) }}
            </span>
          </template>
          <template v-else-if="column.key === 'status'">
            <span class="status-pill" :class="{ off: !isRecordEnabled(record) }">
              {{ isRecordEnabled(record) ? '启用' : '禁用' }}
            </span>
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-popconfirm
              v-if="isRecordEnabled(record)"
              title="确定禁用该向量源？将释放运行时连接。"
              ok-text="确认"
              cancel-text="取消"
              @confirm="() => handleSetStatus(record, false)"
            >
              <a-button type="link" class="action-link">
                <template #icon><StopOutlined /></template>
                禁用
              </a-button>
            </a-popconfirm>
            <a-popconfirm
              v-else
              title="确定启用该向量源？将加载运行时连接。"
              ok-text="确认"
              cancel-text="取消"
              @confirm="() => handleSetStatus(record, true)"
            >
              <a-button type="link" class="action-link">
                <template #icon><CheckCircleOutlined /></template>
                启用
              </a-button>
            </a-popconfirm>
            <a-divider type="vertical" />
            <a-button type="link" class="action-link" @click="openEdit(record)">
              <template #icon><edit-outlined /></template>
              编辑
            </a-button>
            <a-divider type="vertical" />
            <a-button type="link" class="action-link" @click="testConnection(record)">
              <template #icon><ReloadOutlined /></template>
              测试连接
            </a-button>
            <a-divider type="vertical" />
            <a-popconfirm
              title="确定删除吗？"
              ok-text="确认"
              cancel-text="取消"
              @confirm="() => handleDeleteOne(record.id)"
            >
              <a-button type="link" danger class="action-link">
                <template #icon><delete-outlined /></template>
                删除
              </a-button>
            </a-popconfirm>
          </template>
        </template>
      </a-table>

      <div class="pagination-wrap">
        <a-pagination
          :current="page.pageNum"
          :page-size="page.pageSize"
          :total="page.total"
          :show-size-changer="false"
          @change="onPageChange"
        />
      </div>

      <VecSourceFormModal
        v-model:open="modal.open"
        :mode="modal.mode"
        :confirm-loading="modal.submitting"
        :initial="modalInitial"
        @submit="handleFormSubmit"
      />
    </div>
  </AstrsomnPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  CheckCircleOutlined,
  DeleteOutlined,
  EditOutlined,
  PlusOutlined,
  ReloadOutlined,
  StopOutlined
} from '@ant-design/icons-vue'
import AstrsomnPageShell from '@/components/home/AstrsomnPageShell.vue'
import AstrsomnListToolbar from '@/components/home/AstrsomnListToolbar.vue'
import AstrsomnOverview from '@/components/home/AstrsomnOverview.vue'
import AstrsomnStateSwitch from '@/components/home/AstrsomnStateSwitch.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import VecSourceFormModal from './VecSourceFormModal.vue'
import { aiVecSourceApi, type AiVecSource, type PageResponse } from '@/api/aiVecSource.ts'

type QueryState = {
  name?: string
  provider?: string
  status?: string
}

const columns = [
  { title: '向量源名称', key: 'source', width: 240 },
  { title: '提供商', dataIndex: 'provider', key: 'provider', width: 140 },
  { title: '连接信息', key: 'connection', width: 380 },
  { title: '状态', key: 'status', width: 100 },
  { title: '操作', key: 'actions', width: 300, fixed: 'right' as const }
]

const providerLabelMap: Record<string, string> = {
  MILVUS: 'Milvus',
  PINECONE: 'Pinecone',
  DASHVECTOR: 'DashVector',
  CHROMA: 'Chroma',
  QDRANT: 'Qdrant',
  WEAVIATE: 'Weaviate',
  OTHER: '其他'
}

const normalizeText = (value?: string, fallback = '—') => {
  const text = String(value || '').trim()
  return text || fallback
}

const getProviderLabel = (provider?: string) => {
  const key = String(provider || '').toUpperCase()
  return providerLabelMap[key] || normalizeText(provider)
}

const getConnectionInfo = (record: AiVecSource) => {
  if (record.host) {
    const port = record.port ? `:${record.port}` : ''
    return `${record.host}${port}`
  }
  return '未配置连接地址'
}

const isRecordEnabled = (record: AiVecSource) => {
  return String(record.status || '').toLowerCase() === 'enabled'
}

const query = reactive<QueryState>({})
const list = ref<AiVecSource[]>([])

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const selectedRowKeys = ref<Array<number | string>>([])

const currentPageIds = computed(() =>
  list.value
    .map((item) => item.id)
    .filter((id): id is number | string => id !== undefined && id !== null)
)

const allCurrentSelected = computed(() => {
  return currentPageIds.value.length > 0 && currentPageIds.value.every((id) => selectedRowKeys.value.includes(id))
})

const partCurrentSelected = computed(() => {
  if (currentPageIds.value.length === 0) return false
  const count = currentPageIds.value.filter((id) => selectedRowKeys.value.includes(id)).length
  return count > 0 && count < currentPageIds.value.length
})

const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys: Array<number | string>) => {
    selectedRowKeys.value = keys
  }
}))

const toggleSelectAllCurrentPage = (checked: boolean) => {
  if (checked) {
    selectedRowKeys.value = Array.from(new Set([...selectedRowKeys.value, ...currentPageIds.value]))
    return
  }
  selectedRowKeys.value = selectedRowKeys.value.filter((id) => !currentPageIds.value.includes(id))
}

const resetFilters = () => {
  query.name = undefined
  query.provider = undefined
  query.status = undefined
  page.pageNum = 1
  selectedRowKeys.value = []
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
        title: `确定删除选中的 ${n} 个向量源吗？`,
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
  },
  {
    label: '新增',
    type: 'primary',
    icon: PlusOutlined,
    onClick: openCreate
  }
])

const modal = reactive({
  open: false,
  mode: 'create' as 'create' | 'edit',
  submitting: false
})

const modalInitial = ref<AiVecSource | null>(null)

const fetchList = async () => {
  const payload = {
    pageNo: page.pageNum,
    pageSize: page.pageSize,
    param: {
      name: query.name || undefined,
      provider: query.provider || undefined,
      status: query.status || undefined
    }
  }

  const resp: PageResponse<AiVecSource> = await aiVecSourceApi.queryPage(payload)
  list.value = resp.list || []
  page.total = resp.total || 0
}

const onPageChange = (p: number, size: number) => {
  page.pageNum = p
  page.pageSize = size
  void fetchList()
}

const openCreate = () => {
  modal.mode = 'create'
  modalInitial.value = null
  modal.open = true
}

const openEdit = async (record: AiVecSource) => {
  modal.mode = 'edit'
  const id = record.id
  if (id == null) return

  const detail = await aiVecSourceApi.detail(id)
  modalInitial.value = detail
  modal.open = true
}

const handleDeleteOne = async (id: number | string) => {
  if (id == null) return
  const msg = await aiVecSourceApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await aiVecSourceApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleFormSubmit = async (form: AiVecSource) => {
  modal.submitting = true
  try {
    const payload: AiVecSource = { ...form }

    let msg: string
    if (modal.mode === 'create') {
      delete (payload as { id?: unknown }).id
      msg = await aiVecSourceApi.create(payload)
    } else {
      msg = await aiVecSourceApi.update(payload)
    }

    message.success(msg)
    modal.open = false
    void fetchList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '保存失败')
  } finally {
    modal.submitting = false
  }
}

const testConnection = async (record: AiVecSource) => {
  try {
    const msg = await aiVecSourceApi.testConnection(record)
    message.success(msg)
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '测试连接失败')
  }
}

const handleSetStatus = async (record: AiVecSource, enabled: boolean) => {
  const id = record.id
  if (id == null) return
  try {
    const msg = await aiVecSourceApi.setStatus(id, enabled)
    message.success(msg)
    void fetchList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '操作失败')
  }
}

void fetchList()
</script>

<style scoped>
.vecsource-page {
  padding: 20px;
}

.pagination-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.cell-stack {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.cell-title {
  color: var(--text-primary, #111827);
  font-weight: 600;
  line-height: 1.5;
}

.cell-subtitle {
  color: var(--text-secondary, #6b7280);
  line-height: 1.5;
}

.multiline-2 {
  display: -webkit-box;
  overflow: hidden;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.mono-chip,
.mono-text-inline {
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
}

.type-pill,
.status-pill,
.detail-pill {
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

.type-pill {
  color: #1d4ed8;
  background: #eff6ff;
}

.type-pill-milvus {
  color: #0ea5e9;
  background: #f0f9ff;
}

.type-pill-pinecone {
  color: #8b5cf6;
  background: #faf5ff;
}

.type-pill-dashvector {
  color: #f59e0b;
  background: #fffbeb;
}

.type-pill-chroma {
  color: #10b981;
  background: #ecfdf5;
}

.type-pill-qdrant {
  color: #ef4444;
  background: #fef2f2;
}

.type-pill-weaviate {
  color: #6366f1;
  background: #eef2ff;
}

.status-pill {
  color: #166534;
  background: #f0fdf4;
}

.status-pill.off {
  color: #9a3412;
  background: #fff7ed;
}

.detail-pills {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.detail-pill {
  justify-content: flex-start;
  color: var(--text-secondary, #6b7280);
  background: color-mix(in srgb, var(--bg-surface) 85%, white);
}

.action-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding-inline: 4px;
}

@media (max-width: 720px) {
  .pagination-wrap {
    justify-content: center;
  }
}
</style>
