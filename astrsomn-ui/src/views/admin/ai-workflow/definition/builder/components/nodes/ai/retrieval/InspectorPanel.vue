<template>
  <BaseInspectorPanel :all-nodes="allNodes" :selected-node="selectedNode" @update-node="$emit('update-node', $event)"/>
</template>

<script lang="ts" setup>
import BaseInspectorPanel from '../../shared/BaseInspectorPanel.vue'
import type {WorkflowNode} from '../../../../domain/types'

defineProps<{
  selectedNode: WorkflowNode
  allNodes: WorkflowNode[]
}>()

defineEmits<{
  'update-node': [payload: { label?: string; description?: string; config?: Record<string, unknown> }]
}>()
</script>

