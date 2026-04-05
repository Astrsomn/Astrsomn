<template>
  <AdminPageShell
    title="系统配置"
    description="管理系统配置项（SYSTEM_CONFIG），支持按分组维护运行时参数。"
    empty-text="暂无系统配置。"
  >
    <div class="config-page">
      <AdminListToolbar>
        <template #left>
       
            <AstrsomnSearchPill
              v-model="query.configGroup"
              placeholder="配置分组"
              button-label="搜索"
              layout="toolbar"
              @search="fetchList"
            />
        

          <AstrsomnStateSwitch
            v-model="query.status"
            :options="statusOptions"
            @change="handleStatusChange"
          />
        </template>

        <template #right>
          <AstrsomnSegmentedButton :buttons="actionButtons" />
        </template>
      </AdminListToolbar>

      <AstrsomnOverview
        :list-length="list.length"
        :selected-count="selectedRowKeys.length"
        :all-current-selected="allCurrentSelected"
        :part-current-selected="partCurrentSelected"
        :show-actions="list.length > 0"
        :summary-text="`当前页 ${list.length} 条系统配置，已选 ${selectedRowKeys.length} 条。`"
        @toggle-select-all="toggleSelectAllCurrentPage"
      />

      <a-table
        :columns="columns"
        :data-source="list"
        :pagination="false"
        row-key="id"
        :row-selection="rowSelection"
        :scroll="{ x: 1360 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'configKey'">
            <code class="config-key">{{ record.configKey }}</code>
          </template>
          <template v-else-if="column.key === 'configValue'">
            <span class="config-value">{{ preview(record.configValue) }}</span>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.status === 'ENABLED' ? 'green' : 'default'">
              {{ statusLabel(record.status) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'isSystem'">
            <a-tag :color="record.isSystem ? 'blue' : 'default'">
              {{ record.isSystem ? '系统内置' : '自定义' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-button type="link" @click="openEdit(record)">编辑</a-button>
            <a-divider type="vertical" />
            <a-popconfirm
              title="确定删除吗？"
              ok-text="确认"
              cancel-text="取消"
              @confirm="() => handleDeleteOne(record.id)"
            >
              <a-button type="link" danger>删除</a-button>
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

      <ConfigFormModal
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
import { computed, reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  AppstoreOutlined,
  CheckCircleOutlined,
  DeleteOutlined,
  PlusOutlined,
  StopOutlined
} from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AdminListToolbar from '@/components/home/AdminListToolbar.vue'
import AstrsomnOverview from '@/components/home/AstrsomnOverview.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnStateSwitch from '@/components/home/AstrsomnStateSwitch.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'
import ConfigFormModal from './ConfigFormModal.vue'
import { systemConfigApi, type PageResponse, type SystemConfig } from '@/api/systemConfig.ts'

type QueryState = {
  configKey?: string
  configGroup?: string
  status?: string
}

const columns = [
  { title: '配置 Key', key: 'configKey', width: 260, ellipsis: true },
  { title: '配置分组', dataIndex: 'configGroup', key: 'configGroup', width: 160, ellipsis: true },
  { title: '配置值', key: 'configValue', width: 320, ellipsis: true },
  { title: '状态', key: 'status', width: 100 },
  { title: '属性', key: 'isSystem', width: 110 },
  { title: '描述', dataIndex: 'description', key: 'description', width: 220, ellipsis: true },
  { title: '操作', key: 'actions', width: 160, fixed: 'right' as const }
]

const query = reactive<QueryState>({})
const list = ref<SystemConfig[]>([])

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const statusOptions = [
  { label: '全部', value: undefined, color: '#3b82f6', icon: AppstoreOutlined },
  { label: '启用', value: 'ENABLED', color: '#10b981', icon: CheckCircleOutlined },
  { label: '禁用', value: 'DISABLED', color: '#f43f5e', icon: StopOutlined }
]

const selectedRowKeys = ref<Array<number | string>>([])

const actionButtons = computed<SegmentedButton[]>(() => [
  {
    label: '批量删除',
    type: 'danger',
    icon: DeleteOutlined,
    disabled: selectedRowKeys.value.length === 0,
    onClick: handleBatchDelete
  },
  {
    label: '重置',
    type: 'default',
    onClick: resetFilters
  },
  {
    label: '新增',
    type: 'default',
    icon: PlusOutlined,
    onClick: openCreate
  }
])

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

const handleStatusChange = (value: string | undefined) => {
  query.status = value
  void fetchList()
}

const resetFilters = () => {
  query.configKey = undefined
  query.configGroup = undefined
  query.status = undefined
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

const modal = reactive({
  open: false,
  mode: 'create' as 'create' | 'edit',
  submitting: false
})

const modalInitial = ref<SystemConfig | null>(null)

const preview = (raw: string | undefined) => {
  if (!raw) return '—'
  const text = raw.replace(/\s+/g, ' ').trim()
  return text.length > 84 ? `${text.slice(0, 84)}…` : text
}

const statusLabel = (value: string | undefined) => {
  if (value === 'ENABLED') return '启用'
  if (value === 'DISABLED') return '禁用'
  return value ?? '—'
}

const fetchList = async () => {
  const payload = {
    pageNo: page.pageNum,
    pageSize: page.pageSize,
    param: {
      configKey: query.configKey || undefined,
      configGroup: query.configGroup || undefined,
      status: query.status || undefined
    }
  }

  const resp: PageResponse<SystemConfig> = await systemConfigApi.queryPage(payload)
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

const openEdit = async (record: SystemConfig) => {
  modal.mode = 'edit'
  const id = record.id
  if (id == null) return

  const detail = await systemConfigApi.detail(id)
  modalInitial.value = detail
  modal.open = true
}

const handleDeleteOne = async (id: number | string | undefined) => {
  if (id == null) return
  const msg = await systemConfigApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  
  try {
    await new Promise<void>((resolve, reject) => {
      const modal = Modal.confirm({
        title: '确定批量删除选中的配置项吗？',
        okText: '确认',
        cancelText: '取消',
        onOk: () => resolve(),
        onCancel: () => reject(new Error('取消删除'))
      })
    })
    
    const msg = await systemConfigApi.delete(ids)
    message.success(msg)
    selectedRowKeys.value = []
    void fetchList()
  } catch (error) {
    // 用户取消删除，不执行任何操作
  }
}

const handleFormSubmit = async (form: SystemConfig) => {
  modal.submitting = true
  try {
    const payload: SystemConfig = { ...form }
    let msg: string
    if (modal.mode === 'create') {
      delete (payload as { id?: unknown }).id
      msg = await systemConfigApi.create(payload)
    } else {
      msg = await systemConfigApi.update(payload)
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
.config-page {
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

.search-cluster :deep(.ant-input-affix-wrapper) {
  border: none;
  box-shadow: none;
  background: transparent;
}

.search-cluster :deep(.ant-input-affix-wrapper:hover),
.search-cluster :deep(.ant-input-affix-wrapper-focused) {
  border: none;
  box-shadow: none;
  background: color-mix(in srgb, var(--bg-card) 85%, var(--bg-surface));
}

.toolbar-input {
  width: 200px;
}

.search-main-input {
  width: 320px;
}

.search-sub-input {
  width: 220px;
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

.status-switch {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 4px;
  border-radius: 14px;
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
}

.status-btn {
  height: 36px;
  border: none;
  border-radius: 10px;
  color: var(--text-secondary);
  background: transparent;
  box-shadow: none;
}

.status-btn.active {
  color: var(--primary);
  background: color-mix(in srgb, var(--primary) 10%, var(--bg-card));
}

.pagination-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.config-key {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}

.config-value {
  color: rgba(0, 0, 0, 0.65);
}

@media (max-width: 720px) {
  .toolbar-input,
  .search-main-input,
  .search-sub-input {
    width: 100%;
  }

  .search-cluster,
  .status-switch {
    width: 100%;
  }

  .search-cluster {
    padding: 8px;
  }

  .status-switch {
    justify-content: space-between;
  }

  .status-btn {
    flex: 1;
  }

  .pagination-wrap {
    justify-content: center;
  }
}
</style>
