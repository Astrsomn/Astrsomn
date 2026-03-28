<template>
  <div class="mgmt-sections">
    <section
      v-for="group in groups"
      :key="group.id"
      class="mgmt-section"
    >
      <header class="mgmt-section-head">
        <h3 class="mgmt-section-title">{{ group.title }}</h3>
        <p v-if="group.subtitle" class="mgmt-section-sub">{{ group.subtitle }}</p>
      </header>

      <div class="card-grid">
        <AdminEntryCard
          v-for="item in group.items"
          :key="item.key"
          :item="item"
          @navigate="emit('navigate', $event)"
        />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import type { ManagementGroup } from './management'
// Vetur occasionally misses Vue SFC default exports in script setup files.
// @ts-ignore
import AdminEntryCard from './AdminEntryCard.vue'

defineProps<{
  groups: ManagementGroup[]
}>()

const emit = defineEmits<{
  navigate: [route: string]
}>()
</script>

<script lang="ts">
export default {
  name: 'AdminManagementView',
}
</script>

<style scoped>
.mgmt-sections {
  padding: 0 16px;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.mgmt-section {
  margin: 0;
}

.mgmt-section-head {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-subtle);
}

.mgmt-section-title {
  margin: 0;
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 0.02em;
}

.mgmt-section-sub {
  margin: 6px 0 0;
  font-size: 12px;
  line-height: 1.45;
  color: var(--text-muted);
  max-width: 720px;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

@media (max-width: 1024px) {
  .card-grid {
    grid-template-columns: 1fr;
  }
}
</style>
