import request from '@/utils/request'

import type {AiInstance} from './aiInstance'

export type AiAgent = {
    instanceList?: AiInstance[]
    id?: number | string
    envCode?: string
    createUser?: string
    createTime?: string
    agentKey?: string
    agentName?: string
    description?: string
    workflowKey?: string
    /** 推理实例列表（一对多关系） */
    instanceList?: AiInstance[]
    /** 对话模型实例（旧版兼容） */
    chatInstanceKey?: string
    /** 前端展示用（列表联表或本地缓存，不必提交） */
    chatInstanceName?: string
    /** 图像模型实例（旧版兼容） */
    imageInstanceKey?: string
    imageInstanceName?: string
    /** 语音 / TTS 等推理实例（旧版兼容） */
    voiceInstanceKey?: string
    voiceInstanceName?: string
    promptKey?: string
    status?: string
    knowledgeBaseKeys?: string
    toolKeys?: string
    mcpKeys?: string
    memoryMode?: string
    memoryWindowSize?: string
    enableStream?: boolean
    /** queryPage 联表：对话实例背后的 AI_MODEL.MODEL_NAME */
    modelName?: string
    /** queryPage 联表：对话模型 PROVIDER（厂商 code） */
    modelProvider?: string
    /** queryPage 子查询：SYSTEM_EXTENSION 中与厂商匹配的展示头像（SVG） */
    providerAvatar?: string
    /** queryPage 联表：AI_PROMPT 当前版本标题 */
    promptTitle?: string
    toolNames?: string
    mcpNames?: string
    routeStrategy?: string
}

export type PageResponse<T> = {
    total: number
    pageSize: number
    pageNum: number
    pages: number
    hasNext: boolean
    list: T[]
}

export const aiAgentApi = {
    queryPage: (payload: any): Promise<PageResponse<AiAgent>> => {
        return request({
            url: '/v1/astro/ai-agent/queryPage',
            method: 'post',
            data: payload
        })
    },

    detail: (id: number | string): Promise<AiAgent> => {
        return request({
            url: `/v1/astro/ai-agent/detail?id=${encodeURIComponent(String(id))}`,
            method: 'get'
        })
    },

    create: (payload: AiAgent): Promise<string> => {
        return request({
            url: '/v1/astro/ai-agent/create',
            method: 'post',
            data: payload
        })
    },

    createFullAgent: (payload: AiAgent): Promise<string> => {
        return request({
            url: '/v1/astro/ai-agent/createFullAgent',
            method: 'post',
            data: payload
        })
    },

    update: (payload: AiAgent): Promise<string> => {
        return request({
            url: '/v1/astro/ai-agent/update',
            method: 'post',
            data: payload
        })
    },

    delete: (ids: Array<number | string>): Promise<string> => {
        const joined = ids.map((x) => String(x)).join(',')
        return request({
            url: `/v1/astro/ai-agent/delete/${joined}`,
            method: 'delete'
        })
    }
}

