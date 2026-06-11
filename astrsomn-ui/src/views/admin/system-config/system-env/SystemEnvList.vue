<template>
  <AstPageShell
      :description="t.list.description"
      :empty-text="t.list.emptyText"
      :title="t.list.title"
  >
    <div class="env-page">
      <AstDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">

              <AstSearchInput
                  v-model="query.envName"
                  :button-label="t.list.searchButton"
                  layout="toolbar"
                  :placeholder="t.list.searchPlaceholder"
                  @search="fetchList"
              />
            </div>

            <div class="toolbar-right">
              <AstegmentedButton :buttons="actionButtons"/>
            </div>
          </div>
        </template>


        <AstDataView
            :columns="columns"
            :data-source="list"
            :loading="loading"
            :row-selection="rowSelection"
            :scroll="{ x: 800 }"
            :empty-text="t.list.emptyMatch"
            mode="table"
            row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'description'">
              <span class="desc-preview">{{ preview(record.description) }}</span>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-button type="link" @click="openEdit(record)">{{ t.list.btnEdit }}</a-button>
              <a-divider type="vertical"/>
              <a-popconfirm
                  :cancel-text="t.list.btnCancel"
                  :ok-text="t.list.btnConfirm"
                  :title="t.list.confirmDelete"
                  @confirm="() => handleDeleteOne(record.id)"
              >
                <a-button danger type="link">{{ t.list.btnDelete }}</a-button>
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

      <SystemEnvForm
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
import {message, Modal} from 'ant-design-vue'
import {DeleteOutlined, PlusOutlined} from '@ant-design/icons-vue'
import {usePageTranslation} from '@/locales/pages.ts'
import AstPageShell from '@/components/home/AstPageShell.vue'
import AstDataSection from '@/components/home/AstDataSection.vue'
import AstDataView from '@/components/home/AstDataView.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstegmentedButton, {type SegmentedButton} from '@/components/home/AstegmentedButton.vue'
import SystemEnvForm from './component/SystemEnvForm.vue'
import {type PageResponse, type SystemEnv, systemEnvApi} from '@/api/systemEnv.ts'

const t = usePageTranslation('system-env')

type QueryState = {
  envName?: string

  envCode?: string
}

const preview = (raw: string | undefined) => {
  if (!raw) return '—'
  const t = raw.replace(/\s+/g, ' ').trim()
  return t.length > 48 ? `${t.slice(0, 48)}…` : t
}

const columns = computed(() => [
  {title: t.value.list.columnEnvName, dataIndex: 'envName', key: 'envName', width: 180, ellipsis: true},
  {title: t.value.list.columnEnvKey, dataIndex: 'envKey', key: 'envKey', width: 140, ellipsis: true, copyable: true},
  {title: t.value.list.columnDescription, key: 'description', width: 260, ellipsis: true},
  {title: t.value.list.columnActions, key: 'actions', width: 160, fixed: 'right' as const}
])

const query = reactive<QueryState>({})
const list = ref<SystemEnv[]>([])
const loading = ref(false)

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const selectedRowKeys = ref<Array<number | string>>([])

const actionButtons = computed<SegmentedButton[]>(() => [
  {
    label: t.value.list.btnReset,
    type: 'primary',
    onClick: resetFilters,
    plain: true
  },
  {
    label: selectedRowKeys.value.length > 0 ? t.value.list.btnBatchDeleteCount.replace('{count}', String(selectedRowKeys.value.length)) : t.value.list.btnBatchDelete,
    type: 'danger',
    icon: DeleteOutlined,
    disabled: selectedRowKeys.value.length === 0,
    onClick: handleBatchDelete,
    plain: true
  },
  {
    label: t.value.list.btnCreate,
    type: 'primary',
    icon: PlusOutlined,
    onClick: openCreate,
    plain: false
  }
])

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
  query.envName = undefined
  query.envCode = undefined
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

const modal = reactive({
  open: false,
  mode: 'create' as 'create' | 'edit',
  submitting: false
})

const modalInitial = ref<SystemEnv | null>(null)

const fetchList = async () => {
  loading.value = true
  try {
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        envName: query.envName || undefined,
        envCode: query.envCode || undefined
      }
    }
    const resp: PageResponse<SystemEnv> = await systemEnvApi.queryPage(payload)
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

const openEdit = async (record: SystemEnv) => {
  modal.mode = 'edit'
  const id = record.id
  if (id == null) return

  const detail = await systemEnvApi.detail(id)
  modalInitial.value = detail
  modal.open = true
}

const handleDeleteOne = async (id: number | string) => {
  if (id == null) return
  const msg = await systemEnvApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return

  try {
    await new Promise<void>((resolve, reject) => {
      const modal = Modal.confirm({
        title: t.value.list.confirmBatchDelete,
        okText: t.value.list.btnConfirm,
        cancelText: t.value.list.btnCancel,
        onOk: () => resolve(),
        onCancel: () => reject(new Error(t.value.list.cancelDelete))
      })
    })

    const msg = await systemEnvApi.delete(ids)
    message.success(msg)
    selectedRowKeys.value = []
    void fetchList()
  } catch (error) {

  }
}

const handleFormSubmit = async (form: SystemEnv) => {
  modal.submitting = true
  try {
    const payload: SystemEnv = {...form}

    let msg: string
    if (modal.mode === 'create') {
      delete (payload as { id?: unknown }).id
      msg = await systemEnvApi.create(payload)
    } else {
      msg = await systemEnvApi.update(payload)
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

void fetchList()
</script>

<style scoped>
.env-page {
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

.search-cluster :deep(.ant-input-affix-wrapper) {
  border: none;
  box-shadow: none;
  background: transparent;
}

.search-cluster :deep(.ant-input-affix-wrapper:hover),
.search-cluster :deep(.ant-input-affix-wrapper-focused) {
  border: none;
  box-shadow: none;
  background: color-mix(in srgb, var(--bg-card) 85%, var(--bg-surface));
}

.toolbar-input {
  width: 240px;
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

.desc-preview {
  color: var(--text-muted);
  font-size: 12px;
}

@media (max-width: 720px) {
  .toolbar-input,
  .search-main-input,
  .search-sub-input {
    width: 100%;
  }

  .search-cluster {
    width: 100%;
    padding: 8px;
  }

  .toolbar-left,
  .toolbar-right {
    width: 100%;
  }
}
</style>
