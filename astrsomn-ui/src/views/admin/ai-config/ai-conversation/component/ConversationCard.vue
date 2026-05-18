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
            <a-tooltip title="复原">
              <reload-outlined class="icon-btn" @click.stop="recoverConversation"/>
            </a-tooltip>
            <a-tooltip title="删除">
              <delete-outlined class="icon-btn danger" @click.stop="deleteConversation"/>
            </a-tooltip>
          </div>
        </div>

        <div class="metadata">
          <span class="count">{{ conversationCount }} 条对话</span>
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
  return props.conversation.content || '无内容对话'
}

const copyMemoryKey = async () => {
  if (!props.conversation.memoryKey) return
  try {
    await navigator.clipboard.writeText(props.conversation.memoryKey)
    message.success('Memory Key 已复制')
  } catch (err) {
    message.error('复制失败')
  }
}

const recoverConversation = () => emit('recover', props.conversation.memoryKey)
const deleteConversation = () => {
  Modal.confirm({
    title: '删除此对话？',
    content: '删除后无法恢复',
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
  border: 1px solid #f0f0f0;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.conversation-card:hover {
  border-color: #d9d9d9;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.conversation-card.selected {
  border-color: #1890ff;
  background-color: #f0f7ff;
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
  color: #262626;
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
  color: #8c8c8c;
}

.icon-btn:hover {
  color: #1890ff;
}

.icon-btn.danger:hover {
  color: #ff4d4f;
}

.metadata {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #8c8c8c;
}

.divider {
  margin: 0 6px;
  color: #d9d9d9;
}

.key-footer {
  margin-top: 4px;
}

.key-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 2px 8px;
  background: #f5f5f5;
  border-radius: 4px;
  max-width: 100%;
  transition: all 0.2s;
}

.key-tag:hover {
  background: #e8e8e8;
  color: #1890ff;
}

.key-icon {
  font-size: 11px;
  color: #bfbfbf;
}

.key-text {
  font-family: 'SFMono-Regular', Consolas, monospace;
  font-size: 11px;
  color: #595959;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.copy-trigger {
  font-size: 10px;
  color: #bfbfbf;
}
</style>