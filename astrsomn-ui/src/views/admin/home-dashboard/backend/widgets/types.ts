import type { DashboardPageModuleKind } from '../dashboardLayoutTypes'
import type { ManagementEntry } from '../../resource-library/management.ts'

export interface ConsolePageModuleProps {
  pageKind: DashboardPageModuleKind
  route: string
  gridW: number
  gridH: number
  editMode?: boolean
  entryByRoute?: Partial<Record<string, ManagementEntry>>
}

export interface ModuleHeaderProps {
  tier: number
  title: string
  icon?: any
  description?: string
}

export interface ModuleMetricsProps {
  items: Array<{ label: string; value: string }>
  layout: 'landscape' | 'portrait' | 'square'
}

export interface ModuleAdaptorContext {
  tier: number
  layoutMode: 'landscape' | 'portrait' | 'square'
  isIconOnly: boolean
  showDesc: boolean
  showMetrics: boolean
  showFootnote: boolean
  maxMetrics: number
  isWideLayout: boolean
  isTallLayout: boolean
}