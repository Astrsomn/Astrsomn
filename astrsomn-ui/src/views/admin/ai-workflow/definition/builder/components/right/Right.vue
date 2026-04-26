<template>
  <aside class="node-inspector">
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

    <CanvasPaneConfig
      v-else
      :canvas-config="canvasConfig"
      @update-canvas-config="emit('update-canvas-config', $event)"
      @apply-edge-style-all="emit('apply-edge-style-all', $event)"
    />
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { CanvasConfig, CanvasEdgeStyle, WorkflowEdge, WorkflowMeta, WorkflowNode } from '../../domain/types'
import type { WorkflowNodeType } from '../../domain/types'
import { nodeInspectorMap } from '../nodes/registry'
import CanvasPaneConfig from './component/CanvasPaneConfig.vue'

const props = defineProps<{
  workflowMeta: WorkflowMeta
  canvasConfig: CanvasConfig
  allNodes?: WorkflowNode[]
  selectedNode?: WorkflowNode
  selectedEdge?: WorkflowEdge
}>()

const emit = defineEmits<{
  'update-node': [payload: { label?: string; description?: string; config?: Record<string, unknown> }]
  'update-edge': [payload: { label?: string }]
  'update-canvas-config': [payload: Partial<CanvasConfig>]
  'apply-edge-style-all': [edgeStyle: CanvasEdgeStyle]
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

</style>
