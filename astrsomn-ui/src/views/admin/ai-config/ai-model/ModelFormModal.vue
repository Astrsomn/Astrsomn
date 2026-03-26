<template>
  <a-modal
    :open="open"
    :title="mode === 'create' ? '新增 AI 模型' : '编辑 AI 模型'"
    width="720px"
    :confirm-loading="confirmLoading"
    :footer="null"
    @update:open="onUpdateOpen"
    @cancel="onCancel"
  >
    <div class="modal-content">
      <a-steps :current="currentStep" size="small" class="form-steps">
        <a-step title="基础信息" />
        <a-step title="服务连接" />
        <a-step title="高级能力" />
      </a-steps>

      <a-form
        ref="formRef"
        :model="form"
        :rules="rules"
        layout="vertical"
        class="stepped-form"
      >
        <div v-show="currentStep === 0" class="step-container">
          <div class="form-grid">
            <a-form-item label="模型名称" name="modelName">
              <a-input v-model:value="form.modelName" placeholder="例如：GPT-4o Mini" />
            </a-form-item>
            
            <a-form-item label="供应商" name="provider">
              <a-select v-model:value="form.provider" :options="providerOptions" placeholder="请选择供应商" />
            </a-form-item>

            <a-form-item label="模型类型" name="modelType">
              <a-radio-group v-model:value="form.modelType" button-style="solid">
                <a-radio-button value="chat">对话 (Chat)</a-radio-button>
                <a-radio-button value="embedding">向量 (Embedding)</a-radio-button>
              </a-radio-group>
            </a-form-item>

            <a-form-item label="状态" name="status">
              <a-switch 
                :checked="form.status === 'enabled'" 
                @change="(val) => form.status = val ? 'enabled' : 'disabled'" 
                checked-children="启用" 
                un-checked-children="禁用" 
              />
            </a-form-item>

            <a-form-item label="模型 Key (唯一标识)" name="modelKey" class="span-2">
              <a-input v-model:value="form.modelKey" placeholder="建议留空，系统将自动生成">
                <template #suffix>
                  <a-tooltip title="清空后保存将自动生成">
                    <close-circle-outlined v-if="form.modelKey" @click="form.modelKey = ''" style="color: rgba(0,0,0,.45)" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </div>
        </div>

        <div v-show="currentStep === 1" class="step-container">
          <a-alert message="安全提示" description="API Key 将被加密存储，请放心填写。" type="info" show-icon style="margin-bottom: 20px" />
          <a-form-item label="API Endpoint (URL)" name="apiUrl">
            <a-input v-model:value="form.apiUrl" placeholder="https://api.openai.com/v1" />
          </a-form-item>
          <div class="form-grid">
            <a-form-item label="API Key" name="apiKey">
              <a-input-password v-model:value="form.apiKey" placeholder="sk-..." />
            </a-form-item>
            <a-form-item label="API Secret" name="apiSecret">
              <a-input-password v-model:value="form.apiSecret" placeholder="可选" />
            </a-form-item>
          </div>
          <a-form-item label="是否设为该供应商默认" name="isDefault">
            <a-checkbox :checked="form.isDefault === 1" @change="e => form.isDefault = e.target.checked ? 1 : 0">
              设为默认模型
            </a-checkbox>
          </a-form-item>
        </div>

        <div v-show="currentStep === 2" class="step-container">
          <a-collapse v-model:activeKey="activeCollapse" ghost>
            <a-collapse-panel key="1" header="核心能力 (Capabilities)">
              <a-checkbox-group v-model:value="capabilitiesSelected" class="capabilities-grid">
                <a-checkbox v-for="opt in capabilityOptions" :key="opt.value" :value="opt.value">
                  {{ opt.label }}
                </a-checkbox>
              </a-checkbox-group>
            </a-collapse-panel>
            
            <a-collapse-panel key="2" header="模型微调参数">
              <div class="form-grid">
                <a-form-item label="响应长度限制" name="responseLimit">
                  <a-input-number v-model:value="form.responseLimit" :min="0" style="width: 100%" />
                </a-form-item>
                <a-form-item label="随机索引" name="randomIndex">
                  <a-input-number v-model:value="form.randomIndex" style="width: 100%" />
                </a-form-item>
              </div>
              <a-form-item label="JSON 配置参数" name="modelParams">
                <a-textarea v-model:value="form.modelParams" :rows="3" placeholder='{"temperature": 0.7}' />
              </a-form-item>
            </a-collapse-panel>
          </a-collapse>
        </div>
      </a-form>

      <div class="step-footer">
        <a-button v-if="currentStep > 0" @click="currentStep--">上一步</a-button>
        <a-button v-if="currentStep < 2" type="primary" @click="nextStep">下一步</a-button>
        <a-button v-else type="primary" :loading="confirmLoading" @click="handleSubmit">立即保存</a-button>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { CloseCircleOutlined } from '@ant-design/icons-vue'
import type { FormInstance } from 'ant-design-vue'
import type { AiModel } from '@/api/aiModel.ts'
import { useDictionary } from '@/locales/dictionary'

const props = withDefaults(
  defineProps<{
    open: boolean
    mode: 'create' | 'edit'
    confirmLoading?: boolean
    initialData?: AiModel | null
    providerOptions: { label: string; value: string }[]
    statusOptions: { label: string; value: string }[]
    isDefaultOptions: { label: string; value: number }[]
    submitHandler: (payload: AiModel) => Promise<void>
  }>(),
  { confirmLoading: false, initialData: null }
)

const emit = defineEmits(['update:open'])

// 状态控制
const currentStep = ref(0)
const activeCollapse = ref(['1'])
const formRef = ref<FormInstance | null>(null)
const capabilitiesDict = useDictionary('ai-model.capabilities')
const capabilityOptions = computed(() => capabilitiesDict.value.options())
const capabilitiesSelected = ref<string[]>([])

const form = reactive<AiModel>({
  modelName: '',
  modelKey: '',
  modelType: 'chat',
  provider: '',
  apiUrl: '',
  status: 'enabled',
  isDefault: 0,
  responseLimit: 4096,
  apiKey: '',
  apiSecret: '',
  modelParams: '',
  capabilities: '',
  randomIndex: 0,
  topVariance: 0
})

const rules = {
  modelName: [{ required: true, message: '模型名称必填' }],
  provider: [{ required: true, message: '请选择供应商' }],
  apiUrl: [{ required: true, message: 'API URL 必填' }],
}

const nextStep = async () => {
  try {
    // 这里可以分步校验，只校验当前步骤的字段
    if (currentStep.value === 0) {
      await formRef.value?.validateFields(['modelName', 'provider'])
    }
    currentStep.value++
  } catch (e) {
    console.log('校验失败')
  }
}

// 逻辑复用 (与原逻辑一致)
function parseCapabilities(raw: any): string[] {
  if (!raw || typeof raw !== 'string') return []
  try {
    const parsed = JSON.parse(raw)
    return Array.isArray(parsed) ? parsed : []
  } catch { return [] }
}

const syncForm = () => {
  currentStep.value = 0
  if (props.mode === 'create' || !props.initialData) {
    Object.assign(form, { id: undefined, modelName: '', modelKey: '', modelType: 'chat', provider: '', apiUrl: '', status: 'enabled', isDefault: 0, apiKey: '', apiSecret: '', modelParams: '', capabilities: '' })
    capabilitiesSelected.value = []
  } else {
    Object.assign(form, props.initialData)
    capabilitiesSelected.value = parseCapabilities(form.capabilities)
  }
}

watch(() => props.open, (v) => v && syncForm())

const handleSubmit = async () => {
  await formRef.value?.validate()
  const payload = { ...form }
  const cap = capabilitiesDict.value
  const merged = cap.sortKeys([...new Set([...capabilitiesSelected.value])])
  payload.capabilities = merged.length > 0 ? JSON.stringify(merged) : ''
  
  await props.submitHandler(payload)
}

const onCancel = () => emit('update:open', false)
const onUpdateOpen = (v: boolean) => emit('update:open', v)
</script>

<style scoped>
.modal-content {
  padding: 10px 0;
}

.form-steps {
  margin-bottom: 32px;
  padding: 0 20px;
}

.step-container {
  min-height: 280px;
  padding: 0 4px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0 24px;
}

.span-2 {
  grid-column: span 2;
}

.capabilities-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  padding: 8px 0;
}

.step-footer {
  margin-top: 32px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 优化动画切换 */
.stepped-form {
  transition: all 0.3s ease;
}

:deep(.ant-form-item-label > label) {
  font-weight: 500;
  color: #555;
}
</style>