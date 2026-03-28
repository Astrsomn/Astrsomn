<template>
  <AdminPageShell
    title="工作流"
    description="管理流程定义（AI_WORKFLOW），编辑跳转至画布编排。"
    empty-text="暂无工作流。"
  >
    <div class="wf-page">
      <AdminListToolbar>
        <template #left>
          <div class="search-cluster">
            <a-input
              v-model:value="query.workflowName"
              placeholder="搜索工作流名称"
              class="toolbar-input search-main-input"
              allow-clear
              @pressEnter="fetchList"
            >
              <template #prefix><search-outlined /></template>
            </a-input>
            <a-input
              v-model:value="query.workflowKey"
              placeholder="Workflow Key"
              class="toolbar-input search-sub-input"
              allow-clear
              @pressEnter="fetchList"
            >
              <template #prefix><key-outlined /></template>
            </a-input>
          </div>

          <div class="status-switch" role="group" aria-label="状态筛选">
            <a-button
              class="status-btn"
              :class="{ active: query.status === 'PUBLISHED' }"
              @click="toggleStatusFilter('PUBLISHED')"
            >
              <template #icon><check-circle-outlined /></template>
              已发布
            </a-button>
            <a-button
              class="status-btn"
              :class="{ active: query.status === 'DRAFT' }"
              @click="toggleStatusFilter('DRAFT')"
            >
              <template #icon><edit-outlined /></template>
              草稿
            </a-button>
            <a-button
              class="status-btn"
              :class="{ active: query.status === 'DISABLED' }"
              @click="toggleStatusFilter('DISABLED')"
            >
              <template #icon><stop-outlined /></template>
              停用
            </a-button>
          </div>
        </template>

        <template #right>
          <a-button type="primary" class="primary-btn" @click="fetchList">
            <template #icon><search-outlined /></template>
            查询
          </a-button>
          <a-popconfirm
            v-if="selectedRowKeys.length > 0"
            title="确定批量删除选中的工作流吗？"
            ok-text="确认"
            cancel-text="取消"
            @confirm="handleBatchDelete"
          >
            <a-button danger class="ghost-btn danger-btn">
              <template #icon><delete-outlined /></template>
              批量删除
            </a-button>
          </a-popconfirm>
          <a-button class="ghost-btn" @click="resetFilters">重置</a-button>
          <a-button class="ghost-btn" @click="openCreate">
            <template #icon><plus-outlined /></template>
            新增
          </a-button>
          <a-button class="ghost-btn" @click="goSimpleTest">编排测试</a-button>
        </template>
      </AdminListToolbar>

      <BaseOverview
        :list-length="list.length"
        :selected-count="selectedRowKeys.length"
        :all-current-selected="allCurrentSelected"
        :part-current-selected="partCurrentSelected"
        :show-actions="list.length > 0"
        :summary-text="`当前页 ${list.length} 条工作流，已选 ${selectedRowKeys.length} 条。`"
        @toggle-select-all="toggleSelectAllCurrentPage"
      />

      <a-table
        :columns="columns"
        :data-source="list"
        :pagination="false"
        row-key="id"
        :row-selection="rowSelection"
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
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  CheckCircleOutlined,
  DeleteOutlined,
  EditOutlined,
  KeyOutlined,
  PlusOutlined,
  SearchOutlined,
  StopOutlined
} from '@ant-design/icons-vue'
import AdminPageShell from '@/views/admin/components/admin/AdminPageShell.vue'
import AdminListToolbar from '@/views/admin/components/admin/AdminListToolbar.vue'
import BaseOverview from '@/views/admin/components/admin/BaseOverview.vue'
import { aiWorkflowApi, type AiWorkflow } from '@/api/aiWorkflow'

const router = useRouter()

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
const selectedRowKeys = ref<Array<number | string>>([])
const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

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

const toggleStatusFilter = (value: 'DRAFT' | 'PUBLISHED' | 'DISABLED') => {
  query.status = query.status === value ? undefined : value
}

const resetFilters = () => {
  query.workflowKey = undefined
  query.workflowName = undefined
  query.status = undefined
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

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
    selectedRowKeys.value = []
    void fetchList()
  } catch (e: unknown) {
    message.error(e instanceof Error ? e.message : '删除失败')
  }
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  try {
    await aiWorkflowApi.delete(ids)
    message.success('已删除')
    selectedRowKeys.value = []
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

.search-cluster {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
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
  width: 320px;
}

.search-sub-input {
  width: 220px;
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

.pagination-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
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

  .pagination-wrap {
    justify-content: center;
  }
}
</style>
