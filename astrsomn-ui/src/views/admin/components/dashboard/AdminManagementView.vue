<template>
  <DashboardPanel
    title="管理后台"
    subtitle="按分类浏览后台模块，直接进入对应配置页。"
  >
    <div class="mgmt-sections">
      <AdminManagementSection
        v-for="group in groups"
        :key="group.id"
        :group="group"
        @navigate="emit('navigate', $event)"
      />
    </div>
  </DashboardPanel>
</template>

<script lang="ts">
export default {
  name: 'AdminManagementView',
}
</script>

<script setup lang="ts">
import type { ManagementGroup } from './management'
import DashboardPanel from './DashboardPanel.vue'
import AdminManagementSection from './AdminManagementSection.vue'

defineProps<{
  groups: ManagementGroup[]
}>()

const emit = defineEmits<{
  navigate: [route: string]
}>()
</script>

<style scoped>
.mgmt-sections {
  display: flex;
  flex-direction: column;
  gap: 32px;
}
</style>
