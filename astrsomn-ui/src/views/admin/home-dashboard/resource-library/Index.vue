<template>
  <div class="immersive-wrapper">
    <div class="glow-orb orb-1"></div>
    <div class="glow-orb orb-2"></div>

    <div class="reslib-container">
      <div class="reslib-layout">
        <aside class="reslib-toc">
          <div class="glass-panel">
            <div class="toc-head">
              <div class="toc-title">AI 资源库</div>
              <div class="toc-subtitle">RESOURCE CENTER</div>
            </div>

            <div class="toc-search">
              <a-input
                v-model:value="keyword"
                placeholder="搜索资源..."
                allow-clear
                class="glass-input"
              >
                <template #prefix><SearchOutlined /></template>
              </a-input>
            </div>

            <div class="toc-list">
              <button
                type="button"
                class="toc-btn"
                :class="{ active: activeGroupId === 'all' }"
                @click="activeGroupId = 'all'"
              >
                <span>全部</span>
                <span class="count-tag">{{ filteredTotalCount }}</span>
              </button>

              <button
                v-for="g in groupsFiltered"
                :key="g.id"
                type="button"
                class="toc-btn"
                :class="{ active: activeGroupId === g.id }"
                @click="activeGroupId = g.id"
              >
                <span class="title-text">{{ g.title }}</span>
                <span class="count-tag">{{ g.filteredItems.length }}</span>
              </button>
            </div>
          </div>
        </aside>

        <main class="reslib-content">
          <div class="content-header">
            <div class="header-info">
              <h1 class="main-title">
                {{ activeGroupId === 'all' ? '全部入口' : (groupTitleById[activeGroupId]) }}
              </h1>
              <div class="status-bar">
                <span class="dot"></span>
                系统资源实时同步中
              </div>
            </div>
            
            <div class="top-pagination" v-if="totalPages > 1">
              <button class="nav-btn" :disabled="currentPage === 1" @click="changePage(currentPage - 1)">
                <LeftOutlined />
              </button>
              <span class="page-num"><b>{{ currentPage }}</b> / {{ totalPages }}</span>
              <button class="nav-btn" :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)">
                <RightOutlined />
              </button>
            </div>
          </div>

          <div class="grid-container" ref="gridContainerRef">
            <div v-if="paginatedEntries.length === 0" class="empty-state">
              <a-empty :image="Empty.PRESENTED_IMAGE_SIMPLE" />
            </div>
            <div v-else class="reslib-grid">
              <MenuSlotCard
                v-for="entry in paginatedEntries"
                :key="entry.route"
                :entry="entry"
                variant="compact"
                class="glass-card"
                @navigate="navigateTo"
              />
            </div>
          </div>
          
          <div class="content-footer">
            <span class="footer-hint">Total: {{ filteredEntries.length }} entries filtered</span>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Empty } from 'ant-design-vue'
import { 
  SearchOutlined, LeftOutlined, RightOutlined, 
  IdcardOutlined, KeyOutlined 
} from '@ant-design/icons-vue'
import MenuSlotCard from '../demo/MenuSlotCard.vue'
import type { ManagementEntry, ManagementGroup } from '../backend/management'
import { getCurrentUserRole, resolveManagementGroups } from '../backend/management'

// --- 基础数据逻辑 ---
const router = useRouter()
const keyword = ref('')
const activeGroupId = ref<string>('all')
const currentPage = ref(1)
const pageSize = ref(9) // 每页固定显示 12 个
const gridContainerRef = ref<HTMLElement | null>(null)

const currentRole = computed(() => getCurrentUserRole())
const groups = computed<ManagementGroup[]>(() => resolveManagementGroups(currentRole.value))
const keywordTrim = computed(() => keyword.value.trim().toLowerCase())

const matchesKeyword = (entry: ManagementEntry) => {
  if (!keywordTrim.value) return true
  const kw = keywordTrim.value
  return (
    entry.label.toLowerCase().includes(kw) ||
    entry.description.toLowerCase().includes(kw)
  )
}

const groupsFiltered = computed(() =>
  groups.value.map((g) => ({
    ...g,
    filteredItems: g.items.filter(matchesKeyword),
  })).filter(g => g.filteredItems.length > 0 || !keywordTrim.value)
)

const filteredTotalCount = computed(() =>
  groupsFiltered.value.reduce((sum, g) => sum + g.filteredItems.length, 0),
)

const groupTitleById = computed(() =>
  Object.fromEntries(groups.value.map((g) => [g.id, g.title])),
)

// --- 分页逻辑核心 ---
const filteredEntries = computed(() => {
  if (activeGroupId.value === 'all') {
    return groupsFiltered.value.flatMap((g) => g.filteredItems)
  }
  const g = groupsFiltered.value.find((x) => x.id === activeGroupId.value)
  return g ? g.filteredItems : []
})

const totalPages = computed(() => Math.ceil(filteredEntries.value.length / pageSize.value))

const paginatedEntries = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredEntries.value.slice(start, end)
})

// 监听条件变化，重置页码
watch([keyword, activeGroupId], () => {
  currentPage.value = 1
})

const changePage = (page: number) => {
  currentPage.value = page
  // 切换页面时仅滚动当前卡片列表容器，避免整页出现滚动条
  gridContainerRef.value?.scrollTo({ top: 0, behavior: 'smooth' })
}

const navigateTo = (path: string) => void router.push(path)
</script>
<style scoped>
/* 强制页面不滚动：铺满全屏 + 固定定位 + 内部区域滚动 */
.immersive-wrapper {
  /* Component-level palette (adaptive to theme.css dark/light via CSS vars overrides below) */
  --glass-bg: rgba(255, 255, 255, 0.05);
  --glass-border: rgba(255, 255, 255, 0.1);
  --glass-hover-bg: rgba(255, 255, 255, 0.07);
  --text-muted-local: var(--text-muted);
  --accent-soft: rgba(59, 130, 246, 0.18);
  
  height: 100dvh;
  min-height: 100vh;
  width: 100%;
  overflow: hidden;
  overflow-x: hidden;
  overflow-y: hidden;
  position: fixed;
  inset: 0;

  /* 更克制的渐变背景（减少“艳色”） */
  background: radial-gradient(circle at top right, rgba(59, 130, 246, 0.16), var(--bg-base) 45%, var(--bg-base) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-primary);
}

/* 浅色模式下的玻璃/背景适配 */
:global(:root.light) .immersive-wrapper {
  --glass-bg: rgba(255, 255, 255, 0.72);
  --glass-border: rgba(0, 0, 0, 0.08);
  --glass-hover-bg: rgba(0, 0, 0, 0.04);
  --text-muted-local: var(--text-muted);
  background: radial-gradient(circle at top right, rgba(0, 123, 255, 0.12), var(--bg-base) 35%, var(--bg-base) 100%);
}

/* 2. 背景发光球 */
.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(90px);
  z-index: 0;
  opacity: 0.22; /* 降低亮度/饱和感 */
  animation: float 10s infinite alternate;
}
.orb-1 {
  width: 360px; height: 360px;
  background: rgba(59, 130, 246, 0.55);
  top: -100px; left: -100px;
}
.orb-2 {
  width: 440px; height: 440px;
  background: rgba(124, 58, 237, 0.36);
  bottom: -150px; right: -100px;
  animation-delay: -5s;
}

@keyframes float {
  from { transform: translate(0, 0); }
  to { transform: translate(50px, 100px); }
}

/* 3. 布局容器 */
.reslib-container {
  position: relative;
  z-index: 1;
  width: calc(100% - 32px);
  max-width: 1500px;
  height: 85dvh; /* 限制高度 */
}

.reslib-layout {
  display: grid;
  grid-template-columns: 280px minmax(0, 1fr);
  gap: 30px;
  height: 100%;
  min-height: 0;
}

/* 4. 左侧玻璃面板 */
.glass-panel {
  background: var(--glass-bg);
  backdrop-filter: blur(20px);
  border: 1px solid var(--glass-border);
  border-radius: 32px;
  height: 100%;
  padding: 30px;
  display: flex;
  flex-direction: column;
}

.toc-title { font-size: 22px; font-weight: 800; letter-spacing: 1px; color: var(--text-heading); }
.toc-subtitle { font-size: 10px; color: var(--accent-blue); font-weight: 700; margin-bottom: 24px; }

.glass-input :deep(.ant-input) {
  background: rgba(0, 0, 0, 0.22) !important;
  color: var(--text-primary);
  border: 1px solid var(--glass-border);
  border-radius: 12px;
}

:global(:root.light) .glass-input :deep(.ant-input) {
  background: rgba(0, 0, 0, 0.04) !important;
  color: var(--text-primary);
}

.toc-list {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  margin-top: 10px;
  padding-right: 5px;
  min-height: 0;
}

.toc-btn {
  all: unset;
  width: 100%;
  box-sizing: border-box;
  padding: 14px 20px;
  margin-bottom: 8px;
  border-radius: 16px;
  display: flex;
  justify-content: space-between;
  cursor: pointer;
  transition: 0.3s;
  font-size: 14px;
  color: var(--text-secondary);
}

.toc-btn:hover { background: rgba(255,255,255,0.06); color: var(--text-primary); }

:global(:root.light) .toc-btn:hover { background: rgba(0,0,0,0.03); }

.toc-btn.active {
  background: linear-gradient(90deg, rgba(59, 130, 246, 0.32), rgba(168, 85, 247, 0.22));
  color: var(--text-primary);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.22);
}

.count-tag {
  background: var(--border-subtle);
  padding: 2px 8px;
  border-radius: 8px;
  font-size: 11px;
}

/* 5. 右侧内容区 */
.reslib-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-width: 0;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.main-title { font-size: 32px; font-weight: 800; margin: 0; }
.status-bar {
  display: flex; align-items: center; gap: 8px;
  font-size: 12px; color: var(--success); margin-top: 5px;
}
.status-bar .dot {
  width: 8px; height: 8px;
  background: var(--success);
  border-radius: 50%; box-shadow: 0 0 10px rgba(16, 185, 129, 0.55);
}

/* 分页器 */
.top-pagination {
  display: flex;
  align-items: center;
  gap: 15px;
  background: var(--glass-bg);
  padding: 8px 16px;
  border-radius: 99px;
  border: 1px solid var(--glass-border);
}

.nav-btn {
  all: unset;
  cursor: pointer;
  color: var(--text-secondary);
  transition: 0.3s;
}
.nav-btn:hover:not(:disabled) { color: var(--text-primary); transform: scale(1.1); }
.nav-btn:disabled { opacity: 0.2; cursor: not-allowed; }
.page-num { font-size: 14px; }
.page-num b { color: var(--accent-blue); }

/* 网格区滚动 */
.grid-container {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 10px;
  min-height: 0;
}

/* 针对内部滚动条的优化 */
.grid-container::-webkit-scrollbar { width: 6px; }
.grid-container::-webkit-scrollbar-thumb { background: var(--glass-border); border-radius: 10px; }

.reslib-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr); /* 默认 4 列（会在小屏下自动降列） */
  gap: 20px;
  min-width: 0;
}

@media (max-width: 1200px) {
  .reslib-grid { grid-template-columns: repeat(3, 1fr); }
}

@media (max-width: 900px) {
  .reslib-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 600px) {
  .reslib-grid { grid-template-columns: repeat(1, 1fr); }
}

/* 卡片毛玻璃化 */
.glass-card {
  background: var(--glass-bg) !important;
  backdrop-filter: blur(10px);
  border: 1px solid var(--glass-border) !important;
  border-radius: 24px !important;
  transition: 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.glass-card:hover {
  transform: translateY(-6px) scale(1.01);
  background: var(--glass-hover-bg) !important;
  border-color: rgba(255,255,255,0.3) !important;
  box-shadow: 0 16px 28px rgba(0,0,0,0.22);
}

.content-footer {
  margin-top: 20px;
  font-size: 12px;
  color: var(--text-muted-local);
  text-align: center;
}
</style>