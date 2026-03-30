import request from '@/utils/request'

export type AiTool = {
  id?: number | string
  toolKey?: string
  toolName?: string
  description?: string
  beanName?: string
  methodName?: string
  /** html | method（与 AiToolEnum.TypeEnum 一致） */
  type?: string
  /** enabled | disabled */
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
  queryPage: (payload: unknown): Promise<PageResponse<AiTool>> => {
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
  },

  create: (payload: AiTool): Promise<string> => {
    return request({
      url: '/v1/astro/ai-tool/create',
      method: 'post',
      data: payload
    })
  },

  update: (payload: AiTool): Promise<string> => {
    return request({
      url: '/v1/astro/ai-tool/update',
      method: 'post',
      data: payload
    })
  },

  delete: (ids: Array<number | string>): Promise<string> => {
    const joined = ids.map((x) => String(x)).join(',')
    return request({
      url: `/v1/astro/ai-tool/delete/${joined}`,
      method: 'delete'
    })
  }
}
