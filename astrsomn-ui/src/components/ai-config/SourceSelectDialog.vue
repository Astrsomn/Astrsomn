<template>
  <a-modal
    v-model:open="open"
    title="选择向量源"
    width="800px"
    :footer="null"
    :destroy-on-close="true"
    @cancel="onCancel"
  >
    <div class="source-select-dialog">
      <div class="dialog-header">
        <AstrsomnSearchPill
          v-model="searchQuery"
          placeholder="搜索向量源名称..."
          button-label="搜索"
          layout="toolbar"
          @search="fetchSources"
        />
        <a-tabs v-model:activeKey="providerFilter" class="provider-tabs">
          <a-tab-pane key="all" tab="全部类型" />
          <a-tab-pane key="MILVUS" tab="Milvus" />
          <a-tab-pane key="CHROMA" tab="Chroma" />
          <a-tab-pane key="QDRANT" tab="Qdrant" />
        </a-tabs>
      </div>

      <div class="table-container">
        <a-table
          :columns="columns"
          :data-source="sources"
          :loading="loading"
          :pagination="{ pageSize: 10, showTotal: (t: number) => `共 ${t} 个向量源` }"
          :scroll="{ y: 400 }"
          :row-selection="rowSelection"
          row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'source'">
              <div class="source-cell">
                <div class="source-icon" :class="getProviderClass(record.provider)">
                  <DatabaseOutlined />
                </div>
                <div class="source-info">
                  <span class="source-name">{{ record.name || '未命名向量源' }}</span>
                  <span class="source-provider">{{ getProviderLabel(record.provider) }}</span>
                </div>
              </div>
            </template>
            <template v-else-if="column.key === 'connection'">
              <span class="mono-text">{{ getConnectionInfo(record) }}</span>
            </template>
            <template v-else-if="column.key === 'status'">
              <span class="status-pill" :class="{ off: record.status !== 'enabled' }">
                {{ record.status === 'enabled' ? '启用' : '禁用' }}
              </span>
            </template>
          </template>
        </a-table>
      </div>

      <div class="dialog-footer">
        <a-button @click="onCancel">取消</a-button>
        <a-button type="primary" :disabled="!selectedSource" @click="handleSelect">
          选择
        </a-button>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { DatabaseOutlined } from '@ant-design/icons-vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import { aiVecSourceApi, type AiVecSource } from '@/api/aiVecSource'

const open = defineModel<boolean>('open', { required: true })
const emit = defineEmits<{ select: [source: AiVecSource] }>()

const loading = ref(false)
const sources = ref<AiVecSource[]>([])
const searchQuery = ref('')
const providerFilter = ref('all')
const selectedKeys = ref<Array<number | string>>([])

const columns = [
  { title: '向量源', key: 'source', width: 280 },
  { title: '连接信息', key: 'connection', width: 250, ellipsis: true },
  { title: '状态', key: 'status', width: 100 }
]

const rowSelection = computed(() => ({
  selectedRowKeys: selectedKeys.value,
  onChange: (keys: Array<number | string>) => {
    selectedKeys.value = keys
  },
  type: 'radio' as const
}))

const selectedSource = computed(() => {
  if (selectedKeys.value.length === 0) return null
  const id = selectedKeys.value[0]
  return sources.value.find(source => source.id === id) || null
})

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

const getConnectionInfo = (record: AiVecSource) => {
  if (record.host) {
    const port = record.port ? `:${record.port}` : ''
    return `${record.host}${port}`
  }
  return '未配置连接地址'
}

const fetchSources = async () => {
  loading.value = true
  try {
    const response = await aiVecSourceApi.queryPage({
      pageNo: 1,
      pageSize: 100,
      param: {
        name: searchQuery.value || undefined,
        provider: providerFilter.value !== 'all' ? providerFilter.value : undefined,
        status: 'enabled'
      }
    })
    sources.value = response.list || []
  } finally {
    loading.value = false
  }
}

const handleSelect = () => {
  const source = selectedSource.value
  if (source) {
    emit('select', source)
    open.value = false
  }
}

const onCancel = () => {
  open.value = false
  selectedKeys.value = []
}

watch(providerFilter, () => {
  fetchSources()
})

onMounted(() => {
  fetchSources()
})
</script>

<style scoped>
.source-select-dialog {
  padding: 10px 0;
}

.dialog-header {
  margin-bottom: 20px;
}

.provider-tabs {
  margin-top: 12px;
}

.table-container {
  margin-bottom: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 16px;
  border-top: 1px solid var(--border-default);
}

.source-cell {
  display: flex;
  align-items: center;
  gap: 12px;
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
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.source-name {
  font-weight: 600;
  color: var(--text-primary);
}

.source-provider {
  font-size: 12px;
  color: var(--text-secondary);
}

.mono-text {
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 12px;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 24px;
  padding: 0 8px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
  color: #166534;
  background: #f0fdf4;
}

.status-pill.off {
  color: #9a3412;
  background: #fff7ed;
}
</style>
