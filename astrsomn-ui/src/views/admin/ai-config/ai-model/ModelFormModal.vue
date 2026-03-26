<template>
  <a-modal
    :open="open"
    :title="null"
    width="820px"
    :footer="null"
    :destroy-on-close="true"
    @cancel="onCancel"
    class="premium-model-modal"
  >
    <div class="modal-header-gradient">
      <div class="header-content">
        <div class="title-area">
          <div class="icon-box" :class="form.modelType">
            <template v-if="form.modelType === 'chat'"><MessageOutlined /></template>
            <template v-else><PartitionOutlined /></template>
          </div>
          <div class="text-group">
            <h2>{{ mode === 'create' ? '注册新 AI 模型' : '编辑模型配置' }}</h2>
            <p>配置模型供应源、API 端点及核心推理能力</p>
          </div>
        </div>
        <div class="steps-nav">
          <div 
            v-for="(s, index) in ['基础', '连接', '能力']" 
            :key="index" 
            :class="['step-item', { active: currentStep === index, done: currentStep > index }]"
          >
            <span class="step-num">{{ index + 1 }}</span>
            <span class="step-text">{{ s }}</span>
          </div>
        </div>
      </div>
    </div>

    <a-form
      ref="formRef"
      :model="form"
      :rules="rules"
      layout="vertical"
      class="professional-form"
    >
      <div v-show="currentStep === 0" class="step-container animate-fade">
        <div class="form-section">
          <h3 class="section-headline"><IdcardOutlined /> 模型身份识别</h3>
          <div class="form-grid">
            <a-form-item label="模型名称" name="modelName">
              <a-input v-model:value="form.modelName" placeholder="例如：Claude 3.5 Sonnet" size="large" />
            </a-form-item>
            
            <a-form-item label="供应商 (Provider)" name="provider">
              <a-select v-model:value="form.provider" :options="providerOptions" placeholder="选择供应商" size="large" />
            </a-form-item>

            <a-form-item label="模型类型" name="modelType">
              <a-radio-group v-model:value="form.modelType" button-style="solid" class="full-width-radio">
                <a-radio-button value="chat">对话 (Chat)</a-radio-button>
                <a-radio-button value="embedding">向量 (Embedding)</a-radio-button>
              </a-radio-group>
            </a-form-item>

            <a-form-item label="服务状态" name="status">
              <div class="status-switch-card">
                <span class="status-label">启用此模型实例</span>
                <a-switch 
                  :checked="form.status === 'enabled'" 
                  @change="(val) => form.status = val ? 'enabled' : 'disabled'"
                  checked-children="ON" 
                  un-checked-children="OFF"
                />
              </div>
            </a-form-item>

            <a-form-item label="模型唯一识别码 (Model Key)" name="modelKey" class="span-2">
              <a-input v-model:value="form.modelKey" placeholder="建议留空，保存时将根据名称自动生成" size="large">
                <template #suffix>
                  <a-tooltip title="重置 Key">
                    <ReloadOutlined v-if="form.modelKey" @click="form.modelKey = ''" class="input-action-icon" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </div>
        </div>
      </div>

      <div v-show="currentStep === 1" class="step-container animate-fade">
        <div class="form-section">
          <h3 class="section-headline"><CloudServerOutlined /> 接口连接设定</h3>
          
          <div class="security-alert">
            <SafetyCertificateOutlined class="alert-icon" />
            <div class="alert-content">
              <h4>安全加密存储</h4>
              <p>所有的凭据均经过 AES-256 位加密处理，确保您的数据资产安全。</p>
            </div>
          </div>

          <a-form-item label="API Endpoint (接口地址)" name="apiUrl">
            <a-input v-model:value="form.apiUrl" placeholder="https://api.openai.com/v1" size="large">
              <template #prefix><GlobalOutlined style="color: #bfbfbf" /></template>
            </a-input>
          </a-form-item>

          <div class="form-grid">
            <a-form-item label="API Key" name="apiKey">
              <a-input-password v-model:value="form.apiKey" placeholder="sk-..." size="large" />
            </a-form-item>
            <a-form-item label="API Secret (可选)" name="apiSecret">
              <a-input-password v-model:value="form.apiSecret" placeholder="特定供应商必填" size="large" />
            </a-form-item>
          </div>

          <div class="default-setting-card">
            <div class="card-info">
              <div class="card-title">设为默认模型</div>
              <div class="card-desc">当智能体未配置特定模型时，系统将默认调用此项</div>
            </div>
            <a-checkbox :checked="form.isDefault === 1" @change="e => form.isDefault = e.target.checked ? 1 : 0" />
          </div>
        </div>
      </div>

      <div v-show="currentStep === 2" class="step-container animate-fade">
        <div class="form-section">
          <h3 class="section-headline"><ThunderboltOutlined /> 模型核心能力定义</h3>
          
          <div class="capability-selector-box">
            <div class="box-header">
              <span>选择该模型支持的功能特性</span>
              <span class="count-badge">{{ capabilitiesSelected.length }} 已选</span>
            </div>
            <div class="tags-overflow-container">
               <div 
                 v-for="opt in capabilityOptions" 
                 :key="opt.value"
                 :class="['cap-tag-item', { checked: capabilitiesSelected.includes(opt.value) }]"
                 @click="toggleCapability(opt.value)"
               >
                 <div class="check-icon"><CheckOutlined /></div>
                 {{ opt.label }}
               </div>
            </div>
          </div>

          <h3 class="section-headline mt-32"><ControlOutlined /> 运行参数预设</h3>
          <div class="parameter-compact-row">
            <div class="param-input-group">
              <span class="p-label">响应 Token 限制</span>
              <a-input-number v-model:value="form.responseLimit" :min="0" placeholder="4096" />
            </div>
            <div class="param-input-group">
              <span class="p-label">负载均衡优先级</span>
              <a-input-number v-model:value="form.randomIndex" placeholder="0" />
            </div>
            <div class="param-input-group">
              <span class="p-label">Top Variance</span>
              <a-input-number v-model:value="form.topVariance" :step="0.01" placeholder="0.00" />
            </div>
          </div>
        </div>
      </div>
    </a-form>

    <div class="modal-footer-action">
      <div class="footer-left">
        <span class="secure-tag"><LockOutlined /> 加密传输模式</span>
      </div>
      <div class="footer-right">
        <a-button v-if="currentStep > 0" class="btn-flat" @click="currentStep--">返回</a-button>
        <a-button v-if="currentStep < 2" type="primary" class="btn-next" @click="nextStep">下一步</a-button>
        <a-button 
          v-else 
          type="primary" 
          class="btn-submit" 
          :loading="confirmLoading" 
          @click="handleSubmit"
        >
          保存并启用模型
        </a-button>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { 
  IdcardOutlined, CloudServerOutlined, ReloadOutlined, MessageOutlined, 
  PartitionOutlined, SafetyCertificateOutlined, GlobalOutlined, 
  LockOutlined, ThunderboltOutlined, CheckOutlined, ControlOutlined
} from '@ant-design/icons-vue'
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
    submitHandler: (payload: AiModel) => Promise<void>
  }>(),
  { confirmLoading: false, initialData: null }
)

const emit = defineEmits(['update:open'])

const currentStep = ref(0)
const formRef = ref<FormInstance | null>(null)
const capabilitiesDict = useDictionary('ai-model.capabilities')
const capabilityOptions = computed(() => capabilitiesDict.value.options())
const capabilitiesSelected = ref<string[]>([])

const form = reactive<AiModel>({
  modelName: '', modelKey: '', modelType: 'chat', provider: '',
  apiUrl: '', status: 'enabled', isDefault: 0, responseLimit: 4096,
  apiKey: '', apiSecret: '', modelParams: '', capabilities: '',
  randomIndex: 0, topVariance: 0
})

const rules = {
  modelName: [{ required: true, message: '模型名称必填' }],
  provider: [{ required: true, message: '请选择供应商' }],
  apiUrl: [{ required: true, message: '接口地址必填' }],
}

// 切换能力选项
const toggleCapability = (val: string) => {
  const index = capabilitiesSelected.value.indexOf(val)
  if (index > -1) capabilitiesSelected.value.splice(index, 1)
  else capabilitiesSelected.value.push(val)
}

const nextStep = async () => {
  try {
    if (currentStep.value === 0) await formRef.value?.validateFields(['modelName', 'provider'])
    if (currentStep.value === 1) await formRef.value?.validateFields(['apiUrl'])
    currentStep.value++
  } catch (e) {}
}

const syncForm = () => {
  currentStep.value = 0
  if (props.mode === 'create' || !props.initialData) {
    Object.assign(form, { modelName: '', modelKey: '', modelType: 'chat', provider: '', apiUrl: '', status: 'enabled', isDefault: 0, apiKey: '', apiSecret: '', modelParams: '', capabilities: '', randomIndex: 0, topVariance: 0 })
    capabilitiesSelected.value = []
  } else {
    Object.assign(form, props.initialData)
    try {
      const parsed = JSON.parse(form.capabilities || '[]')
      capabilitiesSelected.value = Array.isArray(parsed) ? parsed : []
    } catch { capabilitiesSelected.value = [] }
  }
}

watch(() => props.open, (v) => v && syncForm())

const handleSubmit = async () => {
  await formRef.value?.validate()
  const payload = { ...form }
  // 保持原字典排序逻辑
  const merged = capabilitiesDict.value.sortKeys([...capabilitiesSelected.value])
  payload.capabilities = merged.length > 0 ? JSON.stringify(merged) : ''
  await props.submitHandler(payload)
}

const onCancel = () => emit('update:open', false)
</script>

<style scoped>
/* 核心弹窗样式 */
.premium-model-modal :deep(.ant-modal-content) {
  padding: 0; border-radius: 20px; overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.15);
}

/* 顶部 Header */
.modal-header-gradient {
  background: linear-gradient(135deg, #f0f7ff 0%, #ffffff 100%);
  padding: 32px 40px; border-bottom: 1px solid #edf2f9;
}
.header-content { display: flex; justify-content: space-between; align-items: center; }
.title-area { display: flex; gap: 16px; align-items: center; }
.icon-box {
  width: 52px; height: 52px; border-radius: 16px; display: flex; align-items: center;
  justify-content: center; font-size: 24px; color: white;
}
.icon-box.chat { background: #0061ff; box-shadow: 0 8px 16px rgba(0, 97, 255, 0.2); }
.icon-box.embedding { background: #7c4dff; box-shadow: 0 8px 16px rgba(124, 77, 255, 0.2); }
.text-group h2 { margin: 0; font-size: 22px; font-weight: 700; color: #1a1a1a; }
.text-group p { margin: 4px 0 0; color: #666; font-size: 14px; }

/* 导航 */
.steps-nav { display: flex; gap: 24px; }
.step-item { display: flex; align-items: center; gap: 8px; opacity: 0.4; transition: 0.3s; }
.step-item.active { opacity: 1; }
.step-item.done { color: #0061ff; opacity: 0.8; }
.step-num {
  width: 22px; height: 22px; border-radius: 50%; border: 1.5px solid currentColor;
  display: flex; align-items: center; justify-content: center; font-size: 11px; font-weight: bold;
}

/* 内容布局 */
.professional-form { padding: 32px 40px; min-height: 400px; }
.section-headline { font-size: 15px; font-weight: 600; margin-bottom: 20px; display: flex; align-items: center; gap: 8px; color: #333; }
.form-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px 24px; }
.span-2 { grid-column: span 2; }

/* 状态开关卡片 */
.status-switch-card {
  display: flex; justify-content: space-between; align-items: center;
  background: #f8f9fb; padding: 0 16px; border-radius: 10px; border: 1px solid #eef1f6; height: 40px;
}
.status-label { font-size: 13px; color: #888; }

/* 安全提示 */
.security-alert {
  background: #fffbe6; border: 1px solid #ffe58f; padding: 14px 18px;
  border-radius: 12px; display: flex; gap: 12px; margin-bottom: 24px;
}
.alert-icon { font-size: 22px; color: #faad14; }
.alert-content h4 { margin: 0; font-size: 14px; font-weight: 600; }
.alert-content p { margin: 2px 0 0; font-size: 12px; color: #856404; opacity: 0.8; }

/* 能力多选优化区 */
.capability-selector-box {
  background: #fafbfc; border: 1px solid #f0f0f0; border-radius: 16px; padding: 20px;
}
.box-header {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 16px; font-size: 13px; color: #666; font-weight: 500;
}
.count-badge { background: #0061ff; color: white; padding: 2px 8px; border-radius: 10px; font-size: 11px; }

.tags-overflow-container {
  display: flex; flex-wrap: wrap; gap: 10px; max-height: 220px; overflow-y: auto; padding: 2px;
}

/* 个性化 Tag 样式 */
.cap-tag-item {
  padding: 8px 16px; background: white; border: 1px solid #e8e8e8; border-radius: 10px;
  cursor: pointer; transition: all 0.2s; font-size: 13px; color: #555;
  display: flex; align-items: center; gap: 6px; user-select: none;
}
.cap-tag-item:hover { border-color: #0061ff; color: #0061ff; background: #f0f7ff; }
.cap-tag-item.checked {
  background: #0061ff; border-color: #0061ff; color: white;
  box-shadow: 0 4px 10px rgba(0, 97, 255, 0.2);
}
.check-icon { 
  width: 0; overflow: hidden; transition: width 0.2s; font-size: 10px;
}
.cap-tag-item.checked .check-icon { width: 14px; }

/* 参数紧凑行 */
.parameter-compact-row {
  display: flex; gap: 16px; background: #f4f6f8; padding: 16px; border-radius: 12px;
}
.param-input-group { flex: 1; display: flex; flex-direction: column; gap: 8px; }
.p-label { font-size: 12px; color: #888; font-weight: 500; }

/* 底部操作 */
.modal-footer-action {
  padding: 20px 40px; background: #fff; border-top: 1px solid #f0f0f0;
  display: flex; justify-content: space-between; align-items: center;
}
.secure-tag { font-size: 12px; color: #52c41a; display: flex; align-items: center; gap: 4px; }
.btn-flat { border: none; box-shadow: none; font-weight: 600; color: #888; }
.btn-next, .btn-submit { border-radius: 10px; height: 40px; padding: 0 24px; font-weight: 600; }

.full-width-radio :deep(.ant-radio-button-wrapper) { width: 50%; text-align: center; }
.mt-32 { margin-top: 32px; }

.animate-fade { animation: slideIn 0.3s ease-out; }
@keyframes slideIn { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }

.input-action-icon:hover { color: #ff4d4f; cursor: pointer; }
</style>