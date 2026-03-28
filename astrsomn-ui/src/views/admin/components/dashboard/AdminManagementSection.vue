<template>
  <section class="mgmt-section">
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
</template>

<script setup lang="ts">
import type { ManagementGroup } from './management'
import AdminEntryCard from './AdminEntryCard.vue'

defineProps<{
  group: ManagementGroup
}>()

const emit = defineEmits<{
  navigate: [route: string]
}>()
</script>

<style scoped>
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
  color: var(--section-title);
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
