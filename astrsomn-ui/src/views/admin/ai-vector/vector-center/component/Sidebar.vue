<template>
  <div class="sidebar-wrapper">
  <SidebarShell :collapsed="collapsed">
    <template #top>
      <a-tooltip :placement="collapsed ? 'right' : 'bottom'">
        <template #title>{{ collapsed ? '展开侧边栏' : '收起侧边栏' }}</template>
        <div class="collapse-toggle" @click="emit('toggle-collapse')">
          <MenuUnfoldOutlined v-if="collapsed"/>
          <MenuFoldOutlined v-else/>
        </div>
      </a-tooltip>
      <AstSearchInput
          v-if="!collapsed"
          v-model="searchText"
          class="sidebar-search-pill"
          layout="fluid"
          placeholder="搜索数据源..."
          @search="handleSearch"
      />
    </template>

    <!-- 折叠模式：源图标列表 -->
    <div v-if="collapsed" class="collapsed-content">
      <div
          v-for="source in sourceTree"
          :key="source.id"
          class="collapsed-source-item"
          @click="selectSource(source.id)"
          @mouseenter="openHoverPanel(source.id, $event)"
          @mouseleave="scheduleCloseHoverPanel"
      >
        <img v-if="source.providerAvatar" :src="source.providerAvatar" alt="" class="collapsed-source-avatar"/>
        <ClusterOutlined v-else class="collapsed-source-icon"/>
      </div>
      <a-tooltip placement="right">
        <template #title>添加数据源</template>
        <div class="collapsed-add-btn" @click="handleAddSource">
          <PlusOutlined class="collapsed-add-icon"/>
        </div>
      </a-tooltip>
    </div>

    <!-- 展开模式：源树 -->
    <div v-else class="source-tree" @contextmenu="onSidebarBlankContextMenu">
      <div
          v-for="source in sourceTree"
          :key="source.id"
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
            <a-menu @click="onSourceMenuClick($event, source)">
              <a-menu-item key="addDb">
                <template #icon>
                  <PlusOutlined/>
                </template>
                新增数据库
              </a-menu-item>
              <a-menu-item key="edit">
                <template #icon>
                  <EditOutlined/>
                </template>
                编辑数据源
              </a-menu-item>
              <a-menu-divider/>
              <a-menu-item key="delete" class="danger-item">
                <template #icon>
                  <DeleteOutlined/>
                </template>
                删除数据源
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>

        <transition name="expand">
          <a-dropdown v-if="openKeys.includes(source.id)" :trigger="['contextmenu']">
            <div class="db-container">
              <a-dropdown
                  v-for="db in source.dbs"
                  :key="db.id"
                  :trigger="['contextmenu']"
              >
                <DbNode
                    :active="db.active"
                    :db-name="db.dbName"
                    :dim="db.dim"
                    :is-selected="String(selectedStoreId) === String(db.id)"
                    :model-name="db.modelName"
                    @select="selectDb(source.id, db.id)"
                />
                <template #overlay>
                  <a-menu @click="onDbMenuClick($event, source.id, db)">
                    <a-menu-item key="edit">
                      <template #icon>
                        <EditOutlined/>
                      </template>
                      编辑数据库
                    </a-menu-item>
                    <a-menu-divider/>
                    <a-menu-item key="delete" class="danger-item">
                      <template #icon>
                        <DeleteOutlined/>
                      </template>
                      删除数据库
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
              <div v-if="!source.dbs.length" class="db-empty-hint">右键空白区域可新建数据库</div>
            </div>
            <template #overlay>
              <a-menu @click="onDbBlankMenuClick($event, source)">
                <a-menu-item key="addDb">
                  <template #icon>
                    <PlusOutlined/>
                  </template>
                  新建数据库
                </a-menu-item>
                <a-menu-item key="editSource">
                  <template #icon>
                    <EditOutlined/>
                  </template>
                  编辑数据源
                </a-menu-item>
                <a-menu-divider/>
                <a-menu-item key="deleteSource" class="danger-item">
                  <template #icon>
                    <DeleteOutlined/>
                  </template>
                  删除数据源
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </transition>
      </div>
    </div>

    <!-- 添加数据源按钮（展开模式） -->
    <div v-if="!collapsed" class="add-source-section">
      <div class="add-source-btn" @click="handleAddSource">
        <PlusOutlined class="add-source-icon"/>
        <span>添加数据源</span>
      </div>
    </div>

    <template #footer>
      <SidebarFooter
          :collapsed="collapsed"
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
          <span>新建数据源</span>
        </div>
        <div class="blank-context-menu-divider"/>
        <div class="blank-context-menu-item" @click="onBlankMenuAction('refresh')">
          <ReloadOutlined/>
          <span>刷新</span>
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
  <!-- 悬浮面板（折叠模式下 hover source 时弹出） -->
  <Teleport to="body">
    <div
        v-if="hoverPanelVisible && hoverSource"
        class="hover-panel-overlay"
        @mouseenter="onHoverPanelEnter"
        @mouseleave="onHoverPanelLeave"
    >
      <div
          class="hover-panel"
          :style="{ left: hoverPanelX + 'px', top: hoverPanelY + 'px' }"
      >
        <SourceCard
            :ip="hoverSource.ip"
            :is-checking="checkingSourceMap[String(hoverSource.id)] === true"
            :is-connected="hoverSource.connected"
            :is-open="true"
            :port="hoverSource.port"
            :provider-avatar="hoverSource.providerAvatar"
            :source-name="hoverSource.name"
            :source-type="hoverSource.type"
            @toggle="selectSource(hoverSource.id)"
        />
        <div v-if="hoverSource.dbs.length" class="hover-panel-dbs">
          <DbNode
              v-for="db in hoverSource.dbs"
              :key="db.id"
              :active="db.active"
              :db-name="db.dbName"
              :dim="db.dim"
              :is-selected="String(selectedStoreId) === String(db.id)"
              :model-name="db.modelName"
              @select="onHoverSelectDb(hoverSource.id, db.id)"
          />
        </div>
        <div v-else class="hover-panel-empty">暂无数据库</div>
      </div>
    </div>
  </Teleport>
  </div>
</template>

<script lang="ts" setup>
import {computed, onMounted, ref, watch} from 'vue'
import {message} from 'ant-design-vue'
import {
  ClusterOutlined,
  DeleteOutlined,
  EditOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  PlusOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue'
import SidebarShell from '@/components/sidebar/SidebarShell.vue'
import SidebarFooter from '@/components/sidebar/SidebarFooter.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import SourceCard from './sidebar/SourceCard.vue'
import DbNode from './sidebar/DbNode.vue'
import VecSourceFormModal from '@/views/admin/ai-vector/vec-source/VecSourceFormModal.vue'
import VecStoreFormModal from '@/views/admin/ai-vector/vec-store/VecStoreFormModal.vue'
import {type AiVecSource, aiVecSourceApi} from '@/api/aiVecSource.ts'
import {type AiVecStore, aiVecStoreApi} from '@/api/aiVecStore.ts'
import {systemExtensionApi} from '@/api/systemExtension.ts'
import {useRouter} from 'vue-router'

const props = defineProps<{
  sources: AiVecSource[]
  stores: AiVecStore[]
  selectedStoreId?: number | string
  collapsed?: boolean
}>()

const emit = defineEmits<{
  'select-source': [id: number | string]
  'select-store': [id: number | string]
  changed: []
  'toggle-collapse': []
}>()

const searchText = ref('')
const handleSearch = () => {
  // TODO: 实现搜索逻辑
}

// 悬浮面板相关
const hoverSourceId = ref<number | string | null>(null)
const hoverPanelX = ref(0)
const hoverPanelY = ref(0)
const hoverPanelVisible = ref(false)
let hoverCloseTimer: ReturnType<typeof setTimeout> | null = null
let hoverOpenTimer: ReturnType<typeof setTimeout> | null = null

const clearHoverTimer = () => {
  if (hoverCloseTimer) {
    clearTimeout(hoverCloseTimer)
    hoverCloseTimer = null
  }
  if (hoverOpenTimer) {
    clearTimeout(hoverOpenTimer)
    hoverOpenTimer = null
  }
}

const openHoverPanel = (sourceId: number | string, e: MouseEvent) => {
  clearHoverTimer()
  hoverOpenTimer = setTimeout(() => {
    const rect = (e.currentTarget as HTMLElement).getBoundingClientRect()
    hoverSourceId.value = sourceId
    hoverPanelX.value = rect.right + 6
    hoverPanelY.value = rect.top
    hoverPanelVisible.value = true
  }, 150)
}

const scheduleCloseHoverPanel = () => {
  clearHoverTimer()
  hoverCloseTimer = setTimeout(() => {
    hoverPanelVisible.value = false
    hoverSourceId.value = null
  }, 200)
}

const onHoverPanelEnter = () => {
  clearHoverTimer()
}

const onHoverPanelLeave = () => {
  scheduleCloseHoverPanel()
}

const hoverSource = computed(() => {
  if (hoverSourceId.value == null) return null
  return sourceTree.value.find(s => String(s.id) === String(hoverSourceId.value)) || null
})

const onHoverSelectDb = (sourceId: number | string, dbId: number | string) => {
  pendingStoreSyncSourceKey.value = String(sourceId)
  emit('select-source', sourceId)
  emit('select-store', dbId)
  hoverPanelVisible.value = false
  hoverSourceId.value = null
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
const enabledExtensions = ref<Array<{ key: string; name: string; avatar: string; initial: string }>>([])
const storesBySourceCache = ref<Record<string, AiVecStore[]>>({})
const pendingStoreSyncSourceKey = ref<string>('')
const blankContextMenuVisible = ref(false)
const blankContextMenuX = ref(0)
const blankContextMenuY = ref(0)
const router = useRouter()

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
      const connected =
          sourceConnectedOverride.value[sourceKey] !== undefined
              ? sourceConnectedOverride.value[sourceKey]
              : sourceStatus === 'enabled'
      return {
        id: source.id as number | string,
        name: source.name || `Source-${source.id}`,
        type: sourceType,
        providerAvatar: source.providerAvatar,
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

const selectSource = async (id: number | string) => {
  const isOpening = !openKeys.value.includes(id)
  pendingStoreSyncSourceKey.value = String(id)
  emit('select-source', id)
  if (!isOpening) {
    toggleSource(id)
    return
  }
  const checked = await testConnectionOnFirstExpand(id)
  if (!checked) return
  toggleSource(id)
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
      target.closest('.source-section') ||
      target.closest('.sidebar-header') ||
      target.closest('.sidebar-footer') ||
      target.closest('.blank-context-menu')
  ) {
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

const handleDbMenuClick = (key: string, _sourceId: number | string, db: Db) => {
  switch (key) {
    case 'edit':
      openEditStore(db.id)
      break
    case 'delete':
      void deleteStore(db.id)
      break
  }
}

const openEditSource = async (id: number | string) => {
  const detail = await aiVecSourceApi.detail(id)
  sourceModalMode.value = 'edit'
  sourceModalInitial.value = detail
  sourceModalOpen.value = true
}

const deleteSource = async (id: number | string) => {
  try {
    const msg = await aiVecSourceApi.delete([id])
    message.success(msg)
    emit('changed')
  } catch (error) {
    const err = error as { message?: string }
    message.error(err?.message || '删除数据源失败')
  }
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
    message.error(err?.message || '保存数据源失败')
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
    const msg = await aiVecStoreApi.delete([id])
    message.success(msg)
    emit('changed')
  } catch (error) {
    const err = error as { message?: string }
    message.error(err?.message || '删除数据库失败')
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
    storeModalOpen.value = false
    emit('changed')
  } catch (error) {
    const err = error as { message?: string }
    message.error(err?.message || '保存数据库失败')
  } finally {
    storeModalSubmitting.value = false
  }
}

const onSourceMenuClick = (payload: unknown, source: Source) => {
  const key = String((payload as { key?: string | number })?.key ?? '')
  handleSourceMenuClick(key, source)
}

const onDbMenuClick = (payload: unknown, sourceId: number | string, db: Db) => {
  const key = String((payload as { key?: string | number })?.key ?? '')
  handleDbMenuClick(key, sourceId, db)
}

const onDbBlankMenuClick = (payload: unknown, source: Source) => {
  const key = String((payload as { key?: string | number })?.key ?? '')
  handleDbBlankMenuClick(key, source)
}

const onSourceModalOpenChange = (value: unknown) => {
  sourceModalOpen.value = Boolean(value)
}

const onStoreModalOpenChange = (value: unknown) => {
  storeModalOpen.value = Boolean(value)
}

const handleDbBlankMenuClick = (key: string, source: Source) => {
  switch (key) {
    case 'addDb':
      handleSourceMenuClick('addDb', source)
      break
    case 'editSource':
      openEditSource(source.id)
      break
    case 'deleteSource':
      void deleteSource(source.id)
      break
  }
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
    message.success('连接成功，已展开数据库列表')
    return true
  } catch (error) {
    sourceConnectedOverride.value[sourceKey] = false
    const err = error as { message?: string; code?: string }
    const raw = String(err?.message || '')
    const isTimeout = err?.code === 'ECONNABORTED' || raw.toLowerCase().includes('timeout')
    message.warning(
        isTimeout
            ? '连接超时（25s），请检查数据源网络或服务状态后重试'
            : (err?.message || '连接失败，已自动收起，请检查配置后重试')
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
      const name = String(item.extensionName || item.extensionKey || '扩展')
      return {
        key: String(item.id ?? item.extensionKey ?? name),
        name,
        avatar: String(item.avatar || ''),
        initial: name.slice(0, 1).toUpperCase()
      }
    })
  } catch {
    enabledExtensions.value = []
  }
}

const goPluginMarketplace = () => {
  void router.push({
    path: '/admin/system/extensions',
    query: {panel: 'marketplace'}
  })
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
.add-icon {
  width: 30px;
  height: 30px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  cursor: pointer;
  transition: all 0.2s;
}

.add-icon:hover {
  background: var(--primary-hover);
  color: var(--primary);
}

.collapse-toggle {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-sm);
  color: var(--text-muted);
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}

.collapse-toggle:hover {
  color: var(--primary);
  background: var(--primary-hover);
}

.ast-sidebar.collapsed .collapse-toggle {
  width: 36px;
  height: 36px;
}

.source-tree {
  flex: 1;
  overflow-y: auto;
  padding: 10px 8px;
}

.source-section {
  margin: 0 4px;
}

.db-container {
  margin: 0 6px 8px 18px;
  padding-left: 12px;
  border-left: 1px solid var(--border-default);
  overflow: hidden;
}

.db-empty-hint {
  padding: 10px 12px;
  margin-top: 6px;
  border-radius: var(--radius-md);
  color: var(--text-muted);
  font-size: 12px;
  background: var(--bg-input);
}

.expand-enter-active,
.expand-leave-active {
  transition: all 0.3s ease;
  max-height: 500px;
  opacity: 1;
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
  background: rgba(239, 68, 68, 0.1) !important;
}

/* 折叠模式内容 */
.collapsed-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 8px 0;
  width: 100%;
}

.collapsed-source-item {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  background: var(--bg-input);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  flex-shrink: 0;
}

.collapsed-source-item:hover {
  background: var(--primary-hover);
  transform: translateY(-1px);
}

.collapsed-source-avatar {
  width: 22px;
  height: 22px;
  object-fit: contain;
  border-radius: var(--radius-lg);
}

.collapsed-source-icon {
  font-size: 16px;
  color: var(--text-secondary);
}

.collapsed-add-btn {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  background: var(--bg-input);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  flex-shrink: 0;
}

.collapsed-add-btn:hover {
  background: var(--primary-hover);
  color: var(--primary);
}

.collapsed-add-icon {
  font-size: 16px;
  color: var(--text-muted);
}

.sidebar-search-pill {
  flex: 1;
  min-width: 0;
}

.add-source-section {
  flex-shrink: 0;
  padding: 8px;
  border-top: 1px solid var(--border-default);
}

.add-source-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: var(--radius-sm);
  color: var(--text-muted);
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
}

.add-source-btn:hover {
  background: var(--primary-hover);
  color: var(--primary);
}

.add-source-icon {
  font-size: 14px;
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
  border-radius: 8px;
  padding: 4px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12), 0 3px 6px rgba(0, 0, 0, 0.08);
  z-index: 1001;
}

.blank-context-menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  font-size: 13px;
  color: var(--text-primary, #334155);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.15s;

  &:hover {
    background: var(--primary-hover, #eff6ff);
    color: var(--primary, #2563eb);
  }
}

.blank-context-menu-divider {
  height: 1px;
  margin: 4px 8px;
  background: var(--border-default, #e2e8f0);
}

/* 悬浮面板（折叠模式） */
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
  border-radius: 10px;
  padding: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12), 0 4px 8px rgba(0, 0, 0, 0.06);
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
</style>
