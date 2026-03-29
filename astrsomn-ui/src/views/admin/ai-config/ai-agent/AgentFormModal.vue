<template>
  <div class="agent-form-modal-root">
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
            <p>定义 Agent 的身份、实例化模型分层（对话 / 向量 / 图像）与专业能力范围</p>
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
            <h3 class="section-headline"><DotChartOutlined /> 2. 模型实例与提示词</h3>
            <p class="step-hint">对话 / 向量 / 图像能力分别绑定「推理实例」(Instance)；采样参数在实例管理中配置，不再在 Agent 上重复维护。</p>

            <div class="resource-selection-group instance-trio">
              <div class="picker-card" @click="openPicker('chatInstance')">
                <div class="card-icon model-icon"><DeploymentUnitOutlined /></div>
                <div class="card-info">
                  <span class="label">对话实例 (Chat)</span>
                  <span class="val">{{ chatInstanceDisplay || '点击选择' }}</span>
                </div>
                <RightOutlined class="arrow" />
              </div>
              <div class="picker-card" @click="openPicker('embeddingInstance')">
                <div class="card-icon embed-icon"><ClusterOutlined /></div>
                <div class="card-info">
                  <span class="label">向量实例 (Embedding)</span>
                  <span class="val">{{ embeddingInstanceDisplay || '可选' }}</span>
                </div>
                <RightOutlined class="arrow" />
              </div>
              <div class="picker-card" @click="openPicker('imageInstance')">
                <div class="card-icon image-icon"><PictureOutlined /></div>
                <div class="card-info">
                  <span class="label">图像实例 (Image)</span>
                  <span class="val">{{ imageInstanceDisplay || '可选' }}</span>
                </div>
                <RightOutlined class="arrow" />
              </div>
            </div>

            <a-form-item name="chatInstanceKey" :rules="rules.chatInstanceKey" class="hidden-chat-instance-field">
              <a-input v-model:value="form.chatInstanceKey" tabindex="-1" class="visually-hidden-input" read-only />
            </a-form-item>

            <div class="picker-card prompt-single-row" @click="openPicker('prompt')">
              <div class="card-icon prompt-icon"><MessageOutlined /></div>
              <div class="card-info">
                <span class="label">系统提示词 (System Prompt)</span>
                <span class="val">{{ promptDisplayText || '点击配置指令' }}</span>
              </div>
              <RightOutlined class="arrow" />
            </div>
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
                <template v-for="t in toolKeyTags" :key="'t' + t">
                  <a-tag closable color="blue" @close="removeKey('tool', t)">{{ t }}</a-tag>
                </template>
                <template v-for="m in mcpKeyTags" :key="'m' + m">
                  <a-tag closable color="purple" @close="removeKey('mcp', m)">{{ m }}</a-tag>
                </template>
                <div v-if="!toolKeyTags.length && !mcpKeyTags.length" class="empty-placeholder">未绑定外部能力</div>
              </div>
            </div>

            <a-form-item label="关联知识库 (Knowledge Base Keys)" class="mt-16">
              <a-textarea v-model:value="form.knowledgeBaseKeys" :auto-size="{ minRows: 2, maxRows: 2 }" placeholder="输入知识库唯一标识，多个请用逗号分隔..." />
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
        <a-button v-if="currentStep < 2" type="primary" class="btn-next" @click="currentStep++">下一步：{{ stepLabels[currentStep + 1] }}</a-button>
        <a-button v-else type="primary" class="btn-submit" :loading="confirmLoading" @click="handleOk"> 保存并发布智能体 </a-button>
      </div>
    </div>
  </a-modal>

  <AgentResourcePickModal
    v-model:open="picker.chatInstance"
    kind="instance"
    instance-model-type="chat"
    :initial-single="form.chatInstanceKey ?? null"
    @confirm="onResourceConfirm"
  />
  <AgentResourcePickModal
    v-model:open="picker.embeddingInstance"
    kind="instance"
    instance-model-type="embedding"
    :initial-single="form.embeddingInstanceKey ?? null"
    @confirm="onResourceConfirm"
  />
  <AgentResourcePickModal
    v-model:open="picker.imageInstance"
    kind="instance"
    instance-model-type="image"
    :initial-single="form.imageInstanceKey ?? null"
    @confirm="onResourceConfirm"
  />
  <AgentResourcePickModal v-model:open="picker.prompt" kind="prompt" :initial-single="form.promptKey || null" @confirm="onResourceConfirm" />
  <AgentResourcePickModal v-model:open="picker.tool" kind="tool" :initial-keys="parsedToolKeys" @confirm="onResourceConfirm" />
  <AgentResourcePickModal v-model:open="picker.mcp" kind="mcp" :initial-keys="parsedMcpKeys" @confirm="onResourceConfirm" />
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import {
  PlusOutlined,
  EditOutlined,
  IdcardOutlined,
  DotChartOutlined,
  ThunderboltOutlined,
  RightOutlined,
  DeploymentUnitOutlined,
  MessageOutlined,
  ClusterOutlined,
  PictureOutlined
} from '@ant-design/icons-vue'
import type { FormInstance } from 'ant-design-vue'
import type { AiAgent } from '@/api/aiAgent.ts'
import AgentResourcePickModal from './AgentResourcePickModal.vue'

const props = defineProps<{ mode: 'create' | 'edit'; confirmLoading: boolean; initial: AiAgent | null }>()
const emit = defineEmits<{ submit: [payload: AiAgent] }>()
const open = defineModel<boolean>('open', { required: true })

const currentStep = ref(0)
const stepLabels = ['基础设定', '模型实例', '能力增强']
const formRef = ref<FormInstance | null>(null)
const promptTitleCache = ref('')
const picker = reactive({
  chatInstance: false,
  embeddingInstance: false,
  imageInstance: false,
  prompt: false,
  tool: false,
  mcp: false
})

const statusOptions = [
  { label: '开启', value: 'enabled' },
  { label: '关闭', value: 'disabled' }
]
const memoryModeOptions = [
  { label: '不开启记忆', value: 'NONE' },
  { label: '滑动窗口', value: 'SLIDING_WINDOW' },
  { label: '向量长期记忆', value: 'VECTOR' },
  { label: '混合模式', value: 'hybrid' }
]

const emptyForm = (): AiAgent => ({
  agentName: '',
  agentKey: '',
  description: '',
  workflowKey: '',
  chatInstanceKey: undefined,
  chatInstanceName: undefined,
  embeddingInstanceKey: undefined,
  embeddingInstanceName: undefined,
  imageInstanceKey: undefined,
  imageInstanceName: undefined,
  promptKey: '',
  status: 'enabled',
  knowledgeBaseKeys: '',
  toolKeys: '',
  mcpKeys: '',
  memoryMode: 'SLIDING_WINDOW',
  memoryWindowSize: '10',
  enableStream: true
})

const form = reactive<AiAgent>(emptyForm())

const parsedToolKeys = computed(() => parseJsonKeys(form.toolKeys))
const parsedMcpKeys = computed(() => parseJsonKeys(form.mcpKeys))
const toolKeyTags = computed(() => parsedToolKeys.value)
const mcpKeyTags = computed(() => parsedMcpKeys.value)

function parseJsonKeys(raw: unknown) {
  if (!raw) return []
  try {
    return typeof raw === 'string' ? JSON.parse(raw) : (raw as string[])
  } catch {
    return String(raw)
      .split(',')
      .filter(Boolean)
  }
}

const chatInstanceDisplay = computed(() => form.chatInstanceName || form.chatInstanceKey || '')
const embeddingInstanceDisplay = computed(() => form.embeddingInstanceName || form.embeddingInstanceKey || '')
const imageInstanceDisplay = computed(() => form.imageInstanceName || form.imageInstanceKey || '')
const promptDisplayText = computed(() => promptTitleCache.value || form.promptKey || '')

const goToStep = (i: number) => {
  if (i < currentStep.value || form.agentName) currentStep.value = i
}
const openPicker = (k: keyof typeof picker) => {
  picker[k] = true
}

function removeKey(which: 'tool' | 'mcp', key: string) {
  const keys = which === 'tool' ? [...parsedToolKeys.value] : [...parsedMcpKeys.value]
  const filtered = keys.filter((x) => x !== key)
  if (which === 'tool') form.toolKeys = JSON.stringify(filtered)
  else form.mcpKeys = JSON.stringify(filtered)
}

function onResourceConfirm(payload: {
  kind: string
  instanceModelType?: string
  instanceKey?: string
  instanceName?: string
  modelKey?: string
  modelName?: string
  promptKey?: string
  promptTitle?: string
  keys?: string[]
}) {
  if (payload.kind === 'instance') {
    const t = payload.instanceModelType
    if (t === 'chat') {
      form.chatInstanceKey = payload.instanceKey || undefined
      form.chatInstanceName = payload.instanceName
    } else if (t === 'embedding') {
      form.embeddingInstanceKey = payload.instanceKey || undefined
      form.embeddingInstanceName = payload.instanceName
    } else if (t === 'image') {
      form.imageInstanceKey = payload.instanceKey || undefined
      form.imageInstanceName = payload.instanceName
    }
    return
  }
  if (payload.kind === 'prompt') {
    form.promptKey = payload.promptKey
    promptTitleCache.value = payload.promptTitle || ''
  }
  if (payload.kind === 'tool' && payload.keys) form.toolKeys = JSON.stringify(payload.keys)
  if (payload.kind === 'mcp' && payload.keys) form.mcpKeys = JSON.stringify(payload.keys)
}

const rules = {
  agentName: [{ required: true, message: '请输入智能体名称' }],
  agentKey: [{ required: true, message: '请输入唯一识别码' }],
  chatInstanceKey: [{ required: true, message: '请选择对话 (Chat) 推理实例' }]
}

watch(
  () => [open.value, props.initial] as const,
  ([isOpen, init]) => {
    if (isOpen) {
      currentStep.value = 0
      promptTitleCache.value = init?.promptTitle || ''
      Object.assign(form, emptyForm(), init || {})
    }
  },
  { immediate: true }
)

async function handleOk() {
  await formRef.value?.validate()
  emit('submit', { ...form })
}
const onCancel = () => {
  open.value = false
}
</script>

<style scoped>
/* 核心容器高度控制 */
.professional-form {
  height: 520px;
  display: flex;
  flex-direction: column;
}

.form-scroll-container {
  flex: 1;
  overflow-y: auto;
  padding: 24px 40px;
}

.form-scroll-container::-webkit-scrollbar {
  width: 4px;
}
.form-scroll-container::-webkit-scrollbar-thumb {
  background: #e0e0e0;
  border-radius: 4px;
}

.premium-agent-modal :deep(.ant-modal-content) {
  padding: 0;
  overflow: hidden;
  border-radius: 16px;
}
.modal-header-gradient {
  background: #fff;
  padding: 24px 40px;
  border-bottom: 1px solid #f0f0f0;
}
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.title-area {
  display: flex;
  gap: 14px;
  align-items: center;
}
.icon-box {
  width: 42px;
  height: 42px;
  background: #0061ff;
  color: white;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}
.text-group h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}
.text-group p {
  margin: 2px 0 0;
  color: #999;
  font-size: 13px;
}

.steps-nav {
  display: flex;
  gap: 20px;
}
.step-item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #bfbfbf;
}
.step-item.active {
  color: #0061ff;
  font-weight: 600;
}
.step-item.done {
  color: #52c41a;
}
.step-num {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 1px solid currentColor;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px 24px;
}
.span-2 {
  grid-column: span 2;
}

.compact-switch-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f8f9fb;
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid #eee;
}
.compact-switch-card .hint {
  font-size: 12px;
  color: #999;
}

.memory-setting-row {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 16px;
  background: #f0f5ff;
  padding: 12px;
  border-radius: 8px;
  margin-top: 4px;
}

.step-hint {
  font-size: 12px;
  color: #8c8c8c;
  margin: 0 0 16px;
  line-height: 1.5;
}

.resource-selection-group {
  display: grid;
  gap: 16px;
  margin-bottom: 16px;
}
.resource-selection-group.instance-trio {
  grid-template-columns: repeat(3, 1fr);
}
@media (max-width: 900px) {
  .resource-selection-group.instance-trio {
    grid-template-columns: 1fr;
  }
}

.picker-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #fff;
  border: 1px solid #eef0f4;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
}
.picker-card:hover {
  border-color: #0061ff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}
.prompt-single-row {
  margin-top: 8px;
}
.card-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}
.model-icon {
  background: #e6f7ff;
  color: #1890ff;
}
.embed-icon {
  background: #f9f0ff;
  color: #722ed1;
}
.image-icon {
  background: #fff7e6;
  color: #fa8c16;
}
.prompt-icon {
  background: #f6ffed;
  color: #52c41a;
}
.card-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.card-info .label {
  font-size: 11px;
  color: #999;
}
.card-info .val {
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.arrow {
  font-size: 12px;
  color: #bfbfbf;
}

.hidden-chat-instance-field {
  margin: 0;
  min-height: 0;
}
.hidden-chat-instance-field :deep(.ant-form-item-row) {
  display: none;
}
.visually-hidden-input {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  border: 0;
  opacity: 0;
}

.capability-card {
  border: 1px solid #f0f0f0;
  border-radius: 10px;
  padding: 12px;
}
.cap-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.cap-header .title {
  font-size: 14px;
  font-weight: 600;
}
.tag-render-area {
  min-height: 40px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  padding: 8px;
  background: #f9f9f9;
  border-radius: 6px;
}
.empty-placeholder {
  color: #ccc;
  font-size: 12px;
  margin: auto;
}

.modal-footer-action {
  padding: 16px 40px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.status-indicator {
  width: 6px;
  height: 6px;
  background: #52c41a;
  border-radius: 50%;
  display: inline-block;
  margin-right: 6px;
}
.footer-left {
  color: #999;
  font-size: 12px;
}
.btn-next,
.btn-submit {
  border-radius: 8px;
  height: 38px;
  padding: 0 20px;
  font-weight: 600;
}

.section-headline {
  font-size: 15px;
  margin-bottom: 16px;
  color: #1a1a1a;
}
</style>
