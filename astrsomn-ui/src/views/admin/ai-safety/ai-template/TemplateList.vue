<template>
  <AstrsomnPageShell
    title="FTL 模板管理"
    description="维护 Freemarker / StringTemplate 模板（AI_TEMPLATE），与 AiTemplateController 对应。"
    empty-text="暂无模板，请先创建。"
    :breadcrumbs="breadcrumbs"
  >
    <div class="template-page">
      <AstrsomnDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
          <AstrsomnSearchPill
            v-model="query.templateTitle"
            placeholder="搜索模板标题"
            @search="fetchList"
          />
          <AstrsomnStateSwitch v-model="query.status" @change="fetchList" />
            </div>
            <div class="toolbar-right">
          <AstrsomnSegmentedButton :buttons="segmentedButtons" />
            </div>
          </div>
        </template>

        <template v-if="showAdvanced" #toolbar-extra>
          <a-input
            v-model:value="query.category"
            placeholder="分类"
            class="toolbar-input narrow"
            allow-clear
            @pressEnter="fetchList"
          >
            <template #prefix><tags-outlined /></template>
          </a-input>
          <a-select
            v-model:value="query.templateType"
            :options="templateTypeFilterOptions"
            placeholder="模板类型"
            class="toolbar-select narrow-select"
            allow-clear
          />
        </template>



        <AstrsomnDataView
          mode="table"
          :data-source="list"
          :loading="loading"
          :columns="columns"
          :row-selection="rowSelection"
          :scroll="{ x: 1100 }"
          row-key="id"
          empty-text="暂无匹配的模板"
        >
          <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'templateType'">
            <span>{{ renderTemplateType(String(record.templateType || '')) }}</span>
          </template>
          <template v-else-if="column.key === 'status'">
            <span>{{ renderStatus(String(record.status || '')) }}</span>
          </template>
          <template v-else-if="column.key === 'content'">
            <span class="content-preview">{{ previewContent(record.content) }}</span>
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

      <TemplateFormModal
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
import { message } from 'ant-design-vue'
import {
  CheckCircleOutlined,
  DeleteOutlined,
  FilterOutlined,
  KeyOutlined,
  PlusOutlined,
  SearchOutlined,
  StopOutlined,
  TagsOutlined
} from '@ant-design/icons-vue'
import AstrsomnPageShell from '@/components/home/AstrsomnPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnSegmentedButton from '@/components/home/AstrsomnSegmentedButton.vue'
import AstrsomnStateSwitch from '@/components/home/AstrsomnStateSwitch.vue'
import TemplateFormModal from './TemplateFormModal.vue'
import { aiTemplateApi, type AiTemplate, type PageResponse } from '@/api/aiTemplate.ts'

const breadcrumbs = [
  { title: 'AI 安全', href: '/admin/ai-safety' },
  { title: 'FTL 模板管理' },
]

type QueryState = {
  templateTitle?: string
  templateKey?: string
  category?: string
  templateType?: string
  status?: string
}

const statusOptions = [
  { label: 'Enabled', value: 'enabled' },
  { label: 'Disabled', value: 'disabled' }
]

const templateTypeFilterOptions = [
  { label: 'Freemarker', value: 'FREEMARKER' },
  { label: 'StringTemplate', value: 'STRING_TEMPLATE' }
]

const renderStatus = (status: string) => {
  return statusOptions.find((x) => x.value === status)?.label ?? status
}

const renderTemplateType = (t: string) => {
  return templateTypeFilterOptions.find((x) => x.value === t)?.label ?? t
}

const previewContent = (raw: string | undefined) => {
  if (!raw) return '—'
  const one = raw.replace(/\s+/g, ' ').trim()
  return one.length > 80 ? `${one.slice(0, 80)}…` : one
}

const columns = [
  { title: 'Template Key', dataIndex: 'templateKey', key: 'templateKey', width: 160, ellipsis: true },
  { title: '标题', dataIndex: 'templateTitle', key: 'templateTitle', width: 180, ellipsis: true },
  { title: '分类', dataIndex: 'category', key: 'category', width: 100, ellipsis: true },
  { title: '类型', key: 'templateType', width: 130 },
  { title: '版本', dataIndex: 'version', key: 'version', width: 72 },
  { title: '内容预览', key: 'content', width: 260, ellipsis: true },
  { title: '状态', key: 'status', width: 90 },
  { title: '操作', key: 'actions', width: 160, fixed: 'right' as const }
]

const query = reactive<QueryState>({})
const showAdvanced = ref(false)
const list = ref<AiTemplate[]>([])
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

const toggleStatusFilter = (value: 'enabled' | 'disabled') => {
  query.status = query.status === value ? undefined : value
}

const resetFilters = () => {
  query.templateTitle = undefined
  query.templateKey = undefined
  query.category = undefined
  query.templateType = undefined
  query.status = undefined
  showAdvanced.value = false
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

const modal = reactive({
  open: false,
  mode: 'create' as 'create' | 'edit',
  submitting: false
})

const modalInitial = ref<AiTemplate | null>(null)

const segmentedButtons = computed(() => {
  const buttons = [
    {
      label: '重置',
      icon: FilterOutlined,
      onClick: resetFilters
    },
    {
      label: selectedRowKeys.value.length > 0 ? `删除 (${selectedRowKeys.value.length})` : '删除',
      icon: DeleteOutlined,
      disabled: selectedRowKeys.value.length === 0,
      onClick: handleBatchDelete
    },
    {
      label: '新增',
      icon: PlusOutlined,
      type: 'primary',
      onClick: openCreate
    }
  ]
  
  return buttons
})

const fetchList = async () => {
  loading.value = true
  try {
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        templateTitle: query.templateTitle || undefined,
        templateKey: query.templateKey || undefined,
        category: query.category || undefined,
        templateType: query.templateType || undefined,
        status: query.status || undefined
      }
    }
    const resp: PageResponse<AiTemplate> = await aiTemplateApi.queryPage(payload)
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

const openEdit = async (record: AiTemplate) => {
  modal.mode = 'edit'
  const id = record.id
  if (id == null) return

  const detail = await aiTemplateApi.detail(id)
  modalInitial.value = detail
  modal.open = true
}

const handleDeleteOne = async (id: number | string) => {
  if (id == null) return
  const msg = await aiTemplateApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  
  if (confirm('确定批量删除选中的模板吗？')) {
    const msg = await aiTemplateApi.delete(ids)
    message.success(msg)
    selectedRowKeys.value = []
    void fetchList()
  }
}

const handleFormSubmit = async (form: AiTemplate) => {
  modal.submitting = true
  try {
    const payload: AiTemplate = { ...form }
    const v = payload.version
    if (v !== undefined && v !== null) {
      const n = Number(v)
      payload.version = Number.isFinite(n) ? n : undefined
    }

    let msg: string
    if (modal.mode === 'create') {
      delete (payload as { id?: unknown }).id
      msg = await aiTemplateApi.create(payload)
    } else {
      msg = await aiTemplateApi.update(payload)
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
.template-page {
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

.toolbar-input.narrow {
  width: 160px;
}

.toolbar-select {
  width: 180px;
}

.toolbar-select.narrow-select {
  width: 180px;
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

.filter-toggle-btn {
  height: 40px;
  border-radius: 12px;
  color: var(--text-secondary);
}

.content-preview {
  color: rgba(0, 0, 0, 0.45);
  font-size: 12px;
}

@media (max-width: 720px) {
  .toolbar-input,
  .toolbar-input.narrow,
  .search-main-input,
  .search-sub-input,
  .toolbar-select,
  .toolbar-select.narrow-select {
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
  .toolbar-left,
  .toolbar-right {
    width: 100%;
  }
}
</style>
