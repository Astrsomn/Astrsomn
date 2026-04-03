/**
 * 控制台栅格模块类型，命名对齐后端实体（如 AiAgentEntity → AiAgent）。
 */
export type DashboardShortcutVariant = 'compact' | 'mcp' | 'gradient-market' | 'gradient-apps'

/** 内置业务模块（无 route） */
export type DashboardBuiltinModuleKind = 'AiAgent' | 'AiInstance' | 'ConsoleSystemLoad'

export type DashboardModuleKind = DashboardBuiltinModuleKind | 'RouteShortcut'

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

export interface DashboardAiInstanceItem extends DashboardLayoutItemBase {
  kind: 'AiInstance'
}

export interface DashboardConsoleSystemLoadItem extends DashboardLayoutItemBase {
  kind: 'ConsoleSystemLoad'
}

export interface DashboardRouteShortcutItem extends DashboardLayoutItemBase {
  kind: 'RouteShortcut'
  route: string
  shortcutVariant: DashboardShortcutVariant
  subtitle?: string
}

export type DashboardLayoutItem =
  | DashboardAiAgentItem
  | DashboardAiInstanceItem
  | DashboardConsoleSystemLoadItem
  | DashboardRouteShortcutItem
