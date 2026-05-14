import request from '@/utils/request'
import type { PageResponse } from './systemExtension'

export type ExtensionMarketplaceItem = {
  extensionKey?: string
  extensionName?: string
  type?: string
  version?: string
  author?: string
  description?: string
  jarName?: string
  extensionCode?: string
  avatar?: string
}

/**
 * 对应 SystemExtensionMarketController：`/v1/astro/extension-marketplace`
 */
export const extensionMarketplaceApi = {
  catalog: (type?: string, pageNo: number = 1, pageSize: number = 10): Promise<PageResponse<ExtensionMarketplaceItem>> => {
    let q = type && type !== 'ALL' ? `?type=${encodeURIComponent(type)}` : ''
    q += (q ? '&' : '?') + `pageNo=${pageNo}&pageSize=${pageSize}`
    return request({
      url: `/v1/astro/extension-marketplace/catalog${q}`,
      method: 'get'
    })
  },

  install: (pluginId: string, version: string): Promise<string> => {
    return request({
      url: '/v1/astro/extension-marketplace/install',
      method: 'post',
      params: { pluginId, version }
    })
  }
}
