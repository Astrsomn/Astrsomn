<template>
  <div class="right-stack">
    <!-- Environment Distribution -->
    <section class="panel-card">
      <div class="panel-head">
        <PieChartOutlined class="head-icon" />
        <h3 class="panel-title">{{ t.panels.envDistribution }}</h3>
      </div>
      <div class="panel-body panel-body--row">
        <div class="donut-wrap">
          <div :style="{ background: conicGradient }" aria-hidden="true" class="donut-ring" />
          <div class="donut-center">
            <span class="donut-total">{{ envDistribution.total }}</span>
            <span class="donut-label">{{ t.panels.total }}</span>
          </div>
        </div>
        <ul class="legend">
          <li v-for="(row, i) in envDistribution.items" :key="i" class="legend-row">
            <span :class="`legend-dot legend-dot--${row.colorToken}`" />
            <span class="legend-name">{{ row.label }}</span>
            <span class="legend-meta">
              <span class="legend-val">{{ row.value }}</span>
              <span class="legend-pct">{{ row.pct }}%</span>
            </span>
          </li>
        </ul>
      </div>
    </section>

    <!-- Resource Usage -->
    <section class="panel-card">
      <div class="panel-head">
        <DashboardOutlined class="head-icon" />
        <h3 class="panel-title">{{ t.panels.resourceUsage }}</h3>
      </div>
      <div class="usage-rows">
        <div v-for="bar in resourceBars" :key="bar.key" class="usage-row">
          <div class="usage-top">
            <span class="usage-label-wrap">
              <component :is="bar.icon" :class="`usage-ico usage-ico--${bar.color}`" />
              <span class="usage-label">{{ bar.label }}</span>
            </span>
            <span class="usage-numbers">{{ bar.usedText }} / {{ bar.limitText }}</span>
            <span class="usage-pct">{{ bar.pct }}%</span>
          </div>
          <div class="bar-track">
            <div
              :class="`bar-fill bar-fill--${bar.color}`"
              :style="{ width: `${bar.pct}%` }"
            />
          </div>
        </div>
      </div>
    </section>

    <!-- Recent Changes -->
    <section class="panel-card panel-card--grow">
      <div class="panel-head panel-head--between">
        <div class="panel-head-left">
          <HistoryOutlined class="head-icon" />
          <h3 class="panel-title">{{ t.panels.recentChanges }}</h3>
        </div>
        <button class="link-btn" type="button" @click="emit('view-all-changes')">
          {{ t.dashboard.viewAllChanges }}
        </button>
      </div>

      <ul v-if="recentChanges.items.length > 0" class="change-list">
        <li v-for="(row, i) in recentChanges.items" :key="i" class="change-row">
          <div :class="`change-icon change-icon--${changeColorKeys[i % 4]}`">
            <component :is="row.icon" />
          </div>
          <div class="change-main">
            <div class="change-top">
              <span class="change-title">{{ row.title }}</span>
              <span class="change-time">{{ row.timeText }}</span>
            </div>
            <span :class="`env-badge env-badge--${row.env}`">{{ envLabel(row.env) }}</span>
          </div>
        </li>
      </ul>
      <div v-else class="change-empty">
        <span class="change-empty-text">{{ t.panels.noChanges }}</span>
      </div>
    </section>
  </div>
</template>

<script lang="ts" setup>
import {
  ApiOutlined,
  DashboardOutlined,
  HddOutlined,
  HistoryOutlined,
  PieChartOutlined,
  ThunderboltOutlined,
} from '@ant-design/icons-vue'
import type {Component} from 'vue'
import {computed} from 'vue'
import {usePageTranslation} from '@/locales/pages'
import {useDictionary} from '@/locales/dictionary/registry'
import type {
  DistColorToken,
  EnvDistribution,
  RecentChanges,
  ResourceUsagePanel,
  SystemEnvUi,
} from '../useSystemConfigCenter'

const props = defineProps<{
  envDistribution: EnvDistribution
  resourceUsage: ResourceUsagePanel
  recentChanges: RecentChanges
}>()

const emit = defineEmits<{
  (e: 'view-all-changes'): void
}>()

const t = usePageTranslation('system-config-center')
const envDict = useDictionary('system.config-center.env')

const envLabel = (env: SystemEnvUi): string => {
  const code = env === 'prod' ? 'PROD' : env === 'pre' ? 'PRE' : env === 'test' ? 'TEST' : 'DEV'
  return envDict.value.getLabel(code) ?? code
}

/* ---- Donut Chart ---- */
const DONUT_COLORS: Record<DistColorToken, string> = {
  green: '#22c55e',
  cyan: '#38bdf8',
  violet: '#a78bfa',
  amber: '#fb923c',
}

const conicGradient = computed(() => {
  let acc = 0
  const stops: string[] = []
  for (const row of props.envDistribution.items) {
    const start = acc
    acc += (row.pct / 100) * 360
    const c = DONUT_COLORS[row.colorToken]
    stops.push(`${c} ${start}deg ${acc}deg`)
  }
  return `conic-gradient(from -90deg, ${stops.join(', ')})`
})

/* ---- Resource Bars ---- */
const fmtNum = (n: number) => n.toLocaleString('en-US')

const pct = (used: number, limit: number) =>
  limit <= 0 ? 0 : Math.min(100, Math.round((used / limit) * 1000) / 10)

const resourceBars = computed(() => [
  {
    key: 'api',
    label: t.value.panels.apiCalls,
    icon: ApiOutlined,
    color: 'blue',
    usedText: fmtNum(props.resourceUsage.apiCalls.used),
    limitText: fmtNum(props.resourceUsage.apiCalls.limit),
    pct: pct(props.resourceUsage.apiCalls.used, props.resourceUsage.apiCalls.limit),
  },
  {
    key: 'concurrency',
    label: t.value.panels.concurrency,
    icon: ThunderboltOutlined,
    color: 'violet',
    usedText: fmtNum(props.resourceUsage.concurrency.used),
    limitText: fmtNum(props.resourceUsage.concurrency.limit),
    pct: pct(props.resourceUsage.concurrency.used, props.resourceUsage.concurrency.limit),
  },
  {
    key: 'storage',
    label: t.value.panels.storage,
    icon: HddOutlined,
    color: 'green',
    usedText: `${props.resourceUsage.storage.usedGB} GB`,
    limitText: `${props.resourceUsage.storage.limitGB} GB`,
    pct: pct(props.resourceUsage.storage.usedGB, props.resourceUsage.storage.limitGB),
  },
])

const changeColorKeys = ['blue', 'violet', 'green', 'amber'] as const
</script>

<style scoped>
.right-stack {
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
  min-height: 0;
}

/* ---- Panel Card ---- */
.panel-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-card);
  padding: 14px 16px;
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
  margin-bottom: 12px;
}

.panel-head--between {
  justify-content: space-between;
}

.panel-head-left {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.head-icon {
  font-size: 17px;
  color: var(--text-muted);
  flex-shrink: 0;
}

.panel-title {
  margin: 0;
  font-size: 15px;
  font-weight: 700;
  color: var(--text-heading);
}

.link-btn {
  flex-shrink: 0;
  border: none;
  background: none;
  padding: 0;
  font-size: 12px;
  font-weight: 600;
  color: var(--primary);
  cursor: pointer;
  transition: opacity 0.2s;
}

.link-btn:hover {
  opacity: 0.8;
}

.panel-body--row {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* ---- Donut ---- */
.donut-wrap {
  position: relative;
  width: 120px;
  height: 120px;
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
  font-size: 20px;
  font-weight: 800;
  color: var(--text-heading);
  line-height: 1.1;
}

.donut-label {
  font-size: 10px;
  color: var(--text-muted);
  margin-top: 1px;
}

/* ---- Legend ---- */
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

.legend-dot--green { background: #22c55e; }
.legend-dot--cyan { background: #38bdf8; }
.legend-dot--violet { background: #a78bfa; }
.legend-dot--amber { background: #fb923c; }

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

/* ---- Usage Bars ---- */
.usage-rows {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.usage-top {
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
  font-size: 13px;
  flex-shrink: 0;
}

.usage-ico--blue { color: var(--primary); }
.usage-ico--violet { color: #a78bfa; }
.usage-ico--green { color: var(--success); }

.usage-label {
  color: var(--text-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.usage-numbers {
  color: var(--text-primary);
  font-weight: 600;
  font-variant-numeric: tabular-nums;
  white-space: nowrap;
}

.usage-pct {
  color: var(--text-muted);
  font-size: 11px;
  width: 38px;
  text-align: right;
  flex-shrink: 0;
}

.bar-track {
  height: 6px;
  border-radius: 10px;
  background: var(--bg-input);
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 10px;
  transition: width 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.bar-fill--blue {
  background: linear-gradient(90deg, rgba(96,165,250,0.7), var(--primary));
}

.bar-fill--violet {
  background: linear-gradient(90deg, rgba(196,181,253,0.7), #a78bfa);
}

.bar-fill--green {
  background: linear-gradient(90deg, rgba(110,231,183,0.7), var(--success));
}

/* ---- Changes List ---- */
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
  width: 34px;
  height: 34px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
}

.change-icon--blue { background: rgba(59,130,246,0.12); color: var(--primary); }
.change-icon--violet { background: rgba(139,92,246,0.12); color: #a78bfa; }
.change-icon--green { background: rgba(16,185,129,0.12); color: var(--success); }
.change-icon--amber { background: rgba(251,146,60,0.12); color: #fb923c; }

.change-main {
  flex: 1;
  min-width: 0;
}

.change-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 4px;
}

.change-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-heading);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.change-time {
  font-size: 11px;
  color: var(--text-muted);
  flex-shrink: 0;
}

.env-badge {
  display: inline-block;
  padding: 1px 8px;
  font-size: 10px;
  font-weight: 600;
  border-radius: 10px;
}

.env-badge--prod { background: rgba(16,185,129,0.12); color: var(--success); }
.env-badge--pre { background: rgba(59,130,246,0.12); color: var(--primary); }
.env-badge--test { background: rgba(139,92,246,0.12); color: #a78bfa; }
.env-badge--dev { background: rgba(251,146,60,0.12); color: #fb923c; }

.change-empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.change-empty-text {
  font-size: 13px;
  color: var(--text-muted);
}
</style>
