<template>
  <AstModal
      :closable="false"
      :max-width="'min(80vw, 1400px)'"
      :open="open"
      body-height="80vh"
      main-padding="0"
      max-body-height="800px"
      width="80vw"
      wrap-class-name="instance-edit-modal-wrap"
      @cancel="handleCancel"
      @update:open="emit('update:open', $event)"
  >
    <template #header-logo>
      <CloudServerOutlined/>
    </template>
    <template #header-title>
      {{ isEdit ? '编辑推理实例' : '新建推理实例' }}
    </template>
    <template #header-subtitle>
      配置模型端点、推理参数与关联账号
    </template>
    <template #header-actions>
      <a-button class="header-action-btn header-action-btn-cancel" @click="handleCancel">取消</a-button>
      <a-button
          class="header-action-btn header-action-btn-save"
          type="primary"
          @click="handleConfirm"
      >
        确认
      </a-button>
    </template>

    <div class="modal-body">
      <div class="modal-left">
        <!-- 实例名称 -->
        <div class="form-section">
          <label class="form-label">实例名称</label>
          <a-input v-model:value="formData.instanceName" placeholder="例如：GPT-4o 主力" size="large"/>
        </div>

        <!-- 模型源 -->
        <div class="form-section">
          <label class="form-label">模型源</label>
          <div
              :class="['model-pick-card', {'has-model': !!formData.modelKey}]"
              @click="modelDrawerOpen = true"
          >
            <template v-if="formData.modelKey && selectedModel">
              <img
                  v-if="getModelAvatar(formData.modelKey)"
                  :alt="selectedModel.modelName"
                  :src="getModelAvatar(formData.modelKey)"
                  class="model-pick-avatar"
              />
              <div class="model-pick-info">
                <span class="model-pick-name">{{ selectedModel.modelName }}</span>
                <span class="model-pick-key">{{ formData.modelKey }}</span>
              </div>
              <SwapOutlined class="model-pick-swap"/>
            </template>
            <template v-else>
              <SearchOutlined class="model-pick-placeholder-icon"/>
              <span class="model-pick-placeholder">点击选择模型</span>
            </template>
          </div>
        </div>

        <!-- 关联账号 -->
        <div class="form-section">
          <label class="form-label">关联账号</label>
          <div class="account-input-row">
            <a-input
                :value="formData.accountKey"
                :disabled="true"
                :placeholder="formData.accountKey || '请选择关联账号'"
                class="account-input"
                size="large"
                @click="accountDrawerOpen = true"
            />
            <a-button size="large" type="primary" @click="accountDrawerOpen = true">
              选择
            </a-button>
          </div>
        </div>

        <!-- 降级备选 -->
        <div class="form-section">
          <label class="form-label">降级备选</label>
          <a-select
              v-model:value="formData.fallbackInstanceKey"
              :options="fallbackOptions"
              allow-clear
              class="soft-select"
              placeholder="无自动降级"
              size="large"
          />
        </div>

        <!-- 负载均衡权重 -->
        <div v-if="routeStrategy === 'weightedRandom'" class="form-section">
          <label class="form-label">实例权重</label>
          <div class="weight-bar">
            <a-slider
                v-model:value="formData.routeWeight"
                :max="100"
                :min="1"
                :step="1"
                class="weight-slider"
            />
            <span class="weight-value">{{ formData.routeWeight ?? 1 }}</span>
          </div>
        </div>

        <!-- 上下文历史长度 -->
        <div class="form-section">
          <label class="form-label">上下文历史长度</label>
          <div class="context-btn-group">
            <button
                v-for="opt in contextOptions"
                :key="opt.value"
                :class="['context-btn', {active: formData.memoryWindowSize === opt.value}]"
                @click="formData.memoryWindowSize = opt.value"
            >
              {{ opt.label }}
            </button>
          </div>
        </div>
      </div>

      <div class="modal-right">
        <!-- 参数提示 -->
        <div class="param-section-header">
          <span class="param-section-title">{{ paramVisibility.paramSectionTitle.value }}</span>
          <a-tag v-if="formData.modelKey" class="model-key-tag" color="blue">{{ formData.modelKey }}</a-tag>
        </div>

        <p v-if="paramVisibility.capabilityHint.value" class="cap-hint">{{ paramVisibility.capabilityHint.value }}</p>
        <p v-if="paramVisibility.hasParamSchema.value && paramVisibility.unsupportedParamCodes.value.length > 0"
           class="cap-hint muted">
          当前模型参数中有 {{ paramVisibility.unsupportedParamCodes.value.length }} 项暂不支持实例侧填写：{{
            paramVisibility.unsupportedParamCodes.value.join(', ')
          }}
        </p>

        <!-- 对话模型参数 -->
        <template v-if="paramVisibility.modelKind.value === 'chat'">
          <div class="param-list">
            <div v-if="paramVisibility.showChatTemperature.value" class="param-group-card">
              <div class="p-header">
                <span class="p-label">采样温度 (Temperature)</span>
                <a-input-number v-model:value="formData.temperature" :max="2" :min="0" :step="0.1" size="small"/>
              </div>
              <a-slider
                  v-model:value="formData.temperature"
                  :marks="{ 0: '严谨', 0.7: '平衡', 1.5: '创意', 2: '随机' }"
                  :max="2"
                  :min="0"
                  :step="0.1"
              />
              <div class="p-desc-bar">
                {{ getTempInfo(formData.temperature ?? 0.7).text }}
              </div>
            </div>

            <div v-if="paramVisibility.showChatMaxTokens.value" class="param-group-card">
              <div class="p-header">
                <span class="p-label">响应上限 (Max Tokens)</span>
                <a-input-number v-model:value="formData.maxTokens" :max="128000" :min="1" size="small"/>
              </div>
              <a-slider
                  v-model:value="formData.maxTokens"
                  :marks="{ 0: '短', 2048: '中等', 4096: '长', 8192: '超长' }"
                  :max="8192"
                  :min="0"
                  :step="256"
              />
            </div>

            <div v-if="paramVisibility.showChatTopP.value" class="param-group-card">
              <div class="p-header">
                <span class="p-label">核采样 (Top P)</span>
                <a-input-number v-model:value="formData.topP" :max="1" :min="0" :step="0.01" size="small"/>
              </div>
              <a-slider
                  v-model:value="formData.topP"
                  :marks="{ 0: '极窄', 0.5: '标准', 1: '完整' }"
                  :max="1"
                  :min="0"
                  :step="0.05"
              />
            </div>

            <div v-if="paramVisibility.showChatTopK.value" class="param-group-card">
              <div class="p-header">
                <span class="p-label">Top K</span>
                <a-input-number v-model:value="formData.topK" :max="100" :min="0" :step="1" size="small"/>
              </div>
              <p class="p-inline-hint">0 表示不启用</p>
            </div>

            <div v-if="paramVisibility.showChatSeed.value" class="param-group-card">
              <div class="p-header">
                <span class="p-label">随机种子 (Seed)</span>
                <a-input-number v-model:value="formData.seed" :max="2147483647" :min="0" :step="1" size="small"/>
              </div>
            </div>

            <div v-if="paramVisibility.showChatStopSequences.value" class="param-group-card">
              <div class="p-header">
                <span class="p-label">停止序列 (Stop)</span>
              </div>
              <a-textarea v-model:value="formData.stopSequences" :rows="2" placeholder="多个序列用英文逗号分隔"/>
            </div>

            <div v-if="paramVisibility.showChatPenalties.value" class="penalty-row">
              <div v-if="paramVisibility.showChatFrequencyPenalty.value" class="mini-param-card">
                <span class="mini-label">重复惩罚 (Frequency)</span>
                <a-slider v-model:value="formData.frequencyPenalty" :max="2" :min="-2" :step="0.1"/>
              </div>
              <div v-if="paramVisibility.showChatPresencePenalty.value" class="mini-param-card">
                <span class="mini-label">新鲜度 (Presence)</span>
                <a-slider v-model:value="formData.presencePenalty" :max="2" :min="-2" :step="0.1"/>
              </div>
            </div>
          </div>
        </template>

        <!-- Embedding 模型参数 -->
        <template v-else-if="paramVisibility.modelKind.value === 'embedding'">
          <div class="param-list">
            <div v-if="paramVisibility.showEmbeddingDimensions.value" class="param-group-card">
              <div class="p-header">
                <span class="p-label">向量维度 (Dimensions)</span>
              </div>
              <a-select
                  v-model:value="formData.dimensions"
                  :filter-option="filterDimensionOption"
                  :options="dimensionOptions"
                  allow-clear
                  placeholder="选择或输入维度"
                  show-search
                  size="large"
                  style="width: 100%"
              />
            </div>
            <p v-if="!paramVisibility.embeddingHasAnyControl.value" class="cap-hint muted">当前端点未开放向量可调参数。</p>
          </div>
        </template>

        <!-- 图像模型参数 -->
        <template v-else-if="paramVisibility.modelKind.value === 'image'">
          <div class="param-list">
            <div v-if="paramVisibility.showImageSize.value" class="param-group-card">
              <div class="p-header">
                <span class="p-label">画幅尺寸 (Size)</span>
              </div>
              <a-input v-model:value="formData.size" allow-clear placeholder="例如 1024x1024" size="large"/>
            </div>
            <div v-if="paramVisibility.showImageStyle.value" class="param-group-card">
              <div class="p-header">
                <span class="p-label">风格 (Style)</span>
              </div>
              <a-input v-model:value="formData.style" allow-clear placeholder="例如 vivid / natural" size="large"/>
            </div>
            <p v-if="!paramVisibility.imageHasAnyControl.value" class="cap-hint muted">当前端点未开放图像可调参数。</p>
          </div>
        </template>
      </div>
    </div>
  </AstModal>

  <ModelSelectorDrawer
      :fixed-model-type="modelType"
      :open="modelDrawerOpen"
      @select="onModelDrawerSelect"
      @update:open="modelDrawerOpen = $event"
  />
  <AccountSelectorDrawer
      :open="accountDrawerOpen"
      :provider-filter="selectedModel?.extensionCode"
      @select="onAccountSelect"
      @update:open="accountDrawerOpen = $event"
  />
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import {CloudServerOutlined, SearchOutlined, SwapOutlined} from '@ant-design/icons-vue'
import AstModal from '@/components/home/AstModal.vue'
import type {AiInstance} from '@/api/aiInstance.ts'
import type {AiModel} from '@/api/aiModel.ts'
import type {AiAccount} from '@/api/aiAccount.ts'
import ModelSelectorDrawer from '@/views/admin/ai-config/ai-model/selector/ModelSelectorDrawer.vue'
import AccountSelectorDrawer from '@/views/admin/ai-config/ai-account/selector/AccountSelectorDrawer.vue'
import {getTempInfo, useInstanceParamVisibility} from '@/views/admin/ai-config/ai-instance/useInstanceParamVisibility.ts'

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

const isEdit = computed(() => !!props.record?.instanceKey)

const modelDrawerOpen = ref(false)
const accountDrawerOpen = ref(false)

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

const contextOptions = [
  {value: '10', label: '10 Rounds'},
  {value: '20', label: '20 Rounds'},
  {value: 'full', label: 'Full Window'},
]

const dimensionOptions = [
  {value: 256, label: '256 — 轻量级，适合简单检索'},
  {value: 512, label: '512 — 紧凑型，平衡性能与精度'},
  {value: 768, label: '768 — 常用基线（BGE / text-embedding-ada）'},
  {value: 1024, label: '1024 — 中高维度，语义表达更丰富'},
  {value: 1536, label: '1536 — 主流高维（OpenAI text-embedding-3）'},
  {value: 2048, label: '2048 — 高精度场景'},
  {value: 3072, label: '3072 — 超高精度，适合专业语义匹配'},
  {value: 4096, label: '4096 — 最大常用档位'},
  {value: 8192, label: '8192 — 极限维度，计算成本极高'},
]

function filterDimensionOption(input: string, option: { value: number; label: string }) {
  return String(option.value).includes(input) || option.label.toLowerCase().includes(input.toLowerCase())
}

const fallbackOptions = computed(() =>
    props.instanceList
        .filter(i => i.instanceKey !== props.record?.instanceKey)
        .map(inst => ({
          value: inst.instanceKey || '',
          label: (inst.instanceName || inst.instanceKey || '未命名实例') + (inst.isDefault === 'Y' ? ' (默认)' : ''),
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
  emit('update:open', false)
}

watch(() => props.open, (val) => {
  if (!val) return
  if (props.record) {
    populateFromRecord(props.record)
  } else {
    resetForm()
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

.header-action-btn {
  height: 38px;
  min-width: 100px;
  border-radius: var(--radius-md);
  padding: 0 20px;
  font-weight: 600;
}

:deep(.header-action-btn-cancel.ant-btn-default) {
  color: #475569;
  border-color: #cbd5e1;
  background: #fff;
}

:deep(.header-action-btn-save.ant-btn-primary) {
  box-shadow: none;
}

.modal-body {
  display: grid;
  grid-template-columns: 1fr 1.2fr;
  height: 100%;
  overflow: hidden;
}

.modal-left {
  padding: 24px 28px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow-y: auto;
  border-right: 1px solid var(--border-default);
}

.modal-right {
  padding: 24px 28px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  font-size: 11px;
  font-weight: 700;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.model-pick-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border: 1px solid var(--border-subtle);
  border-radius: 8px;
  cursor: pointer;
  transition: border-color 0.2s;
}

.model-pick-card:hover {
  border-color: var(--text-muted);
}

.model-pick-avatar {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  object-fit: contain;
  flex-shrink: 0;
}

.model-pick-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.model-pick-name {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.model-pick-key {
  font-size: 10px;
  color: var(--text-muted);
  font-family: monospace;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.model-pick-placeholder-icon {
  color: var(--text-muted);
  font-size: 14px;
}

.model-pick-placeholder {
  font-size: 12px;
  color: var(--text-muted);
}

.model-pick-swap {
  color: var(--text-muted);
  font-size: 12px;
  flex-shrink: 0;
}

.account-input-row {
  display: flex;
  gap: 8px;
}

.account-input {
  flex: 1;
  cursor: pointer;
}

.soft-select {
  border-radius: 8px;
}

.weight-bar {
  display: flex;
  align-items: center;
  gap: 16px;
}

.weight-slider {
  flex: 1;
  margin: 0;
}

.weight-value {
  font-size: 14px;
  font-weight: 700;
  color: var(--primary);
  font-family: monospace;
  min-width: 32px;
  text-align: right;
}

.context-btn-group {
  display: flex;
  gap: 6px;
}

.context-btn {
  flex: 1;
  padding: 6px 8px;
  border: 1px solid var(--border-subtle);
  border-radius: 8px;
  background: transparent;
  color: var(--text-muted);
  font-size: 10px;
  font-weight: 600;
  cursor: pointer;
  transition: border-color 0.15s, color 0.15s;
}

.context-btn:hover {
  border-color: var(--text-muted);
}

.context-btn.active {
  border-color: var(--text-primary);
  color: var(--text-primary);
  font-weight: 700;
}

.param-section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.param-section-title {
  font-size: 13px;
  font-weight: 700;
  color: var(--text-primary);
}

.model-key-tag {
  flex-shrink: 0;
  max-width: 180px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.cap-hint {
  font-size: 12px;
  color: var(--text-muted);
  line-height: 1.5;
  margin: 0;
}

.cap-hint.muted {
  opacity: 0.7;
}

.param-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.param-group-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.p-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.p-label {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-secondary);
}

.p-desc-bar {
  font-size: 11px;
  font-weight: 500;
  color: var(--text-muted);
}

.p-inline-hint {
  font-size: 11px;
  color: var(--text-muted);
  margin: 0;
}

.penalty-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.mini-param-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.mini-label {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-secondary);
}
</style>
