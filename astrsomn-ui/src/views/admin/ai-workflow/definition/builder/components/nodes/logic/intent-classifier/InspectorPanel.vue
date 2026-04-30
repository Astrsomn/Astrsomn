<template>
  <BaseInspectorPanel :selected-node="selectedNode" :all-nodes="allNodes" @update-node="$emit('update-node', $event)" />
</template>

<script setup lang="ts">
import BaseInspectorPanel from '../../shared/BaseInspectorPanel.vue'
import type { WorkflowNode } from '../../../../domain/types'

defineProps<{
  selectedNode: WorkflowNode
  allNodes: WorkflowNode[]
}>()

defineEmits<{
  'update-node': [payload: { label?: string; description?: string; config?: Record<string, unknown> }]
}>()
</script>

