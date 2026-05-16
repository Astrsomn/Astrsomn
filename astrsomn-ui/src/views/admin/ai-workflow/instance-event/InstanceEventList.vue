<template>
  <AstPageShell description="记录流程实例在节点上的关键事件与追踪信息。" empty-text="暂无实例事件。"
                     title="实例事件">
    <div class="page-wrap">
      <AstDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstSearchInput v-model="query.traceId" placeholder="搜索 traceId" @search="onSearch"/>
              <a-input
                  v-model:value="query.instanceIdText"
                  allow-clear
                  class="toolbar-input"
                  placeholder="instanceId"
                  @pressEnter="onSearch"
              />
              <a-input
                  v-model:value="query.eventType"
                  allow-clear
                  class="toolbar-input"
                  placeholder="eventType"
                  @pressEnter="onSearch"
              />
              <a-input
                  v-model:value="query.nodeId"
                  allow-clear
                  class="toolbar-input"
                  placeholder="nodeId"
                  @pressEnter="onSearch"
              />
            </div>
            <div class="toolbar-right">
              <AstegmentedButton :buttons="segmentedButtons"/>
            </div>
          </div>
        </template>

        <template #overview>
          <AstOverview
              :all-current-selected="allCurrentSelected"
              :list-length="list.length"
              :part-current-selected="partCurrentSelected"
              :selected-count="selectedRowKeys.length"
              :show-actions="list.length > 0"
              :summary-text="`当前页 ${list.length} 条事件记录，已选 ${selectedRowKeys.length} 条。`"
              @toggle-select-all="toggleSelectAllCurrentPage"
          />
        </template>

        <AstDataView
            :columns="columns"
            :data-source="list"
            :loading="loading"
            :pagination="false"
            :row-selection="rowSelection"
            :scroll="{ x: 1400 }"
            mode="table"
            row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'eventDataJson'">
              <span :title="String(record.eventDataJson ?? '')" class="ellipsis mono">
                {{ record.eventDataJson ? String(record.eventDataJson) : '—' }}
              </span>
            </template>
            <template v-else-if="column.key === 'eventTimeMs'">
              <span class="mono">{{ record.eventTimeMs ?? '—' }}</span>
            </template>
            <template v-else-if="column.key === 'traceId'">
              <span class="mono">{{ record.traceId ?? '—' }}</span>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-button size="small" type="link" @click="openDetail(record.id)">详情</a-button>
              <a-divider type="vertical"/>
              <a-popconfirm cancel-text="取消" ok-text="确认" title="确定删除该事件吗？"
                            @confirm="() => handleDeleteOne(record.id)">
                <a-button danger size="small" type="link">删除</a-button>
              </a-popconfirm>
            </template>
          </template>
        </AstDataView>

        <template #pagination>
          <AstPagination :current="page.pageNum" :page-size="page.pageSize" :total="page.total"
                              @change="onPageChange"/>
        </template>
      </AstDataSection>
    </div>

    <a-drawer v-model:open="detail.open" destroy-on-close title="实例事件详情" width="860">
      <template v-if="detail.loading">
        <a-skeleton active/>
      </template>
      <template v-else>
        <a-descriptions :column="1" bordered size="small">
          <a-descriptions-item label="ID">{{ detail.data?.id ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="实例 ID">{{ detail.data?.instanceId ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="事件类型">{{ detail.data?.eventType ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="节点 ID">{{ detail.data?.nodeId ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="事件时间(ms)"><span class="mono">{{ detail.data?.eventTimeMs ?? '—' }}</span>
          </a-descriptions-item>
          <a-descriptions-item label="traceId"><span class="mono">{{ detail.data?.traceId ?? '—' }}</span>
          </a-descriptions-item>
          <a-descriptions-item label="环境">{{ detail.data?.envCode ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="创建时间">{{ detail.data?.createTime ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="更新时间">{{ detail.data?.updateTime ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="事件数据(JSON)">
            <pre class="json-pre">{{ safeJson(detail.data?.eventDataJson) }}</pre>
          </a-descriptions-item>
        </a-descriptions>
      </template>
    </a-drawer>
  </AstPageShell>
</template>

<script lang="ts" setup>
import {computed, reactive, ref} from 'vue'
import {message, Modal} from 'ant-design-vue'
import {DeleteOutlined, FilterOutlined, ReloadOutlined} from '@ant-design/icons-vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import AstDataSection from '@/components/home/AstDataSection.vue'
import AstDataView from '@/components/home/AstDataView.vue'
import AstOverview from '@/components/home/AstOverview.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstegmentedButton, {type SegmentedButton} from '@/components/home/AstegmentedButton.vue'
import {aiWorkflowOpsApi, type InstanceEventRecord} from '@/api/aiWorkflowOps'

const columns = [
  {title: 'ID', dataIndex: 'id', key: 'id', width: 110},
  {title: '实例 ID', dataIndex: 'instanceId', key: 'instanceId', width: 130},
  {title: '事件类型', dataIndex: 'eventType', key: 'eventType', width: 160, ellipsis: true},
  {title: '节点 ID', dataIndex: 'nodeId', key: 'nodeId', width: 160, ellipsis: true},
  {title: '事件时间(ms)', key: 'eventTimeMs', width: 160},
  {title: 'traceId', key: 'traceId', width: 220, ellipsis: true},
  {title: '事件数据', key: 'eventDataJson', width: 260, ellipsis: true},
  {title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', width: 180},
  {title: '操作', key: 'actions', width: 140, fixed: 'right' as const}
]

const query = reactive<{
  traceId?: string
  instanceIdText?: string
  eventType?: string
  nodeId?: string
}>({
  traceId: undefined,
  instanceIdText: undefined,
  eventType: undefined,
  nodeId: undefined
})

const list = ref<InstanceEventRecord[]>([])
const loading = ref(false)

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const selectedRowKeys = ref<Array<number | string>>([])

const currentPageIds = computed(() =>
    list.value
        .map((item) => item.id)
        .filter((id): id is number | string => id !== undefined && id !== null)
)

const allCurrentSelected = computed(() => {
  return currentPageIds.value.length > 0 && currentPageIds.value.every((id) => selectedRowKeys.value.includes(id))
})

const partCurrentSelected = computed(() => {
  if (currentPageIds.value.length === 0) return false
  const count = currentPageIds.value.filter((id) => selectedRowKeys.value.includes(id)).length
  return count > 0 && count < currentPageIds.value.length
})

const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys: Array<number | string>) => {
    selectedRowKeys.value = keys
  }
}))

const toggleSelectAllCurrentPage = (checked: boolean) => {
  if (checked) {
    selectedRowKeys.value = Array.from(new Set([...selectedRowKeys.value, ...currentPageIds.value]))
    return
  }
  selectedRowKeys.value = selectedRowKeys.value.filter((id) => !currentPageIds.value.includes(id))
}

const parseInstanceId = () => {
  const text = String(query.instanceIdText || '').trim()
  if (!text) return undefined
  const n = Number(text)
  return Number.isFinite(n) ? n : text
}

const fetchList = async () => {
  loading.value = true
  try {
    const resp = await aiWorkflowOpsApi.instanceEventQueryPage({
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        traceId: query.traceId || undefined,
        instanceId: parseInstanceId(),
        eventType: query.eventType || undefined,
        nodeId: query.nodeId || undefined
      }
    })
    list.value = resp.list || []
    page.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

const onSearch = () => {
  page.pageNum = 1
  void fetchList()
}

const onPageChange = (pageNum: number, pageSize: number) => {
  page.pageNum = pageNum
  page.pageSize = pageSize
  void fetchList()
}

const resetFilters = () => {
  query.traceId = undefined
  query.instanceIdText = undefined
  query.eventType = undefined
  query.nodeId = undefined
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

const handleDeleteOne = async (id: number | string | undefined) => {
  if (id == null) return
  const msg = await aiWorkflowOpsApi.instanceEventDelete([id])
  message.success(msg || '删除成功')
  selectedRowKeys.value = selectedRowKeys.value.filter((x) => x !== id)
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await aiWorkflowOpsApi.instanceEventDelete(ids)
  message.success(msg || '删除成功')
  selectedRowKeys.value = []
  void fetchList()
}

const detail = reactive<{
  open: boolean
  loading: boolean
  data: InstanceEventRecord | null
}>({
  open: false,
  loading: false,
  data: null
})

const openDetail = async (id: number | string | undefined) => {
  if (id == null) return
  detail.open = true
  detail.loading = true
  detail.data = null
  try {
    detail.data = await aiWorkflowOpsApi.instanceEventDetail(id)
  } finally {
    detail.loading = false
  }
}

const safeJson = (value: unknown) => {
  const text = String(value ?? '').trim()
  if (!text) return '—'
  try {
    return JSON.stringify(JSON.parse(text), null, 2)
  } catch {
    return text
  }
}

const segmentedButtons = computed<SegmentedButton[]>(() => [
  {
    label: '批量删除',
    type: 'danger',
    plain: true,
    icon: DeleteOutlined,
    disabled: selectedRowKeys.value.length === 0,
    onClick: () => {
      const n = selectedRowKeys.value.length
      if (n === 0) return
      Modal.confirm({
        title: `确定删除选中的 ${n} 条事件记录吗？`,
        onOk: () => handleBatchDelete()
      })
    }
  },
  {
    label: '重置',
    type: 'primary',
    plain: true,
    icon: ReloadOutlined,
    onClick: resetFilters
  },
  {
    label: '查询',
    icon: FilterOutlined,
    type: 'primary',
    onClick: onSearch
  }
])

void fetchList()
</script>

<style scoped>
.page-wrap {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
}

.toolbar-input {
  width: 190px;
}

.mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
}

.ellipsis {
  display: inline-block;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.json-pre {
  margin: 0;
  padding: 10px 12px;
  border-radius: 10px;
  background: color-mix(in srgb, var(--bg-surface) 92%, white);
  border: 1px solid var(--border-default);
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 12px;
  line-height: 1.45;
}
</style>

