<template>
  <div class="screen-grid">
    <DashboardPanel title="总览" subtitle="先看关键指标，再进入具体配置模块。">
      <div class="metric-grid">
        <div v-for="metric in metrics" :key="metric.label" class="metric-card">
          <div class="metric-label">{{ metric.label }}</div>
          <div class="metric-value">{{ metric.value }}</div>
          <div class="metric-desc">{{ metric.description }}</div>
        </div>
      </div>
    </DashboardPanel>

    <DashboardPanel title="流量统计" subtitle="近 7 天请求量与 Token 消耗走势">
      <TrafficStats />
    </DashboardPanel>

    <DashboardPanel title="模型调用分布" subtitle="本周模型使用占比">
      <ModelUsageChart />
    </DashboardPanel>

    <DashboardPanel title="常用入口" subtitle="优先展示高频后台功能，减少层层查找。">
      <div class="quick-grid">
        <AdminEntryCard
          v-for="item in quickEntries"
          :key="item.key"
          :item="item"
          @navigate="emit('navigate', $event)"
        />
      </div>
    </DashboardPanel>
  </div>
</template>

<script lang="ts">
export default {
  name: 'AdminDataScreen',
}
</script>

<script setup lang="ts">
import { computed } from 'vue'
import type { ManagementEntry, ManagementGroup } from './management'
import DashboardPanel from './DashboardPanel.vue'
import AdminEntryCard from './AdminEntryCard.vue'
import TrafficStats from '../echarts/TrafficStats.vue'
import ModelUsageChart from '../echarts/ModelUsageChart.vue'

const props = defineProps<{
  groups: ManagementGroup[]
  roleLabel: string
}>()

const emit = defineEmits<{
  navigate: [route: string]
}>()

const allEntries = computed(() => props.groups.flatMap((group) => group.items))

const quickEntries = computed<ManagementEntry[]>(() => {
  const highlighted = allEntries.value.filter((item) => item.highlight)
  return (highlighted.length > 0 ? highlighted : allEntries.value).slice(0, 4)
})

const metrics = computed(() => [
  {
    label: '可用模块',
    value: String(allEntries.value.length),
    description: '当前账号可直接访问的后台能力',
  },
  {
    label: '分类分区',
    value: String(props.groups.length),
    description: '按业务域拆分，定位更直接',
  },
  {
    label: '常用入口',
    value: String(quickEntries.value.length),
    description: '首页优先展示的高频操作',
  },
  {
    label: '当前角色',
    value: props.roleLabel,
    description: '入口会根据账号权限自动过滤',
  },
])
</script>

<style scoped>
.screen-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 24px;
}

.screen-grid > :first-child,
.screen-grid > :last-child {
  grid-column: span 2;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.metric-card {
  padding: 18px;
  border-radius: 16px;
  background: color-mix(in srgb, var(--primary) 5%, var(--bg-card));
  border: 1px solid color-mix(in srgb, var(--primary) 12%, var(--border-subtle));
}

.metric-label {
  font-size: 12px;
  color: var(--text-muted);
}

.metric-value {
  margin-top: 10px;
  font-size: 28px;
  font-weight: 700;
  line-height: 1.1;
  color: var(--text-heading);
}

.metric-desc {
  margin-top: 8px;
  font-size: 12px;
  line-height: 1.5;
  color: var(--text-secondary);
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

@media (max-width: 1200px) {
  .metric-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 1024px) {
  .screen-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .screen-grid > :first-child,
  .screen-grid > :last-child {
    grid-column: auto;
  }
}

@media (max-width: 640px) {
  .metric-grid,
  .quick-grid {
    grid-template-columns: 1fr;
  }

  .metric-value {
    font-size: 24px;
  }
}
</style>
