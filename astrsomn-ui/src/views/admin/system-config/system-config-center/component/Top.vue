<template>
  <section class="quick-access-root">
    <div class="quick-access-grid">
      <button
        v-for="(item, index) in moduleCards"
        :key="item.routeName"
        type="button"
        class="quick-access-card card-hover"
        @click="emit('go-to', item.routeName)"
      >
        <div class="icon-container" :class="`icon-${(index % 4) + 1}`">
          <component :is="item.icon" />
        </div>
        <div class="text-block">
          <h3 class="card-title">{{ item.title }}</h3>
          <p class="card-desc">{{ item.desc }}</p>
        </div>
      </button>
    </div>
  </section>
</template>

<script setup lang="ts">
import type { Component } from 'vue'

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
  background: #ffffff;
  padding: 14px 16px;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
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
  border-color: #3b82f6;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.12);
}

.icon-container {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}

.icon-1 {
  background: #dbeafe;
  color: #3b82f6;
}

.icon-2 {
  background: #d1fae5;
  color: #10b981;
}

.icon-3 {
  background: #fef3c7;
  color: #f59e0b;
}

.icon-4 {
  background: #ede9fe;
  color: #8b5cf6;
}

.text-block {
  min-width: 0;
}

.card-title {
  font-weight: 700;
  color: #1e293b;
  font-size: 14px;
  margin: 0;
}

.card-desc {
  font-size: 11px;
  color: #94a3b8;
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
