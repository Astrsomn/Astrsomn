import { ref } from 'vue'
import type { Layout } from 'grid-layout-plus'

export type DashboardShortcutVariant = 'compact' | 'mcp' | 'gradient-market' | 'gradient-apps'

export interface DashboardShortcutItem {
  i: string
  route: string
  variant: DashboardShortcutVariant
  /** 副标题（可选，默认用 management 描述） */
  subtitle?: string
  x: number
  y: number
  w: number
  h: number
  minW?: number
  minH?: number
}

const STORAGE_KEY = 'astrsomn.dashboard.shortcuts.v1'
const STORAGE_VERSION = 1

/** 同页内添加/重置后刷新首页栅格 */
export const dashboardShortcutsRevision = ref(0)

export const DEFAULT_SHORTCUT_ITEMS: DashboardShortcutItem[] = [
  {
    i: 'sc_kb',
    route: '/admin/knowledge-bases',
    variant: 'compact',
    subtitle: '12 个集群',
    x: 0,
    y: 0,
    w: 3,
    h: 1,
    minW: 2,
    minH: 1,
  },
  {
    i: 'sc_models',
    route: '/admin/models',
    variant: 'compact',
    subtitle: '8 个模型',
    x: 3,
    y: 0,
    w: 3,
    h: 1,
    minW: 2,
    minH: 1,
  },
  {
    i: 'sc_ftl',
    route: '/admin/templates',
    variant: 'compact',
    subtitle: '24 个模板',
    x: 6,
    y: 0,
    w: 3,
    h: 1,
    minW: 2,
    minH: 1,
  },
  {
    i: 'sc_prompts',
    route: '/admin/prompts',
    variant: 'compact',
    subtitle: '版本化模板与预设',
    x: 9,
    y: 0,
    w: 3,
    h: 1,
    minW: 2,
    minH: 1,
  },
  {
    i: 'sc_mcp',
    route: '/admin/mcp',
    variant: 'mcp',
    x: 0,
    y: 1,
    w: 3,
    h: 1,
    minW: 2,
    minH: 1,
  },
  {
    i: 'sc_tools',
    route: '/admin/tools',
    variant: 'gradient-market',
    x: 3,
    y: 1,
    w: 3,
    h: 1,
    minW: 2,
    minH: 1,
  },
  {
    i: 'sc_apps',
    route: '/admin/resource-library',
    variant: 'gradient-apps',
    x: 6,
    y: 1,
    w: 3,
    h: 1,
    minW: 2,
    minH: 1,
  },
]

function cloneDefaults(): DashboardShortcutItem[] {
  return DEFAULT_SHORTCUT_ITEMS.map((row) => ({ ...row }))
}

export function inferShortcutVariant(route: string): DashboardShortcutVariant {
  if (route === '/admin/mcp') return 'mcp'
  if (route === '/admin/tools') return 'gradient-market'
  if (route === '/admin/resource-library') return 'gradient-apps'
  return 'compact'
}

function makeStableId(route: string): string {
  return `sc_${route.replace(/^\/admin\/?/, '').replace(/\//g, '_') || 'root'}`
}

function collides(
  items: DashboardShortcutItem[],
  x: number,
  y: number,
  w: number,
  h: number,
  ignoreI?: string,
): boolean {
  for (const it of items) {
    if (it.i === ignoreI) continue
    const ox2 = it.x + it.w
    const oy2 = it.y + it.h
    const nx2 = x + w
    const ny2 = y + h
    if (x < ox2 && nx2 > it.x && y < oy2 && ny2 > it.y) return true
  }
  return false
}

function findNextSlot(items: DashboardShortcutItem[], w: number, h: number): { x: number; y: number } {
  for (let y = 0; y < 80; y++) {
    for (let x = 0; x <= 12 - w; x++) {
      if (!collides(items, x, y, w, h)) return { x, y }
    }
  }
  return { x: 0, y: 0 }
}

export function itemsToLayout(items: DashboardShortcutItem[]): Layout {
  return items.map(({ x, y, w, h, i, minW, minH }) => ({
    x,
    y,
    w,
    h,
    i,
    minW: minW ?? 2,
    minH: minH ?? 1,
    maxW: 12,
    maxH: 6,
  }))
}

export function loadShortcutItems(): DashboardShortcutItem[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) return cloneDefaults()
    const data = JSON.parse(raw) as { v?: number; items?: DashboardShortcutItem[] }
    if (data.v !== STORAGE_VERSION || !Array.isArray(data.items) || data.items.length === 0) {
      return cloneDefaults()
    }
    return data.items.map((row) => ({
      ...row,
      minW: row.minW ?? 2,
      minH: row.minH ?? 1,
    }))
  } catch {
    return cloneDefaults()
  }
}

export function saveShortcutItems(items: DashboardShortcutItem[]) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify({ v: STORAGE_VERSION, items }))
}

let saveTimer: ReturnType<typeof setTimeout> | null = null

export function debouncedSaveShortcutItems(items: DashboardShortcutItem[]) {
  if (saveTimer) clearTimeout(saveTimer)
  saveTimer = setTimeout(() => {
    saveShortcutItems(items)
    saveTimer = null
  }, 400)
}

export function flushShortcutSave(items: DashboardShortcutItem[]) {
  if (saveTimer) {
    clearTimeout(saveTimer)
    saveTimer = null
  }
  saveShortcutItems(items)
}

export function mergeLayoutIntoItems(layout: Layout, items: DashboardShortcutItem[]): DashboardShortcutItem[] {
  const m = new Map(layout.map((cell) => [String(cell.i), cell]))
  return items.map((it) => {
    const L = m.get(it.i)
    if (!L) return it
    return { ...it, x: L.x, y: L.y, w: L.w, h: L.h }
  })
}

/** 从应用库固定到控制台；已存在同 route 则返回 false */
export function addDashboardShortcut(route: string): boolean {
  const items = loadShortcutItems()
  if (items.some((it) => it.route === route)) return false
  let id = makeStableId(route)
  if (items.some((it) => it.i === id)) id = `${id}_${Date.now()}`
  const variant = inferShortcutVariant(route)
  const pos = findNextSlot(items, 3, 1)
  items.push({
    i: id,
    route,
    variant,
    x: pos.x,
    y: pos.y,
    w: 3,
    h: 1,
    minW: 2,
    minH: 1,
  })
  saveShortcutItems(items)
  dashboardShortcutsRevision.value += 1
  return true
}

export function removeDashboardShortcut(i: string) {
  const items = loadShortcutItems().filter((it) => it.i !== i)
  saveShortcutItems(items)
  dashboardShortcutsRevision.value += 1
}

/** 恢复默认布局与入口集合 */
export function resetDashboardShortcutsToDefault() {
  saveShortcutItems(cloneDefaults())
  dashboardShortcutsRevision.value += 1
}
