import request from '@/utils/request'
import type { LoginRequest, LoginResponse } from '@/types'

export const login = (data: LoginRequest): Promise<LoginResponse> => {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export const logout = (): Promise<void> => {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

export const getUserInfo = (): Promise<any> => {
  return request({
    url: '/auth/user-info',
    method: 'get'
  })
}
