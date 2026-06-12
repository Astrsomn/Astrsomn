<template>
  <AstPageShell
      :show-view-toggle="true"
      :view-mode="viewMode"
      :view-toggle-handler="handleViewToggle"
  
      :empty-text="t.list.emptyText"

  >
    <div ref="pageRef" class="account-page">
      <AstDataSection>
        <template #toolbar>
          <div class="toolbar">
            <AstSearchInput
                v-model="query.accountName"
                :button-label="t.list.searchButton"
                layout="toolbar"
                :placeholder="t.list.searchPlaceholder"
                @search="fetchList"
            />
            <div class="provider-filter">
              <ExtensionSelector
                  v-model:value="query.extensionCode"
                  :allow-clear="true"
                  :only-applied="true"
                  :placeholder="t.list.filterProviderPlaceholder"
                  size="middle"
                  @update:value="onProviderChange"
              />
            </div>
          </div>
        </template>

        <!-- Card Grid Mode -->
        <div v-if="dataViewMode === 'card'" class="account-grid-section">
          <a-spin :spinning="loading">
            <div class="account-grid">
              <!-- Create Card -->
              <div class="add-card" @click="goCreate">
                <PlusOutlined class="add-icon"/>
                <span class="add-text">{{ t.list.btnCreate }}</span>
              </div>
              <!-- Account Cards -->
              <AccountCard
                  v-for="record in list"
                  :key="record.id"
                  :account="record"
                  :selected="record.id != null && selectedKeySet.has(record.id)"
                  @delete="handleDeleteOne"
                  @edit="goEdit"
                  @toggle="onToggleSelect"
                  @show-models="openModelsDrawer"
              />
            </div>
          </a-spin>
        </div>

        <!-- Table Mode -->
        <AstDataView
            v-else
            :columns="columns"
            :data-source="list"
            :loading="loading"
            mode="table"
            :row-selection="rowSelection"
            :scroll="{ x: 1180 }"
            :empty-text="t.list.emptyMatchText"
            row-key="id"
        >
          <template #bodyCell="{ column, record }">

            <template v-if="column.key === 'callCount'">
              <span class="metric-chip metric-chip--call">
                <span class="metric-value">{{ Number(record.callCount ?? 0).toLocaleString() }}</span>
                <span class="metric-unit">{{ t.list.metricCallUnit }}</span>
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
                <a-popconfirm :title="t.list.deleteConfirm" @confirm="() => handleDeleteOne(record.id)">
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
import {computed, reactive, ref} from 'vue'
import {message, Modal} from 'ant-design-vue'
import {DeleteOutlined, EditOutlined, LinkOutlined, PlusOutlined} from '@ant-design/icons-vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import AstDataSection from '@/components/home/AstDataSection.vue'
import AstDataView from '@/components/home/AstDataView.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AccountForm from './component/AccountForm.vue'
import AccountModelsDrawer from './component/AccountModelsDrawer.vue'
import AccountCard from './component/AccountCard.vue'
import ExtensionSelector from '@/views/admin/system-config/system-extension/selector/ExtensionSelector.vue'
import {type AiAccount, aiAccountApi, type PageResponse} from '@/api/aiAccount'
import type {AiModel} from '@/api/aiModel'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-account')

const props = withDefaults(defineProps<{
  initialViewMode?: 'grid' | 'list'
}>(), {
  initialViewMode: 'list'
})

const pageRef = ref<HTMLElement | null>(null)
const formVisible = ref(false)
const currentRecord = ref<AiAccount | undefined>(undefined)
const viewMode = ref<'grid' | 'list'>(props.initialViewMode)
const dataViewMode = computed<'card' | 'table'>(() => (viewMode.value === 'grid' ? 'card' : 'table'))

type QueryState = {
  accountKey?: string
  accountName?: string
  extensionCode?: string
}

const columns = computed(() => [
  {
    title: t.value.list.colProvider,
    key: 'providerAvatar',
    dataIndex: 'providerAvatar',
    width: 80,
    align: 'center',
    enableBase64Render: true
  },
  {title: t.value.list.colAccountName, dataIndex: 'accountName', key: 'accountName', width: 180, ellipsis: true},
  {title: t.value.list.colAccountKey, dataIndex: 'accountKey', key: 'accountKey', width: 180, ellipsis: true, copyable: true},
  {title: t.value.list.colCallCount, dataIndex: 'callCount', key: 'callCount', width: 140, align: 'center'},
  {title: t.value.list.colTotalTokens, dataIndex: 'totalTokens', key: 'totalTokens', width: 170, align: 'center'},
  {title: t.value.list.colAccountTokens, dataIndex: 'accountTokens', key: 'accountTokens', width: 170, align: 'center'},
  {title: t.value.list.colRemainingTokens, key: 'remainingTokens', width: 170, align: 'center'},

  {title: t.value.list.colExtensionName, dataIndex: 'extensionName', key: 'extensionName', width: 120, ellipsis: true},
  {title: t.value.list.colApiUrl, dataIndex: 'apiUrl', key: 'apiUrl', width: 120, ellipsis: true},
  {
    title: t.value.list.colStatus,
    dataIndex: 'status',
    key: 'status',
    width: 120,
    ellipsis: true,
    tag: true,
    tagColor: (status: string) => status === 'enabled' ? 'green' : 'red'
  },
  {title: t.value.list.colEnvCode, dataIndex: 'envCode', key: 'envCode', width: 120, ellipsis: true, tag: true, tagColor: 'blue'},
  {title: t.value.list.colCreateTime, dataIndex: 'createTime', key: 'createTime', width: 150, dateFormat: true},
  {title: t.value.list.colCreateUser, dataIndex: 'createUser', key: 'createUser', width: 150},
  {title: t.value.list.colActions, key: 'actions', width: 220, fixed: 'right' as const}
])

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

const onToggleSelect = (id: number | string, checked: boolean) => {
  if (checked) {
    if (!selectedRowKeys.value.includes(id)) {
      selectedRowKeys.value = [...selectedRowKeys.value, id]
    }
    return
  }
  selectedRowKeys.value = selectedRowKeys.value.filter((item) => item !== id)
}

const handleViewToggle = () => {
  viewMode.value = viewMode.value === 'grid' ? 'list' : 'grid'
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

const providerAvatarCell = (record: AiAccount) => {
  const raw = record?.providerAvatar
  return typeof raw === 'string' && raw.trim() ? raw.trim() : ''
}

const openModelsDrawer = async (account: AiAccount) => {
  if (!account.accountKey) {
    message.error(t.value.list.errorAccountKeyEmpty)
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
    message.error(t.value.list.errorLoadModels)
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
  align-items: center;
  gap: 14px;
  flex-wrap: wrap;
}

.provider-filter {
  width: 260px;
  min-width: 220px;
}

.secret-mask {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 12px;
  color: var(--text-secondary);
}

/* ── Card Grid (matching AgentSection.vue) ── */
.account-grid-section {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.account-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 14px;
}

.account-grid > * {
  animation: cardEnter 0.35s ease both;
}

.account-grid > *:nth-child(1) { animation-delay: 0ms; }
.account-grid > *:nth-child(2) { animation-delay: 40ms; }
.account-grid > *:nth-child(3) { animation-delay: 80ms; }
.account-grid > *:nth-child(4) { animation-delay: 120ms; }
.account-grid > *:nth-child(5) { animation-delay: 160ms; }
.account-grid > *:nth-child(6) { animation-delay: 200ms; }
.account-grid > *:nth-child(7) { animation-delay: 240ms; }
.account-grid > *:nth-child(8) { animation-delay: 280ms; }
.account-grid > *:nth-child(9) { animation-delay: 320ms; }
.account-grid > *:nth-child(10) { animation-delay: 360ms; }
.account-grid > *:nth-child(n+11) { animation-delay: 400ms; }

@keyframes cardEnter {
  from {
    opacity: 0;
    transform: translateY(16px) scale(0.97);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}


/* ── Add Card (matching AgentSection.vue) ── */
.add-card {
  border: 2px dashed var(--border-subtle);
  background: transparent;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 140px;
  color: var(--text-muted);
}

.add-card:hover {
  border-color: var(--primary);
  color: var(--primary);
  background: color-mix(in srgb, var(--primary) 2%, transparent);
}

.add-icon {
  font-size: 22px;
  opacity: 0.4;
  transition: opacity 0.2s;
}

.add-card:hover .add-icon {
  opacity: 0.8;
}

.add-text {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

/* ── Metric Chips (table mode) ── */
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
</style>
