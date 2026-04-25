<template>
  <AdminPageShell
    title="系统消息"
    description="管理 SYS_MESSAGE，支持创建通知、更新已读状态、查看来源与错误码。"
    empty-text="暂无系统消息。"
  >
    <div class="message-page">
      <AstrsomnDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstrsomnSearchPill
                v-model="query.title"
                placeholder="按标题搜索"
                layout="toolbar"
                @search="fetchList"
              />

              <a-select
                v-model:value="query.messageType"
                class="toolbar-select"
                placeholder="消息类型"
                allow-clear
                :options="messageTypeOptions"
                @change="onFilterChanged"
              />

              <a-select
                v-model:value="query.messageLevel"
                class="toolbar-select"
                placeholder="消息级别"
                allow-clear
                :options="messageLevelOptions"
                @change="onFilterChanged"
              />

              <a-select
                v-model:value="query.readStatus"
                class="toolbar-select"
                placeholder="阅读状态"
                allow-clear
                :options="readStatusOptions"
                @change="onFilterChanged"
              />
            </div>

            <div class="toolbar-right">
              <AstrsomnSegmentedButton :buttons="actionButtons" />
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
            :summary-text="`当前页 ${list.length} 条消息，已选 ${selectedRowKeys.length} 条。`"
            @toggle-select-all="toggleSelectAllCurrentPage"
          />
        </template>

        <AstrsomnDataView
          mode="table"
          :data-source="list"
          :loading="loading"
          :columns="columns"
          :row-selection="rowSelection"
          :scroll="{ x: 1320 }"
          row-key="id"
          empty-text="暂无匹配的系统消息"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'messageType'">
              <a-tag>{{ messageTypeLabel(record.messageType) }}</a-tag>
            </template>
            <template v-else-if="column.key === 'messageLevel'">
              <a-tag :color="levelColor(record.messageLevel)">{{ messageLevelLabel(record.messageLevel) }}</a-tag>
            </template>
            <template v-else-if="column.key === 'readStatus'">
              <a-tag :color="record.readStatus === 'READ' ? 'green' : 'orange'">
                {{ record.readStatus === 'READ' ? '已读' : '未读' }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'title'">
              <span class="title-cell">{{ record.title || '—' }}</span>
            </template>
            <template v-else-if="column.key === 'source'">
              <span>{{ record.source || 'SYSTEM' }}</span>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-button type="link" @click="goEdit(record)">编辑</a-button>
              <a-divider type="vertical" />
              <a-popconfirm title="确定删除该消息吗？" @confirm="() => handleDeleteOne(record.id)">
                <a-button type="link" danger>删除</a-button>
              </a-popconfirm>
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
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import { DeleteOutlined, PlusOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'
import AstrsomnOverview from '@/components/home/AstrsomnOverview.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'
import { systemMessageApi, type PageResponse, type SystemMessage } from '@/api/systemMessage'

const router = useRouter()

type QueryState = {
  title?: string
  messageType?: string
  messageLevel?: string
  readStatus?: string
}

const messageTypeOptions = [
  { label: '插件已安装', value: 'PLUGIN_INSTALLED' },
  { label: '插件安装失败', value: 'PLUGIN_INSTALL_FAILED' },
  { label: '插件已卸载', value: 'PLUGIN_UNINSTALLED' },
  { label: '上线通知', value: 'DEPLOYMENT_ONLINE' },
  { label: '调用失败', value: 'API_CALL_FAILED' },
  { label: '系统通知', value: 'SYSTEM_NOTICE' },
  { label: '其他', value: 'OTHER' }
]

const messageLevelOptions = [
  { label: '信息', value: 'INFO' },
  { label: '成功', value: 'SUCCESS' },
  { label: '警告', value: 'WARN' },
  { label: '错误', value: 'ERROR' }
]

const readStatusOptions = [
  { label: '未读', value: 'UNREAD' },
  { label: '已读', value: 'READ' }
]

const messageTypeLabel = (v?: string) => messageTypeOptions.find((x) => x.value === v)?.label || (v || '—')
const messageLevelLabel = (v?: string) => messageLevelOptions.find((x) => x.value === v)?.label || (v || '—')

const levelColor = (v?: string) => {
  if (v === 'SUCCESS') return 'green'
  if (v === 'WARN') return 'orange'
  if (v === 'ERROR') return 'red'
  return 'blue'
}

const columns = [
  { title: '标题', key: 'title', width: 260, ellipsis: true },
  { title: '类型', key: 'messageType', width: 180 },
  { title: '级别', key: 'messageLevel', width: 100 },
  { title: '状态', key: 'readStatus', width: 100 },
  { title: '来源', key: 'source', width: 140, ellipsis: true },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180, ellipsis: true },
  { title: '操作', key: 'actions', width: 160, fixed: 'right' as const }
]

const query = reactive<QueryState>({})
const list = ref<SystemMessage[]>([])
const loading = ref(false)
const page = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const selectedRowKeys = ref<Array<number | string>>([])

const actionButtons = computed<SegmentedButton[]>(() => [
  {
    label: '批量删除',
    type: 'danger',
    icon: DeleteOutlined,
    disabled: selectedRowKeys.value.length === 0,
    plain: true,
    onClick: handleBatchDelete
  },
  {
    label: '重置',
    type: 'primary',
    icon: ReloadOutlined,
    plain: true,
    onClick: resetFilters
  },
  {
    label: '新增',
    type: 'primary',
    icon: PlusOutlined,
    onClick: goCreate
  }
])

const currentPageIds = computed(() => list.value.map((item) => item.id).filter((id): id is number | string => !!id))
const allCurrentSelected = computed(() => currentPageIds.value.length > 0 && currentPageIds.value.every((id) => selectedRowKeys.value.includes(id)))
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

const onFilterChanged = () => {
  page.pageNum = 1
  void fetchList()
}

const resetFilters = () => {
  query.title = undefined
  query.messageType = undefined
  query.messageLevel = undefined
  query.readStatus = undefined
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

const fetchList = async () => {
  loading.value = true
  try {
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        title: query.title || undefined,
        messageType: query.messageType || undefined,
        messageLevel: query.messageLevel || undefined,
        readStatus: query.readStatus || undefined
      }
    }
    const resp: PageResponse<SystemMessage> = await systemMessageApi.queryPage(payload)
    list.value = resp.list || []
    page.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

const onPageChange = (p: number, size: number) => {
  page.pageNum = p
  page.pageSize = size
  void fetchList()
}

const goCreate = () => {
  void router.push({ name: 'AdminSystemMessageNew' })
}

const goEdit = (row: SystemMessage) => {
  if (row.id == null) return
  void router.push({ name: 'AdminSystemMessageEdit', params: { id: String(row.id) } })
}

const handleDeleteOne = async (id: number | string | undefined) => {
  if (id == null) return
  const msg = await systemMessageApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  Modal.confirm({
    title: '确定批量删除选中的系统消息吗？',
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      const msg = await systemMessageApi.delete(ids)
      message.success(msg)
      selectedRowKeys.value = []
      void fetchList()
    }
  })
}

void fetchList()
</script>

<style scoped>
.message-page {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  flex-wrap: wrap;
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

.toolbar-select {
  width: 160px;
}

.title-cell {
  font-weight: 500;
}

@media (max-width: 720px) {
  .toolbar-left,
  .toolbar-right,
  .toolbar-select {
    width: 100%;
  }
}
</style>
