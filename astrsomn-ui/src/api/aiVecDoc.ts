import request from '@/utils/request'

export type AiVecDoc = {
  id?: number | string
  collectionId?: number | string
  docIdInStore?: string
  contentSummary?: string
  syncStatus?: string
  createTime?: string
  updateTime?: string
  createUser?: string
  updateUser?: string
  envCode?: string
}

export type PageResponse<T> = {
  total: number
  pageSize: number
  pageNum: number
  pages: number
  hasNext: boolean
  list: T[]
}

export const aiVecDocApi = {
  queryPage: (payload: unknown): Promise<PageResponse<AiVecDoc>> => {
    return request({
      url: '/v1/astro/ai-vec-doc/queryPage',
      method: 'post',
      data: payload
    })
  },

  detail: (id: number | string): Promise<AiVecDoc> => {
    return request({
      url: `/v1/astro/ai-vec-doc/detail?id=${encodeURIComponent(String(id))}`,
      method: 'get'
    })
  },

  create: (payload: AiVecDoc): Promise<string> => {
    return request({
      url: '/v1/astro/ai-vec-doc/create',
      method: 'post',
      data: payload
    })
  },

  update: (payload: AiVecDoc): Promise<string> => {
    return request({
      url: '/v1/astro/ai-vec-doc/update',
      method: 'post',
      data: payload
    })
  },

  delete: (ids: Array<number | string>): Promise<string> => {
    const joined = ids.map((x) => String(x)).join(',')
    return request({
      url: `/v1/astro/ai-vec-doc/delete/${joined}`,
      method: 'delete'
    })
  }
}
