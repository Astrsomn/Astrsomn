<template>
  <AstrsomnPageShell description="工作流定时器任务队列：到期执行、重试与失败排查。" empty-text="暂无定时任务。"
                     title="定时任务">
    <div class="page-wrap">
      <AstrsomnDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstrsomnSearchPill v-model="query.nodeId" placeholder="搜索 nodeId" @search="onSearch"/>
              <a-input
                  v-model:value="query.instanceIdText"
                  allow-clear
                  class="toolbar-input"
                  placeholder="instanceId"
                  @pressEnter="onSearch"
              />
              <a-input
                  v-model:value="query.jobType"
                  allow-clear
                  class="toolbar-input"
                  placeholder="jobType"
                  @pressEnter="onSearch"
              />
              <a-input
                  v-model:value="query.jobStatus"
                  allow-clear
                  class="toolbar-input"
                  placeholder="jobStatus"
                  @pressEnter="onSearch"
              />
            </div>
            <div class="toolbar-right">
              <AstrsomnSegmentedButton :buttons="segmentedButtons"/>
            </div>
          </div>
        </template>

        <template #overview>
          <AstrsomnOverview
              :all-current-selected="allCurrentSelected"
              :list-length="list.length"
              :part-current-selected="partCurrentSelected"
              :selected-count="selectedRowKeys.length"
              :show-actions="list.length > 0"
              :summary-text="`当前页 ${list.length} 条定时任务，已选 ${selectedRowKeys.length} 条。`"
              @toggle-select-all="toggleSelectAllCurrentPage"
          />
        </template>

        <AstrsomnDataView
            :columns="columns"
            :data-source="list"
            :loading="loading"
            :pagination="false"
            :row-selection="rowSelection"
            :scroll="{ x: 1550 }"
            mode="table"
            row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'dueTimeMs'">
              <span class="mono">{{ record.dueTimeMs ?? '—' }}</span>
            </template>
            <template v-else-if="column.key === 'retry'">
              <span>{{ record.retryCount ?? '—' }} / {{ record.maxRetry ?? '—' }}</span>
            </template>
            <template v-else-if="column.key === 'payloadJson'">
              <span :title="String(record.payloadJson ?? '')" class="ellipsis mono">
                {{ record.payloadJson ? String(record.payloadJson) : '—' }}
              </span>
            </template>
            <template v-else-if="column.key === 'lastError'">
              <span :title="String(record.lastError ?? '')" class="ellipsis">
                {{ record.lastError ? String(record.lastError) : '—' }}
              </span>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-button size="small" type="link" @click="openDetail(record.id)">详情</a-button>
              <a-divider type="vertical"/>
              <a-popconfirm cancel-text="取消" ok-text="确认" title="确定删除该任务吗？"
                            @confirm="() => handleDeleteOne(record.id)">
                <a-button danger size="small" type="link">删除</a-button>
              </a-popconfirm>
            </template>
          </template>
        </AstrsomnDataView>

        <template #pagination>
          <AstrsomnPagination :current="page.pageNum" :page-size="page.pageSize" :total="page.total"
                              @change="onPageChange"/>
        </template>
      </AstrsomnDataSection>
    </div>

    <a-drawer v-model:open="detail.open" destroy-on-close title="定时任务详情" width="920">
      <template v-if="detail.loading">
        <a-skeleton active/>
      </template>
      <template v-else>
        <a-descriptions :column="1" bordered size="small">
          <a-descriptions-item label="ID">{{ detail.data?.id ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="实例 ID">{{ detail.data?.instanceId ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="节点 ID">{{ detail.data?.nodeId ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="任务类型">{{ detail.data?.jobType ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="到期时间(ms)"><span class="mono">{{ detail.data?.dueTimeMs ?? '—' }}</span>
          </a-descriptions-item>
          <a-descriptions-item label="任务状态">{{ detail.data?.jobStatus ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="重试次数">{{ detail.data?.retryCount ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="最大重试">{{ detail.data?.maxRetry ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="环境">{{ detail.data?.envCode ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="创建时间">{{ detail.data?.createTime ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="更新时间">{{ detail.data?.updateTime ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="Payload(JSON)">
            <pre class="json-pre">{{ safeJson(detail.data?.payloadJson) }}</pre>
          </a-descriptions-item>
          <a-descriptions-item label="最后错误">
            <pre class="json-pre error-pre">{{ detail.data?.lastError ? String(detail.data?.lastError) : '—' }}</pre>
          </a-descriptions-item>
        </a-descriptions>
      </template>
    </a-drawer>
  </AstrsomnPageShell>
</template>

<script lang="ts" setup>
import {computed, reactive, ref} from 'vue'
import {message, Modal} from 'ant-design-vue'
import {DeleteOutlined, FilterOutlined, ReloadOutlined} from '@ant-design/icons-vue'
import AstrsomnPageShell from '@/components/home/AstrsomnPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'
import AstrsomnOverview from '@/components/home/AstrsomnOverview.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnSegmentedButton, {type SegmentedButton} from '@/components/home/AstrsomnSegmentedButton.vue'
import {aiWorkflowOpsApi, type TimerJobRecord} from '@/api/aiWorkflowOps'

const columns = [
  {title: 'ID', dataIndex: 'id', key: 'id', width: 110},
  {title: '实例 ID', dataIndex: 'instanceId', key: 'instanceId', width: 130},
  {title: '节点 ID', dataIndex: 'nodeId', key: 'nodeId', width: 180, ellipsis: true},
  {title: '任务类型', dataIndex: 'jobType', key: 'jobType', width: 140, ellipsis: true},
  {title: '状态', dataIndex: 'jobStatus', key: 'jobStatus', width: 120, ellipsis: true},
  {title: '到期时间(ms)', key: 'dueTimeMs', width: 170},
  {title: '重试/上限', key: 'retry', width: 120},
  {title: 'Payload', key: 'payloadJson', width: 260, ellipsis: true},
  {title: '最后错误', key: 'lastError', width: 220, ellipsis: true},
  {title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', width: 180},
  {title: '操作', key: 'actions', width: 140, fixed: 'right' as const}
]

const query = reactive<{
  nodeId?: string
  instanceIdText?: string
  jobType?: string
  jobStatus?: string
}>({
  nodeId: undefined,
  instanceIdText: undefined,
  jobType: undefined,
  jobStatus: undefined
})

const list = ref<TimerJobRecord[]>([])
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
    const resp = await aiWorkflowOpsApi.timerJobQueryPage({
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        nodeId: query.nodeId || undefined,
        instanceId: parseInstanceId(),
        jobType: query.jobType || undefined,
        jobStatus: query.jobStatus || undefined
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
  query.nodeId = undefined
  query.instanceIdText = undefined
  query.jobType = undefined
  query.jobStatus = undefined
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

const handleDeleteOne = async (id: number | string | undefined) => {
  if (id == null) return
  const msg = await aiWorkflowOpsApi.timerJobDelete([id])
  message.success(msg || '删除成功')
  selectedRowKeys.value = selectedRowKeys.value.filter((x) => x !== id)
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await aiWorkflowOpsApi.timerJobDelete(ids)
  message.success(msg || '删除成功')
  selectedRowKeys.value = []
  void fetchList()
}

const detail = reactive<{
  open: boolean
  loading: boolean
  data: TimerJobRecord | null
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
    detail.data = await aiWorkflowOpsApi.timerJobDetail(id)
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
        title: `确定删除选中的 ${n} 条定时任务吗？`,
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

.error-pre {
  background: color-mix(in srgb, var(--error) 6%, var(--bg-surface));
  border-color: color-mix(in srgb, var(--error) 22%, var(--border-default));
}
</style>

