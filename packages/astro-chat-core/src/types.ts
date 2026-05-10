export type StreamEventType = 'text' | 'thought' | 'html' | 'image' | 'error' | 'done'

export type StreamEvent = {
  type: StreamEventType
  content: string
}
