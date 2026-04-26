<template>
  <BaseCanvasNode
    :data="data"
    fallback-label="条件分支"
    fallback-description="根据表达式进行路由"
    :source-handles="[{ id: 'true', style: { top: '35%' } }, { id: 'false', style: { top: '70%' } }]"
    subtitle="Condition"
    icon-text="C"
    icon-background="#fff7ed"
    icon-color="#ea580c"
    :theme="{
      borderColor: '#fed7aa',
      background: '#ffffff',
      titleColor: '#0f172a',
      descColor: '#475569'
    }"
  >
    <div class="route-item">包含 \"投诉\"</div>
    <div class="route-item">默认路径</div>
  </BaseCanvasNode>
</template>
<script setup lang="ts">
import BaseCanvasNode from '../../shared/BaseCanvasNode.vue'
import type { WorkflowNodeViewData } from '../../shared/node-props'
defineProps<{ data?: WorkflowNodeViewData }>()
</script>

<style scoped>
.route-item {
  border: 1px solid #f1f5f9;
  border-radius: 10px;
  padding: 8px;
  font-size: 11px;
  color: #475569;
}
.route-item + .route-item {
  margin-top: 6px;
}
</style>

