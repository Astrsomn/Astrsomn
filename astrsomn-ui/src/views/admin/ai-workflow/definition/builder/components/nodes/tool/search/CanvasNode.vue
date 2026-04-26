<template>
  <BaseCanvasNode
    :data="data"
    fallback-label="搜索"
    fallback-description="联网检索实时信息"
    subtitle="Search"
    icon-text="Q"
    icon-background="#ffedd5"
    icon-color="#c2410c"
    :theme="{
      borderColor: '#fed7aa',
      background: '#ffffff',
      titleColor: '#0f172a',
      descColor: '#475569'
    }"
  >
    <div class="tags">
      <span>标题</span>
      <span>链接</span>
      <span>摘要</span>
    </div>
  </BaseCanvasNode>
</template>
<script setup lang="ts">
import BaseCanvasNode from '../../shared/BaseCanvasNode.vue'
import type { WorkflowNodeViewData } from '../../shared/node-props'
defineProps<{ data?: WorkflowNodeViewData }>()
</script>

<style scoped>
.tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}
.tags span {
  font-size: 10px;
  color: #475569;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 7px;
  padding: 2px 6px;
}
</style>

