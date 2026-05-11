<template>
  <AgentConfigSectionShell :step="1" title="人设">
    <div class="layer1-row">
      <div class="name-desc-panel">
        <label class="card-label">智能体名称</label>
        <div class="persona-field-frame persona-field-frame--single">
          <a-input
            :value="agentName"
            class="field-input persona-prompt-like"
            placeholder="智能体名称"
            :bordered="false"
            @update:value="onName"
          />
        </div>
        <label class="card-label field-gap">智能体描述</label>
        <div class="persona-field-frame persona-field-frame--textarea">
          <a-textarea
            :value="agentDescription"
            class="field-input persona-prompt-like"
            placeholder="简要说明该智能体的用途与能力边界"
            :auto-size="{ minRows: 7, maxRows: 22 }"
            allow-clear
            :bordered="false"
            @update:value="onDescription"
          />
        </div>
      </div>
      <div class="prompt-card-wrap">
        <PromptCard
          :prompt="currentPrompt"
          :improve-loading="improveLoading"
          :textarea-rows="7"
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
  agentDescription: string
  currentPrompt: AiPrompt | undefined
  improveLoading: boolean
}>()

const emit = defineEmits<{
  'update:agentName': [value: string]
  'update:agentDescription': [value: string]
  'open-prompt-drawer': []
  'open-prompt-form': []
  'prompt-history': []
  'improve-prompt': []
  'update-prompt-content': [content: string]
}>()

function onName(v: string) {
  emit('update:agentName', v)
}

function onDescription(v: string) {
  emit('update:agentDescription', v)
}
</script>

<style scoped>
.layer1-row {
  display: flex;
  flex-direction: row;
  align-items: stretch;
  gap: 16px;
  min-height: 0;
}

.name-desc-panel {
  flex: 1 1 40%;
  min-width: 320px;
  max-width: 560px;
  display: flex;
  flex-direction: column;
  min-height: 0;
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  padding: 14px 16px;
  box-shadow: none;
}

.field-gap {
  margin-top: 14px;
}

.field-input {
  width: 100%;
}

.persona-field-frame {
  border: 1px dashed var(--border-subtle);
  border-radius: 12px;
  padding: 14px 16px;
  background: transparent;
}

.persona-field-frame--single {
  flex-shrink: 0;
}

.persona-field-frame--textarea {
  flex: 1;
  min-height: 160px;
  display: flex;
  flex-direction: column;
  margin-top: 0;
}

.persona-field-frame :deep(.ant-input-affix-wrapper),
.persona-field-frame :deep(input.ant-input),
.persona-field-frame :deep(textarea.ant-input) {
  border: none !important;
  box-shadow: none !important;
  background: transparent !important;
}

.persona-field-frame :deep(.ant-input-affix-wrapper) {
  padding-inline-start: 0;
  padding-inline-end: 22px;
}

.persona-field-frame--single :deep(.ant-input-affix-wrapper) {
  padding-inline-end: 0;
}

.persona-field-frame :deep(.ant-input),
.persona-field-frame :deep(textarea.ant-input) {
  font-size: 12px;
  font-weight: 400;
  color: #64748b;
  line-height: 1.625;
  font-family: inherit;
}

.persona-field-frame :deep(.ant-input::placeholder),
.persona-field-frame :deep(textarea.ant-input::placeholder) {
  color: #94a3b8;
}

.persona-field-frame :deep(textarea.ant-input) {
  resize: none;
  padding: 0;
}

.persona-field-frame--textarea :deep(textarea.ant-input) {
  overflow-y: auto;
}

.persona-field-frame--textarea :deep(textarea.ant-input)::-webkit-scrollbar {
  width: 6px;
}

.persona-field-frame--textarea :deep(textarea.ant-input)::-webkit-scrollbar-track {
  background: transparent;
}

.persona-field-frame--textarea :deep(textarea.ant-input)::-webkit-scrollbar-thumb {
  background: var(--border-default);
  border-radius: 10px;
}

.persona-field-frame--textarea :deep(textarea.ant-input)::-webkit-scrollbar-thumb:hover {
  background: var(--border-strong, var(--border-default));
}

.prompt-card-wrap {
  flex: 1;
  min-width: 0;
  min-height: 0;
}

.prompt-card-wrap :deep(.prompt-card) {
  min-height: 200px;
  border-color: var(--border-subtle);
  box-shadow: none;
}

.prompt-card-wrap :deep(.prompt-card:hover) {
  border-color: var(--border-subtle);
  box-shadow: none;
}

.prompt-card-wrap :deep(.dashed-frame) {
  min-height: 140px;
  border: 1px dashed var(--border-subtle);
  background: transparent;
}

.card-label {
  font-size: 10px;
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  margin-bottom: 8px;
  display: block;
}
</style>
