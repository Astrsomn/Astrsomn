<template>
  <a-card :bordered="false" class="tool-card" :class="{ 'is-disabled': record.enableFlag !== 'enabled' }">
    <div class="deco-bubble bubble-1"></div>
    <div class="deco-bubble bubble-2"></div>

    <div class="card-inner">
      <div class="card-header">
        <div class="title-group">
          <div class="tool-icon">
            <ToolOutlined />
          </div>
          <div class="text-info">
            <h3 class="tool-title" :title="record.toolName">
              {{ record.toolName || record.toolKey || '工具' }}
            </h3>
            <div class="meta-under-title">
              <span class="type-badge">{{ record.type || '--' }}</span>
            </div>
          </div>
        </div>
        <div class="status-dot" :class="{ active: record.enableFlag === 'enabled' }"></div>
      </div>

      <div class="key-section">
        <span class="key-label">KEY</span>
        <code class="key-value">{{ record.toolKey || '--' }}</code>
      </div>

      <div class="detail-section">
        <div v-if="record.beanName" class="detail-item">
          <span class="detail-label">Bean</span>
          <span class="detail-value mono">{{ record.beanName }}</span>
        </div>
        <div v-if="record.methodName" class="detail-item">
          <span class="detail-label">方法</span>
          <span class="detail-value mono">{{ record.methodName }}</span>
        </div>
        <div v-if="record.description" class="detail-item">
          <span class="detail-label">描述</span>
          <span class="detail-value">{{ preview(record.description) }}</span>
        </div>
      </div>

      <div class="card-actions">
        <a-tooltip title="编辑配置" placement="top">
          <a-button type="text" class="action-btn edit" @click="emit('edit', record)">
            <EditOutlined />
          </a-button>
        </a-tooltip>
        <a-popconfirm title="确定删除吗？" ok-text="确认" cancel-text="取消" @confirm="emit('delete', record.id)">
          <a-tooltip title="删除" placement="top">
            <a-button type="text" danger class="action-btn delete">
              <DeleteOutlined />
            </a-button>
          </a-tooltip>
        </a-popconfirm>
      </div>
    </div>
  </a-card>
</template>

<script setup lang="ts">
import { ToolOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import type { AiTool } from '@/api/aiTool.ts'

defineProps<{
  record: AiTool
}>()

const emit = defineEmits(['edit', 'delete'])

const preview = (raw?: string) => {
  if (!raw) return '暂无描述'
  const t = raw.replace(/\s+/g, ' ').trim()
  return t.length > 60 ? `${t.slice(0, 60)}...` : t
}
</script>

<style scoped>
:deep(.ant-card-body) { padding: 0; }

.tool-card {
  position: relative;
  border-radius: 16px;
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-card);
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  overflow: hidden;
  width: 100%;
  min-height: 220px;
}

.tool-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-overview);
  border-color: var(--primary);
}

.tool-card.is-disabled {
  opacity: 0.6;
}

.deco-bubble {
  position: absolute;
  border-radius: 50%;
  opacity: 0.2;
  filter: blur(20px);
  z-index: 1;
}
.bubble-1 {
  width: 80px; height: 80px;
  background: rgba(16, 185, 129, 0.4);
  top: -30px; right: -20px;
}
.bubble-2 {
  width: 60px; height: 60px;
  background: rgba(59, 130, 246, 0.3);
  bottom: -20px; left: -15px;
}

.card-inner {
  position: relative;
  z-index: 2;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.title-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.tool-icon {
  width: 36px; height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  display: flex; align-items: center; justify-content: center;
  font-size: 16px;
  box-shadow: 0 4px 10px rgba(16, 185, 129, 0.3);
  flex-shrink: 0;
}

.text-info { display: flex; flex-direction: column; gap: 4px; min-width: 0; }

.tool-title {
  margin: 0; font-size: 14px; font-weight: 700; color: var(--text-primary);
  max-width: 180px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}

.meta-under-title {
  display: flex; align-items: center; gap: 6px;
}

.type-badge {
  font-size: 10px;
  font-weight: 700;
  padding: 1px 8px;
  border-radius: 99px;
  background: color-mix(in srgb, #10b981 12%, transparent);
  color: #059669;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.status-dot {
  width: 10px; height: 10px;
  border-radius: 50%;
  background: var(--text-muted);
  flex-shrink: 0;
  margin-top: 4px;
}
.status-dot.active {
  background: var(--success);
  box-shadow: 0 0 0 2px var(--bg-card), 0 0 0 4px var(--success);
}

.key-section {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 10px;
  background: var(--bg-elevated);
  border: 1px solid var(--border-subtle);
}

.key-label {
  font-size: 9px;
  font-weight: 900;
  color: var(--text-muted);
  flex-shrink: 0;
}

.key-value {
  font-family: 'JetBrains Mono', monospace;
  font-size: 12px;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 10px;
  border-radius: 10px;
  background: var(--bg-elevated);
  border: 1px solid var(--border-subtle);
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
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
}

.card-actions {
  display: flex;
  justify-content: flex-end;
  gap: 4px;
  padding-top: 4px;
  border-top: 1px solid var(--border-subtle);
}

.action-btn {
  width: 32px; height: 32px;
  border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  color: var(--text-muted);
  transition: all 0.2s;
}
.action-btn.edit:hover {
  color: var(--primary);
  background: var(--primary-hover);
}
.action-btn.delete:hover {
  color: var(--error);
  background: color-mix(in srgb, var(--error) 10%, transparent);
}
</style>
