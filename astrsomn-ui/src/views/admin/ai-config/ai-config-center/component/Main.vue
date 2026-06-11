<template>
  <a-layout-content>
    <transition mode="out-in" name="fade">
      <!-- 全局管理页面 -->
      <component
          :is="currentGlobalComponent"
          v-if="currentViewType === 'global'"
          :key="currentViewKey"
          :initial-view-mode="initialViewMode"
      />
      <!-- Agent 配置页 -->
      <AgentForm
          v-else-if="showConfig"
          :agent-id="configAgentId"
          :agent-name="configAgentName"
          @back="handleConfigBack"
      />
      <!-- Agent + Model Tab 切换 -->
      <div v-else :key="currentProviderKey" class="provider-detail-view">
        <a-tabs v-model:activeKey="activeTab" class="config-tabs">
          <a-tab-pane key="agents" :tab="t.main.tabs.agents">
            <AgentSection
                :provider-key="currentProviderKey"
                :provider-name="selectedProvider?.name"
                :provider-description="selectedProvider?.description"
                :provider-avatar="selectedProvider?.avatar"
                @create="handleCreateAgent"
                @select="handleSelectAgent"
            />
          </a-tab-pane>
          <a-tab-pane key="models" :tab="t.main.tabs.models">
            <ModelSection
                :provider-key="currentProviderKey"
                :extension-id="selectedProvider?.id"
            />
          </a-tab-pane>
        </a-tabs>
      </div>
    </transition>
  </a-layout-content>
</template>

<script lang="ts" setup>
import {ref} from 'vue'
import AgentSection from './right/AgentSection.vue'
import ModelSection from './right/ModelSection.vue'
import AgentForm from './right/AgentForm.vue'
import type {AiAgent} from '@/api/aiAgent'
import {usePageTranslation} from '@/locales/pages.ts'

const props = defineProps<{
  currentViewType: string
  currentViewKey: string
  currentGlobalComponent: any
  initialViewMode: 'grid' | 'list'
  currentProviderKey: string
  selectedProvider: { id: string | number; key: string; name: string; description: string; avatar: string } | null
}>()

const emit = defineEmits<{
  'create-agent': []
  'select-agent': [agent: AiAgent]
  'config-back': []
}>()

const activeTab = ref('agents')
const showConfig = ref(false)
const configAgentName = ref('')
const configAgentId = ref<string | number | undefined>(undefined)

const t = usePageTranslation('ai-config-center')

const handleCreateAgent = () => {
  configAgentId.value = undefined
  configAgentName.value = t.value.main.newAgent
  showConfig.value = true
  emit('create-agent')
}

const handleSelectAgent = (agent: AiAgent) => {
  configAgentId.value = agent.id
  configAgentName.value = agent.agentName || 'Agent'
  showConfig.value = true
  emit('select-agent', agent)
}

const handleConfigBack = () => {
  showConfig.value = false
  configAgentName.value = ''
  configAgentId.value = undefined
  emit('config-back')
}
</script>

<style scoped>
.ant-layout-content {
  flex: 1;
  min-height: 0;
  height: 100%;
  overflow: visible;
  background-color: var(--bg-surface);
}


.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}


.provider-detail-view {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.provider-detail-view :deep(.config-tabs) {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.provider-detail-view :deep(.config-tabs .ant-tabs-nav) {
  margin: 0 0 0 24px;
  padding-top: 16px;
  flex-shrink: 0;
}

.provider-detail-view :deep(.config-tabs .ant-tabs-nav::before) {
  border-bottom: 1px solid var(--border-default);
}

.provider-detail-view :deep(.config-tabs .ant-tabs-tab) {
  color: var(--text-secondary);
  font-size: 14px;
  padding: 8px 20px;
  transition: color 0.2s ease;
}

.provider-detail-view :deep(.config-tabs .ant-tabs-tab:hover) {
  color: var(--text-primary);
}

.provider-detail-view :deep(.config-tabs .ant-tabs-tab.ant-tabs-tab-active .ant-tabs-tab-btn) {
  color: var(--primary);
  font-weight: 600;
}

.provider-detail-view :deep(.config-tabs .ant-tabs-ink-bar) {
  background: var(--primary);
  height: 2px;
  border-radius: 2px;
}

.provider-detail-view :deep(.config-tabs .ant-tabs-content-holder) {
  flex: 1;
  min-height: 0;
  overflow: auto;
}

.provider-detail-view :deep(.config-tabs .ant-tabs-content) {
  height: 100%;
}

.provider-detail-view :deep(.config-tabs .ant-tabs-tabpane) {
  height: 100%;
}
</style>
