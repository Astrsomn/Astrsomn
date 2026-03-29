import request from '@/utils/request'

export type AiModel = {
  id?: number | string
  modelName?: string
  modelKey?: string
  /** 详情返回：有推理实例引用该 modelKey 时为 true，前端应禁止改 modelKey */
  modelKeyImmutable?: boolean
  modelType?: string
  provider?: string
  accountKey?: string
  apiUrl?: string
  status?: string
  responseLimit?: number
  randomIndex?: number
  topVariance?: number
  isDefault?: number
  capabilities?: string
  maxQuotaTokens?: number
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

export const aiModelApi = {
  queryPage: (payload: any): Promise<PageResponse<AiModel>> => {
    return request({
      url: '/v1/astro/ai-model/queryPage',
      method: 'post',
      data: payload
    })
  },

  detail: (id: number | string): Promise<AiModel> => {
    return request({
      url: `/v1/astro/ai-model/detail?id=${encodeURIComponent(String(id))}`,
      method: 'get'
    })
  },

  create: (payload: AiModel): Promise<string> => {
    return request({
      url: '/v1/astro/ai-model/create',
      method: 'post',
      data: payload
    })
  },

  update: (payload: AiModel): Promise<string> => {
    return request({
      url: '/v1/astro/ai-model/update',
      method: 'post',
      data: payload
    })
  },

  delete: (ids: Array<number | string>): Promise<string> => {
    const joined = ids.map((x) => String(x)).join(',')
    return request({
      url: `/v1/astro/ai-model/delete/${joined}`,
      method: 'delete'
    })
  }
}

