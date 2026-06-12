<template>
  <AstPageShell
      :show-view-toggle="true"
      :view-mode="viewMode"
      :view-toggle-handler="handleViewToggle"
      :empty-text="t.list.emptyText"

  >
    <div ref="pageRef" class="mcp-page">
      <AstDataSection>
        <template #toolbar>
          <div class="toolbar">
            <AstSearchInput
                v-model="query.mcpKey"
                :button-label="t.list.searchButton"
                layout="toolbar"
                :placeholder="t.list.searchPlaceholder"
                @search="fetchList"
            />
            <AstStatusSwitch
                v-model="query.enabled"
                :options="[
                { label: t.list.status.all, value: undefined, color: '#1676fd', icon: CheckCircleOutlined },
                { label: t.list.status.enabled, value: 1, color: '#10b981', icon: CheckCircleOutlined },
                { label: t.list.status.disabled, value: 0, color: '#f43f5e', icon: StopOutlined }
              ]"
                @change="fetchList"
            />
          </div>
        </template>


        <!-- Card Grid Mode -->
        <div v-if="dataViewMode === 'card'" class="mcp-grid-section">
          <a-spin :spinning="loading">
            <div class="mcp-grid">
              <!-- Create Card -->
              <div class="add-card" @click="openCreate">
                <PlusOutlined class="add-icon"/>
                <span class="add-text">{{ t.list.create }}</span>
              </div>
              <!-- MCP Cards -->
              <McpCard
                  v-for="record in list"
                  :key="record.id"
                  :record="record"
                  :selected="record.id != null && selectedKeySet.has(record.id)"
                  @delete="handleDeleteOne"
                  @edit="openEdit"
                  @toggle="onMcpCardToggle"
              />
            </div>
          </a-spin>
        </div>

        <!-- Table Mode -->
        <AstDataView
            v-else
            :columns="columns"
            :data-source="list"
            :loading="loading"
            mode="table"
            :row-selection="rowSelection"
            :scroll="{ x: 730 }"
            :empty-text="t.list.emptyMatchText"
            row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'enabled'">
              <span :class="{ off: record.enabled !== 1 }" class="status-pill">
                {{ record.enabled === 1 ? t.list.statusPill.enabled : t.list.statusPill.disabled }}
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
                  :cancel-text="t.list.cancel"
                  :ok-text="t.list.confirm"
                  :title="t.list.deleteConfirm"
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
import {computed, reactive, ref} from 'vue'
import {message} from 'ant-design-vue'
import {usePageTranslation} from '@/locales/pages.ts'
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
import McpFormModal from './component/McpFormModal.vue'
import McpCard from './component/McpCard.vue'
import {type AiMcp, aiMcpApi, type PageResponse} from '@/api/aiMcp'

const t = usePageTranslation('ai-mcp')

const props = withDefaults(defineProps<{
  initialViewMode?: 'grid' | 'list'
}>(), {
  initialViewMode: 'list'
})

const pageRef = ref<HTMLElement | null>(null)
const viewMode = ref<'grid' | 'list'>(props.initialViewMode)
const dataViewMode = computed<'card' | 'table'>(() => (viewMode.value === 'grid' ? 'card' : 'table'))

type QueryState = {
  mcpKey?: string
  type?: string
  enabled?: number
}

const columns = computed(() => [
  {title: t.value.list.column.mcpKey, dataIndex: 'mcpKey', key: 'mcpKey', width: 160, ellipsis: true, copyable: true},
  {title: t.value.list.column.serverName, dataIndex: 'serverName', key: 'serverName', width: 160, ellipsis: true},
  {title: t.value.list.column.type, dataIndex: 'type', key: 'type', width: 90},
  {title: t.value.list.column.enabled, key: 'enabled', width: 80},
  {title: t.value.list.column.createTime, dataIndex: 'createTime', key: 'createTime', width: 140, dateFormat: true},
  {title: t.value.list.column.actions, key: 'actions', width: 100, fixed: 'right' as const}
])

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

const getJsonEntryCountLabel = (raw?: string, label?: string) => {
  const effectiveLabel = label || t.value.list.item
  const text = String(raw || '').trim()
  if (!text) return t.value.list.itemCount.replace('{label}', effectiveLabel).replace('{count}', '0')

  try {
    const parsed = JSON.parse(text)
    if (parsed && typeof parsed === 'object' && !Array.isArray(parsed)) {
      return t.value.list.itemCount.replace('{label}', effectiveLabel).replace('{count}', String(Object.keys(parsed).length))
    }
  } catch (error) {
  }

  return t.value.list.itemConfigured.replace('{label}', effectiveLabel)
}

const getArgsLabel = (args?: string) => {
  const text = String(args || '').trim()
  if (!text) return t.value.list.argsCount.replace('{count}', '0')

  try {
    const parsed = JSON.parse(text)
    if (Array.isArray(parsed)) {
      return t.value.list.argsCount.replace('{count}', String(parsed.length))
    }
  } catch (error) {
  }

  const segmentCount = text.split(/\s+/).filter(Boolean).length
  return t.value.list.argsCount.replace('{count}', String(segmentCount || 1))
}

const getConnectionPrimary = (record: AiMcp) => {
  if (record.type === 'SSE') {
    return normalizeText(record.sseAddress, t.value.list.sseAddressNotConfigured)
  }

  return normalizeText(record.command, t.value.list.commandNotConfigured)
}

const getConnectionDetails = (record: AiMcp) => {
  if (record.type === 'SSE') {
    return [getJsonEntryCountLabel(record.requestHeaderConfig, t.value.list.requestHeaders)]
  }

  return [
    getArgsLabel(record.args),
    getJsonEntryCountLabel(record.envVars, t.value.list.envVars)
  ]
}

const copyMcpKey = async (value?: string) => {
  const text = String(value || '').trim()
  if (!text) {
    message.warning(t.value.list.noKeyToCopy)
    return
  }

  try {
    await navigator.clipboard.writeText(text)
    message.success(t.value.list.keyCopied)
  } catch (error) {
    message.error(t.value.list.copyFailed)
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
const selectedKeySet = computed(() => new Set(selectedRowKeys.value))

const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys: Array<number | string>) => {
    selectedRowKeys.value = keys
  }
}))

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

const onMcpCardToggle = (id: number | string, checked: boolean) => {
  if (checked) {
    if (!selectedRowKeys.value.includes(id)) {
      selectedRowKeys.value = [...selectedRowKeys.value, id]
    }
    return
  }
  selectedRowKeys.value = selectedRowKeys.value.filter((k) => k !== id)
}

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
    message.error(err?.message || t.value.list.saveFailed)
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
  align-items: center;
  gap: 14px;
  flex-wrap: wrap;
}

/* ── Card Grid (matching AgentSection.vue) ── */
.mcp-grid-section {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.mcp-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 14px;
}

.mcp-grid > * {
  animation: cardEnter 0.35s ease both;
}

.mcp-grid > *:nth-child(1) { animation-delay: 0ms; }
.mcp-grid > *:nth-child(2) { animation-delay: 40ms; }
.mcp-grid > *:nth-child(3) { animation-delay: 80ms; }
.mcp-grid > *:nth-child(4) { animation-delay: 120ms; }
.mcp-grid > *:nth-child(5) { animation-delay: 160ms; }
.mcp-grid > *:nth-child(6) { animation-delay: 200ms; }
.mcp-grid > *:nth-child(7) { animation-delay: 240ms; }
.mcp-grid > *:nth-child(8) { animation-delay: 280ms; }
.mcp-grid > *:nth-child(9) { animation-delay: 320ms; }
.mcp-grid > *:nth-child(10) { animation-delay: 360ms; }
.mcp-grid > *:nth-child(n+11) { animation-delay: 400ms; }

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

/* ── Table Cell Styles ── */
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
  color: var(--text-secondary);
  background: color-mix(in srgb, var(--bg-surface) 85%, transparent);
}

.action-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding-inline: 4px;
}
</style>
