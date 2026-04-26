<template>
  <section
    class="flow-canvas"
    :class="{ 'compact-node': compactNode }"
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
    <TopTools :interaction-mode="interactionMode" @set-mode="setInteractionMode" @clear-selection="clearSelection" @placeholder="notifyPlaceholder" />
    <RightActions :saving="saving" @save="$emit('save')" />
    <BottomRightZoom :zoom-percent="zoomPercent" @fit-view="onFitView" @zoom-in="onZoomIn" @zoom-out="onZoomOut" />
  </section>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import { VueFlow, type Connection, type EdgeMouseEvent, type NodeMouseEvent, useVueFlow } from '@vue-flow/core'
import { Background } from '@vue-flow/background'
import { nodeCanvasTypes } from './nodes/registry'
import PaletteDock from './flow-canvas/PaletteDock.vue'
import TopTools from './flow-canvas/controls/TopTools.vue'
import RightActions from './flow-canvas/controls/RightActions.vue'
import BottomRightZoom from './flow-canvas/controls/BottomRightZoom.vue'
import { useNodeDnD } from '../composables/useNodeDnD'
import type { CanvasContextMenuPayload, CanvasPaletteIconItem, NodeDropPayload, WorkflowEdge, WorkflowNode } from '../types'

const props = withDefaults(
  defineProps<{
    nodes: WorkflowNode[]
    edges: WorkflowEdge[]
    paletteIcons: CanvasPaletteIconItem[]
    saving?: boolean
    compactNode?: boolean
    initialZoomMode?: 'fit-compact' | 'normal'
  }>(),
  {
    compactNode: false,
    initialZoomMode: 'normal'
  }
)

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
const zoomPercent = ref(100)
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
  fitView({
    padding: props.initialZoomMode === 'fit-compact' ? 0.34 : 0.24,
    duration: 300,
    maxZoom: props.initialZoomMode === 'fit-compact' ? 0.85 : 1.2
  })
  syncZoomPercent()
}

const onZoomIn = () => {
  zoomIn()
  syncZoomPercent()
}

const onZoomOut = () => {
  zoomOut()
  syncZoomPercent()
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
  syncZoomPercent()
}

const onCanvasMouseUp = (ev: MouseEvent) => {
  if (ev.button === 2 && rightPanState.value.active) {
    rightPanState.value.active = false
  }
  syncZoomPercent()
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

const syncZoomPercent = () => {
  zoomPercent.value = Math.round(getViewport().zoom * 100)
}

onMounted(() => {
  nextTick(() => {
    onFitView()
    syncZoomPercent()
  })
})
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

.flow-canvas.compact-node {
  --wf-node-min-width: 220px;
  --wf-node-header-padding: 10px 12px 8px;
  --wf-node-content-padding: 0 12px 12px;
  --wf-node-icon-size: 28px;
  --wf-node-icon-font-size: 12px;
  --wf-node-title-font-size: 12px;
  --wf-node-subtitle-font-size: 9px;
  --wf-node-desc-font-size: 11px;
  --wf-node-badge-padding: 3px 7px;
  --wf-node-handle-size: 8px;
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

</style>
