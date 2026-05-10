export type StreamEventType = 'text' | 'thought' | 'html' | 'error' | 'done'

export type StreamEvent = {
  type: StreamEventType
  content: string
}
