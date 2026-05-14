export interface LoginRequest {
    username: string
    password: string
}

export interface LoginResponse {
    token: string
    userInfo: {
        id: string
        username: string
        email?: string
        /** SUPER_ADMIN | ENV_ADMIN | USER */
        userRole?: string
    }
}

export interface ApiResponse<T = any> {
    code: number
    message: string
    data: T
}
