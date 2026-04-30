<template>
  <div class="sidebar-container">
    <div class="sidebar-header">
      <div class="header-left">
        <div class="header-icon-box">
          <DatabaseFilled class="header-icon" />
        </div>
        <span class="header-title">资产目录</span>
      </div>
      <PlusOutlined class="add-icon" @click="handleAddSource" />
    </div>

    <div class="sidebar-content">
      <div
        v-for="source in sourceTree"
        :key="source.id"
        class="source-section"
      >
        <a-dropdown :trigger="['contextmenu']">
          <SourceCard
            :source-name="source.name"
            :source-type="source.type"
            :provider-avatar="source.providerAvatar"
            :ip="source.ip"
            :port="source.port"
            :is-connected="source.connected"
            :is-checking="checkingSourceMap[String(source.id)] === true"
            :is-open="openKeys.includes(source.id)"
            @toggle="selectSource(source.id)"
          />
          <template #overlay>
            <a-menu @click="onSourceMenuClick($event, source)">
              <a-menu-item key="addDb">
                <template #icon><PlusOutlined /></template>
                新增数据库
              </a-menu-item>
              <a-menu-item key="edit">
                <template #icon><EditOutlined /></template>
                编辑数据源
              </a-menu-item>
              <a-menu-divider />
              <a-menu-item key="delete" class="danger-item">
                <template #icon><DeleteOutlined /></template>
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
                  :db-name="db.dbName"
                  :model-name="db.modelName"
                  :dim="db.dim"
                  :active="db.active"
                  :is-selected="String(selectedStoreId) === String(db.id)"
                  @select="selectDb(source.id, db.id)"
                />
                <template #overlay>
                  <a-menu @click="onDbMenuClick($event, source.id, db)">
                    <a-menu-item key="edit">
                      <template #icon><EditOutlined /></template>
                      编辑数据库
                    </a-menu-item>
                    <a-menu-divider />
                    <a-menu-item key="delete" class="danger-item">
                      <template #icon><DeleteOutlined /></template>
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
                  <template #icon><PlusOutlined /></template>
                  新建数据库
                </a-menu-item>
                <a-menu-item key="editSource">
                  <template #icon><EditOutlined /></template>
                  编辑数据源
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="deleteSource" class="danger-item">
                  <template #icon><DeleteOutlined /></template>
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
                class="s-av s-av-real"
                :title="item.name"
              >
                <img v-if="item.avatar" :src="item.avatar" :alt="item.name" />
                <span v-else>{{ item.initial }}</span>
              </span>
            </template>
            <span v-else class="s-av">-</span>
          </div>
          <span class="s-text">
            {{ enabledExtensions.length ? `已启用扩展 ${enabledExtensions.length}` : '暂无已启用扩展' }}
          </span>
        </div>
        <AppstoreOutlined class="m-btn" title="打开插件市场" @click="goPluginMarketplace" />
      </div>
    </div>
    <VecSourceFormModal
      :open="sourceModalOpen"
      @update:open="onSourceModalOpenChange"
      :mode="sourceModalMode"
      :confirm-loading="sourceModalSubmitting"
      :initial="sourceModalInitial"
      @submit="handleSourceSubmit"
    />
    <VecStoreFormModal
      :open="storeModalOpen"
      @update:open="onStoreModalOpenChange"
      :mode="storeModalMode"
      :confirm-loading="storeModalSubmitting"
      :initial="storeModalInitial"
      :default-source-id="storeModalSourceId"
      @submit="handleStoreSubmit"
    />
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  DatabaseFilled,
  AppstoreOutlined
} from '@ant-design/icons-vue'
import SourceCard from './SourceCard.vue'
import DbNode from './DbNode.vue'
import VecSourceFormModal from '@/views/admin/ai-vector/vec-source/VecSourceFormModal.vue'
import VecStoreFormModal from '@/views/admin/ai-vector/vec-store/VecStoreFormModal.vue'
import { aiVecSourceApi, type AiVecSource } from '@/api/aiVecSource.ts'
import { aiVecStoreApi, type AiVecStore } from '@/api/aiVecStore.ts'
import { systemExtensionApi } from '@/api/systemExtension.ts'
import { useRouter } from 'vue-router'

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
        modelName: store.instanceName || store.instanceKey || '-',
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
    if (provider !== 'qdrant' && provider !== 'chroma') {
      message.warning('当前仅允许创建 qdrant/chroma 类型数据源')
      return
    }
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
    query: { panel: 'marketplace' }
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
  { deep: true }
)
</script>

<style lang="less" scoped>
@primary-blue: #2563eb;
@bg-main: #fcfdfe;
@border-subtle: rgba(226, 232, 240, 0.6);
@text-main: #1e293b;
@text-sub: #64748b;
@text-muted: #94a3b8;

.sidebar-container {
  height: calc(100vh - 74px);
  width: 100%;
  background: #fff;
  display: flex;
  flex-direction: column;
  border: 1px solid @border-subtle;
  border-radius: 12px;
  margin: 0;
  overflow: hidden;
}

.sidebar-header {
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(to bottom, #ffffff, #fafafa);
  border-bottom: 1px solid #f1f5f9;

  .header-left {
    display: flex;
    align-items: center;
    gap: 10px;

    .header-icon-box {
      width: 32px;
      height: 32px;
      background: @primary-blue;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);

      .header-icon {
        color: #fff;
        font-size: 14px;
      }
    }

    .header-title {
      font-size: 15px;
      font-weight: 700;
      color: @text-main;
      letter-spacing: -0.01em;
    }
  }

  .add-icon {
    width: 28px;
    height: 28px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: @text-muted;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      background: #f1f5f9;
      color: @primary-blue;
    }
  }
}

.sidebar-content {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.source-section {
  margin: 0 8px;
}

.db-container {
  margin: 0 8px 8px 20px;
  padding-left: 16px;
  border-left: 1.5px solid #f1f5f9;
  overflow: hidden;
}

.db-empty-hint {
  padding: 10px 12px;
  margin-top: 6px;
  border-radius: 8px;
  color: #94a3b8;
  font-size: 12px;
  background: #f8fafc;
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
  padding: 14px 16px;
  border-top: 1px solid #f5f5f5;

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
          background: #e6f7ff;
          color: @primary-blue;
          font-size: 9px;
          font-weight: 700;
          display: flex;
          align-items: center;
          justify-content: center;
          border: 1px solid #fff;
          margin-right: -4px;
        }
      }

      .s-text {
        font-size: 11px;
        color: @text-muted;
      }
    }

    .m-btn {
      color: #ddd;
      cursor: pointer;
      transition: color 0.2s;

      &:hover {
        color: @primary-blue;
      }
    }
  }
}

:deep(.danger-item) {
  color: #ff4d4f;
  
  &:hover {
    color: #ff4d4f !important;
    background: #fff1f0 !important;
  }
}

/* Unified style: align with AdminModuleShell */
.sidebar-container {
  border: 1px solid #f1f5f9;
  border-radius: 14px;
  background: #ffffff;
}

.sidebar-header {
  padding: 14px 16px;
  background: #ffffff;
  border-bottom: 1px solid #f1f5f9;
}

.sidebar-header .header-left .header-icon-box {
  width: 30px;
  height: 30px;
  background: #1a73e8;
  border-radius: 8px;
  box-shadow: none;
}

.sidebar-header .header-left .header-title {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.sidebar-header .add-icon {
  width: 30px;
  height: 30px;
  border-radius: 8px;
}

.sidebar-header .add-icon:hover {
  background: #e8f0fe;
  color: #1a73e8;
}

.sidebar-content {
  padding: 10px 8px;
}

.source-section {
  margin: 0 4px;
}

.db-container {
  margin: 0 6px 8px 18px;
  padding-left: 12px;
  border-left: 1px solid #f1f5f9;
}

.sidebar-footer {
  padding: 12px 14px;
  border-top: 1px solid #f1f5f9;
}

.sidebar-footer .footer-row .driver-info .s-avatars .s-av {
  background: #e8f0fe;
  color: #1a73e8;
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
</style>
