<template>
  <div class="prompt-card">
    <div class="card-header">
      <div class="header-left">
        <div class="icon-badge">
          <FileTextOutlined />
        </div>
        <div class="header-info">
          <h3 class="card-title">人设与指令 (Prompt)</h3>
          <span v-if="prompt?.promptKey" class="prompt-key-display">{{ prompt.promptKey }}</span>
        </div>
      </div>
      <div class="header-actions">
        <button class="action-btn primary" title="选择提示词" @click.stop="emit('select')">
          <AppstoreOutlined />
        </button>
        <button class="action-btn" title="新建提示词" @click.stop="emit('create')">
          <PlusOutlined />
        </button>
      </div>
    </div>
    <textarea
      class="prompt-textarea custom-scrollbar"
      v-model="promptContent"
      rows="6"
      placeholder="给你的 Agent 一个酷炫的人设..."
    ></textarea>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { FileTextOutlined, AppstoreOutlined, PlusOutlined } from '@ant-design/icons-vue'
import type { AiPrompt } from '@/api/aiPrompt'

interface Props {
  prompt?: AiPrompt
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'select'): void
  (e: 'create'): void
  (e: 'update:promptContent', value: string): void
}>()

const promptContent = computed({
  get: () => props.prompt?.promptContent || '',
  set: (val) => emit('update:promptContent', val)
})
</script>

<style scoped>
.prompt-card {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-xl);
  padding: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.prompt-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-card);
  border-color: var(--primary);
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
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #6366f1 0%, #a855f7 100%);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.25);
}

.icon-badge .anticon {
  font-size: 18px;
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.card-title {
  font-weight: bold;
  font-size: 14px;
  color: var(--text-primary);
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
  width: 36px;
  height: 36px;
  background: var(--bg-elevated);
  color: var(--text-secondary);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--border-default);
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.action-btn.primary {
  background: var(--primary);
  color: white;
  border-color: var(--primary);
}

.action-btn.primary:hover {
  background: var(--primary-light);
  border-color: var(--primary-light);
}

.action-btn .anticon {
  font-size: 16px;
}

.prompt-textarea {
  width: 100%;
  min-height: 160px;
  background: #ffffff;
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  padding: 16px;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-primary);
  outline: none;
  transition: all 0.3s;
  resize: vertical;
  font-family: inherit;
  line-height: 1.6;
}

.prompt-textarea::placeholder {
  color: var(--text-tertiary);
}

.prompt-textarea:focus {
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
  background: #ffffff;
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
