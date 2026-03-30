<template>
  <div class="screen-grid">
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

    <DashboardPanel title="流量统计" subtitle="近 7 天请求量与 Token 消耗走势">
      <TrafficStats />
    </DashboardPanel>

    <DashboardPanel title="模型调用分布" subtitle="本周模型使用占比">
      <ModelUsageChart />
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
import type { ManagementEntry, ManagementGroup } from '../backend/management'
// Vetur occasionally misses Vue SFC default exports in script setup files.
// @ts-ignore
import DashboardPanel from './DashboardPanel.vue'
// @ts-ignore
import AdminEntryCard from '../backend/AdminEntryCard.vue'
// @ts-ignore
import TrafficStats from './TrafficStats.vue'
// @ts-ignore
import ModelUsageChart from './ModelUsageChart.vue'

const props = defineProps<{
  groups: ManagementGroup[]
  roleLabel: string
}>()

const emit = defineEmits<{
  navigate: [route: string]
}>()

const allEntries = computed(() => props.groups.reduce<ManagementEntry[]>((entries, group) => {
  entries.push(...group.items)
  return entries
}, []))

const quickEntries = computed<ManagementEntry[]>(() => {
  const highlighted = allEntries.value.filter((item) => item.highlight)
  return (highlighted.length > 0 ? highlighted : allEntries.value).slice(0, 4)
})

</script>

<style scoped>
.screen-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 24px;
}

.screen-grid > :first-child {
  grid-column: span 2;
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

  .screen-grid > :first-child {
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
