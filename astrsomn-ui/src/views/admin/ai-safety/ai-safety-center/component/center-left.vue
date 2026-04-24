<template>
  <div class="panel-card">
    <div class="panel-head">
      <h3>调用链路状态看板</h3>
    </div>
    <div class="trace-overview-grid">
      <article v-for="item in traceStats" :key="item.label" class="trace-overview-item">
        <div class="trace-overview-head">
          <component :is="item.icon" />
          <span>{{ item.label }}</span>
        </div>
        <strong>{{ item.value }}</strong>
        <div class="trace-overview-hint">{{ item.hint }}</div>
      </article>
    </div>
    <div class="trace-detail-list">
      <article v-for="trace in traceCases" :key="trace.name" class="trace-detail-item">
        <div class="trace-detail-top">
          <span class="trace-detail-name">{{ trace.name }}</span>
          <span class="trace-detail-status" :class="`status-${trace.status}`">
            <span class="status-dot"></span>
            {{ trace.statusText }}
          </span>
        </div>
        <div class="trace-detail-meta">
          <span>平均耗时 {{ trace.cost }}</span>
          <span>最近更新 {{ trace.updatedAt }}</span>
        </div>
      </article>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Component } from 'vue'

type TraceStat = {
  label: string
  value: string
  hint: string
  icon: Component
}

type TraceCase = {
  name: string
  status: 'success' | 'failed' | 'running'
  statusText: string
  cost: string
  updatedAt: string
}

defineProps<{
  traceStats: TraceStat[]
  traceCases: TraceCase[]
}>()
</script>

<style scoped>
.panel-card {
  border: 1px solid var(--border-default);
  border-radius: 14px;
  background: var(--bg-card);
  padding: 20px;
}

.panel-head {
  margin-bottom: 14px;
}

.panel-head h3 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-heading);
}

.trace-overview-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.trace-overview-item {
  border: 1px solid var(--border-default);
  border-radius: 10px;
  padding: 12px;
  background: color-mix(in srgb, var(--bg-card) 88%, var(--bg-base));
}

.trace-overview-head {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--text-secondary);
  font-size: 12px;
}

.trace-overview-item strong {
  display: block;
  margin-top: 8px;
  font-size: 24px;
  line-height: 1;
  color: var(--text-heading);
}

.trace-overview-hint {
  margin-top: 8px;
  font-size: 12px;
  color: var(--text-secondary);
}

.trace-detail-list {
  margin-top: 12px;
  display: grid;
  gap: 10px;
}

.trace-detail-item {
  border: 1px solid var(--border-default);
  border-radius: 10px;
  background: var(--bg-card);
  padding: 12px;
}

.trace-detail-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.trace-detail-name {
  font-size: 13px;
  color: var(--text-heading);
}

.trace-detail-status {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  border-radius: 999px;
  border: 1px solid transparent;
  padding: 2px 8px;
  font-size: 12px;
}

.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 999px;
  background: currentColor;
}

.status-success {
  color: #2e9f5d;
  background: color-mix(in srgb, #2e9f5d 12%, transparent);
  border-color: color-mix(in srgb, #2e9f5d 24%, transparent);
}

.status-failed {
  color: #dd4b39;
  background: color-mix(in srgb, #dd4b39 12%, transparent);
  border-color: color-mix(in srgb, #dd4b39 24%, transparent);
}

.status-running {
  color: #f39c12;
  background: color-mix(in srgb, #f39c12 12%, transparent);
  border-color: color-mix(in srgb, #f39c12 24%, transparent);
}

.trace-detail-meta {
  margin-top: 6px;
  display: flex;
  justify-content: space-between;
  gap: 10px;
  font-size: 12px;
  color: var(--text-secondary);
}

@media (max-width: 1100px) {
  .trace-overview-grid {
    grid-template-columns: 1fr;
  }
}
</style>