import { ref } from 'vue'
import type { Layout } from 'grid-layout-plus'
import type {
  DashboardLayoutItem,
  DashboardRouteShortcutItem,
  DashboardShortcutVariant,
} from './dashboardLayoutTypes'

export type { DashboardLayoutItem, DashboardShortcutVariant } from './dashboardLayoutTypes'

const STORAGE_V2_KEY = 'astrsomn.dashboard.layout.v2'
const STORAGE_V1_KEY = 'astrsomn.dashboard.shortcuts.v1'
const STORAGE_VERSION = 2

export const dashboardLayoutRevision = ref(0)
/** @deprecated 使用 dashboardLayoutRevision */
export const dashboardShortcutsRevision = dashboardLayoutRevision

const DEFAULT_BUILTIN_ITEMS: DashboardLayoutItem[] = [
  {
    i: 'mod_AiAgent',
    kind: 'AiAgent',
    x: 0,
    y: 0,
    w: 8,
    h: 3,
    minW: 3,
    minH: 2,
    maxW: 12,
    maxH: 6,
  },
  {
    i: 'mod_AiInstance',
    kind: 'AiInstance',
    x: 8,
    y: 0,
    w: 4,
    h: 2,
    minW: 2,
    minH: 1,
    maxW: 12,
    maxH: 6,
  },
  {
    i: 'mod_ConsoleSystemLoad',
    kind: 'ConsoleSystemLoad',
    x: 8,
    y: 2,
    w: 4,
    h: 2,
    minW: 2,
    minH: 1,
    maxW: 12,
    maxH: 6,
  },
]

const DEFAULT_SHORTCUT_ROWS: DashboardRouteShortcutItem[] = [
  {
    i: 'sc_kb',
    kind: 'RouteShortcut',
    route: '/admin/knowledge-bases',
    shortcutVariant: 'compact',
    subtitle: '12 个集群',
    x: 0,
    y: 4,
    w: 3,
    h: 1,
    minW: 1,
    minH: 1,
    maxW: 12,
    maxH: 6,
  },
  {
    i: 'sc_models',
    kind: 'RouteShortcut',
    route: '/admin/models',
    shortcutVariant: 'compact',
    subtitle: '8 个模型',
    x: 3,
    y: 4,
    w: 3,
    h: 1,
    minW: 1,
    minH: 1,
    maxW: 12,
    maxH: 6,
  },
  {
    i: 'sc_ftl',
    kind: 'RouteShortcut',
    route: '/admin/templates',
    shortcutVariant: 'compact',
    subtitle: '24 个模板',
    x: 6,
    y: 4,
    w: 3,
    h: 1,
    minW: 1,
    minH: 1,
    maxW: 12,
    maxH: 6,
  },
  {
    i: 'sc_prompts',
    kind: 'RouteShortcut',
    route: '/admin/prompts',
    shortcutVariant: 'compact',
    subtitle: '版本化模板与预设',
    x: 9,
    y: 4,
    w: 3,
    h: 1,
    minW: 1,
    minH: 1,
    maxW: 12,
    maxH: 6,
  },
  {
    i: 'sc_mcp',
    kind: 'RouteShortcut',
    route: '/admin/mcp',
    shortcutVariant: 'mcp',
    x: 0,
    y: 5,
    w: 3,
    h: 1,
    minW: 1,
    minH: 1,
    maxW: 12,
    maxH: 6,
  },
  {
    i: 'sc_tools',
    kind: 'RouteShortcut',
    route: '/admin/tools',
    shortcutVariant: 'gradient-market',
    x: 3,
    y: 5,
    w: 3,
    h: 1,
    minW: 1,
    minH: 1,
    maxW: 12,
    maxH: 6,
  },
  {
    i: 'sc_apps',
    kind: 'RouteShortcut',
    route: '/admin/resource-library',
    shortcutVariant: 'gradient-apps',
    x: 6,
    y: 5,
    w: 3,
    h: 1,
    minW: 1,
    minH: 1,
    maxW: 12,
    maxH: 6,
  },
]

export function defaultDashboardLayoutItems(): DashboardLayoutItem[] {
  return [...DEFAULT_BUILTIN_ITEMS.map((r) => ({ ...r })), ...DEFAULT_SHORTCUT_ROWS.map((r) => ({ ...r }))]
}

function isRouteShortcut(it: DashboardLayoutItem): it is DashboardRouteShortcutItem {
  return it.kind === 'RouteShortcut'
}

function normalizeItem(row: DashboardLayoutItem): DashboardLayoutItem {
  const base = {
    ...row,
    minW: row.minW ?? 1,
    minH: row.minH ?? 1,
    maxW: row.maxW ?? 12,
    maxH: row.maxH ?? 6,
  }
  return base as DashboardLayoutItem
}

export function itemsToLayout(items: DashboardLayoutItem[]): Layout {
  return items.map(({ x, y, w, h, i, minW, minH, maxW, maxH }) => ({
    x,
    y,
    w,
    h,
    i,
    minW: minW ?? 1,
    minH: minH ?? 1,
    maxW: maxW ?? 12,
    maxH: maxH ?? 6,
  }))
}

export function mergeLayoutIntoItems(layout: Layout, items: DashboardLayoutItem[]): DashboardLayoutItem[] {
  const m = new Map(layout.map((cell) => [String(cell.i), cell]))
  return items.map((it) => {
    const L = m.get(it.i)
    if (!L) return it
    return { ...it, x: L.x, y: L.y, w: L.w, h: L.h }
  })
}

export function loadDashboardLayoutItems(): DashboardLayoutItem[] {
  try {
    const rawV2 = localStorage.getItem(STORAGE_V2_KEY)
    if (rawV2) {
      const data = JSON.parse(rawV2) as { v?: number; items?: DashboardLayoutItem[] }
      if (data.v === STORAGE_VERSION && Array.isArray(data.items) && data.items.length > 0) {
        return data.items.map((row) => normalizeItem(row))
      }
    }

    const rawV1 = localStorage.getItem(STORAGE_V1_KEY)
    if (rawV1) {
      const data = JSON.parse(rawV1) as {
        v?: number
        items?: Array<{
          i: string
          route: string
          variant: DashboardShortcutVariant
          subtitle?: string
          x: number
          y: number
          w: number
          h: number
          minW?: number
          minH?: number
        }>
      }
      if (data.v === 1 && Array.isArray(data.items) && data.items.length > 0) {
        const migrated: DashboardLayoutItem[] = [
          ...DEFAULT_BUILTIN_ITEMS.map((r) => ({ ...r })),
          ...data.items.map(
            (it) =>
              ({
                i: it.i,
                kind: 'RouteShortcut' as const,
                route: it.route,
                shortcutVariant: it.variant,
                subtitle: it.subtitle,
                x: it.x,
                y: it.y + 4,
                w: it.w,
                h: it.h,
                minW: it.minW ?? 1,
                minH: it.minH ?? 1,
                maxW: 12,
                maxH: 6,
              }) satisfies DashboardRouteShortcutItem,
          ),
        ]
        saveDashboardLayoutItems(migrated)
        return migrated.map(normalizeItem)
      }
    }
  } catch {
    /* fallthrough */
  }

  return defaultDashboardLayoutItems().map(normalizeItem)
}

export function saveDashboardLayoutItems(items: DashboardLayoutItem[]) {
  localStorage.setItem(STORAGE_V2_KEY, JSON.stringify({ v: STORAGE_VERSION, items }))
}

let saveTimer: ReturnType<typeof setTimeout> | null = null

export function debouncedSaveDashboardItems(items: DashboardLayoutItem[]) {
  if (saveTimer) clearTimeout(saveTimer)
  saveTimer = setTimeout(() => {
    saveDashboardLayoutItems(items)
    saveTimer = null
  }, 400)
}

export function flushDashboardSave(items: DashboardLayoutItem[]) {
  if (saveTimer) {
    clearTimeout(saveTimer)
    saveTimer = null
  }
  saveDashboardLayoutItems(items)
}

function collides(
  items: DashboardLayoutItem[],
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

function findNextSlot(items: DashboardLayoutItem[], w: number, h: number): { x: number; y: number } {
  for (let y = 0; y < 120; y++) {
    for (let x = 0; x <= 12 - w; x++) {
      if (!collides(items, x, y, w, h)) return { x, y }
    }
  }
  return { x: 0, y: 0 }
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

/** 从应用库固定；已存在同 route 则 false */
export function addDashboardShortcut(route: string): boolean {
  const items = loadDashboardLayoutItems()
  if (items.some((it) => isRouteShortcut(it) && it.route === route)) return false
  let id = makeStableId(route)
  if (items.some((it) => it.i === id)) id = `${id}_${Date.now()}`
  const shortcutVariant = inferShortcutVariant(route)
  const pos = findNextSlot(items, 3, 1)
  const next: DashboardRouteShortcutItem = {
    i: id,
    kind: 'RouteShortcut',
    route,
    shortcutVariant,
    x: pos.x,
    y: pos.y,
    w: 3,
    h: 1,
    minW: 1,
    minH: 1,
    maxW: 12,
    maxH: 6,
  }
  items.push(next)
  saveDashboardLayoutItems(items)
  dashboardLayoutRevision.value += 1
  return true
}

export function removeDashboardItem(i: string) {
  const items = loadDashboardLayoutItems().filter((it) => it.i !== i)
  saveDashboardLayoutItems(items)
  dashboardLayoutRevision.value += 1
}

/** @deprecated 使用 removeDashboardItem */
export function removeDashboardShortcut(i: string) {
  removeDashboardItem(i)
}

export function resetDashboardLayoutToDefault() {
  saveDashboardLayoutItems(defaultDashboardLayoutItems().map(normalizeItem))
  dashboardLayoutRevision.value += 1
}

/** @deprecated 使用 resetDashboardLayoutToDefault */
export function resetDashboardShortcutsToDefault() {
  resetDashboardLayoutToDefault()
}
