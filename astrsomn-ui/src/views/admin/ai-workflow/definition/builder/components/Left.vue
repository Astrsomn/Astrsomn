<template>
  <aside class="left-panel">
    <div class="left-head">
      <div class="head-title">
        <h3>流程导航</h3>
      </div>
      <LeftViewToggle v-model="currentMode" />
    </div>

    <div class="left-body">
      <WorkflowListPanel
        v-if="currentMode === 'workflow-list'"
        :items="workflowItems"
        :active-workflow-id="activeWorkflowId"
        @select="$emit('select-workflow', $event)"
      />
      <ModelChatPanel
        v-else
        :draft="chatDraft"
        :messages="chatMessages"
        @update:draft="chatDraft = $event"
        @send="onSendMessage"
      />
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import LeftViewToggle from './left-panel/LeftViewToggle.vue'
import WorkflowListPanel from './left-panel/WorkflowListPanel.vue'
import ModelChatPanel from './left-panel/ModelChatPanel.vue'
import type { LeftViewMode, WorkflowListItem } from '../types'

defineProps<{
  workflowItems: WorkflowListItem[]
  activeWorkflowId?: string
}>()

defineEmits<{
  'select-workflow': [item: WorkflowListItem]
}>()

const currentMode = ref<LeftViewMode>('workflow-list')
const chatDraft = ref('')
const chatMessages = ref<Array<{ id: string; role: 'user' | 'assistant'; content: string }>>([
  { id: 'welcome-1', role: 'assistant', content: '你好，我是模型助手。这里先作为模型对话区域占位。' }
])

const onSendMessage = () => {
  if (!chatDraft.value.trim()) return
  chatMessages.value.push({
    id: `u-${Date.now()}`,
    role: 'user',
    content: chatDraft.value.trim()
  })
  chatMessages.value.push({
    id: `a-${Date.now()}`,
    role: 'assistant',
    content: '已收到你的消息，后续可在这里接入真实模型会话接口。'
  })
  chatDraft.value = ''
}
</script>

<style scoped>
.left-panel {
  height: 100%;
  border: 1px solid #edf1f6;
  border-radius: 12px;
  background: #fff;
  display: flex;
  flex-direction: column;
}

.left-head {
  padding: 12px;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.head-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.left-head h3 {
  margin: 0;
  font-size: 14px;
  color: #1e293b;
}

.left-body {
  flex: 1;
  min-height: 0;
}
</style>
