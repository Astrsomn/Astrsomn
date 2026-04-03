<template>
  <section
    class="glass-card glass-card--interactive console-page-mod"
    :class="[`console-page-mod--${accent}`, sizeClass]"
    :data-accent="accent"
    role="button"
    tabindex="0"
    :aria-disabled="editMode"
    @click="go"
    @keydown.enter.prevent="go"
    @keydown.space.prevent="go"
  >
    <div class="console-page-mod-head">
      <div class="console-page-mod-icon" aria-hidden="true">
        <component :is="iconComponent" v-if="iconComponent" class="console-page-mod-icon-glyph" />
        <appstore-outlined v-else class="console-page-mod-icon-glyph" />
      </div>
      <div class="console-page-mod-head-text">
        <h3 class="console-page-mod-title" :title="displayTitle">{{ displayTitle }}</h3>
        <p class="console-page-mod-desc" :title="displayDescription">{{ displayDescription }}</p>
      </div>
    </div>

    <div class="console-page-mod-metrics">
      <div v-for="(m, idx) in visibleMetrics" :key="idx" class="console-page-mod-metric">
        <span class="console-page-mod-metric-label" :title="m.label">{{ m.label }}</span>
        <span class="console-page-mod-metric-value" :title="m.value">{{ m.value }}</span>
      </div>
    </div>

    <p
      v-if="footnoteVisible && mock.footnote"
      class="console-page-mod-foot"
      :title="mock.footnote"
    >
      {{ mock.footnote }}
    </p>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { AppstoreOutlined } from '@ant-design/icons-vue'
import type { DashboardPageModuleKind } from '../dashboardLayoutTypes'
import { PAGE_MODULE_MOCK } from '../dashboardPageRegistry'
import type { ManagementEntry } from '../../resource-library/management.ts'
import { dashboardSizeClassSuffix, gridColTier, gridRowTier } from '../dashboardSizeTier'

const props = withDefaults(
  defineProps<{
    pageKind: DashboardPageModuleKind
    route: string
    gridW: number
    gridH: number
    editMode?: boolean
    entryByRoute?: Partial<Record<string, ManagementEntry>>
  }>(),
  { editMode: false },
)

const router = useRouter()

const mock = computed(() => PAGE_MODULE_MOCK[props.pageKind])

const entry = computed(() => props.entryByRoute?.[props.route])

const displayTitle = computed(() => entry.value?.label ?? mock.value.title)

const displayDescription = computed(() => entry.value?.description ?? mock.value.description)

const accent = computed(() => entry.value?.accent ?? mock.value.accent)

const iconComponent = computed(() => entry.value?.icon)

const sizeClass = computed(() => `console-page-mod--size-${dashboardSizeClassSuffix(props.gridW, props.gridH)}`)

const visibleMetrics = computed(() => {
  const list = mock.value.metrics
  const rt = gridRowTier(props.gridH)
  const ct = gridColTier(props.gridW)
  if (rt <= 1 && ct <= 1) return list.slice(0, 1)
  if (rt <= 1) return list.slice(0, 2)
  if (ct <= 1) return list.slice(0, 1)
  return list
})

const footnoteVisible = computed(() => gridRowTier(props.gridH) >= 2 && gridColTier(props.gridW) >= 2)

const go = () => {
  if (props.editMode) return
  void router.push(props.route)
}
</script>

<style scoped>
.console-page-mod {
  width: 100%;
  height: 100%;
  min-height: 0;
  box-sizing: border-box;
  padding: 16px;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  text-align: left;
  border: none;
  font: inherit;
  color: inherit;
  gap: 12px;
}

.console-page-mod:disabled {
  cursor: default;
  opacity: 1;
}

.console-page-mod-head {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  min-width: 0;
}

.console-page-mod-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: color-mix(in srgb, var(--primary) 14%, var(--bg-card));
  color: color-mix(in srgb, var(--primary) 80%, #2563eb);
}

.console-page-mod-icon-glyph {
  font-size: 18px;
}

.console-page-mod-head-text {
  min-width: 0;
  flex: 1;
}

.console-page-mod-title {
  margin: 0;
  font-size: 0.875rem;
  font-weight: 800;
  color: var(--text-heading);
  line-height: 1.3;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.console-page-mod--size-c1-r1 .console-page-mod-title {
  font-size: 0.8125rem;
}

.console-page-mod-desc {
  margin: 4px 0 0;
  font-size: 11px;
  color: var(--text-muted);
  line-height: 1.45;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.console-page-mod--size-c1-r1 .console-page-mod-desc,
.console-page-mod--size-c1-r2 .console-page-mod-desc {
  font-size: 10px;
}

.console-page-mod-metrics {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
  margin-top: auto;
}

.console-page-mod--size-c1-r1 .console-page-mod-metrics {
  grid-template-columns: 1fr;
}

.console-page-mod-metric {
  padding: 8px 10px;
  border-radius: 10px;
  border: 1px solid var(--border-subtle);
  background: color-mix(in srgb, var(--text-muted) 6%, var(--bg-card));
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.console-page-mod-metric-label {
  font-size: 10px;
  font-weight: 700;
  color: var(--text-muted);
  min-width: 0;
  display: block;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.console-page-mod-metric-value {
  font-size: 0.8125rem;
  font-weight: 800;
  color: var(--text-heading);
  min-width: 0;
  display: block;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.console-page-mod-foot {
  margin: 0;
  font-size: 10px;
  color: var(--text-muted);
  line-height: 1.4;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
</style>
