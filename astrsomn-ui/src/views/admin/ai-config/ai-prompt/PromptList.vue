<template>
  <AstPageShell
      :show-view-toggle="true"
      :view-mode="viewMode"
      :view-toggle-handler="handleViewToggle"

      :empty-text="t.list.emptyText"

  >
    <div class="prompt-page">
      <AstDataSection>
        <template #toolbar>
          <div class="toolbar">
            <AstSearchInput
                v-model="query.promptTitle"
                :button-label="t.list.searchButton"
                layout="toolbar"
                :placeholder="t.list.searchPlaceholder"
                @search="fetchList"
            />
            <PromptSceneTagSelector v-model="query.sceneTags" @change="fetchList"/>
            <AstStatusSwitch v-model="query.status" @change="fetchList"/>
          </div>
        </template>


      <div v-if="dataViewMode === 'card'" class="prompt-grid-section">
        <a-spin :spinning="loading">
          <div class="prompt-grid">
            <div class="add-card" @click="openCreate">
              <PlusOutlined class="add-icon"/>
              <span class="add-text">{{ t.list.create }}</span>
            </div>
            <PromptCard
                v-for="record in list"
                :key="record.id"
                :record="record"
                :selected="record.id != null && selectedKeySet.has(record.id)"
                @delete="handleDeleteOne"
                @edit="openEdit"
                @toggle="onPromptCardSelectChange"
            />
          </div>
        </a-spin>
      </div>
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
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 'enabled' ? 'green' : 'default'">
              {{ record.status === 'enabled' ? t.list.status.enabled : t.list.status.disabled }}
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
              <a-button size="small" type="link" @click="openHistory(record)">
                <HistoryOutlined/>
              </a-button>
              <a-button size="small" type="link" @click="openEdit(record)">
                <EditOutlined/>
              </a-button>
              <a-popconfirm :cancel-text="t.list.cancel" :ok-text="t.list.confirm" :title="t.list.deleteConfirm"
                            @confirm="() => handleDeleteOne(record.id)">
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

      <PromptFormModal
          v-model:open="modal.open"
          :confirm-loading="modal.submitting"
          :initial="modalInitial"
          :mode="modal.mode"
          @submit="handleFormSubmit"
      />

      <PromptHistoryModal
          v-model:open="historyModal.open"
          :env-code="historyModal.envCode"
          :prompt-key="historyModal.promptKey"
      />
    </div>
  </AstPageShell>
</template>

<script lang="ts" setup>
import {computed, reactive, ref} from 'vue'
import {message} from 'ant-design-vue'
import {DeleteOutlined, EditOutlined, HistoryOutlined, PlusOutlined} from '@ant-design/icons-vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import AstDataSection from '@/components/home/AstDataSection.vue'
import AstDataView from '@/components/home/AstDataView.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstStatusSwitch from '@/components/home/AstStatusSwitch.vue'
import PromptFormModal from './component/PromptFormModal.vue'
import PromptHistoryModal from './component/PromptHistoryModal.vue'
import PromptCard from './component/PromptCard.vue'
import PromptSceneTagSelector from './component/PromptSceneTagSelector.vue'
import {type AiPrompt, aiPromptApi, type PageResponse} from '@/api/aiPrompt'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-prompt')

const props = withDefaults(defineProps<{
  initialViewMode?: 'grid' | 'list'
}>(), {
  initialViewMode: 'list'
})



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
const columns = computed(() => [
  {title: 'Prompt Key', dataIndex: 'promptKey', key: 'promptKey', width: 180, ellipsis: true, copyable: true},
  {title: t.value.list.column.promptTitle, dataIndex: 'promptTitle', key: 'promptTitle', width: 220, ellipsis: true},
  {title: t.value.list.column.scene, dataIndex: 'scene', key: 'scene', width: 140, ellipsis: true},
  {title: t.value.list.column.envCode, dataIndex: 'envCode', key: 'envCode', width: 120, ellipsis: true},
  {title: t.value.list.column.status, dataIndex: 'status', key: 'status', width: 100},
  {title: t.value.list.column.version, dataIndex: 'version', key: 'version', width: 90},
  {title: t.value.list.column.envCode, dataIndex: 'envCode', key: 'envCode', width: 80, ellipsis: true, tag: true, tagColor: 'blue'},
  {title: t.value.list.column.createTime, dataIndex: 'createTime', key: 'createTime', width: 150, dateFormat: true},
  {title: t.value.list.column.createUser, dataIndex: 'createUser', key: 'createUser', width: 150},
  {title: t.value.list.column.actions, key: 'actions', width: 150, fixed: 'right' as const}
])

const page = reactive({
  pageNum: 1,
  pageSize: 8,
  total: 0
})

const viewMode = ref<'grid' | 'list'>(props.initialViewMode)
const dataViewMode = computed<'card' | 'table'>(() => (viewMode.value === 'grid' ? 'card' : 'table'))
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
    const payload: AiPrompt = {...form}
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
    message.error(err?.message || t.value.list.saveFailed)
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
  } catch (err) {
  }
  return scene
}

void fetchList()
</script>

<style scoped>
.prompt-page {
  padding: 20px;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 14px;
  flex-wrap: wrap;
}

/* ── Card Grid (matching AgentSection.vue) ── */
.prompt-grid-section {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.prompt-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 14px;
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
</style>
