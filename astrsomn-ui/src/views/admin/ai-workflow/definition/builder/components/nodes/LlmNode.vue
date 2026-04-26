<template>
  <div class="wf-node wf-node-llm">
    <Handle type="target" :position="Position.Left" />
    <Handle type="source" :position="Position.Right" />
    <div class="wf-node-title">{{ data?.label || 'LLM' }}</div>
    <div class="wf-node-desc">{{ data?.description || '大模型调用' }}</div>
  </div>
</template>

<script setup lang="ts">
import { Handle, Position } from '@vue-flow/core'

defineProps<{
  data?: {
    label?: string
    description?: string
  }
}>()
</script>

<style scoped>
.wf-node {
  min-width: 150px;
  border-radius: 10px;
  padding: 10px 12px;
  border: 1px solid #d8f3e3;
  background: #eefcf4;
}

.wf-node-title {
  font-size: 13px;
  font-weight: 600;
  color: #1e5c3a;
}

.wf-node-desc {
  margin-top: 6px;
  color: #348457;
  font-size: 12px;
}
</style>
