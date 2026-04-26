<template>
  <div class="wf-node wf-node-condition">
    <Handle type="target" :position="Position.Left" />
    <Handle id="true" type="source" :position="Position.Right" :style="{ top: '35%' }" />
    <Handle id="false" type="source" :position="Position.Right" :style="{ top: '70%' }" />
    <div class="wf-node-title">{{ data?.label || '条件分支' }}</div>
    <div class="wf-node-desc">{{ data?.description || '根据条件分流' }}</div>
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
  min-width: 160px;
  border-radius: 10px;
  padding: 10px 12px;
  border: 1px solid #eadcff;
  background: #f8f2ff;
}

.wf-node-title {
  font-size: 13px;
  font-weight: 600;
  color: #583e8d;
}

.wf-node-desc {
  margin-top: 6px;
  color: #7853c2;
  font-size: 12px;
}
</style>
