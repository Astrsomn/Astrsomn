<template>
  <div class="wf-node wf-node--task" :class="{ 'wf-node--selected': selected }">
    <Handle id="in" class="wf-handle wf-handle--in" type="target" :position="Position.Top" />
    <div class="wf-node__chip">任务 · 模型</div>
    <div class="wf-node__title">{{ displayLabel }}</div>
    <div v-if="agentLine" class="wf-node__meta">
      <code>{{ agentLine }}</code>
    </div>
    <div v-if="modelLine" class="wf-node__sub">{{ modelLine }}</div>
    <div v-else class="wf-node__placeholder">未绑定智能体：在右侧或左侧资源库选择</div>
    <Handle id="out" class="wf-handle wf-handle--out" type="source" :position="Position.Bottom" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Handle, Position } from '@vue-flow/core'
import type { NodeProps } from '@vue-flow/core'

type TaskData = {
  label?: string
  agentKey?: string
  agentName?: string
  modelName?: string
  kind?: string
}

const props = defineProps<NodeProps<TaskData>>()

const displayLabel = computed(() => {
  const d = props.data || {}
  return String(d.label || '任务')
})

const agentLine = computed(() => {
  const d = props.data || {}
  if (d.agentKey) return String(d.agentKey)
  return ''
})

const modelLine = computed(() => {
  const d = props.data || {}
  const name = d.agentName ? String(d.agentName) : ''
  const model = d.modelName ? String(d.modelName) : ''
  if (name && model) return `${name} · ${model}`
  if (model) return model
  if (name) return name
  return ''
})
</script>

<style scoped>
.wf-node {
  min-width: 168px;
  max-width: 240px;
  padding: 10px 12px 14px;
  border-radius: 10px;
  border: 1px solid color-mix(in srgb, var(--primary, #6366f1) 55%, var(--border-subtle, #444));
  background: linear-gradient(
    145deg,
    color-mix(in srgb, var(--primary, #6366f1) 14%, var(--bg-card, #141414)) 0%,
    var(--bg-card, #141414) 100%
  );
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.35);
  font-size: 13px;
  color: var(--text-primary, #eee);
}
.wf-node--selected {
  border-color: color-mix(in srgb, var(--primary, #6366f1) 90%, transparent);
  box-shadow: 0 0 0 1px color-mix(in srgb, var(--primary, #6366f1) 45%, transparent),
    0 6px 20px rgba(0, 0, 0, 0.4);
}
.wf-node__chip {
  display: inline-block;
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.04em;
  text-transform: uppercase;
  color: color-mix(in srgb, var(--primary, #a5b4fc) 95%, #fff);
  margin-bottom: 6px;
}
.wf-node__title {
  font-weight: 600;
  line-height: 1.35;
}
.wf-node__meta {
  margin-top: 8px;
  font-size: 11px;
}
.wf-node__meta code {
  display: block;
  padding: 4px 6px;
  border-radius: 6px;
  background: rgba(0, 0, 0, 0.35);
  color: color-mix(in srgb, var(--primary, #c7d2fe) 90%, #fff);
  word-break: break-all;
}
.wf-node__sub {
  margin-top: 6px;
  font-size: 11px;
  color: var(--text-secondary, #9ca3af);
  line-height: 1.35;
}
.wf-node__placeholder {
  margin-top: 8px;
  font-size: 11px;
  color: var(--text-muted, #6b7280);
  font-style: italic;
  line-height: 1.35;
}
.wf-handle {
  width: 10px;
  height: 10px;
  border: 2px solid var(--bg-card, #141414);
}
.wf-handle--in {
  background: color-mix(in srgb, var(--primary, #818cf8) 90%, #fff);
}
.wf-handle--out {
  background: color-mix(in srgb, var(--primary, #6366f1) 90%, #fff);
}
</style>
