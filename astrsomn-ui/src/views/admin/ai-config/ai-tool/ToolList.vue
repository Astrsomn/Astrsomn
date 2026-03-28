<template>
  <AdminPageShell
    title="AI Tools"
    description="管理本地工具与调用配置（AI_TOOL），对接 AiToolController。"
    empty-text="暂无可用工具。"
  >
    <div class="tool-page">
      <div class="toolbar">
        <div class="toolbar-left">
          <a-input
            v-model:value="query.toolName"
            placeholder="工具名称（模糊）"
            class="toolbar-input"
            allow-clear
          />
          <a-input
            v-model:value="query.toolKey"
            placeholder="Tool Key（精确）"
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
            v-model:value="query.enableFlag"
            :options="enableFilterOptions"
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
            title="确定批量删除选中的工具吗？"
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
        :scroll="{ x: 1180 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'enableFlag'">
            <span>{{ renderEnable(String(record.enableFlag || '')) }}</span>
          </template>
          <template v-else-if="column.key === 'description'">
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

      <ToolFormModal
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
import AdminPageShell from '@/views/admin/components/admin/AdminPageShell.vue'
import ToolFormModal from './ToolFormModal.vue'
import { aiToolApi, type AiTool, type PageResponse } from '@/api/aiTool.ts'

type QueryState = {
  toolName?: string
  toolKey?: string
  type?: string
  enableFlag?: string
}

const typeFilterOptions = [
  { label: 'HTML', value: 'html' },
  { label: 'Method', value: 'method' }
]

const enableFilterOptions = [
  { label: '启用', value: 'enabled' },
  { label: '停用', value: 'disabled' }
]

const renderEnable = (f: string) => enableFilterOptions.find((x) => x.value === f)?.label ?? f

const preview = (raw: string | undefined) => {
  if (!raw) return '—'
  const t = raw.replace(/\s+/g, ' ').trim()
  return t.length > 48 ? `${t.slice(0, 48)}…` : t
}

const columns = [
  { title: 'Tool Key', dataIndex: 'toolKey', key: 'toolKey', width: 180, ellipsis: true },
  { title: '名称', dataIndex: 'toolName', key: 'toolName', width: 140, ellipsis: true },
  { title: '类型', dataIndex: 'type', key: 'type', width: 90 },
  { title: 'Bean', dataIndex: 'beanName', key: 'beanName', width: 140, ellipsis: true },
  { title: '方法', dataIndex: 'methodName', key: 'methodName', width: 120, ellipsis: true },
  { title: '描述', key: 'description', width: 200, ellipsis: true },
  { title: '状态', key: 'enableFlag', width: 80 },
  { title: '操作', key: 'actions', width: 160, fixed: 'right' as const }
]

const query = reactive<QueryState>({})
const list = ref<AiTool[]>([])

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

const modalInitial = ref<AiTool | null>(null)

const fetchList = async () => {
  const payload = {
    pageNo: page.pageNum,
    pageSize: page.pageSize,
    param: {
      toolName: query.toolName || undefined,
      toolKey: query.toolKey || undefined,
      type: query.type || undefined,
      enableFlag: query.enableFlag || undefined
    }
  }

  const resp: PageResponse<AiTool> = await aiToolApi.queryPage(payload)
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

const openEdit = async (record: AiTool) => {
  modal.mode = 'edit'
  const id = record.id
  if (id == null) return

  const detail = await aiToolApi.detail(id)
  modalInitial.value = detail
  modal.open = true
}

const handleDeleteOne = async (id: number | string) => {
  if (id == null) return
  const msg = await aiToolApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await aiToolApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleFormSubmit = async (form: AiTool) => {
  modal.submitting = true
  try {
    const payload: AiTool = { ...form }

    let msg: string
    if (modal.mode === 'create') {
      delete (payload as { id?: unknown }).id
      msg = await aiToolApi.create(payload)
    } else {
      msg = await aiToolApi.update(payload)
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
.tool-page {
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

.desc-preview {
  color: rgba(0, 0, 0, 0.45);
  font-size: 12px;
}
</style>
