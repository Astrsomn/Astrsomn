<template>
  <AdminPageShell
    title="系统扩展"
    description="管理系统扩展包（SYSTEM_EXTENSION），对接 SystemExtensionController。"
    empty-text="暂无系统扩展。"
  >
    <div class="extension-page">
      <a-layout class="extension-layout">
        <a-layout-sider width="208" theme="light" class="extension-sider">
          <a-menu mode="inline" :selected-keys="[listScope]" @click="onListScopeSelect">
            <a-menu-item key="MARKETPLACE">插件市场</a-menu-item>
            <a-menu-item key="INSTALLED">已安装插件</a-menu-item>
          </a-menu>
        </a-layout-sider>
        <a-layout-content class="extension-main">
      <AdminListToolbar>
        <template #left>
          <div class="search-cluster">
            <a-input
              v-model:value="query.extensionKey"
              placeholder="搜索扩展 Key"
              class="toolbar-input search-main-input"
              allow-clear
              @pressEnter="fetchList"
            >
              <template #prefix><key-outlined /></template>
            </a-input>
            <a-input
              v-model:value="query.extensionName"
              placeholder="搜索扩展名称"
              class="toolbar-input search-sub-input"
              allow-clear
              @pressEnter="fetchList"
            >
              <template #prefix><file-text-outlined /></template>
            </a-input>
          </div>

          <div
            v-if="listScope === 'INSTALLED'"
            class="status-switch"
            role="group"
            aria-label="状态筛选"
          >
            <a-button
              class="status-btn"
              :class="{ active: query.status === 'INSTALLED' }"
              @click="toggleStatusFilter('INSTALLED')"
            >
              已安装
            </a-button>
            <a-button
              class="status-btn"
              :class="{ active: query.status === 'APPLIED' }"
              @click="toggleStatusFilter('APPLIED')"
            >
              已应用
            </a-button>
            <a-button
              class="status-btn"
              :class="{ active: query.status === 'UNINSTALLED' }"
              @click="toggleStatusFilter('UNINSTALLED')"
            >
              未安装
            </a-button>
          </div>
        </template>

        <template #right>
          <a-button type="primary" class="primary-btn" @click="fetchList">
            <template #icon><search-outlined /></template>
            查询
          </a-button>

          <a-popconfirm
            v-if="listScope === 'INSTALLED' && selectedRowKeys.length > 0"
            title="确定批量删除选中的扩展吗？"
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

      <a-tabs v-model:activeKey="typeTabKey" class="type-tabs" @change="onTypeTabChange">
        <a-tab-pane key="ALL" tab="全部" />
        <a-tab-pane key="MODEL_PROVIDER" tab="模型" />
        <a-tab-pane key="VECTOR_STORE" tab="向量库" />
        <a-tab-pane key="MCP" tab="MCP" />
      </a-tabs>

      <BaseOverview
        :list-length="list.length"
        :selected-count="selectedRowKeys.length"
        :all-current-selected="allCurrentSelected"
        :part-current-selected="partCurrentSelected"
        :show-actions="list.length > 0"
        :summary-text="listSummaryText"
        @toggle-select-all="toggleSelectAllCurrentPage"
      />

      <a-table
        :columns="tableColumns"
        :data-source="list"
        :pagination="false"
        :row-key="tableRowKey"
        :row-selection="tableRowSelection"
        :scroll="{ x: listScope === 'MARKETPLACE' ? 1480 : 1920 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'type'">
            {{ extensionTypeLabel(record.type) }}
          </template>
          <template v-else-if="column.key === 'description'">
            <span class="desc-preview">{{ preview(record.description) }}</span>
          </template>
          <template v-else-if="column.key === 'jarName'">
            <code class="jar-code">{{ record.jarName || '—' }}</code>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="statusTagColor(record.status)">{{ statusLabel(record.status) }}</a-tag>
          </template>
          <template v-else-if="column.key === 'applied'">
            <a-tag :color="appliedTagColor(record.applied)">{{ appliedLabel(record.applied) }}</a-tag>
          </template>
          <template v-else-if="column.key === 'actions'">
            <template v-if="listScope === 'MARKETPLACE'">
              <a-button type="link" @click="installFromCatalog(record)">安装</a-button>
            </template>
            <template v-else>
              <template v-if="record.type === 'MODEL_PROVIDER'">
                <a-button type="link" :disabled="record.id == null" @click="openLoadModelsPreview(record)">
                  加载模型
                </a-button>
                <a-divider type="vertical" />
                <a-button type="link" danger :disabled="record.id == null" @click="openUnloadModelsPreview(record)">
                  卸载模型
                </a-button>
                <a-divider type="vertical" />
                <a-popconfirm
                  title="将该厂商在当前环境下的全部 AI 模型状态设为停用（disabled），确认？"
                  ok-text="确认"
                  cancel-text="取消"
                  @confirm="() => handleDisableProviderModels(record.id)"
                >
                  <a-button type="link" :disabled="record.id == null">禁用</a-button>
                </a-popconfirm>
                <a-divider type="vertical" />
              </template>

              <a-popconfirm
                v-if="record.status === 'APPLIED'"
                :title="
                  record.type === 'MODEL_PROVIDER'
                    ? '卸载插件。模型类扩展：若仍有 AI 实例引用该厂商模型，服务端将拒绝卸载。'
                    : '确定卸载该插件吗？'
                "
                ok-text="确认"
                cancel-text="取消"
                @confirm="() => handleUninstall(record.id)"
              >
                <a-button type="link" danger :disabled="record.id == null">卸载</a-button>
              </a-popconfirm>

              <a-popconfirm
                v-else
                title="确定应用该插件吗？"
                ok-text="确认"
                cancel-text="取消"
                @confirm="() => handleApply(record.id)"
              >
                <a-button type="link" :disabled="!record.jarName || record.id == null">应用</a-button>
              </a-popconfirm>
            </template>
          </template>
        </template>
      </a-table>

      <div v-if="listScope === 'INSTALLED'" class="pagination-wrap">
        <a-pagination
          :current="page.pageNum"
          :page-size="page.pageSize"
          :total="page.total"
          :show-size-changer="false"
          @change="onPageChange"
        />
      </div>

      <ExtensionFormModel
        v-model:open="modal.open"
        mode="create"
        :confirm-loading="modal.submitting"
        :initial="null"
        @submit="handleFormSubmit"
      />

      <a-modal
        v-model:open="modelSyncModal.open"
        :title="modelSyncModalTitle"
        width="640px"
        destroy-on-close
        :ok-text="modelSyncModalOkText"
        :ok-button-props="modelSyncOkButtonProps"
        @ok="confirmModelSync"
        @cancel="resetModelSyncModal"
      >
        <div v-if="modelSyncModal.previewError" class="model-sync-alert">
          <a-alert type="error" :message="modelSyncModal.previewError" show-icon />
        </div>
        <a-spin v-else :spinning="modelSyncModal.loadingPreview">
          <template v-if="modelSyncModal.mode === 'load' && modelSyncModal.loadPreview">
            <p v-if="modelSyncModalEmptyHint" class="model-sync-hint">{{ modelSyncModalEmptyHint }}</p>
            <div v-if="(modelSyncModal.loadPreview.skippedInvalidCount ?? 0) > 0" class="model-sync-hint">
              厂商返回条目中有 {{ modelSyncModal.loadPreview.skippedInvalidCount }} 条缺少 modelKey，将跳过。
            </div>
            <div class="preview-section">
              <div class="preview-section-title">将保存（新增）</div>
              <div v-if="(modelSyncModal.loadPreview.toCreate?.length ?? 0) > 0" class="preview-list">
                <div v-for="(r, i) in modelSyncModal.loadPreview.toCreate" :key="'c' + i" class="preview-line">
                  {{ formatPreviewRow(r) }}
                </div>
              </div>
              <div v-else class="preview-empty">无</div>
            </div>
            <div class="preview-section">
              <div class="preview-section-title">已存在将跳过</div>
              <div v-if="(modelSyncModal.loadPreview.skippedExisting?.length ?? 0) > 0" class="preview-list">
                <div v-for="(r, i) in modelSyncModal.loadPreview.skippedExisting" :key="'s' + i" class="preview-line">
                  {{ formatPreviewRow(r) }}
                </div>
              </div>
              <div v-else class="preview-empty">无</div>
            </div>
          </template>
          <template v-else-if="modelSyncModal.mode === 'unload' && modelSyncModal.unloadPreview">
            <p v-if="modelSyncModalEmptyHint" class="model-sync-hint">{{ modelSyncModalEmptyHint }}</p>
            <div class="preview-section">
              <div class="preview-section-title">将卸载（删除）</div>
              <div v-if="(modelSyncModal.unloadPreview.toRemove?.length ?? 0) > 0" class="preview-list">
                <div v-for="(r, i) in modelSyncModal.unloadPreview.toRemove" :key="'r' + i" class="preview-line">
                  {{ formatPreviewRow(r) }}
                </div>
              </div>
              <div v-else class="preview-empty">无</div>
            </div>
            <div class="preview-section">
              <div class="preview-section-title">因实例引用将保留</div>
              <div v-if="(modelSyncModal.unloadPreview.keptReferenced?.length ?? 0) > 0" class="preview-list">
                <div v-for="(r, i) in modelSyncModal.unloadPreview.keptReferenced" :key="'k' + i" class="preview-line">
                  {{ formatPreviewRow(r) }}
                </div>
              </div>
              <div v-else class="preview-empty">无</div>
            </div>
          </template>
        </a-spin>
      </a-modal>
        </a-layout-content>
      </a-layout>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  DeleteOutlined,
  FileTextOutlined,
  KeyOutlined,
  PlusOutlined,
  SearchOutlined
} from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AdminListToolbar from '@/components/home/AdminListToolbar.vue'
import BaseOverview from '@/components/home/BaseOverview.vue'
import ExtensionFormModel from './ExtensionFormModel.vue'
import {
  systemExtensionApi,
  type ExtensionMarketplaceItem,
  type ExtensionModelLoadPreview,
  type ExtensionModelSyncPreviewRow,
  type ExtensionModelUnloadPreview,
  type PageResponse,
  type SystemExtension,
  type SystemExtensionListScope,
  type SystemExtensionQueryPagePayload
} from '@/api/systemExtension'

type ExtensionTableRow = SystemExtension & ExtensionMarketplaceItem

type QueryState = {
  extensionKey?: string
  extensionName?: string
  status?: string
}

const preview = (raw: string | undefined) => {
  if (!raw) return '—'
  const text = raw.replace(/\s+/g, ' ').trim()
  return text.length > 84 ? `${text.slice(0, 84)}…` : text
}

function statusLabel(value: string | undefined) {
  if (value === 'INSTALLED') return '已安装'
  if (value === 'APPLIED') return '已应用'
  if (value === 'UNINSTALLED') return '未安装'
  return value ?? '—'
}

function statusTagColor(value: string | undefined) {
  if (value === 'APPLIED') return 'green'
  if (value === 'INSTALLED') return 'blue'
  if (value === 'UNINSTALLED') return 'red'
  return 'default'
}

function appliedLabel(value: string | undefined) {
  if (value === 'Y') return '已应用'
  if (value === 'N') return '未应用'
  return value ?? '—'
}

function appliedTagColor(value: string | undefined) {
  if (value === 'Y') return 'green'
  return 'default'
}

function extensionTypeLabel(type: string | undefined) {
  if (!type) return '—'
  if (type === 'MODEL_PROVIDER') return '模型'
  if (type === 'VECTOR_STORE') return '向量库'
  if (type === 'MCP') return 'MCP'
  return type
}

const listScope = ref<SystemExtensionListScope>('INSTALLED')
const typeTabKey = ref('ALL')

const allColumns = [
  { title: '扩展 Key', dataIndex: 'extensionKey', key: 'extensionKey', width: 180, ellipsis: true },
  { title: '扩展名称', dataIndex: 'extensionName', key: 'extensionName', width: 210, ellipsis: true },
  { title: '类型', dataIndex: 'type', key: 'type', width: 120, ellipsis: true },
  { title: '版本', dataIndex: 'version', key: 'version', width: 100, ellipsis: true },
  { title: '作者', dataIndex: 'author', key: 'author', width: 120, ellipsis: true },
  { title: 'jarName', key: 'jarName', width: 260, ellipsis: true },
  { title: '状态', key: 'status', width: 110 },
  { title: '已应用', key: 'applied', width: 100 },
  { title: '描述', dataIndex: 'description', key: 'description', width: 320, ellipsis: true },
  { title: '操作', key: 'actions', width: 400, fixed: 'right' as const }
]

const tableColumns = computed(() => {
  if (listScope.value === 'MARKETPLACE') {
    return allColumns.filter((c) => c.key !== 'status' && c.key !== 'applied')
  }
  return allColumns
})

const query = reactive<QueryState>({})
const list = ref<ExtensionTableRow[]>([])

const listSummaryText = computed(() => {
  if (listScope.value === 'MARKETPLACE') {
    return `市场目录 ${list.value.length} 条（Mock），安装后请到「已安装插件」管理。`
  }
  return `当前页 ${list.value.length} 条扩展记录，已选 ${selectedRowKeys.value.length} 条。`
})

const tableRowKey = (record: ExtensionTableRow) => {
  if (listScope.value === 'MARKETPLACE') {
    return String(record.extensionKey ?? '')
  }
  return record.id != null ? String(record.id) : String(record.extensionKey ?? '')
}

const tableRowSelection = computed(() => (listScope.value === 'INSTALLED' ? rowSelection.value : undefined))

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

const toggleStatusFilter = (value: 'INSTALLED' | 'APPLIED' | 'UNINSTALLED') => {
  query.status = query.status === value ? undefined : value
}

function onListScopeSelect({ key }: { key: string }) {
  const next = key as SystemExtensionListScope
  if (next !== 'MARKETPLACE' && next !== 'INSTALLED') return
  if (listScope.value === next) return
  listScope.value = next
  query.status = undefined
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

const onTypeTabChange = () => {
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

const resetFilters = () => {
  query.extensionKey = undefined
  query.extensionName = undefined
  query.status = undefined
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

const modal = reactive({
  open: false,
  submitting: false
})

type ModelSyncMode = 'load' | 'unload'

const modelSyncModal = reactive({
  open: false,
  mode: null as ModelSyncMode | null,
  extensionId: null as number | string | null,
  extensionLabel: '',
  loadingPreview: false,
  previewError: '',
  loadPreview: null as ExtensionModelLoadPreview | null,
  unloadPreview: null as ExtensionModelUnloadPreview | null
})

const fetchList = async () => {
  if (listScope.value === 'MARKETPLACE') {
    const typeQ = typeTabKey.value === 'ALL' ? undefined : typeTabKey.value
    let rows = await systemExtensionApi.marketplaceCatalog(typeQ)
    const k = query.extensionKey?.trim().toLowerCase()
    if (k) {
      rows = rows.filter((r) => (r.extensionKey || '').toLowerCase().includes(k))
    }
    const n = query.extensionName?.trim().toLowerCase()
    if (n) {
      rows = rows.filter((r) => (r.extensionName || '').toLowerCase().includes(n))
    }
    list.value = rows as ExtensionTableRow[]
    page.total = rows.length
    return
  }

  const payload: SystemExtensionQueryPagePayload = {
    pageNo: page.pageNum,
    pageSize: page.pageSize,
    param: {
      extensionKey: query.extensionKey || undefined,
      extensionName: query.extensionName || undefined,
      status: query.status || undefined,
      listScope: listScope.value,
      type: typeTabKey.value === 'ALL' ? undefined : typeTabKey.value
    }
  }

  const resp: PageResponse<SystemExtension> = await systemExtensionApi.queryPage(payload)
  list.value = (resp.list || []) as ExtensionTableRow[]
  page.total = resp.total || 0
}

const installFromCatalog = async (item: ExtensionTableRow) => {
  const payload: SystemExtension = {
    extensionKey: item.extensionKey,
    extensionName: item.extensionName,
    type: item.type,
    version: item.version,
    author: item.author,
    description: item.description,
    jarName: item.jarName,
    providerCode: item.providerCode,
    applied: 'N',
    status: 'INSTALLED'
  }
  const msg = await systemExtensionApi.create(payload)
  message.success(msg)
  message.info('可在「已安装插件」中查看、应用插件或加载模型。')
}

function formatPreviewRow(r: ExtensionModelSyncPreviewRow) {
  const parts = [r.modelKey, r.modelName, r.modelType, r.provider].filter(Boolean)
  return parts.length ? parts.join(' · ') : '—'
}

const modelSyncModalTitle = computed(() => {
  const name = modelSyncModal.extensionLabel || '扩展'
  if (modelSyncModal.mode === 'load') return `确认加载模型 — ${name}`
  if (modelSyncModal.mode === 'unload') return `确认卸载模型 — ${name}`
  return '模型同步'
})

const modelSyncModalOkText = computed(() =>
  modelSyncModal.mode === 'load'
    ? '确认加载模型'
    : modelSyncModal.mode === 'unload'
      ? '确认卸载模型'
      : '确认'
)

const modelSyncOkDisabled = computed(
  () =>
    modelSyncModal.loadingPreview || Boolean(modelSyncModal.previewError) || modelSyncModal.mode == null
)

const modelSyncOkButtonProps = computed(() => ({
  disabled: modelSyncOkDisabled.value,
  danger: modelSyncModal.mode === 'unload'
}))

const modelSyncModalEmptyHint = computed(() => {
  if (modelSyncModal.loadingPreview || modelSyncModal.previewError) return ''
  if (modelSyncModal.mode === 'load' && modelSyncModal.loadPreview) {
    const p = modelSyncModal.loadPreview
    const total =
      (p.toCreate?.length ?? 0) + (p.skippedExisting?.length ?? 0) + (p.skippedInvalidCount ?? 0)
    if (total === 0) return '厂商未返回可用模型条目，确认后不会产生新增。'
  }
  if (modelSyncModal.mode === 'unload' && modelSyncModal.unloadPreview) {
    const p = modelSyncModal.unloadPreview
    const total = (p.toRemove?.length ?? 0) + (p.keptReferenced?.length ?? 0)
    if (total === 0) return '当前环境下该厂商暂无模型记录，确认后不会产生删除。'
  }
  return ''
})

function resetModelSyncModal() {
  modelSyncModal.open = false
  modelSyncModal.mode = null
  modelSyncModal.extensionId = null
  modelSyncModal.extensionLabel = ''
  modelSyncModal.previewError = ''
  modelSyncModal.loadPreview = null
  modelSyncModal.unloadPreview = null
  modelSyncModal.loadingPreview = false
}

async function openLoadModelsPreview(record: ExtensionTableRow) {
  const id = record.id
  if (id == null) return
  modelSyncModal.open = true
  modelSyncModal.mode = 'load'
  modelSyncModal.extensionId = id
  modelSyncModal.extensionLabel = String(record.extensionName || record.extensionKey || id)
  modelSyncModal.previewError = ''
  modelSyncModal.loadPreview = null
  modelSyncModal.unloadPreview = null
  modelSyncModal.loadingPreview = true
  try {
    const data = await systemExtensionApi.previewLoadModels(id)
    modelSyncModal.loadPreview = data ?? {
      toCreate: [],
      skippedExisting: [],
      skippedInvalidCount: 0
    }
  } catch (e: unknown) {
    const err = e as { message?: string }
    modelSyncModal.previewError = err?.message || '加载预览失败'
    modelSyncModal.loadPreview = null
  } finally {
    modelSyncModal.loadingPreview = false
  }
}

async function openUnloadModelsPreview(record: ExtensionTableRow) {
  const id = record.id
  if (id == null) return
  modelSyncModal.open = true
  modelSyncModal.mode = 'unload'
  modelSyncModal.extensionId = id
  modelSyncModal.extensionLabel = String(record.extensionName || record.extensionKey || id)
  modelSyncModal.previewError = ''
  modelSyncModal.loadPreview = null
  modelSyncModal.unloadPreview = null
  modelSyncModal.loadingPreview = true
  try {
    const data = await systemExtensionApi.previewUnloadModels(id)
    modelSyncModal.unloadPreview = data ?? { toRemove: [], keptReferenced: [] }
  } catch (e: unknown) {
    const err = e as { message?: string }
    modelSyncModal.previewError = err?.message || '卸载预览失败'
    modelSyncModal.unloadPreview = null
  } finally {
    modelSyncModal.loadingPreview = false
  }
}

async function confirmModelSync() {
  const id = modelSyncModal.extensionId
  const mode = modelSyncModal.mode
  if (id == null || mode == null || modelSyncModal.previewError) return
  try {
    if (mode === 'load') {
      const msg = await systemExtensionApi.loadModels(id)
      message.success(msg)
    } else {
      const msg = await systemExtensionApi.unloadModels(id)
      message.success(msg)
    }
    resetModelSyncModal()
    void fetchList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || (mode === 'load' ? '加载模型失败' : '卸载模型失败'))
    throw e
  }
}

const onPageChange = (p: number) => {
  page.pageNum = p
  void fetchList()
}

const openCreate = () => {
  modal.open = true
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await systemExtensionApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleApply = async (id: number | string | undefined) => {
  if (id == null) return
  const msg = await systemExtensionApi.apply(id)
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleUninstall = async (id: number | string | undefined) => {
  if (id == null) return
  try {
    const msg = await systemExtensionApi.uninstall(id)
    message.success(msg)
    selectedRowKeys.value = []
    void fetchList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '卸载失败')
  }
}

const handleDisableProviderModels = async (id: number | string | undefined) => {
  if (id == null) return
  try {
    const msg = await systemExtensionApi.disableProviderModels(id)
    message.success(msg)
    void fetchList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '禁用模型失败')
  }
}

const handleFormSubmit = async (form: SystemExtension) => {
  modal.submitting = true
  try {
    const payload: SystemExtension = { ...form }
    delete (payload as { id?: unknown }).id
    const msg = await systemExtensionApi.create(payload)
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
.extension-page {
  padding: 0 4px;
}

.extension-layout {
  background: transparent;
  align-items: stretch;
}

.extension-sider {
  border-radius: 12px;
  border: 1px solid var(--border-default);
  background: var(--bg-card) !important;
  overflow: auto;
}

.extension-sider :deep(.ant-layout-sider-children) {
  padding: 8px 0;
}

.extension-main {
  padding-left: 16px;
  min-width: 0;
}

.type-tabs {
  margin: 12px 0 8px;
}

.type-tabs :deep(.ant-tabs-nav) {
  margin-bottom: 0;
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
  width: 260px;
}

.search-sub-input {
  width: 240px;
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

.jar-code {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  color: rgba(0, 0, 0, 0.65);
  font-size: 12px;
}

.model-sync-alert {
  margin-bottom: 8px;
}

.model-sync-hint {
  margin: 0 0 12px;
  color: rgba(0, 0, 0, 0.55);
  font-size: 13px;
}

.preview-section {
  margin-bottom: 16px;
}

.preview-section-title {
  font-weight: 600;
  margin-bottom: 8px;
  font-size: 13px;
}

.preview-list {
  max-height: 220px;
  overflow: auto;
  border: 1px solid var(--border-default, #f0f0f0);
  border-radius: 8px;
  padding: 8px 10px;
  background: var(--bg-surface, #fafafa);
}

.preview-line {
  font-size: 12px;
  line-height: 1.5;
  padding: 2px 0;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}

.preview-empty {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.45);
}

@media (max-width: 720px) {
  .extension-layout {
    flex-direction: column;
  }

  .extension-sider {
    width: 100% !important;
    max-width: 100% !important;
    margin-bottom: 12px;
  }

  .extension-main {
    padding-left: 0;
  }

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

