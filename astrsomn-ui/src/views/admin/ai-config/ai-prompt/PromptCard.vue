<template>
  <div
    class="prompt-card"
    :class="{
      selected,
      disabled: String(record.enabledFlag || '') !== 'enabled'
    }"
  >
    <div class="card-checkbox-corner">
      <a-checkbox :checked="selected" @change="onCheckedChange" />
    </div>

    <div class="card-top-row">
      <div class="left-section">
        <div class="mini-avatar">
          <file-text-outlined />
        </div>
        <h3 class="prompt-title" :title="record.promptTitle">
          {{ record.promptTitle || '未命名提示词' }}
        </h3>
      </div>
      <div class="status-tag">
        <a-tag :color="String(record.enabledFlag || '') === 'enabled' ? 'success' : 'error'" class="compact-tag">
          {{ renderEnabled(String(record.enabledFlag || 'disabled')) }}
        </a-tag>
      </div>
    </div>

    <div class="card-info-grid">
      <div class="tag-group">
        <span class="info-label"><cloud-outlined /> {{ record.envCode || '默认' }}</span>
        <span class="info-label"><tags-outlined /> {{ record.scene || '通用' }}</span>
        <span class="info-label version-label">V{{ record.version ?? 1 }}</span>
      </div>
      <button
        type="button"
        class="compact-key"
        :disabled="!record.promptKey"
        @click="copyPromptKey(record.promptKey)"
      >
        <span class="key-text">{{ record.promptKey || '自动生成' }}</span>
        <copy-outlined v-if="record.promptKey" />
      </button>
    </div>

    <p class="content-preview">{{ previewContent(record.promptContent) }}</p>

    <div class="card-footer">
      <div class="meta-info">
        <span :title="record.createUser"><user-outlined /> {{ record.createUser || '系统' }}</span>
        <span><calendar-outlined /> {{ formatShortTime(record.createTime) }}</span>
      </div>
      <div class="card-actions">
        <a-button type="text" size="small" class="action-btn history-btn" @click="emit('history', record)">
          <template #icon><history-outlined /></template>
        </a-button>
        <a-button type="text" size="small" class="action-btn edit-btn" @click="emit('edit', record)">
          <template #icon><edit-outlined /></template>
        </a-button>
        <a-popconfirm title="确定删除吗？" @confirm="onDelete">
          <a-button type="text" size="small" danger class="action-btn delete-btn">
            <template #icon><delete-outlined /></template>
          </a-button>
        </a-popconfirm>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { message } from 'ant-design-vue'
import {
  CalendarOutlined,
  CloudOutlined,
  CopyOutlined,
  DeleteOutlined,
  EditOutlined,
  FileTextOutlined,
  HistoryOutlined,
  TagsOutlined,
  UserOutlined
} from '@ant-design/icons-vue'

type PromptCardRecord = {
  id?: number | string
  envCode?: string
  createUser?: string
  promptKey?: string
  promptTitle?: string
  promptContent?: string
  scene?: string
  enabledFlag?: string
  version?: number
  createTime?: string
}

const props = defineProps<{
  record: PromptCardRecord
  selected: boolean
}>()

const emit = defineEmits(['history', 'edit', 'delete', 'select-change'])

const renderEnabled = (f: string) => f === 'enabled' ? '启用' : '停用'

const previewContent = (raw?: string) => {
  if (!raw) return '暂无内容...'
  const clean = raw.replace(/\s+/g, ' ').trim()
  return clean.length > 70 ? `${clean.slice(0, 70)}...` : clean
}

const formatShortTime = (raw?: string) => {
  if (!raw) return '--'
  return raw.split('T')[0].slice(2) // 返回 YY-MM-DD 格式节省空间
}

const onCheckedChange = (e: any) => emit('select-change', e.target.checked)
const onDelete = () => props.record.id && emit('delete', props.record.id)

const copyPromptKey = async (key?: string) => {
  if (!key) return
  await navigator.clipboard.writeText(key)
  message.success('已复制')
}
</script>

<style scoped>
.prompt-card {
  --primary-color: #4f46e5;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 10px 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  transition: all 0.2s ease;
  position: relative;
  overflow: hidden;
}

.card-checkbox-corner {
  position: absolute;
  left: 12px;
  bottom: 10px;
  display: flex;
  align-items: center;
  z-index: 1;
}

.prompt-card:hover {
  border-color: var(--primary-color);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.prompt-card.selected {
  border-color: var(--primary-color);
  background: #f5f3ff;
}

.prompt-card.disabled {
  opacity: 0.6;
  background: #fafafa;
}

/* 顶部：标题栏 */
.card-top-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.left-section {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 0;
}

.mini-avatar {
  width: 24px;
  height: 24px;
  background: linear-gradient(135deg, #279ea9, #3b64b2);
  color: white;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  flex-shrink: 0;
}

.prompt-title {
  margin: 0;
  font-size: 13px;
  font-weight: 600;
  color: #1f2937;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.compact-tag {
  margin: 0;
  padding: 0 4px;
  font-size: 10px;
  line-height: 16px;
  border-radius: 4px;
}

/* 信息网格 */
.card-info-grid {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 4px;
}

.tag-group {
  display: flex;
  gap: 8px;
  color: #6b7280;
  font-size: 11px;
}

.info-label {
  display: flex;
  align-items: center;
  gap: 3px;
}

.version-label {
  color: #b45309;
  font-weight: bold;
}

.compact-key {
  cursor: pointer;
  border: 1px solid #e5e7eb;
  background: #f9fafb;
  border-radius: 6px;
  padding: 2px 6px;
  font-size: 10px;
  font-family: monospace;
  display: flex;
  align-items: center;
  gap: 4px;
  max-width: 100px;
}

.compact-key:hover {
  color: var(--primary-color);
  border-color: var(--primary-color);
}

.key-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 内容预览 */
.content-preview {
  margin: 0;
  font-size: 11px;
  color: #4b5563;
  line-height: 1.5;
  min-height: 32px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 底部操作区 */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 4px;
  padding-top: 8px;
  padding-left: 24px;
  border-top: 1px dashed #f3f4f6;
}

.meta-info {
  display: flex;
  gap: 8px;
  font-size: 10px;
  color: #9ca3af;
}

.card-actions {
  display: flex;
  gap: 2px;
}

.action-btn {
  padding: 0 4px;
  height: 24px;
  width: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.history-btn {
  color: #16a34a;
}

.history-btn:hover {
  color: #15803d;
  background: #f0fdf4;
}

.edit-btn {
  color: #2563eb;
}

.edit-btn:hover {
  color: #1d4ed8;
  background: #eff6ff;
}

.delete-btn {
  color: #dc2626;
}

.delete-btn:hover {
  color: #b91c1c;
  background: #fef2f2;
}
</style>