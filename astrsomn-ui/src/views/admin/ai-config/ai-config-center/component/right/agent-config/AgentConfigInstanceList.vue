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
    <div v-if="filteredInstances.length === 0" class="empty-state">
      <CloudServerOutlined class="empty-icon"/>
      <p class="empty-text">暂无 {{ activeTabLabel }} 推理实例</p>
      <a-button type="primary" @click="startAdd">添加第一个实例</a-button>
    </div>

    <!-- 实例卡片列表 -->
    <div v-else class="instance-card-list">
      <div
          v-for="(instance, idx) in filteredInstances"
          :key="instance.instanceKey || idx"
          class="instance-card"
          @click="editInstance(instance)"
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
        <div class="instance-actions">
          <a-tag v-if="instance.isDefault === 'Y'" class="default-tag" color="blue">默认</a-tag>
          <a-button
              class="delete-btn"
              danger
              size="small"
              type="text"
              @click.stop="removeInstance(instance)"
          >
            <DeleteOutlined/>
          </a-button>
        </div>
      </div>

      <button class="add-dashed-btn" @click="startAdd">
        + 添加新{{ activeTabLabel }}实例
      </button>
    </div>

    <!-- 实例编辑弹窗 -->
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
import {ref, computed} from 'vue'
import {PlusOutlined, DeleteOutlined, CloudServerOutlined} from '@ant-design/icons-vue'
import type {AiModel} from '@/api/aiModel.ts'
import type {AiInstance} from '@/api/aiInstance.ts'
import InstanceEditModal from './InstanceEditModal.vue'

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
    // Edit existing
    const newList = props.instanceList.map(inst =>
        inst.instanceKey === editingInstance.value!.instanceKey ? {...inst, ...instance} : inst
    )
    emit('update:instanceList', newList)
  } else {
    // Add new
    const isFirstOfType = !props.instanceList.some(i => i.modelType === instance.modelType)
    const newInstance = {
      ...instance,
      instanceKey: instance.instanceKey || `inst_${Date.now()}`,
      isDefault: isFirstOfType ? 'Y' : (instance.isDefault || 'N'),
    }
    let newList: AiInstance[]
    if (newInstance.isDefault === 'Y') {
      const cleared = props.instanceList.map(inst => {
        if (inst.modelType === instance.modelType) return {...inst, isDefault: 'N'}
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
</script>

<style scoped>
.glass-panel {
  border-radius: 12px;
  overflow: hidden;
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* Tab bar */
.engine-tab-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  flex-shrink: 0;
}

.tab-group {
  display: flex;
  gap: 24px;
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
  width: 200px;
}

.add-instance-btn {
  width: 32px;
  height: 32px;
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
  padding: 40px;
  flex: 1;
}

.empty-icon {
  font-size: 40px;
  color: var(--text-muted);
  margin-bottom: 12px;
}

.empty-text {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 12px;
}

/* Instance card list */
.instance-card-list {
  flex: 1;
  min-height: 0;
  padding: 0 20px 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow-y: auto;
}

.instance-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.15s;
  border: 1px solid var(--border-subtle);
}

.instance-card:hover {
  background: var(--bg-elevated);
  border-color: var(--text-muted);
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
  font-family: monospace;
}

.instance-actions {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
}

.default-tag {
  font-size: 10px;
}

.delete-btn {
  color: var(--text-muted);
  opacity: 0;
  transition: opacity 0.15s;
}

.instance-card:hover .delete-btn {
  opacity: 1;
}

.delete-btn:hover {
  color: var(--error);
}

.add-dashed-btn {
  width: 100%;
  padding: 10px;
  border: 1px dashed var(--border-subtle);
  border-radius: 10px;
  background: transparent;
  color: var(--text-muted);
  font-size: 11px;
  cursor: pointer;
  transition: color 0.15s;
  flex-shrink: 0;
}

.add-dashed-btn:hover {
  border-color: var(--text-muted);
  color: var(--text-secondary);
}
</style>
