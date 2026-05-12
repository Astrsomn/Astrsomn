<template>
  <AstrsomnPageShell title="资源库" description="管理系统资源和能力入口">
    <div class="resource-layout">
      <aside class="filter-column">
        <div class="filter-inner-wrapper">
          <header class="filter-header">
            <h2 class="filter-title">筛选导航</h2>
            <div class="search-container">
              <AstrsomnSearchPill
                v-model="keyword"
                placeholder="搜索资源..."
                button-label="搜索"
                layout="fluid"
                @search="handleSearch"
              />
            </div>
          </header>

          <nav class="group-nav">
            <button
              class="nav-item"
              :class="{ active: activeGroupId === 'all' }"
              @click="activeGroupId = 'all'"
            >
              <div class="nav-icon">
                <component :is="getGroupIcon('all')" />
              </div>
              <span class="nav-label">全部入口</span>
              <span class="nav-badge">{{ filteredTotalCount }}</span>
            </button>

            <button
              v-for="group in groupsFiltered"
              :key="group.id"
              class="nav-item"
              :class="{ active: activeGroupId === group.id }"
              @click="activeGroupId = group.id"
            >
              <div class="nav-icon">
                <component :is="getGroupIcon(group.id)" />
              </div>
              <span class="nav-label">{{ group.title }}</span>
              <span class="nav-badge">{{ group.filteredItems.length }}</span>
            </button>
          </nav>
        </div>
      </aside>

      <main class="content-column">
        <div class="content-inner-wrapper">
          <header class="content-header">
            <div class="title-section">
              <h1 class="main-title">{{ activeGroupTitle }}</h1>
              <span class="count-chip">共 {{ filteredEntries.length }} 项</span>
            </div>

            <div class="action-section">
              <div class="modern-pagination" v-if="totalPages > 1">
                <button 
                  class="page-btn" 
                  :disabled="currentPage === 1" 
                  @click="changePage(currentPage - 1)"
                >
                  <LeftOutlined />
                </button>
                <div class="page-indicator">
                  <span class="current">{{ currentPage }}</span>
                  <span class="divider">/</span>
                  <span class="total">{{ totalPages }}</span>
                </div>
                <button 
                  class="page-btn" 
                  :disabled="currentPage === totalPages" 
                  @click="changePage(currentPage + 1)"
                >
                  <RightOutlined />
                </button>
              </div>
            </div>
          </header>

          <section class="grid-section" ref="gridContainerRef">
            <div v-if="paginatedEntries.length === 0" class="empty-state">
              <a-empty :image="Empty.PRESENTED_IMAGE_SIMPLE" description="未找到相关资源" />
            </div>

            <div v-else class="resource-grid">
              <MenuSlotCard
                v-for="(entry, index) in paginatedEntries"
                :key="entry.route"
                :entry="entry"
                :title="entry.label"
                :description="entry.description"
                :accent="entry.accent"
                :style="{ animationDelay: `${index * 0.05}s` }"
                variant="compact"
                @navigate="navigateTo"
              />
            </div>
          </section>

          <footer class="content-footer">
            <div class="footer-info">
              显示第 {{ (currentPage - 1) * pageSize + 1 }} - 
              {{ Math.min(currentPage * pageSize, filteredEntries.length) }} 条结果
            </div>
          </footer>
        </div>
      </main>
    </div>
  </AstrsomnPageShell>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Empty } from 'ant-design-vue'
import { LeftOutlined, RightOutlined, HomeOutlined, RobotOutlined, FileTextOutlined, SafetyCertificateOutlined, SettingOutlined } from '@ant-design/icons-vue'
// Vetur occasionally misses Vue SFC default exports in script setup files.
// @ts-ignore
import MenuSlotCard from './MenuSlotCard.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnPageShell from '@/components/home/AstrsomnPageShell.vue'
import { getCurrentUserRole, resolveManagementGroups } from './management.ts'

const CARD_MIN_WIDTH = 300
const GRID_GAP = 20
const ROWS = 3

const router = useRouter()
const keyword = ref('')
const activeGroupId = ref<string>('all')
const currentPage = ref(1)
const pageSize = ref(9)
const gridContainerRef = ref<HTMLElement | null>(null)
const contentColumnRef = ref<HTMLElement | null>(null)
let resizeObserver: ResizeObserver | null = null
let resizeTimeout: ReturnType<typeof setTimeout> | null = null
let lastColumns = 0

const calculateColumns = (containerWidth: number): number => {
  const rawColumns = Math.floor((containerWidth + GRID_GAP) / (CARD_MIN_WIDTH + GRID_GAP))
  const columns = Math.max(1, rawColumns)
  if (lastColumns > 0 && columns !== lastColumns) {
    const threshold = CARD_MIN_WIDTH * 0.1
    const prevWidth = lastColumns * CARD_MIN_WIDTH + (lastColumns - 1) * GRID_GAP
    if (Math.abs(containerWidth - prevWidth) < threshold) {
      return lastColumns
    }
  }
  lastColumns = columns
  return columns
}

const updatePageSize = () => {
  if (contentColumnRef.value) {
    const width = contentColumnRef.value.clientWidth
    const columns = calculateColumns(width)
    pageSize.value = columns * ROWS
  }
}

onMounted(async () => {
  await nextTick()
  const contentColumn = document.querySelector('.content-column')
  if (contentColumn) {
    contentColumnRef.value = contentColumn as HTMLElement
    updatePageSize()
    resizeObserver = new ResizeObserver(() => {
      if (resizeTimeout) clearTimeout(resizeTimeout)
      resizeTimeout = setTimeout(updatePageSize, 100)
    })
    resizeObserver.observe(contentColumn)
  }
})

onUnmounted(() => {
  if (resizeObserver) {
    resizeObserver.disconnect()
    resizeObserver = null
  }
  if (resizeTimeout) {
    clearTimeout(resizeTimeout)
    resizeTimeout = null
  }
})

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

const getGroupIcon = (groupId: string) => {
  switch (groupId) {
    case 'all':
      return HomeOutlined
    case 'ai-core':
      return RobotOutlined
    case 'content':
      return FileTextOutlined
    case 'ops':
      return SafetyCertificateOutlined
    case 'system':
      return SettingOutlined
    default:
      return HomeOutlined
  }
}

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

const handleSearch = () => {
  currentPage.value = 1
}
</script>

<style scoped>
/* 样式重构 - 统一现代简约风格 */

.resource-layout {
  display: grid;
  grid-template-columns: 280px 1fr;
  min-height: calc(100vh - 70px);
  background-color: var(--bg-surface);
}

/* --- 侧边栏样式 --- */
.filter-column {

  border-right: 1px solid var(--border-default);
  padding: 32px 16px;
}

.filter-header {
  margin-bottom: 32px;
  padding: 0 8px;
}

.filter-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 16px;
}

.search-container :deep(.toolbar-search-pill) {
  background: var(--bg-input);
  border: 1px solid var(--border-input);
  height: 48px;
  transition: all 0.3s;
  box-shadow: none;
}

.search-container :deep(.toolbar-search-pill:focus-within) {
  border-color: var(--text-muted);
  box-shadow: 0 0 0 4px rgba(0, 0, 0, 0.1);
}

.group-nav {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-item {
  all: unset;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 16px;
  border-radius: 12px;
  cursor: pointer;
  color: var(--text-secondary);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
}

.nav-item:hover {

  color: var(--text-primary);
  border-color: var(--border-default);
}

.nav-item.active {

  color: var(--text-primary);
  border-color: var(--border-default);
  box-shadow: var(--shadow-card);
}

.nav-icon {
  font-size: 18px;
  display: flex;
  align-items: center;
  color: var(--text-muted);
}

.nav-item:hover .nav-icon,
.nav-item.active .nav-icon {
  color: var(--text-primary);
}

.nav-label {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
}

.nav-badge {
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  background: var(--bg-input);
  border-radius: 20px;
  color: var(--text-muted);
  border: 1px solid var(--border-default);
}

.nav-item.active .nav-badge {
  background: var(--bg-input);
  color: var(--text-primary);
  border-color: var(--border-default);
}

/* --- 主内容区样式 --- */
.content-column {
  padding: 40px;
  overflow-y: auto;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 32px;
}

.main-title {
  font-size: 28px;
  font-weight: 800;
  color: var(--text-heading);
  margin: 0;
}

.count-chip {
  display: inline-block;
  margin-top: 8px;
  font-size: 13px;
  color: var(--text-muted);
}

/* 翻页器重构 */
.modern-pagination {
  display: flex;
  align-items: center;
  background: var(--bg-input);
  padding: 4px;
  border-radius: 12px;
  gap: 4px;
}

.page-btn {
  all: unset;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  cursor: pointer;
  color: var(--text-secondary);
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
  background: var(--bg-card);
  color: var(--primary);
  box-shadow: var(--shadow-card);
}

.page-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.page-indicator {
  padding: 0 12px;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
}

.divider {
  margin: 0 4px;
  color: var(--border-default);
}

/* 网格布局 */
.grid-section {
  min-height: 400px;
}

.resource-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

/* 卡片入场动画 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.resource-grid :deep(.slot-wrapper) {
  animation: fadeInUp 0.5s cubic-bezier(0.23, 1, 0.32, 1) both;
}

/* 空状态 */
.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 300px;
  background: var(--bg-elevated);
  border-radius: 24px;
  border: 2px dashed var(--border-default);
}

.content-footer {
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px solid var(--border-default);
  color: var(--text-muted);
  font-size: 13px;
}

/* 响应式适配 */
@media (max-width: 1024px) {
  .resource-layout {
    grid-template-columns: 1fr;
  }
  .filter-column {
    border-right: none;
    border-bottom: 1px solid var(--border-default);
  }
}
</style>