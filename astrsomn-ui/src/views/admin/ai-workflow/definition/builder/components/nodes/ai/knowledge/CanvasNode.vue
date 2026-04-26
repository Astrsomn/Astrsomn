<template>
  <BaseCanvasNode
    :data="data"
    fallback-label="知识库检索"
    fallback-description="RAG 检索相关上下文"
    subtitle="Vector Search"
    icon-text="K"
    icon-background="#ecfeff"
    icon-color="#0891b2"
    :theme="{
      borderColor: '#bae6fd',
      background: '#ffffff',
      titleColor: '#0f172a',
      descColor: '#475569'
    }"
  >
    <div class="score-head">
      <span>相似度分数</span>
      <span>85%</span>
    </div>
    <div class="bar"><span /></div>
  </BaseCanvasNode>
</template>
<script setup lang="ts">
import BaseCanvasNode from '../../shared/BaseCanvasNode.vue'
import type { WorkflowNodeViewData } from '../../shared/node-props'
defineProps<{ data?: WorkflowNodeViewData }>()
</script>

<style scoped>
.score-head {
  display: flex;
  justify-content: space-between;
  font-size: 10px;
  color: #0e7490;
}
.bar {
  margin-top: 8px;
  background: #e2e8f0;
  height: 6px;
  border-radius: 999px;
  overflow: hidden;
}
.bar span {
  display: block;
  height: 100%;
  width: 85%;
  background: #06b6d4;
}
</style>

