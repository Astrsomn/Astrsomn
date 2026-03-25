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
            placeholder="Agent 名称（可选）"
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
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
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

      <a-modal
        v-model:open="modal.open"
        :title="modal.mode === 'create' ? '新增 Agent' : '编辑 Agent'"
        width="860px"
        @ok="handleSubmit"
        @cancel="closeModal"
        :confirm-loading="modal.submitting"
      >
        <a-form
          ref="formRef"
          :model="form"
          :rules="rules"
          layout="vertical"
        >
          <div class="form-grid">
            <a-form-item label="Agent 名称" name="agentName">
              <a-input v-model:value="form.agentName" placeholder="例如：General Assistant" />
            </a-form-item>

            <a-form-item label="Agent Key" name="agentKey">
              <a-input v-model:value="form.agentKey" placeholder="例如：general" />
            </a-form-item>

            <a-form-item label="默认模型 Key" name="modelKey">
              <a-input v-model:value="form.modelKey" placeholder="例如：1" />
            </a-form-item>

            <a-form-item label="Prompt Key" name="promptKey">
              <a-input v-model:value="form.promptKey" placeholder="例如：prompt-uuid" />
            </a-form-item>

            <a-form-item label="状态" name="status">
              <a-select v-model:value="form.status" :options="statusOptions" />
            </a-form-item>

            <a-form-item label="记忆模式" name="memoryMode">
              <a-select v-model:value="form.memoryMode" :options="memoryModeOptions" />
            </a-form-item>

            <a-form-item label="记忆窗口大小" name="memoryWindowSize">
              <a-input v-model:value="form.memoryWindowSize" placeholder="例如：10" />
            </a-form-item>

            <a-form-item label="是否启用流式输出" name="enableStream">
              <a-switch :checked="form.enableStream" @change="(v) => (form.enableStream = v)" />
            </a-form-item>

            <a-form-item label="描述" name="description" class="span-2">
              <a-textarea
                v-model:value="form.description"
                :auto-size="{ minRows: 2, maxRows: 5 }"
                placeholder="简要描述 Agent 的用途与能力"
              />
            </a-form-item>

            <a-form-item label="Config Params（JSON 字符串）" name="configParams" class="span-2">
              <a-textarea
                v-model:value="form.configParams"
                :auto-size="{ minRows: 3, maxRows: 7 }"
                placeholder='例如：{"temperature":0.7,"max_tokens":2048}'
              />
            </a-form-item>
          </div>
        </a-form>
      </a-modal>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import type { FormInstance } from 'ant-design-vue'
import AdminPageShell from '@/views/admin/components/AdminPageShell.vue'
import { aiAgentApi, type AiAgent, type PageResponse } from '@/api/aiAgent.ts'

type QueryState = {
  agentName?: string
  status?: string
}

const statusOptions = [
  { label: 'Enabled', value: 'enabled' },
  { label: 'Disabled', value: 'disabled' }
]

const memoryModeOptions = [
  { label: 'Short Term', value: 'shortTerm' },
  { label: 'Long Term', value: 'longTerm' },
  { label: 'Hybrid', value: 'hybrid' }
]

const renderStatus = (status: string) => {
  return statusOptions.find((x) => x.value === status)?.label ?? status
}

const columns = [
  { title: 'Agent 名称', dataIndex: 'agentName', key: 'agentName' },
  { title: 'Agent Key', dataIndex: 'agentKey', key: 'agentKey' },
  { title: '默认模型 Key', dataIndex: 'modelKey', key: 'modelKey' },
  { title: 'Prompt Key', dataIndex: 'promptKey', key: 'promptKey' },
  { title: '记忆模式', dataIndex: 'memoryMode', key: 'memoryMode' },
  { title: '状态', key: 'status' },
  { title: '流式输出', key: 'enableStream' },
  { title: '操作', key: 'actions' }
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

const formRef = ref<FormInstance | null>(null)

const form = reactive<AiAgent>({
  agentName: '',
  agentKey: '',
  description: '',
  modelKey: undefined,
  promptKey: '',
  configParams: '',
  status: 'enabled',
  memoryMode: 'hybrid',
  memoryWindowSize: '',
  enableStream: false
})

const resetForm = () => {
  Object.assign(form, {
    id: undefined,
    agentName: '',
    agentKey: '',
    description: '',
    modelKey: undefined,
    promptKey: '',
    configParams: '',
    status: 'enabled',
    memoryMode: 'hybrid',
    memoryWindowSize: '',
    enableStream: false
  })
}

const rules = {
  agentName: [{ required: true, message: '请输入 Agent 名称' }],
  agentKey: [{ required: true, message: '请输入 Agent Key' }],
  modelKey: [{ required: true, message: '请输入默认模型 Key' }],
  promptKey: [{ required: true, message: '请输入 Prompt Key' }],
  status: [{ required: true, message: '请选择状态' }]
}

const fetchList = async () => {
  const payload = {
    pageNo: page.pageNum,
    pageSize: page.pageSize,
    param: {
      // AiAgentMapper.xml 里用的是 req.name / req.status
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
  resetForm()
  modal.open = true
}

const openEdit = async (record: AiAgent) => {
  modal.mode = 'edit'
  resetForm()
  const id = record.id
  if (id == null) return

  const detail = await aiAgentApi.detail(id)
  Object.assign(form, detail)
  modal.open = true
}

const closeModal = () => {
  modal.open = false
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

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()

  modal.submitting = true
  try {
    const toNumberOrUndefined = (v: any) => {
      if (v === '' || v === undefined || v === null) return undefined
      const n = Number(v)
      return Number.isFinite(n) ? n : undefined
    }

    const payload: AiAgent = { ...form }
    payload.modelKey = toNumberOrUndefined(payload.modelKey)

    let msg: string
    if (modal.mode === 'create') {
      msg = await aiAgentApi.create(payload)
    } else {
      msg = await aiAgentApi.update(payload)
    }

    message.success(msg)
    modal.open = false
    void fetchList()
  } catch (e: any) {
    message.error(e?.message || '保存失败')
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

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px 16px;
}

.span-2 {
  grid-column: span 2;
}

@media (max-width: 1024px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
  .span-2 {
    grid-column: auto;
  }
}
</style>
