import { getWorkspaceEnv } from '@/api/auth'
import { WORKSPACE_ENV_STORAGE_KEY } from '@/constants/workspaceEnv'

/**
 * 若本地未持久化工作空间环境，则请求后端当前生效环境并写入 localStorage，
 * 使后续 API 自动带上 {@link WORKSPACE_ENV_HEADER}，与后端租户/EnvScope 一致。
 */
export async function ensureWorkspaceEnvInStorage(): Promise<void> {
  const existing = localStorage.getItem(WORKSPACE_ENV_STORAGE_KEY)?.trim()
  if (existing) return
  try {
    const w = await getWorkspaceEnv()
    if (w?.effectiveEnvCode) {
      localStorage.setItem(WORKSPACE_ENV_STORAGE_KEY, String(w.effectiveEnvCode).trim())
    }
  } catch {
    /* 未登录或接口失败时跳过，由后续请求使用后端默认环境 */
  }
}
