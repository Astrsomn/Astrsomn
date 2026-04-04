<template>
  <div class="toolbar-segmented-btn">
    <button
      v-for="(btn, index) in buttons"
      :key="index"
      :class="[
        'seg-btn',
        btn.type === 'primary' ? 'seg-btn-primary' : 'seg-btn-default',
        { 'seg-btn-first': index === 0, 'seg-btn-last': index === buttons.length - 1 }
      ]"
      @click="btn.onClick"
    >
      <component v-if="btn.icon" :is="btn.icon" class="seg-btn-icon" />
      <span>{{ btn.label }}</span>
    </button>
  </div>
</template>

<script setup lang="ts">
import type { Component } from 'vue'

export interface SegmentedButton {
  label: string
  type?: 'default' | 'primary'
  icon?: Component
  onClick?: () => void
}

defineProps<{
  buttons: SegmentedButton[]
}>()
</script>

<style scoped>
.toolbar-segmented-btn {
  display: inline-flex;
  align-items: center;
  gap: 0;
  border-radius: var(--radius-pro, 30px);
  overflow: hidden;
  box-shadow: var(--shadow-overview);
}

.seg-btn {
  all: unset;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 44px;
  padding: 0 22px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid var(--border-subtle, rgba(255, 255, 255, 0.06));
  white-space: nowrap;
}

.seg-btn-default {
  background: color-mix(in srgb, var(--bg-elevated, #111a2e) 60%, transparent);
  color: var(--text-secondary, #7b93b6);
}

.seg-btn-default:hover {
  background: color-mix(in srgb, var(--bg-elevated, #111a2e) 80%, transparent);
  color: var(--text-heading, #ffffff);
  border-color: color-mix(in srgb, var(--primary) 32%, var(--border-subtle));
}

.seg-btn-primary {
  background: var(--primary, #3b82f6);
  color: #ffffff;
  border-color: var(--primary, #3b82f6);
}

.seg-btn-primary:hover {
  background: var(--primary-light, #60a5fa);
  border-color: var(--primary-light, #60a5fa);
}

.seg-btn-first {
  border-right: none;
}

.seg-btn:not(.seg-btn-first):not(.seg-btn-last) {
  border-right: none;
}

.seg-btn-icon {
  font-size: 16px;
}
</style>
