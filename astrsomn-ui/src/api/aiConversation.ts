import request from '@/utils/request'

export type AiConversation = {
  id?: number | string
  memoryKey?: string
  conversationContent?: string
  createTime?: string
  updateTime?: string
  createUser?: string
  updateUser?: string
  status?: string
}

export type AiConversationCreateRequestDTO = {
  memoryKey?: string
  conversationContent?: string
  status?: string
}

export type AiConversationUpdateRequestDTO = {
  id?: number | string
  memoryKey?: string
  conversationContent?: string
  status?: string
}

export type AiConversationQueryRequestDTO = {
  memoryKey?: string
  status?: string
  createUser?: string
  startTime?: string
  endTime?: string
}

export type PageResponse<T> = {
  total: number
  pageSize: number
  pageNum: number
  pages: number
  hasNext: boolean
  list: T[]
}

export const aiConversationApi = {
  queryPage: (payload: unknown): Promise<PageResponse<AiConversation>> => {
    return request({
      url: '/v1/astro/ai-conversation/queryPage',
      method: 'post',
      data: payload
    })
  },

  queryGroups: (payload: unknown): Promise<PageResponse<AiConversation>> => {
    return request({
      url: '/v1/astro/ai-conversation/queryGroups',
      method: 'post',
      data: payload
    })
  },

  detail: (id: number | string): Promise<AiConversation> => {
    return request({
      url: `/v1/astro/ai-conversation/detail?id=${encodeURIComponent(String(id))}`,
      method: 'get'
    })
  },

  recoverByMemoryKey: (memoryKey: string): Promise<AiConversation[]> => {
    return request({
      url: `/v1/astro/ai-conversation/recoverByMemoryKey?memoryKey=${encodeURIComponent(memoryKey)}`,
      method: 'get'
    })
  },

  create: (payload: AiConversationCreateRequestDTO): Promise<string> => {
    return request({
      url: '/v1/astro/ai-conversation/create',
      method: 'post',
      data: payload
    })
  },

  update: (payload: AiConversationUpdateRequestDTO): Promise<string> => {
    return request({
      url: '/v1/astro/ai-conversation/update',
      method: 'post',
      data: payload
    })
  },

  delete: (ids: Array<number | string>): Promise<string> => {
    const joined = ids.map((x) => String(x)).join(',')
    return request({
      url: `/v1/astro/ai-conversation/delete/${joined}`,
      method: 'delete'
    })
  }
}