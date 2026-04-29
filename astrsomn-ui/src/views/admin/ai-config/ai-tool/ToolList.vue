<template>
  <AdminPageShell
    title="AI Tools"
    description="管理本地工具与调用配置（AI_TOOL），对接 AiToolController。"
    empty-text="暂无可用工具。"
    :breadcrumbs="breadcrumbs"
  >
    <div class="tool-page">
      <AstrsomnDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstrsomnSearchPill
                v-model="query.toolName"
                placeholder="搜索工具名称"
                button-label="搜索"
                layout="toolbar"
                @search="fetchList"
              />
              <AstrsomnStateSwitch
                v-model="query.enableFlag"
                @change="fetchList"
                :options="[
                  { label: '全部', value: undefined, color: '#6366f1', icon: CheckCircleOutlined },
                  { label: '启用', value: 'enabled', color: '#10b981', icon: CheckCircleOutlined },
                  { label: '禁用', value: 'disabled', color: '#f43f5e', icon: StopOutlined }
                ]"
              />
            </div>
            <div class="toolbar-right">
              <AstrsomnSegmentedButton :buttons="toolbarSegmentButtons" />
            </div>
          </div>
        </template>



        <AstrsomnDataView
          mode="table"
          :data-source="list"
          :loading="loading"
          :columns="columns"
          :row-selection="rowSelection"
          :scroll="{ x: 1180 }"
          row-key="id"
          empty-text="暂无匹配的工具记录"
        >
          <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'enableFlag'">
            <span>{{ renderEnable(String(record.enableFlag || '')) }}</span>
          </template>
          <template v-else-if="column.key === 'description'">
            <span class="desc-preview">{{ preview(record.description) }}</span>
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-space>
              <a-button type="link" size="small" @click="openEdit(record)">
                <EditOutlined />
              </a-button>
              <a-popconfirm
                title="确定删除吗？"
                ok-text="确认"
                cancel-text="取消"
                @confirm="() => handleDeleteOne(record.id)"
              >
                <a-button type="link" danger size="small">
                  <DeleteOutlined />
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
          </template>
        </AstrsomnDataView>

        <template #pagination>
          <AstrsomnPagination
            :current="page.pageNum"
            :page-size="page.pageSize"
            :total="page.total"
            @change="onPageChange"
          />
        </template>
      </AstrsomnDataSection>

      <ToolFormModal
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
  CheckCircleOutlined,
  DeleteOutlined,
  EditOutlined,
  KeyOutlined,
  PlusOutlined,
  ReloadOutlined,
  StopOutlined
} from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import AstrsomnStateSwitch from '@/components/home/AstrsomnStateSwitch.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import ToolFormModal from './ToolFormModal.vue'
import { aiToolApi, type AiTool, type PageResponse } from '@/api/aiTool.ts'

type QueryState = {
  toolName?: string
  toolKey?: string
  type?: string
  enableFlag?: string
}

const breadcrumbs = [
  { title: 'AI 配置', href: '/admin/ai-config' },
  { title: 'AI Tools' },
]

const typeFilterOptions = [
  { label: 'HTML', value: 'html' },
  { label: 'Method', value: 'method' }
]

const enableFilterOptions = [
  { label: '启用', value: 'enabled' },
  { label: '停用', value: 'disabled' }
]

const renderEnable = (f: string) => enableFilterOptions.find((x) => x.value === f)?.label ?? f

const preview = (raw: string | undefined) => {
  if (!raw) return '—'
  const t = raw.replace(/\s+/g, ' ').trim()
  return t.length > 48 ? `${t.slice(0, 48)}…` : t
}

const columns = [
  { title: 'Tool Key', dataIndex: 'toolKey', key: 'toolKey', width: 180, ellipsis: true, copyable: true },
  { title: '名称', dataIndex: 'toolName', key: 'toolName', width: 140, ellipsis: true },
  { title: '类型', dataIndex: 'type', key: 'type', width: 90 },
  { title: 'Bean', dataIndex: 'beanName', key: 'beanName', width: 140, ellipsis: true },
  { title: '方法', dataIndex: 'methodName', key: 'methodName', width: 120, ellipsis: true },
  { title: '描述', key: 'description', width: 200, ellipsis: true },
  { title: '状态', key: 'enableFlag', width: 80 },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 170, dateFormat: true },
  { title: '操作', key: 'actions', width: 100, fixed: 'right' as const }
]

const query = reactive<QueryState>({})
const list = ref<AiTool[]>([])
const loading = ref(false)

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

const toggleEnabledFilter = (value: 'enabled' | 'disabled') => {
  query.enableFlag = query.enableFlag === value ? undefined : value
}

const resetFilters = () => {
  query.toolName = undefined
  query.toolKey = undefined
  query.type = undefined
  query.enableFlag = undefined
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

const toolbarSegmentButtons = computed<SegmentedButton[]>(() => [
  {
    label: '重置',
    icon: ReloadOutlined,
    onClick: resetFilters
  },
  {
    label: selectedRowKeys.value.length > 0 ? `删除 (${selectedRowKeys.value.length})` : '删除',
    type: 'danger',
    plain: true,
    icon: DeleteOutlined,
    disabled: selectedRowKeys.value.length === 0,
    onClick: () => {
      const n = selectedRowKeys.value.length
      if (n === 0) return
      Modal.confirm({
        title: `确定删除选中的 ${n} 个工具吗？`,
        onOk: () => handleBatchDelete()
      })
    }
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

const modalInitial = ref<AiTool | null>(null)

const fetchList = async () => {
  loading.value = true
  try {
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        toolName: query.toolName || undefined,
        toolKey: query.toolKey || undefined,
        type: query.type || undefined,
        enableFlag: query.enableFlag || undefined
      }
    }
    const resp: PageResponse<AiTool> = await aiToolApi.queryPage(payload)
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

const openEdit = async (record: AiTool) => {
  modal.mode = 'edit'
  const id = record.id
  if (id == null) return

  const detail = await aiToolApi.detail(id)
  modalInitial.value = detail
  modal.open = true
}

const handleDeleteOne = async (id: number | string) => {
  if (id == null) return
  const msg = await aiToolApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await aiToolApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleFormSubmit = async (form: AiTool) => {
  modal.submitting = true
  try {
    const payload: AiTool = { ...form }

    let msg: string
    if (modal.mode === 'create') {
      delete (payload as { id?: unknown }).id
      msg = await aiToolApi.create(payload)
    } else {
      msg = await aiToolApi.update(payload)
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
.tool-page {
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
  width: 320px;
}

.search-sub-input {
  width: 220px;
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

.desc-preview {
  color: rgba(0, 0, 0, 0.45);
  font-size: 12px;
}

@media (max-width: 720px) {
  .toolbar-input,
  .search-main-input,
  .search-sub-input,
  .toolbar-select,
  .type-select {
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
