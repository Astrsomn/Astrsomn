<template>
  <AdminPageShell
    title="智能体管理"
    description="管理 Agent 配置、执行策略与发布状态。"
    :breadcrumbs="breadcrumbs"
    :show-view-toggle="true"
    :view-mode="viewMode"
    :view-toggle-handler="handleViewToggle"
  >
    <div ref="pageRef" class="agent-page">
      <AstrsomnDataSection>
        <template #toolbar>
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
        </template>

        <AstrsomnDataView
          :mode="dataViewMode"
          :data-source="list"
          :loading="loading"
          :columns="tableColumns"
          :row-selection="tableRowSelection"
          :scroll="{ x: 980 }"
          row-key="id"
          empty-text="暂无匹配的智能体"
          :card-columns="currentGridColumns"
          :card-gap="agentGridGap"
          :card-min-width="agentCardMinWidth"
        >
          <template #card="{ record }">
            <AgentCard
              :record="record"
              :selected="record.id != null && selectedKeys.has(record.id)"
              @edit="openEdit"
              @delete="handleDeleteOne"
              @toggle="onToggleSelect"
            />
          </template>

          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag :color="record.status === 'enabled' ? 'green' : 'default'">
                {{ record.status === 'enabled' ? '启用' : '禁用' }}
              </a-tag>
            </template>

            <template v-else-if="column.key === 'actions'">
              <a-space>
                <a-button type="link" size="small" @click="openEdit(record)">
                  <EditOutlined />
                </a-button>
                <a-popconfirm title="确定删除该智能体吗？" @confirm="handleDeleteFromRecord(record)">
                  <a-button type="link" danger size="small">
                    <DeleteOutlined />
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </AstrsomnDataView>

        <template #pagination>
          <AstrsomnPagination
            :current="page.pageNum"
            :page-size="page.pageSize"
            :total="page.total"
            @change="onPageChange"
          />
        </template>
      </AstrsomnDataSection>

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
import { DeleteOutlined, EditOutlined, PlusOutlined, ReloadOutlined, ClockCircleOutlined, UserOutlined, ClusterOutlined } from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'

import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
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
const viewMode = ref<'grid' | 'list'>('list')
const selectedKeys = ref<Set<string | number>>(new Set())
const currentGridColumns = ref(resolveGridColumns())
const dataViewMode = computed<'card' | 'table'>(() => (viewMode.value === 'grid' ? 'card' : 'table'))

const breadcrumbs = [
  { title: 'AI 配置', href: '/admin/ai-config' },
  { title: '智能体管理' },
]

const handleViewToggle = () => {
  viewMode.value = viewMode.value === 'grid' ? 'list' : 'grid'
}

const selectedCount = computed(() => selectedKeys.value.size)
const allCurrentSelected = computed(() => list.value.length > 0 && selectedKeys.value.size === list.value.length)
const partCurrentSelected = computed(() => selectedKeys.value.size > 0 && selectedKeys.value.size < list.value.length)
const tableSelectedRowKeys = computed<Array<string | number>>(() => Array.from(selectedKeys.value))

const tableColumns = [
  { 
    title: 'Agent Key', 
    dataIndex: 'agentKey', 
    key: 'agentKey', 
    ellipsis: true, 
    width: 200,
    copyable: true
  },
  { title: '智能体名称', dataIndex: 'agentName', key: 'agentName', ellipsis: true, width: 200 },
  { title: '模型实例', dataIndex: 'chatInstanceName', key: 'chatInstanceName', ellipsis: true, width: 180 },
  { title: '提示词策略', dataIndex: 'promptTitle', key: 'promptTitle', ellipsis: true, width: 180 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  {title: '环境', dataIndex: 'envCode', key: 'envCode', width: 120, ellipsis: true, tag: true, tagColor: 'blue', icon: ClusterOutlined},
  {title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 200, dateFormat: true, icon: ClockCircleOutlined},
  {title: '创建人', dataIndex: 'createUser', key: 'createUser', width: 150, icon: UserOutlined},
  { title: '操作', key: 'actions', width: 120, fixed: 'right' as const }
]

const tableRowSelection = computed(() => ({
  selectedRowKeys: tableSelectedRowKeys.value,
  onChange: (keys: Array<string | number>) => {
    selectedKeys.value = new Set(keys)
  }
}))

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

const onPageChange = (p: number, size: number) => {
  page.pageNum = p
  page.pageSize = size
  void fetchList()
}

const openCreate = () => {
  editingId.value = undefined
  assemblyVisible.value = true
}

const toolbarSegmentButtons = computed(() => [
  { label: '重置', plain: true, icon: ReloadOutlined, onClick: resetFilters },
  { 
    label: selectedCount.value > 0 ? `删除 (${selectedCount.value})` : '删除', 
    icon: DeleteOutlined, 
    onClick: () => handleBatchDelete(Array.from(selectedKeys.value)),
    disabled: selectedCount.value === 0,
    type: 'danger',
    plain: true
  },
  { label: '新增', type: 'primary', icon: PlusOutlined, onClick: openCreate, plain: true },
])

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

const handleDeleteFromRecord = async (record: AiAgent) => {
  const id = record.id
  if (id == null) return
  await handleDeleteOne(id)
}

const onViewModeChange = (mode: 'grid' | 'list') => {
  viewMode.value = mode
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

  flex-wrap: wrap;

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

}
</style>