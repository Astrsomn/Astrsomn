import request from '@/utils/request'

export type AiSensitiveWord = {
    id?: number | string
    word?: string
    matchType?: string
    scopeKey?: string
    action?: string
    replacement?: string
    status?: string
    category?: string
    createTime?: string
    updateTime?: string
}

export type PageResponse<T> = {
    total: number
    pageSize: number
    pageNum: number
    pages: number
    hasNext: boolean
    list: T[]
}

export const aiSensitiveWordApi = {
    queryPage: (payload: unknown): Promise<PageResponse<AiSensitiveWord>> => {
        return request({
            url: '/v1/astro/ai-sensitive-word/queryPage',
            method: 'post',
            data: payload
        })
    },

    detail: (id: number | string): Promise<AiSensitiveWord> => {
        return request({
            url: `/v1/astro/ai-sensitive-word/detail?id=${encodeURIComponent(String(id))}`,
            method: 'get'
        })
    },

    create: (payload: AiSensitiveWord): Promise<string> => {
        return request({
            url: '/v1/astro/ai-sensitive-word/create',
            method: 'post',
            data: payload
        })
    },

    update: (payload: AiSensitiveWord): Promise<string> => {
        return request({
            url: '/v1/astro/ai-sensitive-word/update',
            method: 'post',
            data: payload
        })
    },

    delete: (ids: Array<number | string>): Promise<string> => {
        const joined = ids.map((x) => String(x)).join(',')
        return request({
            url: `/v1/astro/ai-sensitive-word/delete/${joined}`,
            method: 'delete'
        })
    }
}
