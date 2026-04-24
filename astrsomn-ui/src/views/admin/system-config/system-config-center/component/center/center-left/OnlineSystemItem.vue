<template>
  <div class="online-item">
    <div class="online-top">
      <div class="online-name">
        <component :is="system.icon" />
        <span class="system-name">{{ system.name }}</span>
      </div>
      <span class="online-status" :class="`status-${system.status}`">
        <span class="status-dot"></span>
        {{ system.statusText }}
      </span>
    </div>
    <div class="online-meta">
      <div class="meta-item">
        <ClockCircleOutlined />
        <span>{{ system.lastHeartbeat }}</span>
      </div>
      <div class="meta-item">
        <DatabaseOutlined />
        <span>{{ system.sessions }} 会话</span>
      </div>
    </div>
    <div class="online-extra" v-if="system.extraInfo">
      <div v-for="(value, key) in system.extraInfo" :key="key" class="extra-item">
        <span class="extra-key">{{ key }}:</span>
        <span class="extra-value">{{ value }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
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
  extraInfo?: Record<string, string>
}

defineProps<{
  system: OnlineSystem
}>()
</script>

<style scoped>
.online-item {
  border: 1px solid var(--border-default);
  border-radius: 10px;
  background: var(--bg-card);
  padding: 12px;
  width: 220px;
  height: 100px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  transition: all 0.2s ease;
}

.online-item:hover {
  border-color: var(--primary);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transform: translateY(-1px);
}

.online-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 8px;
}

.online-name {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 1;
  min-width: 0;
}

.online-name :deep(.anticon) {
  font-size: 14px;
  color: var(--text-secondary);
}

.system-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-heading);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
}

.online-status {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 2px 6px;
  border-radius: 999px;
  font-size: 11px;
  border: 1px solid transparent;
  flex-shrink: 0;
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
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 6px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 10px;
  color: var(--text-secondary);
}

.meta-item :deep(.anticon) {
  font-size: 10px;
}

.online-extra {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-top: 4px;
}

.extra-item {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 9px;
  color: var(--text-muted);
  background: color-mix(in srgb, var(--bg-elevated) 80%, transparent);
  padding: 1px 4px;
  border-radius: 4px;
  border: 1px solid var(--border-default);
}

.extra-key {
  font-weight: 500;
}

.extra-value {
  font-family: ui-monospace, monospace;
}
</style>
