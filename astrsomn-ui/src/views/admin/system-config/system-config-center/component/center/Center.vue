<template>
  <div class="main-content">


    <!-- 2. 主体布局 (严格遵循原图左右结构) -->
    <main class="main-layout">
      <!-- 左侧：在线业务系统 -->
      <div class="online-systems-panel">
            <!-- 1. 顶部快捷入口 (保持截图4列布局) -->
    <section class="quick-access-grid">
      <div v-for="(item, index) in moduleCards" :key="item.routeName" class="quick-access-card card-hover" @click="emit('go-to', item.routeName)">
        <div class="icon-container" :class="`icon-${index % 4 + 1}`">
          <component :is="item.icon" />
        </div>
        <div>
          <h3 class="card-title">{{ item.title }}</h3>
          <p class="card-desc">{{ item.desc }}</p>
        </div>
      </div>
    </section>
        <CenterLeftBottom
          :paged-online-systems="pagedOnlineSystems"
          :current-page="currentPage"
          :total-pages="totalPages"
          :total-systems="totalSystems"
          @prev-page="emit('prev-page')"
          @next-page="emit('next-page')"
        />
      </div>

      <!-- 右侧：待处理与扩展 -->
      <aside class="right-panel">
        <CenterRightTop :todo-items="todoItems" />
        <CenterRightBottom :extension-cards="extensionCards" />
      </aside>
    </main>
  </div>
</template>

<script setup lang="ts">
import type { Component } from 'vue'
import CenterLeftBottom from './center-left/CenterLeftBottom.vue'
import CenterRightTop from './center-right/CenterRightTop.vue'
import CenterRightBottom from './center-right/CenterRightBottom.vue'

type ModuleCard = {
  title: string
  desc: string
  routeName: string
  icon: Component
}

type OnlineStatus = 'online' | 'degraded' | 'offline'

type OnlineSystem = {
  name: string
  status: OnlineStatus
  statusText: string
  sessions: number
  lastHeartbeat: string
  icon: Component
}

type ExtensionCard = {
  name: string
  count: number
  desc: string
  icon: Component
  tags: string[]
}

defineProps<{
  moduleCards: ModuleCard[]
  pagedOnlineSystems: OnlineSystem[]
  currentPage: number
  totalPages: number
  totalSystems: number
  todoItems: string[]
  extensionCards: ExtensionCard[]
}>()

const emit = defineEmits<{
  (e: 'prev-page'): void
  (e: 'next-page'): void
  (e: 'go-to', routeName: string): void
}>()
</script>

<style scoped>
.main-content {
  width: 100%;
  max-width: 1600px;
  margin: 0 auto;
  padding: 24px;
  height: calc(100vh - 70px);
  display: flex;
  flex-direction: column;
  gap: 24px;
  box-sizing: border-box;
}

/* 顶部快捷入口（各中心页顶栏与此前保持一致：统一高度与卡片样式） */
.quick-access-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.quick-access-card {
  box-sizing: border-box;
  min-height: 100px;
  background: #ffffff;
  padding: 20px;
  border-radius: 20px;
  border: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.2s ease;
}

.card-hover:hover {
  border-color: #3b82f6;
  background-color: #ffffff;
}

.icon-container {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  border-radius: 16px;
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
}

/* 主体布局 */
.main-layout {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 24px;
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.online-systems-panel {
  background: white;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.right-panel {
  display: flex;
  flex-direction: column;
  gap: 24px;
  min-height: 0;
  overflow: hidden;
}

@media (max-width: 1100px) {
  .main-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .quick-access-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .quick-access-grid {
    grid-template-columns: 1fr;
  }
}
</style>
