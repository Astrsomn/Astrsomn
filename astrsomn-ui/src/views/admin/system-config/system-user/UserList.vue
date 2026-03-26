<template>
  <AdminPageShell
    title="用户管理"
    description="管理系统用户（SYSTEM_USER），对接 SystemUserController。"
    empty-text="暂无用户数据。"
  >
    <div class="user-page">
      <div class="toolbar">
        <div class="toolbar-left">
          <a-input
            v-model:value="query.username"
            placeholder="用户名（模糊）"
            class="toolbar-input"
            allow-clear
          />
          <a-input
            v-model:value="query.email"
            placeholder="邮箱（模糊）"
            class="toolbar-input"
            allow-clear
          />
          <a-select
            v-model:value="query.adminFlag"
            :options="adminFilterOptions"
            placeholder="管理员"
            class="toolbar-select"
            allow-clear
          />
        </div>
        <div class="toolbar-right">
          <a-button type="primary" @click="fetchList">查询</a-button>
          <a-button @click="openCreate">新增</a-button>

          <a-popconfirm
            v-if="selectedRowKeys.length > 0"
            title="确定批量删除选中的用户吗？"
            ok-text="确认"
            cancel-text="取消"
            @confirm="handleBatchDelete"
          >
            <a-button danger>批量删除</a-button>
          </a-popconfirm>
        </div>
      </div>

      <a-table
        :columns="columns"
        :data-source="list"
        :pagination="false"
        row-key="id"
        :row-selection="rowSelection"
        :scroll="{ x: 900 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'adminFlag'">
            <span>{{ record.adminFlag === 'Y' ? '是' : '否' }}</span>
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
      </a-table>

      <div class="pagination-wrap">
        <a-pagination
          :current="page.pageNum"
          :page-size="page.pageSize"
          :total="page.total"
          :show-size-changer="false"
          @change="onPageChange"
        />
      </div>

      <UserFormModal
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
import AdminPageShell from '@/views/admin/components/AdminPageShell.vue'
import UserFormModal from './UserFormModal.vue'
import { systemUserApi, type SystemUser, type PageResponse } from '@/api/systemUser.ts'

type QueryState = {
  username?: string
  email?: string
  adminFlag?: string
}

const adminFilterOptions = [
  { label: '管理员', value: 'Y' },
  { label: '普通', value: 'N' }
]

const columns = [
  { title: '用户名', dataIndex: 'username', key: 'username', width: 160, ellipsis: true },
  { title: '邮箱', dataIndex: 'email', key: 'email', width: 220, ellipsis: true },
  { title: '管理员', key: 'adminFlag', width: 90 },
  { title: '操作', key: 'actions', width: 160, fixed: 'right' as const }
]

const query = reactive<QueryState>({})
const list = ref<SystemUser[]>([])

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const selectedRowKeys = ref<Array<number | string>>([])

const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys: Array<number | string>) => {
    selectedRowKeys.value = keys
  }
}))

const modal = reactive({
  open: false,
  mode: 'create' as 'create' | 'edit',
  submitting: false
})

const modalInitial = ref<SystemUser | null>(null)

const fetchList = async () => {
  const payload = {
    pageNo: page.pageNum,
    pageSize: page.pageSize,
    param: {
      username: query.username || undefined,
      email: query.email || undefined,
      adminFlag: query.adminFlag || undefined
    }
  }

  const resp: PageResponse<SystemUser> = await systemUserApi.queryPage(payload)
  list.value = resp.list || []
  page.total = resp.total || 0
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
    message.error(err?.message || '保存失败')
  } finally {
    modal.submitting = false
  }
}

void fetchList()
</script>

<style scoped>
.user-page {
  padding: 0 4px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.toolbar-left {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.toolbar-right {
  display: flex;
  gap: 12px;
  align-items: center;
}

.toolbar-input {
  width: 200px;
}

.toolbar-select {
  width: 160px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
