<template>
  <AstModal
      :open="visible"
      :width="680"
      :body-height="620"
      :max-width="700"
      :footer="null"
      wrap-class-name="ai-account-form-modal"
      @update:open="emit('update:visible', $event)"
  >
    <template #header-title>{{ isEdit ? t.form.titleEdit : t.form.titleCreate }}</template>
    <template #header-subtitle>{{ t.form.subtitle }}</template>

    <div class="account-form-container">
      <!-- 步骤进度指示器 -->
      <div class="stepper-bar">
        <div
            v-for="(step, index) in steps"
            :key="step.key"
            class="stepper-item"
            :class="{
              'active': currentStep === index + 1,
              'completed': currentStep > index + 1
            }"
        >
          <div class="stepper-dot">
            <component v-if="currentStep > index + 1" :is="CheckOutlined" class="w-4 h-4"/>
            <span v-else>{{ index + 1 }}</span>
          </div>
          <span class="stepper-label">{{ step.label }}</span>
        </div>
        <div
            v-for="i in 2"
            :key="'line-' + i"
            class="stepper-line"
            :class="{ completed: currentStep > i }"
        />
      </div>

      <!-- 表单内容区域 -->
      <a-spin :spinning="loading">
        <a-form :model="form" layout="vertical" class="account-form">
          <!-- 第一步：基础信息 -->
          <div
              v-show="currentStep === 1"
              class="step-content"
          >
            <a-form-item :label="t.form.labelAccountKey" name="accountKey">
              <div class="key-input-wrapper">
                <span class="key-prefix">{{ AI_ACCOUNT_KEY_PREFIX }}</span>
                <a-input
                    :value="form.accountKey"
                    :disabled="accountKeyImmutable"
                    :placeholder="accountKeyImmutable ? '' : t.form.placeholderAccountKey"
                    class="key-input"
                />
                <a-button
                    v-if="!accountKeyImmutable"
                    type="text"
                    class="key-refresh-btn"
                    @click="generateAccountKey"
                >
                  <ReloadOutlined :class="{ spinning: isGeneratingKey }"/>
                </a-button>
              </div>
            </a-form-item>

            <a-form-item
                :rules="[{ required: true, message: t.form.validationSelectProvider }]"
                :label="t.form.labelExtensionCode"
                name="extensionCode"
            >
              <ExtensionSelector
                  v-model:value="form.extensionCode"
                  :allow-clear="true"
                  :only-applied="true"
                  :placeholder="t.form.placeholderExtensionCode"
                  size="large"
              />
            </a-form-item>

            <a-form-item
                :rules="[{ required: true, message: t.form.validationAccountName }]"
                :label="t.form.labelAccountName"
                name="accountName"
            >
              <a-input
                  v-model:value="form.accountName"
                  allow-clear
                  :placeholder="t.form.placeholderAccountName"
                  size="large"
              />
            </a-form-item>

            <a-form-item :label="t.form.labelStatus" name="status">
              <div class="status-toggle">
                <div
                    class="status-toggle-bg"
                    :class="{ 'active': form.status === 'enabled' }"
                />
                <a-button
                    type="text"
                    class="status-btn"
                    :class="{ active: form.status === 'enabled' }"
                    @click="form.status = 'enabled'"
                >
                  <span class="status-dot enabled"></span>
                  {{ t.form.statusEnabled }}
                </a-button>
                <a-button
                    type="text"
                    class="status-btn"
                    :class="{ active: form.status === 'disabled' }"
                    @click="form.status = 'disabled'"
                >
                  <span class="status-dot disabled"></span>
                  {{ t.form.statusDisabled }}
                </a-button>
              </div>
            </a-form-item>
          </div>

          <!-- 第二步：接口凭证 -->
          <div
              v-show="currentStep === 2"
              class="step-content"
          >
            <a-form-item :label="t.form.labelApiUrl" name="apiUrl">
              <a-input
                  v-model:value="form.apiUrl"
                  allow-clear
                  :placeholder="t.form.placeholderApiUrl"
                  size="large"
                  class="icon-input"
              >
                <template #prefix>
                  <LinkOutlined class="input-icon"/>
                </template>
              </a-input>
            </a-form-item>

            <a-form-item :label="t.form.labelApiKey" name="apiKey">
              <a-input-password
                  v-model:value="form.apiKey"
                  :placeholder="t.form.placeholderApiKey"
                  size="large"
                  :visibility-toggle="{ visible: apiKeyVisible, onVisibleChange: handleApiKeyVisibleChange }"
                  class="icon-input"
              >
                <template #prefix>
                  <KeyOutlined class="input-icon"/>
                </template>
              </a-input-password>
            </a-form-item>

            <a-form-item :label="t.form.labelApiSecret" name="apiSecret">
              <a-input-password
                  v-model:value="form.apiSecret"
                  :placeholder="t.form.placeholderApiSecret"
                  size="large"
                  :visibility-toggle="{ visible: apiSecretVisible, onVisibleChange: handleApiSecretVisibleChange }"
                  class="icon-input"
              >
                <template #prefix>
                  <LockOutlined class="input-icon"/>
                </template>
              </a-input-password>
            </a-form-item>
          </div>

          <!-- 第三步：额度控制 -->
          <div
              v-show="currentStep === 3"
              class="step-content"
          >
            <a-form-item :label="t.form.labelAccountTokens" name="accountTokens">
              <a-input
                  v-model:value="form.accountTokens"
                  type="number"
                  :min="0"
                  class="w-full icon-input"
                  :placeholder="t.form.placeholderAccountTokens"
                  size="large"
              >
                <template #prefix>
                  <CheckCircleOutlined class="input-icon"/>
                </template>
              </a-input>
              <p class="tokens-hint">
                {{ t.form.tokensHint }}
              </p>
            </a-form-item>

            <!-- 配置摘要卡片 -->
            <div class="summary-card">
              <h4 class="summary-title">{{ t.form.summaryTitle }}</h4>
              <div class="summary-grid">
                <div class="summary-item">
                  <span class="summary-label">{{ t.form.summaryAccountKey }}</span>
                  <span class="summary-value">{{ AI_ACCOUNT_KEY_PREFIX }}{{ form.accountKey }}</span>
                </div>
                <div class="summary-item">
                  <span class="summary-label">{{ t.form.summaryProvider }}</span>
                  <span class="summary-value">{{ selectedProviderName }}</span>
                </div>
                <div class="summary-item">
                  <span class="summary-label">{{ t.form.summaryAccountName }}</span>
                  <span class="summary-value">{{ form.accountName || '-' }}</span>
                </div>
                <div class="summary-item">
                  <span class="summary-label">{{ t.form.summaryStatus }}</span>
                  <span
                      class="summary-value"
                      :class="{ enabled: form.status === 'enabled', disabled: form.status === 'disabled' }"
                  >
                    {{ form.status === 'enabled' ? t.form.statusEnabled : t.form.statusDisabled }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </a-form>
      </a-spin>

      <!-- 底部按钮区域 -->
      <div class="form-footer">
        <div class="footer-left">
          <a-button
              v-if="currentStep > 1"
              type="text"
              class="footer-btn prev-btn"
              @click="prevStep"
          >
            <ArrowLeftOutlined/>
            {{ t.form.btnPrev }}
          </a-button>
          <a-button
              v-else
              type="text"
              class="footer-btn cancel-btn"
              @click="handleCancel"
          >
            {{ t.form.btnCancel }}
          </a-button>
        </div>
        <a-button
            :loading="submitting"
            type="primary"
            class="footer-btn next-btn"
            :class="{ 'create-btn': currentStep === 3 }"
            @click="handleNext"
        >
          <span>{{ currentStep === 3 ? (isEdit ? t.form.btnSubmitSave : t.form.btnSubmitCreate) : t.form.btnNext }}</span>
          <CheckOutlined v-if="currentStep === 3"/>
          <ArrowRightOutlined v-else/>
        </a-button>
      </div>
    </div>
  </AstModal>
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import {message} from 'ant-design-vue'
import {
  ArrowLeftOutlined,
  ArrowRightOutlined,
  CheckCircleOutlined,
  CheckOutlined,
  KeyOutlined,
  LinkOutlined,
  LockOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue'
import AstModal from '@/components/home/AstModal.vue'
import ExtensionSelector from '@/views/admin/system-config/system-extension/selector/ExtensionSelector.vue'
import {type AiAccount, aiAccountApi} from '@/api/aiAccount.ts'
import {AI_ACCOUNT_KEY_PREFIX} from '@/constants/aiConfigKeyPrefixes.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const PROVIDER_DEFAULT_URLS: Record<string, string> = {
  openai: 'https://api.openai.com',
  deepseek: 'https://api.deepseek.com',
  ali: 'https://dashscope.aliyuncs.com',
  qianfan: 'https://aip.baidubce.com',
  zhipu: 'https://open.bigmodel.cn/api/paas/v4',
  xiaomi: 'https://api.xiaomimimo.com/v1',
  anthropic: 'https://api.anthropic.com',
  baichuan: 'https://api.baichuan-ai.com/v1',
  gemini: 'https://generativelanguage.googleapis.com',
  minimax: 'https://api.minimax.chat/v1',
  moonshot: 'https://api.moonshot.cn/v1',
  tencent: 'https://api.lkeap.cloud.tencent.com/v1',
  volcengine: 'https://ark.cn-beijing.volces.com',
  ollama: 'http://localhost:11434',
}

const t = usePageTranslation('ai-account')

interface Props {
  visible: boolean
  record?: AiAccount
}

const props = defineProps<Props>()
const emit = defineEmits(['update:visible', 'success'])

const loading = ref(false)
const submitting = ref(false)
const accountKeyImmutable = ref(false)
const isEdit = computed(() => !!props.record?.id)
const currentStep = ref(1)
const isGeneratingKey = ref(false)
const apiKeyVisible = ref(false)
const apiSecretVisible = ref(false)

const steps = [
  {key: 'basic', label: t.value.form.stepBasicInfo},
  {key: 'api', label: t.value.form.stepApiCredential},
  {key: 'quota', label: t.value.form.stepQuotaControl}
]

const form = reactive<AiAccount>({
  accountKey: '',
  accountName: '',
  extensionCode: undefined,
  apiUrl: '',
  apiKey: '',
  apiSecret: '',
  accountTokens: undefined,
  status: 'enabled'
})

const selectedProviderName = computed(() => {
  if (!form.extensionCode) return '-'
  const names: Record<string, string> = {
    openai: 'OpenAI (GPT-4o, GPT-3.5)',
    anthropic: 'Anthropic Claude',
    gemini: 'Google Gemini',
    deepseek: 'DeepSeek',
    cohere: 'Cohere',
    custom: 'Custom API'
  }
  return names[form.extensionCode] || form.extensionCode
})

const handleCancel = () => {
  emit('update:visible', false)
  currentStep.value = 1
}

const generateAccountKey = () => {
  isGeneratingKey.value = true
  setTimeout(() => {
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'
    let randomString = ''
    for (let i = 0; i < 16; i++) {
      randomString += characters.charAt(Math.floor(Math.random() * characters.length))
    }
    form.accountKey = randomString
    isGeneratingKey.value = false
  }, 500)
}

const handleApiKeyVisibleChange = (visible: boolean) => {
  apiKeyVisible.value = visible
}

const handleApiSecretVisibleChange = (visible: boolean) => {
  apiSecretVisible.value = visible
}

const loadDetail = async (id: string | number) => {
  loading.value = true
  try {
    const detail = await aiAccountApi.detail(id)
    Object.assign(form, detail, {
      provider: detail.provider || undefined,
      apiUrl: detail.apiUrl || '',
      status: detail.status || 'enabled'
    })
    accountKeyImmutable.value = detail.accountKeyImmutable === true
  } catch (e: any) {
    message.error(e?.message || t.value.form.errorLoadDetail)
  } finally {
    loading.value = false
  }
}

const validateStep = (step: number): boolean => {
  if (step === 1) {
    if (!form.extensionCode) {
      message.error(t.value.form.validationSelectProvider)
      return false
    }
    if (!form.accountName?.trim()) {
      message.error(t.value.form.validationAccountName)
      return false
    }
  } else if (step === 2) {
    const apiUrl = form.apiUrl?.trim()
    if (apiUrl && !apiUrl.startsWith('http://') && !apiUrl.startsWith('https://')) {
      message.error(t.value.form.validationApiUrl)
      return false
    }
  }
  return true
}

const prevStep = () => {
  if (currentStep.value > 1) {
    currentStep.value--
  }
}

const nextStep = () => {
  if (validateStep(currentStep.value)) {
    if (currentStep.value < 3) {
      currentStep.value++
    }
  }
}

const handleNext = () => {
  if (currentStep.value < 3) {
    nextStep()
  } else {
    onSubmit()
  }
}

const onSubmit = async () => {
  if (!form.accountName?.trim()) {
    message.error(t.value.form.errorAccountNameRequired)
    return
  }
  submitting.value = true
  try {
    const payload = {...form}
    if (!isEdit.value) delete payload.id
    const msg = isEdit.value ? await aiAccountApi.update(payload) : await aiAccountApi.create(payload)
    message.success(msg || t.value.form.successOperation)
    emit('update:visible', false)
    emit('success')
    currentStep.value = 1
  } catch (e: any) {
    message.error(e?.message || t.value.form.errorSubmit)
  } finally {
    submitting.value = false
  }
}

watch(() => props.visible, (val) => {
  if (val) {
    Object.assign(form, {
      id: undefined,
      accountKey: '',
      accountName: '',
      provider: undefined,
      apiUrl: '',
      apiKey: '',
      apiSecret: '',
      accountTokens: undefined,
      status: 'enabled'
    })
    accountKeyImmutable.value = false
    currentStep.value = 1
    apiKeyVisible.value = false
    apiSecretVisible.value = false

    if (props.record?.id) {
      loadDetail(props.record.id)
    } else {
      generateAccountKey()
    }
  }
})

watch(() => form.extensionCode, (code) => {
  if (code && !form.apiUrl) {
    const defaultUrl = PROVIDER_DEFAULT_URLS[code]
    if (defaultUrl) {
      form.apiUrl = defaultUrl
    }
  }
})
</script>

<style scoped>
.ai-account-form-modal {
  max-width: 560px !important;
}

.account-form-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 0 24px;
}

.stepper-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 0;
  border-bottom: 1px solid var(--border-default);
  margin-bottom: 20px;
}

.stepper-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  flex: 1;
}

.stepper-dot {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--bg-elevated);
  color: var(--text-muted);
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.stepper-item.active .stepper-dot {
  background: var(--primary);
  color: #fff;
  box-shadow: 0 0 0 4px color-mix(in srgb, var(--primary) 20%, transparent);
}

.stepper-item.completed .stepper-dot {
  background: var(--success);
  color: #fff;
}

.stepper-label {
  font-size: 12px;
  color: var(--text-muted);
  font-weight: 500;
  transition: color 0.3s ease;
}

.stepper-item.active .stepper-label {
  color: var(--text-heading);
  font-weight: 600;
}

.stepper-item.completed .stepper-label {
  color: var(--success);
}

.stepper-line {
  flex: 1;
  height: 2px;
  background: var(--border-default);
  margin: 0 8px;
  border-radius: 1px;
  transition: background 0.3s ease;
}

.stepper-line.completed {
  background: var(--success);
}

.account-form {
  flex: 1;
  overflow-y: auto;
}

.step-content {
  animation: stepFadeIn 0.25s ease;
}

@keyframes stepFadeIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.key-input-wrapper {
  display: flex;
  align-items: center;
  border: 1px solid var(--border-input);
  border-radius: 8px;
  overflow: hidden;
  background: var(--bg-input);
}

.key-prefix {
  padding: 0 12px;
  font-family: monospace;
  font-size: 14px;
  color: var(--text-muted);
  border-right: 1px solid var(--border-input);
  height: 40px;
  display: flex;
  align-items: center;
}

.key-input {
  flex: 1;
  border: none;
  border-radius: 0;
}

.key-refresh-btn {
  padding: 0 12px;
  color: var(--primary);
}

.key-refresh-btn:hover {
  background: color-mix(in srgb, var(--primary) 10%, transparent);
}

.spinning {
  animation: spin 0.5s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.status-toggle {
  display: flex;
  align-items: center;
  background: var(--bg-elevated);
  border-radius: 8px;
  padding: 2px;
  position: relative;
  height: 44px;
}

.status-toggle-bg {
  position: absolute;
  top: 2px;
  left: 2px;
  right: 50%;
  bottom: 2px;
  background: #fff;
  border-radius: 6px;
  box-shadow: var(--shadow-sm);
  transition: transform 0.3s ease;
}

.status-toggle-bg.active {
  transform: translateX(100%);
}

.status-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 40px;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-muted);
  transition: color 0.3s ease;
  z-index: 1;
}

.status-btn.active {
  color: var(--primary);
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-dot.enabled {
  background: var(--success);
}

.status-dot.disabled {
  background: var(--text-muted);
}

.icon-input {
  padding-left: 36px;
}

.input-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-muted);
  font-size: 14px;
}

.tokens-hint {
  margin-top: 8px;
  font-size: 13px;
  color: var(--text-muted);
  line-height: 1.5;
}

.summary-card {
  background: color-mix(in srgb, var(--primary) 5%, transparent);
  border: 1px solid color-mix(in srgb, var(--primary) 15%, transparent);
  border-radius: 12px;
  padding: 20px;
  margin-top: 16px;
}

.summary-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--primary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 16px;
}

.summary-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.summary-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.summary-label {
  font-size: 12px;
  color: var(--text-muted);
}

.summary-value {
  font-size: 14px;
  color: var(--text-heading);
  font-weight: 500;
  font-family: inherit;
}

.summary-value.enabled {
  color: var(--success);
}

.summary-value.disabled {
  color: var(--text-muted);
}

.form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-top: 1px solid var(--border-default);
  margin-top: 20px;
}

.footer-left {
  display: flex;
  align-items: center;
}

.footer-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.cancel-btn {
  color: var(--text-muted);
}

.cancel-btn:hover {
  color: var(--text-heading);
  background: var(--bg-elevated);
}

.prev-btn {
  color: var(--text-muted);
}

.prev-btn:hover {
  color: var(--text-heading);
  background: var(--bg-elevated);
}

.next-btn {
  background: var(--primary);
  color: #fff;
  padding: 8px 20px;
}

.next-btn:hover {
  background: color-mix(in srgb, var(--primary) 90%, #000);
}

.next-btn.create-btn {
  background: var(--success);
}

.next-btn.create-btn:hover {
  background: color-mix(in srgb, var(--success) 90%, #000);
}
</style>
