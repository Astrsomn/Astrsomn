<template>
  <div class="persona-grid">
    <!-- 左卡片：身份标识 -->
    <div class="glass-card identity-card">
      <div class="card-heading">
        <div class="heading-bar identity-bar"></div>
        <h2 class="heading-title">身份标识</h2>
      </div>
      <div class="card-fields">
        <div class="field-group avatar-field">
          <label class="field-label">头像</label>
          <AstIconPicker
              :model-value="agentAvatar"
              :name="agentName"
              :size="56"
              @update:model-value="emit('update:agentAvatar', $event)"
          />
        </div>
        <div class="field-group">
          <label class="field-label">Agent 名称</label>
          <a-input
              :value="agentName"
              class="soft-input"
              placeholder="例如：翻译助手"
              @update:value="emit('update:agentName', $event)"
          />
        </div>
        <div class="field-group">
          <label class="field-label">Agent Key</label>
          <AstKeyGenerator
              :model-value="agentKey"
              :prefix="AI_AGENT_KEY_PREFIX"
              class="soft-input"
              placeholder="留空则自动生成"
              @update:model-value="emit('update:agentKey', $event)"
          />
        </div>
      </div>
    </div>

    <!-- 右卡片：系统指令 -->
    <div class="glass-card prompt-card-wrapper">
      <div class="card-heading">
        <div class="heading-bar prompt-bar"></div>
        <h2 class="heading-title">系统指令 (Prompt)</h2>
        <a-button
            :loading="improveLoading"
            class="improve-trigger"
            size="small"
            @click="emit('improve-prompt')"
        >
          <ThunderboltOutlined/>
          AI 优化
        </a-button>
      </div>
      <div class="prompt-body">
        <PromptCard
            :improve-loading="improveLoading"
            :prompt="currentPrompt"
            :textarea-rows="6"
            @create="emit('open-prompt-form')"
            @history="emit('prompt-history')"
            @improve="emit('improve-prompt')"
            @select="emit('open-prompt-drawer')"
            @update:prompt-content="emit('update-prompt-content', $event)"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {ThunderboltOutlined} from '@ant-design/icons-vue'
import type {AiPrompt} from '@/api/aiPrompt.ts'
import PromptCard from '@/views/admin/ai-config/builder/component/left-center/PromptCard.vue'
import AstKeyGenerator from '@/components/home/AstKeyGenerator.vue'
import AstIconPicker from '@/components/home/AstIconPicker.vue'
import {AI_AGENT_KEY_PREFIX} from '@/constants/aiConfigKeyPrefixes.ts'

defineProps<{
  agentName: string
  agentKey: string
  agentAvatar: string
  currentPrompt: AiPrompt | undefined
  improveLoading: boolean
}>()

const emit = defineEmits<{
  'update:agentName': [value: string]
  'update:agentKey': [value: string]
  'update:agentAvatar': [value: string]
  'open-prompt-drawer': []
  'open-prompt-form': []
  'prompt-history': []
  'improve-prompt': []
  'update-prompt-content': [content: string]
}>()
</script>

<style scoped>
.persona-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
  min-height: 0;
}

/* Glass card base */
.glass-card {
  background: var(--ac-glass-bg);
  backdrop-filter: var(--ac-glass-backdrop);
  -webkit-backdrop-filter: var(--ac-glass-backdrop);
  border: 1px solid var(--ac-glass-border);
  border-radius: 12px;
  box-shadow: var(--ac-glass-shadow);
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.prompt-card-wrapper {
  flex: 1;
  min-height: 0;
}

/* Card heading with colored bar */
.card-heading {
  display: flex;
  align-items: center;
  gap: 10px;
}

.heading-bar {
  width: 4px;
  height: 16px;
  border-radius: 999px;
  flex-shrink: 0;
}

.identity-bar {
  background: var(--primary);
}

.prompt-bar {
  background: #a855f7;
}

.heading-title {
  font-size: 13px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  flex: 1;
}

/* Fields */
.card-fields {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: 11px;
  color: var(--text-muted);
  margin-left: 2px;
}

.soft-input {
  border-radius: 8px;
  transition: all 0.2s;
}

.soft-input:focus-within {
  border-color: var(--primary) !important;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.08) !important;
}

.avatar-field {
  align-items: flex-start;
}

/* Improve button in heading */
.improve-trigger {
  font-size: 11px;
  font-weight: 600;
  border-radius: 6px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.improve-trigger:hover {
  filter: brightness(1.1);
  color: #fff;
}

.improve-trigger:disabled {
  opacity: 0.6;
  filter: none;
}

/* Prompt body: suppress PromptCard's own card shell */
.prompt-body {
  flex: 1;
  min-height: 0;
  display: flex;
}

.prompt-body :deep(.prompt-card) {
  background: transparent;
  backdrop-filter: none;
  border: none;
  border-radius: 0;
  box-shadow: none;
  padding: 0;
  gap: 12px;
}

.prompt-body :deep(.prompt-card:hover) {
  border-color: transparent;
  box-shadow: none;
}

.prompt-body :deep(.card-header) {
  display: none;
}

.prompt-body :deep(.dashed-frame) {
  border: 1px solid var(--border-subtle);
  background: var(--bg-input);
  border-radius: 8px;
  min-height: 140px;
}

.prompt-body :deep(.improve-btn) {
  display: none;
}
</style>
