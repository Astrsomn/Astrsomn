<template>
  <div class="prompt-card">
    <div class="card-header">
      <div class="header-left">
        <div class="icon-badge">
          <FileTextOutlined/>
        </div>
        <div class="header-info">
          <h3 class="card-title">{{ t.promptCard.title }}</h3>
          <span v-if="prompt?.promptKey" class="prompt-key-display">{{ prompt.promptKey }}</span>
        </div>
      </div>
      <div class="header-actions">
        <button class="action-btn primary" :title="t.promptCard.selectPrompt" @click.stop="emit('select')">
          <AppstoreOutlined/>
        </button>
        <button class="action-btn secondary" :title="t.promptCard.createPrompt" @click.stop="emit('create')">
          <PlusOutlined/>
        </button>
        <button :disabled="!prompt?.promptKey" class="action-btn history" :title="t.promptCard.historyVersion"
                @click.stop="emit('history')">
          <HistoryOutlined/>
        </button>
      </div>
    </div>

    <PromptEditor
        v-model="promptContent"
        :label="t.promptCard.contentLabel"
        :loading="improveLoading"
        :min-rows="6"
        :placeholder="t.promptCard.textareaPlaceholder"
        :improve-button-text="t.promptCard.beautify"
        :char-count-label="t.promptCard.charCount"
        @improve="emit('improve')"
    />
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {
  AppstoreOutlined,
  FileTextOutlined,
  HistoryOutlined,
  PlusOutlined,
} from '@ant-design/icons-vue'
import type {AiPrompt} from '@/api/aiPrompt'
import {usePageTranslation} from '@/locales/pages.ts'
import PromptEditor from '@/views/admin/ai-config/component/PromptEditor.vue'

const t = usePageTranslation('ai-builder')

interface Props {
  prompt?: AiPrompt
  improveLoading?: boolean
  textareaRows?: number
}

const props = withDefaults(defineProps<Props>(), {
  textareaRows: 6,
})

const emit = defineEmits<{
  (e: 'select'): void
  (e: 'create'): void
  (e: 'history'): void
  (e: 'improve'): void
  (e: 'update:promptContent', value: string): void
}>()

const promptContent = computed({
  get: () => props.prompt?.promptContent || '',
  set: (val) => emit('update:promptContent', val)
})
</script>

<style scoped>
.prompt-card {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
  gap: 12px;
  background: var(--ab-glass-bg);
  backdrop-filter: blur(var(--ab-glass-haze, 10px));
  -webkit-backdrop-filter: blur(var(--ab-glass-haze, 10px));
  border: 1px solid var(--ab-glass-border);
  border-radius: var(--ab-glass-radius, 16px);
  box-shadow: var(--ab-glass-shadow);
  padding: 20px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.prompt-card:hover {
  border-color: var(--ab-hover-line);
  box-shadow: var(--ab-hover-shadow, 0 0 15px color-mix(in srgb, var(--primary) 15%, transparent));
}

/* ── card header ── */
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.icon-badge {
  width: 32px;
  height: 32px;
  flex-shrink: 0;
  background: #f3e8ff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9333ea;
  box-shadow: none;
}

.icon-badge .anticon {
  font-size: 16px;
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.card-title {
  font-weight: 700;
  font-size: 14px;
  color: var(--text-primary);
  margin: 0;
}

.prompt-key-display {
  font-size: 11px;
  color: var(--text-secondary);
  font-family: 'JetBrains Mono', monospace;
}

/* ── header actions ── */
.header-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 1px 2px color-mix(in srgb, var(--text-primary) 4%, transparent);
  border: none;
}

.action-btn .anticon {
  font-size: 12px;
}

.action-btn.primary {
  background: #2563eb;
  color: #fff;
}

.action-btn.primary:hover {
  background: #1d4ed8;
}

.action-btn.secondary {
  background: #eff6ff;
  color: #2563eb;
  border: 1px solid #bfdbfe;
}

.action-btn.secondary:hover {
  background: #dbeafe;
}

.action-btn.history {
  background: #fefce8;
  color: #ca8a04;
  border: 1px solid #fde68a;
}

.action-btn.history:hover {
  background: #fef9c3;
}

.action-btn.history:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
</style>
