import { reactive } from 'vue'

const STORAGE_KEY = 'astrsomn_dashboard_layout'

export interface WidgetSize {
  colSpan: number
  height: number
}

const defaults: Record<string, WidgetSize> = {
  'traffic-chart': { colSpan: 1, height: 340 },
  'model-chart': { colSpan: 1, height: 340 },
  'ai-config': { colSpan: 2, height: 320 },
  'ai-kb': { colSpan: 1, height: 260 },
  'system': { colSpan: 1, height: 260 },
}

function load(): Record<string, WidgetSize> {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (raw) {
      const saved = JSON.parse(raw) as Record<string, WidgetSize>
      const merged: Record<string, WidgetSize> = {}
      for (const [k, v] of Object.entries(defaults)) {
        merged[k] = saved[k] ? { ...v, ...saved[k] } : { ...v }
      }
      return merged
    }
  } catch { /* ignore */ }
  return JSON.parse(JSON.stringify(defaults))
}

function persist(data: Record<string, WidgetSize>) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(data))
}

const state = reactive<Record<string, WidgetSize>>(load())

export function useDashboardLayout() {
  function get(id: string): WidgetSize {
    return state[id] ?? { colSpan: 1, height: 300 }
  }

  function update(id: string, patch: Partial<WidgetSize>) {
    if (!state[id]) state[id] = { ...(defaults[id] ?? { colSpan: 1, height: 300 }) }
    Object.assign(state[id], patch)
    persist({ ...state })
  }

  function reset() {
    const fresh = JSON.parse(JSON.stringify(defaults)) as Record<string, WidgetSize>
    for (const k of Object.keys(state)) delete state[k]
    Object.assign(state, fresh)
    persist({ ...state })
  }

  return { get, update, reset }
}
