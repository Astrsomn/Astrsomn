<template>
  <AdminPageShell title="流程搭建" description="通过拖拽节点快速编排工作流骨架。" empty-text="暂无流程数据。">
    <div class="workflow-builder-page">
      <main class="builder-layout">
        <Left
          class="left-panel"
          :class="{ collapsed: leftCollapsed }"
          :workflow-items="workflowItems"
          :active-workflow-id="activeWorkflowId"
          :open-create-dialog-tick="openCreateDialogTick"
          @select-workflow="onSelectWorkflow"
          @create-workflow="onCreateWorkflow"
          @edit-workflow="onEditWorkflow"
          @delete-workflow="onDeleteWorkflow"
        />

        <div class="center-wrap">
          <LeftBottom :collapsed="leftCollapsed" @toggle="leftCollapsed = !leftCollapsed" />
          <Center
            class="center-panel"
            :nodes="nodes"
            :edges="edges"
            :palette-icons="canvasPaletteIcons"
            :canvas-config="canvasConfig"
            :history-seed="activeWorkflowId || 'draft-workflow'"
            :compact-node="compactNode"
            :initial-zoom-mode="initialZoomMode"
            @update:nodes="onNodesUpdate"
            @update:edges="onEdgesUpdate"
            @connect="handleConnect"
            @drop-node="onDropNode"
            @select-node="onSelectNode"
            @select-edge="onSelectEdge"
            @clear-selection="clearSelection"
            @contextmenu="onCanvasContextmenu"
            @selection-change="onSelectionChange"
            @restore-graph-state="onRestoreGraphState"
            :saving="saving"
            @save="handleSaveAction"
            @nodes-delete="clearSelection"
            @edges-delete="clearSelection"
          />
        </div>

        <Right
          class="right-panel"
          :workflow-meta="workflowMeta"
          :canvas-config="canvasConfig"
          :all-nodes="nodes"
          :selected-node="selectedNode"
          :selected-edge="selectedEdge"
          @update-node="updateSelectedNode"
          @update-edge="updateSelectedEdge"
          @update-canvas-config="updateCanvasConfig"
          @apply-edge-style-all="applyEdgeTypeToAll"
          @remove-selection="removeSelection"
        />
      </main>

      <ContextMenu
        :visible="contextMenu.visible"
        :position="contextMenu.position"
        :items="contextMenu.items"
        @close="closeContextMenu"
        @action="onContextMenuAction"
      />
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { useRoute } from 'vue-router'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import { useBuilderPage } from './app/useBuilderPage'
import { useContextMenuActions } from './app/useContextMenuActions'
import { useWorkflowPersistence } from './app/useWorkflowPersistence'
import Left from './components/left/Left.vue'
import Center from './components/center/Center.vue'
import Right from './components/right/Right.vue'
import LeftBottom from '@/views/admin/ai-workflow/definition/builder/components/center/components/Left-Bottom.vue'
import ContextMenu from './components/context-menu/ContextMenu.vue'
import { canvasPaletteIcons } from './domain/palette'
import { useWorkflowGraph } from '@/views/admin/ai-workflow/definition/builder/composables/useWorkflowGraph'
import { aiWorkflowApi } from '@/api/aiWorkflow'
import { defaultCanvasConfig } from './domain/types'
import type { CanvasGraphState, NodeDropPayload, WorkflowListItem, WorkflowMeta } from './domain/types'

const route = useRoute()
const saving = ref(false)
const activeWorkflowId = ref<string>()
const workflowItems = ref<WorkflowListItem[]>([])
const savedGraphSnapshot = ref('')
const switchingWorkflow = ref(false)
const openCreateDialogTick = ref(0)
const { leftCollapsed, compactNode, initialZoomMode, layoutColumns, layoutColumnsSmall } = useBuilderPage()

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
  const { selected, dragging, resizing, positionAbsolute, dimensions, events, ...rest } = node
  return rest
}

const stripTransientEdgeState = (edge: Record<string, unknown>) => {
  const { selected, updating, events, ...rest } = edge
  return rest
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
      viewport: parsed.viewport || { x: 0, y: 0, zoom: 1 },
      meta: {
        canvasConfig: {
          ...defaultCanvasConfig,
          ...(((parsed.meta?.canvasConfig as Record<string, unknown>) || {}) as Record<string, unknown>)
        }
      }
    }
    return JSON.stringify(normalized)
  } catch {
    return ''
  }
}

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

const isGraphDirtyAgainstBackend = async () => {
  if (!activeWorkflowId.value) return graphDirty.value
  if (workflowMeta.id == null) return graphDirty.value
  try {
    const detail = await aiWorkflowApi.detail(workflowMeta.id)
    const serverSnapshot = normalizeGraphSnapshot(detail.graphJson || '')
    if (serverSnapshot) {
      savedGraphSnapshot.value = serverSnapshot
    }
    return buildGraphSnapshot() !== savedGraphSnapshot.value
  } catch {
    return graphDirty.value
  }
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
    message.info('先完善流程信息，再继续拖拽搭建')
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

const { contextMenu, closeContextMenu, onCanvasContextmenu, onContextMenuAction } = useContextMenuActions({
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

const confirmSaveBeforeSwitch = (target: WorkflowListItem) =>
  new Promise<boolean>((resolve) => {
    Modal.confirm({
      title: '检测到未保存更改，是否先保存？',
      content: `保存当前流程后切换到「${target.workflowName}」`,
      okText: '保存并切换',
      cancelText: '取消',
      onOk: async () => {
        const saved = await handleSaveDraft()
        if (!saved) {
          resolve(false)
          return
        }
        refreshSavedGraphSnapshot()
        resolve(true)
      },
      onCancel: () => resolve(false)
    })
  })

const onSelectWorkflow = async (item: WorkflowListItem) => {
  if (item.id === activeWorkflowId.value) return
  if (switchingWorkflow.value) return

  const dirty = await isGraphDirtyAgainstBackend()
  if (!dirty) {
    await switchToWorkflow(item)
    return
  }

  const confirmed = await confirmSaveBeforeSwitch(item)
  if (!confirmed) return
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
</script>

<style scoped>
.workflow-builder-page {
  padding: 12px;
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
