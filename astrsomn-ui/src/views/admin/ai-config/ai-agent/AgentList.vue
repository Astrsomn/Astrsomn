<template>
  <AstPageShell
      :breadcrumbs="breadcrumbs"
      :show-view-toggle="true"
      :view-mode="viewMode"
      :view-toggle-handler="handleViewToggle"
      :description="t.list.description"
      :title="t.list.title"
  >
    <div ref="pageRef" class="agent-page">
      <AstDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstSearchInput
                  v-model="query.agentName"
                  layout="toolbar"
                  :placeholder="t.list.searchPlaceholder"
                  @search="fetchList"
              />
              <AstStatusSwitch v-model="query.status" @change="fetchList"/>
            </div>
            <div class="toolbar-right">
              <AstegmentedButton :buttons="toolbarSegmentButtons"/>
            </div>
          </div>
        </template>

        <AstDataView
            :card-columns="currentGridColumns"
            :card-gap="agentGridGap"
            :card-min-width="agentCardMinWidth"
            :columns="tableColumns"
            :data-source="list"
            :loading="loading"
            :mode="dataViewMode"
            :row-selection="tableRowSelection"
            :scroll="{ x: 980 }"
            :empty-text="t.list.emptyText"
            row-key="id"
        >
          <template #card="{ record }">
            <AgentCard
                :record="record"
                :selected="record.id != null && selectedKeys.has(record.id)"
                @delete="handleDeleteOne"
                @edit="openEdit"
                @toggle="onToggleSelect"
            />
          </template>

          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'providerAvatar'">
              <img
                  v-if="providerAvatarCell(record)"
                  :alt="record.agentName || 'provider'"
                  :src="providerAvatarCell(record)"
                  aria-hidden="true"
                  class="provider-avatar-cell"
              />
              <span v-else class="text-secondary">—</span>
            </template>
            <template v-else-if="column.key === 'status'">
              <a-tag :color="record.status === 'enabled' ? 'green' : 'default'">
                {{ record.status === 'enabled' ? t.list.status.enabled : t.list.status.disabled }}
              </a-tag>
            </template>

            <template v-else-if="column.key === 'actions'">
              <a-space>
                <a-button size="small" type="link" @click="openEdit(record)">
                  <EditOutlined/>
                </a-button>
                <a-popconfirm :title="t.list.deleteConfirm" @confirm="handleDeleteFromRecord(record)">
                  <a-button danger size="small" type="link">
                    <DeleteOutlined/>
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </AstDataView>

        <template #pagination>
          <AstPagination
              :current="page.pageNum"
              :page-size="page.pageSize"
              :total="page.total"
              @change="onPageChange"
          />
        </template>
      </AstDataSection>

    </div>
  </AstPageShell>
</template>

<script lang="ts" setup>
import {computed, onBeforeUnmount, onMounted, reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import {message} from 'ant-design-vue'
import {usePageTranslation} from '@/locales/pages.ts'
import {
  ClockCircleOutlined,
  ClusterOutlined,
  DeleteOutlined,
  EditOutlined,
  PlusOutlined,
  ReloadOutlined,
  UserOutlined
} from '@ant-design/icons-vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import AstDataSection from '@/components/home/AstDataSection.vue'
import AstDataView from '@/components/home/AstDataView.vue'

import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstegmentedButton from '@/components/home/AstegmentedButton.vue'
import AstStatusSwitch from '@/components/home/AstStatusSwitch.vue'
import AgentCard from './component/AgentCard.vue'
import {type AiAgent, aiAgentApi, type PageResponse} from '@/api/aiAgent.ts'

const t = usePageTranslation('ai-agent')

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
const viewMode = ref<'grid' | 'list'>('list')
const selectedKeys = ref<Set<string | number>>(new Set())
const currentGridColumns = ref(resolveGridColumns())
const dataViewMode = computed<'card' | 'table'>(() => (viewMode.value === 'grid' ? 'card' : 'table'))

const breadcrumbs = computed(() => [
  {title: t.value.list.breadcrumb.aiConfig, href: '/admin/ai-config'},
  {title: t.value.list.breadcrumb.agentManagement},
])

const handleViewToggle = () => {
  viewMode.value = viewMode.value === 'grid' ? 'list' : 'grid'
}

const selectedCount = computed(() => selectedKeys.value.size)
const allCurrentSelected = computed(() => list.value.length > 0 && selectedKeys.value.size === list.value.length)
const partCurrentSelected = computed(() => selectedKeys.value.size > 0 && selectedKeys.value.size < list.value.length)
const tableSelectedRowKeys = computed<Array<string | number>>(() => Array.from(selectedKeys.value))

const tableColumns = computed(() => [
  {title: t.value.list.column.provider, key: 'providerAvatar', width: 80, align: 'center' as const},
  {
    title: 'Agent Key',
    dataIndex: 'agentKey',
    key: 'agentKey',
    ellipsis: true,
    width: 200,
    copyable: true
  },
  {title: t.value.list.column.agentName, dataIndex: 'agentName', key: 'agentName', ellipsis: true, width: 200},
  {title: t.value.list.column.chatInstanceName, dataIndex: 'chatInstanceName', key: 'chatInstanceName', ellipsis: true, width: 180},
  {title: t.value.list.column.promptTitle, dataIndex: 'promptTitle', key: 'promptTitle', ellipsis: true, width: 180},
  {title: t.value.list.column.status, dataIndex: 'status', key: 'status', width: 100},
  {
    title: t.value.list.column.envCode,
    dataIndex: 'envCode',
    key: 'envCode',
    width: 120,
    ellipsis: true,
    tag: true,
    tagColor: 'blue',
    icon: ClusterOutlined
  },
  {
    title: t.value.list.column.createTime,
    dataIndex: 'createTime',
    key: 'createTime',
    width: 200,
    dateFormat: true,
    icon: ClockCircleOutlined
  },
  {title: t.value.list.column.createUser, dataIndex: 'createUser', key: 'createUser', width: 150, icon: UserOutlined},
  {title: t.value.list.column.actions, key: 'actions', width: 120, fixed: 'right' as const}
])

const providerAvatarCell = (record: AiAgent) => {
  const raw = record?.providerAvatar
  return typeof raw === 'string' && raw.trim() ? raw.trim() : ''
}

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
  router.push('/admin/ai-config/builder')
}

const toolbarSegmentButtons = computed(() => [
  {label: t.value.list.reset, plain: true, icon: ReloadOutlined, onClick: resetFilters},
  {
    label: selectedCount.value > 0 ? t.value.list.deleteCount.replace('{n}', String(selectedCount.value)) : t.value.list.delete,
    icon: DeleteOutlined,
    onClick: () => handleBatchDelete(Array.from(selectedKeys.value)),
    disabled: selectedCount.value === 0,
    type: 'danger',
    plain: true
  },
  {label: t.value.list.create, type: 'primary', icon: PlusOutlined, onClick: openCreate, plain: true},
])

const openEdit = (record: AiAgent) => {
  const id = record.id
  if (id == null) return
  router.push({path: '/admin/ai-config/builder', query: {id: String(id)}})
}

const handleDeleteOne = async (id: number | string) => {
  await aiAgentApi.delete([id])
  message.success(t.value.list.deleted)
  void fetchList()
}

const handleBatchDelete = async (ids: Array<number | string>) => {
  if (ids.length === 0) return
  await aiAgentApi.delete(ids)
  message.success(t.value.list.deleted)
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
  border: 1px solid var(--border-default);
  background: var(--bg-card);
  overflow: hidden;
  box-shadow: var(--shadow-card);
}

.status-switch :deep(.status-btn.ant-btn) {
  height: 44px;
  border-radius: 0;
  border: none;
  box-shadow: none;
  color: var(--text-muted);
  background: transparent;
  font-weight: 600;
}

.status-switch :deep(.status-btn-last.ant-btn) {
  border-left: 1px solid var(--border-default);
}

.status-switch :deep(.status-btn.active.ant-btn) {
  color: var(--primary);
  background: color-mix(in srgb, var(--primary) 10%, var(--bg-card));
}

.provider-avatar-cell {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  vertical-align: middle;
}

.provider-avatar-cell :deep(svg) {
  width: 22px;
  height: 22px;
  display: block;
}

.text-secondary {
  color: var(--text-muted);
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