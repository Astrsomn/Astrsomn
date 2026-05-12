<template>
  <AstrsomnPageShell
    title="提示词管理"
    description="同一 Prompt Key 共用一个逻辑提示词；每次保存生成新版本，列表按 Key 聚合展示当前最新版本。"
    empty-text="暂无提示词，请先创建。"
    :breadcrumbs="breadcrumbs"
    :show-view-toggle="true"
    :view-mode="viewMode"
    :view-toggle-handler="handleViewToggle"
  >
    <div ref="pageRef" class="prompt-page">
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
              <PromptSceneTagSelector v-model="query.sceneTags" @change="fetchList" />
              <AstrsomnStateSwitch v-model="query.status" @change="fetchList" />
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
          :scroll="{ x: 1180 }"
          row-key="id"
          empty-text="暂无匹配的提示词"
          :card-columns="currentGridColumns"
          :card-min-width="promptCardMinWidth"
          :card-gap="promptCardGap"
        >
          <template #card="{ record }">
            <PromptCard
              :record="record"
              :selected="record.id != null && selectedKeySet.has(record.id)"
              @select-change="onPromptCardSelectChange.bind(null, record.id)"
              @history="openHistory"
              @edit="openEdit"
              @delete="handleDeleteOne"
            />
          </template>
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag :color="record.status === 'enabled' ? 'green' : 'default'">
                {{ record.status === 'enabled' ? '启用' : '禁用' }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'version'">
              v{{ record.version || 1 }}
            </template>
            <template v-else-if="column.key === 'scene'">
              {{ renderScene(record.scene) }}
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-space>
                <a-button type="link" size="small" @click="openHistory(record)">
                  <HistoryOutlined />
                </a-button>
                <a-button type="link" size="small" @click="openEdit(record)">
                  <EditOutlined />
                </a-button>
                <a-popconfirm title="确定删除吗？" ok-text="确认" cancel-text="取消" @confirm="() => handleDeleteOne(record.id)">
                  <a-button type="link" danger size="small">
                    <DeleteOutlined />
                  </a-button>
                </a-popconfirm>
              </a-space>
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
  </AstrsomnPageShell>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  DeleteOutlined,
  EditOutlined,
  HistoryOutlined,
  PlusOutlined,
  ReloadOutlined,
} from '@ant-design/icons-vue'
import AstrsomnPageShell from '@/components/home/AstrsomnPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'
import AstrsomnStateSwitch from '@/components/home/AstrsomnStateSwitch.vue'
import PromptFormModal from './PromptFormModal.vue'
import PromptHistoryModal from './PromptHistoryModal.vue'
import PromptCard from './PromptCard.vue'
import PromptSceneTagSelector from './component/PromptSceneTagSelector.vue'
import { aiPromptApi, type AiPrompt, type PageResponse } from '@/api/aiPrompt'

const PROMPT_CARD_MIN_WIDTH_PX = 320
const PROMPT_CARD_GAP_PX = 12
const promptCardMinWidth = `${PROMPT_CARD_MIN_WIDTH_PX}px`
const promptCardGap = `${PROMPT_CARD_GAP_PX}px`

const breadcrumbs = [
  { title: 'AI 配置', href: '/admin/ai-config' },
  { title: '提示词管理' },
]

type QueryState = {
  promptTitle?: string
  promptKey?: string
  sceneTags?: string[]
  envCode?: string
  createUser?: string
  status?: string
}

const query = reactive<QueryState>({})
const loading = ref(false)
const list = ref<AiPrompt[]>([])
const columns = [
    { title: 'Prompt Key', dataIndex: 'promptKey', key: 'promptKey', width: 180, ellipsis: true, copyable: true },
  { title: '标题', dataIndex: 'promptTitle', key: 'promptTitle', width: 220, ellipsis: true },
  { title: '场景', dataIndex: 'scene', key: 'scene', width: 140, ellipsis: true },
  { title: '环境', dataIndex: 'envCode', key: 'envCode', width: 120, ellipsis: true },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '版本', dataIndex: 'version', key: 'version', width: 90 },
  {title: '环境', dataIndex: 'envCode', key: 'envCode', width: 80, ellipsis: true, tag: true, tagColor: 'blue'},
  {title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 150, dateFormat: true},
  {title: '创建人', dataIndex: 'createUser', key: 'createUser', width: 150},
  { title: '操作', key: 'actions', width: 150, fixed: 'right' as const }
]

const page = reactive({
  pageNum: 1,
  pageSize: 8,
  total: 0
})

const pageRef = ref<HTMLElement | null>(null)
const viewMode = ref<'grid' | 'list'>('list')
const dataViewMode = computed<'card' | 'table'>(() => (viewMode.value === 'grid' ? 'card' : 'table'))
const currentGridColumns = ref(3)
const selectedRowKeys = ref<Array<number | string>>([])
const selectedKeySet = computed(() => new Set(selectedRowKeys.value))

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

const handleViewToggle = () => {
  viewMode.value = viewMode.value === 'grid' ? 'list' : 'grid'
}

const onPromptCardSelectChange = (id: number | string | undefined, checked: boolean) => {
  if (id == null) return
  if (checked) {
    if (!selectedRowKeys.value.includes(id)) {
      selectedRowKeys.value = [...selectedRowKeys.value, id]
    }
    return
  }
  selectedRowKeys.value = selectedRowKeys.value.filter((k) => k !== id)
}

const resetFilters = () => {
  query.promptTitle = undefined
  query.promptKey = undefined
  query.sceneTags = undefined
  query.envCode = undefined
  query.createUser = undefined
  query.status = undefined
  page.pageNum = 1
  void fetchList()
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
        title: '将删除选中项对应的 Prompt Key 下全部历史版本，确定吗？',
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
        scene: query.sceneTags?.[0] || undefined,
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

const renderScene = (scene?: string) => {
  if (!scene) {
    return '-'
  }
  try {
    const parsed = JSON.parse(scene)
    if (Array.isArray(parsed)) {
      return parsed.join(', ')
    }
  } catch (err) {}
  return scene
}

const resolveGridColumns = () => {
  if (typeof window === 'undefined') return 3
  const width = pageRef.value?.clientWidth ?? window.innerWidth
  const n = Math.floor((width + PROMPT_CARD_GAP_PX) / (PROMPT_CARD_MIN_WIDTH_PX + PROMPT_CARD_GAP_PX))
  return Math.max(1, Math.min(3, n))
}

const syncGridColumns = () => {
  currentGridColumns.value = resolveGridColumns()
}

let resizeObserver: ResizeObserver | null = null

onMounted(() => {
  syncGridColumns()
  if (typeof ResizeObserver !== 'undefined' && pageRef.value) {
    resizeObserver = new ResizeObserver(syncGridColumns)
    resizeObserver.observe(pageRef.value)
  } else {
    window.addEventListener('resize', syncGridColumns)
  }
  void fetchList()
})

onBeforeUnmount(() => {
  resizeObserver?.disconnect()
  window.removeEventListener('resize', syncGridColumns)
})
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
