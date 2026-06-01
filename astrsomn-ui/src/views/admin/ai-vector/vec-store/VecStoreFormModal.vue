<template>
  <AstModal
      :confirm-loading="confirmLoading"
      :confirm-text="t.vecStore.form.confirmText"
      :max-width="maxWidth"
      :open="open"
      body-height="75vh"
      main-padding="0"
      max-body-height="720px"
      width="80vw"
      wrap-class-name="vec-store-form-wrap"
      @confirm="handleOk"
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
    <div class="vec-store-form-shell">
      <div class="form-scroll-area">
        <a-form
            ref="formRef"
            :model="form"
            :rules="rules"
            class="professional-form"
            layout="vertical"
        >
          <div class="form-body-container">
            <div class="form-section">
              <h3 class="section-headline">
                <IdcardOutlined/>
                {{ t.vecStore.form.basicConfig }}
              </h3>

              <div class="form-grid">
                <a-form-item :label="t.vecStore.form.collectionName.label" name="collectionName">
                  <a-input v-model:value="form.collectionName" :placeholder="t.vecStore.form.collectionName.placeholder" size="large"/>
                </a-form-item>

                <a-form-item :label="t.vecStore.form.dimension.label" name="dimension">
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
                  <div v-if="form.modelKey && form.dimension" class="dimension-hint">
                    {{ t.vecStore.form.dimensionHint.replace('{model}', form.modelKey).replace('{dim}', String(form.dimension)) }}
                  </div>
                </a-form-item>

                <a-form-item :label="t.vecStore.form.distanceMetric.label" name="distanceMetric">
                  <a-select v-model:value="form.distanceMetric" size="large">
                    <a-select-option value="cosine">{{ t.vecStore.form.cosine }}</a-select-option>
                    <a-select-option value="euclidean">{{ t.vecStore.form.euclidean }}</a-select-option>
                    <a-select-option value="manhattan">{{ t.vecStore.form.manhattan }}</a-select-option>
                  </a-select>
                </a-form-item>

                <a-form-item :label="t.vecStore.form.modelKey.label" name="modelKey">
                  <a-space class="w-full">
                    <a-input
                        :value="selectedModelDisplay"
                        disabled
                        :placeholder="t.vecStore.form.modelKey.placeholder"
                        size="large"
                        style="flex: 1"
                    />
                    <a-button size="large" type="primary" @click="modelSelectorOpen = true">
                      {{ t.vecStore.form.modelKey.selectBtn }}
                    </a-button>
                  </a-space>
                </a-form-item>

                <a-form-item :label="t.vecStore.form.accountKey.label" name="accountKey">
                  <a-space class="w-full">
                    <a-input
                        :value="form.accountKey || ''"
                        disabled
                        :placeholder="t.vecStore.form.accountKey.placeholder"
                        size="large"
                        style="flex: 1"
                    />
                    <a-button size="large" type="primary" @click="accountSelectorOpen = true">
                      {{ t.vecStore.form.accountKey.selectBtn }}
                    </a-button>
                  </a-space>
                </a-form-item>

                <a-form-item :label="t.vecStore.form.metadataSchema.label" class="span-2" name="metadataSchema">
                  <div class="json-editor-wrapper">
                    <a-textarea
                        v-model:value="form.metadataSchema"
                        :auto-size="{ minRows: 4, maxRows: 6 }"
                        class="mono-text"
                        placeholder='{"type": "object", "properties": {"title": {"type": "string"}}}'
                    />
                  </div>
                </a-form-item>
              </div>
            </div>

            <div class="form-section">
              <h3 class="section-headline">
                <ScissorOutlined/>
                {{ t.vecStore.form.chunkConfig }}
              </h3>

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
                  <div class="dimension-hint">{{ t.vecStore.form.denseWeight.hint }}</div>
                </a-form-item>

                <a-form-item :label="t.vecStore.form.instructionPrefix.label" class="span-2" name="instructionPrefix">
                  <a-input
                      v-model:value="form.instructionPrefix"
                      :placeholder="t.vecStore.form.instructionPrefix.placeholder"
                      size="large"
                  />
                  <div class="dimension-hint">{{ t.vecStore.form.instructionPrefix.hint }}</div>
                </a-form-item>
              </div>
            </div>
          </div>
        </a-form>

        <div class="modal-footer-info">
          <SafetyCertificateOutlined/>
          {{ t.vecStore.form.securityInfo }}
        </div>
      </div>
    </div>

    <ModelSelectorDrawer
        :open="modelSelectorOpen"
        fixed-model-type="embedding"
        @update:open="modelSelectorOpen = $event"
        @select="handleModelSelect"
    />
    <AccountSelectorDrawer
        :open="accountSelectorOpen"
        @update:open="accountSelectorOpen = $event"
        @select="handleAccountSelect"
    />
  </AstModal>
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import {DatabaseOutlined, IdcardOutlined, SafetyCertificateOutlined, ScissorOutlined} from '@ant-design/icons-vue'
import type {FormInstance} from 'ant-design-vue'
import type {AiVecStore} from '@/api/aiVecStore.ts'
import AstModal from '@/components/home/AstModal.vue'
import ModelSelectorDrawer from '@/views/admin/ai-config/ai-model/selector/ModelSelectorDrawer.vue'
import AccountSelectorDrawer from '@/views/admin/ai-config/ai-account/selector/AccountSelectorDrawer.vue'
import type {AiModel} from '@/api/aiModel'
import type {AiAccount} from '@/api/aiAccount'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-vector')

const props = defineProps<{
  mode: 'create' | 'edit',
  confirmLoading: boolean,
  initial: AiVecStore | null,
  defaultSourceId?: number | string | null
}>()
const emit = defineEmits<{
  submit: [payload: AiVecStore],
  'update:open': [value: boolean]
}>()
const open = defineModel<boolean>('open', {required: true})
const maxWidth = computed(() => 'min(80vw, 1000px)')

const formRef = ref<FormInstance | null>(null)
const modelSelectorOpen = ref(false)
const accountSelectorOpen = ref(false)
const selectedModelName = ref('')

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

const rules = computed(() => ({
  collectionName: [{required: true, message: t.value.vecStore.form.validation.collectionNameRequired}],
  dimension: [{required: true, message: t.value.vecStore.form.validation.dimensionRequired}],
  distanceMetric: [{required: true, message: t.value.vecStore.form.validation.distanceMetricRequired}],
  modelKey: [{required: true, message: t.value.vecStore.form.validation.modelKeyRequired}]
}))

const selectedModelDisplay = computed(() => {
  if (!form.modelKey) return ''
  return selectedModelName.value
      ? `${selectedModelName.value} (${form.modelKey})`
      : form.modelKey
})

function assignFromInitial(src: AiVecStore) {
  Object.assign(form, emptyForm(), src)
  selectedModelName.value = (src as any).instanceName || ''
}

watch(() => [open.value, props.initial, props.defaultSourceId] as const, ([isOpen, initial, defaultSourceId]) => {
  if (isOpen) {
    if (initial && Object.keys(initial).length > 0) {
      assignFromInitial(initial)
    } else {
      Object.assign(form, emptyForm())
      selectedModelName.value = ''
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
  modelSelectorOpen.value = false
}

function handleAccountSelect(account: AiAccount) {
  form.accountKey = account.accountKey || ''
  accountSelectorOpen.value = false
}

async function handleOk() {
  await formRef.value?.validate()
  const payload: AiVecStore = {...form}
  emit('submit', payload)
}

</script>

<style scoped>
:global(.vec-store-form-wrap.ant-modal-wrap) {
  display: flex;
  align-items: center;
  justify-content: center;
}

:global(.vec-store-form-wrap .ant-modal) {
  top: 0;
  padding-bottom: 0;
}

.vec-store-form-shell {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  overflow: hidden;
}

.form-scroll-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow-y: auto;
  background: var(--bg-surface);
}

.form-scroll-area::-webkit-scrollbar {
  width: 4px;
}

.form-scroll-area::-webkit-scrollbar-thumb {
  background: var(--border-input);
  border-radius: 4px;
}

.professional-form {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.form-body-container {
  flex: 1;
  overflow-y: auto;
  padding: 24px 40px;
}

.form-section {
  animation: fadeIn 0.25s ease;
}

.section-headline {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-primary);
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 24px;
}

.span-2 {
  grid-column: span 2;
}

.dimension-hint {
  margin-top: 6px;
  font-size: 12px;
  color: var(--success);
  font-weight: 500;
  line-height: 1.5;
}

.dimension-hint b {
  color: var(--primary);
}

.instance-info {
  margin-top: 8px;
  font-size: 12px;
  color: var(--primary);
  font-weight: 600;
}

.json-editor-wrapper {
  border: 1px solid var(--border-input);
  border-radius: 8px;
  overflow: hidden;
  background: var(--bg-input);
  transition: 0.3s;
}

.json-editor-wrapper:focus-within {
  border-color: var(--primary);
  box-shadow: 0 0 0 2px color-mix(in srgb, var(--primary) 15%, transparent);
}

.mono-text {
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 13px;
  background: transparent;
  border: none;
  padding: 12px;
}

.mono-text:focus {
  box-shadow: none;
}

.modal-footer-info {
  flex-shrink: 0;
  padding: 16px 40px;
  background: var(--bg-card);
  border-top: 1px solid var(--border-default);
  font-size: 12px;
  color: var(--success);
  display: flex;
  align-items: center;
  gap: 6px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .span-2 {
    grid-column: span 1;
  }

  .form-body-container {
    padding: 16px 20px;
  }

  .modal-footer-info {
    padding: 12px 20px;
  }
}
</style>
