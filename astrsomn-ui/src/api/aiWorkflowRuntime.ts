import request from '@/utils/request'

export type RuntimePageResponse<T> = {
    total: number
    pageSize: number
    pageNum: number
    pages: number
    hasNext: boolean
    list: T[]
}

export type WorkflowRuntimeRecord = {
    id?: number | string
    [key: string]: unknown
}

type RuntimeQueryPayload = {
    pageNo: number
    pageSize: number
    param?: Record<string, unknown>
}

const queryPage = <T>(url: string, payload: RuntimeQueryPayload): Promise<RuntimePageResponse<T>> =>
    request({
        url,
        method: 'post',
        data: payload
    })

const detail = <T>(url: string, id: number | string): Promise<T> =>
    request({
        url: `${url}?id=${encodeURIComponent(String(id))}`,
        method: 'get'
    })

export const aiWorkflowRuntimeApi = {
    deploymentQueryPage: (payload: RuntimeQueryPayload): Promise<RuntimePageResponse<WorkflowRuntimeRecord>> =>
        queryPage('/v1/astro/ai-workflow/deployment/queryPage', payload),
    deploymentDetail: (id: number | string): Promise<WorkflowRuntimeRecord> =>
        detail('/v1/astro/ai-workflow/deployment/detail', id),

    instanceQueryPage: (payload: RuntimeQueryPayload): Promise<RuntimePageResponse<WorkflowRuntimeRecord>> =>
        queryPage('/v1/astro/ai-workflow/instance/queryPage', payload),
    instanceDetail: (id: number | string): Promise<WorkflowRuntimeRecord> =>
        detail('/v1/astro/ai-workflow/instance/detail', id),

    nodeConfigQueryPage: (payload: RuntimeQueryPayload): Promise<RuntimePageResponse<WorkflowRuntimeRecord>> =>
        queryPage('/v1/astro/ai-workflow/node-config/queryPage', payload),
    nodeConfigDetail: (id: number | string): Promise<WorkflowRuntimeRecord> =>
        detail('/v1/astro/ai-workflow/node-config/detail', id),

    humanTaskQueryPage: (payload: RuntimeQueryPayload): Promise<RuntimePageResponse<WorkflowRuntimeRecord>> =>
        queryPage('/v1/astro/ai-workflow/human-task/queryPage', payload),
    humanTaskDetail: (id: number | string): Promise<WorkflowRuntimeRecord> =>
        detail('/v1/astro/ai-workflow/human-task/detail', id),

    nodeHistoryQueryPage: (payload: RuntimeQueryPayload): Promise<RuntimePageResponse<WorkflowRuntimeRecord>> =>
        queryPage('/v1/astro/ai-workflow/node-history/queryPage', payload),
    nodePublishHistoryQueryPage: (payload: RuntimeQueryPayload): Promise<RuntimePageResponse<WorkflowRuntimeRecord>> =>
        queryPage('/v1/astro/ai-workflow/node-history/queryPublishHistory', payload),
    nodeHistoryDetail: (id: number | string): Promise<WorkflowRuntimeRecord> =>
        detail('/v1/astro/ai-workflow/node-history/detail', id),

    publish: (payload: Record<string, unknown>): Promise<string> =>
        request({
            url: '/v1/astro/ai-workflow/publish',
            method: 'post',
            data: payload
        }),

    testRun: (payload: Record<string, unknown>): Promise<Record<string, unknown>> =>
        request({
            url: '/v1/astro/ai-workflow/test-run',
            method: 'post',
            data: payload
        })
}
