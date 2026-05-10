<template>
  <div class="chat-home">
    <AppHeader 
      :showBrand="true" 
      :showBack="false"
      brandStatus="AI Assistant"
      :showDoc="true"
      :showSwitch="true"
      switchTarget="admin"
    />

    <main class="chat-main">
      <ChatSessionSidebar
        :loading="sessionLoading"
        :items="sessionItems"
        :selected-memory-key="currentMemoryKey"
        :collapsed="sidebarCollapsed"
        @update:collapsed="sidebarCollapsed = $event"
        @open="openSession"
        @create="createNewSession"
        @delete="deleteSession"
      />

      <section class="chat-content">
        <div v-if="isNewSessionView" class="new-session-stage">
          <div class="new-session-intro">
            <h2>今天想聊点什么？</h2>
            <p>输入问题即可开启新会话，你可以选择不同 Agent 与模型实例。</p>
          </div>
          <ChatInputPanel
            layout="centered"
            v-model:selected-agent="selectedAgent"
            v-model:selected-chat-instance-key="selectedChatInstanceKey"
            v-model:user-input="userInput"
            v-model:file-url-list="fileUrlList"
            v-model:is-deep-thinking="isDeepThinking"
            v-model:is-web-search="isWebSearch"
            :is-streaming="isStreaming"
            :options-loading="optionsLoading"
            :send-disabled="sendDisabled"
            :agent-options="agentOptions"
            :chat-instance-options="chatInstanceOptions"
            :model-capabilities="currentInstanceCapabilities"
            @submit="submitQuestion"
            @stop="stopStreaming"
          />
        </div>

        <div v-else ref="messagesContainerRef" class="chat-messages-container">
          <div class="message-scroll-area">
            <transition-group name="message-fade">
              <ChatMessageItem
                v-for="item in messages"
                :key="item.id"
                :role="item.role"
                :content="item.content"
                :segments="item.segments"
                :streaming="item.streaming"
                :error="item.error"
              />
            </transition-group>
            <div ref="messagesBottomRef" class="messages-bottom-spacer"></div>
          </div>
        </div>

        <ChatInputPanel
          v-if="!isNewSessionView"
          layout="bottom"
          v-model:selected-agent="selectedAgent"
          v-model:selected-chat-instance-key="selectedChatInstanceKey"
          v-model:user-input="userInput"
          v-model:file-url-list="fileUrlList"
          v-model:is-deep-thinking="isDeepThinking"
          v-model:is-web-search="isWebSearch"
          :is-streaming="isStreaming"
          :options-loading="optionsLoading"
          :send-disabled="sendDisabled"
          :agent-options="agentOptions"
          :chat-instance-options="chatInstanceOptions"
          :model-capabilities="currentInstanceCapabilities"
          @submit="submitQuestion"
          @stop="stopStreaming"
        />
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { message, Modal } from 'ant-design-vue'
import AppHeader from '@/components/top/AppHeader.vue'
import ChatInputPanel from '@/views/chat-index/ChatInputPanel.vue'
import ChatMessageItem from '@/views/chat-index/ChatMessageItem.vue'
import ChatSessionSidebar from '@/views/chat-index/ChatSessionSidebar.vue'
import { adaptSessionToSessionItem, aiChatSessionApi } from '@/api/aiChatSession'
import { aiInstanceApi, type AiInstance } from '@/api/aiInstance.ts'
import { aiAgentApi, type AiAgent } from '@/api/aiAgent.ts'
import { aiConversationApi, type AiConversation } from '@/api/aiConversation'
import type { ChatSessionItem } from '@/components/chat-session/types'
import { WORKSPACE_ENV_HEADER, WORKSPACE_ENV_STORAGE_KEY } from '@/constants/workspaceEnv.ts'

type ChatMessage = {
  id: string
  role: 'user' | 'ai'
  content: string
  segments?: ChatSegment[]
  streaming?: boolean
  error?: boolean
}

type ChatSegmentType = 'text' | 'thought' | 'html'

type ChatSegment = {
  type: ChatSegmentType
  content: string
}

type StreamEventType = 'text' | 'thought' | 'html' | 'error' | 'done'

type StreamEvent = {
  type: StreamEventType
  content: string
}

const CHAT_MEMORY_KEY = 'astrsomn-chat-memory-key'
const CHAT_DRAFT_KEY_PREFIX = 'astrsomn-chat-draft:'

type ChatDraftState = {
  userInput: string
  fileUrlList: string[]
  isDeepThinking: boolean
  isWebSearch: boolean
  selectedAgent?: string
  selectedChatInstanceKey?: string
}

const userInput = ref('')
const fileUrlList = ref<string[]>([])
const selectedChatInstanceKey = ref<string>()
const selectedAgent = ref<string>()
const isDeepThinking = ref(false)
const isWebSearch = ref(false)
const isStreaming = ref(false)
const optionsLoading = ref(false)
const chatInstanceOptions = ref<AiInstance[]>([])
const agentOptions = ref<AiAgent[]>([])
const messagesContainerRef = ref<HTMLElement | null>(null)
const messagesBottomRef = ref<HTMLElement | null>(null)
const sessionLoading = ref(false)
const sessionItems = ref<ChatSessionItem[]>([])
const currentMemoryKey = ref('')
const sidebarCollapsed = ref(false)
const messages = ref<ChatMessage[]>([])

let abortController: AbortController | null = null

const sendDisabled = computed(() => {
  if (isStreaming.value) {
    return false
  }
  return (
    (!userInput.value.trim() && fileUrlList.value.length === 0) ||
    !selectedChatInstanceKey.value ||
    !selectedAgent.value
  )
})

const isNewSessionView = computed(() => {
  return messages.value.length === 1 && messages.value[0]?.id === 'welcome'
})

const currentInstanceCapabilities = computed<string[]>(() => {
  const instance = chatInstanceOptions.value.find(
    (i) => i.instanceKey === selectedChatInstanceKey.value
  )
  if (!instance?.capabilities) return []
  try {
    const parsed = JSON.parse(instance.capabilities)
    return Array.isArray(parsed) ? parsed.map(String) : []
  } catch {
    return []
  }
})

const getAgentPreferredChatInstanceKey = (agentKey?: string) => {
  if (!agentKey) {
    return undefined
  }
  const agent = agentOptions.value.find((item) => item.agentKey === agentKey)
  const ik = agent?.chatInstanceKey == null ? undefined : String(agent.chatInstanceKey)
  if (!ik) {
    return undefined
  }
  return chatInstanceOptions.value.some((item) => item.instanceKey === ik) ? ik : undefined
}

const getDefaultChatInstanceKey = (agentKey?: string) => {
  return (
    getAgentPreferredChatInstanceKey(agentKey) ||
    chatInstanceOptions.value.find((item) => item.isDefault === 1)?.instanceKey ||
    chatInstanceOptions.value[0]?.instanceKey ||
    undefined
  )
}

const syncChatInstanceWithAgent = (agentKey?: string) => {
  const preferred = getAgentPreferredChatInstanceKey(agentKey)
  if (preferred) {
    selectedChatInstanceKey.value = preferred
  }
}

const getDraftStorageKey = (memoryKey: string) => `${CHAT_DRAFT_KEY_PREFIX}${memoryKey}`

const clearDraftState = (memoryKey: string) => {
  if (!memoryKey) return
  sessionStorage.removeItem(getDraftStorageKey(memoryKey))
}

const saveDraftState = (memoryKey = currentMemoryKey.value) => {
  if (!memoryKey) return
  const draftState: ChatDraftState = {
    userInput: userInput.value,
    fileUrlList: [...fileUrlList.value],
    isDeepThinking: isDeepThinking.value,
    isWebSearch: isWebSearch.value,
    selectedAgent: selectedAgent.value,
    selectedChatInstanceKey: selectedChatInstanceKey.value
  }
  try {
    sessionStorage.setItem(getDraftStorageKey(memoryKey), JSON.stringify(draftState))
  } catch {
    // Ignore storage write failures.
  }
}

const restoreDraftState = (memoryKey: string) => {
  if (!memoryKey) return false
  const raw = sessionStorage.getItem(getDraftStorageKey(memoryKey))
  if (!raw) return false
  try {
    const parsed = JSON.parse(raw) as Partial<ChatDraftState>
    userInput.value = typeof parsed.userInput === 'string' ? parsed.userInput : ''
    fileUrlList.value = Array.isArray(parsed.fileUrlList)
      ? parsed.fileUrlList.filter((item): item is string => typeof item === 'string' && !!item.trim())
      : []
    isDeepThinking.value = parsed.isDeepThinking === true
    isWebSearch.value = parsed.isWebSearch === true
    selectedAgent.value = typeof parsed.selectedAgent === 'string' && parsed.selectedAgent.trim()
      ? parsed.selectedAgent
      : undefined
    selectedChatInstanceKey.value =
      typeof parsed.selectedChatInstanceKey === 'string' && parsed.selectedChatInstanceKey.trim()
        ? parsed.selectedChatInstanceKey
        : undefined
    return true
  } catch {
    clearDraftState(memoryKey)
    return false
  }
}

const resetInputDraftState = () => {
  userInput.value = ''
  fileUrlList.value = []
  isDeepThinking.value = false
  isWebSearch.value = false
  selectedAgent.value = agentOptions.value[0]?.agentKey || undefined
  selectedChatInstanceKey.value = getDefaultChatInstanceKey(selectedAgent.value)
}

const getMemoryKey = () => {
  if (currentMemoryKey.value) return currentMemoryKey.value
  let memoryKey = sessionStorage.getItem(CHAT_MEMORY_KEY) || ''
  if (!memoryKey) {
    memoryKey = `web:${Date.now()}`
    sessionStorage.setItem(CHAT_MEMORY_KEY, memoryKey)
  }
  currentMemoryKey.value = memoryKey
  return memoryKey
}

const setCurrentMemoryKey = (memoryKey: string) => {
  currentMemoryKey.value = memoryKey
  sessionStorage.setItem(CHAT_MEMORY_KEY, memoryKey)
}

const resetWelcomeMessage = () => {
  messages.value = []
}

const isChatNotFoundError = (error: unknown) => {
  const text = String((error as { message?: unknown })?.message ?? '').toLowerCase()
  return (
    text.includes('chat_not_found') ||
    text.includes('not found') ||
    text.includes('不存在') ||
    text.includes('未找到')
  )
}

const mapConversationMessages = (conversationList: AiConversation[]): ChatMessage[] =>
  conversationList.map((item, index) => ({
    id: `${item.id ?? item.memoryKey ?? 'msg'}-${index}`,
    role: item.role === 'user' ? 'user' : 'ai',
    content: item.content || item.conversationContent || ''
  }))

const loadSessionGroups = async () => {
  sessionLoading.value = true
  try {
    const resp = await aiChatSessionApi.queryPage({
      pageNo: 1,
      pageSize: 50,
      param: {
        sessionStatus: 'active'
      }
    })
    sessionItems.value = (resp.list || [])
      .filter((item) => !!item.memoryKey)
      .map((item) => adaptSessionToSessionItem(item))
  } finally {
    sessionLoading.value = false
  }
}

const openSession = async (memoryKey: string) => {
  if (!memoryKey) return
  setCurrentMemoryKey(memoryKey)
  restoreDraftState(memoryKey)
  sessionLoading.value = true
  try {
    const history = await aiConversationApi.recoverByMemoryKey(memoryKey)
    const restored = mapConversationMessages(history || [])
    messages.value = restored.length
      ? restored
      : []
    await scrollToBottom()
  } catch (error: any) {
    if (isChatNotFoundError(error)) {
      resetWelcomeMessage()
      await scrollToBottom()
      return
    }
    message.error(error?.message || '加载会话失败')
  } finally {
    sessionLoading.value = false
  }
}

const createNewSession = () => {
  const key = `web:${Date.now()}`
  setCurrentMemoryKey(key)
  clearDraftState(key)
  resetInputDraftState()
  resetWelcomeMessage()
}

const deleteSession = (session: ChatSessionItem) => {
  if (!session.memoryKey) {
    return
  }
  Modal.confirm({
    title: '删除此会话？',
    content: '删除后无法恢复',
    okType: 'danger',
    onOk: async () => {
      try {
        if (session.id !== undefined && session.id !== null && `${session.id}`.trim() !== '') {
          await aiChatSessionApi.delete([session.id])
        }
        sessionItems.value = sessionItems.value.filter((item) => item.memoryKey !== session.memoryKey)
        if (currentMemoryKey.value === session.memoryKey) {
          createNewSession()
        }
        message.success('会话已删除')
      } catch (error: any) {
        message.error(error?.message || '删除会话失败')
      }
    }
  })
}

const scrollToBottom = async () => {
  await nextTick()
  const bottom = messagesBottomRef.value
  if (bottom) {
    bottom.scrollIntoView({ block: 'end' })
    return
  }
  const container = messagesContainerRef.value
  if (container) {
    container.scrollTop = container.scrollHeight
  }
}

const mergeMessageContent = (segments: ChatSegment[]) =>
  segments
    .map((segment) => (segment.type === 'thought' ? `[思考]\n${segment.content}` : segment.content))
    .join('\n')

const appendAssistantContent = async (
  messageId: string,
  chunk: string,
  type: ChatSegmentType = 'text'
) => {
  if (!chunk) {
    return
  }
  const target = messages.value.find((item) => item.id === messageId)
  if (!target) {
    return
  }
  if (target.role !== 'ai') {
    target.content += chunk
    await scrollToBottom()
    return
  }
  if (!target.segments) {
    target.segments = []
  }
  const lastSegment = target.segments[target.segments.length - 1]
  if (lastSegment && lastSegment.type === type) {
    lastSegment.content += chunk
  } else {
    target.segments.push({ type, content: chunk })
  }
  target.content = mergeMessageContent(target.segments)
  await scrollToBottom()
}

const parseSseEvent = (eventBlock: string) => {
  return eventBlock
    .split(/\r?\n/)
    .filter((line) => line.startsWith('data:'))
    .map((line) => line.slice(5).trimStart())
    .join('\n')
}

const splitJsonObjects = (input: string) => {
  const blocks: string[] = []
  const fragments: string[] = []
  let depth = 0
  let start = -1
  let cursor = 0
  let inString = false
  let escaped = false

  for (let i = 0; i < input.length; i++) {
    const char = input[i]
    if (inString) {
      if (escaped) {
        escaped = false
      } else if (char === '\\') {
        escaped = true
      } else if (char === '"') {
        inString = false
      }
      continue
    }

    if (char === '"') {
      inString = true
      continue
    }

    if (char === '{') {
      if (depth === 0) {
        const fragment = input.slice(cursor, i).trim()
        if (fragment) {
          fragments.push(fragment)
        }
        start = i
      }
      depth++
      continue
    }

    if (char === '}') {
      depth--
      if (depth === 0 && start >= 0) {
        blocks.push(input.slice(start, i + 1))
        cursor = i + 1
        start = -1
      }
    }
  }

  if (depth === 0 && cursor < input.length) {
    const fragment = input.slice(cursor).trim()
    if (fragment) {
      fragments.push(fragment)
    }
  }

  return {
    blocks,
    trailing: fragments.join('\n'),
    hasIncompleteBlock: depth > 0 || start >= 0
  }
}

const toStreamEvent = (payload: unknown): StreamEvent | null => {
  if (typeof payload === 'string') {
    if (payload === '[DONE]') {
      return { type: 'done', content: payload }
    }
    return { type: 'text', content: payload }
  }

  if (!payload || typeof payload !== 'object') {
    return null
  }

  const record = payload as Record<string, unknown>
  const rawType = typeof record.type === 'string' ? record.type.trim().toLowerCase() : 'text'
  const type: StreamEventType =
    rawType === 'thought' || rawType === 'html' || rawType === 'error' || rawType === 'done'
      ? rawType
      : 'text'
  const content = typeof record.content === 'string' ? record.content : ''
  if (type === 'done' || content || type === 'error') {
    return { type, content }
  }
  return null
}

const normalizeStreamPayload = (raw: string): StreamEvent[] => {
  const payload = raw.trim()
  if (!payload) {
    return []
  }

  if (payload === '[DONE]') {
    return [{ type: 'done', content: payload }]
  }

  try {
    const parsed = JSON.parse(payload)
    const event = toStreamEvent(parsed)
    return event ? [event] : []
  } catch {
    // ignore and try other stream formats
  }

  const { blocks, trailing } = splitJsonObjects(payload)
  if (blocks.length > 0) {
    const events = blocks
      .map((block) => {
        try {
          return toStreamEvent(JSON.parse(block))
        } catch {
          return null
        }
      })
      .filter((item): item is StreamEvent => item != null)

    if (trailing) {
      events.push({ type: 'text', content: trailing })
    }
    return events
  }

  return [{ type: 'text', content: raw }]
}

const extractJsonPayloads = (buffer: string) => {
  const payload = buffer.trim()
  if (!payload) {
    return { events: [] as StreamEvent[], remaining: '' }
  }

  if (payload === '[DONE]') {
    return {
      events: [{ type: 'done', content: '[DONE]' } as StreamEvent],
      remaining: ''
    }
  }

  const { blocks, trailing, hasIncompleteBlock } = splitJsonObjects(buffer)
  const events = blocks
    .map((block) => {
      try {
        return toStreamEvent(JSON.parse(block))
      } catch {
        return null
      }
    })
    .filter((item): item is StreamEvent => item != null)

  if (!blocks.length) {
    return {
      events: [],
      remaining: buffer
    }
  }

  if (!hasIncompleteBlock && trailing) {
    events.push(...normalizeStreamPayload(trailing))
    return { events, remaining: '' }
  }

  return {
    events,
    remaining: hasIncompleteBlock ? buffer.slice(buffer.lastIndexOf('{')) : ''
  }
}

const applyStreamEvent = async (messageId: string, event: StreamEvent) => {
  const target = messages.value.find((item) => item.id === messageId)
  if (!target) {
    return true
  }

  switch (event.type) {
    case 'text':
      await appendAssistantContent(messageId, event.content, 'text')
      return true
    case 'html':
      await appendAssistantContent(messageId, event.content, 'html')
      return true
    case 'error':
      target.error = true
      await appendAssistantContent(messageId, event.content || '流式响应异常')
      return false
    case 'thought':
      await appendAssistantContent(messageId, event.content, 'thought')
      return true
    case 'done':
      return false
    default:
      return true
  }
}

const readStreamText = async (response: Response, messageId: string) => {
  const reader = response.body?.getReader()
  if (!reader) {
    throw new Error('未获取到流式响应体')
  }

  const decoder = new TextDecoder('utf-8')
  const contentType = response.headers.get('content-type') || ''
  const isSse = contentType.includes('text/event-stream')
  let sseBuffer = ''
  let rawBuffer = ''

  while (true) {
    const { done, value } = await reader.read()
    if (done) {
      break
    }

    const chunk = decoder.decode(value, { stream: true })
    if (!chunk) {
      continue
    }

    if (!isSse) {
      rawBuffer += chunk
      const { events, remaining } = extractJsonPayloads(rawBuffer)
      rawBuffer = remaining
      for (const event of events) {
        const shouldContinue = await applyStreamEvent(messageId, event)
        if (!shouldContinue) {
          await reader.cancel()
          return
        }
      }
      continue
    }

    sseBuffer += chunk
    const blocks = sseBuffer.split(/\r?\n\r?\n/)
    sseBuffer = blocks.pop() || ''
    for (const block of blocks) {
      const data = parseSseEvent(block)
      for (const event of normalizeStreamPayload(data)) {
        const shouldContinue = await applyStreamEvent(messageId, event)
        if (!shouldContinue) {
          await reader.cancel()
          return
        }
      }
    }
  }

  if (!isSse && rawBuffer.trim()) {
    for (const event of normalizeStreamPayload(rawBuffer)) {
      const shouldContinue = await applyStreamEvent(messageId, event)
      if (!shouldContinue) {
        await reader.cancel()
        return
      }
    }
  }

  if (isSse && sseBuffer.trim()) {
    const data = parseSseEvent(sseBuffer)
    for (const event of normalizeStreamPayload(data)) {
      const shouldContinue = await applyStreamEvent(messageId, event)
      if (!shouldContinue) {
        await reader.cancel()
        return
      }
    }
  }
}

const buildStreamError = async (response: Response) => {
  const raw = await response.text()
  if (!raw) {
    return `请求失败 (${response.status})`
  }
  try {
    const parsed = JSON.parse(raw)
    return parsed.message || raw
  } catch {
    return raw
  }
}

const loadOptions = async () => {
  optionsLoading.value = true
  try {
    const [instanceResp, agentResp] = await Promise.all([
      aiInstanceApi.queryPage({
        pageNo: 1,
        pageSize: 200,
        param: { status: 'enabled', modelType: 'chat' }
      }),
      aiAgentApi.queryPage({
        pageNo: 1,
        pageSize: 100,
        param: { status: 'enabled' }
      })
    ])

    chatInstanceOptions.value = instanceResp.list || []
    agentOptions.value = agentResp.list || []

    console.log('Agent options loaded:', agentOptions.value)
    console.log('Chat instance options loaded:', chatInstanceOptions.value)

    if (!selectedAgent.value || !agentOptions.value.some((item) => item.agentKey === selectedAgent.value)) {
      selectedAgent.value = agentOptions.value[0]?.agentKey || undefined
    }
    if (
      !selectedChatInstanceKey.value ||
      !chatInstanceOptions.value.some((item) => item.instanceKey === selectedChatInstanceKey.value)
    ) {
      selectedChatInstanceKey.value = getDefaultChatInstanceKey(selectedAgent.value)
    }
  } catch (error: any) {
    console.error('Error loading options:', error)
    message.error(error?.message || '加载聊天配置失败')
  } finally {
    optionsLoading.value = false
  }
}

watch(selectedAgent, (agentKey, previousAgentKey) => {
  if (agentKey && agentKey !== previousAgentKey) {
    syncChatInstanceWithAgent(agentKey)
  }
})

const submitQuestion = async (promptArg?: string) => {
  const prompt = (typeof promptArg === 'string' ? promptArg : userInput.value).trim()
  const currentFileUrlList = [...fileUrlList.value]
  if ((!prompt && currentFileUrlList.length === 0) || !selectedChatInstanceKey.value || !selectedAgent.value || isStreaming.value) {
    return
  }
  userInput.value = ''
  fileUrlList.value = []

  const userMessageId = `user-${Date.now()}`
  const assistantMessageId = `ai-${Date.now()}`
  messages.value.push({ id: userMessageId, role: 'user', content: prompt })
  messages.value.push({
    id: assistantMessageId,
    role: 'ai',
    content: '',
    segments: [],
    streaming: true
  })
  isStreaming.value = true
  await scrollToBottom()

  const token = localStorage.getItem('token')
  const workspaceEnv = localStorage.getItem(WORKSPACE_ENV_STORAGE_KEY)
  abortController = new AbortController()

  try {
    const response = await fetch('/v1/astro/chat/stream', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'text/event-stream',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...(workspaceEnv ? { [WORKSPACE_ENV_HEADER]: workspaceEnv.trim() } : {})
      },
      body: JSON.stringify({
        agentKey: selectedAgent.value,
        instanceKey: selectedChatInstanceKey.value,
        memoryKey: getMemoryKey(),
        userMessage: prompt,
        enableDeepThinking: isDeepThinking.value,
        enableNetwork: isWebSearch.value,
        enableStream: true,
        fileUrlList: currentFileUrlList
      }),
      signal: abortController.signal
    })

    if (!response.ok) {
      if (response.status === 401) {
        localStorage.removeItem('token')
        window.location.href = '/login'
        return
      }
      throw new Error(await buildStreamError(response))
    }

    await readStreamText(response, assistantMessageId)
    const target = messages.value.find((item) => item.id === assistantMessageId)
    if (target && !target.content) {
      target.content = '本次没有返回内容。'
    }
  } catch (error: any) {
    const target = messages.value.find((item) => item.id === assistantMessageId)
    if (target) {
      if (error?.name === 'AbortError') {
        const stopText = target.content || '已停止生成'
        target.content = stopText
        if (!target.segments?.length) {
          target.segments = [{ type: 'text', content: stopText }]
        }
      } else {
        // 解析错误响应（须同步 segments，否则 ChatMessageItem 在 segments 存在时会忽略 content）
        let errorMessage = '请求失败，请稍后重试'
        let errorDetail = ''

        if (error?.response?.data) {
          const errorData = error.response.data
          errorMessage = errorData.message || errorMessage
          if (errorData.rootCause) {
            errorDetail = errorData.rootCause
          }
        } else if (error?.data) {
          errorMessage = error.data.message || error.message || errorMessage
          if (error.data.rootCause) {
            errorDetail = error.data.rootCause
          }
        } else {
          errorMessage = error?.message || errorMessage
        }

        const fullText = errorDetail ? `${errorMessage}\n\n原因：${errorDetail}` : errorMessage
        target.content = fullText
        target.segments = [{ type: 'text', content: fullText }]
        target.error = true
      }
    }
    if (error?.name !== 'AbortError') {
      // 解析错误消息用于弹窗
      let errorMessage = '聊天请求失败'
      if (error?.response?.data?.message) {
        errorMessage = error.response.data.message
      } else if (error?.data?.message) {
        errorMessage = error.data.message
      } else if (error?.message) {
        errorMessage = error.message
      }
      message.error(errorMessage)
    }
  } finally {
    const target = messages.value.find((item) => item.id === assistantMessageId)
    if (target) {
      target.streaming = false
    }
    isStreaming.value = false
    abortController = null
    await loadSessionGroups()
    await scrollToBottom()
  }
}

const stopStreaming = () => {
  abortController?.abort()
}

onMounted(async () => {
  const memoryKey = getMemoryKey()
  restoreDraftState(memoryKey)
  await loadOptions()
  await loadSessionGroups()
  if (currentMemoryKey.value && sessionItems.value.some((item) => item.memoryKey === currentMemoryKey.value)) {
    await openSession(currentMemoryKey.value)
  } else {
    resetWelcomeMessage()
  }
  await scrollToBottom()
})

onBeforeUnmount(() => {
  abortController?.abort()
})

watch(
  [userInput, fileUrlList, isDeepThinking, isWebSearch, selectedAgent, selectedChatInstanceKey],
  () => {
    if (!currentMemoryKey.value) return
    saveDraftState(currentMemoryKey.value)
  },
  { deep: true }
)
</script>

<style scoped>
/* 容器与背景 */
.chat-home {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-base);
  background-image: 
    radial-gradient(circle at 50% -20%, rgba(59, 130, 246, 0.08), transparent 50%),
    radial-gradient(circle at 0% 100%, rgba(16, 185, 129, 0.05), transparent 40%);
  color: var(--text-primary);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 聊天主体 */
.chat-main {
  flex: 1;
  position: relative;
  display: flex;
  flex-direction: row;
  overflow: hidden;
}

.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  position: relative;
}

.new-session-stage {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 24px 0;
  transform: translateY(-28px);
}

.new-session-intro {
  text-align: center;
  margin: 0 auto 18px;
  max-width: 640px;
  padding: 0 24px;
}

.new-session-intro h2 {
  margin: 0 0 8px;
  font-size: 30px;
  font-weight: 700;
}

.new-session-intro p {
  margin: 0;
  color: var(--text-secondary);
  font-size: 14px;
}

.chat-messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 40px 20px 176px;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.chat-messages-container::-webkit-scrollbar {
  display: none;
}

.message-scroll-area {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.messages-bottom-spacer {
  height: 176px;
  flex-shrink: 0;
}

.message-fade-enter-active {
  transition: all 0.3s ease;
}

.message-fade-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.message-fade-leave-active {
  transition: all 0.2s ease;
}

.message-fade-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

.message-fade-move {
  transition: transform 0.3s ease;
}

/* 响应式调整 */
@media (max-width: 640px) {
  .chat-main {
    flex-direction: column;
  }
  .top-bar { padding: 0 16px; }
  .brand-name { display: none; }
}
</style>