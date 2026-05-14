<template>
  <section
      :class="{ 'compact-node': compactNode, disabled: disabled }"
      :style="canvasInlineStyle"
      class="flow-canvas"
      @dragover="onDragOver"
      @drop="onDropToCanvas"
      @mousedown.capture="onCanvasMouseDown"
      @mousemove.capture="onCanvasMouseMove"
      @mouseup.capture="onCanvasMouseUp"
      @contextmenu.capture="onNativeContextmenu"
  >

    <VueFlow
        ref="flowRef"
        :connect-on-click="false"
        :edges="flowEdges"
        :edges-updatable="true"
        :elements-selectable="interactionMode !== 'pan'"
        :max-zoom="1.5"
        :min-zoom="0.3"
        :node-types="nodeTypes"
        :nodes="flowNodes"
        :nodes-connectable="true"
        :nodes-draggable="interactionMode !== 'pan'"
        :pan-on-drag="interactionMode === 'pan'"
        :selection-key-code="interactionMode === 'box' ? true : null"
        :selection-on-drag="interactionMode === 'box'"
        :snap-grid="[canvasConfig.snapGridSize, canvasConfig.snapGridSize]"
        :snap-to-grid="canvasConfig.snapToGridEnabled"
        class="canvas-inner"
        @connect="onConnect"
        @move="onViewportMove"
        @update:nodes="onNodesUpdate"
        @update:edges="onEdgesUpdate"
        @node-click="onNodeClick"
        @edge-click="onEdgeClick"
        @pane-click="clearSelection"
        @selection-change="onSelectionChange"
        @nodes-delete="$emit('nodes-delete')"
        @edges-delete="$emit('edges-delete')"
    >
      <div v-if="canvasConfig.backgroundVariant !== 'none'" :style="patternOverlayStyle"
           class="canvas-pattern-overlay"></div>

    </VueFlow>
    <div
        v-if="canvasConfig.showOriginMarker"
        :style="{
        left: `${originPoint.x}px`,
        top: `${originPoint.y}px`
      }"
        class="origin-marker"
    >
      <span class="origin-dot"/>
      <span class="origin-label">(0,0)</span>
    </div>
    <LeftCenter :items="paletteIcons"/>
    <LeftTop
        :can-redo="canRedo"
        :can-undo="canUndo"
        :interaction-mode="interactionMode"
        @redo="onRedo"
        @undo="onUndo"
        @set-mode="setInteractionMode"
        @clear-selection="clearSelection"
    />
    <RightTop :saving="saving" @save="$emit('save')"/>
    <RightBottom
        :zoom-percent="zoomPercent"
        @fit-view="onFitView"
        @zoom-in="onZoomIn"
        @zoom-out="onZoomOut"
        @set-zoom-percent="onSetZoomPercent"
    />
    <div class="autosave-tip">
      {{ autoSaveHint }}
    </div>
    <div v-if="disabled" class="canvas-disabled-mask">
      <div class="canvas-disabled-tip">请先在左侧新建或选择流程后再开始搭建</div>
    </div>
  </section>
</template>

<script lang="ts" setup>
import {computed, nextTick, onMounted, ref, watch} from 'vue'
import {
  type Connection,
  type Edge,
  type EdgeMouseEvent,
  type Node,
  type NodeMouseEvent,
  useVueFlow,
  VueFlow
} from '@vue-flow/core'
import {nodeCanvasTypes} from '../nodes/registry'
import LeftCenter from '@/views/admin/ai-workflow/definition/builder/components/center/components/Left-Center.vue'
import LeftTop from '@/views/admin/ai-workflow/definition/builder/components/center/components/Left-Top.vue'
import RightTop from '@/views/admin/ai-workflow/definition/builder/components/center/components/Right-Top.vue'
import RightBottom from '@/views/admin/ai-workflow/definition/builder/components/center/components/Right-Bottom.vue'
import {useNodeDnD} from '@/views/admin/ai-workflow/definition/builder/composables/useNodeDnD'
import {
  useCanvasHistory
} from '@/views/admin/ai-workflow/definition/builder/components/center/composables/useCanvasHistory'
import {useCanvasTools} from '@/views/admin/ai-workflow/definition/builder/components/center/composables/useCanvasTools'
import type {
  CanvasConfig,
  CanvasContextMenuPayload,
  CanvasGraphState,
  CanvasPaletteIconItem,
  NodeDropPayload,
  WorkflowEdge,
  WorkflowNode
} from '../../domain/types'

const props = withDefaults(
    defineProps<{
      nodes: WorkflowNode[]
      edges: WorkflowEdge[]
      paletteIcons: CanvasPaletteIconItem[]
      canvasConfig: CanvasConfig
      historySeed?: string
      saving?: boolean
      compactNode?: boolean
      initialZoomMode?: 'fit-compact' | 'normal'
      disabled?: boolean
      autoSaveEnabled?: boolean
      autoSaveStatus?: 'idle' | 'saving' | 'success' | 'failed'
      autoSaveDisplayTime?: string
    }>(),
    {
      compactNode: false,
      historySeed: '',
      initialZoomMode: 'normal',
      disabled: false,
      autoSaveEnabled: true,
      autoSaveStatus: 'idle',
      autoSaveDisplayTime: ''
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
  'restore-graph-state': [snapshot: CanvasGraphState]
  save: []
}>()

const {parseDropType} = useNodeDnD()
const history = useCanvasHistory()
const isRestoringHistory = ref(false)
const {
  interactionMode,
  canUndo,
  canRedo,
  setInteractionMode,
  clearSelection: triggerClearSelection,
  undo,
  redo
} = useCanvasTools({
  canUndo: () => history.canUndo.value,
  canRedo: () => history.canRedo.value,
  onClearSelection: () => emit('clear-selection'),
  onUndo: () => {
    const snapshot = history.undo()
    if (!snapshot) return
    isRestoringHistory.value = true
    emit('restore-graph-state', snapshot)
    window.setTimeout(() => {
      isRestoringHistory.value = false
    }, 0)
  },
  onRedo: () => {
    const snapshot = history.redo()
    if (!snapshot) return
    isRestoringHistory.value = true
    emit('restore-graph-state', snapshot)
    window.setTimeout(() => {
      isRestoringHistory.value = false
    }, 0)
  }
})
const {project, zoomIn, zoomOut, fitView, getViewport, setViewport} = useVueFlow()
const flowRef = ref<InstanceType<typeof VueFlow> | null>(null)
const zoomPercent = ref(100)
const originPoint = ref({x: 0, y: 0})
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

const flowNodes = computed(() => props.nodes as unknown as Node[])
const flowEdges = computed(() => props.edges as unknown as Edge[])
const canvasInlineStyle = computed(() => ({
  backgroundColor: props.canvasConfig.backgroundColor
}))
const patternOverlayStyle = computed(() => {
  const gap = Math.max(8, Number(props.canvasConfig.patternGap || 22))
  const stroke = Math.max(1, Number(props.canvasConfig.patternSize || 1.2))
  const color = props.canvasConfig.patternColor || '#94a3b8'
  if (props.canvasConfig.backgroundVariant === 'dots') {
    return {
      backgroundImage: `radial-gradient(${color} ${stroke}px, transparent ${Math.max(stroke + 0.6, stroke * 1.3)}px)`,
      backgroundSize: `${gap}px ${gap}px`,
      opacity: 0.45
    }
  }
  if (props.canvasConfig.backgroundVariant === 'lines') {
    return {
      backgroundImage: `repeating-linear-gradient(0deg, transparent 0, transparent ${gap - stroke}px, ${color} ${gap - stroke}px, ${color} ${gap}px), repeating-linear-gradient(90deg, transparent 0, transparent ${gap - stroke}px, ${color} ${gap - stroke}px, ${color} ${gap}px)`,
      backgroundSize: `${gap}px ${gap}px`,
      opacity: 0.28
    }
  }
  const crossLen = Math.max(stroke * 3.2, 3)
  const half = gap / 2
  const svg = `<svg xmlns='http://www.w3.org/2000/svg' width='${gap}' height='${gap}' viewBox='0 0 ${gap} ${gap}'><line x1='${half - crossLen / 2}' y1='${half}' x2='${half + crossLen / 2}' y2='${half}' stroke='${color}' stroke-width='${stroke}' stroke-linecap='round'/><line x1='${half}' y1='${half - crossLen / 2}' x2='${half}' y2='${half + crossLen / 2}' stroke='${color}' stroke-width='${stroke}' stroke-linecap='round'/></svg>`
  const encoded = encodeURIComponent(svg)
  return {
    backgroundImage: `url("data:image/svg+xml,${encoded}")`,
    backgroundSize: `${gap}px ${gap}px`,
    backgroundRepeat: 'repeat',
    opacity: 0.85
  }
})

const autoSaveHint = computed(() => {
  if (!props.autoSaveEnabled) return '自动保存已关闭'
  if (props.autoSaveStatus === 'saving') return '自动保存中...'
  if (props.autoSaveStatus === 'failed') return '自动保存失败（可手动保存）'
  if (props.autoSaveDisplayTime) return `自动保存已开启，上次保存 ${props.autoSaveDisplayTime}`
  return '自动保存已开启，每 30 秒保存一次'
})

const onConnect = (connection: Connection) => {
  emit('connect', connection)
}

const onNodeClick = ({node}: NodeMouseEvent) => {
  const nodeId = (node as { id?: string }).id
  if (!nodeId) return
  emit('select-node', nodeId)
}

const onEdgeClick = ({edge}: EdgeMouseEvent) => {
  const edgeId = (edge as { id?: string }).id
  if (!edgeId) return
  emit('select-edge', edgeId)
}

const clearSelection = () => {
  triggerClearSelection()
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

const onSetZoomPercent = (value: number) => {
  const current = getViewport()
  const nextZoom = Math.min(1.5, Math.max(0.3, value / 100))
  setViewport({
    x: current.x,
    y: current.y,
    zoom: nextZoom
  })
  syncZoomPercent()
}

const onSelectionChange = (payload: { nodes?: Array<{ id: string }> }) => {
  emit(
      'selection-change',
      (payload.nodes || []).map((node) => node.id)
  )
}

const onViewportMove = () => {
  syncZoomPercent()
  const viewport = getViewport()
  originPoint.value = {x: viewport.x, y: viewport.y}
}

const onDragOver = (ev: DragEvent) => {
  ev.preventDefault()
  if (ev.dataTransfer) ev.dataTransfer.dropEffect = 'copy'
}

const buildSnapshot = (): CanvasGraphState => {
  return {
    nodes: JSON.parse(JSON.stringify(props.nodes)),
    edges: JSON.parse(JSON.stringify(props.edges)),
    canvasConfig: JSON.parse(JSON.stringify(props.canvasConfig))
  }
}

const onNodesUpdate = (value: unknown[]) => {
  const nextNodes = value as WorkflowNode[]
  emit('update:nodes', nextNodes)
  if (isRestoringHistory.value) return
  history.queuePushSnapshot({
    nodes: nextNodes,
    edges: props.edges,
    canvasConfig: props.canvasConfig
  })
}

const onEdgesUpdate = (value: unknown[]) => {
  const nextEdges = value as WorkflowEdge[]
  emit('update:edges', nextEdges)
  if (isRestoringHistory.value) return
  history.queuePushSnapshot({
    nodes: props.nodes,
    edges: nextEdges,
    canvasConfig: props.canvasConfig
  })
}

const onUndo = () => {
  undo()
}

const onRedo = () => {
  redo()
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
  const viewport = getViewport()
  zoomPercent.value = Math.round(viewport.zoom * 100)
  originPoint.value = {x: viewport.x, y: viewport.y}
}

onMounted(() => {
  nextTick(() => {
    history.resetHistory(buildSnapshot())
    onFitView()
    syncZoomPercent()
  })
})

watch(
    () => props.historySeed,
    () => {
      history.resetHistory(buildSnapshot())
    }
)

watch(
    () => props.canvasConfig,
    (value) => {
      if (isRestoringHistory.value) return
      history.queuePushSnapshot({
        nodes: props.nodes,
        edges: props.edges,
        canvasConfig: value
      })
    },
    {deep: true}
)
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

.flow-canvas.disabled {
  background: #f5f7fb;
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
  position: relative;
  z-index: 2;
}

.flow-canvas.disabled :deep(.vue-flow) {
  pointer-events: none;
  filter: grayscale(0.6);
  opacity: 0.66;
}

.canvas-pattern-overlay {
  position: absolute;
  inset: 0;
  z-index: 1;
  pointer-events: none;
}

.origin-marker {
  position: absolute;
  z-index: 6;
  transform: translate(-50%, -50%);
  display: inline-flex;
  align-items: center;
  gap: 6px;
  pointer-events: none;
}

.origin-dot {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: #ef4444;
  border: 2px solid #fff;
  box-shadow: 0 0 0 1px rgba(239, 68, 68, 0.4);
}

.origin-label {
  font-size: 11px;
  color: #334155;
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid #cbd5e1;
  border-radius: 999px;
  padding: 2px 6px;
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

.canvas-disabled-mask {
  position: absolute;
  inset: 0;
  z-index: 20;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(245, 247, 251, 0.62);
}

.canvas-disabled-tip {
  padding: 10px 14px;
  border-radius: 10px;
  border: 1px solid #dbe3ee;
  background: #fff;
  color: #64748b;
  font-size: 13px;
}

.autosave-tip {
  position: absolute;
  left: 84px;
  bottom: 12px;
  z-index: 9;
  font-size: 12px;
  color: #64748b;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid #e2e8f0;
  border-radius: 999px;
  padding: 4px 10px;
}

</style>
