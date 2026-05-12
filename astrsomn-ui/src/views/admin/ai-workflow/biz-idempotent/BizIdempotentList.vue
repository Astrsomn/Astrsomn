<template>
  <AstrsomnPageShell title="业务幂等" description="用于防重复提交：按幂等键、业务类型与业务 ID 查询与清理。" empty-text="暂无幂等记录。">
    <div class="page-wrap">
      <AstrsomnDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstrsomnSearchPill v-model="query.idempotentKey" placeholder="搜索幂等键 idempotentKey" @search="onSearch" />
              <a-input
                v-model:value="query.bizType"
                class="toolbar-input"
                allow-clear
                placeholder="bizType"
                @pressEnter="onSearch"
              />
              <a-input
                v-model:value="query.bizId"
                class="toolbar-input"
                allow-clear
                placeholder="bizId"
                @pressEnter="onSearch"
              />
            </div>
            <div class="toolbar-right">
              <AstrsomnSegmentedButton :buttons="segmentedButtons" />
            </div>
          </div>
        </template>

        <template #overview>
          <AstrsomnOverview
            :list-length="list.length"
            :selected-count="selectedRowKeys.length"
            :all-current-selected="allCurrentSelected"
            :part-current-selected="partCurrentSelected"
            :show-actions="list.length > 0"
            :summary-text="`当前页 ${list.length} 条幂等记录，已选 ${selectedRowKeys.length} 条。`"
            @toggle-select-all="toggleSelectAllCurrentPage"
          />
        </template>

        <AstrsomnDataView
          :data-source="list"
          :columns="columns"
          row-key="id"
          mode="table"
          :pagination="false"
          :loading="loading"
          :row-selection="rowSelection"
          :scroll="{ x: 1280 }"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'idempotentKey'">
              <span class="mono">{{ record.idempotentKey || '—' }}</span>
            </template>
            <template v-else-if="column.key === 'expireAtMs'">
              <span class="mono">{{ record.expireAtMs ?? '—' }}</span>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-button type="link" size="small" @click="openDetail(record.id)">详情</a-button>
              <a-divider type="vertical" />
              <a-popconfirm title="确定删除该记录吗？" ok-text="确认" cancel-text="取消" @confirm="() => handleDeleteOne(record.id)">
                <a-button type="link" size="small" danger>删除</a-button>
              </a-popconfirm>
            </template>
          </template>
        </AstrsomnDataView>

        <template #pagination>
          <AstrsomnPagination :current="page.pageNum" :page-size="page.pageSize" :total="page.total" @change="onPageChange" />
        </template>
      </AstrsomnDataSection>
    </div>

    <a-drawer v-model:open="detail.open" title="幂等记录详情" width="720" destroy-on-close>
      <template v-if="detail.loading">
        <a-skeleton active />
      </template>
      <template v-else>
        <a-descriptions bordered size="small" :column="1">
          <a-descriptions-item label="ID">{{ detail.data?.id ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="幂等键"><span class="mono">{{ detail.data?.idempotentKey ?? '—' }}</span></a-descriptions-item>
          <a-descriptions-item label="业务类型">{{ detail.data?.bizType ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="业务 ID">{{ detail.data?.bizId ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="请求 Hash"><span class="mono">{{ detail.data?.requestHash ?? '—' }}</span></a-descriptions-item>
          <a-descriptions-item label="结果引用">{{ detail.data?.resultRef ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="过期时间(ms)"><span class="mono">{{ detail.data?.expireAtMs ?? '—' }}</span></a-descriptions-item>
          <a-descriptions-item label="环境">{{ detail.data?.envCode ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="创建时间">{{ detail.data?.createTime ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="更新时间">{{ detail.data?.updateTime ?? '—' }}</a-descriptions-item>
        </a-descriptions>
      </template>
    </a-drawer>
  </AstrsomnPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { DeleteOutlined, FilterOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import AstrsomnPageShell from '@/components/home/AstrsomnPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'
import AstrsomnOverview from '@/components/home/AstrsomnOverview.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'
import { aiWorkflowOpsApi, type BizIdempotentQuery, type BizIdempotentRecord } from '@/api/aiWorkflowOps'

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 120 },
  { title: '幂等键', key: 'idempotentKey', width: 260, ellipsis: true },
  { title: '业务类型', dataIndex: 'bizType', key: 'bizType', width: 160, ellipsis: true },
  { title: '业务 ID', dataIndex: 'bizId', key: 'bizId', width: 180, ellipsis: true },
  { title: '过期时间(ms)', key: 'expireAtMs', width: 160 },
  { title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', width: 180 },
  { title: '操作', key: 'actions', width: 140, fixed: 'right' as const }
]

const query = reactive<BizIdempotentQuery>({
  idempotentKey: undefined,
  bizType: undefined,
  bizId: undefined
})

const list = ref<BizIdempotentRecord[]>([])
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

const fetchList = async () => {
  loading.value = true
  try {
    const resp = await aiWorkflowOpsApi.bizIdempotentQueryPage({
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        idempotentKey: query.idempotentKey || undefined,
        bizType: query.bizType || undefined,
        bizId: query.bizId || undefined
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
  query.idempotentKey = undefined
  query.bizType = undefined
  query.bizId = undefined
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

const handleDeleteOne = async (id: number | string | undefined) => {
  if (id == null) return
  const msg = await aiWorkflowOpsApi.bizIdempotentDelete([id])
  message.success(msg || '删除成功')
  selectedRowKeys.value = selectedRowKeys.value.filter((x) => x !== id)
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await aiWorkflowOpsApi.bizIdempotentDelete(ids)
  message.success(msg || '删除成功')
  selectedRowKeys.value = []
  void fetchList()
}

const detail = reactive<{
  open: boolean
  loading: boolean
  data: BizIdempotentRecord | null
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
    detail.data = await aiWorkflowOpsApi.bizIdempotentDetail(id)
  } finally {
    detail.loading = false
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
        title: `确定删除选中的 ${n} 条幂等记录吗？`,
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
  width: 200px;
}

.mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
}
</style>

