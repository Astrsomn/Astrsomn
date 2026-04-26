<template>
  <BaseCanvasNode
    :data="data"
    fallback-label="多模态识别"
    fallback-description="图像识别与结构化提取"
    subtitle="Vision"
    icon-text="V"
    icon-background="#fdf2f8"
    icon-color="#db2777"
    :theme="{
      borderColor: '#fbcfe8',
      background: '#ffffff',
      titleColor: '#0f172a',
      descColor: '#475569'
    }"
  >
    <div class="thumbs">
      <div class="thumb" />
      <div class="thumb thumb-empty">+</div>
    </div>
  </BaseCanvasNode>
</template>
<script setup lang="ts">
import BaseCanvasNode from '../../shared/BaseCanvasNode.vue'
import type { WorkflowNodeViewData } from '../../shared/node-props'
defineProps<{ data?: WorkflowNodeViewData }>()
</script>

<style scoped>
.thumbs {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}
.thumb {
  aspect-ratio: 16 / 9;
  border-radius: 8px;
  background: #f1f5f9;
}
.thumb-empty {
  border: 1px dashed #cbd5e1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  font-weight: 700;
}
</style>

