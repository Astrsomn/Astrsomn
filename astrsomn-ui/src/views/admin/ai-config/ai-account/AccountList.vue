<template>
  <AdminPageShell
    title="AI 账号"
    description="维护 AI_ACCOUNT：供应商账号、API 凭证与额度，供模型路由等使用。"
    empty-text="暂无账号。"
  >
    <div class="config-page">
      <AdminListToolbar>
        <template #left>
          <div class="search-cluster">
            <a-input
              v-model:value="query.accountKey"
              placeholder="搜索 Account Key"
              class="toolbar-input search-main-input"
              allow-clear
              @pressEnter="fetchList"
            >
              <template #prefix><search-outlined /></template>
            </a-input>
            <a-input
              v-model:value="query.accountName"
              placeholder="账号名称"
              class="toolbar-input search-sub-input"
              allow-clear
              @pressEnter="fetchList"
            >
              <template #prefix><user-outlined /></template>
            </a-input>
          </div>
        </template>

        <template #right>
          <a-button type="primary" class="primary-btn" @click="fetchList">
            <template #icon><search-outlined /></template>
            查询
          </a-button>
          <a-popconfirm
            v-if="selectedRowKeys.length > 0"
            title="确定批量删除选中的账号吗？"
            ok-text="确认"
            cancel-text="取消"
            @confirm="handleBatchDelete"
          >
            <a-button danger class="ghost-btn danger-btn">
              <template #icon><delete-outlined /></template>
              批量删除
            </a-button>
          </a-popconfirm>
          <a-button class="ghost-btn" @click="resetFilters">重置</a-button>
          <a-button class="ghost-btn add-btn" @click="goCreate">
            <template #icon><plus-outlined /></template>
            新增
          </a-button>
        </template>
      </AdminListToolbar>

      <BaseOverview
        :list-length="list.length"
        :selected-count="selectedRowKeys.length"
        :all-current-selected="allCurrentSelected"
        :part-current-selected="partCurrentSelected"
        :show-actions="list.length > 0"
        :summary-text="`当前页 ${list.length} 条账号，已选 ${selectedRowKeys.length} 条。`"
        @toggle-select-all="toggleSelectAllCurrentPage"
      />

      <a-table
        :columns="columns"
        :data-source="list"
        :pagination="false"
        row-key="id"
        :row-selection="rowSelection"
        :scroll="{ x: 1100 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'accountKey'">
            <code class="code-text">{{ record.accountKey || '—' }}</code>
          </template>
          <template v-else-if="column.key === 'accountName'">
            <span>{{ record.accountName || '—' }}</span>
          </template>
          <template v-else-if="column.key === 'apiKey'">
            <span class="secret-mask">{{ maskSecret(record.apiKey) }}</span>
          </template>
          <template v-else-if="column.key === 'accountTokens'">
            <span>{{ record.accountTokens != null ? record.accountTokens : '—' }}</span>
          </template>
          <template v-else-if="column.key === 'envCode'">
            <span>{{ record.envCode || '—' }}</span>
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-button type="link" @click="goEdit(record)">编辑</a-button>
            <a-divider type="vertical" />
            <a-popconfirm
              title="确定删除吗？"
              ok-text="确认"
              cancel-text="取消"
              @confirm="() => handleDeleteOne(record.id)"
            >
              <a-button type="link" danger>删除</a-button>
            </a-popconfirm>
          </template>
        </template>
      </a-table>

      <div class="pagination-wrap">
        <a-pagination
          :current="page.pageNum"
          :page-size="page.pageSize"
          :total="page.total"
          :show-size-changer="false"
          @change="onPageChange"
        />
      </div>

      <AccountForm
        v-model:visible="formVisible"
        :record="currentRecord"
        @success="handleFormSuccess"
      />
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  DeleteOutlined,
  PlusOutlined,
  SearchOutlined,
  UserOutlined
} from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AdminListToolbar from '@/components/home/AdminListToolbar.vue'
import BaseOverview from '@/components/home/BaseOverview.vue'
import AccountForm from './AccountForm.vue'
import { aiAccountApi, type AiAccount, type PageResponse } from '@/api/aiAccount'

const formVisible = ref(false)
const currentRecord = ref<AiAccount | undefined>(undefined)

type QueryState = {
  accountKey?: string
  accountName?: string
}

const columns = [
  { title: 'Account Key', key: 'accountKey', width: 200, ellipsis: true },
  { title: '账号名称', key: 'accountName', width: 160, ellipsis: true },
  { title: 'API Key', key: 'apiKey', width: 200, ellipsis: true },
  { title: '额度', key: 'accountTokens', width: 120 },
  { title: '环境', key: 'envCode', width: 100 },
  { title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', width: 180, ellipsis: true },
  { title: '操作', key: 'actions', width: 160, fixed: 'right' as const }
]

const query = reactive<QueryState>({})
const list = ref<AiAccount[]>([])
const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})
const selectedRowKeys = ref<Array<number | string>>([])

const maskSecret = (value: string | undefined) => {
  if (value == null || value === '') return '—'
  if (value.length <= 8) return '••••••••'
  return `${value.slice(0, 4)}…${value.slice(-4)}`
}

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

const fetchList = async () => {
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
</script>

<style scoped>
.config-page {
  padding: 0 4px;
}

.search-cluster {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
  padding: 6px;
  border-radius: 16px;
  border: 1px solid var(--border-default);
  background: var(--bg-surface);
}

.search-cluster :deep(.ant-input-affix-wrapper) {
  border: none !important;
  box-shadow: none !important;
  background: transparent !important;
}

.search-cluster :deep(.ant-input-affix-wrapper:hover),
.search-cluster :deep(.ant-input-affix-wrapper-focused) {
  background: color-mix(in srgb, var(--bg-card) 85%, var(--bg-surface)) !important;
}

.toolbar-input {
  width: 200px;
}

.search-main-input {
  width: 280px;
}

.search-sub-input {
  width: 220px;
}

.primary-btn,
.ghost-btn {
  height: 40px;
  border-radius: 12px;
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

.pagination-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.code-text {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 12px;
}

.secret-mask {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 12px;
  color: var(--text-secondary);
}

@media (max-width: 720px) {
  .toolbar-input,
  .search-main-input,
  .search-sub-input {
    width: 100%;
  }

  .search-cluster {
    width: 100%;
  }

  .pagination-wrap {
    justify-content: center;
  }
}
</style>
