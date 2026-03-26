import request from '@/utils/request'

export type SystemEnv = {
  id?: number | string
  envName?: string
  envKey?: string
  description?: string
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

/** 对应 SystemEnvController：`/api/astro/system-env` */
export const systemEnvApi = {
  queryPage: (payload: unknown): Promise<PageResponse<SystemEnv>> => {
    return request({
      url: '/api/astro/system-env/queryPage',
      method: 'post',
      data: payload
    })
  },

  detail: (id: number | string): Promise<SystemEnv> => {
    return request({
      url: `/api/astro/system-env/detail?id=${encodeURIComponent(String(id))}`,
      method: 'get'
    })
  },

  create: (payload: SystemEnv): Promise<string> => {
    return request({
      url: '/api/astro/system-env/create',
      method: 'post',
      data: payload
    })
  },

  update: (payload: SystemEnv): Promise<string> => {
    return request({
      url: '/api/astro/system-env/update',
      method: 'post',
      data: payload
    })
  },

  delete: (ids: Array<number | string>): Promise<string> => {
    const joined = ids.map((x) => String(x)).join(',')
    return request({
      url: `/api/astro/system-env/delete/${joined}`,
      method: 'delete'
    })
  }
}
