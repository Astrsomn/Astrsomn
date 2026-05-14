<template>
  <div class="panel-card">
    <div class="panel-head">
      <h3>最新风险事件</h3>
    </div>
    <div class="event-list">
      <article v-for="event in latestEvents" :key="event.title" class="event-item">
        <div class="event-content">
          <div class="event-title">{{ event.title }}</div>
          <div class="event-meta">{{ event.source }} · {{ event.time }}</div>
        </div>
        <span :class="`level-${event.level}`" class="event-level">{{ event.levelText }}</span>
      </article>
    </div>
  </div>
</template>

<script lang="ts" setup>
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
  border: 1px solid #e2e8f0;
  border-radius: 30px;
  background: #ffffff;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.05), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
  padding: 24px;
  min-height: 500px;
  display: flex;
  flex-direction: column;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.panel-head h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.event-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  flex: 1;
}

.event-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px;
  border: 1px solid #f1f5f9;
  border-radius: 20px;
  background: rgba(248, 250, 252, 0.2);
  transition: all 0.2s ease;
  cursor: pointer;
}

.event-item:hover {
  background: #ffffff;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.05);
}

.event-content {
  flex: 1;
}

.event-title {
  font-size: 14px;
  font-weight: 600;
  color: #475569;
  transition: color 0.2s ease;
}

.event-item:hover .event-title {
  color: #3b82f6;
}

.event-meta {
  margin-top: 4px;
  font-size: 11px;
  font-weight: 500;
  color: #94a3b8;
}

.event-level {
  font-size: 10px;
  font-weight: 600;
  border-radius: 6px;
  padding: 2px 8px;
  border: 1px solid transparent;
}

.level-high {
  color: #ef4444;
  background: #fef2f2;
  border-color: #fee2e2;
}

.level-medium {
  color: #f97316;
  background: #fff7ed;
  border-color: #fed7aa;
}

.level-low {
  color: #10b981;
  background: #ecfdf5;
  border-color: #d1fae5;
}
</style>