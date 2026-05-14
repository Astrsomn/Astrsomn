import request from '@/utils/request'
import type {AiModel} from '@/api/aiModel'

export type AiAccount = {
    id?: number | string
    accountKey?: string
    /** 详情接口返回：有模型引用该 Key 时为 true，前端应禁止改 accountKey */
    accountKeyImmutable?: boolean
    accountName?: string
    extensionCode?: string
    apiUrl?: string
    apiKey?: string
    apiSecret?: string
    accountTokens?: number
    callCount?: number
    promptTokens?: number
    completionTokens?: number
    totalTokens?: number
    envCode?: string
    /** 同环境下使用该 accountKey 的模型数量 */
    usedModelCount?: number
    /** 同环境下使用该 accountKey 的模型 Key 列表（逗号分隔） */
    usedModelKeys?: string
    /** 同环境下使用该 accountKey 的模型名称列表（逗号分隔） */
    usedModelNames?: string
    createUser?: string
    createTime?: string
    updateUser?: string
    updateTime?: string
    status?: string
}

export type PageResponse<T> = {
    total: number
    pageSize: number
    pageNum: number
    pages: number
    hasNext: boolean
    list: T[]
}

export const aiAccountApi = {
    queryPage: (payload: unknown): Promise<PageResponse<AiAccount>> => {
        return request({
            url: '/v1/astro/ai-account/queryPage',
            method: 'post',
            data: payload
        })
    },

    queryModelsByAccountKey: (payload: unknown): Promise<PageResponse<AiModel>> => {
        return request({
            url: '/v1/astro/ai-account/queryModelsByAccountKey',
            method: 'post',
            data: payload
        })
    },

    detail: (id: number | string): Promise<AiAccount> => {
        return request({
            url: `/v1/astro/ai-account/detail?id=${encodeURIComponent(String(id))}`,
            method: 'get'
        })
    },

    create: (payload: AiAccount): Promise<string> => {
        return request({
            url: '/v1/astro/ai-account/create',
            method: 'post',
            data: payload
        })
    },

    update: (payload: AiAccount): Promise<string> => {
        return request({
            url: '/v1/astro/ai-account/update',
            method: 'post',
            data: payload
        })
    },

    delete: (ids: Array<number | string>): Promise<string> => {
        const joined = ids.map((x) => String(x)).join(',')
        return request({
            url: `/v1/astro/ai-account/delete/${joined}`,
            method: 'delete'
        })
    }
}
