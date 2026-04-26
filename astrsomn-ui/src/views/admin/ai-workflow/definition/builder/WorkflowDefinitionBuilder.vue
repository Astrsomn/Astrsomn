<template>
  <AdminPageShell title="流程搭建" description="通过拖拽节点快速编排工作流骨架。" empty-text="暂无流程数据。">
    <div class="workflow-builder-page">
      <main class="builder-layout">
        <Left class="left-panel" :pin-refresh-token="palettePinRefreshToken" @palette-contextmenu="onPaletteContextmenu" />

        <CenterFlowCanvas
          class="center-panel"
          :nodes="nodes"
          :edges="edges"
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
import { useRoute, useRouter } from 'vue-router'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import { aiWorkflowApi } from '@/api/aiWorkflow'
import Left from './components/Left.vue'
import CenterFlowCanvas from './components/Center-FlowCanvas.vue'
import Right from './components/Right.vue'
import ContextMenu from './components/context-menu/ContextMenu.vue'
import { useWorkflowGraph } from './composables/useWorkflowGraph'
import type { ContextMenuAction, ContextMenuPosition } from './components/context-menu/types'
import type { CanvasContextMenuPayload, NodeDropPayload, PaletteContextMenuPayload, WorkflowMeta } from './types'

const route = useRoute()
const saving = ref(false)
const isEditMode = computed(() => route.name === 'AdminWorkflowDefinitionEditBuilder')
const pageSubtitle = computed(() =>
  isEditMode.value ? '修改已有流程的画布与节点配置' : '左侧拖拽节点到画布，右侧配置节点参数'
)

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

const PIN_STORAGE_KEY = 'workflow-node-pins-v1'
const selectedNodeIds = ref<string[]>([])
const palettePinRefreshToken = ref(0)
const contextMenu = reactive<{
  visible: boolean
  position: ContextMenuPosition
  items: ContextMenuAction[]
  source: 'node' | 'edge' | 'pane' | 'palette' | undefined
  nodeId?: string
  edgeId?: string
  paletteType?: NodeDropPayload['type']
  paletteLabel?: string
  paletteDescription?: string
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
  source: 'node' | 'edge' | 'pane' | 'palette'
  position: ContextMenuPosition
  items: ContextMenuAction[]
  nodeId?: string
  edgeId?: string
  paletteType?: NodeDropPayload['type']
  paletteLabel?: string
  paletteDescription?: string
}) => {
  contextMenu.visible = true
  contextMenu.source = payload.source
  contextMenu.position = payload.position
  contextMenu.items = payload.items
  contextMenu.nodeId = payload.nodeId
  contextMenu.edgeId = payload.edgeId
  contextMenu.paletteType = payload.paletteType
  contextMenu.paletteLabel = payload.paletteLabel
  contextMenu.paletteDescription = payload.paletteDescription
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
  } else {
    openContextMenu({
      source: 'pane',
      position: { x: payload.x, y: payload.y },
      items: [{ key: 'pane-clear-selection', label: '清空选择', icon: '✖' }]
    })
  }
}

const onPaletteContextmenu = (payload: PaletteContextMenuPayload) => {
  const pinned = getPinnedTypes().includes(payload.type)
  openContextMenu({
    source: 'palette',
    position: { x: payload.x, y: payload.y },
    paletteType: payload.type,
    paletteLabel: payload.label,
    paletteDescription: payload.description,
    items: [
      { key: 'palette-pin', label: pinned ? '取消置顶' : '置顶节点', icon: '📌' },
      { key: 'palette-insert', label: '插入到画布中心', icon: '➕' },
      { key: 'palette-doc', label: '查看节点说明', icon: 'ℹ' }
    ]
  })
}

const getPinnedTypes = () => {
  try {
    const parsed = JSON.parse(localStorage.getItem(PIN_STORAGE_KEY) || '[]') as string[]
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

const togglePinType = (type: string) => {
  const current = getPinnedTypes()
  const exists = current.includes(type)
  const next = exists ? current.filter((item) => item !== type) : [type, ...current]
  localStorage.setItem(PIN_STORAGE_KEY, JSON.stringify(next))
  palettePinRefreshToken.value += 1
}

const onContextMenuAction = (key: string) => {
  if (key === 'pane-clear-selection') {
    clearSelection()
  } else if (key === 'node-delete' && contextMenu.nodeId) {
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
  } else if (key === 'palette-pin' && contextMenu.paletteType) {
    togglePinType(contextMenu.paletteType)
  } else if (key === 'palette-insert' && contextMenu.paletteType) {
    createNode(contextMenu.paletteType, { x: 480, y: 240 })
  } else if (key === 'palette-doc') {
    message.info(contextMenu.paletteDescription || '暂无节点说明')
  }
  closeContextMenu()
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
  grid-template-columns: 280px minmax(640px, 1fr) 320px;
  gap: 12px;
}

.left-panel,
.center-panel,
.right-panel {
  min-height: 0;
}

@media (max-width: 1500px) {
  .builder-layout {
    grid-template-columns: 240px minmax(540px, 1fr) 300px;
  }
}
</style>
