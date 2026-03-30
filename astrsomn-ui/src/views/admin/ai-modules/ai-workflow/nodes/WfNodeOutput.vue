<template>
  <div class="wf-node wf-node--output" :class="{ 'wf-node--selected': selected }">
    <Handle id="in" class="wf-handle" type="target" :position="Position.Top" />
    <div class="wf-node__chip">出口</div>
    <div class="wf-node__title">{{ displayLabel }}</div>
    <div class="wf-node__hint">流程结束，汇总输出</div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Handle, Position } from '@vue-flow/core'
import type { NodeProps } from '@vue-flow/core'

const props = defineProps<NodeProps<Record<string, unknown>>>()

const displayLabel = computed(() => {
  const d = props.data || {}
  return String(d.label || '结束')
})
</script>

<style scoped>
.wf-node {
  min-width: 148px;
  max-width: 220px;
  padding: 14px 12px 10px;
  border-radius: 10px;
  border: 1px solid color-mix(in srgb, var(--danger, #f43f5e) 50%, var(--border-subtle, #444));
  background: linear-gradient(
    145deg,
    color-mix(in srgb, var(--danger, #f43f5e) 12%, var(--bg-card, #141414)) 0%,
    var(--bg-card, #141414) 100%
  );
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.35);
  font-size: 13px;
  color: var(--text-primary, #eee);
}
.wf-node--selected {
  border-color: color-mix(in srgb, var(--danger, #f43f5e) 85%, transparent);
  box-shadow: 0 0 0 1px color-mix(in srgb, var(--danger, #f43f5e) 40%, transparent),
    0 6px 20px rgba(0, 0, 0, 0.4);
}
.wf-node__chip {
  display: inline-block;
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: color-mix(in srgb, var(--danger, #f43f5e) 90%, #fff);
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
  background: color-mix(in srgb, var(--danger, #f43f5e) 85%, #fff);
}
</style>
