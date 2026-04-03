<template>
  <AdminPageShell
    title="智能体管理"
    description="管理 Agent 配置、执行策略与发布状态。"
  >
    <div ref="pageRef" class="agent-page">
      <div class="toolbar">
        <div class="toolbar-left">
          <ToolbarSearchPill
            v-model="query.agentName"
            layout="toolbar"
            placeholder="搜索智能体名称"
            @search="fetchList"
          />

          <div class="status-switch" role="group" aria-label="状态筛选">
            <a-button
              class="status-btn status-btn-first"
              :class="{ active: query.status === 'enabled' }"
              @click="toggleStatusFilter('enabled')"
            >
              <template #icon><CheckCircleOutlined /></template>
              启用
            </a-button>
            <a-button
              class="status-btn status-btn-last"
              :class="{ active: query.status === 'disabled' }"
              @click="toggleStatusFilter('disabled')"
            >
              <template #icon><StopOutlined /></template>
              禁用
            </a-button>
          </div>
        </div>

        <div class="toolbar-right">
          <div class="toolbar-action-pair">
            <a-button class="toolbar-seg-btn toolbar-seg-reset" @click="resetFilters">重置</a-button>
            <a-button type="primary" class="toolbar-seg-btn toolbar-seg-add" @click="openCreate">
              <template #icon><PlusOutlined /></template>
              新增
            </a-button>
          </div>
        </div>
      </div>

      <BaseOverview
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
import { CheckCircleOutlined, PlusOutlined, StopOutlined } from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import BaseOverview from '@/components/home/BaseOverview.vue'
import ToolbarSearchPill from '@/components/home/ToolbarSearchPill.vue'
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

.toolbar-action-pair {
  display: inline-flex;
  align-items: stretch;
}

.toolbar-seg-btn {
  height: 44px;
  min-width: 100px;
  padding: 0 22px;
  font-weight: 600;
}

.toolbar-action-pair :deep(.toolbar-seg-reset.ant-btn) {
  border-top-left-radius: 14px;
  border-bottom-left-radius: 14px;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
  color: #475569;
  border-color: #cbd5e1;
  background: #fff;
  border-right: none;
  box-shadow: none;
}

.toolbar-action-pair :deep(.toolbar-seg-reset.ant-btn:hover) {
  color: #334155;
  border-color: #94a3b8;
  background: #f8fafc;
}

.toolbar-action-pair :deep(.toolbar-seg-add.ant-btn) {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
  border-top-right-radius: 14px;
  border-bottom-right-radius: 14px;
  margin-left: -1px;
  border: none;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  color: #fff;
  box-shadow:
    0 1px 0 rgba(255, 255, 255, 0.18) inset,
    0 3px 6px rgba(29, 78, 216, 0.4),
    0 10px 24px rgba(37, 99, 235, 0.32);
}

.toolbar-action-pair :deep(.toolbar-seg-add.ant-btn-primary) {
  box-shadow:
    0 1px 0 rgba(255, 255, 255, 0.18) inset,
    0 3px 6px rgba(29, 78, 216, 0.4),
    0 10px 24px rgba(37, 99, 235, 0.32);
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

  .toolbar-action-pair {
    width: 100%;
  }

  .toolbar-action-pair :deep(.toolbar-seg-reset.ant-btn),
  .toolbar-action-pair :deep(.toolbar-seg-add.ant-btn) {
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