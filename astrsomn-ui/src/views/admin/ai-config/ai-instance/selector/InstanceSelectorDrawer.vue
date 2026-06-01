<template>
  <AstDrawer
      :open="props.open"
      @update:open="emit('update:open', $event)"
      :width="560"
      root-class-name="instance-select-drawer"
  >
    <template #title>{{ drawerTitle }}</template>

    <div class="select-drawer-content">
      <div class="toolbar">
        <AstSearchInput
            v-model="keyword"
            class="toolbar-search"
            layout="fluid"
            :placeholder="t.selector.drawer.searchPlaceholder"
            @search="handleSearch"
        />
        <a-select
            v-model:value="queryStatus"
            allow-clear
            class="toolbar-status"
            :placeholder="t.selector.drawer.statusPlaceholder"
            @change="handleSearch"
        >
          <a-select-option value="enabled">{{ t.selector.drawer.status.enabled }}</a-select-option>
          <a-select-option value="disabled">{{ t.selector.drawer.status.disabled }}</a-select-option>
        </a-select>
        <a-button @click="emit('create')">
          <template #icon>
            <PlusOutlined/>
          </template>
          {{ t.selector.drawer.create }}
        </a-button>
      </div>

      <a-tabs v-if="!props.fixedModelType" v-model:activeKey="activeTypeTab" class="type-tabs"
              @change="handleTypeTabChange">
        <a-tab-pane key="all" :tab="t.selector.drawer.tab.all"/>
        <a-tab-pane key="chat" :tab="t.selector.drawer.tab.chat"/>
        <a-tab-pane key="embedding" :tab="t.selector.drawer.tab.embedding"/>
        <a-tab-pane key="image" :tab="t.selector.drawer.tab.image"/>
        <a-tab-pane key="voice" :tab="t.selector.drawer.tab.voice"/>
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
              <div class="instance-name">{{ inst.instanceName || t.selector.drawer.unnamed }}</div>
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
                  {{ inst.status === 'enabled' ? t.selector.drawer.status.enabled : t.selector.drawer.status.disabled }}
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

          <a-empty v-if="!loading && list.length === 0" :description="t.selector.drawer.empty"/>
        </div>
      </a-spin>
    </div>

    <template #footer>
      <AstPagination
          :current="page.pageNum"
          :page-size="page.pageSize"
          :show-size-changer="true"
          :total="page.total"
          @change="onPageChange"
      />
    </template>
  </AstDrawer>
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
} from '@ant-design/icons-vue'
import AstDrawer from '@/components/home/AstDrawer.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import {type AiInstance, aiInstanceApi, type PageResponse} from '@/api/aiInstance.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-instance')

const props = defineProps<{
  open: boolean
  disableTtlEdit?: boolean
  defaultModelType?: string
  fixedModelType?: string

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
  if (type === 'embedding') return t.value.modelType.embedding
  if (type === 'image') return t.value.modelType.image
  if (type === 'voice') return t.value.modelType.voice
  return t.value.modelType.chat
}

const drawerTitle = computed(() => {
  if (props.fixedModelType) return t.value.selector.drawer.selectTypeTitle.replace('{type}', modelTypeLabel(props.fixedModelType))
  return t.value.selector.drawer.title
})

const formatTime = (t2?: string) => {
  if (!t2) return '-'
  return t2.length > 16 ? t2.substring(0, 16) : t2
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
  box-shadow: var(--shadow-card);
}

.instance-item.selected {
  border-color: var(--primary);
  background: var(--primary-hover, color-mix(in srgb, var(--primary) 5%, transparent));
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
  color: var(--text-tertiary);
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
  background: var(--bg-secondary);
  border-radius: 4px;
  color: var(--text-secondary);
}

.model-key-tag {
  font-size: 10px;
  padding: 2px 6px;
  background: var(--bg-secondary);
  border-radius: 4px;
  color: var(--primary);
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
  background: color-mix(in srgb, var(--success) 15%, transparent);
  color: var(--success);
}

.status-badge.disabled {
  background: color-mix(in srgb, var(--error) 15%, transparent);
  color: var(--error);
}
</style>
