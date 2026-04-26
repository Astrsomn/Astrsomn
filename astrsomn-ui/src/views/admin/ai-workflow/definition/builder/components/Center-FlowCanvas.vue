<template>
  <section class="flow-canvas" @dragover="onDragOver" @drop="onDropToCanvas">
    <VueFlow
      v-model:nodes="innerNodes"
      v-model:edges="innerEdges"
      class="canvas-inner"
      fit-view-on-init
      :min-zoom="0.3"
      :max-zoom="1.5"
      :node-types="nodeTypes"
      @connect="onConnect"
      @node-click="onNodeClick"
      @edge-click="onEdgeClick"
      @pane-click="clearSelection"
      @nodes-delete="$emit('nodes-delete')"
      @edges-delete="$emit('edges-delete')"
    >
      <Background pattern-color="#e2e8f0" :gap="18" />
      <Controls />
      <div class="canvas-tip">拖拽左侧节点到此处，点击节点/连线可在右侧编辑</div>
    </VueFlow>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { VueFlow, type Connection } from '@vue-flow/core'
import { Background } from '@vue-flow/background'
import { Controls } from '@vue-flow/controls'
import StartNode from './nodes/StartNode.vue'
import LlmNode from './nodes/LlmNode.vue'
import ToolNode from './nodes/ToolNode.vue'
import ConditionNode from './nodes/ConditionNode.vue'
import EndNode from './nodes/EndNode.vue'
import { useNodeDnD } from '../composables/useNodeDnD'
import type { NodeDropPayload, WorkflowEdge, WorkflowNode } from '../types'

const props = defineProps<{
  nodes: WorkflowNode[]
  edges: WorkflowEdge[]
}>()

const emit = defineEmits<{
  'update:nodes': [value: WorkflowNode[]]
  'update:edges': [value: WorkflowEdge[]]
  'connect': [payload: Connection]
  'drop-node': [payload: NodeDropPayload]
  'select-node': [id: string]
  'select-edge': [id: string]
  'clear-selection': []
  'nodes-delete': []
  'edges-delete': []
}>()

const { parseDropType } = useNodeDnD()

const nodeTypes = {
  start: StartNode,
  llm: LlmNode,
  tool: ToolNode,
  condition: ConditionNode,
  end: EndNode
}

const innerNodes = computed({
  get: () => props.nodes,
  set: (value) => emit('update:nodes', value)
})

const innerEdges = computed({
  get: () => props.edges,
  set: (value) => emit('update:edges', value)
})

const onConnect = (connection: Connection) => {
  emit('connect', connection)
}

const onNodeClick = (_ev: MouseEvent, node: WorkflowNode) => {
  emit('select-node', node.id)
}

const onEdgeClick = (_ev: MouseEvent, edge: WorkflowEdge) => {
  emit('select-edge', edge.id)
}

const clearSelection = () => {
  emit('clear-selection')
}

const onDragOver = (ev: DragEvent) => {
  ev.preventDefault()
  if (ev.dataTransfer) ev.dataTransfer.dropEffect = 'copy'
}

const onDropToCanvas = (ev: DragEvent) => {
  ev.preventDefault()
  const type = parseDropType(ev)
  if (!type) return
  const container = ev.currentTarget as HTMLElement | null
  const rect = container?.getBoundingClientRect()
  if (!rect) return
  emit('drop-node', {
    type,
    position: {
      x: ev.clientX - rect.left - 80,
      y: ev.clientY - rect.top - 30
    }
  })
}
</script>

<style scoped>
.flow-canvas {
  height: 100%;
  border: 1px solid #edf1f6;
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
}

.canvas-inner {
  width: 100%;
  height: 100%;
}

.canvas-tip {
  position: absolute;
  top: 12px;
  left: 12px;
  z-index: 5;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 6px 10px;
  font-size: 12px;
  color: #475569;
}
</style>
