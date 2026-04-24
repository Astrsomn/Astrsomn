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
        <div class="trace-detail-content">
          <div class="trace-detail-name">{{ trace.name }}</div>
          <div class="trace-detail-cost">平均耗时 {{ trace.cost }}</div>
        </div>
        <div class="trace-detail-right">
          <span class="trace-detail-status" :class="`status-${trace.status}`">
            <span class="status-dot"></span>
            {{ trace.statusText }}
          </span>
          <span class="trace-detail-updated">最近更新 {{ trace.updatedAt }}</span>
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
  border: 1px solid #e2e8f0;
  border-radius: 30px;
  background: #ffffff;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.05), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
}

.panel-head {
  padding: 24px 24px 0;
  border-bottom: 1px solid #f1f5f9;
}

.panel-head h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.trace-overview-grid {
  padding: 24px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.trace-overview-item {
  border: 1px solid #f1f5f9;
  border-radius: 20px;
  padding: 20px;
  background: rgba(248, 250, 252, 0.2);
}

.trace-overview-head {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #94a3b8;
  font-size: 11px;
  margin-bottom: 8px;
}

.trace-overview-item strong {
  display: block;
  font-size: 24px;
  line-height: 1;
  font-weight: 700;
  color: #1e293b;
}

.trace-overview-hint {
  margin-top: 4px;
  font-size: 10px;
  color: #94a3b8;
}

.trace-detail-list {
  padding: 0 24px 24px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.trace-detail-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border: 1px solid transparent;
  border-radius: 12px;
  transition: all 0.2s ease;
  cursor: pointer;
}

.trace-detail-item:hover {
  border-color: #f1f5f9;
  background: #f8fafc;
}

.trace-detail-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.trace-detail-name {
  font-size: 14px;
  font-weight: 600;
  color: #475569;
}

.trace-detail-cost {
  font-size: 11px;
  font-weight: 500;
  color: #94a3b8;
}

.trace-detail-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.trace-detail-status {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border-radius: 6px;
  padding: 2px 8px;
  font-size: 10px;
  font-weight: 600;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.status-success {
  color: #10b981;
  background: #ecfdf5;
}

.status-failed {
  color: #ef4444;
  background: #fef2f2;
}

.status-running {
  color: #f97316;
  background: #fff7ed;
}

.trace-detail-updated {
  font-size: 10px;
  font-weight: 500;
  font-style: italic;
  color: #94a3b8;
}

@media (max-width: 1200px) {
  .trace-overview-grid {
    grid-template-columns: 1fr;
  }
}
</style>