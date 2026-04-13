import request from '@/utils/request'

/** 与后端 {@code AiVecDocEnum.SyncStatus} 一致 */
export const AiVecDocSyncStatus = {
  PENDING: 'PENDING',
  STORED: 'STORED',
  INVALID: 'INVALID'
} as const

export type AiVecDoc = {
  id?: number | string
  collectionId?: number | string
  docIdInStore?: string
  contentSummary?: string
  syncStatus?: string
  filePath?: string
  originalFileName?: string
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

export const aiVecDocApi = {
  queryPage: (payload: unknown): Promise<PageResponse<AiVecDoc>> => {
    return request({
      url: '/v1/astro/ai-vec-doc/queryPage',
      method: 'post',
      data: payload
    })
  },

  detail: (id: number | string): Promise<AiVecDoc> => {
    return request({
      url: `/v1/astro/ai-vec-doc/detail?id=${encodeURIComponent(String(id))}`,
      method: 'get'
    })
  },

  create: (payload: AiVecDoc): Promise<string> => {
    return request({
      url: '/v1/astro/ai-vec-doc/create',
      method: 'post',
      data: payload
    })
  },

  update: (payload: AiVecDoc): Promise<string> => {
    return request({
      url: '/v1/astro/ai-vec-doc/update',
      method: 'post',
      data: payload
    })
  },

  delete: (ids: Array<number | string>): Promise<string> => {
    const joined = ids.map((x) => String(x)).join(',')
    return request({
      url: `/v1/astro/ai-vec-doc/delete/${joined}`,
      method: 'delete'
    })
  },

  upload: (file: File | Blob, collectionId: number | string): Promise<AiVecDoc> => {
    const fd = new FormData()
    fd.append('file', file)
    fd.append('collectionId', String(collectionId))
    return request({
      url: '/v1/astro/ai-vec-doc/upload',
      method: 'post',
      data: fd,
      timeout: 120000
    })
  },

  vectorize: (id: number | string): Promise<string> => {
    return request({
      url: '/v1/astro/ai-vec-doc/vectorize',
      method: 'post',
      data: { id },
      timeout: 300000
    })
  }
}
