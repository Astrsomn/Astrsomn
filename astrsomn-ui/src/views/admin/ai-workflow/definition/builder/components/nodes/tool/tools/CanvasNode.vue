<template>
  <BaseCanvasNode
      :data="data"
      :theme="{
      borderColor: '#fed7aa',
      background: '#ffffff',
      titleColor: '#0f172a',
      descColor: '#475569'
    }"
      fallback-description="调用预定义工具/插件能力"
      fallback-label="工具 (Tools)"
      icon-background="#fff7ed"
      icon-color="#ea580c"
      icon-text="T"
      subtitle="Tools"
  >
    <div class="tool-tip">Function Calling / Plugin Adapter</div>
  </BaseCanvasNode>
</template>
<script lang="ts" setup>
import BaseCanvasNode from '../../shared/BaseCanvasNode.vue'
import type {WorkflowNodeViewData} from '../../shared/node-props'

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

