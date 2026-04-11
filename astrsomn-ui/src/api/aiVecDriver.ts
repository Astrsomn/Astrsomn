import request from '@/utils/request'

export interface AiVecDriver {
  id?: number | string
  driverName?: string
  provider?: string
  driverType?: string
  params?: string
  createTime?: string
  updateTime?: string
}

export interface PageResponse<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

export interface AiVecDriverQueryRequest {
  driverName?: string
  provider?: string
  driverType?: string
}

export const aiVecDriverApi = {
  async create(data: AiVecDriver): Promise<string> {
    const res = await request.post<{ message: string }>('/v1/astro/ai-vec-driver/create', data)
    return res.message
  },

  async update(data: AiVecDriver): Promise<string> {
    const res = await request.post<{ message: string }>('/v1/astro/ai-vec-driver/update', data)
    return res.message
  },

  async delete(ids: Array<number | string>): Promise<string> {
    const res = await request.delete<{ message: string }>(`/v1/astro/ai-vec-driver/delete/${ids.join(',')}`)
    return res.message
  },

  async detail(id: number | string): Promise<AiVecDriver> {
    const res = await request.get<{ data: AiVecDriver }>(`/v1/astro/ai-vec-driver/detail?id=${id}`)
    return res.data
  },

  async queryPage(params: {
    pageNo: number
    pageSize: number
    param?: AiVecDriverQueryRequest
  }): Promise<PageResponse<AiVecDriver>> {
    const res = await request.post<PageResponse<AiVecDriver>>('/v1/astro/ai-vec-driver/queryPage', params)
    return res
  }
}