<template>
  <BaseCanvasNode
    :data="data"
    fallback-label="输入增强"
    fallback-description="采集中间态补充信息"
    subtitle="Form Input"
    icon-text="F"
    icon-background="#dcfce7"
    icon-color="#15803d"
    :theme="{
      borderColor: '#bbf7d0',
      background: '#ffffff',
      titleColor: '#0f172a',
      descColor: '#475569'
    }"
  >
    <div class="fields">fields: email, phone, remark</div>
  </BaseCanvasNode>
</template>
<script setup lang="ts">
import BaseCanvasNode from '../../shared/BaseCanvasNode.vue'
import type { WorkflowNodeViewData } from '../../shared/node-props'
defineProps<{ data?: WorkflowNodeViewData }>()
</script>

<style scoped>
.fields {
  font-size: 11px;
  color: #166534;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-radius: 10px;
  padding: 8px;
}
</style>

