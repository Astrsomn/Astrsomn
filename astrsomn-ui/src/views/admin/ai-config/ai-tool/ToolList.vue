<template>
  <AdminPageShell
    title="AI Tools"
    description="管理本地工具与调用配置（AI_TOOL），对接 AiToolController。"
    empty-text="暂无可用工具。"
  >
    <div class="tool-page">
      <AdminListToolbar>
        <template #left>
          <div class="search-cluster">
            <a-input
              v-model:value="query.toolName"
              placeholder="搜索工具名称"
              class="toolbar-input search-main-input"
              allow-clear
              @pressEnter="fetchList"
            >
              <template #prefix><search-outlined /></template>
            </a-input>
            <a-input
              v-model:value="query.toolKey"
              placeholder="Tool Key"
              class="toolbar-input search-sub-input"
              allow-clear
              @pressEnter="fetchList"
            >
              <template #prefix><key-outlined /></template>
            </a-input>
            <a-select
              v-model:value="query.type"
              :options="typeFilterOptions"
              placeholder="所有类型"
              class="toolbar-select type-select"
              allow-clear
            />
          </div>

          <div class="status-switch" role="group" aria-label="状态筛选">
            <a-button
              class="status-btn"
              :class="{ active: query.enableFlag === 'enabled' }"
              @click="toggleEnabledFilter('enabled')"
            >
              <template #icon><check-circle-outlined /></template>
              启用
            </a-button>
            <a-button
              class="status-btn"
              :class="{ active: query.enableFlag === 'disabled' }"
              @click="toggleEnabledFilter('disabled')"
            >
              <template #icon><stop-outlined /></template>
              禁用
            </a-button>
          </div>
        </template>

        <template #right>
          <a-button type="primary" class="primary-btn" @click="fetchList">
            <template #icon><search-outlined /></template>
            查询
          </a-button>
          <a-popconfirm
            v-if="selectedRowKeys.length > 0"
            title="确定批量删除选中的工具吗？"
            ok-text="确认"
            cancel-text="取消"
            @confirm="handleBatchDelete"
          >
            <a-button danger class="ghost-btn danger-btn">
              <template #icon><delete-outlined /></template>
              批量删除
            </a-button>
          </a-popconfirm>
          <a-button class="ghost-btn" @click="resetFilters">重置</a-button>
          <a-button class="ghost-btn" @click="openCreate">
            <template #icon><plus-outlined /></template>
            新增
          </a-button>
        </template>
      </AdminListToolbar>

      <BaseOverview
        :list-length="list.length"
        :selected-count="selectedRowKeys.length"
        :all-current-selected="allCurrentSelected"
        :part-current-selected="partCurrentSelected"
        :show-actions="list.length > 0"
        :summary-text="`当前页 ${list.length} 条工具记录，已选 ${selectedRowKeys.length} 条。`"
        @toggle-select-all="toggleSelectAllCurrentPage"
      />

      <a-table
        :columns="columns"
        :data-source="list"
        :pagination="false"
        row-key="id"
        :row-selection="rowSelection"
        :scroll="{ x: 1180 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'enableFlag'">
            <span>{{ renderEnable(String(record.enableFlag || '')) }}</span>
          </template>
          <template v-else-if="column.key === 'description'">
            <span class="desc-preview">{{ preview(record.description) }}</span>
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
import { message } from 'ant-design-vue'
import {
  CheckCircleOutlined,
  DeleteOutlined,
  KeyOutlined,
  PlusOutlined,
  SearchOutlined,
  StopOutlined
} from '@ant-design/icons-vue'
import AdminPageShell from '@/views/admin/components/admin/AdminPageShell.vue'
import AdminListToolbar from '@/views/admin/components/admin/AdminListToolbar.vue'
import BaseOverview from '@/views/admin/components/admin/BaseOverview.vue'
import ToolFormModal from './ToolFormModal.vue'
import { aiToolApi, type AiTool, type PageResponse } from '@/api/aiTool.ts'

type QueryState = {
  toolName?: string
  toolKey?: string
  type?: string
  enableFlag?: string
}

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
  { title: 'Tool Key', dataIndex: 'toolKey', key: 'toolKey', width: 180, ellipsis: true },
  { title: '名称', dataIndex: 'toolName', key: 'toolName', width: 140, ellipsis: true },
  { title: '类型', dataIndex: 'type', key: 'type', width: 90 },
  { title: 'Bean', dataIndex: 'beanName', key: 'beanName', width: 140, ellipsis: true },
  { title: '方法', dataIndex: 'methodName', key: 'methodName', width: 120, ellipsis: true },
  { title: '描述', key: 'description', width: 200, ellipsis: true },
  { title: '状态', key: 'enableFlag', width: 80 },
  { title: '操作', key: 'actions', width: 160, fixed: 'right' as const }
]

const query = reactive<QueryState>({})
const list = ref<AiTool[]>([])

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

const modal = reactive({
  open: false,
  mode: 'create' as 'create' | 'edit',
  submitting: false
})

const modalInitial = ref<AiTool | null>(null)

const fetchList = async () => {
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
  padding: 0 4px;
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

.pagination-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
  flex-wrap: wrap;
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

  .pagination-wrap {
    justify-content: center;
  }
}
</style>
