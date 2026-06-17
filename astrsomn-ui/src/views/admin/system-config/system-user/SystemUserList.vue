<template>
  <AstPageShell
      :description="t.list.description"
      :empty-text="t.list.emptyText"
      :title="t.list.title"
  >
    <div class="user-page">
      <AstDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstSearchInput
                  v-model="query.username"
                  :placeholder="t.list.searchPlaceholder"
                  @search="fetchList"
              />

              <AstegmentedButton :buttons="roleFilterButtons" />
            </div>

            <div class="toolbar-right">
              <AstegmentedButton :buttons="actionButtons" />
            </div>
          </div>
        </template>


        <AstDataView
            :columns="columns"
            :data-source="list"
            :loading="loading"
            :row-selection="rowSelection"
            :scroll="{ x: 1020 }"
            :empty-text="t.list.emptyMatch"
            mode="table"
            row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'userRole'">
              <span>{{ roleLabel(record.userRole) }}</span>
            </template>
            <template v-else-if="column.key === 'envCode'">
              <span>{{ record.envCode || '—' }}</span>
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

      <SystemUserForm
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
import { computed, reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { DeleteOutlined, PlusOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import AstDataSection from '@/components/home/AstDataSection.vue'
import AstDataView from '@/components/home/AstDataView.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstegmentedButton from '@/components/home/AstegmentedButton.vue'
import type { SegmentedButton } from '@/components/home/AstegmentedButton.vue'
import SystemUserForm from './component/SystemUserForm.vue'
import { type PageResponse, type SystemUser, systemUserApi } from '@/api/systemUser.ts'
import { usePageTranslation } from '@/locales/pages.ts'
import { getDictionary } from '@/locales/dictionary/registry.ts'

const t = usePageTranslation('system-user')
const tCommon = usePageTranslation('common')
const roleDict = getDictionary('system.user.role')

type QueryState = {
  username?: string
  email?: string
  userRole?: string
}

function roleLabel(code: string | undefined) {
  return roleDict.getLabel(code) ?? code ?? '—'
}

const columns = computed(() => [
  { title: t.value.list.columnUsername, dataIndex: 'username', key: 'username', width: 160, ellipsis: true },
  { title: t.value.list.columnRole, key: 'userRole', width: 120 },
  { title: t.value.list.columnEnv, key: 'envCode', width: 88, ellipsis: true },
  { title: t.value.list.columnEmail, dataIndex: 'email', key: 'email', width: 200, ellipsis: true },
  { title: t.value.list.columnCreateTime, dataIndex: 'createTime', key: 'createTime', width: 150, dateFormat: true },
  { title: t.value.list.columnActions, key: 'actions', width: 160, fixed: 'right' as const }
])

const query = reactive<QueryState>({})
const list = ref<SystemUser[]>([])
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

// ---- role filter buttons ----
const toggleRoleFilter = (value: 'SUPER_ADMIN' | 'ENV_ADMIN' | 'USER') => {
  query.userRole = query.userRole === value ? undefined : value
  page.pageNum = 1
  void fetchList()
}

const roleFilterButtons = computed<SegmentedButton[]>(() => [
  {
    label: tCommon.value.statusSwitch.all,
    type: !query.userRole ? 'primary' : 'default',
    plain: !!query.userRole,
    onClick: () => {
      query.userRole = undefined
      page.pageNum = 1
      void fetchList()
    }
  },
  {
    label: t.value.list.roleSuperAdmin,
    type: query.userRole === 'SUPER_ADMIN' ? 'primary' : 'default',
    plain: query.userRole !== 'SUPER_ADMIN',
    onClick: () => toggleRoleFilter('SUPER_ADMIN')
  },
  {
    label: t.value.list.roleEnvAdmin,
    type: query.userRole === 'ENV_ADMIN' ? 'primary' : 'default',
    plain: query.userRole !== 'ENV_ADMIN',
    onClick: () => toggleRoleFilter('ENV_ADMIN')
  },
  {
    label: t.value.list.roleUser,
    type: query.userRole === 'USER' ? 'primary' : 'default',
    plain: query.userRole !== 'USER',
    onClick: () => toggleRoleFilter('USER')
  }
])

// ---- action buttons ----
const actionButtons = computed<SegmentedButton[]>(() => [
  {
    label: selectedRowKeys.value.length > 0
        ? t.value.list.btnBatchDeleteCount.replace('{count}', String(selectedRowKeys.value.length))
        : t.value.list.btnDelete,
    type: 'danger',
    icon: DeleteOutlined,
    disabled: selectedRowKeys.value.length === 0,
    plain: true,
    onClick: handleBatchDelete
  },
  {
    label: t.value.list.btnReset,
    type: 'primary',
    icon: ReloadOutlined,
    plain: true,
    onClick: resetFilters
  },
  {
    label: t.value.list.btnCreate,
    type: 'primary',
    icon: PlusOutlined,
    onClick: openCreate
  }
])

const resetFilters = () => {
  query.username = undefined
  query.email = undefined
  query.userRole = undefined
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

const modal = reactive({
  open: false,
  mode: 'create' as 'create' | 'edit',
  submitting: false
})

const modalInitial = ref<SystemUser | null>(null)

const fetchList = async () => {
  loading.value = true
  try {
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        username: query.username || undefined,
        email: query.email || undefined,
        userRole: query.userRole || undefined
      }
    }
    const resp: PageResponse<SystemUser> = await systemUserApi.queryPage(payload)
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

const openEdit = async (record: SystemUser) => {
  modal.mode = 'edit'
  const id = record.id
  if (id == null) return

  const detail = await systemUserApi.detail(id)
  modalInitial.value = detail
  modal.open = true
}

const handleDeleteOne = async (id: number | string) => {
  if (id == null) return
  const msg = await systemUserApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  Modal.confirm({
    title: t.value.list.confirmBatchDelete,
    okText: t.value.list.btnConfirm,
    cancelText: t.value.list.btnCancel,
    onOk: async () => {
      const msg = await systemUserApi.delete(ids)
      message.success(msg)
      selectedRowKeys.value = []
      void fetchList()
    }
  })
}

const handleFormSubmit = async (form: SystemUser) => {
  modal.submitting = true
  try {
    const payload: SystemUser = { ...form }

    let msg: string
    if (modal.mode === 'create') {
      delete (payload as { id?: unknown }).id
      msg = await systemUserApi.create(payload)
    } else {
      msg = await systemUserApi.update(payload)
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

const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys: Array<number | string>) => {
    selectedRowKeys.value = keys
  }
}))

void fetchList()
</script>

<style scoped>
.user-page {
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

@media (max-width: 720px) {
  .toolbar-left,
  .toolbar-right {
    width: 100%;
  }
}
</style>
