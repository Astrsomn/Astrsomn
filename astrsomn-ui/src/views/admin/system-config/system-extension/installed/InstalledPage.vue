<template>
  <div class="extension-subpage">
    <AdminListToolbar>
      <template #left>
        <AstrsomnSearchPill
          v-model="extensionNameInput"
          placeholder="搜索扩展名称"
          layout="toolbar"
          @search="fetchList"
        />
      </template>

      <template #right>
        <a-upload
          :show-upload-list="false"
          accept=".jar,application/java-archive"
          :before-upload="onBeforeUploadJar"
        >
          <a-button type="default" class="import-jar-btn" :loading="jarUploading">
            <template #icon><upload-outlined /></template>
            导入插件
          </a-button>
        </a-upload>
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
    <AstrsomnOverview
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

    <ExtensionModelLoadDialog
      v-model:open="loadSyncModal.open"
      :extension-label="loadSyncModal.extensionLabel"
      :loading-preview="loadSyncModal.loadingPreview"
      :preview-error="loadSyncModal.previewError"
      :load-preview="loadSyncModal.loadPreview"
      :confirm="confirmLoadModels"
      @cancel="resetLoadSyncModal"
    />
    <ExtensionModelUnloadDialog
      v-model:open="unloadSyncModal.open"
      :extension-label="unloadSyncModal.extensionLabel"
      :loading-preview="unloadSyncModal.loadingPreview"
      :preview-error="unloadSyncModal.previewError"
      :unload-preview="unloadSyncModal.unloadPreview"
      :confirm="confirmUnloadModels"
      @cancel="resetUnloadSyncModal"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { DeleteOutlined, UploadOutlined } from '@ant-design/icons-vue'
import AdminListToolbar from '@/components/home/AdminListToolbar.vue'
import AstrsomnOverview from '@/components/home/AstrsomnOverview.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import ExtensionInstalledCard from './ExtensionInstalledCard.vue'
import ExtensionModelLoadDialog from '../shared/ExtensionModelLoadDialog.vue'
import ExtensionModelUnloadDialog from '../shared/ExtensionModelUnloadDialog.vue'
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
const jarUploading = ref(false)

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

const loadSyncModal = reactive({
  open: false,
  extensionId: null as number | string | null,
  extensionLabel: '',
  loadingPreview: false,
  previewError: '',
  loadPreview: null as ExtensionModelLoadPreview | null
})

const unloadSyncModal = reactive({
  open: false,
  extensionId: null as number | string | null,
  extensionLabel: '',
  loadingPreview: false,
  previewError: '',
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

function resetLoadSyncModal() {
  loadSyncModal.open = false
  loadSyncModal.extensionId = null
  loadSyncModal.extensionLabel = ''
  loadSyncModal.previewError = ''
  loadSyncModal.loadPreview = null
  loadSyncModal.loadingPreview = false
}

function resetUnloadSyncModal() {
  unloadSyncModal.open = false
  unloadSyncModal.extensionId = null
  unloadSyncModal.extensionLabel = ''
  unloadSyncModal.previewError = ''
  unloadSyncModal.unloadPreview = null
  unloadSyncModal.loadingPreview = false
}

async function openLoadModelsPreview(record: ExtensionRow) {
  const id = record.id
  if (id == null) return
  loadSyncModal.open = true
  loadSyncModal.extensionId = id
  loadSyncModal.extensionLabel = String(record.extensionName || record.extensionKey || id)
  loadSyncModal.previewError = ''
  loadSyncModal.loadPreview = null
  loadSyncModal.loadingPreview = true
  try {
    const data = await systemExtensionApi.previewLoadModels(id)
    loadSyncModal.loadPreview = data ?? {
      toCreate: [],
      skippedExisting: [],
      skippedInvalidCount: 0
    }
  } catch (e: unknown) {
    const err = e as { message?: string }
    loadSyncModal.previewError = err?.message || '加载预览失败'
    loadSyncModal.loadPreview = null
  } finally {
    loadSyncModal.loadingPreview = false
  }
}

async function openUnloadModelsPreview(record: ExtensionRow) {
  const id = record.id
  if (id == null) return
  unloadSyncModal.open = true
  unloadSyncModal.extensionId = id
  unloadSyncModal.extensionLabel = String(record.extensionName || record.extensionKey || id)
  unloadSyncModal.previewError = ''
  unloadSyncModal.unloadPreview = null
  unloadSyncModal.loadingPreview = true
  try {
    const data = await systemExtensionApi.previewUnloadModels(id)
    unloadSyncModal.unloadPreview = data ?? { toRemove: [], keptReferenced: [] }
  } catch (e: unknown) {
    const err = e as { message?: string }
    unloadSyncModal.previewError = err?.message || '卸载预览失败'
    unloadSyncModal.unloadPreview = null
  } finally {
    unloadSyncModal.loadingPreview = false
  }
}

async function confirmLoadModels(selectedModelKeys: string[]) {
  const id = loadSyncModal.extensionId
  if (id == null || loadSyncModal.previewError || selectedModelKeys.length === 0) return
  try {
    const msg = await systemExtensionApi.loadModels(id, selectedModelKeys)
    message.success(msg)
    resetLoadSyncModal()
    void fetchList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '加载模型失败')
    throw e
  }
}

async function confirmUnloadModels(selectedModelKeys: string[]) {
  const id = unloadSyncModal.extensionId
  if (id == null || unloadSyncModal.previewError || selectedModelKeys.length === 0) return
  try {
    const msg = await systemExtensionApi.unloadModels(id, selectedModelKeys)
    message.success(msg)
    resetUnloadSyncModal()
    void fetchList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '卸载模型失败')
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

const onBeforeUploadJar = async (file: File) => {
  jarUploading.value = true
  try {
    const msg = await systemExtensionApi.uploadJar(file)
    message.success(msg)
    message.info('可在「已安装插件」中查看并应用。')
    void fetchList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '上传失败')
  } finally {
    jarUploading.value = false
  }
  return false
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

.import-jar-btn {
  height: 40px;
  border-radius: var(--radius-lg);
  margin-right: 12px;
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
