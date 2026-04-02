<template>
  <AdminPageShell
    title="智能体管理"
    description="管理 Agent 配置、执行策略与发布状态。"
  >
    <div ref="pageRef" class="agent-page">
      <div class="toolbar">
        <div class="toolbar-left">
          <div class="search-cluster">
            <a-input
              v-model:value="query.agentName"
              placeholder="搜索智能体名称"
              class="toolbar-input search-main-input"
              allow-clear
              @pressEnter="fetchList"
            >
              <template #prefix><search-outlined /></template>
            </a-input>
          </div>

          <div class="status-switch" role="group" aria-label="状态筛选">
            <a-button
              class="status-btn"
              :class="{ active: query.status === 'enabled' }"
              @click="toggleStatusFilter('enabled')"
            >
              <template #icon><check-circle-outlined /></template>
              启用
            </a-button>
            <a-button
              class="status-btn"
              :class="{ active: query.status === 'disabled' }"
              @click="toggleStatusFilter('disabled')"
            >
              <template #icon><stop-outlined /></template>
              禁用
            </a-button>
          </div>
        </div>

        <div class="toolbar-right">
          <router-link v-slot="{ navigate }" to="/admin/agents/model-assembly" custom>
            <a-button class="ghost-btn" @click="navigate">
              <template #icon><apartment-outlined /></template>
              模型组装
            </a-button>
          </router-link>
          <a-button type="primary" class="primary-btn" @click="fetchList">
            <template #icon><search-outlined /></template>
            查询
          </a-button>

          <a-button class="ghost-btn" @click="resetFilters">重置</a-button>
          <a-button class="ghost-btn" @click="openCreate">
            <template #icon><plus-outlined /></template>
            新增
          </a-button>
        </div>
      </div>

      <a-spin :spinning="loading">
        <div v-if="list.length > 0" class="agent-grid">
          <div
            v-for="item in list"
            :key="item.id ?? item.agentKey ?? item.agentName ?? 'agent'"
            class="agent-grid-item"
          >
            <AgentCard
              :record="item"
              @edit="openEdit"
              @delete="handleDeleteOne"
            />
          </div>
        </div>

        <div v-else class="empty-wrap">
          <a-empty description="暂无匹配的智能体卡片" />
        </div>
      </a-spin>

      <div class="pagination-wrap">
        <span class="pagination-total">共 {{ page.total }} 条</span>
        <a-pagination
          :current="page.pageNum"
          :page-size="page.pageSize"
          :total="page.total"
          :show-size-changer="false"
          @change="onPageChange"
        />
      </div>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import {
  ApartmentOutlined,
  CheckCircleOutlined,
  DeleteOutlined,
  PlusOutlined,
  SearchOutlined,
  StopOutlined
} from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AgentCard from './AgentCard.vue'
import { aiAgentApi, type AiAgent, type PageResponse } from '@/api/aiAgent.ts'

const AGENT_CARD_MIN_WIDTH_PX = 360
const AGENT_GRID_GAP_PX = 12
const agentCardMinWidth = `${AGENT_CARD_MIN_WIDTH_PX}px`
const agentGridGap = `${AGENT_GRID_GAP_PX}px`

const resolveGridColumns = () => {
  if (typeof window === 'undefined') return 3
  const width = pageRef.value?.clientWidth ?? window.innerWidth
  const columns = Math.floor((width + AGENT_GRID_GAP_PX) / (AGENT_CARD_MIN_WIDTH_PX + AGENT_GRID_GAP_PX))
  return Math.max(1, Math.min(3, columns))
}

const resolvePageSize = (columns: number) => {
  if (columns >= 3) return 9
  if (columns === 2) return 8
  return 6
}

const router = useRouter()
const pageRef = ref<HTMLElement | null>(null)
const loading = ref(false)
const query = reactive<{ agentName?: string; status?: string }>({})
const list = ref<AiAgent[]>([])
const currentGridColumns = ref(resolveGridColumns())
const agentGridTemplateColumns = computed(() => `repeat(${currentGridColumns.value}, minmax(0, 1fr))`)

const page = reactive({
  pageNum: 1,
  pageSize: resolvePageSize(currentGridColumns.value),
  total: 0
})

const fetchList = async () => {
  loading.value = true
  try {
    const resp: PageResponse<AiAgent> = await aiAgentApi.queryPage({
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        name: query.agentName || undefined,
        status: query.status || undefined
      }
    })
    list.value = resp.list || []
    page.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

const syncPageSizeWithGrid = async () => {
  const nextColumns = resolveGridColumns()
  currentGridColumns.value = nextColumns
  const nextPageSize = resolvePageSize(nextColumns)
  if (page.pageSize === nextPageSize) return
  page.pageSize = nextPageSize
  page.pageNum = 1
  await fetchList()
}

const toggleStatusFilter = (value: 'enabled' | 'disabled') => {
  query.status = query.status === value ? undefined : value
}

const resetFilters = () => {
  query.agentName = undefined
  query.status = undefined
  page.pageNum = 1
  void fetchList()
}

const onPageChange = (p: number) => {
  page.pageNum = p
  void fetchList()
}

const openCreate = () => {
  router.push('/admin/agents/model-assembly')
}

const openEdit = async (record: AiAgent) => {
  const id = record.id
  if (id == null) return
  router.push(`/admin/agents/model-assembly?id=${id}`)
}

const handleDeleteOne = async (id: number | string) => {
  await aiAgentApi.delete([id])
  message.success('已删除')
  void fetchList()
}

const handleBatchDelete = async (ids: Array<number | string>) => {
  if (ids.length === 0) return
  await aiAgentApi.delete(ids)
  message.success('已删除')
  void fetchList()
}

let resizeObserver: ResizeObserver | null = null

const voidSyncPageSizeWithGrid = () => {
  void syncPageSizeWithGrid()
}

onMounted(() => {
  voidSyncPageSizeWithGrid()
  if (typeof ResizeObserver !== 'undefined' && pageRef.value) {
    resizeObserver = new ResizeObserver(() => {
      voidSyncPageSizeWithGrid()
    })
    resizeObserver.observe(pageRef.value)
    return
  }
  window.addEventListener('resize', voidSyncPageSizeWithGrid)
})

onBeforeUnmount(() => {
  resizeObserver?.disconnect()
  window.removeEventListener('resize', voidSyncPageSizeWithGrid)
})

void fetchList()
</script>

<style scoped>
.agent-page {
  padding: 0 2px 0;
  margin-top: -8px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 6px;
  flex-wrap: wrap;
  padding: 16px 0;
  border-radius: var(--radius-xl);

}

.toolbar-left {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
  flex: 1;
}

.toolbar-right {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.search-cluster {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
  padding: 6px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-default);
  background: var(--bg-surface);
  box-shadow: none;
}

.search-cluster :deep(.ant-input-affix-wrapper) {
  border: none;
  box-shadow: none;
  background: transparent;
}

.search-cluster :deep(.ant-input-affix-wrapper:hover),
.search-cluster :deep(.ant-input-affix-wrapper-focused) {
  border: none;
  box-shadow: none;
  background: color-mix(in srgb, var(--bg-card) 85%, var(--bg-surface));
}

.search-cluster :deep(.ant-input) {
  font-size: 14px;
}

.toolbar-input {
  width: 200px;
}

.search-main-input {
  width: 360px;
}

.primary-btn,
.ghost-btn {
  height: 40px;
  border-radius: var(--radius-sm);
}

.danger-btn {
  color: var(--error);
  border-color: color-mix(in srgb, var(--error) 28%, var(--border-default));
  background: color-mix(in srgb, var(--error) 7%, var(--bg-card));
}

.danger-btn:hover,
.danger-btn:focus {
  color: var(--error) !important;
  border-color: color-mix(in srgb, var(--error) 42%, var(--border-default)) !important;
  background: color-mix(in srgb, var(--error) 12%, var(--bg-card)) !important;
}

.status-switch {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 4px;
  border-radius: var(--radius-sm);
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
}

.status-btn {
  height: 36px;
  border: none;
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  background: transparent;
  box-shadow: none;
}

.status-btn.active {
  color: var(--primary);
  background: color-mix(in srgb, var(--primary) 10%, var(--bg-card));
}

.agent-grid {
  display: grid;
  grid-template-columns: v-bind(agentGridTemplateColumns);
  gap: v-bind(agentGridGap);
}

.agent-grid-item {
  min-width: v-bind(agentCardMinWidth);
}

.empty-wrap {
  display: flex;
  justify-content: center;
  padding: 32px 0 12px;
}

.pagination-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.pagination-total {
  font-size: 13px;
  color: var(--text-secondary);
}

@media (max-width: 720px) {
  .toolbar {
    padding: 14px;
  }

  .toolbar-input,
  .search-main-input {
    width: 100%;
  }

  .toolbar-left,
  .toolbar-right,
  .search-cluster {
    width: 100%;
  }

  .search-cluster {
    padding: 8px;
  }

  .status-switch {
    width: 100%;
    justify-content: space-between;
  }

  .status-btn {
    flex: 1;
  }

  .agent-grid {
    grid-template-columns: 1fr;
  }

  .agent-grid-item {
    min-width: 0;
  }

  .pagination-wrap {
    justify-content: center;
  }
}

@media (max-width: 560px) {
  .pagination-total {
    width: 100%;
    text-align: center;
  }
}
</style>