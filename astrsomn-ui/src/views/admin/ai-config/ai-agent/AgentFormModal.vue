<template>
  <a-modal
    v-model:open="open"
    :title="null"
    width="960px"
    :footer="null"
    :destroy-on-close="true"
    @cancel="onCancel"
    class="premium-agent-modal"
  >
    <div class="modal-header-gradient">
      <div class="header-content">
        <div class="title-area">
          <div class="icon-box">
            <template v-if="mode === 'create'"><PlusOutlined /></template>
            <template v-else><EditOutlined /></template>
          </div>
          <div class="text-group">
            <h2>{{ mode === 'create' ? '构建新智能体' : '配置智能体' }}</h2>
            <p>定义 Agent 的身份、模型大脑及其专业能力范围</p>
          </div>
        </div>
        <div class="steps-nav">
          <div 
            v-for="(s, index) in stepLabels" 
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
      <div v-show="currentStep === 0" class="step-container">
        <div class="form-section">
          <h3 class="section-headline"><IdcardOutlined /> 基础身份</h3>
          <div class="form-grid">
            <a-form-item label="智能体名称" name="agentName">
              <a-input v-model:value="form.agentName" placeholder="例如：高级文案专家" size="large" />
            </a-form-item>
            <a-form-item label="唯一识别码 (Agent Key)" name="agentKey">
              <a-input v-model:value="form.agentKey" :disabled="mode === 'edit'" placeholder="agent_unique_id" size="large" />
            </a-form-item>
            <a-form-item label="运行状态" name="status">
              <a-segmented v-model:value="form.status" :options="statusOptions" block size="large" />
            </a-form-item>
            <a-form-item label="职能描述" name="description" class="span-2">
              <a-textarea v-model:value="form.description" :auto-size="{ minRows: 3, maxRows: 5 }" placeholder="详述该 Agent 的应用场景和核心能力..." />
            </a-form-item>
          </div>
        </div>
      </div>

      <div v-show="currentStep === 1" class="step-container">
        <div class="form-section">
          <h3 class="section-headline"><DotChartOutlined /> 模型核心与采样控制</h3>
          <div class="form-grid">
            <div class="resource-card-picker span-2">
              <div class="picker-item" @click="openPicker('model')">
                <span class="label">驱动模型 (AI Model)</span>
                <div class="value-display">
                  <span v-if="modelDisplayText" class="active-text">{{ modelDisplayText }}</span>
                  <span v-else class="placeholder">未关联模型，点击选择</span>
                  <RightOutlined />
                </div>
              </div>
              <div class="picker-item" @click="openPicker('prompt')">
                <span class="label">系统提示词 (System Prompt)</span>
                <div class="value-display">
                  <span v-if="promptDisplayText" class="active-text">{{ promptDisplayText }}</span>
                  <span v-else class="placeholder">未关联提示词，点击选择</span>
                  <RightOutlined />
                </div>
              </div>
            </div>

            <div class="parameter-panel span-2">
              <div class="param-row">
                <a-form-item label="Temperature (多样性)" class="param-item">
                  <a-input-number v-model:value="form.temperature" :min="0" :max="2" :step="0.1" />
                  <a-slider v-model:value="form.temperature" :min="0" :max="2" :step="0.1" />
                </a-form-item>
                <a-form-item label="Max Tokens (最大长度)" class="param-item">
                  <a-input-number v-model:value="form.maxTokens" style="width: 100%" />
                </a-form-item>
              </div>
              <div class="param-row three-col">
                <a-form-item label="Top P"><a-input-number v-model:value="form.topP" :min="0" :max="1" :step="0.05" /></a-form-item>
                <a-form-item label="Top K"><a-input-number v-model:value="form.topK" :precision="0" /></a-form-item>
                <a-form-item label="Seed (种子)"><a-input-number v-model:value="form.seed" :precision="0" /></a-form-item>
              </div>
              <div class="param-row">
                <a-form-item label="Presence Penalty"><a-input-number v-model:value="form.presencePenalty" :step="0.1" /></a-form-item>
                <a-form-item label="Frequency Penalty"><a-input-number v-model:value="form.frequencyPenalty" :step="0.1" /></a-form-item>
              </div>
              <a-form-item label="停止序列 (Stop Sequences)" class="span-2">
                <a-input v-model:value="form.stopSequences" placeholder='["\n", "User:"]' />
              </a-form-item>
              <a-form-item label="Config Params (扩展配置 JSON)" class="span-2">
                <a-textarea v-model:value="form.configParams" :auto-size="{ minRows: 2 }" placeholder='{"custom_flag": true}' />
              </a-form-item>
            </div>
          </div>
        </div>
      </div>

      <div v-show="currentStep === 2" class="step-container">
        <div class="form-section">
          <h3 class="section-headline"><ThunderboltOutlined /> 扩展能力与上下文管理</h3>
          
          <div class="cap-group">
            <div class="cap-item">
              <div class="cap-header">
                <span>工具与 MCP 服务</span>
                <a-space>
                  <a-button size="small" @click="openPicker('tool')">选择工具</a-button>
                  <a-button size="small" @click="openPicker('mcp')">选择 MCP</a-button>
                </a-space>
              </div>
              <div class="tag-box">
                <template v-for="t in toolKeyTags" :key="'t'+t">
                  <a-tag closable color="blue" @close="removeKey('tool', t)">{{ t }}</a-tag>
                </template>
                <template v-for="m in mcpKeyTags" :key="'m'+m">
                  <a-tag closable color="purple" @close="removeKey('mcp', m)">{{ m }}</a-tag>
                </template>
                <span v-if="!toolKeyTags.length && !mcpKeyTags.length" class="empty-hint">暂未绑定任何工具或服务</span>
              </div>
            </div>

            <a-form-item label="知识库 Keys" class="mt-16">
              <a-textarea v-model:value="form.knowledgeBaseKeys" :auto-size="{ minRows: 2 }" placeholder="KNOWLEDGE_BASE_ID_1, KNOWLEDGE_BASE_ID_2" />
            </a-form-item>

            <div class="memory-card">
              <div class="form-grid">
                <a-form-item label="记忆模式 (Memory Mode)">
                  <a-select v-model:value="form.memoryMode" :options="memoryModeOptions" />
                </a-form-item>
                <a-form-item label="窗口大小 (Window Size)">
                  <a-input v-model:value="form.memoryWindowSize" placeholder="10" />
                </a-form-item>
                <a-form-item label="输出流模式 (Stream Response)" class="span-2">
                  <div class="switch-row">
                    <span>启用流式输出，提升用户交互体验感</span>
                    <a-switch v-model:checked="form.enableStream" />
                  </div>
                </a-form-item>
              </div>
            </div>
          </div>
        </div>
      </div>
    </a-form>

    <div class="modal-footer-action">
      <div class="footer-left">
        <span class="status-indicator"></span> 所有的更改都将同步至云端
      </div>
      <div class="footer-right">
        <a-button v-if="currentStep > 0" class="btn-flat" @click="currentStep--">上一步</a-button>
        <a-button v-if="currentStep < 2" type="primary" class="btn-next" @click="currentStep++">下一步</a-button>
        <a-button 
          v-else 
          type="primary" 
          class="btn-submit" 
          :loading="confirmLoading" 
          @click="handleOk"
        >
          保存并发布
        </a-button>
      </div>
    </div>
  </a-modal>

  <AgentResourcePickModal v-model:open="picker.model" kind="model" :initial-single="form.modelKey ?? null" @confirm="onResourceConfirm" />
  <AgentResourcePickModal v-model:open="picker.prompt" kind="prompt" :initial-single="form.promptKey || null" @confirm="onResourceConfirm" />
  <AgentResourcePickModal v-model:open="picker.tool" kind="tool" :initial-keys="parsedToolKeys" @confirm="onResourceConfirm" />
  <AgentResourcePickModal v-model:open="picker.mcp" kind="mcp" :initial-keys="parsedMcpKeys" @confirm="onResourceConfirm" />
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { 
  PlusOutlined, EditOutlined, IdcardOutlined, DotChartOutlined, 
  ThunderboltOutlined, RightOutlined 
} from '@ant-design/icons-vue'
import type { FormInstance } from 'ant-design-vue'
import type { AiAgent } from '@/api/aiAgent.ts'
import AgentResourcePickModal from './AgentResourcePickModal.vue'

const props = defineProps<{ mode: 'create' | 'edit', confirmLoading: boolean, initial: AiAgent | null }>()
const emit = defineEmits<{ submit: [payload: AiAgent] }>()
const open = defineModel<boolean>('open', { required: true })

const currentStep = ref(0)
const stepLabels = ['身份设定', '灵魂配置', '能力增强']
const formRef = ref<FormInstance | null>(null)
const promptTitleCache = ref('')

const picker = reactive({ model: false, prompt: false, tool: false, mcp: false })

const statusOptions = [
  { label: '启用', value: 'enabled' },
  { label: '停用', value: 'disabled' }
]

const memoryModeOptions = [
  { label: '不记忆 (NONE)', value: 'NONE' },
  { label: '滑动窗口 (SLIDING_WINDOW)', value: 'SLIDING_WINDOW' },
  { label: '向量搜索 (VECTOR)', value: 'VECTOR' },
  { label: '混合模式 (Hybrid)', value: 'hybrid' }
]

const emptyForm = (): AiAgent => ({
  agentName: '', agentKey: '', description: '', modelKey: undefined,
  promptKey: '', configParams: '', status: 'enabled', knowledgeBaseKeys: '',
  toolKeys: '', mcpKeys: '', memoryMode: 'SLIDING_WINDOW', memoryWindowSize: '10',
  maxTokens: 2048, temperature: 0.7, topP: 1, topK: 0, presencePenalty: 0,
  frequencyPenalty: 0, stopSequences: '', seed: undefined, enableStream: true,
  interfaceClass: ''
})

const form = reactive<AiAgent>(emptyForm())

// 辅助逻辑
const parsedToolKeys = computed(() => parseJsonKeys(form.toolKeys))
const parsedMcpKeys = computed(() => parseJsonKeys(form.mcpKeys))
const toolKeyTags = computed(() => parsedToolKeys.value)
const mcpKeyTags = computed(() => parsedMcpKeys.value)

function parseJsonKeys(raw: any) {
  if (!raw) return []
  try { return typeof raw === 'string' ? JSON.parse(raw) : raw } catch { return String(raw).split(',').filter(Boolean) }
}

const modelDisplayText = computed(() => {
  if (form.modelName) return form.modelName
  if (form.modelKey != null && form.modelKey !== '') return String(form.modelKey)
  return ''
})
const promptDisplayText = computed(() => promptTitleCache.value || form.promptKey)

const goToStep = (i: number) => { if (i < currentStep.value || form.agentName) currentStep.value = i }

const openPicker = (k: keyof typeof picker) => picker[k] = true

function removeKey(which: 'tool' | 'mcp', key: string) {
  const keys = which === 'tool' ? [...parsedToolKeys.value] : [...parsedMcpKeys.value]
  const filtered = keys.filter(k => k !== key)
  const json = JSON.stringify(filtered)
  if (which === 'tool') form.toolKeys = json; else form.mcpKeys = json
}

function onResourceConfirm(payload: any) {
  if (payload.kind === 'model') {
    const mk = payload.modelKey != null && String(payload.modelKey).trim() !== '' ? String(payload.modelKey).trim() : undefined
    form.modelKey = mk
    form.modelName = payload.modelName
  }
  if (payload.kind === 'prompt') { form.promptKey = payload.promptKey; promptTitleCache.value = payload.promptTitle }
  if (payload.kind === 'tool') form.toolKeys = JSON.stringify(payload.keys)
  if (payload.kind === 'mcp') form.mcpKeys = JSON.stringify(payload.keys)
}

const rules = {
  agentName: [{ required: true, message: '请输入名称' }],
  agentKey: [{ required: true, message: '请输入识别码' }],
  modelKey: [{ required: true, message: '请选择模型' }]
}

watch([() => open.value, () => props.initial], ([isOpen, init]) => {
  if (isOpen) {
    currentStep.value = 0
    Object.assign(form, emptyForm(), init || {})
    promptTitleCache.value = ''
  }
}, { immediate: true })

async function handleOk() {
  await formRef.value?.validate()
  emit('submit', { ...form })
}

const onCancel = () => open.value = false
</script>

<style scoped>
/* 核心布局：高级感来自留白与边框处理 */
.premium-agent-modal :deep(.ant-modal-content) {
  padding: 0;
  overflow: hidden;
  border-radius: 20px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.15);
}

/* 顶部渐变标题栏 */
.modal-header-gradient {
  background: linear-gradient(135deg, #f5f7ff 0%, #ffffff 100%);
  padding: 32px 40px;
  border-bottom: 1px solid #edf0f5;
}
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.title-area {
  display: flex;
  gap: 16px;
  align-items: center;
}
.icon-box {
  width: 48px;
  height: 48px;
  background: #0061ff;
  color: white;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  box-shadow: 0 8px 16px rgba(0, 97, 255, 0.2);
}
.text-group h2 { margin: 0; font-size: 22px; font-weight: 700; color: #1a1a1a; }
.text-group p { margin: 4px 0 0; color: #666; font-size: 14px; }

/* 步骤导航条 */
.steps-nav { display: flex; gap: 24px; }
.step-item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  opacity: 0.4;
  transition: all 0.3s;
}
.step-item.active { opacity: 1; }
.step-item.done { opacity: 0.8; color: #0061ff; }
.step-num {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: 1px solid currentColor;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
}

/* 表单容器 */
.professional-form { padding: 32px 40px; min-height: 480px; }
.form-section { animation: slideUp 0.4s ease-out; }
.section-headline {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333;
}

/* 字段网格布局 */
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px 24px;
}
.span-2 { grid-column: span 2; }

/* 选择器卡片 */
.resource-card-picker {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
}
.picker-item {
  flex: 1;
  background: #f8f9fb;
  border: 1px solid #eef0f4;
  padding: 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}
.picker-item:hover { border-color: #0061ff; background: #fff; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
.picker-item .label { display: block; font-size: 12px; color: #888; margin-bottom: 8px; }
.value-display { display: flex; justify-content: space-between; align-items: center; }
.active-text { font-weight: 600; color: #0061ff; }

/* 参数调优面板 */
.parameter-panel {
  background: #fafafa;
  border-radius: 12px;
  padding: 20px;
  border: 1px dashed #d9d9d9;
}
.param-row { display: grid; grid-template-columns: 1.5fr 1fr; gap: 20px; margin-bottom: 12px; }
.param-row.three-col { grid-template-columns: 1fr 1fr 1fr; }
.mt-16 { margin-top: 16px; }

/* 能力组展示 */
.cap-group { display: flex; flex-direction: column; gap: 16px; }
.cap-item { border: 1px solid #f0f0f0; border-radius: 12px; padding: 16px; }
.cap-header { display: flex; justify-content: space-between; margin-bottom: 12px; font-weight: 600; }
.tag-box { min-height: 32px; display: flex; flex-wrap: wrap; gap: 8px; }
.empty-hint { color: #bfbfbf; font-size: 13px; font-style: italic; }

.memory-card { background: #f0f5ff; padding: 20px; border-radius: 12px; margin-top: 16px; }
.switch-row { display: flex; justify-content: space-between; align-items: center; background: white; padding: 12px 16px; border-radius: 8px; }

/* 底部操作 */
.modal-footer-action {
  padding: 20px 40px;
  background: #fff;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.status-indicator { width: 8px; height: 8px; background: #52c41a; border-radius: 50%; display: inline-block; margin-right: 4px; }
.footer-left { color: #999; font-size: 13px; }
.btn-flat { border: none; box-shadow: none; font-weight: 600; }
.btn-next, .btn-submit { border-radius: 10px; padding: 0 24px; height: 40px; font-weight: 600; }

@keyframes slideUp {
  from { opacity: 0; transform: translateY(15px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>