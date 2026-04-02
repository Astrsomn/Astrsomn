<template>
  <div class="agent-card" :class="{ 'is-disabled': record.status !== 'enabled', 'is-selected': selected }">
    <div class="card-checkbox">
      <a-checkbox :checked="selected" @change="onToggle" />
    </div>
    <div class="card-header">
      <div class="avatar-box">
        <span class="avatar-letter">{{ initialLetter }}</span>
        <div class="status-indicator" :class="record.status" />
      </div>
      
      <div class="header-main">
        <div class="title-row">
          <h3 class="agent-name">{{ record.agentName }}</h3>
          <span class="model-tag">{{ record.chatInstanceName || record.modelName || '未配置对话实例' }}</span>
        </div>
        <div class="key-row" @click="copyAgentKey">
          <key-outlined class="icon-small" />
          <code>{{ record.agentKey || 'NO_KEY' }}</code>
          <copy-outlined v-if="record.agentKey" class="copy-icon" />
        </div>
      </div>
    </div>

    <div class="card-body">
      <p class="description">{{ record.description || '暂无详细描述信息...' }}</p>
      
      <div class="meta-info">
        <div class="meta-item"><file-text-outlined /> {{ record.promptTitle || '默认策略' }}</div>
        <div class="meta-item"><cloud-outlined /> {{ record.envCode || '默认环境' }}</div>
        <div class="meta-item"><calendar-outlined /> {{ formatTime(record.createTime) }}</div>
      </div>
    </div>

    <div class="card-footer">
      <div class="footer-action" @click="emit('edit', record)">
        <edit-outlined /> 编辑配置
      </div>
      <div class="footer-divider" />
      <a-popconfirm title="确定删除该智能体吗？" @confirm="onConfirmDelete">
        <div class="footer-action danger">
          <delete-outlined /> 删除
        </div>
      </a-popconfirm>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { message } from 'ant-design-vue'
import {
  CalendarOutlined,
  CloudOutlined,
  CopyOutlined,
  EditOutlined,
  DeleteOutlined,
  FileTextOutlined,
  KeyOutlined,
} from '@ant-design/icons-vue'

const props = defineProps<{ record: any; selected?: boolean }>()
const emit = defineEmits(['edit', 'delete', 'toggle'])

const onToggle = (e: { target?: { checked?: boolean } }) => {
  emit('toggle', props.record.id, Boolean(e?.target?.checked))
}

const initialLetter = computed(() => props.record.agentName?.charAt(0).toUpperCase() || '?')
const formatTime = (raw?: string) => raw ? raw.replace('T', ' ').slice(5, 16) : '--'
const onConfirmDelete = () => props.record.id && emit('delete', props.record.id)

const copyAgentKey = async () => {
  if (!props.record.agentKey) return
  try {
    await navigator.clipboard.writeText(props.record.agentKey)
    message.success('Key 已复制')
  } catch {
    message.error('复制失败，请手动复制')
  }
}
</script>

<style scoped>
.agent-card {
  --primary-color: #4f46e5;
  --model-color: #0891b2;
  --text-main: #1e293b;
  --text-sub: #64748b;
  
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, #ffffff 100%);
  border: 1px solid #e2e8f0;
  border-radius: var(--radius-sm);
  display: flex;
  flex-direction: column;
  position: relative;
  transition: all 0.3s ease;
  min-height: 200px;
  overflow: hidden;
  box-shadow:
    0 10px 28px rgba(15, 23, 42, 0.04),
    inset 0 1px 0 rgba(255, 255, 255, 0.75);
  padding: 8px 8px 0;
}

.card-checkbox {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 1;
}

.agent-card.is-selected {
  border-color: var(--primary-color);
  background: linear-gradient(180deg, rgba(79, 70, 229, 0.05) 0%, #ffffff 100%);
  box-shadow:
    0 16px 36px rgba(79, 70, 229, 0.08),
    0 0 0 1px rgba(79, 70, 229, 0.2);
}

.agent-card:hover {
  border-color: var(--primary-color);
  box-shadow:
    0 16px 36px rgba(79, 70, 229, 0.08),
    0 0 0 1px rgba(79, 70, 229, 0.08);
  transform: translateY(-1px);
}

.card-header {
  display: flex;
  padding: 20px 20px 12px;
  gap: 16px;
}

.avatar-box {
  flex-shrink: 0;
  width: 52px;
  height: 52px;
  background: linear-gradient(135deg, #eef2ff 0%, #f8fafc 100%);
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  border: 1px solid #e2e8f0;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.85);
}

.avatar-letter {
  font-size: 22px;
  font-weight: 700;
  color: #4338ca;
}

.status-indicator {
  position: absolute;
  bottom: -2px;
  right: -2px;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  border: 2px solid #fff;
  background: #cbd5e1;
}
.status-indicator.enabled { background: #10b981; }

.header-main {
  flex: 1;
  min-width: 0;
}

.title-row {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 8px;
}

.agent-name {
  margin: 0;
  font-size: 17px;
  font-weight: 700;
  color: var(--text-main);
  letter-spacing: -0.01em;
}

.model-tag {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  font-size: 12px;
  color: var(--model-color);
  font-weight: 600;
  padding: 2px 8px;
  border-radius: var(--radius-sm);
  background: rgba(8, 145, 178, 0.08);
}

.key-row {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  color: var(--text-sub);
  cursor: pointer;
  background: #f8fafc;
  padding: 4px 8px;
  border-radius: var(--radius-sm);
  border: 1px solid #e2e8f0;
  width: fit-content;
  transition:
    color 0.2s ease,
    border-color 0.2s ease,
    background 0.2s ease;
}

.key-row:hover {
  color: var(--primary-color);
  border-color: rgba(79, 70, 229, 0.3);
  background: rgba(79, 70, 229, 0.05);
}

.key-row code {
  background: transparent;
  color: inherit;
  padding: 0;
}

.icon-small,
.copy-icon {
  flex-shrink: 0;
}

.card-body {
  flex: 1;
  padding: 0 20px 16px;
}

.description {
  font-size: 13px;
  color: var(--text-sub);
  line-height: 1.6;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.meta-info {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 11px;
  color: #94a3b8;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 5px 8px;
  border-radius: var(--radius-sm);
  background: #f8fafc;
  border: 1px solid #eef2f7;
}

.card-footer {
  display: flex;
  align-items: center;
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
  border-top: 1px solid #f1f5f9;
  height: 44px;
}

.footer-action {
  flex: 1;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
  gap: 6px;
}

.footer-action:hover {
  background: rgba(255, 255, 255, 0.82);
  color: var(--primary-color);
}

.footer-action.danger:hover {
  color: #ef4444;
  background: rgba(254, 242, 242, 0.95);
}

.footer-divider {
  width: 1px;
  height: 16px;
  background: #e2e8f0;
}

.is-disabled {
  opacity: 0.8;
}
</style>