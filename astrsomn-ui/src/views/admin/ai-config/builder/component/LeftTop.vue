<template>
  <div class="left-top">
    <div class="builder-topbar">
      <a-tooltip title="返回">
        <button class="topbar-icon-btn" @click="emit('cancel')">
          <LeftOutlined />
        </button>
      </a-tooltip>
      <span class="topbar-title">{{ isEdit ? '编辑智能体' : '新建智能体' }}</span>
      <div class="topbar-actions">
        <a-tooltip title="重置">
          <button class="topbar-icon-btn" :disabled="submitting" @click="emit('reset')">
            <ReloadOutlined />
          </button>
        </a-tooltip>
        <a-tooltip :title="isEdit ? '保存' : '发布'">
          <button class="topbar-icon-btn primary" :disabled="submitting" @click="emit('submit')">
            <span v-if="submitting" class="spinner"></span>
            <RocketOutlined v-else />
          </button>
        </a-tooltip>
      </div>
    </div>

    <div class="section-header">
      <div class="section-title">
        <span class="title-indicator"></span>
        <h2 class="title-text">核心运行链</h2>
      </div>
      <span class="section-badge">必填项</span>
    </div>

    <div class="card-chain">
      <AccountCard
        :account="currentAccount"
        @add="handleAddAccount"
        @switch="handleSwitchAccount"
      />
      <ModelCard
        :model="currentModel"
        @select="handleSelectModel"
      />
      <InstanceCard
        :instance="currentInstance"
        @select="handleSelectInstance"
        @create="handleCreateInstance"
      />
      <AgentCard 
        :agent-name="agentName"
        :description="description"
        @update:agentName="handleAgentNameUpdate"
        @update:description="handleDescriptionUpdate"
      />
    </div>

    <AccountForm
      v-model:visible="formVisible"
      :record="currentRecord"
      @success="handleFormSuccess"
    />

    <AccountSelectorTable
      v-model:open="selectDrawerOpen"
      @select="handleAccountSelect"
    />

    <ModelSelector
      v-model:open="modelDrawerOpen"
      @select="handleModelSelect"
    />

    <InstanceSelector
      v-model:open="instanceDrawerOpen"
      @select="handleInstanceSelect"
    />

    <InstanceForm
      v-model:visible="instanceFormVisible"
      :record="currentInstance"
      @success="handleInstanceFormSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { LeftOutlined, ReloadOutlined, RocketOutlined } from '@ant-design/icons-vue'
import AccountCard from './left-top/AccountCard.vue'
import ModelCard from './left-top/ModelCard.vue'
import InstanceCard from './left-top/InstanceCard.vue'
import AgentCard from './left-top/AgentCard.vue'
import AccountForm from '../../ai-account/AccountForm.vue'
import AccountSelectorTable from '../../ai-account/selector/AccountSelectorTable.vue'
import ModelSelector from '../../ai-model/selector/ModelSelector.vue'
import InstanceSelector from '../../ai-instance/selector/InstanceSelector.vue'
import InstanceForm from '../../ai-instance/InstanceForm.vue'
import type { AiAccount } from '@/api/aiAccount'
import type { AiModel } from '@/api/aiModel'
import type { AiInstance } from '@/api/aiInstance'

const props = defineProps<{
  agentName?: string
  description?: string
  isEdit?: boolean
  submitting?: boolean
}>()

const emit = defineEmits<{
  (e: 'update:agentName', value: string): void
  (e: 'update:description', value: string): void
  (e: 'update:account', value: AiAccount | undefined): void
  (e: 'update:model', value: AiModel | undefined): void
  (e: 'update:instance', value: AiInstance | undefined): void
  (e: 'cancel'): void
  (e: 'reset'): void
  (e: 'submit'): void
}>()

const formVisible = ref(false)
const currentRecord = ref<AiAccount | undefined>(undefined)
const selectDrawerOpen = ref(false)
const modelDrawerOpen = ref(false)
const instanceDrawerOpen = ref(false)
const instanceFormVisible = ref(false)
const currentInstance = ref<AiInstance | undefined>(undefined)
const currentAccount = ref<AiAccount | undefined>(undefined)
const currentModel = ref<AiModel | undefined>(undefined)

const handleAddAccount = () => {
  currentRecord.value = undefined
  formVisible.value = true
}

const handleSwitchAccount = () => {
  selectDrawerOpen.value = true
}

const handleFormSuccess = () => {
  formVisible.value = false
}

const handleAccountSelect = (account: AiAccount) => {
  currentAccount.value = account
  selectDrawerOpen.value = false
  emit('update:account', account)
}

const handleSelectModel = () => {
  modelDrawerOpen.value = true
}

const handleModelSelect = (model: AiModel) => {
  currentModel.value = model
  modelDrawerOpen.value = false
  emit('update:model', model)
}

const handleSelectInstance = () => {
  instanceDrawerOpen.value = true
}

const handleCreateInstance = () => {
  currentInstance.value = undefined
  instanceFormVisible.value = true
}

const handleInstanceSelect = (instance: AiInstance) => {
  currentInstance.value = instance
  instanceDrawerOpen.value = false
  emit('update:instance', instance)
}

const handleInstanceFormSuccess = () => {
  instanceFormVisible.value = false
}

const handleAgentNameUpdate = (value: string) => {
  emit('update:agentName', value)
}

const handleDescriptionUpdate = (value: string) => {
  emit('update:description', value)
}

function setAccount(account: AiAccount | undefined) {
  currentAccount.value = account
}

function setModel(model: AiModel | undefined) {
  currentModel.value = model
}

function setInstance(instance: AiInstance | undefined) {
  currentInstance.value = instance
}

defineExpose({ setAccount, setModel, setInstance })
</script>

<style scoped>
.left-top {
  flex-shrink: 0;
  padding: 0 8px 0 0;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-indicator {
  width: 6px;
  height: 16px;
  background: #2563eb;
  border-radius: 4px;
}

.title-text {
  font-weight: 700;
  font-size: 14px;
  color: #1e293b;
  margin: 0;
}

.section-badge {
  font-size: 10px;
  color: #2563eb;
  background: #dbeafe;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 700;
}

.builder-topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 0 12px 0;
  margin-bottom: 4px;
  border-bottom: 1px solid var(--border-default, #e2e8f0);
}

.topbar-title {
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
  flex: 1;
  text-align: center;
}

.topbar-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.topbar-icon-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 1px solid var(--border-default, #e2e8f0);
  background: #fff;
  color: #64748b;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 15px;
  padding: 0;
}

.topbar-icon-btn:hover {
  color: #1e293b;
  border-color: #94a3b8;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.topbar-icon-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.topbar-icon-btn.primary {
  background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%);
  color: #fff;
  border: none;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.35);
}

.topbar-icon-btn.primary:hover {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  box-shadow: 0 2px 12px rgba(59, 130, 246, 0.45);
}

.spinner {
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.card-chain {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  align-items: stretch;
}

.card-chain > :deep(.node-connector) {
  min-width: 0;
  min-height: 0;
  height: 100%;
  display: flex;
}
</style>
