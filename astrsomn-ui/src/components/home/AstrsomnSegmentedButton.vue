<template>
  <div class="toolbar-segmented-btn">
    <button
      v-for="(btn, index) in buttons"
      :key="index"
      type="button"
      :disabled="Boolean(btn.disabled || btn.loading)"
      :class="[
        'seg-btn',
        btn.type ? `seg-btn-${btn.type}` : 'seg-btn-default',
        {
          'seg-btn-first': index === 0,
          'seg-btn-last': index === buttons.length - 1,
          'seg-btn-plain': btn.plain
        }
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
  type?: 'default' | 'primary' | 'danger' | 'warning' | 'success'
  icon?: Component
  disabled?: boolean
  loading?: boolean
  plain?: boolean
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
  border: 1px solid var(--border-default);
}

.seg-btn {
  all: unset;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 40px;
  padding: 0 12px;
  font-size: 8px;

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

.seg-btn-danger {
  background: var(--error, #ef4444);
  color: #ffffff;
  border-color: var(--error, #ef4444);
}

.seg-btn-danger:hover:not(:disabled) {
  background: var(--error-light, #f87171);
  border-color: var(--error-light, #f87171);
}

.seg-btn-warning {
  background: var(--warning, #f59e0b);
  color: #ffffff;
  border-color: var(--warning, #f59e0b);
}

.seg-btn-warning:hover:not(:disabled) {
  background: var(--warning-light, #fbbf24);
  border-color: var(--warning-light, #fbbf24);
}

.seg-btn-success {
  background: var(--success, #10b981);
  color: #ffffff;
  border-color: var(--success, #10b981);
}

.seg-btn-success:hover:not(:disabled) {
  background: var(--success-light, #34d399);
  border-color: var(--success-light, #34d399);
}

.seg-btn-plain {
  background: transparent !important;
  border-color: var(--border-subtle, rgba(255, 255, 255, 0.06)) !important;
}

.seg-btn-plain.seg-btn-default {
  color: var(--text-secondary, #7b93b6);
}

.seg-btn-plain.seg-btn-primary {
  color: var(--primary, #3b82f6);
}

.seg-btn-plain.seg-btn-danger {
  color: var(--error, #ef4444);
}

.seg-btn-plain.seg-btn-warning {
  color: var(--warning, #f59e0b);
}

.seg-btn-plain.seg-btn-success {
  color: var(--success, #10b981);
}

.seg-btn-plain:hover:not(:disabled) {
  background: color-mix(in srgb, currentColor 8%, transparent) !important;
  border-color: color-mix(in srgb, currentColor 32%, var(--border-subtle)) !important;
}

.seg-btn:disabled {
  cursor: not-allowed;
  opacity: 0.55;
}

.seg-btn-default:disabled {
  color: var(--text-muted, #64748b);
}

.seg-btn-primary:disabled,
.seg-btn-danger:disabled,
.seg-btn-warning:disabled,
.seg-btn-success:disabled {
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
