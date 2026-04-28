import request from '@/utils/request'

export type AiWorkflow = {
  id?: number | string
  workflowKey?: string
  workflowName?: string
  versionNo?: number
  description?: string
  category?: string
  graphJson?: string
  status?: string
}

export type PageResponse<T> = {
  total: number
  pageSize: number
  pageNum: number
  pages: number
  hasNext: boolean
  list: T[]
}

export type WorkflowTestRunResult = {
  status?: string
  lastNodeId?: string
  message?: string
  variables?: Record<string, unknown>
}

export const aiWorkflowApi = {
  queryPage: (payload: unknown): Promise<PageResponse<AiWorkflow>> => {
    return request({
      url: '/v1/astro/ai-workflow/queryPage',
      method: 'post',
      data: payload
    })
  },

  detail: (id: number | string): Promise<AiWorkflow> => {
    return request({
      url: `/v1/astro/ai-workflow/detail?id=${encodeURIComponent(String(id))}`,
      method: 'get'
    })
  },

  create: (payload: Partial<AiWorkflow>): Promise<string> => {
    return request({
      url: '/v1/astro/ai-workflow/create',
      method: 'post',
      data: payload
    })
  },

  update: (payload: Partial<AiWorkflow>): Promise<string> => {
    return request({
      url: '/v1/astro/ai-workflow/update',
      method: 'post',
      data: payload
    })
  },

  delete: (ids: Array<number | string>): Promise<string> => {
    const joined = ids.map((x) => String(x)).join(',')
    return request({
      url: `/v1/astro/ai-workflow/delete/${joined}`,
      method: 'delete'
    })
  },

  publish: (id: number | string): Promise<string> => {
    return request({
      url: '/v1/astro/ai-workflow/publish',
      method: 'post',
      data: { id }
    })
  },

  /** 按 id 测当前图（含草稿）；或仅传 workflowKey 测已发布版本 */
  testRun: (payload: {
    id?: number | string
    workflowKey?: string
    userMessage?: string
    memoryKey?: string
  }): Promise<WorkflowTestRunResult> => {
    return request({
      url: '/v1/astro/ai-workflow/test-run',
      method: 'post',
      data: payload,
      timeout: 120000
    })
  }
}
