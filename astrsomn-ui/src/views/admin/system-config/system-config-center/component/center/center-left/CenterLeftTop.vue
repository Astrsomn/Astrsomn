<template>
  <div class="panel-card module-panel">
    <div class="card-grid">
      <button
        v-for="(item, index) in moduleCards"
        :key="item.routeName"
        :class="['route-card', `card-${index % 4 + 1}`]"
        type="button"
        @click="emit('go-to', item.routeName)"
      >
        <div class="card-title">
          <div class="icon-wrapper">
            <component :is="item.icon" />
          </div>
          <span class="title-text">{{ item.title }}</span>
        </div>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { AppstoreOutlined } from '@ant-design/icons-vue'
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
.panel-card {
  border-radius: 14px;
  background: var(--bg-card);
  padding: 14px 16px;
}

.module-panel {
  width: 100%;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}

.route-card {
  min-height: 80px;
  border: 1px solid var(--border-default);
  border-radius: 10px;
  padding: 12px 10px;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.route-card:hover {
  border-color: var(--primary);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.card-1 {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-color: #e2e8f0;
}

.card-2 {
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  border-color: #bbf7d0;
}

.card-3 {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border-color: #fcd34d;
}

.card-4 {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  border-color: #93c5fd;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-height: 0;
}

.icon-wrapper {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.icon-wrapper :deep(.anticon) {
  font-size: 20px;
  line-height: 1;
}

.title-text {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-heading);
  flex: 1;
  min-width: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-desc {
  margin-top: 6px;
  color: var(--text-secondary);
  line-height: 1.3;
  font-size: 11px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

@media (max-width: 1200px) {
  .card-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .card-grid {
    grid-template-columns: 1fr;
  }
}
</style>
