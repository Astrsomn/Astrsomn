<template>
  <BaseCanvasNode
    :data="data"
    fallback-label="结束"
    fallback-description="流程出口"
    :target-handles="[{ id: undefined }]"
    :source-handles="[]"
    subtitle="Output Result"
    icon-text="E"
    icon-background="#0f172a"
    icon-color="#ffffff"
    :theme="{
      borderColor: '#e2e8f0',
      background: '#ffffff',
      titleColor: '#0f172a',
      descColor: '#475569'
    }"
  >
    <div class="ready">
      <span class="dot" />
      <span>Stream Ready</span>
    </div>
  </BaseCanvasNode>
</template>

<script setup lang="ts">
import BaseCanvasNode from '../../shared/BaseCanvasNode.vue'
import type { WorkflowNodeViewData } from '../../shared/node-props'

defineProps<{ data?: WorkflowNodeViewData }>()
</script>

<style scoped>
.ready {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #059669;
  font-size: 11px;
  font-weight: 700;
}
.dot {
  width: 6px;
  height: 6px;
  border-radius: 99px;
  background: #10b981;
}
</style>

