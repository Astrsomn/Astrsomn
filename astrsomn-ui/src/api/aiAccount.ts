import request from '@/utils/request'

export type AiAccount = {
  id?: number | string
  accountKey?: string
  accountName?: string
  apiKey?: string
  apiSecret?: string
  accountTokens?: number
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

export const aiAccountApi = {
  queryPage: (payload: unknown): Promise<PageResponse<AiAccount>> => {
    return request({
      url: '/v1/astro/ai-account/queryPage',
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
