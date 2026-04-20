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
        :agent-desc="agentDesc"
        @update:agentName="handleAgentNameUpdate"
        @update:agentDesc="handleAgentDescUpdate"
      />
    </div>

    <AccountForm
      v-model:visible="formVisible"
      :record="currentRecord"
      @success="handleFormSuccess"
    />

    <AccountSelectDrawer
      v-model:open="selectDrawerOpen"
      @select="handleAccountSelect"
    />

    <ModelSelectDrawer
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
import AccountSelectDrawer from '../../ai-account/AccountSelectDrawer.vue'
import ModelSelectDrawer from '../../ai-model/ModelSelectDrawer.vue'
import InstanceSelectDrawer from '../../ai-instance/InstanceSelectDrawer.vue'
import InstanceForm from '../../ai-instance/InstanceForm.vue'
import type { AiAccount } from '@/api/aiAccount'
import type { AiModel } from '@/api/aiModel'
import type { AiInstance } from '@/api/aiInstance'

const props = defineProps<{
  agentName?: string
  agentDesc?: string
}>()

const emit = defineEmits<{
  (e: 'update:agentName', value: string): void
  (e: 'update:agentDesc', value: string): void
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

const handleAgentDescUpdate = (value: string) => {
  emit('update:agentDesc', value)
}
</script>

<style scoped>
.left-top {
  padding: 32px 32px 16px;
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
  background: var(--primary);
  border-radius: 3px;
}

.title-text {
  font-weight: bold;
  font-size: 14px;
  color: var(--text-heading);
  margin: 0;
}

.section-badge {
  font-size: 10px;
  color: var(--primary);
  background: var(--primary-hover);
  padding: 2px 8px;
  border-radius: var(--radius-max);
  font-weight: bold;
}

.card-chain {
  display: flex;
  align-items: center;
  gap: 16px;
}
</style>
