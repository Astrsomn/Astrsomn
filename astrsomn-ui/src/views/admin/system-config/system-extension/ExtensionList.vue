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
        :scroll="{ x: listScope === 'MARKETPLACE' ? 1480 : 1880 }"
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
              <a-button type="link" @click="openEdit(record)">编辑</a-button>
              <a-divider type="vertical" />

              <template v-if="record.type === 'MODEL_PROVIDER'">
                <a-popconfirm
                  title="从厂商 SPI 拉取可用模型并写入当前环境（已存在的 modelKey 会跳过）"
                  ok-text="确认"
                  cancel-text="取消"
                  @confirm="() => handleLoadModels(record.id)"
                >
                  <a-button type="link" :disabled="record.id == null">加载模型</a-button>
                </a-popconfirm>
                <a-divider type="vertical" />
                <a-popconfirm
                  title="确定按厂商卸载当前环境下的模型数据吗？（被实例引用的模型将保留）"
                  ok-text="确认"
                  cancel-text="取消"
                  @confirm="() => handleUnloadModels(record.id)"
                >
                  <a-button type="link" danger :disabled="record.id == null">卸载模型</a-button>
                </a-popconfirm>
                <a-divider type="vertical" />
              </template>

              <a-popconfirm
                v-if="record.status === 'APPLIED'"
                title="确定卸载该插件吗？"
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

              <a-divider type="vertical" />
              <a-popconfirm
                title="确定删除吗？"
                ok-text="确认"
                cancel-text="取消"
                @confirm="() => handleDeleteOne(record.id)"
              >
                <a-button type="link" danger :disabled="record.id == null">删除</a-button>
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
        :mode="modal.mode"
        :confirm-loading="modal.submitting"
        :initial="modalInitial"
        @submit="handleFormSubmit"
      />
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
  { title: '操作', key: 'actions', width: 380, fixed: 'right' as const }
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
  mode: 'create' as 'create' | 'edit',
  submitting: false
})

const modalInitial = ref<SystemExtension | null>(null)

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

const handleLoadModels = async (id: number | string | undefined) => {
  if (id == null) return
  try {
    const msg = await systemExtensionApi.loadModels(id)
    message.success(msg)
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '加载模型失败')
  }
}

const handleUnloadModels = async (id: number | string | undefined) => {
  if (id == null) return
  try {
    const msg = await systemExtensionApi.unloadModels(id)
    message.success(msg)
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '卸载模型失败')
  }
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

const openEdit = async (record: ExtensionTableRow) => {
  modal.mode = 'edit'
  const id = record.id
  if (id == null) return

  const detail = await systemExtensionApi.detail(id)
  modalInitial.value = detail
  modal.open = true
}

const handleDeleteOne = async (id: number | string | undefined) => {
  if (id == null) return
  const msg = await systemExtensionApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
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
  const msg = await systemExtensionApi.uninstall(id)
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleFormSubmit = async (form: SystemExtension) => {
  modal.submitting = true
  try {
    const payload: SystemExtension = { ...form }
    let msg: string
    if (modal.mode === 'create') {
      delete (payload as { id?: unknown }).id
      msg = await systemExtensionApi.create(payload)
    } else {
      msg = await systemExtensionApi.update(payload)
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

