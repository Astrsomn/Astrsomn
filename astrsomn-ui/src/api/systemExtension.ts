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
  /** 公用：厂商/提供方 code（模型扩展时与 AiModelEnum.ProviderEnum 一致） */
  providerCode?: string
  createTime?: string
  updateTime?: string
}

/** 插件市场目录项（GET marketplace/catalog） */
export type ExtensionMarketplaceItem = {
  extensionKey?: string
  extensionName?: string
  type?: string
  version?: string
  author?: string
  description?: string
  jarName?: string
  providerCode?: string
}

export type PageResponse<T> = {
  total: number
  pageSize: number
  pageNum: number
  pages: number
  hasNext: boolean
  list: T[]
}

/** 与 SystemExtensionEnum.ExtensionListScopeEnum.code 一致 */
export type SystemExtensionListScope = 'MARKETPLACE' | 'INSTALLED'

/** 分页查询 param，与 SystemExtensionQueryRequestDTO 对齐 */
export type SystemExtensionQueryParam = {
  extensionKey?: string
  extensionName?: string
  status?: string
  /** ExtensionTypeEnum：MODEL_PROVIDER | VECTOR_STORE | MCP */
  type?: string
  listScope?: SystemExtensionListScope
}

export type SystemExtensionQueryPagePayload = {
  pageNo: number
  pageSize: number
  param?: SystemExtensionQueryParam
}

/** 模型同步预览单行 */
export type ExtensionModelSyncPreviewRow = {
  modelKey?: string
  modelName?: string
  modelType?: string
  provider?: string
}

/** GET load-models/preview */
export type ExtensionModelLoadPreview = {
  toCreate?: ExtensionModelSyncPreviewRow[]
  skippedExisting?: ExtensionModelSyncPreviewRow[]
  skippedInvalidCount?: number
}

/** GET unload-models/preview */
export type ExtensionModelUnloadPreview = {
  toRemove?: ExtensionModelSyncPreviewRow[]
  keptReferenced?: ExtensionModelSyncPreviewRow[]
}

/**
 * 对应 SystemExtensionController：`/v1/astro/system-extension`
 */
export const systemExtensionApi = {
  queryPage: (payload: SystemExtensionQueryPagePayload): Promise<PageResponse<SystemExtension>> => {
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
  },

  /** 模型类扩展：当前环境下该厂商全部模型 status → disabled */
  disableProviderModels: (id: number | string): Promise<string> => {
    return request({
      url: `/v1/astro/system-extension/disable-provider-models?id=${encodeURIComponent(String(id))}`,
      method: 'post'
    })
  },

  marketplaceCatalog: (type?: string): Promise<ExtensionMarketplaceItem[]> => {
    const q = type && type !== 'ALL' ? `?type=${encodeURIComponent(type)}` : ''
    return request({
      url: `/v1/astro/system-extension/marketplace/catalog${q}`,
      method: 'get'
    })
  },

  previewLoadModels: (id: number | string): Promise<ExtensionModelLoadPreview> => {
    return request({
      url: `/v1/astro/system-extension/load-models/preview?id=${encodeURIComponent(String(id))}`,
      method: 'get'
    })
  },

  loadModels: (id: number | string): Promise<string> => {
    return request({
      url: `/v1/astro/system-extension/load-models?id=${encodeURIComponent(String(id))}`,
      method: 'post'
    })
  },

  previewUnloadModels: (id: number | string): Promise<ExtensionModelUnloadPreview> => {
    return request({
      url: `/v1/astro/system-extension/unload-models/preview?id=${encodeURIComponent(String(id))}`,
      method: 'get'
    })
  },

  unloadModels: (id: number | string): Promise<string> => {
    return request({
      url: `/v1/astro/system-extension/unload-models?id=${encodeURIComponent(String(id))}`,
      method: 'post'
    })
  }
}

