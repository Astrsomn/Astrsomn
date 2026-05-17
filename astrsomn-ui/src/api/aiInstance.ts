import request from '@/utils/request'

export type AiInstance = {
    id?: number | string
    instanceKey?: string
    instanceName?: string
    modelKey?: string
    /** e.g. chat | embedding — drives list card preview */
    modelType?: string
    /** queryPage 子查询：关联模型 PROVIDER 在 SYSTEM_EXTENSION 中的头像（SVG） */
    providerAvatar?: string
    /** 关联模型的能力列表 JSON 字符串，例如 '["streaming","vision","tools"]' */
    capabilities?: string
    maxTokens?: number
    temperature?: number
    presencePenalty?: number
    frequencyPenalty?: number
    stopSequences?: string
    seed?: number
    topP?: number
    topK?: number
    style?: string
    size?: string
    dimensions?: number
    status?: string
    accountKey?: string
    accountName?: string
    isDefault?: string
    routeStrategy?: string
    routeWeight?: number
    resilienceEnabled?: string
    resilienceInstanceName?: string
    circuitBreakerEnabled?: string
    circuitBreakerFailureRateThreshold?: number
    circuitBreakerWaitDuration?: number
    retryEnabled?: string
    retryMaxAttempts?: number
    retryWaitDuration?: number
    timeoutEnabled?: string
    timeoutDuration?: number
    fallbackEnabled?: string
    fallbackInstanceKey?: string
    maxQuotaTokens?: number
    envCode?: string
    createUser?: string
    createTime?: string
    updateUser?: string
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

export const aiInstanceApi = {
    queryPage: (payload: unknown): Promise<PageResponse<AiInstance>> => {
        return request({
            url: '/v1/astro/ai-instance/queryPage',
            method: 'post',
            data: payload
        })
    },

    detail: (id: number | string): Promise<AiInstance> => {
        return request({
            url: `/v1/astro/ai-instance/detail?id=${encodeURIComponent(String(id))}`,
            method: 'get'
        })
    },

    create: (payload: AiInstance): Promise<string> => {
        return request({
            url: '/v1/astro/ai-instance/create',
            method: 'post',
            data: payload
        })
    },

    update: (payload: AiInstance): Promise<string> => {
        return request({
            url: '/v1/astro/ai-instance/update',
            method: 'post',
            data: payload
        })
    },

    delete: (ids: Array<number | string>): Promise<string> => {
        const joined = ids.map((x) => String(x)).join(',')
        return request({
            url: `/v1/astro/ai-instance/delete/${joined}`,
            method: 'delete'
        })
    }
}
