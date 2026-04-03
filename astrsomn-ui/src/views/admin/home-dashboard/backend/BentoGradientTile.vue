<template>
  <button
    type="button"
    class="grad-tile"
    :class="[
      embedded
        ? 'grad-tile--embedded'
        : [
            gridSpan === 6 ? 'bento-span-6-1' : gridSpan === 3 ? 'bento-span-3-1' : 'bento-span-2-1',
            { 'grad-tile--half': gridSpan === 6, 'grad-tile--third': gridSpan === 3 },
          ],
      variant === 'market' ? 'grad-tile--market' : 'grad-tile--apps',
    ]"
    :disabled="disabled"
    @click="go"
  >
    <div class="grad-tile-icon">
      <component :is="icon" class="grad-tile-glyph" />
    </div>
    <p class="grad-tile-title">{{ title }}</p>
    <p class="grad-tile-sub">{{ subtitle }}</p>
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
    variant: 'market' | 'apps'
    disabled?: boolean
    gridSpan?: 2 | 3 | 6
    embedded?: boolean
  }>(),
  { disabled: false, gridSpan: 2, embedded: false },
)

const router = useRouter()

const go = () => {
  if (props.disabled) return
  void router.push(props.to)
}
</script>

<style scoped>
.grad-tile--embedded {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
}

.grad-tile {
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.45);
  cursor: pointer;
  font: inherit;
  transition:
    transform 0.25s ease,
    box-shadow 0.25s ease;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.06);
}

.grad-tile--half {
  min-height: 128px;
  padding: 24px 20px;
}

.grad-tile--third {
  min-height: 108px;
  padding: 16px 10px;
}

.grad-tile--third .grad-tile-title {
  font-size: 0.8125rem;
}

.grad-tile--third .grad-tile-sub {
  font-size: 0.65rem;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.grad-tile:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 12px 24px -8px rgba(0, 0, 0, 0.12);
}

.grad-tile:disabled {
  cursor: not-allowed;
  opacity: 0.55;
}

.grad-tile--market {
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
  color: #1e293b;
}

.grad-tile--apps {
  background: linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%);
  color: #1e293b;
}

.grad-tile-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
  background: rgba(255, 255, 255, 0.35);
  backdrop-filter: blur(4px);
}

.grad-tile--market .grad-tile-icon {
  color: #ff6b6b;
}

.grad-tile--apps .grad-tile-icon {
  color: #0ea5e9;
}

.grad-tile-glyph {
  font-size: 18px;
}

.grad-tile-title {
  margin: 0;
  font-size: 0.9rem;
  font-weight: 700;
  color: #1e293b;
}

.grad-tile-sub {
  margin: 4px 0 0;
  font-size: 0.7rem;
  color: rgba(30, 41, 59, 0.72);
}
</style>
