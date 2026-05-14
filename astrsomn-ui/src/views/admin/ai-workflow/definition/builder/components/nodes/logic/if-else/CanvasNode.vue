<template>
  <BaseCanvasNode
      :data="data"
      :source-handles="[{ id: 'true', style: { top: '35%' } }, { id: 'false', style: { top: '70%' } }]"
      :theme="{
      borderColor: '#fed7aa',
      background: '#ffffff',
      titleColor: '#0f172a',
      descColor: '#475569'
    }"
      fallback-description="按表达式结果选择路径"
      fallback-label="条件分支 (If/Else)"
      icon-background="#fff7ed"
      icon-color="#ea580c"
      icon-text="IF"
      subtitle="If Else"
  >
    <div class="route-item">满足条件 -> true</div>
    <div class="route-item">不满足 -> false</div>
  </BaseCanvasNode>
</template>
<script lang="ts" setup>
import BaseCanvasNode from '../../shared/BaseCanvasNode.vue'
import type {WorkflowNodeViewData} from '../../shared/node-props'

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

