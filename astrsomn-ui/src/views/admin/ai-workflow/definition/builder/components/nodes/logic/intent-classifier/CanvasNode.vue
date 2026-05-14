<template>
  <BaseCanvasNode
      :data="data"
      :source-handles="[{ id: 'classA', style: { top: '35%' } }, { id: 'classB', style: { top: '70%' } }]"
      :theme="{
      borderColor: '#a5f3fc',
      background: '#ffffff',
      titleColor: '#0f172a',
      descColor: '#475569'
    }"
      fallback-description="使用 LLM 对意图分类路由"
      fallback-label="问题分类器"
      icon-background="#ecfeff"
      icon-color="#0e7490"
      icon-text="IC"
      subtitle="Intent Classifier"
  >
    <div class="tag-row">问候 / 售后 / 投诉</div>
  </BaseCanvasNode>
</template>
<script lang="ts" setup>
import BaseCanvasNode from '../../shared/BaseCanvasNode.vue'
import type {WorkflowNodeViewData} from '../../shared/node-props'

defineProps<{ data?: WorkflowNodeViewData }>()
</script>

<style scoped>
.tag-row {
  font-size: 11px;
  color: #0e7490;
  background: #ecfeff;
  border: 1px solid #a5f3fc;
  border-radius: 10px;
  padding: 8px;
}
</style>

