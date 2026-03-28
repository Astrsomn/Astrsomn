<template>
  <button
    type="button"
    class="entry-card"
    :class="{ 'entry-card--highlight': item.highlight }"
    :data-accent="item.accent"
    @click="emit('navigate', item.route)"
  >
    <div class="icon-wrapper">
      <component :is="item.icon" class="entry-icon" />
    </div>
    <div class="entry-content">
      <div class="entry-title">{{ item.label }}</div>
      <div class="entry-desc">{{ item.description }}</div>
    </div>
    <div class="arrow-hint">→</div>
  </button>
</template>

<script setup lang="ts">
import type { ManagementEntry } from './management.ts'

defineProps<{
  item: ManagementEntry
}>()

const emit = defineEmits<{
  navigate: [route: string]
}>()
</script>

<style scoped>
.entry-card {
  --entry-color: var(--accent-blue);

  position: relative;
  border: 1px solid color-mix(in srgb, var(--entry-color) 30%, var(--border-default));
  border-radius: 14px;
  background: var(--bg-card);
  padding: 24px;
  display: flex;
  gap: 18px;
  align-items: center;
  text-align: left;
  cursor: pointer;
  outline: none;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.entry-card[data-accent='primary'] {
  --entry-color: var(--primary);
}

.entry-card[data-accent='cyan'] {
  --entry-color: var(--accent-cyan);
}

.entry-card[data-accent='blue'] {
  --entry-color: var(--accent-blue);
}

.entry-card[data-accent='sky'] {
  --entry-color: var(--section-title);
}

.entry-card[data-accent='mint'] {
  --entry-color: var(--success);
}

.entry-card[data-accent='coral'] {
  --entry-color: var(--error);
}

.entry-card[data-accent='primary-light'] {
  --entry-color: var(--primary-light);
}

.entry-card[data-accent='indigo'] {
  --entry-color: color-mix(in srgb, var(--primary) 45%, var(--accent-blue) 55%);
}

.entry-card[data-accent='ocean'] {
  --entry-color: color-mix(in srgb, var(--primary) 35%, var(--accent-cyan) 65%);
}

.entry-card[data-accent='violet'] {
  --entry-color: color-mix(in srgb, var(--section-title) 88%, var(--accent-blue) 12%);
}

.entry-card[data-accent='teal'] {
  --entry-color: color-mix(in srgb, var(--success) 48%, var(--accent-cyan) 52%);
}

.entry-card[data-accent='frost'] {
  --entry-color: color-mix(in srgb, var(--section-title) 52%, var(--accent-cyan) 48%);
}

.entry-card[data-accent='brand'] {
  --entry-color: color-mix(in srgb, var(--primary) 52%, var(--accent-blue) 48%);
}

.entry-card--highlight {
  background: color-mix(in srgb, var(--entry-color) 9%, var(--bg-card));
}

.entry-card:hover {
  border-color: color-mix(in srgb, var(--entry-color) 58%, var(--border-default));
  transform: translateY(-4px);
  box-shadow:
    0 12px 24px -10px color-mix(in srgb, var(--entry-color) 22%, transparent),
    0 4px 12px -4px color-mix(in srgb, var(--entry-color) 12%, transparent);
}

.icon-wrapper {
  flex-shrink: 0;
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: 1px solid color-mix(in srgb, var(--entry-color) 38%, var(--border-subtle));
  border-radius: 12px;
  transition: all 0.3s ease;
}

.entry-card--highlight .icon-wrapper {
  background: color-mix(in srgb, var(--entry-color) 11%, var(--bg-card));
}

.entry-card:hover .icon-wrapper {
  border-color: color-mix(in srgb, var(--entry-color) 52%, var(--border-default));
  background: color-mix(in srgb, var(--entry-color) 14%, var(--bg-card));
}

.entry-icon {
  font-size: 26px;
  color: var(--entry-color);
  transition: color 0.3s ease;
}

.entry-content {
  flex: 1;
  min-width: 0;
}

.entry-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--entry-color);
  margin-bottom: 4px;
}

.entry-desc {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.5;
}

.arrow-hint {
  font-size: 18px;
  color: var(--text-muted);
  opacity: 0;
  transform: translateX(-10px);
  transition: all 0.3s ease;
}

.entry-card:hover .arrow-hint {
  opacity: 1;
  transform: translateX(0);
  color: var(--entry-color);
}
</style>
