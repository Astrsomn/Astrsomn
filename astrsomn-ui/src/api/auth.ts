import request from '@/utils/request'
import type {LoginRequest, LoginResponse} from '@/types'

type BackendLoginResponse = {
    userId: number | string
    username: string
    email?: string
    adminFlag?: string
    userRole?: string
    token: string
    expiresIn?: number
}

const toUiLoginResponse = (backend: BackendLoginResponse): LoginResponse => {
    return {
        token: backend.token,
        userInfo: {
            id: String(backend.userId),
            username: backend.username,
            email: backend.email,
            userRole: backend.userRole
        }
    }
}

export const login = (data: LoginRequest): Promise<LoginResponse> => {
    return request({
        url: '/v1/astro/auth/login',
        method: 'post',
        data
    }).then((backend: BackendLoginResponse) => toUiLoginResponse(backend))
}

export const logout = (): Promise<void> => {
    return request({
        url: '/v1/astro/auth/logout',
        method: 'post'
    }).then(() => {
    })
}

export const getUserInfo = (): Promise<any> => {
    return request({
        url: '/v1/astro/auth/current-user',
        method: 'get'
    }).then((backend: BackendLoginResponse) => toUiLoginResponse(backend).userInfo)
}

export type WorkspaceEnvContext = {
    effectiveEnvCode: string
    canSwitchWorkspace: boolean
    userEnvCode?: string | null
}

/** 当前请求生效的数据环境（与后端租户一致） */
export const getWorkspaceEnv = (): Promise<WorkspaceEnvContext> => {
    return request({
        url: '/v1/astro/auth/workspace-env',
        method: 'get'
    })
}
