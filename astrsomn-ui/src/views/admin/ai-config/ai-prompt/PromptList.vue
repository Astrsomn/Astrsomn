<template>
  <AdminPageShell
    title="提示词管理"
    description="同一 Prompt Key 共用一个逻辑提示词；每次保存生成新版本，列表按 Key 聚合展示当前最新版本。"
    empty-text="暂无提示词，请先创建。"
  >
    <div ref="pageRef" class="prompt-page">
      <AdminListToolbar>
        <template #left>
          <div class="prompt-toolbar-searches">
            <ToolbarSearchPill
              v-model="query.promptTitle"
              layout="toolbar"
              placeholder="搜索标题"
              button-label="查询"
              @search="fetchList"
            />
      
          </div>

          <TrioStateSwitch v-model="query.status" @change="fetchList" />


      
        </template>

        <template #right>
          <ToolbarSegmentedButton :buttons="toolbarSegmentButtons" />
        </template>


      </AdminListToolbar>

      <BaseOverview
        :list-length="list.length"
        :selected-count="selectedRowKeys.length"
        :all-current-selected="allCurrentSelected"
        :part-current-selected="partCurrentSelected"
        :show-actions="list.length > 0"
        @toggle-select-all="toggleSelectAllCurrentPage"
      />

      <a-spin :spinning="loading">
        <div v-if="list.length > 0" class="prompt-grid">
          <div
            v-for="item in list"
            :key="item.id ?? `${item.promptKey ?? 'prompt'}-${item.version ?? 0}`"
            class="prompt-grid-item"
          >
            <PromptCard
              :record="item"
              :selected="isSelected(item.id)"
              @select-change="(checked) => toggleSelect(item.id, checked)"
              @history="openHistory"
              @edit="openEdit"
              @delete="handleDeleteOne"
            />
          </div>
        </div>

        <div v-else class="empty-wrap">
          <a-empty description="暂无匹配的提示词卡片" />
        </div>
      </a-spin>

      <div class="pagination-wrap">
        <span class="pagination-total">共 {{ page.total }} 条</span>
        <a-pagination
          :current="page.pageNum"
          :page-size="page.pageSize"
          :total="page.total"
          :show-size-changer="false"
          @change="onPageChange"
        />
      </div>

      <PromptFormModal
        v-model:open="modal.open"
        :mode="modal.mode"
        :confirm-loading="modal.submitting"
        :initial="modalInitial"
        @submit="handleFormSubmit"
      />

      <PromptHistoryModal
        v-model:open="historyModal.open"
        :prompt-key="historyModal.promptKey"
        :env-code="historyModal.envCode"
      />
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  CheckCircleOutlined,
  CloudOutlined,
  DeleteOutlined,
  FilterOutlined,
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined,
  StopOutlined,
  TagsOutlined,
  UserOutlined
} from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AdminListToolbar from '@/components/home/AdminListToolbar.vue'
import ToolbarSearchPill from '@/components/home/ToolbarSearchPill.vue'
import ToolbarSegmentedButton, { type SegmentedButton } from '@/components/home/ToolbarSegmentedButton.vue'
import TrioStateSwitch from '@/components/home/TrioStateSwitch.vue'
import PromptCard from './PromptCard.vue'
import PromptFormModal from './PromptFormModal.vue'
import PromptHistoryModal from './PromptHistoryModal.vue'
import BaseOverview from '../../../../components/home/BaseOverview.vue'
import { aiPromptApi, type AiPrompt, type PageResponse } from '@/api/aiPrompt.ts'

type QueryState = {
  promptTitle?: string
  promptKey?: string
  scene?: string
  envCode?: string
  createUser?: string
  status?: string
}

const PROMPT_CARD_MIN_WIDTH_PX = 280
const PROMPT_GRID_GAP_PX = 12
const promptCardMinWidth = `${PROMPT_CARD_MIN_WIDTH_PX}px`
const promptGridGap = `${PROMPT_GRID_GAP_PX}px`

const resolveGridColumns = () => {
  if (typeof window === 'undefined') return 4
  const width = pageRef.value?.clientWidth ?? window.innerWidth
  const columns = Math.floor((width + PROMPT_GRID_GAP_PX) / (PROMPT_CARD_MIN_WIDTH_PX + PROMPT_GRID_GAP_PX))
  return Math.max(1, Math.min(4, columns))
}

const resolvePageSize = (columns: number) => {
  if (columns >= 4) return 12
  if (columns === 3) return 9
  if (columns === 2) return 8
  return 6
}

const query = reactive<QueryState>({})
const showAdvanced = ref(false)
const loading = ref(false)
const list = ref<AiPrompt[]>([])
const pageRef = ref<HTMLElement | null>(null)
const currentGridColumns = ref(resolveGridColumns())
const promptGridTemplateColumns = computed(() => `repeat(${currentGridColumns.value}, minmax(0, 1fr))`)

const page = reactive({
  pageNum: 1,
  pageSize: resolvePageSize(currentGridColumns.value),
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

const isSelected = (id: number | string | undefined) => {
  if (id == null) return false
  return selectedRowKeys.value.includes(id)
}

const toggleSelect = (id: number | string | undefined, checked: boolean) => {
  if (id == null) return
  if (checked) {
    if (!selectedRowKeys.value.includes(id)) {
      selectedRowKeys.value = [...selectedRowKeys.value, id]
    }
    return
  }
  selectedRowKeys.value = selectedRowKeys.value.filter((key) => key !== id)
}

const toggleSelectAllCurrentPage = (checked: boolean) => {
  if (checked) {
    selectedRowKeys.value = Array.from(new Set([...selectedRowKeys.value, ...currentPageIds.value]))
    return
  }
  selectedRowKeys.value = selectedRowKeys.value.filter((id) => !currentPageIds.value.includes(id))
}

const toggleEnabledFilter = (value: 'enabled' | 'disabled') => {
  query.status = query.status === value ? undefined : value
}

const syncPageSizeWithGrid = async () => {
  const nextColumns = resolveGridColumns()
  currentGridColumns.value = nextColumns
  const nextPageSize = resolvePageSize(nextColumns)
  if (page.pageSize === nextPageSize) return
  page.pageSize = nextPageSize
  page.pageNum = 1
  await fetchList()
}

const resetFilters = () => {
  query.promptTitle = undefined
  query.promptKey = undefined
  query.scene = undefined
  query.envCode = undefined
  query.createUser = undefined
  query.status = undefined
  showAdvanced.value = false
  page.pageNum = 1
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
        title: '将删除选中项对应的 Prompt Key 下全部历史版本，确定吗？',
        okText: '确认',
        cancelText: '取消',
        onOk: () => handleBatchDelete()
      })
    }
  },
  {
    label: '重置',
    icon: ReloadOutlined,
    onClick: resetFilters
  },
  {
    label: '新增',
    type: 'primary',
    icon: PlusOutlined,
    onClick: openCreate
  }
])

const modal = reactive({
  open: false,
  mode: 'create' as 'create' | 'edit',
  submitting: false
})

const modalInitial = ref<AiPrompt | null>(null)

const historyModal = reactive({
  open: false,
  promptKey: undefined as string | undefined,
  envCode: undefined as string | undefined
})

const openHistory = (record: AiPrompt) => {
  historyModal.promptKey = record.promptKey
  historyModal.envCode = record.envCode
  historyModal.open = true
}

const fetchList = async () => {
  loading.value = true
  try {
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        promptTitle: query.promptTitle || undefined,
        promptKey: query.promptKey || undefined,
        scene: query.scene || undefined,
        envCode: query.envCode || undefined,
        createUser: query.createUser || undefined,
        status: query.status || undefined
      }
    }

    const resp: PageResponse<AiPrompt> = await aiPromptApi.queryPage(payload)
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

const openCreate = () => {
  modal.mode = 'create'
  modalInitial.value = null
  modal.open = true
}

const openEdit = async (record: AiPrompt) => {
  modal.mode = 'edit'
  const id = record.id
  if (id == null) return

  const detail = await aiPromptApi.detail(id)
  modalInitial.value = detail
  modal.open = true
}

const handleDeleteOne = async (id: number | string) => {
  if (id == null) return
  const msg = await aiPromptApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await aiPromptApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleFormSubmit = async (form: AiPrompt) => {
  modal.submitting = true
  try {
    const payload: AiPrompt = { ...form }
    delete payload.version

    let msg: string
    if (modal.mode === 'create') {
      delete (payload as { id?: unknown }).id
      msg = await aiPromptApi.create(payload)
    } else {
      msg = await aiPromptApi.update(payload)
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

let resizeObserver: ResizeObserver | null = null

const voidSyncPageSizeWithGrid = () => {
  void syncPageSizeWithGrid()
}

onMounted(() => {
  voidSyncPageSizeWithGrid()
  if (typeof ResizeObserver !== 'undefined' && pageRef.value) {
    resizeObserver = new ResizeObserver(() => {
      voidSyncPageSizeWithGrid()
    })
    resizeObserver.observe(pageRef.value)
    return
  }
  window.addEventListener('resize', voidSyncPageSizeWithGrid)
})

onBeforeUnmount(() => {
  resizeObserver?.disconnect()
  window.removeEventListener('resize', voidSyncPageSizeWithGrid)
})

void fetchList()
</script>

<style scoped>
.prompt-page {
  padding: 0 20px;
  margin-top: -8px;
}

.prompt-toolbar-searches {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  flex: 1;
  min-width: 0;
}

.toolbar-input {
  width: 200px;
}

.toolbar-input.narrow {
  width: 160px;
}

.status-switch {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 4px;
  border-radius: var(--radius-sm);
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
}

.status-btn {
  height: 36px;
  border: none;
  border-radius: var(--radius-sm);
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
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
}

.prompt-grid {
  display: grid;
  grid-template-columns: v-bind(promptGridTemplateColumns);
  gap: v-bind(promptGridGap);
}

.prompt-grid-item {
  min-width: v-bind(promptCardMinWidth);
}

.empty-wrap {
  display: flex;
  justify-content: center;
  padding: 32px 0 12px;
}

.pagination-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.pagination-total {
  font-size: 13px;
  color: var(--text-secondary);
}

@media (max-width: 720px) {
  .toolbar-input,
  .toolbar-input.narrow {
    width: 100%;
  }

  .prompt-toolbar-searches,
  .status-switch {
    width: 100%;
  }

  .status-switch {
    justify-content: space-between;
  }

  .status-btn {
    flex: 1;
  }

  .prompt-grid {
    grid-template-columns: 1fr;
  }

  .prompt-grid-item {
    min-width: 0;
  }

  .pagination-wrap {
    justify-content: center;
  }
}

@media (max-width: 560px) {
  .pagination-total {
    width: 100%;
    text-align: center;
  }
}
</style>
