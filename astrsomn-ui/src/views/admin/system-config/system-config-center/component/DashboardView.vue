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

    <!-- Quick Nav Cards -->
    <section class="quick-nav">
      <button
        v-for="card in quickNavCards"
        :key="card.routeName"
        class="quick-nav-card"
        type="button"
        @click="emit('goTo', card.routeName)"
      >
        <div :class="`quick-nav-icon quick-nav-icon--${card.color}`">
          <component :is="card.icon" />
        </div>
        <div class="quick-nav-text">
          <span class="quick-nav-title">{{ card.title }}</span>
          <span class="quick-nav-desc">{{ card.desc }}</span>
        </div>
      </button>
    </section>

    <!-- Main Grid: Table + Right Panel -->
    <div class="main-grid">
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
          @update:page-size="emit('update:pageSize', $event)"
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
  AlertOutlined,
  ApiOutlined,
  AppstoreOutlined,
  CheckCircleOutlined,
  ClusterOutlined,
  ExclamationCircleOutlined,
  PauseCircleOutlined,
  SettingOutlined,
  UserOutlined,
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
  statusFilter: 'all' | 'online' | 'offline' | 'maintenance'
  statusCounts: { all: number; online: number; offline: number; maintenance: number }
  pageSize: number
  envDistribution: EnvDistribution
  resourceUsage: ResourceUsagePanel
  recentChanges: RecentChanges
}>()

const emit = defineEmits<{
  (e: 'update:statusFilter', value: 'all' | 'online' | 'offline' | 'maintenance'): void
  (e: 'update:page', value: number): void
  (e: 'update:pageSize', value: number): void
  (e: 'export'): void
  (e: 'refresh'): void
  (e: 'goTo', routeName: string): void
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

const QUICK_NAV_DATA = [
  { routeName: 'AdminUsers', icon: UserOutlined, color: 'blue' as const },
  { routeName: 'AdminEnv', icon: ClusterOutlined, color: 'green' as const },
  { routeName: 'AdminSystemConfig', icon: SettingOutlined, color: 'cyan' as const },
  { routeName: 'AdminSystemMessage', icon: AlertOutlined, color: 'violet' as const },
  { routeName: 'AdminSystemExtension', icon: ApiOutlined, color: 'amber' as const },
]

const QUICK_NAV_TITLE_KEYS = ['users', 'env', 'config', 'messages', 'extensions'] as const
const QUICK_NAV_DESC_KEYS = ['usersDesc', 'envDesc', 'configDesc', 'messagesDesc', 'extensionsDesc'] as const

const quickNavCards = computed(() =>
  QUICK_NAV_DATA.map((item, i) => ({
    ...item,
    title: (t.value.dashboard.quickNav as Record<string, string>)[QUICK_NAV_TITLE_KEYS[i]] ?? '',
    desc: (t.value.dashboard.quickNav as Record<string, string>)[QUICK_NAV_DESC_KEYS[i]] ?? '',
  }))
)
</script>

<style scoped>
.dashboard-view {
  width: 100%;
  max-width: 1600px;
  margin: 0 auto;
  padding: 24px 24px 80px 24px;
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
  box-shadow: var(--shadow-overview);
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

/* ---- Quick Nav ---- */
.quick-nav {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
}

.quick-nav-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  text-align: left;
  outline: none;
}

.quick-nav-card:hover {
  border-color: var(--primary);
  box-shadow: var(--shadow-overview);
}

.quick-nav-icon {
  flex-shrink: 0;
  width: 42px;
  height: 42px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.quick-nav-icon--blue {
  background: rgba(59, 130, 246, 0.12);
  color: var(--primary);
}

.quick-nav-icon--green {
  background: rgba(16, 185, 129, 0.12);
  color: var(--success);
}

.quick-nav-icon--cyan {
  background: rgba(56, 189, 248, 0.12);
  color: #38bdf8;
}

.quick-nav-icon--violet {
  background: rgba(139, 92, 246, 0.12);
  color: #a78bfa;
}

.quick-nav-icon--amber {
  background: rgba(245, 158, 11, 0.12);
  color: #fb923c;
}

.quick-nav-text {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.quick-nav-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-heading);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.quick-nav-desc {
  font-size: 11px;
  color: var(--text-muted);
  line-height: 1.3;
}

/* ---- Main Grid ---- */
.main-grid {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 12px;
  flex: 1;
  min-height: 0;
}

.table-panel {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-card);
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.right-panel {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-height: 0;
}

/* ---- Responsive ---- */
@media (max-width: 1200px) {
  .quick-nav {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 1100px) {
  .main-grid {
    grid-template-columns: 1fr;
  }

  .right-panel {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }
}

@media (max-width: 768px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }

  .quick-nav {
    grid-template-columns: repeat(2, 1fr);
  }

  .right-panel {
    grid-template-columns: 1fr;
  }

  .dashboard-view {
    padding: 16px 12px 80px 12px;
  }
}

@media (max-width: 480px) {
  .stats-row {
    grid-template-columns: 1fr;
  }

  .quick-nav {
    grid-template-columns: 1fr;
  }
}
</style>
