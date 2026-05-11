<template>
  <div class="config-container">
    <AgentConfigHeader :submitting="submitting" @back="emit('back')" @save="handleSave" />

    <a-spin :spinning="loading">
      <div class="config-content">
        <AgentConfigPersonaSection
          :agent-name="localAgentName"
          :agent-description="localAgentDescription"
          :current-prompt="currentPrompt"
          :improve-loading="improveLoading"
          @update:agent-name="localAgentName = $event"
          @update:agent-description="localAgentDescription = $event"
          @open-prompt-drawer="promptDrawerOpen = true"
          @open-prompt-form="promptFormOpen = true"
          @prompt-history="onPromptHistory"
          @improve-prompt="handleImprovePrompt"
          @update-prompt-content="onPromptContentUpdate"
        />

        <AgentConfigIntegrationsSection
          :tools="placedTools"
          :mcps="placedMcps"
          :knowledge-keys="knowledgeKeys"
          @tool-add="onToolAdd"
          @tool-remove="onToolRemove"
          @mcp-add="onMcpAdd"
          @mcp-remove="onMcpRemove"
          @knowledge-add="onKnowledgeAdd"
          @knowledge-remove="onKnowledgeRemove"
        />

        <AgentConfigModelTracksSection
          :chat-model="chatModel"
          :image-model="imageModel"
          :voice-model="voiceModel"
          :current-chat-instance="currentChatInstance"
          :current-image-instance="currentImageInstance"
          :current-voice-instance="currentVoiceInstance"
          :chat-account="chatAccount"
          :image-account="imageAccount"
          :voice-account="voiceAccount"
          :chat-param-form="chatParamForm"
          :image-param-form="imageParamForm"
          :voice-param-form="voiceParamForm"
          :chat-param-section-title="chatParamSectionTitle"
          :chat-capability-hint="chatCapabilityHint"
          :chat-has-param-schema="chatHasParamSchema"
          :chat-unsupported-param-codes="chatUnsupportedParamCodes"
          :chat-model-kind="chatModelKind"
          :chat-show-chat-temperature="chatShowChatTemperature"
          :chat-show-chat-max-tokens="chatShowChatMaxTokens"
          :chat-show-chat-top-p="chatShowChatTopP"
          :chat-show-chat-top-k="chatShowChatTopK"
          :chat-show-chat-seed="chatShowChatSeed"
          :chat-show-chat-stop-sequences="chatShowChatStopSequences"
          :chat-show-chat-penalties="chatShowChatPenalties"
          :chat-show-chat-frequency-penalty="chatShowChatFrequencyPenalty"
          :chat-show-chat-presence-penalty="chatShowChatPresencePenalty"
          :chat-show-embedding-dimensions="chatShowEmbeddingDimensions"
          :chat-show-image-size="chatShowImageSize"
          :chat-show-image-style="chatShowImageStyle"
          :chat-embedding-has-any-control="chatEmbeddingHasAnyControl"
          :chat-image-has-any-control="chatImageHasAnyControl"
          :image-param-section-title="imageParamSectionTitle"
          :image-capability-hint="imageCapabilityHint"
          :image-has-param-schema="imageHasParamSchema"
          :image-unsupported-param-codes="imageUnsupportedParamCodes"
          :image-model-kind="imageModelKind"
          :image-show-chat-temperature="imageShowChatTemperature"
          :image-show-chat-max-tokens="imageShowChatMaxTokens"
          :image-show-chat-top-p="imageShowChatTopP"
          :image-show-chat-top-k="imageShowChatTopK"
          :image-show-chat-seed="imageShowChatSeed"
          :image-show-chat-stop-sequences="imageShowChatStopSequences"
          :image-show-chat-penalties="imageShowChatPenalties"
          :image-show-chat-frequency-penalty="imageShowChatFrequencyPenalty"
          :image-show-chat-presence-penalty="imageShowChatPresencePenalty"
          :image-show-embedding-dimensions="imageShowEmbeddingDimensions"
          :image-show-image-size="imageShowImageSize"
          :image-show-image-style="imageShowImageStyle"
          :image-embedding-has-any-control="imageEmbeddingHasAnyControl"
          :image-image-has-any-control="imageImageHasAnyControl"
          :voice-param-section-title="voiceParamSectionTitle"
          :voice-capability-hint="voiceCapabilityHint"
          :voice-has-param-schema="voiceHasParamSchema"
          :voice-unsupported-param-codes="voiceUnsupportedParamCodes"
          :voice-model-kind="voiceModelKind"
          :voice-show-chat-temperature="voiceShowChatTemperature"
          :voice-show-chat-max-tokens="voiceShowChatMaxTokens"
          :voice-show-chat-top-p="voiceShowChatTopP"
          :voice-show-chat-top-k="voiceShowChatTopK"
          :voice-show-chat-seed="voiceShowChatSeed"
          :voice-show-chat-stop-sequences="voiceShowChatStopSequences"
          :voice-show-chat-penalties="voiceShowChatPenalties"
          :voice-show-chat-frequency-penalty="voiceShowChatFrequencyPenalty"
          :voice-show-chat-presence-penalty="voiceShowChatPresencePenalty"
          :voice-show-embedding-dimensions="voiceShowEmbeddingDimensions"
          :voice-show-image-size="voiceShowImageSize"
          :voice-show-image-style="voiceShowImageStyle"
          :voice-embedding-has-any-control="voiceEmbeddingHasAnyControl"
          :voice-image-has-any-control="voiceImageHasAnyControl"
          :get-temp-info="getTempInfo"
          @clear-chat="clearChatTrack"
          @clear-image="clearImageTrack"
          @clear-voice="clearVoiceTrack"
          @open-model="openModelSelector"
          @open-instance="openInstanceSelectorSafe"
        />

        <div class="bottom-spacing"></div>
      </div>
    </a-spin>

    <PromptSelectDrawer
      :open="promptDrawerOpen"
      @update:open="promptDrawerOpen = $event"
      @select="onPromptSelect"
    />

    <PromptFormModal
      :open="promptFormOpen"
      @update:open="promptFormOpen = $event"
      mode="create"
      :confirm-loading="false"
      :initial="null"
      @submit="onPromptFormSubmit"
    />

    <PromptHistoryModal
      :open="historyModalOpen"
      @update:open="historyModalOpen = $event"
      :prompt-key="currentPrompt?.promptKey"
      :env-code="currentPrompt?.envCode"
    />

    <AgentConfigPromptImproveModal
      :open="diffModalVisible"
      :original-content="originalContent"
      :improved-content="improvedContent"
      @update:open="diffModalVisible = $event"
      @apply="handleApplyImproved"
    />

    <ModelSelector
      :open="modelDrawerOpen"
      :fixed-model-type="modelSelectorKind === 'chat' ? 'chat' : modelSelectorKind === 'image' ? 'image' : 'voice'"
      @update:open="modelDrawerOpen = $event"
      @select="onModelSelect"
    />

    <InstanceSelector
      :open="instanceDrawerOpen"
      :fixed-model-type="instanceSelectorKind"
      :filter-model-key="instanceFilterModelKey"
      @update:open="instanceDrawerOpen = $event"
      @select="onInstanceSelect"
      @edit="onInstanceEditFromDrawer"
      @create="onCreateInstanceFromDrawer"
    />

    <InstanceForm
      :visible="instanceFormVisible"
      @update:visible="instanceFormVisible = $event"
      :record="instanceFormRecord"
      @success="onInstanceFormSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed } from 'vue'
import { message } from 'ant-design-vue'
import { aiAgentApi, type AiAgent } from '@/api/aiAgent'
import { aiInstanceApi, type AiInstance } from '@/api/aiInstance'
import { aiModelApi, type AiModel } from '@/api/aiModel'
import { aiAccountApi, type AiAccount } from '@/api/aiAccount'
import { aiPromptApi, type AiPrompt } from '@/api/aiPrompt'
import { aiToolApi, type AiTool } from '@/api/aiTool'
import { aiMcpApi, type AiMcp } from '@/api/aiMcp'
import PromptSelectDrawer from '@/views/admin/ai-config/ai-prompt/PromptSelectDrawer.vue'
import PromptFormModal from '@/views/admin/ai-config/ai-prompt/PromptFormModal.vue'
import PromptHistoryModal from '@/views/admin/ai-config/ai-prompt/PromptHistoryModal.vue'
import ModelSelector from '@/views/admin/ai-config/ai-model/selector/ModelSelector.vue'
import InstanceSelector from '@/views/admin/ai-config/ai-instance/selector/InstanceSelector.vue'
import InstanceForm from '@/views/admin/ai-config/ai-instance/InstanceForm.vue'
import AgentConfigHeader from './agent-config/AgentConfigHeader.vue'
import AgentConfigPersonaSection from './agent-config/AgentConfigPersonaSection.vue'
import AgentConfigIntegrationsSection from './agent-config/AgentConfigIntegrationsSection.vue'
import AgentConfigModelTracksSection from './agent-config/AgentConfigModelTracksSection.vue'
import AgentConfigPromptImproveModal from './agent-config/AgentConfigPromptImproveModal.vue'
import { getTempInfo, useInstanceParamVisibility } from '@/views/admin/ai-config/ai-instance/useInstanceParamVisibility'

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
const localAgentDescription = ref('')

const currentPrompt = ref<AiPrompt | undefined>(undefined)
const loadedPromptContent = ref('')
const placedTools = ref<AiTool[]>([])
const placedMcps = ref<AiMcp[]>([])
const knowledgeKeys = ref<string[]>([])

const modelDrawerOpen = ref(false)
const modelSelectorKind = ref<'chat' | 'image' | 'voice'>('chat')

const currentChatInstance = ref<AiInstance | undefined>(undefined)
const currentImageInstance = ref<AiInstance | undefined>(undefined)
const currentVoiceInstance = ref<AiInstance | undefined>(undefined)
const chatModel = ref<AiModel | undefined>(undefined)
const imageModel = ref<AiModel | undefined>(undefined)
const voiceModel = ref<AiModel | undefined>(undefined)
const chatAccount = ref<AiAccount | undefined>(undefined)
const imageAccount = ref<AiAccount | undefined>(undefined)
const voiceAccount = ref<AiAccount | undefined>(undefined)

const chatParamSnapshot = ref('')
const imageParamSnapshot = ref('')
const voiceParamSnapshot = ref('')

const promptDrawerOpen = ref(false)
const promptFormOpen = ref(false)
const historyModalOpen = ref(false)
const improveLoading = ref(false)
const diffModalVisible = ref(false)
const originalContent = ref('')
const improvedContent = ref('')

const instanceDrawerOpen = ref(false)
const instanceSelectorKind = ref<'chat' | 'image' | 'voice'>('chat')

const instanceFilterModelKey = computed(() => {
  const kind = instanceSelectorKind.value
  const mk =
    kind === 'chat'
      ? chatModel.value?.modelKey
      : kind === 'image'
        ? imageModel.value?.modelKey
        : voiceModel.value?.modelKey
  return mk?.trim() || undefined
})
const instanceFormVisible = ref(false)
const instanceFormRecord = ref<AiInstance | undefined>(undefined)
const instanceFormTargetKind = ref<'chat' | 'image' | 'voice'>('chat')

function defaultParamForm(): AiInstance {
  return {
    status: 'enabled',
    isDefault: 'N',
    temperature: 0.7,
    maxTokens: 2048,
    topP: 1.0,
    frequencyPenalty: 0,
    presencePenalty: 0,
    accountKey: '',
  }
}

const chatParamForm = reactive<AiInstance>(defaultParamForm())
const imageParamForm = reactive<AiInstance>({ ...defaultParamForm() })
const voiceParamForm = reactive<AiInstance>({ ...defaultParamForm() })

const {
  paramSectionTitle: chatParamSectionTitle,
  capabilityHint: chatCapabilityHint,
  hasParamSchema: chatHasParamSchema,
  unsupportedParamCodes: chatUnsupportedParamCodes,
  modelKind: chatModelKind,
  showChatTemperature: chatShowChatTemperature,
  showChatMaxTokens: chatShowChatMaxTokens,
  showChatTopP: chatShowChatTopP,
  showChatTopK: chatShowChatTopK,
  showChatSeed: chatShowChatSeed,
  showChatStopSequences: chatShowChatStopSequences,
  showChatPenalties: chatShowChatPenalties,
  showChatFrequencyPenalty: chatShowChatFrequencyPenalty,
  showChatPresencePenalty: chatShowChatPresencePenalty,
  showEmbeddingDimensions: chatShowEmbeddingDimensions,
  showImageSize: chatShowImageSize,
  showImageStyle: chatShowImageStyle,
  embeddingHasAnyControl: chatEmbeddingHasAnyControl,
  imageHasAnyControl: chatImageHasAnyControl,
} = useInstanceParamVisibility(chatModel)

const {
  paramSectionTitle: imageParamSectionTitle,
  capabilityHint: imageCapabilityHint,
  hasParamSchema: imageHasParamSchema,
  unsupportedParamCodes: imageUnsupportedParamCodes,
  modelKind: imageModelKind,
  showChatTemperature: imageShowChatTemperature,
  showChatMaxTokens: imageShowChatMaxTokens,
  showChatTopP: imageShowChatTopP,
  showChatTopK: imageShowChatTopK,
  showChatSeed: imageShowChatSeed,
  showChatStopSequences: imageShowChatStopSequences,
  showChatPenalties: imageShowChatPenalties,
  showChatFrequencyPenalty: imageShowChatFrequencyPenalty,
  showChatPresencePenalty: imageShowChatPresencePenalty,
  showEmbeddingDimensions: imageShowEmbeddingDimensions,
  showImageSize: imageShowImageSize,
  showImageStyle: imageShowImageStyle,
  embeddingHasAnyControl: imageEmbeddingHasAnyControl,
  imageHasAnyControl: imageImageHasAnyControl,
} = useInstanceParamVisibility(imageModel)

const {
  paramSectionTitle: voiceParamSectionTitle,
  capabilityHint: voiceCapabilityHint,
  hasParamSchema: voiceHasParamSchema,
  unsupportedParamCodes: voiceUnsupportedParamCodes,
  modelKind: voiceModelKind,
  showChatTemperature: voiceShowChatTemperature,
  showChatMaxTokens: voiceShowChatMaxTokens,
  showChatTopP: voiceShowChatTopP,
  showChatTopK: voiceShowChatTopK,
  showChatSeed: voiceShowChatSeed,
  showChatStopSequences: voiceShowChatStopSequences,
  showChatPenalties: voiceShowChatPenalties,
  showChatFrequencyPenalty: voiceShowChatFrequencyPenalty,
  showChatPresencePenalty: voiceShowChatPresencePenalty,
  showEmbeddingDimensions: voiceShowEmbeddingDimensions,
  showImageSize: voiceShowImageSize,
  showImageStyle: voiceShowImageStyle,
  embeddingHasAnyControl: voiceEmbeddingHasAnyControl,
  imageHasAnyControl: voiceImageHasAnyControl,
} = useInstanceParamVisibility(voiceModel)

function serializeParams(form: AiInstance): string {
  const keys = [
    'temperature',
    'maxTokens',
    'topP',
    'topK',
    'seed',
    'stopSequences',
    'frequencyPenalty',
    'presencePenalty',
    'dimensions',
    'size',
    'style',
  ] as const
  const o: Record<string, unknown> = {}
  for (const k of keys) {
    o[k] = form[k]
  }
  return JSON.stringify(o)
}

function syncParamForm(form: AiInstance, inst: AiInstance) {
  Object.assign(form, defaultParamForm(), {
    id: inst.id,
    instanceKey: inst.instanceKey,
    instanceName: inst.instanceName,
    modelKey: inst.modelKey,
    modelType: inst.modelType,
    temperature: inst.temperature ?? 0.7,
    maxTokens: inst.maxTokens ?? 2048,
    topP: inst.topP ?? 1,
    topK: inst.topK,
    seed: inst.seed,
    stopSequences: inst.stopSequences,
    frequencyPenalty: inst.frequencyPenalty ?? 0,
    presencePenalty: inst.presencePenalty ?? 0,
    dimensions: inst.dimensions,
    size: inst.size,
    style: inst.style,
    accountKey: inst.accountKey ?? '',
    status: inst.status ?? 'enabled',
    isDefault: inst.isDefault ?? 'N',
  })
}

async function resolveModelAndAccountForTrack(
  instance: AiInstance,
  modelRef: typeof chatModel,
  accountRef: typeof chatAccount
) {
  const tasks: Promise<void>[] = []
  if (instance.modelKey) {
    tasks.push(
      aiModelApi
        .queryPage({ pageNo: 1, pageSize: 1, param: { modelKey: instance.modelKey } })
        .then((resp) => {
          modelRef.value = resp.list?.[0]
        })
    )
  } else {
    modelRef.value = undefined
  }
  if (instance.accountKey) {
    tasks.push(
      aiAccountApi
        .queryPage({ pageNo: 1, pageSize: 1, param: { accountKey: instance.accountKey } })
        .then((resp) => {
          accountRef.value = resp.list?.[0]
        })
    )
  } else {
    accountRef.value = undefined
  }
  await Promise.all(tasks.map((p) => p.catch(() => undefined)))
}

async function resolveFullInstance(inst: AiInstance): Promise<AiInstance> {
  if (inst.id != null && String(inst.id) !== '') {
    const d = await aiInstanceApi.detail(inst.id).catch(() => null)
    if (d) return d
  }
  const key = inst.instanceKey?.trim()
  if (key) {
    const resp = await aiInstanceApi.queryPage({
      pageNo: 1,
      pageSize: 1,
      param: { instanceKey: key },
    })
    const row = resp.list?.[0]
    if (!row) return inst
    if (row.id != null && String(row.id) !== '') {
      const d2 = await aiInstanceApi.detail(row.id).catch(() => row)
      return d2 || row
    }
    return row
  }
  return inst
}

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
    const improved = await aiPromptApi.improvePrompt(content)
    improvedContent.value = improved
    diffModalVisible.value = true
  } catch {
    message.error('美化失败，请重试')
  } finally {
    improveLoading.value = false
  }
}

function handleApplyImproved() {
  if (currentPrompt.value) {
    currentPrompt.value.promptContent = improvedContent.value
  }
  diffModalVisible.value = false
  message.success('已应用美化后的提示词')
}

function openModelSelector(kind: 'chat' | 'image' | 'voice') {
  modelSelectorKind.value = kind
  modelDrawerOpen.value = true
}

function onModelSelect(model: AiModel) {
  modelDrawerOpen.value = false
  const kind = modelSelectorKind.value
  if (kind === 'chat') {
    chatModel.value = model
    if (currentChatInstance.value?.modelKey && currentChatInstance.value.modelKey !== model.modelKey) {
      currentChatInstance.value = undefined
      chatAccount.value = undefined
      Object.assign(chatParamForm, defaultParamForm())
      chatParamSnapshot.value = serializeParams(chatParamForm)
    }
  } else if (kind === 'image') {
    imageModel.value = model
    if (currentImageInstance.value?.modelKey && currentImageInstance.value.modelKey !== model.modelKey) {
      currentImageInstance.value = undefined
      imageAccount.value = undefined
      Object.assign(imageParamForm, defaultParamForm())
      imageParamSnapshot.value = serializeParams(imageParamForm)
    }
  } else {
    voiceModel.value = model
    if (currentVoiceInstance.value?.modelKey && currentVoiceInstance.value.modelKey !== model.modelKey) {
      currentVoiceInstance.value = undefined
      voiceAccount.value = undefined
      Object.assign(voiceParamForm, defaultParamForm())
      voiceParamSnapshot.value = serializeParams(voiceParamForm)
    }
  }
}

function openInstanceSelectorSafe(kind: 'chat' | 'image' | 'voice') {
  const model = kind === 'chat' ? chatModel.value : kind === 'image' ? imageModel.value : voiceModel.value
  if (!model?.modelKey?.trim()) {
    const msg =
      kind === 'chat'
        ? '请先在「模型」中选择对话模型'
        : kind === 'image'
          ? '请先在「模型」中选择图像模型'
          : '请先在「模型」中选择语音模型'
    message.warning(msg)
    return
  }
  instanceSelectorKind.value = kind
  instanceDrawerOpen.value = true
}

async function bindTrackInstance(kind: 'chat' | 'image' | 'voice', instance: AiInstance) {
  const full = await resolveFullInstance(instance)
  if (kind === 'chat') {
    currentChatInstance.value = full
    syncParamForm(chatParamForm, full)
    chatParamSnapshot.value = serializeParams(chatParamForm)
    await resolveModelAndAccountForTrack(full, chatModel, chatAccount)
  } else if (kind === 'image') {
    currentImageInstance.value = full
    syncParamForm(imageParamForm, full)
    imageParamSnapshot.value = serializeParams(imageParamForm)
    await resolveModelAndAccountForTrack(full, imageModel, imageAccount)
  } else {
    currentVoiceInstance.value = full
    syncParamForm(voiceParamForm, full)
    voiceParamSnapshot.value = serializeParams(voiceParamForm)
    await resolveModelAndAccountForTrack(full, voiceModel, voiceAccount)
  }
}

async function onInstanceSelect(instance: AiInstance) {
  const kind = instanceSelectorKind.value
  instanceDrawerOpen.value = false
  await bindTrackInstance(kind, instance)
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

async function pickOrCreateInstanceForModel(model: AiModel, kind: 'chat' | 'image' | 'voice'): Promise<AiInstance> {
  const modelType = kind === 'chat' ? 'chat' : kind === 'image' ? 'image' : 'voice'
  const mk = model.modelKey?.trim()
  if (!mk || model.id == null || String(model.id) === '') {
    throw new Error('模型数据不完整，无法创建推理实例')
  }
  const resp = await aiInstanceApi.queryPage({
    pageNo: 1,
    pageSize: 100,
    param: { modelKey: mk, modelType, status: 'enabled' },
  })
  const candidates = (resp.list || []).filter((i) => i.modelKey === mk && i.status === 'enabled')
  let picked =
    candidates.find((i) => i.isDefault === 'Y') ||
    [...candidates].sort((a, b) => String(a.createTime || '').localeCompare(String(b.createTime || '')))[0]
  if (!picked) {
    await aiModelApi.generateInstances([model.id])
    const resp2 = await aiInstanceApi.queryPage({
      pageNo: 1,
      pageSize: 30,
      param: { modelKey: mk, modelType, status: 'enabled' },
    })
    const list2 = resp2.list || []
    picked =
      list2.find((i) => i.modelKey === mk && i.isDefault === 'Y') ||
      list2.find((i) => i.modelKey === mk) ||
      list2[0]
  }
  if (!picked?.instanceKey) {
    throw new Error('无法为该模型创建推理实例，请检查实例与账号配置')
  }
  return picked
}

async function ensureChatInstanceOrCreate() {
  if (currentChatInstance.value?.instanceKey) return
  const m = chatModel.value
  if (!m?.modelKey) return
  const row = await pickOrCreateInstanceForModel(m, 'chat')
  await bindTrackInstance('chat', row)
}

async function ensureImageInstanceOrCreateIfNeeded() {
  if (currentImageInstance.value?.instanceKey) return
  const m = imageModel.value
  if (!m?.modelKey) return
  const row = await pickOrCreateInstanceForModel(m, 'image')
  await bindTrackInstance('image', row)
}

async function ensureVoiceInstanceOrCreateIfNeeded() {
  if (currentVoiceInstance.value?.instanceKey) return
  const m = voiceModel.value
  if (!m?.modelKey) return
  const row = await pickOrCreateInstanceForModel(m, 'voice')
  await bindTrackInstance('voice', row)
}

function clearChatTrack() {
  currentChatInstance.value = undefined
  chatModel.value = undefined
  chatAccount.value = undefined
  Object.assign(chatParamForm, defaultParamForm())
  chatParamSnapshot.value = serializeParams(chatParamForm)
}

function clearImageTrack() {
  currentImageInstance.value = undefined
  imageModel.value = undefined
  imageAccount.value = undefined
  Object.assign(imageParamForm, defaultParamForm())
  imageParamSnapshot.value = serializeParams(imageParamForm)
}

function clearVoiceTrack() {
  currentVoiceInstance.value = undefined
  voiceModel.value = undefined
  voiceAccount.value = undefined
  Object.assign(voiceParamForm, defaultParamForm())
  voiceParamSnapshot.value = serializeParams(voiceParamForm)
}

function onCreateInstanceFromDrawer() {
  instanceDrawerOpen.value = false
  instanceFormTargetKind.value = instanceSelectorKind.value
  instanceFormRecord.value = undefined
  instanceFormVisible.value = true
}

function onInstanceEditFromDrawer(instance: AiInstance) {
  instanceDrawerOpen.value = false
  const mt = instance.modelType
  instanceFormTargetKind.value = mt === 'image' ? 'image' : mt === 'voice' ? 'voice' : 'chat'
  instanceFormRecord.value = instance
  instanceFormVisible.value = true
}

async function onInstanceFormSuccess() {
  instanceFormVisible.value = false
  const rec = instanceFormRecord.value
  const kind = instanceFormTargetKind.value
  if (!rec?.instanceKey?.trim()) return
  const resp = await aiInstanceApi.queryPage({
    pageNo: 1,
    pageSize: 1,
    param: { instanceKey: rec.instanceKey.trim() },
  })
  const row = resp.list?.[0]
  if (!row) return
  const full = await resolveFullInstance(row)
  if (kind === 'image') {
    if (currentImageInstance.value?.instanceKey === full.instanceKey) {
      currentImageInstance.value = full
      syncParamForm(imageParamForm, full)
      imageParamSnapshot.value = serializeParams(imageParamForm)
      await resolveModelAndAccountForTrack(full, imageModel, imageAccount)
    }
  } else if (kind === 'voice') {
    if (currentVoiceInstance.value?.instanceKey === full.instanceKey) {
      currentVoiceInstance.value = full
      syncParamForm(voiceParamForm, full)
      voiceParamSnapshot.value = serializeParams(voiceParamForm)
      await resolveModelAndAccountForTrack(full, voiceModel, voiceAccount)
    }
  } else if (currentChatInstance.value?.instanceKey === full.instanceKey) {
    currentChatInstance.value = full
    syncParamForm(chatParamForm, full)
    chatParamSnapshot.value = serializeParams(chatParamForm)
    await resolveModelAndAccountForTrack(full, chatModel, chatAccount)
  }
}

function resetEmptyForm() {
  detailSnapshot.value = null
  localAgentName.value = props.agentName || '新 Agent'
  localAgentDescription.value = ''
  currentPrompt.value = undefined
  loadedPromptContent.value = ''
  placedTools.value = []
  placedMcps.value = []
  knowledgeKeys.value = []
  clearChatTrack()
  clearImageTrack()
  clearVoiceTrack()
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

async function resolveToolsByKeys(keys: string[]): Promise<AiTool[]> {
  const out: AiTool[] = []
  for (const key of keys) {
    const resp = await aiToolApi.queryPage({
      pageNo: 1,
      pageSize: 20,
      param: { toolKey: key },
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
      param: { mcpKey: key },
    })
    const hit = (resp.list || []).find((m) => m.mcpKey === key)
    if (hit) out.push(hit)
  }
  return out
}

async function loadInstanceRowByKey(instanceKey: string): Promise<AiInstance | undefined> {
  const key = instanceKey.trim()
  if (!key) return undefined
  const resp = await aiInstanceApi.queryPage({
    pageNo: 1,
    pageSize: 1,
    param: { instanceKey: key },
  })
  const row = resp.list?.[0]
  if (!row) return undefined
  return resolveFullInstance(row)
}

async function backfillFromDetail(detail: AiAgent) {
  localAgentName.value = detail.agentName || props.agentName || '未命名的智能体'
  localAgentDescription.value = detail.description ?? ''

  if (detail.chatInstanceKey) {
    const inst = await loadInstanceRowByKey(detail.chatInstanceKey)
    if (inst) {
      currentChatInstance.value = inst
      syncParamForm(chatParamForm, inst)
      chatParamSnapshot.value = serializeParams(chatParamForm)
      await resolveModelAndAccountForTrack(inst, chatModel, chatAccount)
    } else {
      clearChatTrack()
    }
  } else {
    clearChatTrack()
  }

  if (detail.imageInstanceKey) {
    const inst = await loadInstanceRowByKey(detail.imageInstanceKey)
    if (inst) {
      currentImageInstance.value = inst
      syncParamForm(imageParamForm, inst)
      imageParamSnapshot.value = serializeParams(imageParamForm)
      await resolveModelAndAccountForTrack(inst, imageModel, imageAccount)
    } else {
      clearImageTrack()
    }
  } else {
    clearImageTrack()
  }

  if (detail.voiceInstanceKey) {
    const inst = await loadInstanceRowByKey(detail.voiceInstanceKey)
    if (inst) {
      currentVoiceInstance.value = inst
      syncParamForm(voiceParamForm, inst)
      voiceParamSnapshot.value = serializeParams(voiceParamForm)
      await resolveModelAndAccountForTrack(inst, voiceModel, voiceAccount)
    } else {
      clearVoiceTrack()
    }
  } else {
    clearVoiceTrack()
  }

  if (detail.promptKey) {
    currentPrompt.value = await resolvePromptByKey(detail.promptKey)
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
    return
  }
  loading.value = true
  try {
    const detail = await aiAgentApi.detail(props.agentId)
    detailSnapshot.value = { ...detail }
    await backfillFromDetail(detail)
  } catch (e: any) {
    message.error(e?.message || '加载智能体详情失败')
    resetEmptyForm()
  } finally {
    loading.value = false
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
  return {
    ...base,
    agentName: localAgentName.value.trim(),
    description: localAgentDescription.value.trim(),
    promptKey: currentPrompt.value?.promptKey,
    chatInstanceKey: currentChatInstance.value?.instanceKey,
    imageInstanceKey: currentImageInstance.value?.instanceKey,
    voiceInstanceKey: currentVoiceInstance.value?.instanceKey,
    knowledgeBaseKeys: knowledgeKeys.value.length ? knowledgeKeys.value.join(',') : '',
    toolKeys: placedTools.value.map((t) => t.toolKey).filter(Boolean).join(','),
    mcpKeys: placedMcps.value.map((m) => m.mcpKey).filter(Boolean).join(','),
  }
}

async function maybePersistInstanceTrack(track: 'chat' | 'image' | 'voice') {
  const inst =
    track === 'chat'
      ? currentChatInstance.value
      : track === 'image'
        ? currentImageInstance.value
        : currentVoiceInstance.value
  const form =
    track === 'chat' ? chatParamForm : track === 'image' ? imageParamForm : voiceParamForm
  const snapRef =
    track === 'chat' ? chatParamSnapshot : track === 'image' ? imageParamSnapshot : voiceParamSnapshot
  if (!inst?.id || String(inst.id) === '') return
  if (serializeParams(form) === snapRef.value) return
  const fresh = await aiInstanceApi.detail(inst.id)
  const merged: AiInstance = {
    ...fresh,
    temperature: form.temperature,
    maxTokens: form.maxTokens,
    topP: form.topP,
    topK: form.topK,
    seed: form.seed,
    stopSequences: form.stopSequences,
    frequencyPenalty: form.frequencyPenalty,
    presencePenalty: form.presencePenalty,
    dimensions: form.dimensions,
    size: form.size,
    style: form.style,
  }
  await aiInstanceApi.update(merged)
  const after = await aiInstanceApi.detail(inst.id)
  if (track === 'chat') {
    currentChatInstance.value = after
    syncParamForm(chatParamForm, after)
    chatParamSnapshot.value = serializeParams(chatParamForm)
  } else if (track === 'image') {
    currentImageInstance.value = after
    syncParamForm(imageParamForm, after)
    imageParamSnapshot.value = serializeParams(imageParamForm)
  } else {
    currentVoiceInstance.value = after
    syncParamForm(voiceParamForm, after)
    voiceParamSnapshot.value = serializeParams(voiceParamForm)
  }
}

async function handleSave() {
  if (!localAgentName.value.trim()) {
    message.warning('请输入智能体名称')
    return
  }
  if (!currentPrompt.value?.promptKey) {
    message.warning('请先选择或新建提示词')
    return
  }
  if (!chatModel.value?.modelKey) {
    message.warning('请选择对话模型')
    return
  }
  submitting.value = true
  try {
    try {
      await ensureChatInstanceOrCreate()
    } catch (e: any) {
      message.error(e?.message || '无法准备对话推理实例')
      return
    }
    try {
      await ensureImageInstanceOrCreateIfNeeded()
    } catch (e: any) {
      message.error(e?.message || '无法准备图像推理实例')
      return
    }
    try {
      await ensureVoiceInstanceOrCreateIfNeeded()
    } catch (e: any) {
      message.error(e?.message || '无法准备语音推理实例')
      return
    }
    if (!currentChatInstance.value?.instanceKey) {
      message.error('未能绑定对话推理实例')
      return
    }
    const p = currentPrompt.value
    if (p?.id != null && String(p.id) !== '' && String(p.promptContent ?? '') !== String(loadedPromptContent.value ?? '')) {
      await aiPromptApi.update({ ...p })
      loadedPromptContent.value = p.promptContent ?? ''
    }
    await maybePersistInstanceTrack('chat')
    await maybePersistInstanceTrack('image')
    await maybePersistInstanceTrack('voice')
    const payload = buildSubmitPayload()
    if (props.agentId != null && props.agentId !== '') {
      await aiAgentApi.update({ ...payload, id: props.agentId })
      message.success('智能体已保存')
    } else {
      await aiAgentApi.create(payload)
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
  { immediate: true }
)

watch(
  () => props.agentName,
  (name) => {
    if (props.agentId == null || props.agentId === '') {
      localAgentName.value = name || '新 Agent'
    }
  },
  { immediate: true }
)
</script>

<style scoped>
.config-container {
  padding: 32px;
  max-width: 1200px;
  margin: 0 auto;
  overflow-y: auto;
  height: 100%;
}

.config-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.bottom-spacing {
  height: 40px;
}
</style>
