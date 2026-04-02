<template>
  <div class="resource-page">
    <div class="resource-shell">
  

      <div class="resource-layout">
        <aside class="filter-column">
          <section class="panel-card filter-card">
            <header class="panel-head">
              <h2 class="panel-title">筛选导航</h2>
              <p class="panel-subtitle">支持按分组和关键词快速收敛入口范围。</p>
            </header>

            <div class="search-box">
              <a-input
                v-model="keyword"
                placeholder="搜索资源、能力或描述"
                allow-clear
                class="resource-search"
              >
                <template #prefix><SearchOutlined /></template>
              </a-input>
            </div>

            <nav class="group-nav">
              <button
                class="nav-item"
                :class="{ active: activeGroupId === 'all' }"
                @click="activeGroupId = 'all'"
              >
                <span class="label">全部入口</span>
                <span class="badge">{{ filteredTotalCount }}</span>
              </button>
              <button
                v-for="group in groupsFiltered"
                :key="group.id"
                class="nav-item"
                :class="{ active: activeGroupId === group.id }"
                @click="activeGroupId = group.id"
              >
                <span class="label">{{ group.title }}</span>
                <span class="badge">{{ group.filteredItems.length }}</span>
              </button>
            </nav>
          </section>
        </aside>

        <main class="content-column">
          <section class="panel-card content-card">
            <header class="content-head">
              <div class="content-title-wrap">
                <div class="content-kicker">当前分组</div>
                <h2 class="content-title">{{ activeGroupTitle }}</h2>
                <p class="content-subtitle">{{ activeGroupSubtitle }}</p>
              </div>

              <div class="content-tools">
                <span class="summary-chip">共 {{ filteredEntries.length }} 个入口</span>
                <div class="pagination-wrapper" v-if="totalPages > 1">
                  <button class="icon-btn" :disabled="currentPage === 1" @click="changePage(currentPage - 1)">
                    <LeftOutlined />
                  </button>
                  <span class="page-info"><b>{{ currentPage }}</b> / {{ totalPages }}</span>
                  <button
                    class="icon-btn"
                    :disabled="currentPage === totalPages"
                    @click="changePage(currentPage + 1)"
                  >
                    <RightOutlined />
                  </button>
                </div>
              </div>
            </header>

            <div class="content-toolbar">
              <span class="toolbar-chip">{{ keyword ? `关键词：${keyword}` : '未启用关键词筛选' }}</span>
              <span class="toolbar-chip">{{ totalPages > 1 ? `当前第 ${currentPage} / ${totalPages} 页` : '当前为单页展示' }}</span>
            </div>

            <section class="content-body" ref="gridContainerRef">
              <div v-if="paginatedEntries.length === 0" class="empty-holder">
                <a-empty :image="Empty.PRESENTED_IMAGE_SIMPLE" description="暂无匹配入口" />
              </div>

              <div v-else class="resource-grid">
                <MenuSlotCard
                  v-for="entry in paginatedEntries"
                  :key="entry.route"
                  :entry="entry"
                  :title="entry.label"
                  :description="entry.description"
                  :accent="entry.accent"
                  variant="compact"
                  @navigate="navigateTo"
                />
              </div>
            </section>

            <footer class="content-footer">
              <span>共筛选到 {{ filteredEntries.length }} 个入口</span>
              <span>{{ keyword ? `当前关键词：${keyword}` : '可通过左侧分组或搜索框快速定位资源' }}</span>
            </footer>
          </section>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Empty } from 'ant-design-vue'
import { LeftOutlined, RightOutlined, SearchOutlined } from '@ant-design/icons-vue'
// Vetur occasionally misses Vue SFC default exports in script setup files.
// @ts-ignore
import MenuSlotCard from '../backend/MenuSlotCard.vue'
import { getCurrentUserRole, resolveManagementGroups } from '../backend/management'

const router = useRouter()
const keyword = ref('')
const activeGroupId = ref<string>('all')
const currentPage = ref(1)
const pageSize = ref(9)
const gridContainerRef = ref<HTMLElement | null>(null)

const currentRole = computed(() => getCurrentUserRole())
const groups = computed(() => resolveManagementGroups(currentRole.value))

const groupsFiltered = computed(() =>
  groups.value
    .map((group) => ({
      ...group,
      filteredItems: group.items.filter(
        (entry) =>
          entry.label.toLowerCase().includes(keyword.value.toLowerCase()) ||
          entry.description.toLowerCase().includes(keyword.value.toLowerCase()),
      ),
    }))
    .filter((group) => group.filteredItems.length > 0 || !keyword.value),
)

const filteredTotalCount = computed(() =>
  groupsFiltered.value.reduce((sum, group) => sum + group.filteredItems.length, 0),
)

const activeGroup = computed(() =>
  groups.value.find((group) => group.id === activeGroupId.value),
)

const activeGroupTitle = computed(() =>
  activeGroupId.value === 'all' ? '全部入口' : activeGroup.value?.title ?? '全部入口',
)

const activeGroupSubtitle = computed(() => {
  if (activeGroupId.value === 'all') {
    return keyword.value
      ? `已根据关键词筛选出 ${filteredTotalCount.value} 个可用入口`
      : '汇总展示当前角色可访问的全部后台能力入口'
  }

  return activeGroup.value?.subtitle ?? '按分组浏览当前角色可访问的后台入口'
})

const filteredEntries = computed(() => {
  if (activeGroupId.value === 'all') {
    return groupsFiltered.value.reduce<typeof groupsFiltered.value[number]['filteredItems']>(
      (entries, group) => entries.concat(group.filteredItems),
      [],
    )
  }

  return groupsFiltered.value.find((group) => group.id === activeGroupId.value)?.filteredItems ?? []
})

const totalPages = computed(() =>
  Math.max(1, Math.ceil(filteredEntries.value.length / pageSize.value)),
)

const paginatedEntries = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredEntries.value.slice(start, start + pageSize.value)
})

watch([keyword, activeGroupId], () => {
  currentPage.value = 1
})

watch(totalPages, (pageCount) => {
  if (currentPage.value > pageCount) currentPage.value = pageCount
})

const changePage = (page: number) => {
  currentPage.value = Math.min(Math.max(page, 1), totalPages.value)
  gridContainerRef.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

const navigateTo = (path: string) => {
  void router.push(path)
}
</script>

<style scoped>
.resource-page {
  position: relative;
  box-sizing: border-box;
  min-height: 100%;
  padding: 12px 16px 24px;
  background: var(--bg-base);
  overflow: hidden;
}

.resource-page::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image: radial-gradient(
    color-mix(in srgb, var(--text-muted) 40%, transparent) 1px,
    transparent 0
  );
  background-size: 24px 24px;
  opacity: 0.2;
  pointer-events: none;
}

.resource-shell {
  position: relative;
  z-index: 1;
  max-width: 1600px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.panel-card {
  border: 1px solid var(--border-subtle);
  border-radius: 28px;
  background: color-mix(in srgb, var(--bg-card) 96%, transparent);
  box-shadow:
    0 16px 40px -32px rgba(15, 23, 42, 0.6),
    0 4px 16px -12px rgba(15, 23, 42, 0.3);
}

.page-hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20px;
  padding: 28px 30px;
}

.page-copy {
  max-width: 760px;
}

.page-eyebrow {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.12em;
  color: var(--primary);
}

.page-title {
  margin: 10px 0 0;
  font-size: 32px;
  line-height: 1.15;
  font-weight: 800;
  color: var(--text-heading);
}

.page-subtitle {
  margin: 12px 0 0;
  font-size: 14px;
  line-height: 1.7;
  color: var(--text-muted);
}

.page-metrics {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: flex-end;
}

.metric-card {
  min-width: 132px;
  padding: 14px 16px;
  border-radius: 18px;
  border: 1px solid var(--border-subtle);
  background: color-mix(in srgb, var(--bg-base) 55%, transparent);
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.metric-label {
  font-size: 12px;
  color: var(--text-muted);
}

.metric-value {
  font-size: 24px;
  line-height: 1;
  color: var(--text-heading);
}

.metric-card--status {
  min-width: 192px;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  color: var(--success);
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--success);
  box-shadow: 0 0 0 6px color-mix(in srgb, var(--success) 16%, transparent);
}

.resource-layout {
  display: grid;
  grid-template-columns: 300px minmax(0, 1fr);
  gap: 24px;
  align-items: start;
}

.filter-column,
.content-column {
  min-width: 0;
}

.filter-card,
.content-card {
  padding: 24px;
}

.filter-card {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.panel-head {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.panel-title {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: var(--text-heading);
}

.panel-subtitle {
  margin: 0;
  font-size: 12px;
  line-height: 1.6;
  color: var(--text-muted);
}

.search-box {
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border-subtle);
}

.group-nav {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-item {
  all: unset;
  box-sizing: border-box;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 16px;
  border: 1px solid transparent;
  color: var(--text-secondary);
  cursor: pointer;
  transition:
    background 0.2s ease,
    border-color 0.2s ease,
    color 0.2s ease,
    transform 0.2s ease;
}

.nav-item:hover {
  background: color-mix(in srgb, var(--primary) 8%, var(--bg-card));
  border-color: color-mix(in srgb, var(--primary) 18%, var(--border-subtle));
  color: var(--text-heading);
}

.nav-item.active {
  background: color-mix(in srgb, var(--primary) 14%, var(--bg-card));
  border-color: color-mix(in srgb, var(--primary) 32%, var(--border-subtle));
  color: var(--text-heading);
  transform: translateY(-1px);
}

.label {
  font-size: 13px;
  font-weight: 600;
}

.badge {
  flex-shrink: 0;
  min-width: 28px;
  padding: 3px 8px;
  border-radius: 999px;
  background: color-mix(in srgb, var(--text-muted) 12%, transparent);
  font-size: 11px;
  line-height: 1.2;
  text-align: center;
  color: var(--text-muted);
}

.nav-item.active .badge {
  background: color-mix(in srgb, var(--primary) 18%, transparent);
  color: var(--primary);
}

.content-card {
  display: flex;
  flex-direction: column;
  gap: 18px;
  min-height: 580px;
}

.content-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  padding-bottom: 18px;
  border-bottom: 1px solid var(--border-subtle);
}

.content-title-wrap {
  min-width: 0;
}

.content-kicker {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: var(--primary);
}

.content-title {
  margin: 8px 0 0;
  font-size: 24px;
  line-height: 1.2;
  font-weight: 800;
  color: var(--text-heading);
}

.content-subtitle {
  margin: 8px 0 0;
  max-width: 760px;
  font-size: 13px;
  line-height: 1.65;
  color: var(--text-muted);
}

.content-tools {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex-wrap: wrap;
  gap: 12px;
}

.summary-chip,
.toolbar-chip,
.pagination-wrapper {
  border: 1px solid var(--border-subtle);
  background: color-mix(in srgb, var(--bg-base) 58%, transparent);
}

.summary-chip,
.toolbar-chip {
  display: inline-flex;
  align-items: center;
  min-height: 36px;
  padding: 0 14px;
  border-radius: 999px;
  font-size: 12px;
  color: var(--text-muted);
}

.pagination-wrapper {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 6px 10px;
  border-radius: 999px;
}

.icon-btn {
  all: unset;
  width: 28px;
  height: 28px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  cursor: pointer;
  color: var(--text-muted);
  transition:
    background 0.2s ease,
    color 0.2s ease;
}

.icon-btn:hover:not(:disabled) {
  background: color-mix(in srgb, var(--primary) 14%, transparent);
  color: var(--primary);
}

.icon-btn:disabled {
  opacity: 0.35;
  cursor: not-allowed;
}

.page-info {
  min-width: 58px;
  text-align: center;
  font-size: 12px;
  color: var(--text-muted);
}

.content-toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.content-body {
  flex: 1;
  min-height: 320px;
}

.resource-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.resource-grid :deep(.slot-wrapper),
.resource-grid :deep(.slot-card) {
  height: 100%;
}

.empty-holder {
  min-height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed var(--border-subtle);
  border-radius: 24px;
  background: color-mix(in srgb, var(--bg-base) 55%, transparent);
}

.content-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding-top: 6px;
  border-top: 1px solid var(--border-subtle);
  font-size: 12px;
  color: var(--text-muted);
}

.resource-search :deep(.ant-input-affix-wrapper) {
  min-height: 44px;
  border-radius: 14px;
  border-color: var(--border-subtle);
  background: color-mix(in srgb, var(--bg-base) 55%, transparent);
  box-shadow: none;
}

.resource-search :deep(.ant-input-affix-wrapper:hover),
.resource-search :deep(.ant-input-affix-wrapper-focused) {
  border-color: color-mix(in srgb, var(--primary) 32%, var(--border-subtle));
}

.resource-search :deep(.ant-input) {
  background: transparent;
  color: var(--text-heading);
}

.resource-search :deep(.ant-input-prefix) {
  color: var(--text-muted);
}

@media (max-width: 1280px) {
  .resource-layout {
    grid-template-columns: 1fr;
  }

  .group-nav {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  }
}

@media (max-width: 900px) {
  .page-hero,
  .content-head,
  .content-footer {
    flex-direction: column;
    align-items: flex-start;
  }

  .page-metrics,
  .content-tools {
    width: 100%;
    justify-content: flex-start;
  }

  .content-card {
    min-height: 0;
  }
}

@media (max-width: 640px) {
  .resource-page {
    padding: 10px 12px 20px;
  }

  .page-hero,
  .filter-card,
  .content-card {
    padding: 20px;
  }

  .page-title {
    font-size: 26px;
  }

  .content-title {
    font-size: 22px;
  }

  .group-nav,
  .resource-grid {
    grid-template-columns: 1fr;
  }
}
</style>