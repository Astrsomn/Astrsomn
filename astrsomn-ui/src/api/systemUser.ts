import request from '@/utils/request'

export type SystemUser = {
    id?: number | string
    username?: string
    password?: string
    /** Y / N，与 userRole 联动由后端维护 */
    adminFlag?: string
    /** SUPER_ADMIN | ENV_ADMIN | USER */
    userRole?: string
    envCode?: string
    email?: string
    createUser?: string
    updateUser?: string
    deleted?: boolean
    createTime?: string
    updateTime?: string
    /** SystemUserResponseDTO：业务系统展示名 */
    systemDisplayName?: string
    /** ONLINE | OFFLINE | MAINTENANCE */
    systemStatus?: string
    todayApiCalls?: number
    errorRate?: number
    lastAccessTime?: string
}

/** 与 SystemUserQueryRequestDTO 对齐 */
export type SystemUserQueryParam = {
    username?: string
    adminFlag?: string
    email?: string
    userRole?: string
    envCode?: string
    deleted?: boolean
    /** 预留，与 Mapper SYSTEM_STATUS 对接后生效 */
    systemStatus?: string
}

export type SystemUserQueryPagePayload = {
    pageNo: number
    pageSize: number
    param?: SystemUserQueryParam
}

export type PageResponse<T> = {
    total: number
    pageSize: number
    pageNum: number
    pages: number
    hasNext: boolean
    list: T[]
}

/** 对应 SystemUserController：`/v1/astro/system-user`（与 AiToolController 等同前缀风格） */
export const systemUserApi = {
    queryPage: (payload: SystemUserQueryPagePayload): Promise<PageResponse<SystemUser>> => {
        return request({
            url: '/v1/astro/system-user/queryPage',
            method: 'post',
            data: payload
        })
    },

    detail: (id: number | string): Promise<SystemUser> => {
        return request({
            url: `/v1/astro/system-user/detail?id=${encodeURIComponent(String(id))}`,
            method: 'get'
        })
    },

    create: (payload: SystemUser): Promise<string> => {
        return request({
            url: '/v1/astro/system-user/create',
            method: 'post',
            data: payload
        })
    },

    update: (payload: SystemUser): Promise<string> => {
        return request({
            url: '/v1/astro/system-user/update',
            method: 'post',
            data: payload
        })
    },

    delete: (ids: Array<number | string>): Promise<string> => {
        const joined = ids.map((x) => String(x)).join(',')
        return request({
            url: `/v1/astro/system-user/delete/${joined}`,
            method: 'delete'
        })
    }
}
