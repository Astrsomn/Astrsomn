<template>
  <BaseCanvasNode
      :data="data"
      :source-handles="[{ id: undefined }]"
      :target-handles="[]"
      :theme="{
      borderColor: '#d1fae5',
      background: '#ffffff',
      titleColor: '#0f172a',
      descColor: '#475569'
    }"
      fallback-description="流程入口"
      fallback-label="开始"
      icon-background="#ecfdf3"
      icon-color="#059669"
      icon-text="S"
      subtitle="Input Trigger"
  >
    <div class="slot-box">
      <div class="slot-head">INPUT VARS</div>
      <div class="slot-row">
        <span v-pre>{{query}}</span>
        <span>STR</span>
      </div>
    </div>
  </BaseCanvasNode>
</template>

<script lang="ts" setup>
import BaseCanvasNode from '../../shared/BaseCanvasNode.vue'
import type {WorkflowNodeViewData} from '../../shared/node-props'

defineProps<{ data?: WorkflowNodeViewData }>()
</script>

<style scoped>
.slot-box {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 10px;
}

.slot-head {
  font-size: 10px;
  color: #94a3b8;
  font-weight: 700;
}

.slot-row {
  margin-top: 6px;
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: #475569;
}
</style>

