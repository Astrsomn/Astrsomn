<template>
  <AdminPageShell
    title="向量驱动"
    description="管理向量数据库驱动配置，支持多种向量数据库。"
    empty-text="暂无向量驱动数据。"
  >
    <div class="vec-driver-page">
      <AdminListToolbar>
        <template #left>
          <AstrsomnSearchPill
            v-model="query.driverName"
            placeholder="搜索驱动名称"
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
        :summary-text="`当前页 ${list.length} 条向量驱动，已选 ${selectedRowKeys.length} 条。`"
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
          <template v-if="column.key === 'params'">
            <div class="params-cell">
              <template v-if="getParamsArray(record.params).length > 0">
                <a-tag 
                  v-for="param in getParamsArray(record.params)" 
                  :key="param" 
                  class="param-tag"
                  color="blue"
                >
                  {{ param }}
                </a-tag>
              </template>
              <span v-else class="params-info">{{ getParamsInfo(record.params) }}</span>
            </div>
          </template>
          <template v-else-if="column.key === 'createTime'">
            {{ formatDate(record.createTime) }}
          </template>
          <template v-else-if="column.key === 'status'">
            <a-select 
              :value="record.status" 
              :class="['status-select', record.status === 'enabled' ? 'status-enabled' : 'status-disabled']"
              @change="(value) => handleStatusChange(record, value)"
              :bordered="false"
            >
              <a-select-option value="enabled">
                <CheckCircleOutlined class="status-icon enabled-icon" />
                启用
              </a-select-option>
              <a-select-option value="disabled">
                <CloseCircleOutlined class="status-icon disabled-icon" />
                禁用
              </a-select-option>
            </a-select>
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-button type="link" class="action-link" @click="openView(record)">
              <template #icon><eye-outlined /></template>
              查看
            </a-button>
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

      <VecDriverFormModal
        v-model:open="modal.open"
        :mode="modal.mode"
        :confirm-loading="modal.submitting"
        :initial="modalInitial"
        @submit="handleFormSubmit"
      />
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, nextTick, reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  CheckCircleOutlined,
  CloseCircleOutlined,
  DeleteOutlined,
  EditOutlined,
  EyeOutlined,
  PlusOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AdminListToolbar from '@/components/home/AdminListToolbar.vue'
import AstrsomnOverview from '@/components/home/AstrsomnOverview.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnStateSwitch from '@/components/home/AstrsomnStateSwitch.vue'
import VecDriverFormModal from './VecDriverFormModal.vue'
import { aiVecDriverApi, type AiVecDriver, type PageResponse } from '@/api/aiVecDriver'

type QueryState = {
  driverName?: string
  provider?: string
  driverType?: string
}

const columns = [
  { title: '驱动名称', dataIndex: 'driverName', key: 'driverName', width: 200 },
  { title: '提供商', dataIndex: 'provider', key: 'provider', width: 200 },
  { title: '参数配置', key: 'params', width: 300 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 200 },
  { title: '操作', dataIndex: 'actions', key: 'actions', width: 160, fixed: 'right' as const }
]

const getParamsArray = (params?: string): string[] => {
  const text = String(params || '').trim()
  if (!text) return []
  
  try {
    const parsed = JSON.parse(text)
    if (Array.isArray(parsed)) {
      return parsed.filter((item): item is string => typeof item === 'string')
    }
  } catch (error) {}
  
  return []
}

const getParamsInfo = (params?: string) => {
  const text = String(params || '').trim()
  if (!text) return '未配置'

  try {
    const parsed = JSON.parse(text)
    if (Array.isArray(parsed)) {
      return parsed.join(', ')
    } else if (parsed && typeof parsed === 'object') {
      return `属性: ${Object.keys(parsed).length}`
    }
  } catch (error) {}

  return '已配置'
}

const formatDate = (dateString?: string) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const query = reactive<QueryState>({})
const list = ref<AiVecDriver[]>([])

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
  query.driverName = undefined
  query.provider = undefined
  query.driverType = undefined
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

const toolbarSegmentButtons = computed<SegmentedButton[]>(() => [
  {
    label: '重置',
    type: 'primary',
    plain: true,
    icon: ReloadOutlined,
    onClick: resetFilters
  }
])

const modal = reactive({
  open: false,
  mode: 'view' as 'create' | 'edit' | 'view',
  submitting: false
})

const modalInitial = ref<AiVecDriver | null>(null)

const fetchList = async () => {
  const payload = {
    pageNo: page.pageNum,
    pageSize: page.pageSize,
    param: {
      driverName: query.driverName || undefined,
      provider: query.provider || undefined,
      driverType: query.driverType || undefined
    }
  }

  const resp: PageResponse<AiVecDriver> = await aiVecDriverApi.queryPage(payload)
  list.value = resp.list || []
  page.total = resp.total || 0
}

const onPageChange = (p: number) => {
  page.pageNum = p
  void fetchList()
}

const openView = async (record: AiVecDriver) => {
  modal.mode = 'view'
  const id = record.id
  if (id == null) return

  const detail = await aiVecDriverApi.detail(id)
  modalInitial.value = detail
  await nextTick()
  modal.open = true
}

const handleStatusChange = async (record: AiVecDriver, value: string) => {
  const id = record.id
  if (id == null) return
  
  try {
    await aiVecDriverApi.update({
      ...record,
      status: value
    })
    message.success('状态切换成功')
    void fetchList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '状态切换失败')
  }
}

const handleDeleteOne = async (id: number | string) => {
  if (id == null) return
  const msg = await aiVecDriverApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await aiVecDriverApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleFormSubmit = async (form: AiVecDriver) => {
  modal.submitting = true
  try {
    let msg: string
    if (modal.mode === 'create') {
      delete (form as { id?: unknown }).id
      msg = await aiVecDriverApi.create(form)
    } else {
      msg = await aiVecDriverApi.update(form)
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

void fetchList()
</script>

<style scoped>
.vec-driver-page {
  padding: 20px;
}

.search-cluster {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
  padding: 6px;
  border-radius: 16px;
  border: 1px solid var(--border-default);
  background: var(--bg-surface);
}

.search-cluster :deep(.ant-input-affix-wrapper),
.search-cluster :deep(.ant-select-selector) {
  border: none !important;
  box-shadow: none !important;
  background: transparent !important;
}

.search-cluster :deep(.ant-input-affix-wrapper:hover),
.search-cluster :deep(.ant-input-affix-wrapper-focused),
.search-cluster :deep(.ant-select-focused .ant-select-selector),
.search-cluster :deep(.ant-select-selector:hover) {
  background: color-mix(in srgb, var(--bg-card) 85%, var(--bg-surface)) !important;
}

.toolbar-input {
  width: 200px;
}

.search-main-input {
  width: 360px;
}

.toolbar-select {
  width: 180px;
}

.type-select {
  min-width: 180px;
}

.primary-btn,
.ghost-btn {
  height: 40px;
  border-radius: 12px;
}

.danger-btn {
  color: var(--error);
  border-color: color-mix(in srgb, var(--error) 28%, var(--border-default));
  background: color-mix(in srgb, var(--error) 7%, var(--bg-card));
}

.danger-btn:hover,
.danger-btn:focus {
  color: var(--error) !important;
  border-color: color-mix(in srgb, var(--error) 42%, var(--border-default)) !important;
  background: color-mix(in srgb, var(--error) 12%, var(--bg-card)) !important;
}

.pagination-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.ellipsis {
  display: inline-block;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: bottom;
}

.params-cell {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 4px;
}

.param-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
}

.params-info {
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

.status-select {
  min-width: 90px;
  font-weight: 500;
}

.status-select :deep(.ant-select-selector) {
  padding: 0 8px !important;
  border-radius: 16px !important;
}

.status-enabled :deep(.ant-select-selector) {
  background: rgba(82, 196, 26, 0.1) !important;
  color: #52c41a !important;
}

.status-enabled :deep(.ant-select-arrow) {
  color: #52c41a !important;
}

.status-disabled :deep(.ant-select-selector) {
  background: rgba(255, 77, 79, 0.1) !important;
  color: #ff4d4f !important;
}

.status-disabled :deep(.ant-select-arrow) {
  color: #ff4d4f !important;
}

.status-icon {
  margin-right: 4px;
}

.enabled-icon {
  color: #52c41a;
}

.disabled-icon {
  color: #ff4d4f;
}

@media (max-width: 720px) {
  .toolbar-input,
  .search-main-input,
  .toolbar-select,
  .type-select {
    width: 100%;
  }

  .search-cluster {
    width: 100%;
  }

  .search-cluster {
    padding: 8px;
  }

  .pagination-wrap {
    justify-content: center;
  }
}
</style>