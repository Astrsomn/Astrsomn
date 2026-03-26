<template>
  <AdminPageShell
    title="AI MCP"
    description="管理 MCP 服务接入（SSE / STDIO 等），对接 AiMcpController。"
    empty-text="暂无 MCP 服务。"
  >
    <div class="mcp-page">
      <div class="toolbar">
        <div class="toolbar-left">
          <a-input
            v-model:value="query.mcpKey"
            placeholder="MCP Key（精确）"
            class="toolbar-input"
            allow-clear
          />
          <a-select
            v-model:value="query.type"
            :options="typeFilterOptions"
            placeholder="类型"
            class="toolbar-select"
            allow-clear
          />
          <a-select
            v-model:value="query.enabled"
            :options="enabledFilterOptions"
            placeholder="启用状态"
            class="toolbar-select"
            allow-clear
          />
        </div>
        <div class="toolbar-right">
          <a-button type="primary" @click="fetchList">查询</a-button>
          <a-button @click="openCreate">新增</a-button>

          <a-popconfirm
            v-if="selectedRowKeys.length > 0"
            title="确定批量删除选中的 MCP 吗？"
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
        :scroll="{ x: 1100 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'enabled'">
            <span>{{ record.enabled === 1 ? '启用' : '停用' }}</span>
          </template>
          <template v-else-if="column.key === 'sseAddress'">
            <span class="ellipsis">{{ record.sseAddress || '—' }}</span>
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

      <McpFormModal
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
import McpFormModal from './McpFormModal.vue'
import { aiMcpApi, type AiMcp, type PageResponse } from '@/api/aiMcp.ts'

type QueryState = {
  mcpKey?: string
  type?: string
  enabled?: number
}

const typeFilterOptions = [
  { label: 'SSE', value: 'SSE' },
  { label: 'STDIO', value: 'STDIO' },
  { label: 'STEAMABLE', value: 'STEAMABLE' }
]

const enabledFilterOptions = [
  { label: '启用', value: 1 },
  { label: '停用', value: 0 }
]

const columns = [
  { title: 'MCP Key', dataIndex: 'mcpKey', key: 'mcpKey', width: 180, ellipsis: true },
  { title: '服务名', dataIndex: 'serverName', key: 'serverName', width: 160, ellipsis: true },
  { title: '类型', dataIndex: 'type', key: 'type', width: 100 },
  { title: 'SSE 地址', key: 'sseAddress', width: 220, ellipsis: true },
  { title: '启用', key: 'enabled', width: 80 },
  { title: '操作', key: 'actions', width: 160, fixed: 'right' as const }
]

const query = reactive<QueryState>({})
const list = ref<AiMcp[]>([])

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

const modalInitial = ref<AiMcp | null>(null)

const fetchList = async () => {
  const payload = {
    pageNo: page.pageNum,
    pageSize: page.pageSize,
    param: {
      mcpKey: query.mcpKey || undefined,
      type: query.type || undefined,
      enabled: query.enabled !== undefined && query.enabled !== null ? query.enabled : undefined
    }
  }

  const resp: PageResponse<AiMcp> = await aiMcpApi.queryPage(payload)
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

const openEdit = async (record: AiMcp) => {
  modal.mode = 'edit'
  const id = record.id
  if (id == null) return

  const detail = await aiMcpApi.detail(id)
  modalInitial.value = detail
  modal.open = true
}

const handleDeleteOne = async (id: number | string) => {
  if (id == null) return
  const msg = await aiMcpApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await aiMcpApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleFormSubmit = async (form: AiMcp) => {
  modal.submitting = true
  try {
    const payload: AiMcp = { ...form }
    if (payload.enabled !== undefined && payload.enabled !== null) {
      payload.enabled = Number(payload.enabled) === 0 ? 0 : 1
    }

    let msg: string
    if (modal.mode === 'create') {
      delete (payload as { id?: unknown }).id
      msg = await aiMcpApi.create(payload)
    } else {
      msg = await aiMcpApi.update(payload)
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
.mcp-page {
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
  width: 220px;
}

.toolbar-select {
  width: 160px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.ellipsis {
  display: inline-block;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: bottom;
}
</style>
