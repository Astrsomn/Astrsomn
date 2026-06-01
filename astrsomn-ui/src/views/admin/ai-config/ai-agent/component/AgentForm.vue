<template>
  <AstModal
      :open="visible"
      @cancel="handleCancel"
      @update:open="emit('update:visible', $event)"
  >
    <template #header-logo>
      <ThunderboltFilled/>
    </template>
    <template #header-title>
      {{ isEdit ? t.form.editTitle : t.form.createTitle }}
    </template>
    <template #header-subtitle>
      {{ t.form.subtitle }}
    </template>
    <template #header-actions>
      <AstegmentedButton :buttons="headerFormSegmentButtons"/>
    </template>

    <div class="assembly-page">
      <div class="assembly-layout">
        <AssemblyLeftPalette
            ref="leftPaletteRef"
            :chat-items="chatItems"
            :chat-page="pagination.chat"
            :image-items="imageItems"
            :image-page="pagination.image"
            :page-size="pageSize.value"
            @search="onLeftSearch"
            @chat-page="onChatPage"
            @image-page="onImagePage"
            @drag-start="onDragStart"
            @drag-end="onDragEnd"
        />

        <AssemblyCanvas
            v-model:agent-form="agentForm"
            :active-drop-key="activeDropKey"
            :chat-instance="chatInstance"
            :dragging-payload="dragPayload"
            :image-instance="imageInstance"
            :knowledge-keys="knowledgeKeys"
            :mcps="placedMcps"
            :prompt-instance="promptInstance"
            :tools="placedTools"
            @clear="onClearInstance"
            @drop="onCanvasDrop"
            @hover="activeDropKey = $event"
            @reset="handleReset"
            @submit="handleSubmit"
            @remove-tool="removeTool"
            @remove-mcp="removeMcp"
            @remove-knowledge-key="removeKnowledgeKey"
        />

        <AssemblyRightPalette
            ref="rightPaletteRef"
            :mcp-page="pagination.mcp"
            :mcps="mcpItems"
            :page-size="pageSize.value"
            :prompt-page="pagination.prompt"
            :prompts="promptItems"
            :tool-page="pagination.tool"
            :tools="toolItems"
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
  </AstModal>
</template>

<script lang="ts" setup>
import {computed, onMounted, reactive, ref, watch} from 'vue'
import {message} from 'ant-design-vue'
import {CloseOutlined, CloudUploadOutlined, SaveOutlined, ThunderboltFilled,} from '@ant-design/icons-vue'
import {usePageTranslation} from '@/locales/pages.ts'
import AstModal from '@/components/home/AstModal.vue'
import AstegmentedButton, {type SegmentedButton} from '@/components/home/AstegmentedButton.vue'
import type {AiInstance} from '@/api/aiInstance.ts'
import type {AiTool} from '@/api/aiTool.ts'
import type {AiMcp} from '@/api/aiMcp.ts'
import type {AiPrompt} from '@/api/aiPrompt.ts'
import AssemblyLeftPalette from '../assembly/AssemblyLeftPalette.vue'
import AssemblyRightPalette from '../assembly/AssemblyRightPalette.vue'
import AssemblyCanvas from '../assembly/AssemblyCanvas.vue'
import {fetchInstancesPaged, fetchMcpsPaged, fetchPromptsPaged, fetchToolsPaged} from '../assembly/assemblyFetch.ts'
import type {AiAgent} from '@/api/aiAgent.ts'
import {aiAgentApi} from '@/api/aiAgent.ts'
import type {
  AssemblyAgentForm,
  AssemblyDragPayload,
  AssemblySlotKey,
  InstanceModelType
} from '../assembly/assemblyTypes.ts'

interface Props {
  visible: boolean
  recordId?: string | number
}

const props = defineProps<Props>()
const emit = defineEmits(['update:visible', 'success'])

const t = usePageTranslation('ai-agent')

const loading = ref(false)

const isEdit = computed(() => !!props.recordId)

const headerFormSegmentButtons = computed<SegmentedButton[]>(() => [
  {
    label: t.value.form.cancel,
    icon: CloseOutlined,
    disabled: loading.value,
    onClick: handleCancel,
  },
  {
    label: isEdit.value ? t.value.form.save : t.value.form.publish,
    type: 'primary',
    icon: isEdit.value ? SaveOutlined : CloudUploadOutlined,
    loading: loading.value,
    onClick: handleSubmit,
  },
])


const calculatePageSize = () => {

  const containerHeight = window.innerHeight - 100


  const headerHeight = 100
  const availableHeight = containerHeight - headerHeight


  const chipHeight = 60


  const maxChips = Math.floor(availableHeight / chipHeight) - 1


  return Math.max(3, maxChips)
}


const pageSize = ref(calculatePageSize())


window.addEventListener('resize', () => {
  const oldPageSize = pageSize.value
  pageSize.value = calculatePageSize()


  if (oldPageSize !== pageSize.value) {
    resetPagination()
    void loadAll()
  }
})

const leftPaletteRef = ref<InstanceType<typeof AssemblyLeftPalette> | null>(null)
const rightPaletteRef = ref<InstanceType<typeof AssemblyRightPalette> | null>(null)

const chatItems = ref<AiInstance[]>([])
const imageItems = ref<AiInstance[]>([])
const toolItems = ref<AiTool[]>([])
const mcpItems = ref<AiMcp[]>([])
const promptItems = ref<AiPrompt[]>([])

const pagination = reactive({
  chat: {current: 1, total: 0},
  image: {current: 1, total: 0},
  tool: {current: 1, total: 0},
  mcp: {current: 1, total: 0},
  prompt: {current: 1, total: 0}
})

const chatInstance = ref<AiInstance | null>(null)
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
  const {list, total} = await fetchInstancesPaged('chat', kw, pagination.chat.current, pageSize.value)
  chatItems.value = list
  pagination.chat.total = total
}

async function loadImage() {
  const kw = leftPaletteRef.value?.getKeywords?.().image ?? ''
  const {list, total} = await fetchInstancesPaged('image', kw, pagination.image.current, pageSize.value)
  imageItems.value = list
  pagination.image.total = total
}

async function loadTools() {
  const kw = rightPaletteRef.value?.getKeywords?.().tool ?? ''
  const {list, total} = await fetchToolsPaged(kw, pagination.tool.current, pageSize.value)
  toolItems.value = list
  pagination.tool.total = total
}

async function loadMcps() {
  const kw = rightPaletteRef.value?.getKeywords?.().mcp ?? ''
  const {list, total} = await fetchMcpsPaged(kw, pagination.mcp.current, pageSize.value)
  mcpItems.value = list
  pagination.mcp.total = total
}

async function loadPrompts() {
  const kw = rightPaletteRef.value?.getKeywords?.().prompt ?? ''
  const {list, total} = await fetchPromptsPaged(kw, pagination.prompt.current, pageSize.value)
  promptItems.value = list
  pagination.prompt.total = total
}

async function loadAll() {
  try {
    await Promise.all([loadChat(), loadImage(), loadTools(), loadMcps(), loadPrompts()])
  } catch {
    message.error(t.value.form.loadResourceFailed)
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
    message.error(e?.message || t.value.form.loadDetailFailed)
  } finally {
    loading.value = false
  }
}

function onChatPage(page: number) {
  pagination.chat.current = page
  void loadChat()
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
  else pagination.image.current = 1

  try {
    if (modelType === 'chat') await loadChat()
    else await loadImage()
  } catch {
    message.error(t.value.form.queryFailed)
  }
}

async function onSearchTool() {
  pagination.tool.current = 1
  try {
    await loadTools()
  } catch {
    message.error(t.value.form.queryToolFailed)
  }
}

async function onSearchMcp() {
  pagination.mcp.current = 1
  try {
    await loadMcps()
  } catch {
    message.error(t.value.form.queryMcpFailed)
  }
}

async function onSearchPrompt() {
  pagination.prompt.current = 1
  try {
    await loadPrompts()
  } catch {
    message.error(t.value.form.queryPromptFailed)
  }
}

function onCanvasDrop(p: AssemblyDragPayload) {
  if (p.kind === 'instance') {
    const row = p.data
    if (p.instanceModelType === 'chat') {
      chatInstance.value = {...row}
      message.success(t.value.form.chatInstancePlaced)
    } else {
      imageInstance.value = {...row}
      message.success(t.value.form.imageInstancePlaced)
    }
    return
  }
  if (p.kind === 'tool') {
    const key = p.data.toolKey
    if (!key) return
    if (placedTools.value.some((x) => x.toolKey === key)) {
      message.info(t.value.form.toolAlreadyExists)
      return
    }
    placedTools.value = [...placedTools.value, {...p.data}]
    message.success(t.value.form.toolAdded)
    return
  }
  if (p.kind === 'mcp') {
    const mkey = p.data.mcpKey
    if (!mkey) return
    if (placedMcps.value.some((x) => x.mcpKey === mkey)) {
      message.info(t.value.form.mcpAlreadyExists)
      return
    }
    placedMcps.value = [...placedMcps.value, {...p.data}]
    message.success(t.value.form.mcpAdded)
    return
  }
  if (p.kind === 'knowledgeBase') {
    const kb = p.data.kbKey?.trim()
    if (!kb) return
    if (knowledgeKeys.value.includes(kb)) {
      message.info(t.value.form.knowledgeKeyAlreadyExists)
      return
    }
    knowledgeKeys.value = [...knowledgeKeys.value, kb]
    message.success(t.value.form.knowledgeKeyAdded)
    return
  }
  if (p.kind === 'prompt') {
    promptInstance.value = {...p.data}
    message.success(t.value.form.promptAdded)
  }
}

function removeKnowledgeKey(key: string) {
  knowledgeKeys.value = knowledgeKeys.value.filter((k) => k !== key)
}

function onClearInstance(which: 'chatInstance' | 'imageInstance' | 'promptInstance') {
  if (which === 'chatInstance') chatInstance.value = null
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
  pagination.image.current = 1
  pagination.tool.current = 1
  pagination.mcp.current = 1
  pagination.prompt.current = 1
}

function handleReset() {
  chatInstance.value = null
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
  message.info(t.value.form.resetAssembly)
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
      message.success(t.value.form.agentUpdated)
    } else {
      await aiAgentApi.create(payload as AiAgent)
      message.success(t.value.form.publishSuccess)
      handleReset()
    }
    emit('success')
    emit('update:visible', false)
  } catch (e: any) {
    message.error(e.message || t.value.form.publishFailed)
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
  padding: 5px;
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
