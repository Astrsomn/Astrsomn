<template>
  <section class="main-grid">
    <div class="left-cards">
      <div class="card-grid">
        <button
          v-for="item in leftModuleCards"
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
    <div class="nav-area"></div>
    <div class="right-cards">
      <div class="card-grid">
        <button
          v-for="item in rightModuleCards"
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

const props = defineProps<{
  moduleCards: ModuleCard[]
}>()

const emit = defineEmits<{
  (e: 'go-to', routeName: string): void
}>()

// 分割卡片数据
const leftModuleCards = props.moduleCards.slice(0, Math.ceil(props.moduleCards.length / 2))
const rightModuleCards = props.moduleCards.slice(Math.ceil(props.moduleCards.length / 2))
</script>

<style scoped>
.main-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 16px;
  padding: 14px 0;
}

.nav-area {
  /* 中间导航栏区域 */
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
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

@media (max-width: 1100px) {
  .main-grid {
    grid-template-columns: 1fr;
  }
  
  .nav-area {
    display: none;
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
