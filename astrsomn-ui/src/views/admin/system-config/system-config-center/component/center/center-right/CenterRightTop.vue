<template>
  <div class="panel-card">
    <div class="panel-head">
      <PieChartOutlined class="head-icon" />
      <h3 class="panel-title">环境分布</h3>
    </div>
    <div class="panel-body">
      <div class="donut-wrap">
        <div class="donut-ring" :style="{ background: conicGradient }" aria-hidden="true" />
        <div class="donut-center">
          <span class="donut-total">{{ distribution.total }}</span>
          <span class="donut-label">总量</span>
        </div>
      </div>
      <ul class="legend">
        <li v-for="(row, i) in distribution.items" :key="i" class="legend-row">
          <span class="legend-dot" :class="`dot--${row.colorToken}`" />
          <span class="legend-name">{{ row.label }}</span>
          <span class="legend-meta">
            <span class="legend-val">{{ row.value }}</span>
            <span class="legend-pct">{{ row.pct }}%</span>
          </span>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { PieChartOutlined } from '@ant-design/icons-vue'
import { computed } from 'vue'

type DistColorToken = 'green' | 'cyan' | 'violet' | 'amber'

type EnvDistribution = {
  total: number
  items: Array<{
    label: string
    value: number
    pct: number
    colorToken: DistColorToken
  }>
}

const props = defineProps<{
  distribution: EnvDistribution
}>()

const COLOR_MAP: Record<DistColorToken, string> = {
  green: '#22c55e',
  cyan: '#38bdf8',
  violet: '#a78bfa',
  amber: '#fb923c'
}

const conicGradient = computed(() => {
  let acc = 0
  const stops: string[] = []
  for (const row of props.distribution.items) {
    const start = acc
    acc += (row.pct / 100) * 360
    const c = COLOR_MAP[row.colorToken]
    stops.push(`${c} ${start}deg ${acc}deg`)
  }
  return `conic-gradient(from -90deg, ${stops.join(', ')})`
})
</script>

<style scoped>
.panel-card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-default);
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  padding: 14px 14px 12px;
  flex-shrink: 0;
}

.panel-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.head-icon {
  font-size: 18px;
  color: var(--text-muted);
}

.panel-title {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: var(--text-heading);
}

.panel-body {
  display: flex;
  align-items: center;
  gap: 12px;
}

.donut-wrap {
  position: relative;
  width: 132px;
  height: 132px;
  flex-shrink: 0;
}

.donut-ring {
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

.donut-center {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 58%;
  height: 58%;
  border-radius: 50%;
  background: var(--bg-card);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: inset 0 0 0 1px var(--border-default);
}

.donut-total {
  font-size: 22px;
  font-weight: 800;
  color: var(--text-heading);
  line-height: 1.1;
}

.donut-label {
  font-size: 11px;
  color: var(--text-muted);
  margin-top: 2px;
}

.legend {
  list-style: none;
  margin: 0;
  padding: 0;
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.legend-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.dot--green {
  background: #22c55e;
}

.dot--cyan {
  background: #38bdf8;
}

.dot--violet {
  background: #a78bfa;
}

.dot--amber {
  background: #fb923c;
}

.legend-name {
  flex: 1;
  min-width: 0;
  color: var(--text-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.legend-meta {
  display: flex;
  align-items: baseline;
  gap: 8px;
  flex-shrink: 0;
}

.legend-val {
  font-weight: 700;
  color: var(--text-heading);
}

.legend-pct {
  font-size: 11px;
  color: var(--text-muted);
}
</style>
