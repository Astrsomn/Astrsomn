<template>
  <div class="main-content">
    <main class="main-layout">
      <div class="online-systems-panel">
        <CenterLeftBottom
          :loading="loading"
          :paged-online-systems="pagedOnlineSystems"
          :current-page="currentPage"
          :filtered-total="filteredTotal"
          :status-filter="statusFilter"
          :status-counts="statusCounts"
          :page-size="pageSize"
          @update:status-filter="emit('update:statusFilter', $event)"
          @update:page="emit('update:page', $event)"
          @update:page-size="emit('update:pageSize', $event)"
          @export="emit('export')"
          @refresh="emit('refresh')"
        />
      </div>

      <aside class="right-panel">
        <CenterRightTop :distribution="envDistribution" />
        <CenterRightBottom
          :resource-usage="resourceUsage"
          :recent-changes="recentChanges"
          @view-all-changes="emit('view-all-changes')"
        />
      </aside>
    </main>
  </div>
</template>

<script setup lang="ts">
import type { Component } from 'vue'
import CenterLeftBottom from './center/center-left/CenterLeftBottom.vue'
import CenterRightTop from './center/center-right/CenterRightTop.vue'
import CenterRightBottom from './center/center-right/CenterRightBottom.vue'

type SystemStatus = 'online' | 'offline' | 'maintenance'
type SystemEnv = 'prod' | 'pre' | 'test' | 'dev'
type IconTheme = 'blue' | 'green' | 'cyan' | 'amber'
type OwnerDot = 'blue' | 'green' | 'violet' | 'orange'

type OnlineSystem = {
  id: string
  name: string
  slug: string
  ownerName: string
  ownerDot: OwnerDot
  env: SystemEnv
  status: SystemStatus
  todayCalls: number
  errorRate: number
  lastAccess: string
  icon: Component
  iconTheme: IconTheme
}

type StatusCounts = {
  all: number
  online: number
  offline: number
  maintenance: number
}

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

type ResourceUsagePanel = {
  apiCalls: { used: number; limit: number }
  concurrency: { used: number; limit: number }
  storage: { usedGB: number; limitGB: number }
}

type RecentChangeItem = {
  title: string
  env: SystemEnv
  timeText: string
  icon: Component
}

type RecentChanges = {
  items: RecentChangeItem[]
}

defineProps<{
  loading?: boolean
  pagedOnlineSystems: OnlineSystem[]
  currentPage: number
  filteredTotal: number
  statusFilter: 'all' | 'online' | 'offline' | 'maintenance'
  statusCounts: StatusCounts
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
  (e: 'view-all-changes'): void
}>()
</script>

<style scoped>
.main-content {
  width: 100%;
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
  box-sizing: border-box;
  overflow: hidden;
}

.main-layout {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 12px;
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.online-systems-panel {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  border: 1px solid var(--border-default);
  display: flex;
  flex-direction: column;
  min-height: 0;
  height: 100%;
  overflow: hidden;
}

.right-panel {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-height: 0;
  overflow: hidden;
}

@media (max-width: 1100px) {
  .main-layout {
    grid-template-columns: 1fr;
  }
}
</style>
