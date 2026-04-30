<template>
  <div class="online-system-card card-hover">
    <div class="card-header">
      <div class="system-info">
        <div class="icon-wrapper">
          <component :is="system.icon" />
        </div>
        <h4 class="system-name">{{ system.name }}</h4>
      </div>
      <span class="status-badge" :class="`status-${system.status}`">
        {{ getStatusText(system.status) }}
      </span>
    </div>
    <div class="card-footer">
      <span class="meta-item">
        <ClockCircleOutlined />
        <span>{{ system.lastHeartbeat }}</span>
      </span>
      <span class="meta-item">
        <MessageOutlined />
        <span>{{ system.sessions }} 会话</span>
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  ClockCircleOutlined,
  MessageOutlined
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
  extraInfo?: Record<string, string>
}

defineProps<{
  system: OnlineSystem
}>()

const getStatusText = (status: OnlineStatus): string => {
  switch (status) {
    case 'online': return 'ONLINE'
    case 'degraded': return 'DELAY'
    case 'offline': return 'OFFLINE'
    default: return 'UNKNOWN'
  }
}
</script>

<style scoped>
.online-system-card {
  border: 1px solid #f1f5f9;
  border-radius: 12px;
  background: #f8fafc;
  padding: 12px;
  height: 112px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  transition: all 0.2s ease;
}

.card-hover:hover {
  border-color: #3b82f6;
  background-color: #ffffff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.system-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.icon-wrapper {
  width: 28px;
  height: 28px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper :deep(.anticon) {
  width: 14px;
  height: 14px;
  color: #64748b;
}

.system-name {
  font-size: 14px;
  font-weight: 700;
  color: #334155;
  margin: 0;
}

.status-badge {
  font-size: 9px;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.status-online {
  background: #d1fae5;
  color: #059669;
}

.status-degraded {
  background: #fef3c7;
  color: #d97706;
}

.status-offline {
  background: #fee2e2;
  color: #dc2626;
}

.card-footer {
  display: flex;
  align-items: center;
  gap: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 10px;
  color: #94a3b8;
}

.meta-item :deep(.anticon) {
  width: 12px;
  height: 12px;
}

.status-degraded .meta-item:first-child {
  color: #f59e0b;
}

.status-degraded .meta-item:first-child :deep(.anticon) {
  color: #f59e0b;
}
</style>
