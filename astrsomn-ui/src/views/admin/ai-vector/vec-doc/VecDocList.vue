<template>
  <AstPageShell
      description="管理向量知识库中的文档记录，支持文档入库状态追踪。"
      empty-text="暂无向量文档数据。"
      title="向量文档"
  >
    <div class="vecdoc-page">
      <AstListToolbar>
        <template #left>
          <AstSearchInput
              v-model="query.docIdInStore"
              button-label="搜索"
              layout="toolbar"
              placeholder="搜索文档 ID"
              @search="fetchList"
          />

          <AstStatusSwitch
              v-model="query.syncStatus"
              :options="[
              { label: '全部', value: undefined, color: '#1676fd', icon: CheckCircleOutlined },
              { label: '待向量化', value: AiVecDocSyncStatus.PENDING, color: '#f59e0b', icon: ClockCircleOutlined },
              { label: '已入库', value: AiVecDocSyncStatus.STORED, color: '#10b981', icon: CheckCircleOutlined },
              { label: '已失效', value: AiVecDocSyncStatus.INVALID, color: '#f43f5e', icon: CloseCircleOutlined }
            ]"
              @change="fetchList"
          />

          <a-select
              v-model:value="uploadCollectionId"
              :options="storeOptions"
              allow-clear
              placeholder="上传前选择向量集合"
              style="width: 260px"
          />
          <a-upload :custom-request="handleUpload" :show-upload-list="false" accept=".txt">
            <a-button :disabled="!uploadCollectionId" :loading="uploadSubmitting" ghost type="primary">
              <template #icon>
                <UploadOutlined/>
              </template>
              上传 txt
            </a-button>
          </a-upload>
        </template>

        <template #right>
          <AstegmentedButton :buttons="toolbarSegmentButtons"/>
        </template>
      </AstListToolbar>

      <AstOverview
          :all-current-selected="allCurrentSelected"
          :list-length="list.length"
          :part-current-selected="partCurrentSelected"
          :selected-count="selectedRowKeys.length"
          :show-actions="list.length > 0"
          :summary-text="`当前页 ${list.length} 条向量文档，已选 ${selectedRowKeys.length} 条。`"
          @toggle-select-all="toggleSelectAllCurrentPage"
      />

      <a-table
          :columns="columns"
          :data-source="list"
          :pagination="false"
          :row-selection="rowSelection"
          :scroll="{ x: 1280 }"
          row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'content'">
            <div class="cell-stack">
              <span class="cell-title">{{ record.contentSummary || '无摘要' }}</span>
              <span class="cell-subtitle">集合 ID: {{ record.collectionId || '—' }}</span>
            </div>
          </template>
          <template v-else-if="column.key === 'docId'">
            <div class="copyable-key">
              <span class="mono-chip">{{ record.docIdInStore || '—' }}</span>
              <a-tooltip title="复制文档 ID">
                <a-button
                    :disabled="!record.docIdInStore"
                    class="copy-btn"
                    type="text"
                    @click="copyDocId(record.docIdInStore)"
                >
                  <template #icon>
                    <copy-outlined/>
                  </template>
                </a-button>
              </a-tooltip>
            </div>
          </template>
          <template v-else-if="column.key === 'status'">
            <span :class="`status-pill-${record.syncStatus?.toLowerCase()}`" class="status-pill">
              {{ getStatusLabel(record.syncStatus) }}
            </span>
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-button
                v-if="String(record.syncStatus || '').toUpperCase() === AiVecDocSyncStatus.PENDING"
                :loading="vectorizeLoadingId === record.id"
                class="action-link"
                type="link"
                @click="handleVectorize(record)"
            >
              向量化
            </a-button>
            <a-divider
                v-if="String(record.syncStatus || '').toUpperCase() === AiVecDocSyncStatus.PENDING"
                type="vertical"
            />
            <a-button class="action-link" type="link" @click="openEdit(record)">
              <template #icon>
                <edit-outlined/>
              </template>
              编辑
            </a-button>
            <a-divider type="vertical"/>
            <a-popconfirm
                cancel-text="取消"
                ok-text="确认"
                title="确定删除吗？"
                @confirm="() => handleDeleteOne(record.id)"
            >
              <a-button class="action-link" danger type="link">
                <template #icon>
                  <delete-outlined/>
                </template>
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
            :show-size-changer="false"
            :total="page.total"
            @change="onPageChange"
        />
      </div>

      <VecDocFormModal
          v-model:open="modal.open"
          :confirm-loading="modal.submitting"
          :initial="modalInitial"
          :mode="modal.mode"
          @submit="handleFormSubmit"
      />
    </div>
  </AstPageShell>
</template>

<script lang="ts" setup>
import {computed, onMounted, reactive, ref} from 'vue'
import type {UploadProps} from 'ant-design-vue'
import {message, Modal} from 'ant-design-vue'
import {
  CheckCircleOutlined,
  ClockCircleOutlined,
  CloseCircleOutlined,
  CopyOutlined,
  DeleteOutlined,
  EditOutlined,
  PlusOutlined,
  ReloadOutlined,
  UploadOutlined
} from '@ant-design/icons-vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import AstListToolbar from '@/components/home/AstListToolbar.vue'
import AstOverview from '@/components/home/AstOverview.vue'
import AstStatusSwitch from '@/components/home/AstStatusSwitch.vue'
import AstegmentedButton, {type SegmentedButton} from '@/components/home/AstegmentedButton.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import VecDocFormModal from './VecDocFormModal.vue'
import {aiVecStoreApi} from '@/api/aiVecStore.ts'
import {type AiVecDoc, aiVecDocApi, AiVecDocSyncStatus, type PageResponse} from '@/api/aiVecDoc.ts'

type QueryState = {
  docIdInStore?: string
  collectionId?: number | string
  syncStatus?: string
}

const columns = [
  {title: '文档摘要', key: 'content', width: 380},
  {title: '存储文档 ID', key: 'docId', width: 220, ellipsis: true},
  {title: '集合 ID', dataIndex: 'collectionId', key: 'collectionId', width: 140},
  {title: '同步状态', key: 'status', width: 120},
  {title: '操作', key: 'actions', width: 240, fixed: 'right' as const}
]

const statusLabelMap: Record<string, string> = {
  [AiVecDocSyncStatus.PENDING]: '待向量化',
  [AiVecDocSyncStatus.STORED]: '已入库',
  [AiVecDocSyncStatus.INVALID]: '已失效'
}

const normalizeText = (value?: string | number, fallback = '—') => {
  const text = String(value || '').trim()
  return text || fallback
}

const getStatusLabel = (status?: string) => statusLabelMap[String(status || '').toUpperCase()] || normalizeText(status)

const copyDocId = async (value?: string) => {
  const text = String(value || '').trim()
  if (!text) {
    message.warning('当前没有可复制的文档 ID')
    return
  }

  try {
    await navigator.clipboard.writeText(text)
    message.success('文档 ID 已复制')
  } catch (error) {
    message.error('复制失败，请手动复制')
  }
}

const query = reactive<QueryState>({})
const list = ref<AiVecDoc[]>([])

const uploadCollectionId = ref<number | string | undefined>()
const storeOptions = ref<{ label: string; value: number | string }[]>([])
const uploadSubmitting = ref(false)
const vectorizeLoadingId = ref<number | string | null>(null)

const loadStores = async () => {
  try {
    const resp = await aiVecStoreApi.queryPage({pageNo: 1, pageSize: 500, param: {}})
    storeOptions.value = (resp.list || [])
        .filter((s) => s.id != null)
        .map((s) => ({
          label: `${s.collectionName} (id=${s.id})`,
          value: s.id as number | string
        }))
  } catch {
    storeOptions.value = []
  }
}

const handleUpload: UploadProps['customRequest'] = async (options) => {
  const raw = options.file as File
  if (uploadCollectionId.value == null || uploadCollectionId.value === '') {
    message.warning('请先选择向量集合')
    options.onError?.(new Error('no collection'))
    return
  }
  uploadSubmitting.value = true
  try {
    await aiVecDocApi.upload(raw, uploadCollectionId.value)
    message.success('上传成功')
    options.onSuccess?.({})
    void fetchList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '上传失败')
    options.onError?.(e as Error)
  } finally {
    uploadSubmitting.value = false
  }
}

const handleVectorize = async (record: AiVecDoc) => {
  if (record.id == null) return
  vectorizeLoadingId.value = record.id
  try {
    const msg = await aiVecDocApi.vectorize(record.id)
    message.success(typeof msg === 'string' ? msg : '向量化完成')
    void fetchList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '向量化失败')
  } finally {
    vectorizeLoadingId.value = null
  }
}

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
  query.docIdInStore = undefined
  query.collectionId = undefined
  query.syncStatus = undefined
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
        title: `确定删除选中的 ${n} 个向量文档吗？`,
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

const modalInitial = ref<AiVecDoc | null>(null)

const fetchList = async () => {
  const payload = {
    pageNo: page.pageNum,
    pageSize: page.pageSize,
    param: {
      docIdInStore: query.docIdInStore || undefined,
      collectionId: query.collectionId || undefined,
      syncStatus: query.syncStatus || undefined
    }
  }

  const resp: PageResponse<AiVecDoc> = await aiVecDocApi.queryPage(payload)
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

const openEdit = async (record: AiVecDoc) => {
  modal.mode = 'edit'
  const id = record.id
  if (id == null) return

  const detail = await aiVecDocApi.detail(id)
  modalInitial.value = detail
  modal.open = true
}

const handleDeleteOne = async (id: number | string) => {
  if (id == null) return
  const msg = await aiVecDocApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await aiVecDocApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleFormSubmit = async (form: AiVecDoc) => {
  modal.submitting = true
  try {
    const payload: AiVecDoc = {...form}

    let msg: string
    if (modal.mode === 'create') {
      delete (payload as { id?: unknown }).id
      msg = await aiVecDocApi.create(payload)
    } else {
      msg = await aiVecDocApi.update(payload)
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

onMounted(() => {
  void loadStores()
})

void fetchList()
</script>

<style scoped>
.vecdoc-page {
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

.mono-chip,
.mono-text-inline {
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
}

.mono-chip {
  display: inline-flex;
  align-items: center;
  max-width: 100%;
  padding: 4px 10px;
  border-radius: 999px;
  background: color-mix(in srgb, var(--bg-surface) 80%, white);
  color: var(--text-primary, #111827);
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

.status-pill-pending {
  color: #92400e;
  background: #fffbeb;
}

.status-pill-stored {
  color: #166534;
  background: #f0fdf4;
}

.status-pill-invalid {
  color: #9a3412;
  background: #fff7ed;
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
