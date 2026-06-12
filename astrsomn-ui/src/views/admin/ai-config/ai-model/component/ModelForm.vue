<template>
  <AstModal
      :confirm-loading="confirmLoading"
      :confirm-text="props.mode !== 'view' ? t.form.confirmText : undefined"
      :max-width="maxWidth"
      :open="open"
      body-height="90vh"
      main-padding="0"
      max-body-height="800px"
      width="80vw"
      wrap-class-name="model-form-fsm-wrap"
      @cancel="onCancel"
      @confirm="handleSubmit"
      @update:open="emit('update:open', $event)"
  >
    <template #header-logo>
      <span :class="form.modelType" class="header-type-icon">
        <template v-if="form.modelType === 'chat'"><MessageOutlined/></template>
        <template v-else-if="form.modelType === 'embedding'"><PartitionOutlined/></template>
        <template v-else-if="form.modelType === 'voice'"><AudioOutlined/></template>
        <template v-else><PictureOutlined/></template>
      </span>
    </template>
    <template #header-title>
      {{ mode === 'create' ? t.form.createTitle : mode === 'view' ? t.form.viewTitle : t.form.editTitle }}
    </template>
    <template #header-subtitle>
      {{ t.form.subtitle }}
    </template>

    <div class="model-form-shell">
      <a-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="model-form-root"
          layout="vertical"
      >
        <div class="model-form-split">
          <div class="model-form-pane model-form-pane--left">
            <div class="form-section">
              <h3 class="section-headline">
                <IdcardOutlined/>
                {{ t.form.sectionBasicInfo }}
              </h3>
              <div class="form-grid">
                <a-form-item :label="t.form.labelModelName" name="modelName">
                  <a-input v-model:value="form.modelName" :disabled="props.mode === 'view' || isPluginModel"
                           :placeholder="t.form.placeholderModelName" size="large"/>
                </a-form-item>

                <a-form-item :label="t.form.labelModelKey" name="modelKey">
                  <a-tooltip
                      v-if="modelKeyImmutable || props.mode === 'view' || isPluginModel"
                      :title="isPluginModel ? t.form.tooltipPluginImmutable : (modelKeyImmutable ? t.form.tooltipKeyImmutable : t.form.tooltipViewMode)"
                  >
                    <a-input v-model:value="form.modelKey" disabled :placeholder="t.form.placeholderModelKey" size="large"/>
                  </a-tooltip>
                  <AstKeyGenerator
                      v-else
                      v-model="form.modelKey"
                      :prefix="AI_MODEL_KEY_PREFIX"
                      :placeholder="t.form.placeholderModelKeyAuto"
                      size="large"
                  />
                </a-form-item>

                <a-form-item :label="t.form.labelSourceType" name="sourceType">
                  <a-select v-model:value="form.sourceType" :disabled="props.mode === 'view' || props.mode === 'create' || isPluginModel" size="large">
                    <a-select-option value="user_custom">{{ t.form.optionUserCustom }}</a-select-option>
                    <a-select-option value="plugin">{{ t.form.optionPluginModel }}</a-select-option>
                  </a-select>
                </a-form-item>

                <a-form-item :label="t.form.labelProvider" name="provider">
                  <ExtensionSelector
                      v-model:value="form.extensionCode"
                      :disabled="props.mode === 'view' || isPluginModel"
                      :placeholder="t.form.placeholderProvider"
                      size="large"
                  />
                </a-form-item>

                <a-form-item :label="t.form.labelModelType" name="modelType">
                  <a-segmented v-model:value="form.modelType"
                               :disabled="props.mode === 'view' || isPluginModel"
                               :options="[{label: t.form.optionChat, value:'chat'}, {label: t.form.optionEmbedding, value:'embedding'}, {label: t.form.optionImage, value:'image'}, {label: t.form.optionVoice, value:'voice'}]" block size="large"/>
                </a-form-item>

                <a-form-item :label="t.form.labelStatus" name="status">
                  <a-segmented
                      v-model:value="form.status"
                      :disabled="props.mode === 'view'"
                      :options="[{label: t.form.optionEnabled, value:'enabled'}, {label: t.form.optionDisabled, value:'disabled'}]"
                      block
                      class="status-segmented"
                      size="large"
                  />
                </a-form-item>
              </div>
              <h3 class="section-headline">
                <ThunderboltOutlined/>
                {{ t.form.sectionCapabilities }}
              </h3>

              <div v-if="form.modelType === 'chat' || form.modelType === 'voice'" class="capability-panel-section">
                <div class="cap-tag-grid">
                  <div
                      v-for="opt in chatCapabilitiesOptions"
                      :key="opt.value"
                      :class="['custom-cap-tag', { active: chatCapabilities.includes(opt.value) }]"
                      :style="{ cursor: props.mode === 'view' ? 'default' : 'pointer' }"
                      @click="props.mode !== 'view' && toggleChatCapability(opt.value)"
                  >
                    <div class="custom-cap-tag__body">
                      <CheckCircleFilled v-if="chatCapabilities.includes(opt.value)" class="custom-cap-tag__check"/>
                      <div class="custom-cap-tag__text">
                        <span class="custom-cap-tag__title">{{ opt.titleZh }}</span>
                        <span class="custom-cap-tag__field">{{ opt.fieldCode }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div aria-hidden="true" class="model-form-divider"/>

          <div class="model-form-pane model-form-pane--right">
            <div class="form-section">


              <div v-if="form.modelType === 'embedding'" class="capability-panel-section">
                <div class="cap-tag-grid">
                  <div
                      v-for="opt in embeddingCapabilitiesOptions"
                      :key="opt.value"
                      :class="['custom-cap-tag', { active: embeddingCapabilities.includes(opt.value) }]"
                      :style="{ cursor: props.mode === 'view' ? 'default' : 'pointer' }"
                      @click="props.mode !== 'view' && toggleEmbeddingCapability(opt.value)"
                  >
                    <div class="custom-cap-tag__body">
                      <CheckCircleFilled v-if="embeddingCapabilities.includes(opt.value)"
                                         class="custom-cap-tag__check"/>
                      <div class="custom-cap-tag__text">
                        <span class="custom-cap-tag__title">{{ opt.titleZh }}</span>
                        <span class="custom-cap-tag__field">{{ opt.fieldCode }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div v-if="form.modelType === 'image'" class="capability-panel-section">
                <div class="cap-tag-grid">
                  <div
                      v-for="opt in imageCapabilitiesOptions"
                      :key="opt.value"
                      :class="['custom-cap-tag', { active: imageCapabilities.includes(opt.value) }]"
                      :style="{ cursor: props.mode === 'view' ? 'default' : 'pointer' }"
                      @click="props.mode !== 'view' && toggleImageCapability(opt.value)"
                  >
                    <div class="custom-cap-tag__body">
                      <CheckCircleFilled v-if="imageCapabilities.includes(opt.value)" class="custom-cap-tag__check"/>
                      <div class="custom-cap-tag__text">
                        <span class="custom-cap-tag__title">{{ opt.titleZh }}</span>
                        <span class="custom-cap-tag__field">{{ opt.fieldCode }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="param-schema-section">
                <h3 class="section-headline">
                  <SettingOutlined/>
                  {{ t.form.sectionParamSchema }}
                </h3>
                <p class="section-desc">{{ t.form.paramSchemaDesc }}</p>

                <div class="param-table">
                  <div class="param-table-header">
                    <div class="param-col param-col--id">{{ t.form.paramColumnName }}</div>
                    <div class="param-col param-col--desc">{{ t.form.paramColumnDesc }}</div>


                    <div class="param-col param-col--toggle">{{ t.form.paramColumnToggle }}</div>
                  </div>
                  <div class="param-table-body">
                    <div v-for="param in currentParams" :key="param.id" class="param-row">
                      <div class="param-col param-col--id">
                        <span class="param-id">{{ param.id }}</span>
                      </div>
                      <div class="param-col param-col--desc">
                        <span class="param-desc">{{ param.desc }}</span>
                        <span class="param-mapping">{{ param.mapping }}</span>
                      </div>
                      <div class="param-col param-col--toggle">
                        <a-switch
                            v-model:checked="param.active"
                            :disabled="props.mode === 'view'"
                        />
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="runtime-params-box">
                <div class="box-title">
                  <ControlOutlined/>
                  {{ t.form.sectionResourceLimit }}
                </div>
                <div class="param-grid">
                  <div class="param-item">
                    <span class="pl">{{ t.form.labelResponseLimit }}</span>
                    <a-input-number v-model:value="form.responseLimit" :disabled="props.mode === 'view'" :min="0" block
                                    :placeholder="t.form.placeholderResponseLimit"/>
                  </div>
                  <div class="param-item">
                    <span class="pl">{{ t.form.labelMaxQuotaTokens }}</span>
                    <a-input-number v-model:value="form.maxQuotaTokens" :disabled="props.mode === 'view'" :min="0" block
                                    :placeholder="t.form.placeholderMaxQuotaTokens"/>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </a-form>

      <div v-if="props.mode !== 'view'" class="modal-footer-action">
        <div class="footer-left">
          <LockOutlined/>
          {{ t.form.footerWarning }}
        </div>
      </div>
    </div>
  </AstModal>
</template>

<script lang="ts" setup>
import {computed, onMounted, reactive, ref, watch} from 'vue'
import type {FormInstance} from 'ant-design-vue'
import {
  AudioOutlined,
  CheckCircleFilled,
  ControlOutlined,
  IdcardOutlined,
  LockOutlined,
  MessageOutlined,
  PartitionOutlined,
  PictureOutlined,
  SettingOutlined,
  ThunderboltOutlined
} from '@ant-design/icons-vue'
import AstModal from '@/components/home/AstModal.vue'
import AstKeyGenerator from '@/components/home/AstKeyGenerator.vue'
import ExtensionSelector from '@/views/admin/system-config/system-extension/selector/ExtensionSelector.vue'
import type {AiModel} from '@/api/aiModel.ts'
import {AI_MODEL_KEY_PREFIX} from '@/constants/aiConfigKeyPrefixes.ts'
import {aiModelCapabilitiesDictionary} from '@/locales/zh-CN/dictionary/ai-config/ai-model.ts'
import {
  CHAT_CAPABILITIES_CODES,
  CHAT_CAPABILITIES_SET,
  EMBEDDING_CAPABILITIES_CODES,
  EMBEDDING_CAPABILITIES_SET,
  IMAGE_CAPABILITIES_CODES,
  IMAGE_CAPABILITIES_SET
} from '@/constants/aiModelEnums.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-model')

const props = withDefaults(
    defineProps<{
      open: boolean
      mode: 'create' | 'edit' | 'view'
      confirmLoading?: boolean
      initialData?: AiModel | null
      statusOptions: { label: string; value: string }[]
      submitHandler: (payload: AiModel) => Promise<void>
    }>(),
    {confirmLoading: false, initialData: null}
)

const emit = defineEmits(['update:open'])

const formRef = ref<FormInstance | null>(null)

const modelKeyImmutable = ref(false)
const maxWidth = computed(() => 'min(80vw, 1600px)')

const PARAM_TEMPLATES = computed(() => ({
  chat: [
    {id: 'temperature', desc: t.value.form.paramDescTemperature, mapping: 'temperature', default: '0.7', range: '0-2.0', active: false},
    {id: 'top_p', desc: t.value.form.paramDescTopP, mapping: 'top_p', default: '1.0', range: '0-1.0', active: false},
    {id: 'top_k', desc: t.value.form.paramDescTopK, mapping: 'top_k', default: '', range: 'int', active: false},
    {id: 'max_tokens', desc: t.value.form.paramDescMaxTokens, mapping: 'max_tokens', default: '2048', range: '1-32k', active: false},
    {
      id: 'presence_penalty',
      desc: t.value.form.paramDescPresencePenalty,
      mapping: 'presence_penalty',
      default: '0',
      range: '-2-2',
      active: false
    },
    {
      id: 'frequency_penalty',
      desc: t.value.form.paramDescFrequencyPenalty,
      mapping: 'frequency_penalty',
      default: '0',
      range: '-2-2',
      active: false
    },
    {id: 'stop_sequences', desc: t.value.form.paramDescStopSequences, mapping: 'stop_sequences', default: '', range: 'Array', active: false},
    {id: 'seed', desc: t.value.form.paramDescSeed, mapping: 'seed', default: '', range: 'int', active: false},
    {id: 'logit_bias', desc: t.value.form.paramDescLogitBias, mapping: 'logit_bias', default: '', range: 'Object', active: false}
  ],
  embedding: [
    {id: 'dimensions', desc: t.value.form.paramDescDimensions, mapping: 'dimensions', default: '1536', range: 'int', active: false},
    {id: 'model_name', desc: t.value.form.paramDescModelName, mapping: 'model_name', default: '', range: 'string', active: false},
    {id: 'user', desc: t.value.form.paramDescUser, mapping: 'user', default: '', range: 'string', active: false}
  ],
  image: [
    {id: 'size', desc: t.value.form.paramDescSize, mapping: 'size', default: '1024x1024', range: 'string', active: false},
    {id: 'quality', desc: t.value.form.paramDescQuality, mapping: 'quality', default: 'standard', range: 'standard/hd', active: false},
    {id: 'style', desc: t.value.form.paramDescStyle, mapping: 'style', default: 'vivid', range: 'vivid/natural', active: false},
    {
      id: 'response_format',
      desc: t.value.form.paramDescResponseFormat,
      mapping: 'response_format',
      default: 'url',
      range: 'url/b64_json',
      active: false
    }
  ]
}))

function capOptionRow(code: string) {
  const titleZh = aiModelCapabilitiesDictionary.getLabel(code) ?? code
  return {value: code, titleZh, fieldCode: code}
}

const chatCapabilitiesOptions = computed(() => CHAT_CAPABILITIES_CODES.map((value) => capOptionRow(value)))
const embeddingCapabilitiesOptions = computed(() => EMBEDDING_CAPABILITIES_CODES.map((value) => capOptionRow(value)))
const imageCapabilitiesOptions = computed(() => IMAGE_CAPABILITIES_CODES.map((value) => capOptionRow(value)))

const chatCapabilities = ref<string[]>([])
const chatOrphanCapabilities = ref<string[]>([])

const embeddingCapabilities = ref<string[]>([])
const embeddingOrphanCapabilities = ref<string[]>([])

const imageCapabilities = ref<string[]>([])
const imageOrphanCapabilities = ref<string[]>([])

const currentParams = ref<any[]>(JSON.parse(JSON.stringify(PARAM_TEMPLATES.value.chat)))

const isPluginModel = computed(() => form.sourceType === 'plugin')

const form = reactive<AiModel>({
  modelName: '', modelKey: '', modelType: 'chat', provider: '',
  status: 'enabled', responseLimit: 4096,
  capabilities: '', param: '', randomIndex: 0, topVariance: 0, maxQuotaTokens: 0, sourceType: 'user_custom'
})

const rules = computed(() => ({
  modelName: [{required: true, message: t.value.form.validationModelName}],
  extensionCode: [{required: true, message: t.value.form.validationProvider}],
}))

function toggleInList(list: string[], val: string) {
  const i = list.indexOf(val)
  if (i > -1) list.splice(i, 1)
  else list.push(val)
}

const toggleChatCapability = (val: string) => toggleInList(chatCapabilities.value, val)
const removeChatOrphan = (val: string) => {
  chatOrphanCapabilities.value = chatOrphanCapabilities.value.filter((c) => c !== val)
}

const toggleEmbeddingCapability = (val: string) => toggleInList(embeddingCapabilities.value, val)
const removeEmbeddingOrphan = (val: string) => {
  embeddingOrphanCapabilities.value = embeddingOrphanCapabilities.value.filter((c) => c !== val)
}

const toggleImageCapability = (val: string) => toggleInList(imageCapabilities.value, val)
const removeImageOrphan = (val: string) => {
  imageOrphanCapabilities.value = imageOrphanCapabilities.value.filter((c) => c !== val)
}

function localizeAndSortParams(params: any[]): any[] {
  return params
      .map((p) => ({
        ...p,
        desc: aiModelCapabilitiesDictionary.getLabel(p.id) ?? p.desc
      }))
      .sort((a, b) => (a.active === b.active ? 0 : a.active ? -1 : 1))
}

function partitionConfig(caps: string[], params: any[], modelType: string) {
  chatCapabilities.value = []
  chatOrphanCapabilities.value = []
  embeddingCapabilities.value = []
  embeddingOrphanCapabilities.value = []
  imageCapabilities.value = []
  imageOrphanCapabilities.value = []

  if (modelType === 'chat' || modelType === 'voice') {
    chatCapabilities.value = caps.filter((c) => CHAT_CAPABILITIES_SET.has(c))
    chatOrphanCapabilities.value = caps.filter((c) => !CHAT_CAPABILITIES_SET.has(c))
    if (params && params.length > 0) {
      currentParams.value = localizeAndSortParams(params)
    } else {
      currentParams.value = JSON.parse(JSON.stringify(PARAM_TEMPLATES.value.chat))
    }
  } else if (modelType === 'embedding') {
    embeddingCapabilities.value = caps.filter((c) => EMBEDDING_CAPABILITIES_SET.has(c))
    embeddingOrphanCapabilities.value = caps.filter((c) => !EMBEDDING_CAPABILITIES_SET.has(c))
    if (params && params.length > 0) {
      currentParams.value = localizeAndSortParams(params)
    } else {
      currentParams.value = JSON.parse(JSON.stringify(PARAM_TEMPLATES.value.embedding))
    }
  } else if (modelType === 'image') {
    imageCapabilities.value = caps.filter((c) => IMAGE_CAPABILITIES_SET.has(c))
    imageOrphanCapabilities.value = caps.filter((c) => !IMAGE_CAPABILITIES_SET.has(c))
    if (params && params.length > 0) {
      currentParams.value = localizeAndSortParams(params)
    } else {
      currentParams.value = JSON.parse(JSON.stringify(PARAM_TEMPLATES.value.image))
    }
  }
}

const syncForm = () => {
  if (props.mode === 'create' || !props.initialData) {
    modelKeyImmutable.value = false
    Object.assign(form, {
      id: '',
      modelName: '',
      modelKey: '',
      modelType: 'chat',
      provider: '',
      status: 'enabled',
      capabilities: '',
      param: '',
      randomIndex: 0,
      topVariance: 0,
      maxQuotaTokens: 0,
      sourceType: 'user_custom'
    })
    partitionConfig([], [], String(form.modelType ?? 'chat'))
  } else {
    Object.assign(form, props.initialData)
    modelKeyImmutable.value = props.initialData.modelKeyImmutable === true
    delete (form as Record<string, unknown>).modelKeyImmutable

    if (!form.sourceType) {
      form.sourceType = 'user_custom'
    }

    let capabilitiesArray: string[] = []
    let paramsArray: any[] = []

    try {
      const parsedCaps = JSON.parse(form.capabilities || '[]')
      capabilitiesArray = Array.isArray(parsedCaps) ? parsedCaps.map((c: any) => String(c).toLowerCase()) : []
    } catch {
      capabilitiesArray = []
    }

    try {
      const parsedParams = JSON.parse(form.params || form.param || '[]')
      paramsArray = Array.isArray(parsedParams) ? parsedParams : []
    } catch {
      paramsArray = []
    }

    partitionConfig(capabilitiesArray, paramsArray, String(form.modelType || 'chat'))
  }
}

onMounted(() => {
  if (props.open) {
    syncForm()
  }
})

watch(
    () => props.open,
    (v) => {
      if (!v) return
      syncForm()
    }
)

watch(
    () => props.mode,
    () => {
      if (props.open) {
        syncForm()
      }
    }
)

watch(
    () => props.initialData,
    () => {
      if (props.open) {
        syncForm()
      }
    }
)

watch(() => form.modelType, () => {
  partitionConfig([], [], String(form.modelType || 'chat'))
})

const handleSubmit = async () => {
  await formRef.value?.validate()
  const payload = {...form}

  let allCapabilities: string[] = []
  if (form.modelType === 'chat' || form.modelType === 'voice') {
    allCapabilities = [...chatCapabilities.value, ...chatOrphanCapabilities.value]
  } else if (form.modelType === 'embedding') {
    allCapabilities = [...embeddingCapabilities.value, ...embeddingOrphanCapabilities.value]
  } else if (form.modelType === 'image') {
    allCapabilities = [...imageCapabilities.value, ...imageOrphanCapabilities.value]
  }

  payload.capabilities = allCapabilities.length > 0 ? JSON.stringify(allCapabilities) : ''
  payload.params = currentParams.value.length > 0 ? JSON.stringify(currentParams.value) : ''
  payload.param = payload.params
  delete (payload as { modelKeyImmutable?: unknown }).modelKeyImmutable
  await props.submitHandler(payload)
}

const onCancel = () => emit('update:open', false)
</script>

<style scoped>
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

.header-type-icon.voice {
  color: #a5f3fc;
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
  background: var(--bg-card);
}

.model-form-pane--right {
  background: var(--bg-surface);
}

.model-form-divider {
  width: 1px;
  flex-shrink: 0;
  background: var(--border-default);
  align-self: stretch;
}

.model-form-pane::-webkit-scrollbar {
  width: 6px;
}

.model-form-pane::-webkit-scrollbar-thumb {
  background: var(--border-default);
  border-radius: 4px;
}

.section-headline {
  font-size: 14px;
  font-weight: 600;
  margin: 0 0 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-heading);
}

.section-desc {
  font-size: 12px;
  color: var(--text-secondary);
  margin: -8px 0 16px 0;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 24px;
}

.span-2 {
  grid-column: span 2;
}

:deep(.ant-segmented-item-selected) {
  background-color: var(--primary) !important;
  color: white !important;
}

:deep(.ant-segmented-item-selected:hover) {
  background-color: var(--primary) !important;
  color: white !important;
}

.status-segmented :deep(.ant-segmented-item:nth-child(1).ant-segmented-item-selected) {
  background-color: #52c41a !important;
}

.status-segmented :deep(.ant-segmented-item:nth-child(1).ant-segmented-item-selected:hover) {
  background-color: #73d13d !important;
}

.status-segmented :deep(.ant-segmented-item:nth-child(2).ant-segmented-item-selected) {
  background-color: #ff4d4f !important;
}

.status-segmented :deep(.ant-segmented-item:nth-child(2).ant-segmented-item-selected:hover) {
  background-color: #ff7875 !important;
}

.capability-panel-section {
  margin-bottom: 20px;
}

.cap-tag-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.custom-cap-tag {
  min-height: 72px;
  padding: 12px 14px;
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: 10px;
  cursor: pointer;
  transition: border-color 0.2s,
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
  color: var(--primary);
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
  color: var(--text-heading);
  line-height: 1.4;
  word-break: break-word;
}

.custom-cap-tag__field {
  font-size: 11px;
  font-weight: 500;
  color: var(--text-muted);
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  line-height: 1.35;
  word-break: break-all;
}

.custom-cap-tag:hover {
  border-color: var(--primary);
  box-shadow: 0 1px 4px color-mix(in srgb, var(--primary) 12%, transparent);
}

.custom-cap-tag:hover .custom-cap-tag__title {
  color: var(--primary);
}

.custom-cap-tag.active {
  background: color-mix(in srgb, var(--primary) 8%, var(--bg-card));
  border-color: var(--primary);
  box-shadow: 0 1px 4px color-mix(in srgb, var(--primary) 18%, transparent);
}

.custom-cap-tag.active .custom-cap-tag__title {
  color: var(--primary);
}

.custom-cap-tag.active .custom-cap-tag__field {
  color: var(--text-secondary);
}

.param-schema-section {
  margin-top: 24px;
}

.param-table {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: 8px;
  overflow: hidden;
}

.param-table-header {
  display: grid;
  grid-template-columns: 120px 1fr 100px 100px 60px;
  gap: 12px;
  padding: 12px 16px;
  background: var(--bg-surface);
  border-bottom: 1px solid var(--border-default);
  font-size: 11px;
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
}

.param-table-body {
  max-height: 320px;
  overflow-y: auto;
}

.param-row {
  display: grid;
  grid-template-columns: 120px 1fr 100px 100px 60px;
  gap: 12px;
  padding: 10px 16px;
  border-bottom: 1px solid var(--border-default);
  align-items: center;
}

.param-row:hover {
  background: var(--bg-surface);
}

.param-col {
  display: flex;
  align-items: center;
}

.param-col--id {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 12px;
  font-weight: 600;
  color: var(--text-heading);
}

.param-id {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 12px;
  font-weight: 600;
  color: var(--text-heading);
}

.param-col--desc {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: flex-start;
}

.param-desc {
  font-size: 12px;
  color: var(--text-secondary);
}

.param-mapping {
  font-size: 10px;
  color: var(--text-muted);
  background: var(--bg-surface);
  padding: 2px 6px;
  border-radius: 4px;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}

.param-col--range {
  justify-content: center;
}

.param-range {
  font-size: 10px;
  color: var(--text-muted);
  text-align: center;
}

.param-col--toggle {
  justify-content: center;
}

.runtime-params-box {
  margin-top: 24px;
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: 16px;
  padding: 16px;
}

.box-title {
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 16px;
  color: var(--text-heading);
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
  color: var(--text-muted);
}

.modal-footer-action {
  flex-shrink: 0;
  padding: 14px 24px;
  background: var(--bg-card);
  border-top: 1px solid var(--border-default);
  display: flex;
  align-items: center;
}

.footer-left {
  font-size: 12px;
  color: var(--success);
  display: flex;
  align-items: center;
  gap: 4px;
}

</style>
