<template>
  <a-modal
    v-model:open="open"
    :title="null"
    width="840px"
    :footer="null"
    :destroy-on-close="true"
    @cancel="onCancel"
    class="premium-tool-modal"
  >
    <div class="modal-header-gradient">
      <div class="header-content">
        <div class="title-area">
          <div class="icon-box" :class="form.type">
            <BuildOutlined v-if="form.type === 'method'" />
            <Html5Outlined v-else />
          </div>
          <div class="text-group">
            <h2>{{ mode === 'create' ? '构建新增强工具' : '编辑工具配置' }}</h2>
            <p>定义 AI 智能体可调用的外部函数或界面渲染组件</p>
          </div>
        </div>
        <div class="steps-nav">
          <div 
            v-for="(s, index) in ['基础识别', '执行定义']" 
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
            <h3 class="section-headline"><IdcardOutlined /> 1. 工具元数据</h3>
            
            <a-alert
              v-if="mode === 'create'"
              type="info"
              show-icon
              message="Tool Key 是工具的唯一逻辑标识，建议使用下划线命名（如 weather_api）。"
              class="custom-alert"
            />

            <div class="form-grid mt-16">
              <a-form-item label="工具显示名称" name="toolName">
                <a-input v-model:value="form.toolName" placeholder="例如：实时天气查询" size="large" />
              </a-form-item>

              <a-form-item label="实现类型" name="type">
                <a-segmented v-model:value="form.type" :options="toolTypeOptions" block size="large" />
              </a-form-item>

              <a-form-item label="Tool Key (逻辑标识)" name="toolKey" class="span-2">
                <a-input 
                  v-model:value="form.toolKey" 
                  :disabled="mode === 'edit'" 
                  placeholder="留空则由系统自动生成" 
                  size="large"
                />
              </a-form-item>

              <a-form-item label="服务可用性" class="span-2">
                <div class="status-toggle-card">
                  <div class="info">
                    <span class="t">公开调用状态</span>
                    <span class="d">关闭后，该工具将从智能体的可用工具列表中隐藏</span>
                  </div>
                  <a-switch 
                    :checked="form.enableFlag === 'enabled'" 
                    @change="(val) => form.enableFlag = val ? 'enabled' : 'disabled'"
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
            <h3 class="section-headline"><RocketOutlined /> 2. 后端注入与执行</h3>
            
            <div class="impl-hint">
              <div class="hint-title">Spring Context 注入配置</div>
              <p>系统将通过指定的 Bean 名称从 Spring 容器中索引实例，并反射执行目标方法。</p>
            </div>

            <div class="form-grid mt-24">
              <a-form-item label="Spring Bean ID" name="beanName">
                <a-input v-model:value="form.beanName" placeholder="例如：weatherToolService" size="large">
                  <template #prefix><BlockOutlined style="color: #bfbfbf" /></template>
                </a-input>
              </a-form-item>

              <a-form-item label="执行方法名 (Method)" name="methodName">
                <a-input v-model:value="form.methodName" placeholder="例如：getWeather" size="large">
                  <template #prefix><CodeOutlined style="color: #bfbfbf" /></template>
                </a-input>
              </a-form-item>

              <a-form-item label="功能详细描述 (给 AI 看)" name="description" class="span-2">
                <a-textarea 
                  v-model:value="form.description" 
                  :auto-size="{ minRows: 4, maxRows: 6 }" 
                  placeholder="请清晰描述工具的功能及其参数含义，这有助于大模型更准确地进行 Tool Call..." 
                />
              </a-form-item>
            </div>
          </div>
        </div>

      </div>
    </a-form>

    <div class="modal-footer-action">
      <div class="footer-left">
        <SafetyOutlined /> 高安全沙箱运行环境
      </div>
      <div class="footer-right">
        <a-button v-if="currentStep > 0" class="btn-flat" @click="currentStep--">返回</a-button>
        <a-button v-if="currentStep < 1" type="primary" class="btn-next" @click="nextStep">下一步：定义执行</a-button>
        <a-button 
          v-else 
          type="primary" 
          class="btn-submit" 
          :loading="confirmLoading" 
          @click="handleOk"
        >
          注册工具并发布
        </a-button>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { 
  BuildOutlined, Html5Outlined, IdcardOutlined, RocketOutlined, 
  BlockOutlined, CodeOutlined, SafetyOutlined 
} from '@ant-design/icons-vue'
import type { FormInstance } from 'ant-design-vue'
import type { AiTool } from '@/api/aiTool.ts'

const props = defineProps<{ mode: 'create' | 'edit', confirmLoading: boolean, initial: AiTool | null }>()
const emit = defineEmits<{ submit: [payload: AiTool] }>()
const open = defineModel<boolean>('open', { required: true })

const currentStep = ref(0)
const formRef = ref<FormInstance | null>(null)

const toolTypeOptions = [
  { label: 'Method (后端)', value: 'method' },
  { label: 'HTML (组件)', value: 'html' }
]

function emptyForm(): AiTool {
  return {
    toolKey: '', toolName: '', description: '',
    beanName: '', methodName: '', type: 'method', enableFlag: 'enabled'
  }
}

const form = reactive<AiTool>(emptyForm())

const rules = {
  toolName: [{ required: true, message: '请输入工具名称' }],
  type: [{ required: true, message: '请选择实现类型' }],
  beanName: [{ required: true, message: 'Bean ID 不能为空' }],
  methodName: [{ required: true, message: '方法名不能为空' }]
}

const nextStep = async () => {
  try {
    await formRef.value?.validateFields(['toolName', 'type'])
    currentStep.value++
  } catch (e) {}
}

function assignFromInitial(src: AiTool) {
  Object.assign(form, emptyForm(), src)
}

watch(() => [open.value, props.initial] as const, ([isOpen, initial]) => {
  if (isOpen) {
    currentStep.value = 0
    if (initial && Object.keys(initial).length > 0) assignFromInitial(initial)
    else Object.assign(form, emptyForm())
  }
})

async function handleOk() {
  await formRef.value?.validate()
  const payload: AiTool = { ...form }
  if (props.mode === 'create' && !String(payload.toolKey || '').trim()) {
    delete payload.toolKey
  }
  emit('submit', payload)
}

const onCancel = () => { open.value = false }
</script>

<style scoped>
/* 延续 Premium 风格 */
.premium-tool-modal :deep(.ant-modal-content) { padding: 0; border-radius: 20px; overflow: hidden; }

.modal-header-gradient { background: #fff; padding: 32px 40px; border-bottom: 1px solid #f0f2f5; }
.header-content { display: flex; justify-content: space-between; align-items: center; }
.title-area { display: flex; gap: 16px; align-items: center; }
.icon-box {
  width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center;
  justify-content: center; font-size: 22px; color: white; transition: 0.3s;
}
.icon-box.method { background: linear-gradient(135deg, #1890ff, #36cfc9); }
.icon-box.html { background: linear-gradient(135deg, #fa8c16, #ffd666); }

.text-group h2 { margin: 0; font-size: 20px; font-weight: 700; color: #111; }
.text-group p { margin: 4px 0 0; color: #999; font-size: 13px; }

/* 导航 */
.steps-nav { display: flex; gap: 24px; }
.step-item { display: flex; align-items: center; gap: 8px; color: #ccc; }
.step-item.active { color: #1890ff; font-weight: 600; }
.step-item.done { color: #52c41a; }
.step-num { width: 18px; height: 18px; border-radius: 50%; border: 1.5px solid currentColor; display: flex; align-items: center; justify-content: center; font-size: 10px; font-weight: 800; }

/* 核心布局 */
.professional-form { height: 480px; display: flex; flex-direction: column; }
.form-body-container { flex: 1; overflow-y: auto; padding: 24px 40px; }

.section-headline { font-size: 15px; font-weight: 600; margin-bottom: 16px; display: flex; align-items: center; gap: 8px; color: #333; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px 24px; }
.span-2 { grid-column: span 2; }

/* 状态卡片 */
.status-toggle-card {
  display: flex; justify-content: space-between; align-items: center;
  background: #f8f9fb; padding: 12px 16px; border-radius: 12px; border: 1px solid #eef1f6;
}
.status-toggle-card .t { display: block; font-size: 13px; font-weight: 600; }
.status-toggle-card .d { font-size: 12px; color: #999; }

/* 第二步专用提示 */
.impl-hint {
  background: #f0f5ff; border: 1px solid #adc6ff; padding: 16px; border-radius: 12px;
}
.hint-title { font-size: 13px; font-weight: 700; color: #1d39c4; margin-bottom: 4px; }
.impl-hint p { font-size: 12px; color: #2f54eb; margin: 0; opacity: 0.8; }

/* 底部 */
.modal-footer-action {
  padding: 16px 40px; background: #fff; border-top: 1px solid #f0f0f0;
  display: flex; justify-content: space-between; align-items: center;
}
.footer-left { font-size: 12px; color: #8c8c8c; display: flex; align-items: center; gap: 6px; }
.btn-flat { border: none; color: #999; font-weight: 600; }
.btn-next, .btn-submit { border-radius: 8px; font-weight: 600; height: 38px; padding: 0 24px; }

.mt-16 { margin-top: 16px; }
.mt-24 { margin-top: 24px; }
.animate-fade { animation: slideIn 0.3s ease-out; }
@keyframes slideIn { from { opacity: 0; transform: translateX(10px); } to { opacity: 1; transform: translateX(0); } }
</style>