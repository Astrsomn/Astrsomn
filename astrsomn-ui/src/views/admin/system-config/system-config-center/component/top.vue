<template>
  <section class="stats-grid">
    <article v-for="item in overviewStats" :key="item.label" class="stat-card">
      <div class="stat-head">
        <component :is="item.icon" class="stat-icon" />
        <div class="stat-label">{{ item.label }}</div>
      </div>
      <div class="stat-value">{{ item.value }}</div>
      <div class="stat-trend" :class="`trend-${item.trend}`">
        {{ item.hint }}
      </div>
    </article>
  </section>
</template>

<script setup lang="ts">
import type { Component } from 'vue'

type Trend = 'up' | 'down' | 'flat'

type OverviewStat = {
  label: string
  value: string
  hint: string
  trend: Trend
  icon: Component
}

defineProps<{
  overviewStats: OverviewStat[]
}>()
</script>

<style scoped>
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.stat-card {
  border: 1px solid var(--border-default);
  border-radius: 14px;
  background: var(--bg-card);
  padding: 12px 14px;
}

.stat-head {
  display: flex;
  align-items: center;
  gap: 6px;
}

.stat-icon {
  color: var(--primary);
  font-size: 14px;
}

.stat-label {
  color: var(--text-secondary);
  font-size: 12px;
}

.stat-value {
  margin-top: 6px;
  font-size: 24px;
  line-height: 1;
  font-weight: 700;
  color: var(--text-heading);
}

.stat-trend {
  margin-top: 8px;
  font-size: 11px;
}

.trend-up {
  color: var(--success, #2e9f5d);
}

.trend-down {
  color: var(--warning, #dd4b39);
}

.trend-flat {
  color: var(--text-secondary);
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
