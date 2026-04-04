<template>
  <div class="extension-subpage">
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
          v-if="selectedRowKeys.length > 0"
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
    <BaseOverview
      :list-length="list.length"
      :selected-count="selectedRowKeys.length"
      :all-current-selected="allCurrentSelected"
      :part-current-selected="partCurrentSelected"
      :show-actions="list.length > 0"
      :summary-text="listSummaryText"
      @toggle-select-all="toggleSelectAllCurrentPage"
    />
    <a-tabs v-model:activeKey="typeTabKey" class="type-tabs" @change="onTypeTabChange">
      <a-tab-pane key="ALL" tab="全部" />
      <a-tab-pane key="MODEL_PROVIDER" tab="模型" />
      <a-tab-pane key="VECTOR_STORE" tab="向量库" />
      <a-tab-pane key="MCP" tab="MCP" />
    </a-tabs>



    <div v-if="list.length > 0" class="extension-grid">
      <ExtensionInstalledCard
        v-for="item in list"
        :key="rowKey(item)"
        :record="item"
        :selected="isRowSelected(item)"
        @toggle-select="(checked) => onCardToggleSelect(item, checked)"
        @load-models="openLoadModelsPreview(item)"
        @unload-models="openUnloadModelsPreview(item)"
        @revoke-apply="handleRevokeApply(item.id)"
        @uninstall="handleUninstall(item.id)"
        @apply="handleApply(item.id)"
      />
    </div>
    <div v-else class="extension-empty">
      <a-empty description="暂无已安装扩展" />
    </div>

    <div class="pagination-wrap">
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
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { DeleteOutlined } from '@ant-design/icons-vue'
import AdminListToolbar from '@/components/home/AdminListToolbar.vue'
import BaseOverview from '@/components/home/BaseOverview.vue'
import ToolbarSearchPill from '@/components/home/ToolbarSearchPill.vue'
import ExtensionInstalledCard from './ExtensionInstalledCard.vue'
import ExtensionModelSyncDialog from '../shared/ExtensionModelSyncDialog.vue'
import type { ExtensionRow } from '../shared/extensionDisplay'
import {
  systemExtensionApi,
  type ExtensionModelLoadPreview,
  type ExtensionModelUnloadPreview,
  type PageResponse,
  type SystemExtension,
  type SystemExtensionQueryPagePayload
} from '@/api/systemExtension'

const typeTabKey = ref('ALL')
const extensionNameInput = ref('')
const list = ref<ExtensionRow[]>([])

const listSummaryText = computed(
  () => `当前页 ${list.value.length} 条扩展记录，已选 ${selectedRowKeys.value.length} 条。`
)

function rowKey(record: ExtensionRow) {
  return record.id != null ? String(record.id) : String(record.extensionKey ?? '')
}

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const selectedRowKeys = ref<string[]>([])

const currentPageIds = computed(() =>
  list.value
    .map((item) => item.id)
    .filter((id): id is number | string => id !== undefined && id !== null)
    .map((id) => String(id))
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
  const payload: SystemExtensionQueryPagePayload = {
    pageNo: page.pageNum,
    pageSize: page.pageSize,
    param: {
      extensionName: extensionNameInput.value?.trim() || undefined,
      listScope: 'INSTALLED',
      type: typeTabKey.value === 'ALL' ? undefined : typeTabKey.value
    }
  }

  const resp: PageResponse<SystemExtension> = await systemExtensionApi.queryPage(payload)
  list.value = (resp.list || []) as ExtensionRow[]
  page.total = resp.total || 0
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
  const ids: Array<number | string> = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await systemExtensionApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleApply = async (id: number | string | undefined) => {
  if (id == null) return
  try {
    const msg = await systemExtensionApi.apply(id)
    message.success(msg)
    selectedRowKeys.value = []
    void fetchList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '应用失败')
  }
}

const handleRevokeApply = async (id: number | string | undefined) => {
  if (id == null) return
  try {
    const msg = await systemExtensionApi.revokeApply(id)
    message.success(msg)
    void fetchList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '取消应用失败')
  }
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

void fetchList()
</script>

<style scoped>
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
  .extension-grid {
    grid-template-columns: 1fr;
  }

  .pagination-wrap {
    justify-content: center;
  }
}
</style>
