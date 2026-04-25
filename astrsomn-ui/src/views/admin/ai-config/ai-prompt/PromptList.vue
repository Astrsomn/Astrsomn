<template>
  <AdminPageShell
    title="提示词管理"
    description="同一 Prompt Key 共用一个逻辑提示词；每次保存生成新版本，列表按 Key 聚合展示当前最新版本。"
    empty-text="暂无提示词，请先创建。"
  >
    <div class="prompt-page">
      <AstrsomnDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstrsomnSearchPill
                v-model="query.promptTitle"
                layout="toolbar"
                placeholder="搜索标题"
                button-label="查询"
                @search="fetchList"
              />
              <AstrsomnStateSwitch v-model="query.status" @change="fetchList" />
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
            @toggle-select-all="toggleSelectAllCurrentPage"
          />
        </template>

        <AstrsomnDataView
          mode="table"
          :data-source="list"
          :loading="loading"
          :columns="columns"
          :row-selection="rowSelection"
          :scroll="{ x: 1180 }"
          row-key="id"
          empty-text="暂无匹配的提示词"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag :color="record.status === 'enabled' ? 'green' : 'default'">
                {{ record.status === 'enabled' ? '启用' : '禁用' }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'version'">
              v{{ record.version || 1 }}
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-button type="link" size="small" @click="openHistory(record)">历史</a-button>
              <a-divider type="vertical" />
              <a-button type="link" size="small" @click="openEdit(record)">编辑</a-button>
              <a-divider type="vertical" />
              <a-popconfirm title="确定删除吗？" ok-text="确认" cancel-text="取消" @confirm="() => handleDeleteOne(record.id)">
                <a-button type="link" danger size="small">删除</a-button>
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
import { computed, reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  DeleteOutlined,
  PlusOutlined,
  ReloadOutlined,
} from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'
import AstrsomnStateSwitch from '@/components/home/AstrsomnStateSwitch.vue'
import PromptFormModal from './PromptFormModal.vue'
import PromptHistoryModal from './PromptHistoryModal.vue'
import AstrsomnOverview from '@/components/home/AstrsomnOverview.vue'
import { aiPromptApi, type AiPrompt, type PageResponse } from '@/api/aiPrompt.ts'

type QueryState = {
  promptTitle?: string
  promptKey?: string
  scene?: string
  envCode?: string
  createUser?: string
  status?: string
}

const query = reactive<QueryState>({})
const loading = ref(false)
const list = ref<AiPrompt[]>([])
const columns = [
  { title: '标题', dataIndex: 'promptTitle', key: 'promptTitle', width: 220, ellipsis: true },
  { title: 'Prompt Key', dataIndex: 'promptKey', key: 'promptKey', width: 200, ellipsis: true },
  { title: '场景', dataIndex: 'scene', key: 'scene', width: 140, ellipsis: true },
  { title: '环境', dataIndex: 'envCode', key: 'envCode', width: 120, ellipsis: true },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '版本', dataIndex: 'version', key: 'version', width: 90 },
  { title: '操作', key: 'actions', width: 180, fixed: 'right' as const }
]

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

const resetFilters = () => {
  query.promptTitle = undefined
  query.promptKey = undefined
  query.scene = undefined
  query.envCode = undefined
  query.createUser = undefined
  query.status = undefined
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

void fetchList()
</script>

<style scoped>
.prompt-page {
  padding: 20px;
  margin-top: -8px;
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

@media (max-width: 720px) {
  .toolbar-left,
  .toolbar-right {
    width: 100%;
  }
}
</style>
