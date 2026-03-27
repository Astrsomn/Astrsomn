<template>
  <AdminPageShell
    title="智能体管理"
    description="管理 Agent 配置、执行策略与发布状态。"
    empty-text="暂无智能体，请先创建。"
  >
    <div class="agent-page">
      <div class="toolbar">
        <div class="toolbar-left">
          <a-input
            v-model:value="query.agentName"
            placeholder="名称（可选）"
            class="toolbar-input"
            allow-clear
          />
          <a-select
            v-model:value="query.status"
            :options="statusOptions"
            placeholder="状态（可选）"
            class="toolbar-select"
            allow-clear
          />
        </div>
        <div class="toolbar-right">
          <a-button type="primary" @click="fetchList">查询</a-button>
          <a-button @click="openCreate">新增</a-button>

          <a-popconfirm
            v-if="selectedRowKeys.length > 0"
            title="确定批量删除选中的 Agent 吗？"
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
        :scroll="{ x: 980 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'modelCell'">
            <div class="cell-stack">
              <div class="line-main">{{ displayLine(record.modelName) }}</div>
              <div class="line-sub">{{ displayLine(record.modelKey) }}</div>
            </div>
          </template>
          <template v-else-if="column.key === 'promptCell'">
            <div class="cell-stack">
              <div class="line-main">{{ displayLine(record.promptTitle) }}</div>
              <div class="line-sub">{{ displayLine(record.promptKey) }}</div>
            </div>
          </template>
          <template v-else-if="column.key === 'status'">
            <span>{{ renderStatus(String(record.status || '')) }}</span>
          </template>
          <template v-else-if="column.key === 'enableStream'">
            <span>{{ record.enableStream ? '是' : '否' }}</span>
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

      <AgentFormModal
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
import AgentFormModal from './AgentFormModal.vue'
import { aiAgentApi, type AiAgent, type PageResponse } from '@/api/aiAgent.ts'

type QueryState = {
  agentName?: string
  status?: string
}

const statusOptions = [
  { label: '启用', value: 'enabled' },
  { label: '停用', value: 'disabled' }
]

const renderStatus = (status: string) => {
  return statusOptions.find((x) => x.value === status)?.label ?? status
}

/** 列表展示：空值统一为 em dash */
function displayLine(v: unknown) {
  if (v == null) return '—'
  const t = String(v).trim()
  return t ? t : '—'
}

const columns = [
  { title: '名称', dataIndex: 'agentName', key: 'agentName', width: 168, ellipsis: true },
  { title: '标识', dataIndex: 'agentKey', key: 'agentKey', width: 140, ellipsis: true },
  { title: '模型', key: 'modelCell', width: 200, ellipsis: true },
  { title: '提示词', key: 'promptCell', width: 220, ellipsis: true },
  { title: '状态', key: 'status', width: 72 },
  { title: '流式', key: 'enableStream', width: 56 },
  { title: '操作', key: 'actions', width: 148, fixed: 'right' as const }
]

const query = reactive<QueryState>({})
const list = ref<AiAgent[]>([])

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

const modalInitial = ref<AiAgent | null>(null)

const fetchList = async () => {
  const payload = {
    pageNo: page.pageNum,
    pageSize: page.pageSize,
    param: {
      name: query.agentName || undefined,
      status: query.status || undefined
    }
  }

  const resp: PageResponse<AiAgent> = await aiAgentApi.queryPage(payload)
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

const openEdit = async (record: AiAgent) => {
  modal.mode = 'edit'
  const id = record.id
  if (id == null) return

  const detail = await aiAgentApi.detail(id)
  modalInitial.value = detail
  modal.open = true
}

const handleDeleteOne = async (id: number | string) => {
  if (id == null) return
  const msg = await aiAgentApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await aiAgentApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleFormSubmit = async (form: AiAgent) => {
  modal.submitting = true
  try {
    const payload: AiAgent = { ...form }
    // modelKey 为业务字符串（AI_MODEL.MODEL_KEY），不可做 Number()，否则如 qwen-max 会丢失
    const mk = payload.modelKey
    payload.modelKey =
      mk != null && String(mk).trim() !== '' ? String(mk).trim() : undefined

    let msg: string
    if (modal.mode === 'create') {
      msg = await aiAgentApi.create(payload)
    } else {
      msg = await aiAgentApi.update(payload)
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
.agent-page {
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
  width: 240px;
}

.toolbar-select {
  width: 180px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.cell-stack {
  line-height: 1.35;
}
.cell-stack .line-main {
  font-size: 13px;
  color: rgba(0, 0, 0, 0.88);
}
.cell-stack .line-sub {
  font-size: 12px;
  color: #8c8c8c;
  margin-top: 2px;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}
</style>
