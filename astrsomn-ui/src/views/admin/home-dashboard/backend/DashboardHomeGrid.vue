<template>
  <div class="dashboard-home-grid-root">
    <GridLayout
      v-model:layout="layoutModel"
      :col-num="12"
      :row-height="rowHeight"
      :margin="margin"
      :is-draggable="editMode"
      :is-resizable="editMode"
      :vertical-compact="true"
      class="dashboard-vgl"
      @layout-updated="onLayoutUpdated"
    >
      <GridItem
        v-for="cell in layoutModel"
        :key="cell.i"
        :x="cell.x"
        :y="cell.y"
        :w="cell.w"
        :h="cell.h"
        :i="cell.i"
        :min-w="cell.minW ?? 1"
        :min-h="cell.minH ?? 1"
        :max-w="cell.maxW ?? 12"
        :max-h="cell.maxH ?? 6"
      >
        <div class="grid-cell" :class="{ 'grid-cell--edit': editMode }">
          <button
            v-if="editMode && isRouteShortcutRow(cell.i)"
            type="button"
            class="grid-cell-remove"
            title="从控制台移除"
            aria-label="移除"
            @click.stop="remove(String(cell.i))"
          >
            ×
          </button>
          <div class="grid-cell-body">
            <component
              :is="widgetFor(cell.i)"
              v-if="itemById[String(cell.i)]"
              v-bind="widgetProps(itemById[String(cell.i)]!, cell)"
            />
          </div>
        </div>
      </GridItem>
    </GridLayout>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { GridLayout, GridItem } from 'grid-layout-plus'
import type { Layout } from 'grid-layout-plus'
import type { DashboardLayoutItem, DashboardRouteShortcutItem } from './dashboardLayoutTypes'
import {
  dashboardLayoutRevision,
  debouncedSaveDashboardItems,
  flushDashboardSave,
  itemsToLayout,
  loadDashboardLayoutItems,
  mergeLayoutIntoItems,
  removeDashboardItem,
} from './dashboardLayoutStorage'
import { DASHBOARD_WIDGET_BY_KIND } from './dashboardWidgets'
import type { ManagementEntry } from './management'

const props = defineProps<{
  editMode: boolean
  entryByRoute: Partial<Record<string, ManagementEntry>>
}>()

const rowHeight = 108
const margin: [number, number] = [20, 20]

const items = ref<DashboardLayoutItem[]>(loadDashboardLayoutItems())
const layoutModel = ref<Layout>(itemsToLayout(items.value))

const itemById = computed(() => {
  const m: Record<string, DashboardLayoutItem> = {}
  for (const it of items.value) m[it.i] = it
  return m
})

function syncFromStorage() {
  items.value = loadDashboardLayoutItems()
  layoutModel.value = itemsToLayout(items.value)
}

onMounted(() => {
  syncFromStorage()
})

watch(dashboardLayoutRevision, () => {
  syncFromStorage()
})

function onLayoutUpdated(l: Layout) {
  items.value = mergeLayoutIntoItems(l, items.value)
  debouncedSaveDashboardItems(items.value)
}

watch(
  () => props.editMode,
  (editing) => {
    if (!editing) flushDashboardSave(items.value)
  },
)

function remove(i: string) {
  removeDashboardItem(i)
}

function isRouteShortcutRow(i: string | number): boolean {
  const row = itemById.value[String(i)]
  return row?.kind === 'RouteShortcut'
}

function widgetFor(i: string | number) {
  const row = itemById.value[String(i)]
  if (!row) return DASHBOARD_WIDGET_BY_KIND.AiAgent
  return DASHBOARD_WIDGET_BY_KIND[row.kind]
}

function widgetProps(full: DashboardLayoutItem, cell: Layout[number]) {
  const w = cell.w
  const h = cell.h
  if (full.kind === 'RouteShortcut') {
    return {
      item: full as DashboardRouteShortcutItem,
      gridW: w,
      gridH: h,
      editMode: props.editMode,
      entryByRoute: props.entryByRoute,
    }
  }
  return { gridW: w, gridH: h }
}
</script>

<style scoped src="./grid-layout-vgl.css"></style>

<style scoped>
.dashboard-home-grid-root {
  width: 100%;
  max-width: 1600px;
  margin: 0 auto;
  min-width: 0;
}

.dashboard-vgl {
  min-height: 120px;
}

.grid-cell {
  position: relative;
  height: 100%;
  min-height: 0;
  box-sizing: border-box;
}

.grid-cell--edit {
  outline: 1px dashed color-mix(in srgb, var(--primary) 35%, transparent);
  outline-offset: 2px;
  border-radius: 14px;
}

.grid-cell-remove {
  position: absolute;
  top: 6px;
  right: 6px;
  z-index: 5;
  width: 26px;
  height: 26px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 18px;
  line-height: 1;
  font-weight: 700;
  color: var(--text-heading);
  background: color-mix(in srgb, var(--bg-card) 88%, transparent);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.grid-cell-remove:hover {
  background: color-mix(in srgb, var(--error) 15%, var(--bg-card));
  color: var(--error);
}

.grid-cell-body {
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
}
</style>
