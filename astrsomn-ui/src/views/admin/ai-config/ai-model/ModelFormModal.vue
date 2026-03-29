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
            <h2>{{ mode === 'create' ? '注册新 AI 模型' : '编辑模型配置' }}</h2>
            <p>配置模型供应源、API 端点及核心推理能力</p>
          </div>
        </div>
        <div class="steps-nav">
          <div
              v-for="(s, index) in ['基础', '能力']"
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
                <a-segmented v-model:value="form.modelType" :options="[{label:'对话模型', value:'chat'}, {label:'向量模型', value:'embedding'}, {label:'图像模型', value:'image'}]" block size="large" />
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

              <a-form-item label="账号 Key" name="accountKey" class="span-2">
                <a-select
                  v-model:value="form.accountKey"
                  :options="accountSelectOptions"
                  :loading="accountOptionsLoading"
                  allow-clear
                  show-search
                  :filter-option="filterAccountOption"
                  placeholder="选择当前用户在当前环境下的 AI 账号"
                  size="large"
                  option-filter-prop="label"
                />
              </a-form-item>

              <a-form-item label="API URL" name="apiUrl" class="span-2">
                <a-input v-model:value="form.apiUrl" placeholder="https://api.openai.com/v1" size="large">
                  <template #prefix><GlobalOutlined style="color: #bfbfbf" /></template>
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
                      @change="onStatusSwitch"
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
            <h3 class="section-headline"><ThunderboltOutlined /> 2. 能力矩阵与推理预设</h3>

            <div v-if="form.modelType === 'chat'" class="capability-wrapper">
              <div class="cap-header">
                <span>对话模型 · 与后端 AiModelEnum 对齐</span>
                <span class="badge">{{ chatCapabilityTotal }}</span>
              </div>

              <div class="cap-subhead">能力标签（ChatCapabilitiesEnum）</div>
              <div class="cap-tag-grid">
                <div
                    v-for="opt in chatFeatureOptions"
                    :key="opt.value"
                    :class="['custom-cap-tag', { active: chatFeatureCapabilities.includes(opt.value) }]"
                    @click="toggleChatFeature(opt.value)"
                >
                  <CheckCircleFilled v-if="chatFeatureCapabilities.includes(opt.value)" />
                  {{ opt.label }}
                </div>
              </div>

              <div class="cap-subhead">推理超参（InferenceParamEnum，含旧版 *_setting 别名）</div>
              <div class="cap-tag-grid">
                <div
                    v-for="opt in chatInferenceOptions"
                    :key="opt.value"
                    :class="['custom-cap-tag', { active: chatInferenceCapabilities.includes(opt.value) }]"
                    @click="toggleChatInference(opt.value)"
                >
                  <CheckCircleFilled v-if="chatInferenceCapabilities.includes(opt.value)" />
                  {{ opt.label }}
                </div>
              </div>

              <div v-if="chatOrphanCapabilities.length" class="orphan-block">
                <div class="cap-subhead muted">未匹配的 code（将原样保存，可点击移除）</div>
                <div class="cap-tag-grid">
                  <div
                      v-for="c in chatOrphanCapabilities"
                      :key="c"
                      class="custom-cap-tag orphan"
                      @click="removeChatOrphan(c)"
                  >
                    {{ c }}
                  </div>
                </div>
              </div>
            </div>

            <div v-if="form.modelType === 'embedding'" class="capability-wrapper">
              <div class="cap-header">
                <span>向量模型 · EmbeddingCapabilities + EmbeddingInferenceParam</span>
                <span class="badge">{{ embeddingCapabilityTotal }}</span>
              </div>

              <div class="cap-subhead">能力（EmbeddingCapabilitiesEnum）</div>
              <div class="cap-tag-grid">
                <div
                    v-for="opt in embeddingFeatureOptions"
                    :key="opt.value"
                    :class="['custom-cap-tag', { active: embeddingFeatureCapabilities.includes(opt.value) }]"
                    @click="toggleEmbeddingFeature(opt.value)"
                >
                  <CheckCircleFilled v-if="embeddingFeatureCapabilities.includes(opt.value)" />
                  {{ opt.label }}
                </div>
              </div>

              <div class="cap-subhead">嵌入调用参数（EmbeddingInferenceParamEnum）</div>
              <div class="cap-tag-grid">
                <div
                    v-for="opt in embeddingInferenceOptions"
                    :key="opt.value"
                    :class="['custom-cap-tag', { active: embeddingInferenceCapabilities.includes(opt.value) }]"
                    @click="toggleEmbeddingInference(opt.value)"
                >
                  <CheckCircleFilled v-if="embeddingInferenceCapabilities.includes(opt.value)" />
                  {{ opt.label }}
                </div>
              </div>

              <div v-if="embeddingOrphanCapabilities.length" class="orphan-block">
                <div class="cap-subhead muted">未匹配的 code（将原样保存，可点击移除）</div>
                <div class="cap-tag-grid">
                  <div
                      v-for="c in embeddingOrphanCapabilities"
                      :key="c"
                      class="custom-cap-tag orphan"
                      @click="removeEmbeddingOrphan(c)"
                  >
                    {{ c }}
                  </div>
                </div>
              </div>
            </div>

            <div v-if="form.modelType === 'image'" class="capability-wrapper">
              <div class="cap-header">
                <span>图像模型 · ImageCapabilities + ImageGenParam</span>
                <span class="badge">{{ imageCapabilityTotal }}</span>
              </div>

              <div class="cap-subhead">能力（ImageCapabilitiesEnum）</div>
              <div class="cap-tag-grid">
                <div
                    v-for="opt in imageFeatureOptions"
                    :key="opt.value"
                    :class="['custom-cap-tag', { active: imageFeatureCapabilities.includes(opt.value) }]"
                    @click="toggleImageFeature(opt.value)"
                >
                  <CheckCircleFilled v-if="imageFeatureCapabilities.includes(opt.value)" />
                  {{ opt.label }}
                </div>
              </div>

              <div class="cap-subhead">文生图参数（ImageGenParamEnum，含 size_setting / style_setting 别名）</div>
              <div class="cap-tag-grid">
                <div
                    v-for="opt in imageGenOptions"
                    :key="opt.value"
                    :class="['custom-cap-tag', { active: imageGenCapabilities.includes(opt.value) }]"
                    @click="toggleImageGen(opt.value)"
                >
                  <CheckCircleFilled v-if="imageGenCapabilities.includes(opt.value)" />
                  {{ opt.label }}
                </div>
              </div>

              <div v-if="imageOrphanCapabilities.length" class="orphan-block">
                <div class="cap-subhead muted">未匹配的 code（将原样保存，可点击移除）</div>
                <div class="cap-tag-grid">
                  <div
                      v-for="c in imageOrphanCapabilities"
                      :key="c"
                      class="custom-cap-tag orphan"
                      @click="removeImageOrphan(c)"
                  >
                    {{ c }}
                  </div>
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
                  <span class="pl">最大配额 Tokens</span>
                  <a-input-number v-model:value="form.maxQuotaTokens" :min="0" placeholder="0" block />
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
        <a-button
            v-if="currentStep < 1"
            type="primary"
            class="btn-next"
            @click="nextStep"
        >
          下一步
        </a-button>
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
import { message } from 'ant-design-vue'
import {
  IdcardOutlined, ReloadOutlined, MessageOutlined,
  PartitionOutlined, LockOutlined, ThunderboltOutlined,
  CheckCircleFilled, ControlOutlined, PictureOutlined, GlobalOutlined
} from '@ant-design/icons-vue'
import type { FormInstance } from 'ant-design-vue'
import type { AiModel } from '@/api/aiModel'
import { aiAccountApi, type AiAccount } from '@/api/aiAccount'
import { WORKSPACE_ENV_STORAGE_KEY } from '@/constants/workspaceEnv'
import { aiModelCapabilitiesDictionary } from '@/locales/zh-CN/dictionary/ai-model'
import {
  CHAT_ALL_KNOWN_SET,
  CHAT_FEATURE_CODES,
  CHAT_FEATURE_SET,
  CHAT_INFERENCE_CODES,
  CHAT_INFERENCE_SET,
  EMBEDDING_ALL_KNOWN_SET,
  EMBEDDING_FEATURE_CODES,
  EMBEDDING_FEATURE_SET,
  EMBEDDING_INFERENCE_CODES,
  EMBEDDING_INFERENCE_SET,
  IMAGE_ALL_KNOWN_SET,
  IMAGE_FEATURE_CODES,
  IMAGE_FEATURE_SET,
  IMAGE_GEN_CODES,
  IMAGE_GEN_SET
} from '@/constants/aiModelCapabilityCodes'

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

const accountList = ref<AiAccount[]>([])
const accountOptionsLoading = ref(false)

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
  return aiModelCapabilitiesDictionary.getLabel(code) ?? code
}

function capOptions(codes: readonly string[]) {
  return codes.map((value) => ({ value, label: capLabel(value) }))
}

const chatFeatureOptions = computed(() => capOptions(CHAT_FEATURE_CODES))
const chatInferenceOptions = computed(() => capOptions(CHAT_INFERENCE_CODES))
const embeddingFeatureOptions = computed(() => capOptions(EMBEDDING_FEATURE_CODES))
const embeddingInferenceOptions = computed(() => capOptions(EMBEDDING_INFERENCE_CODES))
const imageFeatureOptions = computed(() => capOptions(IMAGE_FEATURE_CODES))
const imageGenOptions = computed(() => capOptions(IMAGE_GEN_CODES))

const chatFeatureCapabilities = ref<string[]>([])
const chatInferenceCapabilities = ref<string[]>([])
const chatOrphanCapabilities = ref<string[]>([])

const embeddingFeatureCapabilities = ref<string[]>([])
const embeddingInferenceCapabilities = ref<string[]>([])
const embeddingOrphanCapabilities = ref<string[]>([])

const imageFeatureCapabilities = ref<string[]>([])
const imageGenCapabilities = ref<string[]>([])
const imageOrphanCapabilities = ref<string[]>([])

const chatCapabilityTotal = computed(
    () =>
        chatFeatureCapabilities.value.length +
        chatInferenceCapabilities.value.length +
        chatOrphanCapabilities.value.length
)
const embeddingCapabilityTotal = computed(
    () =>
        embeddingFeatureCapabilities.value.length +
        embeddingInferenceCapabilities.value.length +
        embeddingOrphanCapabilities.value.length
)
const imageCapabilityTotal = computed(
    () =>
        imageFeatureCapabilities.value.length +
        imageGenCapabilities.value.length +
        imageOrphanCapabilities.value.length
)

const form = reactive<AiModel>({
  modelName: '', modelKey: '', modelType: 'chat', provider: '',
  accountKey: '', apiUrl: '', status: 'enabled', isDefault: 0, responseLimit: 4096,
  capabilities: '', randomIndex: 0, topVariance: 0, maxQuotaTokens: 0
})

const rules = {
  modelName: [{ required: true, message: '请输入模型名称' }],
  provider: [{ required: true, message: '请选择供应商' }],
  apiUrl: [{ required: true, message: '请输入 API URL' }],
}

function toggleInList(list: string[], val: string) {
  const i = list.indexOf(val)
  if (i > -1) list.splice(i, 1)
  else list.push(val)
}

const toggleChatFeature = (val: string) => toggleInList(chatFeatureCapabilities.value, val)
const toggleChatInference = (val: string) => toggleInList(chatInferenceCapabilities.value, val)
const removeChatOrphan = (val: string) => {
  chatOrphanCapabilities.value = chatOrphanCapabilities.value.filter((c) => c !== val)
}

const toggleEmbeddingFeature = (val: string) => toggleInList(embeddingFeatureCapabilities.value, val)
const toggleEmbeddingInference = (val: string) => toggleInList(embeddingInferenceCapabilities.value, val)
const removeEmbeddingOrphan = (val: string) => {
  embeddingOrphanCapabilities.value = embeddingOrphanCapabilities.value.filter((c) => c !== val)
}

const toggleImageFeature = (val: string) => toggleInList(imageFeatureCapabilities.value, val)
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
  chatFeatureCapabilities.value = []
  chatInferenceCapabilities.value = []
  chatOrphanCapabilities.value = []
  embeddingFeatureCapabilities.value = []
  embeddingInferenceCapabilities.value = []
  embeddingOrphanCapabilities.value = []
  imageFeatureCapabilities.value = []
  imageGenCapabilities.value = []
  imageOrphanCapabilities.value = []

  if (modelType === 'chat') {
    chatFeatureCapabilities.value = caps.filter((c) => CHAT_FEATURE_SET.has(c))
    chatInferenceCapabilities.value = caps.filter((c) => CHAT_INFERENCE_SET.has(c))
    chatOrphanCapabilities.value = caps.filter((c) => !CHAT_ALL_KNOWN_SET.has(c))
  } else if (modelType === 'embedding') {
    embeddingFeatureCapabilities.value = caps.filter((c) => EMBEDDING_FEATURE_SET.has(c))
    embeddingInferenceCapabilities.value = caps.filter((c) => EMBEDDING_INFERENCE_SET.has(c))
    embeddingOrphanCapabilities.value = caps.filter((c) => !EMBEDDING_ALL_KNOWN_SET.has(c))
  } else if (modelType === 'image') {
    imageFeatureCapabilities.value = caps.filter((c) => IMAGE_FEATURE_SET.has(c))
    imageGenCapabilities.value = caps.filter((c) => IMAGE_GEN_SET.has(c))
    imageOrphanCapabilities.value = caps.filter((c) => !IMAGE_ALL_KNOWN_SET.has(c))
  }
}

const syncForm = () => {
  currentStep.value = 0
  if (props.mode === 'create' || !props.initialData) {
    Object.assign(form, {
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

watch(() => form.modelType, () => {
  partitionCapabilities([], String(form.modelType || 'chat'))
})

const handleSubmit = async () => {
  await formRef.value?.validate()
  const payload = { ...form }
  
  let allCapabilities: string[] = []
  if (form.modelType === 'chat') {
    allCapabilities = [
      ...chatFeatureCapabilities.value,
      ...chatInferenceCapabilities.value,
      ...chatOrphanCapabilities.value
    ]
  } else if (form.modelType === 'embedding') {
    allCapabilities = [
      ...embeddingFeatureCapabilities.value,
      ...embeddingInferenceCapabilities.value,
      ...embeddingOrphanCapabilities.value
    ]
  } else if (form.modelType === 'image') {
    allCapabilities = [
      ...imageFeatureCapabilities.value,
      ...imageGenCapabilities.value,
      ...imageOrphanCapabilities.value
    ]
  }
  
  payload.capabilities = allCapabilities.length > 0 ? JSON.stringify(allCapabilities) : ''
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
  display: flex; justify-content: space-between; align-items: center;
  background: #f8f9fb; padding: 12px 16px; border-radius: 12px; border: 1px solid #eef1f6;
}
.status-toggle-card .t { display: block; font-size: 13px; font-weight: 600; }
.status-toggle-card .d { font-size: 12px; color: #999; }

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