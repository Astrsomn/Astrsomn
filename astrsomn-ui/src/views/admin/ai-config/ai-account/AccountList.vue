<template>
  <AdminPageShell
    title="凭证管理"
    description="维护 AI_ACCOUNT：供应商账号、API 凭证与额度，供模型路由等使用。"
    empty-text="暂无账号。"
  >
    <div class="account-page">
      <AstrsomnDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstrsomnSearchPill
                v-model="query.accountName"
                layout="toolbar"
                placeholder="账号名称"
                button-label="查询"
                @search="fetchList"
              />
            </div>
            <div class="toolbar-right">
              <AstrsomnSegmentedButton :buttons="toolbarSegmentButtons" />
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
            :summary-text="`当前页 ${list.length} 条账号，已选 ${selectedRowKeys.length} 条。`"
            @toggle-select-all="toggleSelectAllCurrentPage"
          />
        </template>

        <AstrsomnDataView
          mode="table"
          :data-source="list"
          :loading="loading"
          :columns="columns"
          :row-selection="rowSelection"
          row-key="id"
          :scroll="{ x: 1180 }"
          empty-text="暂无匹配的账号"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'apiKey'">
              <span class="secret-mask">{{ maskSecret(record.apiKey) }}</span>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-button type="link" size="small" @click="goEdit(record)">编辑</a-button>
              <a-divider type="vertical" />
              <a-button type="link" size="small" @click="openModelsDrawer(record)">关联模型</a-button>
              <a-divider type="vertical" />
              <a-popconfirm title="确定删除吗？" @confirm="() => handleDeleteOne(record.id)">
                <a-button type="link" size="small" danger>删除</a-button>
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

      <AccountForm
        v-model:visible="formVisible"
        :record="currentRecord"
        @success="handleFormSuccess"
      />
    </div>
    <AccountModelsDrawer
      v-model:open="modelsDrawer.open"
      :account="modelsDrawer.account"
      :loading="modelsDrawer.loading"
      :models="modelsDrawer.models"
    />
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  DeleteOutlined,
  PlusOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'
import AstrsomnOverview from '@/components/home/AstrsomnOverview.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'
import AccountForm from './AccountForm.vue'
import AccountModelsDrawer from './AccountModelsDrawer.vue'
import { aiAccountApi, type AiAccount, type PageResponse } from '@/api/aiAccount'
import type { AiModel } from '@/api/aiModel'

const formVisible = ref(false)
const currentRecord = ref<AiAccount | undefined>(undefined)

type QueryState = {
  accountKey?: string
  accountName?: string
}

const columns = [
  { title: '账号名称', dataIndex: 'accountName', key: 'accountName', width: 180, ellipsis: true },
  { title: '账号 Key', dataIndex: 'accountKey', key: 'accountKey', width: 180, ellipsis: true },
  { title: '供应商', dataIndex: 'provider', key: 'provider', width: 120, ellipsis: true },
  { title: '环境', dataIndex: 'envCode', key: 'envCode', width: 120, ellipsis: true },
  { title: 'API Key', dataIndex: 'apiKey', key: 'apiKey', width: 240, ellipsis: true },
  { title: '操作', key: 'actions', width: 220, fixed: 'right' as const }
]

const query = reactive<QueryState>({})
const list = ref<AiAccount[]>([])
const loading = ref(false)
const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})
const selectedRowKeys = ref<Array<number | string>>([])

// maskSecret 函数已在 AccountCard 组件中实现

const currentPageIds = computed(() =>
  list.value
    .map((item) => item.id)
    .filter((id): id is number | string => id !== undefined && id !== null)
)

const allCurrentSelected = computed(() => {
  return (
    currentPageIds.value.length > 0 &&
    currentPageIds.value.every((id) => selectedRowKeys.value.includes(id))
  )
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

const resetFilters = () => {
  query.accountKey = undefined
  query.accountName = undefined
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

const toolbarSegmentButtons = computed<SegmentedButton[]>(() => [

  {
    label: '批量删除',
    icon: DeleteOutlined,
    disabled: selectedRowKeys.value.length === 0,
    onClick: () => {
      if (selectedRowKeys.value.length === 0) return
      Modal.confirm({
        title: '确定批量删除选中的账号吗？',
        okText: '确认',
        cancelText: '取消',
        onOk: () => handleBatchDelete()
      })
    }
  },

  {
    label: '新增',
    type: 'primary',
    icon: PlusOutlined,
    onClick: goCreate
  }
])

const maskSecret = (raw?: string) => {
  if (!raw) return '—'
  if (raw.length <= 10) return `${raw.slice(0, 2)}***${raw.slice(-2)}`
  return `${raw.slice(0, 4)}****${raw.slice(-4)}`
}

const fetchList = async () => {
  loading.value = true
  try {
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        accountKey: query.accountKey || undefined,
        accountName: query.accountName || undefined
      }
    }
    const resp: PageResponse<AiAccount> = await aiAccountApi.queryPage(payload)
    list.value = resp.list || []
    page.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

const onPageChange = (p: number) => {
  page.pageNum = p
  void fetchList()
}

const goCreate = () => {
  currentRecord.value = undefined
  formVisible.value = true
}

const goEdit = (record: AiAccount) => {
  currentRecord.value = record
  formVisible.value = true
}

const handleFormSuccess = () => {
  void fetchList()
}

const handleDeleteOne = async (id: number | string | undefined) => {
  if (id == null) return
  const msg = await aiAccountApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await aiAccountApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

void fetchList()

const modelsDrawer = reactive({
  open: false,
  loading: false,
  account: undefined as AiAccount | undefined,
  models: [] as AiModel[]
})

const openModelsDrawer = async (account: AiAccount) => {
  if (!account.accountKey) {
    message.error('accountKey 不能为空，无法加载关联模型')
    return
  }
  modelsDrawer.account = account
  modelsDrawer.open = true
  modelsDrawer.loading = true
  modelsDrawer.models = []
  try {
    const payload = {
      pageNo: 1,
      pageSize: 50,
      param: {
        accountKey: account.accountKey || undefined,
        envCode: account.envCode || undefined
      }
    }
    const resp: PageResponse<AiModel> = await aiAccountApi.queryModelsByAccountKey(payload)
    modelsDrawer.models = resp.list || []
  } catch {
    message.error('加载关联模型失败')
  } finally {
    modelsDrawer.loading = false
  }
}
</script>

<style scoped>
.account-page {
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
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  flex: 1;
}

.toolbar-right {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.secret-mask {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 12px;
  color: var(--text-secondary);
}

@media (max-width: 720px) {
  .toolbar-left,
  .toolbar-right {
    width: 100%;
  }
}

/* drawer styles moved to AccountModelsDrawer.vue */
</style>
