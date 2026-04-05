/**
 * 控制台栅格模块类型：内置三块 + 与应用库 management 条目对应的页面模块（命名贴近实体）。
 */
export type DashboardBuiltinModuleKind = 'AiAgent'

export const DASHBOARD_PAGE_MODULE_KINDS = [
  'AiModel',
  'AiAccount',
  'AiMcp',
  'AiTool',
  'AiPrompt',
  'AiTemplate',
  'KnowledgeBase',
  'AiDocument',
  'SecurityPolicy',
  'TraceInsight',
  'AiWorkflow',
  'AiWorkflowTest',
  'SystemUser',
  'SystemEnv',
  'SystemConfig',
  'SystemExtension',
  'ConsoleResourceLibrary',
] as const

export type DashboardPageModuleKind = (typeof DASHBOARD_PAGE_MODULE_KINDS)[number]

export type DashboardModuleKind = DashboardBuiltinModuleKind | DashboardPageModuleKind

export function isDashboardPageModuleKind(k: string): k is DashboardPageModuleKind {
  return (DASHBOARD_PAGE_MODULE_KINDS as readonly string[]).includes(k)
}

export interface DashboardLayoutItemBase {
  i: string
  x: number
  y: number
  w: number
  h: number
  minW?: number
  minH?: number
  maxW?: number
  maxH?: number
}

export interface DashboardAiAgentItem extends DashboardLayoutItemBase {
  kind: 'AiAgent'
}



export interface DashboardPageModuleItem extends DashboardLayoutItemBase {
  kind: DashboardPageModuleKind
  route: string
}

export type DashboardLayoutItem =
  | DashboardAiAgentItem
  | DashboardPageModuleItem
