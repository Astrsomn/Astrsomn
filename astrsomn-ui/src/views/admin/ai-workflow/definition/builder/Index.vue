<template>
  <AdminPageShell title="流程搭建" description="通过拖拽节点快速编排工作流骨架。" empty-text="暂无流程数据。">
    <div class="workflow-builder-page">
      <main class="builder-layout">
        <Left
          class="left-panel"
          :class="{ collapsed: leftCollapsed }"
          :workflow-items="workflowItems"
          :active-workflow-id="activeWorkflowId"
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
            :saving="saving"
            @save="handleSaveDraft"
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
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
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
import type { NodeDropPayload, WorkflowListItem, WorkflowMeta } from './domain/types'

const route = useRoute()
const saving = ref(false)
const activeWorkflowId = ref<string>()
const workflowItems = ref<WorkflowListItem[]>([])
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
  onConnect,
  validateGraph,
  loadGraph,
  toGraphJson
} = useWorkflowGraph()

const selectedNodeIds = ref<string[]>([])

const handleConnect = (connection: Parameters<typeof onConnect>[0]) => {
  const result = onConnect(connection)
  if (!result.ok) {
    message.warning(result.message)
  }
}

const onDropNode = (payload: NodeDropPayload) => {
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

const onSelectWorkflow = async (item: WorkflowListItem) => {
  activeWorkflowId.value = item.id
  await fetchWorkflowDetail(item.id)
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
  message.success('流程已创建并进入编辑')
}

const onEditWorkflow = async (
  item: WorkflowListItem,
  payload: { workflowName: string; workflowKey: string; category: string }
) => {
  await updateWorkflowMeta(item, payload)
  message.success('流程信息已更新')
}

const onDeleteWorkflow = async (item: WorkflowListItem) => {
  await deleteWorkflow(item)
  message.success('流程已删除')
}

onMounted(() => {
  void fetchWorkflowList()
  void fetchDetailIfNeeded()
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
