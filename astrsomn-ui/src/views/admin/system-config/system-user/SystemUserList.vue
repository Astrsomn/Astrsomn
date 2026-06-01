<template>
  <AstPageShell
      :breadcrumbs="breadcrumbs"
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


              <div :aria-label="t.list.roleFilterLabel" class="status-switch" role="group">
                <a-button
                    :class="{ active: query.userRole === 'SUPER_ADMIN' }"
                    class="status-btn"
                    @click="toggleRoleFilter('SUPER_ADMIN')"
                >
                  {{ t.list.roleSuperAdmin }}
                </a-button>
                <a-button
                    :class="{ active: query.userRole === 'ENV_ADMIN' }"
                    class="status-btn"
                    @click="toggleRoleFilter('ENV_ADMIN')"
                >
                  {{ t.list.roleEnvAdmin }}
                </a-button>
                <a-button
                    :class="{ active: query.userRole === 'USER' }"
                    class="status-btn"
                    @click="toggleRoleFilter('USER')"
                >
                  {{ t.list.roleUser }}
                </a-button>
              </div>
            </div>

            <div class="toolbar-right">

              <a-popconfirm
                  v-if="selectedRowKeys.length > 0"
                  :cancel-text="t.list.btnCancel"
                  :ok-text="t.list.btnConfirm"
                  :title="t.list.confirmBatchDelete"
                  @confirm="handleBatchDelete"
              >
                <a-button class="ghost-btn danger-btn" danger>
                  <template #icon>
                    <delete-outlined/>
                  </template>
                  {{ t.list.btnBatchDeleteCount.replace('{count}', String(selectedRowKeys.length)) }}
                </a-button>
              </a-popconfirm>
              <a-button v-else class="ghost-btn danger-btn" danger disabled>
                <template #icon>
                  <delete-outlined/>
                </template>
                {{ t.list.btnDelete }}
              </a-button>
              <a-button class="ghost-btn" @click="resetFilters">{{ t.list.btnReset }}</a-button>
              <a-button class="ghost-btn" @click="openCreate">
                <template #icon>
                  <plus-outlined/>
                </template>
                {{ t.list.btnCreate }}
              </a-button>
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
import {computed, reactive, ref} from 'vue'
import {message} from 'ant-design-vue'
import {DeleteOutlined, PlusOutlined,} from '@ant-design/icons-vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import AstDataSection from '@/components/home/AstDataSection.vue'
import AstDataView from '@/components/home/AstDataView.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import SystemUserForm from './component/SystemUserForm.vue'
import {type PageResponse, type SystemUser, systemUserApi} from '@/api/systemUser.ts'
import {usePageTranslation} from '@/locales/pages.ts'
import {getDictionary} from '@/locales/dictionary/registry.ts'

const t = usePageTranslation('system-user')
const roleDict = getDictionary('system.user.role')

const breadcrumbs = [
  {title: t.value.list.breadcrumbParent, href: '/admin/system-config'},
  {title: t.value.list.breadcrumb},
]

type QueryState = {
  username?: string
  email?: string
  userRole?: string
}

function roleLabel(code: string | undefined) {
  return roleDict.getLabel(code) ?? code ?? '—'
}

const columns = computed(() => [
  {title: t.value.list.columnUsername, dataIndex: 'username', key: 'username', width: 160, ellipsis: true},
  {title: t.value.list.columnRole, key: 'userRole', width: 120},
  {title: t.value.list.columnEnv, key: 'envCode', width: 88, ellipsis: true},
  {title: t.value.list.columnEmail, dataIndex: 'email', key: 'email', width: 200, ellipsis: true},
  {title: t.value.list.columnCreateTime, dataIndex: 'createTime', key: 'createTime', width: 150, dateFormat: true},
  {title: t.value.list.columnActions, key: 'actions', width: 160, fixed: 'right' as const}
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

const toggleRoleFilter = (value: 'SUPER_ADMIN' | 'ENV_ADMIN' | 'USER') => {
  query.userRole = query.userRole === value ? undefined : value
}

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
  const msg = await systemUserApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleFormSubmit = async (form: SystemUser) => {
  modal.submitting = true
  try {
    const payload: SystemUser = {...form}

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
  width: 200px;
}

.search-main-input {
  width: 260px;
}

.search-sub-input {
  width: 240px;
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

@media (max-width: 720px) {
  .toolbar-input,
  .search-main-input,
  .search-sub-input {
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
