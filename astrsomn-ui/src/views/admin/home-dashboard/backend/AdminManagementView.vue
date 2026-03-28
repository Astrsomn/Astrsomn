<template>

    <div class="mgmt-sections">
      <AdminManagementSection
        v-for="group in groups"
        :key="group.id"
        :group="group"
        @navigate="emit('navigate', $event)"
      />
    </div>

</template>

<script lang="ts">
export default {
  name: 'AdminManagementView',
}
</script>

<script setup lang="ts">
import type { ManagementGroup } from './management.ts'
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
