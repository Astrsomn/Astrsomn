<template>
  <AstPageShell
      :breadcrumbs="breadcrumbs"
      description="管理系统配置项（SYSTEM_CONFIG），支持按分组维护运行时参数。"
      empty-text="暂无系统配置。"
      title="系统配置"
  >
    <div class="config-page">
      <AstDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstSearchInput
                  v-model="query.configKey"
                  placeholder="配置 Key"
                  @search="fetchList"
              />

              <AstStatusSwitch
                  v-model="query.status"
                  :options="statusOptions"
                  @change="handleStatusChange"
              />
            </div>

            <div class="toolbar-right">
              <AstegmentedButton :buttons="actionButtons"/>
            </div>
          </div>
        </template>

        <AstDataView
            :columns="columns"
            :data-source="list"
            :loading="loading"
            :row-selection="rowSelection"
            :scroll="{ x: 1400 }"
            empty-text="暂无匹配的系统配置"
            mode="table"
            row-key="id"
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
              <a-divider type="vertical"/>
              <a-popconfirm
                  v-if="!record.isSystem"
                  cancel-text="取消"
                  ok-text="确认"
                  title="确定删除吗？"
                  @confirm="() => handleDeleteOne(record.id)"
              >
                <a-button danger type="link">删除</a-button>
              </a-popconfirm>
              <a-button v-else disabled type="link">删除</a-button>
            </template>
          </template>
        </AstDataView>

        <template #pagination>
          <AstPagination
              :current="page.pageNum"
              :page-size="page.pageSize"
              :total="page.total"
              @change="onPageChange"
          />
        </template>
      </AstDataSection>

      <SystemConfigForm
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
import {computed, reactive, ref} from 'vue'
import {message} from 'ant-design-vue'
import {AppstoreOutlined, CheckCircleOutlined, DeleteOutlined, PlusOutlined, StopOutlined} from '@ant-design/icons-vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import AstDataSection from '@/components/home/AstDataSection.vue'
import AstDataView from '@/components/home/AstDataView.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstStatusSwitch from '@/components/home/AstStatusSwitch.vue'
import AstegmentedButton, {type SegmentedButton} from '@/components/home/AstegmentedButton.vue'
import SystemConfigForm from './component/SystemConfigForm.vue'
import {type PageResponse, type SystemConfig, systemConfigApi} from '@/api/systemConfig.ts'

const breadcrumbs = [
  {title: '系统配置', href: '/admin/system-config'},
  {title: '系统配置'},
]

type QueryState = {
  configKey?: string
  configGroup?: string
  status?: string
}

const columns = [
  {title: '配置 Key', dataIndex: 'configKey', key: 'configKey', width: 220, ellipsis: true, copyable: true},
  {title: '配置分组', dataIndex: 'configGroup', key: 'configGroup', width: 160, ellipsis: true},
  {title: '配置值', key: 'configValue', width: 280, ellipsis: true},
  {title: '状态', key: 'status', width: 100},
  {title: '属性', key: 'isSystem', width: 110},
  {title: '描述', dataIndex: 'description', key: 'description', width: 200, ellipsis: true},
  {title: '操作', key: 'actions', width: 160, fixed: 'right' as const}
]

const query = reactive<QueryState>({})
const list = ref<SystemConfig[]>([])
const loading = ref(false)

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const statusOptions = [
  {label: '全部', value: undefined, color: '#3b82f6', icon: AppstoreOutlined},
  {label: '启用', value: 'ENABLED', color: '#10b981', icon: CheckCircleOutlined},
  {label: '禁用', value: 'DISABLED', color: '#f43f5e', icon: StopOutlined}
]

const selectedRowKeys = ref<Array<number | string>>([])

const actionButtons = computed<SegmentedButton[]>(() => [
  {
    label: `删除 (${selectedRowKeys.value.length})`,
    type: 'danger',
    icon: DeleteOutlined,
    plain: true,
    disabled: selectedRowKeys.value.length === 0,
    onClick: handleBatchDelete
  },
  {
    label: '重置',
    type: 'default',
    plain: true,
    onClick: resetFilters
  },
  {
    label: '新增',
    type: 'primary',
    icon: PlusOutlined,
    plain: true,
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

const modal = reactive({
  open: false,
  mode: 'create' as 'create' | 'edit',
  submitting: false
})

const modalInitial = ref<SystemConfig | null>(null)

const fetchList = async () => {
  loading.value = true
  try {
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
  } finally {
    loading.value = false
  }
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

const openEdit = async (record: SystemConfig) => {
  modal.mode = 'edit'
  const id = record.id
  if (id == null) return

  const detail = await systemConfigApi.detail(id)
  modalInitial.value = detail
  modal.open = true
}

const handleDeleteOne = async (id: number | string) => {
  if (id == null) return
  const msg = await systemConfigApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return

  const nonSystemIds = ids.filter(id => {
    const config = list.value.find(item => item.id === id)
    return !config?.isSystem
  })

  if (nonSystemIds.length === 0) {
    message.warning('没有可删除的配置项（系统内置配置不可删除）')
    return
  }

  const modal = (window as unknown as {
    $modal: {
      confirm: (options: {
        title: string;
        content: string;
        okText: string;
        cancelText: string;
        onOk: () => void
      }) => void
    }
  }).$modal
  if (modal) {
    modal.confirm({
      title: '确认删除',
      content: `确定要删除选中的 ${nonSystemIds.length} 个配置项吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        const msg = await systemConfigApi.delete(nonSystemIds)
        message.success(msg)
        selectedRowKeys.value = []
        void fetchList()
      }
    })
  } else {
    const msg = await systemConfigApi.delete(nonSystemIds)
    message.success(msg)
    selectedRowKeys.value = []
    void fetchList()
  }
}

const handleFormSubmit = async (form: SystemConfig) => {
  modal.submitting = true
  try {
    const payload: SystemConfig = {...form}

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

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  flex-wrap: wrap;
}

.toolbar-left {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
  flex: 1;
}

.toolbar-right {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
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

  .toolbar-left,
  .toolbar-right {
    width: 100%;
  }
}
</style>
