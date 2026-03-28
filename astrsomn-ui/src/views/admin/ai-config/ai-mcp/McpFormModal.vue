<template>
  <a-modal
    v-model:open="open"
    :title="null"
    width="860px"
    :footer="null"
    :destroy-on-close="true"
    @cancel="onCancel"
    class="premium-mcp-modal"
  >
    <div class="modal-header-gradient">
      <div class="header-content">
        <div class="title-area">
          <div class="icon-box">
            <ApiOutlined v-if="form.type === 'SSE'" />
            <ConsoleSqlOutlined v-else />
          </div>
          <div class="text-group">
            <h2>{{ mode === 'create' ? '注册 MCP 服务' : '编辑 MCP 配置' }}</h2>
            <p>连接外部工具能力，扩展智能体的专业技能边界</p>
          </div>
        </div>
        <div class="steps-nav">
          <div 
            v-for="(s, index) in ['基础设定', '协议配置']" 
            :key="index" 
            :class="['step-item', { active: currentStep === index, done: currentStep > index }]"
            @click="goToStep(index)"
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
            <h3 class="section-headline"><IdcardOutlined /> 1. 服务身份与类型</h3>
            
            <a-alert
              v-if="mode === 'create'"
              type="info"
              show-icon
              message="MCP Key 可留空，系统将根据名称自动生成唯一标识。"
              class="custom-alert"
            />

            <div class="form-grid mt-16">
              <a-form-item label="服务展示名称" name="serverName">
                <a-input v-model:value="form.serverName" placeholder="例如：Google Search API" size="large" />
              </a-form-item>

              <a-form-item label="协议类型 (Connection Type)" name="type">
                <a-segmented v-model:value="form.type" :options="mcpTypeOptions" block size="large" />
              </a-form-item>

              <a-form-item label="MCP Key (识别码)" name="mcpKey" class="span-2">
                <a-input v-model:value="form.mcpKey" :disabled="mode === 'edit'" placeholder="留空则服务端自动生成" size="large" />
              </a-form-item>

              <a-form-item label="服务状态" class="span-2">
                <div class="status-card">
                  <div class="info">
                    <span class="t">启用此服务</span>
                    <span class="d">关闭后，所有关联此 MCP 的智能体将无法调用其工具</span>
                  </div>
                  <a-switch v-model:checked="enabledChecked" />
                </div>
              </a-form-item>

              <a-form-item label="服务描述" name="description" class="span-2">
                <a-textarea v-model:value="form.description" :auto-size="{ minRows: 2, maxRows: 3 }" placeholder="详述此 MCP 服务的功能及用途..." />
              </a-form-item>
            </div>
          </div>
        </div>

        <div v-show="currentStep === 1" class="step-container animate-fade">
          <div class="form-section">
            <h3 class="section-headline">
              <LinkOutlined v-if="form.type === 'SSE'" />
              <CodeOutlined v-else />
              2. {{ form.type }} 通讯配置
            </h3>

            <div v-if="form.type === 'SSE'" class="protocol-box">
              <a-form-item label="SSE 服务地址 (Endpoint URL)" name="sseAddress">
                <a-input v-model:value="form.sseAddress" placeholder="https://mcp-server.example.com/sse" size="large">
                  <template #prefix><GlobalOutlined style="color: #bfbfbf" /></template>
                </a-input>
              </a-form-item>

              <a-form-item label="请求头配置 (Headers JSON)" name="requestHeaderConfig">
                <div class="json-editor-wrapper">
                  <a-textarea
                    v-model:value="form.requestHeaderConfig"
                    :auto-size="{ minRows: 4, maxRows: 6 }"
                    placeholder='{"Authorization": "Bearer your_token"}'
                    class="mono-text"
                  />
                </div>
              </a-form-item>
            </div>

            <div v-else class="protocol-box">
              <div class="command-input-row">
                <a-form-item label="执行命令 (Command)" name="command" style="flex: 1">
                  <a-input v-model:value="form.command" placeholder="npx / python / node" size="large">
                    <template #prefix><RightSquareOutlined style="color: #bfbfbf" /></template>
                  </a-input>
                </a-form-item>
              </div>

              <a-form-item label="启动参数 (Arguments)" name="args">
                <a-textarea
                  v-model:value="form.args"
                  :auto-size="{ minRows: 2, maxRows: 3 }"
                  placeholder="请输入启动参数，支持空格分隔或 JSON 数组格式"
                  class="mono-text"
                />
              </a-form-item>

              <a-form-item label="环境变量 (Environment Variables)" name="envVars">
                <div class="json-editor-wrapper">
                  <a-textarea
                    v-model:value="form.envVars"
                    :auto-size="{ minRows: 4, maxRows: 6 }"
                    placeholder='{"API_KEY": "sk-xxx"}'
                    class="mono-text"
                  />
                </div>
              </a-form-item>
            </div>
          </div>
        </div>

      </div>
    </a-form>

    <div class="modal-footer-action">
      <div class="footer-left">
        <SafetyCertificateOutlined /> 通讯链路受系统级保护
      </div>
      <div class="footer-right">
        <a-button v-if="currentStep > 0" class="btn-flat" @click="currentStep--">返回</a-button>
        <a-button v-if="currentStep < 1" type="primary" class="btn-next" @click="nextStep">配置通讯详情</a-button>
        <a-button 
          v-else 
          type="primary" 
          class="btn-submit" 
          :loading="confirmLoading" 
          @click="handleOk"
        >
          保存并同步服务
        </a-button>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { 
  ApiOutlined, ConsoleSqlOutlined, IdcardOutlined, LinkOutlined, 
  CodeOutlined, GlobalOutlined, RightSquareOutlined, SafetyCertificateOutlined 
} from '@ant-design/icons-vue'
import type { FormInstance } from 'ant-design-vue'
import type { AiMcp } from '@/api/aiMcp.ts'

const props = defineProps<{ mode: 'create' | 'edit', confirmLoading: boolean, initial: AiMcp | null }>()
const emit = defineEmits<{ submit: [payload: AiMcp] }>()
const open = defineModel<boolean>('open', { required: true })

const currentStep = ref(0)
const formRef = ref<FormInstance | null>(null)

const mcpTypeOptions = [
  { label: 'SSE (远程)', value: 'SSE' },
  { label: 'STDIO (本地)', value: 'STDIO' },
  { label: 'STEAMABLE', value: 'STEAMABLE' }
]

function emptyForm(): AiMcp {
  return {
    mcpKey: '', serverName: '', description: '', type: 'SSE',
    sseAddress: '', requestHeaderConfig: '', enabled: 1,
    command: '', args: '', envVars: ''
  }
}

const form = reactive<AiMcp>(emptyForm())

const enabledChecked = computed({
  get: () => form.enabled === 1,
  set: (v: boolean) => { form.enabled = v ? 1 : 0 }
})

const rules = {
  serverName: [{ required: true, message: '请输入服务名称' }],
  type: [{ required: true, message: '请选择协议类型' }],
  sseAddress: [{ required: true, message: 'SSE 地址不能为空', trigger: 'blur' }]
}

const nextStep = async () => {
  try {
    await formRef.value?.validateFields(['serverName', 'type'])
    currentStep.value++
  } catch (e) {}
}

const goToStep = (i: number) => { if (i < currentStep.value || form.serverName) currentStep.value = i }

function assignFromInitial(src: AiMcp) {
  Object.assign(form, emptyForm(), src)
  if (form.enabled !== 0 && form.enabled !== 1) {
    form.enabled = form.enabled == null ? 1 : Number(form.enabled) === 0 ? 0 : 1
  }
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
  const payload: AiMcp = { ...form }
  if (props.mode === 'create' && !String(payload.mcpKey || '').trim()) {
    delete payload.mcpKey
  }
  emit('submit', payload)
}

const onCancel = () => { open.value = false }
</script>

<style scoped>
/* 弹窗基础：统一风格 */
.premium-mcp-modal :deep(.ant-modal-content) { padding: 0; border-radius: 20px; overflow: hidden; }

.modal-header-gradient { background: #fff; padding: 32px 40px; border-bottom: 1px solid #f0f2f5; }
.header-content { display: flex; justify-content: space-between; align-items: center; }
.title-area { display: flex; gap: 16px; align-items: center; }
.icon-box {
  width: 48px; height: 48px; background: #5c67f2; color: white; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; font-size: 22px;
  box-shadow: 0 8px 16px rgba(92, 103, 242, 0.2);
}
.text-group h2 { margin: 0; font-size: 20px; font-weight: 700; color: #111; }
.text-group p { margin: 4px 0 0; color: #999; font-size: 13px; }

/* 导航 */
.steps-nav { display: flex; gap: 24px; }
.step-item { display: flex; align-items: center; gap: 8px; color: #ccc; cursor: pointer; transition: 0.3s; }
.step-item.active { color: #5c67f2; font-weight: 600; }
.step-item.done { color: #52c41a; }
.step-num { width: 18px; height: 18px; border-radius: 50%; border: 1.5px solid currentColor; display: flex; align-items: center; justify-content: center; font-size: 10px; font-weight: 800; }

/* 容器高度控制 */
.professional-form { height: 500px; display: flex; flex-direction: column; }
.form-body-container { flex: 1; overflow-y: auto; padding: 24px 40px; }
.form-body-container::-webkit-scrollbar { width: 4px; }
.form-body-container::-webkit-scrollbar-thumb { background: #eee; border-radius: 4px; }

/* 内部组件样式 */
.section-headline { font-size: 15px; font-weight: 600; margin-bottom: 16px; display: flex; align-items: center; gap: 8px; color: #333; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px 24px; }
.span-2 { grid-column: span 2; }
.custom-alert { border-radius: 10px; margin-bottom: 16px; }

.status-card {
  display: flex; justify-content: space-between; align-items: center;
  background: #f8f9fb; padding: 12px 16px; border-radius: 12px; border: 1px solid #eef1f6;
}
.status-card .t { display: block; font-size: 13px; font-weight: 600; }
.status-card .d { font-size: 12px; color: #999; }

/* 协议配置区 */
.protocol-box { animation: fadeIn 0.4s ease; }
.json-editor-wrapper {
  border: 1px solid #d9d9d9; border-radius: 8px; overflow: hidden;
  background: #fafafa; transition: 0.3s;
}
.json-editor-wrapper:focus-within { border-color: #5c67f2; box-shadow: 0 0 0 2px rgba(92, 103, 242, 0.1); }
.mono-text {
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 13px; background: transparent; border: none; padding: 12px;
}
.mono-text:focus { box-shadow: none; }

/* 底部操作 */
.modal-footer-action {
  padding: 16px 40px; background: #fff; border-top: 1px solid #f0f0f0;
  display: flex; justify-content: space-between; align-items: center;
}
.footer-left { font-size: 12px; color: #52c41a; display: flex; align-items: center; gap: 6px; }
.btn-flat { border: none; color: #999; font-weight: 600; }
.btn-next, .btn-submit { border-radius: 8px; font-weight: 600; height: 38px; padding: 0 24px; }

.mt-16 { margin-top: 16px; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
</style>