<template>
  <AgentConfigSectionShell :step="2" title="推理实例列表">
    <div class="instance-list-container">
      <div class="instance-list-header">
        <span class="header-desc">一个 Agent 可以配置多个推理实例，支持负载均衡和故障降级</span>
        <div class="header-actions">
          <a-select
              :value="props.routeStrategy || 'roundRobin'"
              :options="routeStrategyOptions"
              size="small"
              class="shared-strategy-select"
              @change="emit('update:routeStrategy', $event)"
          />
          <a-button type="primary" size="small" @click="startAdd">
            <PlusOutlined/>
            添加实例
          </a-button>
        </div>
      </div>

      <div class="model-type-tabs">
        <div
            v-for="mt in modelTypeOptions"
            :key="mt.value"
            :class="['type-tab', { active: activeTab === mt.value, configured: hasInstances(mt.value) }]"
            @click="activeTab = mt.value"
        >
          <component :is="mt.icon"/>
          <span>{{ mt.label }}</span>
          <span v-if="hasInstances(mt.value)" class="tab-count">{{ countByType(mt.value) }}</span>
        </div>
      </div>

      <div v-if="filteredInstances.length === 0 && !showInlineForm" class="empty-state">
        <CloudServerOutlined class="empty-icon"/>
        <p class="empty-text">暂无 {{ activeTab === 'chat' ? '对话' : '图像' }} 推理实例</p>
        <a-button type="primary" @click="startAdd">添加第一个实例</a-button>
      </div>

      <div v-else class="instance-card-list">
        <div
            v-for="(instance, idx) in filteredInstances"
            :key="instance.instanceKey || idx"
            class="instance-card"
        >
          <div class="card-header">
            <div class="card-title">
              <span class="instance-index">{{ idx + 1 }}</span>
              <img
                  v-if="getModelAvatar(instance.modelKey)"
                  :src="getModelAvatar(instance.modelKey)"
                  class="card-provider-avatar"
                  alt=""
              />
              <span class="instance-name">{{ instance.instanceName || instance.instanceKey || '未命名实例' }}</span>
              <a-tag :color="instance.modelType === 'chat' ? 'blue' : 'orange'" class="type-tag">
                {{ instance.modelType === 'chat' ? '对话' : '图像' }}
              </a-tag>
              <a-tag v-if="instance.isDefault === 'Y'" color="gold" class="default-tag">默认</a-tag>
            </div>
            <div class="card-actions">
              <a-button size="small" @click="setDefault(instance)">
                {{ instance.isDefault === 'Y' ? '取消默认' : '设为默认' }}
              </a-button>
              <a-button size="small" @click="startEdit(instance)">
                <EditOutlined/>
              </a-button>
              <a-button size="small" danger @click="removeInstance(instance)">
                <DeleteOutlined/>
              </a-button>
            </div>
          </div>

          <div class="card-content">
            <div class="model-info">
              <span class="label">模型</span>
              <span class="value">{{ getModelLabel(instance.modelKey) }}</span>
            </div>
            <div v-if="instance.accountKey" class="model-info">
              <span class="label">账号</span>
              <span class="value">{{ instance.accountName || instance.accountKey }}</span>
            </div>
          </div>

        </div>

        <div v-if="showInlineForm" class="inline-add-form">
          <div class="form-header">
            <div class="form-title">
              {{ editingInstance ? '编辑实例' : '添加实例' }}
            </div>
            <div class="form-actions-top">
              <a-button @click="cancelForm">取消</a-button>
              <a-button type="primary" :disabled="!formData.modelKey" @click="confirmForm">
                {{ editingInstance ? '保存修改' : '确认添加' }}
              </a-button>
            </div>
          </div>

          <div class="form-body">
            <div class="form-col-left">
              <div class="form-section-card">
                <div class="section-card-title">基础配置</div>

                <div class="form-field">
                  <label class="field-label">实例类型</label>
                  <a-radio-group
                      :value="formData.modelType"
                      button-style="solid"
                      size="large"
                      @change="onFormModelTypeChange"
                  >
                    <a-radio-button value="chat">对话</a-radio-button>
                    <a-radio-button value="image">图像理解</a-radio-button>
                  </a-radio-group>
                </div>

                <div class="form-field">
                  <label class="field-label">选择模型</label>
                  <div
                      :class="['model-pick-card', { 'has-model': !!formData.modelKey }]"
                      @click="modelDrawerOpen = true"
                  >
                    <template v-if="formData.modelKey && selectedModelForForm">
                      <img
                          v-if="getModelAvatar(formData.modelKey)"
                          :src="getModelAvatar(formData.modelKey)"
                          class="model-pick-avatar"
                          alt=""
                      />
                      <div v-else class="model-pick-icon">
                        <MessageOutlined v-if="formData.modelType === 'chat'"/>
                        <PictureOutlined v-else/>
                      </div>
                      <div class="model-pick-info">
                        <span class="model-pick-name">{{ selectedModelForForm.modelName }}</span>
                        <span class="model-pick-key">{{ formData.modelKey }}</span>
                      </div>
                      <SwapOutlined class="model-pick-swap"/>
                    </template>
                    <template v-else>
                      <div class="model-pick-icon model-pick-icon--empty">
                        <SearchOutlined/>
                      </div>
                      <span class="model-pick-placeholder">点击选择模型</span>
                    </template>
                  </div>
                </div>

                <div class="form-field">
                  <label class="field-label">关联账号</label>
                  <div
                      :class="['model-pick-card', { 'has-model': !!formData.accountKey }]"
                      @click="accountDrawerOpen = true"
                  >
                    <template v-if="formData.accountKey">
                      <div class="model-pick-icon">
                        <UserOutlined/>
                      </div>
                      <div class="model-pick-info">
                        <span class="model-pick-name">{{ formData.accountName || formData.accountKey }}</span>
                        <span class="model-pick-key">{{ formData.accountKey }}</span>
                      </div>
                      <SwapOutlined class="model-pick-swap"/>
                    </template>
                    <template v-else>
                      <div class="model-pick-icon model-pick-icon--empty">
                        <KeyOutlined/>
                      </div>
                      <span class="model-pick-placeholder">点击选择账号</span>
                    </template>
                  </div>
                </div>

                <div class="form-field">
                  <label class="field-label">实例名称</label>
                  <a-input
                      v-model:value="formData.instanceName"
                      size="large"
                      :placeholder="'可选，留空自动生成'"
                  />
                </div>

                <div v-if="props.routeStrategy === 'weightedRandom'" class="form-field">
                  <label class="field-label">权重</label>
                  <a-input-number
                      v-model:value="formData.routeWeight"
                      :min="1" :max="100"
                      size="large"
                      style="width: 100%"
                  />
                </div>

                <div v-if="props.instanceList.length > 0" class="form-field">
                  <label class="field-label">降级实例</label>
                  <a-select
                      v-model:value="formData.fallbackInstanceKey"
                      :options="fallbackOptionsForForm"
                      :placeholder="'当主实例不可用时降级到'"
                      allow-clear
                      size="large"
                  />
                </div>

                </div>
            </div>

            <div class="form-col-right">
              <div class="form-section-card">
                <div class="section-card-title">
                  模型参数
                  <a-tag v-if="formData.modelKey && selectedModelForForm" color="blue" class="model-key-tag">
                    {{ selectedModelForForm.modelKey }}
                  </a-tag>
                </div>

                <div v-if="!formData.modelKey" class="params-empty">
                  <ControlOutlined class="params-empty-icon"/>
                  <p>请先在左侧选择一个模型</p>
                </div>

                <div v-else class="params-content">
                  <p v-if="paramVisibility.capabilityHint.value" class="cap-hint">
                    {{ paramVisibility.capabilityHint.value }}
                  </p>

                  <template v-if="paramVisibility.modelKind.value === 'chat'">
                    <div v-if="paramVisibility.showChatTemperature.value" class="param-group-card">
                      <div class="p-header">
                        <a-tooltip placement="left">
                          <template #title>控制生成内容的随机性。值越高，输出越多样。</template>
                          <span class="p-label">采样温度 (Temperature) <QuestionCircleOutlined/></span>
                        </a-tooltip>
                        <a-input-number v-model:value="formData.temperature" :max="2" :min="0" :step="0.1" size="large"/>
                      </div>
                      <div class="slider-box">
                        <a-slider v-model:value="formData.temperature" :marks="{ 0: '严谨', 0.7: '平衡', 1.5: '创意', 2: '随机' }" :max="2" :min="0" :step="0.1"/>
                      </div>
                      <div :class="getTempInfo(formData.temperature ?? 0.7).color" class="p-desc-bar">
                        {{ getTempInfo(formData.temperature ?? 0.7).text }}
                      </div>
                    </div>

                    <div v-if="paramVisibility.showChatMaxTokens.value" class="param-group-card">
                      <div class="p-header">
                        <a-tooltip placement="left">
                          <template #title>设置生成内容的最大长度限制。</template>
                          <span class="p-label">响应上限 (Max Tokens) <QuestionCircleOutlined/></span>
                        </a-tooltip>
                        <a-input-number v-model:value="formData.maxTokens" :max="128000" :min="1" size="large"/>
                      </div>
                      <div class="slider-box">
                        <a-slider v-model:value="formData.maxTokens" :marks="{ 0: '短', 2048: '中等', 4096: '长', 8192: '超长' }" :max="8192" :min="0" :step="256"/>
                      </div>
                    </div>

                    <div v-if="paramVisibility.showChatTopP.value" class="param-group-card">
                      <div class="p-header">
                        <a-tooltip placement="left" title="核心采样，控制词汇选择的范围">
                          <span class="p-label">核采样 (Top P) <QuestionCircleOutlined/></span>
                        </a-tooltip>
                        <a-input-number v-model:value="formData.topP" :max="1" :min="0" :step="0.01" size="large"/>
                      </div>
                      <div class="slider-box">
                        <a-slider v-model:value="formData.topP" :marks="{ 0: '极窄', 0.5: '标准', 1: '完整' }" :max="1" :min="0" :step="0.05"/>
                      </div>
                    </div>

                    <a-collapse v-if="hasAdvancedParams" ghost class="advanced-params-collapse">
                      <a-collapse-panel key="advanced" header="更多参数 (可选)">
                        <div v-if="paramVisibility.showChatTopK.value" class="param-group-card">
                          <div class="p-header">
                            <span class="p-label">Top K <QuestionCircleOutlined/></span>
                            <a-input-number v-model:value="formData.topK" :max="100" :min="0" :step="1" size="large"/>
                          </div>
                          <p class="p-inline-hint">0 表示不启用</p>
                        </div>

                        <div v-if="paramVisibility.showChatSeed.value" class="param-group-card">
                          <div class="p-header">
                            <span class="p-label">随机种子 (Seed) <QuestionCircleOutlined/></span>
                            <a-input-number v-model:value="formData.seed" :max="2147483647" :min="0" :step="1" size="large"/>
                          </div>
                        </div>

                        <div v-if="paramVisibility.showChatStopSequences.value" class="param-group-card">
                          <div class="p-header">
                            <span class="p-label">停止序列 (Stop)</span>
                          </div>
                          <a-textarea v-model:value="formData.stopSequences" :rows="2" class="stop-seq-input" placeholder="多个序列用英文逗号分隔"/>
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
                      </a-collapse-panel>
                    </a-collapse>
                  </template>

                  <template v-else-if="paramVisibility.modelKind.value === 'image'">
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
                    <p v-if="!paramVisibility.imageHasAnyControl.value" class="cap-hint muted">当前模型未开放图像可调参数。</p>
                  </template>

                  <p v-if="paramVisibility.hasParamSchema.value && paramVisibility.unsupportedParamCodes.value.length > 0" class="cap-hint muted">
                    当前模型参数中有 {{ paramVisibility.unsupportedParamCodes.value.length }} 项暂不支持填写：{{
                      paramVisibility.unsupportedParamCodes.value.join(', ')
                    }}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <ModelSelector
        :fixed-model-type="formData.modelType"
        :open="modelDrawerOpen"
        @select="onModelDrawerSelect"
        @update:open="modelDrawerOpen = $event"
    />

    <AccountSelectorTable
        :open="accountDrawerOpen"
        :provider-filter="selectedModelForForm?.extensionCode"
        @select="onAccountSelect"
        @update:open="accountDrawerOpen = $event"
    />
  </AgentConfigSectionShell>
</template>

<script lang="ts" setup>
import {ref, reactive, computed, watch, type Component} from 'vue'
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  CloudServerOutlined,
  MessageOutlined,
  PictureOutlined,
  ControlOutlined,
  QuestionCircleOutlined,
  SearchOutlined,
  SwapOutlined,
  UserOutlined,
  KeyOutlined,
} from '@ant-design/icons-vue'
import type {AiModel} from '@/api/aiModel'
import type {AiInstance} from '@/api/aiInstance'
import type {AiAccount} from '@/api/aiAccount'
import AgentConfigSectionShell from './AgentConfigSectionShell.vue'
import ModelSelector from '@/views/admin/ai-config/ai-model/selector/ModelSelector.vue'
import AccountSelectorTable from '@/views/admin/ai-config/ai-account/selector/AccountSelectorTable.vue'
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

interface ModelTypeOption {
  value: string
  label: string
  icon: Component
}

const modelTypeOptions: ModelTypeOption[] = [
  {value: 'chat', label: '对话', icon: MessageOutlined},
  {value: 'image', label: '图像理解', icon: PictureOutlined},
]

const activeTab = ref<string>('chat')
const showInlineForm = ref(false)
const editingInstance = ref<AiInstance | null>(null)
const modelDrawerOpen = ref(false)
const accountDrawerOpen = ref(false)

const routeStrategyOptions = [
  {value: 'roundRobin', label: '轮询 (Round Robin)'},
  {value: 'random', label: '随机 (Random)'},
  {value: 'weightedRandom', label: '加权随机 (Weighted)'},
  {value: 'stickyMemory', label: '粘性会话 (Sticky)'},
  {value: 'failoverOrdered', label: '故障转移 (Failover)'},
]

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
})

const selectedModelForForm = ref<AiModel | undefined>(undefined)

watch(() => formData.modelKey, (key) => {
  selectedModelForForm.value = props.availableModels.find((m) => m.modelKey === key)
})

const paramVisibility = useInstanceParamVisibility(selectedModelForForm)

const hasAdvancedParams = computed(() => {
  return paramVisibility.showChatTopK.value
      || paramVisibility.showChatSeed.value
      || paramVisibility.showChatStopSequences.value
      || paramVisibility.showChatPenalties.value
})

const filteredInstances = computed(() => {
  return props.instanceList.filter((i) => i.modelType === activeTab.value)
})

const fallbackOptionsForForm = computed(() => {
  return props.instanceList
      .filter((i) => i !== editingInstance.value)
      .map((inst) => ({
        value: inst.instanceKey || '',
        label: (inst.instanceName || inst.instanceKey || '未命名实例') + (inst.isDefault === 'Y' ? ' (默认)' : ''),
      }))
})

function hasInstances(type: string): boolean {
  return props.instanceList.some((i) => i.modelType === type)
}

function countByType(type: string): number {
  return props.instanceList.filter((i) => i.modelType === type).length
}

function getModelLabel(modelKey?: string): string {
  if (!modelKey) return '未选择'
  const model = props.availableModels.find((m) => m.modelKey === modelKey)
  return model?.modelName || modelKey
}

function getModelAvatar(modelKey?: string): string {
  if (!modelKey) return ''
  const model = props.availableModels.find((m) => m.modelKey === modelKey)
  const raw = model?.providerAvatar
  return typeof raw === 'string' && raw.trim() ? raw.trim() : ''
}

function onModelDrawerSelect(model: AiModel) {
  formData.modelKey = model.modelKey || ''
  formData.modelType = model.modelType || formData.modelType
  modelDrawerOpen.value = false
}

function onAccountSelect(account: AiAccount) {
  formData.accountKey = account.accountKey || ''
  formData.accountName = account.accountName || ''
  accountDrawerOpen.value = false
}

function onFormModelTypeChange(e: { target: { value: string } }) {
  formData.modelType = e.target.value
  formData.modelKey = ''
}

function resetFormData() {
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
}

function startAdd() {
  editingInstance.value = null
  resetFormData()
  showInlineForm.value = true
}

function startEdit(instance: AiInstance) {
  editingInstance.value = instance
  formData.modelType = instance.modelType || 'chat'
  formData.modelKey = instance.modelKey || ''
  formData.accountKey = instance.accountKey || ''
  formData.accountName = instance.accountName || ''
  formData.instanceName = instance.instanceName || ''
  formData.isDefault = instance.isDefault || 'N'
  formData.routeWeight = instance.routeWeight
  formData.fallbackInstanceKey = instance.fallbackInstanceKey
  formData.temperature = instance.temperature ?? 0.7
  formData.maxTokens = instance.maxTokens ?? 2048
  formData.topP = instance.topP ?? 1.0
  formData.topK = instance.topK
  formData.seed = instance.seed
  formData.stopSequences = instance.stopSequences
  formData.frequencyPenalty = instance.frequencyPenalty ?? 0
  formData.presencePenalty = instance.presencePenalty ?? 0
  formData.dimensions = instance.dimensions
  formData.size = instance.size
  formData.style = instance.style
  showInlineForm.value = true
}

function cancelForm() {
  showInlineForm.value = false
  editingInstance.value = null
}

function buildInstanceFromForm(): AiInstance {
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
  }
}

function confirmForm() {
  if (!formData.modelKey) return

  if (editingInstance.value) {
    const newList = props.instanceList.map((inst) => {
      if (inst === editingInstance.value) {
        return {
          ...inst,
          ...buildInstanceFromForm(),
        }
      }
      return inst
    })
    emit('update:instanceList', newList)
  } else {
    const isFirstOfType = !props.instanceList.some((i) => i.modelType === formData.modelType)
    const newInstance = {
      ...buildInstanceFromForm(),
      isDefault: isFirstOfType ? 'Y' : formData.isDefault,
    }
    if (newInstance.isDefault === 'Y') {
      const cleared = props.instanceList.map((inst) => {
        if (inst.modelType === formData.modelType) {
          return {...inst, isDefault: 'N'}
        }
        return inst
      })
      emit('update:instanceList', [...cleared, newInstance])
    } else {
      emit('update:instanceList', [...props.instanceList, newInstance])
    }
  }

  showInlineForm.value = false
  editingInstance.value = null
}

function removeInstance(instance: AiInstance) {
  const newList = props.instanceList.filter((i) => i !== instance)
  emit('update:instanceList', newList)
}

function setDefault(instance: AiInstance) {
  const newDefault = instance.isDefault === 'Y' ? 'N' : 'Y'
  const newList = props.instanceList.map((inst) => {
    if (inst.modelType === instance.modelType) {
      if (inst === instance) {
        return {...inst, isDefault: newDefault}
      }
      if (newDefault === 'Y') {
        return {...inst, isDefault: 'N'}
      }
    }
    return inst
  })
  emit('update:instanceList', newList)
}
</script>

<style scoped>
.instance-list-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.instance-list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-desc {
  font-size: 12px;
  color: var(--text-secondary);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.shared-strategy-select {
  width: 180px;
}

.model-type-tabs {
  display: flex;
  gap: 8px;
}

.type-tab {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  background: var(--bg-surface);
  border: 1px solid var(--border-subtle);
  cursor: pointer;
  transition: all 0.2s;
}

.type-tab:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.type-tab.active {
  background: var(--primary-hover);
  border-color: var(--primary);
  color: var(--primary);
}

.type-tab.configured {
  border-color: var(--primary);
}

.tab-count {
  background: var(--primary);
  color: #fff;
  font-size: 11px;
  padding: 0 6px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
  line-height: 18px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  border: 1px dashed var(--border-subtle);
  border-radius: var(--radius-lg);
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

.instance-card-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.instance-card {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 1px solid var(--border-subtle);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.instance-index {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--primary);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-provider-avatar {
  width: 20px;
  height: 20px;
  border-radius: 4px;
  object-fit: contain;
}

.type-tag {
  font-size: 11px;
}

.instance-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
}

.default-tag {
  font-size: 11px;
}

.card-actions {
  display: flex;
  gap: 4px;
}

.card-content {
  padding: 14px 16px;
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.card-content .label {
  font-size: 11px;
  color: var(--text-muted);
  margin-right: 6px;
}

.card-content .value {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
}

/* ========== Inline Form ========== */
.inline-add-form {
  border: 2px solid var(--primary);
  border-radius: var(--radius-lg);
  padding: 28px;
  background: #fff;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.08);
}

.form-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e2e8f0;
}

.form-title {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
}

.form-actions-top {
  display: flex;
  gap: 8px;
}

.form-body {
  display: grid;
  grid-template-columns: 340px 1fr;
  gap: 28px;
}

@media (max-width: 900px) {
  .form-body {
    grid-template-columns: 1fr;
  }
}

.form-col-left {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-col-right {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-width: 0;
}

.form-section-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.section-card-title {
  font-size: 13px;
  font-weight: 700;
  color: #334155;
  display: flex;
  align-items: center;
  gap: 8px;
}

.model-key-tag {
  font-size: 11px;
  font-weight: 400;
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: 12px;
  font-weight: 600;
  color: #475569;
}

/* Model pick card */
.model-pick-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border: 2px dashed #cbd5e1;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  background: #fff;
}

.model-pick-card:hover {
  border-color: #3b82f6;
  background: #f0f7ff;
}

.model-pick-card.has-model {
  border-style: solid;
  border-color: #3b82f6;
  background: #f0f7ff;
}

.model-pick-avatar {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  object-fit: contain;
  flex-shrink: 0;
}

.model-pick-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  background: linear-gradient(135deg, #6366f1 0%, #a855f7 100%);
  color: #fff;
  flex-shrink: 0;
}

.model-pick-icon--empty {
  background: #e2e8f0;
  color: #94a3b8;
}

.model-pick-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.model-pick-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.model-pick-key {
  font-size: 11px;
  color: #94a3b8;
  font-family: 'JetBrains Mono', monospace;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.model-pick-placeholder {
  font-size: 14px;
  color: #94a3b8;
}

.model-pick-swap {
  color: #94a3b8;
  font-size: 14px;
  flex-shrink: 0;
}

/* Parameters */
.params-empty {
  text-align: center;
  padding: 60px 0;
  color: #cbd5e1;
}

.params-empty-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.params-content {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.cap-hint {
  font-size: 12px;
  color: #64748b;
  line-height: 1.5;
  padding: 10px 12px;
  background: #fff;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
}

.cap-hint.muted {
  color: #94a3b8;
  background: #fafafa;
  border-style: dashed;
}

.param-group-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 16px;
  transition: all 0.2s;
}

.p-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.p-label {
  font-size: 13px;
  font-weight: 600;
  color: #334155;
  cursor: help;
  display: flex;
  align-items: center;
  gap: 4px;
}

.slider-box {
  padding: 0 8px 16px 8px;
}

.p-desc-bar {
  margin-top: 8px;
  padding: 10px 14px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
  border-left: 4px solid transparent;
}

.p-desc-bar.c-blue {
  background: #eff6ff;
  border-left-color: #3b82f6;
  color: #1e40af;
}

.p-desc-bar.c-purple {
  background: #f5f3ff;
  border-left-color: #8b5cf6;
  color: #5b21b6;
}

.p-desc-bar.c-orange {
  background: #fff7ed;
  border-left-color: #f97316;
  color: #9a3412;
}

.p-desc-bar.c-red {
  background: #fef2f2;
  border-left-color: #ef4444;
  color: #991b1b;
}

.p-inline-hint {
  font-size: 11px;
  color: #94a3b8;
  margin: 0;
}

.penalty-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.mini-param-card {
  background: #fff;
  padding: 14px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.mini-label {
  font-size: 12px;
  font-weight: 700;
  color: #64748b;
  margin-bottom: 10px;
  display: block;
}

.stop-seq-input {
  font-family: 'JetBrains Mono', monospace;
}

/* Advanced params collapse */
.advanced-params-collapse {
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
}

.advanced-params-collapse :deep(.ant-collapse-header) {
  font-size: 13px;
  font-weight: 600;
  color: #475569;
}

.advanced-params-collapse :deep(.ant-collapse-content-box) {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 8px;
}
</style>
