<template>
  <section class="main-grid">
    <CenterLeft :traceStats="traceStats" :traceCases="traceCases" />
    <CenterRight :latestEvents="latestEvents" />
  </section>
</template>

<script setup lang="ts">
import CenterLeft from './center/center-left.vue'
import CenterRight from './center/center-right.vue'

type TraceStat = {
  label: string
  value: string
  hint: string
  icon: any
}

type TraceCase = {
  name: string
  status: 'success' | 'failed' | 'running'
  statusText: string
  cost: string
  updatedAt: string
}

type LatestEvent = {
  title: string
  level: 'high' | 'medium' | 'low'
  levelText: string
  source: string
  time: string
}

defineProps<{
  traceStats: TraceStat[]
  traceCases: TraceCase[]
  latestEvents: LatestEvent[]
}>()
</script>

<style scoped>
.main-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
}

@media (max-width: 1100px) {
  .main-grid {
    grid-template-columns: 1fr;
  }
}
</style>
