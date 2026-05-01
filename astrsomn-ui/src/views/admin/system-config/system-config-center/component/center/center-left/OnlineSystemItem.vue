<template>
  <tr class="biz-row">
    <td class="col-name">
      <div class="name-cell">
        <div class="icon-swatch" :class="`icon-swatch--${system.iconTheme}`">
          <component :is="system.icon" class="icon-swatch__ico" />
        </div>
        <span class="name-text">{{ system.name }}</span>
      </div>
    </td>
    <td class="col-slug">
      <span class="slug-text">{{ system.slug }}</span>
    </td>
    <td class="col-owner">
      <span class="owner-line">
        <span class="owner-dot" :class="`owner-dot--${system.ownerDot}`" aria-hidden="true" />
        {{ system.ownerName }}
      </span>
    </td>
    <td class="col-env">
      <span class="env-tag" :class="`env-tag--${system.env}`">{{ envLabel(system.env) }}</span>
    </td>
    <td class="col-status">
      <span class="status-line" :class="`status-line--${system.status}`">
        <span class="status-dot" aria-hidden="true" />
        {{ statusLabel(system.status) }}
      </span>
    </td>
    <td class="col-calls">{{ formatCalls(system.todayCalls) }}</td>
    <td class="col-err">
      <span :class="['err-rate', errorRateClass(system.errorRate)]">{{ formatErrorRate(system.errorRate) }}</span>
    </td>
    <td class="col-last">
      <span class="last-text">{{ system.lastAccess }}</span>
    </td>
    <td class="col-actions">
      <div class="action-btns">
        <button type="button" class="icon-action" aria-label="监控" @click.stop>
          <MonitorOutlined />
        </button>
        <button type="button" class="icon-action" aria-label="编辑" @click.stop>
          <EditOutlined />
        </button>
        <button type="button" class="icon-action" aria-label="更多" @click.stop>
          <EllipsisOutlined />
        </button>
      </div>
    </td>
  </tr>
</template>

<script setup lang="ts">
import { EditOutlined, EllipsisOutlined, MonitorOutlined } from '@ant-design/icons-vue'
import type { Component } from 'vue'

type SystemStatus = 'online' | 'offline' | 'maintenance'
type SystemEnv = 'prod' | 'pre' | 'test' | 'dev'
type IconTheme = 'blue' | 'green' | 'cyan' | 'amber'
type OwnerDot = 'blue' | 'green' | 'violet' | 'orange'

type OnlineSystem = {
  id: string
  name: string
  slug: string
  ownerName: string
  ownerDot: OwnerDot
  env: SystemEnv
  status: SystemStatus
  todayCalls: number
  errorRate: number
  lastAccess: string
  icon: Component
  iconTheme: IconTheme
}

defineProps<{
  system: OnlineSystem
}>()

const envLabel = (env: SystemEnv): string => {
  switch (env) {
    case 'prod':
      return '生产环境'
    case 'pre':
      return '预生产环境'
    case 'test':
      return '测试环境'
    case 'dev':
      return '开发环境'
    default:
      return env
  }
}

const statusLabel = (status: SystemStatus): string => {
  switch (status) {
    case 'online':
      return '在线'
    case 'offline':
      return '离线'
    case 'maintenance':
      return '维护中'
    default:
      return status
  }
}

const formatCalls = (n: number): string => {
  if (n === 0) return '0'
  return n.toLocaleString('zh-CN')
}

const formatErrorRate = (pct: number): string => `${pct.toFixed(2)}%`

const errorRateClass = (pct: number): string => {
  return pct >= 0.5 ? 'err-rate--warn' : 'err-rate--ok'
}
</script>

<style scoped>
.biz-row td {
  padding: 10px 8px;
  vertical-align: middle;
  border-bottom: 1px solid #f1f5f9;
  color: #334155;
}

.name-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.icon-swatch {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-swatch--blue {
  background: #dbeafe;
  color: #2563eb;
}

.icon-swatch--green {
  background: #d1fae5;
  color: #059669;
}

.icon-swatch--cyan {
  background: #cffafe;
  color: #0891b2;
}

.icon-swatch--amber {
  background: #fef3c7;
  color: #d97706;
}

.icon-swatch__ico {
  font-size: 16px;
}

.name-text {
  font-weight: 600;
  color: #1e293b;
}

.slug-text {
  color: #94a3b8;
  font-size: 13px;
}

.owner-line {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.owner-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex-shrink: 0;
}

.owner-dot--blue {
  background: #3b82f6;
}

.owner-dot--green {
  background: #10b981;
}

.owner-dot--violet {
  background: #8b5cf6;
}

.owner-dot--orange {
  background: #f97316;
}

.env-tag {
  display: inline-block;
  padding: 2px 10px;
  font-size: 12px;
  font-weight: 500;
  border-radius: 10px;
}

.env-tag--prod {
  background: #d1fae5;
  color: #047857;
}

.env-tag--pre {
  background: #dbeafe;
  color: #1d4ed8;
}

.env-tag--test {
  background: #ede9fe;
  color: #6d28d9;
}

.env-tag--dev {
  background: #ffedd5;
  color: #c2410c;
}

.status-line {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex-shrink: 0;
}

.status-line--online .status-dot {
  background: #10b981;
}

.status-line--online {
  color: #047857;
}

.status-line--offline .status-dot {
  background: #94a3b8;
}

.status-line--offline {
  color: #64748b;
}

.status-line--maintenance .status-dot {
  background: #f97316;
}

.status-line--maintenance {
  color: #c2410c;
}

.err-rate {
  font-variant-numeric: tabular-nums;
}

.err-rate--ok {
  color: #059669;
}

.err-rate--warn {
  color: #ea580c;
}

.last-text {
  color: #94a3b8;
  font-size: 13px;
}

.col-actions {
  text-align: right;
}

.action-btns {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  justify-content: flex-end;
}

.icon-action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  padding: 0;
  border: none;
  border-radius: 10px;
  background: transparent;
  color: #94a3b8;
  cursor: pointer;
  transition: background 0.15s, color 0.15s;
}

.icon-action:hover {
  background: #f1f5f9;
  color: #64748b;
}

.icon-action :deep(.anticon) {
  font-size: 15px;
}
</style>
