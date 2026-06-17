<template>
  <button class="notice-test-btn" title="测试系统通知" @click="fireTestNotice">
    <BellOutlined />
  </button>
</template>

<script lang="ts" setup>
import { onBeforeUnmount, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { notification } from 'ant-design-vue'
import { BellOutlined } from '@ant-design/icons-vue'
import { WORKSPACE_ENV_STORAGE_KEY } from '@/constants/workspaceEnv.ts'
import { usePageTranslation } from '@/locales/pages.ts'

type SystemMessagePushPayload = {
  id?: number | string
  messageType?: string
  messageLevel?: string
  title?: string
  content?: string
  source?: string
  createTime?: string
  envCode?: string
}

const route = useRoute()
const t = usePageTranslation('common')
let source: EventSource | null = null
let reconnectTimer: ReturnType<typeof setTimeout> | null = null
let reconnectAttempt = 0
const seenIds = new Set<string>()

const levelToNoticeType = (level?: string) => {
  if (level === 'SUCCESS') return 'success'
  if (level === 'WARN') return 'warning'
  if (level === 'ERROR') return 'error'
  return 'info'
}

const parsePayload = (raw: string) => {
  try {
    return JSON.parse(raw) as SystemMessagePushPayload
  } catch {
    return null
  }
}

const noticeKey = (p: SystemMessagePushPayload) => {
  if (p.id != null) return `system-message-${String(p.id)}`
  return `system-message-${p.title || 'untitled'}-${p.createTime || Date.now()}`
}

const openNotice = (payload: SystemMessagePushPayload) => {
  const key = noticeKey(payload)
  if (seenIds.has(key)) return
  seenIds.add(key)
  if (seenIds.size > 200) {
    const first = seenIds.values().next().value
    if (first) seenIds.delete(first)
  }

  const type = levelToNoticeType(payload.messageLevel)
  const title = payload.title || t.value.systemMessage.defaultTitle
  const description = [payload.content, payload.source ? t.value.systemMessage.source + '：' + payload.source : '', payload.envCode ? t.value.systemMessage.env + '：' + payload.envCode : '']
      .filter(Boolean)
      .join(' | ')

  notification[type]({
    message: title,
    description: description || t.value.systemMessage.defaultDescription,
    placement: 'topRight',
    duration: 6
  })
}

// ---- test notice ----
const fireTestNotice = () => {
  const levels = ['SUCCESS', 'INFO', 'WARN', 'ERROR'] as const
  const randomLevel = levels[Math.floor(Math.random() * levels.length)]
  openNotice({
    id: `test-${Date.now()}`,
    messageType: 'SYSTEM_NOTICE',
    messageLevel: randomLevel,
    title: `[测试] ${levelLabel(randomLevel)}通知`,
    content: `这是一条 ${levelLabel(randomLevel)} 级别的测试通知，用于验证系统通知样式。`,
    source: 'TEST',
    createTime: new Date().toISOString(),
    envCode: localStorage.getItem(WORKSPACE_ENV_STORAGE_KEY) || undefined
  })
}

const levelLabel = (level: string) => {
  if (level === 'SUCCESS') return '成功'
  if (level === 'WARN') return '警告'
  if (level === 'ERROR') return '错误'
  return '普通'
}

// ---- SSE ----
const cleanupSource = () => {
  if (source) {
    source.close()
    source = null
  }
}

const clearReconnect = () => {
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }
}

const shouldConnect = () => {
  const token = localStorage.getItem('token')
  if (!token) return false
  return route.path.startsWith('/admin')
}

const connect = () => {
  clearReconnect()
  cleanupSource()

  const token = localStorage.getItem('token')
  if (!token) return

  const envCode = localStorage.getItem(WORKSPACE_ENV_STORAGE_KEY)
  const query = new URLSearchParams({ token })
  if (envCode) query.set('envCode', envCode)

  source = new EventSource(`/v1/astro/sse/system-message?${query.toString()}`)

  source.addEventListener('SYSTEM_MESSAGE', (event) => {
    const payload = parsePayload((event as MessageEvent).data)
    if (!payload) return
    openNotice(payload)
  })

  source.onerror = () => {
    cleanupSource()
    reconnectAttempt += 1
    const backoff = Math.min(20000, 1000 * 2 ** Math.min(reconnectAttempt, 5))
    reconnectTimer = setTimeout(() => {
      if (shouldConnect()) connect()
    }, backoff)
  }

  source.onopen = () => {
    reconnectAttempt = 0
  }
}

const refreshConnection = () => {
  if (!shouldConnect()) {
    clearReconnect()
    cleanupSource()
    return
  }
  connect()
}

onMounted(() => {
  refreshConnection()
})

watch(
    () => route.path,
    () => {
      refreshConnection()
    }
)

onBeforeUnmount(() => {
  clearReconnect()
  cleanupSource()
})
</script>

<style scoped>
.notice-test-btn {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 9999;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: 1px solid var(--border-default, #e2e8f0);
  background: var(--bg-card, #fff);
  color: var(--text-secondary, #64748b);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  box-shadow: var(--shadow-card, 0 4px 12px rgba(0, 0, 0, 0.1));
  transition: all 0.25s ease;
}

.notice-test-btn:hover {
  color: var(--primary, #3b82f6);
  border-color: var(--primary, #3b82f6);
  box-shadow: 0 4px 16px color-mix(in srgb, var(--primary) 30%, transparent);
  transform: translateY(-2px);
}
</style>
