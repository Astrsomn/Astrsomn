/**
 * 将 12 列栅格上的宽高映射为 1–3 档，供各 Dashboard 模块做样式适配。
 * 列：≤4 → 1，≤8 → 2，否则 3；行：≤1 → 1，2 → 2，否则 3。
 */
export type DashboardSizeTier = 1 | 2 | 3

export function gridColTier(w: number): DashboardSizeTier {
  const n = Math.max(1, Math.min(12, w))
  if (n <= 4) return 1
  if (n <= 8) return 2
  return 3
}

export function gridRowTier(h: number): DashboardSizeTier {
  const n = Math.max(1, h)
  if (n <= 1) return 1
  if (n === 2) return 2
  return 3
}

/** 组合 class 后缀，如 `c2-r3` */
export function dashboardSizeClassSuffix(w: number, h: number): string {
  return `c${gridColTier(w)}-r${gridRowTier(h)}`
}
