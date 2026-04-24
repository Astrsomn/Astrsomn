<template>
  <div class="panel-card online-panel">
    <div class="panel-head">
      <h3>
        <TeamOutlined />
        在线业务系统
      </h3>
      <span>用户即业务系统，展示实时在线状态</span>
    </div>
    <div class="online-grid">
      <div v-for="system in pagedOnlineSystems" :key="system.name" class="online-item">
        <div class="online-top">
          <div class="online-name">
            <component :is="system.icon" />
            <span>{{ system.name }}</span>
          </div>
          <span class="online-status" :class="`status-${system.status}`">
            <span class="status-dot"></span>
            {{ system.statusText }}
          </span>
        </div>
        <div class="online-meta">
          <span><ClockCircleOutlined /> 最后心跳 {{ system.lastHeartbeat }}</span>
          <span><DatabaseOutlined /> 活跃会话 {{ system.sessions }}</span>
        </div>
      </div>
    </div>
    <div class="pager-wrap">
      <button type="button" class="pager-btn" :disabled="currentPage === 1" @click="emit('prev-page')">
        上一页
      </button>
      <span class="pager-text">第 {{ currentPage }} / {{ totalPages }} 页，共 {{ totalSystems }} 个系统</span>
      <button type="button" class="pager-btn" :disabled="currentPage === totalPages" @click="emit('next-page')">
        下一页
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  TeamOutlined,
  ClockCircleOutlined,
  DatabaseOutlined
} from '@ant-design/icons-vue'
import type { Component } from 'vue'

type OnlineStatus = 'online' | 'degraded' | 'offline'

type OnlineSystem = {
  name: string
  status: OnlineStatus
  statusText: string
  sessions: number
  lastHeartbeat: string
  icon: Component
}

defineProps<{
  pagedOnlineSystems: OnlineSystem[]
  currentPage: number
  totalPages: number
  totalSystems: number
}>()

const emit = defineEmits<{
  (e: 'prev-page'): void
  (e: 'next-page'): void
}>()
</script>

<style scoped>
.panel-card {
  border: 1px solid var(--border-default);
  border-radius: 14px;
  background: var(--bg-card);
  padding: 16px;
}

.online-panel {
  display: flex;
  flex-direction: column;
  height: calc(100% - 65px);
  overflow: hidden;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 10px;
}

.panel-head h3 {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 16px;
  color: var(--text-heading);
}

.panel-head span {
  font-size: 12px;
  color: var(--text-secondary);
}

.online-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  flex: 1;
  overflow-y: auto;
  padding-right: 4px;
  min-height: 0;
}

.online-item {
  border: 1px solid var(--border-default);
  border-radius: 10px;
  background: var(--bg-card);
  padding: 10px 12px;
  flex: 1 1 calc(50% - 5px);
  min-width: 200px;
  max-width: calc(50% - 5px);
}

.online-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.online-name {
  display: flex;
  align-items: center;
  gap: 5px;
  color: var(--text-heading);
  font-size: 13px;
  font-weight: 600;
}

.online-status {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 2px 6px;
  border-radius: 999px;
  font-size: 11px;
  border: 1px solid transparent;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 999px;
  background: currentColor;
}

.status-online {
  color: #2e9f5d;
  background: color-mix(in srgb, #2e9f5d 12%, transparent);
  border-color: color-mix(in srgb, #2e9f5d 26%, transparent);
}

.status-degraded {
  color: #f39c12;
  background: color-mix(in srgb, #f39c12 12%, transparent);
  border-color: color-mix(in srgb, #f39c12 24%, transparent);
}

.status-offline {
  color: #dd4b39;
  background: color-mix(in srgb, #dd4b39 12%, transparent);
  border-color: color-mix(in srgb, #dd4b39 24%, transparent);
}

.online-meta {
  margin-top: 6px;
  display: flex;
  justify-content: space-between;
  gap: 6px;
  color: var(--text-secondary);
  font-size: 10px;
}

.online-meta span {
  display: inline-flex;
  align-items: center;
  gap: 3px;
}

.pager-wrap {
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.pager-btn {
  border: 1px solid var(--border-default);
  border-radius: 8px;
  background: var(--bg-card);
  color: var(--text-primary);
  font-size: 12px;
  padding: 4px 10px;
  cursor: pointer;
}

.pager-btn:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.pager-text {
  color: var(--text-secondary);
  font-size: 12px;
}

@media (max-width: 900px) {
  .pager-wrap {
    flex-direction: column;
    align-items: stretch;
  }

  .pager-text {
    text-align: center;
  }
}
</style>
