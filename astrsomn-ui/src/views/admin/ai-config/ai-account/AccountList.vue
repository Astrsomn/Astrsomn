<template>
  <AdminPageShell
    title="凭证管理"
    description="维护 AI_ACCOUNT：供应商账号、API 凭证与额度，供模型路由等使用。"
    empty-text="暂无账号。"
    :breadcrumbs="breadcrumbs"
    :show-view-toggle="true"
    :view-mode="viewMode"
    :view-toggle-handler="handleViewToggle"
  >
    <div ref="pageRef" class="account-page">
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
              <div class="provider-filter">
                <ModelProviderSelect
                  v-model:value="query.extensionCode"
                  placeholder="根据插件编码筛选"
                  :allow-clear="true"
                  size="middle"
                  @update:value="onProviderChange"
                />
              </div>
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
          :columns="columns"
          :row-selection="rowSelection"
          row-key="id"
          :scroll="{ x: 1180 }"
          empty-text="暂无匹配的账号"
          :card-columns="currentGridColumns"
          :card-min-width="accountCardMinWidth"
          :card-gap="accountCardGap"
        >
          <template #card="{ record }">
            <AccountCard
              :account="record"
              :selected="record.id != null && selectedKeySet.has(record.id)"
              @edit="goEdit"
              @delete="handleDeleteOne"
              @show-models="openModelsDrawer"
              @toggle="onToggleSelect"
            />
          </template>

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
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  DeleteOutlined,
  PlusOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'
import AccountForm from './AccountForm.vue'
import AccountModelsDrawer from './AccountModelsDrawer.vue'
import AccountCard from './AccountCard.vue'
import ModelProviderSelect from '../ai-model/ModelProviderSelect.vue'
import { aiAccountApi, type AiAccount, type PageResponse } from '@/api/aiAccount'
import type { AiModel } from '@/api/aiModel'

const ACCOUNT_CARD_MIN_WIDTH_PX = 340
const ACCOUNT_CARD_GAP_PX = 8
const accountCardMinWidth = `${ACCOUNT_CARD_MIN_WIDTH_PX}px`
const accountCardGap = `${ACCOUNT_CARD_GAP_PX}px`

const breadcrumbs = [
  { title: 'AI 配置', href: '/admin/ai-config' },
  { title: '凭证管理' },
]

const pageRef = ref<HTMLElement | null>(null)
const formVisible = ref(false)
const currentRecord = ref<AiAccount | undefined>(undefined)
const viewMode = ref<'grid' | 'list'>('list')
const dataViewMode = computed<'card' | 'table'>(() => (viewMode.value === 'grid' ? 'card' : 'table'))
const currentGridColumns = ref(3)

type QueryState = {
  accountKey?: string
  accountName?: string
  extensionCode?: string
}

const columns = [
  { title: '账号名称', dataIndex: 'accountName', key: 'accountName', width: 180, ellipsis: true },
  { title: '账号 Key', dataIndex: 'accountKey', key: 'accountKey', width: 180, ellipsis: true, copyable: true },
  { title: '扩展名称', dataIndex: 'extensionName', key: 'extensionName', width: 120, ellipsis: true },
  { title: '环境', dataIndex: 'envCode', key: 'envCode', width: 120, ellipsis: true },
  { title: '请求路径', dataIndex: 'apiUrl', key: 'apiUrl', width: 120, ellipsis: true },
  { title: '额度', dataIndex: 'accountTokens', key: 'accountTokens', width: 120, ellipsis: true },
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
const selectedKeySet = computed(() => new Set(selectedRowKeys.value))

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

const toolbarSegmentButtons = computed<SegmentedButton[]>(() => [
  {
    label: '重置',
    icon: ReloadOutlined,
    onClick: resetFilters
  },
  {
    label: selectedRowKeys.value.length > 0 ? `删除 (${selectedRowKeys.value.length})` : '删除',
    icon: DeleteOutlined,
    disabled: selectedRowKeys.value.length === 0,
    onClick: () => {
      if (selectedRowKeys.value.length === 0) return
      Modal.confirm({
        title: `确定批量删除选中的 ${selectedRowKeys.value.length} 个账号吗？`,
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

const resetFilters = () => {
  query.accountName = undefined
  query.extensionCode = undefined
  page.pageNum = 1
  void fetchList()
}

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
        accountName: query.accountName || undefined,
        extensionCode: query.extensionCode || undefined
      }
    }
    const resp: PageResponse<AiAccount> = await aiAccountApi.queryPage(payload)
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

const onProviderChange = () => {
  page.pageNum = 1
  void fetchList()
}

const handleViewToggle = () => {
  viewMode.value = viewMode.value === 'grid' ? 'list' : 'grid'
}

const onToggleSelect = (id: number | string, checked: boolean) => {
  if (checked) {
    if (!selectedRowKeys.value.includes(id)) {
      selectedRowKeys.value = [...selectedRowKeys.value, id]
    }
    return
  }
  selectedRowKeys.value = selectedRowKeys.value.filter((item) => item !== id)
}

const resolveGridColumns = () => {
  if (typeof window === 'undefined') return 3
  const width = pageRef.value?.clientWidth ?? window.innerWidth
  const columns = Math.floor((width + ACCOUNT_CARD_GAP_PX) / (ACCOUNT_CARD_MIN_WIDTH_PX + ACCOUNT_CARD_GAP_PX))
  return Math.max(1, Math.min(3, columns))
}

const syncGridColumns = () => {
  currentGridColumns.value = resolveGridColumns()
}

let resizeObserver: ResizeObserver | null = null

const onResize = () => {
  syncGridColumns()
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
syncGridColumns()

onMounted(() => {
  onResize()
  if (typeof ResizeObserver !== 'undefined' && pageRef.value) {
    resizeObserver = new ResizeObserver(onResize)
    resizeObserver.observe(pageRef.value)
    return
  }
  window.addEventListener('resize', onResize)
})

onBeforeUnmount(() => {
  resizeObserver?.disconnect()
  window.removeEventListener('resize', onResize)
})

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

.provider-filter {
  width: 260px;
  min-width: 220px;
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
