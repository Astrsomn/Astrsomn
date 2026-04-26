<template>
  <aside class="left-panel">
    <div class="left-head">
      <h3 class="head-title">{{ currentMode === 'workflow-list' ? '流程列表' : '模型对话' }}</h3>
      <div class="mode-switcher" role="tablist" aria-label="左侧视图切换">
        <button
          type="button"
          class="mode-btn"
          :class="{ active: currentMode === 'workflow-list' }"
          title="流程列表"
          @click="currentMode = 'workflow-list'"
        >
          <UnorderedListOutlined />
        </button>
        <button
          type="button"
          class="mode-btn"
          :class="{ active: currentMode === 'model-chat' }"
          title="模型对话"
          @click="currentMode = 'model-chat'"
        >
          <MessageOutlined />
        </button>
      </div>
    </div>

    <div class="left-body">
      <WorkflowListPanel
        v-if="currentMode === 'workflow-list'"
        :items="workflowItems"
        :active-workflow-id="activeWorkflowId"
        @select="$emit('select-workflow', $event)"
        @create="$emit('create-workflow', $event)"
        @edit="$emit('edit-workflow', $event.item, $event.payload)"
        @delete="$emit('delete-workflow', $event)"
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
import { MessageOutlined, UnorderedListOutlined } from '@ant-design/icons-vue'
import WorkflowListPanel from '@/views/admin/ai-workflow/definition/builder/components/left/component/WorkflowListPanel.vue'
import ModelChatPanel from '@/views/admin/ai-workflow/definition/builder/components/left/component/ModelChatPanel.vue'
import type { LeftViewMode, WorkflowListItem } from '../../domain/types'

defineProps<{
  workflowItems: WorkflowListItem[]
  activeWorkflowId?: string
}>()

defineEmits<{
  'select-workflow': [item: WorkflowListItem]
  'create-workflow': [payload: { workflowName: string; workflowKey: string; category: string }]
  'edit-workflow': [item: WorkflowListItem, payload: { workflowName: string; workflowKey: string; category: string }]
  'delete-workflow': [item: WorkflowListItem]
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
  padding: 8px 10px;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.head-title {
  margin: 0;
  font-size: 14px;
  line-height: 20px;
  color: #334155;
  font-weight: 600;
}

.mode-switcher {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #f8fafc;
  border: 1px solid #eaf0f7;
  border-radius: 8px;
  padding: 3px;
}

.mode-btn {
  width: 28px;
  height: 28px;
  border: 0;
  border-radius: 6px;
  background: transparent;
  color: #8ca0b3;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.mode-btn:hover {
  color: #5f7388;
  background: #eef3f8;
}

.mode-btn.active {
  color: #4b84cf;
  background: #eaf3ff;
}

.left-body {
  flex: 1;
  min-height: 0;
}
</style>
