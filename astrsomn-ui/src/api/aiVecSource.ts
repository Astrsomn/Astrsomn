import request from '@/utils/request'

export type AiVecSource = {
  id?: number | string
  name?: string
  provider?: string
  host?: string
  port?: string
  username?: string
  password?: string
  databaseName?: string
  token?: string
  configJson?: string
  status?: string
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

export const aiVecSourceApi = {
  queryPage: (payload: unknown): Promise<PageResponse<AiVecSource>> => {
    return request({
      url: '/v1/astro/ai-vec-source/queryPage',
      method: 'post',
      data: payload
    })
  },

  detail: (id: number | string): Promise<AiVecSource> => {
    return request({
      url: `/v1/astro/ai-vec-source/detail?id=${encodeURIComponent(String(id))}`,
      method: 'get'
    })
  },

  create: (payload: AiVecSource): Promise<string> => {
    return request({
      url: '/v1/astro/ai-vec-source/create',
      method: 'post',
      data: payload
    })
  },

  update: (payload: AiVecSource): Promise<string> => {
    return request({
      url: '/v1/astro/ai-vec-source/update',
      method: 'post',
      data: payload
    })
  },

  delete: (ids: Array<number | string>): Promise<string> => {
    const joined = ids.map((x) => String(x)).join(',')
    return request({
      url: `/v1/astro/ai-vec-source/delete/${joined}`,
      method: 'delete'
    })
  },

  testConnection: (data: AiVecSource): Promise<string> => {
    return request({
      url: '/v1/astro/ai-vec-source/test-connection',
      method: 'post',
      data
    })
  },

  /** 启用或禁用：更新状态并同步运行时连接 */
  setStatus: (id: number | string, enabled: boolean): Promise<string> => {
    return request({
      url: '/v1/astro/ai-vec-source/set-status',
      method: 'post',
      data: { id, enabled }
    })
  }
}
