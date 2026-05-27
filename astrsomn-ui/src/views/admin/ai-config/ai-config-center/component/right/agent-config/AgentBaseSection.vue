<template>
  <div :style="cardStyle" class="persona-card">
    <div class="card-top">
      <div class="top-left">
        <div class="avatar-wrapper">
          <AstIconPicker
            :model-value="agentAvatar"
            :name="agentName"
            :size="44"
            @update:model-value="emit('update:agentAvatar', $event)"
          />
        </div>
        <div class="top-fields">
          <a-input
            :value="agentName"
            class="name-input"
            placeholder="例如：翻译助手"
            @update:value="emit('update:agentName', $event)"
          />
          <div class="key-generator-wrapper">
            <AstKeyGenerator
              :model-value="agentKey"
              :prefix="AI_AGENT_KEY_PREFIX"
              class="key-input"
              placeholder="agent_key"
              @update:model-value="emit('update:agentKey', $event)"
            />
          </div>
        </div>
      </div>
    </div>

    <div class="divider" />

    <div class="prompt-area">
      <div class="prompt-container-relative">
        <PromptCard
          :improve-loading="improveLoading"
          :prompt="currentPrompt"
          :textarea-rows="9"
          @create="emit('open-prompt-form')"
          @history="emit('prompt-history')"
          @improve="emit('improve-prompt')"
          @select="emit('open-prompt-drawer')"
          @update:prompt-content="emit('update-prompt-content', $event)"
        />

        <a-button
          :loading="improveLoading"
          :disabled="improveLoading"
          class="ai-optimize-btn-absolute"
          size="small"
          @click="emit('improve-prompt')"
        >
          <template #icon><ThunderboltOutlined /></template>
          AI 优化
        </a-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {ThunderboltOutlined} from '@ant-design/icons-vue'
import type {AiPrompt} from '@/api/aiPrompt.ts'
import PromptCard from '@/views/admin/ai-config/builder/component/left-center/PromptCard.vue'
import AstKeyGenerator from '@/components/home/AstKeyGenerator.vue'
import AstIconPicker from '@/components/home/AstIconPicker.vue'
import {AI_AGENT_KEY_PREFIX} from '@/constants/aiConfigKeyPrefixes.ts'

const APP_HEADER_H = 60
const CONFIG_HEADER_H = 56
const CONTENT_PADDING = 40

const cardStyle = computed(() => ({
  height: `calc(100vh - ${APP_HEADER_H + CONFIG_HEADER_H + CONTENT_PADDING}px)`,
}))

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
/* 全局卡片容器 */
.persona-card {
  background: var(--bg-card, #ffffff);
  border: 1px solid var(--border-default, #eef0f2);
  border-radius: 14px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 0;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.01);
  transition: all 0.25s ease;
}

.persona-card:hover {
  border-color: #e2e8f0;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.03);
}

/* ── 顶部样式精简 ── */
.card-top {
  display: flex;
  align-items: center;
  padding: 18px 20px 14px;
  flex-shrink: 0;
}

.top-left {
  display: flex;
  align-items: center;
  gap: 14px;
  flex: 1;
  min-width: 0;
}

.avatar-wrapper {
  flex-shrink: 0;
}

.top-fields {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
  min-width: 0;
}

/* 名字输入框清爽化 */
.name-input {
  height: 32px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  border: 1px solid transparent !important;
  background: transparent !important;
  padding: 0 4px !important;
  color: #1e293b;
  box-shadow: none !important;
}
.name-input:hover, .name-input:focus {
  background: #f1f5f9 !important;
  border-color: #cbd5e1 !important;
  padding: 0 8px !important;
}


/* ── 分隔线 ── */
.divider {
  height: 1px;
  margin: 0 20px;
  background: #f1f5f9;
  flex-shrink: 0;
}

/* ── 提示词区域（核心重构） ── */
.prompt-area {
  flex: 1;
  min-height: 0;
  padding: 16px 20px 20px;
  display: flex;
}

/* 创建一个相对定位包裹器，用于承载绝对定位的 AI 按钮 */
.prompt-container-relative {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
}

/* 深度穿透覆盖三方 Prompt 组件 */
.prompt-area :deep(.prompt-card) {
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
  padding: 0 !important;
  width: 100%;
}

.prompt-area :deep(.card-header),
.prompt-area :deep(.improve-btn) {
  display: none !important;
}

/* 大文本框的外框美化 */
.prompt-area :deep(.dashed-frame) {
  border: 1px solid #e2e8f0 !important;
  background: #f8fafc !important;
  border-radius: 12px !important;
  height: 100%;
  transition: all 0.25s ease !important;
}

.prompt-area :deep(.dashed-frame:hover) {
  border-color: #cbd5e1 !important;
  background: #f1f5f9 !important;
}

.prompt-area :deep(.dashed-frame:focus-within) {
  border-color: #8b5cf6 !important;
  background: #ffffff !important;
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1) !important;
}

/* 核心核心：为了不让输入的文字被右上角的 AI 按钮遮挡，给底层 textarea 加上顶部与右侧留白 */
.prompt-area :deep(textarea) {
  font-size: 13px !important;
  line-height: 1.6 !important;
  color: #334155 !important;
  padding-top: 42px !important; /* 留出上方空间给 AI 按钮悬浮 */
  padding-right: 12px !important;
}


/* ── 绝对定位：内嵌在输入框内部右上角的 AI 优化按钮 ── */
.ai-optimize-btn-absolute {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 10; /* 确保悬浮在 textarea 之上 */
  height: 28px;
  padding: 0 12px;
  font-size: 12px;
  font-weight: 500;
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  /* 现代感的科技渐变紫 */
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%) !important;
  color: #ffffff !important;
  border: none !important;
  transition: all 0.2s ease !important;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.25);
}

.ai-optimize-btn-absolute:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.35);
  opacity: 0.95;
}

.ai-optimize-btn-absolute:active:not(:disabled) {
  transform: translateY(0);
}

/* 按钮 Loading 和禁用状态：完美融入输入框背景，不显脏 */
.ai-optimize-btn-absolute:disabled,
.ai-optimize-btn-absolute.ant-btn-loading {
  background: #e2e8f0 !important;
  color: #94a3b8 !important;
  box-shadow: none !important;
  transform: none !important;
  border: none !important;
  opacity: 1 !important;
}
</style>