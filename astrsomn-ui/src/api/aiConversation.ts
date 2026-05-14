import request from '@/utils/request'
import type {ChatSessionItem} from '@/components/chat-session/types'

/** 与后端 AiChatMessageResponseDTO / 实体对齐的单条消息行（camelCase JSON）。 */
export type AiChatMessageRow = {
    id?: number | string
    memoryKey?: string
    turnNo?: number
    messageOrder?: number
    role?: string
    messageType?: string
    content?: string
    extJson?: string
    responseStatus?: string
    finishReason?: string
    traceId?: string
    errorCode?: string
    promptTokens?: number
    completionTokens?: number
    totalTokens?: number
    agentKey?: string
    instanceKey?: string
    modelKey?: string
    accountKey?: string
    promptKey?: string
    createTime?: string
    updateTime?: string
}

/** 与后端 AiChatTurnBundleDTO 对齐。 */
export type AiChatTurnBundle = {
    turnNo?: number
    orderedRows?: AiChatMessageRow[]
}

export type AiConversation = {
    id?: number | string
    memoryKey?: string
    role?: string
    content?: string
    conversationContent?: string
    sessionTitle?: string
    lastMessagePreview?: string
    messageCount?: number
    createTime?: string
    updateTime?: string
    createUser?: string
    updateUser?: string
    status?: string
    sessionStatus?: string
}

export type AiConversationCreateRequestDTO = {
    memoryKey?: string
    conversationContent?: string
    status?: string
}

export type AiConversationUpdateRequestDTO = {
    id?: number | string
    memoryKey?: string
    conversationContent?: string
    status?: string
}

export type AiConversationQueryRequestDTO = {
    memoryKey?: string
    status?: string
    createUser?: string
    startTime?: string
    endTime?: string
}

export type PageResponse<T> = {
    total: number
    pageSize: number
    pageNum: number
    pages: number
    hasNext: boolean
    list: T[]
}

export const aiConversationApi = {
    queryPage: (payload: unknown): Promise<PageResponse<AiConversation>> => {
        return request({
            url: '/v1/astro/ai-chat-message/queryPage',
            method: 'post',
            data: payload
        })
    },

    queryGroups: (payload: unknown): Promise<PageResponse<AiConversation>> => {
        return request({
            url: '/v1/astro/ai-chat-message/queryGroups',
            method: 'post',
            data: payload
        })
    },

    detail: (id: number | string): Promise<AiConversation> => {
        return request({
            url: `/v1/astro/ai-chat-message/detail?id=${encodeURIComponent(String(id))}`,
            method: 'get'
        })
    },

    recoverByMemoryKey: (memoryKey: string): Promise<AiConversation[]> => {
        return request({
            url: `/v1/astro/ai-chat-message/recoverByMemoryKey?memoryKey=${encodeURIComponent(memoryKey)}`,
            method: 'get'
        })
    },

    recoverTurnsByMemoryKey: (memoryKey: string): Promise<AiChatTurnBundle[]> => {
        return request({
            url: `/v1/astro/ai-chat-message/recoverTurnsByMemoryKey?memoryKey=${encodeURIComponent(memoryKey)}`,
            method: 'get'
        })
    },

    create: (payload: AiConversationCreateRequestDTO): Promise<string> => {
        return request({
            url: '/v1/astro/ai-chat-message/create',
            method: 'post',
            data: payload
        })
    },

    update: (payload: AiConversationUpdateRequestDTO): Promise<string> => {
        return request({
            url: '/v1/astro/ai-chat-message/update',
            method: 'post',
            data: payload
        })
    },

    delete: (ids: Array<number | string>): Promise<string> => {
        const joined = ids.map((x) => String(x)).join(',')
        return request({
            url: `/v1/astro/ai-chat-message/delete/${joined}`,
            method: 'delete'
        })
    }
}

export const adaptConversationToSessionItem = (conversation: AiConversation): ChatSessionItem => {
    const title = conversation.sessionTitle || conversation.content || conversation.conversationContent || '无内容对话'
    const preview = conversation.lastMessagePreview || conversation.content || conversation.conversationContent || ''
    return {
        id: conversation.id,
        memoryKey: conversation.memoryKey || '',
        title,
        preview,
        messageCount: conversation.messageCount,
        createTime: conversation.createTime,
        updateTime: conversation.updateTime,
        raw: conversation as unknown as Record<string, unknown>
    }
}