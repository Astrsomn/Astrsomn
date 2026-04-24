<template>
  <div class="panel-card">
    <div class="panel-head">
      <h3>
        <AppstoreOutlined />
        核心模块
      </h3>
      <span>快速进入系统管理能力</span>
    </div>
    <div class="card-grid">
      <button
        v-for="item in moduleCards"
        :key="item.routeName"
        class="route-card"
        type="button"
        @click="emit('go-to', item.routeName)"
      >
        <div class="card-title">
          <component :is="item.icon" />
          {{ item.title }}
        </div>
        <div class="card-desc">{{ item.desc }}</div>
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
  border: 1px solid var(--border-default);
  border-radius: 14px;
  background: var(--bg-card);
  padding: 14px 16px;
  width: calc((100% - 16px) * 2 / 3);
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 12px;
}

.panel-head h3 {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 16px;
  color: var(--text-heading);
}

.panel-head span {
  font-size: 12px;
  color: var(--text-secondary);
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}

.route-card {
  min-height: 72px;
  border: 1px solid var(--border-default);
  border-radius: 10px;
  background: var(--bg-card);
  padding: 9px 10px;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
  overflow: hidden;
}

.route-card:hover {
  border-color: var(--primary);
  transform: translateY(-1px);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-heading);
}

.card-desc {
  margin-top: 4px;
  color: var(--text-secondary);
  line-height: 1.3;
  font-size: 11px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

@media (max-width: 1200px) {
  .panel-card {
    width: 100%;
  }

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
