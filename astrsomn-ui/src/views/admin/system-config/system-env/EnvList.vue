<template>
  <AdminPageShell
    title="环境管理"
    description="管理运行环境（SYSTEM_ENV），对接 SystemEnvController。"
    empty-text="暂无环境配置。"
    :breadcrumbs="breadcrumbs"
  >
    <div class="env-page">
      <AstrsomnDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
  
            <AstrsomnSearchPill
              v-model="query.envName"
              placeholder="搜索环境名称"
              button-label="搜索"
              layout="toolbar"
              @search="fetchList"
            />
            </div>

            <div class="toolbar-right">
          <AstrsomnSegmentedButton :buttons="actionButtons" />
            </div>
          </div>
        </template>



        <AstrsomnDataView
          mode="table"
          :data-source="list"
          :loading="loading"
          :columns="columns"
          :row-selection="rowSelection"
          :scroll="{ x: 800 }"
          row-key="id"
          empty-text="暂无匹配的环境"
        >
          <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'description'">
            <span class="desc-preview">{{ preview(record.description) }}</span>
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

      <EnvFormModal
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
import { message, Modal } from 'ant-design-vue'
import {
  DeleteOutlined,
  PlusOutlined
} from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'
import EnvFormModal from './EnvFormModal.vue'
import { systemEnvApi, type SystemEnv, type PageResponse } from '@/api/systemEnv.ts'

const breadcrumbs = [
  { title: '系统配置', href: '/admin/system-config' },
  { title: '环境管理' },
]

type QueryState = {
  envName?: string
  /** 对应后端 Query DTO 的 envCode，匹配 ENV_KEY */
  envCode?: string
}

const preview = (raw: string | undefined) => {
  if (!raw) return '—'
  const t = raw.replace(/\s+/g, ' ').trim()
  return t.length > 48 ? `${t.slice(0, 48)}…` : t
}

const columns = [
  { title: '环境名称', dataIndex: 'envName', key: 'envName', width: 180, ellipsis: true },
  { title: '环境 Key', dataIndex: 'envKey', key: 'envKey', width: 140, ellipsis: true, copyable: true },
  { title: '描述', key: 'description', width: 260, ellipsis: true },
  { title: '操作', key: 'actions', width: 160, fixed: 'right' as const }
]

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
    label: '重置',
    type: 'primary',
    onClick: resetFilters,
    plain: true
  },
  {
    label: selectedRowKeys.value.length > 0 ? `删除 (${selectedRowKeys.value.length})` : '删除',
    type: 'danger',
    icon: DeleteOutlined,
    disabled: selectedRowKeys.value.length === 0,
    onClick: handleBatchDelete,
    plain: true
  },
  {
    label: '新增',
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
        title: '确定批量删除选中的环境吗？',
        okText: '确认',
        cancelText: '取消',
        onOk: () => resolve(),
        onCancel: () => reject(new Error('取消删除'))
      })
    })
    
    const msg = await systemEnvApi.delete(ids)
    message.success(msg)
    selectedRowKeys.value = []
    void fetchList()
  } catch (error) {
    // 用户取消删除，不执行任何操作
  }
}

const handleFormSubmit = async (form: SystemEnv) => {
  modal.submitting = true
  try {
    const payload: SystemEnv = { ...form }

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
    message.error(err?.message || '保存失败')
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
  color: rgba(0, 0, 0, 0.45);
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
