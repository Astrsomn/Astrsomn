<template>
  <div class="wf-node wf-node-end">
    <Handle type="target" :position="Position.Left" />
    <div class="wf-node-title">{{ data?.label || '结束' }}</div>
    <div class="wf-node-desc">{{ data?.description || '流程出口' }}</div>
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
  border: 1px solid #ffd6d6;
  background: #fff1f1;
}

.wf-node-title {
  font-size: 13px;
  font-weight: 600;
  color: #7e2424;
}

.wf-node-desc {
  margin-top: 6px;
  color: #a74444;
  font-size: 12px;
}
</style>
