<template>
  <div class="agent-card">
    <div class="card-accent" aria-hidden="true" />

    <div class="section-avatar">
      <div class="avatar-box">
        <span class="avatar-letter">{{ initialLetter }}</span>
      </div>
      <span
        class="avatar-status-ring"
        :class="record.status"
        :title="statusTitle"
        aria-hidden="true"
      />
    </div>

    <div class="section-body">
      <div class="title-row">
        <h3 class="agent-name" :title="record.agentName">{{ record.agentName }}</h3>
        <span class="status-pill" :class="record.status">{{ statusLabel }}</span>
      </div>

      <div v-if="record.agentKey" class="key-row">
        <code class="agent-key" :title="record.agentKey">{{ record.agentKey }}</code>
      </div>

      <p v-if="record.description" class="agent-desc" :title="record.description">
        {{ record.description }}
      </p>

      <div class="meta-row">
        <div class="meta-chip" title="关联模型">
          <api-outlined class="meta-icon" />
          <span class="meta-value">{{ record.modelName || '未关联' }}</span>
        </div>
        <div class="meta-chip" title="执行策略">
          <file-text-outlined class="meta-icon" />
          <span class="meta-value">{{ record.promptTitle || '默认' }}</span>
        </div>
      </div>
    </div>

    <div class="section-actions">
      <button type="button" class="action-btn" title="配置" @click="emit('edit', record)">
        <edit-outlined />
      </button>
      <a-popconfirm
        title="确定要删除该智能体吗？"
        ok-text="确定"
        cancel-text="取消"
        @confirm="onConfirmDelete"
      >
        <button type="button" class="action-btn danger" title="删除">
          <delete-outlined />
        </button>
      </a-popconfirm>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import {
  ApiOutlined,
  EditOutlined,
  DeleteOutlined,
  FileTextOutlined,
} from '@ant-design/icons-vue'
import type { AiAgent } from '@/api/aiAgent'

const props = defineProps<{ record: AiAgent }>()

const emit = defineEmits<{
  edit: [record: AiAgent]
  delete: [id: number | string]
}>()

const initialLetter = computed(
  () => props.record.agentName?.charAt(0).toUpperCase() || '?',
)

const statusLabel = computed(() =>
  props.record.status === 'enabled' ? '启用' : '停用',
)

const statusTitle = computed(() =>
  props.record.status === 'enabled' ? '运行中' : '已停用',
)

function onConfirmDelete() {
  if (props.record.id != null) {
    emit('delete', props.record.id)
  }
}
</script>

<style scoped>
.agent-card {
  display: flex;
  align-items: stretch;
  gap: 16px;
  width: 100%;
  min-height: 112px;
  padding: 16px 18px;
  position: relative;
  border-radius: var(--radius-xl, 14px);
  border: 1px solid var(--border-default);
  background: var(--bg-card);
  overflow: hidden;
  transition:
    border-color 0.22s ease,
    box-shadow 0.22s ease,
    transform 0.22s cubic-bezier(0.4, 0, 0.2, 1);
}

.agent-card:hover {
  border-color: color-mix(in srgb, var(--primary) 35%, var(--border-default));
  box-shadow:
    var(--shadow-card, 0 10px 30px rgba(0, 0, 0, 0.08)),
    0 0 0 1px color-mix(in srgb, var(--primary) 12%, transparent);
  transform: translateY(-1px);
}

/* 左侧细条：主题蓝渐变 */
.card-accent {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: var(--primary-gradient);
  opacity: 0.9;
  border-radius: var(--radius-xl, 14px) 0 0 var(--radius-xl, 14px);
  transition: opacity 0.2s;
}

.agent-card:hover .card-accent {
  opacity: 1;
}

/* --- 头像 --- */
.section-avatar {
  position: relative;
  flex-shrink: 0;
  display: flex;
  align-items: center;
}

.avatar-box {
  width: 52px;
  height: 52px;
  border-radius: var(--radius-xl, 14px);
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--logo-gradient);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.2),
    0 4px 14px color-mix(in srgb, var(--primary) 35%, transparent);
}

.avatar-letter {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
  letter-spacing: -0.02em;
  user-select: none;
}

.avatar-status-ring {
  position: absolute;
  right: -3px;
  bottom: -3px;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  border: 2.5px solid var(--bg-card);
  background: var(--text-muted);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.15);
}

.avatar-status-ring.enabled {
  background: var(--success);
}

/* --- 正文 --- */
.section-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 6px;
  padding-top: 1px;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  min-width: 0;
}

.agent-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-heading);
  letter-spacing: -0.02em;
  line-height: 1.3;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  min-width: 0;
  flex: 1;
}

.status-pill {
  flex-shrink: 0;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.02em;
  padding: 2px 8px;
  border-radius: 999px;
  border: 1px solid var(--border-default);
  background: var(--bg-surface);
  color: var(--text-secondary);
}

.status-pill.enabled {
  border-color: color-mix(in srgb, var(--success) 45%, var(--border-default));
  background: color-mix(in srgb, var(--success) 12%, var(--bg-surface));
  color: var(--success);
}

.key-row {
  min-width: 0;
}

.agent-key {
  display: inline-block;
  max-width: 100%;
  font-size: 11px;
  font-family: ui-monospace, 'JetBrains Mono', 'SF Mono', monospace;
  color: var(--text-secondary);
  background: var(--bg-input);
  padding: 2px 8px;
  border-radius: var(--radius-sm, 6px);
  border: 1px solid var(--border-default);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.agent-desc {
  margin: 0;
  font-size: 12px;
  line-height: 1.45;
  color: var(--text-secondary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 2px;
}

.meta-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  max-width: 100%;
  padding: 5px 10px;
  border-radius: var(--radius-md, 8px);
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  font-size: 12px;
  color: var(--text-primary);
}

.meta-icon {
  flex-shrink: 0;
  font-size: 13px;
  color: var(--accent-blue);
}

.meta-value {
  font-weight: 500;
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  min-width: 0;
}

/* --- 操作 --- */
.section-actions {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 6px;
  padding-left: 4px;
  border-left: 1px solid var(--border-subtle);
  margin-left: 2px;
}

.action-btn {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  border: 1px solid transparent;
  background: transparent;
  color: var(--text-muted);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  transition:
    color 0.18s,
    background 0.18s,
    border-color 0.18s;
}

.action-btn:hover {
  background: var(--primary-hover);
  color: var(--primary);
  border-color: color-mix(in srgb, var(--primary) 28%, transparent);
}

.action-btn.danger:hover {
  background: color-mix(in srgb, var(--error) 12%, var(--bg-card));
  color: var(--error);
  border-color: color-mix(in srgb, var(--error) 35%, transparent);
}

@media (max-width: 480px) {
  .agent-card {
    flex-wrap: wrap;
    min-height: auto;
    padding: 14px;
  }

  .section-actions {
    flex-direction: row;
    width: 100%;
    justify-content: flex-end;
    border-left: none;
    padding-left: 0;
    margin-left: 0;
    padding-top: 8px;
    border-top: 1px solid var(--border-subtle);
  }

  .meta-row {
    flex-direction: column;
  }
}
</style>
