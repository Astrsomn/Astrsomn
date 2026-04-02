<template>
  <AdminPageShell
    title="凭证管理"
    description="维护 AI_ACCOUNT：供应商账号、API 凭证与额度，供模型路由等使用。"
    empty-text="暂无账号。"
  >
    <div class="account-page">
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

      <div class="account-grid">
        <AccountCard
          v-for="account in list"
          :key="account.id"
          :account="account"
          @edit="goEdit"
          @delete="handleDeleteOne"
          @show-models="openModelsDrawer"
        />
      </div>

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

    <a-drawer
      :open="modelsDrawer.open"
      placement="right"
      :width="520"
      :maskClosable="false"
      @close="modelsDrawer.open = false"
      class="models-drawer"
    >
      <template #title>
        <div class="drawer-title">
          <div class="drawer-title-main">
            <span class="drawer-title-h">{{ modelsDrawer.account?.accountName || '关联模型' }}</span>
            <span class="drawer-title-sub">{{ modelsDrawer.account?.accountKey || '' }}</span>
          </div>
          <a-tag v-if="modelsDrawer.account?.envCode" color="blue" class="drawer-env-tag">
            {{ modelsDrawer.account.envCode }}
          </a-tag>
        </div>
      </template>

      <a-spin :spinning="modelsDrawer.loading">
        <a-table
          :columns="modelsDrawer.columns"
          :data-source="modelsDrawer.models"
          :pagination="false"
          row-key="id"
          :scroll="{ x: 640 }"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'modelKey'">
              <code class="code-text">{{ record.modelKey }}</code>
            </template>
            <template v-else-if="column.key === 'status'">
              <a-tag :color="record.status === 'enabled' ? 'green' : 'default'">
                {{ record.status }}
              </a-tag>
            </template>
          </template>
        </a-table>

        <div v-if="!modelsDrawer.loading && modelsDrawer.models.length === 0" class="drawer-empty">
          暂无关联模型
        </div>
      </a-spin>
    </a-drawer>
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
import AccountCard from './AccountCard.vue'
import { aiAccountApi, type AiAccount, type PageResponse } from '@/api/aiAccount'
import type { AiModel } from '@/api/aiModel'

const formVisible = ref(false)
const currentRecord = ref<AiAccount | undefined>(undefined)

type QueryState = {
  accountKey?: string
  accountName?: string
}

// 卡片布局不需要表格列定义

const query = reactive<QueryState>({})
const list = ref<AiAccount[]>([])
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

const modelsDrawer = reactive({
  open: false,
  loading: false,
  account: undefined as AiAccount | undefined,
  models: [] as AiModel[],
  columns: [
    { title: '模型名称', dataIndex: 'modelName', key: 'modelName', width: 220, ellipsis: true },
    { title: '模型 Key', dataIndex: 'modelKey', key: 'modelKey', width: 190, ellipsis: true },
    { title: '类型', dataIndex: 'modelType', key: 'modelType', width: 90 },
    { title: '供应商', dataIndex: 'provider', key: 'provider', width: 140, ellipsis: true },
    { title: '状态', dataIndex: 'status', key: 'status', width: 110 }
  ]
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
  padding: 0 4px;
}

.search-cluster {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
  padding: 6px;
  border-radius: var(--radius-sm);
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
  border-radius: var(--radius-sm);
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

.account-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.pagination-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .account-grid {
    grid-template-columns: 1fr;
  }
}

@media (min-width: 769px) and (max-width: 1200px) {
  .account-grid {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  }
}

@media (min-width: 1201px) {
  .account-grid {
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  }
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

.models-drawer {
  :deep(.ant-drawer-body) {
    padding: 12px 16px;
  }
}

.drawer-title {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.drawer-title-main {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.drawer-title-h {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-heading);
}

.drawer-title-sub {
  font-size: 12px;
  color: var(--text-secondary);
}

.drawer-env-tag {
  flex-shrink: 0;
}

.drawer-empty {
  padding: 28px 0 12px;
  text-align: center;
  color: var(--text-secondary);
}
</style>
