<template>
  <AstPageShell
      :breadcrumbs="breadcrumbs"
      :show-view-toggle="true"
      :view-mode="viewMode"
      :view-toggle-handler="handleViewToggle"
      description="管理 MCP 服务接入（SSE / STDIO / STEAMABLE），对接 AiMcpController。"
      empty-text="暂无 MCP 服务。"
      title="AI MCP"
  >
    <div ref="pageRef" class="mcp-page">
      <AstDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstSearchInput
                  v-model="query.mcpKey"
                  button-label="搜索"
                  layout="toolbar"
                  placeholder="搜索 MCP Key"
                  @search="fetchList"
              />
              <AstStatusSwitch
                  v-model="query.enabled"
                  :options="[
                  { label: '全部', value: undefined, color: '#1676fd', icon: CheckCircleOutlined },
                  { label: '启用', value: 1, color: '#10b981', icon: CheckCircleOutlined },
                  { label: '禁用', value: 0, color: '#f43f5e', icon: StopOutlined }
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
            :card-gap="mcpCardGap"
            :card-min-width="mcpCardMinWidth"
            :columns="columns"
            :data-source="list"
            :loading="loading"
            :mode="dataViewMode"
            :row-selection="rowSelection"
            :scroll="{ x: 1280 }"
            empty-text="暂无匹配的 MCP 服务"
            row-key="id"
        >
          <template #card="{ record }">
            <McpCard
                :record="record"
                @delete="handleDeleteOne"
                @edit="openEdit"
            />
          </template>
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'enabled'">
              <span :class="{ off: record.enabled !== 1 }" class="status-pill">
                {{ record.enabled === 1 ? '启用' : '停用' }}
              </span>
            </template>
            <template v-if="column.key === 'actions'">
              <a-button class="action-link" type="link" @click="openEdit(record)">
                <template #icon>
                  <edit-outlined/>
                </template>
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
                </a-button>
              </a-popconfirm>
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

      <McpFormModal
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
import McpFormModal from './component/McpFormModal.vue'
import McpCard from './component/McpCard.vue'
import {type AiMcp, aiMcpApi, type PageResponse} from '@/api/aiMcp'

const props = withDefaults(defineProps<{
  initialViewMode?: 'grid' | 'list'
}>(), {
  initialViewMode: 'list'
})

const MCP_CARD_MIN_WIDTH_PX = 320
const MCP_CARD_GAP_PX = 12
const mcpCardMinWidth = `${MCP_CARD_MIN_WIDTH_PX}px`
const mcpCardGap = `${MCP_CARD_GAP_PX}px`

const pageRef = ref<HTMLElement | null>(null)
const viewMode = ref<'grid' | 'list'>(props.initialViewMode)
const dataViewMode = computed<'card' | 'table'>(() => (viewMode.value === 'grid' ? 'card' : 'table'))
const currentGridColumns = ref(3)

type QueryState = {
  mcpKey?: string
  type?: string
  enabled?: number
}

const breadcrumbs = [
  {title: 'AI 配置', href: '/admin/ai-config'},
  {title: 'AI MCP'},
]

const typeFilterOptions = [
  {label: 'SSE', value: 'SSE'},
  {label: 'STDIO', value: 'STDIO'},
  {label: 'STEAMABLE', value: 'STEAMABLE'}
]

const columns = [
  {title: 'MCP Key', dataIndex: 'mcpKey', key: 'mcpKey', width: 180, ellipsis: true, copyable: true},
  {title: '服务名称', dataIndex: 'serverName', key: 'serverName', width: 240},
  {title: '类型', dataIndex: 'type', key: 'type', width: 120},
  {title: '启用', key: 'enabled', width: 90},
  {title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 170, dateFormat: true},
  {title: '操作', key: 'actions', width: 160, fixed: 'right' as const}
]

const typeLabelMap: Record<string, string> = {
  SSE: 'SSE',
  STDIO: 'STDIO',
  STEAMABLE: 'STEAMABLE'
}

const normalizeText = (value?: string, fallback = '—') => {
  const text = String(value || '').trim()
  return text || fallback
}

const getTypeLabel = (type?: string) => typeLabelMap[String(type || '').toUpperCase()] || normalizeText(type)

const getJsonEntryCountLabel = (raw?: string, label = '项') => {
  const text = String(raw || '').trim()
  if (!text) return `${label}: 0`

  try {
    const parsed = JSON.parse(text)
    if (parsed && typeof parsed === 'object' && !Array.isArray(parsed)) {
      return `${label}: ${Object.keys(parsed).length}`
    }
  } catch (error) {
  }

  return `${label}: 已配置`
}

const getArgsLabel = (args?: string) => {
  const text = String(args || '').trim()
  if (!text) return '参数: 0'

  try {
    const parsed = JSON.parse(text)
    if (Array.isArray(parsed)) {
      return `参数: ${parsed.length}`
    }
  } catch (error) {
  }

  const segmentCount = text.split(/\s+/).filter(Boolean).length
  return `参数: ${segmentCount || 1}`
}

const getConnectionPrimary = (record: AiMcp) => {
  if (record.type === 'SSE') {
    return normalizeText(record.sseAddress, '未配置 SSE 地址')
  }

  return normalizeText(record.command, '未配置执行命令')
}

const getConnectionDetails = (record: AiMcp) => {
  if (record.type === 'SSE') {
    return [getJsonEntryCountLabel(record.requestHeaderConfig, '请求头')]
  }

  return [
    getArgsLabel(record.args),
    getJsonEntryCountLabel(record.envVars, '环境变量')
  ]
}

const copyMcpKey = async (value?: string) => {
  const text = String(value || '').trim()
  if (!text) {
    message.warning('当前没有可复制的 MCP Key')
    return
  }

  try {
    await navigator.clipboard.writeText(text)
    message.success('MCP Key 已复制')
  } catch (error) {
    message.error('复制失败，请手动复制')
  }
}

const query = reactive<QueryState>({})
const list = ref<AiMcp[]>([])
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

const toggleEnabledFilter = (value: 0 | 1) => {
  query.enabled = query.enabled === value ? undefined : value
}

const resetFilters = () => {
  query.mcpKey = undefined
  query.type = undefined
  query.enabled = undefined
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
        title: `确定删除选中的 ${n} 个 MCP 吗？`,
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

const modalInitial = ref<AiMcp | null>(null)

const fetchList = async () => {
  loading.value = true
  try {
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        mcpKey: query.mcpKey || undefined,
        type: query.type || undefined,
        enabled: query.enabled !== undefined && query.enabled !== null ? query.enabled : undefined
      }
    }
    const resp: PageResponse<AiMcp> = await aiMcpApi.queryPage(payload)
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

const openEdit = async (record: AiMcp) => {
  modal.mode = 'edit'
  const id = record.id
  if (id == null) return

  const detail = await aiMcpApi.detail(id)
  modalInitial.value = detail
  modal.open = true
}

const handleViewToggle = () => {
  viewMode.value = viewMode.value === 'grid' ? 'list' : 'grid'
}

const resolveGridColumns = () => {
  if (typeof window === 'undefined') return 3
  const width = pageRef.value?.clientWidth ?? window.innerWidth
  const n = Math.floor((width + MCP_CARD_GAP_PX) / (MCP_CARD_MIN_WIDTH_PX + MCP_CARD_GAP_PX))
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

const handleDeleteOne = async (id: number | string) => {
  if (id == null) return
  const msg = await aiMcpApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await aiMcpApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleFormSubmit = async (form: AiMcp) => {
  modal.submitting = true
  try {
    const payload: AiMcp = {...form}
    if (payload.enabled !== undefined && payload.enabled !== null) {
      payload.enabled = Number(payload.enabled) === 0 ? 0 : 1
    }

    let msg: string
    if (modal.mode === 'create') {
      delete (payload as { id?: unknown }).id
      msg = await aiMcpApi.create(payload)
    } else {
      msg = await aiMcpApi.update(payload)
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
.mcp-page {
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

.ellipsis {
  display: inline-block;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: bottom;
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

.multiline-2 {
  display: -webkit-box;
  overflow: hidden;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
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

.type-pill,
.status-pill,
.detail-pill {
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

.type-pill {
  color: #1d4ed8;
  background: #eff6ff;
}

.type-pill-stdio {
  color: #7c3aed;
  background: #f5f3ff;
}

.type-pill-steamable {
  color: #0f766e;
  background: #ecfdf5;
}

.status-pill {
  color: #166534;
  background: #f0fdf4;
}

.status-pill.off {
  color: #9a3412;
  background: #fff7ed;
}

.detail-pills {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.detail-pill {
  justify-content: flex-start;
  color: var(--text-secondary, #6b7280);
  background: color-mix(in srgb, var(--bg-surface) 85%, white);
}

.action-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding-inline: 4px;
}

@media (max-width: 720px) {
  .toolbar-input,
  .search-main-input,
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
