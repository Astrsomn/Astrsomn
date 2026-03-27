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
      <div ref="messagesContainerRef" class="chat-messages-container">
        <div class="message-scroll-area">
          <transition-group name="message-fade">
            <div
              v-for="item in messages"
              :key="item.id"
              class="message"
              :class="[item.role, { error: item.error }]"
            >
              <div v-if="item.role === 'ai'" class="avatar-mini">A</div>
              <div class="content">
                <div v-if="item.content" class="message-text">{{ item.content }}</div>
                <div v-else-if="item.streaming" class="typing-placeholder">正在思考中...</div>
              </div>
            </div>
          </transition-group>
          <div ref="messagesBottomRef" class="messages-bottom-spacer"></div>
        </div>
      </div>

      <div class="chat-input-section">
        <transition name="input-slide" appear>
          <div class="input-panel">
            <div class="input-toolbar">
              <div class="toolbar-left">
                <a-select
                  v-model:value="selectedAgent"
                  class="panel-select"
                  placeholder="选择 Agent"
                  :bordered="false"
                  :loading="optionsLoading"
                >
                  <a-select-option
                    v-for="agent in agentOptions"
                    :key="agent.agentKey"
                    :value="agent.agentKey"
                  >
                    {{ agent.agentName || agent.agentKey }}
                  </a-select-option>
                </a-select>

                <div class="v-divider"></div>

                <a-select
                  v-model:value="selectedModel"
                  class="panel-select"
                  placeholder="选择模型"
                  :bordered="false"
                  dropdown-class-name="custom-dropdown"
                  :loading="optionsLoading"
                >
                  <a-select-option
                    v-for="model in modelOptions"
                    :key="model.modelKey"
                    :value="model.modelKey"
                  >
                    {{ model.modelName || model.modelKey }}
                  </a-select-option>
                </a-select>
              </div>
            </div>

            <div class="input-body">
              <a-textarea
                v-model:value="userInput"
                :auto-size="{ minRows: 1, maxRows: 6 }"
                placeholder="问点什么吧..."
                class="main-textarea"
                @pressEnter="handleEnter"
                :disabled="isStreaming"
              />
            </div>

            <div class="input-footer">
              <div class="footer-left">
                <a-upload :show-upload-list="false" class="upload-trigger">
                  <button class="icon-btn" title="上传文件">
                    <paper-clip-outlined />
                  </button>
                </a-upload>
                
                <div class="feature-switches">
                  <div 
                    class="feature-tag" 
                    :class="{ active: isDeepThinking }"
                    @click="isDeepThinking = !isDeepThinking"
                  >
                    <bulb-outlined /> 深度思考
                  </div>
                  <div 
                    class="feature-tag" 
                    :class="{ active: isWebSearch }"
                    @click="isWebSearch = !isWebSearch"
                  >
                    <global-outlined /> 联网搜索
                  </div>
                </div>
              </div>
              
              <div class="footer-right">
                <div class="char-count" v-if="userInput.length > 0">
                  {{ userInput.length }}
                </div>
                <div v-else-if="isStreaming" class="stream-status">流式回复中</div>
                <a-button 
                  type="primary" 
                  class="send-btn" 
                  :disabled="sendDisabled"
                  @click="isStreaming ? stopStreaming() : submitQuestion()"
                >
                  <template #icon>
                    <stop-outlined v-if="isStreaming" />
                    <arrow-up-outlined v-else />
                  </template>
                </a-button>
              </div>
            </div>
          </div>
        </transition>
        <p class="input-hint">Astrsomn 可能产生错误信息，请核查重要内容。</p>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import {
  PaperClipOutlined,
  BulbOutlined,
  GlobalOutlined,
  ArrowUpOutlined,
  StopOutlined
} from '@ant-design/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
import { aiModelApi, type AiModel } from '@/api/aiModel'
import { aiAgentApi, type AiAgent } from '@/api/aiAgent'
import { WORKSPACE_ENV_HEADER, WORKSPACE_ENV_STORAGE_KEY } from '@/constants/workspaceEnv'

type ChatMessage = {
  id: string
  role: 'user' | 'ai'
  content: string
  streaming?: boolean
  error?: boolean
}

type StreamEventType = 'text' | 'thought' | 'html' | 'error' | 'done'

type StreamEvent = {
  type: StreamEventType
  content: string
}

const CHAT_MEMORY_KEY = 'astrsomn-chat-memory-key'

const userInput = ref('')
const selectedModel = ref<string>()
const selectedAgent = ref<string>()
const isDeepThinking = ref(false)
const isWebSearch = ref(false)
const isStreaming = ref(false)
const optionsLoading = ref(false)
const modelOptions = ref<AiModel[]>([])
const agentOptions = ref<AiAgent[]>([])
const messagesContainerRef = ref<HTMLElement | null>(null)
const messagesBottomRef = ref<HTMLElement | null>(null)
const messages = ref<ChatMessage[]>([
  {
    id: 'welcome',
    role: 'ai',
    content: '你好，我是 Astrsomn AI 助手。今天有什么我可以帮你的？'
  }
])

let abortController: AbortController | null = null

const sendDisabled = computed(() => {
  if (isStreaming.value) {
    return false
  }
  return !userInput.value.trim() || !selectedModel.value || !selectedAgent.value
})

const getAgentPreferredModelKey = (agentKey?: string) => {
  if (!agentKey) {
    return undefined
  }
  const agent = agentOptions.value.find((item) => item.agentKey === agentKey)
  const modelKey = agent?.modelKey == null ? undefined : String(agent.modelKey)
  if (!modelKey) {
    return undefined
  }
  return modelOptions.value.some((item) => item.modelKey === modelKey) ? modelKey : undefined
}

const syncModelWithAgent = (agentKey?: string) => {
  const preferredModelKey = getAgentPreferredModelKey(agentKey)
  if (preferredModelKey) {
    selectedModel.value = preferredModelKey
  }
}

const handleEnter = (e: KeyboardEvent) => {
  if (!e.shiftKey) {
    e.preventDefault()
    if (!isStreaming.value) {
      submitQuestion()
    }
  }
}

const getMemoryKey = () => {
  let memoryKey = sessionStorage.getItem(CHAT_MEMORY_KEY)
  if (!memoryKey) {
    memoryKey = `web:${Date.now()}`
    sessionStorage.setItem(CHAT_MEMORY_KEY, memoryKey)
  }
  return memoryKey
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

const appendAssistantContent = async (messageId: string, chunk: string) => {
  if (!chunk) {
    return
  }
  const target = messages.value.find((item) => item.id === messageId)
  if (!target) {
    return
  }
  target.content += chunk
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
    case 'html':
      await appendAssistantContent(messageId, event.content)
      return true
    case 'error':
      target.error = true
      await appendAssistantContent(messageId, event.content || '流式响应异常')
      return false
    case 'thought':
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
    const [modelResp, agentResp] = await Promise.all([
      aiModelApi.queryPage({
        pageNo: 1,
        pageSize: 100,
        param: { status: 'enabled' }
      }),
      aiAgentApi.queryPage({
        pageNo: 1,
        pageSize: 100,
        param: { status: 'enabled' }
      })
    ])

    modelOptions.value = modelResp.list || []
    agentOptions.value = agentResp.list || []

    if (!selectedAgent.value) {
      selectedAgent.value = agentOptions.value[0]?.agentKey || undefined
    }
    syncModelWithAgent(selectedAgent.value)
    if (!selectedModel.value) {
      selectedModel.value =
        modelOptions.value.find((item) => item.isDefault === 1)?.modelKey ||
        modelOptions.value[0]?.modelKey ||
        undefined
    }
  } catch (error: any) {
    message.error(error?.message || '加载聊天配置失败')
  } finally {
    optionsLoading.value = false
  }
}

watch(selectedAgent, (agentKey, previousAgentKey) => {
  if (agentKey && agentKey !== previousAgentKey) {
    syncModelWithAgent(agentKey)
  }
})

const submitQuestion = async () => {
  const prompt = userInput.value.trim()
  if (!prompt || !selectedModel.value || !selectedAgent.value || isStreaming.value) {
    return
  }

  const userMessageId = `user-${Date.now()}`
  const assistantMessageId = `ai-${Date.now()}`
  messages.value.push({ id: userMessageId, role: 'user', content: prompt })
  messages.value.push({ id: assistantMessageId, role: 'ai', content: '', streaming: true })
  userInput.value = ''
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
        modelKey: selectedModel.value,
        memoryKey: getMemoryKey(),
        userMessage: prompt,
        enableNetwork: isWebSearch.value,
        enableStream: true,
        fileUrlList: []
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
        target.content = target.content || '已停止生成'
      } else {
        target.content = error?.message || '请求失败，请稍后重试'
        target.error = true
      }
    }
    if (error?.name !== 'AbortError') {
      message.error(error?.message || '聊天请求失败')
    }
  } finally {
    const target = messages.value.find((item) => item.id === assistantMessageId)
    if (target) {
      target.streaming = false
    }
    isStreaming.value = false
    abortController = null
    await scrollToBottom()
  }
}

const stopStreaming = () => {
  abortController?.abort()
}

onMounted(async () => {
  await loadOptions()
  await scrollToBottom()
})

onBeforeUnmount(() => {
  abortController?.abort()
})
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
  flex-direction: column;
  overflow: hidden;
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

.message {
  display: flex;
  gap: 16px;
  max-width: 85%;
}

.message.ai {
  align-self: flex-start;
}

.message.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.avatar-mini {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: var(--bg-elevated);
  border: 1px solid var(--border-default);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  flex-shrink: 0;
}

.message .content {
  padding: 12px 18px;
  border-radius: 18px;
  line-height: 1.6;
  font-size: 15px;
}

.message.ai .content {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  color: var(--text-primary);
  border-top-left-radius: 4px;
}

.message.user .content {
  background: var(--primary);
  color: white;
  border-top-right-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.2);
}

.message.error .content {
  border-color: rgba(255, 77, 79, 0.35);
  color: #ff7875;
}

.message-text {
  white-space: pre-wrap;
  word-break: break-word;
}

.typing-placeholder {
  color: var(--text-secondary);
}

/* 高级输入框面板 */
.chat-input-section {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 2;
  pointer-events: none;
  padding: 20px 20px 30px;
  background: transparent;
}

.input-panel {
  max-width: 840px;
  margin: 0 auto;
  pointer-events: auto;
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: 24px;
  box-shadow: var(--shadow-card);
  transition: border-color 0.3s, box-shadow 0.3s;
  overflow: hidden;
}

.input-panel:focus-within {
  border-color: var(--primary);
  box-shadow: 0 0 0 4px var(--primary-hover);
}

/* 输入框内部工具栏 */
.input-toolbar {
  padding: 12px 16px 4px;
  display: flex;
  justify-content: space-between;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.v-divider {
  width: 1px;
  height: 18px;
  background: var(--border-default);
}

.panel-select {
  min-width: 100px;
  font-size: 13px;
  font-weight: 500;
}

.panel-select :deep(.ant-select-selection-item) {
  color: var(--text-secondary) !important;
}

/* 输入主体 */
.input-body {
  padding: 4px 16px;
}

.main-textarea {
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
  font-size: 16px;
  color: var(--text-primary);
  padding: 8px 0;
  resize: none;
}

/* 输入框底部 */
.input-footer {
  padding: 8px 16px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.icon-btn {
  background: var(--bg-input);
  border: 1px solid var(--border-default);
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--text-secondary);
  transition: all 0.2s;
}

.icon-btn:hover {
  background: var(--border-subtle);
  color: var(--primary);
}

.feature-switches {
  display: flex;
  gap: 8px;
}

.feature-tag {
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 12px;
  background: var(--bg-input);
  border: 1px solid var(--border-default);
  color: var(--text-muted);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;
  user-select: none;
}

.feature-tag:hover {
  border-color: var(--text-muted);
}

.feature-tag.active {
  background: var(--primary-hover);
  border-color: var(--primary);
  color: var(--primary);
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.char-count {
  font-size: 12px;
  color: var(--text-muted);
}

.stream-status {
  font-size: 12px;
  color: var(--primary);
}

.send-btn {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}

.input-hint {
  pointer-events: auto;
  text-align: center;
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 12px;
}

.input-slide-enter-active {
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.input-slide-enter-from {
  opacity: 0;
  transform: translateY(100%);
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
  .feature-tag span { display: none; }
  .top-bar { padding: 0 16px; }
  .brand-name { display: none; }
}
</style>