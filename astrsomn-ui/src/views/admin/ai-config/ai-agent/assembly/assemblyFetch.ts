/**
 * 查询参数与 AgentResourcePickModal / AiResourcePickerModal 中对应资源一致。
 */
import { aiInstanceApi } from '@/api/aiInstance.ts'
import { aiToolApi } from '@/api/aiTool.ts'
import { aiMcpApi } from '@/api/aiMcp.ts'
import { aiPromptApi } from '@/api/aiPrompt.ts'
import type { AiInstance } from '@/api/aiInstance'
import type { AiTool } from '@/api/aiTool'
import type { AiMcp } from '@/api/aiMcp'
import type { AiPrompt } from '@/api/aiPrompt'
import type { InstanceModelType } from './assemblyTypes'

export type PagedResult<T> = { list: T[]; total: number }

function buildPayload(pageNo: number, pageSize: number, param: Record<string, unknown>) {
  return { pageNo, pageSize, param }
}

/** 推理预设分页 */
export async function fetchInstancesPaged(
  modelType: InstanceModelType,
  keyword: string | undefined,
  pageNo: number,
  pageSize: number
): Promise<PagedResult<AiInstance>> {
  const resp = await aiInstanceApi.queryPage(
    buildPayload(pageNo, pageSize, {
      instanceName: keyword?.trim() || undefined,
      modelType,
      status: 'enabled'
    })
  )
  return { list: resp.list || [], total: Number(resp.total) || 0 }
}

/** 工具分页 */
export async function fetchToolsPaged(
  keyword: string | undefined,
  pageNo: number,
  pageSize: number
): Promise<PagedResult<AiTool>> {
  const resp = await aiToolApi.queryPage(
    buildPayload(pageNo, pageSize, {
      toolName: keyword?.trim() || undefined
    })
  )
  return { list: resp.list || [], total: Number(resp.total) || 0 }
}

/** MCP 分页 */
export async function fetchMcpsPaged(
  keyword: string | undefined,
  pageNo: number,
  pageSize: number
): Promise<PagedResult<AiMcp>> {
  const resp = await aiMcpApi.queryPage(
    buildPayload(pageNo, pageSize, {
      mcpKey: keyword?.trim() || undefined
    })
  )
  return { list: resp.list || [], total: Number(resp.total) || 0 }
}

/** Prompt 分页 */
export async function fetchPromptsPaged(
  keyword: string | undefined,
  pageNo: number,
  pageSize: number
): Promise<PagedResult<AiPrompt>> {
  const resp = await aiPromptApi.queryPage(
    buildPayload(pageNo, pageSize, {
      promptTitle: keyword?.trim() || undefined
    })
  )
  return { list: resp.list || [], total: Number(resp.total) || 0 }
}
