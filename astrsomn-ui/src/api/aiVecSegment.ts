import request from '@/utils/request'

export type AiVecSegment = {
  id?: number | string
  docId?: number | string
  collectionId?: number | string
  vectorId?: string
  segmentContent?: string
  wordCount?: number | string
  chunkIndex?: number | string
  metadataJson?: string
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

export const aiVecSegmentApi = {
  queryPage: (payload: unknown): Promise<PageResponse<AiVecSegment>> => {
    return request({
      url: '/v1/astro/ai-vec-segment/queryPage',
      method: 'post',
      data: payload
    })
  },

  detail: (id: number | string): Promise<AiVecSegment> => {
    return request({
      url: `/v1/astro/ai-vec-segment/detail?id=${encodeURIComponent(String(id))}`,
      method: 'get'
    })
  },

  create: (payload: AiVecSegment): Promise<string> => {
    return request({
      url: '/v1/astro/ai-vec-segment/create',
      method: 'post',
      data: payload
    })
  },

  update: (payload: AiVecSegment): Promise<string> => {
    return request({
      url: '/v1/astro/ai-vec-segment/update',
      method: 'post',
      data: payload
    })
  },

  delete: (ids: Array<number | string>): Promise<string> => {
    const joined = ids.map((x) => String(x)).join(',')
    return request({
      url: `/v1/astro/ai-vec-segment/delete/${joined}`,
      method: 'delete'
    })
  }
}
