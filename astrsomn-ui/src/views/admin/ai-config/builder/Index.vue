<template>
  <main class="builder-main">
    <div class="builder-body">
      <div class="builder-left-viewport">
        <div class="builder-topbar">
          <a-tooltip :title="t.index.back">
            <button class="topbar-icon-btn" @click="handleCancel">
              <LeftOutlined/>
            </button>
          </a-tooltip>
          <span class="topbar-title">{{ isEdit ? t.index.editAgent : t.index.createAgent }}</span>
          <div class="topbar-actions">
            <a-tooltip :title="t.index.reset">
              <button :disabled="submitting" class="topbar-icon-btn" @click="handleReset">
                <ReloadOutlined/>
              </button>
            </a-tooltip>
            <a-tooltip :title="isEdit ? t.index.save : t.index.publish">
              <button :disabled="submitting" class="topbar-icon-btn primary" @click="handleSubmit">
                <span v-if="submitting" class="spinner"></span>
                <RocketOutlined v-else/>
              </button>
            </a-tooltip>
          </div>
        </div>
        <section class="builder-left">
          <LeftTop
              ref="leftTopRef"
              :agent-name="agentName"
              :description="description"
              @update:agentName="agentName = $event"
              @update:description="description = $event"
              @update:account="onAccountUpdate"
              @update:model="onModelUpdate"
              @update:instance="onInstanceUpdate"
          />
          <LeftCenter
              ref="leftCenterRef"
              :knowledge-keys="knowledgeKeys"
              :mcps="placedMcps"
              :tools="placedTools"
              @update:prompt="onPromptUpdate"
              @update:image-instance="onImageInstanceUpdate"
              @add:tool="onToolAdd"
              @remove:tool="onToolRemove"
              @add:mcp="onMcpAdd"
              @remove:mcp="onMcpRemove"
              @add:kb="onKbAdd"
              @remove:kb="onKbRemove"
          />
        </section>
      </div>
      <Right/>
    </div>
  </main>
</template>

<script lang="ts" setup>
import {computed, onMounted, provide, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {message} from 'ant-design-vue'
import {LeftOutlined, ReloadOutlined, RocketOutlined} from '@ant-design/icons-vue'
import type {AiAgent} from '@/api/aiAgent'
import {aiAgentApi} from '@/api/aiAgent'
import type {AiAccount} from '@/api/aiAccount'
import {aiAccountApi} from '@/api/aiAccount'
import type {AiModel} from '@/api/aiModel'
import {aiModelApi} from '@/api/aiModel'
import type {AiInstance} from '@/api/aiInstance'
import {aiInstanceApi} from '@/api/aiInstance'
import type {AiPrompt} from '@/api/aiPrompt'
import {aiPromptApi} from '@/api/aiPrompt'
import type {AiTool} from '@/api/aiTool'
import {aiToolApi} from '@/api/aiTool'
import type {AiMcp} from '@/api/aiMcp'
import {aiMcpApi} from '@/api/aiMcp'

import LeftTop from './component/LeftTop.vue'
import LeftCenter from './component/LeftCenter.vue'
import Right from './component/Right.vue'
import {BUILDER_CHAT_CONTEXT} from './component/builderChatInjection'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-builder')

const route = useRoute()
const router = useRouter()

const leftTopRef = ref<InstanceType<typeof LeftTop> | null>(null)
const leftCenterRef = ref<InstanceType<typeof LeftCenter> | null>(null)

const agentId = computed(() => route.query.id as string | undefined)
const isEdit = computed(() => !!agentId.value)
const submitting = ref(false)

const agentName = ref(t.value.index.unnamedAgent)
const description = ref('')
const status = ref('enabled')
const agentKey = ref('')
const enableStream = ref(true)
const memoryMode = ref('SLIDING_WINDOW')
const memoryWindowSize = ref('10')

const currentAccount = ref<AiAccount | undefined>(undefined)
const currentModel = ref<AiModel | undefined>(undefined)
const currentInstance = ref<AiInstance | undefined>(undefined)
const currentPrompt = ref<AiPrompt | undefined>(undefined)
const currentImageInstance = ref<AiInstance | undefined>(undefined)
const placedTools = ref<AiTool[]>([])
const placedMcps = ref<AiMcp[]>([])
const knowledgeKeys = ref<string[]>([])

const builderChatMemoryKey = ref(`builder-preview:${Date.now()}`)

const builderChatSnapshot = computed(() => ({
  currentAccount: currentAccount.value,
  currentModel: currentModel.value,
  currentInstance: currentInstance.value,
  currentPrompt: currentPrompt.value,
  placedTools: placedTools.value,
  placedMcps: placedMcps.value,
  knowledgeKeys: knowledgeKeys.value
}))

provide(BUILDER_CHAT_CONTEXT, {
  snapshot: builderChatSnapshot,
  memoryKey: builderChatMemoryKey
})

function onAccountUpdate(account: AiAccount | undefined) {
  currentAccount.value = account
}

function onModelUpdate(model: AiModel | undefined) {
  currentModel.value = model
}

function onInstanceUpdate(instance: AiInstance | undefined) {
  currentInstance.value = instance
}

function onPromptUpdate(prompt: AiPrompt | undefined) {
  currentPrompt.value = prompt
}

function onImageInstanceUpdate(instance: AiInstance | undefined) {
  currentImageInstance.value = instance
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

function onKbAdd(kbKey: string) {
  if (kbKey && !knowledgeKeys.value.includes(kbKey)) {
    knowledgeKeys.value = [...knowledgeKeys.value, kbKey]
  }
}

function onKbRemove(kbKey: string) {
  knowledgeKeys.value = knowledgeKeys.value.filter((k) => k !== kbKey)
}

function buildSubmitPayload(): AiAgent {
  return {
    agentName: agentName.value,
    agentKey: agentKey.value || undefined,
    status: status.value,
    enableStream: enableStream.value,
    description: description.value,
    memoryMode: memoryMode.value,
    memoryWindowSize: memoryWindowSize.value,
    knowledgeBaseKeys: knowledgeKeys.value.join(','),
    chatInstanceKey: currentInstance.value?.instanceKey,
    imageInstanceKey: currentImageInstance.value?.instanceKey,
    promptKey: currentPrompt.value?.promptKey,
    toolKeys: placedTools.value.map((t) => t.toolKey).filter(Boolean).join(','),
    mcpKeys: placedMcps.value.map((m) => m.mcpKey).filter(Boolean).join(',')
  }
}

async function handleSubmit() {
  if (!agentName.value.trim()) {
    message.warning(t.value.index.agentNameRequired)
    return
  }
  submitting.value = true
  try {
    const payload = buildSubmitPayload()
    if (isEdit.value) {
      await aiAgentApi.update({...payload, id: agentId.value as string})
      message.success(t.value.index.agentSaved)
    } else {
      await aiAgentApi.create(payload)
      message.success(t.value.index.agentPublished)
    }
    router.push('/admin/ai-config/agents')
  } catch (e: any) {
    message.error(e?.message || t.value.index.submitFailed)
  } finally {
    submitting.value = false
  }
}

function handleCancel() {
  router.push('/admin/ai-config/agents')
}

function handleReset() {
  agentName.value = t.value.index.unnamedAgent
  description.value = ''
  status.value = 'enabled'
  agentKey.value = ''
  enableStream.value = true
  memoryMode.value = 'SLIDING_WINDOW'
  memoryWindowSize.value = '10'
  currentAccount.value = undefined
  currentModel.value = undefined
  currentInstance.value = undefined
  currentPrompt.value = undefined
  currentImageInstance.value = undefined
  placedTools.value = []
  placedMcps.value = []
  knowledgeKeys.value = []
  leftTopRef.value?.setAccount(undefined)
  leftTopRef.value?.setModel(undefined)
  leftTopRef.value?.setInstance(undefined)
  leftCenterRef.value?.setPrompt(undefined)
  leftCenterRef.value?.setImageInstance(undefined)
}

async function backfillFromDetail(detail: AiAgent) {
  agentName.value = detail.agentName || t.value.index.unnamedAgent
  description.value = detail.description || ''
  status.value = detail.status || 'enabled'
  agentKey.value = detail.agentKey || ''
  enableStream.value = detail.enableStream ?? true
  memoryMode.value = detail.memoryMode || 'SLIDING_WINDOW'
  memoryWindowSize.value = detail.memoryWindowSize || '10'

  if (detail.knowledgeBaseKeys) {
    knowledgeKeys.value = detail.knowledgeBaseKeys.split(',').map((k) => k.trim()).filter(Boolean)
  }

  const promises: Promise<void>[] = []

  if (detail.chatInstanceKey) {
    promises.push(
        aiInstanceApi.queryPage({pageNo: 1, pageSize: 1, param: {instanceKey: detail.chatInstanceKey}})
            .then(async (resp) => {
              const inst = resp.list?.[0]
              if (inst) {
                currentInstance.value = inst
                leftTopRef.value?.setInstance(inst)

                const subTasks: Promise<void>[] = []
                if (inst.modelKey) {
                  subTasks.push(
                      aiModelApi.queryPage({pageNo: 1, pageSize: 1, param: {modelKey: inst.modelKey}})
                          .then((mResp) => {
                            const model = mResp.list?.[0]
                            if (model) {
                              currentModel.value = model
                              leftTopRef.value?.setModel(model)
                            }
                          })
                  )
                }
                if (inst.accountKey) {
                  subTasks.push(
                      aiAccountApi.queryPage({pageNo: 1, pageSize: 1, param: {accountKey: inst.accountKey}})
                          .then((aResp) => {
                            const account = aResp.list?.[0]
                            if (account) {
                              currentAccount.value = account
                              leftTopRef.value?.setAccount(account)
                            }
                          })
                  )
                }
                await Promise.allSettled(subTasks)
              }
            })
    )
  }

  if (detail.imageInstanceKey) {
    promises.push(
        aiInstanceApi.queryPage({pageNo: 1, pageSize: 1, param: {instanceKey: detail.imageInstanceKey}})
            .then((resp) => {
              const inst = resp.list?.[0]
              if (inst) {
                currentImageInstance.value = inst
                leftCenterRef.value?.setImageInstance(inst)
              }
            })
    )
  }

  if (detail.promptKey) {
    promises.push(
        aiPromptApi.queryPage({pageNo: 1, pageSize: 1, param: {promptKey: detail.promptKey}})
            .then((resp) => {
              const p = resp.list?.[0]
              if (p) {
                currentPrompt.value = p
                leftCenterRef.value?.setPrompt(p)
              }
            })
    )
  }

  if (detail.toolKeys) {
    const keys = detail.toolKeys.split(',').map((k) => k.trim()).filter(Boolean)
    if (keys.length) {
      promises.push(
          aiToolApi.queryPage({pageNo: 1, pageSize: keys.length + 5, param: {}})
              .then((resp) => {
                placedTools.value = (resp.list || []).filter((t) => t.toolKey && keys.includes(t.toolKey))
              })
      )
    }
  }

  if (detail.mcpKeys) {
    const keys = detail.mcpKeys.split(',').map((k) => k.trim()).filter(Boolean)
    if (keys.length) {
      promises.push(
          aiMcpApi.queryPage({pageNo: 1, pageSize: keys.length + 5, param: {}})
              .then((resp) => {
                placedMcps.value = (resp.list || []).filter((m) => m.mcpKey && keys.includes(m.mcpKey))
              })
      )
    }
  }

  await Promise.allSettled(promises)
}

async function initEdit() {
  if (!agentId.value) return
  try {
    const detail = await aiAgentApi.detail(agentId.value)
    await backfillFromDetail(detail)
  } catch (e: any) {
    message.error(e?.message || t.value.index.loadDetailFailed)
  }
}

onMounted(() => {
  if (isEdit.value) {
    void initEdit()
  }
})
</script>

<style scoped>
.builder-main {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: calc(100vh - 60px);
  max-height: calc(100vh - 60px);

  overflow: hidden;
}

.builder-body {
  flex: 1;
  min-height: 0;
  display: flex;
  gap: 16px;
  overflow: hidden;
  --ab-bg-page: var(--builder-bg-page);
  --ab-glass-bg: var(--builder-glass-bg);
  --ab-glass-border: var(--builder-glass-border);
  --ab-glass-shadow: var(--builder-glass-shadow);
  --ab-glass-radius: 16px;
  --ab-glass-haze: 10px;
  --ab-hover-line: var(--builder-hover-line);
  --ab-hover-shadow: var(--builder-hover-shadow);
  --ab-btn-glow: var(--builder-glass-shadow);
  background: var(--builder-bg-page);
  padding: 0 0 0 16px;
}

.builder-left-viewport {
  flex: 2.5 1 0;
  min-width: 0;
  position: relative;
  overflow-y: auto;
}

.builder-topbar {
  position: sticky;
  top: 16px;
  left: 0;
  right: 0;
  height: 36px;
  display: flex;
  align-items: center;
  z-index: 10;
  background: transparent;
  padding-bottom: 12px;
  margin-bottom: 12px;
}

.topbar-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--builder-topbar-title);
  white-space: nowrap;
  margin-left: 8px;
}

.topbar-actions {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  gap: 8px;
}

.topbar-icon-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 1px solid var(--builder-icon-btn-border);
  background: var(--builder-icon-btn-bg);
  color: var(--builder-icon-btn-color);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 15px;
  padding: 0;
}

.topbar-icon-btn:hover {
  color: var(--builder-icon-btn-hover-color);
  border-color: var(--builder-icon-btn-hover-border);
  box-shadow: var(--builder-icon-btn-hover-shadow);
}

.topbar-icon-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.topbar-icon-btn.primary {
  background: var(--primary-gradient);
  color: #fff;
  border: none;
  box-shadow: var(--assembly-btn-primary-shadow);
}

.topbar-icon-btn.primary:hover {
  filter: brightness(1.1);
  box-shadow: 0 2px 12px color-mix(in srgb, var(--primary) 45%, transparent);
}

.spinner {
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid color-mix(in srgb, #fff 30%, transparent);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.builder-left {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding-top: 4px;
}
</style>
