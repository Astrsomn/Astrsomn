<template>
  <div class="vec-store-page-wrapper">
    <SourceSidebar
      v-model:selectedSourceId="selectedSourceId"
      :totalStoreCount="totalStoreCount"
      @refresh="fetchList"
    />

    <div class="store-content">
      <AdminPageShell
        :title="currentSourceTitle"
        :description="currentSourceDescription"
        empty-text="暂无向量存储配置。"
      >
        <div v-if="selectedSourceId === null" class="no-source-selected">
          <div class="no-source-icon">
            <DatabaseOutlined />
          </div>
          <h3>请选择一个向量源</h3>
          <p>在左侧边栏选择一个向量源，以管理其对应的向量存储配置</p>
        </div>
        <div v-else class="vec-store-page">
          <AdminListToolbar>
            <template #left>
              <AstrsomnSearchPill
                v-model="query.collectionName"
                placeholder="搜索集合名称"
                button-label="搜索"
                layout="toolbar"
                @search="fetchList"
              />
            </template>

            <template #right>
              <AstrsomnSegmentedButton :buttons="toolbarSegmentButtons" />
            </template>
          </AdminListToolbar>

          <AstrsomnOverview
            :list-length="list.length"
            :selected-count="selectedRowKeys.length"
            :all-current-selected="allCurrentSelected"
            :part-current-selected="partCurrentSelected"
            :show-actions="list.length > 0"
            :summary-text="`当前页 ${list.length} 条向量存储，已选 ${selectedRowKeys.length} 条。`"
            @toggle-select-all="toggleSelectAllCurrentPage"
          />

          <a-table
            :columns="columns"
            :data-source="list"
            :pagination="false"
            row-key="id"
            :row-selection="rowSelection"
            :scroll="{ x: 1400 }"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'collectionName'">
                <div class="cell-stack">
                  <span class="cell-title">{{ record.collectionName || '未命名集合' }}</span>
                  <span class="cell-subtitle">维度: {{ record.dimension || '-' }}</span>
                </div>
              </template>
              <template v-if="column.key === 'metadataSchema'">
                <div class="metadata-cell">
                  <span class="metadata-info">{{ getMetadataInfo(record.metadataSchema) }}</span>
                </div>
              </template>
              <template v-else-if="column.key === 'sourceName'">
                <div class="source-cell-inline">
                  <div class="source-icon-small" :class="getProviderClass(record.sourceProvider)">
                    <DatabaseOutlined />
                  </div>
                  <span>{{ record.sourceName || '未关联' }}</span>
                </div>
              </template>
              <template v-else-if="column.key === 'instanceName'">
                <span class="instance-name">{{ record.instanceName || '未关联' }}</span>
              </template>
              <template v-else-if="column.key === 'actions'">
                <a-button type="link" class="action-link" @click="openEdit(record)">
                  <template #icon><edit-outlined /></template>
                  编辑
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

          <VecStoreFormModal
            v-model:open="modal.open"
            :mode="modal.mode"
            :confirm-loading="modal.submitting"
            :initial="modalInitial"
            :default-source-id="selectedSourceId"
            @submit="handleFormSubmit"
          />
        </div>
      </AdminPageShell>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, onMounted, watch } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  DeleteOutlined,
  EditOutlined,
  PlusOutlined,
  ReloadOutlined,
  DatabaseOutlined
} from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AdminListToolbar from '@/components/home/AdminListToolbar.vue'
import AstrsomnOverview from '@/components/home/AstrsomnOverview.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import SourceSidebar from '@/components/ai/SourceSidebar.vue'
import VecStoreFormModal from './VecStoreFormModal.vue'
import { aiVecStoreApi, type AiVecStore, type PageResponse } from '@/api/aiVecStore'
import { aiVecSourceApi, type AiVecSource } from '@/api/aiVecSource'

type QueryState = {
  collectionName?: string
  sourceId?: number | string
  instanceKey?: string
  dimension?: number
}

const columns = [
  { title: '集合名称', key: 'collectionName', width: 240 },
  { title: '距离度量', dataIndex: 'distanceMetric', key: 'distanceMetric', width: 150 },
  { title: '向量源', key: 'sourceName', width: 200 },
  { title: '实例名称', key: 'instanceName', width: 200 },
  { title: '实例 Key', dataIndex: 'instanceKey', key: 'instanceKey', width: 200, ellipsis: true },
  { title: '元数据模式', key: 'metadataSchema', width: 300 },
  { title: '操作', key: 'actions', width: 160, fixed: 'right' as const }
]

const getMetadataInfo = (metadataSchema?: string) => {
  const text = String(metadataSchema || '').trim()
  if (!text) return '未配置'

  try {
    const parsed = JSON.parse(text)
    if (parsed && typeof parsed === 'object' && !Array.isArray(parsed)) {
      const properties = parsed.properties || {}
      return `属性: ${Object.keys(properties).length}`
    }
  } catch (error) {}

  return '已配置'
}

const providerLabelMap: Record<string, string> = {
  MILVUS: 'Milvus',
  PINECONE: 'Pinecone',
  DASHVECTOR: 'DashVector',
  CHROMA: 'Chroma',
  QDRANT: 'Qdrant',
  WEAVIATE: 'Weaviate',
  OTHER: '其他'
}

const getProviderLabel = (provider?: string) => {
  const key = String(provider || '').toUpperCase()
  return providerLabelMap[key] || provider || '未知'
}

const getProviderClass = (provider?: string) => {
  const key = String(provider || '').toLowerCase()
  return `provider-${key}`
}

const query = reactive<QueryState>({})
const list = ref<AiVecStore[]>([])

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const selectedRowKeys = ref<Array<number | string>>([])
const selectedSourceId = ref<number | string | null>(null)

const totalStoreCount = computed(() => page.total)

const currentSourceTitle = computed(() => {
  if (selectedSourceId.value === null) return '向量存储'
  return '向量存储'
})

const currentSourceDescription = computed(() => {
  if (selectedSourceId.value === null) {
    return '管理向量集合配置，定义维度、距离度量和元数据模式。'
  }
  return '管理向量集合配置，定义维度、距离度量和元数据模式。'
})

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
  query.collectionName = undefined
  query.instanceKey = undefined
  query.dimension = undefined
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
        title: `确定删除选中的 ${n} 个向量存储吗？`,
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

const modalInitial = ref<AiVecStore | null>(null)

const fetchList = async () => {
  const payload = {
    pageNo: page.pageNum,
    pageSize: page.pageSize,
    param: {
      collectionName: query.collectionName || undefined,
      sourceId: query.sourceId || undefined,
      instanceKey: query.instanceKey || undefined,
      dimension: query.dimension || undefined
    }
  }

  const resp: PageResponse<AiVecStore> = await aiVecStoreApi.queryPage(payload)
  list.value = resp.list || []
  page.total = resp.total || 0
}

const onPageChange = (p: number) => {
  page.pageNum = p
  void fetchList()
}

const openCreate = () => {
  modal.mode = 'create'
  modalInitial.value = null
  modal.open = true
}

const openEdit = async (record: AiVecStore) => {
  modal.mode = 'edit'
  const id = record.id
  if (id == null) return

  const detail = await aiVecStoreApi.detail(id)
  modalInitial.value = detail
  modal.open = true
}

const handleDeleteOne = async (id: number | string) => {
  if (id == null) return
  const msg = await aiVecStoreApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await aiVecStoreApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleFormSubmit = async (form: AiVecStore) => {
  modal.submitting = true
  try {
    let msg: string
    if (modal.mode === 'create') {
      delete (form as { id?: unknown }).id
      msg = await aiVecStoreApi.create(form)
    } else {
      msg = await aiVecStoreApi.update(form)
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

watch(selectedSourceId, (newValue) => {
  query.sourceId = newValue || undefined
  page.pageNum = 1
  void fetchList()
})

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.vec-store-page-wrapper {
  display: flex;
  min-height: 100%;
  background: var(--bg-page);
}

.store-content {
  flex: 1;
  min-width: 0;
  height: 100vh;
  overflow: auto;
}

.vec-store-page {
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

.metadata-cell {
  display: flex;
  align-items: center;
}

.metadata-info {
  padding: 4px 10px;
  border-radius: 999px;
  background: color-mix(in srgb, var(--bg-surface) 85%, white);
  color: var(--text-secondary, #6b7280);
  font-size: 12px;
  font-weight: 600;
}

.action-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding-inline: 4px;
}

.instance-name {
  font-weight: 600;
  color: var(--text-primary);
}

.cell-stack {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.cell-title {
  font-weight: 600;
  color: var(--text-primary);
}

.cell-subtitle {
  font-size: 12px;
  color: var(--text-secondary);
}

.source-cell-inline {
  display: flex;
  align-items: center;
  gap: 8px;
}

.source-icon-small {
  width: 20px;
  height: 20px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  color: white;
  flex-shrink: 0;
}

.source-icon-small.provider-milvus {
  background: linear-gradient(135deg, #0ea5e9, #0284c7);
}

.source-icon-small.provider-chroma {
  background: linear-gradient(135deg, #10b981, #047857);
}

.source-icon-small.provider-qdrant {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.source-icon-small.provider-pinecone {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
}

.source-icon-small.provider-dashvector {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.source-icon-small.provider-weaviate {
  background: linear-gradient(135deg, #6366f1, #4f46e5);
}

@media (max-width: 1024px) {
  .vec-store-page-wrapper {
    flex-direction: column;
  }

  .store-content {
    height: auto;
  }
}

@media (max-width: 720px) {
  .pagination-wrap {
    justify-content: center;
  }
}

.no-source-selected {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  background: var(--bg-surface);
  border-radius: 12px;
  margin: 20px 0;
}

.no-source-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: linear-gradient(135deg, #6366f1, #4f46e5);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 20px;
}

.no-source-selected h3 {
  margin: 0 0 8px;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
}

.no-source-selected p {
  margin: 0;
  font-size: 14px;
  color: var(--text-secondary);
  max-width: 400px;
}
</style>
