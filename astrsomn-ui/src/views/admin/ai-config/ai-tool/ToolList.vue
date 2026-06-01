<template>
  <AstPageShell
      :breadcrumbs="breadcrumbs"
      :show-view-toggle="true"
      :view-mode="viewMode"
      :view-toggle-handler="handleViewToggle"
      :description="t.list.description"
      :empty-text="t.list.emptyText"
      :title="t.list.title"
  >
    <div ref="pageRef" class="tool-page">
      <AstDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstSearchInput
                  v-model="query.toolName"
                  :button-label="t.list.searchButton"
                  layout="toolbar"
                  :placeholder="t.list.searchPlaceholder"
                  @search="fetchList"
              />
              <AstStatusSwitch
                  v-model="query.enableFlag"
                  :options="[
                  { label: t.list.status.all, value: undefined, color: '#6366f1', icon: CheckCircleOutlined },
                  { label: t.list.status.enabled, value: 'enabled', color: '#10b981', icon: CheckCircleOutlined },
                  { label: t.list.status.disabled, value: 'disabled', color: '#f43f5e', icon: StopOutlined }
                ]"
                  @change="fetchList"
              />
            </div>
            <div class="toolbar-right">
              <AstegmentedButton :buttons="toolbarSegmentButtons"/>
            </div>
          </div>
        </template>


        <AstDataView
            :card-columns="currentGridColumns"
            :card-gap="toolCardGap"
            :card-min-width="toolCardMinWidth"
            :columns="columns"
            :data-source="list"
            :loading="loading"
            :mode="dataViewMode"
            :row-selection="rowSelection"
            :scroll="{ x: 1180 }"
            :empty-text="t.list.emptyMatchText"
            row-key="id"
        >
          <template #card="{ record }">
            <ToolCard
                :record="record"
                @delete="handleDeleteOne"
                @edit="openEdit"
            />
          </template>
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'enableFlag'">
              <span>{{ renderEnable(String(record.enableFlag || '')) }}</span>
            </template>
            <template v-else-if="column.key === 'description'">
              <span class="desc-preview">{{ preview(record.description) }}</span>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-space>
                <a-button size="small" type="link" @click="openEdit(record)">
                  <EditOutlined/>
                </a-button>
                <a-popconfirm
                    :cancel-text="t.list.cancel"
                    :ok-text="t.list.confirm"
                    :title="t.list.deleteConfirm"
                    @confirm="() => handleDeleteOne(record.id)"
                >
                  <a-button danger size="small" type="link">
                    <DeleteOutlined/>
                  </a-button>
                </a-popconfirm>
              </a-space>
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

      <ToolForm
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
import {computed, onBeforeUnmount, onMounted, reactive, ref} from 'vue'
import {message, Modal} from 'ant-design-vue'
import {
  CheckCircleOutlined,
  DeleteOutlined,
  EditOutlined,
  PlusOutlined,
  ReloadOutlined,
  StopOutlined
} from '@ant-design/icons-vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import AstDataSection from '@/components/home/AstDataSection.vue'
import AstDataView from '@/components/home/AstDataView.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstStatusSwitch from '@/components/home/AstStatusSwitch.vue'
import AstegmentedButton, {type SegmentedButton} from '@/components/home/AstegmentedButton.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import ToolForm from './component/ToolForm.vue'
import ToolCard from './component/ToolCard.vue'
import {type AiTool, aiToolApi, type PageResponse} from '@/api/aiTool.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-tool')

const props = withDefaults(defineProps<{
  initialViewMode?: 'grid' | 'list'
}>(), {
  initialViewMode: 'list'
})

const TOOL_CARD_MIN_WIDTH_PX = 320
const TOOL_CARD_GAP_PX = 12
const toolCardMinWidth = `${TOOL_CARD_MIN_WIDTH_PX}px`
const toolCardGap = `${TOOL_CARD_GAP_PX}px`

const pageRef = ref<HTMLElement | null>(null)
const viewMode = ref<'grid' | 'list'>(props.initialViewMode)
const dataViewMode = computed<'card' | 'table'>(() => (viewMode.value === 'grid' ? 'card' : 'table'))
const currentGridColumns = ref(3)

type QueryState = {
  toolName?: string
  toolKey?: string
  type?: string
  enableFlag?: string
}

const breadcrumbs = computed(() => [
  {title: t.value.list.breadcrumb.aiConfig, href: '/admin/ai-config'},
  {title: t.value.list.breadcrumb.aiTools},
])

const typeFilterOptions = [
  {label: 'HTML', value: 'html'},
  {label: 'Method', value: 'method'}
]

const enableFilterOptions = computed(() => [
  {label: t.value.list.enableFilter.enabled, value: 'enabled'},
  {label: t.value.list.enableFilter.disabled, value: 'disabled'}
])

const renderEnable = (f: string) => enableFilterOptions.value.find((x) => x.value === f)?.label ?? f

const preview = (raw: string | undefined) => {
  if (!raw) return '—'
  const txt = raw.replace(/\s+/g, ' ').trim()
  return txt.length > 48 ? `${txt.slice(0, 48)}…` : txt
}

const columns = computed(() => [
  {title: 'Tool Key', dataIndex: 'toolKey', key: 'toolKey', width: 180, ellipsis: true, copyable: true},
  {title: t.value.list.column.toolName, dataIndex: 'toolName', key: 'toolName', width: 140, ellipsis: true},
  {title: t.value.list.column.type, dataIndex: 'type', key: 'type', width: 90},
  {title: 'Bean', dataIndex: 'beanName', key: 'beanName', width: 140, ellipsis: true},
  {title: t.value.list.column.methodName, dataIndex: 'methodName', key: 'methodName', width: 120, ellipsis: true},
  {title: t.value.list.column.description, key: 'description', width: 200, ellipsis: true},
  {title: t.value.list.column.status, key: 'enableFlag', width: 80},
  {title: t.value.list.column.envCode, dataIndex: 'envCode', key: 'envCode', width: 80, ellipsis: true, tag: true, tagColor: 'blue'},
  {title: t.value.list.column.createTime, dataIndex: 'createTime', key: 'createTime', width: 150, dateFormat: true},
  {title: t.value.list.column.createUser, dataIndex: 'createUser', key: 'createUser', width: 150},
  {title: t.value.list.column.actions, key: 'actions', width: 100, fixed: 'right' as const}
])

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

const handleViewToggle = () => {
  viewMode.value = viewMode.value === 'grid' ? 'list' : 'grid'
}

const resolveGridColumns = () => {
  if (typeof window === 'undefined') return 3
  const width = pageRef.value?.clientWidth ?? window.innerWidth
  const n = Math.floor((width + TOOL_CARD_GAP_PX) / (TOOL_CARD_MIN_WIDTH_PX + TOOL_CARD_GAP_PX))
  return Math.max(1, Math.min(3, n))
}

const syncGridColumns = () => {
  currentGridColumns.value = resolveGridColumns()
}

let resizeObserver: ResizeObserver | null = null

onMounted(() => {
  syncGridColumns()
  if (typeof ResizeObserver !== 'undefined' && pageRef.value) {
    resizeObserver = new ResizeObserver(syncGridColumns)
    resizeObserver.observe(pageRef.value)
  } else {
    window.addEventListener('resize', syncGridColumns)
  }
})

onBeforeUnmount(() => {
  resizeObserver?.disconnect()
  window.removeEventListener('resize', syncGridColumns)
})

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
    label: t.value.list.reset,
    icon: ReloadOutlined,
    onClick: resetFilters
  },
  {
    label: selectedRowKeys.value.length > 0 ? t.value.list.deleteCount.replace('{n}', String(selectedRowKeys.value.length)) : t.value.list.delete,
    type: 'danger',
    plain: true,
    icon: DeleteOutlined,
    disabled: selectedRowKeys.value.length === 0,
    onClick: () => {
      const n = selectedRowKeys.value.length
      if (n === 0) return
      Modal.confirm({
        title: t.value.list.batchDeleteConfirm.replace('{n}', String(n)),
        onOk: () => handleBatchDelete()
      })
    }
  },
  {
    label: t.value.list.create,
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
    const payload: AiTool = {...form}

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
    message.error(err?.message || t.value.list.saveFailed)
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
  color: var(--text-tertiary);
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
