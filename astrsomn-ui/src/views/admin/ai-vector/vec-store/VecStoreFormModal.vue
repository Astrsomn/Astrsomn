<template>
  <AstModal
      :closable="false"
      :max-width="maxWidth"
      :open="open"
      body-height="75vh"
      main-padding="0"
      max-body-height="720px"
      width="80vw"
      wrap-class-name="vec-store-form-wrap"
      @cancel="onCancel"
      @update:open="emit('update:open', $event)"
  >
    <template #header-logo>
      <DatabaseOutlined/>
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
          :loading="confirmLoading"
          class="header-action-btn header-action-btn-save"
          type="primary"
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
            class="professional-form"
            layout="vertical"
        >
          <div class="form-body-container">
            <div class="form-section">
              <h3 class="section-headline">
                <IdcardOutlined/>
                基本配置
              </h3>

              <div class="form-grid">
                <a-form-item label="集合名称" name="collectionName">
                  <a-input v-model:value="form.collectionName" placeholder="例如：document_embeddings" size="large"/>
                </a-form-item>

                <a-form-item label="向量维度" name="dimension">
                  <a-select
                      v-model:value="form.dimension"
                      :filter-option="filterDimensionOption"
                      :options="dimensionOptions"
                      allow-clear
                      placeholder="选择或输入维度"
                      show-search
                      size="large"
                      style="width: 100%"
                  />
                  <div v-if="form.modelKey && form.dimension" class="dimension-hint">
                    由模型 <b>{{ form.modelKey }}</b> 推荐（{{ form.dimension }} 维），可手动覆盖
                  </div>
                </a-form-item>

                <a-form-item label="距离度量" name="distanceMetric">
                  <a-select v-model:value="form.distanceMetric" size="large">
                    <a-select-option value="cosine">余弦相似度 (cosine)</a-select-option>
                    <a-select-option value="euclidean">欧氏距离 (euclidean)</a-select-option>
                    <a-select-option value="manhattan">曼哈顿距离 (manhattan)</a-select-option>
                  </a-select>
                </a-form-item>

                <a-form-item label="Embedding 模型" name="modelKey">
                  <a-select
                      v-model:value="form.modelKey"
                      :loading="modelLoading"
                      :options="modelOptions"
                      allow-clear
                      placeholder="选择 Embedding 模型"
                      show-search
                      size="large"
                      style="width: 100%"
                      @change="handleModelChange"
                  />
                  <div v-if="selectedModelInfo" class="instance-info">
                    已选择：{{ selectedModelInfo }}
                  </div>
                </a-form-item>

                <a-form-item class="span-2" label="元数据模式 (JSON)" name="metadataSchema">
                  <div class="json-editor-wrapper">
                    <a-textarea
                        v-model:value="form.metadataSchema"
                        :auto-size="{ minRows: 4, maxRows: 6 }"
                        class="mono-text"
                        placeholder='{"type": "object", "properties": {"title": {"type": "string"}}}'
                    />
                  </div>
                </a-form-item>
              </div>
            </div>

            <div class="form-section">
              <h3 class="section-headline">
                <ScissorOutlined/>
                切片与向量化配置
              </h3>

              <div class="form-grid">
                <a-form-item label="切片策略" name="chunkStrategy">
                  <a-select v-model:value="form.chunkStrategy" size="large">
                    <a-select-option value="RECURSIVE">递归分割（默认）</a-select-option>
                    <a-select-option value="FIXED_SIZE">固定大小</a-select-option>
                    <a-select-option value="PARAGRAPH">按段落</a-select-option>
                    <a-select-option value="SENTENCE">按句子</a-select-option>
                  </a-select>
                </a-form-item>

                <a-form-item label="切片大小（字符数）" name="chunkSize">
                  <a-input-number
                      v-model:value="form.chunkSize"
                      :max="4000"
                      :min="100"
                      placeholder="默认 800"
                      size="large"
                      style="width: 100%"
                  />
                </a-form-item>

                <a-form-item label="重叠范围（字符数）" name="chunkOverlap">
                  <a-input-number
                      v-model:value="form.chunkOverlap"
                      :max="500"
                      :min="0"
                      placeholder="默认 100"
                      size="large"
                      style="width: 100%"
                  />
                </a-form-item>

                <a-form-item label="稠密权重" name="denseWeight">
                  <a-slider
                      v-model:value="form.denseWeight"
                      :max="1"
                      :min="0"
                      :step="0.05"
                      :tooltip-formatter="(v: any) => Number(v).toFixed(2)"
                  />
                  <div class="dimension-hint">混合检索时稠密向量的权重（0~1），当前暂存值</div>
                </a-form-item>

                <a-form-item class="span-2" label="指令前缀" name="instructionPrefix">
                  <a-input
                      v-model:value="form.instructionPrefix"
                      placeholder="可选，如 BGE 模型的 query 前缀：为这个句子生成表示以用于检索中文文档"
                      size="large"
                  />
                  <div class="dimension-hint">嵌入时添加到文本前的指令，部分模型（如 BGE、Instructor）需要</div>
                </a-form-item>
              </div>
            </div>
          </div>
        </a-form>

        <div class="modal-footer-info">
          <SafetyCertificateOutlined/>
          数据安全加密存储
        </div>
      </div>
    </div>
  </AstModal>
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import {DatabaseOutlined, IdcardOutlined, SafetyCertificateOutlined, ScissorOutlined} from '@ant-design/icons-vue'
import type {FormInstance} from 'ant-design-vue'
import type {AiVecStore} from '@/api/aiVecStore.ts'
import AstModal from '@/components/home/AstModal.vue'
import {type AiModel, aiModelApi} from '@/api/aiModel'

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
const open = defineModel<boolean>('open', {required: true})
const maxWidth = computed(() => 'min(80vw, 1000px)')

const formRef = ref<FormInstance | null>(null)
const modelLoading = ref(false)
const modelOptions = ref<Array<{ value: string; label: string }>>([])
const allModels = ref<AiModel[]>([])

const dimensionOptions = [
  {value: 256, label: '256 — 轻量级，适合简单检索'},
  {value: 512, label: '512 — 紧凑型，平衡性能与精度'},
  {value: 768, label: '768 — 常用基线（BGE / text-embedding-ada）'},
  {value: 1024, label: '1024 — 中高维度，语义表达更丰富'},
  {value: 1536, label: '1536 — 主流高维（OpenAI text-embedding-3）'},
  {value: 2048, label: '2048 — 高精度场景'},
  {value: 3072, label: '3072 — 超高精度，适合专业语义匹配'},
  {value: 4096, label: '4096 — 最大常用档位'},
  {value: 8192, label: '8192 — 极限维度，计算成本极高'}
]

function filterDimensionOption(input: string, option: { value: number; label: string }) {
  return String(option.value).includes(input) || option.label.toLowerCase().includes(input.toLowerCase())
}

function emptyForm(): AiVecStore {
  return {
    sourceId: props.defaultSourceId || undefined,
    collectionName: '',
    dimension: undefined as unknown as number,
    distanceMetric: 'cosine',
    metadataSchema: '',
    modelKey: '',
    chunkStrategy: 'RECURSIVE',
    chunkSize: 800,
    chunkOverlap: 100,
    denseWeight: 1,
    instructionPrefix: ''
  }
}

const form = reactive<AiVecStore>(emptyForm())

const rules = {
  collectionName: [{required: true, message: '请输入集合名称'}],
  dimension: [{required: true, message: '请选择或输入向量维度'}],
  distanceMetric: [{required: true, message: '请选择距离度量'}],
  modelKey: [{required: true, message: '请选择 Embedding 模型'}]
}

const selectedModelInfo = computed(() => {
  if (!form.modelKey) return ''
  const m = allModels.value.find(x => x.modelKey === form.modelKey)
  return m ? `${m.modelName || m.modelKey} (${m.modelKey})` : form.modelKey
})

function assignFromInitial(src: AiVecStore) {
  Object.assign(form, emptyForm(), src)
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
    fetchModelOptions()
  }
})

async function fetchModelOptions() {
  modelLoading.value = true
  try {
    const resp = await aiModelApi.queryPage({
      pageNo: 1,
      pageSize: 200,
      param: {}
    })
    const list = resp.list || []
    allModels.value = list
    modelOptions.value = list
        .filter((x) => String(x.modelType || '').toLowerCase().includes('embedding'))
        .map((x) => ({
          value: String(x.modelKey || ''),
          label: `${x.modelName || x.modelKey} (${x.modelKey})`
        }))
        .filter((x) => x.value)
  } finally {
    modelLoading.value = false
  }
}

async function handleModelChange(modelKey: string) {
  if (!modelKey) {
    return
  }
  const m = allModels.value.find(x => x.modelKey === modelKey)
  if (m?.responseLimit && m.responseLimit > 0) {
    form.dimension = m.responseLimit
    return
  }
  try {
    const detail = await aiModelApi.queryPage({
      pageNo: 1,
      pageSize: 1,
      param: {modelKey}
    })
    const hit = detail.list?.[0] as Record<string, unknown> | undefined
    const dims = hit?.dimensions ?? hit?.responseLimit
    if (dims && Number(dims) > 0) {
      form.dimension = Number(dims)
    }
  } catch {
    /* ignore — user can set manually */
  }
}

async function handleOk() {
  await formRef.value?.validate()
  const payload: AiVecStore = {...form}
  emit('submit', payload)
}

const onCancel = () => {
  open.value = false
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

.form-scroll-area::-webkit-scrollbar {
  width: 4px;
}

.form-scroll-area::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 4px;
}

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

.span-2 {
  grid-column: span 2;
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

.mono-text:focus {
  box-shadow: none;
}

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
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
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
}
</style>
