<template>
  <a-card :bordered="false" class="mcp-card" :class="{ 'is-disabled': record.enabled !== 1 }">
    <div class="deco-bubble bubble-1"></div>
    <div class="deco-bubble bubble-2"></div>

    <div class="card-inner">
      <div class="card-header">
        <div class="title-group">
          <div class="mcp-icon">
            <ApiOutlined />
          </div>
          <div class="text-info">
            <h3 class="mcp-title" :title="record.serverName">
              {{ record.serverName || record.mcpKey || 'MCP 服务' }}
            </h3>
            <div class="meta-under-title">
              <span class="type-badge">{{ record.type || 'STDIO' }}</span>
            </div>
          </div>
        </div>
        <div class="status-dot" :class="{ active: record.enabled === 1 }"></div>
      </div>

      <div class="key-section">
        <span class="key-label">KEY</span>
        <code class="key-value">{{ record.mcpKey || '--' }}</code>
      </div>

      <div class="detail-section">
        <div v-if="record.type === 'SSE'" class="detail-item">
          <span class="detail-label">SSE 地址</span>
          <span class="detail-value mono">{{ record.sseAddress || '未配置' }}</span>
        </div>
        <template v-else>
          <div class="detail-item">
            <span class="detail-label">执行命令</span>
            <span class="detail-value mono">{{ record.command || '未配置' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">参数</span>
            <span class="detail-value mono">{{ argsCount }}</span>
          </div>
        </template>
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
import { computed } from 'vue'
import { ApiOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import type { AiMcp } from '@/api/aiMcp'

const props = defineProps<{
  record: AiMcp
}>()

const emit = defineEmits(['edit', 'delete'])

const argsCount = computed(() => {
  const raw = props.record.args
  if (!raw) return '0'
  try {
    const parsed = JSON.parse(raw)
    if (Array.isArray(parsed)) return `${parsed.length}`
  } catch {}
  return `${raw.split(/\s+/).filter(Boolean).length || 1}`
})
</script>

<style scoped>
:deep(.ant-card-body) { padding: 0; }

.mcp-card {
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

.mcp-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-overview);
  border-color: var(--primary);
}

.mcp-card.is-disabled {
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
  background: rgba(99, 102, 241, 0.4);
  top: -30px; right: -20px;
}
.bubble-2 {
  width: 60px; height: 60px;
  background: rgba(16, 185, 129, 0.3);
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

.mcp-icon {
  width: 36px; height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: white;
  display: flex; align-items: center; justify-content: center;
  font-size: 16px;
  box-shadow: 0 4px 10px rgba(99, 102, 241, 0.3);
  flex-shrink: 0;
}

.text-info { display: flex; flex-direction: column; gap: 4px; min-width: 0; }

.mcp-title {
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
  background: var(--primary-hover);
  color: var(--primary);
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
