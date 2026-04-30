<template>
  <AstrsomnModal
    :open="visible"
    @update:open="emit('update:visible', $event)"
    @cancel="handleCancel"
  >
    <template #header-logo>
      <ThunderboltFilled />
    </template>
    <template #header-title>
      {{ isEdit ? '编辑智能体' : '新建智能体' }}
    </template>
    <template #header-subtitle>
      通过拖拽组装模型实例、工具与知识库，定义 Astrsomn 智能体策略
    </template>
    <template #header-actions>
      <AstrsomnSegmentedButton :buttons="headerFormSegmentButtons" />
    </template>

    <div class="assembly-page">
      <div class="assembly-layout">
        <AssemblyLeftPalette
          ref="leftPaletteRef"
          :page-size="pageSize.value"
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
          :prompt-instance="promptInstance"
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
          :page-size="pageSize.value"
          :tools="toolItems"
          :mcps="mcpItems"
          :prompts="promptItems"
          :tool-page="pagination.tool"
          :mcp-page="pagination.mcp"
          :prompt-page="pagination.prompt"
          @search-tool="onSearchTool"
          @search-mcp="onSearchMcp"
          @search-prompt="onSearchPrompt"
          @tool-page="onToolPage"
          @mcp-page="onMcpPage"
          @prompt-page="onPromptPage"
          @drag-start="onDragStart"
          @drag-end="onDragEnd"
        />
      </div>
    </div>
  </AstrsomnModal>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import {
  ThunderboltFilled,
  CloseOutlined,
  SaveOutlined,
  CloudUploadOutlined,
} from '@ant-design/icons-vue'
import AstrsomnModal from '@/components/home/AstrsomnModal.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'
import type { AiInstance } from '@/api/aiInstance'
import type { AiTool } from '@/api/aiTool'
import type { AiMcp } from '@/api/aiMcp'
import type { AiPrompt } from '@/api/aiPrompt'
import AssemblyLeftPalette from './assembly/AssemblyLeftPalette.vue'
import AssemblyRightPalette from './assembly/AssemblyRightPalette.vue'
import AssemblyCanvas from './assembly/AssemblyCanvas.vue'
import {
  fetchInstancesPaged,
  fetchMcpsPaged,
  fetchToolsPaged,
  fetchPromptsPaged
} from './assembly/assemblyFetch'
import { aiAgentApi } from '@/api/aiAgent'
import type {
  AssemblyAgentForm,
  AssemblyDragPayload,
  AssemblySlotKey,
  InstanceModelType
} from './assembly/assemblyTypes'
import type { AiAgent } from '@/api/aiAgent'

interface Props {
  visible: boolean
  recordId?: string | number
}

const props = defineProps<Props>()
const emit = defineEmits(['update:visible', 'success'])

const loading = ref(false)

const isEdit = computed(() => !!props.recordId)

const headerFormSegmentButtons = computed<SegmentedButton[]>(() => [
  {
    label: '取消',
    icon: CloseOutlined,
    disabled: loading.value,
    onClick: handleCancel,
  },
  {
    label: isEdit.value ? '保存' : '发布',
    type: 'primary',
    icon: isEdit.value ? SaveOutlined : CloudUploadOutlined,
    loading: loading.value,
    onClick: handleSubmit,
  },
])

// 动态计算 pageSize，基于容器高度保守估算
const calculatePageSize = () => {
  // 容器高度：calc(100vh - 100px)
  const containerHeight = window.innerHeight - 100
  
  // 减去 tab 栏、搜索框、分页按钮的高度
  const headerHeight = 100 // tab栏 + 搜索框 + 分页按钮
  const availableHeight = containerHeight - headerHeight
  
  // 每个芯片的估算高度（包括间距）
  const chipHeight = 60 // 保守估算，包括上下间距
  
  // 计算最大可容纳的芯片数量，保守一点，取整后减1
  const maxChips = Math.floor(availableHeight / chipHeight) - 1
  
  // 确保至少显示 3 个
  return Math.max(3, maxChips)
}

// 初始计算 pageSize
const pageSize = ref(calculatePageSize())

// 监听窗口大小变化，重新计算 pageSize
window.addEventListener('resize', () => {
  const oldPageSize = pageSize.value
  pageSize.value = calculatePageSize()
  
  // 如果 pageSize 发生变化，重置页码并重新加载数据
  if (oldPageSize !== pageSize.value) {
    resetPagination()
    void loadAll()
  }
})

const leftPaletteRef = ref<InstanceType<typeof AssemblyLeftPalette> | null>(null)
const rightPaletteRef = ref<InstanceType<typeof AssemblyRightPalette> | null>(null)

const chatItems = ref<AiInstance[]>([])
const embeddingItems = ref<AiInstance[]>([])
const imageItems = ref<AiInstance[]>([])
const toolItems = ref<AiTool[]>([])
const mcpItems = ref<AiMcp[]>([])
const promptItems = ref<AiPrompt[]>([])

const pagination = reactive({
  chat: { current: 1, total: 0 },
  embedding: { current: 1, total: 0 },
  image: { current: 1, total: 0 },
  tool: { current: 1, total: 0 },
  mcp: { current: 1, total: 0 },
  prompt: { current: 1, total: 0 }
})

const chatInstance = ref<AiInstance | null>(null)
const embeddingInstance = ref<AiInstance | null>(null)
const imageInstance = ref<AiInstance | null>(null)
const promptInstance = ref<AiPrompt | null>(null)
const placedTools = ref<AiTool[]>([])
const placedMcps = ref<AiMcp[]>([])
const knowledgeKeys = ref<string[]>([])

const agentForm = ref<AssemblyAgentForm>({
  agentName: '',
  agentKey: '',
  status: 'enabled',
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
  const { list, total } = await fetchInstancesPaged('chat', kw, pagination.chat.current, pageSize.value)
  chatItems.value = list
  pagination.chat.total = total
}

async function loadEmbedding() {
  const kw = leftPaletteRef.value?.getKeywords?.().embedding ?? ''
  const { list, total } = await fetchInstancesPaged('embedding', kw, pagination.embedding.current, pageSize.value)
  embeddingItems.value = list
  pagination.embedding.total = total
}

async function loadImage() {
  const kw = leftPaletteRef.value?.getKeywords?.().image ?? ''
  const { list, total } = await fetchInstancesPaged('image', kw, pagination.image.current, pageSize.value)
  imageItems.value = list
  pagination.image.total = total
}

async function loadTools() {
  const kw = rightPaletteRef.value?.getKeywords?.().tool ?? ''
  const { list, total } = await fetchToolsPaged(kw, pagination.tool.current, pageSize.value)
  toolItems.value = list
  pagination.tool.total = total
}

async function loadMcps() {
  const kw = rightPaletteRef.value?.getKeywords?.().mcp ?? ''
  const { list, total } = await fetchMcpsPaged(kw, pagination.mcp.current, pageSize.value)
  mcpItems.value = list
  pagination.mcp.total = total
}

async function loadPrompts() {
  const kw = rightPaletteRef.value?.getKeywords?.().prompt ?? ''
  const { list, total } = await fetchPromptsPaged(kw, pagination.prompt.current, pageSize.value)
  promptItems.value = list
  pagination.prompt.total = total
}

async function loadAll() {
  try {
    await Promise.all([loadChat(), loadEmbedding(), loadImage(), loadTools(), loadMcps(), loadPrompts()])
  } catch {
    message.error('加载资源失败')
  }
}

function applyAgentDetail(detail: AiAgent) {
  agentForm.value = {
    agentName: detail.agentName ?? '',
    agentKey: detail.agentKey ?? '',
    status: detail.status ?? 'enabled',
    enableStream: detail.enableStream ?? true,
    description: detail.description ?? '',
    memoryMode: (detail.memoryMode as AssemblyAgentForm['memoryMode']) ?? 'SLIDING_WINDOW',
    memoryWindowSize: detail.memoryWindowSize ?? '10'
  }

  knowledgeKeys.value = (detail.knowledgeBaseKeys || '')
    .split(',')
    .map((k) => k.trim())
    .filter(Boolean)

  if (detail.chatInstanceKey) {
    chatInstance.value =
      chatItems.value.find((i) => i.instanceKey === detail.chatInstanceKey) ?? null
  }
  if (detail.embeddingInstanceKey) {
    embeddingInstance.value =
      embeddingItems.value.find((i) => i.instanceKey === detail.embeddingInstanceKey) ?? null
  }
  if (detail.imageInstanceKey) {
    imageInstance.value =
      imageItems.value.find((i) => i.instanceKey === detail.imageInstanceKey) ?? null
  }
  if (detail.promptKey) {
    promptInstance.value =
      promptItems.value.find((p) => p.promptKey === detail.promptKey) ?? null
  }

  if (detail.toolKeys) {
    const keys = detail.toolKeys
      .split(',')
      .map((k) => k.trim())
      .filter(Boolean)
    placedTools.value = toolItems.value.filter((t) => t.toolKey && keys.includes(t.toolKey))
  }

  if (detail.mcpKeys) {
    const keys = detail.mcpKeys
      .split(',')
      .map((k) => k.trim())
      .filter(Boolean)
    placedMcps.value = mcpItems.value.filter((m) => m.mcpKey && keys.includes(m.mcpKey))
  }
}

async function initEditState() {
  if (!props.recordId) return
  loading.value = true
  try {
    const detail = await aiAgentApi.detail(props.recordId)
    applyAgentDetail(detail)
  } catch (e: any) {
    message.error(e?.message || '加载智能体详情失败')
  } finally {
    loading.value = false
  }
}

function onChatPage(page: number) {
  pagination.chat.current = page
  void loadChat()
}

function onEmbeddingPage(page: number) {
  pagination.embedding.current = page
  void loadEmbedding()
}

function onImagePage(page: number) {
  pagination.image.current = page
  void loadImage()
}

function onToolPage(page: number) {
  pagination.tool.current = page
  void loadTools()
}

function onMcpPage(page: number) {
  pagination.mcp.current = page
  void loadMcps()
}

function onPromptPage(page: number) {
  pagination.prompt.current = page
  void loadPrompts()
}

async function onLeftSearch(modelType: InstanceModelType, _keyword: string) {
  if (modelType === 'chat') pagination.chat.current = 1
  else if (modelType === 'embedding') pagination.embedding.current = 1
  else pagination.image.current = 1

  try {
    if (modelType === 'chat') await loadChat()
    else if (modelType === 'embedding') await loadEmbedding()
    else await loadImage()
  } catch {
    message.error('查询失败')
  }
}

async function onSearchTool() {
  pagination.tool.current = 1
  try {
    await loadTools()
  } catch {
    message.error('查询工具失败')
  }
}

async function onSearchMcp() {
  pagination.mcp.current = 1
  try {
    await loadMcps()
  } catch {
    message.error('查询 MCP 失败')
  }
}

async function onSearchPrompt() {
  pagination.prompt.current = 1
  try {
    await loadPrompts()
  } catch {
    message.error('查询 Prompt 失败')
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
    return
  }
  if (p.kind === 'prompt') {
    promptInstance.value = { ...p.data }
    message.success('已添加提示词')
  }
}

function removeKnowledgeKey(key: string) {
  knowledgeKeys.value = knowledgeKeys.value.filter((k) => k !== key)
}

function onClearInstance(which: 'chatInstance' | 'embeddingInstance' | 'imageInstance' | 'promptInstance') {
  if (which === 'chatInstance') chatInstance.value = null
  else if (which === 'embeddingInstance') embeddingInstance.value = null
  else if (which === 'imageInstance') imageInstance.value = null
  else if (which === 'promptInstance') promptInstance.value = null
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
  pagination.prompt.current = 1
}

function handleReset() {
  chatInstance.value = null
  embeddingInstance.value = null
  imageInstance.value = null
  promptInstance.value = null
  placedTools.value = []
  placedMcps.value = []
  knowledgeKeys.value = []
  agentForm.value = {
    agentName: '',
    agentKey: '',
    status: 'enabled',
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
    agentKey: f.agentKey,
    status: f.status,
    enableStream: f.enableStream,
    description: f.description,
    memoryMode: f.memoryMode,
    memoryWindowSize: String(f.memoryWindowSize ?? ''),
    knowledgeBaseKeys: knowledgeKeys.value.join(','),
    chatInstanceKey: chatInstance.value?.instanceKey,
    embeddingInstanceKey: embeddingInstance.value?.instanceKey,
    imageInstanceKey: imageInstance.value?.instanceKey,
    promptKey: promptInstance.value?.promptKey,
    toolKeys: placedTools.value.map((t) => t.toolKey).filter(Boolean).join(','),
    mcpKeys: placedMcps.value.map((m) => m.mcpKey).filter(Boolean).join(',')
  }
}

async function handleSubmit() {
  loading.value = true
  try {
    const payload = buildSubmitPayload()
    if (isEdit.value) {
      await aiAgentApi.update({
        ...(payload as AiAgent),
        id: props.recordId as string | number
      })
      message.success('智能体已更新')
    } else {
      await aiAgentApi.create(payload as AiAgent)
      message.success('发布成功')
      handleReset()
    }
    emit('success')
    emit('update:visible', false)
  } catch (e: any) {
    message.error(e.message || '发布失败')
  } finally {
    loading.value = false
  }
}

function handleCancel() {
  emit('update:visible', false)
}

watch(
  () => props.visible,
  async (val) => {
    if (!val) return
    await loadAll()
    if (props.recordId) {
      await initEditState()
    } else {
      handleReset()
    }
  }
)

onMounted(async () => {
  if (props.visible) {
    await loadAll()
    await initEditState()
  }
})
</script>

<style scoped>
.assembly-page {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
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
