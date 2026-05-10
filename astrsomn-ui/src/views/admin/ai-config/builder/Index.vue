<template>
  <main class="builder-main">
    <div class="builder-body">
      <section class="builder-left">
        <LeftTop
          ref="leftTopRef"
          :agent-name="agentName"
          :description="description"
          :is-edit="isEdit"
          :submitting="submitting"
          @update:agentName="agentName = $event"
          @update:description="description = $event"
          @update:account="onAccountUpdate"
          @update:model="onModelUpdate"
          @update:instance="onInstanceUpdate"
          @cancel="handleCancel"
          @reset="handleReset"
          @submit="handleSubmit"
        />
        <LeftCenter
          ref="leftCenterRef"
          :tools="placedTools"
          :mcps="placedMcps"
          :knowledge-keys="knowledgeKeys"
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
      <Right />
    </div>
  </main>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { aiAgentApi } from '@/api/aiAgent'
import type { AiAgent } from '@/api/aiAgent'
import type { AiAccount } from '@/api/aiAccount'
import type { AiModel } from '@/api/aiModel'
import type { AiInstance } from '@/api/aiInstance'
import type { AiPrompt } from '@/api/aiPrompt'
import type { AiTool } from '@/api/aiTool'
import type { AiMcp } from '@/api/aiMcp'
import { aiInstanceApi } from '@/api/aiInstance'
import { aiToolApi } from '@/api/aiTool'
import { aiMcpApi } from '@/api/aiMcp'
import { aiPromptApi } from '@/api/aiPrompt'

import LeftTop from './component/LeftTop.vue'
import LeftCenter from './component/LeftCenter.vue'
import Right from './component/Right.vue'

const route = useRoute()
const router = useRouter()

const leftTopRef = ref<InstanceType<typeof LeftTop> | null>(null)
const leftCenterRef = ref<InstanceType<typeof LeftCenter> | null>(null)

const agentId = computed(() => route.query.id as string | undefined)
const isEdit = computed(() => !!agentId.value)
const submitting = ref(false)

const agentName = ref('未命名的智能体')
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

function onAccountUpdate(account: AiAccount | undefined) { currentAccount.value = account }
function onModelUpdate(model: AiModel | undefined) { currentModel.value = model }
function onInstanceUpdate(instance: AiInstance | undefined) { currentInstance.value = instance }
function onPromptUpdate(prompt: AiPrompt | undefined) { currentPrompt.value = prompt }
function onImageInstanceUpdate(instance: AiInstance | undefined) { currentImageInstance.value = instance }

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
    message.warning('请输入智能体名称')
    return
  }
  submitting.value = true
  try {
    const payload = buildSubmitPayload()
    if (isEdit.value) {
      await aiAgentApi.update({ ...payload, id: agentId.value as string })
      message.success('智能体已保存')
    } else {
      await aiAgentApi.create(payload)
      message.success('智能体已发布')
    }
    router.push('/admin/ai-config/agents')
  } catch (e: any) {
    message.error(e?.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

function handleCancel() {
  router.push('/admin/ai-config/agents')
}

function handleReset() {
  agentName.value = '未命名的智能体'
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
  agentName.value = detail.agentName || '未命名的智能体'
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
      aiInstanceApi.queryPage({ pageNo: 1, pageSize: 1, param: { instanceKey: detail.chatInstanceKey } })
        .then((resp) => {
          const inst = resp.list?.[0]
          if (inst) {
            currentInstance.value = inst
            leftTopRef.value?.setInstance(inst)
          }
        })
    )
  }

  if (detail.imageInstanceKey) {
    promises.push(
      aiInstanceApi.queryPage({ pageNo: 1, pageSize: 1, param: { instanceKey: detail.imageInstanceKey } })
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
      aiPromptApi.queryPage({ pageNo: 1, pageSize: 1, param: { promptKey: detail.promptKey } })
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
        aiToolApi.queryPage({ pageNo: 1, pageSize: keys.length + 5, param: {} })
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
        aiMcpApi.queryPage({ pageNo: 1, pageSize: keys.length + 5, param: {} })
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
    message.error(e?.message || '加载智能体详情失败')
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
  --ab-bg-page: #f8fafc;
  --ab-glass-bg: rgba(255, 255, 255, 0.8);
  --ab-glass-border: rgba(255, 255, 255, 0.6);
  --ab-glass-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  --ab-glass-radius: 16px;
  --ab-glass-haze: 10px;
  --ab-hover-line: #3b82f6;
  --ab-hover-shadow: 0 0 15px rgba(59, 130, 246, 0.15);
  background: var(--ab-bg-page);
  padding: 16px;
}

.builder-left {
  flex: 2.5 1 0;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 24px;
  overflow: hidden;
}
</style>
