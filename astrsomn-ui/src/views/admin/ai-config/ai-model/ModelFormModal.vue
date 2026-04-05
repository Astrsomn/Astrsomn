<template>
  <AstrsomnModal
    :open="open"
    width="80vw"
    max-width="80vw"
    body-height="80vh"
    :closable="true"
    main-padding="0"
    wrap-class-name="model-form-fsm-wrap"
    @update:open="emit('update:open', $event)"
    @cancel="onCancel"
  >
    <template #header-logo>
      <span class="header-type-icon" :class="form.modelType">
        <template v-if="form.modelType === 'chat'"><MessageOutlined /></template>
        <template v-else-if="form.modelType === 'embedding'"><PartitionOutlined /></template>
        <template v-else><PictureOutlined /></template>
      </span>
    </template>
    <template #header-title>
      {{ mode === 'create' ? '注册接入端点' : mode === 'view' ? '查看端点配置' : '编辑端点配置' }}
    </template>
    <template #header-subtitle>
      左侧填写基本信息，右侧勾选能力并设置资源约束
    </template>

    <div class="model-form-shell">
    <a-form
      ref="formRef"
      :model="form"
      :rules="rules"
      layout="vertical"
      class="model-form-root"
    >
      <div class="model-form-split">
        <div class="model-form-pane model-form-pane--left">
          <div class="form-section">
            <h3 class="section-headline"><IdcardOutlined /> 端点身份识别</h3>
            <div class="form-grid">
              <a-form-item label="服务供应商 (Provider)" name="provider">
                <ModelProviderSelect
                  v-model:value="form.provider"
                  placeholder="请选择端点所属服务商"
                  size="large"
                  :disabled="props.mode === 'view'"
                />
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
                <div class="endpoint-status-btns" :class="{ 'endpoint-status-btns--disabled': props.mode === 'view' }">
                  <button
                    type="button"
                    class="endpoint-status-btn endpoint-status-btn--enable"
                    :class="{ 'is-selected': form.status === 'enabled' }"
                    :disabled="props.mode === 'view'"
                    @click="setEndpointStatus('enabled')"
                  >
                    启用
                  </button>
                  <button
                    type="button"
                    class="endpoint-status-btn endpoint-status-btn--disable"
                    :class="{ 'is-selected': form.status === 'disabled' }"
                    :disabled="props.mode === 'view'"
                    @click="setEndpointStatus('disabled')"
                  >
                    禁用
                  </button>
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

        <div class="model-form-divider" aria-hidden="true" />

        <div class="model-form-pane model-form-pane--right">
          <div class="form-section">
            <h3 class="section-headline"><ThunderboltOutlined /> 端点能力（写入 Capabilities）</h3>

            <div v-if="form.modelType === 'chat'" class="capability-panel-section">
              <h4 class="cap-panel-title">对话端点能力 · InferenceParamEnum</h4>
              <div class="cap-tag-grid">
                <div
                  v-for="opt in chatInferenceOptions"
                  :key="opt.value"
                  :class="['custom-cap-tag', { active: chatInferenceCapabilities.includes(opt.value) }]"
                  @click="props.mode !== 'view' && toggleChatInference(opt.value)"
                  :style="{ cursor: props.mode === 'view' ? 'default' : 'pointer' }"
                >
                  <div class="custom-cap-tag__body">
                    <CheckCircleFilled v-if="chatInferenceCapabilities.includes(opt.value)" class="custom-cap-tag__check" />
                    <div class="custom-cap-tag__text">
                      <span class="custom-cap-tag__title">{{ opt.titleZh }}</span>
                      <span class="custom-cap-tag__field">{{ opt.fieldCode }}</span>
                    </div>
                  </div>
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
                    <div class="custom-cap-tag__body">
                      <div class="custom-cap-tag__text">
                        <span class="custom-cap-tag__title">{{ capabilityOrphanTitle(c) }}</span>
                        <span class="custom-cap-tag__field">{{ c }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="form.modelType === 'embedding'" class="capability-panel-section">
              <h4 class="cap-panel-title">向量端点能力 · EmbeddingInferenceParamEnum</h4>
              <div class="cap-tag-grid">
                <div
                  v-for="opt in embeddingInferenceOptions"
                  :key="opt.value"
                  :class="['custom-cap-tag', { active: embeddingInferenceCapabilities.includes(opt.value) }]"
                  @click="props.mode !== 'view' && toggleEmbeddingInference(opt.value)"
                  :style="{ cursor: props.mode === 'view' ? 'default' : 'pointer' }"
                >
                  <div class="custom-cap-tag__body">
                    <CheckCircleFilled v-if="embeddingInferenceCapabilities.includes(opt.value)" class="custom-cap-tag__check" />
                    <div class="custom-cap-tag__text">
                      <span class="custom-cap-tag__title">{{ opt.titleZh }}</span>
                      <span class="custom-cap-tag__field">{{ opt.fieldCode }}</span>
                    </div>
                  </div>
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
                    <div class="custom-cap-tag__body">
                      <div class="custom-cap-tag__text">
                        <span class="custom-cap-tag__title">{{ capabilityOrphanTitle(c) }}</span>
                        <span class="custom-cap-tag__field">{{ c }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="form.modelType === 'image'" class="capability-panel-section">
              <h4 class="cap-panel-title">图像端点能力 · ImageGenParamEnum</h4>
              <div class="cap-tag-grid">
                <div
                  v-for="opt in imageGenOptions"
                  :key="opt.value"
                  :class="['custom-cap-tag', { active: imageGenCapabilities.includes(opt.value) }]"
                  @click="props.mode !== 'view' && toggleImageGen(opt.value)"
                  :style="{ cursor: props.mode === 'view' ? 'default' : 'pointer' }"
                >
                  <div class="custom-cap-tag__body">
                    <CheckCircleFilled v-if="imageGenCapabilities.includes(opt.value)" class="custom-cap-tag__check" />
                    <div class="custom-cap-tag__text">
                      <span class="custom-cap-tag__title">{{ opt.titleZh }}</span>
                      <span class="custom-cap-tag__field">{{ opt.fieldCode }}</span>
                    </div>
                  </div>
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
                    <div class="custom-cap-tag__body">
                      <div class="custom-cap-tag__text">
                        <span class="custom-cap-tag__title">{{ capabilityOrphanTitle(c) }}</span>
                        <span class="custom-cap-tag__field">{{ c }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
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
      </div>
    </a-form>

    <div class="modal-footer-action" v-if="props.mode !== 'view'">
      <div class="footer-left">
        <LockOutlined /> 端点变更将影响下游所有推理实例
      </div>
      <div class="footer-right">
        <a-button type="primary" class="btn-submit" :loading="confirmLoading" @click="handleSubmit">
          确认并保存端点
        </a-button>
      </div>
    </div>
    <div class="modal-footer-action" v-else>
      <div class="footer-right">
        <a-button type="primary" @click="emit('update:open', false)">关闭</a-button>
      </div>
    </div>
    </div>
  </AstrsomnModal>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import {
  IdcardOutlined, ReloadOutlined, MessageOutlined,
  PartitionOutlined, LockOutlined, ThunderboltOutlined,
  CheckCircleFilled, ControlOutlined, PictureOutlined, GlobalOutlined
} from '@ant-design/icons-vue'
import AstrsomnModal from '@/components/home/AstrsomnModal.vue'
import ModelProviderSelect from './ModelProviderSelect.vue'
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
      statusOptions: { label: string; value: string }[]
      submitHandler: (payload: AiModel) => Promise<void>
    }>(),
    { confirmLoading: false, initialData: null }
)

const emit = defineEmits(['update:open'])

const formRef = ref<FormInstance | null>(null)

const accountList = ref<AiAccount[]>([])
const accountOptionsLoading = ref(false)
/** 编辑时由详情接口返回：被推理实例引用则不可改 modelKey */
const modelKeyImmutable = ref(false)

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

/** 能力卡片：上行中文说明，下行字段 code（与 Capabilities JSON 一致） */
function capOptionRow(code: string) {
  const titleZh = aiModelCapabilitiesDictionary.getLabel(code) ?? code
  return { value: code, titleZh, fieldCode: code }
}

function capabilityOrphanTitle(code: string): string {
  const zh = aiModelCapabilitiesDictionary.getLabel(code)
  if (zh && zh !== code) return zh
  return '历史 / 非枚举标签'
}

function capOptions(codes: readonly string[]) {
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

  return codes
    .filter((value) => !aliasParams.has(value))
    .map((value) => capOptionRow(value))
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

const setEndpointStatus = (status: 'enabled' | 'disabled') => {
  form.status = status
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
/* 80vw×80vh 弹层在视口中居中 */
:global(.model-form-fsm-wrap.ant-modal-wrap) {
  display: flex;
  align-items: center;
  justify-content: center;
}

:global(.model-form-fsm-wrap .ant-modal) {
  top: 0;
  padding-bottom: 0;
}

.header-type-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: #fff;
}

.header-type-icon.chat {
  color: #bae6fd;
}

.header-type-icon.embedding {
  color: #e9d5ff;
}

.header-type-icon.image {
  color: #fef08a;
}

.model-form-shell {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  overflow: hidden;
}

.model-form-root {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.model-form-split {
  display: flex;
  flex: 1;
  min-height: 0;
  gap: 0;
  align-items: stretch;
}

.model-form-pane {
  flex: 1;
  min-width: 0;
  min-height: 0;
  overflow-y: auto;
  padding: 20px 24px;
  box-sizing: border-box;
}

.model-form-pane--left {
  background: #fff;
}

.model-form-pane--right {
  background: #f8fafc;
}

.model-form-divider {
  width: 1px;
  flex-shrink: 0;
  background: #e2e8f0;
  align-self: stretch;
}

.model-form-pane::-webkit-scrollbar {
  width: 6px;
}

.model-form-pane::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 4px;
}

.section-headline {
  font-size: 14px;
  font-weight: 600;
  margin: 0 0 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #444;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 24px;
}

.span-2 {
  grid-column: span 2;
}

.endpoint-status-btns {
  display: flex;
  gap: 12px;
  max-width: 420px;
}

.endpoint-status-btns--disabled {
  opacity: 0.85;
}

.endpoint-status-btn {
  flex: 1;
  min-height: 40px;
  padding: 0 16px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  border: 1px solid #d9d9d9;
  background: #fff;
  color: #595959;
  transition:
    background 0.2s,
    border-color 0.2s,
    color 0.2s,
    box-shadow 0.2s;
}

.endpoint-status-btn:disabled {
  cursor: not-allowed;
  opacity: 0.65;
}

.endpoint-status-btn--enable:not(:disabled):hover {
  border-color: #73d13d;
  color: #389e0d;
}

.endpoint-status-btn--enable.is-selected {
  background: #52c41a;
  border-color: #52c41a;
  color: #fff;
  box-shadow: 0 1px 2px rgba(82, 196, 26, 0.35);
}

.endpoint-status-btn--disable:not(:disabled):hover {
  border-color: #ff7875;
  color: #cf1322;
}

.endpoint-status-btn--disable.is-selected {
  background: #ff4d4f;
  border-color: #ff4d4f;
  color: #fff;
  box-shadow: 0 1px 2px rgba(255, 77, 79, 0.35);
}

:deep(.ant-segmented-item-selected) {
  background-color: #1890ff !important;
  color: white !important;
}

:deep(.ant-segmented-item-selected:hover) {
  background-color: #40a9ff !important;
  color: white !important;
}

.cap-panel-title {
  margin: 0 0 12px;
  font-size: 13px;
  font-weight: 600;
  color: #334155;
}

.capability-panel-section + .capability-panel-section {
  margin-top: 8px;
}

.cap-subhead {
  font-size: 12px;
  font-weight: 600;
  color: #555;
  margin: 16px 0 10px;
}

.cap-subhead:first-of-type {
  margin-top: 0;
}

.cap-subhead.muted {
  color: #888;
  font-weight: 500;
}

.orphan-block {
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px dashed #e8e8e8;
}

.cap-tag-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.custom-cap-tag {
  min-height: 72px;
  padding: 12px 14px;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  cursor: pointer;
  transition:
    border-color 0.2s,
    background 0.2s,
    box-shadow 0.2s;
  box-sizing: border-box;
}

.custom-cap-tag__body {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  min-height: 48px;
}

.custom-cap-tag__check {
  flex-shrink: 0;
  margin-top: 2px;
  font-size: 16px;
  color: #0061ff;
}

.custom-cap-tag__text {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 6px;
  min-width: 0;
  flex: 1;
}

.custom-cap-tag__title {
  font-size: 13px;
  font-weight: 600;
  color: #334155;
  line-height: 1.4;
  word-break: break-word;
}

.custom-cap-tag__field {
  font-size: 11px;
  font-weight: 500;
  color: #94a3b8;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  line-height: 1.35;
  word-break: break-all;
}

.custom-cap-tag:hover {
  border-color: #0061ff;
  box-shadow: 0 1px 4px rgba(0, 97, 255, 0.12);
}

.custom-cap-tag:hover .custom-cap-tag__title {
  color: #0061ff;
}

.custom-cap-tag.active {
  background: #eff6ff;
  border-color: #0061ff;
  box-shadow: 0 1px 4px rgba(0, 97, 255, 0.18);
}

.custom-cap-tag.active .custom-cap-tag__title {
  color: #1d4ed8;
}

.custom-cap-tag.active .custom-cap-tag__field {
  color: #64748b;
}

.custom-cap-tag.orphan {
  background: #fafafa;
  border-style: dashed;
}

.custom-cap-tag.orphan:hover {
  border-color: #ff4d4f;
  box-shadow: 0 1px 4px rgba(255, 77, 79, 0.12);
}

.custom-cap-tag.orphan:hover .custom-cap-tag__title {
  color: #cf1322;
}

.runtime-params-box {
  margin-top: 24px;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 16px;
}

.box-title {
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #111;
  display: flex;
  align-items: center;
  gap: 6px;
}

.param-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.param-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.pl {
  font-size: 11px;
  color: #999;
}

.modal-footer-action {
  flex-shrink: 0;
  padding: 14px 24px;
  background: #fff;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-left {
  font-size: 12px;
  color: #52c41a;
  display: flex;
  align-items: center;
  gap: 4px;
}

.btn-submit {
  border-radius: 8px;
  font-weight: 600;
  padding: 0 24px;
  height: 38px;
}

.input-action-icon:hover {
  color: #ff4d4f;
  cursor: pointer;
}
</style>