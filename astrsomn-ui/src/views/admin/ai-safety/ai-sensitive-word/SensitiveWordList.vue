<template>
  <AdminPageShell
    title="安全治理"
    description="配置敏感词、注入检测与风控策略。"
    empty-text="暂无安全策略。"
  >
    <div class="sensitive-page">
      <AstrsomnDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
          <div class="search-cluster">
            <a-input
              v-model:value="query.word"
              placeholder="搜索敏感词"
              class="toolbar-input search-main-input"
              allow-clear
              @pressEnter="fetchList"
            >
              <template #prefix><search-outlined /></template>
            </a-input>
            <a-input
              v-model:value="query.scopeKey"
              placeholder="作用范围"
              class="toolbar-input search-sub-input"
              allow-clear
              @pressEnter="fetchList"
            >
              <template #prefix><appstore-outlined /></template>
            </a-input>
          </div>

          <div class="status-switch" role="group" aria-label="状态筛选">
            <a-button
              class="status-btn"
              :class="{ active: query.status === 'ENABLED' }"
              @click="toggleStatusFilter('ENABLED')"
            >
              <template #icon><check-circle-outlined /></template>
              启用
            </a-button>
            <a-button
              class="status-btn"
              :class="{ active: query.status === 'DISABLED' }"
              @click="toggleStatusFilter('DISABLED')"
            >
              <template #icon><stop-outlined /></template>
              禁用
            </a-button>
          </div>

          <a-button class="filter-toggle-btn" @click="showAdvanced = !showAdvanced">
            <template #icon><filter-outlined /></template>
            {{ showAdvanced ? '收起筛选' : '更多筛选' }}
          </a-button>
            </div>

            <div class="toolbar-right">
          <a-button type="primary" class="primary-btn" @click="fetchList">
            <template #icon><search-outlined /></template>
            查询
          </a-button>
          <a-popconfirm
            v-if="selectedRowKeys.length > 0"
            title="确定批量删除选中的敏感词规则吗？"
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
          <a-button class="ghost-btn" @click="openCreate">
            <template #icon><plus-outlined /></template>
            新增
          </a-button>
            </div>
          </div>
        </template>

        <template v-if="showAdvanced" #toolbar-extra>
          <a-select
            v-model:value="query.matchType"
            :options="matchTypeOptions"
            placeholder="匹配类型"
            class="toolbar-select"
            allow-clear
          />
          <a-select
            v-model:value="query.action"
            :options="actionOptions"
            placeholder="处置动作"
            class="toolbar-select"
            allow-clear
          />
          <a-input
            v-model:value="query.category"
            placeholder="分类"
            class="toolbar-input narrow"
            allow-clear
            @pressEnter="fetchList"
          >
            <template #prefix><tags-outlined /></template>
          </a-input>
        </template>

        <template #overview>
          <AstrsomnOverview
        :list-length="list.length"
        :selected-count="selectedRowKeys.length"
        :all-current-selected="allCurrentSelected"
        :part-current-selected="partCurrentSelected"
        :show-actions="list.length > 0"
        :summary-text="`当前页 ${list.length} 条敏感词规则，已选 ${selectedRowKeys.length} 条。`"
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
          empty-text="暂无匹配的安全规则"
        >
          <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'word'">
            <code class="word-text">{{ record.word }}</code>
          </template>
          <template v-else-if="column.key === 'matchType'">
            <a-tag :color="matchTypeColorMap[record.matchType || ''] || 'default'">
              {{ matchTypeLabel(record.matchType) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-tag :color="actionColorMap[record.action || ''] || 'default'">
              {{ actionLabel(record.action) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.status === 'ENABLED' ? 'green' : 'default'">
              {{ statusLabel(record.status) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'replacement'">
            <span>{{ record.replacement || '—' }}</span>
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-button type="link" @click="openEdit(record)">编辑</a-button>
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

      <SensitiveWordFormModal
        v-model:open="modal.open"
        :mode="modal.mode"
        :confirm-loading="modal.submitting"
        :initial="modalInitial"
        @submit="handleFormSubmit"
      />
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  AppstoreOutlined,
  CheckCircleOutlined,
  DeleteOutlined,
  FilterOutlined,
  PlusOutlined,
  SearchOutlined,
  StopOutlined,
  TagsOutlined
} from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'
import AstrsomnOverview from '@/components/home/AstrsomnOverview.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import SensitiveWordFormModal from './SensitiveWordFormModal.vue'
import { aiSensitiveWordApi, type AiSensitiveWord, type PageResponse } from '@/api/aiSensitiveWord.ts'

type QueryState = {
  word?: string
  matchType?: string
  scopeKey?: string
  action?: string
  status?: string
  category?: string
}

const matchTypeOptions = [
  { label: '精确匹配', value: 'EXACT' },
  { label: '模糊匹配', value: 'FUZZY' },
  { label: '正则匹配', value: 'REGEX' }
]

const actionOptions = [
  { label: '直接拦截', value: 'BLOCK' },
  { label: '替换文本', value: 'REPLACE' },
  { label: '仅告警', value: 'WARN' }
]

const columns = [
  { title: '敏感词', key: 'word', width: 220, ellipsis: true },
  { title: '匹配类型', key: 'matchType', width: 120 },
  { title: '作用范围', dataIndex: 'scopeKey', key: 'scopeKey', width: 160, ellipsis: true },
  { title: '分类', dataIndex: 'category', key: 'category', width: 120, ellipsis: true },
  { title: '处置动作', key: 'action', width: 120 },
  { title: '替换文本', key: 'replacement', width: 160, ellipsis: true },
  { title: '状态', key: 'status', width: 100 },
  { title: '操作', key: 'actions', width: 160, fixed: 'right' as const }
]

const matchTypeColorMap: Record<string, string> = {
  EXACT: 'blue',
  FUZZY: 'purple',
  REGEX: 'orange'
}

const actionColorMap: Record<string, string> = {
  BLOCK: 'red',
  REPLACE: 'gold',
  WARN: 'cyan'
}

const query = reactive<QueryState>({})
const showAdvanced = ref(false)
const list = ref<AiSensitiveWord[]>([])
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

const toggleStatusFilter = (value: 'ENABLED' | 'DISABLED') => {
  query.status = query.status === value ? undefined : value
}

const resetFilters = () => {
  query.word = undefined
  query.matchType = undefined
  query.scopeKey = undefined
  query.action = undefined
  query.status = undefined
  query.category = undefined
  showAdvanced.value = false
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

const modal = reactive({
  open: false,
  mode: 'create' as 'create' | 'edit',
  submitting: false
})

const modalInitial = ref<AiSensitiveWord | null>(null)

const matchTypeLabel = (value: string | undefined) => {
  return matchTypeOptions.find((item) => item.value === value)?.label ?? value ?? '—'
}

const actionLabel = (value: string | undefined) => {
  return actionOptions.find((item) => item.value === value)?.label ?? value ?? '—'
}

const statusLabel = (value: string | undefined) => {
  if (value === 'ENABLED') return '启用'
  if (value === 'DISABLED') return '禁用'
  return value ?? '—'
}

const fetchList = async () => {
  loading.value = true
  try {
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        word: query.word || undefined,
        matchType: query.matchType || undefined,
        scopeKey: query.scopeKey || undefined,
        action: query.action || undefined,
        status: query.status || undefined,
        category: query.category || undefined
      }
    }
    const resp: PageResponse<AiSensitiveWord> = await aiSensitiveWordApi.queryPage(payload)
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

const openCreate = () => {
  modal.mode = 'create'
  modalInitial.value = null
  modal.open = true
}

const openEdit = async (record: AiSensitiveWord) => {
  modal.mode = 'edit'
  const id = record.id
  if (id == null) return

  const detail = await aiSensitiveWordApi.detail(id)
  modalInitial.value = detail
  modal.open = true
}

const handleDeleteOne = async (id: number | string | undefined) => {
  if (id == null) return
  const msg = await aiSensitiveWordApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await aiSensitiveWordApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleFormSubmit = async (form: AiSensitiveWord) => {
  modal.submitting = true
  try {
    const payload: AiSensitiveWord = { ...form }
    let msg: string
    if (modal.mode === 'create') {
      delete (payload as { id?: unknown }).id
      msg = await aiSensitiveWordApi.create(payload)
    } else {
      msg = await aiSensitiveWordApi.update(payload)
    }

    message.success(msg)
    modal.open = false
    void fetchList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '保存失败')
  } finally {
    modal.submitting = false
  }
}

void fetchList()
</script>

<style scoped>
.sensitive-page {
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

.search-cluster :deep(.ant-input-affix-wrapper),
.search-cluster :deep(.ant-select-selector) {
  border: none !important;
  box-shadow: none !important;
  background: transparent !important;
}

.search-cluster :deep(.ant-input-affix-wrapper:hover),
.search-cluster :deep(.ant-input-affix-wrapper-focused),
.search-cluster :deep(.ant-select-focused .ant-select-selector),
.search-cluster :deep(.ant-select-selector:hover) {
  background: color-mix(in srgb, var(--bg-card) 85%, var(--bg-surface)) !important;
}

.toolbar-input {
  width: 200px;
}

.search-main-input {
  width: 320px;
}

.search-sub-input {
  width: 220px;
}

.toolbar-input.narrow,
.toolbar-select {
  width: 180px;
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

.status-switch {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 4px;
  border-radius: 14px;
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
}

.status-btn {
  height: 36px;
  border: none;
  border-radius: 10px;
  color: var(--text-secondary);
  background: transparent;
  box-shadow: none;
}

.status-btn.active {
  color: var(--primary);
  background: color-mix(in srgb, var(--primary) 10%, var(--bg-card));
}

.filter-toggle-btn {
  height: 40px;
  border-radius: 12px;
  color: var(--text-secondary);
}

.word-text {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}

@media (max-width: 720px) {
  .toolbar-input,
  .toolbar-input.narrow,
  .search-main-input,
  .search-sub-input,
  .toolbar-select {
    width: 100%;
  }

  .search-cluster,
  .status-switch {
    width: 100%;
  }

  .search-cluster {
    padding: 8px;
  }

  .status-switch {
    justify-content: space-between;
  }

  .status-btn {
    flex: 1;
  }

  .toolbar-left,
  .toolbar-right {
    width: 100%;
  }
}
</style>
