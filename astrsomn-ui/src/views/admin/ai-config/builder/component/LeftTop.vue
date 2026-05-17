<template>
  <div class="left-top">
    <div class="card-chain">
      <AgentCard
          :agent-name="agentName"
          :description="description"
          @update:agentName="handleAgentNameUpdate"
          @update:description="handleDescriptionUpdate"
      />
      <InstanceCard
          :instance="currentInstance"
          @create="handleCreateInstance"
          @select="handleSelectInstance"
      />
      <ModelCard
          :model="currentModel"
          readonly
      />
      <AccountCard
          :account="currentAccount"
          readonly
      />
    </div>

    <InstanceSelectorDrawer
        v-model:open="instanceDrawerOpen"
        @create="handleCreateFromDrawer"
        @edit="handleInstanceEditFromDrawer"
        @select="handleInstanceSelect"
    />

    <InstanceForm
        v-model:visible="instanceFormVisible"
        :record="currentInstance"
        @success="handleInstanceFormSuccess"
    />
  </div>
</template>

<script lang="ts" setup>
import {ref} from 'vue'
import AccountCard from './left-top/AccountCard.vue'
import ModelCard from './left-top/ModelCard.vue'
import InstanceCard from './left-top/InstanceCard.vue'
import AgentCard from './left-top/AgentCard.vue'
import InstanceSelectorDrawer from '../../ai-instance/selector/InstanceSelectorDrawer.vue'
import InstanceForm from '../../ai-instance/InstanceForm.vue'
import type {AiModel} from '@/api/aiModel'
import {aiModelApi} from '@/api/aiModel'
import type {AiAccount} from '@/api/aiAccount'
import {aiAccountApi} from '@/api/aiAccount'
import type {AiInstance} from '@/api/aiInstance'

defineProps<{
  agentName?: string
  description?: string
}>()

const emit = defineEmits<{
  (e: 'update:agentName', value: string): void
  (e: 'update:description', value: string): void
  (e: 'update:account', value: AiAccount | undefined): void
  (e: 'update:model', value: AiModel | undefined): void
  (e: 'update:instance', value: AiInstance | undefined): void
}>()

const instanceDrawerOpen = ref(false)
const instanceFormVisible = ref(false)
const currentInstance = ref<AiInstance | undefined>(undefined)
const currentAccount = ref<AiAccount | undefined>(undefined)
const currentModel = ref<AiModel | undefined>(undefined)

async function resolveModelAndAccount(instance: AiInstance) {
  const tasks: Promise<void>[] = []

  if (instance.modelKey) {
    tasks.push(
        aiModelApi.queryPage({pageNo: 1, pageSize: 1, param: {modelKey: instance.modelKey}})
            .then((resp) => {
              const model = resp.list?.[0]
              currentModel.value = model
              emit('update:model', model)
            })
    )
  } else {
    currentModel.value = undefined
    emit('update:model', undefined)
  }

  if (instance.accountKey) {
    tasks.push(
        aiAccountApi.queryPage({pageNo: 1, pageSize: 1, param: {accountKey: instance.accountKey}})
            .then((resp) => {
              const account = resp.list?.[0]
              currentAccount.value = account
              emit('update:account', account)
            })
    )
  } else {
    currentAccount.value = undefined
    emit('update:account', undefined)
  }

  await Promise.allSettled(tasks)
}

const handleSelectInstance = () => {
  instanceDrawerOpen.value = true
}

const handleCreateInstance = () => {
  currentInstance.value = undefined
  instanceFormVisible.value = true
}

const handleCreateFromDrawer = () => {
  instanceDrawerOpen.value = false
  currentInstance.value = undefined
  instanceFormVisible.value = true
}

const handleInstanceEditFromDrawer = (instance: AiInstance) => {
  instanceDrawerOpen.value = false
  currentInstance.value = instance
  instanceFormVisible.value = true
}

const handleInstanceSelect = (instance: AiInstance) => {
  currentInstance.value = instance
  instanceDrawerOpen.value = false
  emit('update:instance', instance)
  void resolveModelAndAccount(instance)
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

defineExpose({setAccount, setModel, setInstance})
</script>

<style scoped>
.left-top {
  padding: 0 8px 0 0;
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
