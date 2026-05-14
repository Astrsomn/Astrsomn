<template>
  <tr class="biz-row">
    <td class="col-name">
      <div class="name-cell">
        <div :class="`icon-swatch--${system.iconTheme}`" class="icon-swatch">
          <component :is="system.icon" class="icon-swatch__ico"/>
        </div>
        <span class="name-text">{{ system.name }}</span>
      </div>
    </td>
    <td class="col-slug">
      <span class="slug-text">{{ system.slug }}</span>
    </td>
    <td class="col-owner">
      <span class="owner-line">
        <span :class="`owner-dot--${system.ownerDot}`" aria-hidden="true" class="owner-dot"/>
        {{ system.ownerName }}
      </span>
    </td>
    <td class="col-env">
      <span :class="`env-tag--${system.env}`" class="env-tag">{{ envLabel(system.env) }}</span>
    </td>
    <td class="col-status">
      <span :class="`status-line--${system.status}`" class="status-line">
        <span aria-hidden="true" class="status-dot"/>
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
        <button aria-label="监控" class="icon-action" type="button" @click.stop>
          <MonitorOutlined/>
        </button>
        <button aria-label="编辑" class="icon-action" type="button" @click.stop>
          <EditOutlined/>
        </button>
        <button aria-label="更多" class="icon-action" type="button" @click.stop>
          <EllipsisOutlined/>
        </button>
      </div>
    </td>
  </tr>
</template>

<script lang="ts" setup>
import {EditOutlined, EllipsisOutlined, MonitorOutlined} from '@ant-design/icons-vue'
import type {Component} from 'vue'

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
  border-bottom: 1px solid var(--border-default);
  color: var(--text-primary);
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
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-swatch--blue {
  background: rgba(59, 130, 246, 0.15);
  color: var(--primary);
}

.icon-swatch--green {
  background: rgba(16, 185, 129, 0.15);
  color: var(--success);
}

.icon-swatch--cyan {
  background: rgba(56, 189, 248, 0.15);
  color: #38bdf8;
}

.icon-swatch--amber {
  background: rgba(245, 158, 11, 0.15);
  color: #fb923c;
}

.icon-swatch__ico {
  font-size: 16px;
}

.name-text {
  font-weight: 600;
  color: var(--text-heading);
}

.slug-text {
  color: var(--text-muted);
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
  background: var(--primary);
}

.owner-dot--green {
  background: var(--success);
}

.owner-dot--violet {
  background: #a78bfa;
}

.owner-dot--orange {
  background: #fb923c;
}

.env-tag {
  display: inline-block;
  padding: 2px 10px;
  font-size: 12px;
  font-weight: 500;
  border-radius: 10px;
}

.env-tag--prod {
  background: rgba(16, 185, 129, 0.12);
  color: var(--success);
}

.env-tag--pre {
  background: rgba(59, 130, 246, 0.12);
  color: var(--primary);
}

.env-tag--test {
  background: rgba(139, 92, 246, 0.12);
  color: #a78bfa;
}

.env-tag--dev {
  background: rgba(251, 146, 60, 0.12);
  color: #fb923c;
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
  background: var(--success);
}

.status-line--online {
  color: var(--success);
}

.status-line--offline .status-dot {
  background: var(--text-muted);
}

.status-line--offline {
  color: var(--text-muted);
}

.status-line--maintenance .status-dot {
  background: #fb923c;
}

.status-line--maintenance {
  color: #fb923c;
}

.err-rate {
  font-variant-numeric: tabular-nums;
}

.err-rate--ok {
  color: var(--success);
}

.err-rate--warn {
  color: #fb923c;
}

.last-text {
  color: var(--text-muted);
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
  border-radius: var(--radius-md);
  background: transparent;
  color: var(--text-muted);
  cursor: pointer;
  transition: background 0.15s, color 0.15s;
}

.icon-action:hover {
  background: var(--bg-input);
  color: var(--text-secondary);
}

.icon-action :deep(.anticon) {
  font-size: 15px;
}
</style>
