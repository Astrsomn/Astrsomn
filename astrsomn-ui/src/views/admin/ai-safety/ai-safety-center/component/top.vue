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
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  background: #ffffff;
  padding: 24px;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.05), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
}

.stat-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.stat-icon {
  color: #3b82f6;
  font-size: 16px;
}

.stat-label {
  font-size: 12px;
  font-weight: 600;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.stat-value {
  font-size: 30px;
  line-height: 1;
  font-weight: 700;
  color: #1e293b;
  letter-spacing: -0.02em;
}

.stat-hint {
  margin-top: 8px;
  font-size: 11px;
  font-weight: 600;
}

.trend-up {
  color: #10b981;
}

.trend-down {
  color: #f97316;
}

.trend-flat {
  color: #94a3b8;
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
