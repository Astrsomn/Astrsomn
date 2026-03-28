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
      <div class="form-scroll-container">
        <div v-show="currentStep === 0" class="step-container">
          <div class="form-section">
            <h3 class="section-headline"><IdcardOutlined /> 1. 身份定义与基础表现</h3>
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
              <a-form-item label="流式响应 (Stream)" name="enableStream">
                <div class="compact-switch-card">
                   <span class="hint">启用实时打字机效果</span>
                   <a-switch v-model:checked="form.enableStream" />
                </div>
              </a-form-item>
              <a-form-item label="职能描述" name="description" class="span-2">
                <a-textarea v-model:value="form.description" :auto-size="{ minRows: 2, maxRows: 2 }" placeholder="简述该 Agent 的核心能力..." />
              </a-form-item>
              
              <div class="memory-setting-row span-2">
                <a-form-item label="记忆模式">
                  <a-select v-model:value="form.memoryMode" :options="memoryModeOptions" />
                </a-form-item>
                <a-form-item label="窗口大小">
                  <a-input-number v-model:value="form.memoryWindowSize" style="width: 100%" placeholder="10" />
                </a-form-item>
              </div>
            </div>
          </div>
        </div>

        <div v-show="currentStep === 1" class="step-container">
          <div class="form-section">
            <h3 class="section-headline"><DotChartOutlined /> 2. 思考引擎与大脑配置</h3>
            <div class="resource-selection-group">
              <div class="picker-card" @click="openPicker('model')">
                <div class="card-icon model-icon"><DeploymentUnitOutlined /></div>
                <div class="card-info">
                  <span class="label">驱动模型 (AI Model)</span>
                  <span class="val">{{ modelDisplayText || '点击关联模型' }}</span>
                </div>
                <RightOutlined class="arrow" />
              </div>
              <div class="picker-card" @click="openPicker('prompt')">
                <div class="card-icon prompt-icon"><MessageOutlined /></div>
                <div class="card-info">
                  <span class="label">系统提示词 (System Prompt)</span>
                  <span class="val">{{ promptDisplayText || '点击配置指令' }}</span>
                </div>
                <RightOutlined class="arrow" />
              </div>
            </div>

            <div class="parameter-compact-grid">
              <a-form-item label="Temperature (多样性)">
                <div class="slider-with-input">
                  <a-slider v-model:value="form.temperature" :min="0" :max="2" :step="0.1" style="flex: 1" />
                  <a-input-number v-model:value="form.temperature" :min="0" :max="2" :step="0.1" size="small" />
                </div>
              </a-form-item>
              <a-form-item label="Max Tokens">
                <a-input-number v-model:value="form.maxTokens" style="width: 100%" />
              </a-form-item>
            </div>

            <a-collapse ghost expand-icon-position="right" class="advanced-params-collapse">
              <a-collapse-panel key="1" header="高级采样参数 (TopP / Seed / Penalty)">
                <div class="form-grid">
                  <a-form-item label="Top P"><a-input-number v-model:value="form.topP" :min="0" :max="1" :step="0.05" block/></a-form-item>
                  <a-form-item label="Top K"><a-input-number v-model:value="form.topK" :precision="0" /></a-form-item>
                  <a-form-item label="Presence Penalty"><a-input-number v-model:value="form.presencePenalty" :step="0.1" /></a-form-item>
                  <a-form-item label="Frequency Penalty"><a-input-number v-model:value="form.frequencyPenalty" :step="0.1" /></a-form-item>
                  <a-form-item label="Seed (种子码)" class="span-2"><a-input-number v-model:value="form.seed" style="width:100%" /></a-form-item>
                  <a-form-item label="停止序列 (Stop Sequences)" class="span-2">
                    <a-input v-model:value="form.stopSequences" placeholder='["\n", "User:"]' />
                  </a-form-item>
                </div>
              </a-collapse-panel>
            </a-collapse>
          </div>
        </div>

        <div v-show="currentStep === 2" class="step-container">
          <div class="form-section">
            <h3 class="section-headline"><ThunderboltOutlined /> 3. 技能扩展与知识增强</h3>
            
            <div class="capability-card">
              <div class="cap-header">
                <span class="title">工具与 MCP 服务</span>
                <a-space>
                  <a-button type="link" size="small" @click="openPicker('tool')">添加工具</a-button>
                  <a-button type="link" size="small" @click="openPicker('mcp')">添加 MCP</a-button>
                </a-space>
              </div>
              <div class="tag-render-area">
                <template v-for="t in toolKeyTags" :key="'t'+t">
                  <a-tag closable color="blue" @close="removeKey('tool', t)">{{ t }}</a-tag>
                </template>
                <template v-for="m in mcpKeyTags" :key="'m'+m">
                  <a-tag closable color="purple" @close="removeKey('mcp', m)">{{ m }}</a-tag>
                </template>
                <div v-if="!toolKeyTags.length && !mcpKeyTags.length" class="empty-placeholder">未绑定外部能力</div>
              </div>
            </div>

            <a-form-item label="关联知识库 (Knowledge Base Keys)" class="mt-16">
              <a-textarea v-model:value="form.knowledgeBaseKeys" :auto-size="{ minRows: 2, maxRows: 2 }" placeholder="输入知识库唯一标识，多个请用逗号分隔..." />
            </a-form-item>

            <a-form-item label="扩展配置 (Custom Config JSON)">
              <a-textarea v-model:value="form.configParams" :auto-size="{ minRows: 3, maxRows: 3 }" placeholder='{"custom_flag": true}' />
            </a-form-item>
          </div>
        </div>
      </div>
    </a-form>

    <div class="modal-footer-action">
      <div class="footer-left">
        <span class="status-indicator"></span> 配置将实时校验并同步
      </div>
      <div class="footer-right">
        <a-button v-if="currentStep > 0" class="btn-flat" @click="currentStep--">上一步</a-button>
        <a-button v-if="currentStep < 2" type="primary" class="btn-next" @click="currentStep++">下一步：{{ stepLabels[currentStep+1] }}</a-button>
        <a-button 
          v-else 
          type="primary" 
          class="btn-submit" 
          :loading="confirmLoading" 
          @click="handleOk"
        >
          保存并发布智能体
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
  ThunderboltOutlined, RightOutlined, DeploymentUnitOutlined, MessageOutlined 
} from '@ant-design/icons-vue'
import type { FormInstance } from 'ant-design-vue'
import type { AiAgent } from '@/api/aiAgent.ts'
import AgentResourcePickModal from './AgentResourcePickModal.vue'

const props = defineProps<{ mode: 'create' | 'edit', confirmLoading: boolean, initial: AiAgent | null }>()
const emit = defineEmits<{ submit: [payload: AiAgent] }>()
const open = defineModel<boolean>('open', { required: true })

const currentStep = ref(0)
const stepLabels = ['基础设定', '模型大脑', '能力增强']
const formRef = ref<FormInstance | null>(null)
const promptTitleCache = ref('')
const picker = reactive({ model: false, prompt: false, tool: false, mcp: false })

const statusOptions = [{ label: '开启', value: 'enabled' }, { label: '关闭', value: 'disabled' }]
const memoryModeOptions = [
  { label: '不开启记忆', value: 'NONE' },
  { label: '滑动窗口', value: 'SLIDING_WINDOW' },
  { label: '向量长期记忆', value: 'VECTOR' },
  { label: '混合模式', value: 'hybrid' }
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

// 逻辑解析部分保持原样
const parsedToolKeys = computed(() => parseJsonKeys(form.toolKeys))
const parsedMcpKeys = computed(() => parseJsonKeys(form.mcpKeys))
const toolKeyTags = computed(() => parsedToolKeys.value)
const mcpKeyTags = computed(() => parsedMcpKeys.value)

function parseJsonKeys(raw: any) {
  if (!raw) return []
  try { return typeof raw === 'string' ? JSON.parse(raw) : raw } catch { return String(raw).split(',').filter(Boolean) }
}

const modelDisplayText = computed(() => form.modelName || form.modelKey || '')
const promptDisplayText = computed(() => promptTitleCache.value || form.promptKey || '')

const goToStep = (i: number) => { if (i < currentStep.value || form.agentName) currentStep.value = i }
const openPicker = (k: keyof typeof picker) => picker[k] = true

function removeKey(which: 'tool' | 'mcp', key: string) {
  const keys = which === 'tool' ? [...parsedToolKeys.value] : [...parsedMcpKeys.value]
  const filtered = keys.filter(k => k !== key)
  if (which === 'tool') form.toolKeys = JSON.stringify(filtered); else form.mcpKeys = JSON.stringify(filtered)
}

function onResourceConfirm(payload: any) {
  if (payload.kind === 'model') {
    form.modelKey = payload.modelKey || undefined
    form.modelName = payload.modelName
  }
  if (payload.kind === 'prompt') { form.promptKey = payload.promptKey; promptTitleCache.value = payload.promptTitle }
  if (payload.kind === 'tool') form.toolKeys = JSON.stringify(payload.keys)
  if (payload.kind === 'mcp') form.mcpKeys = JSON.stringify(payload.keys)
}

const rules = {
  agentName: [{ required: true, message: '请输入智能体名称' }],
  agentKey: [{ required: true, message: '请输入唯一识别码' }],
  modelKey: [{ required: true, message: '请关联驱动模型' }]
}

watch([() => open.value, () => props.initial], ([isOpen, init]) => {
  if (isOpen) {
    currentStep.value = 0
    Object.assign(form, emptyForm(), init || {})
  }
}, { immediate: true })

async function handleOk() {
  await formRef.value?.validate()
  emit('submit', { ...form })
}
const onCancel = () => open.value = false
</script>

<style scoped>
/* 核心容器高度控制 */
.professional-form {
  height: 520px; /* 统一高度，防止 Modal 抖动 */
  display: flex;
  flex-direction: column;
}

.form-scroll-container {
  flex: 1;
  overflow-y: auto;
  padding: 24px 40px;
}

/* 隐藏滚动条美化 */
.form-scroll-container::-webkit-scrollbar { width: 4px; }
.form-scroll-container::-webkit-scrollbar-thumb { background: #e0e0e0; border-radius: 4px; }

/* 基础样式复用与增强 */
.premium-agent-modal :deep(.ant-modal-content) { padding: 0; overflow: hidden; border-radius: 16px; }
.modal-header-gradient { background: #fff; padding: 24px 40px; border-bottom: 1px solid #f0f0f0; }
.header-content { display: flex; justify-content: space-between; align-items: center; }
.title-area { display: flex; gap: 14px; align-items: center; }
.icon-box { width: 42px; height: 42px; background: #0061ff; color: white; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 18px; }
.text-group h2 { margin: 0; font-size: 18px; font-weight: 600; }
.text-group p { margin: 2px 0 0; color: #999; font-size: 13px; }

/* 步骤条美化 */
.steps-nav { display: flex; gap: 20px; }
.step-item { display: flex; align-items: center; gap: 8px; cursor: pointer; color: #bfbfbf; }
.step-item.active { color: #0061ff; font-weight: 600; }
.step-item.done { color: #52c41a; }
.step-num { width: 20px; height: 20px; border-radius: 50%; border: 1px solid currentColor; display: flex; align-items: center; justify-content: center; font-size: 11px; }

/* 栅格布局优化 */
.form-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px 24px; }
.span-2 { grid-column: span 2; }

/* 紧凑型卡片样式 */
.compact-switch-card {
  display: flex; justify-content: space-between; align-items: center;
  background: #f8f9fb; padding: 8px 12px; border-radius: 8px; border: 1px solid #eee;
}
.compact-switch-card .hint { font-size: 12px; color: #999; }

.memory-setting-row { display: grid; grid-template-columns: 1.5fr 1fr; gap: 16px; background: #f0f5ff; padding: 12px; border-radius: 8px; margin-top: 4px; }

/* 资源选择器卡片 */
.resource-selection-group { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 20px; }
.picker-card {
  display: flex; align-items: center; gap: 12px; padding: 12px;
  background: #fff; border: 1px solid #eef0f4; border-radius: 10px;
  cursor: pointer; transition: all 0.2s;
}
.picker-card:hover { border-color: #0061ff; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
.card-icon { width: 36px; height: 36px; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 18px; }
.model-icon { background: #e6f7ff; color: #1890ff; }
.prompt-icon { background: #f6ffed; color: #52c41a; }
.card-info { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
.card-info .label { font-size: 11px; color: #999; }
.card-info .val { font-size: 13px; font-weight: 600; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.arrow { font-size: 12px; color: #bfbfbf; }

/* 参数输入优化 */
.parameter-compact-grid { display: grid; grid-template-columns: 1.5fr 1fr; gap: 24px; margin-bottom: 8px; }
.slider-with-input { display: flex; align-items: center; gap: 12px; }
.advanced-params-collapse { background: #fafafa; border-radius: 8px; border: none; }
:deep(.ant-collapse-header) { padding: 8px 12px !important; font-size: 13px; color: #666 !important; }

/* 能力卡片 */
.capability-card { border: 1px solid #f0f0f0; border-radius: 10px; padding: 12px; }
.cap-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.cap-header .title { font-size: 14px; font-weight: 600; }
.tag-render-area { min-height: 40px; display: flex; flex-wrap: wrap; gap: 6px; padding: 8px; background: #f9f9f9; border-radius: 6px; }
.empty-placeholder { color: #ccc; font-size: 12px; margin: auto; }

/* 底部按钮 */
.modal-footer-action { padding: 16px 40px; border-top: 1px solid #f0f0f0; display: flex; justify-content: space-between; align-items: center; }
.status-indicator { width: 6px; height: 6px; background: #52c41a; border-radius: 50%; display: inline-block; margin-right: 6px; }
.footer-left { color: #999; font-size: 12px; }
.btn-next, .btn-submit { border-radius: 8px; height: 38px; padding: 0 20px; font-weight: 600; }

.section-headline { font-size: 15px; margin-bottom: 16px; color: #1a1a1a; }
</style>