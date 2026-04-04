<template>
  <div class="toolbar-segmented-btn">
    <button
      v-for="(btn, index) in buttons"
      :key="index"
      type="button"
      :disabled="Boolean(btn.disabled || btn.loading)"
      :class="[
        'seg-btn',
        btn.type === 'primary' ? 'seg-btn-primary' : 'seg-btn-default',
        { 'seg-btn-first': index === 0, 'seg-btn-last': index === buttons.length - 1 }
      ]"
      @click="btn.onClick"
    >
      <LoadingOutlined v-if="btn.loading" class="seg-btn-icon seg-btn-icon-spin" />
      <component v-else-if="btn.icon" :is="btn.icon" class="seg-btn-icon" />
      <span>{{ btn.label }}</span>
    </button>
  </div>
</template>

<script setup lang="ts">
import { LoadingOutlined } from '@ant-design/icons-vue'
import type { Component } from 'vue'

export interface SegmentedButton {
  label: string
  type?: 'default' | 'primary'
  icon?: Component
  disabled?: boolean
  loading?: boolean
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
  height: 50px;
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

.seg-btn-default:hover:not(:disabled) {
  background: color-mix(in srgb, var(--bg-elevated, #111a2e) 80%, transparent);
  color: var(--text-heading, #ffffff);
  border-color: color-mix(in srgb, var(--primary) 32%, var(--border-subtle));
}

.seg-btn-primary {
  background: var(--primary, #3b82f6);
  color: #ffffff;
  border-color: var(--primary, #3b82f6);
}

.seg-btn-primary:hover:not(:disabled) {
  background: var(--primary-light, #60a5fa);
  border-color: var(--primary-light, #60a5fa);
}

.seg-btn:disabled {
  cursor: not-allowed;
  opacity: 0.55;
}

.seg-btn-default:disabled {
  color: var(--text-muted, #64748b);
}

.seg-btn-primary:disabled {
  opacity: 0.65;
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

.seg-btn-icon-spin {
  animation: seg-btn-spin 0.85s linear infinite;
}

@keyframes seg-btn-spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
