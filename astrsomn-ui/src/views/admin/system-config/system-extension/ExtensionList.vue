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
              <ToolbarSearchPill
                v-model="extensionNameInput"
                placeholder="搜索扩展名称"
                layout="toolbar"
                @search="fetchList"
              />
            </template>

            <template #right>
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

          <div v-if="list.length > 0" class="extension-grid">
            <ExtensionCard
              v-for="item in list"
              :key="rowKey(item)"
              :record="item"
              :list-scope="listScope"
              :selected="isRowSelected(item)"
              @toggle-select="(checked) => onCardToggleSelect(item, checked)"
              @install="installFromCatalog(item)"
              @load-models="openLoadModelsPreview(item)"
              @unload-models="openUnloadModelsPreview(item)"
              @disable-provider-models="handleDisableProviderModels(item.id)"
              @uninstall="handleUninstall(item.id)"
              @apply="handleApply(item.id)"
            />
          </div>
          <div v-else class="extension-empty">
            <a-empty :description="listScope === 'MARKETPLACE' ? '暂无市场插件' : '暂无已安装扩展'" />
          </div>

          <div v-if="listScope === 'INSTALLED'" class="pagination-wrap">
            <a-pagination
              :current="page.pageNum"
              :page-size="page.pageSize"
              :total="page.total"
              :show-size-changer="false"
              @change="onPageChange"
            />
          </div>

          <ExtensionModelSyncDialog
            v-model:open="modelSyncModal.open"
            :mode="modelSyncModal.mode"
            :extension-label="modelSyncModal.extensionLabel"
            :loading-preview="modelSyncModal.loadingPreview"
            :preview-error="modelSyncModal.previewError"
            :load-preview="modelSyncModal.loadPreview"
            :unload-preview="modelSyncModal.unloadPreview"
            :confirm="confirmModelSync"
            @cancel="resetModelSyncModal"
          />
        </a-layout-content>
      </a-layout>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { DeleteOutlined } from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AdminListToolbar from '@/components/home/AdminListToolbar.vue'
import BaseOverview from '@/components/home/BaseOverview.vue'
import ToolbarSearchPill from '@/components/home/ToolbarSearchPill.vue'
import ExtensionCard from './ExtensionCard.vue'
import ExtensionModelSyncDialog from './ExtensionModelSyncDialog.vue'
import type { ExtensionRow } from './extensionDisplay'
import {
  systemExtensionApi,
  type ExtensionModelLoadPreview,
  type ExtensionModelUnloadPreview,
  type PageResponse,
  type SystemExtension,
  type SystemExtensionListScope,
  type SystemExtensionQueryPagePayload
} from '@/api/systemExtension'

const listScope = ref<SystemExtensionListScope>('INSTALLED')
const typeTabKey = ref('ALL')

const extensionNameInput = ref('')
const list = ref<ExtensionRow[]>([])

const listSummaryText = computed(() => {
  if (listScope.value === 'MARKETPLACE') {
    return `市场目录 ${list.value.length} 条（Mock），安装后请到「已安装插件」管理。`
  }
  return `当前页 ${list.value.length} 条扩展记录，已选 ${selectedRowKeys.value.length} 条。`
})

function rowKey(record: ExtensionRow) {
  if (listScope.value === 'MARKETPLACE') {
    return String(record.extensionKey ?? '')
  }
  return record.id != null ? String(record.id) : String(record.extensionKey ?? '')
}

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

function isRowSelected(record: ExtensionRow) {
  return selectedRowKeys.value.includes(rowKey(record))
}

function onCardToggleSelect(record: ExtensionRow, checked: boolean) {
  const k = rowKey(record)
  if (checked) {
    selectedRowKeys.value = Array.from(new Set([...selectedRowKeys.value, k]))
    return
  }
  selectedRowKeys.value = selectedRowKeys.value.filter((x) => x !== k)
}

const toggleSelectAllCurrentPage = (checked: boolean) => {
  if (checked) {
    selectedRowKeys.value = Array.from(new Set([...selectedRowKeys.value, ...currentPageIds.value]))
    return
  }
  selectedRowKeys.value = selectedRowKeys.value.filter((id) => !currentPageIds.value.includes(id))
}

function onListScopeSelect({ key }: { key: string }) {
  const next = key as SystemExtensionListScope
  if (next !== 'MARKETPLACE' && next !== 'INSTALLED') return
  if (listScope.value === next) return
  listScope.value = next
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

const onTypeTabChange = () => {
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

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
    const n = extensionNameInput.value?.trim().toLowerCase()
    if (n) {
      rows = rows.filter((r) => (r.extensionName || '').toLowerCase().includes(n))
    }
    list.value = rows as ExtensionRow[]
    page.total = rows.length
    return
  }

  const payload: SystemExtensionQueryPagePayload = {
    pageNo: page.pageNum,
    pageSize: page.pageSize,
    param: {
      extensionName: extensionNameInput.value?.trim() || undefined,
      listScope: listScope.value,
      type: typeTabKey.value === 'ALL' ? undefined : typeTabKey.value
    }
  }

  const resp: PageResponse<SystemExtension> = await systemExtensionApi.queryPage(payload)
  list.value = (resp.list || []) as ExtensionRow[]
  page.total = resp.total || 0
}

const installFromCatalog = async (item: ExtensionRow) => {
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

async function openLoadModelsPreview(record: ExtensionRow) {
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

async function openUnloadModelsPreview(record: ExtensionRow) {
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
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-default);
  background: var(--bg-card) !important;
  overflow: auto;
}

.extension-sider :deep(.ant-layout-sider-children) {
  padding: 8px 0;
}

.extension-sider :deep(.ant-menu) {
  background: transparent;
  color: var(--text-primary);
  border-inline-end: none !important;
}

.extension-sider :deep(.ant-menu-item) {
  color: var(--text-secondary);
  border-radius: var(--radius-md);
  margin: 4px 8px;
  width: auto;
}

.extension-sider :deep(.ant-menu-item-selected) {
  background: color-mix(in srgb, var(--primary) 16%, transparent) !important;
  color: var(--primary-light) !important;
}

.extension-sider :deep(.ant-menu-item:hover) {
  color: var(--text-hover);
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

.type-tabs :deep(.ant-tabs-tab) {
  color: var(--text-secondary);
}

.type-tabs :deep(.ant-tabs-tab-active .ant-tabs-tab-btn) {
  color: var(--primary-light) !important;
}

.type-tabs :deep(.ant-tabs-ink-bar) {
  background: var(--primary-gradient);
}

.ghost-btn {
  height: 40px;
  border-radius: var(--radius-lg);
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

.extension-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  margin-top: 16px;
}

.extension-empty {
  margin-top: 48px;
  padding: 24px;
}

.extension-empty :deep(.ant-empty-description) {
  color: var(--text-muted);
}

.pagination-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
  flex-wrap: wrap;
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

  .extension-grid {
    grid-template-columns: 1fr;
  }

  .pagination-wrap {
    justify-content: center;
  }
}
</style>
