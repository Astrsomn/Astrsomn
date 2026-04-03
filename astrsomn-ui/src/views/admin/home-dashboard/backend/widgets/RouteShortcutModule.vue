<template>
  <div class="route-shortcut-mod">
    <DashboardWideLinkTile
      v-if="item.shortcutVariant === 'mcp'"
      :title="title"
      :description="desc"
      :to="item.route"
      :disabled="editMode || !entryByRoute[item.route]"
      :compact="compact"
      class="route-shortcut-fill"
    />
    <DashboardGradientLinkTile
      v-else-if="item.shortcutVariant === 'gradient-market'"
      :title="title"
      :subtitle="subtitle"
      :to="item.route"
      :icon="ShopOutlined"
      variant="market"
      :disabled="editMode || !entryByRoute[item.route]"
      :compact="compact"
      class="route-shortcut-fill"
    />
    <DashboardGradientLinkTile
      v-else-if="item.shortcutVariant === 'gradient-apps'"
      :title="title"
      :subtitle="subtitle"
      :to="item.route"
      :icon="AppstoreOutlined"
      variant="apps"
      :disabled="editMode"
      :compact="compact"
      class="route-shortcut-fill"
    />
    <div v-else class="route-shortcut-compact">
      <MenuSlotCard
        :entry="entryByRoute[item.route]"
        :title="title"
        :description="desc"
        :accent="entryByRoute[item.route]?.accent ?? 'primary'"
        variant="compact"
        :disabled="editMode"
        @navigate="go"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ShopOutlined, AppstoreOutlined } from '@ant-design/icons-vue'
import type { DashboardRouteShortcutItem } from '../dashboardLayoutTypes'
import type { ManagementEntry } from '../management'
import { gridColTier, gridRowTier } from '../dashboardSizeTier'
import MenuSlotCard from '../MenuSlotCard.vue'
import DashboardWideLinkTile from './DashboardWideLinkTile.vue'
import DashboardGradientLinkTile from './DashboardGradientLinkTile.vue'

const props = defineProps<{
  item: DashboardRouteShortcutItem
  gridW: number
  gridH: number
  editMode: boolean
  entryByRoute: Partial<Record<string, ManagementEntry>>
}>()

const router = useRouter()

const compact = computed(
  () => gridColTier(props.gridW) <= 1 || gridRowTier(props.gridH) <= 1,
)

const title = computed(() => {
  const e = props.entryByRoute[props.item.route]
  return e?.label ?? props.item.route.replace('/admin/', '')
})

const desc = computed(() => {
  const e = props.entryByRoute[props.item.route]
  return props.item.subtitle ?? e?.description ?? ''
})

const subtitle = computed(() => {
  const e = props.entryByRoute[props.item.route]
  return props.item.subtitle ?? e?.description ?? ''
})

const go = (path: string) => {
  if (!props.editMode) void router.push(path)
}
</script>

<style scoped>
.route-shortcut-mod {
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.route-shortcut-fill {
  flex: 1;
  min-height: 0;
  height: 100%;
}

.route-shortcut-fill :deep(.wide-tile),
.route-shortcut-fill :deep(.grad-tile) {
  height: 100%;
  min-height: 100%;
}

.route-shortcut-compact {
  flex: 1;
  min-height: 0;
  display: flex;
}

.route-shortcut-compact :deep(.slot-wrapper) {
  flex: 1;
  min-width: 0;
  display: flex;
}

.route-shortcut-compact :deep(.slot-card) {
  flex: 1;
  min-height: 100%;
  width: 100%;
}
</style>
