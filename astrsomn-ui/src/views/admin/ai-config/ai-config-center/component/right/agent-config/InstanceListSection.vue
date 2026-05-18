<template>
  <div class="instance-panel">
    <!-- 头部：Tab + 操作栏 -->
    <div class="panel-header">
      <div class="tab-group">
        <button
            v-for="mt in modelTypeOptions"
            :key="mt.value"
            :class="['tab-btn', { active: activeTab === mt.value }]"
            @click="onTabChange(mt.value)"
        >
          <span class="tab-text">{{ mt.label }}</span>
          <span v-if="countByType(mt.value) > 0" class="tab-badge">{{ countByType(mt.value) }}</span>
        </button>
      </div>
      <div class="header-actions">
        <div class="strategy-wrapper">
          <span class="strategy-label">策略</span>
          <a-select
              :value="props.routeStrategy || 'roundRobin'"
              :options="routeStrategyOptions"
              class="strategy-select"
              size="small"
              @change="onStrategyChange"
          />
        </div>
        <a-button class="add-btn" size="small" type="primary" @click="startAdd">
          <template #icon><PlusOutlined /></template>
          添加
        </a-button>
      </div>
    </div>

    <!-- 内容区 -->
    <div class="panel-body">
      <!-- 空状态 -->
      <div v-if="filteredInstances.length === 0" class="empty-state">
        <div class="empty-icon-wrapper">
          <CloudServerOutlined />
        </div>
        <p class="empty-title">暂无{{ activeTabLabel }}实例</p>
        <p class="empty-desc">添加第一个推理实例开始使用</p>
        <a-button class="empty-add-btn" type="primary" @click="startAdd">
          <template #icon><PlusOutlined /></template>
          添加实例
        </a-button>
      </div>

      <!-- 实例列表 -->
      <div v-else class="instance-list">
        <div
            v-for="(instance, idx) in filteredInstances"
            :key="instance.instanceKey || idx"
            :class="['instance-item', { active: instance.status === 'enabled' }]"
            @click="editInstance(instance)"
        >
          <div class="instance-icon">
            <img
                v-if="getModelAvatar(instance.modelKey)"
                :alt="getModelLabel(instance.modelKey)"
                :src="getModelAvatar(instance.modelKey)"
                class="model-avatar"
            />
            <div v-else class="model-placeholder">
              <ApartmentOutlined />
            </div>
          </div>
          <div class="instance-content">
            <div class="instance-header">
              <span class="instance-name">{{ instance.instanceName || getModelLabel(instance.modelKey) || '未命名' }}</span>
              <a-tag v-if="instance.isDefault === 'Y'" class="default-badge" color="blue">默认</a-tag>
            </div>
            <div class="instance-meta">
              <code class="model-key">{{ instance.modelKey }}</code>
              <span v-if="instance.status === 'enabled'" class="status-indicator active">
                <span class="status-dot"></span>
                启用
              </span>
              <span v-else class="status-indicator inactive">
                <span class="status-dot"></span>
                禁用
              </span>
            </div>
          </div>
          <div class="instance-actions">
            <a-button
                class="action-btn"
                type="text"
                size="small"
                @click.stop="editInstance(instance)"
            >
              <template #icon><EditOutlined /></template>
            </a-button>
            <a-button
                class="action-btn delete"
                type="text"
                size="small"
                danger
                @click.stop="removeInstance(instance)"
            >
              <template #icon><DeleteOutlined /></template>
            </a-button>
          </div>
        </div>

        <button class="add-more-btn" @click="startAdd">
          <PlusOutlined />
          <span>添加{{ activeTabLabel }}实例</span>
        </button>
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <InstanceEditModal
        :available-models="availableModels"
        :instance-list="instanceList"
        :model-type="activeTab"
        :open="modalOpen"
        :record="editingInstance"
        :route-strategy="routeStrategy"
        @confirm="onModalConfirm"
        @update:open="modalOpen = $event"
    />
  </div>
</template>

<script lang="ts" setup>
import {computed, ref} from 'vue'
import {ApartmentOutlined, CloudServerOutlined, DeleteOutlined, EditOutlined, PlusOutlined} from '@ant-design/icons-vue'
import type {AiModel} from '@/api/aiModel.ts'
import type {AiInstance} from '@/api/aiInstance.ts'
import InstanceEditModal from './form/InstanceEditModal.vue'

const props = defineProps<{
  instanceList: AiInstance[]
  availableModels: AiModel[]
  routeStrategy?: string
}>()

const emit = defineEmits<{
  (e: 'update:instanceList', list: AiInstance[]): void
  (e: 'update:routeStrategy', value: string): void
}>()


const modelTypeOptions = [
  { value: 'chat', label: '对话模型' },
  { value: 'image', label: '图像模型' },
]
const activeTab = ref('chat')
const activeTabLabel = computed(() => modelTypeOptions.find(t => t.value === activeTab.value)?.label || '')

function onTabChange(tab: string) {
  activeTab.value = tab
}

function countByType(type: string): number {
  return props.instanceList.filter(i => i.modelType === type).length
}

const filteredInstances = computed(() =>
    props.instanceList.filter(i => i.modelType === activeTab.value)
)

// ── Modal ──
const modalOpen = ref(false)
const editingInstance = ref<AiInstance | null>(null)

function startAdd() {
  editingInstance.value = null
  modalOpen.value = true
}

function editInstance(instance: AiInstance) {
  editingInstance.value = instance
  modalOpen.value = true
}

function onModalConfirm(instance: AiInstance) {
  if (editingInstance.value?.instanceKey) {
    const newList = props.instanceList.map(inst =>
        inst.instanceKey === editingInstance.value!.instanceKey ? { ...inst, ...instance } : inst
    )
    emit('update:instanceList', newList)
  } else {
    const isFirstOfType = !props.instanceList.some(i => i.modelType === instance.modelType)
    const newInstance = {
      ...instance,
      instanceKey: instance.instanceKey || `inst_${Date.now()}`,
      isDefault: isFirstOfType ? 'Y' : (instance.isDefault || 'N'),
    }
    let newList: AiInstance[]
    if (newInstance.isDefault === 'Y') {
      const cleared = props.instanceList.map(inst => {
        if (inst.modelType === instance.modelType) return { ...inst, isDefault: 'N' }
        return inst
      })
      newList = [...cleared, newInstance]
    } else {
      newList = [...props.instanceList, newInstance]
    }
    emit('update:instanceList', newList)
  }
}

function removeInstance(instance: AiInstance) {
  const newList = props.instanceList.filter(i => i !== instance)
  emit('update:instanceList', newList)
}

// ── Helpers ──
const routeStrategyOptions = [
  { value: 'roundRobin', label: '轮询' },
  { value: 'random', label: '随机' },
  { value: 'weightedRandom', label: '加权随机' },
  { value: 'stickyMemory', label: '粘性会话' },
  { value: 'failoverOrdered', label: '故障转移' },
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
</script>

<style scoped>
.instance-panel {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: 12px;
  overflow: hidden;
  height: 100%;
  display: flex;
  flex-direction: column;
}


.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid var(--border-subtle);
  flex-shrink: 0;
}

.tab-group {
  display: flex;
  gap: 4px;
  background: var(--bg-surface);
  border-radius: 8px;
  padding: 3px;
}

.tab-btn {
  position: relative;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  color: var(--text-muted);
  padding: 6px 12px;
  border-radius: 6px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  gap: 6px;
}

.tab-btn:hover {
  color: var(--text-secondary);
}

.tab-btn.active {
  background: var(--bg-card);
  color: var(--text-primary);
  font-weight: 600;
  box-shadow: var(--shadow-sm);
}

.tab-badge {
  font-size: 10px;
  font-weight: 600;
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
  padding: 1px 6px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}

.tab-btn.active .tab-badge {
  background: #3b82f6;
  color: #fff;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.strategy-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.strategy-label {
  font-size: 12px;
  color: var(--text-muted);
  font-weight: 500;
}

.strategy-select {
  width: 160px;
}

.add-btn {
  height: 32px;
  padding: 0 12px;
  font-size: 12px;
  font-weight: 500;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}


.panel-body {
  flex: 1;
  min-height: 0;
  overflow: hidden;
}


.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  height: 100%;
}

.empty-icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  background: var(--bg-surface);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: var(--text-muted);
  margin-bottom: 16px;
}

.empty-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 4px;
}

.empty-desc {
  font-size: 12px;
  color: var(--text-muted);
  margin: 0 0 20px;
}

.empty-add-btn {
  height: 36px;
  padding: 0 16px;
  font-size: 13px;
  font-weight: 500;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}


.instance-list {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow-y: auto;
  height: 100%;
}

.instance-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid var(--border-subtle);
  background: var(--bg-surface);
}

.instance-item:hover {
  background: var(--bg-input);
  border-color: var(--border-default);
}

.instance-item.active {
  border-color: rgba(59, 130, 246, 0.15);
  background: rgba(59, 130, 246, 0.02);
}

.instance-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  overflow: hidden;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-surface);
}

.model-avatar {
  width: 28px;
  height: 28px;
  object-fit: contain;
}

.model-placeholder {
  font-size: 18px;
  color: var(--text-muted);
}

.instance-content {
  flex: 1;
  min-width: 0;
}

.instance-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.instance-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.default-badge {
  font-size: 10px;
  font-weight: 500;
  padding: 0 6px;
  height: 18px;
  line-height: 18px;
  border-radius: 4px;
}

.instance-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.model-key {
  font-size: 11px;
  color: var(--text-muted);
  background: var(--bg-surface);
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'SF Mono', 'Monaco', 'Menlo', monospace;
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 11px;
  font-weight: 500;
}

.status-indicator.active {
  color: #10b981;
}

.status-indicator.inactive {
  color: var(--text-muted);
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.status-indicator.active .status-dot {
  background: #10b981;
  box-shadow: 0 0 6px rgba(16, 185, 129, 0.4);
}

.status-indicator.inactive .status-dot {
  background: var(--text-muted);
}

.instance-actions {
  display: flex;
  align-items: center;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.2s;
}

.instance-item:hover .instance-actions {
  opacity: 1;
}

.action-btn {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  color: var(--text-muted);
  transition: all 0.2s;
}

.action-btn:hover {
  background: var(--bg-surface);
  color: var(--text-primary);
}

.action-btn.delete:hover {
  background: rgba(239, 68, 68, 0.08);
  color: #ef4444;
}


.add-more-btn {
  width: 100%;
  padding: 12px;
  border: 1px dashed var(--border-default);
  border-radius: 10px;
  background: transparent;
  color: var(--text-muted);
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.add-more-btn:hover {
  border-color: #3b82f6;
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.02);
}
</style>
