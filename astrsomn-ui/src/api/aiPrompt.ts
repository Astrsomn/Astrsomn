import request from '@/utils/request'

export type AiPrompt = {
  id?: number | string
  envCode?: string
  createUser?: string
  promptKey?: string
  promptTitle?: string
  promptContent?: string
  scene?: string
  enabledFlag?: string
  version?: number
  createTime?: string
}

export type PageResponse<T> = {
  total: number
  pageSize: number
  pageNum: number
  pages: number
  hasNext: boolean
  list: T[]
}

export const aiPromptApi = {
  queryPage: (payload: unknown): Promise<PageResponse<AiPrompt>> => {
    return request({
      url: '/v1/astro/ai-promopt/queryPage',
      method: 'post',
      data: payload
    })
  },

  detail: (id: number | string): Promise<AiPrompt> => {
    return request({
      url: `/v1/astro/ai-promopt/detail?id=${encodeURIComponent(String(id))}`,
      method: 'get'
    })
  },

  create: (payload: AiPrompt): Promise<string> => {
    return request({
      url: '/v1/astro/ai-promopt/create',
      method: 'post',
      data: payload
    })
  },

  update: (payload: AiPrompt): Promise<string> => {
    return request({
      url: '/v1/astro/ai-promopt/update',
      method: 'post',
      data: payload
    })
  },

  delete: (ids: Array<number | string>): Promise<string> => {
    const joined = ids.map((x) => String(x)).join(',')
    return request({
      url: `/v1/astro/ai-promopt/delete/${joined}`,
      method: 'delete'
    })
  },

  /** 同一 promptKey 下全部历史版本（版本号倒序） */
  history: (promptKey: string, envCode?: string): Promise<AiPrompt[]> => {
    const q = new URLSearchParams()
    q.set('promptKey', promptKey)
    if (envCode) {
      q.set('envCode', envCode)
    }
    return request({
      url: `/v1/astro/ai-promopt/history?${q.toString()}`,
      method: 'get'
    })
  },

  improvePrompt: (promptContent: string): Promise<string> => {
    return request({
      url: '/v1/astro/ai-promopt/improvePrompt',
      method: 'post',
      data: { promptContent }
    })
  },

  querySceneTags: (): Promise<string[]> => {
    return request({
      url: '/v1/astro/ai-promopt/scene-tags',
      method: 'get'
    })
  }
}
