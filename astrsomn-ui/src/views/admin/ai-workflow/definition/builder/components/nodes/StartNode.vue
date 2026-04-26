<template>
  <div class="wf-node wf-node-start">
    <Handle type="source" :position="Position.Right" />
    <div class="wf-node-title">{{ data?.label || '开始' }}</div>
    <div class="wf-node-desc">{{ data?.description || '流程入口' }}</div>
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
  min-width: 140px;
  border-radius: 10px;
  padding: 10px 12px;
  border: 1px solid #d9e4ff;
  background: #edf3ff;
}

.wf-node-title {
  font-size: 13px;
  font-weight: 600;
  color: #173463;
}

.wf-node-desc {
  margin-top: 6px;
  color: #45608e;
  font-size: 12px;
}
</style>
