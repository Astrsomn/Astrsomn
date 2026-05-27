<template>
  <AstPageShell
      :breadcrumbs="breadcrumbs"
      description="配置敏感词、注入检测与风控策略。"
      empty-text="暂无安全策略。"
      title="安全治理"
  >
    <div class="sensitive-page">
      <AstDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstSearchInput
                  v-model="query.word"
                  layout="toolbar"
                  placeholder="搜索敏感词"
                  @search="fetchList"
              />
              <a-input
                  v-model:value="query.scopeKey"
                  allow-clear
                  class="toolbar-input"
                  placeholder="作用范围"
                  @pressEnter="fetchList"
              >
                <template #prefix>
                  <appstore-outlined/>
                </template>
              </a-input>

              <div aria-label="状态筛选" class="status-switch" role="group">
                <a-button
                    :class="{ active: query.status === 'ENABLED' }"
                    class="status-btn"
                    @click="toggleStatusFilter('ENABLED')"
                >
                  <template #icon>
                    <check-circle-outlined/>
                  </template>
                  启用
                </a-button>
                <a-button
                    :class="{ active: query.status === 'DISABLED' }"
                    class="status-btn"
                    @click="toggleStatusFilter('DISABLED')"
                >
                  <template #icon>
                    <stop-outlined/>
                  </template>
                  禁用
                </a-button>
              </div>

              <a-button class="filter-toggle-btn" @click="showAdvanced = !showAdvanced">
                <template #icon>
                  <filter-outlined/>
                </template>
                {{ showAdvanced ? '收起筛选' : '更多筛选' }}
              </a-button>
            </div>

            <div class="toolbar-right">
              <a-popconfirm
                  v-if="selectedRowKeys.length > 0"
                  cancel-text="取消"
                  ok-text="确认"
                  title="确定批量删除选中的敏感词规则吗？"
                  @confirm="handleBatchDelete"
              >
                <a-button class="ghost-btn danger-btn" danger>
                  <template #icon>
                    <delete-outlined/>
                  </template>
                  删除 ({{ selectedRowKeys.length }})
                </a-button>
              </a-popconfirm>
              <a-button v-else class="ghost-btn danger-btn" danger disabled>
                <template #icon>
                  <delete-outlined/>
                </template>
                删除
              </a-button>
              <a-button class="ghost-btn" @click="resetFilters">重置</a-button>
              <a-button class="ghost-btn" @click="openCreate">
                <template #icon>
                  <plus-outlined/>
                </template>
                新增
              </a-button>
            </div>
          </div>
        </template>

        <template v-if="showAdvanced" #toolbar-extra>
          <a-select
              v-model:value="query.matchType"
              :options="matchTypeOptions"
              allow-clear
              class="toolbar-select"
              placeholder="匹配类型"
          />
          <a-select
              v-model:value="query.action"
              :options="actionOptions"
              allow-clear
              class="toolbar-select"
              placeholder="处置动作"
          />
          <a-input
              v-model:value="query.category"
              allow-clear
              class="toolbar-input narrow"
              placeholder="分类"
              @pressEnter="fetchList"
          >
            <template #prefix>
              <tags-outlined/>
            </template>
          </a-input>
        </template>


        <AstDataView
            :columns="columns"
            :data-source="list"
            :loading="loading"
            :row-selection="rowSelection"
            :scroll="{ x: 1320 }"
            empty-text="暂无匹配的安全规则"
            mode="table"
            row-key="id"
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
              <a-divider type="vertical"/>
              <a-popconfirm
                  cancel-text="取消"
                  ok-text="确认"
                  title="确定删除吗？"
                  @confirm="() => handleDeleteOne(record.id)"
              >
                <a-button danger type="link">删除</a-button>
              </a-popconfirm>
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

      <SensitiveWordFormModal
          v-model:open="modal.open"
          :confirm-loading="modal.submitting"
          :initial="modalInitial"
          :mode="modal.mode"
          @submit="handleFormSubmit"
      />
    </div>
  </AstPageShell>
</template>

<script lang="ts" setup>
import {computed, reactive, ref} from 'vue'
import {message} from 'ant-design-vue'
import {
  AppstoreOutlined,
  CheckCircleOutlined,
  DeleteOutlined,
  FilterOutlined,
  PlusOutlined,
  StopOutlined,
  TagsOutlined
} from '@ant-design/icons-vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import AstDataSection from '@/components/home/AstDataSection.vue'
import AstDataView from '@/components/home/AstDataView.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import SensitiveWordFormModal from './SensitiveWordFormModal.vue'
import {type AiSensitiveWord, aiSensitiveWordApi, type PageResponse} from '@/api/aiSensitiveWord.ts'

const breadcrumbs = [
  {title: 'AI 安全', href: '/admin/ai-safety'},
  {title: '安全治理'},
]

type QueryState = {
  word?: string
  matchType?: string
  scopeKey?: string
  action?: string
  status?: string
  category?: string
}

const matchTypeOptions = [
  {label: '精确匹配', value: 'EXACT'},
  {label: '模糊匹配', value: 'FUZZY'},
  {label: '正则匹配', value: 'REGEX'}
]

const actionOptions = [
  {label: '直接拦截', value: 'BLOCK'},
  {label: '替换文本', value: 'REPLACE'},
  {label: '仅告警', value: 'WARN'}
]

const columns = [
  {title: '敏感词', key: 'word', width: 220, ellipsis: true},
  {title: '匹配类型', key: 'matchType', width: 120},
  {title: '作用范围', dataIndex: 'scopeKey', key: 'scopeKey', width: 160, ellipsis: true},
  {title: '分类', dataIndex: 'category', key: 'category', width: 120, ellipsis: true},
  {title: '处置动作', key: 'action', width: 120},
  {title: '替换文本', key: 'replacement', width: 160, ellipsis: true},
  {title: '状态', key: 'status', width: 100},
  {title: '操作', key: 'actions', width: 160, fixed: 'right' as const}
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
    const payload: AiSensitiveWord = {...form}
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

.toolbar-input {
  width: 220px;
}

.toolbar-input.narrow,
.toolbar-select {
  width: 180px;
}

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
  .toolbar-select {
    width: 100%;
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
