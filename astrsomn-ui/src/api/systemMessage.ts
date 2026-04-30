import request from '@/utils/request'

export type SystemMessage = {
  id?: number | string
  messageType?: string
  messageLevel?: string
  readStatus?: string
  title?: string
  content?: string
  refType?: string
  refId?: number | string
  refKey?: string
  source?: string
  errorCode?: string
  envCode?: string
  createTime?: string
  updateTime?: string
  createUser?: string
}

export type PageResponse<T> = {
  total: number
  pageSize: number
  pageNum: number
  pages: number
  hasNext: boolean
  list: T[]
}

export const systemMessageApi = {
  queryPage: (payload: unknown): Promise<PageResponse<SystemMessage>> => {
    return request({
      url: '/v1/astro/system-message/queryPage',
      method: 'post',
      data: payload
    })
  },

  detail: (id: number | string): Promise<SystemMessage> => {
    return request({
      url: `/v1/astro/system-message/detail?id=${encodeURIComponent(String(id))}`,
      method: 'get'
    })
  },

  create: (payload: SystemMessage): Promise<string> => {
    return request({
      url: '/v1/astro/system-message/create',
      method: 'post',
      data: payload
    })
  },

  update: (payload: SystemMessage): Promise<string> => {
    return request({
      url: '/v1/astro/system-message/update',
      method: 'post',
      data: payload
    })
  },

  delete: (ids: Array<number | string>): Promise<string> => {
    const joined = ids.map((x) => String(x)).join(',')
    return request({
      url: `/v1/astro/system-message/delete/${joined}`,
      method: 'delete'
    })
  }
}
