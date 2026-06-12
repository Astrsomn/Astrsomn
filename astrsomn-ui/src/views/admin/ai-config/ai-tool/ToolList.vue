<template>
  <AstPageShell
      :show-view-toggle="true"
      :view-mode="viewMode"
      :view-toggle-handler="handleViewToggle"

      :empty-text="t.list.emptyText"

  >
    <div class="tool-page">
    <AstDataSection>
      <template #toolbar>
        <div class="toolbar">
          <AstSearchInput
              v-model="query.toolName"
              :button-label="t.list.searchButton"
              layout="toolbar"
              :placeholder="t.list.searchPlaceholder"
              @search="fetchList"
          />
          <AstStatusSwitch
              v-model="query.status"
              :options="[
                { label: t.list.status.all, value: undefined, color: '#6366f1', icon: CheckCircleOutlined },
                { label: t.list.status.enabled, value: 'enabled', color: '#10b981', icon: CheckCircleOutlined },
                { label: t.list.status.disabled, value: 'disabled', color: '#f43f5e', icon: StopOutlined }
              ]"
              @change="fetchList"
          />
        </div>
      </template>

      <div v-if="dataViewMode === 'card'" class="tool-grid-section">
      <a-spin :spinning="loading">
        <div class="tool-grid">
          <div class="add-card" @click="openCreate">
            <PlusOutlined class="add-icon"/>
            <span class="add-text">{{ t.list.create }}</span>
          </div>
          <ToolCard
              v-for="record in list"
              :key="record.id"
              :record="record"
              :selected="record.id != null && selectedKeySet.has(record.id)"
              @delete="handleDeleteOne"
              @edit="openEdit"
              @toggle="onToolCardToggle"
          />
        </div>
      </a-spin>
    </div>

    <AstDataView
        v-else
        :columns="columns"
        :data-source="list"
        :loading="loading"
        mode="table"
        :row-selection="rowSelection"
        :scroll="{ x: 960 }"
        :empty-text="t.list.emptyMatchText"
        row-key="id"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <span>{{ renderEnable(String(record.status || '')) }}</span>
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
import {computed, reactive, ref} from 'vue'
import {message} from 'ant-design-vue'
import {
  CheckCircleOutlined,
  DeleteOutlined,
  EditOutlined,
  PlusOutlined,
  StopOutlined
} from '@ant-design/icons-vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import AstDataSection from '@/components/home/AstDataSection.vue'
import AstDataView from '@/components/home/AstDataView.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstStatusSwitch from '@/components/home/AstStatusSwitch.vue'
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

const viewMode = ref<'grid' | 'list'>(props.initialViewMode)
const dataViewMode = computed<'card' | 'table'>(() => (viewMode.value === 'grid' ? 'card' : 'table'))

type QueryState = {
  toolName?: string
  toolKey?: string
  type?: string
  status?: string
}

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
  {title: 'Tool Key', dataIndex: 'toolKey', key: 'toolKey', width: 160, ellipsis: true, copyable: true},
  {title: t.value.list.column.toolName, dataIndex: 'toolName', key: 'toolName', width: 120, ellipsis: true},
  {title: t.value.list.column.type, dataIndex: 'type', key: 'type', width: 80},
  {title: 'Bean', dataIndex: 'beanName', key: 'beanName', width: 120, ellipsis: true},
  {title: t.value.list.column.methodName, dataIndex: 'methodName', key: 'methodName', width: 110, ellipsis: true},
  {title: t.value.list.column.description, key: 'description', width: 160, ellipsis: true},
  {title: t.value.list.column.status, key: 'status', width: 80},
  {title: t.value.list.column.createTime, dataIndex: 'createTime', key: 'createTime', width: 140, dateFormat: true},
  {title: t.value.list.column.actions, key: 'actions', width: 90, fixed: 'right' as const}
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
const selectedKeySet = computed(() => new Set(selectedRowKeys.value))

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

const handleViewToggle = () => {
  viewMode.value = viewMode.value === 'grid' ? 'list' : 'grid'
}

const onToolCardToggle = (id: number | string, checked: boolean) => {
  if (checked) {
    if (!selectedRowKeys.value.includes(id)) {
      selectedRowKeys.value = [...selectedRowKeys.value, id]
    }
    return
  }
  selectedRowKeys.value = selectedRowKeys.value.filter((k) => k !== id)
}

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
        enableFlag: query.status || undefined
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

  try {
    const detail = await aiToolApi.detail(id)
    modalInitial.value = detail
    modal.open = true
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || t.value.list.saveFailed)
  }
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
  align-items: center;
  gap: 14px;
  flex-wrap: wrap;
}

/* ── Card Grid (matching AgentSection.vue) ── */
.tool-grid-section {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.tool-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 14px;
}

.tool-grid > * {
  animation: cardEnter 0.35s ease both;
}

.tool-grid > *:nth-child(1) { animation-delay: 0ms; }
.tool-grid > *:nth-child(2) { animation-delay: 40ms; }
.tool-grid > *:nth-child(3) { animation-delay: 80ms; }
.tool-grid > *:nth-child(4) { animation-delay: 120ms; }
.tool-grid > *:nth-child(5) { animation-delay: 160ms; }
.tool-grid > *:nth-child(6) { animation-delay: 200ms; }
.tool-grid > *:nth-child(7) { animation-delay: 240ms; }
.tool-grid > *:nth-child(8) { animation-delay: 280ms; }
.tool-grid > *:nth-child(9) { animation-delay: 320ms; }
.tool-grid > *:nth-child(10) { animation-delay: 360ms; }
.tool-grid > *:nth-child(n+11) { animation-delay: 400ms; }

@keyframes cardEnter {
  from {
    opacity: 0;
    transform: translateY(16px) scale(0.97);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}


/* ── Add Card (matching AgentSection.vue) ── */
.add-card {
  border: 2px dashed var(--border-subtle);
  background: transparent;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 140px;
  color: var(--text-muted);
}

.add-card:hover {
  border-color: var(--primary);
  color: var(--primary);
  background: color-mix(in srgb, var(--primary) 2%, transparent);
}

.add-icon {
  font-size: 22px;
  opacity: 0.4;
  transition: opacity 0.2s;
}

.add-card:hover .add-icon {
  opacity: 0.8;
}

.add-text {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.desc-preview {
  color: var(--text-tertiary);
  font-size: 12px;
}
</style>
