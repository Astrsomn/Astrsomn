import request from '@/utils/request'

export interface AiVecSegment {
  id?: number | string
  docId?: number | string
  collectionId?: number | string
  vectorId?: string
  segmentContent?: string
  wordCount?: number
  chunkIndex?: number
  metadataJson?: string
}

export interface PageResponse<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

export interface AiVecSegmentQueryRequest {
  docId?: number
  collectionId?: number
  vectorId?: string
  chunkIndex?: number
}

export const aiVecSegmentApi = {
  async create(data: AiVecSegment): Promise<string> {
    const res = await request.post<{ message: string }>('/v1/astro/ai-vec-segment/create', data)
    return res.message
  },

  async update(data: AiVecSegment): Promise<string> {
    const res = await request.post<{ message: string }>('/v1/astro/ai-vec-segment/update', data)
    return res.message
  },

  async delete(ids: Array<number | string>): Promise<string> {
    const res = await request.delete<{ message: string }>(`/v1/astro/ai-vec-segment/delete/${ids.join(',')}`)
    return res.message
  },

  async detail(id: number | string): Promise<AiVecSegment> {
    const res = await request.get<{ data: AiVecSegment }>(`/v1/astro/ai-vec-segment/detail?id=${id}`)
    return res.data
  },

  async queryPage(params: {
    pageNo: number
    pageSize: number
    param?: AiVecSegmentQueryRequest
  }): Promise<PageResponse<AiVecSegment>> {
    const res = await request.post<PageResponse<AiVecSegment>>('/v1/astro/ai-vec-segment/queryPage', params)
    return res
  }
}
