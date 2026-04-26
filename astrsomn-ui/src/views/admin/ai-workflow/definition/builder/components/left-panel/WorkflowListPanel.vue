<template>
  <div class="workflow-list-panel">
    <div v-if="!items.length" class="empty-tip">暂无流程，请先创建流程。</div>
    <button
      v-for="item in items"
      :key="item.id"
      type="button"
      class="workflow-item"
      :class="{ active: item.id === activeWorkflowId }"
      @click="$emit('select', item)"
    >
      <span class="workflow-name">{{ item.workflowName }}</span>
      <span class="workflow-key">{{ item.workflowKey || '-' }}</span>
    </button>
  </div>
</template>

<script setup lang="ts">
import type { WorkflowListItem } from '../../types'

defineProps<{
  items: WorkflowListItem[]
  activeWorkflowId?: string
}>()

defineEmits<{
  select: [item: WorkflowListItem]
}>()
</script>

<style scoped>
.workflow-list-panel {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow: auto;
}

.empty-tip {
  border: 1px dashed #d9e1ec;
  border-radius: 8px;
  padding: 12px;
  color: #64748b;
  font-size: 12px;
}

.workflow-item {
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  background: #fff;
  padding: 10px;
  text-align: left;
  cursor: pointer;
}

.workflow-item:hover {
  border-color: #91caff;
  background: #f8fbff;
}

.workflow-item.active {
  border-color: #1677ff;
  background: #f0f6ff;
}

.workflow-name {
  display: block;
  color: #1e293b;
  font-size: 13px;
  font-weight: 600;
}

.workflow-key {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: #64748b;
}
</style>
