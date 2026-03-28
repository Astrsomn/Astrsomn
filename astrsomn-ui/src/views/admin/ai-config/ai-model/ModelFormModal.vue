<template>
  <a-modal
      :open="open"
      :title="null"
      width="860px"
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
      <div class="form-body-container">
        <div v-show="currentStep === 0" class="step-container animate-fade">
          <div class="form-section">
            <h3 class="section-headline"><IdcardOutlined /> 1. 模型身份识别</h3>
            <div class="form-grid">
              <a-form-item label="供应商 (Provider)" name="provider">
                <a-select v-model:value="form.provider" :options="providerOptions" placeholder="请选择服务商" size="large" />
              </a-form-item>

              <a-form-item label="模型类型" name="modelType">
                <a-segmented v-model:value="form.modelType" :options="[{label:'对话模型', value:'chat'}, {label:'向量模型', value:'embedding'}]" block size="large" />
              </a-form-item>

              <a-form-item label="模型显示名称" name="modelName" class="span-2">
                <a-input v-model:value="form.modelName" placeholder="例如：GPT-4o 或 Claude 3.5 Sonnet" size="large" />
              </a-form-item>

              <a-form-item label="模型 Key (内部识别码)" name="modelKey" class="span-2">
                <a-input v-model:value="form.modelKey" placeholder="建议留空，系统将根据名称自动生成" size="large">
                  <template #suffix>
                    <a-tooltip title="重置识别码">
                      <ReloadOutlined v-if="form.modelKey" @click="form.modelKey = ''" class="input-action-icon" />
                    </a-tooltip>
                  </template>
                </a-input>
              </a-form-item>

              <a-form-item label="服务实例状态" class="span-2">
                <div class="status-toggle-card">
                  <div class="info">
                    <span class="t">启用此模型</span>
                    <span class="d">控制该实例是否进入系统的调度资源池</span>
                  </div>
                  <a-switch
                      :checked="form.status === 'enabled'"
                      @change="(val) => form.status = val ? 'enabled' : 'disabled'"
                      checked-children="已启用"
                      un-checked-children="已禁用"
                  />
                </div>
              </a-form-item>
            </div>
          </div>
        </div>

        <div v-show="currentStep === 1" class="step-container animate-fade">
          <div class="form-section">
            <h3 class="section-headline"><CloudServerOutlined /> 2. 接口通讯链路</h3>

            <div class="security-info-bar">
              <SafetyCertificateFilled class="icon" />
              <span>凭据将进行 AES-256 位加密存储，仅在请求时调用。</span>
            </div>

            <a-form-item label="API Endpoint (接口地址)" name="apiUrl" class="mt-16">
              <a-input v-model:value="form.apiUrl" placeholder="https://api.openai.com/v1" size="large">
                <template #prefix><GlobalOutlined style="color: #bfbfbf" /></template>
              </a-input>
            </a-form-item>

            <div class="form-grid">
              <a-form-item label="API Key" name="apiKey">
                <a-input-password v-model:value="form.apiKey" placeholder="sk-..." size="large" />
              </a-form-item>
              <a-form-item label="API Secret (可选)" name="apiSecret">
                <a-input-password v-model:value="form.apiSecret" placeholder="特定通道需要" size="large" />
              </a-form-item>
            </div>

            <div class="setting-card-item">
              <div class="text">
                <div class="label">设为系统默认模型</div>
                <div class="desc">若 Agent 未指定模型，将自动降级使用此默认选项</div>
              </div>
              <a-checkbox :checked="form.isDefault === 1" @change="e => form.isDefault = e.target.checked ? 1 : 0" />
            </div>
          </div>
        </div>

        <div v-show="currentStep === 2" class="step-container animate-fade">
          <div class="form-section">
            <h3 class="section-headline"><ThunderboltOutlined /> 3. 能力矩阵与推理预设</h3>

            <div class="capability-wrapper">
              <div class="cap-header">
                <span>支持的功能特性 (Capabilities)</span>
                <span class="badge">{{ capabilitiesSelected.length }}</span>
              </div>
              <div class="cap-tag-grid">
                <div
                    v-for="opt in capabilityOptions"
                    :key="opt.value"
                    :class="['custom-cap-tag', { active: capabilitiesSelected.includes(opt.value) }]"
                    @click="toggleCapability(opt.value)"
                >
                  <CheckCircleFilled v-if="capabilitiesSelected.includes(opt.value)" />
                  {{ opt.label }}
                </div>
              </div>
            </div>

            <div class="runtime-params-box">
              <div class="box-title"><ControlOutlined /> 运行约束预设</div>
              <div class="param-grid">
                <div class="param-item">
                  <span class="pl">响应 Token 限制</span>
                  <a-input-number v-model:value="form.responseLimit" :min="0" placeholder="4096" block />
                </div>
                <div class="param-item">
                  <span class="pl">调度优先级 (Weight)</span>
                  <a-input-number v-model:value="form.randomIndex" placeholder="0" block />
                </div>
                <div class="param-item">
                  <span class="pl">Top Variance</span>
                  <a-input-number v-model:value="form.topVariance" :step="0.01" placeholder="0.00" block />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </a-form>

    <div class="modal-footer-action">
      <div class="footer-left">
        <LockOutlined /> 加密传输环境
      </div>
      <div class="footer-right">
        <a-button v-if="currentStep > 0" class="btn-flat" @click="currentStep--">返回上一步</a-button>
        <a-button v-if="currentStep < 2" type="primary" class="btn-next" @click="nextStep">下一步</a-button>
        <a-button
            v-else
            type="primary"
            class="btn-submit"
            :loading="confirmLoading"
            @click="handleSubmit"
        >
          确认并保存模型
        </a-button>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import {
  IdcardOutlined, CloudServerOutlined, ReloadOutlined, MessageOutlined,
  PartitionOutlined, SafetyCertificateFilled, GlobalOutlined,
  LockOutlined, ThunderboltOutlined, CheckCircleFilled, ControlOutlined
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
  modelName: [{ required: true, message: '请输入模型名称' }],
  provider: [{ required: true, message: '请选择供应商' }],
  apiUrl: [{ required: true, message: '接口地址必填' }],
}

const toggleCapability = (val: string) => {
  const index = capabilitiesSelected.value.indexOf(val)
  if (index > -1) capabilitiesSelected.value.splice(index, 1)
  else capabilitiesSelected.value.push(val)
}

const nextStep = async () => {
  try {
    if (currentStep.value === 0) await formRef.value?.validateFields(['provider', 'modelName'])
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
  const merged = capabilitiesDict.value.sortKeys([...capabilitiesSelected.value])
  payload.capabilities = merged.length > 0 ? JSON.stringify(merged) : ''
  await props.submitHandler(payload)
}

const onCancel = () => emit('update:open', false)
</script>

<style scoped>
/* 弹窗核心：统一视觉语言 */
.premium-model-modal :deep(.ant-modal-content) {
  padding: 0; border-radius: 20px; overflow: hidden;
}

.modal-header-gradient {
  background: #fff; padding: 32px 40px; border-bottom: 1px solid #f0f2f5;
}
.header-content { display: flex; justify-content: space-between; align-items: center; }
.title-area { display: flex; gap: 16px; align-items: center; }
.icon-box {
  width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center;
  justify-content: center; font-size: 22px; color: white;
}
.icon-box.chat { background: linear-gradient(135deg, #0061ff, #60efff); }
.icon-box.embedding { background: linear-gradient(135deg, #7c4dff, #f94dff); }
.text-group h2 { margin: 0; font-size: 20px; font-weight: 700; color: #111; }
.text-group p { margin: 4px 0 0; color: #999; font-size: 13px; }

/* 步骤导航微调 */
.steps-nav { display: flex; gap: 20px; }
.step-item { display: flex; align-items: center; gap: 8px; color: #ccc; transition: 0.3s; font-size: 14px; }
.step-item.active { color: #111; font-weight: 600; }
.step-item.done { color: #0061ff; }
.step-num {
  width: 18px; height: 18px; border-radius: 50%; border: 1.5px solid currentColor;
  display: flex; align-items: center; justify-content: center; font-size: 10px; font-weight: 800;
}

/* 核心高度控制区 */
.professional-form { height: 500px; display: flex; flex-direction: column; }
.form-body-container { flex: 1; overflow-y: auto; padding: 24px 40px; }
.form-body-container::-webkit-scrollbar { width: 4px; }
.form-body-container::-webkit-scrollbar-thumb { background: #eee; border-radius: 4px; }

/* 分段布局 */
.section-headline { font-size: 14px; font-weight: 600; margin-bottom: 16px; display: flex; align-items: center; gap: 8px; color: #444; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px 24px; }
.span-2 { grid-column: span 2; }

/* 开关卡片 */
.status-toggle-card {
  display: flex; justify-content: space-between; align-items: center;
  background: #f8f9fb; padding: 12px 16px; border-radius: 12px; border: 1px solid #eef1f6;
}
.status-toggle-card .t { display: block; font-size: 13px; font-weight: 600; }
.status-toggle-card .d { font-size: 12px; color: #999; }

/* 安全条 */
.security-info-bar {
  background: #f6ffed; border: 1px solid #b7eb8f; padding: 10px 16px;
  border-radius: 8px; display: flex; align-items: center; gap: 8px; font-size: 12px; color: #389e0d;
}

/* 默认勾选卡片 */
.setting-card-item {
  margin-top: 16px; display: flex; justify-content: space-between; align-items: center;
  padding: 16px; background: #fff; border: 1px solid #eee; border-radius: 12px;
}
.setting-card-item .label { font-weight: 600; font-size: 13px; }
.setting-card-item .desc { font-size: 12px; color: #999; }

/* 能力矩阵布局 */
.capability-wrapper { background: #fafafa; border-radius: 16px; padding: 20px; border: 1px solid #f0f0f0; }
.cap-header { display: flex; justify-content: space-between; margin-bottom: 12px; font-size: 13px; font-weight: 600; }
.cap-header .badge { background: #0061ff; color: #fff; padding: 0 8px; border-radius: 10px; font-size: 11px; }
.cap-tag-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(120px, 1fr)); gap: 10px; }
.custom-cap-tag {
  padding: 8px 12px; background: #fff; border: 1px solid #e8e8e8; border-radius: 8px;
  cursor: pointer; transition: 0.2s; font-size: 12px; color: #666; display: flex; align-items: center; gap: 6px;
}
.custom-cap-tag:hover { border-color: #0061ff; color: #0061ff; }
.custom-cap-tag.active {
  background: #e6f0ff; border-color: #0061ff; color: #0061ff; font-weight: 600;
}

/* 运行参数卡片 */
.runtime-params-box { margin-top: 24px; background: #fff; border: 1px solid #eee; border-radius: 16px; padding: 16px; }
.box-title { font-size: 13px; font-weight: 600; margin-bottom: 16px; color: #111; display: flex; align-items: center; gap: 6px; }
.param-grid { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 16px; }
.param-item { display: flex; flex-direction: column; gap: 6px; }
.pl { font-size: 11px; color: #999; }

/* 底部操作 */
.modal-footer-action {
  padding: 16px 40px; background: #fff; border-top: 1px solid #f0f0f0;
  display: flex; justify-content: space-between; align-items: center;
}
.footer-left { font-size: 12px; color: #52c41a; display: flex; align-items: center; gap: 4px; }
.btn-flat { border: none; color: #999; }
.btn-next, .btn-submit { border-radius: 8px; font-weight: 600; padding: 0 24px; height: 38px; }

.mt-16 { margin-top: 16px; }
.animate-fade { animation: fadeIn 0.3s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(5px); } to { opacity: 1; transform: translateY(0); } }

.input-action-icon:hover { color: #ff4d4f; cursor: pointer; }
</style>