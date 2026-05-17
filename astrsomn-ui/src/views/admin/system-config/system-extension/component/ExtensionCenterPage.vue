<template>
  <div class="extension-center">
    <div class="toolbar">
      <div class="toolbar-left">
        <AstSearchInput
            v-model="activeQuery.keyword"
            layout="toolbar"
            placeholder="搜索插件名称 / Provider / 能力"
            @search="onSearch"
        />

      </div>
      <div class="toolbar-right">
        <a-upload
            v-if="isInstalledTab"
            :before-upload="onBeforeUploadJar"
            :show-upload-list="false"
            accept=".jar,application/java-archive"
        >
          <a-button :loading="jarUploading" class="ghost-btn">
            <template #icon>
              <upload-outlined/>
            </template>
            导入插件
          </a-button>
        </a-upload>
        <a-popconfirm
            v-if="isInstalledTab && selectedRowKeys.length > 0"
            cancel-text="取消"
            ok-text="确认"
            title="确定批量删除选中的扩展吗？"
            @confirm="handleBatchDelete"
        >
          <a-button class="ghost-btn" danger>
            <template #icon>
              <delete-outlined/>
            </template>
            批量删除
          </a-button>
        </a-popconfirm>
      </div>
    </div>


    <div class="type-tab-row">
      <a-tabs :active-key="activeQuery.type" class="type-tabs" @change="onTypeChange">
        <a-tab-pane key="ALL" tab="全部"/>
        <a-tab-pane key="MODEL_PROVIDER" tab="模型"/>
        <a-tab-pane key="VECTOR_STORE" tab="向量库"/>
        <a-tab-pane key="MCP" tab="MCP"/>
      </a-tabs>
      <a-pagination
          :current="activeQuery.pageNo"
          :page-size="activeQuery.pageSize"
          :show-size-changer="false"
          :total="activeTotal"
          @change="onPageChange"
      />
    </div>

    <div v-if="activeList.length > 0" class="card-list">
      <transition-group name="card-list">
        <ExtensionCard
            v-for="item in activeList"
            :key="rowKey(item)"
            :is-selected="isRowSelected(item)"
            :item="item"
            :show-actions="isInstalledTab"
            @apply="handleApply"
            @install="installFromCatalog"
            @uninstall="handleUninstall"
            @load-models="openLoadModelsPreview"
            @unload-models="openUnloadModelsPreview"
            @revoke-apply="handleRevokeApply"
            @toggle-select="onCardToggleSelect"
        />
      </transition-group>
    </div>
    <div v-else class="extension-empty">
      <a-empty :description="isInstalledTab ? '暂无已安装扩展' : '暂无市场插件'"/>
    </div>

    <ExtensionModelLoadDialog
        :confirm="confirmLoadModels"
        :extension-label="loadSyncModal.extensionLabel"
        :load-preview="loadSyncModal.loadPreview"
        :loading-preview="loadSyncModal.loadingPreview"
        :open="loadSyncModal.open"
        :preview-error="loadSyncModal.previewError"
        @cancel="resetLoadSyncModal"
        @update:open="(v) => (loadSyncModal.open = v)"
    />
    <ExtensionModelUnloadDialog
        :confirm="confirmUnloadModels"
        :extension-label="unloadSyncModal.extensionLabel"
        :loading-preview="unloadSyncModal.loadingPreview"
        :open="unloadSyncModal.open"
        :preview-error="unloadSyncModal.previewError"
        :unload-preview="unloadSyncModal.unloadPreview"
        @cancel="resetUnloadSyncModal"
        @update:open="(v) => (unloadSyncModal.open = v)"
    />
  </div>
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import {message} from 'ant-design-vue'
import {DeleteOutlined, UploadOutlined} from '@ant-design/icons-vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import {
  type ExtensionModelLoadPreview,
  type ExtensionModelUnloadPreview,
  type PageResponse,
  type SystemExtension,
  systemExtensionApi,
  type SystemExtensionQueryPagePayload
} from '@/api/systemExtension.ts'
import {extensionMarketplaceApi} from '@/api/extensionMarketplace.ts'
import {type ExtensionRow} from '@/views/admin/system-config/system-extension/utils/extensionDisplay.ts'
import ExtensionModelLoadDialog
  from '@/views/admin/system-config/system-extension/component/model-dialog/ExtensionModelLoadDialog.vue'
import ExtensionModelUnloadDialog
  from '@/views/admin/system-config/system-extension/component/model-dialog/ExtensionModelUnloadDialog.vue'
import ExtensionCard from './ExtensionCard.vue'

type ExtensionPanel = 'marketplace' | 'installed'
type QueryState = { keyword: string; type: string; pageNo: number; pageSize: number }

const props = defineProps<{ activeTab: ExtensionPanel }>()
const emit = defineEmits<{ 'update:activeTab': [tab: ExtensionPanel] }>()

const activeTabLocal = ref<ExtensionPanel>(props.activeTab || 'installed')
watch(
    () => props.activeTab,
    (v) => {
      if (v && v !== activeTabLocal.value) {
        activeTabLocal.value = v
        void fetchActiveList()
      }
    }
)

const installedQuery = reactive<QueryState>({keyword: '', type: 'ALL', pageNo: 1, pageSize: 10})
const marketplaceQuery = reactive<QueryState>({keyword: '', type: 'ALL', pageNo: 1, pageSize: 8})
const installedList = ref<ExtensionRow[]>([])
const marketplaceList = ref<ExtensionRow[]>([])
const installedTotal = ref(0)
const marketplaceTotal = ref(0)
const selectedRowKeys = ref<string[]>([])
const jarUploading = ref(false)

const typeFilterOptions = [
  {label: '全部类型', value: 'ALL'},
  {label: '模型', value: 'MODEL_PROVIDER'},
  {label: '向量库', value: 'VECTOR_STORE'},
  {label: 'MCP', value: 'MCP'}
]

const isInstalledTab = computed(() => activeTabLocal.value === 'installed')
const activeQuery = computed(() => (isInstalledTab.value ? installedQuery : marketplaceQuery))
const activeList = computed(() => (isInstalledTab.value ? installedList.value : marketplaceList.value))
const activeTotal = computed(() => (isInstalledTab.value ? installedTotal.value : marketplaceTotal.value))

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

function rowKey(record: ExtensionRow) {
  const base = record.id != null ? String(record.id) : String(record.extensionKey ?? '')
  return `${record.type || 'UNKNOWN'}-${base}`
}

function onSearch() {
  activeQuery.value.pageNo = 1
  void fetchActiveList()
}

function onTypeChange(type: string) {
  activeQuery.value.type = type
  activeQuery.value.pageNo = 1
  if (isInstalledTab.value) selectedRowKeys.value = []
  void fetchActiveList()
}

function onPageChange(page: number, pageSize: number) {
  activeQuery.value.pageNo = page
  activeQuery.value.pageSize = pageSize
  void fetchActiveList()
}

function onPanelTabChange(tab: string) {
  if (tab !== 'installed' && tab !== 'marketplace') return
  activeTabLocal.value = tab
  emit('update:activeTab', tab)
  selectedRowKeys.value = []
  void fetchActiveList()
}

function isRowSelected(record: ExtensionRow) {
  return selectedRowKeys.value.includes(rowKey(record))
}

function onCardToggleSelect(record: ExtensionRow, checked: boolean) {
  const key = rowKey(record)
  if (checked) {
    selectedRowKeys.value = Array.from(new Set([...selectedRowKeys.value, key]))
    return
  }
  selectedRowKeys.value = selectedRowKeys.value.filter((x) => x !== key)
}

async function fetchInstalledList() {
  const payload: SystemExtensionQueryPagePayload = {
    pageNo: installedQuery.pageNo,
    pageSize: installedQuery.pageSize,
    param: {
      extensionName: installedQuery.keyword?.trim() || undefined,
      listScope: 'INSTALLED',
      type: installedQuery.type === 'ALL' ? undefined : installedQuery.type
    }
  }
  const resp: PageResponse<SystemExtension> = await systemExtensionApi.queryPage(payload)
  installedList.value = (resp.list || []) as ExtensionRow[]
  installedTotal.value = resp.total || 0
}

async function fetchMarketplaceList() {
  const typeQ = marketplaceQuery.type === 'ALL' ? undefined : marketplaceQuery.type
  const resp = await extensionMarketplaceApi.catalog(typeQ, marketplaceQuery.pageNo, marketplaceQuery.pageSize)
  let rows = resp.list || []
  const keyword = marketplaceQuery.keyword?.trim().toLowerCase()
  if (keyword) {
    rows = rows.filter((r) => (r.extensionName || '').toLowerCase().includes(keyword))
  }
  marketplaceList.value = rows as ExtensionRow[]
  marketplaceTotal.value = resp.total || 0
}

async function fetchActiveList() {
  if (isInstalledTab.value) {
    await fetchInstalledList()
    return
  }
  await fetchMarketplaceList()
}

async function installFromCatalog(item: ExtensionRow) {
  const payload: SystemExtension = {
    extensionKey: item.extensionKey,
    extensionName: item.extensionName,
    type: item.type,
    version: item.version,
    author: item.author,
    description: item.description,
    jarName: item.jarName,
    extensionCode: item.extensionCode,
    avatar: item.avatar,
    applied: 'N',
    status: 'INSTALLED'
  }
  const msg = await systemExtensionApi.create(payload)
  message.success(msg)
  message.info('可在「已安装插件」中查看、应用插件或加载模型。')
}

async function handleBatchDelete() {
  const ids = installedList.value
      .filter((x) => selectedRowKeys.value.includes(rowKey(x)))
      .map((x) => x.id)
      .filter((x): x is string | number => x != null)
  if (ids.length === 0) return
  const msg = await systemExtensionApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = []
  await fetchInstalledList()
}

async function handleApply(id: number | string | undefined) {
  if (id == null) return
  try {
    const msg = await systemExtensionApi.apply(id)
    message.success(msg)
    await fetchInstalledList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '应用失败')
  }
}

async function handleRevokeApply(id: number | string | undefined) {
  if (id == null) return
  try {
    const msg = await systemExtensionApi.revokeApply(id)
    message.success(msg)
    await fetchInstalledList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '取消应用失败')
  }
}

async function handleUninstall(id: number | string | undefined) {
  if (id == null) return
  try {
    const msg = await systemExtensionApi.uninstall(id)
    message.success(msg)
    selectedRowKeys.value = []
    await fetchInstalledList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '卸载失败')
  }
}

async function onBeforeUploadJar(file: File) {
  jarUploading.value = true
  try {
    const msg = await systemExtensionApi.uploadJar(file)
    message.success(msg)
    message.info('可在「已安装插件」中查看并应用。')
    await fetchInstalledList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '上传失败')
  } finally {
    jarUploading.value = false
  }
  return false
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
    loadSyncModal.loadPreview = data ?? {toCreate: [], skippedExisting: [], skippedInvalidCount: 0}
  } catch (e: unknown) {
    const err = e as { message?: string }
    loadSyncModal.previewError = err?.message || '加载预览失败'
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
    unloadSyncModal.unloadPreview = data ?? {toRemove: [], keptReferenced: []}
  } catch (e: unknown) {
    const err = e as { message?: string }
    unloadSyncModal.previewError = err?.message || '卸载预览失败'
  } finally {
    unloadSyncModal.loadingPreview = false
  }
}

async function confirmLoadModels(selectedModelKeys: string[]) {
  const id = loadSyncModal.extensionId
  if (id == null || selectedModelKeys.length === 0 || loadSyncModal.previewError) return
  try {
    const msg = await systemExtensionApi.loadModels(id, selectedModelKeys)
    message.success(msg)
    resetLoadSyncModal()
    await fetchInstalledList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '加载模型失败')
    throw e
  }
}

async function confirmUnloadModels(selectedModelKeys: string[]) {
  const id = unloadSyncModal.extensionId
  if (id == null || selectedModelKeys.length === 0 || unloadSyncModal.previewError) return
  try {
    const msg = await systemExtensionApi.unloadModels(id, selectedModelKeys)
    message.success(msg)
    resetUnloadSyncModal()
    await fetchInstalledList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '卸载模型失败')
    throw e
  }
}

void fetchActiveList()
</script>

<style scoped>
.extension-center {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.toolbar-left {
  display: flex;
  gap: 10px;
  flex: 1;
  min-width: 360px;
}

.type-filter {
  width: 132px;
}

.toolbar-right {
  display: flex;
  gap: 10px;
}

.ghost-btn {
  height: 40px;
  border-radius: 10px;
}

.panel-tabs :deep(.ant-tabs-nav) {
  margin-bottom: 0;
}

.type-tab-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--border-default);
}

.type-tabs {
  flex: 1;
}

.type-tabs :deep(.ant-tabs-nav) {
  margin-bottom: 0;
}

.type-tabs :deep(.ant-tabs-tab) {
  color: var(--text-secondary);
}

.card-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 200px;
}

.card-list-enter-active,
.card-list-leave-active {
  transition: all 0.4s ease;
}

.card-list-enter-from {
  opacity: 0;
  transform: translateY(-20px);
}

.card-list-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

.card-list-move {
  transition: transform 0.4s ease;
}

.extension-empty {
  margin-top: 40px;
}

@media (max-width: 768px) {
  .toolbar-left {
    min-width: 100%;
    flex-wrap: wrap;
  }

  .type-filter {
    width: 100%;
  }

  .type-tab-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>
