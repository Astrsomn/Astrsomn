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
            <template v-else-if="form.modelType === 'embedding'"><PartitionOutlined /></template>
            <template v-else><PictureOutlined /></template>
          </div>
          <div class="text-group">
            <h2>{{ mode === 'create' ? '注册接入端点' : '编辑端点配置' }}</h2>
            <p>配置物理供应源、API 地址及该端点支持的协议能力</p>
          </div>
        </div>
        <div class="steps-nav">
          <div
              v-for="(s, index) in ['接入识别', '能力定义']"
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
            <h3 class="section-headline"><IdcardOutlined /> 1. 端点身份识别</h3>
            <div class="form-grid">
              <a-form-item label="服务供应商 (Provider)" name="provider">
                <a-select v-model:value="form.provider" :options="providerOptions" placeholder="请选择端点所属服务商" size="large" :disabled="props.mode === 'view'" />
              </a-form-item>

              <a-form-item label="端点服务类型" name="modelType">
                <a-segmented v-model:value="form.modelType" :options="[{label:'对话端点', value:'chat'}, {label:'向量端点', value:'embedding'}, {label:'图像端点', value:'image'}]" block size="large" :disabled="props.mode === 'view'" />
              </a-form-item>

              <a-form-item label="端点展示名称" name="modelName">
                <a-input v-model:value="form.modelName" placeholder="例如：OpenAI 官方端点 或 私有部署 Llama3" size="large" :disabled="props.mode === 'view'" />
              </a-form-item>

              <a-form-item label="端点识别码 (Model Key)" name="modelKey">
                <a-tooltip
                    v-if="modelKeyImmutable || props.mode === 'view'"
                    :title="modelKeyImmutable ? '已有推理实例在同环境下引用该端点 Key，不可修改' : '查看模式下不可修改'"
                >
                  <a-input v-model:value="form.modelKey" placeholder="建议留空，系统将自动生成唯一索引" size="large" disabled />
                </a-tooltip>
                <a-input
                    v-else
                    v-model:value="form.modelKey"
                    placeholder="建议留空，系统将自动生成唯一索引"
                    size="large"
                >
                  <template #suffix>
                    <a-tooltip title="重置识别码">
                      <ReloadOutlined v-if="form.modelKey" @click="form.modelKey = ''" class="input-action-icon" />
                    </a-tooltip>
                  </template>
                </a-input>
              </a-form-item>

              <a-form-item label="关联凭证账号" name="accountKey">
                <a-select
                    v-model:value="form.accountKey"
                    :options="accountSelectOptions"
                    :loading="accountOptionsLoading"
                    allow-clear
                    show-search
                    :filter-option="filterAccountOption"
                    placeholder="请关联对应的 API 凭证资产"
                    size="large"
                    option-filter-prop="label"
                    :disabled="props.mode === 'view'"
                />
              </a-form-item>

              <a-form-item label="端点激活状态">
                <div class="status-toggle-card">
                  <a-switch
                      :checked="form.status === 'enabled'"
                      @change="onStatusSwitch"
                      checked-children="已上线"
                      un-checked-children="已下线"
                      :un-checked-color="'#ff4d4f'"
                      :disabled="props.mode === 'view'"
                  />
                </div>
              </a-form-item>

              <a-form-item label="接入地址 (API URL)" name="apiUrl" class="span-2">
                <a-input v-model:value="form.apiUrl" placeholder="供应商 Base URL，如 https://api.openai.com/v1" size="large" :disabled="props.mode === 'view'">
                  <template #prefix><GlobalOutlined style="color: #bfbfbf" /></template>
                </a-input>
              </a-form-item>
            </div>
          </div>
        </div>

        <div v-show="currentStep === 1" class="step-container animate-fade">
          <div class="form-section">
            <h3 class="section-headline"><ThunderboltOutlined /> 2. 端点能力定义（写入 Capabilities 供解析）</h3>

            <div class="capability-quick-view">
              <a-button type="primary" ghost @click="openCapabilitiesModal">
                <template #icon><EyeOutlined /></template>
                配置支持能力集
              </a-button>
            </div>

            <div class="runtime-params-box">
              <div class="box-title"><ControlOutlined /> 端点资源约束</div>
              <div class="param-grid">
                <div class="param-item">
                  <span class="pl">单次响应上限 (Tokens)</span>
                  <a-input-number v-model:value="form.responseLimit" :min="0" placeholder="默认 4096" block :disabled="props.mode === 'view'" />
                </div>
                <div class="param-item">
                  <span class="pl">累计消耗配额 (Tokens)</span>
                  <a-input-number v-model:value="form.maxQuotaTokens" :min="0" placeholder="0 表示无限制" block :disabled="props.mode === 'view'" />
                </div>
              </div>
            </div>
          </div>
        </div>

        <a-modal
            v-model:open="showCapabilitiesModal"
            :title="capabilitiesModalTitle"
            width="600px"
            :footer="null"
        >
          <div class="capabilities-modal-content">
            <div v-if="form.modelType === 'chat'" class="capability-modal-section">
              <h4>对话端点能力 · InferenceParamEnum</h4>
              <div class="cap-tag-grid">
                <div
                    v-for="opt in chatInferenceOptions"
                    :key="opt.value"
                    :class="['custom-cap-tag', { active: chatInferenceCapabilities.includes(opt.value) }]"
                    @click="props.mode !== 'view' && toggleChatInference(opt.value)"
                    :style="{ cursor: props.mode === 'view' ? 'default' : 'pointer' }"
                >
                  <CheckCircleFilled v-if="chatInferenceCapabilities.includes(opt.value)" />
                  {{ opt.label }}
                </div>
              </div>

              <div v-if="chatOrphanCapabilities.length" class="orphan-block">
                <div class="cap-subhead muted">非推理枚举 code（可能是历史标签，点击可移除）</div>
                <div class="cap-tag-grid">
                  <div
                      v-for="c in chatOrphanCapabilities"
                      :key="c"
                      class="custom-cap-tag orphan"
                      @click="props.mode !== 'view' && removeChatOrphan(c)"
                      :style="{ cursor: props.mode === 'view' ? 'default' : 'pointer' }"
                  >
                    {{ c }}
                  </div>
                </div>
              </div>
            </div>

            <div v-if="form.modelType === 'embedding'" class="capability-modal-section">
              <h4>向量端点能力 · EmbeddingInferenceParamEnum</h4>
              <div class="cap-tag-grid">
                <div
                    v-for="opt in embeddingInferenceOptions"
                    :key="opt.value"
                    :class="['custom-cap-tag', { active: embeddingInferenceCapabilities.includes(opt.value) }]"
                    @click="props.mode !== 'view' && toggleEmbeddingInference(opt.value)"
                    :style="{ cursor: props.mode === 'view' ? 'default' : 'pointer' }"
                >
                  <CheckCircleFilled v-if="embeddingInferenceCapabilities.includes(opt.value)" />
                  {{ opt.label }}
                </div>
              </div>

              <div v-if="embeddingOrphanCapabilities.length" class="orphan-block">
                <div class="cap-subhead muted">自定义能力 code</div>
                <div class="cap-tag-grid">
                  <div
                      v-for="c in embeddingOrphanCapabilities"
                      :key="c"
                      class="custom-cap-tag orphan"
                      @click="props.mode !== 'view' && removeEmbeddingOrphan(c)"
                      :style="{ cursor: props.mode === 'view' ? 'default' : 'pointer' }"
                  >
                    {{ c }}
                  </div>
                </div>
              </div>
            </div>

            <div v-if="form.modelType === 'image'" class="capability-modal-section">
              <h4>图像端点能力 · ImageGenParamEnum</h4>
              <div class="cap-tag-grid">
                <div
                    v-for="opt in imageGenOptions"
                    :key="opt.value"
                    :class="['custom-cap-tag', { active: imageGenCapabilities.includes(opt.value) }]"
                    @click="props.mode !== 'view' && toggleImageGen(opt.value)"
                    :style="{ cursor: props.mode === 'view' ? 'default' : 'pointer' }"
                >
                  <CheckCircleFilled v-if="imageGenCapabilities.includes(opt.value)" />
                  {{ opt.label }}
                </div>
              </div>

              <div v-if="imageOrphanCapabilities.length" class="orphan-block">
                <div class="cap-subhead muted">自定义能力 code</div>
                <div class="cap-tag-grid">
                  <div
                      v-for="c in imageOrphanCapabilities"
                      :key="c"
                      class="custom-cap-tag orphan"
                      @click="props.mode !== 'view' && removeImageOrphan(c)"
                      :style="{ cursor: props.mode === 'view' ? 'default' : 'pointer' }"
                  >
                    {{ c }}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <a-button type="primary" @click="closeCapabilitiesModal">关闭</a-button>
          </div>
        </a-modal>
      </div>
    </a-form>

    <div class="modal-footer-action" v-if="props.mode !== 'view'">
      <div class="footer-left">
        <LockOutlined /> 端点变更将影响下游所有推理实例
      </div>
      <div class="footer-right">
        <a-button v-if="currentStep > 0" class="btn-flat" @click="currentStep--">返回接入识别</a-button>
        <a-button
            v-if="currentStep < 1"
            type="primary"
            class="btn-next"
            @click="nextStep"
        >
          配置能力集
        </a-button>
        <a-button
            v-else
            type="primary"
            class="btn-submit"
            :loading="confirmLoading"
            @click="handleSubmit"
        >
          确认并保存端点
        </a-button>
      </div>
    </div>
    <div class="modal-footer-action" v-else>
      <div class="footer-right">
        <a-button type="primary" @click="emit('update:open', false)">关闭</a-button>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  IdcardOutlined, ReloadOutlined, MessageOutlined,
  PartitionOutlined, LockOutlined, ThunderboltOutlined,
  CheckCircleFilled, ControlOutlined, PictureOutlined, GlobalOutlined,
  EyeOutlined
} from '@ant-design/icons-vue'
import type { FormInstance } from 'ant-design-vue'
import type { AiModel } from '@/api/aiModel'
import { aiAccountApi, type AiAccount } from '@/api/aiAccount'
import { WORKSPACE_ENV_STORAGE_KEY } from '@/constants/workspaceEnv'
import { aiModelCapabilitiesDictionary } from '@/locales/zh-CN/dictionary/ai-model'
import {
  CHAT_INFERENCE_CODES,
  CHAT_INFERENCE_SET,
  EMBEDDING_INFERENCE_CODES,
  EMBEDDING_INFERENCE_SET,
  IMAGE_GEN_CODES,
  IMAGE_GEN_SET
} from '@/constants/aiModelCapabilityCodes'

const props = withDefaults(
    defineProps<{
      open: boolean
      mode: 'create' | 'edit' | 'view'
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

const accountList = ref<AiAccount[]>([])
const accountOptionsLoading = ref(false)
/** 编辑时由详情接口返回：被推理实例引用则不可改 modelKey */
const modelKeyImmutable = ref(false)

// 能力标签查看对话框
const showCapabilitiesModal = ref(false)
const capabilitiesModalTitle = computed(() => {
  return `${form.modelName} - 能力标签`
})

// 打开能力标签查看对话框
const openCapabilitiesModal = () => {
  showCapabilitiesModal.value = true
}

// 关闭能力标签查看对话框
const closeCapabilitiesModal = () => {
  showCapabilitiesModal.value = false
}

function resolveQueryCreateUser(): string | undefined {
  try {
    const raw = localStorage.getItem('userInfo')
    if (!raw) return undefined
    const u = JSON.parse(raw) as { username?: string; id?: string }
    const name = u.username?.trim()
    if (name) return name
    if (u.id) return String(u.id)
  } catch {
    /* ignore */
  }
  return undefined
}

function resolveQueryEnvCode(): string | undefined {
  const v = localStorage.getItem(WORKSPACE_ENV_STORAGE_KEY)
  return v?.trim() || undefined
}

async function fetchAccountOptions() {
  const createUser = resolveQueryCreateUser()
  if (!createUser) {
    accountList.value = []
    message.warning('未获取到登录用户，无法加载账号列表')
    return
  }
  accountOptionsLoading.value = true
  try {
    const resp = await aiAccountApi.queryPage({
      pageNo: 1,
      pageSize: 500,
      param: {
        createUser,
        envCode: resolveQueryEnvCode()
      }
    })
    accountList.value = resp.list || []
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '加载 AI 账号失败')
    accountList.value = []
  } finally {
    accountOptionsLoading.value = false
  }
}

const accountSelectOptions = computed(() => {
  const opts = accountList.value
    .filter((a) => a.accountKey)
    .map((a) => ({
      value: a.accountKey as string,
      label: `${a.accountName || a.accountKey} (${a.accountKey})`
    }))
  const key = form.accountKey?.trim()
  if (key && !opts.some((o) => o.value === key)) {
    opts.unshift({ value: key, label: `${key}（当前值，不在可选列表）` })
  }
  return opts
})

const filterAccountOption = (input: string, option: { label?: string }) => {
  const q = input.trim().toLowerCase()
  if (!q) return true
  return String(option?.label ?? '').toLowerCase().includes(q)
}

function capLabel(code: string): string {
  // 对于推理参数，只返回英文参数名，保持学术简洁性
  const inferenceParams = new Set([
    'temperature', 'top_p', 'top_k', 'presence_penalty', 'frequency_penalty',
    'max_tokens', 'seed', 'image_size', 'image_quality', 'image_style',
    'image_user', 'image_response_format', 'image_max_retries', 'image_timeout_seconds',
    'embedding_dimensions', 'embedding_user', 'embedding_max_retries',
    'embedding_max_segments_per_batch', 'embedding_encoding_format', 'embedding_timeout_seconds'
  ])
  
  if (inferenceParams.has(code)) {
    return code
  }
  
  // 对于其他能力标签，保持原有标签
  return aiModelCapabilitiesDictionary.getLabel(code) ?? code
}

function capOptions(codes: readonly string[]) {
  // 定义别名参数列表
  const aliasParams = new Set([
    'temperature_setting',
    'top_p_setting',
    'top_k_setting',
    'presence_penalty_setting',
    'frequency_penalty_setting',
    'max_token_setting',
    'stop_sequences_setting',
    'seed_setting',
    'size_setting',
    'style_setting'
  ])
  
  // 过滤掉别名参数，只保留主参数
  return codes
    .filter(value => !aliasParams.has(value))
    .map(value => ({ value, label: capLabel(value) }))
}

const chatInferenceOptions = computed(() => capOptions(CHAT_INFERENCE_CODES))
const embeddingInferenceOptions = computed(() => capOptions(EMBEDDING_INFERENCE_CODES))
const imageGenOptions = computed(() => capOptions(IMAGE_GEN_CODES))

const chatInferenceCapabilities = ref<string[]>([])
const chatOrphanCapabilities = ref<string[]>([])

const embeddingInferenceCapabilities = ref<string[]>([])
const embeddingOrphanCapabilities = ref<string[]>([])

const imageGenCapabilities = ref<string[]>([])
const imageOrphanCapabilities = ref<string[]>([])

const chatInferenceTotal = computed(
    () => chatInferenceCapabilities.value.length + chatOrphanCapabilities.value.length
)
const embeddingInferenceTotal = computed(
    () => embeddingInferenceCapabilities.value.length + embeddingOrphanCapabilities.value.length
)
const imageGenTotal = computed(
    () => imageGenCapabilities.value.length + imageOrphanCapabilities.value.length
)

const form = reactive<AiModel>({
  modelName: '', modelKey: '', modelType: 'chat', provider: '',
  accountKey: '', apiUrl: '', status: 'enabled', isDefault: 0, responseLimit: 4096,
  capabilities: '', randomIndex: 0, topVariance: 0, maxQuotaTokens: 0
})

const rules = {
  modelName: [{ required: true, message: '请输入模型名称' }],
  provider: [{ required: true, message: '请选择供应商' }],
  apiUrl: [],
}

function toggleInList(list: string[], val: string) {
  const i = list.indexOf(val)
  if (i > -1) list.splice(i, 1)
  else list.push(val)
}

const toggleChatInference = (val: string) => toggleInList(chatInferenceCapabilities.value, val)
const removeChatOrphan = (val: string) => {
  chatOrphanCapabilities.value = chatOrphanCapabilities.value.filter((c) => c !== val)
}

const toggleEmbeddingInference = (val: string) => toggleInList(embeddingInferenceCapabilities.value, val)
const removeEmbeddingOrphan = (val: string) => {
  embeddingOrphanCapabilities.value = embeddingOrphanCapabilities.value.filter((c) => c !== val)
}

const toggleImageGen = (val: string) => toggleInList(imageGenCapabilities.value, val)
const removeImageOrphan = (val: string) => {
  imageOrphanCapabilities.value = imageOrphanCapabilities.value.filter((c) => c !== val)
}

const onStatusSwitch = (checked: boolean) => {
  form.status = checked ? 'enabled' : 'disabled'
}

const nextStep = async () => {
  try {
    if (currentStep.value === 0) await formRef.value?.validateFields(['provider', 'modelName'])
    currentStep.value++
  } catch (e) {}
}

function partitionCapabilities(caps: string[], modelType: string) {
  chatInferenceCapabilities.value = []
  chatOrphanCapabilities.value = []
  embeddingInferenceCapabilities.value = []
  embeddingOrphanCapabilities.value = []
  imageGenCapabilities.value = []
  imageOrphanCapabilities.value = []

  if (modelType === 'chat') {
    chatInferenceCapabilities.value = caps.filter((c) => CHAT_INFERENCE_SET.has(c))
    chatOrphanCapabilities.value = caps.filter((c) => !CHAT_INFERENCE_SET.has(c))
  } else if (modelType === 'embedding') {
    embeddingInferenceCapabilities.value = caps.filter((c) => EMBEDDING_INFERENCE_SET.has(c))
    embeddingOrphanCapabilities.value = caps.filter((c) => !EMBEDDING_INFERENCE_SET.has(c))
  } else if (modelType === 'image') {
    imageGenCapabilities.value = caps.filter((c) => IMAGE_GEN_SET.has(c))
    imageOrphanCapabilities.value = caps.filter((c) => !IMAGE_GEN_SET.has(c))
  }
}

const syncForm = () => {
  currentStep.value = 0
  if (props.mode === 'create' || !props.initialData) {
    modelKeyImmutable.value = false
    // 完全重置 form 对象，确保所有字段都被清空
    Object.assign(form, {
      id: '',
      modelName: '',
      modelKey: '',
      modelType: 'chat',
      provider: '',
      accountKey: '',
      apiUrl: '',
      status: 'enabled',
      isDefault: 0,
      capabilities: '',
      randomIndex: 0,
      topVariance: 0,
      maxQuotaTokens: 0
    })
    partitionCapabilities([], String(form.modelType ?? 'chat'))
  } else {
    Object.assign(form, props.initialData)
    modelKeyImmutable.value = props.initialData.modelKeyImmutable === true
    delete (form as Record<string, unknown>).modelKeyImmutable
    try {
      const parsed = JSON.parse(form.capabilities || '[]')
      const caps = Array.isArray(parsed) ? parsed.map(String) : []
      partitionCapabilities(caps, String(form.modelType || 'chat'))
    } catch {
      partitionCapabilities([], String(form.modelType || 'chat'))
    }
  }
}

watch(
  () => props.open,
  (v) => {
    if (!v) return
    syncForm()
    void fetchAccountOptions()
  }
)

// 监听模式变化，确保从编辑切换到创建时表单数据会重置
watch(
  () => props.mode,
  () => {
    if (props.open) {
      syncForm()
    }
  }
)

watch(() => form.modelType, () => {
  partitionCapabilities([], String(form.modelType || 'chat'))
})

const handleSubmit = async () => {
  await formRef.value?.validate()
  const payload = { ...form }
  
  let allCapabilities: string[] = []
  if (form.modelType === 'chat') {
    allCapabilities = [...chatInferenceCapabilities.value, ...chatOrphanCapabilities.value]
  } else if (form.modelType === 'embedding') {
    allCapabilities = [...embeddingInferenceCapabilities.value, ...embeddingOrphanCapabilities.value]
  } else if (form.modelType === 'image') {
    allCapabilities = [...imageGenCapabilities.value, ...imageOrphanCapabilities.value]
  }
  
  payload.capabilities = allCapabilities.length > 0 ? JSON.stringify(allCapabilities) : ''
  delete (payload as { modelKeyImmutable?: unknown }).modelKeyImmutable
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
.icon-box.image { background: linear-gradient(135deg, #ff6b6b, #ffd93d); }
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
  display: flex; justify-content: center; align-items: center;
  background: #f8f9fb; padding: 12px 16px; border-radius: 12px; border: 1px solid #eef1f6;
}

/* 模型类型选中高亮蓝色 */
:deep(.ant-segmented-item-selected) {
  background-color: #1890ff !important;
  color: white !important;
}

:deep(.ant-segmented-item-selected:hover) {
  background-color: #40a9ff !important;
  color: white !important;
}

/* 能力矩阵布局 */
.capability-wrapper { background: #fafafa; border-radius: 16px; padding: 20px; border: 1px solid #f0f0f0; }
.cap-header { display: flex; justify-content: space-between; margin-bottom: 12px; font-size: 13px; font-weight: 600; }
.cap-header .badge { background: #0061ff; color: #fff; padding: 0 8px; border-radius: 10px; font-size: 11px; }
.cap-subhead {
  font-size: 12px;
  font-weight: 600;
  color: #555;
  margin: 16px 0 10px;
}
.cap-subhead:first-of-type { margin-top: 0; }
.cap-subhead.muted { color: #888; font-weight: 500; }
.orphan-block { margin-top: 14px; padding-top: 12px; border-top: 1px dashed #e8e8e8; }
.cap-tag-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(120px, 1fr)); gap: 10px; }
.custom-cap-tag {
  padding: 8px 12px; background: #fff; border: 1px solid #e8e8e8; border-radius: 8px;
  cursor: pointer; transition: 0.2s; font-size: 12px; color: #666; display: flex; align-items: center; gap: 6px;
}
.custom-cap-tag:hover { border-color: #0061ff; color: #0061ff; }
.custom-cap-tag.active {
  background: #e6f0ff; border-color: #0061ff; color: #0061ff; font-weight: 600;
}
.custom-cap-tag.orphan {
  background: #fafafa;
  border-style: dashed;
  font-family: ui-monospace, monospace;
  font-size: 11px;
}
.custom-cap-tag.orphan:hover { border-color: #ff4d4f; color: #ff4d4f; }

/* 运行参数卡片 */
.runtime-params-box { margin-top: 24px; background: #fff; border: 1px solid #eee; border-radius: 16px; padding: 16px; }
.box-title { font-size: 13px; font-weight: 600; margin-bottom: 16px; color: #111; display: flex; align-items: center; gap: 6px; }
.param-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
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