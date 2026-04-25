<template>
  <span style="display: none"></span>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { notification } from 'ant-design-vue'
import { WORKSPACE_ENV_STORAGE_KEY } from '@/constants/workspaceEnv'

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
  const title = payload.title || '系统消息'
  const description = [payload.content, payload.source ? `来源：${payload.source}` : '', payload.envCode ? `环境：${payload.envCode}` : '']
    .filter(Boolean)
    .join(' | ')

  notification[type]({
    message: title,
    description: description || '收到一条系统通知',
    placement: 'topRight',
    duration: 6
  })
}

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
