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
        />

        <div class="center-wrap">
          <a-tooltip :title="leftCollapsed ? '展开侧栏' : '收起侧栏'">
            <a-button class="canvas-left-toggle" shape="circle" @click="leftCollapsed = !leftCollapsed">
              <span class="toggle-icon" :class="{ collapsed: leftCollapsed }">
                <MenuUnfoldOutlined v-if="leftCollapsed" />
                <MenuFoldOutlined v-else />
              </span>
            </a-button>
          </a-tooltip>
          <CenterFlowCanvas
            class="center-panel"
            :nodes="nodes"
            :edges="edges"
            :palette-icons="canvasPaletteIcons"
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
          :all-nodes="nodes"
          :selected-node="selectedNode"
          :selected-edge="selectedEdge"
          @update-node="updateSelectedNode"
          @update-edge="updateSelectedEdge"
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
import { message } from 'ant-design-vue'
import { MenuFoldOutlined, MenuUnfoldOutlined } from '@ant-design/icons-vue'
import { useRoute } from 'vue-router'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import { aiWorkflowApi } from '@/api/aiWorkflow'
import Left from './components/Left.vue'
import CenterFlowCanvas from './components/Center-FlowCanvas.vue'
import Right from './components/Right.vue'
import ContextMenu from './components/context-menu/ContextMenu.vue'
import { canvasPaletteIcons } from './constants'
import { useWorkflowGraph } from './composables/useWorkflowGraph'
import type { ContextMenuAction, ContextMenuPosition } from './components/context-menu/types'
import type { CanvasContextMenuPayload, NodeDropPayload, WorkflowListItem, WorkflowMeta } from './types'

const route = useRoute()
const saving = ref(false)
const leftCollapsed = ref(false)
const activeWorkflowId = ref<string>()
const workflowItems = ref<WorkflowListItem[]>([])
const layoutColumns = computed(() => (leftCollapsed.value ? '0px minmax(680px, 1fr) 320px' : '360px minmax(680px, 1fr) 320px'))
const layoutColumnsSmall = computed(() => (leftCollapsed.value ? '0px minmax(520px, 1fr) 300px' : '320px minmax(520px, 1fr) 300px'))

const workflowMeta = reactive<WorkflowMeta>({
  id: undefined,
  workflowName: '未命名流程',
  workflowKey: '',
  description: ''
})

const {
  nodes,
  edges,
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
  onConnect,
  validateGraph,
  loadGraph,
  toGraphJson
} = useWorkflowGraph()

const selectedNodeIds = ref<string[]>([])
const contextMenu = reactive<{
  visible: boolean
  position: ContextMenuPosition
  items: ContextMenuAction[]
  source: 'node' | 'edge' | 'pane' | undefined
  nodeId?: string
  edgeId?: string
}>({
  visible: false,
  position: { x: 0, y: 0 },
  items: [],
  source: undefined
})

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

const closeContextMenu = () => {
  contextMenu.visible = false
}

const openContextMenu = (payload: {
  source: 'node' | 'edge' | 'pane'
  position: ContextMenuPosition
  items: ContextMenuAction[]
  nodeId?: string
  edgeId?: string
}) => {
  contextMenu.visible = true
  contextMenu.source = payload.source
  contextMenu.position = payload.position
  contextMenu.items = payload.items
  contextMenu.nodeId = payload.nodeId
  contextMenu.edgeId = payload.edgeId
}

const onCanvasContextmenu = (payload: CanvasContextMenuPayload) => {
  if (payload.target === 'node' && payload.nodeId) {
    const multi = selectedNodeIds.value.length > 1 && selectedNodeIds.value.includes(payload.nodeId)
    openContextMenu({
      source: 'node',
      position: { x: payload.x, y: payload.y },
      nodeId: payload.nodeId,
      items: multi
        ? [
            { key: 'batch-delete', label: '批量删除节点', icon: '🗑', danger: true },
            { key: 'batch-disable', label: '批量禁用节点', icon: '⛔' }
          ]
        : [
            { key: 'node-delete', label: '删除节点', icon: '🗑', danger: true },
            { key: 'node-copy', label: '复制节点', icon: '📄' },
            { key: 'node-disable', label: '启用/禁用', icon: '⛔' },
            { key: 'node-runfrom', label: '从此节点开始调试', icon: '▶' }
          ]
    })
  } else if (payload.target === 'edge' && payload.edgeId) {
    openContextMenu({
      source: 'edge',
      position: { x: payload.x, y: payload.y },
      edgeId: payload.edgeId,
      items: [
        { key: 'edge-delete', label: '删除连线', icon: '🗑', danger: true },
        { key: 'edge-label', label: '编辑连线标签', icon: '🏷' },
        {
          key: 'edge-style',
          label: '切换连线样式',
          icon: '🔀',
          children: [{ key: 'edge-style-straight', label: '直线' }, { key: 'edge-style-smooth', label: '曲线' }]
        },
        { key: 'edge-style-straight', label: '样式: 直线', icon: '⎯' },
        { key: 'edge-style-smooth', label: '样式: 曲线', icon: '〰' }
      ]
    })
  }
}

const onSelectWorkflow = (item: WorkflowListItem) => {
  activeWorkflowId.value = item.id
  workflowMeta.workflowName = item.workflowName
  workflowMeta.workflowKey = item.workflowKey || ''
  workflowMeta.description = item.description || ''
}

const onContextMenuAction = (key: string) => {
  if (key === 'node-delete' && contextMenu.nodeId) {
    removeNodeById(contextMenu.nodeId)
  } else if (key === 'node-copy' && contextMenu.nodeId) {
    duplicateNodeById(contextMenu.nodeId)
  } else if (key === 'node-disable' && contextMenu.nodeId) {
    toggleNodeDisabled(contextMenu.nodeId)
  } else if (key === 'node-runfrom' && contextMenu.nodeId) {
    message.info(`已设置从节点 ${contextMenu.nodeId} 开始调试（占位）`)
  } else if (key === 'batch-delete') {
    batchRemoveNodes(selectedNodeIds.value)
  } else if (key === 'batch-disable') {
    batchDisableNodes(selectedNodeIds.value)
  } else if (key === 'edge-delete' && contextMenu.edgeId) {
    removeEdgeById(contextMenu.edgeId)
  } else if (key === 'edge-label' && contextMenu.edgeId) {
    const current = edges.value.find((edge) => edge.id === contextMenu.edgeId)?.label
    const next = window.prompt('请输入连线标签', String(current || ''))
    if (next != null) setEdgeLabelById(contextMenu.edgeId, next)
  } else if (key === 'edge-style-straight' && contextMenu.edgeId) {
    updateEdgeStyleById(contextMenu.edgeId, 'straight')
  } else if (key === 'edge-style-smooth' && contextMenu.edgeId) {
    updateEdgeStyleById(contextMenu.edgeId, 'smoothstep')
  }
  closeContextMenu()
}

const fetchWorkflowList = async () => {
  try {
    const resp = await aiWorkflowApi.queryPage({ pageNo: 1, pageSize: 20, param: {} })
    const list = (resp.list || []).map((item) => ({
      id: String(item.id || item.workflowKey || item.workflowName || Math.random()),
      workflowName: item.workflowName || '未命名流程',
      workflowKey: item.workflowKey || '',
      description: item.description || ''
    }))
    workflowItems.value = list
    if (!activeWorkflowId.value && list.length > 0) {
      activeWorkflowId.value = list[0].id
    }
  } catch {
    workflowItems.value = []
  }
}

const submitPayload = computed(() => ({
  id: workflowMeta.id,
  workflowName: workflowMeta.workflowName,
  workflowKey: workflowMeta.workflowKey,
  description: workflowMeta.description,
  graphJson: toGraphJson({
    id: workflowMeta.id,
    workflowName: workflowMeta.workflowName,
    workflowKey: workflowMeta.workflowKey,
    description: workflowMeta.description
  })
}))

const handleSaveDraft = async () => {
  if (!workflowMeta.workflowName || !workflowMeta.workflowKey) {
    message.warning('请先填写流程名称和 Flow Key')
    return
  }
  const graphValidation = validateGraph()
  if (!graphValidation.ok) {
    message.warning(graphValidation.errors[0] || '流程校验未通过')
    return
  }

  saving.value = true
  try {
    let tip = ''
    if (workflowMeta.id == null) {
      tip = await aiWorkflowApi.create({ ...submitPayload.value, id: undefined })
      message.success(tip || '草稿已保存')
    } else {
      tip = await aiWorkflowApi.update(submitPayload.value)
      message.success(tip || '草稿已更新')
    }
  } catch {
    message.warning('当前阶段以页面搭建为主，保存接口可后续联调')
  } finally {
    saving.value = false
  }
}

const fetchDetailIfNeeded = async () => {
  const routeId = route.params.id ?? route.query.id
  const cloneId = route.query.cloneId
  const targetId = routeId ?? cloneId
  if (!targetId) return

  const detail = await aiWorkflowApi.detail(String(targetId))
  workflowMeta.id = detail.id
  workflowMeta.workflowName = detail.workflowName || workflowMeta.workflowName
  workflowMeta.workflowKey = detail.workflowKey || ''
  workflowMeta.description = detail.description || ''
  loadGraph(detail.graphJson)

  if (!routeId && cloneId) {
    workflowMeta.id = undefined
    workflowMeta.workflowName = `${workflowMeta.workflowName}-副本`
    workflowMeta.workflowKey = ''
  }
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

.canvas-left-toggle {
  position: absolute;
  bottom: 14px;
  left: 14px;
  top: auto;
  z-index: 12;
  width: 36px;
  height: 36px;
  border-color: #d7dee8;
  color: #8a94a6;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 6px 14px rgba(15, 23, 42, 0.1);
  transition: border-color 0.2s ease, color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

.canvas-left-toggle:hover {
  border-color: #c5cedb;
  color: #64748b;
  transform: translateY(-1px);
}

.toggle-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.24s ease, opacity 0.24s ease;
}

.toggle-icon.collapsed {
  transform: rotate(180deg) scale(1.06);
}

@media (max-width: 1500px) {
  .builder-layout {
    grid-template-columns: v-bind(layoutColumnsSmall);
  }
}
</style>
