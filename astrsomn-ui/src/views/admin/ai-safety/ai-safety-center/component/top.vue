<template>
  <section class="stats-grid">
    <article v-for="item in safetyStats" :key="item.label" class="stat-card">
      <div class="stat-head">
        <component :is="item.icon" class="stat-icon" />
        <span class="stat-label">{{ item.label }}</span>
      </div>
      <div class="stat-value">{{ item.value }}</div>
      <div class="stat-hint" :class="`trend-${item.trend}`">{{ item.hint }}</div>
    </article>
  </section>
</template>

<script setup lang="ts">
import type { Component } from 'vue'

type Trend = 'up' | 'down' | 'flat'

type SafetyStat = {
  label: string
  value: string
  hint: string
  trend: Trend
  icon: Component
}

defineProps<{
  safetyStats: SafetyStat[]
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
  padding: 16px;
}

.stat-head {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stat-icon {
  color: var(--primary);
  font-size: 16px;
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
}

.stat-value {
  margin-top: 10px;
  font-size: 28px;
  line-height: 1;
  font-weight: 700;
  color: var(--text-heading);
}

.stat-hint {
  margin-top: 10px;
  font-size: 12px;
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

@media (max-width: 1100px) {
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
