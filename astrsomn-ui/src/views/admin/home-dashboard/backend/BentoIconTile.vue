<template>
  <button
    type="button"
    class="glass-card glass-card--interactive icon-tile"
    :class="[
      gridSpan === 6 ? 'bento-span-6-1' : gridSpan === 3 ? 'bento-span-3-1' : 'bento-span-2-1',
      { 'icon-tile--half': gridSpan === 6, 'icon-tile--third': gridSpan === 3 },
    ]"
    :disabled="disabled"
    @click="go"
  >
    <div class="icon-tile-icon" :class="`icon-tile-icon--${variant}`">
      <component :is="icon" class="icon-tile-glyph" />
    </div>
    <p class="icon-tile-title">{{ title }}</p>
    <p class="icon-tile-sub">{{ subtitle }}</p>
  </button>
</template>

<script setup lang="ts">
import type { Component } from 'vue'
import { useRouter } from 'vue-router'

const props = withDefaults(
  defineProps<{
    title: string
    subtitle: string
    to: string
    icon: Component
    variant: 'amber' | 'cyan' | 'rose' | 'sky'
    disabled?: boolean
    /** 2 / 3 / 6 列 */
    gridSpan?: 2 | 3 | 6
  }>(),
  { disabled: false, gridSpan: 2 },
)

const router = useRouter()

const go = () => {
  if (props.disabled) return
  void router.push(props.to)
}
</script>

<style scoped>
.icon-tile {
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  border: none;
  font: inherit;
  color: inherit;
}

.icon-tile--half {
  min-height: 128px;
  padding: 24px 20px;
}

.icon-tile--third {
  min-height: 108px;
  padding: 16px 10px;
}

.icon-tile--third .icon-tile-title {
  font-size: 0.8125rem;
}

.icon-tile--third .icon-tile-icon {
  width: 40px;
  height: 40px;
  margin-bottom: 6px;
}

.icon-tile--third .icon-tile-glyph {
  font-size: 17px;
}

.icon-tile--third .icon-tile-sub {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  font-size: 0.65rem;
}

.icon-tile:disabled {
  cursor: not-allowed;
  opacity: 0.55;
  transform: none;
}

.icon-tile-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
  color: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.icon-tile-icon--amber {
  background: linear-gradient(135deg, #f59e0b, #ea580c);
}
.icon-tile-icon--cyan {
  background: linear-gradient(135deg, #06b6d4, #0d9488);
}
.icon-tile-icon--rose {
  background: linear-gradient(135deg, #f43f5e, #db2777);
}
.icon-tile-icon--sky {
  background: linear-gradient(135deg, #0ea5e9, #0891b2);
}

.icon-tile-glyph {
  font-size: 20px;
}

.icon-tile-title {
  margin: 0;
  font-size: 0.875rem;
  font-weight: 800;
  color: var(--text-heading);
}

.icon-tile-sub {
  margin: 4px 0 0;
  font-size: 10px;
  color: var(--text-muted);
}
</style>
