import request from '@/utils/request'

export type SystemUser = {
  id?: number | string
  username?: string
  password?: string
  /** Y / N */
  adminFlag?: string
  email?: string
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

/** 对应 SystemUserController：`/api/astro/system-user` */
export const systemUserApi = {
  queryPage: (payload: unknown): Promise<PageResponse<SystemUser>> => {
    return request({
      url: '/api/astro/system-user/queryPage',
      method: 'post',
      data: payload
    })
  },

  detail: (id: number | string): Promise<SystemUser> => {
    return request({
      url: `/api/astro/system-user/detail?id=${encodeURIComponent(String(id))}`,
      method: 'get'
    })
  },

  create: (payload: SystemUser): Promise<string> => {
    return request({
      url: '/api/astro/system-user/create',
      method: 'post',
      data: payload
    })
  },

  update: (payload: SystemUser): Promise<string> => {
    return request({
      url: '/api/astro/system-user/update',
      method: 'post',
      data: payload
    })
  },

  delete: (ids: Array<number | string>): Promise<string> => {
    const joined = ids.map((x) => String(x)).join(',')
    return request({
      url: `/api/astro/system-user/delete/${joined}`,
      method: 'delete'
    })
  }
}
