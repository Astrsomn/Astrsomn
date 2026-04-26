<template>
  <BaseCanvasNode
    :data="data"
    fallback-label="工具 (Tools)"
    fallback-description="调用预定义工具/插件能力"
    subtitle="Tools"
    icon-text="T"
    icon-background="#fff7ed"
    icon-color="#ea580c"
    :theme="{
      borderColor: '#fed7aa',
      background: '#ffffff',
      titleColor: '#0f172a',
      descColor: '#475569'
    }"
  >
    <div class="tool-tip">Function Calling / Plugin Adapter</div>
  </BaseCanvasNode>
</template>
<script setup lang="ts">
import BaseCanvasNode from '../../shared/BaseCanvasNode.vue'
import type { WorkflowNodeViewData } from '../../shared/node-props'
defineProps<{ data?: WorkflowNodeViewData }>()
</script>

<style scoped>
.tool-tip {
  font-size: 11px;
  color: #c2410c;
  background: #fff7ed;
  border: 1px solid #fdba74;
  border-radius: 10px;
  padding: 8px;
}
</style>

