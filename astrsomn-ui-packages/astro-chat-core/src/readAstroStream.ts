import type {StreamEvent} from './types.js'
import {extractJsonPayloads, normalizeStreamPayload, parseSseEvent} from './streamParser.js'

export type StreamEventHandler = (event: StreamEvent) => boolean | Promise<boolean>

/**
 * Reads a fetch Response body (SSE or raw JSON chunks) and invokes the handler for each normalized event.
 * @returns true if completed normally, false if handler requested early stop (reader cancelled).
 */
export async function readAstroStream(
    response: Response,
    onEvent: StreamEventHandler
): Promise<boolean> {
    const reader = response.body?.getReader()
    if (!reader) {
        throw new Error('未获取到流式响应体')
    }

    const decoder = new TextDecoder('utf-8')
    const contentType = response.headers.get('content-type') || ''
    const isSse = contentType.includes('text/event-stream')
    let sseBuffer = ''
    let rawBuffer = ''

    const dispatch = async (event: StreamEvent): Promise<boolean> => {
        const shouldContinue = await onEvent(event)
        return shouldContinue !== false
    }

    while (true) {
        const {done, value} = await reader.read()
        if (done) {
            break
        }

        const chunk = decoder.decode(value, {stream: true})
        if (!chunk) {
            continue
        }

        if (!isSse) {
            rawBuffer += chunk
            const {events, remaining} = extractJsonPayloads(rawBuffer)
            rawBuffer = remaining
            for (const event of events) {
                const ok = await dispatch(event)
                if (!ok) {
                    await reader.cancel()
                    return false
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
                const ok = await dispatch(event)
                if (!ok) {
                    await reader.cancel()
                    return false
                }
            }
        }
    }

    if (!isSse && rawBuffer.trim()) {
        for (const event of normalizeStreamPayload(rawBuffer)) {
            const ok = await dispatch(event)
            if (!ok) {
                await reader.cancel()
                return false
            }
        }
    }

    if (isSse && sseBuffer.trim()) {
        const data = parseSseEvent(sseBuffer)
        for (const event of normalizeStreamPayload(data)) {
            const ok = await dispatch(event)
            if (!ok) {
                await reader.cancel()
                return false
            }
        }
    }

    return true
}
