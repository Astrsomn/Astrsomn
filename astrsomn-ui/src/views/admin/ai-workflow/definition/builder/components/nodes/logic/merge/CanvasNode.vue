<template>
  <BaseCanvasNode
    :data="data"
    fallback-label="变量聚合 (Merge)"
    fallback-description="合并多个分支变量"
    :target-handles="[{ id: 'left', style: { top: '35%' } }, { id: 'right', style: { top: '70%' } }]"
    :source-handles="[{ id: 'result' }]"
    subtitle="Merge"
    icon-text="MG"
    icon-background="#eef2ff"
    icon-color="#4338ca"
    :theme="{
      borderColor: '#c7d2fe',
      background: '#ffffff',
      titleColor: '#0f172a',
      descColor: '#475569'
    }"
  >
    <div class="merge-tip">将多路输出聚合为单路结果</div>
  </BaseCanvasNode>
</template>
<script setup lang="ts">
import BaseCanvasNode from '../../shared/BaseCanvasNode.vue'
import type { WorkflowNodeViewData } from '../../shared/node-props'
defineProps<{ data?: WorkflowNodeViewData }>()
</script>

<style scoped>
.merge-tip {
  font-size: 11px;
  color: #4338ca;
  background: #eef2ff;
  border: 1px solid #c7d2fe;
  border-radius: 10px;
  padding: 8px;
}
</style>

