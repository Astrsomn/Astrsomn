<template>
  <div class="extension-center">
    <div class="toolbar">
      <div class="toolbar-left">
        <AstrsomnSearchPill
          v-model="activeQuery.keyword"
          placeholder="搜索插件名称 / Provider / 能力"
          layout="toolbar"
          @search="onSearch"
        />
   
      </div>
      <div class="toolbar-right">
        <a-upload
          v-if="isInstalledTab"
          :show-upload-list="false"
          accept=".jar,application/java-archive"
          :before-upload="onBeforeUploadJar"
        >
          <a-button class="ghost-btn" :loading="jarUploading">
            <template #icon><upload-outlined /></template>
            导入插件
          </a-button>
        </a-upload>
        <a-popconfirm
          v-if="isInstalledTab && selectedRowKeys.length > 0"
          title="确定批量删除选中的扩展吗？"
          ok-text="确认"
          cancel-text="取消"
          @confirm="handleBatchDelete"
        >
          <a-button danger class="ghost-btn">
            <template #icon><delete-outlined /></template>
            批量删除
          </a-button>
        </a-popconfirm>
      </div>
    </div>


    <div class="type-tab-row">
      <a-tabs :active-key="activeQuery.type" class="type-tabs" @change="onTypeChange">
        <a-tab-pane key="ALL" tab="全部" />
        <a-tab-pane key="MODEL_PROVIDER" tab="模型" />
        <a-tab-pane key="VECTOR_STORE" tab="向量库" />
        <a-tab-pane key="MCP" tab="MCP" />
      </a-tabs>
      <a-pagination
        :current="activeQuery.pageNo"
        :page-size="activeQuery.pageSize"
        :total="activeTotal"
        :show-size-changer="false"
        @change="onPageChange"
      />
    </div>

    <div v-if="activeList.length > 0" class="card-list">
      <article v-for="item in activeList" :key="rowKey(item)" class="plugin-card">
        <div class="plugin-main">
          <div class="plugin-icon">
            <img v-if="item.avatar?.trim()" class="avatar-img" :src="item.avatar" :alt="item.extensionName" />
            <component :is="getAntdIcon(item.type)" v-else />
          </div>
          <div class="plugin-info">
            <div class="status-row" v-if="isInstalledTab">
              <span class="status-dot" :class="item.applied === 'Y' ? 'enabled' : 'disabled'" />
              <span class="status-text" :class="item.applied === 'Y' ? 'enabled' : 'disabled'">
                {{ item.applied === 'Y' ? '已启用' : '未启用' }}
              </span>
            </div>
            <h3 class="plugin-title">{{ item.extensionName || '未命名扩展' }}</h3>
            <div class="plugin-meta">By {{ item.author || 'Astrsomn' }} · {{ item.version || 'v1.0.0' }}</div>
            <div class="plugin-tags">
              <span class="tag">{{ extensionTypeLabel(item.type) }}</span>
              <span class="tag">{{ item.extensionCode || 'Provider' }}</span>
              <span class="tag soft">{{ item.jarName || 'classpath dependency' }}</span>
            </div>
          </div>
          <div class="plugin-desc">
            <p>{{ preview(item.description) }}</p>
          </div>
        </div>

        <div class="action-row">
          <template v-if="isInstalledTab">
            <a-button
              v-if="item.applied === 'Y' && item.type === 'MODEL_PROVIDER'"
              type="default"
              class="action-btn"
              @click="openLoadModelsPreview(item)"
            >
              <template #icon><cloud-download-outlined /></template>
              加载模型
            </a-button>
            <a-button
              v-if="item.applied === 'Y' && item.type === 'MODEL_PROVIDER'"
              danger
              class="action-btn"
              @click="openUnloadModelsPreview(item)"
            >
              <template #icon><rest-outlined /></template>
              卸载模型
            </a-button>
            <a-popconfirm
              v-if="item.applied === 'N'"
              title="确定应用该插件吗？"
              ok-text="确定"
              cancel-text="取消"
              @confirm="handleApply(item.id)"
            >
              <a-button type="primary" class="action-btn">
                <template #icon><caret-right-outlined /></template>
                启用插件
              </a-button>
            </a-popconfirm>
            <a-popconfirm
              v-else
              title="确定取消启用吗？插件将恢复为未启用状态。"
              ok-text="确定"
              cancel-text="取消"
              @confirm="handleRevokeApply(item.id)"
            >
              <a-button class="action-btn">
                <template #icon><pause-outlined /></template>
                禁用插件
              </a-button>
            </a-popconfirm>
            <a-popconfirm
              v-if="isUninstallableExtension(item)"
              title="确定卸载该插件吗？"
              ok-text="确定"
              cancel-text="取消"
              @confirm="handleUninstall(item.id)"
            >
              <a-button danger class="action-btn icon-btn">
                <template #icon><delete-outlined /></template>
              </a-button>
            </a-popconfirm>
            <a-checkbox :checked="isRowSelected(item)" @change="(e) => onCardToggleSelect(item, e.target.checked)">
              选择
            </a-checkbox>
          </template>
          <template v-else>
            <a-button type="primary" class="action-btn" @click="installFromCatalog(item)">
              <template #icon><download-outlined /></template>
              安装到环境
            </a-button>
          </template>
        </div>
      </article>
    </div>
    <div v-else class="extension-empty">
      <a-empty :description="isInstalledTab ? '暂无已安装扩展' : '暂无市场插件'" />
    </div>

    <ExtensionModelLoadDialog
      :open="loadSyncModal.open"
      :extension-label="loadSyncModal.extensionLabel"
      :loading-preview="loadSyncModal.loadingPreview"
      :preview-error="loadSyncModal.previewError"
      :load-preview="loadSyncModal.loadPreview"
      :confirm="confirmLoadModels"
      @update:open="(v) => (loadSyncModal.open = v)"
      @cancel="resetLoadSyncModal"
    />
    <ExtensionModelUnloadDialog
      :open="unloadSyncModal.open"
      :extension-label="unloadSyncModal.extensionLabel"
      :loading-preview="unloadSyncModal.loadingPreview"
      :preview-error="unloadSyncModal.previewError"
      :unload-preview="unloadSyncModal.unloadPreview"
      :confirm="confirmUnloadModels"
      @update:open="(v) => (unloadSyncModal.open = v)"
      @cancel="resetUnloadSyncModal"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import {
  AppstoreOutlined,
  BuildOutlined,
  CaretRightOutlined,
  CloudDownloadOutlined,
  DeleteOutlined,
  DownloadOutlined,
  PauseOutlined,
  RestOutlined,
  RocketOutlined,
  UploadOutlined
} from '@ant-design/icons-vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import {
  systemExtensionApi,
  type ExtensionModelLoadPreview,
  type ExtensionModelUnloadPreview,
  type PageResponse,
  type SystemExtension,
  type SystemExtensionQueryPagePayload
} from '@/api/systemExtension'
import {
  extensionTypeLabel,
  isUninstallableExtension,
  preview,
  type ExtensionRow
} from './shared/extensionDisplay'
import ExtensionModelLoadDialog from './shared/ExtensionModelLoadDialog.vue'
import ExtensionModelUnloadDialog from './shared/ExtensionModelUnloadDialog.vue'

type ExtensionPanel = 'marketplace' | 'installed'
type QueryState = { keyword: string; type: string; pageNo: number; pageSize: number }

const props = defineProps<{ activeTab: ExtensionPanel }>()
const emit = defineEmits<{ 'update:activeTab': [tab: ExtensionPanel] }>()

const activeTabLocal = ref<ExtensionPanel>(props.activeTab || 'installed')
watch(
  () => props.activeTab,
  (v) => {
    if (v && v !== activeTabLocal.value) activeTabLocal.value = v
  }
)

const installedQuery = reactive<QueryState>({ keyword: '', type: 'ALL', pageNo: 1, pageSize: 10 })
const marketplaceQuery = reactive<QueryState>({ keyword: '', type: 'ALL', pageNo: 1, pageSize: 8 })
const installedList = ref<ExtensionRow[]>([])
const marketplaceList = ref<ExtensionRow[]>([])
const installedTotal = ref(0)
const marketplaceTotal = ref(0)
const selectedRowKeys = ref<string[]>([])
const jarUploading = ref(false)

const typeFilterOptions = [
  { label: '全部类型', value: 'ALL' },
  { label: '模型', value: 'MODEL_PROVIDER' },
  { label: '向量库', value: 'VECTOR_STORE' },
  { label: 'MCP', value: 'MCP' }
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

function getAntdIcon(type?: string) {
  if (type === 'MODEL_PROVIDER') return RocketOutlined
  if (type === 'VECTOR_STORE') return BuildOutlined
  return AppstoreOutlined
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
  const resp = await systemExtensionApi.marketplaceCatalog(typeQ, marketplaceQuery.pageNo, marketplaceQuery.pageSize)
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
    loadSyncModal.loadPreview = data ?? { toCreate: [], skippedExisting: [], skippedInvalidCount: 0 }
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
    unloadSyncModal.unloadPreview = data ?? { toRemove: [], keptReferenced: [] }
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
  border-bottom: 1px solid #f1f5f9;
}

.type-tabs {
  flex: 1;
}

.type-tabs :deep(.ant-tabs-nav) {
  margin-bottom: 0;
}

.type-tabs :deep(.ant-tabs-tab) {
  color: #64748b;
}

.card-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.plugin-card {
  border: 1px solid #f1f5f9;
  border-radius: 14px;
  padding: 18px;
  transition: border-color 0.2s;
  background: #fff;
}

.plugin-card:hover {
  border-color: #dbeafe;
}

.plugin-main {
  display: grid;
  grid-template-columns: 64px 1.2fr 1fr;
  gap: 16px;
  align-items: start;
}

.plugin-icon {
  width: 64px;
  height: 64px;
  border-radius: 10px;
  background: #eff6ff;
  color: #3b82f6;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.avatar-svg,
.avatar-img {
  width: 48px;
  height: 48px;
}

.avatar-img {
  border-radius: 8px;
}

.status-row {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 999px;
}

.status-dot.enabled {
  background: #3b82f6;
}

.status-dot.disabled {
  background: #cbd5e1;
}

.status-text {
  font-size: 12px;
  font-weight: 700;
}

.status-text.enabled {
  color: #3b82f6;
}

.status-text.disabled {
  color: #94a3b8;
}

.plugin-title {
  margin: 0;
  font-size: 18px;
  color: #1f2937;
}

.plugin-meta {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 2px;
}

.plugin-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 10px;
}

.tag {
  padding: 2px 8px;
  border-radius: 8px;
  font-size: 12px;
  color: #2563eb;
  background: #eff6ff;
}

.tag.soft {
  color: #94a3b8;
  background: #f8fafc;
  border: 1px solid #f1f5f9;
}

.plugin-desc {
  color: #64748b;
  font-size: 14px;
  line-height: 1.6;
}

.plugin-desc p {
  margin: 0;
}

.action-row {
  margin-top: 14px;
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: flex-end;
  flex-wrap: wrap;
}

.action-btn {
  border-radius: 10px;
}

.icon-btn {
  width: 40px;
  padding-inline: 0;
}

.extension-empty {
  margin-top: 40px;
}

@media (max-width: 1200px) {
  .plugin-main {
    grid-template-columns: 64px 1fr;
  }

  .plugin-desc {
    grid-column: 1 / -1;
  }
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
