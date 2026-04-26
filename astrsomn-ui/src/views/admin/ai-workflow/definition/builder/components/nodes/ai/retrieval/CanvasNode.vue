<template>
  <BaseCanvasNode
    :data="data"
    fallback-label="知识库 (Retrieval)"
    fallback-description="向量检索相关知识片段"
    subtitle="Retrieval"
    icon-text="R"
    icon-background="#eef2ff"
    icon-color="#4f46e5"
    :theme="{
      borderColor: '#c7d2fe',
      background: '#ffffff',
      titleColor: '#0f172a',
      descColor: '#475569'
    }"
  >
    <div class="retrieval-tip">RAG: TopK + 相似度阈值</div>
  </BaseCanvasNode>
</template>

<script setup lang="ts">
import BaseCanvasNode from '../../shared/BaseCanvasNode.vue'
import type { WorkflowNodeViewData } from '../../shared/node-props'
defineProps<{ data?: WorkflowNodeViewData }>()
</script>

<style scoped>
.retrieval-tip {
  font-size: 11px;
  color: #4f46e5;
  background: #eef2ff;
  border: 1px solid #c7d2fe;
  border-radius: 10px;
  padding: 8px;
}
</style>

