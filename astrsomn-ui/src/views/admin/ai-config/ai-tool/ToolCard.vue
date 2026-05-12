<template>
  <div class="tool-card-400" :class="{ 'is-disabled': record.enableFlag !== 'enabled' }">
    <div class="card-header-status">
      <div class="status-chip" :class="{ active: record.enableFlag === 'enabled' }">
        <span class="status-dot"></span>
        <span class="status-text">{{ record.enableFlag === 'enabled' ? 'Active' : 'Paused' }}</span>
      </div>
    </div>

    <div class="card-content">
      <div class="avatar-section">
        <div class="avatar-glow">
          <ToolOutlined />
        </div>
        <div class="type-tag">{{ record.type || '--' }}</div>
      </div>

      <h3 class="title" :title="record.toolName">
        {{ record.toolName || record.toolKey || '工具' }}
      </h3>

      <div class="key-capsule-btn" @click="copyKey(record.toolKey)">
        <span class="label">KEY</span>
        <code class="code">{{ record.toolKey || '--' }}</code>
        <copy-outlined class="icon" />
      </div>

      <div class="description-box">
        <p class="description-text">
          {{ preview(record.description) }}
        </p>
      </div>

      <div class="details-section">
        <div v-if="record.beanName" class="detail-item">
          <span class="detail-label">Bean</span>
          <span class="detail-value mono">{{ record.beanName }}</span>
        </div>
        <div v-if="record.methodName" class="detail-item">
          <span class="detail-label">方法</span>
          <span class="detail-value mono">{{ record.methodName }}</span>
        </div>
      </div>
    </div>

    <div class="card-footer-action">
      <div class="action-group">
        <button class="action-circle-btn" @click="emit('edit', record)">
          <edit-outlined />
        </button>
        <a-popconfirm title="确定删除吗？" @confirm="emit('delete', record.id)">
          <button class="action-circle-btn delete">
            <delete-outlined />
          </button>
        </a-popconfirm>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ToolOutlined, EditOutlined, DeleteOutlined, CopyOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import type { AiTool } from '@/api/aiTool.ts'

defineProps<{
  record: AiTool
}>()

const emit = defineEmits(['edit', 'delete'])

const preview = (raw?: string) => {
  if (!raw) return '暂无描述内容配置...'
  const t = raw.replace(/\s+/g, ' ').trim()
  return t.length > 120 ? `${t.slice(0, 120)}...` : t
}

const copyKey = async (key?: string) => {
  if (!key) return
  await navigator.clipboard.writeText(key)
  message.success('Key已复制')
}
</script>

<style scoped>
.tool-card-400 {
  --primary-color: var(--primary);
  --text-main: var(--text-primary);
  --text-muted: var(--text-muted);
  --card-bg: var(--bg-card);
  --card-border-subtle: var(--border-subtle);
  --card-border-default: var(--border-default);

  width: 100%;
  max-width: 320px;
  min-height: 400px;
  background: var(--card-bg);
  border-radius: 32px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  border: 1px solid var(--card-border-default);
}

.tool-card-400:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-card);
  border-color: var(--primary);
}

.tool-card-400.is-disabled {
  opacity: 0.6;
}

.card-header-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.status-chip {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  background: var(--bg-elevated);
  border-radius: 100px;
}

.status-chip.active {
  background: rgba(34, 197, 94, 0.15);
}

.status-chip.active .status-dot {
  background: var(--success);
}

.status-chip.active .status-text {
  color: var(--success);
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--text-muted);
}

.status-text {
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  color: var(--text-muted);
}

.card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.avatar-section {
  position: relative;
  margin-bottom: 20px;
}

.avatar-glow {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #10b981, #059669);
  border-radius: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  box-shadow: 0 4px 20px rgba(16, 185, 129, 0.3);
  border: 1px solid var(--border-default);
}

.type-tag {
  position: absolute;
  top: -6px;
  right: -10px;
  background: var(--text-primary);
  color: var(--bg-base);
  font-size: 9px;
  font-weight: 900;
  padding: 2px 6px;
  border-radius: 8px;
  text-transform: uppercase;
}

.title {
  font-size: 18px;
  font-weight: 800;
  color: var(--text-main);
  margin: 0 0 16px;
  letter-spacing: -0.5px;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.key-capsule-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  background: var(--bg-elevated);
  border-radius: 100px;
  border: 1px solid var(--border-default);
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 20px;
}

.key-capsule-btn:hover {
  background: var(--bg-card);
  border-color: var(--primary-color);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.1);
}

.key-capsule-btn .label {
  font-size: 9px;
  font-weight: 900;
  color: var(--text-muted);
}

.key-capsule-btn .code {
  font-family: 'JetBrains Mono', monospace;
  font-size: 11px;
  color: var(--text-main);
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.key-capsule-btn .icon {
  font-size: 11px;
  color: var(--text-muted);
}

.description-box {
  flex: 1;
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.description-text {
  font-size: 13px;
  color: var(--text-placeholder);
  line-height: 1.8;
  font-style: italic;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.details-section {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  background: var(--bg-elevated);
  border-radius: 16px;
  border: 1px solid var(--border-subtle);
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  font-size: 10px;
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.detail-value {
  font-size: 12px;
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
}

.detail-value.mono {
  font-family: 'JetBrains Mono', monospace;
  font-size: 11px;
}

.card-footer-action {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--border-subtle);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.action-group {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.action-circle-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 1px solid var(--border-default);
  background: transparent;
  color: var(--text-muted);
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.action-circle-btn:hover {
  background: var(--primary-color);
  color: #fff;
  border-color: var(--primary-color);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
}

.action-circle-btn.delete:hover {
  background: var(--error);
  border-color: var(--error);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.2);
}
</style>
