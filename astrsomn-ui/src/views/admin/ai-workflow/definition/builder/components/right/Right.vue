<template>
  <aside class="node-inspector">
    <div class="panel-head">
      <h3>属性面板</h3>
      <a-button size="small" danger :disabled="!selectedNode && !selectedEdge" @click="$emit('remove-selection')">
        删除所选
      </a-button>
    </div>

    <a-form layout="vertical" class="meta-form fixed-meta-form">
      <a-form-item label="流程名称" required>
        <a-input v-model="workflowMeta.workflowName" maxlength="128" />
      </a-form-item>
      <a-form-item label="Flow Key" required>
        <a-input v-model="workflowMeta.workflowKey" maxlength="128" />
      </a-form-item>
      <a-form-item label="业务分类">
        <a-input v-model="workflowMeta.description" maxlength="128" />
      </a-form-item>
    </a-form>

    <div v-if="selectedNode" class="selection-block">
      <h4>节点配置</h4>
      <component
        :is="selectedNodeInspector"
        v-if="selectedNodeInspector"
        :selected-node="selectedNode"
        :all-nodes="allNodes || []"
        @update-node="forwardNodeUpdate"
      />
    </div>

    <div v-else-if="selectedEdge" class="selection-block">
      <h4>连线配置</h4>
      <a-form layout="vertical">
        <a-form-item label="连线标签">
          <a-input
            :value="String(selectedEdge.label || '')"
            @update:value="forwardEdgeLabelUpdate"
          />
        </a-form-item>
      </a-form>
    </div>

    <div v-else class="empty-hint">
      未选择节点或连线。可在画布中点击元素后在此配置。
    </div>
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { WorkflowEdge, WorkflowMeta, WorkflowNode } from '../../domain/types.ts'
import type { WorkflowNodeType } from '../../domain/types.ts'
import { nodeInspectorMap } from '../nodes/registry.ts'

const props = defineProps<{
  workflowMeta: WorkflowMeta
  allNodes?: WorkflowNode[]
  selectedNode?: WorkflowNode
  selectedEdge?: WorkflowEdge
}>()

const emit = defineEmits<{
  'update-node': [payload: { label?: string; description?: string; config?: Record<string, unknown> }]
  'update-edge': [payload: { label?: string }]
  'remove-selection': []
}>()

const selectedNodeInspector = computed(() => {
  const nodeType = props.selectedNode?.type
  if (!nodeType) return undefined
  return nodeInspectorMap[nodeType as WorkflowNodeType]
})

const forwardNodeUpdate = (payload: { label?: string; description?: string; config?: Record<string, unknown> }) => {
  emit('update-node', payload)
}

const forwardEdgeLabelUpdate = (value: string) => {
  emit('update-edge', { label: value })
}
</script>

<style scoped>
.node-inspector {
  height: 100%;
  border: 1px solid #edf1f6;
  border-radius: 12px;
  background: #fff;
  padding: 12px;
  overflow: auto;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.panel-head h3,
.selection-block h4 {
  margin: 0;
  color: #1e293b;
}

.meta-form {
  border-bottom: 1px solid #f1f5f9;
  margin-bottom: 12px;
}

.fixed-meta-form {
  position: sticky;
  top: 0;
  z-index: 2;
  background: #fff;
  padding-bottom: 6px;
}

.selection-block {
  margin-top: 10px;
}

.empty-hint {
  color: #64748b;
  font-size: 12px;
  padding: 8px 0;
}
</style>
