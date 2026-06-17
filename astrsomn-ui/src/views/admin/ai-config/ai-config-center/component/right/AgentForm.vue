<template>
  <div class="config-container">
    <AgentConfigHeader :submitting="submitting" @back="emit('back')" @save="handleSave" />

    <a-spin :spinning="loading" class="config-spin">
      <div class="config-content">
        <!-- 左侧：身份与提示词配置 -->
        <div class="config-left">
          <AgentBaseSection
              :agent-key="localAgentKey"
              :agent-name="localAgentName"
              :agent-avatar="localAgentAvatar"
              :current-prompt="currentPrompt"
              :improve-loading="improveLoading"
              @update:agent-name="localAgentName = $event"
              @update:agent-key="localAgentKey = $event"
              @update:agent-avatar="localAgentAvatar = $event"
              @open-prompt-drawer="promptDrawerOpen = true"
              @open-prompt-form="promptFormOpen = true"
              @prompt-history="onPromptHistory"
              @improve-prompt="handleImprovePrompt"
              @update-prompt-content="onPromptContentUpdate"
          />
        </div>

        <!-- 右侧：实例与集成配置 -->
        <div class="config-right">
          <div class="config-right-top">
            <InstanceListSection
                :instance-list="instanceList"
                :available-models="availableModels"
                :route-strategy="routeStrategy"
                @update:instance-list="instanceList = $event"
                @update:route-strategy="routeStrategy = $event"
            />
          </div>
          <div class="config-right-bottom">
            <ToolSection
                :knowledge-keys="knowledgeKeys"
                :mcps="placedMcps"
                :tools="placedTools"
                :template-keys="templateKeys"
                @tool-add="onToolAdd"
                @tool-remove="onToolRemove"
                @mcp-add="onMcpAdd"
                @mcp-remove="onMcpRemove"
                @knowledge-add="onKnowledgeAdd"
                @knowledge-remove="onKnowledgeRemove"
                @template-add="onTemplateAdd"
                @template-remove="onTemplateRemove"
            />
          </div>
        </div>
      </div>
    </a-spin>

    <PromptSelectorDrawer
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

    <PromptImproveModal
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
import {type AiAgent, aiAgentApi} from '@/api/aiAgent.ts'
import {type AiInstance, aiInstanceApi} from '@/api/aiInstance.ts'
import {type AiModel, aiModelApi} from '@/api/aiModel.ts'
import {type AiPrompt, aiPromptApi} from '@/api/aiPrompt.ts'
import {type AiTool, aiToolApi} from '@/api/aiTool.ts'
import {type AiMcp, aiMcpApi} from '@/api/aiMcp.ts'
import {usePageTranslation} from '@/locales/pages.ts'
import PromptSelectorDrawer from '@/views/admin/ai-config/ai-prompt/selector/PromptSelectorDrawer.vue'
import PromptFormModal from '@/views/admin/ai-config/ai-prompt/component/PromptFormModal.vue'
import PromptHistoryModal from '@/views/admin/ai-config/ai-prompt/component/PromptHistoryModal.vue'
import AgentConfigHeader
  from '@/views/admin/ai-config/ai-config-center/component/right/agent-config/AgentConfigHeader.vue'
import AgentBaseSection
  from '@/views/admin/ai-config/ai-config-center/component/right/agent-config/AgentBaseSection.vue'
import InstanceListSection
  from '@/views/admin/ai-config/ai-config-center/component/right/agent-config/InstanceListSection.vue'
import ToolSection from '@/views/admin/ai-config/ai-config-center/component/right/agent-config/ToolSection.vue'
import PromptImproveModal
  from '@/views/admin/ai-config/ai-config-center/component/right/agent-config/form/PromptImproveModal.vue'

const props = defineProps<{
  agentName: string
  agentId?: string | number
}>()

const emit = defineEmits<{
  (e: 'back'): void
  (e: 'saved'): void
}>()

const t = usePageTranslation('ai-config-center')

type KeyEntry = { key: string; _orphaned?: boolean }

const loading = ref(false)
const submitting = ref(false)
const detailSnapshot = ref<AiAgent | null>(null)
const localAgentName = ref('')
const localAgentKey = ref('')
const localAgentDescription = ref('')
const localAgentAvatar = ref('')

const currentPrompt = ref<AiPrompt | undefined>(undefined)
const loadedPromptContent = ref('')
const placedTools = ref<AiTool[]>([])
const placedMcps = ref<AiMcp[]>([])
const knowledgeKeys = ref<KeyEntry[]>([])
const templateKeys = ref<KeyEntry[]>([])

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
    message.warning(t.value.agent.selectPromptFirst)
    return
  }
  historyModalOpen.value = true
}

async function handleImprovePrompt() {
  const content = currentPrompt.value?.promptContent
  if (!content?.trim()) {
    message.warning(t.value.agent.enterPromptContentFirst)
    return
  }
  originalContent.value = content
  improveLoading.value = true
  try {
    const improved = await aiPromptApi.beautify(content)
    improvedContent.value = improved
    diffModalVisible.value = true
  } catch {
    message.error(t.value.agent.improveFailed)
  } finally {
    improveLoading.value = false
  }
}

async function handleApplyImproved() {
  if (currentPrompt.value) {
    currentPrompt.value.promptContent = improvedContent.value
  }
  diffModalVisible.value = false
  try {
    const saved = await aiPromptApi.submit({ ...currentPrompt.value, promptContent: improvedContent.value })
    currentPrompt.value = saved
    loadedPromptContent.value = saved.promptContent ?? improvedContent.value
  } catch {

  }
  message.success(t.value.agent.improveApplied)
}

function onKnowledgeAdd(key: string) {
  const k = String(key || '').trim()
  if (!k || knowledgeKeys.value.some((x) => x.key === k)) return
  knowledgeKeys.value = [...knowledgeKeys.value, { key: k }]
}

function onKnowledgeRemove(key: string) {
  knowledgeKeys.value = knowledgeKeys.value.filter((x) => x.key !== key)
}

function onTemplateAdd(key: string) {
  const k = String(key || '').trim()
  if (!k || templateKeys.value.some((x) => x.key === k)) return
  templateKeys.value = [...templateKeys.value, { key: k }]
}

function onTemplateRemove(key: string) {
  templateKeys.value = templateKeys.value.filter((x) => x.key !== key)
}

function parseKnowledgeKeys(raw?: string): KeyEntry[] {
  const rawStr = raw?.trim()
  if (!rawStr) return []
  try {
    const arr = JSON.parse(rawStr) as unknown
    if (Array.isArray(arr)) return arr.map((x) => ({ key: String(x) })).filter((e) => e.key)
  } catch {
    // not JSON, fall through
  }
  return rawStr.split(',').map((s) => ({ key: s.trim() })).filter((e) => e.key)
}

function resetEmptyForm() {
  detailSnapshot.value = null
  localAgentName.value = props.agentName || t.value.main.newAgent
  localAgentKey.value = ''
  localAgentDescription.value = ''
  localAgentAvatar.value = ''
  currentPrompt.value = { promptContent: '' }
  loadedPromptContent.value = ''
  placedTools.value = []
  placedMcps.value = []
  knowledgeKeys.value = []
  templateKeys.value = []
  instanceList.value = []
  routeStrategy.value = 'roundRobin'
}

async function resolvePromptByKey(promptKey: string): Promise<AiPrompt | undefined> {
  const key = promptKey.trim()
  if (!key) return undefined
  const direct = await aiPromptApi.queryPage({
    pageNo: 1,
    pageSize: 1,
    param: { promptKey: key },
  })
  if (direct.list?.[0]?.promptKey === key) return direct.list[0]
  const wide = await aiPromptApi.queryPage({
    pageNo: 1,
    pageSize: 100,
    param: { promptKey: key },
  })
  return (wide.list || []).find((p) => p.promptKey === key)
}

/**
 * 仅查询后端确认存在的 tool keys，获取完整对象用于展示 toolName。
 * 孤儿检测由后端 detail 接口的 orphanedToolKeys 字段完成，前端不再自行校验。
 */
async function resolveExistingToolsByKeys(keys: string[]): Promise<Map<string, AiTool>> {
  const map = new Map<string, AiTool>()
  if (!keys.length) return map
  for (const key of keys) {
    try {
      const resp = await aiToolApi.queryPage({
        pageNo: 1,
        pageSize: 1,
        param: { toolKey: key },
      })
      const hit = (resp.list || []).find((t) => t.toolKey === key)
      if (hit) map.set(key, hit)
    } catch {
      // 解析失败时回退到仅展示 key
    }
  }
  return map
}

async function resolveExistingMcpsByKeys(keys: string[]): Promise<Map<string, AiMcp>> {
  const map = new Map<string, AiMcp>()
  if (!keys.length) return map
  for (const key of keys) {
    try {
      const resp = await aiMcpApi.queryPage({
        pageNo: 1,
        pageSize: 1,
        param: { mcpKey: key },
      })
      const hit = (resp.list || []).find((m) => m.mcpKey === key)
      if (hit) map.set(key, hit)
    } catch {
      // 解析失败时回退到仅展示 key
    }
  }
  return map
}

async function loadInstanceListForAgent(agentKey: string): Promise<AiInstance[]> {
  if (!agentKey) return []
  const resp = await aiInstanceApi.queryPage({
    pageNo: 1,
    pageSize: 100,
    param: { agentKey },
  })
  return resp.list || []
}

async function backfillFromDetail(detail: AiAgent) {
  localAgentName.value = detail.agentName || props.agentName || t.value.agent.unnamedAgent
  localAgentKey.value = detail.agentKey ?? ''
  localAgentDescription.value = detail.description ?? ''
  localAgentAvatar.value = detail.agentAvatar ?? ''
  routeStrategy.value = detail.routeStrategy || 'roundRobin'

  if (detail.instanceList && detail.instanceList.length > 0) {
    instanceList.value = detail.instanceList
  } else if (detail.agentKey) {
    instanceList.value = await loadInstanceListForAgent(detail.agentKey)
  } else {
    instanceList.value = []
  }

  if (detail.promptKey) {
    const prompt = (detail as any).prompt
    if (prompt?.promptContent) {
      currentPrompt.value = {
        promptKey: prompt.promptKey || detail.promptKey,
        promptTitle: prompt.promptTitle || '',
        promptContent: prompt.promptContent,
        version: prompt.version ?? 1,
      }
    } else {
      const resolved = await resolvePromptByKey(detail.promptKey)
      currentPrompt.value = resolved ?? { promptKey: detail.promptKey, promptContent: '' }
    }
  } else {
    currentPrompt.value = undefined
  }
  loadedPromptContent.value = currentPrompt.value?.promptContent ?? ''

  // ── 工具：后端已校验孤儿 key，前端仅解析存在的 key 获取展示名称 ──
  if (detail.toolKeys) {
    const keys = detail.toolKeys.split(',').map((k) => k.trim()).filter(Boolean)
    const orphanedSet = new Set(detail.orphanedToolKeys || [])
    const validKeys = keys.filter((k) => !orphanedSet.has(k))
    const resolvedMap = await resolveExistingToolsByKeys(validKeys)
    placedTools.value = keys.map((key) =>
      resolvedMap.get(key) || ({ toolKey: key, toolName: key, _orphaned: orphanedSet.has(key) } as any),
    )
  } else {
    placedTools.value = []
  }

  // ── MCP：同理 ──
  if (detail.mcpKeys) {
    const keys = detail.mcpKeys.split(',').map((k) => k.trim()).filter(Boolean)
    const orphanedSet = new Set(detail.orphanedMcpKeys || [])
    const validKeys = keys.filter((k) => !orphanedSet.has(k))
    const resolvedMap = await resolveExistingMcpsByKeys(validKeys)
    placedMcps.value = keys.map((key) =>
      resolvedMap.get(key) || ({ mcpKey: key, serverName: key, _orphaned: orphanedSet.has(key) } as any),
    )
  } else {
    placedMcps.value = []
  }

  // ── 知识库：纯 key，孤儿状态由后端告知 ──
  if (detail.knowledgeBaseKeys) {
    const rawKeys = parseKnowledgeKeys(detail.knowledgeBaseKeys).map((e) => e.key)
    const orphanedSet = new Set(detail.orphanedKnowledgeBaseKeys || [])
    knowledgeKeys.value = rawKeys.map((key) => ({ key, _orphaned: orphanedSet.has(key) }))
  } else {
    knowledgeKeys.value = []
  }

  // ── FTL 模板：纯 key，孤儿状态由后端告知 ──
  if (detail.templateKeys) {
    const rawKeys = detail.templateKeys.split(',').map((k) => k.trim()).filter(Boolean)
    const orphanedSet = new Set(detail.orphanedTemplateKeys || [])
    templateKeys.value = rawKeys.map((key) => ({ key, _orphaned: orphanedSet.has(key) }))
  } else {
    templateKeys.value = []
  }
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
    const { prompt: _prompt, ...clean } = detail as any
    detailSnapshot.value = clean as AiAgent
    await backfillFromDetail(detail)
    await loadAvailableModels()
  } catch (e: any) {
    message.error(e?.message || t.value.agent.loadDetailFailed)
    resetEmptyForm()
    await loadAvailableModels()
  } finally {
    loading.value = false
  }
}

async function loadAvailableModels() {
  try {
    const resp = await aiModelApi.queryPage({ pageNo: 1, pageSize: 100, param: { status: 'enabled' } })
    availableModels.value = resp.list || []
  } catch {
    availableModels.value = []
  }
}

function buildSubmitPayload(): AiAgent {
  const snap = detailSnapshot.value
  const base: AiAgent = snap
      ? { ...snap }
      : {
        status: 'enabled',
        enableStream: true,
        description: '',
        agentKey: '',
        memoryMode: 'SLIDING_WINDOW',
        memoryWindowSize: '10',
        knowledgeBaseKeys: '',
      }

  const promptContent = currentPrompt.value?.promptContent?.trim() || ''
  const promptEntity = promptContent
      ? {
          promptKey: currentPrompt.value?.promptKey,
          promptTitle: currentPrompt.value?.promptTitle || localAgentName.value.trim(),
          promptContent,
          status: currentPrompt.value?.status,
          version: currentPrompt.value?.version,
        }
      : undefined

  return {
    ...base,
    agentName: localAgentName.value.trim(),
    agentKey: localAgentKey.value.trim() || base.agentKey,
    description: localAgentDescription.value.trim(),
    agentAvatar: localAgentAvatar.value || undefined,
    promptKey: currentPrompt.value?.promptKey,
    knowledgeBaseKeys: knowledgeKeys.value.filter((e) => !e._orphaned).map((e) => e.key).filter(Boolean).join(','),
    toolKeys: placedTools.value.filter((t) => !(t as any)._orphaned).map((t) => t.toolKey).filter(Boolean).join(','),
    mcpKeys: placedMcps.value.filter((m) => !(m as any)._orphaned).map((m) => m.mcpKey).filter(Boolean).join(','),
    templateKeys: templateKeys.value.filter((e) => !e._orphaned).map((e) => e.key).filter(Boolean).join(','),
    instanceList: instanceList.value,
    routeStrategy: routeStrategy.value,
    promptEntity,
  }
}

async function handleSave() {
  if (!localAgentName.value.trim()) {
    message.warning(t.value.agent.nameRequired)
    return
  }
  if (instanceList.value.length === 0) {
    message.warning(t.value.agent.instanceRequired)
    return
  }
  submitting.value = true
  try {
    const payload = buildSubmitPayload()
    await aiAgentApi.saveOrUpdate(payload)
    message.success(t.value.agent.saved)
    emit('saved')
    emit('back')
  } catch (e: any) {
    message.error(e?.message || t.value.agent.saveFailed)
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
    { immediate: true }
)

watch(
    () => props.agentName,
    (name) => {
      if (props.agentId == null || props.agentId === '') {
        localAgentName.value = name || t.value.main.newAgent
      }
    },
    { immediate: true }
)
</script>

<style scoped>
.config-container {
  padding: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: transparent;
  gap: 0;
}

.config-spin {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.config-spin :deep(.ant-spin-container) {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

/* ── 整体卡片（设计稿 view-generic 风格） ── */
.config-content {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: 1fr 1.5fr;
  gap: 20px;
  padding: 20px 24px;
  overflow: hidden;
}

/* 左侧 / 右侧两块内容各自包成大卡片 */
.config-left,
.config-right {
  min-height: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: 16px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.config-left:hover,
.config-right:hover {
  border-color: var(--primary);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.06);
}

.config-right {
  display: grid;
  grid-template-rows: 1fr auto;
  gap: 0;
}

.config-right-top,
.config-right-bottom {
  min-height: 0;
  overflow: hidden;
  border-top: 1px solid var(--border-default);
}

.config-right-top {
  border-top: none;
}

@media (max-width: 1200px) {
  .config-content {
    grid-template-columns: 1fr;
    overflow-y: auto;
  }

  .config-right {
    grid-template-rows: auto auto;
  }
}
</style>
