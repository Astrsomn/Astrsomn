<template>
  <div class="persona-grid">
    <!-- 身份标识卡片 -->
    <div class="card identity-card">
      <div class="card-header">
        <div class="card-header-icon">
          <UserOutlined />
        </div>
        <div class="card-header-text">
          <h3 class="card-title">身份标识</h3>
          <p class="card-desc">基础配置信息</p>
        </div>
      </div>
      <div class="card-body">
        <div class="avatar-row">
          <div class="avatar-wrapper">
            <AstIconPicker
                :model-value="agentAvatar"
                :name="agentName"
                :size="48"
                @update:model-value="emit('update:agentAvatar', $event)"
            />
          </div>
          <div class="avatar-hint">
            <span class="hint-label">头像</span>
            <span class="hint-text">点击更换</span>
          </div>
        </div>
        <div class="field-group">
          <label class="field-label">
            <span class="label-text">名称</span>
            <span class="label-required">*</span>
          </label>
          <a-input
              :value="agentName"
              class="config-input"
              placeholder="例如：翻译助手"
              @update:value="emit('update:agentName', $event)"
          />
        </div>
        <div class="field-group">
          <label class="field-label">
            <span class="label-text">Key</span>
            <span class="label-optional">留空自动生成</span>
          </label>
          <AstKeyGenerator
              :model-value="agentKey"
              :prefix="AI_AGENT_KEY_PREFIX"
              class="config-input"
              placeholder="agent_key"
              @update:model-value="emit('update:agentKey', $event)"
          />
        </div>
      </div>
    </div>

    <!-- 系统指令卡片 -->
    <div class="card prompt-card">
      <div class="card-header">
        <div class="card-header-icon icon-purple">
          <CodeOutlined />
        </div>
        <div class="card-header-text">
          <h3 class="card-title">系统提示词</h3>
          <p class="card-desc">定义智能体的行为和能力</p>
        </div>
        <a-button
            :loading="improveLoading"
            class="ai-optimize-btn"
            size="small"
            @click="emit('improve-prompt')"
        >
          <template #icon><ThunderboltOutlined /></template>
          AI 优化
        </a-button>
      </div>
      <div class="prompt-body">
        <PromptCard
            :improve-loading="improveLoading"
            :prompt="currentPrompt"
            :textarea-rows="8"
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
import {CodeOutlined, ThunderboltOutlined, UserOutlined} from '@ant-design/icons-vue'
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


.card {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: 12px;
  overflow: hidden;
  transition: box-shadow 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.card:hover {
  box-shadow: var(--shadow-card);
}

.prompt-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}


.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-subtle);
}

.card-header-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(59, 130, 246, 0.08);
  color: #3b82f6;
  font-size: 16px;
  flex-shrink: 0;
}

.card-header-icon.icon-purple {
  background: rgba(139, 92, 246, 0.08);
  color: #8b5cf6;
}

.card-header-text {
  flex: 1;
  min-width: 0;
}

.card-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
  line-height: 1.4;
}

.card-desc {
  font-size: 12px;
  color: var(--text-muted);
  margin: 0;
  line-height: 1.4;
}


.card-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}


.avatar-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-wrapper {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  border: 2px dashed var(--border-default);
  padding: 4px;
  transition: border-color 0.2s;
  cursor: pointer;
}

.avatar-wrapper:hover {
  border-color: #3b82f6;
}

.avatar-hint {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.hint-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-primary);
}

.hint-text {
  font-size: 12px;
  color: var(--text-muted);
}


.field-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  display: flex;
  align-items: center;
  gap: 6px;
}

.label-text {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-primary);
}

.label-required {
  font-size: 12px;
  color: #ef4444;
}

.label-optional {
  font-size: 11px;
  color: var(--text-muted);
  margin-left: auto;
}


.config-input {
  height: 40px;
  border-radius: 10px;
  font-size: 13px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid var(--border-default);
  background: var(--bg-input);
}

.config-input:hover {
  border-color: var(--border-subtle);
  background: var(--bg-card);
}

.config-input:focus-within {
  border-color: #3b82f6;
  background: var(--bg-card);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}


.ai-optimize-btn {
  height: 32px;
  padding: 0 14px;
  font-size: 12px;
  font-weight: 500;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: #fff;
  border: none;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(99, 102, 241, 0.25);
}

.ai-optimize-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.35);
  filter: brightness(1.05);
  color: #fff;
}

.ai-optimize-btn:active {
  transform: translateY(0);
}

.ai-optimize-btn:disabled {
  opacity: 0.6;
  transform: none;
  box-shadow: none;
}


.prompt-body {
  flex: 1;
  min-height: 0;
  padding: 16px 20px 20px;
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
  width: 100%;
}

.prompt-body :deep(.prompt-card:hover) {
  border-color: transparent;
  box-shadow: none;
}

.prompt-body :deep(.card-header) {
  display: none;
}

.prompt-body :deep(.dashed-frame) {
  border: 1px solid var(--border-default);
  background: var(--bg-input);
  border-radius: 10px;
  min-height: 180px;
  transition: all 0.2s;
}

.prompt-body :deep(.dashed-frame:hover) {
  border-color: var(--border-subtle);
  background: var(--bg-surface);
}

.prompt-body :deep(.dashed-frame:focus-within) {
  border-color: #8b5cf6;
  background: var(--bg-card);
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1);
}

.prompt-body :deep(.improve-btn) {
  display: none;
}
</style>
