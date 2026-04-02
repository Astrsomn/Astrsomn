<template>
  <div class="reslib-page">
    <div class="reslib-container">
      <div class="reslib-layout">
        <aside class="reslib-toc">
          <div class="toc-panel">
            <div class="toc-head">
              <div class="toc-title">资源库</div>
              <div class="toc-subtitle">按分类浏览全部入口</div>
            </div>

            <div class="toc-search">
              <a-input
                v-model:value="keyword"
                placeholder="搜索功能、模型或设置..."
                allow-clear
              />
            </div>

            <div class="toc-list">
              <button
                type="button"
                class="toc-item"
                :class="{ 'toc-item--active': activeGroupId === 'all' }"
                @click="activeGroupId = 'all'"
              >
                <span class="toc-item-title">全部</span>
                <span class="toc-item-count">{{ filteredTotalCount }}</span>
              </button>

              <button
                v-for="g in groupsFiltered"
                :key="g.id"
                type="button"
                class="toc-item"
                :class="{ 'toc-item--active': activeGroupId === g.id }"
                @click="activeGroupId = g.id"
              >
                <span class="toc-item-title">{{ g.title }}</span>
                <span class="toc-item-count">{{ g.filteredItems.length }}</span>
              </button>
            </div>
          </div>
        </aside>

        <main class="reslib-content">
          <div class="content-head">
            <div class="content-head-left">
              <div class="content-title">
                {{ activeGroupId === 'all' ? '全部入口' : (groupTitleById[activeGroupId] || '分类') }}
              </div>
              <div class="content-subtitle">
                {{ keywordTrim ? `包含关键字「${keywordTrim}」` : '按分类聚合展示' }}
              </div>
            </div>

            <div class="content-count">
              共 {{ displayedEntries.length }} 个
            </div>
          </div>

          <div v-if="displayedEntries.length === 0" class="reslib-empty">
            <div class="empty-title">没有匹配的入口</div>
            <div class="empty-subtitle">换个关键词试试，或切换到“全部”。</div>
          </div>

          <div v-else class="reslib-grid">
            <MenuSlotCard
              v-for="entry in displayedEntries"
              :key="entry.route"
              :entry="entry"
              :title="entry.label"
              :description="entry.description"
              :accent="entry.accent"
              variant="compact"
              @navigate="navigateTo"
            />
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import MenuSlotCard from '../demo/MenuSlotCard.vue'
import type { ManagementEntry, ManagementGroup } from '../backend/management'
import { getCurrentUserRole, resolveManagementGroups } from '../backend/management'

type GroupWithFiltered = ManagementGroup & {
  filteredItems: ManagementEntry[]
}

const router = useRouter()

const keyword = ref('')
const activeGroupId = ref<string>('all')

const currentRole = computed(() => getCurrentUserRole())
const groups = computed<ManagementGroup[]>(() => resolveManagementGroups(currentRole.value))

const keywordTrim = computed(() => keyword.value.trim().toLowerCase())

const matchesKeyword = (entry: ManagementEntry) => {
  if (!keywordTrim.value) return true
  const kw = keywordTrim.value
  return (
    entry.label.toLowerCase().includes(kw) ||
    entry.description.toLowerCase().includes(kw) ||
    entry.route.toLowerCase().includes(kw)
  )
}

const groupsFiltered = computed<GroupWithFiltered[]>(() =>
  groups.value.map((g) => ({
    ...g,
    filteredItems: g.items.filter(matchesKeyword),
  })),
)

const filteredTotalCount = computed(() =>
  groupsFiltered.value.reduce((sum, g) => sum + g.filteredItems.length, 0),
)

const groupTitleById = computed<Record<string, string>>(() =>
  Object.fromEntries(groups.value.map((g) => [g.id, g.title])),
)

const displayedEntries = computed<ManagementEntry[]>(() => {
  if (activeGroupId.value === 'all') return groupsFiltered.value.flatMap((g) => g.filteredItems)
  const g = groupsFiltered.value.find((x) => x.id === activeGroupId.value)
  return g ? g.filteredItems : []
})

const navigateTo = (path: string) => {
  void router.push(path)
}
</script>

<style scoped>
.reslib-page {
  position: relative;
  padding: 32px;
  background: var(--bg-base);
  overflow: hidden;
}

.reslib-page::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image: radial-gradient(
    color-mix(in srgb, var(--text-muted) 50%, transparent) 1px,
    transparent 0
  );
  background-size: 26px 26px;
  opacity: 0.22;
  pointer-events: none;
}

.reslib-page::after {
  content: '';
  position: absolute;
  right: -240px;
  top: -240px;
  width: 520px;
  height: 520px;
  background: radial-gradient(circle at center, color-mix(in srgb, var(--primary) 18%, transparent), transparent 60%);
  opacity: 0.9;
  pointer-events: none;
}

.reslib-container {
  position: relative;
  z-index: 1;
  max-width: 1600px;
  margin: 0 auto;
}

.reslib-layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 24px;
  align-items: start;
}

.reslib-toc {
  position: sticky;
  top: 88px;
  align-self: start;
}

.toc-panel {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: 24px;
  padding: 18px;
  box-shadow:
    0 1px 3px rgba(0, 0, 0, 0.02),
    0 4px 6px -1px rgba(0, 0, 0, 0.03);
}

.toc-head {
  margin-bottom: 14px;
}

.toc-title {
  font-weight: 900;
  color: var(--text-heading);
  font-size: 18px;
  margin-bottom: 6px;
}

.toc-subtitle {
  font-size: 12px;
  color: var(--text-muted);
  line-height: 1.5;
}

.toc-search {
  margin-top: 14px;
  margin-bottom: 16px;
}

.toc-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-height: calc(100vh - 160px);
  overflow: auto;
  padding-right: 4px;
}

.toc-item {
  width: 100%;
  text-align: left;
  border: 1px solid var(--border-subtle);
  background: color-mix(in srgb, var(--bg-card) 95%, rgba(255, 255, 255, 0.02));
  border-radius: 16px;
  padding: 12px 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  cursor: pointer;
  transition:
    transform 0.18s ease,
    border-color 0.18s ease,
    background 0.18s ease;
}

.toc-item:hover {
  transform: translateY(-1px);
  border-color: color-mix(in srgb, var(--primary) 35%, var(--border-subtle));
  background: color-mix(in srgb, var(--primary) 10%, var(--bg-card));
}

.toc-item--active {
  border-color: color-mix(in srgb, var(--primary) 60%, var(--border-subtle));
  background: color-mix(in srgb, var(--primary) 14%, var(--bg-card));
}

.toc-item-title {
  font-size: 13px;
  font-weight: 900;
  color: var(--text-heading);
  min-width: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.toc-item-count {
  flex-shrink: 0;
  font-size: 12px;
  font-weight: 900;
  color: var(--text-muted);
}

.reslib-content {
  min-width: 0;
}

.content-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.content-title {
  font-weight: 900;
  color: var(--text-heading);
  font-size: 18px;
}

.content-subtitle {
  margin-top: 6px;
  font-size: 12px;
  color: var(--text-muted);
}

.content-count {
  font-size: 12px;
  color: var(--text-muted);
  font-weight: 800;
  text-align: right;
}

.reslib-empty {
  border-radius: 24px;
  border: 1px dashed color-mix(in srgb, var(--border-subtle) 80%, transparent);
  padding: 28px 22px;
  background: color-mix(in srgb, var(--bg-card) 92%, rgba(255, 255, 255, 0.02));
}

.empty-title {
  font-size: 14px;
  font-weight: 900;
  color: var(--text-heading);
  margin-bottom: 8px;
}

.empty-subtitle {
  font-size: 12px;
  color: var(--text-muted);
  line-height: 1.6;
}

.reslib-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
}

@media (max-width: 1024px) {
  .reslib-page {
    padding: 16px;
  }

  .reslib-layout {
    grid-template-columns: 1fr;
    gap: 14px;
  }

  .reslib-toc {
    position: relative;
    top: auto;
  }

  .reslib-grid {
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  }
}
</style>

