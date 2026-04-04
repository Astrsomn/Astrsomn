<template>
  <div 
    class="agent-card-v3" 
    :class="{ 'is-disabled': record.status !== 'enabled', 'is-selected': selected }"
  >
    <div class="aurora-glow"></div>

    <div class="card-top-bar">
      <div class="env-tag" :class="record.envCode">
        <span class="status-dot"></span>
        {{ record.envCode || '默认环境' }}
      </div>
      <div class="card-checkbox">
        <a-checkbox :checked="selected" @change="onToggle" />
      </div>
    </div>

    <div class="card-header">
      <div class="avatar-container">
        <div class="avatar-main">
          <span class="avatar-letter">{{ initialLetter }}</span>
        </div>
        <div class="status-badge" :class="record.status" :title="record.status === 'enabled' ? '运行中' : '已禁用'"></div>
      </div>
      
      <div class="header-content">
        <div class="title-area">
          <h3 class="agent-name">{{ record.agentName }}</h3>
          <p class="create-time">创建于 {{ formatTime(record.createTime) }}</p>
        </div>
      </div>
    </div>

    <div class="key-section" @click="copyAgentKey" title="点击复制 Key">
      <div class="key-label">
        <key-outlined />
        <span>AGENT KEY</span>
      </div>
      <div class="key-value">
        <code>{{ record.agentKey || '未分配 KEY' }}</code>
        <copy-outlined v-if="record.agentKey" class="copy-icon" />
      </div>
    </div>

    <div class="card-body">
      <p class="description">{{ record.description || '暂无详细描述信息...' }}</p>
      
      <div class="config-grid">
        <div class="config-item">
          <div class="item-label"><robot-outlined /> 模型实例</div>
          <div class="item-value highlight">
             {{ record.chatInstanceName || record.modelName || '未配置' }}
          </div>
        </div>
        <div class="config-item">
          <div class="item-label"><file-text-outlined /> 提示词策略</div>
          <div class="item-value">
            {{ record.promptTitle || '默认策略' }}
          </div>
        </div>
      </div>
    </div>

    <div class="card-footer">
      <button class="action-btn edit" @click="emit('edit', record)">
        <edit-outlined />
        <span>编辑配置</span>
      </button>
      <div class="btn-divider"></div>
      <a-popconfirm title="确定删除该智能体吗？" @confirm="onConfirmDelete">
        <button class="action-btn delete">
          <delete-outlined />
          <span>删除</span>
        </button>
      </a-popconfirm>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { message } from 'ant-design-vue'
import {
  CopyOutlined,
  EditOutlined,
  DeleteOutlined,
  FileTextOutlined,
  KeyOutlined,
  RobotOutlined,
} from '@ant-design/icons-vue'

const props = defineProps<{ record: any; selected?: boolean }>()
const emit = defineEmits(['edit', 'delete', 'toggle'])

// 逻辑保留
const onToggle = (e: any) => {
  emit('toggle', props.record.id, Boolean(e?.target?.checked))
}

const initialLetter = computed(() => props.record.agentName?.charAt(0).toUpperCase() || '?')

const formatTime = (raw?: string) => {
  if (!raw) return '--'
  // 保持原有的简化逻辑，仅展示日期和小时
  return raw.replace('T', ' ').slice(0, 16)
}

const onConfirmDelete = () => props.record.id && emit('delete', props.record.id)

const copyAgentKey = async () => {
  if (!props.record.agentKey) return
  try {
    await navigator.clipboard.writeText(props.record.agentKey)
    message.success('Key 已成功复制到剪贴板')
  } catch {
    message.error('复制失败，请手动复制')
  }
}
</script>

<style scoped>
/* 核心容器 */
.agent-card-v3 {
  --primary-blue: #2563eb;
  --light-blue: #eff6ff;
  --border-color: #e2e8f0;
  --text-main: #1e293b;
  --text-sub: #64748b;
  max-width: 400px;
  max-height: 400px;
  background: #ffffff;
  border: 1px solid var(--border-color);
  border-radius: 24px;
  display: flex;
  flex-direction: column;
  position: relative;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.02), 0 2px 4px -1px rgba(0, 0, 0, 0.01);
}

/* 装饰性背景微光 */
.aurora-glow {
  position: absolute;
  top: -60px;
  right: -60px;
  width: 120px;
  height: 120px;
  background: radial-gradient(circle, rgba(37, 99, 235, 0.08) 0%, transparent 70%);
  z-index: 0;
  pointer-events: none;
}

/* 悬浮与选中状态 */
.agent-card-v3:hover {
  transform: translateY(-4px);
  border-color: #bfdbfe;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.05), 0 10px 10px -5px rgba(0, 0, 0, 0.02);
}

.agent-card-v3.is-selected {
  border-color: var(--primary-blue);
  background: linear-gradient(to bottom right, #ffffff, #f0f7ff);
  box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.1);
}

/* 顶部栏 */
.card-top-bar {
  padding: 16px 16px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 1;
}

.env-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  background: #f1f5f9;
  border-radius: 8px;
  font-size: 10px;
  font-weight: 800;
  color: var(--text-sub);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.env-tag.prod, .env-tag.production {
  background: #ecfdf5;
  color: #059669;
}

.status-dot {
  width: 6px;
  height: 6px;
  background: currentColor;
  border-radius: 50%;
}

/* 头部 */
.card-header {
  padding: 16px;
  display: flex;
  gap: 16px;
  align-items: center;
}

.avatar-container {
  position: relative;
  flex-shrink: 0;
}

.avatar-main {
  width: 52px;
  height: 52px;
  background: linear-gradient(135deg, var(--primary-blue) 0%, #3b82f6 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 16px -4px rgba(37, 99, 235, 0.3);
}

.avatar-letter {
  color: white;
  font-size: 22px;
  font-weight: 800;
}

.status-badge {
  position: absolute;
  bottom: -2px;
  right: -2px;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  border: 3px solid #fff;
  background: #cbd5e1;
}

.status-badge.enabled {
  background: #10b981;
  box-shadow: 0 0 8px rgba(16, 185, 129, 0.4);
}

.header-content {
  flex: 1;
  min-width: 0;
}

.agent-name {
  margin: 0;
  font-size: 18px;
  font-weight: 800;
  color: var(--text-main);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.create-time {
  font-size: 11px;
  color: var(--text-sub);
  margin-top: 2px;
}

/* Key 区块 - 方案三标志性设计 */
.key-section {
  margin: 0 16px 16px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px dashed #e2e8f0;
}

.key-section:hover {
  background: var(--light-blue);
  border-color: #bfdbfe;
}

.key-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 9px;
  font-weight: 800;
  color: #94a3b8;
  margin-bottom: 4px;
}

.key-value {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--primary-blue);
  font-family: ui-monospace, monospace;
  font-size: 12px;
}

.copy-icon {
  font-size: 12px;
  opacity: 0.5;
}

/* 正文与配置 */
.card-body {
  padding: 0 16px 16px;
  flex: 1;
}

.description {
  font-size: 13px;
  color: var(--text-sub);
  line-height: 1.6;
  margin-bottom: 16px;
  height: 40px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.config-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.config-item {
  padding: 10px;
  background: white;
  border: 1px solid #f1f5f9;
  border-radius: 12px;
}

.item-label {
  font-size: 10px;
  color: #94a3b8;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.item-value {
  font-size: 12px;
  font-weight: 700;
  color: var(--text-main);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-value.highlight {
  color: var(--primary-blue);
}

/* 底部操作 */
.card-footer {
  display: flex;
  border-top: 1px solid #f8fafc;
  padding: 8px;
  gap: 8px;
}

.action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 8px 0;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-sub);
  border-radius: 10px;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #f1f5f9;
  color: var(--primary-blue);
}

.action-btn.delete:hover {
  background: #fef2f2;
  color: #ef4444;
}

.btn-divider {
  width: 1px;
  height: 20px;
  background: #f1f5f9;
  align-self: center;
}

/* 禁用状态 */
.is-disabled {
  opacity: 0.7;
}

.is-disabled .avatar-main {
  filter: grayscale(1);
  opacity: 0.6;
}
</style>