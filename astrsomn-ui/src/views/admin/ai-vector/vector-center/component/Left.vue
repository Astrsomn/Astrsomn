<template>
  <div class="sidebar-container">
    <div class="sidebar-header">
      <div class="header-left">
        <div class="header-icon-box">
          <DatabaseFilled class="header-icon"/>
        </div>
        <span class="header-title">资产目录</span>
      </div>
      <PlusOutlined class="add-icon" @click="handleAddSource"/>
    </div>

    <div class="sidebar-content" @contextmenu="onSidebarBlankContextMenu">
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

    <div class="sidebar-footer">
      <div class="footer-row">
        <div class="driver-info">
          <div class="s-avatars">
            <template v-if="enabledExtensions.length">
              <span
                  v-for="item in enabledExtensions.slice(0, 4)"
                  :key="item.key"
                  :title="item.name"
                  class="s-av s-av-real"
              >
                <img v-if="item.avatar" :alt="item.name" :src="item.avatar"/>
                <span v-else>{{ item.initial }}</span>
              </span>
            </template>
            <span v-else class="s-av">-</span>
          </div>
          <span class="s-text">
            {{ enabledExtensions.length ? `已启用扩展 ${enabledExtensions.length}` : '暂无已启用扩展' }}
          </span>
        </div>
        <AppstoreOutlined class="m-btn" title="打开插件市场" @click="goPluginMarketplace"/>
      </div>
    </div>
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
  </div>
</template>

<script lang="ts" setup>
import {computed, onMounted, ref, watch} from 'vue'
import {message} from 'ant-design-vue'
import {
  AppstoreOutlined,
  DatabaseFilled,
  DeleteOutlined,
  EditOutlined,
  PlusOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue'
import SourceCard from './SourceCard.vue'
import DbNode from './DbNode.vue'
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
}>()

const emit = defineEmits<{
  'select-source': [id: number | string]
  'select-store': [id: number | string]
  changed: []
}>()

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
    const provider = String(payload.provider || '').toLowerCase()

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

<style lang="less" scoped>
.sidebar-container {
  height: calc(100vh - 60px);
  width: 100%;
  background: var(--bg-card);
  display: flex;
  flex-direction: column;
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  margin: 0;
  overflow: hidden;
}

.sidebar-header {
  padding: 14px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--bg-card);
  border-bottom: 1px solid var(--border-default);

  .header-left {
    display: flex;
    align-items: center;
    gap: 10px;

    .header-icon-box {
      width: 30px;
      height: 30px;
      background: var(--primary);
      border-radius: var(--radius-md);
      display: flex;
      align-items: center;
      justify-content: center;

      .header-icon {
        color: #fff;
        font-size: 14px;
      }
    }

    .header-title {
      font-size: 15px;
      font-weight: 600;
      color: var(--text-heading);
    }
  }

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

    &:hover {
      background: var(--primary-hover);
      color: var(--primary);
    }
  }
}

.sidebar-content {
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

.sidebar-footer {
  padding: 12px 14px;
  border-top: 1px solid var(--border-default);

  .footer-row {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .driver-info {
      display: flex;
      align-items: center;
      gap: 8px;

      .s-avatars {
        display: flex;

        .s-av {
          width: 18px;
          height: 18px;
          border-radius: 50%;
          background: var(--primary-hover);
          color: var(--primary);
          font-size: 9px;
          font-weight: 700;
          display: flex;
          align-items: center;
          justify-content: center;
          border: 1px solid var(--bg-card);
          margin-right: -4px;
        }
      }

      .s-text {
        font-size: 11px;
        color: var(--text-muted);
      }
    }

    .m-btn {
      color: var(--text-muted);
      cursor: pointer;
      transition: color 0.2s;

      &:hover {
        color: var(--primary);
      }
    }
  }
}

.sidebar-footer .footer-row .driver-info .s-avatars .s-av.s-av-real {
  overflow: hidden;
  padding: 0;
}

.sidebar-footer .footer-row .driver-info .s-avatars .s-av.s-av-real img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

:deep(.danger-item) {
  color: var(--error);

  &:hover {
    color: var(--error) !important;
    background: rgba(239, 68, 68, 0.1) !important;
  }
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
</style>
