<template>
  <div class="wf-node wf-node--input" :class="{ 'wf-node--selected': selected }">
    <div class="wf-node__chip">入口</div>
    <div class="wf-node__title">{{ displayLabel }}</div>
    <div class="wf-node__hint">用户消息进入流程</div>
    <Handle id="out" class="wf-handle" type="source" :position="Position.Bottom" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Handle, Position } from '@vue-flow/core'
import type { NodeProps } from '@vue-flow/core'

const props = defineProps<NodeProps<Record<string, unknown>>>()

const displayLabel = computed(() => {
  const d = props.data || {}
  return String(d.label || '开始')
})
</script>

<style scoped>
.wf-node {
  min-width: 148px;
  max-width: 220px;
  padding: 10px 12px 14px;
  border-radius: 10px;
  border: 1px solid color-mix(in srgb, var(--success, #22c55e) 55%, var(--border-subtle, #444));
  background: linear-gradient(
    145deg,
    color-mix(in srgb, var(--success, #22c55e) 12%, var(--bg-card, #141414)) 0%,
    var(--bg-card, #141414) 100%
  );
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.35);
  font-size: 13px;
  color: var(--text-primary, #eee);
}
.wf-node--selected {
  border-color: color-mix(in srgb, var(--success, #22c55e) 85%, transparent);
  box-shadow: 0 0 0 1px color-mix(in srgb, var(--success, #22c55e) 45%, transparent),
    0 6px 20px rgba(0, 0, 0, 0.4);
}
.wf-node__chip {
  display: inline-block;
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: color-mix(in srgb, var(--success, #22c55e) 90%, #fff);
  margin-bottom: 6px;
}
.wf-node__title {
  font-weight: 600;
  line-height: 1.35;
}
.wf-node__hint {
  margin-top: 6px;
  font-size: 11px;
  color: var(--text-secondary, #9ca3af);
  line-height: 1.3;
}
.wf-handle {
  width: 10px;
  height: 10px;
  border: 2px solid var(--bg-card, #141414);
  background: color-mix(in srgb, var(--success, #22c55e) 85%, #fff);
}
</style>
