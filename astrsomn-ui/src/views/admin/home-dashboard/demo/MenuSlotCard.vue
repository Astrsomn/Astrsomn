<template>
  <div class="slot-wrapper">
    <button
      type="button"
      class="slot-card"
      :data-accent="accent"
      :disabled="!entry"
      @click="handleClick"
    >
      <div class="slot-icon" aria-hidden="true">
        <component v-if="entry" :is="entry.icon" class="slot-icon-actual" />
        <span v-else class="slot-icon-plus">+</span>
      </div>

      <div class="slot-title">{{ title }}</div>
      <div class="slot-desc">{{ description || '暂无接入内容' }}</div>
    </button>
  </div>
</template>

<script setup lang="ts">
import type { EntryAccent, ManagementEntry } from '../backend/management'

const props = withDefaults(
  defineProps<{
    entry?: ManagementEntry
    title: string
    description?: string
    accent?: EntryAccent
  }>(),
  {
    accent: 'primary',
  },
)

const emit = defineEmits<{
  navigate: [route: string]
}>()

const accent = props.accent
const description = props.description
const title = props.title
const entry = props.entry

const handleClick = () => {
  if (!entry) return
  emit('navigate', entry.route)
}
</script>

<style scoped>
.slot-wrapper {
  min-width: 0;
}

.slot-card {
  --entry-color: var(--accent-blue);

  width: 100%;
  border: 1px solid color-mix(in srgb, var(--entry-color) 20%, var(--border-subtle));
  border-radius: 24px;
  background: color-mix(in srgb, var(--bg-card) 92%, rgba(255, 255, 255, 0.02));
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: flex-start;
  text-align: left;
  cursor: pointer;
  outline: none;
  transition:
    border-color 0.2s cubic-bezier(0.4, 0, 0.2, 1),
    transform 0.2s cubic-bezier(0.4, 0, 0.2, 1),
    box-shadow 0.2s cubic-bezier(0.4, 0, 0.2, 1),
    background 0.2s ease;
}

.slot-card:disabled {
  cursor: not-allowed;
  opacity: 0.72;
  box-shadow: none;
  transform: none;
}

.slot-card[data-accent='primary'] {
  --entry-color: var(--primary);
}
.slot-card[data-accent='cyan'] {
  --entry-color: var(--accent-cyan);
}
.slot-card[data-accent='blue'] {
  --entry-color: var(--accent-blue);
}
.slot-card[data-accent='sky'] {
  --entry-color: var(--section-title);
}
.slot-card[data-accent='mint'] {
  --entry-color: var(--success);
}
.slot-card[data-accent='coral'] {
  --entry-color: var(--error);
}
.slot-card[data-accent='primary-light'] {
  --entry-color: var(--primary-light);
}
.slot-card[data-accent='indigo'] {
  --entry-color: color-mix(in srgb, var(--primary) 45%, var(--accent-blue) 55%);
}
.slot-card[data-accent='ocean'] {
  --entry-color: color-mix(in srgb, var(--primary) 35%, var(--accent-cyan) 65%);
}
.slot-card[data-accent='violet'] {
  --entry-color: color-mix(in srgb, var(--section-title) 88%, var(--accent-blue) 12%);
}
.slot-card[data-accent='teal'] {
  --entry-color: color-mix(in srgb, var(--success) 48%, var(--accent-cyan) 52%);
}
.slot-card[data-accent='frost'] {
  --entry-color: color-mix(in srgb, var(--section-title) 52%, var(--accent-cyan) 48%);
}
.slot-card[data-accent='brand'] {
  --entry-color: color-mix(in srgb, var(--primary) 52%, var(--accent-blue) 48%);
}

.slot-icon {
  flex-shrink: 0;
  width: 48px; /* h-12 */
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px; /* rounded-2xl */
  border: 1px solid color-mix(in srgb, var(--entry-color) 18%, var(--border-subtle));
  color: var(--entry-color);
  background: color-mix(in srgb, var(--entry-color) 12%, var(--bg-card));
  font-weight: 900;
  font-size: 22px;
}

.slot-icon-actual {
  width: 22px;
  height: 22px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: inherit;
}

:deep(svg) {
  width: 22px;
  height: 22px;
  fill: currentColor;
}

.slot-title {
  font-size: 14px; /* text-sm */
  font-weight: 800;
  color: var(--text-heading);
  margin-top: 2px;
}

.slot-desc {
  font-size: 12px; /* text-xs */
  color: var(--text-muted);
  line-height: 1.55;
  opacity: 0.95;
  display: -webkit-box;
  -webkit-line-clamp: 2; /* match demo line-clamp-2 */
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.slot-card:not(:disabled):hover {
  border-color: color-mix(in srgb, var(--entry-color) 55%, var(--border-subtle));
  box-shadow:
    0 12px 20px -8px rgba(0, 0, 0, 0.05),
    0 4px 12px -4px color-mix(in srgb, var(--entry-color) 18%, transparent);
  transform: translateY(-2px);
}

.slot-card:not(:disabled):hover .slot-icon {
  border-color: color-mix(in srgb, var(--entry-color) 55%, var(--border-subtle));
  background: color-mix(in srgb, var(--entry-color) 85%, var(--bg-card));
  color: #ffffff;
}

.slot-icon-plus {
  line-height: 1;
}
</style>

