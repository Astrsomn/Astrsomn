<template>
  <AstModal
      :open="open"
      :width="640"
      :body-height="520"
      :max-width="700"
      :footer="null"
      wrap-class-name="vec-source-form-modal"
      @update:open="emit('update:open', $event)"
  >
    <template #header-logo>
      <DatabaseOutlined/>
    </template>
    <template #header-title>
      {{ mode === 'create' ? t.vecSource.form.createTitle : t.vecSource.form.editTitle }}
    </template>
    <template #header-subtitle>
      {{ t.vecSource.form.subtitle }}
    </template>

    <div class="form-container">
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
            class="stepper-line"
            :class="{ completed: currentStep > 1 }"
        />
      </div>

      <!-- 表单内容区域 -->
      <a-spin :spinning="loading">
        <a-form :model="form" layout="vertical" class="modal-form">
          <!-- 第一步：基础信息 -->
          <div v-show="currentStep === 1" class="step-content">
            <a-form-item :label="t.vecSource.form.name.label" name="name"
                         :rules="[{ required: true, message: t.vecSource.form.validation.nameRequired }]">
              <a-input v-model:value="form.name" size="large"/>
            </a-form-item>

            <a-form-item :label="t.vecSource.form.extensionCode.label" name="extensionCode"
                         :rules="[{ required: true, message: t.vecSource.form.validation.extensionCodeRequired }]">
              <ExtensionSelector
                  :value="form.extensionCode"
                  allow-clear
                  extension-type="VECTOR_STORE"
                  size="large"
                  @update:value="onExtensionChange"
              />
            </a-form-item>

            <a-form-item :label="t.vecSource.form.status.label" name="status">
              <AstStatusToggle
                  v-model="form.status"
                  :enabled-label="t.vecSource.form.enabled"
                  :disabled-label="t.vecSource.form.disabled"
              />
            </a-form-item>
          </div>

          <!-- 第二步：连接配置 -->
          <div v-show="currentStep === 2" class="step-content">
            <template v-if="form.extensionCode">
              <div class="form-grid">
                <a-form-item
                    v-for="code in visibleParamCodes"
                    :key="code"
                    :label="paramMeta[code].label"
                    :name="code"
                >
                  <a-input-password
                      v-if="paramMeta[code].password"
                      v-model:value="formRow[code]"
                      size="large"
                      autocomplete="off"
                  >
                    <template v-if="paramMeta[code].prefix" #prefix>
                      <component :is="paramMeta[code].prefix" class="input-prefix-icon"/>
                    </template>
                  </a-input-password>
                  <a-input
                      v-else
                      v-model:value="formRow[code]"
                      size="large"
                  >
                    <template v-if="paramMeta[code].prefix" #prefix>
                      <component :is="paramMeta[code].prefix" class="input-prefix-icon"/>
                    </template>
                  </a-input>
                </a-form-item>
              </div>

              <a-collapse v-model:activeKey="advancedKeys" ghost>
                <a-collapse-panel key="adv" :header="t.vecSource.form.advancedConfig">
                  <a-form-item label="JSON" name="configJson">
                    <a-textarea
                        v-model:value="form.configJson"
                        :auto-size="{ minRows: 2, maxRows: 5 }"
                        class="mono-text"
                    />
                  </a-form-item>
                </a-collapse-panel>
              </a-collapse>
            </template>
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
            {{ t.vecSource.form.btnPrev }}
          </a-button>
          <a-button
              v-else
              type="text"
              class="footer-btn cancel-btn"
              @click="handleCancel"
          >
            {{ t.vecSource.form.btnCancel }}
          </a-button>
        </div>
        <div class="footer-right">
          <a-button
              v-if="currentStep === 2 && form.extensionCode"
              :loading="testLoading"
              class="footer-btn test-btn"
              @click="testConnection"
          >
            <ApiOutlined/>
            {{ t.vecSource.form.testConnection }}
          </a-button>
          <a-button
              :loading="confirmLoading"
              type="primary"
              class="footer-btn next-btn"
              :class="{ 'create-btn': currentStep === 2 }"
              @click="handleNext"
          >
            <span>{{ currentStep === 2 ? (mode === 'edit' ? t.vecSource.form.btnSave : t.vecSource.form.btnCreate) : t.vecSource.form.btnNext }}</span>
            <CheckOutlined v-if="currentStep === 2"/>
            <ArrowRightOutlined v-else/>
          </a-button>
        </div>
      </div>
    </div>
  </AstModal>
</template>

<script lang="ts" setup>
import {computed, nextTick, reactive, ref, watch} from 'vue'
import {message} from 'ant-design-vue'
import {
  ApiOutlined,
  ArrowLeftOutlined,
  ArrowRightOutlined,
  CheckOutlined,
  DatabaseOutlined,
  GlobalOutlined,
  KeyOutlined,
  LockOutlined,
  UserOutlined
} from '@ant-design/icons-vue'
import AstModal from '@/components/home/AstModal.vue'
import AstStatusToggle from '@/components/home/AstStatusToggle.vue'
import ExtensionSelector from '@/views/admin/system-config/system-extension/selector/ExtensionSelector.vue'
import type {AiVecSource} from '@/api/aiVecSource.ts'
import {aiVecSourceApi} from '@/api/aiVecSource.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-vector')

const props = defineProps<{
  mode: 'create' | 'edit'
  confirmLoading: boolean
  initial: AiVecSource | null
}>()
const emit = defineEmits<{
  submit: [payload: AiVecSource]
  'update:open': [value: boolean]
}>()
const open = defineModel<boolean>('open', {required: true})

const loading = ref(false)
const testLoading = ref(false)
const currentStep = ref(1)
const advancedKeys = ref<string | string[]>([])
const syncingInitial = ref(false)

const steps = [
  {key: 'basic', label: t.value.vecSource.form.basicInfo},
  {key: 'connection', label: t.value.vecSource.form.connectionConfig}
]

type ParamMeta = {
  label: string
  placeholder: string
  password?: boolean
  prefix?: typeof GlobalOutlined
}

const paramMeta = computed<Record<string, ParamMeta>>(() => ({
  host: {label: t.value.vecSource.form.paramMeta.host.label, placeholder: t.value.vecSource.form.paramMeta.host.placeholder, prefix: GlobalOutlined},
  port: {label: t.value.vecSource.form.paramMeta.port.label, placeholder: t.value.vecSource.form.paramMeta.port.placeholder, prefix: ApiOutlined},
  databaseName: {label: t.value.vecSource.form.paramMeta.databaseName.label, placeholder: t.value.vecSource.form.paramMeta.databaseName.placeholder, prefix: DatabaseOutlined},
  username: {label: t.value.vecSource.form.paramMeta.username.label, placeholder: t.value.vecSource.form.paramMeta.username.placeholder, prefix: UserOutlined},
  password: {label: t.value.vecSource.form.paramMeta.password.label, placeholder: t.value.vecSource.form.paramMeta.password.placeholder, password: true, prefix: LockOutlined},
  token: {label: t.value.vecSource.form.paramMeta.token.label, placeholder: t.value.vecSource.form.paramMeta.token.placeholder, password: true, prefix: KeyOutlined}
}))

type FormRow = AiVecSource & Record<string, string | undefined>

function emptyForm(): FormRow {
  return {
    name: '',
    extensionCode: undefined,
    host: '',
    port: '',
    username: '',
    password: '',
    databaseName: '',
    token: '',
    configJson: '',
    status: 'enabled'
  }
}

const form = reactive<FormRow>(emptyForm())
const formRow = form as Record<string, string | undefined>

const providerParamMap: Record<string, string[]> = {
  qdrant: ['host', 'port', 'token'],
  chroma: ['host', 'port'],
  milvus: ['host', 'port', 'username', 'password', 'databaseName']
}

const paramCodes = computed(() => {
  const provider = String(form.provider || '').toLowerCase()
  return providerParamMap[provider] || ['host', 'port']
})

const visibleParamCodes = computed(() => paramCodes.value.filter((c) => Boolean(paramMeta.value[c])))

function clearConnectionFields() {
  form.host = ''
  form.port = ''
  form.username = ''
  form.password = ''
  form.databaseName = ''
  form.token = ''
  form.configJson = ''
}

function onProviderChange() {
  if (syncingInitial.value) return
  clearConnectionFields()
}

function onExtensionChange(val: string | undefined) {
  form.extensionCode = val as AiVecSource['extensionCode']
  onProviderChange()
}

function assignFromInitial(src: AiVecSource) {
  Object.assign(form, emptyForm(), src)
  if (!form.status) {
    form.status = 'enabled'
  }
}

watch(
    () => [open.value, props.initial] as const,
    async ([isOpen, initial]) => {
      if (!isOpen) return
      syncingInitial.value = true
      currentStep.value = 1
      if (initial && Object.keys(initial).length > 0) assignFromInitial(initial)
      else Object.assign(form, emptyForm())
      await nextTick()
      syncingInitial.value = false
    }
)

const validateStep = (step: number): boolean => {
  if (step === 1) {
    if (!form.name?.trim()) {
      message.error(t.value.vecSource.form.validation.nameRequired)
      return false
    }
    if (!form.extensionCode) {
      message.error(t.value.vecSource.form.validation.extensionCodeRequired)
      return false
    }
  }
  return true
}

const prevStep = () => {
  if (currentStep.value > 1) currentStep.value--
}

const handleNext = () => {
  if (currentStep.value < 2) {
    if (validateStep(currentStep.value)) currentStep.value++
  } else {
    onSubmit()
  }
}

const handleCancel = () => {
  open.value = false
  currentStep.value = 1
}

const onSubmit = async () => {
  const payload: AiVecSource = {...form}
  emit('submit', payload)
}

const testConnection = async () => {
  testLoading.value = true
  try {
    const payload: AiVecSource = {...form}
    const msg = await aiVecSourceApi.testConnection(payload)
    message.success(msg)
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || t.value.vecSource.form.testFailed)
  } finally {
    testLoading.value = false
  }
}
</script>

<style scoped>
.form-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 0 24px;
  overflow: hidden;
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

.modal-form {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 16px;
  min-width: 0;
}

.form-grid :deep(.ant-form-item) {
  min-width: 0;
}

.form-grid :deep(.ant-form-item-control-input) {
  min-width: 0;
}

.step-content {
  animation: stepFadeIn 0.25s ease;
  overflow-x: hidden;
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

.input-prefix-icon {
  color: var(--text-muted);
  font-size: 14px;
}

.mono-text {
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 12px;
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

.footer-right {
  display: flex;
  align-items: center;
  gap: 8px;
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

.test-btn {
  border: 1px solid var(--border-default);
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
