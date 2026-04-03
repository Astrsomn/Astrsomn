<template>
  <div class="shortcut-grid-root">
    <GridLayout
      v-model:layout="layoutModel"
      :col-num="12"
      :row-height="rowHeight"
      :margin="margin"
      :is-draggable="editMode"
      :is-resizable="editMode"
      :vertical-compact="true"
      class="shortcut-vgl"
      @layout-updated="onLayoutUpdated"
    >
      <GridItem
        v-for="item in layoutModel"
        :key="item.i"
        :x="item.x"
        :y="item.y"
        :w="item.w"
        :h="item.h"
        :i="item.i"
        :min-w="item.minW ?? 2"
        :min-h="item.minH ?? 1"
        :max-w="item.maxW ?? 12"
        :max-h="item.maxH ?? 6"
      >
        <div class="grid-cell" :class="{ 'grid-cell--edit': editMode }">
          <button
            v-if="editMode"
            type="button"
            class="grid-cell-remove"
            title="从控制台移除"
            aria-label="移除"
            @click.stop="remove(item.i)"
          >
            ×
          </button>
          <div class="grid-cell-body">
            <template v-if="metaById[String(item.i)]">
              <BentoWideTile
                v-if="metaById[String(item.i)]!.variant === 'mcp'"
                embedded
                :title="tileTitle(metaById[String(item.i)]!)"
                :description="tileDesc(metaById[String(item.i)]!)"
                :to="metaById[String(item.i)]!.route"
                :disabled="editMode || !entryByRoute[metaById[String(item.i)]!.route]"
                class="grid-cell-fill"
              />
              <BentoGradientTile
                v-else-if="metaById[String(item.i)]!.variant === 'gradient-market'"
                embedded
                :title="tileTitle(metaById[String(item.i)]!)"
                :subtitle="tileSubtitle(metaById[String(item.i)]!)"
                :to="metaById[String(item.i)]!.route"
                :icon="ShopOutlined"
                variant="market"
                :disabled="editMode || !entryByRoute[metaById[String(item.i)]!.route]"
                class="grid-cell-fill"
              />
              <BentoGradientTile
                v-else-if="metaById[String(item.i)]!.variant === 'gradient-apps'"
                embedded
                :title="tileTitle(metaById[String(item.i)]!)"
                :subtitle="tileSubtitle(metaById[String(item.i)]!)"
                :to="metaById[String(item.i)]!.route"
                :icon="AppstoreOutlined"
                variant="apps"
                :disabled="editMode"
                class="grid-cell-fill"
              />
              <div v-else class="grid-cell-compact">
                <MenuSlotCard
                  :entry="entryByRoute[metaById[String(item.i)]!.route]"
                  :title="tileTitle(metaById[String(item.i)]!)"
                  :description="tileDesc(metaById[String(item.i)]!)"
                  :accent="entryByRoute[metaById[String(item.i)]!.route]?.accent ?? 'primary'"
                  variant="compact"
                  :disabled="editMode"
                  @navigate="go"
                />
              </div>
            </template>
          </div>
        </div>
      </GridItem>
    </GridLayout>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { GridLayout, GridItem } from 'grid-layout-plus'
import type { Layout } from 'grid-layout-plus'
import { ShopOutlined, AppstoreOutlined } from '@ant-design/icons-vue'
import type { ManagementEntry } from './management'
import MenuSlotCard from './MenuSlotCard.vue'
import BentoWideTile from './BentoWideTile.vue'
import BentoGradientTile from './BentoGradientTile.vue'
import {
  dashboardShortcutsRevision,
  debouncedSaveShortcutItems,
  flushShortcutSave,
  itemsToLayout,
  loadShortcutItems,
  mergeLayoutIntoItems,
  removeDashboardShortcut,
  type DashboardShortcutItem,
} from './dashboardShortcutLayout'

const props = defineProps<{
  editMode: boolean
  entryByRoute: Partial<Record<string, ManagementEntry>>
}>()

const router = useRouter()
const rowHeight = 108
const margin: [number, number] = [20, 20]

const items = ref<DashboardShortcutItem[]>(loadShortcutItems())
const layoutModel = ref<Layout>(itemsToLayout(items.value))

const metaById = computed(() => {
  const m: Record<string, DashboardShortcutItem> = {}
  for (const it of items.value) m[it.i] = it
  return m
})

function syncFromStorage() {
  items.value = loadShortcutItems()
  layoutModel.value = itemsToLayout(items.value)
}

onMounted(() => {
  syncFromStorage()
})

watch(dashboardShortcutsRevision, () => {
  syncFromStorage()
})

function onLayoutUpdated(l: Layout) {
  items.value = mergeLayoutIntoItems(l, items.value)
  debouncedSaveShortcutItems(items.value)
}

watch(
  () => props.editMode,
  (editing) => {
    if (!editing) flushShortcutSave(items.value)
  },
)

function remove(i: string | number) {
  removeDashboardShortcut(String(i))
}

function go(path: string) {
  if (!props.editMode) void router.push(path)
}

function tileTitle(it: DashboardShortcutItem) {
  const e = props.entryByRoute[it.route]
  return e?.label ?? it.route.replace('/admin/', '')
}

function tileDesc(it: DashboardShortcutItem) {
  const e = props.entryByRoute[it.route]
  return it.subtitle ?? e?.description ?? ''
}

function tileSubtitle(it: DashboardShortcutItem) {
  const e = props.entryByRoute[it.route]
  return it.subtitle ?? e?.description ?? ''
}
</script>

<style scoped src="./grid-layout-vgl.css"></style>

<style scoped>
.shortcut-grid-root {
  grid-column: 1 / -1;
  width: 100%;
  min-width: 0;
}

.shortcut-vgl {
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

.grid-cell-fill {
  flex: 1;
  min-height: 0;
  height: 100%;
}

.grid-cell-fill :deep(.wide-tile),
.grid-cell-fill :deep(.grad-tile) {
  height: 100%;
  min-height: 100%;
}

.grid-cell-compact {
  flex: 1;
  min-height: 0;
  display: flex;
}

.grid-cell-compact :deep(.slot-wrapper) {
  flex: 1;
  min-width: 0;
  display: flex;
}

.grid-cell-compact :deep(.slot-card) {
  flex: 1;
  min-height: 100%;
  width: 100%;
}
</style>
