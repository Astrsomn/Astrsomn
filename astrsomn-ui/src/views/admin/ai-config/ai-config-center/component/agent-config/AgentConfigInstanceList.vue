<template>
  <div class="instance-engine glass-panel">
    <!-- Tab 栏 -->
    <div class="engine-tab-bar">
      <div class="tab-group">
        <button
            v-for="mt in modelTypeOptions"
            :key="mt.value"
            :class="['tab-btn', {active: activeTab === mt.value}]"
            @click="onTabChange(mt.value)"
        >
          {{ mt.label }}
          <span v-if="countByType(mt.value) > 0" class="tab-count">{{ countByType(mt.value) }}</span>
        </button>
      </div>
      <div class="tab-actions">
        <span class="strategy-label">负载均衡:</span>
        <a-select
            :value="props.routeStrategy || 'roundRobin'"
            :options="routeStrategyOptions"
            class="strategy-select"
            size="middle"
            @change="onStrategyChange"
        />
        <a-button class="add-instance-btn" size="middle" type="primary" @click="startAdd">
          <PlusOutlined/>
        </a-button>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="filteredInstances.length === 0 && !isAddingNew" class="empty-state">
      <CloudServerOutlined class="empty-icon"/>
      <p class="empty-text">暂无 {{ activeTabLabel }} 推理实例</p>
      <a-button type="primary" @click="startAdd">添加第一个实例</a-button>
    </div>

    <!-- 主分栏 -->
    <div v-else class="engine-split">
      <!-- 左侧：实例列表 -->
      <div class="split-left">
        <div
            v-for="(instance, idx) in filteredInstances"
            :key="instance.instanceKey || idx"
            :class="['instance-item', {selected: selectedIdx === idx}]"
            @click="selectInstance(idx)"
        >
          <img
              v-if="getModelAvatar(instance.modelKey)"
              :alt="getModelLabel(instance.modelKey)"
              :src="getModelAvatar(instance.modelKey)"
              class="instance-avatar"
          />
          <div v-else :class="['status-dot', instance.status === 'enabled' ? 'active' : 'inactive']"></div>
          <div class="instance-info">
            <p class="instance-name">{{ instance.instanceName || getModelLabel(instance.modelKey) || '未命名实例' }}</p>
            <p class="instance-model">{{ instance.modelKey }}</p>
          </div>
          <RightOutlined class="instance-chevron"/>
        </div>

        <button class="add-dashed-btn" @click="startAdd">
          + 添加新{{ activeTabLabel }}实例
        </button>
      </div>

      <!-- 右侧：配置详情 -->
      <div class="split-right">
        <!-- 未选中实例时的占位提示 -->
        <div v-if="!isAddingNew && !currentDetailInstance" class="no-selection-placeholder">
          <CloudServerOutlined class="no-selection-icon"/>
          <p class="no-selection-text">请从左侧选择一个实例，或点击添加新实例</p>
        </div>

        <template v-else>
        <div class="detail-header">
          <div>
            <h3 class="detail-title">配置详情</h3>
            <p class="detail-subtitle">
              正在调整 [{{ formData.instanceName || formData.modelKey || '新实例' }}] 的核心推理参数
            </p>
          </div>
          <a-button
              v-if="!isAddingNew && currentDetailInstance"
              class="delete-btn"
              danger
              size="small"
              type="text"
              @click="removeSelected"
          >
            <DeleteOutlined/>
          </a-button>
        </div>

        <!-- 负载均衡权重（加权随机模式下显示） -->
        <div v-if="props.routeStrategy === 'weightedRandom'" class="weight-bar">
          <label class="detail-label">实例权重</label>
          <a-slider
              v-model:value="formData.routeWeight"
              :max="100"
              :min="1"
              :step="1"
              class="weight-slider"
          />
          <span class="weight-value">{{ formData.routeWeight ?? 1 }}</span>
        </div>

        <!-- 模型源 + 降级 -->
        <div class="model-source-grid">
          <div class="source-field">
            <label class="detail-label">模型源</label>
            <div
                :class="['model-pick-card', {'has-model': !!formData.modelKey}]"
                @click="modelDrawerOpen = true"
            >
              <template v-if="formData.modelKey && selectedModelForForm">
                <img
                    v-if="getModelAvatar(formData.modelKey)"
                    :alt="selectedModelForForm.modelName"
                    :src="getModelAvatar(formData.modelKey)"
                    class="model-pick-avatar"
                />
                <div class="model-pick-info">
                  <span class="model-pick-name">{{ selectedModelForForm.modelName }}</span>
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

          <div class="source-field">
            <label class="detail-label">降级备选</label>
            <a-select
                v-model:value="formData.fallbackInstanceKey"
                :options="fallbackOptions"
                allow-clear
                class="soft-select"
                placeholder="无自动降级"
                size="large"
            />
          </div>
        </div>

        <!-- 关联账号 -->
        <div class="account-field">
          <label class="detail-label">关联账号</label>
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
              选择账号
            </a-button>
          </div>
        </div>

        <!-- 参数区域标题 -->
        <div class="param-section-header">
          <span class="param-section-title">{{ paramVisibility.paramSectionTitle.value }}</span>
          <a-tag v-if="formData.modelKey" class="model-key-tag" color="blue">{{ formData.modelKey }}</a-tag>
        </div>

        <!-- 参数提示 -->
        <p v-if="paramVisibility.capabilityHint.value" class="cap-hint">{{ paramVisibility.capabilityHint.value }}</p>
        <p v-if="paramVisibility.hasParamSchema.value && paramVisibility.unsupportedParamCodes.value.length > 0" class="cap-hint muted">
          当前模型参数中有 {{ paramVisibility.unsupportedParamCodes.value.length }} 项暂不支持实例侧填写：{{
            paramVisibility.unsupportedParamCodes.value.join(', ')
          }}
        </p>

        <!-- 对话模型参数 -->
        <template v-if="paramVisibility.modelKind.value === 'chat'">
          <div class="param-list">
            <!-- Temperature -->
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

            <!-- Max Tokens -->
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

            <!-- Top P -->
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

            <!-- Top K -->
            <div v-if="paramVisibility.showChatTopK.value" class="param-group-card">
              <div class="p-header">
                <span class="p-label">Top K</span>
                <a-input-number v-model:value="formData.topK" :max="100" :min="0" :step="1" size="small"/>
              </div>
              <p class="p-inline-hint">0 表示不启用</p>
            </div>

            <!-- Seed -->
            <div v-if="paramVisibility.showChatSeed.value" class="param-group-card">
              <div class="p-header">
                <span class="p-label">随机种子 (Seed)</span>
                <a-input-number v-model:value="formData.seed" :max="2147483647" :min="0" :step="1" size="small"/>
              </div>
            </div>

            <!-- Stop Sequences -->
            <div v-if="paramVisibility.showChatStopSequences.value" class="param-group-card">
              <div class="p-header">
                <span class="p-label">停止序列 (Stop)</span>
              </div>
              <a-textarea v-model:value="formData.stopSequences" :rows="2" placeholder="多个序列用英文逗号分隔"/>
            </div>

            <!-- Frequency / Presence Penalty -->
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

        <!-- 上下文历史长度（通用） -->
        <div class="param-group-card">
          <span class="p-label">上下文历史长度</span>
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

        <!-- 底部操作 -->
        <div class="detail-footer">
          <a-button @click="cancelDetail">取消</a-button>
          <a-button :disabled="!formData.modelKey" type="primary" @click="confirmDetail">
            {{ isAddingNew ? '确认添加' : '应用更改' }}
          </a-button>
        </div>
        </template>
      </div>
    </div>

    <!-- Drawers -->
    <ModelSelectorDrawer
        :fixed-model-type="activeTab"
        :open="modelDrawerOpen"
        @select="onModelDrawerSelect"
        @update:open="modelDrawerOpen = $event"
    />
    <AccountSelectorDrawer
        :open="accountDrawerOpen"
        :provider-filter="selectedModelForForm?.extensionCode"
        @select="onAccountSelect"
        @update:open="accountDrawerOpen = $event"
    />
  </div>
</template>

<script lang="ts" setup>
import {ref, reactive, computed, watch} from 'vue'
import {
  PlusOutlined,
  DeleteOutlined,
  CloudServerOutlined,
  RightOutlined,
  SearchOutlined,
  SwapOutlined,
} from '@ant-design/icons-vue'
import type {AiModel} from '@/api/aiModel'
import type {AiInstance} from '@/api/aiInstance'
import type {AiAccount} from '@/api/aiAccount'
import ModelSelectorDrawer from '@/views/admin/ai-config/ai-model/selector/ModelSelectorDrawer.vue'
import AccountSelectorDrawer from '@/views/admin/ai-config/ai-account/selector/AccountSelectorDrawer.vue'
import {useInstanceParamVisibility, getTempInfo} from '@/views/admin/ai-config/ai-instance/useInstanceParamVisibility'

const props = defineProps<{
  instanceList: AiInstance[]
  availableModels: AiModel[]
  routeStrategy?: string
}>()

const emit = defineEmits<{
  (e: 'update:instanceList', list: AiInstance[]): void
  (e: 'update:routeStrategy', value: string): void
}>()

// ── Tab ──
const modelTypeOptions = [
  {value: 'chat', label: '对话模型实例'},
  {value: 'image', label: '图像模型实例'},
]
const activeTab = ref('chat')
const activeTabLabel = computed(() => modelTypeOptions.find(t => t.value === activeTab.value)?.label || '')

function onTabChange(tab: string) {
  activeTab.value = tab
  selectedIdx.value = -1
  isAddingNew.value = false
}

function countByType(type: string): number {
  return props.instanceList.filter(i => i.modelType === type).length
}

const filteredInstances = computed(() =>
    props.instanceList.filter(i => i.modelType === activeTab.value)
)

// ── Selection ──
const selectedIdx = ref(-1)
const isAddingNew = ref(false)

const currentDetailInstance = computed(() => {
  if (isAddingNew.value) return null
  return filteredInstances.value[selectedIdx.value] ?? null
})

function selectInstance(idx: number) {
  isAddingNew.value = false
  selectedIdx.value = idx
  const inst = filteredInstances.value[idx]
  if (inst) populateForm(inst)
}

// ── Form Data ──
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

const selectedModelForForm = computed(() =>
    props.availableModels.find(m => m.modelKey === formData.modelKey)
)

const paramVisibility = useInstanceParamVisibility(selectedModelForForm)

// ── Model/Account drawers ──
const modelDrawerOpen = ref(false)
const accountDrawerOpen = ref(false)

function onModelDrawerSelect(model: AiModel) {
  formData.modelKey = model.modelKey || ''
  formData.modelType = model.modelType || formData.modelType
  // 自动用 modelName 填充实例名称
  if (model.modelName) {
    formData.instanceName = model.modelName
  }
  modelDrawerOpen.value = false
}

function onAccountSelect(account: AiAccount) {
  formData.accountKey = account.accountKey || ''
  formData.accountName = account.accountName || ''
  accountDrawerOpen.value = false
}

// ── Helpers ──
const routeStrategyOptions = [
  {value: 'roundRobin', label: '轮询 (Round Robin)'},
  {value: 'random', label: '随机 (Random)'},
  {value: 'weightedRandom', label: '加权随机 (Weighted)'},
  {value: 'stickyMemory', label: '粘性会话 (Sticky)'},
  {value: 'failoverOrdered', label: '故障转移 (Failover)'},
]

function onStrategyChange(val: string) {
  emit('update:routeStrategy', val)
}

function getModelLabel(modelKey?: string): string {
  if (!modelKey) return '未选择'
  const m = props.availableModels.find(x => x.modelKey === modelKey)
  return m?.modelName || modelKey
}

function getModelAvatar(modelKey?: string): string {
  if (!modelKey) return ''
  const m = props.availableModels.find(x => x.modelKey === modelKey)
  const raw = m?.providerAvatar
  return typeof raw === 'string' && raw.trim() ? raw.trim() : ''
}

const fallbackOptions = computed(() =>
    props.instanceList
        .filter(i => i !== currentDetailInstance.value)
        .map(inst => ({
          value: inst.instanceKey || '',
          label: (inst.instanceName || inst.instanceKey || '未命名实例') + (inst.isDefault === 'Y' ? ' (默认)' : ''),
        }))
)

// ── Form actions ──
function populateForm(inst: AiInstance) {
  formData.modelType = inst.modelType || 'chat'
  formData.modelKey = inst.modelKey || ''
  formData.accountKey = inst.accountKey || ''
  formData.accountName = inst.accountName || ''
  formData.instanceName = inst.instanceName || ''
  formData.isDefault = inst.isDefault || 'N'
  formData.routeWeight = inst.routeWeight
  formData.fallbackInstanceKey = inst.fallbackInstanceKey
  formData.temperature = inst.temperature ?? 0.7
  formData.maxTokens = inst.maxTokens ?? 2048
  formData.topP = inst.topP ?? 1.0
  formData.topK = inst.topK
  formData.seed = inst.seed
  formData.stopSequences = inst.stopSequences
  formData.frequencyPenalty = inst.frequencyPenalty ?? 0
  formData.presencePenalty = inst.presencePenalty ?? 0
  formData.dimensions = inst.dimensions
  formData.size = inst.size
  formData.style = inst.style
  formData.memoryWindowSize = (inst as any).memoryWindowSize ?? '10'
}

function resetForm() {
  formData.modelType = activeTab.value
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

function startAdd() {
  resetForm()
  isAddingNew.value = true
  selectedIdx.value = -1
}

function cancelDetail() {
  isAddingNew.value = false
  if (filteredInstances.value.length > 0) {
    selectInstance(0)
  }
}

function buildInstance(): AiInstance {
  return {
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
  } as AiInstance
}

function confirmDetail() {
  if (!formData.modelKey) return

  if (isAddingNew.value) {
    const isFirstOfType = !props.instanceList.some(i => i.modelType === formData.modelType)
    const newInstance = {
      ...buildInstance(),
      isDefault: isFirstOfType ? 'Y' : formData.isDefault,
    }
    let newList: AiInstance[]
    if (newInstance.isDefault === 'Y') {
      const cleared = props.instanceList.map(inst => {
        if (inst.modelType === formData.modelType) return {...inst, isDefault: 'N'}
        return inst
      })
      newList = [...cleared, newInstance]
    } else {
      newList = [...props.instanceList, newInstance]
    }
    emit('update:instanceList', newList)
    isAddingNew.value = false
    // Select the newly added instance
    const newFiltered = newList.filter(i => i.modelType === activeTab.value)
    selectedIdx.value = newFiltered.length - 1
  } else if (currentDetailInstance.value) {
    const target = currentDetailInstance.value
    const newList = props.instanceList.map(inst => {
      if (inst === target) return {...inst, ...buildInstance()}
      return inst
    })
    emit('update:instanceList', newList)
  }
}

function removeSelected() {
  if (!currentDetailInstance.value) return
  const target = currentDetailInstance.value
  const newList = props.instanceList.filter(i => i !== target)
  emit('update:instanceList', newList)
  selectedIdx.value = -1
  if (filteredInstances.value.length > 0) {
    // Re-select after removal — filteredInstances will update
    selectedIdx.value = 0
  }
}

// Auto-select first instance when tab changes or list updates
watch(filteredInstances, (list) => {
  if (isAddingNew.value) return
  if (list.length === 0) {
    selectedIdx.value = -1
    return
  }
  if (selectedIdx.value < 0 || selectedIdx.value >= list.length) {
    selectInstance(0)
  }
}, {immediate: true})
</script>

<style scoped>
/* Glass panel */
.glass-panel {
  border-radius: 12px;
  overflow: hidden;
}

/* Tab bar */
.engine-tab-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 28px;
}

.tab-group {
  display: flex;
  gap: 28px;
}

.tab-btn {
  position: relative;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  color: var(--ac-tab-inactive);
  padding: 4px 0;
  transition: all 0.2s;
}

.tab-btn:hover {
  color: var(--text-secondary);
}

.tab-btn.active {
  color: var(--ac-tab-active);
  font-weight: 600;
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 50%;
  transform: translateX(-50%);
  width: 5px;
  height: 5px;
  background: var(--ac-tab-dot);
  border-radius: 50%;
}

.tab-count {
  margin-left: 4px;
  font-size: 10px;
  opacity: 0.5;
  font-weight: 400;
}

.tab-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.strategy-label {
  font-size: 12px;
  color: var(--text-secondary);
  font-weight: 500;
}

.strategy-select {
  width: 210px;
}

.add-instance-btn {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Empty state */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 40px;
}

.empty-icon {
  font-size: 48px;
  color: var(--text-muted);
  margin-bottom: 12px;
}

.empty-text {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 16px;
}

/* Split layout */
.engine-split {
  display: grid;
  grid-template-columns: 4fr 8fr;
}

/* Left panel */
.split-left {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.instance-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.15s;
}

.instance-item:hover {
  background: var(--bg-elevated);
}

.instance-item.selected {
  background: var(--bg-elevated);
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.instance-avatar {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  object-fit: contain;
  flex-shrink: 0;
}

.status-dot.active {
  background: var(--ac-status-active);
  box-shadow: 0 0 8px rgba(52, 211, 153, 0.5);
}

.status-dot.inactive {
  background: var(--ac-status-inactive);
}

.instance-info {
  flex: 1;
  min-width: 0;
}

.instance-name {
  font-size: 12px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 2px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.instance-model {
  font-size: 10px;
  color: var(--text-muted);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.instance-chevron {
  font-size: 10px;
  color: var(--text-muted);
  opacity: 0;
  transition: opacity 0.2s, transform 0.2s;
  flex-shrink: 0;
}

.instance-item:hover .instance-chevron,
.instance-item.selected .instance-chevron {
  opacity: 1;
}

.instance-item.selected .instance-chevron {
  color: var(--text-secondary);
}

.add-dashed-btn {
  width: 100%;
  padding: 12px;
  border: 1px dashed var(--border-subtle);
  border-radius: 10px;
  background: transparent;
  color: var(--text-muted);
  font-size: 11px;
  cursor: pointer;
  transition: color 0.15s;
}

.add-dashed-btn:hover {
  border-color: var(--text-muted);
  color: var(--text-secondary);
}

/* ModelParam panel */
.split-right {
  padding: 28px 32px;
  display: flex;
  flex-direction: column;
  gap: 28px;
}

/* No selection placeholder */
.no-selection-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 300px;
  gap: 12px;
}

.no-selection-icon {
  font-size: 40px;
  color: var(--text-muted);
  opacity: 0.4;
}

.no-selection-text {
  font-size: 13px;
  color: var(--text-muted);
  margin: 0;
}

.detail-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.detail-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.detail-subtitle {
  font-size: 11px;
  color: var(--text-muted);
  margin: 4px 0 0;
}

.delete-btn {
  color: var(--text-muted);
}

.delete-btn:hover {
  color: var(--error);
}

/* Model source grid */
.model-source-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.source-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-label {
  font-size: 11px;
  font-weight: 700;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

/* Weight bar */
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

/* Model pick card */
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

.soft-select {
  border-radius: 8px;
}

/* Account field */
.account-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.account-input-row {
  display: flex;
  gap: 8px;
}

.account-input {
  flex: 1;
  cursor: pointer;
}

/* Param section header */
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

/* Capability hints */
.cap-hint {
  font-size: 12px;
  color: var(--text-muted);
  line-height: 1.5;
  margin: 0;
}

/* Param list */
.param-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* Param group card */
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

/* Penalty row */
.penalty-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
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

/* Context buttons */
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

/* Footer */
.detail-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding-top: 16px;
}

@media (max-width: 960px) {
  .engine-split {
    grid-template-columns: 1fr;
  }

  .model-source-grid,
  .penalty-row {
    grid-template-columns: 1fr;
  }
}
</style>
