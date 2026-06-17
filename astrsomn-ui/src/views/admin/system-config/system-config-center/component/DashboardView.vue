<template>
  <div class="dashboard-view">
    <!-- Stats Overview Cards -->
    <section class="stats-row">
      <button
        v-for="stat in statCards"
        :key="stat.key"
        :class="['stat-card', { 'stat-card--active': statusFilter === stat.key || (statusFilter === 'all' && stat.key === 'all') }]"
        type="button"
        @click="emit('update:statusFilter', stat.filterValue)"
      >
        <div :class="`stat-icon stat-icon--${stat.color}`">
          <component :is="stat.icon" />
        </div>
        <div class="stat-body">
          <span class="stat-label">{{ stat.label }}</span>
          <span class="stat-value">{{ stat.value }}</span>
        </div>
      </button>
    </section>

    <!-- Main Grid: Table + Right Panel（与 stats 行同宽，右侧面板对齐第四列） -->
    <div class="main-grid" :style="mainGridStyle">
      <section class="table-panel">
        <BusinessTable
          :current-page="currentPage"
          :filtered-total="filteredTotal"
          :loading="loading"
          :page-size="pageSize"
          :paged-online-systems="pagedOnlineSystems"
          :status-counts="statusCounts"
          :status-filter="statusFilter"
          @export="emit('export')"
          @refresh="emit('refresh')"
          @update:page="emit('update:page', $event)"
          @update:status-filter="emit('update:statusFilter', $event)"
        />
      </section>

      <aside class="right-panel">
        <RightPanel
          :env-distribution="envDistribution"
          :recent-changes="recentChanges"
          :resource-usage="resourceUsage"
          @view-all-changes="emit('view-all-changes')"
        />
      </aside>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {
  AppstoreOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  PauseCircleOutlined,
} from '@ant-design/icons-vue'
import {usePageTranslation} from '@/locales/pages'
import BusinessTable from './BusinessTable.vue'
import RightPanel from './RightPanel.vue'
import type {
  EnvDistribution,
  OnlineSystem,
  RecentChanges,
  ResourceUsagePanel,
} from '../useSystemConfigCenter'

const props = defineProps<{
  loading: boolean
  pagedOnlineSystems: OnlineSystem[]
  currentPage: number
  filteredTotal: number
  pageSize: number
  statusFilter: 'all' | 'online' | 'offline' | 'maintenance'
  statusCounts: { all: number; online: number; offline: number; maintenance: number }
  envDistribution: EnvDistribution
  resourceUsage: ResourceUsagePanel
  recentChanges: RecentChanges
}>()

const emit = defineEmits<{
  (e: 'update:statusFilter', value: 'all' | 'online' | 'offline' | 'maintenance'): void
  (e: 'update:page', value: number): void
  (e: 'export'): void
  (e: 'refresh'): void
  (e: 'view-all-changes'): void
}>()

const t = usePageTranslation('system-config-center')

const statCards = computed(() => [
  {
    key: 'all',
    label: t.value.stats.all,
    value: (props.statusCounts.all ?? 0).toLocaleString(),
    color: 'blue',
    filterValue: 'all' as const,
    icon: AppstoreOutlined,
  },
  {
    key: 'online',
    label: t.value.stats.online,
    value: (props.statusCounts.online ?? 0).toLocaleString(),
    color: 'green',
    filterValue: 'online' as const,
    icon: CheckCircleOutlined,
  },
  {
    key: 'offline',
    label: t.value.stats.offline,
    value: (props.statusCounts.offline ?? 0).toLocaleString(),
    color: 'amber',
    filterValue: 'offline' as const,
    icon: PauseCircleOutlined,
  },
  {
    key: 'maintenance',
    label: t.value.stats.maintenance,
    value: (props.statusCounts.maintenance ?? 0).toLocaleString(),
    color: 'violet',
    filterValue: 'maintenance' as const,
    icon: ExclamationCircleOutlined,
  },
])

/**
 * 铺满页面：Header(60px) + stats行(≈76px含间距) + dashboard上下padding(48px) + gap(16px)
 * = 60 + 76 + 48 + 16 = 200px
 */
const HEADER_H = 60
const TOP_AREA_H = 76
const PADDING_H = 48
const GAP_H = 16
const mainGridStyle = computed(() => ({
  height: `calc(100vh - ${HEADER_H + TOP_AREA_H + PADDING_H + GAP_H}px)`,
}))
</script>

<style scoped>
.dashboard-view {
  width: 100%;
  max-width: 1600px;
  margin: 0 auto;
  padding: 24px 24px 24px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 0;
  box-sizing: border-box;
}

/* ---- Stats Row ---- */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  flex-shrink: 0;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 18px;
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: border-color 0.25s ease, box-shadow 0.25s ease, transform 0.2s ease;
  text-align: left;
  outline: none;
}

.stat-card:hover {
  border-color: var(--primary);
  box-shadow: var(--shadow-overview);
  transform: translateY(-1px);
}

.stat-card--active {
  border-color: var(--primary);
}

.stat-icon {
  flex-shrink: 0;
  width: 44px;
  height: 44px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.stat-icon--blue {
  background: rgba(59, 130, 246, 0.15);
  color: var(--primary);
}

.stat-icon--green {
  background: rgba(16, 185, 129, 0.15);
  color: var(--success);
}

.stat-icon--amber {
  background: rgba(245, 158, 11, 0.15);
  color: #fb923c;
}

.stat-icon--violet {
  background: rgba(139, 92, 246, 0.15);
  color: #a78bfa;
}

.stat-body {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.stat-label {
  font-size: 12px;
  color: var(--text-muted);
}

.stat-value {
  font-size: 24px;
  font-weight: 800;
  color: var(--text-heading);
  line-height: 1.2;
  font-variant-numeric: tabular-nums;
}

/* ---- Main Grid（4 列，与 stats 行对齐，右侧面板占第 4 列） ---- */
.main-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  min-height: 0;
  flex-shrink: 0;
}

.table-panel {
  grid-column: 1 / 4;
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.right-panel {
  grid-column: 4 / 5;
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-height: 0;
}

/* ---- Responsive ---- */
@media (max-width: 1100px) {
  .main-grid {
    grid-template-columns: 1fr;
  }

  .table-panel {
    grid-column: 1;
  }

  .right-panel {
    grid-column: 1;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }
}

@media (max-width: 768px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }

  .right-panel {
    grid-template-columns: 1fr;
  }

  .dashboard-view {
    padding: 16px 12px 16px 12px;
  }
}

@media (max-width: 480px) {
  .stats-row {
    grid-template-columns: 1fr;
  }
}
</style>
