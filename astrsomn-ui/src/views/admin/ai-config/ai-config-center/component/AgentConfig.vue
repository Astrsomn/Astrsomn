<template>
  <div class="config-container">
    <AgentConfigHeader :submitting="submitting" @back="emit('back')" @save="handleSave"/>

    <a-spin :spinning="loading">
      <div class="config-content">
        <AgentConfigPersonaSection
            :agent-key="localBizKey"
            :agent-name="localAgentName"
            :agent-avatar="localAgentAvatar"
            :current-prompt="currentPrompt"
            :improve-loading="improveLoading"
            @update:agent-name="localAgentName = $event"
            @update:agent-key="localBizKey = $event"
            @update:agent-avatar="localAgentAvatar = $event"
            @open-prompt-drawer="promptDrawerOpen = true"
            @open-prompt-form="promptFormOpen = true"
            @prompt-history="onPromptHistory"
            @improve-prompt="handleImprovePrompt"
            @update-prompt-content="onPromptContentUpdate"
        />

        <AgentConfigInstanceList
            :instance-list="instanceList"
            :available-models="availableModels"
            :route-strategy="routeStrategy"
            @update:instance-list="instanceList = $event"
            @update:route-strategy="routeStrategy = $event"
        />

        <AgentConfigIntegrationsSection
            :knowledge-keys="knowledgeKeys"
            :mcps="placedMcps"
            :tools="placedTools"
            @tool-add="onToolAdd"
            @tool-remove="onToolRemove"
            @mcp-add="onMcpAdd"
            @mcp-remove="onMcpRemove"
            @knowledge-add="onKnowledgeAdd"
            @knowledge-remove="onKnowledgeRemove"
        />

        <div class="bottom-spacing"></div>
      </div>
    </a-spin>

    <PromptSelectDrawer
        :open="promptDrawerOpen"
        @select="onPromptSelect"
        @update:open="promptDrawerOpen = $event"
    />

    <PromptFormModal
        :confirm-loading="false"
        :initial="null"
        :open="promptFormOpen"
        mode="create"
        @submit="onPromptFormSubmit"
        @update:open="promptFormOpen = $event"
    />

    <PromptHistoryModal
        :env-code="currentPrompt?.envCode"
        :open="historyModalOpen"
        :prompt-key="currentPrompt?.promptKey"
        @update:open="historyModalOpen = $event"
    />

    <AgentConfigPromptImproveModal
        :improved-content="improvedContent"
        :open="diffModalVisible"
        :original-content="originalContent"
        @apply="handleApplyImproved"
        @update:open="diffModalVisible = $event"
    />
  </div>
</template>

<script lang="ts" setup>
import {ref, watch} from 'vue'
import {message} from 'ant-design-vue'
import {type AiAgent, aiAgentApi} from '@/api/aiAgent'
import {type AiInstance, aiInstanceApi} from '@/api/aiInstance'
import {type AiModel, aiModelApi} from '@/api/aiModel'
import {type AiPrompt, aiPromptApi} from '@/api/aiPrompt'
import {type AiTool, aiToolApi} from '@/api/aiTool'
import {type AiMcp, aiMcpApi} from '@/api/aiMcp'
import PromptSelectDrawer from '@/views/admin/ai-config/ai-prompt/PromptSelectDrawer.vue'
import PromptFormModal from '@/views/admin/ai-config/ai-prompt/PromptFormModal.vue'
import PromptHistoryModal from '@/views/admin/ai-config/ai-prompt/PromptHistoryModal.vue'
import AgentConfigHeader from './agent-config/AgentConfigHeader.vue'
import AgentConfigPersonaSection from './agent-config/AgentConfigPersonaSection.vue'
import AgentConfigInstanceList from './agent-config/AgentConfigInstanceList.vue'
import AgentConfigIntegrationsSection from './agent-config/AgentConfigIntegrationsSection.vue'
import AgentConfigPromptImproveModal from './agent-config/AgentConfigPromptImproveModal.vue'

const props = defineProps<{
  agentName: string
  agentId?: string | number
}>()

const emit = defineEmits<{
  (e: 'back'): void
  (e: 'saved'): void
}>()

const loading = ref(false)
const submitting = ref(false)
const detailSnapshot = ref<AiAgent | null>(null)
const localAgentName = ref('')
const localBizKey = ref('')
const localAgentDescription = ref('')
const localAgentAvatar = ref('')

const currentPrompt = ref<AiPrompt | undefined>(undefined)
const loadedPromptContent = ref('')
const placedTools = ref<AiTool[]>([])
const placedMcps = ref<AiMcp[]>([])
const knowledgeKeys = ref<string[]>([])

const instanceList = ref<AiInstance[]>([])
const availableModels = ref<AiModel[]>([])
const routeStrategy = ref<string>('roundRobin')

const promptDrawerOpen = ref(false)
const promptFormOpen = ref(false)
const historyModalOpen = ref(false)
const improveLoading = ref(false)
const diffModalVisible = ref(false)
const originalContent = ref('')
const improvedContent = ref('')

function onToolAdd(tool: AiTool) {
  if (tool.toolKey && !placedTools.value.some((t) => t.toolKey === tool.toolKey)) {
    placedTools.value = [...placedTools.value, tool]
  }
}

function onToolRemove(toolKey: string) {
  placedTools.value = placedTools.value.filter((t) => t.toolKey !== toolKey)
}

function onMcpAdd(mcp: AiMcp) {
  if (mcp.mcpKey && !placedMcps.value.some((m) => m.mcpKey === mcp.mcpKey)) {
    placedMcps.value = [...placedMcps.value, mcp]
  }
}

function onMcpRemove(mcpKey: string) {
  placedMcps.value = placedMcps.value.filter((m) => m.mcpKey !== mcpKey)
}

function onPromptContentUpdate(content: string) {
  if (currentPrompt.value) {
    currentPrompt.value.promptContent = content
  }
}

function onPromptHistory() {
  if (!currentPrompt.value?.promptKey) {
    message.warning('请先选择一个提示词')
    return
  }
  historyModalOpen.value = true
}

async function handleImprovePrompt() {
  const content = currentPrompt.value?.promptContent
  if (!content?.trim()) {
    message.warning('请先输入提示词内容')
    return
  }
  originalContent.value = content
  improveLoading.value = true
  try {
    const improved = await aiPromptApi.beautify(content)
    improvedContent.value = improved
    diffModalVisible.value = true
  } catch {
    message.error('美化失败，请重试')
  } finally {
    improveLoading.value = false
  }
}

async function handleApplyImproved() {
  if (currentPrompt.value) {
    currentPrompt.value.promptContent = improvedContent.value
  }
  diffModalVisible.value = false
  // 美化后立即保存提示词（新建或更新）
  try {
    const saved = await aiPromptApi.submit({...currentPrompt.value, promptContent: improvedContent.value})
    currentPrompt.value = saved
    loadedPromptContent.value = saved.promptContent ?? improvedContent.value
  } catch {
    // 提示词保存失败不阻塞，handleSave 会重试
  }
  message.success('已应用美化后的提示词')
}

function onKnowledgeAdd(key: string) {
  const k = String(key || '').trim()
  if (!k || knowledgeKeys.value.includes(k)) return
  knowledgeKeys.value = [...knowledgeKeys.value, k]
}

function onKnowledgeRemove(key: string) {
  knowledgeKeys.value = knowledgeKeys.value.filter((x) => x !== key)
}

function parseKnowledgeKeys(raw?: string): string[] {
  const t = raw?.trim()
  if (!t) return []
  try {
    const arr = JSON.parse(t) as unknown
    if (Array.isArray(arr)) return arr.map((x) => String(x)).filter(Boolean)
  } catch {
    /* comma-separated */
  }
  return t.split(',').map((s) => s.trim()).filter(Boolean)
}

function resetEmptyForm() {
  detailSnapshot.value = null
  localAgentName.value = props.agentName || '新 Agent'
  localBizKey.value = ''
  localAgentDescription.value = ''
  localAgentAvatar.value = ''
  currentPrompt.value = {promptContent: ''}
  loadedPromptContent.value = ''
  placedTools.value = []
  placedMcps.value = []
  knowledgeKeys.value = []
  instanceList.value = []
  routeStrategy.value = 'roundRobin'
}

async function resolvePromptByKey(promptKey: string): Promise<AiPrompt | undefined> {
  const key = promptKey.trim()
  if (!key) return undefined
  const direct = await aiPromptApi.queryPage({
    pageNo: 1,
    pageSize: 1,
    param: {promptKey: key},
  })
  if (direct.list?.[0]?.promptKey === key) return direct.list[0]
  const wide = await aiPromptApi.queryPage({
    pageNo: 1,
    pageSize: 100,
    param: {promptKey: key},
  })
  return (wide.list || []).find((p) => p.promptKey === key)
}

async function resolveToolsByKeys(keys: string[]): Promise<AiTool[]> {
  const out: AiTool[] = []
  for (const key of keys) {
    const resp = await aiToolApi.queryPage({
      pageNo: 1,
      pageSize: 20,
      param: {toolKey: key},
    })
    const hit = (resp.list || []).find((t) => t.toolKey === key)
    if (hit) out.push(hit)
  }
  return out
}

async function resolveMcpsByKeys(keys: string[]): Promise<AiMcp[]> {
  const out: AiMcp[] = []
  for (const key of keys) {
    const resp = await aiMcpApi.queryPage({
      pageNo: 1,
      pageSize: 20,
      param: {mcpKey: key},
    })
    const hit = (resp.list || []).find((m) => m.mcpKey === key)
    if (hit) out.push(hit)
  }
  return out
}

async function loadInstanceListForAgent(bizKey: string): Promise<AiInstance[]> {
  if (!bizKey) return []
  const resp = await aiInstanceApi.queryPage({
    pageNo: 1,
    pageSize: 100,
    param: {bizKey},
  })
  return resp.list || []
}

async function backfillFromDetail(detail: AiAgent) {
  localAgentName.value = detail.agentName || props.agentName || '未命名的智能体'
  localBizKey.value = detail.bizKey ?? ''
  localAgentDescription.value = detail.description ?? ''
  localAgentAvatar.value = detail.agentAvatar ?? ''
  routeStrategy.value = detail.routeStrategy || 'roundRobin'

  if (detail.instanceList && detail.instanceList.length > 0) {
    instanceList.value = detail.instanceList
  } else if (detail.bizKey) {
    instanceList.value = await loadInstanceListForAgent(detail.bizKey)
  } else {
    instanceList.value = []
  }

  if (detail.promptKey) {
    if (detail.promptContent) {
      currentPrompt.value = {
        promptKey: detail.promptKey,
        promptTitle: detail.promptTitle,
        promptContent: detail.promptContent,
        version: detail.promptVersion,
      }
    } else {
      const resolved = await resolvePromptByKey(detail.promptKey)
      currentPrompt.value = resolved ?? {promptKey: detail.promptKey, promptContent: ''}
    }
  } else {
    currentPrompt.value = undefined
  }
  loadedPromptContent.value = currentPrompt.value?.promptContent ?? ''

  if (detail.toolKeys) {
    const keys = detail.toolKeys.split(',').map((k) => k.trim()).filter(Boolean)
    placedTools.value = keys.length ? await resolveToolsByKeys(keys) : []
  } else {
    placedTools.value = []
  }

  if (detail.mcpKeys) {
    const keys = detail.mcpKeys.split(',').map((k) => k.trim()).filter(Boolean)
    placedMcps.value = keys.length ? await resolveMcpsByKeys(keys) : []
  } else {
    placedMcps.value = []
  }

  knowledgeKeys.value = detail.knowledgeBaseKeys ? parseKnowledgeKeys(detail.knowledgeBaseKeys) : []
}

async function loadAgent() {
  if (!props.agentId) {
    resetEmptyForm()
    await loadAvailableModels()
    return
  }
  loading.value = true
  try {
    const detail = await aiAgentApi.detail(props.agentId)
    console.log('[AgentConfig] detail response:', JSON.parse(JSON.stringify(detail)))
    detailSnapshot.value = {...detail}
    await backfillFromDetail(detail)
    await loadAvailableModels()
  } catch (e: any) {
    message.error(e?.message || '加载智能体详情失败')
    resetEmptyForm()
    await loadAvailableModels()
  } finally {
    loading.value = false
  }
}

async function loadAvailableModels() {
  try {
    const resp = await aiModelApi.queryPage({pageNo: 1, pageSize: 100, param: {status: 'enabled'}})
    availableModels.value = resp.list || []
  } catch {
    availableModels.value = []
  }
}

function buildSubmitPayload(): AiAgent {
  const snap = detailSnapshot.value
  const base: AiAgent = snap
      ? {...snap}
      : {
        status: 'enabled',
        enableStream: true,
        description: '',
        bizKey: '',
        memoryMode: 'SLIDING_WINDOW',
        memoryWindowSize: '10',
        knowledgeBaseKeys: '',
      }
  return {
    ...base,
    agentName: localAgentName.value.trim(),
    bizKey: localBizKey.value.trim() || base.bizKey,
    description: localAgentDescription.value.trim(),
    agentAvatar: localAgentAvatar.value || undefined,
    promptKey: currentPrompt.value?.promptKey,
    knowledgeBaseKeys: knowledgeKeys.value.length ? knowledgeKeys.value.join(',') : '',
    toolKeys: placedTools.value.map((t) => t.toolKey).filter(Boolean).join(','),
    mcpKeys: placedMcps.value.map((m) => m.mcpKey).filter(Boolean).join(','),
    instanceList: instanceList.value,
    routeStrategy: routeStrategy.value,
  }
}

async function handleSave() {
  if (!localAgentName.value.trim()) {
    message.warning('请输入智能体名称')
    return
  }
  if (instanceList.value.length === 0) {
    message.warning('请至少添加一个推理实例')
    return
  }
  submitting.value = true
  try {
    const p = currentPrompt.value
    const content = p?.promptContent?.trim() || ''
    if (content && content !== String(loadedPromptContent.value ?? '').trim()) {
      // 内容有变化（直接输入或选了又改）→ 美化 + 保存（新建或更新）
      const saved = await aiPromptApi.submit({...p, promptContent: content})
      currentPrompt.value = saved
      loadedPromptContent.value = saved.promptContent ?? content
    }

    const payload = buildSubmitPayload()
    if (props.agentId != null && props.agentId !== '') {
      await aiAgentApi.update({...payload, id: props.agentId})
      message.success('智能体已保存')
    } else {
      await aiAgentApi.createFullAgent(payload)
      message.success('智能体已创建')
    }
    emit('saved')
    emit('back')
  } catch (e: any) {
    message.error(e?.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

function onPromptSelect(prompt: AiPrompt) {
  currentPrompt.value = prompt
  promptDrawerOpen.value = false
  loadedPromptContent.value = prompt.promptContent ?? ''
}

function onPromptFormSubmit(form: AiPrompt) {
  currentPrompt.value = form
  promptFormOpen.value = false
  loadedPromptContent.value = form.promptContent ?? ''
}

watch(
    () => props.agentId,
    () => {
      void loadAgent()
    },
    {immediate: true}
)

watch(
    () => props.agentName,
    (name) => {
      if (props.agentId == null || props.agentId === '') {
        localAgentName.value = name || '新 Agent'
      }
    },
    {immediate: true}
)
</script>

<style scoped>
.config-container {
  padding: 0;
  overflow-y: auto;
  height: 100%;
}

.config-content {
  display: flex;
  flex-direction: column;
  gap: 32px;
  padding: 32px 40px;
}

.bottom-spacing {
  height: 40px;
}
</style>
