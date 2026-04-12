<template>
  <div class="source-sidebar">
    <div class="sidebar-header">
      <h3><DatabaseOutlined /> 向量源</h3>
      <a-button type="primary" size="small" @click="openCreateSource">
        <template #icon><PlusOutlined /></template>
        新增
      </a-button>
    </div>
    
    <div class="sidebar-search">
      <a-input
        v-model:value="sourceSearchQuery"
        placeholder="搜索向量源..."
        size="small"
        allow-clear
      >
        <template #prefix><SearchOutlined /></template>
      </a-input>
    </div>

    <div class="source-list">
      <a-spin :spinning="sourceLoading">
        <div v-for="source in filteredSources" :key="source.id" class="source-item" :class="{ active: selectedSourceId === source.id }">
          <div class="source-icon" :class="getProviderClass(source.provider)">
            <DatabaseOutlined />
          </div>
          <div class="source-info" @click="selectSource(source.id)">
            <span class="source-name">{{ source.name || '未命名' }}</span>
            <span class="source-provider">{{ getProviderLabel(source.provider) }}</span>
          </div>
          <div class="source-actions">
            <a-tooltip title="编辑">
              <a-button 
                type="text" 
                size="small" 
                @click.stop="openEditSource(source)"
              >
                <EditOutlined />
              </a-button>
            </a-tooltip>
            <a-tooltip title="删除">
              <a-popconfirm
                title="确定删除吗？"
                ok-text="确认"
                cancel-text="取消"
                @confirm="() => handleDeleteSource(source.id)"
              >
                <a-button 
                  type="text" 
                  size="small" 
                  danger
                >
                  <DeleteOutlined />
                </a-button>
              </a-popconfirm>
            </a-tooltip>
          </div>
        </div>
      </a-spin>
    </div>

    <VecSourceFormModal
      v-model:open="sourceModal.open"
      :mode="sourceModal.mode"
      :confirm-loading="sourceModal.submitting"
      :initial="sourceModalInitial"
      @submit="handleSourceSubmit"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, onMounted, watch } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  DatabaseOutlined,
  PlusOutlined,
  SearchOutlined,
  AppstoreOutlined,
  EditOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'
import VecSourceFormModal from '@/views/admin/ai-modules/vec-source/VecSourceFormModal.vue'
import { aiVecSourceApi, type AiVecSource, type PageResponse } from '@/api/aiVecSource'

const props = defineProps<{
  selectedSourceId: number | string | null
  totalStoreCount: number
}>()

const emit = defineEmits<{
  'update:selectedSourceId': [sourceId: number | string | null]
  'refresh': []
}>()

const sourceLoading = ref(false)
const sources = ref<AiVecSource[]>([])
const sourceSearchQuery = ref('')

const sourceModal = reactive({
  open: false,
  mode: 'create' as 'create' | 'edit',
  submitting: false
})

const sourceModalInitial = ref<AiVecSource | null>(null)

const providerLabelMap: Record<string, string> = {
  MILVUS: 'Milvus',
  PINECONE: 'Pinecone',
  DASHVECTOR: 'DashVector',
  CHROMA: 'Chroma',
  QDRANT: 'Qdrant',
  WEAVIATE: 'Weaviate',
  OTHER: '其他'
}

const getProviderLabel = (provider?: string) => {
  const key = String(provider || '').toUpperCase()
  return providerLabelMap[key] || provider || '未知'
}

const getProviderClass = (provider?: string) => {
  const key = String(provider || '').toLowerCase()
  return `provider-${key}`
}

const filteredSources = computed(() => {
  if (!sourceSearchQuery.value) return sources.value
  const search = sourceSearchQuery.value.toLowerCase()
  return sources.value.filter(s => 
    (s.name || '').toLowerCase().includes(search) ||
    (s.provider || '').toLowerCase().includes(search)
  )
})

const fetchSources = async () => {
  sourceLoading.value = true
  try {
    const response = await aiVecSourceApi.queryPage({
      pageNo: 1,
      pageSize: 100,
      param: {
        status: 'enabled'
      }
    })
    sources.value = response.list || []
  } finally {
    sourceLoading.value = false
  }
}

const selectSource = (sourceId: number | string | null) => {
  emit('update:selectedSourceId', sourceId)
}

const openCreateSource = () => {
  sourceModal.mode = 'create'
  sourceModalInitial.value = null
  sourceModal.open = true
}

const openEditSource = async (source: AiVecSource) => {
  sourceModal.mode = 'edit'
  const id = source.id
  if (id == null) return

  const detail = await aiVecSourceApi.detail(id)
  sourceModalInitial.value = detail
  sourceModal.open = true
}

const handleDeleteSource = async (id: number | string) => {
  if (id == null) return
  try {
    const msg = await aiVecSourceApi.delete([id])
    message.success(msg)
    await fetchSources()
    if (props.selectedSourceId === id) {
      emit('update:selectedSourceId', null)
    }
    emit('refresh')
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '删除失败')
  }
}

const handleSourceSubmit = async (form: AiVecSource) => {
  sourceModal.submitting = true
  try {
    let msg: string
    if (sourceModal.mode === 'create') {
      delete (form as { id?: unknown }).id
      msg = await aiVecSourceApi.create(form)
    } else {
      msg = await aiVecSourceApi.update(form)
    }

    message.success(msg)
    sourceModal.open = false
    await fetchSources()
    emit('refresh')
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '保存失败')
  } finally {
    sourceModal.submitting = false
  }
}

onMounted(() => {
  fetchSources()
})

watch(() => props.selectedSourceId, (newValue) => {
  // 当选中的向量源被删除时，确保状态同步
  if (newValue !== null) {
    const exists = sources.value.some(s => s.id === newValue)
    if (!exists) {
      emit('update:selectedSourceId', null)
    }
  }
})
</script>

<style scoped>
.source-sidebar {
  width: 280px;
  background: var(--bg-card);
  border-right: 1px solid var(--border-default);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  height: 100%;
}

.sidebar-header {
  padding: 20px 16px 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--border-default);
}

.sidebar-header h3 {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 8px;
}

.sidebar-search {
  padding: 12px 16px;
  border-bottom: 1px solid var(--border-default);
}

.source-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.source-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 4px;
  position: relative;
}

.source-item:hover {
  background: var(--bg-surface);
}

.source-item.active {
  background: color-mix(in srgb, var(--primary) 10%, transparent);
}

.source-item.active .source-name {
  color: var(--primary);
  font-weight: 600;
}

.source-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: white;
  flex-shrink: 0;
}

.source-icon.all {
  background: linear-gradient(135deg, #6366f1, #4f46e5);
}

.source-icon.provider-milvus {
  background: linear-gradient(135deg, #0ea5e9, #0284c7);
}

.source-icon.provider-chroma {
  background: linear-gradient(135deg, #10b981, #047857);
}

.source-icon.provider-qdrant {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.source-icon.provider-pinecone {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
}

.source-icon.provider-dashvector {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.source-icon.provider-weaviate {
  background: linear-gradient(135deg, #6366f1, #4f46e5);
}

.source-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
  cursor: pointer;
}

.source-name {
  font-weight: 500;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.source-provider,
.source-count {
  font-size: 12px;
  color: var(--text-secondary);
}

.source-actions {
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.2s;
  flex-shrink: 0;
}

.source-item:hover .source-actions {
  opacity: 1;
}

.source-actions :deep(.ant-btn) {
  font-size: 12px;
  width: 24px;
  height: 24px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 1024px) {
  .source-sidebar {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid var(--border-default);
  }

  .source-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    padding: 12px;
  }

  .source-item {
    flex: 0 0 auto;
    padding: 8px 12px;
  }

  .source-actions {
    opacity: 1;
  }
}
</style>
