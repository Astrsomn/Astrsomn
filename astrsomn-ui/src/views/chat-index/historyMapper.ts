import type { AiChatMessageRow, AiChatTurnBundle } from '@/api/aiConversation'

export type ChatSegmentType = 'text' | 'thought' | 'html' | 'image' | 'tool'

type ChatSegmentBase = {
  type: ChatSegmentType
  content: string
}

export type ChatSegmentThought = ChatSegmentBase & {
  type: 'thought'
  title?: string
}

export type ChatSegmentTool = ChatSegmentBase & {
  type: 'tool'
  toolName?: string
  args?: string
  result?: string
  title?: string
  toolCallId?: string
  relatedToolCallId?: string
}

export type ChatSegment = ChatSegmentBase | ChatSegmentThought | ChatSegmentTool

export type ChatMessage = {
  id: string
  role: 'user' | 'ai'
  content: string
  segments?: ChatSegment[]
  streaming?: boolean
  error?: boolean
}

type ExtPayload = {
  partKind?: string
  lineage?: string
  toolName?: string
  arguments?: Record<string, unknown>
  toolCallId?: string
  relatedToolCallId?: string
  attachments?: Array<{ kind?: string; url?: string; mimeType?: string; name?: string }>
}

function parseExtJson(raw?: string | null): ExtPayload | null {
  if (raw == null || String(raw).trim() === '') return null
  try {
    return JSON.parse(raw) as ExtPayload
  } catch {
    return null
  }
}

function escapeHtml(text: string): string {
  return text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}

function isSafeHttpUrl(url: string): boolean {
  try {
    const u = new URL(url)
    return u.protocol === 'http:' || u.protocol === 'https:'
  } catch {
    return false
  }
}

function buildUserBubbleHtml(text: string, row: AiChatMessageRow): string {
  const ext = parseExtJson(row.extJson)
  const attachments = ext?.attachments
  let html = escapeHtml(text || '')
  if (Array.isArray(attachments)) {
    for (const att of attachments) {
      const url = typeof att?.url === 'string' ? att.url.trim() : ''
      const kind = typeof att?.kind === 'string' ? att.kind.toLowerCase() : ''
      if (!url || !isSafeHttpUrl(url)) continue
      if (kind === 'image' || kind === '' || kind === 'file') {
        if (kind === 'image' || kind === '') {
          html += `<br><img src="${escapeHtml(url)}" alt="" style="max-width:100%;border-radius:8px;margin-top:8px;display:block;" />`
        } else {
          html += `<br><a href="${escapeHtml(url)}" target="_blank" rel="noopener noreferrer" class="user-attachment-link">${escapeHtml(
            att.name || '附件'
          )}</a>`
        }
      }
    }
  }
  return html
}

function formatToolArgs(ext: ExtPayload | null): string {
  let args = ''
  try {
    args = ext?.arguments != null ? JSON.stringify(ext.arguments, null, 2) : ''
  } catch {
    args = String(ext?.arguments ?? '')
  }
  const cap = args.length > 4000 ? `${args.slice(0, 4000)}\n…` : args
  return cap
}

function rowToSegment(row: AiChatMessageRow): ChatSegment | null {
  const ext = parseExtJson(row.extJson)
  const partKind = ext?.partKind?.toLowerCase() ?? ''
  const mt = row.messageType?.toLowerCase() ?? ''
  const role = row.role?.toLowerCase() ?? ''
  const body = row.content ?? ''

  if (mt === 'reasoning' || partKind === 'thought') {
    return { type: 'thought', content: body, title: '推理分析' }
  }
  if (mt === 'html' || partKind === 'html') {
    return { type: 'html', content: body }
  }
  if (mt === 'image' || partKind === 'image') {
    return { type: 'image', content: body }
  }
  if (mt === 'tool_call') {
    const name = ext?.toolName || 'tool'
    const cap = formatToolArgs(ext)
    return {
      type: 'tool',
      content: '',
      toolName: name,
      args: cap,
      result: '',
      title: `调用工具: ${name}`,
      toolCallId: ext?.toolCallId
    }
  }
  if (mt === 'tool_result' || role === 'tool') {
    const name = ext?.toolName || 'tool'
    const cap = body.length > 12000 ? `${body.slice(0, 12000)}\n…` : body
    return {
      type: 'tool',
      content: cap,
      toolName: name,
      result: cap,
      title: `调用工具: ${name}`,
      relatedToolCallId: ext?.relatedToolCallId
    }
  }
  if (mt === 'error') {
    return { type: 'text', content: body }
  }
  if (body === '' && mt === 'text') {
    return null
  }
  return { type: 'text', content: body }
}

function isToolSegment(s: ChatSegment): s is ChatSegmentTool {
  return s.type === 'tool'
}

function shouldMergeToolPair(a: ChatSegmentTool, b: ChatSegmentTool): boolean {
  const aRes = (a.result ?? a.content ?? '').trim()
  const bRes = (b.result ?? b.content ?? '').trim()
  const aArgs = (a.args ?? '').trim()

  if (a.toolCallId && b.relatedToolCallId && a.toolCallId === b.relatedToolCallId) {
    return true
  }

  const nameA = a.toolName || ''
  const nameB = b.toolName || ''
  if (nameA && nameB && nameA === nameB && aArgs && !aRes && bRes) {
    return true
  }

  return false
}

function mergeToolPair(a: ChatSegmentTool, b: ChatSegmentTool): ChatSegmentTool {
  const toolName = a.toolName || b.toolName || 'tool'
  const args = (a.args ?? '').trim() || (b.args ?? '').trim() || undefined
  const result = (b.result ?? b.content ?? a.result ?? a.content ?? '').trim()
  return {
    type: 'tool',
    content: result,
    toolName,
    args,
    result,
    title: `调用工具: ${toolName}`,
    toolCallId: a.toolCallId
  }
}

/** 将相邻的 tool_call 与 tool_result（按 id 或同名）合并为一条工具段。 */
export function mergeAdjacentToolSegments(segments: ChatSegment[]): ChatSegment[] {
  const out: ChatSegment[] = []
  for (let i = 0; i < segments.length; i++) {
    const cur = segments[i]
    const next = segments[i + 1]
    if (
      isToolSegment(cur) &&
      next &&
      isToolSegment(next) &&
      shouldMergeToolPair(cur, next)
    ) {
      out.push(mergeToolPair(cur, next))
      i++
      continue
    }
    out.push(cur)
  }
  return out
}

export function mergeContentFromSegments(segments: ChatSegment[]): string {
  return segments
    .map((s) => {
      if (s.type === 'thought') return `[思考]\n${s.content}`
      if (s.type === 'image') return `\n![image](${s.content})\n`
      if (s.type === 'tool' && isToolSegment(s)) {
        const name = s.toolName || 'tool'
        const parts = [`[工具] ${name}`]
        if (s.args?.trim()) parts.push(`参数:\n${s.args}`)
        const res = (s.result ?? s.content ?? '').trim()
        if (res) parts.push(`结果:\n${res}`)
        return parts.join('\n\n')
      }
      return s.content
    })
    .join('\n')
}

/**
 * 将后端按轮分组的记录映射为页面用的 user + ai 消息对。
 */
export function mapTurnBundlesToChatMessages(bundles: AiChatTurnBundle[]): ChatMessage[] {
  if (!bundles?.length) return []
  const sorted = [...bundles].sort((a, b) => (a.turnNo ?? 0) - (b.turnNo ?? 0))
  const out: ChatMessage[] = []

  for (const bundle of sorted) {
    const turn = bundle.turnNo ?? 0
    const rows = [...(bundle.orderedRows ?? [])].sort(
      (a, b) => (a.messageOrder ?? 0) - (b.messageOrder ?? 0)
    )
    const userRow = rows.find((r) => r.role?.toLowerCase() === 'user')
    const replyRows = rows.filter((r) => r.role?.toLowerCase() !== 'user')

    if (userRow) {
      const text = userRow.content ?? ''
      out.push({
        id: `turn-${turn}-user`,
        role: 'user',
        content: buildUserBubbleHtml(text, userRow)
      })
    }

    const segments: ChatSegment[] = []
    for (const row of replyRows) {
      const seg = rowToSegment(row)
      if (seg) segments.push(seg)
    }

    const mergedSegments = mergeAdjacentToolSegments(segments)

    if (replyRows.length > 0) {
      out.push({
        id: `turn-${turn}-ai`,
        role: 'ai',
        content: mergeContentFromSegments(mergedSegments),
        segments: mergedSegments.length ? mergedSegments : undefined
      })
    }
  }

  return out
}
