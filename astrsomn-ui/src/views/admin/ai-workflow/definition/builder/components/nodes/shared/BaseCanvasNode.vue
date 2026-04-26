<template>
  <div class="wf-node" :class="{ 'wf-node-active': active }" :style="themeStyles">
    <Handle
      v-for="handle in normalizedTargetHandles"
      :id="handle.id"
      :key="`target-${handle.id || 'default'}`"
      type="target"
      :position="Position.Left"
      class="wf-handle"
      :connectable="true"
      :style="handle.style"
    />
    <Handle
      v-for="handle in normalizedSourceHandles"
      :id="handle.id"
      :key="`source-${handle.id || 'default'}`"
      type="source"
      :position="Position.Right"
      class="wf-handle"
      :connectable="true"
      :style="handle.style"
    />
    <div class="wf-node-header">
      <div class="wf-header-left">
        <div class="wf-icon-box" :style="{ background: iconBackground, color: iconColor }">
          {{ iconText }}
        </div>
        <div class="wf-title-wrap">
          <div class="wf-node-title">{{ data?.label || fallbackLabel }}</div>
          <div class="wf-node-subtitle">{{ subtitle }}</div>
        </div>
      </div>
      <span v-if="badge" class="wf-badge" :style="{ background: badgeBackground, color: badgeColor }">{{ badge }}</span>
    </div>
    <div class="wf-node-content">
      <slot>
        <div class="wf-node-desc">{{ data?.description || fallbackDescription }}</div>
      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Handle, Position } from '@vue-flow/core'
import type { CSSProperties } from 'vue'
import type { WorkflowNodeViewData } from './node-props'

type NodeHandle = {
  id?: string
  style?: CSSProperties
}

const props = withDefaults(
  defineProps<{
    data?: WorkflowNodeViewData
    fallbackLabel: string
    fallbackDescription: string
    targetHandles?: NodeHandle[]
    sourceHandles?: NodeHandle[]
    theme?: {
      borderColor: string
      background: string
      titleColor: string
      descColor: string
    }
    subtitle?: string
    badge?: string
    iconText?: string
    iconBackground?: string
    iconColor?: string
    badgeBackground?: string
    badgeColor?: string
    active?: boolean
    minWidth?: string
  }>(),
  {
    targetHandles: () => [{ id: undefined }],
    sourceHandles: () => [{ id: undefined }],
    theme: () => ({
      borderColor: '#dbe3ee',
      background: '#f8fafc',
      titleColor: '#1e293b',
      descColor: '#475569'
    }),
    subtitle: 'Workflow Node',
    badge: '',
    iconText: 'N',
    iconBackground: '#eff6ff',
    iconColor: '#1d4ed8',
    badgeBackground: '#eff6ff',
    badgeColor: '#1d4ed8',
    active: false,
    minWidth: '240px'
  }
)

const normalizedTargetHandles = computed(() =>
  (props.targetHandles || []).map((handle) => ({
    ...handle,
    style: { ...handle.style, zIndex: 6 }
  }))
)

const normalizedSourceHandles = computed(() =>
  (props.sourceHandles || []).map((handle) => ({
    ...handle,
    style: { ...handle.style, zIndex: 6 }
  }))
)

const themeStyles = computed(() => ({
  '--wf-node-border': props.theme.borderColor,
  '--wf-node-bg': props.theme.background,
  '--wf-node-title': props.theme.titleColor,
  '--wf-node-desc': props.theme.descColor,
  '--wf-node-min-width': props.minWidth
}))
</script>

<style scoped>
.wf-node {
  min-width: var(--wf-node-min-width, 240px);
  border-radius: 16px;
  border: 1px solid var(--wf-node-border);
  background: var(--wf-node-bg);
  box-shadow: 0 4px 6px -1px rgba(15, 23, 42, 0.04), 0 2px 4px -1px rgba(15, 23, 42, 0.03);
  transition: all 0.25s ease;
  overflow: visible;
  cursor: pointer;
}

.wf-node:hover {
  transform: translateY(-2px);
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.18), 0 12px 26px -8px rgba(37, 99, 235, 0.22);
}

.wf-node-active {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.12), 0 10px 22px -8px rgba(59, 130, 246, 0.2);
}

.wf-node-header {
  padding: var(--wf-node-header-padding, 12px 14px 9px);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.wf-header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.wf-icon-box {
  width: var(--wf-node-icon-size, 30px);
  height: var(--wf-node-icon-size, 30px);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--wf-node-icon-font-size, 13px);
  font-weight: 700;
}

.wf-title-wrap {
  min-width: 0;
}

.wf-node-title {
  font-size: var(--wf-node-title-font-size, 12px);
  font-weight: 700;
  color: var(--wf-node-title);
}

.wf-node-subtitle {
  margin-top: 2px;
  font-size: var(--wf-node-subtitle-font-size, 9px);
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.wf-badge {
  font-size: 10px;
  line-height: 1;
  font-weight: 700;
  padding: var(--wf-node-badge-padding, 4px 8px);
  border-radius: 8px;
}

.wf-node-content {
  padding: var(--wf-node-content-padding, 0 14px 14px);
}

.wf-node-desc {
  margin-top: 2px;
  color: var(--wf-node-desc);
  font-size: var(--wf-node-desc-font-size, 11px);
  line-height: 1.45;
}

:deep(.wf-handle) {
  width: var(--wf-node-handle-size, 9px);
  height: var(--wf-node-handle-size, 9px);
  border-radius: 4px;
  border: 1px solid var(--wf-node-border);
  background: #fff;
  box-sizing: border-box;
}

:deep(.wf-handle:hover) {
  border-color: #2563eb;
  background: #2563eb;
}
</style>

