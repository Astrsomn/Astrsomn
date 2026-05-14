/** `tool`：content 为后端 JSON 字符串（toolName / args / result）。 */
export type StreamEventType = 'text' | 'thought' | 'html' | 'image' | 'tool' | 'error' | 'done'

export type StreamEvent = {
    type: StreamEventType
    content: string
}
