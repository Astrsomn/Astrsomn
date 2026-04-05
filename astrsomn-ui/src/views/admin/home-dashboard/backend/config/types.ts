import type { DashboardModuleKind, DashboardPageModuleKind } from '../types/dashboardLayoutTypes'

export interface DashboardLayoutConfig {
  version: number
  description: string
  layout: {
    cols: number
    rowHeight: number
    gap: number
    maxRows: number
  }
  modules: DashboardModuleConfig[]
}

export interface DashboardModuleConfig {
  kind: DashboardModuleKind
  x: number
  y: number
  w: number
  h: number
  minW?: number
  minH?: number
  maxW?: number
  maxH?: number
  route?: string
}

export function isPageModuleConfig(
  config: DashboardModuleConfig
): config is DashboardModuleConfig & { kind: DashboardPageModuleKind; route: string } {
  return typeof config.route === 'string' && config.route.length > 0
}
