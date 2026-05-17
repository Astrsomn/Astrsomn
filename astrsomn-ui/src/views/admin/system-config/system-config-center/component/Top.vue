<template>
  <section class="quick-access-root">
    <div class="quick-access-grid">
      <button
          v-for="(item, index) in moduleCards"
          :key="item.routeName"
          class="quick-access-card card-hover"
          type="button"
          @click="emit('go-to', item.routeName)"
      >
        <div :class="`icon-${(index % 4) + 1}`" class="icon-container">
          <component :is="item.icon"/>
        </div>
        <div class="text-block">
          <h3 class="card-title">{{ item.title }}</h3>
          <p class="card-desc">{{ item.desc }}</p>
        </div>
      </button>
    </div>
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

defineProps<{
  moduleCards: ModuleCard[]
}>()

const emit = defineEmits<{
  (e: 'go-to', routeName: string): void
}>()
</script>

<style scoped>
.quick-access-root {
  width: 100%;
}

.quick-access-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 10px;
}

.quick-access-card {
  box-sizing: border-box;
  min-height: 88px;
  background: var(--bg-card);
  padding: 14px 16px;
  border-radius: var(--radius-md);
  border: 1px solid var(--border-default);
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  text-align: left;
  outline: none;
}

.card-hover:hover {
  border-color: var(--primary);
  box-shadow: var(--shadow-overview);
}

.icon-container {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}

.icon-1 {
  background: rgba(59, 130, 246, 0.15);
  color: var(--primary);
}

.icon-2 {
  background: rgba(16, 185, 129, 0.15);
  color: var(--success);
}

.icon-3 {
  background: rgba(245, 158, 11, 0.15);
  color: #fb923c;
}

.icon-4 {
  background: rgba(139, 92, 246, 0.15);
  color: #a78bfa;
}

.text-block {
  min-width: 0;
}

.card-title {
  font-weight: 700;
  color: var(--text-heading);
  font-size: 14px;
  margin: 0;
}

.card-desc {
  font-size: 11px;
  color: var(--text-muted);
  margin: 4px 0 0 0;
  line-height: 1.4;
  display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

@media (max-width: 1200px) {
  .quick-access-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .quick-access-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 480px) {
  .quick-access-grid {
    grid-template-columns: 1fr;
  }
}
</style>
