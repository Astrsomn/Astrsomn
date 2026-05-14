<template>
  <BaseCanvasNode
      :data="data"
      :theme="{
      borderColor: '#c7d2fe',
      background: '#ffffff',
      titleColor: '#0f172a',
      descColor: '#475569'
    }"
      fallback-description="运行 Python/JS 脚本"
      fallback-label="代码执行"
      icon-background="#e0e7ff"
      icon-color="#4338ca"
      icon-text="Py"
      subtitle="Sandbox"
  >
    <div class="file">
      <span class="size">1.2 KB</span>
      <span>data_cleaner.py</span>
    </div>
  </BaseCanvasNode>
</template>
<script lang="ts" setup>
import BaseCanvasNode from '../../shared/BaseCanvasNode.vue'
import type {WorkflowNodeViewData} from '../../shared/node-props'

defineProps<{ data?: WorkflowNodeViewData }>()
</script>

<style scoped>
.file {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  color: #475569;
}

.size {
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  padding: 2px 6px;
  font-size: 10px;
  font-weight: 700;
}
</style>

