<template>
  <div class="stack">
    <section class="panel-card">
      <div class="panel-head">
        <DashboardOutlined class="head-icon" />
        <h3 class="panel-title">资源使用概览</h3>
      </div>
      <div class="usage-rows">
        <div class="usage-row">
          <div class="usage-row-top">
            <span class="usage-label-wrap">
              <ApiOutlined class="usage-ico usage-ico--blue" />
              <span class="usage-label">API 调用配额</span>
            </span>
            <span class="usage-numbers">
              {{ fmtNum(resourceUsage.apiCalls.used) }} / {{ fmtNum(resourceUsage.apiCalls.limit) }}
            </span>
            <span class="usage-pct">{{ apiPct }}%</span>
          </div>
          <div class="bar-track">
            <div class="bar-fill bar-fill--blue" :style="{ width: `${apiPct}%` }" />
          </div>
        </div>
        <div class="usage-row">
          <div class="usage-row-top">
            <span class="usage-label-wrap">
              <ThunderboltOutlined class="usage-ico usage-ico--violet" />
              <span class="usage-label">并发连接数</span>
            </span>
            <span class="usage-numbers">
              {{ fmtNum(resourceUsage.concurrency.used) }} / {{ fmtNum(resourceUsage.concurrency.limit) }}
            </span>
            <span class="usage-pct">{{ concPct }}%</span>
          </div>
          <div class="bar-track">
            <div class="bar-fill bar-fill--violet" :style="{ width: `${concPct}%` }" />
          </div>
        </div>
        <div class="usage-row">
          <div class="usage-row-top">
            <span class="usage-label-wrap">
              <HddOutlined class="usage-ico usage-ico--green" />
              <span class="usage-label">存储空间</span>
            </span>
            <span class="usage-numbers">
              {{ resourceUsage.storage.usedGB }} GB / {{ resourceUsage.storage.limitGB }} GB
            </span>
            <span class="usage-pct">{{ storagePct }}%</span>
          </div>
          <div class="bar-track">
            <div class="bar-fill bar-fill--green" :style="{ width: `${storagePct}%` }" />
          </div>
        </div>
      </div>
    </section>

    <section class="panel-card panel-card--grow">
      <div class="panel-head panel-head--between">
        <div class="panel-head-left">
          <HistoryOutlined class="head-icon" />
          <h3 class="panel-title">最近变更</h3>
        </div>
        <button type="button" class="link-btn" @click="emit('view-all-changes')">查看全部 →</button>
      </div>
      <ul class="change-list">
        <li v-for="(row, i) in recentChanges.items" :key="i" class="change-row">
          <div class="change-icon" :class="`change-icon--${(i % 4) + 1}`">
            <component :is="row.icon" />
          </div>
          <div class="change-main">
            <div class="change-title-line">
              <span class="change-title">{{ row.title }}</span>
              <span class="change-time">{{ row.timeText }}</span>
            </div>
            <span class="env-tag" :class="`env-tag--${row.env}`">{{ envLabel(row.env) }}</span>
          </div>
        </li>
      </ul>
    </section>
  </div>
</template>

<script setup lang="ts">
import {
  ApiOutlined,
  DashboardOutlined,
  HistoryOutlined,
  HddOutlined,
  ThunderboltOutlined
} from '@ant-design/icons-vue'
import { computed } from 'vue'
import type { Component } from 'vue'

type SystemEnv = 'prod' | 'pre' | 'test' | 'dev'

type ResourceUsagePanel = {
  apiCalls: { used: number; limit: number }
  concurrency: { used: number; limit: number }
  storage: { usedGB: number; limitGB: number }
}

type RecentChangeItem = {
  title: string
  env: SystemEnv
  timeText: string
  icon: Component
}

type RecentChanges = {
  items: RecentChangeItem[]
}

const props = defineProps<{
  resourceUsage: ResourceUsagePanel
  recentChanges: RecentChanges
}>()

const emit = defineEmits<{
  (e: 'view-all-changes'): void
}>()

const pct = (used: number, limit: number) =>
  limit <= 0 ? 0 : Math.min(100, Math.round((used / limit) * 1000) / 10)

const apiPct = computed(() => pct(props.resourceUsage.apiCalls.used, props.resourceUsage.apiCalls.limit))
const concPct = computed(() =>
  pct(props.resourceUsage.concurrency.used, props.resourceUsage.concurrency.limit)
)
const storagePct = computed(() =>
  pct(props.resourceUsage.storage.usedGB, props.resourceUsage.storage.limitGB)
)

const fmtNum = (n: number) => n.toLocaleString('zh-CN')

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
</script>

<style scoped>
.stack {
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
  min-height: 0;
}

.panel-card {
  background: #fff;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  padding: 14px;
  flex-shrink: 0;
}

.panel-card--grow {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.panel-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.panel-head--between {
  justify-content: space-between;
  gap: 12px;
}

.panel-head-left {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.head-icon {
  font-size: 18px;
  color: #94a3b8;
  flex-shrink: 0;
}

.panel-title {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
}

.link-btn {
  flex-shrink: 0;
  border: none;
  background: none;
  padding: 0;
  font-size: 12px;
  font-weight: 600;
  color: #3b82f6;
  cursor: pointer;
}

.link-btn:hover {
  text-decoration: underline;
}

.usage-rows {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.usage-row-top {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
  font-size: 12px;
}

.usage-label-wrap {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 1;
  min-width: 0;
}

.usage-ico {
  font-size: 14px;
  flex-shrink: 0;
}

.usage-ico--blue {
  color: #3b82f6;
}

.usage-ico--violet {
  color: #8b5cf6;
}

.usage-ico--green {
  color: #10b981;
}

.usage-label {
  color: #64748b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.usage-numbers {
  color: #334155;
  font-weight: 600;
  font-variant-numeric: tabular-nums;
  white-space: nowrap;
}

.usage-pct {
  color: #94a3b8;
  font-size: 11px;
  width: 40px;
  text-align: right;
  flex-shrink: 0;
}

.bar-track {
  height: 6px;
  border-radius: 10px;
  background: #f1f5f9;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 10px;
  transition: width 0.35s ease;
}

.bar-fill--blue {
  background: linear-gradient(90deg, #60a5fa, #3b82f6);
}

.bar-fill--violet {
  background: linear-gradient(90deg, #c4b5fd, #8b5cf6);
}

.bar-fill--green {
  background: linear-gradient(90deg, #6ee7b7, #10b981);
}

.change-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
  overflow-y: auto;
  flex: 1;
  min-height: 0;
}

.change-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.change-icon {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.change-icon--1 {
  background: #dbeafe;
  color: #2563eb;
}

.change-icon--2 {
  background: #ede9fe;
  color: #7c3aed;
}

.change-icon--3 {
  background: #d1fae5;
  color: #059669;
}

.change-icon--4 {
  background: #ffedd5;
  color: #ea580c;
}

.change-main {
  flex: 1;
  min-width: 0;
}

.change-title-line {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 6px;
}

.change-title {
  font-size: 13px;
  font-weight: 600;
  color: #1e293b;
}

.change-time {
  font-size: 11px;
  color: #94a3b8;
  flex-shrink: 0;
}

.env-tag {
  display: inline-block;
  padding: 2px 8px;
  font-size: 11px;
  font-weight: 600;
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
</style>
