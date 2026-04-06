import request from '@/utils/request'

export type AiVecStore = {
  id?: number | string
  sourceId?: number | string
  collectionName?: string
  dimension?: number | string
  distanceMetric?: string
  metadataSchema?: string
  modelKey?: string
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

export const aiVecStoreApi = {
  queryPage: (payload: unknown): Promise<PageResponse<AiVecStore>> => {
    return request({
      url: '/v1/astro/ai-vec-store/queryPage',
      method: 'post',
      data: payload
    })
  },

  detail: (id: number | string): Promise<AiVecStore> => {
    return request({
      url: `/v1/astro/ai-vec-store/detail?id=${encodeURIComponent(String(id))}`,
      method: 'get'
    })
  },

  create: (payload: AiVecStore): Promise<string> => {
    return request({
      url: '/v1/astro/ai-vec-store/create',
      method: 'post',
      data: payload
    })
  },

  update: (payload: AiVecStore): Promise<string> => {
    return request({
      url: '/v1/astro/ai-vec-store/update',
      method: 'post',
      data: payload
    })
  },

  delete: (ids: Array<number | string>): Promise<string> => {
    const joined = ids.map((x) => String(x)).join(',')
    return request({
      url: `/v1/astro/ai-vec-store/delete/${joined}`,
      method: 'delete'
    })
  }
}
