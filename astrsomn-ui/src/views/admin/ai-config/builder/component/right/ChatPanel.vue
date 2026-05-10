<template>
  <div class="chat-panel">
    <div ref="scrollRef" class="chat-messages custom-scrollbar">
      <AstroChatMessage
        v-for="item in messages"
        :key="item.id"
        :role="item.role"
        :content="item.content"
        :segments="item.segments"
        :streaming="item.streaming"
        :error="item.error"
        compact
      />
      <div ref="bottomRef" class="messages-bottom-spacer" />
    </div>

    <div class="chat-composer-wrap">
      <AstroChatComposer
        variant="full"
        layout="embedded"
        density="compact"
        :model-value="draft"
        placeholder="发送指令测试 Agent…"
        :disabled="isStreaming"
        :is-streaming="isStreaming"
        :send-disabled="sendDisabled"
        :show-char-count="false"
        @update:model-value="draft = $event"
        @submit="onSubmit"
        @stop="stopStreaming"
      >
        <template #footer-left>
          <div class="feature-switches">
            <div
              class="feature-tag"
              :class="{ active: isDeepThinking }"
              @click="isDeepThinking = !isDeepThinking"
            >
              <BulbOutlined /> 深度思考
            </div>
            <div
              class="feature-tag"
              :class="{ active: isWebSearch }"
              @click="isWebSearch = !isWebSearch"
            >
              <GlobalOutlined /> 联网
            </div>
          </div>
        </template>
      </AstroChatComposer>
    </div>
  </div>
</template>

<script setup lang="ts">
import { BulbOutlined, GlobalOutlined } from '@ant-design/icons-vue'
import { AstroChatComposer, AstroChatMessage } from '@astrsomn/astro-chat-vue'
import { readAstroStream, buildStreamError, type StreamEvent } from '@astrsomn/astro-chat-core'
import { computed, inject, nextTick, onBeforeUnmount, ref } from 'vue'
import { message } from 'ant-design-vue'
import { BUILDER_CHAT_CONTEXT } from '../builderChatInjection'
import { buildAstroBuilderChatRequest } from '../buildAstroBuilderChatRequest'
import { WORKSPACE_ENV_HEADER, WORKSPACE_ENV_STORAGE_KEY } from '@/constants/workspaceEnv.ts'

type ChatSegmentType = 'text' | 'thought' | 'html'

type ChatSegment = {
  type: ChatSegmentType
  content: string
}

type ChatMessage = {
  id: string
  role: 'user' | 'ai'
  content: string
  segments?: ChatSegment[]
  streaming?: boolean
  error?: boolean
}

const ctx = inject(BUILDER_CHAT_CONTEXT)
if (!ctx) {
  throw new Error('BUILDER_CHAT_CONTEXT missing — wrap builder under Index.vue')
}

const STREAM_URL = '/v1/astro/chat/builder/stream'

const messages = ref<ChatMessage[]>([])
const draft = ref('')
const isStreaming = ref(false)
const isDeepThinking = ref(false)
const isWebSearch = ref(false)
const scrollRef = ref<HTMLElement | null>(null)
const bottomRef = ref<HTMLElement | null>(null)
let abortController: AbortController | null = null

const sendDisabled = computed(() => {
  if (isStreaming.value) return false
  return !draft.value.trim()
})

const mergeMessageContent = (segments: ChatSegment[]) =>
  segments
    .map((segment) => (segment.type === 'thought' ? `[思考]\n${segment.content}` : segment.content))
    .join('\n')

const scrollToBottom = async () => {
  await nextTick()
  bottomRef.value?.scrollIntoView({ block: 'end' })
}

const appendAssistantContent = async (
  messageId: string,
  chunk: string,
  type: ChatSegmentType = 'text'
) => {
  if (!chunk) return
  const target = messages.value.find((item) => item.id === messageId)
  if (!target || target.role !== 'ai') return
  if (!target.segments) target.segments = []
  const last = target.segments[target.segments.length - 1]
  if (last && last.type === type) {
    last.content += chunk
  } else {
    target.segments.push({ type, content: chunk })
  }
  target.content = mergeMessageContent(target.segments)
  await scrollToBottom()
}

const applyStreamEvent = async (messageId: string, event: StreamEvent): Promise<boolean> => {
  const target = messages.value.find((item) => item.id === messageId)
  if (!target) return true
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

const stopStreaming = () => {
  abortController?.abort()
}

const onSubmit = async () => {
  const prompt = draft.value.trim()
  if (!prompt || isStreaming.value) return

  let body: Record<string, unknown>
  try {
    body = buildAstroBuilderChatRequest(ctx.snapshot.value, {
      userMessage: prompt,
      memoryKey: ctx.memoryKey.value,
      enableDeepThinking: isDeepThinking.value,
      enableNetwork: isWebSearch.value
    })
  } catch (e: unknown) {
    message.warning((e as Error)?.message || '无法发起预览')
    return
  }

  draft.value = ''
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
    const response = await fetch(STREAM_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'text/event-stream',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...(workspaceEnv ? { [WORKSPACE_ENV_HEADER]: workspaceEnv.trim() } : {})
      },
      body: JSON.stringify(body),
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

    await readAstroStream(response, (event) => applyStreamEvent(assistantMessageId, event))
    const target = messages.value.find((item) => item.id === assistantMessageId)
    if (target && !target.content) {
      target.content = '本次没有返回内容。'
    }
  } catch (error: unknown) {
    const target = messages.value.find((item) => item.id === assistantMessageId)
    if (target) {
      if ((error as { name?: string })?.name === 'AbortError') {
        const stopText = target.content || '已停止生成'
        target.content = stopText
        target.segments = [{ type: 'text', content: stopText }]
      } else {
        const msg = (error as Error)?.message || '请求失败'
        target.content = msg
        target.segments = [{ type: 'text', content: msg }]
        target.error = true
      }
    }
    if ((error as { name?: string })?.name !== 'AbortError') {
      message.error((error as Error)?.message || '预览请求失败')
    }
  } finally {
    const target = messages.value.find((item) => item.id === assistantMessageId)
    if (target) target.streaming = false
    isStreaming.value = false
    abortController = null
    await scrollToBottom()
  }
}

onBeforeUnmount(() => {
  abortController?.abort()
})
</script>

<style scoped>
.chat-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}

.chat-messages {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 16px 16px 8px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  background: transparent;
}

.messages-bottom-spacer {
  height: 8px;
  flex-shrink: 0;
}

.chat-composer-wrap {
  flex-shrink: 0;
  padding: 12px 16px 16px;
  border-top: 1px solid color-mix(in srgb, var(--border-default, #e2e8f0) 50%, transparent);
  background: transparent;
}

.feature-switches {
  display: flex;
  gap: 8px;
}

.feature-tag {
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 12px;
  background: var(--bg-input, #f1f5f9);
  border: 1px solid var(--border-default, #e2e8f0);
  color: var(--text-muted, #64748b);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;
  user-select: none;
}

.feature-tag:hover {
  border-color: var(--text-muted, #64748b);
}

.feature-tag.active {
  background: var(--primary-hover, rgba(59, 130, 246, 0.12));
  border-color: var(--primary, #3b82f6);
  color: var(--primary, #3b82f6);
}

.custom-scrollbar::-webkit-scrollbar {
  width: 5px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: var(--border-default, #e2e8f0);
  border-radius: 10px;
}
</style>
