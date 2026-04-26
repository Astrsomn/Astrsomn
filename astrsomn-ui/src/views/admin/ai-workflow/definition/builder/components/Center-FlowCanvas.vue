<template>
  <section class="flow-canvas" @dragover="onDragOver" @drop="onDropToCanvas" @contextmenu.prevent>
    <VueFlow
      :nodes="nodes"
      :edges="edges"
      class="canvas-inner"
      fit-view-on-init
      :min-zoom="0.3"
      :max-zoom="1.5"
      :node-types="nodeTypes"
      :nodes-connectable="true"
      :elements-selectable="true"
      :edges-updatable="true"
      :connect-on-click="false"
      @update:nodes="(value) => emit('update:nodes', value)"
      @update:edges="(value) => emit('update:edges', value)"
      @connect="onConnect"
      @node-click="onNodeClick"
      @edge-click="onEdgeClick"
      @pane-click="clearSelection"
      @node-contextmenu="onNodeContextmenu"
      @edge-contextmenu="onEdgeContextmenu"
      @pane-contextmenu="onPaneContextmenu"
      @selection-change="onSelectionChange"
      @nodes-delete="$emit('nodes-delete')"
      @edges-delete="$emit('edges-delete')"
    >
      <Background pattern-color="#e2e8f0" :gap="18" />
      <Controls />
      <div class="canvas-tip">拖拽左侧节点到此处，点击节点/连线可在右侧编辑</div>
    </VueFlow>
    <div class="canvas-actions">
      <a-tooltip title="保存草稿">
        <a-button class="action-btn save-btn" shape="circle" :loading="saving" @click="$emit('save')">
          <template #icon><SaveOutlined /></template>
        </a-button>
      </a-tooltip>
      <a-tooltip title="调试运行">
        <a-button class="action-btn debug-btn" shape="circle" disabled>
          <template #icon><BugOutlined /></template>
        </a-button>
      </a-tooltip>
      <a-tooltip title="发布流程">
        <a-button class="action-btn publish-btn" shape="circle" disabled>
          <template #icon><RocketOutlined /></template>
        </a-button>
      </a-tooltip>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { BugOutlined, RocketOutlined, SaveOutlined } from '@ant-design/icons-vue'
import { VueFlow, type Connection, type EdgeMouseEvent, type NodeMouseEvent } from '@vue-flow/core'
import { Background } from '@vue-flow/background'
import { Controls } from '@vue-flow/controls'
import { nodeCanvasTypes } from './nodes/registry'
import { useNodeDnD } from '../composables/useNodeDnD'
import type { CanvasContextMenuPayload, NodeDropPayload, WorkflowEdge, WorkflowNode } from '../types'

const props = defineProps<{
  nodes: WorkflowNode[]
  edges: WorkflowEdge[]
  saving?: boolean
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
  'contextmenu': [payload: CanvasContextMenuPayload]
  'selection-change': [nodeIds: string[]]
  save: []
}>()

const { parseDropType } = useNodeDnD()

const nodeTypes = nodeCanvasTypes

const nodes = computed(() => props.nodes)
const edges = computed(() => props.edges)

const onConnect = (connection: Connection) => {
  emit('connect', connection)
}

const onNodeClick = ({ node }: NodeMouseEvent) => {
  emit('select-node', (node as WorkflowNode).id)
}

const onEdgeClick = ({ edge }: EdgeMouseEvent) => {
  emit('select-edge', (edge as WorkflowEdge).id)
}

const clearSelection = () => {
  emit('clear-selection')
}

const getPointerPosition = (event: MouseEvent | TouchEvent) => {
  if ('touches' in event && event.touches.length > 0) {
    return { x: event.touches[0].clientX, y: event.touches[0].clientY }
  }
  if ('changedTouches' in event && event.changedTouches.length > 0) {
    return { x: event.changedTouches[0].clientX, y: event.changedTouches[0].clientY }
  }
  return { x: (event as MouseEvent).clientX, y: (event as MouseEvent).clientY }
}

const onNodeContextmenu = ({ event, node }: NodeMouseEvent) => {
  event.preventDefault()
  const point = getPointerPosition(event)
  emit('contextmenu', {
    target: 'node',
    x: point.x,
    y: point.y,
    nodeId: (node as WorkflowNode).id
  })
}

const onEdgeContextmenu = ({ event, edge }: EdgeMouseEvent) => {
  event.preventDefault()
  const point = getPointerPosition(event)
  emit('contextmenu', {
    target: 'edge',
    x: point.x,
    y: point.y,
    edgeId: (edge as WorkflowEdge).id
  })
}

const onPaneContextmenu = (event: MouseEvent) => {
  event.preventDefault()
  emit('contextmenu', {
    target: 'pane',
    x: event.clientX,
    y: event.clientY
  })
}

const onSelectionChange = (payload: { nodes?: Array<{ id: string }> }) => {
  emit(
    'selection-change',
    (payload.nodes || []).map((node) => node.id)
  )
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
  height: calc(100vh - 70px);
  border: 1px solid #edf1f6;
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
  position: relative;
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

.canvas-actions {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 6;
  display: flex;
  gap: 10px;
}

.action-btn {
  border: none;
  color: #fff;
}

.save-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.debug-btn {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.publish-btn {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.action-btn:disabled {
  opacity: 0.55;
}
</style>
