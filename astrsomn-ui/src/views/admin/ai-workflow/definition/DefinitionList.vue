<template>
  <AdminPageShell title="流程定义" description="维护工作流主定义与草稿图配置。" empty-text="暂无流程定义。">
    <div class="definition-list-page">
      <AstrsomnDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <a-input
                v-model="query.workflowName"
                allow-clear
                class="toolbar-input"
                placeholder="搜索流程名称"
                @pressEnter="fetchList"
              />
              <a-input
                v-model="query.workflowKey"
                allow-clear
                class="toolbar-input"
                placeholder="搜索 Flow Key"
                @pressEnter="fetchList"
              />
              <a-select
                v-model="query.description"
                allow-clear
                class="toolbar-select"
                placeholder="业务分类"
                :options="categoryOptions"
              />
            </div>
            <div class="toolbar-right">
              <a-button type="primary" ghost @click="fetchList">查询</a-button>
              <a-button @click="resetFilters">重置</a-button>
              <a-button type="primary" @click="openCreate">新建流程</a-button>
            </div>
          </div>
        </template>

        <AstrsomnDataView :data-source="list" :columns="columns" row-key="id" mode="table" :pagination="false" :loading="loading">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'workflowName'">
              <span>{{ record.workflowName || '-' }}</span>
            </template>
            <template v-else-if="column.key === 'workflowKey'">
              <span>{{ record.workflowKey || '-' }}</span>
            </template>
            <template v-else-if="column.key === 'description'">
              <span>{{ record.description || '-' }}</span>
            </template>
            <template v-else-if="column.key === 'status'">
              <a-tag :color="record.status ? 'processing' : 'default'">{{ record.status || '-' }}</a-tag>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-button type="link" @click="openEdit(record)">编辑</a-button>
              <a-divider type="vertical" />
              <a-popconfirm title="确定删除吗？" ok-text="确认" cancel-text="取消" @confirm="() => handleDeleteOne(record.id)">
                <a-button type="link" danger>删除</a-button>
              </a-popconfirm>
            </template>
          </template>
        </AstrsomnDataView>

        <template #pagination>
          <AstrsomnPagination :current="page.pageNum" :page-size="page.pageSize" :total="page.total" @change="onPageChange" />
        </template>
      </AstrsomnDataSection>
    </div>

    <a-modal
      :open="modal.open"
      :title="modal.mode === 'create' ? '新建流程定义' : '编辑流程定义'"
      :confirm-loading="modal.submitting"
      destroy-on-close
      @update:open="(value) => (modal.open = value)"
      @ok="handleSubmit"
    >
      <a-form layout="vertical">
        <a-form-item label="流程名称" required>
          <a-input v-model="form.workflowName" maxlength="128" />
        </a-form-item>
        <a-form-item label="Flow Key" required>
          <a-input v-model="form.workflowKey" maxlength="128" />
        </a-form-item>
        <a-form-item label="业务分类">
          <a-input v-model="form.description" maxlength="128" />
        </a-form-item>
        <a-form-item label="流程图 JSON">
          <a-textarea v-model="form.graphJson" :rows="8" />
        </a-form-item>
      </a-form>
    </a-modal>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import { aiWorkflowApi, type AiWorkflow, type PageResponse } from '@/api/aiWorkflow'

type WorkflowQuery = {
  workflowName?: string
  workflowKey?: string
  description?: string
}

const categoryOptions = [
  { label: '生产', value: '生产' },
  { label: '质检', value: '质检' },
  { label: '通用', value: '通用' }
]

const columns = [
  { title: '流程名称', dataIndex: 'workflowName', key: 'workflowName', width: 220 },
  { title: 'Flow Key', dataIndex: 'workflowKey', key: 'workflowKey', width: 220 },
  { title: '业务分类', dataIndex: 'description', key: 'description', width: 140 },
  { title: '版本号', dataIndex: 'versionNo', key: 'versionNo', width: 100 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 120 },
  { title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', width: 180 },
  { title: '操作', key: 'actions', width: 160 }
]

const loading = ref(false)
const list = ref<AiWorkflow[]>([])

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const query = reactive<WorkflowQuery>({
  workflowName: undefined,
  workflowKey: undefined,
  description: undefined
})

const modal = reactive({
  open: false,
  mode: 'create' as 'create' | 'edit',
  submitting: false
})

const form = reactive<AiWorkflow>({
  id: undefined,
  workflowName: '',
  workflowKey: '',
  description: '',
  graphJson: ''
})

const resetForm = () => {
  form.id = undefined
  form.workflowName = ''
  form.workflowKey = ''
  form.description = ''
  form.graphJson = ''
}

const fetchList = async () => {
  loading.value = true
  try {
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        workflowName: query.workflowName || undefined,
        workflowKey: query.workflowKey || undefined,
        description: query.description || undefined
      }
    }
    const resp: PageResponse<AiWorkflow> = await aiWorkflowApi.queryPage(payload)
    list.value = resp.list || []
    page.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

const onPageChange = (pageNum: number, pageSize: number) => {
  page.pageNum = pageNum
  page.pageSize = pageSize
  void fetchList()
}

const resetFilters = () => {
  query.workflowName = undefined
  query.workflowKey = undefined
  query.description = undefined
  page.pageNum = 1
  void fetchList()
}

const openCreate = () => {
  modal.mode = 'create'
  resetForm()
  modal.open = true
}

const openEdit = async (record: AiWorkflow) => {
  if (record.id == null) return
  modal.mode = 'edit'
  const detail = await aiWorkflowApi.detail(record.id)
  form.id = detail.id
  form.workflowName = detail.workflowName || ''
  form.workflowKey = detail.workflowKey || ''
  form.description = detail.description || ''
  form.graphJson = detail.graphJson || ''
  modal.open = true
}

const handleDeleteOne = async (id?: number | string) => {
  if (id == null) return
  const msg = await aiWorkflowApi.delete([id])
  message.success(msg || '删除成功')
  void fetchList()
}

const handleSubmit = async () => {
  if (!form.workflowName || !form.workflowKey) {
    message.warning('请填写流程名称与 Flow Key')
    return
  }
  modal.submitting = true
  try {
    const payload: AiWorkflow = {
      id: form.id,
      workflowName: form.workflowName,
      workflowKey: form.workflowKey,
      description: form.description,
      graphJson: form.graphJson
    }
    let msg = ''
    if (modal.mode === 'create') {
      delete payload.id
      msg = await aiWorkflowApi.create(payload)
    } else {
      msg = await aiWorkflowApi.update(payload)
    }
    message.success(msg || '保存成功')
    modal.open = false
    void fetchList()
  } finally {
    modal.submitting = false
  }
}

onMounted(() => {
  void fetchList()
})
</script>

<style scoped>
.definition-list-page {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.toolbar-input {
  width: 220px;
}

.toolbar-select {
  width: 160px;
}
</style>
