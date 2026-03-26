import request from '@/utils/request'

export type AiTool = {
  id?: number | string
  toolKey?: string
  toolName?: string
  description?: string
  beanName?: string
  methodName?: string
  type?: string
  enableFlag?: string
}

export type PageResponse<T> = {
  total: number
  pageSize: number
  pageNum: number
  pages: number
  hasNext: boolean
  list: T[]
}

export const aiToolApi = {
  queryPage: (payload: any): Promise<PageResponse<AiTool>> => {
    return request({
      url: '/v1/astro/ai-tool/queryPage',
      method: 'post',
      data: payload
    })
  },

  detail: (id: number | string): Promise<AiTool> => {
    return request({
      url: `/v1/astro/ai-tool/detail?id=${encodeURIComponent(String(id))}`,
      method: 'get'
    })
  }
}
