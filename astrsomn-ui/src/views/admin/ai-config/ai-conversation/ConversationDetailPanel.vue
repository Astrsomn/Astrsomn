<template>
  <div class="right-panel">
    <div v-if="selectedConversation" class="conversation-detail">
      <div class="detail-header">
        <h3>对话详情</h3>
        <div class="detail-info">
          <span class="info-item">Memory Key: {{ selectedConversation.memoryKey }}</span>
          <span class="info-item">创建时间: {{ formatTime(selectedConversation.createTime) }}</span>
          <span class="info-item">状态: 
            <span :class="`status-pill-${String(selectedConversation.status || '').toLowerCase()}`" class="status-pill">
              {{ getStatusLabel(selectedConversation.status) }}
            </span>
          </span>
        </div>
      </div>

      <div class="conversation-content">
        <div v-if="parsedConversation" class="chat-messages">
          <div
              v-for="(message, index) in parsedConversation"
              :key="index"
              :class="message.role"
              class="message-row"
          >
            <div class="avatar-box">
              <div v-if="message.role === 'ai'" aria-hidden="true" class="avatar ai-avatar">
                <svg height="22" viewBox="0 0 24 24" width="22">
                  <path d="M12 2L4.5 20.29L5.21 21L12 18L18.79 21L19.5 20.29L12 2Z" fill="currentColor"/>
                </svg>
              </div>
              <div v-else class="avatar user-avatar">
                <span>ME</span>
              </div>
            </div>

            <div class="message-body">
              <!-- AI 消息 -->
              <div v-if="message.role === 'ai'" class="ai-card">
                <div class="answer-section">
                  <div class="markdown-renderer">
                    {{ message.content }}
                  </div>
                </div>
              </div>

              <!-- 用户消息 -->
              <div v-else class="user-card">
                <div class="user-bubble">
                  <div class="user-html">{{ message.content }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="empty-content">
          <a-empty description="暂无对话内容"/>
        </div>
      </div>
    </div>
    <div v-else class="empty-detail">
      <a-empty description="请选择一个对话组查看详情"/>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed, defineProps} from 'vue'
import {AiConversation} from '@/api/aiConversation'

type ChatMessage = {
  role: 'user' | 'ai'
  content: string
  timestamp: string
}

const props = defineProps<{
  selectedConversation: AiConversation | null
  selectedConversationList: AiConversation[]
}>()

const normalizeText = (value?: string, fallback = '—') => {
  const text = String(value || '').trim()
  return text || fallback
}

const getStatusLabel = (status?: string) => {
  const statusMap: Record<string, string> = {
    enabled: '启用',
    disabled: '禁用'
  }
  return statusMap[String(status || '').toLowerCase()] || normalizeText(status)
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

const parsedConversation = computed<ChatMessage[] | null>(() => {
  if (!props.selectedConversationList || props.selectedConversationList.length === 0) return null

  return props.selectedConversationList.map(item => ({
    role: item.role === 'user' ? 'user' : 'ai',
    content: item.content || '',
    timestamp: item.updateTime || item.createTime || new Date().toISOString()
  }))
})
</script>

<style scoped>
.right-panel {
  flex: 1;
  min-width: 400px;
  border: 1px solid var(--border-default);
  border-radius: var(--radius-sm);
  background: var(--bg-card);
  display: flex;
  flex-direction: column;
}

.empty-detail {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.conversation-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.detail-header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-default);
  background: var(--bg-surface);
}

.detail-header h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.detail-info {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 13px;
  color: var(--text-secondary);
}

.info-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.conversation-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: var(--bg-card);
}

.chat-messages {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin: 20px 0;
  max-width: 100%;
}

.message-row.user {
  flex-direction: row-reverse;
}

.avatar-box {
  flex-shrink: 0;
  padding-top: 2px;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--chat-avatar-shadow);
}

.ai-avatar {
  background: var(--chat-ai-avatar-bg);
  border: 1px solid var(--chat-ai-avatar-border);
  color: var(--chat-ai-avatar-icon);
}

.user-avatar {
  background: var(--chat-user-avatar-bg);
  border: 1px solid var(--chat-user-avatar-border);
  color: var(--chat-user-avatar-text);
  font-size: 10px;
  font-weight: 800;
  letter-spacing: 0.02em;
}

.message-body {
  flex: 1;
  min-width: 0;
  max-width: calc(100% - 48px);
}

/* —— AI 统一卡片 —— */
.ai-card {
  border-radius: 16px;
  border: 1px solid var(--chat-ai-card-border);
  background: var(--chat-ai-card-bg);
  box-shadow: var(--chat-ai-card-shadow);
  overflow: hidden;
}

.answer-section {
  background: var(--chat-answer-bg);
}

.markdown-renderer {
  padding: 16px 18px 12px;
  font-size: 15px;
  line-height: 1.75;
  color: var(--chat-markdown-text);
  word-break: break-word;
}

/* 用户气泡 */
.user-card {
  display: flex;
  justify-content: flex-end;
}

.user-bubble {
  max-width: min(100%, 640px);
  padding: 12px 16px;
  border-radius: 16px 4px 16px 16px;
  background: var(--primary-gradient);
  color: #fff;
  box-shadow: 0 4px 14px rgba(0, 123, 255, 0.28);
}

.user-html {
  font-size: 15px;
  line-height: 1.65;
  word-break: break-word;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 28px;
  padding: 0 10px;
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

@media (max-width: 1200px) {
  .right-panel {
    flex: none;
    min-width: unset;
    width: 100%;
    min-height: 400px;
  }
}

@media (max-width: 720px) {
  .detail-info {
    flex-direction: column;
    gap: 8px;
  }

  .message-body {
    max-width: calc(100% - 40px);
  }

  .user-bubble {
    max-width: min(100%, 100%);
  }
}
</style>