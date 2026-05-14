<template>
  <BaseCanvasNode
      :data="data"
      :theme="{
      borderColor: '#e9d5ff',
      background: '#ffffff',
      titleColor: '#0f172a',
      descColor: '#475569'
    }"
      badge="POST"
      badge-background="#7c3aed"
      badge-color="#ffffff"
      fallback-description="调用外部 API 接口"
      fallback-label="HTTP 请求"
      icon-background="#f3e8ff"
      icon-color="#7c3aed"
      icon-text="H"
      subtitle="Restful API"
  >
    <div class="url">https://api.system.io/v1/sync</div>
  </BaseCanvasNode>
</template>
<script lang="ts" setup>
import BaseCanvasNode from '../../shared/BaseCanvasNode.vue'
import type {WorkflowNodeViewData} from '../../shared/node-props'

defineProps<{ data?: WorkflowNodeViewData }>()
</script>

<style scoped>
.url {
  font-size: 10px;
  color: #cbd5e1;
  background: #0f172a;
  border-radius: 10px;
  padding: 8px;
  font-family: Consolas, Menlo, monospace;
}
</style>

