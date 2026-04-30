<template>
  <div class="left-top">
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

    <AccountSelector
      v-model:open="selectDrawerOpen"
      @select="handleAccountSelect"
    />

    <ModelSelector
      v-model:open="modelDrawerOpen"
      @select="handleModelSelect"
    />

    <InstanceSelectDrawer
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
import AccountCard from './left-top/AccountCard.vue'
import ModelCard from './left-top/ModelCard.vue'
import InstanceCard from './left-top/InstanceCard.vue'
import AgentCard from './left-top/AgentCard.vue'
import AccountForm from '../../ai-account/AccountForm.vue'
import AccountSelector from '../../ai-account/selector/AccountSelector.vue'
import ModelSelector from '../../ai-model/selector/ModelSelector.vue'
import InstanceSelectDrawer from '../../ai-instance/InstanceSelectDrawer.vue'
import InstanceForm from '../../ai-instance/InstanceForm.vue'
import type { AiAccount } from '@/api/aiAccount'
import type { AiModel } from '@/api/aiModel'
import type { AiInstance } from '@/api/aiInstance'

const props = defineProps<{
  agentName?: string
  description?: string
}>()

const emit = defineEmits<{
  (e: 'update:agentName', value: string): void
  (e: 'update:description', value: string): void
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
}

const handleSelectModel = () => {
  modelDrawerOpen.value = true
}

const handleModelSelect = (model: AiModel) => {
  currentModel.value = model
  modelDrawerOpen.value = false
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
