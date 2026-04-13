<template>
  <a-card :body-style="{ padding: '16px' }" class="conversation-card" :class="{ 'selected': isSelected }" @click="handleCardClick">
    <div class="card-header">
      <div class="card-title-container">
        <a-checkbox :checked="isSelected" @change="handleCheckboxChange" class="selection-checkbox" />
        <h3 class="card-title">{{ getCardTitle() }}</h3>
      </div>
      <div class="card-actions">
        <a-tooltip title="复制 Memory Key">
          <a-button
            type="text"
            size="small"
            :disabled="!conversation.memoryKey"
            @click.stop="copyMemoryKey"
          >
            <template #icon><copy-outlined /></template>
          </a-button>
        </a-tooltip>
        <a-tooltip title="复原对话">
          <a-button
            type="text"
            size="small"
            @click.stop="recoverConversation"
          >
            <template #icon><reload-outlined /></template>
          </a-button>
        </a-tooltip>
        <a-tooltip title="删除">
          <a-button
            type="text"
            size="small"
            danger
            @click.stop="deleteConversation"
          >
            <template #icon><delete-outlined /></template>
          </a-button>
        </a-tooltip>
      </div>
    </div>
    
    <div class="card-content">
      <div class="info-row">
        <span class="label">Memory Key:</span>
        <span class="value mono">{{ conversation.memoryKey || '—' }}</span>
      </div>
      <div class="info-row">
        <span class="label">对话数量:</span>
        <span class="value">{{ conversationCount }}</span>
      </div>
      <div class="info-row">
        <span class="label">最新时间:</span>
        <span class="value time">{{ formatTime(latestTime) }}</span>
      </div>
      <div class="info-row">
        <span class="label">状态:</span>
        <span class="status-pill" :class="`status-pill-${String(conversation.status || '').toLowerCase()}`">
          {{ getStatusLabel(conversation.status) }}
        </span>
      </div>
    </div>
  </a-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { CopyOutlined, DeleteOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import type { AiConversation } from '@/api/aiConversation'

interface Props {
  conversation: AiConversation
  conversationCount: number
  latestTime: string
  isSelected: boolean
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'select', memoryKey: string): void
  (e: 'recover', memoryKey: string): void
  (e: 'delete', memoryKey: string): void
}>()

const getStatusLabel = (status?: string) => {
  const statusMap: Record<string, string> = {
    enabled: '启用',
    disabled: '禁用'
  }
  return statusMap[String(status || '').toLowerCase()] || String(status || '—')
}

const formatTime = (time?: string) => {
  if (!time) return '—'
  try {
    const date = new Date(time)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch {
    return time
  }
}

const getCardTitle = () => {
  if (!props.conversation.conversationContent) return '对话组'
  
  try {
    const content = props.conversation.conversationContent
    const parsed = JSON.parse(content)
    if (Array.isArray(parsed) && parsed.length > 0) {
      const firstMessage = parsed[0]
      if (firstMessage.content) {
        const content = String(firstMessage.content).trim()
        return content.length > 20 ? content.substring(0, 20) + '...' : content
      }
    }
  } catch (error) {
    console.error('解析对话内容失败:', error)
  }
  
  return '对话组'
}

const copyMemoryKey = async () => {
  const text = String(props.conversation.memoryKey || '').trim()
  if (!text) {
    message.warning('当前没有可复制的 Memory Key')
    return
  }

  try {
    await navigator.clipboard.writeText(text)
    message.success('Memory Key 已复制')
  } catch (error) {
    message.error('复制失败，请手动复制')
  }
}

const recoverConversation = () => {
  emit('recover', props.conversation.memoryKey || '')
}

const deleteConversation = () => {
  Modal.confirm({
    title: '确定删除此对话组吗？',
    onOk: () => {
      emit('delete', props.conversation.memoryKey || '')
    }
  })
}

const handleCardClick = () => {
  emit('recover', props.conversation.memoryKey || '')
}

const handleCheckboxChange = (e: any) => {
  emit('select', props.conversation.memoryKey || '')
}
</script>

<style scoped>
.conversation-card {
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.conversation-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.conversation-card.selected {
  border: 2px solid var(--primary);
  box-shadow: 0 4px 12px rgba(22, 118, 253, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.card-title-container {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.selection-checkbox {
  flex-shrink: 0;
  margin: 0;
}

.card-title {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 12px;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.label {
  width: 80px;
  color: var(--text-secondary);
  flex-shrink: 0;
}

.value {
  color: var(--text-primary);
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.mono {
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 12px;
}

.time {
  color: var(--text-secondary);
  font-size: 12px;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 24px;
  padding: 0 8px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.status-pill-enabled {
  color: #166534;
  background: #f0fdf4;
}

.status-pill-disabled {
  color: #9a3412;
  background: #fff7ed;
}
</style>