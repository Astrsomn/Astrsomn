import request from '@/utils/request'
import type {ChatSessionItem} from '@/components/chat-session/types'

export type AiChatSession = {
    id?: number | string
    memoryKey?: string
    sessionTitle?: string
    sessionStatus?: string
    lastMessagePreview?: string
    lastMessageAt?: number
    messageCount?: number
    createTime?: string
    updateTime?: string
}

export type AiChatSessionQueryRequestDTO = {
    memoryKey?: string
    sessionStatus?: string
}

export type PageResponse<T> = {
    total: number
    pageSize: number
    pageNum: number
    pages: number
    hasNext: boolean
    list: T[]
}

export const aiChatSessionApi = {
    queryPage: (payload: {
        pageNo: number;
        pageSize: number;
        param?: AiChatSessionQueryRequestDTO
    }): Promise<PageResponse<AiChatSession>> => {
        return request({
            url: '/v1/astro/ai-chat-session/queryPage',
            method: 'post',
            data: payload
        })
    },
    delete: (ids: Array<number | string>): Promise<string> => {
        const joined = ids.join(',')
        return request({
            url: `/v1/astro/ai-chat-session/delete/${joined}`,
            method: 'delete'
        })
    }
}

export const adaptSessionToSessionItem = (session: AiChatSession): ChatSessionItem => {
    const title = session.sessionTitle || '新会话'
    const preview = session.lastMessagePreview || ''
    return {
        id: session.id,
        memoryKey: session.memoryKey || '',
        title,
        preview,
        messageCount: session.messageCount,
        createTime: session.createTime,
        updateTime: session.updateTime,
        raw: session as unknown as Record<string, unknown>
    }
}
