import request from '@/utils/request'

export type AiMcp = {
  id?: number | string
  mcpKey?: string
  serverName?: string
  description?: string
  /** SSE / STEAMABLE / STDIO */
  type?: string
  sseAddress?: string
  requestHeaderConfig?: string
  /** 1 启用 0 停用 */
  enabled?: number
  command?: string
  args?: string
  envVars?: string
}

export type PageResponse<T> = {
  total: number
  pageSize: number
  pageNum: number
  pages: number
  hasNext: boolean
  list: T[]
}

export const aiMcpApi = {
  queryPage: (payload: unknown): Promise<PageResponse<AiMcp>> => {
    return request({
      url: '/v1/astro/ai-mcp/queryPage',
      method: 'post',
      data: payload
    })
  },

  detail: (id: number | string): Promise<AiMcp> => {
    return request({
      url: `/v1/astro/ai-mcp/detail?id=${encodeURIComponent(String(id))}`,
      method: 'get'
    })
  },

  create: (payload: AiMcp): Promise<string> => {
    return request({
      url: '/v1/astro/ai-mcp/create',
      method: 'post',
      data: payload
    })
  },

  update: (payload: AiMcp): Promise<string> => {
    return request({
      url: '/v1/astro/ai-mcp/update',
      method: 'post',
      data: payload
    })
  },

  delete: (ids: Array<number | string>): Promise<string> => {
    const joined = ids.map((x) => String(x)).join(',')
    return request({
      url: `/v1/astro/ai-mcp/delete/${joined}`,
      method: 'delete'
    })
  }
}
