<template>
  <div class="center-grid">
    <div class="center-left">
      <CenterLeftTop :module-cards="moduleCards" @go-to="emit('go-to', $event)" />
      <CenterLeftBottom
        :paged-online-systems="pagedOnlineSystems"
        :current-page="currentPage"
        :total-pages="totalPages"
        :total-systems="totalSystems"
        @prev-page="emit('prev-page')"
        @next-page="emit('next-page')"
      />
    </div>
    <div class="center-right">
      <CenterRightTop :todo-items="todoItems" />
      <CenterRightBottom :extension-cards="extensionCards" />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Component } from 'vue'
import CenterLeftTop from './center-left/CenterLeftTop.vue'
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
.center-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
  flex: 1;
  min-height: 0;
  max-height: calc(100vh - 150px);
  overflow: hidden;
}

.center-left {
  display: grid;
  grid-template-rows: auto 1fr;
  gap: 16px;
  min-height: 0;
}

.center-right {
  display: grid;
  grid-template-rows: 1fr 2fr;
  gap: 16px;
  min-height: 0;
}

@media (max-width: 1200px) {
  .center-grid {
    grid-template-columns: 1fr;
  }

  .center-left,
  .center-right {
    grid-template-rows: auto;
  }
}
</style>
