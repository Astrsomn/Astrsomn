<template>
  <SidebarShell :width="288">
    <template #top>
      <AstSearchInput
          v-model="searchText"
          class="sidebar-search-pill"
          layout="fluid"
          :placeholder="t.vectorCenter.sidebar.searchPlaceholder"
          @search="handleSearch"
      />
      <div class="top-add-btn" :title="t.vectorCenter.sidebar.addSource" @click="handleAddSource">
        <PlusOutlined />
      </div>
    </template>

    <div class="source-tree" @contextmenu="onSidebarBlankContextMenu">
      <div
          v-for="source in sourceTree"
          :key="source.id"
          :data-source-id="source.id"
          class="source-section"
      >
        <a-dropdown :trigger="['contextmenu']">
          <SourceCard
              :ip="source.ip"
              :is-checking="checkingSourceMap[String(source.id)] === true"
              :is-connected="source.connected"
              :is-open="openKeys.includes(source.id)"
              :port="source.port"
              :provider-avatar="source.providerAvatar"
              :source-name="source.name"
              :source-type="source.type"
              @toggle="selectSource(source.id)"
          />
          <template #overlay>
            <SourceContextMenu
                @add-db="handleSourceMenuClick('addDb', source)"
                @edit="openEditSource(source.id)"
                @delete="deleteSource(source.id)"
            />
          </template>
        </a-dropdown>

        <transition name="expand">
          <div v-if="openKeys.includes(source.id)" class="db-container" @contextmenu.prevent="handleDbContainerContextMenu($event, source)">
            <a-dropdown
                v-for="db in source.dbs"
                :key="db.id"
                :trigger="['contextmenu']"
            >
              <DbNode
                  :db-name="db.dbName"
                  :is-selected="String(selectedStoreId) === String(db.id)"
                  @select="selectDb(source.id, db.id)"
              />
              <template #overlay>
                <DbContextMenu
                    @edit="openEditStore(db.id)"
                    @delete="deleteStore(db.id)"
                />
              </template>
            </a-dropdown>
            <div v-if="!source.dbs.length" class="db-empty-hint" @contextmenu.prevent="handleDbContainerContextMenu($event, source)">{{ t.vectorCenter.sidebar.dbEmptyHint }}</div>
          </div>
        </transition>
      </div>
    </div>

    <template #footer>
      <SidebarFooter
          :enabled-extensions="enabledExtensions"
          @open-marketplace="goPluginMarketplace"
      />
    </template>
  </SidebarShell>

  <Teleport to="body">
    <div
        v-if="blankContextMenuVisible"
        class="blank-context-menu-overlay"
        @click="closeBlankContextMenu"
        @contextmenu.prevent="closeBlankContextMenu"
    >
      <div
          :style="{ left: blankContextMenuX + 'px', top: blankContextMenuY + 'px' }"
          class="blank-context-menu"
          @click.stop
      >
        <div class="blank-context-menu-item" @click="onBlankMenuAction('addSource')">
          <PlusOutlined/>
          <span>{{ t.vectorCenter.sidebar.newSource }}</span>
        </div>
        <div class="blank-context-menu-divider"/>
        <div class="blank-context-menu-item" @click="onBlankMenuAction('refresh')">
          <ReloadOutlined/>
          <span>{{ t.vectorCenter.sidebar.refresh }}</span>
        </div>
      </div>
    </div>
  </Teleport>
  <Teleport to="body">
    <div
        v-if="dbContainerMenuVisible"
        class="blank-context-menu-overlay"
        @click="closeDbContainerMenu"
        @contextmenu.prevent="closeDbContainerMenu"
    >
      <div
          :style="{ left: dbContainerMenuX + 'px', top: dbContainerMenuY + 'px' }"
          class="blank-context-menu"
          @click.stop
      >
        <div class="blank-context-menu-item" @click="onDbContainerMenuAction('addDb')">
          <PlusOutlined/>
          <span>{{ t.vectorCenter.sidebar.newDatabase }}</span>
        </div>
        <div class="blank-context-menu-item" @click="onDbContainerMenuAction('edit')">
          <EditOutlined/>
          <span>{{ t.vectorCenter.sidebar.editSource }}</span>
        </div>
        <div class="blank-context-menu-divider"/>
        <div class="blank-context-menu-item danger-item" @click="onDbContainerMenuAction('delete')">
          <DeleteOutlined/>
          <span>{{ t.vectorCenter.sidebar.deleteSource }}</span>
        </div>
      </div>
    </div>
  </Teleport>
  <VecSourceFormModal
      :confirm-loading="sourceModalSubmitting"
      :initial="sourceModalInitial"
      :mode="sourceModalMode"
      :open="sourceModalOpen"
      @submit="handleSourceSubmit"
      @update:open="onSourceModalOpenChange"
  />
  <VecStoreFormModal
      :confirm-loading="storeModalSubmitting"
      :default-source-id="storeModalSourceId"
      :initial="storeModalInitial"
      :mode="storeModalMode"
      :open="storeModalOpen"
      @submit="handleStoreSubmit"
      @update:open="onStoreModalOpenChange"
  />
  <ExtensionMarketplaceDialog
      :open="marketplaceOpen"
      @cancel="marketplaceOpen = false"
      @update:open="marketplaceOpen = $event"
  />
  <!-- 删除数据源确认对话框 -->
  <a-modal
      v-model:open="deleteConfirmModalVisible"
      :title="t.vectorCenter.sidebar.deleteSourceConfirm"
      :footer="null"
      width="520px"
  >
    <div class="delete-confirm-content">
      <div class="delete-confirm-warning">
        <WarningOutlined class="warning-icon" />
        <span>{{ t.vectorCenter.sidebar.deleteSourceWarning }}</span>
      </div>
      
      <div v-if="deleteConfirmSource" class="delete-confirm-detail">
        <div class="detail-item">
          <span class="detail-label">{{ t.vectorCenter.sidebar.sourceName }}:</span>
          <span class="detail-value">{{ deleteConfirmSource.name }}</span>
        </div>
        <div v-if="deleteConfirmSource.dbs.length > 0" class="detail-item">
          <span class="detail-label">{{ t.vectorCenter.sidebar.associatedDatabases }}:</span>
          <span class="detail-value">{{ deleteConfirmSource.dbs.length }} {{ t.vectorCenter.sidebar.items }}</span>
        </div>
      </div>
      
      <div class="delete-confirm-input-group">
        <label class="delete-confirm-label">{{ t.vectorCenter.sidebar.enterSourceName }}</label>
        <a-input
            v-model:value="deleteConfirmInput"
            :placeholder="t.vectorCenter.sidebar.enterSourceNamePlaceholder"
            class="delete-confirm-input"
        />
      </div>
      
      <div class="delete-confirm-actions">
        <a-button @click="closeDeleteConfirmModal">{{ t.vectorCenter.sidebar.cancel }}</a-button>
        <a-button
            type="primary"
            danger
            @click="confirmDeleteSource"
        >
          {{ t.vectorCenter.sidebar.confirmDelete }}
        </a-button>
      </div>
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
import {computed, onMounted, ref, watch} from 'vue'
import {message} from 'ant-design-vue'
import {DeleteOutlined, EditOutlined, PlusOutlined, ReloadOutlined, WarningOutlined} from '@ant-design/icons-vue'
import {usePageTranslation} from '@/locales/pages.ts'
import SidebarShell from '@/components/sidebar/SidebarShell.vue'
import SidebarFooter from '@/components/sidebar/SidebarFooter.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import SourceCard from './sidebar/SourceCard.vue'
import DbNode from './sidebar/DbNode.vue'
import SourceContextMenu from './sidebar/SourceContextMenu.vue'
import DbContextMenu from './sidebar/DbContextMenu.vue'
import VecSourceFormModal from '@/views/admin/ai-vector/vector-center/form/VecSourceFormModal.vue'
import VecStoreFormModal from '@/views/admin/ai-vector/vector-center/form/VecStoreFormModal.vue'
import {type AiVecSource, aiVecSourceApi} from '@/api/aiVecSource.ts'
import {type AiVecStore, aiVecStoreApi} from '@/api/aiVecStore.ts'
import {systemExtensionApi} from '@/api/systemExtension.ts'
import ExtensionMarketplaceDialog
  from '@/views/admin/system-config/system-extension/component/ExtensionMarketplaceDialog.vue'

const t = usePageTranslation('ai-vector')

const props = withDefaults(defineProps<{
  sources: AiVecSource[]
  stores: AiVecStore[]
  selectedStoreId?: number | string
}>(), {})

const emit = defineEmits<{
  'select-source': [id: number | string]
  'select-store': [id: number | string]
  changed: []
}>()

const searchText = ref('')
const handleSearch = () => {

}

const openKeys = ref<Array<number | string>>([])

const sourceModalOpen = ref(false)
const sourceModalMode = ref<'create' | 'edit'>('create')
const sourceModalSubmitting = ref(false)
const sourceModalInitial = ref<AiVecSource | null>(null)

const storeModalOpen = ref(false)
const storeModalMode = ref<'create' | 'edit'>('create')
const storeModalSubmitting = ref(false)
const storeModalInitial = ref<AiVecStore | null>(null)
const storeModalSourceId = ref<number | string | null>(null)
const firstExpandCheckedMap = ref<Record<string, boolean>>({})
const sourceConnectedOverride = ref<Record<string, boolean>>({})
const checkingSourceMap = ref<Record<string, boolean>>({})
const enabledExtensions = ref<Array<{ key: string; name: string; extensionCode: string; avatar: string; initial: string }>>([])
const storesBySourceCache = ref<Record<string, AiVecStore[]>>({})
const pendingStoreSyncSourceKey = ref<string>('')
const blankContextMenuVisible = ref(false)
const blankContextMenuX = ref(0)
const blankContextMenuY = ref(0)
const dbContainerMenuVisible = ref(false)
const dbContainerMenuX = ref(0)
const dbContainerMenuY = ref(0)
const dbContainerMenuSource = ref<Source | null>(null)
const marketplaceOpen = ref(false)

type Db = {
  id: number | string
  dbName: string
  type: string
  modelName: string
  dim: number
  active: boolean
}

type Source = {
  id: number | string
  name: string
  type: string
  providerAvatar?: string
  ip: string
  port: string
  user: string
  connected: boolean
  dbs: Db[]
}

const sourceTree = computed<Source[]>(() =>
    (props.sources || []).map((source) => {
      const sourceIdText = String(source.id ?? '')
      const sourceNameText = String(source.name || '').trim()
      const sourceStoresFromProps = (props.stores || []).filter((store) => {
        const storeSourceIdText = String(store.sourceId ?? '')
        const matchedById = sourceIdText && storeSourceIdText && storeSourceIdText === sourceIdText
        if (matchedById) return true
        const storeSourceNameText = String(store.sourceName || '').trim()
        return !!sourceNameText && !!storeSourceNameText && storeSourceNameText === sourceNameText
      })
      const sourceStores = storesBySourceCache.value[sourceIdText] ?? sourceStoresFromProps
      const sourceType = String(source.provider || source.extensionCode || 'unknown').toLowerCase()
      const sourceStatus = String(source.status || '').toLowerCase()
      const sourceKey = String(source.id ?? '')
      // 状态优先：未启用的 source 始终显示断开；已启用但未测试过的默认也为断开，
      // 只有展开后连接测试成功才显示绿色连接点
      const connected = sourceStatus === 'enabled'
          ? (sourceConnectedOverride.value[sourceKey] !== undefined
              ? sourceConnectedOverride.value[sourceKey]
              : false)
          : false
      // 关联 extension 获取 avatar 作为 fallback
      const extAvatar = enabledExtensions.value.find(
          ext => ext.extensionCode.toLowerCase() === sourceType
              || ext.key.toLowerCase() === sourceType
              || ext.name.toLowerCase() === sourceType
      )?.avatar || ''
      return {
        id: source.id as number | string,
        name: source.name || `Source-${source.id}`,
        type: sourceType,
        providerAvatar: source.providerAvatar || extAvatar,
        ip: source.host || '-',
        port: source.port || '-',
        user: source.username || '-',
        connected,
        dbs: sourceStores.map((store) => ({
          id: store.id as number | string,
          dbName: store.collectionName || `Store-${store.id}`,
          type: source.provider || 'unknown',
          modelName: store.modelKey || store.instanceName || store.instanceKey || '-',
          dim: Number(store.dimension || 0),
          active: true
        }))
      }
    })
)
const selectedStoreId = computed(() => props.selectedStoreId)

const toggleSource = (id: number | string) => {
  const index = openKeys.value.indexOf(id)
  if (index > -1) {
    openKeys.value.splice(index, 1)
  } else {
    openKeys.value.push(id)
  }
}

const selectSource = (id: number | string) => {
  const isOpening = !openKeys.value.includes(id)
  pendingStoreSyncSourceKey.value = String(id)
  emit('select-source', id)
  // 始终展开/折叠，不因连接测试失败而阻止展开
  toggleSource(id)
  // 展开时异步测试连接，更新绿色连接状态点
  if (isOpening) {
    testConnectionOnFirstExpand(id)
  }
}

const selectDb = (sourceId: number | string, id: number | string) => {
  pendingStoreSyncSourceKey.value = String(sourceId)
  emit('select-source', sourceId)
  emit('select-store', id)
}

const handleAddSource = () => {
  sourceModalMode.value = 'create'
  sourceModalInitial.value = null
  sourceModalOpen.value = true
}

const onSidebarBlankContextMenu = (e: MouseEvent) => {
  const target = e.target as HTMLElement
  if (
      target.closest('.sidebar-header') ||
      target.closest('.sidebar-footer') ||
      target.closest('.blank-context-menu')
  ) {
    return
  }
  // 在 source-section 空白区域右键 → 展示该 source 的上下文菜单
  const sourceSection = target.closest('.source-section') as HTMLElement | null
  if (sourceSection) {
    const sourceId = sourceSection.getAttribute('data-source-id')
    if (sourceId) {
      const src = sourceTree.value.find(s => String(s.id) === sourceId)
      if (src) {
        handleDbContainerContextMenu(e, src)
        return
      }
    }
    return
  }
  e.preventDefault()
  blankContextMenuX.value = e.clientX
  blankContextMenuY.value = e.clientY
  blankContextMenuVisible.value = true
}

const closeBlankContextMenu = () => {
  blankContextMenuVisible.value = false
}

const handleDbContainerContextMenu = (e: MouseEvent, source: Source) => {
  if ((e.target as HTMLElement).closest('.db-node')) return
  e.preventDefault()
  dbContainerMenuX.value = e.clientX
  dbContainerMenuY.value = e.clientY
  dbContainerMenuSource.value = source
  dbContainerMenuVisible.value = true
}

const closeDbContainerMenu = () => {
  dbContainerMenuVisible.value = false
}

const onDbContainerMenuAction = (action: string) => {
  const source = dbContainerMenuSource.value
  closeDbContainerMenu()
  if (!source) return
  switch (action) {
    case 'addDb':
      handleSourceMenuClick('addDb', source)
      break
    case 'edit':
      openEditSource(source.id)
      break
    case 'delete':
      deleteSource(source.id)
      break
  }
}

const onBlankMenuAction = (action: string) => {
  closeBlankContextMenu()
  if (action === 'addSource') {
    handleAddSource()
  } else if (action === 'refresh') {
    emit('changed')
  }
}

const handleSourceMenuClick = (key: string, source: Source) => {
  switch (key) {
    case 'addDb':
      storeModalMode.value = 'create'
      storeModalInitial.value = {
        sourceId: source.id,
        collectionName: '',
        dimension: 1536,
        distanceMetric: 'cosine'
      }
      storeModalSourceId.value = source.id
      storeModalOpen.value = true
      break
    case 'edit':
      openEditSource(source.id)
      break
    case 'delete':
      void deleteSource(source.id)
      break
  }
}

const openEditSource = async (id: number | string) => {
  const detail = await aiVecSourceApi.detail(id)
  sourceModalMode.value = 'edit'
  sourceModalInitial.value = detail
  sourceModalOpen.value = true
}

const deleteConfirmModalVisible = ref(false)
const deleteConfirmSource = ref<Source | null>(null)
const deleteConfirmInput = ref('')

const openDeleteConfirmModal = (source: Source) => {
  deleteConfirmSource.value = source
  deleteConfirmInput.value = ''
  deleteConfirmModalVisible.value = true
}

const closeDeleteConfirmModal = () => {
  deleteConfirmModalVisible.value = false
  deleteConfirmSource.value = null
  deleteConfirmInput.value = ''
}

const confirmDeleteSource = async () => {
  const source = deleteConfirmSource.value
  if (!source) return
  
  // 验证输入的数据源名称是否正确
  if (deleteConfirmInput.value.trim() !== source.name) {
    message.warning(t.value.vectorCenter.sidebar.deleteConfirmNameMismatch)
    return
  }
  
  try {
    const msg = await aiVecSourceApi.delete([source.id])
    message.success(msg)
    emit('changed')
    closeDeleteConfirmModal()
  } catch (error) {
    const err = error as { message?: string }
    message.error(err?.message || t.value.vectorCenter.sidebar.deleteSourceFailed)
  }
}

const deleteSource = async (id: number | string) => {
  const source = sourceTree.value.find(s => String(s.id) === String(id))
  if (!source) return
  
  // 直接打开确认对话框，允许级联删除
  openDeleteConfirmModal(source)
}

const handleSourceSubmit = async (payload: AiVecSource) => {
  sourceModalSubmitting.value = true
  try {
    if (sourceModalMode.value === 'create') {
      await aiVecSourceApi.create(payload)
    } else {
      await aiVecSourceApi.update(payload)
    }
    sourceModalOpen.value = false
    emit('changed')
  } catch (error) {
    const err = error as { message?: string }
    message.error(err?.message || t.value.vectorCenter.sidebar.saveSourceFailed)
  } finally {
    sourceModalSubmitting.value = false
  }
}

const openEditStore = async (id: number | string) => {
  const detail = await aiVecStoreApi.detail(id)
  storeModalMode.value = 'edit'
  storeModalInitial.value = detail
  storeModalSourceId.value = detail.sourceId || null
  storeModalOpen.value = true
}

const deleteStore = async (id: number | string) => {
  try {
    const stats = await aiVecStoreApi.stats(id)
    if (stats.docCount > 0) {
      message.warning(t.value.vectorCenter.sidebar.deleteStoreWithDocs)
      return
    }
  } catch {
    message.error(t.value.vectorCenter.sidebar.checkStoreFailed)
    return
  }
  try {
    const msg = await aiVecStoreApi.delete([id])
    message.success(msg)
    emit('changed')
  } catch (error) {
    const err = error as { message?: string }
    message.error(err?.message || t.value.vectorCenter.sidebar.deleteStoreFailed)
  }
}

const handleStoreSubmit = async (payload: AiVecStore) => {
  storeModalSubmitting.value = true
  try {
    const finalPayload: AiVecStore = {
      ...payload,
      sourceId: payload.sourceId || storeModalSourceId.value || undefined
    }
    if (storeModalMode.value === 'create') {
      await aiVecStoreApi.create(finalPayload)
    } else {
      await aiVecStoreApi.update(finalPayload)
    }
    // 清空缓存，确保数据重新拉取
    storesBySourceCache.value = {}
    storeModalOpen.value = false
    emit('changed')
  } catch (error) {
    const err = error as { message?: string }
    message.error(err?.message || t.value.vectorCenter.sidebar.saveStoreFailed)
  } finally {
    storeModalSubmitting.value = false
  }
}

const onSourceModalOpenChange = (value: unknown) => {
  sourceModalOpen.value = Boolean(value)
}

const onStoreModalOpenChange = (value: unknown) => {
  storeModalOpen.value = Boolean(value)
}

const testConnectionOnFirstExpand = async (id: number | string): Promise<boolean> => {
  const sourceKey = String(id)
  if (firstExpandCheckedMap.value[sourceKey]) return true
  checkingSourceMap.value[sourceKey] = true
  try {
    const detail = await aiVecSourceApi.detail(id)
    await aiVecSourceApi.testConnection(detail, 25000)
    firstExpandCheckedMap.value[sourceKey] = true
    sourceConnectedOverride.value[sourceKey] = true
    return true
  } catch (error) {
    sourceConnectedOverride.value[sourceKey] = false
    const err = error as { message?: string; code?: string }
    const raw = String(err?.message || '')
    const isTimeout = err?.code === 'ECONNABORTED' || raw.toLowerCase().includes('timeout')
    message.warning(
        isTimeout
            ? t.value.vectorCenter.sidebar.connectionTimeout
            : (err?.message || t.value.vectorCenter.sidebar.connectionFailed)
    )
    return false
  } finally {
    checkingSourceMap.value[sourceKey] = false
  }
}

const fetchEnabledExtensions = async () => {
  try {
    const resp = await systemExtensionApi.queryPage({
      pageNo: 1,
      pageSize: 50,
      param: {
        listScope: 'INSTALLED',
        type: 'VECTOR_STORE'
      }
    })
    const rows = (resp.list || []).filter((item) => String(item.applied || '').toUpperCase() === 'Y')
    enabledExtensions.value = rows.map((item) => {
      const name = String(item.extensionName || item.extensionKey || t.value.vectorCenter.sidebar.extension)
      return {
        key: String(item.id ?? item.extensionKey ?? name),
        name,
        extensionCode: String(item.extensionCode || ''),
        avatar: String(item.avatar || ''),
        initial: name.slice(0, 1).toUpperCase()
      }
    })
  } catch {
    enabledExtensions.value = []
  }
}

const goPluginMarketplace = () => {
  marketplaceOpen.value = true
}

onMounted(() => {
  void fetchEnabledExtensions()
})

watch(
    () => props.stores,
    (stores) => {
      const sourceKey = pendingStoreSyncSourceKey.value
      if (!sourceKey) return
      storesBySourceCache.value[sourceKey] = [...(stores || [])]
    },
    {deep: true}
)
</script>

<style scoped>
/* 去除 SidebarShell body 的默认内边距，最大化内容区域 */
:deep(.ast-sidebar-body) {
  padding: 0;
}

.source-tree {
  flex: 1;
  overflow-y: auto;
  padding: 4px 4px 0 4px;
}

.source-section {
  margin: 0 2px;
}

.db-container {
  margin: 0 4px 6px 14px;
  padding-left: 10px;
  border-left: 1px solid var(--border-default);
  transition: border-color 0.2s ease;
  overflow: hidden;
}

.db-empty-hint {
  padding: 6px 8px;
  margin-top: 2px;
  color: var(--text-muted);
  font-size: 11px;
  border-radius: 0;
  transition: color 0.2s ease, background 0.2s ease;
}

/* 展开动画 — 限制 max-height 避免动画期间出现滚动条 */
.expand-enter-active,
.expand-leave-active {
  transition: max-height 0.25s ease, opacity 0.2s ease;
  max-height: 300px;
  overflow: hidden;
}

.expand-enter-from,
.expand-leave-to {
  max-height: 0;
  opacity: 0;
  margin-bottom: 0;
}

:deep(.danger-item) {
  color: var(--error);
}

:deep(.danger-item:hover) {
  color: var(--error) !important;
  background: color-mix(in srgb, var(--error) 10%, transparent) !important;
}

.sidebar-search-pill {
  flex: 1;
  min-width: 0;
  border: none;
}

/* 搜索栏旁边的新增图标按钮 */
.top-add-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 15px;
  color: var(--text-muted);
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 0;
  background: transparent;
}

.top-add-btn:hover {
  color: var(--primary);
  background: var(--primary-hover);
}
</style>

<style lang="less">
.blank-context-menu-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
}

.blank-context-menu {
  position: fixed;
  min-width: 160px;
  background: var(--bg-card, #fff);
  border: 1px solid var(--border-default, #e2e8f0);
  border-radius: 0;
  padding: 4px;
  box-shadow: 0 6px 16px color-mix(in srgb, var(--shadow-color, #000) 12%, transparent), 0 3px 6px color-mix(in srgb, var(--shadow-color, #000) 8%, transparent);
  z-index: 1001;
}

.blank-context-menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  font-size: 13px;
  color: var(--text-primary, #334155);
  border-radius: 0;
  cursor: pointer;
  transition: all 0.15s ease;
  border-left: 2px solid transparent;

  &:hover {
    background: var(--primary-hover, #eff6ff);
    color: var(--primary, #2563eb);
    border-left-color: var(--primary);
    padding-left: 14px;
  }
}

.blank-context-menu-divider {
  height: 1px;
  margin: 4px 8px;
  background: var(--border-default, #e2e8f0);
}


.hover-panel-overlay {
  position: fixed;
  inset: 0;
  z-index: 999;
  pointer-events: none;
}

.hover-panel {
  position: fixed;
  width: 280px;
  background: var(--bg-card, #fff);
  border: 1px solid var(--border-default, #e2e8f0);
  border-radius: 0;
  padding: 8px;
  box-shadow: 0 8px 24px color-mix(in srgb, var(--shadow-color, #000) 12%, transparent), 0 4px 8px color-mix(in srgb, var(--shadow-color, #000) 6%, transparent);
  z-index: 1000;
  pointer-events: auto;
  max-height: calc(100vh - 120px);
  overflow-y: auto;
}

.hover-panel-dbs {
  padding: 4px 0 0;
  border-top: 1px solid var(--border-default, #e2e8f0);
  margin-top: 4px;
}

.hover-panel-empty {
  padding: 12px;
  text-align: center;
  color: var(--text-muted, #94a3b8);
  font-size: 12px;
}

/* 删除确认对话框样式 */
.delete-confirm-content {
  padding: 8px 0;
}

.delete-confirm-warning {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: color-mix(in srgb, var(--warning) 10%, transparent);
  border-radius: 0;
  margin-bottom: 20px;
  border-left: 3px solid var(--warning);

  .warning-icon {
    font-size: 20px;
    color: var(--warning);
    flex-shrink: 0;
    margin-top: 2px;
  }

  span {
    color: var(--text-primary);
    font-size: 13px;
    line-height: 1.6;
  }
}

.delete-confirm-detail {
  background: var(--bg-input);
  border-radius: 0;
  padding: 12px 16px;
  margin-bottom: 20px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;

  &:not(:last-child) {
    border-bottom: 1px solid var(--border-default);
  }
}

.detail-label {
  color: var(--text-muted);
  font-size: 13px;
}

.detail-value {
  color: var(--text-primary);
  font-size: 13px;
  font-weight: 500;
}

.delete-confirm-input-group {
  margin-bottom: 24px;
}

.delete-confirm-label {
  display: block;
  color: var(--text-secondary);
  font-size: 13px;
  margin-bottom: 8px;
}

.delete-confirm-input {
  width: 100%;
}

.delete-confirm-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
