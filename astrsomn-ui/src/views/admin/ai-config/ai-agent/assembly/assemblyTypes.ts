import type {AiInstance} from '@/api/aiInstance'
import type {AiTool} from '@/api/aiTool'
import type {AiMcp} from '@/api/aiMcp'
import type {AiPrompt} from '@/api/aiPrompt'

export const ASSEMBLY_DRAG_MIME = 'application/x-astrsomn-assembly'

/** 与 AgentResourcePickModal 中 instanceModelType 一致 */
export type InstanceModelType = 'chat' | 'embedding' | 'image'

export type AssemblyDragPayload =
    | { kind: 'instance'; instanceModelType: InstanceModelType; data: AiInstance }
    | { kind: 'tool'; data: AiTool }
    | { kind: 'mcp'; data: AiMcp }
    | { kind: 'knowledgeBase'; data: { kbKey: string; title?: string } }
    | { kind: 'prompt'; data: AiPrompt }

export type AssemblySlotKey =
    | 'chatInstance'
    | 'imageInstance'
    | 'promptInstance'
    | 'tools'
    | 'mcps'
    | 'knowledgeBase'

/** 与 AgentFormModal 第一步字段对齐（组装页预览） */
export type AssemblyAgentForm = {
    agentName: string
    bizKey?: string
    status?: string
    enableStream: boolean
    description: string
    memoryMode: string
    memoryWindowSize: string | number | undefined
}

export function parseDragPayload(raw: string): AssemblyDragPayload | null {
    if (!raw) return null
    try {
        return JSON.parse(raw) as AssemblyDragPayload
    } catch {
        return null
    }
}

export function payloadAcceptsSlot(payload: AssemblyDragPayload | null, slot: AssemblySlotKey): boolean {
    if (!payload) return false
    if (slot === 'chatInstance') {
        return payload.kind === 'instance' && payload.instanceModelType === 'chat'
    }
    if (slot === 'embeddingInstance') {
        return payload.kind === 'instance' && payload.instanceModelType === 'embedding'
    }
    if (slot === 'imageInstance') {
        return payload.kind === 'instance' && payload.instanceModelType === 'image'
    }
    if (slot === 'promptInstance') return payload.kind === 'prompt'
    if (slot === 'tools') return payload.kind === 'tool'
    if (slot === 'mcps') return payload.kind === 'mcp'
    if (slot === 'knowledgeBase') return payload.kind === 'knowledgeBase'
    return false
}
