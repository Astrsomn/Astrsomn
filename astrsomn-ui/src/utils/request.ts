import axios from 'axios'
import type { AxiosInstance, AxiosResponse } from 'axios'
import { WORKSPACE_ENV_HEADER, WORKSPACE_ENV_STORAGE_KEY } from '@/constants/workspaceEnv'

const instance: AxiosInstance = axios.create({
  // 后端接口是完整前缀，如 /v1/astro/auth/...
  baseURL: '',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      const ws = localStorage.getItem(WORKSPACE_ENV_STORAGE_KEY)
      if (ws != null && String(ws).trim() !== '') {
        config.headers[WORKSPACE_ENV_HEADER] = String(ws).trim()
      }
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

instance.interceptors.response.use(
  (response: AxiosResponse<any>) => {
    const data = response.data

    // 拦截器/过滤器直接返回的结构：{ code, message, data? }
    if (data && typeof data.code === 'number') {
      if (data.code === 200) {
        return data.data
      }
      return Promise.reject(new Error(data.message || '请求失败'))
    }

    // 后端 controller 返回的结构：{ success, message, data }
    if (data && typeof data.success === 'boolean') {
      if (data.success) {
        return data.data
      }
      return Promise.reject(new Error(data.message || '请求失败'))
    }

    // 兜底：尽量返回 data.data 或原样返回
    return data?.data ?? data
  },
  (error) => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          localStorage.removeItem('token')
          if (window.location.pathname !== '/login') {
            window.location.href = '/login'
          }
          break
        case 403:
          console.error('没有权限访问')
          break
        case 404:
          console.error('请求的资源不存在')
          break
        case 500:
          console.error('服务器错误')
          break
        default:
          console.error('请求错误:', error.response.data?.message || '未知错误')
      }
    } else {
      console.error('网络错误:', error.message)
    }

    // 让调用方（例如 Login.vue）能拿到后端 message
    const respData = error?.response?.data as any
    const serverMessage = respData?.message
    if (serverMessage) {
      return Promise.reject(new Error(serverMessage))
    }

    return Promise.reject(error)
  }
)

export default instance
