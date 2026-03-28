import request from '@/utils/request'

export type AiAgent = {
  id?: number | string
  envCode?: string
  createUser?: string
  createTime?: string
  agentKey?: string
  agentName?: string
  description?: string
  modelKey?: number | string
  promptKey?: string
  configParams?: string
  status?: string
  knowledgeBaseKeys?: string
  toolKeys?: string
  mcpKeys?: string
  memoryMode?: string
  memoryWindowSize?: string
  maxTokens?: number
  temperature?: number
  presencePenalty?: number
  frequencyPenalty?: number
  stopSequences?: string
  seed?: number
  topP?: number
  topK?: number
  enableStream?: boolean
  interfaceClass?: string
  /** queryPage 联表：AI_MODEL */
  modelName?: string
  /** queryPage 联表：AI_PROMPT 当前版本标题 */
  promptTitle?: string
  toolNames?: string
  mcpNames?: string
}

export type PageResponse<T> = {
  total: number
  pageSize: number
  pageNum: number
  pages: number
  hasNext: boolean
  list: T[]
}

export const aiAgentApi = {
  queryPage: (payload: any): Promise<PageResponse<AiAgent>> => {
    return request({
      url: '/v1/astro/ai-agent/queryPage',
      method: 'post',
      data: payload
    })
  },

  detail: (id: number | string): Promise<AiAgent> => {
    return request({
      url: `/v1/astro/ai-agent/detail?id=${encodeURIComponent(String(id))}`,
      method: 'get'
    })
  },

  create: (payload: AiAgent): Promise<string> => {
    return request({
      url: '/v1/astro/ai-agent/create',
      method: 'post',
      data: payload
    })
  },

  update: (payload: AiAgent): Promise<string> => {
    return request({
      url: '/v1/astro/ai-agent/update',
      method: 'post',
      data: payload
    })
  },

  delete: (ids: Array<number | string>): Promise<string> => {
    const joined = ids.map((x) => String(x)).join(',')
    return request({
      url: `/v1/astro/ai-agent/delete/${joined}`,
      method: 'delete'
    })
  }
}

