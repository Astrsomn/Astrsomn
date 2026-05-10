<template>
  <AstrsomnModal
    :open="open"
    width="80vw"
    :max-width="maxWidth"
    body-height="75vh"
    max-body-height="720px"
    :closable="false"
    main-padding="0"
    wrap-class-name="vec-store-form-wrap"
    @update:open="emit('update:open', $event)"
    @cancel="onCancel"
  >
    <template #header-logo>
      <DatabaseOutlined />
    </template>
    <template #header-title>
      {{ mode === 'create' ? '创建向量存储' : '编辑向量存储' }}
    </template>
    <template #header-subtitle>
      管理向量集合配置，定义维度、距离度量和元数据模式
    </template>
    <template #header-actions>
      <a-button class="header-action-btn header-action-btn-cancel" @click="onCancel">取消</a-button>
      <a-button
        type="primary"
        class="header-action-btn header-action-btn-save"
        :loading="confirmLoading"
        @click="handleOk"
      >
        保存配置
      </a-button>
    </template>

    <div class="vec-store-form-shell">
      <div class="form-scroll-area">
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
                  <a-input-number
                    v-model:value="form.dimension"
                    size="large"
                    min="1"
                    :disabled="true"
                    :placeholder="selectedInstance ? '自动读取中…' : '请先选择实例'"
                    class="dimension-input"
                  />
                  <div v-if="selectedInstance && form.dimension" class="dimension-hint">
                    由模型 <b>{{ selectedInstance.instanceName }}</b> 自动提供（{{ form.dimension }} 维）
                  </div>
                  <div v-else-if="!selectedInstance" class="dimension-hint dimension-hint-warn">
                    选择一个 embedding 实例后将自动填入维度
                  </div>
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
      </div>
    </div>

    <InstanceSelector
      v-model:open="instanceSelectDialogVisible"
      fixed-model-type="embedding"
      @select="handleInstanceSelect"
    />
  </AstrsomnModal>
</template>

<script setup lang="ts">
import { reactive, ref, watch, computed } from 'vue'
import { 
  DatabaseOutlined,
  IdcardOutlined, SafetyCertificateOutlined, 
  SelectOutlined
} from '@ant-design/icons-vue'
import type { FormInstance } from 'ant-design-vue'
import type { AiVecStore } from '@/api/aiVecStore.ts'
import InstanceSelector from '@/views/admin/ai-config/ai-instance/selector/InstanceSelector.vue'
import AstrsomnModal from '@/components/home/AstrsomnModal.vue'
import { aiInstanceApi, type AiInstance } from '@/api/aiInstance.ts'
import { aiModelApi } from '@/api/aiModel'

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
const maxWidth = computed(() => 'min(80vw, 1000px)')

const formRef = ref<FormInstance | null>(null)
const instanceSelectDialogVisible = ref(false)
const selectedInstance = ref<AiInstance | null>(null)

function emptyForm(): AiVecStore {
  return {
    sourceId: props.defaultSourceId || undefined,
    collectionName: '',
    dimension: undefined as unknown as number,
    distanceMetric: 'cosine',
    metadataSchema: '',
    instanceKey: ''
  }
}

const form = reactive<AiVecStore>(emptyForm())

const rules = {
  collectionName: [{ required: true, message: '请输入集合名称' }],
  dimension: [{
    validator: (_rule: unknown, value: unknown) => {
      if (value == null || value === undefined || value === '') {
        return Promise.reject('请先选择一个 embedding 实例以自动获取维度')
      }
      return Promise.resolve()
    }
  }],
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
    if (src.dimension) {
      form.dimension = src.dimension
    }
  }
}

watch(() => [open.value, props.initial, props.defaultSourceId] as const, ([isOpen, initial, defaultSourceId]) => {
  if (isOpen) {
    if (initial && Object.keys(initial).length > 0) {
      assignFromInitial(initial)
    } else {
      Object.assign(form, emptyForm())
      if (defaultSourceId) {
        form.sourceId = defaultSourceId
      }
    }
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

const handleInstanceSelect = async (instance: AiInstance) => {
  instanceSelectDialogVisible.value = false
  selectedInstance.value = instance
  form.instanceKey = instance.instanceKey
  form.dimension = undefined as unknown as number

  if (instance.dimensions) {
    form.dimension = instance.dimensions
    return
  }

  if (instance.modelKey) {
    try {
      const res = await aiModelApi.queryPage({
        pageNo: 1,
        pageSize: 1,
        param: { modelKey: instance.modelKey }
      })
      const hit = res.list?.[0] as Record<string, unknown> | undefined
      const dims = hit?.dimensions ?? hit?.responseLimit
      if (dims && Number(dims) > 0) {
        form.dimension = Number(dims)
      }
    } catch {
      /* ignore — will be caught by validation */
    }
  }

  if (!form.dimension && instance.id) {
    try {
      const detail = await aiInstanceApi.detail(instance.id)
      if (detail.dimensions && detail.dimensions > 0) {
        form.dimension = detail.dimensions
        selectedInstance.value = detail
      }
    } catch {
      /* ignore */
    }
  }
}
</script>

<style scoped>
:global(.vec-store-form-wrap.ant-modal-wrap) {
  display: flex;
  align-items: center;
  justify-content: center;
}

:global(.vec-store-form-wrap .ant-modal) {
  top: 0;
  padding-bottom: 0;
}

.header-action-btn {
  height: 38px;
  min-width: 110px;
  border-radius: var(--radius-md, 8px);
  padding: 0 20px;
  font-weight: 600;
}

:deep(.header-action-btn-cancel.ant-btn-default) {
  color: #475569;
  border-color: #cbd5e1;
  background: #fff;
}

:deep(.header-action-btn-save.ant-btn-primary) {
  box-shadow: none;
}

.vec-store-form-shell {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  overflow: hidden;
}

.form-scroll-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow-y: auto;
  background: #f8fafc;
}

.form-scroll-area::-webkit-scrollbar { width: 4px; }
.form-scroll-area::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 4px; }

.professional-form {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.form-body-container {
  flex: 1;
  overflow-y: auto;
  padding: 24px 40px;
}

.form-section {
  animation: fadeIn 0.25s ease;
}

.section-headline {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #334155;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 24px;
}

.span-2 { grid-column: span 2; }

.dimension-input {
  width: 100%;
}

.dimension-hint {
  margin-top: 6px;
  font-size: 12px;
  color: #059669;
  font-weight: 500;
  line-height: 1.5;
}

.dimension-hint b {
  color: #1677ff;
}

.dimension-hint-warn {
  color: #d97706;
}

.instance-selector {
  display: flex;
  gap: 10px;
  align-items: center;
}

.instance-selector :deep(.ant-input) {
  flex: 1;
}

.select-button {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 4px;
}

.instance-info {
  margin-top: 8px;
  font-size: 12px;
  color: #1677ff;
  font-weight: 600;
}

.json-editor-wrapper {
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  overflow: hidden;
  background: #fafafa;
  transition: 0.3s;
}

.json-editor-wrapper:focus-within {
  border-color: #1677ff;
  box-shadow: 0 0 0 2px rgba(22, 119, 255, 0.1);
}

.mono-text {
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 13px;
  background: transparent;
  border: none;
  padding: 12px;
}

.mono-text:focus { box-shadow: none; }

.modal-footer-info {
  flex-shrink: 0;
  padding: 16px 40px;
  background: #fff;
  border-top: 1px solid #f0f0f0;
  font-size: 12px;
  color: #52c41a;
  display: flex;
  align-items: center;
  gap: 6px;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .span-2 {
    grid-column: span 1;
  }

  .form-body-container {
    padding: 16px 20px;
  }

  .modal-footer-info {
    padding: 12px 20px;
  }

  .instance-selector {
    flex-direction: column;
    align-items: stretch;
  }

  .select-button {
    width: 100%;
    justify-content: center;
  }
}
</style>
