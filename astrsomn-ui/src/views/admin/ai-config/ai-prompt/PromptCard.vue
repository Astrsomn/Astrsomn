<template>
  <div
    class="air-prompt-card-400"
    :class="{
      'is-selected': selected,
      'is-disabled': String(record.status || '') !== 'enabled'
    }"
  >
    <div class="card-header-status">
      <div class="status-chip" :class="String(record.status || 'disabled')">
        <span class="status-dot"></span>
        <span class="status-text">{{ renderEnabled(String(record.status || 'disabled')) }}</span>
      </div>
      <div class="header-checkbox">
        <a-checkbox :checked="selected" @change="onCheckedChange" />
      </div>
    </div>

    <div class="card-content">
      <div class="avatar-section">
        <div class="avatar-glow">
          <file-text-outlined />
        </div>
        <div class="version-tag">VER {{ record.version ?? 1 }}</div>
      </div>

      <h3 class="title" :title="record.promptTitle">
        {{ record.promptTitle || '未命名提示词' }}
      </h3>

      <div 
        class="key-capsule-btn" 
        @click="copyPromptKey(record.promptKey)"
      >
        <span class="label">KEY</span>
        <code class="code">{{ record.promptKey || '自动生成' }}</code>
        <copy-outlined class="icon" />
      </div>

      <div class="description-box">
        <p class="description-text">
          {{ previewContent(record.promptContent) }}
        </p>
      </div>
    </div>

    <div class="card-footer-action">
      <div class="user-meta">
        <user-outlined class="meta-icon" />
        <span class="meta-info">{{ record.createUser || '系统' }}</span>
        <span class="divider">/</span>
        <span class="meta-info">{{ formatShortTime(record.createTime) }}</span>
      </div>

      <div class="action-group">
        <button class="action-circle-btn" @click="emit('history', record)">
          <history-outlined />
        </button>
        <button class="action-circle-btn" @click="emit('edit', record)">
          <edit-outlined />
        </button>
        <a-popconfirm title="确定删除吗？" @confirm="onDelete">
          <button class="action-circle-btn delete">
            <delete-outlined />
          </button>
        </a-popconfirm>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { message } from 'ant-design-vue'
import {
  CopyOutlined,
  DeleteOutlined,
  EditOutlined,
  FileTextOutlined,
  HistoryOutlined,
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
  status?: string
  version?: number
  createTime?: string
}

const props = defineProps<{
  record: PromptCardRecord
  selected: boolean
}>()

const emit = defineEmits(['history', 'edit', 'delete', 'select-change'])

const renderEnabled = (f: string) => f === 'enabled' ? 'Active' : 'Paused'

const previewContent = (raw?: string) => {
  if (!raw) return '暂无描述内容配置。该智能体尚未定义具体的 Prompt 指令...'
  const clean = raw.replace(/\s+/g, ' ').trim()
  // 增加字数限制以填充 400px 的空间感
  return clean.length > 120 ? `${clean.slice(0, 120)}...` : clean
}

const formatShortTime = (raw?: string) => {
  if (!raw) return '--'
  return raw.split('T')[0].slice(5) 
}

const onCheckedChange = (e: any) => emit('select-change', e.target.checked)
const onDelete = () => props.record.id && emit('delete', props.record.id)

const copyPromptKey = async (key?: string) => {
  if (!key) return
  await navigator.clipboard.writeText(key)
  message.success('密钥已复制')
}
</script>

<style scoped>
.air-prompt-card-400 {
  --primary-color: var(--primary);
  --text-main: var(--text-primary);
  --text-muted: var(--text-muted);
  --bg-card: var(--bg-card);
  --border-subtle: var(--border-subtle);
  --border-default: var(--border-default);
  
  width: 100%;
  max-width: 320px;
  min-height: 400px;
  background: var(--bg-card);
  border-radius: 32px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  border: 1px solid var(--border-default);
}

.air-prompt-card-400:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-card);
  border-color: var(--primary);
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

.status-chip.enabled { 
  background: rgba(34, 197, 94, 0.15); 
}
.status-chip.enabled .status-dot { background: var(--success); }
.status-chip.enabled .status-text { color: var(--success); }

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
  background: var(--bg-elevated);
  border-radius: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: var(--primary-color);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-default);
}

.version-tag {
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
}

.key-capsule-btn .icon {
  font-size: 11px;
  color: var(--text-muted);
}

.description-box {
  flex: 1;
  display: flex;
  align-items: center;
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

.card-footer-action {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--border-subtle);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.user-meta {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.meta-icon {
  font-size: 12px;
  color: var(--text-muted);
}

.meta-info {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-muted);
}

.divider {
  color: var(--border-default);
  font-size: 10px;
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

.is-disabled {
  opacity: 0.5;
  filter: grayscale(1);
}
</style>