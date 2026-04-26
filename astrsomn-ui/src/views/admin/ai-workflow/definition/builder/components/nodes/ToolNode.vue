<template>
  <div class="wf-node wf-node-tool">
    <Handle type="target" :position="Position.Left" />
    <Handle type="source" :position="Position.Right" />
    <div class="wf-node-title">{{ data?.label || 'Tool' }}</div>
    <div class="wf-node-desc">{{ data?.description || '工具节点' }}</div>
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
  border: 1px solid #ffe6bf;
  background: #fff6e8;
}

.wf-node-title {
  font-size: 13px;
  font-weight: 600;
  color: #7a4b06;
}

.wf-node-desc {
  margin-top: 6px;
  color: #9b681f;
  font-size: 12px;
}
</style>
