<template>
  <AstrsomnModal
    :open="open"
    :width="width"
    :footer="null"
    :destroy-on-close="true"
    :closable="true"
    @update:open="emit('update:open', $event)"
    @cancel="onCancel"
  >
    <template #header-logo>
      <div class="icon-box">
        <DatabaseOutlined />
      </div>
    </template>
    <template #header-title>
      {{ mode === 'create' ? '创建向量存储' : '编辑向量存储' }}
    </template>
    <template #header-subtitle>
      管理向量集合配置，定义维度、距离度量和元数据模式
    </template>
    <template #header-actions>
      <div class="header-actions">
        <a-button class="btn-flat" @click="onCancel">
          <CloseOutlined />
          取消
        </a-button>
        <a-button 
          type="primary" 
          class="btn-submit" 
          :loading="confirmLoading" 
          @click="handleOk"
        >
          <SaveOutlined />
          保存配置
        </a-button>
      </div>
    </template>

    <a-form
      ref="formRef"
      :model="form"
      :rules="rules"
      layout="vertical"
      class="professional-form"
    >
      <div class="form-body-container">
        <div class="form-section">
          <h3 class="section-headline"><IdcardOutlined /> 基本配置</h3>

          <div class="form-grid">
            <a-form-item label="集合名称" name="collectionName">
              <a-input v-model:value="form.collectionName" placeholder="例如：document_embeddings" size="large" />
            </a-form-item>

            <a-form-item label="向量维度" name="dimension">
              <a-input-number v-model:value="form.dimension" placeholder="例如：1536" size="large" min="1" />
            </a-form-item>

            <a-form-item label="距离度量" name="distanceMetric">
              <a-select v-model:value="form.distanceMetric" size="large">
                <a-select-option value="cosine">余弦相似度 (cosine)</a-select-option>
                <a-select-option value="euclidean">欧氏距离 (euclidean)</a-select-option>
                <a-select-option value="manhattan">曼哈顿距离 (manhattan)</a-select-option>
              </a-select>
            </a-form-item>

            <a-form-item label="实例 Key" name="instanceKey">
              <div class="instance-selector">
                <a-input 
                  v-model:value="form.instanceKey" 
                  placeholder="选择 AI 实例" 
                  size="large" 
                  readonly
                />
                <a-button 
                  type="primary" 
                  size="large" 
                  class="select-button"
                  @click="openInstanceSelectDialog"
                >
                  <SelectOutlined />
                  选择
                </a-button>
              </div>
              <div v-if="selectedInstanceName" class="instance-info">
                已选择：{{ selectedInstanceName }}
              </div>
            </a-form-item>

            <a-form-item label="元数据模式 (JSON)" name="metadataSchema" class="span-2">
              <div class="json-editor-wrapper">
                <a-textarea
                  v-model:value="form.metadataSchema"
                  :auto-size="{ minRows: 4, maxRows: 6 }"
                  placeholder='{"type": "object", "properties": {"title": {"type": "string"}}}'
                  class="mono-text"
                />
              </div>
            </a-form-item>
          </div>
        </div>
      </div>
    </a-form>

    <div class="modal-footer-info">
      <SafetyCertificateOutlined /> 数据安全加密存储
    </div>

    <InstanceSelectDialog
      v-model:open="instanceSelectDialogVisible"
      :default-type="'embedding'"
      @select="handleInstanceSelect"
    />
  </AstrsomnModal>
</template>

<script setup lang="ts">
import { reactive, ref, watch, computed } from 'vue'
import { 
  IdcardOutlined, SafetyCertificateOutlined, 
  SaveOutlined, CloseOutlined, SelectOutlined
} from '@ant-design/icons-vue'
import type { FormInstance } from 'ant-design-vue'
import type { AiVecStore } from '@/api/aiVecStore.ts'
import InstanceSelectDialog from '@/components/ai-config/InstanceSelectDialog.vue'
import AstrsomnModal from '@/components/home/AstrsomnModal.vue'
import type { AiInstance } from '@/api/aiInstance.ts'

const props = defineProps<{ 
  mode: 'create' | 'edit', 
  confirmLoading: boolean, 
  initial: AiVecStore | null,
  defaultSourceId?: number | string | null
}>()
const emit = defineEmits<{ 
  submit: [payload: AiVecStore],
  'update:open': [value: boolean]
}>()
const open = defineModel<boolean>('open', { required: true })
const width = '860px'

const formRef = ref<FormInstance | null>(null)
const instanceSelectDialogVisible = ref(false)
const selectedInstance = ref<AiInstance | null>(null)

function emptyForm(): AiVecStore {
  return {
    sourceId: props.defaultSourceId || undefined,
    collectionName: '',
    dimension: 1536,
    distanceMetric: 'cosine',
    metadataSchema: '',
    instanceKey: ''
  }
}

const form = reactive<AiVecStore>(emptyForm())

const rules = {
  collectionName: [{ required: true, message: '请输入集合名称' }],
  dimension: [{ required: true, message: '请输入向量维度' }],
  distanceMetric: [{ required: true, message: '请选择距离度量' }]
}

const selectedInstanceName = computed(() => {
  return selectedInstance.value?.instanceName || ''
})

function assignFromInitial(src: AiVecStore) {
  Object.assign(form, emptyForm(), src)
  if (src.instanceKey) {
    selectedInstance.value = {
      instanceKey: src.instanceKey,
      instanceName: src.instanceName
    } as AiInstance
  }
}

watch(() => [open.value, props.initial, props.defaultSourceId] as const, ([isOpen, initial, defaultSourceId]) => {
  if (isOpen) {
    if (initial && Object.keys(initial).length > 0) assignFromInitial(initial)
    else {
      Object.assign(form, emptyForm())
      if (defaultSourceId) {
        form.sourceId = defaultSourceId
      }
    }
    selectedInstance.value = null
  }
})

async function handleOk() {
  await formRef.value?.validate()
  const payload: AiVecStore = { ...form }
  emit('submit', payload)
}

const onCancel = () => { 
  open.value = false 
  instanceSelectDialogVisible.value = false
}

const openInstanceSelectDialog = () => {
  instanceSelectDialogVisible.value = true
}

const handleInstanceSelect = (instance: AiInstance) => {
  selectedInstance.value = instance
  form.instanceKey = instance.instanceKey
  if (instance.modelType === 'embedding' && instance.dimensions) {
    form.dimension = instance.dimensions
  }
}
</script>

<style scoped>
.icon-box {
  width: 48px; height: 48px; background: #1677ff; color: white; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; font-size: 22px;
  box-shadow: 0 8px 16px rgba(22, 119, 255, 0.2);
}

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.professional-form { height: 500px; display: flex; flex-direction: column; }
.form-body-container { flex: 1; overflow-y: auto; padding: 24px 40px; }
.form-body-container::-webkit-scrollbar { width: 4px; }
.form-body-container::-webkit-scrollbar-thumb { background: #eee; border-radius: 4px; }

.section-headline { font-size: 15px; font-weight: 600; margin-bottom: 16px; display: flex; align-items: center; gap: 8px; color: #333; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px 24px; }
.span-2 { grid-column: span 2; }

.source-selector,
.instance-selector {
  display: flex;
  gap: 10px;
  align-items: center;
}

.source-selector :deep(.ant-input),
.instance-selector :deep(.ant-input) {
  flex: 1;
}

.select-button {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 4px;
}

.source-info {
  margin-top: 8px;
  font-size: 12px;
  color: #1677ff;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.source-badge {
  width: 20px;
  height: 20px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  color: white;
}

.source-badge.provider-milvus {
  background: linear-gradient(135deg, #0ea5e9, #0284c7);
}

.source-badge.provider-chroma {
  background: linear-gradient(135deg, #10b981, #047857);
}

.source-badge.provider-qdrant {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.source-badge.provider-pinecone {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
}

.source-badge.provider-dashvector {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.source-badge.provider-weaviate {
  background: linear-gradient(135deg, #6366f1, #4f46e5);
}

.provider-label {
  color: #6b7280;
  font-weight: 400;
}

.instance-info {
  margin-top: 8px;
  font-size: 12px;
  color: #1677ff;
  font-weight: 600;
}

.json-editor-wrapper {
  border: 1px solid #d9d9d9; border-radius: 8px; overflow: hidden;
  background: #fafafa; transition: 0.3s;
}
.json-editor-wrapper:focus-within { border-color: #1677ff; box-shadow: 0 0 0 2px rgba(22, 119, 255, 0.1); }
.mono-text {
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 13px; background: transparent; border: none; padding: 12px;
}
.mono-text:focus { box-shadow: none; }

.modal-footer-info {
  padding: 16px 40px; background: #fff; border-top: 1px solid #f0f0f0;
  font-size: 12px; color: #52c41a; display: flex; align-items: center; gap: 6px;
}

.btn-flat { border: none; color: #999; font-weight: 600; display: flex; align-items: center; gap: 4px; }
.btn-submit { border-radius: 8px; font-weight: 600; height: 38px; padding: 0 24px; display: flex; align-items: center; gap: 4px; }

.mt-16 { margin-top: 16px; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
</style>
