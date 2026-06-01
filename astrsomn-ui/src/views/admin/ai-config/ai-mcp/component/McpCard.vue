<template>
  <div :class="{ 'is-disabled': record.enabled !== 1 }" class="mcp-card-400">
    <div class="card-header-status">
      <div :class="{ active: record.enabled === 1 }" class="status-chip">
        <span class="status-dot"></span>
        <span class="status-text">{{ record.enabled === 1 ? 'Active' : 'Paused' }}</span>
      </div>
    </div>

    <div class="card-content">
      <div class="avatar-section">
        <div class="avatar-glow">
          <ApiOutlined/>
        </div>
        <div class="type-tag">{{ record.type || 'STDIO' }}</div>
      </div>

      <h3 :title="record.serverName" class="title">
        {{ record.serverName || record.mcpKey || t.card.mcpService }}
      </h3>

      <div class="key-capsule-btn" @click="copyKey(record.mcpKey)">
        <span class="label">KEY</span>
        <code class="code">{{ record.mcpKey || '--' }}</code>
        <copy-outlined class="icon"/>
      </div>

      <div class="details-section">
        <div v-if="record.type === 'SSE'" class="detail-item">
          <span class="detail-label">{{ t.card.sseAddress }}</span>
          <span class="detail-value mono">{{ record.sseAddress || t.card.notConfigured }}</span>
        </div>
        <template v-else>
          <div class="detail-item">
            <span class="detail-label">{{ t.card.command }}</span>
            <span class="detail-value mono">{{ record.command || t.card.notConfigured }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">{{ t.card.args }}</span>
            <span class="detail-value mono">{{ argsCount }}</span>
          </div>
        </template>
      </div>
    </div>

    <div class="card-footer-action">
      <div class="action-group">
        <button class="action-circle-btn" @click="emit('edit', record)">
          <edit-outlined/>
        </button>
        <a-popconfirm :title="t.card.deleteConfirm" @confirm="emit('delete', record.id)">
          <button class="action-circle-btn delete">
            <delete-outlined/>
          </button>
        </a-popconfirm>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {ApiOutlined, CopyOutlined, DeleteOutlined, EditOutlined} from '@ant-design/icons-vue'
import {message} from 'ant-design-vue'
import {usePageTranslation} from '@/locales/pages.ts'
import type {AiMcp} from '@/api/aiMcp.ts'

const props = defineProps<{
  record: AiMcp
}>()

const emit = defineEmits(['edit', 'delete'])

const t = usePageTranslation('ai-mcp')

const argsCount = computed(() => {
  const raw = props.record.args
  if (!raw) return '0'
  try {
    const parsed = JSON.parse(raw)
    if (Array.isArray(parsed)) return `${parsed.length}`
  } catch {
  }
  return `${raw.split(/\s+/).filter(Boolean).length || 1}`
})

const copyKey = async (key?: string) => {
  if (!key) return
  await navigator.clipboard.writeText(key)
  message.success(t.value.card.keyCopied)
}
</script>

<style scoped>
.mcp-card-400 {
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

.mcp-card-400:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-card);
  border-color: var(--primary);
}

.mcp-card-400.is-disabled {
  opacity: 0.5;
  filter: grayscale(1);
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
  background: color-mix(in srgb, var(--success) 15%, transparent);
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
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  border-radius: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  box-shadow: 0 4px 20px color-mix(in srgb, #6366f1 30%, transparent);
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
  margin-bottom: 24px;
}

.key-capsule-btn:hover {
  background: var(--bg-card);
  border-color: var(--primary-color);
  box-shadow: 0 4px 12px color-mix(in srgb, var(--primary) 10%, transparent);
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

.details-section {
  flex: 1;
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
  box-shadow: 0 4px 12px color-mix(in srgb, var(--primary) 20%, transparent);
}

.action-circle-btn.delete:hover {
  background: var(--error);
  border-color: var(--error);
  box-shadow: 0 4px 12px color-mix(in srgb, var(--error) 20%, transparent);
}
</style>
