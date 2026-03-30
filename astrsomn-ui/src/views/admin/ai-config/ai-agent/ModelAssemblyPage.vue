<template>
  <AdminPageShell title="模型组装" description="">
    <div class="assembly-page">
      <a-spin :spinning="loading" class="assembly-spin">
        <div class="assembly-layout">
          <AssemblyLeftPalette
            ref="leftPaletteRef"
            :page-size="pageSize"
            :chat-items="chatItems"
            :embedding-items="embeddingItems"
            :image-items="imageItems"
            :chat-page="pagination.chat"
            :embedding-page="pagination.embedding"
            :image-page="pagination.image"
            @search="onLeftSearch"
            @chat-page="onChatPage"
            @embedding-page="onEmbeddingPage"
            @image-page="onImagePage"
            @drag-start="onDragStart"
            @drag-end="onDragEnd"
          />

          <AssemblyCanvas
            v-model:agent-form="agentForm"
            :dragging-payload="dragPayload"
            :active-drop-key="activeDropKey"
            :chat-instance="chatInstance"
            :embedding-instance="embeddingInstance"
            :image-instance="imageInstance"
            :tools="placedTools"
            :mcps="placedMcps"
            :knowledge-keys="knowledgeKeys"
            @hover="activeDropKey = $event"
            @drop="onCanvasDrop"
            @clear="onClearInstance"
            @remove-tool="removeTool"
            @remove-mcp="removeMcp"
            @remove-knowledge-key="removeKnowledgeKey"
            @reset="handleReset"
            @submit="handleSubmit"
          />

          <AssemblyRightPalette
            ref="rightPaletteRef"
            :page-size="pageSize"
            :tools="toolItems"
            :mcps="mcpItems"
            :tool-page="pagination.tool"
            :mcp-page="pagination.mcp"
            @search-tool="onSearchTool"
            @search-mcp="onSearchMcp"
            @tool-page="onToolPage"
            @mcp-page="onMcpPage"
            @drag-start="onDragStart"
            @drag-end="onDragEnd"
          />
        </div>
      </a-spin>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import type { AiInstance } from '@/api/aiInstance'
import type { AiTool } from '@/api/aiTool'
import type { AiMcp } from '@/api/aiMcp'
import AssemblyLeftPalette from './assembly/AssemblyLeftPalette.vue'
import AssemblyRightPalette from './assembly/AssemblyRightPalette.vue'
import AssemblyCanvas from './assembly/AssemblyCanvas.vue'
import {
  fetchInstancesPaged,
  fetchMcpsPaged,
  fetchToolsPaged
} from './assembly/assemblyFetch'
import type {
  AssemblyAgentForm,
  AssemblyDragPayload,
  AssemblySlotKey,
  InstanceModelType
} from './assembly/assemblyTypes'

const pageSize = 8

const loading = ref(false)
const leftPaletteRef = ref<InstanceType<typeof AssemblyLeftPalette> | null>(null)
const rightPaletteRef = ref<InstanceType<typeof AssemblyRightPalette> | null>(null)

const chatItems = ref<AiInstance[]>([])
const embeddingItems = ref<AiInstance[]>([])
const imageItems = ref<AiInstance[]>([])
const toolItems = ref<AiTool[]>([])
const mcpItems = ref<AiMcp[]>([])

const pagination = reactive({
  chat: { current: 1, total: 0 },
  embedding: { current: 1, total: 0 },
  image: { current: 1, total: 0 },
  tool: { current: 1, total: 0 },
  mcp: { current: 1, total: 0 }
})

const chatInstance = ref<AiInstance | null>(null)
const embeddingInstance = ref<AiInstance | null>(null)
const imageInstance = ref<AiInstance | null>(null)
const placedTools = ref<AiTool[]>([])
const placedMcps = ref<AiMcp[]>([])
const knowledgeKeys = ref<string[]>([])

const agentForm = ref<AssemblyAgentForm>({
  agentName: '',
  enableStream: true,
  description: '',
  memoryMode: 'SLIDING_WINDOW',
  memoryWindowSize: '10'
})

const dragPayload = ref<AssemblyDragPayload | null>(null)
const activeDropKey = ref<AssemblySlotKey | null>(null)

function onDragStart(p: AssemblyDragPayload) {
  dragPayload.value = p
  activeDropKey.value = null
}

function onDragEnd() {
  dragPayload.value = null
  activeDropKey.value = null
}

async function loadChat() {
  const kw = leftPaletteRef.value?.getKeywords?.().chat ?? ''
  const { list, total } = await fetchInstancesPaged('chat', kw, pagination.chat.current, pageSize)
  chatItems.value = list
  pagination.chat.total = total
}

async function loadEmbedding() {
  const kw = leftPaletteRef.value?.getKeywords?.().embedding ?? ''
  const { list, total } = await fetchInstancesPaged('embedding', kw, pagination.embedding.current, pageSize)
  embeddingItems.value = list
  pagination.embedding.total = total
}

async function loadImage() {
  const kw = leftPaletteRef.value?.getKeywords?.().image ?? ''
  const { list, total } = await fetchInstancesPaged('image', kw, pagination.image.current, pageSize)
  imageItems.value = list
  pagination.image.total = total
}

async function loadTools() {
  const kw = rightPaletteRef.value?.getKeywords?.().tool ?? ''
  const { list, total } = await fetchToolsPaged(kw, pagination.tool.current, pageSize)
  toolItems.value = list
  pagination.tool.total = total
}

async function loadMcps() {
  const kw = rightPaletteRef.value?.getKeywords?.().mcp ?? ''
  const { list, total } = await fetchMcpsPaged(kw, pagination.mcp.current, pageSize)
  mcpItems.value = list
  pagination.mcp.total = total
}

async function loadAll() {
  loading.value = true
  try {
    await Promise.all([loadChat(), loadEmbedding(), loadImage(), loadTools(), loadMcps()])
  } catch {
    message.error('加载资源失败')
  } finally {
    loading.value = false
  }
}

function onChatPage(page: number) {
  pagination.chat.current = page
  void loadChatWithLoading()
}

function onEmbeddingPage(page: number) {
  pagination.embedding.current = page
  void loadEmbeddingWithLoading()
}

function onImagePage(page: number) {
  pagination.image.current = page
  void loadImageWithLoading()
}

function onToolPage(page: number) {
  pagination.tool.current = page
  void loadToolsWithLoading()
}

function onMcpPage(page: number) {
  pagination.mcp.current = page
  void loadMcpsWithLoading()
}

async function loadChatWithLoading() {
  loading.value = true
  try {
    await loadChat()
  } finally {
    loading.value = false
  }
}

async function loadEmbeddingWithLoading() {
  loading.value = true
  try {
    await loadEmbedding()
  } finally {
    loading.value = false
  }
}

async function loadImageWithLoading() {
  loading.value = true
  try {
    await loadImage()
  } finally {
    loading.value = false
  }
}

async function loadToolsWithLoading() {
  loading.value = true
  try {
    await loadTools()
  } finally {
    loading.value = false
  }
}

async function loadMcpsWithLoading() {
  loading.value = true
  try {
    await loadMcps()
  } finally {
    loading.value = false
  }
}

async function onLeftSearch(modelType: InstanceModelType, _keyword: string) {
  if (modelType === 'chat') pagination.chat.current = 1
  else if (modelType === 'embedding') pagination.embedding.current = 1
  else pagination.image.current = 1

  loading.value = true
  try {
    if (modelType === 'chat') await loadChat()
    else if (modelType === 'embedding') await loadEmbedding()
    else await loadImage()
  } catch {
    message.error('查询失败')
  } finally {
    loading.value = false
  }
}

async function onSearchTool() {
  pagination.tool.current = 1
  loading.value = true
  try {
    await loadTools()
  } catch {
    message.error('查询工具失败')
  } finally {
    loading.value = false
  }
}

async function onSearchMcp() {
  pagination.mcp.current = 1
  loading.value = true
  try {
    await loadMcps()
  } catch {
    message.error('查询 MCP 失败')
  } finally {
    loading.value = false
  }
}

function onCanvasDrop(p: AssemblyDragPayload) {
  if (p.kind === 'instance') {
    const row = p.data
    if (p.instanceModelType === 'chat') {
      chatInstance.value = { ...row }
      message.success('已装入对话推理预设')
    } else if (p.instanceModelType === 'embedding') {
      embeddingInstance.value = { ...row }
      message.success('已装入向量推理预设')
    } else {
      imageInstance.value = { ...row }
      message.success('已装入图像推理预设')
    }
    return
  }
  if (p.kind === 'tool') {
    const key = p.data.toolKey
    if (!key) return
    if (placedTools.value.some((x) => x.toolKey === key)) {
      message.info('该工具已在列表中')
      return
    }
    placedTools.value = [...placedTools.value, { ...p.data }]
    message.success('已添加工具')
    return
  }
  if (p.kind === 'mcp') {
    const mkey = p.data.mcpKey
    if (!mkey) return
    if (placedMcps.value.some((x) => x.mcpKey === mkey)) {
      message.info('该 MCP 已在列表中')
      return
    }
    placedMcps.value = [...placedMcps.value, { ...p.data }]
    message.success('已添加 MCP')
    return
  }
  if (p.kind === 'knowledgeBase') {
    const kb = p.data.kbKey?.trim()
    if (!kb) return
    if (knowledgeKeys.value.includes(kb)) {
      message.info('该知识库索引已存在')
      return
    }
    knowledgeKeys.value = [...knowledgeKeys.value, kb]
    message.success('已添加知识库索引')
  }
}

function removeKnowledgeKey(key: string) {
  knowledgeKeys.value = knowledgeKeys.value.filter((k) => k !== key)
}

function onClearInstance(which: 'chatInstance' | 'embeddingInstance' | 'imageInstance') {
  if (which === 'chatInstance') chatInstance.value = null
  else if (which === 'embeddingInstance') embeddingInstance.value = null
  else imageInstance.value = null
}

function removeTool(toolKey: string) {
  placedTools.value = placedTools.value.filter((t) => t.toolKey !== toolKey)
}

function removeMcp(mcpKey: string) {
  placedMcps.value = placedMcps.value.filter((m) => m.mcpKey !== mcpKey)
}

function resetPagination() {
  pagination.chat.current = 1
  pagination.embedding.current = 1
  pagination.image.current = 1
  pagination.tool.current = 1
  pagination.mcp.current = 1
}

function handleReset() {
  chatInstance.value = null
  embeddingInstance.value = null
  imageInstance.value = null
  placedTools.value = []
  placedMcps.value = []
  knowledgeKeys.value = []
  agentForm.value = {
    agentName: '',
    enableStream: true,
    description: '',
    memoryMode: 'SLIDING_WINDOW',
    memoryWindowSize: '10'
  }
  resetPagination()
  message.info('已重置组装区与列表页码')
  void loadAll()
}

function buildSubmitPayload() {
  const f = agentForm.value
  return {
    agentName: f.agentName,
    enableStream: f.enableStream,
    description: f.description,
    memoryMode: f.memoryMode,
    memoryWindowSize: String(f.memoryWindowSize ?? ''),
    knowledgeBaseKeys: knowledgeKeys.value.join(','),
    chatInstanceKey: chatInstance.value?.instanceKey,
    embeddingInstanceKey: embeddingInstance.value?.instanceKey,
    imageInstanceKey: imageInstance.value?.instanceKey,
    toolKeys: placedTools.value.map((t) => t.toolKey).filter(Boolean),
    mcpKeys: placedMcps.value.map((m) => m.mcpKey).filter(Boolean)
  }
}

function handleSubmit() {
  const payload = buildSubmitPayload()
  const text = JSON.stringify(payload, null, 2)
  if (navigator.clipboard?.writeText) {
    void navigator.clipboard.writeText(text).then(
      () => message.success('已提交并复制组装 JSON 到剪贴板'),
      () => message.success('已提交（预览）')
    )
  } else {
    message.success('已提交（预览）')
  }
}

onMounted(() => {
  void loadAll()
})
</script>

<style scoped>
.assembly-page {
  /* max-height: calc(100vh - 130px); */
  /* min-height: 620px; */
  display: flex;
  flex-direction: column;
}

.assembly-spin {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.assembly-spin :deep(.ant-spin-container) {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.assembly-layout {
  display: grid;
  grid-template-columns: minmax(248px, 300px) minmax(380px, 1fr) minmax(248px, 300px);
  gap: 14px;
  align-items: stretch;
  flex: 1;
  min-height: 0;
  min-width: 0;
}

.assembly-layout > * {
  min-width: 0;
}

@media (max-width: 1200px) {
  .assembly-layout {
    grid-template-columns: 1fr;
    height: auto;
    min-height: calc(100vh - 112px);
  }
}
</style>
