import request from '@/utils/request'

/** 对应 AI_TEMPLATE / AiTemplateController */
export type AiTemplate = {
  id?: number | string
  templateKey?: string
  templateTitle?: string
  content?: string
  category?: string
  /** FREEMARKER | STRING_TEMPLATE */
  templateType?: string
  version?: number
  status?: string
  createTime?: string
  updateTime?: string
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

export const aiTemplateApi = {
  queryPage: (payload: unknown): Promise<PageResponse<AiTemplate>> => {
    return request({
      url: '/v1/astro/ai-template/queryPage',
      method: 'post',
      data: payload
    })
  },

  detail: (id: number | string): Promise<AiTemplate> => {
    return request({
      url: `/v1/astro/ai-template/detail?id=${encodeURIComponent(String(id))}`,
      method: 'get'
    })
  },

  create: (payload: AiTemplate): Promise<string> => {
    return request({
      url: '/v1/astro/ai-template/create',
      method: 'post',
      data: payload
    })
  },

  update: (payload: AiTemplate): Promise<string> => {
    return request({
      url: '/v1/astro/ai-template/update',
      method: 'post',
      data: payload
    })
  },

  delete: (ids: Array<number | string>): Promise<string> => {
    const joined = ids.map((x) => String(x)).join(',')
    return request({
      url: `/v1/astro/ai-template/delete/${joined}`,
      method: 'delete'
    })
  }
}
