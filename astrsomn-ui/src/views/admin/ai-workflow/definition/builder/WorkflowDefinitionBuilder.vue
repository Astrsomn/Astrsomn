<template>
  <AdminPageShell title="流程搭建" description="通过拖拽节点快速编排工作流骨架。" empty-text="暂无流程数据。">
    <div class="workflow-builder-page">
      <Top
        :workflow-name="workflowMeta.workflowName"
        :subtitle="pageSubtitle"
        :saving="saving"
        @back="goBack"
        @save="handleSaveDraft"
        @update:workflowName="(value) => (workflowMeta.workflowName = value)"
      />

      <main class="builder-layout">
        <Left class="left-panel" />

        <CenterFlowCanvas
          class="center-panel"
          :nodes="nodes"
          :edges="edges"
          @update:nodes="onNodesUpdate"
          @update:edges="onEdgesUpdate"
          @connect="onConnect"
          @drop-node="onDropNode"
          @select-node="onSelectNode"
          @select-edge="onSelectEdge"
          @clear-selection="clearSelection"
          @nodes-delete="clearSelection"
          @edges-delete="clearSelection"
        />

        <Right
          class="right-panel"
          :workflow-meta="workflowMeta"
          :selected-node="selectedNode"
          :selected-edge="selectedEdge"
          @update-node="updateSelectedNode"
          @update-edge="updateSelectedEdge"
          @remove-selection="removeSelection"
        />
      </main>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import { aiWorkflowApi } from '@/api/aiWorkflow'
import Top from './components/Top.vue'
import Left from './components/Left.vue'
import CenterFlowCanvas from './components/Center-FlowCanvas.vue'
import Right from './components/Right.vue'
import { useWorkflowGraph } from './composables/useWorkflowGraph'
import type { NodeDropPayload, WorkflowMeta } from './types'

const route = useRoute()
const router = useRouter()
const saving = ref(false)
const isEditMode = computed(() => route.name === 'AdminWorkflowDefinitionEditBuilder')
const pageTitle = computed(() => (isEditMode.value ? 'Workflow Builder · 编辑' : 'Workflow Builder · 新建'))
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
  updateSelectedNode,
  updateSelectedEdge,
  onConnect,
  loadGraph,
  toGraphJson
} = useWorkflowGraph()

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

const goBack = () => {
  void router.push({ name: 'AdminWorkflowDefinitions' })
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
  height: calc(100vh - 90px);
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
