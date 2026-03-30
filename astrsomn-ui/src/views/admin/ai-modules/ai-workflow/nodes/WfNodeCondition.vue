<template>
  <div class="wf-node wf-node--condition" :class="{ 'wf-node--selected': selected }">
    <Handle id="in" class="wf-handle" type="target" :position="Position.Top" />
    <div class="wf-node__chip">条件</div>
    <div class="wf-node__title">{{ displayLabel }}</div>
    <div v-if="cond" class="wf-node__meta"><code>{{ cond }}</code></div>
    <div v-else class="wf-node__placeholder">在右侧填写 conditionKey</div>
    <Handle id="out" class="wf-handle" type="source" :position="Position.Bottom" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Handle, Position } from '@vue-flow/core'
import type { NodeProps } from '@vue-flow/core'

type CondData = { label?: string; conditionKey?: string; kind?: string }

const props = defineProps<NodeProps<CondData>>()

const displayLabel = computed(() => {
  const d = props.data || {}
  return String(d.label || '条件')
})

const cond = computed(() => {
  const k = props.data?.conditionKey
  return k ? String(k) : ''
})
</script>

<style scoped>
.wf-node {
  min-width: 156px;
  max-width: 220px;
  padding: 10px 12px 14px;
  border-radius: 10px;
  border: 1px solid color-mix(in srgb, var(--warning, #f59e0b) 55%, var(--border-subtle, #444));
  background: linear-gradient(
    145deg,
    color-mix(in srgb, var(--warning, #f59e0b) 12%, var(--bg-card, #141414)) 0%,
    var(--bg-card, #141414) 100%
  );
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.35);
  font-size: 13px;
  color: var(--text-primary, #eee);
}
.wf-node--selected {
  border-color: color-mix(in srgb, var(--warning, #fbbf24) 80%, transparent);
  box-shadow: 0 0 0 1px color-mix(in srgb, var(--warning, #f59e0b) 40%, transparent),
    0 6px 20px rgba(0, 0, 0, 0.4);
}
.wf-node__chip {
  display: inline-block;
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: color-mix(in srgb, var(--warning, #fcd34d) 95%, #fff);
  margin-bottom: 6px;
}
.wf-node__title {
  font-weight: 600;
  line-height: 1.35;
}
.wf-node__meta {
  margin-top: 8px;
}
.wf-node__meta code {
  display: block;
  padding: 4px 6px;
  border-radius: 6px;
  background: rgba(0, 0, 0, 0.35);
  font-size: 11px;
  word-break: break-all;
}
.wf-node__placeholder {
  margin-top: 8px;
  font-size: 11px;
  color: var(--text-muted, #6b7280);
  font-style: italic;
}
.wf-handle {
  width: 10px;
  height: 10px;
  border: 2px solid var(--bg-card, #141414);
  background: color-mix(in srgb, var(--warning, #f59e0b) 85%, #fff);
}
</style>
