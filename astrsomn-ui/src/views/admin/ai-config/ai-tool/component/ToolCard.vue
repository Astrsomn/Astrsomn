<template>
  <div :class="{ 'is-disabled': record.enableFlag !== 'enabled' }" class="tool-card">
    <div class="card-header">
      <div class="flex items-center gap-2.5">
        <div class="icon-wrapper">
          <ToolOutlined class="icon" />
        </div>
        <div class="truncate max-w-[120px]">
          <h4 class="title">{{ record.toolName || record.toolKey || t.card.tool }}</h4>
          <p class="sub-title">{{ record.toolKey || 'TO-XXXX' }}</p>
        </div>
      </div>
      <span class="status-tag">{{ record.type || '--' }}</span>
    </div>

    <div class="card-body">
      <span class="body-label flex items-center gap-1">
        <SyncOutlined class="label-icon" />
        {{ t.card.todayCalls }}
      </span>
      <span class="body-value">{{ record.todayCalls || '0' }} 次</span>
    </div>

    <div class="card-footer">
      <span class="status-indicator flex items-center gap-1">
        <span class="status-dot" :class="{ active: record.enableFlag === 'enabled' }"></span>
        {{ record.enableFlag === 'enabled' ? 'Active' : 'Paused' }}
      </span>
      <div class="action-group">
        <button class="action-btn" @click="emit('edit', record)" :title="t.card.edit">
          <EditOutlined class="action-icon" />
        </button>
        <a-popconfirm :title="t.card.deleteConfirm" @confirm="emit('delete', record.id)">
          <button class="action-btn delete" :title="t.card.delete">
            <DeleteOutlined class="action-icon" />
          </button>
        </a-popconfirm>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {DeleteOutlined, EditOutlined, SyncOutlined, ToolOutlined} from '@ant-design/icons-vue'
import {usePageTranslation} from '@/locales/pages.ts'
import type {AiTool} from '@/api/aiTool.ts'

defineProps<{
  record: AiTool
}>()

const emit = defineEmits(['edit', 'delete'])

const t = usePageTranslation('ai-tool')
</script>

<style scoped>
.tool-card {
  --primary-color: #f59e0b;
  --icon-bg: #fef3c7;
  --icon-color: #f59e0b;
  --tag-bg: #fef3c7;
  --tag-color: #d97706;

  width: 100%;
  background: var(--bg-card);
  border-radius: 12px;
  padding: 14px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 130px;
  border: 1px solid var(--border-default);
  transition: all 0.2s ease;
}

.tool-card:hover {
  border-color: var(--primary-color);
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.15);
}

.tool-card.is-disabled {
  opacity: 0.6;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.icon-wrapper {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: var(--icon-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.icon {
  width: 18px;
  height: 18px;
  color: var(--icon-color);
}

.title {
  font-size: 12px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  padding: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sub-title {
  font-size: 9px;
  color: var(--text-muted);
  font-family: monospace;
  margin: 0;
  padding: 0;
}

.status-tag {
  font-size: 9px;
  font-weight: 700;
  padding: 2px 6px;
  border-radius: 4px;
  background: var(--tag-bg);
  color: var(--tag-color);
  border: 1px solid rgba(245, 158, 11, 0.2);
  flex-shrink: 0;
  font-family: monospace;
}

.card-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
  border-top: 1px solid var(--border-subtle);
  border-bottom: 1px solid var(--border-subtle);
  font-size: 10px;
  color: var(--text-muted);
}

.body-label {
  display: flex;
  align-items: center;
  gap: 4px;
}

.label-icon {
  width: 14px;
  height: 14px;
}

.body-value {
  font-weight: 700;
  color: var(--text-primary);
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 10px;
  color: var(--text-muted);
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--text-muted);
}

.status-dot.active {
  background: var(--success);
}

.action-group {
  display: flex;
  gap: 6px;
  opacity: 0.6;
  transition: opacity 0.2s ease;
}

.tool-card:hover .action-group {
  opacity: 1;
}

.action-btn {
  width: 24px;
  height: 24px;
  border: none;
  background: transparent;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--text-muted);
}

.action-btn:hover {
  background: var(--primary-color);
  color: #fff;
}

.action-btn.delete:hover {
  background: var(--error);
}

.action-icon {
  width: 14px;
  height: 14px;
}
</style>