import request from '@/utils/request'

export type SystemExtension = {
  id?: number | string
  extensionKey?: string
  extensionName?: string
  type?: string
  version?: string
  author?: string
  description?: string
  jarName?: string
  applied?: string
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

/**
 * 对应 SystemExtensionController：`/v1/astro/system-extension`
 */
export const systemExtensionApi = {
  queryPage: (payload: unknown): Promise<PageResponse<SystemExtension>> => {
    return request({
      url: '/v1/astro/system-extension/queryPage',
      method: 'post',
      data: payload
    })
  },

  detail: (id: number | string): Promise<SystemExtension> => {
    return request({
      url: `/v1/astro/system-extension/detail?id=${encodeURIComponent(String(id))}`,
      method: 'get'
    })
  },

  create: (payload: SystemExtension): Promise<string> => {
    return request({
      url: '/v1/astro/system-extension/create',
      method: 'post',
      data: payload
    })
  },

  update: (payload: SystemExtension): Promise<string> => {
    return request({
      url: '/v1/astro/system-extension/update',
      method: 'post',
      data: payload
    })
  },

  delete: (ids: Array<number | string>): Promise<string> => {
    const joined = ids.map((x) => String(x)).join(',')
    return request({
      url: `/v1/astro/system-extension/delete/${joined}`,
      method: 'delete'
    })
  },

  apply: (id: number | string): Promise<string> => {
    return request({
      url: `/v1/astro/system-extension/apply?id=${encodeURIComponent(String(id))}`,
      method: 'post'
    })
  },

  uninstall: (id: number | string): Promise<string> => {
    return request({
      url: `/v1/astro/system-extension/uninstall?id=${encodeURIComponent(String(id))}`,
      method: 'post'
    })
  }
}

