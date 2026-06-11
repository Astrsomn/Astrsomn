import request from '@/utils/request'

/**
 * AI 配置中心各模块计数，字段与后端 AiConfigCenterCountsDTO 对齐
 */
export type AiConfigCenterCounts = {
    aiAccountCount: number
    aiPromptCount: number
    aiMcpCount: number
    aiToolCount: number
    aiTemplateCount: number
    aiChatSessionCount: number
}

export const aiConfigCenterApi = {
    counts: (): Promise<AiConfigCenterCounts> => {
        return request({
            url: '/v1/astro/ai-config-center/counts',
            method: 'get'
        })
    }
}
