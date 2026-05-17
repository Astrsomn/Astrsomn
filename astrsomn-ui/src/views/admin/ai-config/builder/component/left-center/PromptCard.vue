<template>
  <div class="prompt-card">
    <div class="card-header">
      <div class="header-left">
        <div class="icon-badge">
          <FileTextOutlined/>
        </div>
        <div class="header-info">
          <h3 class="card-title">人设与指令 (Prompt)</h3>
          <span v-if="prompt?.promptKey" class="prompt-key-display">{{ prompt.promptKey }}</span>
        </div>
      </div>
      <div class="header-actions">
        <button class="action-btn primary" title="选择提示词" @click.stop="emit('select')">
          <AppstoreOutlined/>
        </button>
        <button class="action-btn secondary" title="新建提示词" @click.stop="emit('create')">
          <PlusOutlined/>
        </button>
        <button :disabled="!prompt?.promptKey" class="action-btn history" title="历史版本"
                @click.stop="emit('history')">
          <HistoryOutlined/>
        </button>
      </div>
    </div>
    <div class="dashed-frame">
      <textarea
          v-model="promptContent"
          :rows="textareaRows"
          class="prompt-textarea custom-scrollbar"
          placeholder="给你的智能体配置一个清晰的角色定位和任务指令..."
      ></textarea>
      <button :disabled="improveLoading" class="improve-btn" title="美化提示词" @click.stop="emit('improve')">
        <ThunderboltOutlined :spin="improveLoading"/>
        <span class="improve-btn-text">美化</span>
      </button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {
  AppstoreOutlined,
  FileTextOutlined,
  HistoryOutlined,
  PlusOutlined,
  ThunderboltOutlined
} from '@ant-design/icons-vue'
import type {AiPrompt} from '@/api/aiPrompt'

interface Props {
  prompt?: AiPrompt
  improveLoading?: boolean
  /** 提示词正文 textarea 行数，默认 6 */
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
  gap: 16px;
  background: var(--ab-glass-bg, rgba(255, 255, 255, 0.8));
  backdrop-filter: blur(var(--ab-glass-haze, 10px));
  -webkit-backdrop-filter: blur(var(--ab-glass-haze, 10px));
  border: 1px solid var(--ab-glass-border, rgba(255, 255, 255, 0.6));
  border-radius: var(--ab-glass-radius, 16px);
  box-shadow: var(--ab-glass-shadow, 0 4px 20px rgba(0, 0, 0, 0.03));
  padding: 20px;
  transition: border-color 0.2s,
  box-shadow 0.2s;
}

.prompt-card:hover {
  border-color: var(--ab-hover-line, #3b82f6);
  box-shadow: var(--ab-hover-shadow, 0 0 15px rgba(59, 130, 246, 0.15));
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
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
  color: #334155;
  margin: 0;
}

.prompt-key-display {
  font-size: 11px;
  color: var(--text-secondary);
  font-family: 'JetBrains Mono', monospace;
}

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
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
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

.dashed-frame {
  border: 1px dashed #e2e8f0;
  border-radius: 12px;
  background: rgba(248, 250, 252, 0.3);
  padding: 16px;
  min-height: 160px;
  display: flex;
  flex: 1;
  min-height: 0;
  position: relative;
}

.improve-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.4);
  transition: all 0.2s ease;
  z-index: 2;
}

.improve-btn:hover {
  filter: brightness(1.1);
  transform: translateY(-1px);
}

.improve-btn:active {
  transform: translateY(0);
}

.improve-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  filter: none;
  transform: none;
}

.improve-btn-text {
  font-weight: 600;
  line-height: 1;
}

.prompt-textarea {
  width: 100%;
  min-height: 100%;
  background: transparent;
  border: none;
  border-radius: 0;
  padding: 0;
  font-size: 12px;
  font-weight: 400;
  color: #64748b;
  outline: none;
  transition: all 0.2s;
  resize: none;
  font-family: inherit;
  line-height: 1.625;
}

.prompt-textarea::placeholder {
  color: #94a3b8;
}

.prompt-textarea:focus {
  outline: none;
}

.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: var(--border-default);
  border-radius: 10px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: var(--border-strong);
}
</style>
