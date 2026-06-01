<template>
  <a-card
      :body-style="{ padding: '12px' }"
      :class="{ 'selected': isSelected }"
      class="conversation-card"
      @click="handleCardClick"
  >
    <div class="card-layout">
      <a-checkbox
          :checked="isSelected"
          class="compact-checkbox"
          @change="handleCheckboxChange"
          @click.stop
      />

      <div class="card-main">
        <div class="main-header">
          <h3 class="title">{{ getCardTitle() }}</h3>
          <div class="actions">
            <a-tooltip :title="t.card.recover">
              <reload-outlined class="icon-btn" @click.stop="recoverConversation"/>
            </a-tooltip>
            <a-tooltip :title="t.card.delete">
              <delete-outlined class="icon-btn danger" @click.stop="deleteConversation"/>
            </a-tooltip>
          </div>
        </div>

        <div class="metadata">
          <span class="count">{{ t.card.conversationCount.replace('{n}', String(conversationCount)) }}</span>
          <span class="divider">·</span>
          <span class="time">{{ formatTime(latestTime) }}</span>
        </div>

        <div class="key-footer" @click.stop="copyMemoryKey">
          <div class="key-tag">
            <link-outlined class="key-icon"/>
            <span class="key-text">{{ conversation.memoryKey || 'No Key' }}</span>
            <copy-outlined class="copy-trigger"/>
          </div>
        </div>
      </div>
    </div>
  </a-card>
</template>

<script lang="ts" setup>
import {message, Modal} from 'ant-design-vue'
import {CopyOutlined, DeleteOutlined, LinkOutlined, ReloadOutlined} from '@ant-design/icons-vue'
import type {AiConversation} from '@/api/aiConversation.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-conversation')

const props = defineProps<{
  conversation: AiConversation
  conversationCount: number
  latestTime: string
  isSelected: boolean
}>()

const emit = defineEmits(['select', 'recover', 'delete'])

const formatTime = (time?: string) => {
  if (!time) return '—'
  const date = new Date(time)
  return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
}

const getCardTitle = () => {
  return props.conversation.content || t.value.card.noContent
}

const copyMemoryKey = async () => {
  if (!props.conversation.memoryKey) return
  try {
    await navigator.clipboard.writeText(props.conversation.memoryKey)
    message.success(t.value.card.keyCopied)
  } catch (err) {
    message.error(t.value.card.copyFailed)
  }
}

const recoverConversation = () => emit('recover', props.conversation.memoryKey)
const deleteConversation = () => {
  Modal.confirm({
    title: t.value.card.deleteConfirmTitle,
    content: t.value.card.deleteConfirmContent,
    okType: 'danger',
    onOk: () => emit('delete', props.conversation.memoryKey)
  })
}
const handleCardClick = () => emit('recover', props.conversation.memoryKey)
const handleCheckboxChange = () => emit('select', props.conversation.memoryKey)
</script>

<style scoped>
.conversation-card {
  margin-bottom: 8px;
  cursor: pointer;
  border-radius: 8px;
  border: 1px solid var(--border-default);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.conversation-card:hover {
  border-color: var(--border-strong);
  box-shadow: 0 2px 8px color-mix(in srgb, var(--text-tertiary) 4%, transparent);
}

.conversation-card.selected {
  border-color: var(--primary);
  background-color: color-mix(in srgb, var(--primary) 6%, var(--bg-card));
}

.card-layout {
  display: flex;
  gap: 12px;
}

.compact-checkbox {
  margin-top: 2px;
}

.card-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.main-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.title {
  margin: 0;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.actions {
  display: flex;
  gap: 12px;
  margin-left: 8px;
  opacity: 0;
  transition: opacity 0.2s;
}

.conversation-card:hover .actions {
  opacity: 1;
}

.icon-btn {
  font-size: 14px;
  color: var(--text-tertiary);
}

.icon-btn:hover {
  color: var(--primary);
}

.icon-btn.danger:hover {
  color: var(--error);
}

.metadata {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: var(--text-tertiary);
}

.divider {
  margin: 0 6px;
  color: var(--border-default);
}

.key-footer {
  margin-top: 4px;
}

.key-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 2px 8px;
  background: var(--bg-surface);
  border-radius: 4px;
  max-width: 100%;
  transition: all 0.2s;
}

.key-tag:hover {
  background: var(--bg-elevated);
  color: var(--primary);
}

.key-icon {
  font-size: 11px;
  color: var(--text-quaternary);
}

.key-text {
  font-family: 'SFMono-Regular', Consolas, monospace;
  font-size: 11px;
  color: var(--text-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.copy-trigger {
  font-size: 10px;
  color: var(--text-quaternary);
}
</style>
