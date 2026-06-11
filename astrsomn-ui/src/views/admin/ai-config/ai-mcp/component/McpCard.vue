<template>
  <div
      class="card"
      :class="{ 'card-disabled': record.enabled !== 1, 'card-selected': selected }"
  >
    <!-- Selection checkbox (revealed on hover) -->
    <a-checkbox
        :checked="selected"
        class="card-checkbox"
        @click.stop="onToggle"
    />

    <!-- Card Header -->
    <div class="card-header">
      <div class="card-avatar">
        <ApiOutlined class="card-avatar-icon"/>
      </div>
      <div class="card-header-text">
        <h3 class="card-title">{{ record.serverName || record.mcpKey || t.card.mcpService }}</h3>
        <span class="card-tag">{{ record.type || 'STDIO' }}</span>
      </div>
    </div>

    <!-- Card Info -->
    <p class="card-info">{{ record.mcpKey || '—' }}</p>

    <!-- Card Footer -->
    <div class="card-footer">
      <span class="meta-chip">
        <span class="status-dot" :class="{ active: record.enabled === 1 }"/>
        {{ record.enabled === 1 ? t.card.statusEnabled : t.card.statusDisabled }}
      </span>
      <span class="meta-spacer"/>
      <a-popconfirm
          :title="t.card.deleteConfirm"
          @confirm.stop="onDelete"
      >
        <button class="action-delete-btn" @click.stop><DeleteOutlined/></button>
      </a-popconfirm>
      <button class="action-edit-link" @click.stop="emit('edit', record)">
        {{ t.card.edit }} <RightOutlined/>
      </button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {ApiOutlined, DeleteOutlined, RightOutlined} from '@ant-design/icons-vue'
import {usePageTranslation} from '@/locales/pages.ts'
import type {AiMcp} from '@/api/aiMcp.ts'

const props = defineProps<{
  record: AiMcp
  selected?: boolean
}>()

const emit = defineEmits<{
  (e: 'edit', record: AiMcp): void
  (e: 'delete', id: number | string): void
  (e: 'toggle', id: number | string, checked: boolean): void
}>()

const t = usePageTranslation('ai-mcp')

const onDelete = () => {
  if (props.record.id != null) {
    emit('delete', props.record.id)
  }
}

const onToggle = () => {
  if (props.record.id != null) {
    emit('toggle', props.record.id, !props.selected)
  }
}
</script>

<style scoped>
/* ── Card Container (unified style — light blue accent) ── */
.card {
  --card-accent: #3b82f6;
  --card-gradient: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%);
  --card-tag-bg: #eff6ff;
  --card-tag-color: #2563eb;

  position: relative;
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.card:hover {
  border-color: var(--card-accent);
  box-shadow: 0 4px 20px -4px color-mix(in srgb, var(--card-accent) 15%, transparent);
  transform: translateY(-1px);
}

.card-disabled {
  opacity: 0.6;
}

.card-selected {
  border-color: var(--card-accent);
  box-shadow: 0 0 0 1px var(--card-accent);
}

/* ── Selection Checkbox ── */
.card-checkbox {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 2;
  opacity: 0;
  transition: opacity 0.15s;
}

.card:hover .card-checkbox,
.card-selected .card-checkbox {
  opacity: 1;
}

/* ── Card Header ── */
.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-avatar {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--card-gradient);
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  overflow: hidden;
}

.card-avatar-icon {
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-header-text {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.card-title {
  font-size: 13px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-tag {
  font-size: 9px;
  font-weight: 600;
  padding: 1px 7px;
  border-radius: 10px;
  flex-shrink: 0;
  line-height: 18px;
  background: var(--card-tag-bg);
  color: var(--card-tag-color);
}

/* ── Card Info ── */
.card-info {
  font-size: 11px;
  color: var(--text-muted);
  line-height: 1.6;
  margin: 0;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* ── Card Footer ── */
.card-footer {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: auto;
}

.meta-chip {
  font-size: 10px;
  color: var(--text-muted);
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 2px 7px;
  background: var(--bg-elevated);
  border-radius: 6px;
  font-weight: 500;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--text-muted);
  flex-shrink: 0;
}

.status-dot.active {
  background: #10b981;
}

.meta-spacer {
  flex: 1;
}

/* ── Action Buttons (reveal on hover) ── */
.action-delete-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border: 1px solid var(--border-subtle);
  border-radius: 6px;
  background: var(--bg-elevated);
  color: var(--text-muted);
  cursor: pointer;
  font-size: 12px;
  transition: all 0.15s;
  flex-shrink: 0;
  padding: 0;
  opacity: 0;
}

.card:hover .action-delete-btn {
  opacity: 1;
}

.action-delete-btn:hover {
  color: var(--error);
  border-color: var(--error);
  background: color-mix(in srgb, var(--error) 8%, transparent);
}

.action-edit-link {
  font-size: 10px;
  color: var(--card-accent);
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 3px;
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.15s;
}

.card:hover .action-edit-link {
  opacity: 1;
}

.action-edit-link :deep(svg) {
  font-size: 8px;
}
</style>
