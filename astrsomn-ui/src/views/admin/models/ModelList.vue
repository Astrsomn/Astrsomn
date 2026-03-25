<template>
  <AdminPageShell
    title="模型配置"
    description="管理模型供应商、模型版本与默认路由策略。"
    empty-text="暂无模型配置。"
  >
    <div class="model-page">
      <div class="toolbar">
        <div class="toolbar-left">
          <a-input
            v-model:value="query.modelName"
            placeholder="模型名称（可选）"
            class="toolbar-input"
            allow-clear
          />
          <a-input
            v-model:value="query.modelKey"
            placeholder="模型 Key（可选）"
            class="toolbar-input"
            allow-clear
          />
          <a-select
            v-model:value="query.provider"
            :options="providerOptions"
            placeholder="供应商（可选）"
            class="toolbar-select"
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
            title="确定批量删除选中的模型吗？"
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
        <template #bodyCell="{ column, record, text }">
          <template v-if="column.key === 'status'">
            <span>{{ renderStatus(String(record.status || '')) }}</span>
          </template>
          <template v-else-if="column.key === 'isDefault'">
            <span>{{ record.isDefault === 1 ? '是' : '否' }}</span>
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
        :title="modal.mode === 'create' ? '新增模型' : '编辑模型'"
        width="820px"
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
            <a-form-item label="模型名称" name="modelName">
              <a-input v-model:value="form.modelName" placeholder="例如：gpt-4o-mini" />
            </a-form-item>

            <a-form-item label="模型 Key" name="modelKey">
              <a-input v-model:value="form.modelKey" placeholder="例如：gpt4o_mini" />
            </a-form-item>

            <a-form-item label="模型类型" name="modelType">
              <a-input v-model:value="form.modelType" placeholder="例如：chat / embedding" />
            </a-form-item>

            <a-form-item label="供应商" name="provider">
              <a-select
                v-model:value="form.provider"
                :options="providerOptions"
                placeholder="请选择供应商"
              />
            </a-form-item>

            <a-form-item label="API URL" name="apiUrl">
              <a-input v-model:value="form.apiUrl" placeholder="例如：https://api.xx.com/v1" />
            </a-form-item>

            <a-form-item label="状态" name="status">
              <a-select v-model:value="form.status" :options="statusOptions" />
            </a-form-item>

            <a-form-item label="是否默认" name="isDefault">
              <a-select
                v-model:value="form.isDefault"
                :options="isDefaultOptions"
                placeholder="请选择"
              />
            </a-form-item>

            <a-form-item label="响应限制" name="responseLimit">
              <a-input
                v-model:value="form.responseLimit"
                placeholder="0 - 8192"
              />
            </a-form-item>

            <a-form-item label="API Key" name="apiKey">
              <a-input v-model:value="form.apiKey" placeholder="（可选）" />
            </a-form-item>

            <a-form-item label="API Secret" name="apiSecret">
              <a-input v-model:value="form.apiSecret" placeholder="（可选）" />
            </a-form-item>

            <a-form-item label="模型参数（JSON 字符串）" name="modelParams" class="span-2">
              <a-textarea
                v-model:value="form.modelParams"
                :auto-size="{ minRows: 3, maxRows: 6 }"
                placeholder='例如：{"temperature":0.7}'
              />
            </a-form-item>

            <a-form-item label="Capabilities（JSON 字符串）" name="capabilities" class="span-2">
              <a-textarea
                v-model:value="form.capabilities"
                :auto-size="{ minRows: 3, maxRows: 6 }"
                placeholder='例如：{"text_generation":true}'
              />
            </a-form-item>

            <a-form-item label="随机索引" name="randomIndex">
              <a-input v-model:value="form.randomIndex" placeholder="（可选）" />
            </a-form-item>

            <a-form-item label="Top 方差" name="topVariance">
              <a-input v-model:value="form.topVariance" placeholder="（可选）" />
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
import AdminPageShell from '@/views/admin/components/AdminPageShell.vue'
import { aiModelApi, type AiModel, type PageResponse } from '@/api/aiModel'

import type { FormInstance } from 'ant-design-vue'

type QueryState = {
  modelName?: string
  modelKey?: string
  provider?: string
  status?: string
}

const providerOptions = [
  { label: 'OpenAI', value: 'openai' },
  { label: 'xAI', value: 'xai' },
  { label: 'Anthropic', value: 'anthropic' },
  { label: 'Google', value: 'google' },
  { label: 'Alibaba', value: 'alibaba' },
  { label: 'ZhiPu', value: 'zhipu' },
  { label: 'Moonshot', value: 'moonshot' },
  { label: 'Baidu', value: 'baidu' },
  { label: 'Baichuan', value: 'baichuan' },
  { label: 'MiniMax', value: 'minimax' },
  { label: 'Yi', value: 'yi' },
  { label: 'Siliconflow', value: 'siliconflow' },
  { label: 'Tencent', value: 'tencent' },
  { label: 'DeepSeek', value: 'deepseek' },
  { label: 'Ollama', value: 'ollama' },
  { label: 'Qianfan', value: 'qianfan' }
]

const statusOptions = [
  { label: 'Enabled', value: 'enabled' },
  { label: 'Disabled', value: 'disabled' }
]

const isDefaultOptions = [
  { label: '否', value: 0 },
  { label: '是', value: 1 }
]

const renderStatus = (status: string) => {
  return statusOptions.find((x) => x.value === status)?.label ?? status
}

const columns = [
  { title: '模型名称', dataIndex: 'modelName', key: 'modelName' },
  { title: '模型 Key', dataIndex: 'modelKey', key: 'modelKey' },
  { title: '模型类型', dataIndex: 'modelType', key: 'modelType' },
  { title: '供应商', dataIndex: 'provider', key: 'provider' },
  { title: '状态', key: 'status' },
  { title: '是否默认', key: 'isDefault' },
  { title: '响应限制', dataIndex: 'responseLimit', key: 'responseLimit' },
  { title: '操作', key: 'actions' }
]

const query = reactive<QueryState>({})
const list = ref<AiModel[]>([])

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

const form = reactive<AiModel>({
  modelName: '',
  modelKey: '',
  modelType: '',
  provider: '',
  apiUrl: '',
  status: 'enabled',
  isDefault: 0,
  responseLimit: 0,
  apiKey: '',
  apiSecret: '',
  modelParams: '',
  capabilities: '',
  randomIndex: 0,
  topVariance: 0
})

const resetForm = () => {
  Object.assign(form, {
    id: undefined,
    modelName: '',
    modelKey: '',
    modelType: '',
    provider: '',
    apiUrl: '',
    status: 'enabled',
    isDefault: 0,
    responseLimit: 0,
    apiKey: '',
    apiSecret: '',
    modelParams: '',
    capabilities: '',
    randomIndex: 0,
    topVariance: 0
  })
}

const rules = {
  modelName: [{ required: true, message: '请输入模型名称' }],
  modelKey: [{ required: true, message: '请输入模型 Key' }],
  modelType: [{ required: true, message: '请输入模型类型' }],
  provider: [{ required: true, message: '请选择供应商' }],
  apiUrl: [{ required: true, message: '请输入 API URL' }],
  status: [{ required: true, message: '请选择状态' }]
}

const fetchList = async () => {
  const payload = {
    pageNo: page.pageNum,
    pageSize: page.pageSize,
    param: {
      // 后端 AiModelQueryRequestDTO 里用 supplier 做过滤，这里把 provider 值传给 supplier
      supplier: query.provider || undefined,
      modelName: query.modelName || undefined,
      modelKey: query.modelKey || undefined,
      status: query.status || undefined
    }
  }

  const resp: PageResponse<AiModel> = await aiModelApi.queryPage(payload)
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

const openEdit = async (record: AiModel) => {
  modal.mode = 'edit'
  resetForm()
  const id = record.id
  if (id == null) return
  const detail = await aiModelApi.detail(id)
  Object.assign(form, detail)
  modal.open = true
}

const closeModal = () => {
  modal.open = false
}

const handleDeleteOne = async (id: number | string) => {
  if (id == null) return
  const msg = await aiModelApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await aiModelApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()

  modal.submitting = true
  try {
    const payload: AiModel = { ...form }
    // a-input 默认是字符串，这里把后端需要的数字字段转换回数字
    const toInt = (v: any, fallback: number) => {
      if (v === '' || v === undefined || v === null) return fallback
      const n = Number(v)
      return Number.isFinite(n) ? n : fallback
    }
    payload.responseLimit = toInt(payload.responseLimit, 0)
    payload.randomIndex = toInt(payload.randomIndex, 0)
    payload.topVariance = toInt(payload.topVariance, 0)
    payload.isDefault = toInt(payload.isDefault, 0)

    let msg: string
    if (modal.mode === 'create') {
      msg = await aiModelApi.create(payload)
    } else {
      msg = await aiModelApi.update(payload)
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
.model-page {
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
</style>
