<template>
  <a-drawer
      :closable="true"
      :maskClosable="true"
      :open="props.open"
      :title="drawerTitle"
      :width="560"
      placement="right"
      root-class-name="instance-select-drawer"
      @close="handleClose"
  >
    <div class="select-drawer-content">
      <div class="toolbar">
        <a-input
            v-model:value="keyword"
            allow-clear
            class="toolbar-search"
            placeholder="搜索实例名称"
            @pressEnter="handleSearch"
        >
          <template #prefix>
            <SearchOutlined/>
          </template>
        </a-input>
        <a-select
            v-model:value="queryStatus"
            allow-clear
            class="toolbar-status"
            placeholder="状态筛选"
            @change="handleSearch"
        >
          <a-select-option value="enabled">启用</a-select-option>
          <a-select-option value="disabled">禁用</a-select-option>
        </a-select>
        <a-button type="primary" @click="handleSearch">查询</a-button>
        <a-button @click="emit('create')">
          <template #icon>
            <PlusOutlined/>
          </template>
          新增
        </a-button>
      </div>

      <a-tabs v-if="!props.fixedModelType" v-model:activeKey="activeTypeTab" class="type-tabs"
              @change="handleTypeTabChange">
        <a-tab-pane key="all" tab="全部"/>
        <a-tab-pane key="chat" tab="对话"/>
        <a-tab-pane key="embedding" tab="向量"/>
        <a-tab-pane key="image" tab="图像"/>
        <a-tab-pane key="voice" tab="语音"/>
      </a-tabs>

      <a-spin :spinning="loading">
        <div class="instance-list">
          <div
              v-for="inst in list"
              :key="inst.id"
              :class="{ selected: selectedId === inst.id }"
              class="instance-item"
              @click="handleSelect(inst)"
          >
            <div :class="inst.modelType" class="instance-icon">
              <MessageOutlined v-if="inst.modelType === 'chat'"/>
              <PartitionOutlined v-else-if="inst.modelType === 'embedding'"/>
              <PictureOutlined v-else-if="inst.modelType === 'image'"/>
              <AudioOutlined v-else-if="inst.modelType === 'voice'"/>
              <ControlOutlined v-else/>
            </div>
            <div class="instance-info">
              <div class="instance-name">{{ inst.instanceName || '未命名实例' }}</div>
              <div class="instance-key">
                <KeyOutlined/>
                {{ inst.instanceKey || '-' }}
              </div>
            </div>
            <div class="instance-meta">
              <div class="meta-row">
                <span v-if="inst.modelType" class="type-tag">{{ modelTypeLabel(inst.modelType) }}</span>
                <span v-if="inst.modelKey" class="model-key-tag">{{ inst.modelKey }}</span>
                <span :class="inst.status" class="status-badge">
                  {{ inst.status === 'enabled' ? '启用' : '禁用' }}
                </span>
              </div>
              <div class="meta-bottom">
                <span class="create-time">{{ formatTime(inst.createTime) }}</span>
                <a-button
                    class="edit-btn"
                    size="small"
                    type="text"
                    @click="(e: MouseEvent) => handleEdit(e, inst)"
                >
                  <template #icon>
                    <EditOutlined/>
                  </template>
                </a-button>
              </div>
            </div>
          </div>

          <a-empty v-if="!loading && list.length === 0" description="暂无实例"/>
        </div>
      </a-spin>

      <div class="drawer-footer">
        <AstPagination
            :current="page.pageNum"
            :page-size="page.pageSize"
            :show-size-changer="true"
            :total="page.total"
            @change="onPageChange"
        />
      </div>
    </div>
  </a-drawer>
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import {
  AudioOutlined,
  ControlOutlined,
  EditOutlined,
  KeyOutlined,
  MessageOutlined,
  PartitionOutlined,
  PictureOutlined,
  PlusOutlined,
  SearchOutlined
} from '@ant-design/icons-vue'
import AstPagination from '@/components/home/AstPagination.vue'
import {type AiInstance, aiInstanceApi, type PageResponse} from '@/api/aiInstance.ts'

const props = defineProps<{
  open: boolean
  disableTtlEdit?: boolean
  defaultModelType?: string
  fixedModelType?: string
  /** Narrow list to instances whose model key matches (backend LIKE on full key). */
  filterModelKey?: string
}>()

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void
  (e: 'select', instance: AiInstance): void
  (e: 'edit', instance: AiInstance): void
  (e: 'create'): void
}>()

const activeTypeTab = ref('all')
const keyword = ref('')
const queryStatus = ref<string | undefined>()
const loading = ref(false)
const list = ref<AiInstance[]>([])
const selectedId = ref<number | string | undefined>()
const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const modelTypeLabel = (type?: string) => {
  if (type === 'embedding') return '向量'
  if (type === 'image') return '图像'
  if (type === 'voice') return '语音'
  return '对话'
}

const drawerTitle = computed(() => {
  if (props.fixedModelType) return `选择${modelTypeLabel(props.fixedModelType)}实例`
  return '选择推理配置'
})

const formatTime = (t?: string) => {
  if (!t) return '-'
  return t.length > 16 ? t.substring(0, 16) : t
}

const handleEdit = (e: MouseEvent, inst: AiInstance) => {
  e.stopPropagation()
  emit('edit', inst)
}

const fetchList = async () => {
  loading.value = true
  try {
    const modelType = props.fixedModelType || (activeTypeTab.value !== 'all' ? activeTypeTab.value : undefined)
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        instanceName: keyword.value || undefined,
        status: queryStatus.value || undefined,
        modelType,
        modelKey: props.filterModelKey?.trim() || undefined
      }
    }
    const resp: PageResponse<AiInstance> = await aiInstanceApi.queryPage(payload)
    list.value = resp.list || []
    page.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.pageNum = 1
  void fetchList()
}

const handleTypeTabChange = () => {
  page.pageNum = 1
  void fetchList()
}

const handleSelect = (instance: AiInstance) => {
  selectedId.value = instance.id
  emit('select', instance)
}

const onPageChange = (p: number, size: number) => {
  page.pageNum = p
  page.pageSize = size
  void fetchList()
}

const handleClose = () => {
  emit('update:open', false)
}

watch(() => props.open, (val) => {
  if (val) {
    keyword.value = ''
    queryStatus.value = undefined
    selectedId.value = undefined
    activeTypeTab.value = props.fixedModelType || props.defaultModelType || 'all'
    page.pageNum = 1
    page.pageSize = 10
    void fetchList()
  }
})
</script>

<style scoped>
.select-drawer-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
}

.type-tabs {
  margin-bottom: 0;
  margin-top: 4px;
}

.type-tabs :deep(.ant-tabs-nav) {
  margin-bottom: 0;
}

.type-tabs :deep(.ant-tabs-tab) {
  padding: 8px 0;
  font-size: 13px;
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.toolbar-search {
  flex: 1;
  min-width: 180px;
}

.toolbar-status {
  width: 120px;
}

.instance-list {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.instance-item {
  display: flex;
  align-items: center;
  gap: 12px;
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg, 12px);
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.instance-item:hover {
  border-color: var(--primary);
  box-shadow: var(--shadow-card, 0 2px 8px rgba(0, 0, 0, 0.06));
}

.instance-item.selected {
  border-color: var(--primary);
  background: var(--primary-hover, rgba(59, 130, 246, 0.05));
}

.instance-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.instance-icon.chat {
  background: linear-gradient(135deg, #6366f1 0%, #a855f7 100%);
  color: white;
}

.instance-icon.embedding {
  background: linear-gradient(135deg, #10b981 0%, #22c55e 100%);
  color: white;
}

.instance-icon.image {
  background: linear-gradient(135deg, #f59e0b 0%, #f97316 100%);
  color: white;
}

.instance-icon.voice {
  background: linear-gradient(135deg, #06b6d4 0%, #6366f1 100%);
  color: white;
}

.instance-icon:empty {
  background: linear-gradient(135deg, #94a3b8 0%, #64748b 100%);
  color: white;
}

.instance-info {
  flex: 1;
  min-width: 0;
}

.instance-name {
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.instance-key {
  font-size: 12px;
  color: var(--text-secondary);
  font-family: 'JetBrains Mono', monospace;
  display: flex;
  align-items: center;
  gap: 4px;
}

.instance-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
  flex-shrink: 0;
}

.meta-row {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.meta-bottom {
  display: flex;
  align-items: center;
  gap: 4px;
}

.create-time {
  font-size: 11px;
  color: var(--text-tertiary, #94a3b8);
  white-space: nowrap;
}

.edit-btn {
  color: var(--text-secondary);
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.edit-btn:hover {
  color: var(--primary);
}

.type-tag {
  font-size: 11px;
  padding: 2px 8px;
  background: var(--bg-secondary, #f1f5f9);
  border-radius: 4px;
  color: var(--text-secondary);
}

.model-key-tag {
  font-size: 10px;
  padding: 2px 6px;
  background: #ede9fe;
  border-radius: 4px;
  color: #6366f1;
  font-family: 'JetBrains Mono', monospace;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-badge {
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 600;
}

.status-badge.enabled {
  background: #dcfce7;
  color: #16a34a;
}

.status-badge.disabled {
  background: #fee2e2;
  color: #ef4444;
}

.drawer-footer {
  flex-shrink: 0;
  padding-top: 16px;
  border-top: 1px solid var(--border-default);
}
</style>
