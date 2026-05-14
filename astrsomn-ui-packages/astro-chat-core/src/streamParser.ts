import type {StreamEvent, StreamEventType} from './types.js'

export function parseSseEvent(eventBlock: string): string {
    return eventBlock
        .split(/\r?\n/)
        .filter((line) => line.startsWith('data:'))
        .map((line) => line.slice(5).trimStart())
        .join('\n')
}

export function splitJsonObjects(input: string): {
    blocks: string[]
    trailing: string
    hasIncompleteBlock: boolean
} {
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

export function toStreamEvent(payload: unknown): StreamEvent | null {
    if (typeof payload === 'string') {
        if (payload === '[DONE]') {
            return {type: 'done', content: payload}
        }
        return {type: 'text', content: payload}
    }

    if (!payload || typeof payload !== 'object') {
        return null
    }

    const record = payload as Record<string, unknown>
    const rawType = typeof record.type === 'string' ? record.type.trim().toLowerCase() : 'text'
    const type: StreamEventType =
        rawType === 'thought' ||
        rawType === 'html' ||
        rawType === 'image' ||
        rawType === 'tool' ||
        rawType === 'error' ||
        rawType === 'done'
            ? rawType
            : 'text'
    const content = typeof record.content === 'string' ? record.content : ''
    if (type === 'done' || content || type === 'error' || type === 'image' || type === 'tool') {
        return {type, content}
    }
    return null
}

export function normalizeStreamPayload(raw: string): StreamEvent[] {
    const payload = raw.trim()
    if (!payload) {
        return []
    }

    if (payload === '[DONE]') {
        return [{type: 'done', content: payload}]
    }

    try {
        const parsed = JSON.parse(payload) as unknown
        const event = toStreamEvent(parsed)
        return event ? [event] : []
    } catch {
        // ignore and try other stream formats
    }

    const {blocks, trailing} = splitJsonObjects(payload)
    if (blocks.length > 0) {
        const events = blocks
            .map((block) => {
                try {
                    return toStreamEvent(JSON.parse(block) as unknown)
                } catch {
                    return null
                }
            })
            .filter((item): item is StreamEvent => item != null)

        if (trailing) {
            events.push({type: 'text', content: trailing})
        }
        return events
    }

    return [{type: 'text', content: raw}]
}

export function extractJsonPayloads(buffer: string): { events: StreamEvent[]; remaining: string } {
    const payload = buffer.trim()
    if (!payload) {
        return {events: [], remaining: ''}
    }

    if (payload === '[DONE]') {
        return {
            events: [{type: 'done', content: '[DONE]'}],
            remaining: ''
        }
    }

    const {blocks, trailing, hasIncompleteBlock} = splitJsonObjects(buffer)
    const events = blocks
        .map((block) => {
            try {
                return toStreamEvent(JSON.parse(block) as unknown)
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
        return {events, remaining: ''}
    }

    return {
        events,
        remaining: hasIncompleteBlock ? buffer.slice(buffer.lastIndexOf('{')) : ''
    }
}

export async function buildStreamError(response: Response): Promise<string> {
    const raw = await response.text()
    if (!raw) {
        return `请求失败 (${response.status})`
    }
    try {
        const parsed = JSON.parse(raw) as { message?: string }
        return parsed.message || raw
    } catch {
        return raw
    }
}
