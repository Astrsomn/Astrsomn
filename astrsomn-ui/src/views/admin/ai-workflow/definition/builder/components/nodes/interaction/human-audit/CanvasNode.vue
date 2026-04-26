<template>
  <BaseCanvasNode
    :data="data"
    fallback-label="人工审核"
    fallback-description="流程挂起等待人工确认"
    subtitle="Human Loop"
    icon-text="A"
    icon-background="#fef3c7"
    icon-color="#b45309"
    :theme="{
      borderColor: '#fde68a',
      background: '#ffffff',
      titleColor: '#0f172a',
      descColor: '#475569'
    }"
  >
    <div class="avatars">
      <span>JD</span>
      <span>ML</span>
      <span>+</span>
    </div>
  </BaseCanvasNode>
</template>
<script setup lang="ts">
import BaseCanvasNode from '../../shared/BaseCanvasNode.vue'
import type { WorkflowNodeViewData } from '../../shared/node-props'
defineProps<{ data?: WorkflowNodeViewData }>()
</script>

<style scoped>
.avatars {
  display: flex;
  gap: 6px;
}
.avatars span {
  width: 22px;
  height: 22px;
  border-radius: 99px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e2e8f0;
  font-size: 9px;
  color: #475569;
  background: #f8fafc;
}
</style>

