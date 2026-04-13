<template>
  <AdminPageShell
    title="智能体管理"
    description="管理 Agent 配置、执行策略与发布状态。"
  >
    <div ref="pageRef" class="agent-page">
      <div class="toolbar">
        <div class="toolbar-left">
          <AstrsomnSearchPill
            v-model="query.agentName"
            layout="toolbar"
            placeholder="搜索智能体名称"
            @search="fetchList"
          />

          <AstrsomnStateSwitch v-model="query.status" @change="fetchList" />

        </div>

        <div class="toolbar-right">
          <AstrsomnSegmentedButton :buttons="toolbarSegmentButtons" />
        </div>
      </div>

      <AstrsomnOverview
        :list-length="list.length"
        :selected-count="selectedCount"
        :all-current-selected="allCurrentSelected"
        :part-current-selected="partCurrentSelected"
        :show-actions="true"
        @toggle-select-all="onToggleSelectAll"
      />

      <a-spin :spinning="loading">
        <div v-if="list.length > 0" class="agent-grid">
          <div
            v-for="item in list"
            :key="item.id ?? item.agentKey ?? item.agentName ?? 'agent'"
            class="agent-grid-item"
          >
            <AgentCard
              :record="item"
              :selected="selectedKeys.has(item.id!)"
              @edit="openEdit"
              @delete="handleDeleteOne"
              @toggle="onToggleSelect"
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

      <AgentForm
        v-model:visible="assemblyVisible"
        :record-id="editingId"
        @success="fetchList"
      />
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { CheckCircleOutlined, PlusOutlined, ReloadOutlined, StopOutlined } from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AstrsomnOverview from '@/components/home/AstrsomnOverview.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'
import AstrsomnStateSwitch from '@/components/home/AstrsomnStateSwitch.vue'
import AgentCard from './AgentCard.vue'
import AgentForm from './AgentForm.vue'
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

const pageRef = ref<HTMLElement | null>(null)
const loading = ref(false)
const assemblyVisible = ref(false)
const editingId = ref<string | number | undefined>(undefined)
const query = reactive<{ agentName?: string; status?: string }>({})
const list = ref<AiAgent[]>([])
const selectedKeys = ref<Set<string | number>>(new Set())
const currentGridColumns = ref(resolveGridColumns())
const agentGridTemplateColumns = computed(() => `repeat(${currentGridColumns.value}, minmax(0, 1fr))`)

const selectedCount = computed(() => selectedKeys.value.size)
const allCurrentSelected = computed(() => list.value.length > 0 && selectedKeys.value.size === list.value.length)
const partCurrentSelected = computed(() => selectedKeys.value.size > 0 && selectedKeys.value.size < list.value.length)

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
    selectedKeys.value.clear()
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
  editingId.value = undefined
  assemblyVisible.value = true
}

const toolbarSegmentButtons: SegmentedButton[] = [
  { label: '重置', icon: ReloadOutlined, onClick: resetFilters },
  { label: '新增', type: 'primary', icon: PlusOutlined, onClick: openCreate },
]

const openEdit = async (record: AiAgent) => {
  const id = record.id
  if (id == null) return
  editingId.value = id
  assemblyVisible.value = true
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

const onToggleSelectAll = (checked: boolean) => {
  if (checked) {
    list.value.forEach(item => {
      if (item.id) {
        selectedKeys.value.add(item.id)
      }
    })
  } else {
    selectedKeys.value.clear()
  }
}

const onToggleSelect = (id: number | string, checked: boolean) => {
  if (checked) {
    selectedKeys.value.add(id)
  } else {
    selectedKeys.value.delete(id)
  }
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
  padding: 20px;
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
  border-radius: var(--radius-pro);

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
  align-items: stretch;
  border-radius: 14px;
  border: 1px solid #e2e8f0;
  background: #fff;
  overflow: hidden;
  box-shadow:
    0 1px 2px rgba(15, 23, 42, 0.05),
    0 4px 10px rgba(15, 23, 42, 0.06);
}

.status-switch :deep(.status-btn.ant-btn) {
  height: 44px;
  border-radius: 0;
  border: none;
  box-shadow: none;
  color: #64748b;
  background: transparent;
  font-weight: 600;
}

.status-switch :deep(.status-btn-last.ant-btn) {
  border-left: 1px solid #e2e8f0;
}

.status-switch :deep(.status-btn.active.ant-btn) {
  color: #1d4ed8;
  background: #eff6ff;
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

  .toolbar-left,
  .toolbar-right {
    width: 100%;
  }

  :deep(.toolbar-search-pill--toolbar) {
    max-width: none;
    width: 100%;
  }

  .toolbar-right :deep(.toolbar-segmented-btn) {
    width: 100%;
  }

  .toolbar-right :deep(.seg-btn) {
    flex: 1;
  }

  .status-switch {
    width: 100%;
  }

  .status-switch :deep(.status-btn.ant-btn) {
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