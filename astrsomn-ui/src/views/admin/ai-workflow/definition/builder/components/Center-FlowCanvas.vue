<template>
  <section
    class="flow-canvas"
    @dragover="onDragOver"
    @drop="onDropToCanvas"
    @mousedown.capture="onCanvasMouseDown"
    @mousemove.capture="onCanvasMouseMove"
    @mouseup.capture="onCanvasMouseUp"
    @contextmenu.capture="onNativeContextmenu"
  >
    <PaletteDock :items="paletteIcons" />
    <VueFlow
      :nodes="nodes"
      :edges="edges"
      ref="flowRef"
      class="canvas-inner"
      fit-view-on-init
      :min-zoom="0.3"
      :max-zoom="1.5"
      :node-types="nodeTypes"
      :nodes-connectable="true"
      :elements-selectable="interactionMode !== 'pan'"
      :nodes-draggable="interactionMode !== 'pan'"
      :edges-updatable="true"
      :connect-on-click="false"
      :selection-on-drag="interactionMode === 'box'"
      :pan-on-drag="interactionMode === 'pan'"
      @update:nodes="(value) => emit('update:nodes', value)"
      @update:edges="(value) => emit('update:edges', value)"
      @connect="onConnect"
      @node-click="onNodeClick"
      @edge-click="onEdgeClick"
      @pane-click="clearSelection"
      @selection-change="onSelectionChange"
      @nodes-delete="$emit('nodes-delete')"
      @edges-delete="$emit('edges-delete')"
    >
      <Background pattern-color="#e2e8f0" :gap="18" />
  
    </VueFlow>
    <div class="canvas-tools">
      <a-tooltip title="框选模式">
        <a-button class="tool-btn" :class="{ active: interactionMode === 'box' }" shape="circle" @click="setInteractionMode('box')">
          <template #icon><BorderOutlined /></template>
        </a-button>
      </a-tooltip>
      <a-tooltip title="平移模式">
        <a-button class="tool-btn" :class="{ active: interactionMode === 'pan' }" shape="circle" @click="setInteractionMode('pan')">
          <template #icon><DragOutlined /></template>
        </a-button>
      </a-tooltip>

      <a-tooltip title="清空选择">
        <a-button class="tool-btn" shape="circle" @click="clearSelection">
          <template #icon><CloseCircleOutlined /></template>
        </a-button>
      </a-tooltip>
      <a-tooltip title="撤销（占位）">
        <a-button class="tool-btn" shape="circle" @click="notifyPlaceholder('撤销')">
          <template #icon><UndoOutlined /></template>
        </a-button>
      </a-tooltip>
      <a-tooltip title="重做（占位）">
        <a-button class="tool-btn" shape="circle" @click="notifyPlaceholder('重做')">
          <template #icon><RedoOutlined /></template>
        </a-button>
      </a-tooltip>
    </div>
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
    <div class="zoom-controls">
      <a-tooltip title="适配视图">
        <a-button class="tool-btn" shape="circle" @click="onFitView">
          <template #icon><AimOutlined /></template>
        </a-button>
      </a-tooltip>
      <a-tooltip title="放大">
        <button type="button" class="zoom-btn" @click="onZoomIn">
          <PlusOutlined />
        </button>
      </a-tooltip>
      <a-tooltip title="缩小">
        <button type="button" class="zoom-btn" @click="onZoomOut">
          <MinusOutlined />
        </button>
      </a-tooltip>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  AimOutlined,
  BorderOutlined,
  BugOutlined,
  CloseCircleOutlined,
  DragOutlined,
  MinusOutlined,
  PlusOutlined,
  RedoOutlined,
  RocketOutlined,
  SaveOutlined,
  UndoOutlined
} from '@ant-design/icons-vue'
import { VueFlow, type Connection, type EdgeMouseEvent, type NodeMouseEvent, useVueFlow } from '@vue-flow/core'
import { Background } from '@vue-flow/background'
import { nodeCanvasTypes } from './nodes/registry'
import PaletteDock from './flow-canvas/PaletteDock.vue'
import { useNodeDnD } from '../composables/useNodeDnD'
import type { CanvasContextMenuPayload, CanvasPaletteIconItem, NodeDropPayload, WorkflowEdge, WorkflowNode } from '../types'

const props = defineProps<{
  nodes: WorkflowNode[]
  edges: WorkflowEdge[]
  paletteIcons: CanvasPaletteIconItem[]
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
const { project, zoomIn, zoomOut, fitView, getViewport, setViewport } = useVueFlow()
const flowRef = ref<InstanceType<typeof VueFlow> | null>(null)
const interactionMode = ref<'box' | 'pan'>('box')
const rightPanState = ref<{
  active: boolean
  startClientX: number
  startClientY: number
  startViewportX: number
  startViewportY: number
}>({
  active: false,
  startClientX: 0,
  startClientY: 0,
  startViewportX: 0,
  startViewportY: 0
})

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

const setInteractionMode = (mode: 'box' | 'pan') => {
  interactionMode.value = mode
}

const notifyPlaceholder = (action: string) => {
  message.info(`${action}功能待接入`)
}

const onFitView = () => {
  fitView({ padding: 0.24, duration: 300 })
}

const onZoomIn = () => {
  zoomIn()
}

const onZoomOut = () => {
  zoomOut()
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
  const flowEl = flowRef.value?.$el as HTMLElement | undefined
  const bounds = flowEl?.getBoundingClientRect()
  if (!bounds) return
  const point = project({
    x: ev.clientX - bounds.left,
    y: ev.clientY - bounds.top
  })
  emit('drop-node', {
    type,
    position: point
  })
}

const onCanvasMouseDown = (ev: MouseEvent) => {
  if (ev.button !== 2 || interactionMode.value === 'pan') return
  const target = ev.target as HTMLElement | null
  const paneEl = target?.closest('.vue-flow__pane')
  if (!paneEl || target !== paneEl) return
  ev.preventDefault()
  const viewport = getViewport()
  rightPanState.value = {
    active: true,
    startClientX: ev.clientX,
    startClientY: ev.clientY,
    startViewportX: viewport.x,
    startViewportY: viewport.y
  }
}

const onCanvasMouseMove = (ev: MouseEvent) => {
  if (!rightPanState.value.active) return
  ev.preventDefault()
  const deltaX = ev.clientX - rightPanState.value.startClientX
  const deltaY = ev.clientY - rightPanState.value.startClientY
  setViewport({
    x: rightPanState.value.startViewportX + deltaX,
    y: rightPanState.value.startViewportY + deltaY,
    zoom: getViewport().zoom
  })
}

const onCanvasMouseUp = (ev: MouseEvent) => {
  if (ev.button === 2 && rightPanState.value.active) {
    rightPanState.value.active = false
  }
}

const onNativeContextmenu = (ev: MouseEvent) => {
  ev.preventDefault()
  const target = ev.target as HTMLElement | null
  if (!target) return

  const nodeEl = target.closest('.vue-flow__node') as HTMLElement | null
  if (nodeEl) {
    const nodeId = nodeEl.getAttribute('data-id')
    if (nodeId) {
      emit('contextmenu', {
        target: 'node',
        x: ev.clientX,
        y: ev.clientY,
        nodeId
      })
    }
    return
  }

  const edgeEl = target.closest('.vue-flow__edge') as HTMLElement | null
  if (edgeEl) {
    const edgeId = edgeEl.getAttribute('data-id')
    if (edgeId) {
      emit('contextmenu', {
        target: 'edge',
        x: ev.clientX,
        y: ev.clientY,
        edgeId
      })
    }
  }
}
</script>

<style scoped>
.flow-canvas {
  height: 100%;
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
  left: 84px;
  z-index: 5;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 6px 10px;
  font-size: 12px;
  color: #475569;
}

.canvas-tools {
  position: absolute;
  top: 12px;
  left: 12px;
  z-index: 6;
  display: flex;
  flex-direction: row;
  gap: 10px;
  padding: 6px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.08);
}

.tool-btn {
  width: 38px;
  height: 38px;
  border: 1px solid #d9e1ec;
  color: #334155;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease, color 0.2s ease;
}

.tool-btn:hover {
  transform: translateY(-2px);
  border-color: #91caff;
  color: #1677ff;
  box-shadow: 0 8px 16px rgba(59, 130, 246, 0.2);
}

.tool-btn.active {
  border-color: #1677ff;
  color: #1677ff;
  background: #eff6ff;
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

.zoom-controls {
  position: absolute;
  right: 16px;
  bottom: 16px;
  z-index: 7;
  display: flex;
  flex-direction: row;
  gap: 10px;
  padding: 6px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.08);
}

.zoom-btn {
  width: 38px;
  height: 38px;
  border-radius: 999px;
  border: 1px solid #d9e1ec;
  background: #fff;
  color: #1e293b;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.zoom-btn:hover {
  transform: translateY(-2px) scale(1.04);
  border-color: #60a5fa;
  box-shadow: 0 10px 20px rgba(59, 130, 246, 0.24);
}
</style>
