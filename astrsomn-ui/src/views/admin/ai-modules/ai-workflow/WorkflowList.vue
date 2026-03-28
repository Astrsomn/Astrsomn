<template>
  <AdminPageShell
    title="工作流"
    description="管理流程定义（AI_WORKFLOW），编辑跳转至画布编排。"
    empty-text="暂无工作流。"
  >
    <div class="wf-page">
      <div class="toolbar">
        <div class="toolbar-left">
          <a-input
            v-model:value="query.workflowKey"
            placeholder="Workflow Key"
            class="toolbar-input"
            allow-clear
          />
          <a-input
            v-model:value="query.workflowName"
            placeholder="名称（模糊）"
            class="toolbar-input"
            allow-clear
          />
          <a-select
            v-model:value="query.status"
            :options="statusOptions"
            placeholder="状态"
            class="toolbar-select"
            allow-clear
          />
        </div>
        <div class="toolbar-right">
          <a-button type="primary" @click="fetchList">查询</a-button>
          <a-button @click="openCreate">新增</a-button>
          <a-button @click="goSimpleTest">编排测试</a-button>
        </div>
      </div>

      <a-table
        :columns="columns"
        :data-source="list"
        :pagination="false"
        row-key="id"
        :scroll="{ x: 1100 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 'PUBLISHED' ? 'green' : 'default'">{{ record.status }}</a-tag>
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-button type="link" @click="goEdit(record)">编辑</a-button>
            <a-divider type="vertical" />
            <a-button
              v-if="record.status !== 'PUBLISHED'"
              type="link"
              @click="handlePublish(record)"
            >
              发布
            </a-button>
            <a-divider v-if="record.status !== 'PUBLISHED'" type="vertical" />
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
        v-model:open="createOpen"
        title="新建工作流"
        ok-text="创建"
        :confirm-loading="createSubmitting"
        @ok="submitCreate"
      >
        <a-form layout="vertical">
          <a-form-item label="Workflow Key" required>
            <a-input v-model:value="createForm.workflowKey" placeholder="唯一键，如 demo_flow" />
          </a-form-item>
          <a-form-item label="名称">
            <a-input v-model:value="createForm.workflowName" placeholder="显示名称" />
          </a-form-item>
          <a-form-item label="描述">
            <a-textarea v-model:value="createForm.description" :rows="2" />
          </a-form-item>
        </a-form>
      </a-modal>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import AdminPageShell from '@/views/admin/components/admin/AdminPageShell.vue'
import { aiWorkflowApi, type AiWorkflow } from '@/api/aiWorkflow'

const router = useRouter()

const statusOptions = [
  { label: '草稿', value: 'DRAFT' },
  { label: '已发布', value: 'PUBLISHED' },
  { label: '停用', value: 'DISABLED' }
]

const columns = [
  { title: 'Workflow Key', dataIndex: 'workflowKey', key: 'workflowKey', width: 180, ellipsis: true },
  { title: '名称', dataIndex: 'workflowName', key: 'workflowName', width: 160, ellipsis: true },
  { title: '版本', dataIndex: 'versionNo', key: 'versionNo', width: 72 },
  { title: '状态', key: 'status', width: 100 },
  { title: '描述', dataIndex: 'description', key: 'description', width: 220, ellipsis: true },
  { title: '操作', key: 'actions', width: 220, fixed: 'right' as const }
]

const query = reactive<{ workflowKey?: string; workflowName?: string; status?: string }>({})
const list = ref<AiWorkflow[]>([])
const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const fetchList = async () => {
  try {
    const res = await aiWorkflowApi.queryPage({
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: { ...query }
    })
    list.value = res.list || []
    page.total = res.total
  } catch (e: unknown) {
    message.error(e instanceof Error ? e.message : '加载失败')
  }
}

const onPageChange = (p: number) => {
  page.pageNum = p
  void fetchList()
}

void fetchList()

const goEdit = (record: AiWorkflow) => {
  void router.push({ name: 'AdminWorkflowEdit', params: { id: String(record.id) } })
}

const goSimpleTest = () => {
  void router.push({ name: 'AdminWorkflowSimple' })
}

const handlePublish = async (record: AiWorkflow) => {
  try {
    await aiWorkflowApi.publish(record.id!)
    message.success('已发布')
    void fetchList()
  } catch (e: unknown) {
    message.error(e instanceof Error ? e.message : '发布失败')
  }
}

const handleDeleteOne = async (id: string | number | undefined) => {
  if (id == null) return
  try {
    await aiWorkflowApi.delete([id])
    message.success('已删除')
    void fetchList()
  } catch (e: unknown) {
    message.error(e instanceof Error ? e.message : '删除失败')
  }
}

const createOpen = ref(false)
const createSubmitting = ref(false)
const createForm = reactive({
  workflowKey: '',
  workflowName: '',
  description: ''
})

const openCreate = () => {
  createForm.workflowKey = ''
  createForm.workflowName = ''
  createForm.description = ''
  createOpen.value = true
}

const submitCreate = async () => {
  if (!createForm.workflowKey.trim()) {
    message.warning('请填写 Workflow Key')
    return
  }
  createSubmitting.value = true
  try {
    await aiWorkflowApi.create({
      workflowKey: createForm.workflowKey.trim(),
      workflowName: createForm.workflowName.trim() || createForm.workflowKey.trim(),
      description: createForm.description.trim() || undefined
    })
    message.success('创建成功')
    createOpen.value = false
    void fetchList()
    const res = await aiWorkflowApi.queryPage({
      pageNo: 1,
      pageSize: 1,
      param: { workflowKey: createForm.workflowKey.trim() }
    })
    const row = res.list?.[0]
    if (row?.id != null) {
      void router.push({ name: 'AdminWorkflowEdit', params: { id: String(row.id) } })
    }
  } catch (e: unknown) {
    message.error(e instanceof Error ? e.message : '创建失败')
  } finally {
    createSubmitting.value = false
  }
}
</script>

<style scoped>
.wf-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: space-between;
  align-items: center;
}
.toolbar-left,
.toolbar-right {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}
.toolbar-input {
  width: 200px;
}
.toolbar-select {
  width: 140px;
}
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
}
</style>
