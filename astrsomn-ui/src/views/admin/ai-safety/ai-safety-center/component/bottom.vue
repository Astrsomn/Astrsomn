<template>
  <section class="main-grid">
    <button
        v-for="item in moduleCards"
        :key="item.routeName"
        class="route-card"
        type="button"
        @click="emit('go-to', item.routeName)"
    >
      <div class="card-icon">
        <component :is="item.icon"/>
      </div>
      <div class="card-content">
        <h4 class="card-title">{{ item.title }}</h4>
        <p class="card-desc">{{ item.desc }}</p>
      </div>
    </button>
  </section>
</template>

<script lang="ts" setup>
import type {Component} from 'vue'

type ModuleCard = {
  title: string
  desc: string
  routeName: string
  icon: Component
}

const props = defineProps<{
  moduleCards: ModuleCard[]
}>()

const emit = defineEmits<{
  (e: 'go-to', routeName: string): void
}>()
</script>

<style scoped>
.main-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 24px;
}

.route-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  background: #ffffff;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.05), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: left;
}

.route-card:hover {
  background: #f8fafc;
}

.card-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-icon :deep(*) {
  width: 20px;
  height: 20px;
  color: #94a3b8;
}

.card-content {
  flex: 1;
}

.card-title {
  margin: 0 0 2px 0;
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.card-desc {
  margin: 0;
  font-size: 10px;
  font-weight: 500;
  color: #94a3b8;
  line-height: 1.3;
}

@media (max-width: 768px) {
  .main-grid {
    grid-template-columns: 1fr;
  }
}
</style>
