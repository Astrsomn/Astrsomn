<template>
  <AstModal
      :confirm-text="t.agent.confirm"
      :max-width="'min(80vw, 1400px)'"
      :open="open"
      :z-index="1000"
      body-height="80vh"
      main-padding="0"
      max-body-height="800px"
      width="80vw"
      wrap-class-name="instance-edit-modal-wrap"
      @cancel="handleCancel"
      @confirm="handleConfirm"
      @update:open="emit('update:open', $event)"
  >
    <template #header-logo>
      <CloudServerOutlined />
    </template>
    <template #header-title>
      {{ isEdit ? t.agent.editInstance : t.agent.createInstance }}
    </template>
    <template #header-subtitle>
      {{ t.agent.configureModelEndpoint }}
    </template>
    <div class="modal-body">
      <!-- 左侧：基础配置 -->
      <div class="modal-left">
        <div class="section-title">{{ t.agent.basicConfig }}</div>

        <!-- 实例名称 — 全宽 -->
        <div class="form-group">
          <label class="form-label">{{ t.agent.instanceName }} <span class="required">*</span></label>
          <a-input v-model:value="formData.instanceName" :placeholder="t.agent.instanceNamePlaceholder" size="large" />
        </div>

        <!-- Bento 双卡片：模型 + 账号 -->
        <div class="bento-row">
          <div class="form-group">
            <label class="form-label">{{ t.agent.model }} <span class="required">*</span></label>
            <div :class="['bento-card', { filled: !!formData.modelKey }]" @click="modelDrawerOpen = true">
              <template v-if="formData.modelKey && selectedModel">
                <img v-if="getModelAvatar(formData.modelKey)" :src="getModelAvatar(formData.modelKey)" class="bento-card-avatar" />
                <div v-else class="bento-card-icon"><RobotOutlined /></div>
                <div class="bento-card-body">
                  <span class="bento-card-title">{{ selectedModel.modelName }}</span>
                  <code class="bento-card-key">{{ formData.modelKey }}</code>
                </div>
                <SwapOutlined class="bento-card-swap" />
              </template>
              <template v-else>
                <div class="bento-card-icon"><SearchOutlined /></div>
                <div class="bento-card-body">
                  <span class="bento-card-title">{{ t.agent.selectModel }}</span>
                  <span class="bento-card-hint">{{ t.agent.clickSelectModel }}</span>
                </div>
                <RightOutlined class="bento-card-arrow" />
              </template>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">{{ t.agent.linkedAccount }} <span class="required">*</span></label>
            <div :class="['bento-card', { filled: !!formData.accountKey }]" @click="accountDrawerOpen = true">
              <template v-if="formData.accountKey">
                <div class="bento-card-icon"><KeyOutlined /></div>
                <div class="bento-card-body">
                  <span class="bento-card-title">{{ formData.accountName || formData.accountKey }}</span>
                  <code class="bento-card-key">{{ formData.accountKey }}</code>
                </div>
                <SwapOutlined class="bento-card-swap" />
              </template>
              <template v-else>
                <div class="bento-card-icon"><UserOutlined /></div>
                <div class="bento-card-body">
                  <span class="bento-card-title">{{ t.agent.selectAccount }}</span>
                  <span class="bento-card-hint">{{ t.agent.clickSelectAccount }}</span>
                </div>
                <RightOutlined class="bento-card-arrow" />
              </template>
            </div>
          </div>
        </div>

        <!-- 降级 + 权重 同行 -->
        <div class="form-row">
          <div class="form-group" style="flex:1">
            <label class="form-label">{{ t.agent.fallback }}</label>
            <a-select
                v-model:value="formData.fallbackInstanceKey"
                :options="fallbackOptions"
                allow-clear
                :placeholder="t.agent.noAutoFallback"
                size="large"
            />
          </div>
          <div v-if="routeStrategy === 'weightedRandom'" class="form-group" style="flex:1">
            <label class="form-label">{{ t.agent.weight }}</label>
            <div class="weight-row">
              <a-slider v-model:value="formData.routeWeight" :max="100" :min="1" :step="1" class="weight-slider" />
              <span class="weight-value">{{ formData.routeWeight ?? 1 }}</span>
            </div>
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">{{ t.agent.contextRounds }}</label>
          <div class="context-options">
            <button
                v-for="opt in contextOptions"
                :key="opt.value"
                :class="['context-btn', { active: formData.memoryWindowSize === opt.value }]"
                @click="formData.memoryWindowSize = opt.value"
            >
              {{ opt.label }}
            </button>
          </div>
        </div>
      </div>

      <!-- 右侧：推理参数 -->
      <div class="modal-right">
        <!-- 折叠态 -->
        <div v-if="!showInferenceParams" class="params-collapsed">
          <div class="params-collapsed-icon">
            <SettingOutlined />
          </div>
          <h3 class="params-collapsed-title">{{ t.agent.inferenceParams }}</h3>
          <p class="params-collapsed-desc">
            <template v-if="formData.modelKey && selectedModel">
              {{ paramKindLabel }} · {{ selectedModel.modelName }}
            </template>
            <template v-else>
              {{ t.agent.selectModelToConfig }}
            </template>
          </p>
          <p class="params-collapsed-hint">{{ t.agent.paramHint }}</p>
          <a-button :disabled="!formData.modelKey" type="primary" @click="showInferenceParams = true">
            <template #icon><SettingOutlined /></template>
            {{ t.agent.expandConfig }}
          </a-button>
        </div>

        <!-- 展开态 -->
        <template v-else>
          <div class="section-title">
            <span>{{ t.agent.inferenceParams }}</span>
            <a-tag v-if="formData.modelKey" class="model-tag" color="blue">{{ formData.modelKey }}</a-tag>
            <a-button size="small" type="text" @click="showInferenceParams = false">
              <template #icon><CloseOutlined /></template>
            </a-button>
          </div>

          <p v-if="paramVisibility.capabilityHint.value" class="hint-text">{{ paramVisibility.capabilityHint.value }}</p>
          <p v-if="paramVisibility.hasParamSchema.value && paramVisibility.unsupportedParamCodes.value.length > 0" class="hint-text muted">
            {{ paramVisibility.unsupportedParamCodes.value.length }} {{ t.agent.unsupportedParams }}：{{ paramVisibility.unsupportedParamCodes.value.join(', ') }}
          </p>

          <!-- 对话模型参数 -->
          <template v-if="paramVisibility.modelKind.value === 'chat'">
            <div class="params-list">
              <div v-if="paramVisibility.showChatTemperature.value" class="param-card">
                <div class="param-header">
                  <span class="param-name">Temperature</span>
                  <a-input-number v-model:value="formData.temperature" :max="2" :min="0" :step="0.1" size="small" />
                </div>
                <a-slider
                    v-model:value="formData.temperature"
                    :marks="{ 0: t.agent.strict, 0.7: t.agent.balanced, 1.5: t.agent.creative, 2: t.agent.randomLabel }"
                    :max="2"
                    :min="0"
                    :step="0.1"
                />
                <div class="param-hint">{{ getTempInfo(formData.temperature ?? 0.7).text }}</div>
              </div>

              <div v-if="paramVisibility.showChatMaxTokens.value" class="param-card">
                <div class="param-header">
                  <span class="param-name">Max Tokens</span>
                  <a-input-number v-model:value="formData.maxTokens" :max="128000" :min="1" size="small" />
                </div>
                <a-slider
                    v-model:value="formData.maxTokens"
                    :marks="{ 0: t.agent.short, 2048: t.agent.medium, 4096: t.agent.longLabel, 8192: t.agent.extraLong }"
                    :max="8192"
                    :min="0"
                    :step="256"
                />
              </div>

              <div v-if="paramVisibility.showChatTopP.value" class="param-card">
                <div class="param-header">
                  <span class="param-name">Top P</span>
                  <a-input-number v-model:value="formData.topP" :max="1" :min="0" :step="0.01" size="small" />
                </div>
                <a-slider
                    v-model:value="formData.topP"
                    :marks="{ 0: t.agent.veryNarrow, 0.5: t.agent.standard, 1: t.agent.complete }"
                    :max="1"
                    :min="0"
                    :step="0.05"
                />
              </div>

              <div v-if="paramVisibility.showChatTopK.value" class="param-card">
                <div class="param-header">
                  <span class="param-name">Top K</span>
                  <a-input-number v-model:value="formData.topK" :max="100" :min="0" :step="1" size="small" />
                </div>
                <span class="param-note">{{ t.agent.disabledLabel }}</span>
              </div>

              <div v-if="paramVisibility.showChatSeed.value" class="param-card">
                <div class="param-header">
                  <span class="param-name">Seed</span>
                  <a-input-number v-model:value="formData.seed" :max="2147483647" :min="0" :step="1" size="small" />
                </div>
              </div>

              <div v-if="paramVisibility.showChatStopSequences.value" class="param-card">
                <div class="param-header">
                  <span class="param-name">Stop Sequences</span>
                </div>
                <a-textarea v-model:value="formData.stopSequences" :rows="2" :placeholder="t.agent.commaSeparated" />
              </div>

              <div v-if="paramVisibility.showChatPenalties.value" class="penalty-grid">
                <div v-if="paramVisibility.showChatFrequencyPenalty.value" class="param-card compact">
                  <span class="param-name">Frequency Penalty</span>
                  <a-slider v-model:value="formData.frequencyPenalty" :max="2" :min="-2" :step="0.1" />
                </div>
                <div v-if="paramVisibility.showChatPresencePenalty.value" class="param-card compact">
                  <span class="param-name">Presence Penalty</span>
                  <a-slider v-model:value="formData.presencePenalty" :max="2" :min="-2" :step="0.1" />
                </div>
              </div>
            </div>
          </template>

          <!-- Embedding 模型参数 -->
          <template v-else-if="paramVisibility.modelKind.value === 'embedding'">
            <div class="params-list">
              <div v-if="paramVisibility.showEmbeddingDimensions.value" class="param-card">
                <div class="param-header">
                  <span class="param-name">Dimensions</span>
                </div>
                <a-select
                    v-model:value="formData.dimensions"
                    :filter-option="filterDimensionOption"
                    :options="dimensionOptions"
                    allow-clear
                    :placeholder="t.agent.selectOrInputDimension"
                    show-search
                    size="large"
                    style="width: 100%"
                />
              </div>
              <p v-if="!paramVisibility.embeddingHasAnyControl.value" class="hint-text muted">{{ t.agent.noEmbeddingParams }}</p>
            </div>
          </template>

          <!-- 图像模型参数 -->
          <template v-else-if="paramVisibility.modelKind.value === 'image'">
            <div class="params-list">
              <div v-if="paramVisibility.showImageSize.value" class="param-card">
                <div class="param-header">
                  <span class="param-name">Size</span>
                </div>
                <a-input v-model:value="formData.size" allow-clear :placeholder="t.agent.sizeExample" size="large" />
              </div>
              <div v-if="paramVisibility.showImageStyle.value" class="param-card">
                <div class="param-header">
                  <span class="param-name">Style</span>
                </div>
                <a-input v-model:value="formData.style" allow-clear :placeholder="t.agent.styleExample" size="large" />
              </div>
              <p v-if="!paramVisibility.imageHasAnyControl.value" class="hint-text muted">{{ t.agent.noImageParams }}</p>
            </div>
          </template>
        </template>
      </div>
    </div>
  </AstModal>

  <ModelSelectorDrawer
      :fixed-model-type="modelType"
      :open="modelDrawerOpen"
      root-class-name="instance-modal-drawer"
      @select="onModelDrawerSelect"
      @update:open="modelDrawerOpen = $event"
  />
  <AccountSelectorDrawer
      :open="accountDrawerOpen"
      :provider-filter="selectedModel?.extensionCode"
      root-class-name="instance-modal-drawer"
      @select="onAccountSelect"
      @update:open="accountDrawerOpen = $event"
  />
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import {CloudServerOutlined, CloseOutlined, KeyOutlined, RightOutlined, RobotOutlined, SearchOutlined, SettingOutlined, SwapOutlined, UserOutlined} from '@ant-design/icons-vue'
import {message} from 'ant-design-vue'
import AstModal from '@/components/home/AstModal.vue'
import type {AiInstance} from '@/api/aiInstance.ts'
import type {AiModel} from '@/api/aiModel.ts'
import type {AiAccount} from '@/api/aiAccount.ts'
import ModelSelectorDrawer from '@/views/admin/ai-config/ai-model/selector/ModelSelectorDrawer.vue'
import AccountSelectorDrawer from '@/views/admin/ai-config/ai-account/selector/AccountSelectorDrawer.vue'
import {
  getTempInfo,
  useInstanceParamVisibility
} from '@/views/admin/ai-config/ai-instance/useInstanceParamVisibility.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const props = defineProps<{
  open: boolean
  record: AiInstance | null
  instanceList: AiInstance[]
  availableModels: AiModel[]
  routeStrategy?: string
  modelType?: string
}>()

const emit = defineEmits<{
  'update:open': [value: boolean]
  confirm: [instance: AiInstance]
}>()

const t = usePageTranslation('ai-config-center')

const isEdit = computed(() => !!props.record?.instanceKey)
const modelDrawerOpen = ref(false)
const accountDrawerOpen = ref(false)
const showInferenceParams = ref(false)

const formData = reactive({
  modelType: 'chat' as string,
  modelKey: '' as string,
  accountKey: '' as string,
  accountName: '' as string,
  instanceName: '' as string,
  isDefault: 'N' as string,
  routeWeight: undefined as number | undefined,
  fallbackInstanceKey: undefined as string | undefined,
  temperature: 0.7 as number | undefined,
  maxTokens: 2048 as number | undefined,
  topP: 1.0 as number | undefined,
  topK: undefined as number | undefined,
  seed: undefined as number | undefined,
  stopSequences: undefined as string | undefined,
  frequencyPenalty: 0 as number | undefined,
  presencePenalty: 0 as number | undefined,
  dimensions: undefined as number | undefined,
  size: undefined as string | undefined,
  style: undefined as string | undefined,
  memoryWindowSize: '10' as string,
})

const selectedModel = computed(() =>
    props.availableModels.find(m => m.modelKey === formData.modelKey)
)

const paramVisibility = useInstanceParamVisibility(selectedModel)

const paramKindLabel = computed(() => {
  const kind = paramVisibility.modelKind.value
  if (kind === 'chat') return t.value.agent.chatModel
  if (kind === 'embedding') return t.value.agent.embeddingModel
  if (kind === 'image') return t.value.agent.imageModel
  return t.value.agent.inferenceConfig
})

const contextOptions = computed(() => [
  { value: '10', label: t.value.agent.rounds10 },
  { value: '20', label: t.value.agent.rounds20 },
  { value: 'full', label: t.value.agent.fullWindow },
])

const dimensionOptions = computed(() => [
  { value: 256, label: t.value.agent.dim256 },
  { value: 512, label: t.value.agent.dim512 },
  { value: 768, label: t.value.agent.dim768 },
  { value: 1024, label: t.value.agent.dim1024 },
  { value: 1536, label: t.value.agent.dim1536 },
  { value: 2048, label: t.value.agent.dim2048 },
  { value: 3072, label: t.value.agent.dim3072 },
  { value: 4096, label: t.value.agent.dim4096 },
  { value: 8192, label: t.value.agent.dim8192 },
])

function filterDimensionOption(input: string, option: { value: number; label: string }) {
  return String(option.value).includes(input) || option.label.toLowerCase().includes(input.toLowerCase())
}

const fallbackOptions = computed(() =>
    props.instanceList
        .filter(i => i.instanceKey !== props.record?.instanceKey)
        .map(inst => ({
          value: inst.instanceKey || '',
          label: (inst.instanceName || inst.instanceKey || t.value.agent.unnamed) + (inst.isDefault === 'Y' ? ` ${t.value.agent.defaultSuffix}` : ''),
        }))
)

function getModelAvatar(modelKey?: string): string {
  if (!modelKey) return ''
  const m = props.availableModels.find(x => x.modelKey === modelKey)
  const raw = m?.providerAvatar
  return typeof raw === 'string' && raw.trim() ? raw.trim() : ''
}

function onModelDrawerSelect(model: AiModel) {
  formData.modelKey = model.modelKey || ''
  formData.modelType = model.modelType || formData.modelType
  if (model.modelName && !isEdit.value) {
    formData.instanceName = model.modelName
  }
  modelDrawerOpen.value = false
}

function onAccountSelect(account: AiAccount) {
  formData.accountKey = account.accountKey || ''
  formData.accountName = account.accountName || ''
  accountDrawerOpen.value = false
}

function resetForm() {
  formData.modelType = props.modelType || 'chat'
  formData.modelKey = ''
  formData.accountKey = ''
  formData.accountName = ''
  formData.instanceName = ''
  formData.isDefault = 'N'
  formData.routeWeight = undefined
  formData.fallbackInstanceKey = undefined
  formData.temperature = 0.7
  formData.maxTokens = 2048
  formData.topP = 1.0
  formData.topK = undefined
  formData.seed = undefined
  formData.stopSequences = undefined
  formData.frequencyPenalty = 0
  formData.presencePenalty = 0
  formData.dimensions = undefined
  formData.size = undefined
  formData.style = undefined
  formData.memoryWindowSize = '10'
}

function populateFromRecord(record: AiInstance) {
  formData.modelType = record.modelType || 'chat'
  formData.modelKey = record.modelKey || ''
  formData.accountKey = record.accountKey || ''
  formData.accountName = record.accountName || ''
  formData.instanceName = record.instanceName || ''
  formData.isDefault = record.isDefault || 'N'
  formData.routeWeight = record.routeWeight
  formData.fallbackInstanceKey = record.fallbackInstanceKey
  formData.temperature = record.temperature ?? 0.7
  formData.maxTokens = record.maxTokens ?? 2048
  formData.topP = record.topP ?? 1.0
  formData.topK = record.topK
  formData.seed = record.seed
  formData.stopSequences = record.stopSequences
  formData.frequencyPenalty = record.frequencyPenalty ?? 0
  formData.presencePenalty = record.presencePenalty ?? 0
  formData.dimensions = record.dimensions
  formData.size = record.size
  formData.style = record.style
  formData.memoryWindowSize = (record as any).memoryWindowSize ?? '10'
}

function handleCancel() {
  emit('update:open', false)
}

function handleConfirm() {
  if (!formData.instanceName.trim()) {
    message.warning(t.value.agent.instanceNameRequired)
    return
  }
  if (!formData.modelKey) {
    message.warning(t.value.agent.selectModelRequired)
    return
  }
  if (!formData.accountKey) {
    message.warning(t.value.agent.selectAccountRequired)
    return
  }
  const instance: AiInstance = {
    modelType: formData.modelType,
    modelKey: formData.modelKey,
    accountKey: formData.accountKey,
    accountName: formData.accountName,
    instanceName: formData.instanceName,
    routeStrategy: props.routeStrategy || 'roundRobin',
    routeWeight: formData.routeWeight,
    fallbackInstanceKey: formData.fallbackInstanceKey,
    temperature: formData.temperature,
    maxTokens: formData.maxTokens,
    topP: formData.topP,
    topK: formData.topK,
    seed: formData.seed,
    stopSequences: formData.stopSequences,
    frequencyPenalty: formData.frequencyPenalty,
    presencePenalty: formData.presencePenalty,
    dimensions: formData.dimensions,
    size: formData.size,
    style: formData.style,
    status: 'enabled',
    isDefault: formData.isDefault,
    memoryWindowSize: formData.memoryWindowSize,
  }
  if (props.record?.instanceKey) {
    instance.instanceKey = props.record.instanceKey
    instance.id = props.record.id
  }
  emit('confirm', instance)
}

watch(() => props.open, (val) => {
  if (!val) return
  if (props.record) {
    populateFromRecord(props.record)
    showInferenceParams.value = true
  } else {
    resetForm()
    showInferenceParams.value = false
  }
})
</script>

<style scoped>
:global(.instance-edit-modal-wrap.ant-modal-wrap) {
  display: flex;
  align-items: center;
  justify-content: center;
}

:global(.instance-edit-modal-wrap .ant-modal) {
  top: 0;
  padding-bottom: 0;
}

/* 选择模型/账号的抽屉必须浮在模态框上方 */
:global(.instance-modal-drawer) {
  z-index: 2000 !important;
}

.modal-body {
  display: grid;
  grid-template-columns: 1fr 1fr;
  height: 100%;
  min-height: 0;
  overflow: hidden;
}

.modal-left {
  padding: 24px 28px;
  display: flex;
  flex-direction: column;
  gap: 18px;
  overflow-y: auto;
  min-height: 0;
  border-right: 1px solid var(--border-default);
}

.modal-right {
  padding: 24px 28px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.section-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  font-size: 12px;
  font-weight: 500;
  color: var(--text-secondary);
}

.required {
  color: var(--error);
  font-weight: 600;
}

/* ── Bento 双卡片行 ── */
.bento-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.form-row {
  display: flex;
  gap: 14px;
}

/* ── Bento 卡片 ── */
.bento-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 18px;
  border: 2px dashed var(--border-default);
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.2s;
  background: var(--bg-input);
  min-height: 72px;
}

.bento-card:hover {
  border-color: var(--primary);
  background: var(--bg-card);
  transform: translateY(-1px);
  box-shadow: 0 4px 16px -4px color-mix(in srgb, var(--primary) 12%, transparent);
}

.bento-card.filled {
  border-style: solid;
  border-color: var(--primary);
  background: color-mix(in srgb, var(--primary) 3%, transparent);
}

.bento-card-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, color-mix(in srgb, var(--primary) 12%, transparent) 0%, color-mix(in srgb, #7c3aed 12%, transparent) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: var(--primary);
  flex-shrink: 0;
}

.bento-card-avatar {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  object-fit: contain;
  flex-shrink: 0;
}

.bento-card-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.bento-card-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.bento-card-key {
  font-size: 11px;
  color: var(--text-muted);
  font-family: 'SF Mono', 'Monaco', 'Menlo', monospace;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.bento-card-hint {
  font-size: 11px;
  color: var(--text-muted);
  opacity: 0.7;
}

.bento-card-swap {
  color: var(--text-muted);
  font-size: 14px;
  flex-shrink: 0;
  opacity: 0;
  transition: opacity 0.15s;
}

.bento-card:hover .bento-card-swap {
  opacity: 1;
}

.bento-card-arrow {
  color: var(--text-muted);
  font-size: 13px;
  flex-shrink: 0;
  transition: all 0.15s;
}

.bento-card:hover .bento-card-arrow {
  color: var(--primary);
  transform: translateX(2px);
}

/* ── 右侧折叠态 ── */
.params-collapsed {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  gap: 12px;
  padding: 40px 20px;
}

.params-collapsed-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  background: linear-gradient(135deg, color-mix(in srgb, var(--primary) 10%, transparent) 0%, color-mix(in srgb, #7c3aed 10%, transparent) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: var(--primary);
}

.params-collapsed-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.params-collapsed-desc {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0;
  line-height: 1.5;
}

.params-collapsed-hint {
  font-size: 11px;
  color: var(--text-muted);
  margin: 0 0 8px;
  max-width: 260px;
  line-height: 1.5;
}

/* ── 展开态 ── */
.modal-right .section-title {
  margin-bottom: 12px;
}

.modal-right .params-list {
  margin-top: 4px;
}

/* ── 权重 ── */
.weight-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.weight-slider {
  flex: 1;
  margin: 0;
}

.weight-value {
  font-size: 14px;
  font-weight: 600;
  color: var(--primary);
  font-family: 'SF Mono', 'Monaco', 'Menlo', monospace;
  min-width: 32px;
  text-align: right;
}

/* ── 上下文轮数 ── */
.context-options {
  display: flex;
  gap: 6px;
}

.context-btn {
  flex: 1;
  padding: 10px 14px;
  border: 1px solid var(--border-default);
  border-radius: 10px;
  background: transparent;
  color: var(--text-muted);
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.context-btn:hover {
  border-color: var(--border-subtle);
  color: var(--text-secondary);
}

.context-btn.active {
  border-color: var(--primary);
  color: var(--primary);
  background: color-mix(in srgb, var(--primary) 4%, transparent);
  font-weight: 600;
}

/* ── 参数面板 ── */
.model-tag {
  font-size: 11px;
  max-width: 180px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.hint-text {
  font-size: 12px;
  color: var(--text-muted);
  line-height: 1.5;
  margin: 0;
}

.hint-text.muted {
  opacity: 0.7;
}

.params-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.param-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 12px;
  border: 1px solid var(--border-subtle);
  border-radius: 10px;
  background: var(--bg-surface);
}

.param-card.compact {
  padding: 0;
  border: none;
  background: transparent;
}

.param-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.param-name {
  font-size: 12px;
  font-weight: 500;
  color: var(--text-primary);
}

.param-hint {
  font-size: 11px;
  font-weight: 500;
  color: var(--text-muted);
}

.param-note {
  font-size: 11px;
  color: var(--text-muted);
}

.penalty-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
</style>
