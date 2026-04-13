<template>
  <AdminPageShell
    title="凭证管理"
    description="维护 AI_ACCOUNT：供应商账号、API 凭证与额度，供模型路由等使用。"
    empty-text="暂无账号。"
  >
    <div class="account-page">
      <AdminListToolbar>
        <template #left>
          <div class="account-toolbar-searches">

            <AstrsomnSearchPill
              v-model="query.accountName"
              layout="toolbar"
              placeholder="账号名称"
              button-label="查询"
              @search="fetchList"
            />
          </div>
        </template>

        <template #right>
          <AstrsomnSegmentedButton :buttons="toolbarSegmentButtons" />
        </template>
      </AdminListToolbar>

      <AstrsomnOverview
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
  ReloadOutlined,
  SearchOutlined
} from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AdminListToolbar from '@/components/home/AdminListToolbar.vue'
import AstrsomnOverview from '@/components/home/AstrsomnOverview.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'
import AccountForm from './AccountForm.vue'
import AccountCard from './AccountCard.vue'
import AccountModelsDrawer from './AccountModelsDrawer.vue'
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
  padding: 0 20px;
}

.account-toolbar-searches {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  flex: 1;
  min-width: 0;
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
  .account-toolbar-searches {
    width: 100%;
  }

  .pagination-wrap {
    justify-content: center;
  }
}

/* drawer styles moved to AccountModelsDrawer.vue */
</style>
