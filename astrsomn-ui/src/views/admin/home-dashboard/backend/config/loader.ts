import type { DashboardLayoutItem } from '../dashboardLayoutTypes'
import type { DashboardLayoutConfig, DashboardModuleConfig } from './types'
import defaultLayoutConfig from './dashboard-layout.json'

export function loadDashboardLayoutConfig(): DashboardLayoutConfig {
  return defaultLayoutConfig as DashboardLayoutConfig
}

export function validateModuleConfig(config: DashboardModuleConfig): boolean {
  if (config.x < 0 || config.y < 0) return false
  if (config.w < 1 || config.h < 1) return false
  if (config.minW !== undefined && config.w < config.minW) return false
  if (config.minH !== undefined && config.h < config.minH) return false
  if (config.maxW !== undefined && config.w > config.maxW) return false
  if (config.maxH !== undefined && config.h > config.maxH) return false
  return true
}

export function convertConfigToLayoutItem(
  config: DashboardModuleConfig,
  index: number
): DashboardLayoutItem | null {
  if (!validateModuleConfig(config)) {
    console.warn(`Invalid module config at index ${index}:`, config)
    return null
  }

  // 特殊处理AiAgent模块，允许更大的尺寸
  const isAiAgent = config.kind === 'AiAgent'
  const maxWidth = isAiAgent ? 6 : 3
  const maxHeight = isAiAgent ? 4 : 3

  const base = {
    i: `${config.kind}-${index}`,
    x: config.x,
    y: config.y,
    w: Math.min(maxWidth, config.w),
    h: Math.min(maxHeight, config.h),
    minW: config.minW,
    minH: config.minH,
    maxW: config.maxW ? Math.min(maxWidth, config.maxW) : maxWidth,
    maxH: config.maxH ? Math.min(maxHeight, config.maxH) : maxHeight,
  }

  if (config.route) {
    return {
      ...base,
      kind: config.kind as any,
      route: config.route,
    }
  }

  return {
    ...base,
    kind: config.kind as any,
  }
}

export function loadLayoutFromConfig(): DashboardLayoutItem[] {
  const config = loadDashboardLayoutConfig()
  const items: DashboardLayoutItem[] = []

  config.modules.forEach((moduleConfig, index) => {
    const item = convertConfigToLayoutItem(moduleConfig, index)
    if (item) {
      items.push(item)
    }
  })

  return items
}

export function getLayoutConfigMetadata() {
  const config = loadDashboardLayoutConfig()
  return {
    version: config.version,
    description: config.description,
    cols: config.layout.cols,
    rowHeight: config.layout.rowHeight,
    gap: config.layout.gap,
    maxRows: config.layout.maxRows,
    moduleCount: config.modules.length,
  }
}
