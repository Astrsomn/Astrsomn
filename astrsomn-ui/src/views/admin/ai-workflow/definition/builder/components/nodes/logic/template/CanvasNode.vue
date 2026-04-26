<template>
  <BaseCanvasNode
    :data="data"
    fallback-label="变量聚合"
    fallback-description="模板拼接多变量结果"
    subtitle="Transformer"
    icon-text="T"
    icon-background="#f1f5f9"
    icon-color="#475569"
    :theme="{
      borderColor: '#cbd5e1',
      background: '#ffffff',
      titleColor: '#0f172a',
      descColor: '#475569'
    }"
  >
    <div class="flow">
      <span class="chip">JSON</span>
      <span class="arrow">-></span>
      <span class="chip dark">CSV</span>
    </div>
  </BaseCanvasNode>
</template>
<script setup lang="ts">
import BaseCanvasNode from '../../shared/BaseCanvasNode.vue'
import type { WorkflowNodeViewData } from '../../shared/node-props'
defineProps<{ data?: WorkflowNodeViewData }>()
</script>

<style scoped>
.flow {
  display: flex;
  align-items: center;
  gap: 6px;
}
.chip {
  flex: 1;
  text-align: center;
  padding: 6px 0;
  font-size: 10px;
  border-radius: 8px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #475569;
  font-weight: 700;
}
.chip.dark {
  background: #0f172a;
  border-color: #0f172a;
  color: #fff;
}
.arrow {
  font-size: 10px;
  color: #94a3b8;
}
</style>

