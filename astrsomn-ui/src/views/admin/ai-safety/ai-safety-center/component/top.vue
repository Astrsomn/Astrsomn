<template>
  <section class="stats-grid">
    <article
      v-for="(item, index) in safetyStats"
      :key="item.label"
      class="stat-card"
    >
      <div class="icon-container" :class="`icon-${(index % 4) + 1}`">
        <component :is="item.icon" />
      </div>
      <div class="stat-body">
        <h3 class="card-title">{{ item.label }}</h3>
        <div class="stat-value-row">
          <span class="stat-value">{{ item.value }}</span>
        </div>
        <p class="card-desc" :class="`trend-${item.trend}`">{{ item.hint }}</p>
      </div>
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
/* 与 system-config Center 快捷入口对齐：4 列、卡片高度与图标尺寸一致 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.stat-card {
  box-sizing: border-box;
  min-height: 100px;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  background: #ffffff;
  padding: 20px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  gap: 16px;
}

.icon-container {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}

.icon-1 {
  background: #dbeafe;
  color: #3b82f6;
}

.icon-2 {
  background: #d1fae5;
  color: #10b981;
}

.icon-3 {
  background: #fef3c7;
  color: #f59e0b;
}

.icon-4 {
  background: #ede9fe;
  color: #8b5cf6;
}

.stat-body {
  min-width: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 2px;
}

.card-title {
  font-weight: 700;
  color: #1e293b;
  font-size: 14px;
  margin: 0;
  line-height: 1.3;
}

.stat-value-row {
  line-height: 1.15;
}

.stat-value {
  font-size: 22px;
  font-weight: 800;
  color: #1e293b;
  letter-spacing: -0.02em;
}

.card-desc {
  font-size: 11px;
  font-weight: 600;
  margin: 4px 0 0 0;
  line-height: 1.3;
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
