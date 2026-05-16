<template>
  <AstPageShell description="工作流消息投递出站箱：用于重试、失败定位与幂等对账。" empty-text="暂无 Outbox 消息。"
                     title="消息 Outbox">
    <div class="page-wrap">
      <AstDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstSearchInput v-model="query.idempotentKey" placeholder="搜索幂等键 idempotentKey"
                                  @search="onSearch"/>
              <a-input
                  v-model:value="query.bizType"
                  allow-clear
                  class="toolbar-input"
                  placeholder="bizType"
                  @pressEnter="onSearch"
              />
              <a-input
                  v-model:value="query.bizId"
                  allow-clear
                  class="toolbar-input"
                  placeholder="bizId"
                  @pressEnter="onSearch"
              />
              <a-input
                  v-model:value="query.msgStatus"
                  allow-clear
                  class="toolbar-input"
                  placeholder="msgStatus"
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
              :summary-text="`当前页 ${list.length} 条 Outbox 消息，已选 ${selectedRowKeys.length} 条。`"
              @toggle-select-all="toggleSelectAllCurrentPage"
          />
        </template>

        <AstDataView
            :columns="columns"
            :data-source="list"
            :loading="loading"
            :pagination="false"
            :row-selection="rowSelection"
            :scroll="{ x: 1600 }"
            mode="table"
            row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'payloadJson'">
              <span :title="String(record.payloadJson ?? '')" class="ellipsis mono">
                {{ record.payloadJson ? String(record.payloadJson) : '—' }}
              </span>
            </template>
            <template v-else-if="column.key === 'lastError'">
              <span :title="String(record.lastError ?? '')" class="ellipsis">
                {{ record.lastError ? String(record.lastError) : '—' }}
              </span>
            </template>
            <template v-else-if="column.key === 'nextRetryTimeMs'">
              <span class="mono">{{ record.nextRetryTimeMs ?? '—' }}</span>
            </template>
            <template v-else-if="column.key === 'idempotentKey'">
              <span class="mono">{{ record.idempotentKey ?? '—' }}</span>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-button size="small" type="link" @click="openDetail(record.id)">详情</a-button>
              <a-divider type="vertical"/>
              <a-popconfirm cancel-text="取消" ok-text="确认" title="确定删除该消息吗？"
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

    <a-drawer v-model:open="detail.open" destroy-on-close title="Outbox 详情" width="920">
      <template v-if="detail.loading">
        <a-skeleton active/>
      </template>
      <template v-else>
        <a-descriptions :column="1" bordered size="small">
          <a-descriptions-item label="ID">{{ detail.data?.id ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="业务类型">{{ detail.data?.bizType ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="业务 ID">{{ detail.data?.bizId ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="Topic/Endpoint">{{ detail.data?.topicOrEndpoint ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="消息状态">{{ detail.data?.msgStatus ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="重试次数">{{ detail.data?.retryCount ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="下次重试(ms)"><span class="mono">{{ detail.data?.nextRetryTimeMs ?? '—' }}</span>
          </a-descriptions-item>
          <a-descriptions-item label="幂等键"><span class="mono">{{ detail.data?.idempotentKey ?? '—' }}</span>
          </a-descriptions-item>
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
import {aiWorkflowOpsApi, type MsgOutboxQuery, type MsgOutboxRecord} from '@/api/aiWorkflowOps'

const columns = [
  {title: 'ID', dataIndex: 'id', key: 'id', width: 110},
  {title: '业务类型', dataIndex: 'bizType', key: 'bizType', width: 140, ellipsis: true},
  {title: '业务 ID', dataIndex: 'bizId', key: 'bizId', width: 160, ellipsis: true},
  {title: 'Topic/Endpoint', dataIndex: 'topicOrEndpoint', key: 'topicOrEndpoint', width: 220, ellipsis: true},
  {title: '状态', dataIndex: 'msgStatus', key: 'msgStatus', width: 120, ellipsis: true},
  {title: '重试', dataIndex: 'retryCount', key: 'retryCount', width: 90},
  {title: '下次重试(ms)', key: 'nextRetryTimeMs', width: 160},
  {title: '幂等键', key: 'idempotentKey', width: 200, ellipsis: true},
  {title: 'Payload', key: 'payloadJson', width: 260, ellipsis: true},
  {title: '最后错误', key: 'lastError', width: 220, ellipsis: true},
  {title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', width: 180},
  {title: '操作', key: 'actions', width: 140, fixed: 'right' as const}
]

const query = reactive<MsgOutboxQuery>({
  bizType: undefined,
  bizId: undefined,
  msgStatus: undefined,
  idempotentKey: undefined
})

const list = ref<MsgOutboxRecord[]>([])
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
    const resp = await aiWorkflowOpsApi.msgOutboxQueryPage({
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        bizType: query.bizType || undefined,
        bizId: query.bizId || undefined,
        msgStatus: query.msgStatus || undefined,
        idempotentKey: query.idempotentKey || undefined
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
  query.bizType = undefined
  query.bizId = undefined
  query.msgStatus = undefined
  query.idempotentKey = undefined
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

const handleDeleteOne = async (id: number | string | undefined) => {
  if (id == null) return
  const msg = await aiWorkflowOpsApi.msgOutboxDelete([id])
  message.success(msg || '删除成功')
  selectedRowKeys.value = selectedRowKeys.value.filter((x) => x !== id)
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await aiWorkflowOpsApi.msgOutboxDelete(ids)
  message.success(msg || '删除成功')
  selectedRowKeys.value = []
  void fetchList()
}

const detail = reactive<{
  open: boolean
  loading: boolean
  data: MsgOutboxRecord | null
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
    detail.data = await aiWorkflowOpsApi.msgOutboxDetail(id)
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
        title: `确定删除选中的 ${n} 条 Outbox 消息吗？`,
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

