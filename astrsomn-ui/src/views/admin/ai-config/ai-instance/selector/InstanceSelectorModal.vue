<template>
  <a-modal
      v-model:open="open"
      :destroy-on-close="true"
      :footer="null"
      :title="t.selector.modal.title"
      width="800px"
      @cancel="onCancel"
  >
    <div class="instance-select-dialog">
      <div class="dialog-header">
        <AstSearchInput
            v-model="searchQuery"
            layout="toolbar"
            :placeholder="t.selector.modal.searchPlaceholder"
            @search="fetchInstances"
        />
        <a-tabs v-model:activeKey="typeFilter" class="model-type-tabs">
          <a-tab-pane key="all" :tab="t.selector.modal.tab.all"/>
          <a-tab-pane key="chat" :tab="t.selector.modal.tab.chat"/>
          <a-tab-pane key="embedding" :tab="t.selector.modal.tab.embedding"/>
          <a-tab-pane key="image" :tab="t.selector.modal.tab.image"/>
        </a-tabs>
      </div>

      <div class="table-container">
        <a-table
            :columns="columns"
            :data-source="instances"
            :loading="loading"
            :pagination="{ pageSize: 10, showTotal: (total: number) => t.selector.modal.totalInstances.replace('{n}', String(total)) }"
            :row-selection="rowSelection"
            :scroll="{ y: 400 }"
            row-key="instanceKey"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'modelType'">
              <div class="model-type-cell">
                <div :class="record.modelType" class="model-type-icon">
                  <template v-if="record.modelType === 'chat'">
                    <MessageOutlined/>
                  </template>
                  <template v-else-if="record.modelType === 'embedding'">
                    <PartitionOutlined/>
                  </template>
                  <template v-else-if="record.modelType === 'image'">
                    <PictureOutlined/>
                  </template>
                  <template v-else>
                    <MessageOutlined/>
                  </template>
                </div>
                <span class="model-type-label">{{ modelTypeLabel(record.modelType) }}</span>
              </div>
            </template>
            <template v-else-if="column.key === 'dimensions'">
              <span>{{ record.dimensions || '-' }}</span>
            </template>
          </template>
        </a-table>
      </div>

      <div class="dialog-footer">
        <a-button @click="onCancel">{{ t.selector.modal.cancel }}</a-button>
        <a-button :disabled="!selectedInstance" type="primary" @click="handleSelect">
          {{ t.selector.modal.select }}
        </a-button>
      </div>
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
import {computed, onMounted, ref, watch} from 'vue'
import {MessageOutlined, PartitionOutlined, PictureOutlined} from '@ant-design/icons-vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import {type AiInstance, aiInstanceApi} from '@/api/aiInstance.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-instance')

const open = defineModel<boolean>('open', {required: true})
const emit = defineEmits<{ select: [instance: AiInstance] }>()
const props = defineProps<{
  defaultType?: string
}>()

const loading = ref(false)
const instances = ref<AiInstance[]>([])
const searchQuery = ref('')
const typeFilter = ref(props.defaultType || 'all')
const selectedKeys = ref<string[]>([])


watch(() => open.value, (isOpen) => {
  if (isOpen && props.defaultType) {
    typeFilter.value = props.defaultType
  }
})

const columns = computed(() => [
  {title: t.value.selector.modal.column.instanceName, dataIndex: 'instanceName', key: 'instanceName', width: 200},
  {title: t.value.selector.modal.column.instanceKey, dataIndex: 'instanceKey', key: 'instanceKey', width: 200, ellipsis: true},
  {title: t.value.selector.modal.column.type, dataIndex: 'modelType', key: 'modelType', width: 100},
  {title: t.value.selector.modal.column.dimensions, dataIndex: 'dimensions', key: 'dimensions', width: 100},
  {title: t.value.selector.modal.column.modelKey, dataIndex: 'modelKey', key: 'modelKey', ellipsis: true}
])

const rowSelection = computed(() => ({
  selectedRowKeys: selectedKeys.value,
  onChange: (keys: string[]) => {
    selectedKeys.value = keys
  },
  type: 'radio' as const
}))

const selectedInstance = computed(() => {
  if (selectedKeys.value.length === 0) return null
  const key = selectedKeys.value[0]
  return instances.value.find(instance => instance.instanceKey === key) || null
})

const modelTypeLabel = (type?: string) => {
  if (type === 'embedding') return t.value.modelType.embedding
  if (type === 'image') return t.value.modelType.image
  return t.value.modelType.chat
}

const fetchInstances = async () => {
  loading.value = true
  try {
    const response = await aiInstanceApi.queryPage({
      pageNo: 1,
      pageSize: 100,
      param: {
        instanceName: searchQuery.value || undefined,
        status: 'enabled'
      }
    })
    instances.value = response.list || []
  } finally {
    loading.value = false
  }
}

const handleSelect = () => {
  const instance = selectedInstance.value
  if (instance) {
    emit('select', instance)
    open.value = false
  }
}

const onCancel = () => {
  open.value = false
  selectedKeys.value = []
}

onMounted(() => {
  fetchInstances()
})
</script>

<style scoped>
.instance-select-dialog {
  padding: 10px 0;
}

.dialog-header {
  margin-bottom: 20px;
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

.model-type-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.model-type-icon {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: white;
}

.model-type-icon.chat {
  background: linear-gradient(135deg, #6366f1, #2563eb);
}

.model-type-icon.embedding {
  background: linear-gradient(135deg, #10b981, #047857);
}

.model-type-icon.image {
  background: linear-gradient(135deg, #fb923c, #c2410c);
}

.model-type-label {
  font-size: 12px;
  color: var(--text-secondary);
}
</style>
