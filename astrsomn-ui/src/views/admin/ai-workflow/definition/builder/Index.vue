<template>
  <AstrsomnPageShell empty-text="暂无流程数据。">
    <div class="workflow-builder-page">
      <main class="builder-layout">
        <Left
            :active-workflow-id="activeWorkflowId"
            :class="{ collapsed: leftCollapsed }"
            :open-create-dialog-tick="openCreateDialogTick"
            :workflow-items="workflowItems"
            class="left-panel"
            @select-workflow="onSelectWorkflow"
            @create-workflow="onCreateWorkflow"
            @edit-workflow="onEditWorkflow"
            @delete-workflow="onDeleteWorkflow"
        />

        <div class="center-wrap">
          <LeftBottom :collapsed="leftCollapsed" @toggle="leftCollapsed = !leftCollapsed"/>
          <Center
              :auto-save-display-time="autoSaveDisplayTime"
              :auto-save-enabled="autoSaveEnabled"
              :auto-save-status="autoSaveStatus"
              :canvas-config="canvasConfig"
              :compact-node="compactNode"
              :disabled="!hasActiveWorkflow"
              :edges="edges"
              :history-seed="activeWorkflowId || 'draft-workflow'"
              :initial-zoom-mode="initialZoomMode"
              :nodes="nodes"
              :palette-icons="canvasPaletteIcons"
              :saving="saving"
              class="center-panel"
              @connect="handleConnect"
              @contextmenu="onCanvasContextmenu"
              @save="handleSaveAction"
              @update:nodes="onNodesUpdate"
              @update:edges="onEdgesUpdate"
              @drop-node="onDropNode"
              @select-node="onSelectNode"
              @select-edge="onSelectEdge"
              @clear-selection="clearSelection"
              @selection-change="onSelectionChange"
              @restore-graph-state="onRestoreGraphState"
              @nodes-delete="clearSelection"
              @edges-delete="clearSelection"
          />
        </div>

        <Right
            :all-nodes="nodes"
            :auto-save-enabled="autoSaveEnabled"
            :canvas-config="canvasConfig"
            :selected-edge="selectedEdge"
            :selected-node="selectedNode"
            :workflow-meta="workflowMeta"
            class="right-panel"
            @update-node="updateSelectedNode"
            @update-edge="updateSelectedEdge"
            @update-canvas-config="updateCanvasConfig"
            @apply-edge-style-all="applyEdgeTypeToAll"
            @update-auto-save-enabled="onAutoSaveEnabledChange"
            @remove-selection="removeSelection"
        />
      </main>

      <ContextMenu
          :items="contextMenu.items"
          :position="contextMenu.position"
          :visible="contextMenu.visible"
          @action="onContextMenuAction"
          @close="closeContextMenu"
      />
    </div>
  </AstrsomnPageShell>
</template>

<script lang="ts" setup>
import {computed, onMounted, onUnmounted, reactive, ref} from 'vue'
import {message, Modal} from 'ant-design-vue'
import {useRoute} from 'vue-router'
import AstrsomnPageShell from '@/components/home/AstrsomnPageShell.vue'
import {useBuilderPage} from './app/useBuilderPage'
import {useContextMenuActions} from './app/useContextMenuActions'
import {useWorkflowPersistence} from './app/useWorkflowPersistence'
import Left from './components/left/Left.vue'
import Center from './components/center/Center.vue'
import Right from './components/right/Right.vue'
import LeftBottom from '@/views/admin/ai-workflow/definition/builder/components/center/components/Left-Bottom.vue'
import ContextMenu from './components/context-menu/ContextMenu.vue'
import {canvasPaletteIcons} from './domain/palette'
import {useWorkflowGraph} from '@/views/admin/ai-workflow/definition/builder/composables/useWorkflowGraph'
import type {CanvasGraphState, NodeDropPayload, WorkflowListItem, WorkflowMeta} from './domain/types'
import {defaultCanvasConfig} from './domain/types'

const route = useRoute()
const saving = ref(false)
const activeWorkflowId = ref<string>()
const workflowItems = ref<WorkflowListItem[]>([])
const savedGraphSnapshot = ref('')
const switchingWorkflow = ref(false)
const openCreateDialogTick = ref(0)
const AUTO_SAVE_SETTING_KEY = 'astrsomn-workflow-auto-save-enabled'
const AUTO_SAVE_INTERVAL_MS = 30_000
const {leftCollapsed, compactNode, initialZoomMode, layoutColumns, layoutColumnsSmall} = useBuilderPage()
const autoSaveEnabled = ref(true)
const autoSaveStatus = ref<'idle' | 'saving' | 'success' | 'failed'>('idle')
const autoSaveDisplayTime = ref('')
let autoSaveTimer: ReturnType<typeof window.setInterval> | undefined

const workflowMeta = reactive<WorkflowMeta>({
  id: undefined,
  workflowName: '未命名流程',
  workflowKey: '',
  description: ''
})

const {
  nodes,
  edges,
  canvasConfig,
  selectedNodeId,
  selectedEdgeId,
  selectedNode,
  selectedEdge,
  createNode,
  removeSelection,
  removeNodeById,
  removeEdgeById,
  duplicateNodeById,
  toggleNodeDisabled,
  batchRemoveNodes,
  batchDisableNodes,
  updateSelectedNode,
  updateSelectedEdge,
  setEdgeLabelById,
  updateEdgeStyleById,
  applyEdgeTypeToAll,
  updateCanvasConfig,
  replaceGraphState,
  onConnect,
  validateGraph,
  loadGraph,
  toGraphJson
} = useWorkflowGraph()

const selectedNodeIds = ref<string[]>([])

const stripTransientNodeState = (node: Record<string, unknown>) => {
  const {
    selected,
    dragging,
    resizing,
    positionAbsolute,
    dimensions,
    events,
    width,
    height,
    dragHandle,
    targetPosition,
    sourcePosition,
    handleBounds,
    computedPosition,
    isParent,
    ...rest
  } = node
  return rest
}

const stripTransientEdgeState = (edge: Record<string, unknown>) => {
  const {selected, updating, events, sourceX, sourceY, targetX, targetY, ...rest} = edge
  return rest
}

const normalizeCanvasConfigSnapshot = (raw?: Record<string, unknown>) => {
  const merged = {
    ...defaultCanvasConfig,
    ...(raw || {})
  } as Record<string, unknown>
  if (String(merged.edgeStyleDefault || '') === 'smoothstep') {
    merged.edgeStyleDefault = 'default'
  }
  return merged
}

const normalizeGraphSnapshot = (graphJson?: string) => {
  if (!graphJson) return ''
  try {
    const parsed = JSON.parse(graphJson) as {
      nodes?: Array<Record<string, unknown>>
      edges?: Array<Record<string, unknown>>
      viewport?: Record<string, unknown>
      meta?: Record<string, unknown>
    }
    const normalized = {
      nodes: (parsed.nodes || []).map(stripTransientNodeState).sort((a, b) => String(a.id).localeCompare(String(b.id))),
      edges: (parsed.edges || []).map(stripTransientEdgeState).sort((a, b) => String(a.id).localeCompare(String(b.id))),
      // Viewport is runtime interaction state (pan/zoom), not a semantic graph change.
      viewport: {x: 0, y: 0, zoom: 1},
      meta: {
        canvasConfig: normalizeCanvasConfigSnapshot((parsed.meta?.canvasConfig as Record<string, unknown>) || {})
      }
    }
    return JSON.stringify(normalized)
  } catch {
    return ''
  }
}
const breadcrumbs = [
  {title: 'AI 流程', href: '/admin/ai-config'},
  {title: '编辑流程'},
]
const buildGraphSnapshot = () => normalizeGraphSnapshot(toGraphJson())
const refreshSavedGraphSnapshot = () => {
  savedGraphSnapshot.value = buildGraphSnapshot()
}
const hasActiveWorkflow = computed(() => {
  const activeId = (activeWorkflowId.value || '').trim()
  const workflowKey = (workflowMeta.workflowKey || '').trim()
  return Boolean(activeId && workflowKey)
})
const graphDirty = computed(() => (savedGraphSnapshot.value ? buildGraphSnapshot() !== savedGraphSnapshot.value : false))

const openCreateGuideDialog = () => {
  Modal.info({
    title: '请先创建流程',
    content: '当前为新建状态，画布已禁用。请先在左侧点击“新建流程”并填写基础信息后再开始搭建。',
    okText: '知道了'
  })
}

const handleConnect = (connection: Parameters<typeof onConnect>[0]) => {
  const result = onConnect(connection)
  if (!result.ok) {
    message.warning(result.message)
  }
}

const onDropNode = (payload: NodeDropPayload) => {
  if (!hasActiveWorkflow.value) {
    openCreateDialogTick.value += 1
    openCreateGuideDialog()
    return
  }
  createNode(payload.type, payload.position)
}

const onNodesUpdate = (value: typeof nodes.value) => {
  nodes.value = value
}

const onEdgesUpdate = (value: typeof edges.value) => {
  edges.value = value
}

const onSelectNode = (id: string) => {
  selectedNodeId.value = id
  selectedEdgeId.value = undefined
}

const onSelectEdge = (id: string) => {
  selectedEdgeId.value = id
  selectedNodeId.value = undefined
}

const clearSelection = () => {
  selectedNodeId.value = undefined
  selectedEdgeId.value = undefined
  selectedNodeIds.value = []
}

const onSelectionChange = (nodeIds: string[]) => {
  selectedNodeIds.value = nodeIds
}

const onRestoreGraphState = (snapshot: CanvasGraphState) => {
  replaceGraphState(snapshot)
  clearSelection()
}

const {contextMenu, closeContextMenu, onCanvasContextmenu, onContextMenuAction} = useContextMenuActions({
  selectedNodeIds,
  edges,
  graphActions: {
    removeNodeById,
    duplicateNodeById,
    toggleNodeDisabled,
    batchRemoveNodes,
    batchDisableNodes,
    removeEdgeById,
    setEdgeLabelById,
    updateEdgeStyleById
  },
  notifyInfo: (content) => message.info(content)
})

const switchToWorkflow = async (item: WorkflowListItem) => {
  if (switchingWorkflow.value) return
  switchingWorkflow.value = true
  try {
    await fetchWorkflowDetail(item.id)
    activeWorkflowId.value = item.id
    refreshSavedGraphSnapshot()
  } catch {
    message.warning('流程加载失败，请稍后重试')
  } finally {
    switchingWorkflow.value = false
  }
}

const loadAutoSaveEnabledSetting = () => {
  try {
    const raw = localStorage.getItem(AUTO_SAVE_SETTING_KEY)
    if (raw == null) {
      autoSaveEnabled.value = true
      return
    }
    autoSaveEnabled.value = raw === '1'
  } catch {
    autoSaveEnabled.value = true
  }
}

const persistAutoSaveEnabledSetting = () => {
  try {
    localStorage.setItem(AUTO_SAVE_SETTING_KEY, autoSaveEnabled.value ? '1' : '0')
  } catch {
    // ignore persistence failures
  }
}

const formatNowTime = () => {
  const now = new Date()
  const hh = String(now.getHours()).padStart(2, '0')
  const mm = String(now.getMinutes()).padStart(2, '0')
  const ss = String(now.getSeconds()).padStart(2, '0')
  return `${hh}:${mm}:${ss}`
}

const runAutoSave = async (source: 'switch' | 'timer') => {
  if (!autoSaveEnabled.value) return false
  if (!hasActiveWorkflow.value) return false
  if (!graphDirty.value) return true
  autoSaveStatus.value = 'saving'
  const saved = await handleSaveDraft({skipValidation: true, silent: true})
  if (saved) {
    refreshSavedGraphSnapshot()
    autoSaveStatus.value = 'success'
    autoSaveDisplayTime.value = formatNowTime()
    if (source === 'switch') {
      message.success('已自动保存当前流程')
    }
    return true
  }
  autoSaveStatus.value = 'failed'
  if (source === 'switch') {
    message.warning('自动保存失败，已继续切换流程')
  }
  return false
}

const startAutoSaveTimer = () => {
  if (autoSaveTimer) window.clearInterval(autoSaveTimer)
  autoSaveTimer = window.setInterval(() => {
    void runAutoSave('timer')
  }, AUTO_SAVE_INTERVAL_MS)
}

const onAutoSaveEnabledChange = (enabled: boolean) => {
  autoSaveEnabled.value = enabled
  persistAutoSaveEnabledSetting()
  autoSaveStatus.value = 'idle'
  if (!enabled) {
    autoSaveDisplayTime.value = ''
  }
  message.success(enabled ? '已开启自动保存' : '已关闭自动保存')
}

const onSelectWorkflow = async (item: WorkflowListItem) => {
  if (item.id === activeWorkflowId.value) return
  if (switchingWorkflow.value) return
  await runAutoSave('switch')
  await switchToWorkflow(item)
}

const handleSaveAction = async () => {
  if (!hasActiveWorkflow.value) {
    message.warning('请先新建或选择流程，再执行保存')
    return
  }
  const saved = await handleSaveDraft()
  if (saved) {
    refreshSavedGraphSnapshot()
  }
}

const {
  fetchWorkflowList,
  fetchWorkflowDetail,
  createWorkflow,
  updateWorkflowMeta,
  deleteWorkflow,
  handleSaveDraft,
  fetchDetailIfNeeded
} = useWorkflowPersistence({
  workflowMeta,
  activeWorkflowId,
  workflowItems,
  toGraphJson,
  validateGraph,
  loadGraph,
  saving,
  route,
  notifySuccess: (content) => message.success(content),
  notifyWarning: (content) => message.warning(content)
})

const onCreateWorkflow = async (payload: { workflowName: string; workflowKey: string; category: string }) => {
  await createWorkflow(payload)
  refreshSavedGraphSnapshot()
  message.success('流程已创建并进入编辑')
}

const onEditWorkflow = async (
    item: WorkflowListItem,
    payload: { workflowName: string; workflowKey: string; category: string }
) => {
  await updateWorkflowMeta(item, payload)
  refreshSavedGraphSnapshot()
  message.success('流程信息已更新')
}

const onDeleteWorkflow = async (item: WorkflowListItem) => {
  await deleteWorkflow(item)
  refreshSavedGraphSnapshot()
  message.success('流程已删除')
}

onMounted(() => {
  void (async () => {
    loadAutoSaveEnabledSetting()
    startAutoSaveTimer()
    await fetchWorkflowList()
    await fetchDetailIfNeeded()
    if (!activeWorkflowId.value) {
      workflowMeta.id = undefined
      workflowMeta.workflowName = '未命名流程'
      workflowMeta.workflowKey = ''
      workflowMeta.description = ''
      loadGraph(undefined)
    }
    refreshSavedGraphSnapshot()
  })()
})

onUnmounted(() => {
  if (autoSaveTimer) {
    window.clearInterval(autoSaveTimer)
    autoSaveTimer = undefined
  }
})
</script>

<style scoped>
.workflow-builder-page {

  display: flex;
  flex-direction: column;
  gap: 12px;
  height: calc(100vh - 70px);
}

.builder-layout {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: v-bind(layoutColumns);
  gap: 12px;
  transition: grid-template-columns 0.28s ease;
}

.left-panel,
.right-panel {
  min-height: 0;
}

.left-panel {
  min-width: 0;
  overflow: hidden;
  opacity: 1;
  transform: translateX(0);
  transition: opacity 0.22s ease, transform 0.22s ease;
}

.left-panel.collapsed {
  opacity: 0;
  transform: translateX(-10px);
  pointer-events: none;
}

.center-wrap {
  position: relative;
  min-height: 0;
}

.center-panel {
  height: 100%;
  min-height: 0;
}

@media (max-width: 1500px) {
  .builder-layout {
    grid-template-columns: v-bind(layoutColumnsSmall);
  }
}
</style>
