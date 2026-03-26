import request from '@/utils/request'

export type AiMcp = {
  id?: number | string
  mcpKey?: string
  serverName?: string
  description?: string
  type?: string
  enabled?: number
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
  queryPage: (payload: any): Promise<PageResponse<AiMcp>> => {
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
  }
}
