<template>
  <BaseCanvasNode
      :data="data"
      :source-handles="[{ id: 'branchA', style: { top: '35%' } }, { id: 'branchB', style: { top: '70%' } }]"
      :target-handles="[{ id: 'input' }]"
      :theme="{
      borderColor: '#ddd6fe',
      background: '#ffffff',
      titleColor: '#0f172a',
      descColor: '#475569'
    }"
      fallback-description="触发多个分支并汇聚"
      fallback-label="并行/汇聚"
      icon-background="#ede9fe"
      icon-color="#7c3aed"
      icon-text="P"
      subtitle="Parallel Join"
  >
    <div class="branches">分支 A / 分支 B</div>
  </BaseCanvasNode>
</template>

<script lang="ts" setup>
import BaseCanvasNode from '../../shared/BaseCanvasNode.vue'
import type {WorkflowNodeViewData} from '../../shared/node-props'

defineProps<{ data?: WorkflowNodeViewData }>()
</script>

<style scoped>
.branches {
  font-size: 11px;
  color: #6d28d9;
  background: #f5f3ff;
  border: 1px solid #ddd6fe;
  border-radius: 10px;
  padding: 8px;
}
</style>

