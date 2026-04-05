import { ref } from 'vue'
import type { Layout } from 'grid-layout-plus'
import type {
  DashboardLayoutItem,
  DashboardModuleKind,
  DashboardPageModuleItem,
} from './dashboardLayoutTypes'
import { isDashboardPageModuleKind } from './dashboardLayoutTypes'
import { PAGE_KIND_ROUTE, ROUTE_TO_PAGE_KIND } from './dashboardPageRegistry'
import { loadLayoutFromConfig } from './config'

export type { DashboardLayoutItem } from './dashboardLayoutTypes'

const STORAGE_V3_KEY = 'astrsomn.dashboard.layout.v3'
const STORAGE_V2_KEY = 'astrsomn.dashboard.layout.v2'
const STORAGE_V1_KEY = 'astrsomn.dashboard.shortcuts.v1'
const STORAGE_VERSION = 3

/** 默认占位：12 列栅格下每块 w=2、h=2（2×2） */
const DEFAULT_WH = 2

const DEFAULT_MODULE_ORDER: DashboardModuleKind[] = [
  'AiAgent',
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
]

export const dashboardLayoutRevision = ref(0)
/** @deprecated 使用 dashboardLayoutRevision */
export const dashboardShortcutsRevision = dashboardLayoutRevision

export function defaultDashboardLayoutItems(): DashboardLayoutItem[] {
  try {
    // 从JSON配置文件加载布局
    const configItems = loadLayoutFromConfig()
    if (configItems.length > 0) {
      return configItems
    }
  } catch (error) {
    console.warn('Failed to load layout from config:', error)
  }
  
  //  fallback: 生成默认布局
  const items: DashboardLayoutItem[] = []
  let x = 0
  let y = 0
  const w = DEFAULT_WH
  const h = DEFAULT_WH

  for (const kind of DEFAULT_MODULE_ORDER) {
    if (kind === 'AiAgent') {
      items.push({
        i: 'mod_AiAgent',
        kind: 'AiAgent',
        x,
        y,
        w,
        h,
        minW: 1,
        minH: 1,
        maxW: 12,
        maxH: 6,
      })
    }  else {
      items.push({
        i: `mod_${kind}`,
        kind,
        route: PAGE_KIND_ROUTE[kind],
        x,
        y,
        w,
        h,
        minW: 1,
        minH: 1,
        maxW: 12,
        maxH: 6,
      })
    }
    x += w
    if (x >= 12) {
      x = 0
      y += h
    }
  }
  return items
}

function normalizeItem(row: DashboardLayoutItem): DashboardLayoutItem {
  const base = {
    ...row,
    minW: row.minW ?? 1,
    minH: row.minH ?? 1,
    maxW: row.maxW ?? 12,
    maxH: row.maxH ?? 6,
  }
  if (isDashboardPageModuleKind(base.kind)) {
    const pi = base as DashboardPageModuleItem
    return {
      ...pi,
      route: pi.route || PAGE_KIND_ROUTE[pi.kind],
    }
  }
  return base as DashboardLayoutItem
}

function migrateV2ToV3Items(raw: unknown[]): DashboardLayoutItem[] {
  const out: DashboardLayoutItem[] = []
  for (const row of raw) {
    const it = row as Record<string, unknown> & { i?: string; kind?: string; route?: string }
    if (!it?.i || !it.kind) continue
    if (it.kind === 'RouteShortcut' && typeof it.route === 'string') {
      const k = ROUTE_TO_PAGE_KIND[it.route]
      if (k) {
        out.push(
          normalizeItem({
            i: String(it.i),
            kind: k,
            route: it.route,
            x: Number(it.x) || 0,
            y: Number(it.y) || 0,
            w: Number(it.w) || DEFAULT_WH,
            h: Number(it.h) || DEFAULT_WH,
            minW: Number(it.minW) || 1,
            minH: Number(it.minH) || 1,
            maxW: Number(it.maxW) || 12,
            maxH: Number(it.maxH) || 6,
          } as DashboardPageModuleItem),
        )
      }
      continue
    }
    if (
      it.kind === 'AiAgent' ||
      isDashboardPageModuleKind(it.kind)
    ) {
      out.push(normalizeItem(it as unknown as DashboardLayoutItem))
    }
  }
  return out.length > 0 ? out : defaultDashboardLayoutItems()
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
    const rawV3 = localStorage.getItem(STORAGE_V3_KEY)
    if (rawV3) {
      const data = JSON.parse(rawV3) as { v?: number; items?: DashboardLayoutItem[] }
      if (data.v === STORAGE_VERSION && Array.isArray(data.items) && data.items.length > 0) {
        return data.items.map((row) => normalizeItem(row as DashboardLayoutItem))
      }
    }

    const rawV2 = localStorage.getItem(STORAGE_V2_KEY)
    if (rawV2) {
      const data = JSON.parse(rawV2) as { v?: number; items?: unknown[] }
      if (data.v === 2 && Array.isArray(data.items) && data.items.length > 0) {
        const migrated = migrateV2ToV3Items(data.items)
        saveDashboardLayoutItems(migrated)
        return migrated.map(normalizeItem)
      }
    }

    const rawV1 = localStorage.getItem(STORAGE_V1_KEY)
    if (rawV1) {
      const data = JSON.parse(rawV1) as { v?: number; items?: unknown[] }
      if (data.v === 1 && Array.isArray(data.items)) {
        const next = defaultDashboardLayoutItems()
        saveDashboardLayoutItems(next)
        return next.map(normalizeItem)
      }
    }
  } catch {
    /* fallthrough */
  }

  return defaultDashboardLayoutItems().map(normalizeItem)
}

export function saveDashboardLayoutItems(items: DashboardLayoutItem[]) {
  localStorage.setItem(STORAGE_V3_KEY, JSON.stringify({ v: STORAGE_VERSION, items }))
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

function layoutItemHasRoute(it: DashboardLayoutItem): it is DashboardPageModuleItem {
  return isDashboardPageModuleKind(it.kind)
}

/** 当前布局中已出现在控制台首页的应用路由（含智能体 / 实例等内置块） */
export function getDashboardPinnedRoutes(): Set<string> {
  const items = loadDashboardLayoutItems()
  const set = new Set<string>()
  for (const it of items) {
    if (it.kind === 'AiAgent') set.add('/admin/agents')
    else if (it.kind === 'AiInstance') set.add('/admin/ai-instance')
    else if (layoutItemHasRoute(it)) set.add(it.route)
  }
  return set
}

/** 从应用库固定；已存在同 route 的页面模块则 false */
export function addDashboardShortcut(route: string): boolean {
  const kind = ROUTE_TO_PAGE_KIND[route]
  if (!kind) return false

  const items = loadDashboardLayoutItems()

  if (items.some((it) => layoutItemHasRoute(it) && it.route === route)) return false
  if (route === '/admin/agents' && items.some((it) => it.kind === 'AiAgent')) return false
  if (route === '/admin/ai-instance' && items.some((it) => it.kind === 'AiInstance')) return false

  let id = `mod_${kind}`
  if (items.some((it) => it.i === id)) id = `${id}_${Date.now()}`

  const pos = findNextSlot(items, DEFAULT_WH, DEFAULT_WH)
  const next: DashboardPageModuleItem = {
    i: id,
    kind,
    route: PAGE_KIND_ROUTE[kind],
    x: pos.x,
    y: pos.y,
    w: DEFAULT_WH,
    h: DEFAULT_WH,
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
