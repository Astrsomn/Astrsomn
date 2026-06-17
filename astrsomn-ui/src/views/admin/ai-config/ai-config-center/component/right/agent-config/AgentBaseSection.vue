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
            :placeholder="t.agent.namePlaceholder"
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
      <PromptEditor
          v-model="promptContent"
          :loading="improveLoading"
          :min-rows="9"
          :improve-button-text="t.agent.aiOptimize"
          @improve="emit('improve-prompt')"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import type {AiPrompt} from '@/api/aiPrompt.ts'
import PromptEditor from '@/views/admin/ai-config/component/PromptEditor.vue'
import AstKeyGenerator from '@/components/home/AstKeyGenerator.vue'
import AstIconPicker from '@/components/home/AstIconPicker.vue'
import {AI_AGENT_KEY_PREFIX} from '@/constants/aiConfigKeyPrefixes.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const APP_HEADER_H = 60
const CONFIG_HEADER_H = 56
const CONTENT_PADDING = 40

const cardStyle = computed(() => ({
  height: `calc(100vh - ${APP_HEADER_H + CONFIG_HEADER_H + CONTENT_PADDING}px)`,
}))

const t = usePageTranslation('ai-config-center')

const props = defineProps<{
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

const promptContent = computed({
  get: () => props.currentPrompt?.promptContent || '',
  set: (val) => emit('update-prompt-content', val),
})
</script>

<style scoped>
/* 全局卡片容器 */
.persona-card {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: 14px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 0;
  box-shadow: 0 4px 20px color-mix(in srgb, var(--text-primary) 1%, transparent);
  transition: all 0.25s ease;
}

.persona-card:hover {
  border-color: var(--border-default);
  box-shadow: 0 6px 24px color-mix(in srgb, var(--text-primary) 3%, transparent);
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
  color: var(--text-primary);
  box-shadow: none !important;
}
.name-input:hover, .name-input:focus {
  background: var(--bg-input) !important;
  border-color: var(--border-default) !important;
  padding: 0 8px !important;
}


/* ── 分隔线 ── */
.divider {
  height: 1px;
  margin: 0 20px;
  background: var(--border-subtle);
  flex-shrink: 0;
}

/* ── 提示词区域 ── */
.prompt-area {
  flex: 1;
  min-height: 0;
  padding: 16px 20px 20px;
  display: flex;
}
</style>