import request from '@/utils/request'

export type SystemConfig = {
  id?: number | string
  configKey?: string
  configValue?: string
  configGroup?: string
  description?: string
  isSystem?: boolean
  status?: string
  createTime?: string
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

export const systemConfigApi = {
  queryPage: (payload: unknown): Promise<PageResponse<SystemConfig>> => {
    return request({
      url: '/v1/astro/system-config/queryPage',
      method: 'post',
      data: payload
    })
  },

  detail: (id: number | string): Promise<SystemConfig> => {
    return request({
      url: `/v1/astro/system-config/detail?id=${encodeURIComponent(String(id))}`,
      method: 'get'
    })
  },

  create: (payload: SystemConfig): Promise<string> => {
    return request({
      url: '/v1/astro/system-config/create',
      method: 'post',
      data: payload
    })
  },

  update: (payload: SystemConfig): Promise<string> => {
    return request({
      url: '/v1/astro/system-config/update',
      method: 'post',
      data: payload
    })
  },

  delete: (ids: Array<number | string>): Promise<string> => {
    const joined = ids.map((x) => String(x)).join(',')
    return request({
      url: `/v1/astro/system-config/delete/${joined}`,
      method: 'delete'
    })
  }
}
