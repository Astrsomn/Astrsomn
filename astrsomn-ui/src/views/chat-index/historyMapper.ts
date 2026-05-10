import type { AiChatMessageRow, AiChatTurnBundle } from '@/api/aiConversation'

export type ChatSegmentType = 'text' | 'thought' | 'html' | 'image'

export type ChatSegment = {
  type: ChatSegmentType
  content: string
}

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
          html += `<br><a href="${escapeHtml(url)}" target="_blank" rel="noopener noreferrer" style="color:#dbeafe;">${escapeHtml(
            att.name || '附件'
          )}</a>`
        }
      }
    }
  }
  return html
}

function rowToSegment(row: AiChatMessageRow): ChatSegment | null {
  const ext = parseExtJson(row.extJson)
  const partKind = ext?.partKind?.toLowerCase() ?? ''
  const mt = row.messageType?.toLowerCase() ?? ''
  const role = row.role?.toLowerCase() ?? ''
  const body = row.content ?? ''

  if (mt === 'reasoning' || partKind === 'thought') {
    return { type: 'thought', content: body }
  }
  if (mt === 'html' || partKind === 'html') {
    return { type: 'html', content: body }
  }
  if (mt === 'image' || partKind === 'image') {
    return { type: 'image', content: body }
  }
  if (mt === 'tool_call') {
    const name = ext?.toolName || 'tool'
    let args = ''
    try {
      args =
        ext?.arguments != null ? JSON.stringify(ext.arguments, null, 2) : ''
    } catch {
      args = String(ext?.arguments ?? '')
    }
    const cap = args.length > 4000 ? `${args.slice(0, 4000)}\n…` : args
    return {
      type: 'text',
      content: `**工具调用** \`${name}\`\n\n\`\`\`json\n${cap}\n\`\`\``
    }
  }
  if (mt === 'tool_result' || role === 'tool') {
    const cap = body.length > 12000 ? `${body.slice(0, 12000)}\n…` : body
    return { type: 'text', content: `**工具结果**\n\n${cap}` }
  }
  if (mt === 'error') {
    return { type: 'text', content: body }
  }
  if (body === '' && mt === 'text') {
    return null
  }
  return { type: 'text', content: body }
}

function mergeContentFromSegments(segments: ChatSegment[]): string {
  return segments
    .map((s) => {
      if (s.type === 'thought') return `[思考]\n${s.content}`
      if (s.type === 'image') return `\n![image](${s.content})\n`
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

    if (replyRows.length > 0) {
      out.push({
        id: `turn-${turn}-ai`,
        role: 'ai',
        content: mergeContentFromSegments(segments),
        segments: segments.length ? segments : undefined
      })
    }
  }

  return out
}
