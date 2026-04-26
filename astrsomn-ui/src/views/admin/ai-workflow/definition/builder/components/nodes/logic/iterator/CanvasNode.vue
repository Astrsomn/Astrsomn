<template>
  <BaseCanvasNode
    :data="data"
    fallback-label="迭代器"
    fallback-description="循环处理列表数据"
    subtitle="Loop Controller"
    icon-text="I"
    icon-background="#f0fdfa"
    icon-color="#0f766e"
    :theme="{
      borderColor: '#99f6e4',
      background: '#ffffff',
      titleColor: '#0f172a',
      descColor: '#475569'
    }"
  >
    <div class="iter">
      <span class="dot" />
      <span v-pre>遍历数组 {{products}}</span>
    </div>
  </BaseCanvasNode>
</template>
<script setup lang="ts">
import BaseCanvasNode from '../../shared/BaseCanvasNode.vue'
import type { WorkflowNodeViewData } from '../../shared/node-props'
defineProps<{ data?: WorkflowNodeViewData }>()
</script>

<style scoped>
.iter {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  color: #0f766e;
}
.dot {
  width: 6px;
  height: 6px;
  border-radius: 99px;
  background: #14b8a6;
}
</style>

