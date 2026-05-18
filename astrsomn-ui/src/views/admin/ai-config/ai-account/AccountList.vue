<template>
  <AstPageShell
      :breadcrumbs="breadcrumbs"
      :show-view-toggle="true"
      :view-mode="viewMode"
      :view-toggle-handler="handleViewToggle"
      description="维护 AI_ACCOUNT：供应商账号、API 凭证与额度，供模型路由等使用。"
      empty-text="暂无账号。"
      title="凭证管理"
  >
    <div ref="pageRef" class="account-page">
      <AstDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstSearchInput
                  v-model="query.accountName"
                  button-label="查询"
                  layout="toolbar"
                  placeholder="账号名称"
                  @search="fetchList"
              />
              <div class="provider-filter">
                <ExtensionSelector
                    v-model:value="query.extensionCode"
                    :allow-clear="true"
                    :only-applied="true"
                    placeholder="根据供应商筛选"
                    size="middle"
                    @update:value="onProviderChange"
                />
              </div>
            </div>
            <div class="toolbar-right">
              <AstegmentedButton :buttons="toolbarSegmentButtons"/>
            </div>
          </div>
        </template>


        <AstDataView
            :card-columns="currentGridColumns"
            :card-gap="accountCardGap"
            :card-min-width="accountCardMinWidth"
            :columns="columns"
            :data-source="list"
            :loading="loading"
            :mode="dataViewMode"
            :row-selection="rowSelection"
            :scroll="{ x: 1180 }"
            empty-text="暂无匹配的账号"
            row-key="id"
        >
          <template #card="{ record }">
            <AccountCard
                :account="record"
                :selected="record.id != null && selectedKeySet.has(record.id)"
                @delete="handleDeleteOne"
                @edit="goEdit"
                @toggle="onToggleSelect"
                @show-models="openModelsDrawer"
            />
          </template>

          <template #bodyCell="{ column, record }">

            <template v-if="column.key === 'callCount'">
              <span class="metric-chip metric-chip--call">
                <span class="metric-value">{{ Number(record.callCount ?? 0).toLocaleString() }}</span>
                <span class="metric-unit">次</span>
              </span>
            </template>

            <template v-else-if="column.key === 'totalTokens'">
              <span class="metric-chip metric-chip--token">
                <span class="metric-value">{{ Number(record.totalTokens ?? 0).toLocaleString() }}</span>
                <span class="metric-unit">tokens</span>
              </span>
            </template>

            <template v-else-if="column.key === 'accountTokens'">
              <span class="metric-chip metric-chip--quota">
                <span class="metric-value">{{ Number(record.accountTokens ?? 0).toLocaleString() }}</span>
                <span class="metric-unit">tokens</span>
              </span>
            </template>

            <template v-else-if="column.key === 'remainingTokens'">
              <span class="metric-chip metric-chip--remain">
                <span class="metric-value">{{
                    Number((record.accountTokens ?? 0) - (record.totalTokens ?? 0)).toLocaleString()
                  }}</span>
                <span class="metric-unit">tokens</span>
              </span>
            </template>

            <template v-else-if="column.key === 'apiKey'">
              <span class="secret-mask">{{ maskSecret(record.apiKey) }}</span>
            </template>

            <template v-else-if="column.key === 'actions'">
              <a-space>
                <a-button size="small" type="link" @click="goEdit(record)">
                  <EditOutlined/>
                </a-button>
                <a-button size="small" type="link" @click="openModelsDrawer(record)">
                  <LinkOutlined/>
                </a-button>
                <a-popconfirm title="确定删除吗？" @confirm="() => handleDeleteOne(record.id)">
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
  </AstPageShell>
</template>

<script lang="ts" setup>
import {computed, onBeforeUnmount, onMounted, reactive, ref} from 'vue'
import {message, Modal} from 'ant-design-vue'
import {DeleteOutlined, EditOutlined, LinkOutlined, PlusOutlined, ReloadOutlined} from '@ant-design/icons-vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import AstDataSection from '@/components/home/AstDataSection.vue'
import AstDataView from '@/components/home/AstDataView.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstegmentedButton, {type SegmentedButton} from '@/components/home/AstegmentedButton.vue'
import AccountForm from './component/AccountForm.vue'
import AccountModelsDrawer from './component/AccountModelsDrawer.vue'
import AccountCard from './component/AccountCard.vue'
import ExtensionSelector from '@/views/admin/system-config/system-extension/selector/ExtensionSelector.vue'
import {type AiAccount, aiAccountApi, type PageResponse} from '@/api/aiAccount'
import type {AiModel} from '@/api/aiModel'

const props = withDefaults(defineProps<{
  initialViewMode?: 'grid' | 'list'
}>(), {
  initialViewMode: 'list'
})

const ACCOUNT_CARD_MIN_WIDTH_PX = 340
const ACCOUNT_CARD_GAP_PX = 8
const accountCardMinWidth = `${ACCOUNT_CARD_MIN_WIDTH_PX}px`
const accountCardGap = `${ACCOUNT_CARD_GAP_PX}px`

const breadcrumbs = [
  {title: 'AI 配置', href: '/admin/ai-config'},
  {title: '凭证管理'},
]

const pageRef = ref<HTMLElement | null>(null)
const formVisible = ref(false)
const currentRecord = ref<AiAccount | undefined>(undefined)
const viewMode = ref<'grid' | 'list'>(props.initialViewMode)
const dataViewMode = computed<'card' | 'table'>(() => (viewMode.value === 'grid' ? 'card' : 'table'))
const currentGridColumns = ref(3)

type QueryState = {
  accountKey?: string
  accountName?: string
  extensionCode?: string
}

const columns = [
  {
    title: '供应商',
    key: 'providerAvatar',
    dataIndex: 'providerAvatar',
    width: 80,
    align: 'center',
    enableBase64Render: true
  },
  {title: '账号名称', dataIndex: 'accountName', key: 'accountName', width: 180, ellipsis: true},
  {title: '账号 Key', dataIndex: 'accountKey', key: 'accountKey', width: 180, ellipsis: true, copyable: true},
  {title: '调用次数', dataIndex: 'callCount', key: 'callCount', width: 140, align: 'center'},
  {title: '总消耗', dataIndex: 'totalTokens', key: 'totalTokens', width: 170, align: 'center'},
  {title: '总限量', dataIndex: 'accountTokens', key: 'accountTokens', width: 170, align: 'center'},
  {title: '剩余额度', key: 'remainingTokens', width: 170, align: 'center'},

  {title: '扩展名称', dataIndex: 'extensionName', key: 'extensionName', width: 120, ellipsis: true},
  {title: '请求路径', dataIndex: 'apiUrl', key: 'apiUrl', width: 120, ellipsis: true},
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 120,
    ellipsis: true,
    tag: true,
    tagColor: (status: string) => status === 'enabled' ? 'green' : 'red'
  },
  {title: '环境', dataIndex: 'envCode', key: 'envCode', width: 120, ellipsis: true, tag: true, tagColor: 'blue'},
  {title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 150, dateFormat: true},
  {title: '创建人', dataIndex: 'createUser', key: 'createUser', width: 150},
  {title: '操作', key: 'actions', width: 220, fixed: 'right' as const}
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

const providerAvatarCell = (record: AiAccount) => {
  const raw = record?.providerAvatar
  return typeof raw === 'string' && raw.trim() ? raw.trim() : ''
}

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

.metric-chip {
  display: inline-flex;
  align-items: baseline;
  gap: 6px;
  padding: 3px 10px;
  border-radius: 999px;
  border: 1px solid transparent;
}

.metric-chip--call {
  color: #166534;
  background: #f0fdf4;
  border-color: #bbf7d0;
}

.metric-chip--token {
  color: #0f3a8c;
  background: #eff6ff;
  border-color: #bfdbfe;
}

.metric-chip--quota {
  color: #7c2d12;
  background: #fff7ed;
  border-color: #fed7aa;
}

.metric-chip--remain {
  color: #4c1d95;
  background: #f5f3ff;
  border-color: #ddd6fe;
}

.metric-value {
  font-weight: 600;
  font-variant-numeric: tabular-nums;
}

.metric-unit {
  font-size: 12px;
  opacity: 0.85;
}

@media (max-width: 720px) {
  .toolbar-left,
  .toolbar-right {
    width: 100%;
  }
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
  color: var(--text-muted, #bfbfbf);
}


</style>
