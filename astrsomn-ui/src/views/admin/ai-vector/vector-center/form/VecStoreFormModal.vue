<template>
  <AstModal
      :open="open"
      :width="800"
      :body-height="520"
      :max-width="800"
      :footer="null"
      wrap-class-name="vec-store-form-modal"
      @update:open="emit('update:open', $event)"
  >
    <template #header-logo>
      <DatabaseOutlined/>
    </template>
    <template #header-title>
      {{ mode === 'create' ? t.vecStore.form.createTitle : t.vecStore.form.editTitle }}
    </template>
    <template #header-subtitle>
      {{ t.vecStore.form.subtitle }}
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
          <!-- 第一步：基础配置 -->
          <div v-show="currentStep === 1" class="step-content">
            <div class="form-grid">
              <a-form-item :label="t.vecStore.form.collectionName.label" name="collectionName"
                           :rules="[{ required: true, message: t.vecStore.form.validation.collectionNameRequired }]">
                <a-input v-model:value="form.collectionName" :placeholder="t.vecStore.form.collectionName.placeholder" size="large"/>
              </a-form-item>

              <a-form-item :label="t.vecStore.form.dimension.label" name="dimension"
                           :rules="[{ required: true, message: t.vecStore.form.validation.dimensionRequired }]">
                <a-select
                    v-model:value="form.dimension"
                    :filter-option="filterDimensionOption"
                    :options="dimensionOptions"
                    allow-clear
                    :placeholder="t.vecStore.form.dimension.placeholder"
                    show-search
                    size="large"
                    style="width: 100%"
                />
              </a-form-item>

              <a-form-item :label="t.vecStore.form.distanceMetric.label" name="distanceMetric"
                           :rules="[{ required: true, message: t.vecStore.form.validation.distanceMetricRequired }]">
                <a-select v-model:value="form.distanceMetric" size="large">
                  <a-select-option value="cosine">{{ t.vecStore.form.cosine }}</a-select-option>
                  <a-select-option value="euclidean">{{ t.vecStore.form.euclidean }}</a-select-option>
                  <a-select-option value="manhattan">{{ t.vecStore.form.manhattan }}</a-select-option>
                </a-select>
              </a-form-item>

              <a-form-item :label="t.vecStore.form.modelKey.label" name="modelKey"
                           :rules="[{ required: true, message: t.vecStore.form.validation.modelKeyRequired }]">
                <div class="selector-row">
                  <a-input
                      :value="selectedModelDisplay"
                      disabled
                      :placeholder="t.vecStore.form.modelKey.placeholder"
                      size="large"
                  />
                  <a-button size="large" type="primary" @click="modelSelectorOpen = true">
                    {{ t.vecStore.form.modelKey.selectBtn }}
                  </a-button>
                </div>
              </a-form-item>

              <a-form-item :label="t.vecStore.form.accountKey.label" name="accountKey" class="span-2">
                <div class="selector-row">
                  <a-input
                      :value="selectedAccountDisplay"
                      disabled
                      :placeholder="t.vecStore.form.accountKey.placeholder"
                      size="large"
                  />
                  <a-button size="large" type="primary" @click="accountSelectorOpen = true">
                    {{ t.vecStore.form.accountKey.selectBtn }}
                  </a-button>
                </div>
              </a-form-item>
            </div>
          </div>

          <!-- 第二步：分块配置 -->
          <div v-show="currentStep === 2" class="step-content">
            <div class="form-grid">
              <a-form-item :label="t.vecStore.form.chunkStrategy.label" name="chunkStrategy">
                <a-select v-model:value="form.chunkStrategy" size="large">
                  <a-select-option value="RECURSIVE">{{ t.vecStore.form.chunkRecursive }}</a-select-option>
                  <a-select-option value="FIXED_SIZE">{{ t.vecStore.form.chunkFixedSize }}</a-select-option>
                  <a-select-option value="PARAGRAPH">{{ t.vecStore.form.chunkParagraph }}</a-select-option>
                  <a-select-option value="SENTENCE">{{ t.vecStore.form.chunkSentence }}</a-select-option>
                </a-select>
              </a-form-item>

              <a-form-item :label="t.vecStore.form.chunkSize.label" name="chunkSize">
                <a-input-number
                    v-model:value="form.chunkSize"
                    :max="4000"
                    :min="100"
                    :placeholder="t.vecStore.form.chunkSize.placeholder"
                    size="large"
                    style="width: 100%"
                />
              </a-form-item>

              <a-form-item :label="t.vecStore.form.chunkOverlap.label" name="chunkOverlap">
                <a-input-number
                    v-model:value="form.chunkOverlap"
                    :max="500"
                    :min="0"
                    :placeholder="t.vecStore.form.chunkOverlap.placeholder"
                    size="large"
                    style="width: 100%"
                />
              </a-form-item>

              <a-form-item :label="t.vecStore.form.denseWeight.label" name="denseWeight">
                <a-slider
                    v-model:value="form.denseWeight"
                    :max="1"
                    :min="0"
                    :step="0.05"
                    :tooltip-formatter="(v: any) => Number(v).toFixed(2)"
                />
              </a-form-item>

              <a-form-item :label="t.vecStore.form.instructionPrefix.label" name="instructionPrefix" class="span-2">
                <a-input
                    v-model:value="form.instructionPrefix"
                    :placeholder="t.vecStore.form.instructionPrefix.placeholder"
                    size="large"
                />
              </a-form-item>

              <a-form-item :label="t.vecStore.form.metadataSchema.label" name="metadataSchema" class="span-2">
                <a-textarea
                    v-model:value="form.metadataSchema"
                    :auto-size="{ minRows: 2, maxRows: 4 }"
                    class="mono-text"
                    placeholder='{"type": "object", "properties": {"title": {"type": "string"}}}'
                />
              </a-form-item>
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
            {{ t.vecStore.form.btnPrev }}
          </a-button>
          <a-button
              v-else
              type="text"
              class="footer-btn cancel-btn"
              @click="handleCancel"
          >
            {{ t.vecStore.form.btnCancel }}
          </a-button>
        </div>
        <a-button
            :loading="confirmLoading"
            type="primary"
            class="footer-btn next-btn"
            :class="{ 'create-btn': currentStep === 2 }"
            @click="handleNext"
        >
          <span>{{ currentStep === 2 ? (mode === 'edit' ? t.vecStore.form.btnSave : t.vecStore.form.btnCreate) : t.vecStore.form.btnNext }}</span>
          <CheckOutlined v-if="currentStep === 2"/>
          <ArrowRightOutlined v-else/>
        </a-button>
      </div>
    </div>

    <ModelSelectorDrawer
        :open="modelSelectorOpen"
        fixed-model-type="embedding"
        :fixed-provider-code="selectedProviderCode"
        @update:open="modelSelectorOpen = $event"
        @select="handleModelSelect"
    />
    <AccountSelectorDrawer
        :open="accountSelectorOpen"
        :provider-filter="selectedProviderCode"
        @update:open="accountSelectorOpen = $event"
        @select="handleAccountSelect"
    />
  </AstModal>
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import {message} from 'ant-design-vue'
import {
  ArrowLeftOutlined,
  ArrowRightOutlined,
  CheckOutlined,
  DatabaseOutlined
} from '@ant-design/icons-vue'
import AstModal from '@/components/home/AstModal.vue'
import ModelSelectorDrawer from '@/views/admin/ai-config/ai-model/selector/ModelSelectorDrawer.vue'
import AccountSelectorDrawer from '@/views/admin/ai-config/ai-account/selector/AccountSelectorDrawer.vue'
import type {AiModel} from '@/api/aiModel.ts'
import type {AiAccount} from '@/api/aiAccount.ts'
import type {AiVecStore} from '@/api/aiVecStore.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-vector')

const props = defineProps<{
  mode: 'create' | 'edit'
  confirmLoading: boolean
  initial: AiVecStore | null
  defaultSourceId?: number | string | null
}>()
const emit = defineEmits<{
  submit: [payload: AiVecStore]
  'update:open': [value: boolean]
}>()
const open = defineModel<boolean>('open', {required: true})

const loading = ref(false)
const currentStep = ref(1)
const modelSelectorOpen = ref(false)
const accountSelectorOpen = ref(false)
const selectedModelName = ref('')
const selectedAccountName = ref('')
/** 关联提供方：选择模型后记录其 extensionCode，用于自动过滤账号列表；反之亦然 */
const selectedProviderCode = ref<string | undefined>(undefined)
const selectedAccountProviderCode = ref<string | undefined>(undefined)

const steps = [
  {key: 'basic', label: t.value.vecStore.form.basicConfig},
  {key: 'chunk', label: t.value.vecStore.form.chunkConfig}
]

const dimensionOptions = computed(() => [
  {value: 256, label: t.value.vecStore.form.dimLabels[256]},
  {value: 512, label: t.value.vecStore.form.dimLabels[512]},
  {value: 768, label: t.value.vecStore.form.dimLabels[768]},
  {value: 1024, label: t.value.vecStore.form.dimLabels[1024]},
  {value: 1536, label: t.value.vecStore.form.dimLabels[1536]},
  {value: 2048, label: t.value.vecStore.form.dimLabels[2048]},
  {value: 3072, label: t.value.vecStore.form.dimLabels[3072]},
  {value: 4096, label: t.value.vecStore.form.dimLabels[4096]},
  {value: 8192, label: t.value.vecStore.form.dimLabels[8192]},
])

function filterDimensionOption(input: string, option: { value: number; label: string }) {
  return String(option.value).includes(input) || option.label.toLowerCase().includes(input.toLowerCase())
}

function emptyForm(): AiVecStore {
  return {
    id: undefined,
    sourceId: props.defaultSourceId || undefined,
    collectionName: '',
    dimension: undefined as unknown as number,
    distanceMetric: 'cosine',
    metadataSchema: '',
    modelKey: '',
    accountKey: '',
    chunkStrategy: 'RECURSIVE',
    chunkSize: 800,
    chunkOverlap: 100,
    denseWeight: 1,
    instructionPrefix: ''
  }
}

const form = reactive<AiVecStore>(emptyForm())

const selectedModelDisplay = computed(() => {
  if (!form.modelKey) return ''
  return selectedModelName.value
      ? `${selectedModelName.value} (${form.modelKey})`
      : form.modelKey
})

const selectedAccountDisplay = computed(() => {
  if (!form.accountKey) return ''
  return selectedAccountName.value
      ? `${selectedAccountName.value} (${form.accountKey})`
      : form.accountKey
})

function assignFromInitial(src: AiVecStore) {
  Object.assign(form, emptyForm(), src)
  selectedModelName.value = (src as any).instanceName || ''
  selectedAccountName.value = (src as any).accountName || ''
}

watch(() => [open.value, props.initial, props.defaultSourceId] as const, ([isOpen, initial, defaultSourceId]) => {
  if (isOpen) {
    currentStep.value = 1
    selectedProviderCode.value = undefined
    selectedAccountProviderCode.value = undefined
    if (initial && Object.keys(initial).length > 0) {
      assignFromInitial(initial)
    } else {
      Object.assign(form, emptyForm())
      selectedModelName.value = ''
      selectedAccountName.value = ''
      if (defaultSourceId) {
        form.sourceId = defaultSourceId
      }
    }
  }
})

function handleModelSelect(model: AiModel) {
  form.modelKey = model.modelKey || ''
  selectedModelName.value = model.modelName || ''
  if (model.responseLimit && model.responseLimit > 0) {
    form.dimension = model.responseLimit
  }
  selectedProviderCode.value = model.extensionCode || undefined
  // 若已选账号与当前模型提供方不一致，则清空账号
  if (selectedAccountProviderCode.value && selectedAccountProviderCode.value !== selectedProviderCode.value) {
    form.accountKey = ''
    selectedAccountName.value = ''
    selectedAccountProviderCode.value = undefined
  }
  modelSelectorOpen.value = false
}

function handleAccountSelect(account: AiAccount) {
  form.accountKey = account.accountKey || ''
  selectedAccountName.value = account.accountName || ''
  selectedAccountProviderCode.value = account.extensionCode || undefined
  // 若已选模型提供方不存在，则从账号侧反向设置
  if (!selectedProviderCode.value && account.extensionCode) {
    selectedProviderCode.value = account.extensionCode
  }
  // 若已选模型与当前账号提供方不一致，则清空模型
  if (selectedProviderCode.value && account.extensionCode && selectedProviderCode.value !== account.extensionCode) {
    form.modelKey = ''
    selectedModelName.value = ''
    selectedProviderCode.value = account.extensionCode
  }
  accountSelectorOpen.value = false
}

const validateStep = (step: number): boolean => {
  if (step === 1) {
    if (!form.collectionName?.trim()) {
      message.error(t.value.vecStore.form.validation.collectionNameRequired)
      return false
    }
    if (!form.dimension) {
      message.error(t.value.vecStore.form.validation.dimensionRequired)
      return false
    }
    if (!form.modelKey) {
      message.error(t.value.vecStore.form.validation.modelKeyRequired)
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
  const payload: AiVecStore = {...form}
  emit('submit', payload)
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

.span-2 {
  grid-column: span 2;
}

.selector-row {
  display: flex;
  gap: 8px;
  min-width: 0;
}

.selector-row :deep(.ant-input) {
  flex: 1;
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
