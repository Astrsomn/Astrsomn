<!-- Vue Flow / Ant Design Vue 3 需使用带参数的 v-model -->
<!-- eslint-disable vue/no-v-model-argument -->
<template>
  <div class="wf-editor">
    <div class="wf-editor-bar">
      <a-button @click="back">返回列表</a-button>
      <div class="bar-center">
        <span class="title">{{ meta.workflowName || meta.workflowKey }}</span>
        <span v-if="meta.workflowKey" class="key-pill">
          <code>{{ meta.workflowKey }}</code>
        </span>
      </div>
      <a-space>
        <a-tag>{{ meta.status }}</a-tag>
        <a-button @click="openTest">测试运行</a-button>
        <a-button type="primary" :loading="saving" @click="save">保存</a-button>
      </a-space>
    </div>

    <p class="wf-intro">
      左侧为<strong>模型</strong>与<strong>智能体</strong>资源库（与「模型配置 / 智能体管理」同源数据）。选中画布上的<strong>任务</strong>节点后，点击智能体即可绑定
      <code>agentKey</code>；任务执行时由后端按该 key 调用 LangChain 智能体。
    </p>

    <div class="wf-editor-body">
      <aside class="resource-panel">
        <div class="panel-block">
          <div class="panel-head">
            <span class="panel-title">模型</span>
            <a-button type="link" size="small" class="panel-refresh" :loading="modelsLoading" @click="loadModels">
              刷新
            </a-button>
          </div>
          <a-spin :spinning="modelsLoading">
            <div class="scroll-list">
              <div v-if="!models.length" class="empty-hint">暂无启用模型，请先在模型配置中添加。</div>
              <div v-for="m in models" :key="String(m.id)" class="res-card res-card--model">
                <div class="res-card__title">{{ m.modelName || m.modelKey }}</div>
                <div class="res-card__row">
                  <code class="mono">{{ m.modelKey }}</code>
                  <a-tag v-if="m.provider" size="small" color="blue">{{ m.provider }}</a-tag>
                </div>
                <div v-if="m.modelType" class="res-card__sub">{{ m.modelType }}</div>
              </div>
            </div>
          </a-spin>
        </div>

        <div class="panel-block">
          <div class="panel-head">
            <span class="panel-title">智能体</span>
            <a-button type="link" size="small" class="panel-refresh" :loading="agentsLoading" @click="loadAgents">
              刷新
            </a-button>
          </div>
          <a-input
            v-model:value="agentSearch"
            allow-clear
            size="small"
            placeholder="搜索名称 / agentKey"
            class="agent-search"
          />
          <a-spin :spinning="agentsLoading">
            <div class="scroll-list scroll-list--agents">
              <div v-if="!filteredAgents.length" class="empty-hint">无匹配智能体</div>
              <div v-for="a in filteredAgents" :key="String(a.id)" class="res-card res-card--agent">
                <div class="res-card__title">{{ a.agentName || a.agentKey }}</div>
                <div class="res-card__row">
                  <code class="mono">{{ a.agentKey }}</code>
                </div>
                <div v-if="a.chatInstanceName || a.modelName" class="res-card__sub">
                  对话实例：{{ a.chatInstanceName || a.modelName }}
                </div>
                <a-button type="primary" size="small" ghost block class="bind-btn" @click="applyAgentFromLibrary(a)">
                  绑定到当前任务节点
                </a-button>
              </div>
            </div>
          </a-spin>
        </div>
      </aside>

      <div class="canvas-column">
        <div class="toolbar-nodes">
          <span class="hint">添加节点：</span>
          <a-button size="small" @click="addNode('input')">开始</a-button>
          <a-button size="small" type="primary" ghost @click="addNode('task')">任务</a-button>
          <a-button size="small" @click="addNode('condition')">条件</a-button>
          <a-button size="small" @click="addNode('output')">结束</a-button>
        </div>

        <div class="canvas-wrap">
          <VueFlow
            v-model:nodes="nodes"
            v-model:edges="edges"
            :node-types="nodeTypes"
            fit-view-on-init
            :default-edge-options="{ animated: true, style: { stroke: '#6366f1', strokeWidth: 1.5 } }"
            @node-click="onNodeClick"
          >
            <Background pattern-color="#555" :gap="20" />
            <Controls />
          </VueFlow>
        </div>
      </div>

      <aside v-if="selected" class="side-panel">
        <div class="side-title">节点属性</div>
        <a-form layout="vertical" size="small">
          <a-form-item label="显示名">
            <a-input v-model:value="nodeLabel" />
          </a-form-item>
          <a-form-item label="语义 kind">
            <a-select v-model:value="selectedKind" :options="kindOptions" />
          </a-form-item>

          <template v-if="selectedKind === 'task'">
            <a-divider orientation="left" plain>与智能体 / 模型</a-divider>
            <a-form-item label="选择已配置智能体">
              <a-select
                v-model:value="taskAgentKeyForSelect"
                allow-clear
                show-search
                placeholder="下拉选择或清空后手动输入 agentKey"
                :options="agentSelectOptions"
                :filter-option="filterAgentOption"
                @change="onAgentSelectChange"
              />
            </a-form-item>
            <a-form-item label="agentKey（执行用）">
              <a-input v-model:value="taskAgentKey" placeholder="与 AstroAssistantFactory 注册键一致" />
            </a-form-item>
            <a-form-item label="outputVar">
              <a-input v-model:value="taskOutputVar" placeholder="默认 lastAssistantReply" />
            </a-form-item>
            <div v-if="taskMetaLine" class="meta-readonly">{{ taskMetaLine }}</div>
          </template>

          <a-form-item v-if="selectedKind === 'condition'" label="conditionKey">
            <a-input v-model:value="condKey" placeholder="上下文布尔变量名" />
          </a-form-item>
          <a-form-item>
            <a-button danger size="small" @click="removeSelectedNode">删除此节点</a-button>
          </a-form-item>
        </a-form>
      </aside>

      <div v-else class="side-panel side-panel--empty">
        <div class="side-title">节点属性</div>
        <p class="empty-side">点击画布中的节点以编辑属性、绑定智能体。</p>
      </div>
    </div>

    <a-modal
      v-model:open="testOpen"
      title="测试运行"
      ok-text="执行"
      width="640px"
      :confirm-loading="testLoading"
      @ok="runTest"
    >
      <a-form layout="vertical">
        <a-form-item label="userMessage">
          <a-textarea v-model:value="testForm.userMessage" :rows="3" placeholder="写入 WorkflowContext.userMessage" />
        </a-form-item>
        <a-form-item label="memoryKey（可选）">
          <a-input v-model:value="testForm.memoryKey" placeholder="默认用执行上下文" />
        </a-form-item>
      </a-form>
      <template v-if="lastTestResult">
        <a-divider>最近一次结果</a-divider>
        <a-descriptions size="small" bordered :column="1">
          <a-descriptions-item label="status">{{ lastTestResult.status || '—' }}</a-descriptions-item>
          <a-descriptions-item label="lastNodeId">{{ lastTestResult.lastNodeId || '—' }}</a-descriptions-item>
          <a-descriptions-item label="message">{{ lastTestResult.message || '—' }}</a-descriptions-item>
        </a-descriptions>
        <pre v-if="testVariablesJson" class="test-json">{{ testVariablesJson }}</pre>
      </template>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { computed, markRaw, onMounted, reactive, ref, shallowRef } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { VueFlow } from '@vue-flow/core'
import { Background } from '@vue-flow/background'
import { Controls } from '@vue-flow/controls'
import type { Edge, Node } from '@vue-flow/core'
import { aiWorkflowApi, type AiWorkflow, type WorkflowTestRunResult } from '@/api/aiWorkflow'
import { aiAgentApi, type AiAgent } from '@/api/aiAgent'
import { aiModelApi, type AiModel } from '@/api/aiModel'
import WfNodeInput from './nodes/WfNodeInput.vue'
import WfNodeOutput from './nodes/WfNodeOutput.vue'
import WfNodeTask from './nodes/WfNodeTask.vue'
import WfNodeCondition from './nodes/WfNodeCondition.vue'

const route = useRoute()
const router = useRouter()

const nodeTypes = {
  wfInput: markRaw(WfNodeInput),
  wfOutput: markRaw(WfNodeOutput),
  wfTask: markRaw(WfNodeTask),
  wfCondition: markRaw(WfNodeCondition)
}

const kindToVueType: Record<string, string> = {
  input: 'wfInput',
  output: 'wfOutput',
  task: 'wfTask',
  condition: 'wfCondition'
}

const meta = reactive<AiWorkflow>({})
const nodes = ref<Node[]>([])
const edges = ref<Edge[]>([])
/** 画布节点引用：用 shallowRef 避免 Vue Flow Node 泛型嵌套过深触发类型检查问题 */
const selected = shallowRef<Node | null>(null)
const saving = ref(false)

const modelsLoading = ref(false)
const agentsLoading = ref(false)
const models = ref<AiModel[]>([])
const agents = ref<AiAgent[]>([])
const agentSearch = ref('')

const ensureData = (n: Node) => {
  if (!n.data) n.data = {}
  return n.data as Record<string, string | undefined>
}

/** 将历史画布（input/default 等）规范为自定义节点类型，语义仍以 data.kind 为准 */
const normalizeNode = (n: Node): Node => {
  const d = (n.data || {}) as Record<string, string | undefined>
  const kind = d.kind || 'task'
  let t = String(n.type || '')
  if (t === 'input' || kind === 'input') t = 'wfInput'
  else if (t === 'output' || kind === 'output') t = 'wfOutput'
  else if (kind === 'condition') t = 'wfCondition'
  else if (t === 'default' || kind === 'task' || !t || t === 'task') t = 'wfTask'
  else if (!['wfInput', 'wfOutput', 'wfTask', 'wfCondition'].includes(t)) t = 'wfTask'
  const lbl = String(n.label || d.label || n.id)
  return {
    ...n,
    type: t as Node['type'],
    label: lbl,
    data: { ...d, label: lbl, kind }
  } as Node
}

type NodeLabelView = { label?: string; data?: { label?: string } }

const nodeLabel = computed({
  get: () => {
    const n = selected.value
    if (!n) return ''
    const raw = n as unknown as NodeLabelView
    return String(raw.label || raw.data?.label || '')
  },
  set: (v: string) => {
    const n = selected.value
    if (!n) return
    ensureData(n).label = v
    ;(n as unknown as NodeLabelView).label = v
  }
})

const taskAgentKey = computed({
  get: () => (selected.value ? String(ensureData(selected.value).agentKey || '') : ''),
  set: (v: string) => {
    if (selected.value) ensureData(selected.value).agentKey = v
  }
})

/** 仅用于下拉：与 taskAgentKey 同步，空字符串用 undefined 以便 placeholder */
const taskAgentKeyForSelect = computed({
  get: () => taskAgentKey.value || undefined,
  set: (v: string | undefined) => {
    if (selected.value) {
      ensureData(selected.value).agentKey = v || ''
      if (!v) {
        delete ensureData(selected.value).agentName
        delete ensureData(selected.value).modelName
      }
    }
  }
})

const taskOutputVar = computed({
  get: () => (selected.value ? String(ensureData(selected.value).outputVar || '') : ''),
  set: (v: string) => {
    if (selected.value) ensureData(selected.value).outputVar = v
  }
})

const condKey = computed({
  get: () => (selected.value ? String(ensureData(selected.value).conditionKey || '') : ''),
  set: (v: string) => {
    if (selected.value) ensureData(selected.value).conditionKey = v
  }
})

const selectedKind = computed({
  get: () => (selected.value ? String(ensureData(selected.value).kind || 'task') : 'task'),
  set: (k: string) => {
    const n = selected.value
    if (!n) return
    ensureData(n).kind = k
    n.type = (kindToVueType[k] || 'wfTask') as Node['type']
  }
})

const kindOptions = [
  { label: '开始 input', value: 'input' },
  { label: '任务 task', value: 'task' },
  { label: '条件 condition', value: 'condition' },
  { label: '结束 output', value: 'output' }
]

const filteredAgents = computed(() => {
  const q = agentSearch.value.trim().toLowerCase()
  if (!q) return agents.value
  return agents.value.filter(
    (a) =>
      String(a.agentName || '')
        .toLowerCase()
        .includes(q) ||
      String(a.agentKey || '')
        .toLowerCase()
        .includes(q)
  )
})

const agentSelectOptions = computed(() =>
  agents.value
    .filter((a) => a.agentKey)
    .map((a) => ({
      value: String(a.agentKey),
      label: `${a.agentName || a.agentKey} (${a.agentKey})${
        a.chatInstanceName || a.modelName ? ` · ${a.chatInstanceName || a.modelName}` : ''
      }`
    }))
)

const filterAgentOption = (input: string, option?: { label?: string; value?: string }) => {
  const q = input.trim().toLowerCase()
  if (!q) return true
  const label = String(option?.label || '')
  const value = String(option?.value || '')
  return label.toLowerCase().includes(q) || value.toLowerCase().includes(q)
}

const taskMetaLine = computed(() => {
  const n = selected.value
  if (!n) return ''
  const d = ensureData(n)
  if (d.agentName || d.modelName) {
    const parts = [d.agentName, d.modelName].filter(Boolean)
    return parts.join(' · ')
  }
  return ''
})

const loadModels = async () => {
  modelsLoading.value = true
  try {
    const r = await aiModelApi.queryPage({
      pageNo: 1,
      pageSize: 120,
      param: { status: 'enabled' }
    })
    models.value = r.list || []
  } catch (e: unknown) {
    message.error(e instanceof Error ? e.message : '加载模型失败')
  } finally {
    modelsLoading.value = false
  }
}

const loadAgents = async () => {
  agentsLoading.value = true
  try {
    const r = await aiAgentApi.queryPage({
      pageNo: 1,
      pageSize: 200,
      param: { status: 'enabled' }
    })
    agents.value = r.list || []
  } catch (e: unknown) {
    message.error(e instanceof Error ? e.message : '加载智能体失败')
  } finally {
    agentsLoading.value = false
  }
}

const applyAgentFromLibrary = (a: AiAgent) => {
  const n = selected.value
  if (!n || String(ensureData(n).kind || '') !== 'task') {
    message.warning('请先在画布上点击选中一个「任务」节点')
    return
  }
  if (!a.agentKey) {
    message.warning('该智能体缺少 agentKey')
    return
  }
  const d = ensureData(n)
  d.agentKey = String(a.agentKey)
  if (a.agentName) d.agentName = String(a.agentName)
  const sub = a.chatInstanceName || a.modelName
  if (sub) d.modelName = String(sub)
  message.success('已绑定智能体')
}

const onAgentSelectChange = (val: string | undefined) => {
  if (!selected.value || String(ensureData(selected.value).kind || '') !== 'task') return
  const d = ensureData(selected.value)
  if (!val) {
    delete d.agentName
    delete d.modelName
    return
  }
  const found = agents.value.find((x) => String(x.agentKey) === val)
  if (found) {
    if (found.agentName) d.agentName = String(found.agentName)
    const sub = found.chatInstanceName || found.modelName
    if (sub) d.modelName = String(sub)
  }
}

const load = async () => {
  const id = route.params.id as string
  if (!id) {
    message.error('缺少 id')
    return
  }
  try {
    const row = await aiWorkflowApi.detail(id)
    Object.assign(meta, row)
    if (row.graphJson) {
      const raw = JSON.parse(row.graphJson) as { nodes?: Node[]; edges?: Edge[] }
      nodes.value = (raw.nodes || []).map((n) => normalizeNode(n))
      edges.value = raw.edges || []
    }
  } catch (e: unknown) {
    message.error(e instanceof Error ? e.message : '加载失败')
  }
}

onMounted(() => {
  void load()
  void loadModels()
  void loadAgents()
})

const back = () => {
  void router.push({ name: 'AdminWorkflows' })
}

const addNode = (kind: string) => {
  const id = `n-${Date.now()}`
  const labels: Record<string, string> = {
    input: '开始',
    task: '任务',
    condition: '条件',
    output: '结束'
  }
  const text = labels[kind] || kind
  const vf = kindToVueType[kind] || 'wfTask'
  nodes.value = [
    ...nodes.value,
    {
      id,
      type: vf,
      position: { x: 140 + Math.random() * 180, y: 100 + Math.random() * 120 },
      label: text,
      data: { label: text, kind }
    }
  ]
}

const onNodeClick = (e: { node: Node }) => {
  const n = e.node
  ensureData(n)
  selected.value = n
}

const removeSelectedNode = () => {
  if (!selected.value) return
  const id = selected.value.id
  nodes.value = nodes.value.filter((n) => n.id !== id)
  edges.value = edges.value.filter((e) => e.source !== id && e.target !== id)
  selected.value = null
}

const save = async () => {
  if (!meta.id) return
  saving.value = true
  try {
    const def = {
      id: meta.workflowKey || 'wf',
      name: meta.workflowName || meta.workflowKey,
      nodes: nodes.value,
      edges: edges.value
    }
    await aiWorkflowApi.update({
      id: meta.id,
      graphJson: JSON.stringify(def)
    })
    message.success('已保存')
  } catch (e: unknown) {
    message.error(e instanceof Error ? e.message : '保存失败')
  } finally {
    saving.value = false
  }
}

const testOpen = ref(false)
const testLoading = ref(false)
const testForm = reactive({
  userMessage: 'hello',
  memoryKey: ''
})
const lastTestResult = ref<WorkflowTestRunResult | null>(null)

const testVariablesJson = computed(() => {
  const v = lastTestResult.value?.variables
  if (!v || typeof v !== 'object') return ''
  try {
    return JSON.stringify(v, null, 2)
  } catch {
    return ''
  }
})

const openTest = () => {
  testOpen.value = true
}

const runTest = async () => {
  if (!meta.id) return
  testLoading.value = true
  try {
    const res = await aiWorkflowApi.testRun({
      id: meta.id,
      userMessage: testForm.userMessage,
      memoryKey: testForm.memoryKey || undefined
    })
    lastTestResult.value = res
    message.success(`状态: ${res.status || ''} 末节点: ${res.lastNodeId || ''}`)
  } catch (e: unknown) {
    message.error(e instanceof Error ? e.message : '执行失败')
  } finally {
    testLoading.value = false
  }
}
</script>

<style scoped>
.wf-editor {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-height: calc(100vh - 120px);
}
.wf-editor-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}
.bar-center {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;
}
.wf-editor-bar .title {
  font-weight: 600;
  font-size: 16px;
}
.key-pill {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 6px;
  background: var(--bg-input, rgba(255, 255, 255, 0.06));
  border: 1px solid var(--border-subtle, #333);
}
.key-pill code {
  font-size: 11px;
  color: var(--text-secondary, #aaa);
}
.wf-intro {
  margin: 0;
  font-size: 13px;
  line-height: 1.55;
  color: var(--text-secondary, #9ca3af);
}
.wf-intro code {
  font-size: 12px;
  padding: 0 4px;
  border-radius: 4px;
  background: rgba(0, 0, 0, 0.25);
}
.wf-editor-body {
  display: grid;
  grid-template-columns: 280px 1fr 300px;
  gap: 14px;
  flex: 1;
  min-height: 600px;
  align-items: stretch;
}
.resource-panel {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 0;
}
.panel-block {
  border: 1px solid var(--border-subtle, #333);
  border-radius: 10px;
  padding: 10px;
  background: var(--bg-card, #141414);
  display: flex;
  flex-direction: column;
  min-height: 0;
  flex: 1;
}
.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}
.panel-title {
  font-weight: 600;
  font-size: 14px;
}
.panel-refresh {
  padding: 0;
  height: auto;
}
.scroll-list {
  max-height: 220px;
  overflow: auto;
  padding-right: 4px;
}
.scroll-list--agents {
  max-height: 320px;
}
.agent-search {
  margin-bottom: 8px;
}
.empty-hint {
  font-size: 12px;
  color: var(--text-muted, #6b7280);
  padding: 8px 0;
}
.res-card {
  padding: 8px 10px;
  border-radius: 8px;
  margin-bottom: 8px;
  border: 1px solid var(--border-subtle, #2a2a2a);
  background: rgba(0, 0, 0, 0.2);
}
.res-card--model {
  border-left: 3px solid color-mix(in srgb, var(--primary, #6366f1) 70%, transparent);
}
.res-card--agent {
  border-left: 3px solid color-mix(in srgb, var(--success, #22c55e) 65%, transparent);
}
.res-card__title {
  font-weight: 500;
  font-size: 13px;
  margin-bottom: 4px;
}
.res-card__row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px;
}
.mono {
  font-size: 11px;
  word-break: break-all;
  color: var(--text-secondary, #a1a1aa);
}
.res-card__sub {
  font-size: 11px;
  color: var(--text-muted, #71717a);
  margin-top: 4px;
}
.bind-btn {
  margin-top: 8px;
}
.canvas-column {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: 0;
}
.toolbar-nodes {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}
.hint {
  color: var(--text-secondary, #888);
  font-size: 13px;
}
.canvas-wrap {
  border: 1px solid var(--border-subtle, #333);
  border-radius: 10px;
  min-height: 520px;
  flex: 1;
  background: var(--bg-base, #0f0f0f);
}
.canvas-wrap :deep(.vue-flow) {
  min-height: 520px;
}
.side-panel {
  border: 1px solid var(--border-subtle, #333);
  border-radius: 10px;
  padding: 12px;
  background: var(--bg-card, #141414);
  overflow: auto;
  align-self: stretch;
}
.side-panel--empty {
  display: flex;
  flex-direction: column;
}
.side-title {
  font-weight: 600;
  margin-bottom: 8px;
  font-size: 14px;
}
.empty-side {
  font-size: 13px;
  color: var(--text-muted, #6b7280);
  line-height: 1.5;
}
.meta-readonly {
  font-size: 12px;
  color: var(--text-secondary, #9ca3af);
  padding: 6px 0;
  line-height: 1.4;
}
.test-json {
  margin-top: 10px;
  padding: 10px;
  border-radius: 8px;
  background: rgba(0, 0, 0, 0.35);
  font-size: 11px;
  max-height: 220px;
  overflow: auto;
  border: 1px solid var(--border-subtle, #333);
}
@media (max-width: 1200px) {
  .wf-editor-body {
    grid-template-columns: 1fr;
  }
  .resource-panel {
    flex-direction: row;
    flex-wrap: wrap;
  }
  .panel-block {
    flex: 1 1 240px;
  }
}
</style>
