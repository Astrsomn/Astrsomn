import request from '@/utils/request'

export type AiPrompt = {
  id?: number | string
  promptKey?: string
  promptTitle?: string
  promptContent?: string
  scene?: string
  enabledFlag?: string
  version?: number
}

export type PageResponse<T> = {
  total: number
  pageSize: number
  pageNum: number
  pages: number
  hasNext: boolean
  list: T[]
}

export const aiPromptApi = {
  queryPage: (payload: any): Promise<PageResponse<AiPrompt>> => {
    return request({
      url: '/v1/astro/ai-promopt/queryPage',
      method: 'post',
      data: payload
    })
  },

  detail: (id: number | string): Promise<AiPrompt> => {
    return request({
      url: `/v1/astro/ai-promopt/detail?id=${encodeURIComponent(String(id))}`,
      method: 'get'
    })
  }
}
