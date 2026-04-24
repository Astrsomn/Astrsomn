<template>
  <div class="panel-card">
    <div class="panel-head">
      <h3>最新风险事件</h3>
    </div>
    <div class="event-list">
      <article v-for="event in latestEvents" :key="event.title" class="event-item">
        <div class="event-title-row">
          <span class="event-title">{{ event.title }}</span>
          <span class="event-level" :class="`level-${event.level}`">{{ event.levelText }}</span>
        </div>
        <div class="event-meta">{{ event.source }} · {{ event.time }}</div>
      </article>
    </div>
  </div>
</template>

<script setup lang="ts">
type LatestEvent = {
  title: string
  level: 'high' | 'medium' | 'low'
  levelText: string
  source: string
  time: string
}

defineProps<{
  latestEvents: LatestEvent[]
}>()
</script>

<style scoped>
.panel-card {
  border: 1px solid var(--border-default);
  border-radius: 14px;
  background: var(--bg-card);
  padding: 20px;
}

.panel-head {
  margin-bottom: 14px;
}

.panel-head h3 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-heading);
}

.event-list {
  display: grid;
  gap: 10px;
}

.event-item {
  border: 1px solid var(--border-default);
  border-radius: 10px;
  background: var(--bg-card);
  padding: 12px;
}

.event-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.event-title {
  font-size: 13px;
  color: var(--text-heading);
}

.event-level {
  font-size: 12px;
  border-radius: 999px;
  padding: 2px 8px;
  border: 1px solid transparent;
}

.level-high {
  color: #dd4b39;
  background: color-mix(in srgb, #dd4b39 12%, transparent);
  border-color: color-mix(in srgb, #dd4b39 24%, transparent);
}

.level-medium {
  color: #f39c12;
  background: color-mix(in srgb, #f39c12 12%, transparent);
  border-color: color-mix(in srgb, #f39c12 24%, transparent);
}

.level-low {
  color: #2e9f5d;
  background: color-mix(in srgb, #2e9f5d 12%, transparent);
  border-color: color-mix(in srgb, #2e9f5d 24%, transparent);
}

.event-meta {
  margin-top: 6px;
  color: var(--text-secondary);
  font-size: 12px;
}
</style>