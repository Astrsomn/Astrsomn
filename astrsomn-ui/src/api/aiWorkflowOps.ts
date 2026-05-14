import request from '@/utils/request'

export type OpsPageResponse<T> = {
    total: number
    pageSize: number
    pageNum: number
    pages: number
    hasNext: boolean
    list: T[]
}

type OpsQueryPayload<TParam extends Record<string, unknown>> = {
    pageNo: number
    pageSize: number
    param?: TParam
}

const queryPage = <T, TParam extends Record<string, unknown>>(url: string, payload: OpsQueryPayload<TParam>): Promise<OpsPageResponse<T>> =>
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

const deleteByIds = (urlPrefix: string, ids: Array<number | string>): Promise<string> => {
    const joined = ids.map((x) => String(x)).join(',')
    return request({
        url: `${urlPrefix}/delete/${joined}`,
        method: 'delete'
    })
}

export type BaseEntityFields = {
    id?: number | string
    createTime?: string
    updateTime?: string
    createUser?: string
    updateUser?: string
    deleted?: boolean
    envCode?: string
}

export type BizIdempotentRecord = BaseEntityFields & {
    idempotentKey?: string
    bizType?: string
    bizId?: string
    requestHash?: string
    resultRef?: string
    expireAtMs?: number
}

export type BizIdempotentQuery = {
    idempotentKey?: string
    bizType?: string
    bizId?: string
}

export type InstanceEventRecord = BaseEntityFields & {
    instanceId?: number | string
    eventType?: string
    nodeId?: string
    eventTimeMs?: number
    eventDataJson?: string
    traceId?: string
}

export type InstanceEventQuery = {
    instanceId?: number | string
    eventType?: string
    nodeId?: string
    traceId?: string
}

export type MsgOutboxRecord = BaseEntityFields & {
    bizType?: string
    bizId?: string
    topicOrEndpoint?: string
    payloadJson?: string
    msgStatus?: string
    retryCount?: number
    nextRetryTimeMs?: number
    lastError?: string
    idempotentKey?: string
}

export type MsgOutboxQuery = {
    bizType?: string
    bizId?: string
    msgStatus?: string
    idempotentKey?: string
}

export type TimerJobRecord = BaseEntityFields & {
    instanceId?: number | string
    nodeId?: string
    jobType?: string
    dueTimeMs?: number
    jobStatus?: string
    retryCount?: number
    maxRetry?: number
    lastError?: string
    payloadJson?: string
}

export type TimerJobQuery = {
    instanceId?: number | string
    nodeId?: string
    jobType?: string
    jobStatus?: string
}

export const aiWorkflowOpsApi = {
    bizIdempotentQueryPage: (payload: OpsQueryPayload<BizIdempotentQuery>): Promise<OpsPageResponse<BizIdempotentRecord>> =>
        queryPage('/v1/astro/ai-workflow/biz-idempotent/queryPage', payload),
    bizIdempotentDetail: (id: number | string): Promise<BizIdempotentRecord> =>
        detail('/v1/astro/ai-workflow/biz-idempotent/detail', id),
    bizIdempotentDelete: (ids: Array<number | string>): Promise<string> =>
        deleteByIds('/v1/astro/ai-workflow/biz-idempotent', ids),

    instanceEventQueryPage: (payload: OpsQueryPayload<InstanceEventQuery>): Promise<OpsPageResponse<InstanceEventRecord>> =>
        queryPage('/v1/astro/ai-workflow/instance-event/queryPage', payload),
    instanceEventDetail: (id: number | string): Promise<InstanceEventRecord> =>
        detail('/v1/astro/ai-workflow/instance-event/detail', id),
    instanceEventDelete: (ids: Array<number | string>): Promise<string> =>
        deleteByIds('/v1/astro/ai-workflow/instance-event', ids),

    msgOutboxQueryPage: (payload: OpsQueryPayload<MsgOutboxQuery>): Promise<OpsPageResponse<MsgOutboxRecord>> =>
        queryPage('/v1/astro/ai-workflow/msg-outbox/queryPage', payload),
    msgOutboxDetail: (id: number | string): Promise<MsgOutboxRecord> =>
        detail('/v1/astro/ai-workflow/msg-outbox/detail', id),
    msgOutboxDelete: (ids: Array<number | string>): Promise<string> =>
        deleteByIds('/v1/astro/ai-workflow/msg-outbox', ids),

    timerJobQueryPage: (payload: OpsQueryPayload<TimerJobQuery>): Promise<OpsPageResponse<TimerJobRecord>> =>
        queryPage('/v1/astro/ai-workflow/timer-job/queryPage', payload),
    timerJobDetail: (id: number | string): Promise<TimerJobRecord> =>
        detail('/v1/astro/ai-workflow/timer-job/detail', id),
    timerJobDelete: (ids: Array<number | string>): Promise<string> =>
        deleteByIds('/v1/astro/ai-workflow/timer-job', ids)
}

