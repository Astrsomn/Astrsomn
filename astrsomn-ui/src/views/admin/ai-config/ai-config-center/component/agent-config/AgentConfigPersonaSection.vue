<template>
  <AgentConfigSectionShell :step="1" title="角色设定">
    <div class="persona-row">
      <div class="left-panel">
        <label class="card-label">名称 <span class="required">*</span></label>
        <a-input
          :value="agentName"
          class="field-input"
          placeholder="例如：翻译助手"
          :bordered="false"
          @update:value="emit('update:agentName', $event)"
        />
        <label class="card-label">Agent Key</label>
        <a-input
          :value="agentKey"
          class="field-input"
          placeholder="留空则自动生成"
          :bordered="false"
          @update:value="emit('update:agentKey', $event)"
        />
      </div>
      <div class="right-panel">
        <label class="card-label">人设指令 (Prompt) <span class="required">*</span></label>
        <PromptCard
          :prompt="currentPrompt"
          :improve-loading="improveLoading"
          :textarea-rows="6"
          @select="emit('open-prompt-drawer')"
          @create="emit('open-prompt-form')"
          @history="emit('prompt-history')"
          @improve="emit('improve-prompt')"
          @update:prompt-content="emit('update-prompt-content', $event)"
        />
      </div>
    </div>
  </AgentConfigSectionShell>
</template>

<script setup lang="ts">
import type { AiPrompt } from '@/api/aiPrompt'
import PromptCard from '@/views/admin/ai-config/builder/component/left-center/PromptCard.vue'
import AgentConfigSectionShell from './AgentConfigSectionShell.vue'

defineProps<{
  agentName: string
  agentKey: string
  currentPrompt: AiPrompt | undefined
  improveLoading: boolean
}>()

const emit = defineEmits<{
  'update:agentName': [value: string]
  'update:agentKey': [value: string]
  'open-prompt-drawer': []
  'open-prompt-form': []
  'prompt-history': []
  'improve-prompt': []
  'update-prompt-content': [content: string]
}>()
</script>

<style scoped>
.persona-row {
  display: flex;
  gap: 16px;
  align-items: stretch;
}

.left-panel {
  width: 320px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 14px;
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  padding: 16px 18px;
}

.right-panel {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.right-panel :deep(.prompt-card) {
  min-height: 160px;
  border-color: var(--border-subtle);
  box-shadow: none;
  background: transparent;
}

.right-panel :deep(.prompt-card:hover) {
  border-color: var(--border-subtle);
  box-shadow: none;
}

.right-panel :deep(.dashed-frame) {
  min-height: 110px;
  border: 1px solid var(--border-subtle);
  background: transparent;
}

.card-label {
  font-size: 10px;
  font-weight: 700;
  color: var(--text-muted);
  text-transform: uppercase;
  margin-bottom: 6px;
  display: block;
  letter-spacing: 0.04em;
}

.required {
  color: #ef4444;
}

.field-input {
  width: 100%;
  background: var(--bg-input);
  border: 1px solid var(--border-default) !important;
  border-radius: 10px;
  padding: 9px 12px;
  font-size: 13px;
  color: var(--text-primary);
  transition: border-color 0.2s;
}

.field-input:hover {
  border-color: var(--primary) !important;
}

.field-input:focus,
.field-input:focus-within {
  border-color: var(--primary) !important;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1) !important;
}

.field-input :deep(.ant-input) {
  border: none !important;
  box-shadow: none !important;
  background: transparent !important;
  font-size: 13px;
}

@media (max-width: 768px) {
  .persona-row {
    flex-direction: column;
  }
  .left-panel {
    width: 100%;
  }
}
</style>
