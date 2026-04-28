<template>
  <AdminPageShell title="流程定义" description="维护工作流主定义与草稿图配置。" empty-text="暂无流程定义。">
    <div class="definition-list-page">
      <AstrsomnDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstrsomnSearchPill v-model="query.workflowName" placeholder="搜索流程名称" @search="fetchList" />
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
              <AstrsomnSegmentedButton :buttons="segmentedButtons" />
            </div>
          </div>
        </template>

        <template #overview>
          <AstrsomnOverview
            :list-length="list.length"
            :selected-count="0"
            :all-current-selected="false"
            :part-current-selected="false"
            :show-actions="false"
            :summary-text="`当前页 ${list.length} 条流程定义。`"
          />
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
              <a-tag :color="record.status === '已发布' ? 'processing' : 'default'">{{ record.status || '-' }}</a-tag>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-button type="link" @click="openEdit(record)">编辑</a-button>
              <a-divider type="vertical" />
              <a-button type="link" @click="openCopy(record)">复制</a-button>
              <a-divider type="vertical" />
              <a-popconfirm title="确定发布该流程吗？" ok-text="确认" cancel-text="取消" @confirm="() => handlePublish(record)">
                <a-button type="link">发布</a-button>
              </a-popconfirm>
              <a-divider type="vertical" />
              <a-button type="link" @click="openHistory(record)">历史</a-button>
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
      v-model:open="historyModalOpen"
      :title="`发布历史：${historyWorkflowName || '-'}`"
      :footer="null"
      width="900px"
      destroy-on-close
      @cancel="closeHistoryModal"
    >
      <a-table
        size="small"
        :loading="historyLoading"
        :data-source="historyList"
        :columns="historyColumns"
        :pagination="false"
        row-key="id"
        :scroll="{ y: 420 }"
      />
      <div class="history-pagination">
        <AstrsomnPagination
          :current="historyPage.pageNum"
          :page-size="historyPage.pageSize"
          :total="historyPage.total"
          @change="onHistoryPageChange"
        />
      </div>
    </a-modal>

  </AdminPageShell>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { FilterOutlined, PlusOutlined, SearchOutlined } from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'
import AstrsomnOverview from '@/components/home/AstrsomnOverview.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnSegmentedButton from '@/components/home/AstrsomnSegmentedButton.vue'
import { aiWorkflowApi, type AiWorkflow, type PageResponse } from '@/api/aiWorkflow'
import { aiWorkflowRuntimeApi, type WorkflowRuntimeRecord } from '@/api/aiWorkflowRuntime'

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
  { title: '操作', key: 'actions', width: 280 }
]

const loading = ref(false)
const list = ref<AiWorkflow[]>([])
const router = useRouter()
const historyModalOpen = ref(false)
const historyLoading = ref(false)
const historyWorkflowName = ref('')
const historyWorkflowId = ref<number | string>()
const historyList = ref<WorkflowRuntimeRecord[]>([])
const historyPage = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const historyColumns = [
  { title: '版本号', dataIndex: 'version', key: 'version', width: 90 },
  { title: '节点ID', dataIndex: 'nodeId', key: 'nodeId', width: 180, ellipsis: true },
  { title: '节点名称', dataIndex: 'nodeName', key: 'nodeName', width: 180, ellipsis: true },
  { title: '历史类型', dataIndex: 'historyType', key: 'historyType', width: 120 },
  { title: '记录时间', dataIndex: 'updateTime', key: 'updateTime', width: 180 }
]

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
  void router.push({ name: 'AdminWorkflowDefinitionBuilder' })
}

const openEdit = (record: AiWorkflow) => {
  if (record.id == null) return
  void router.push({
    name: 'AdminWorkflowDefinitionEditBuilder',
    params: { id: String(record.id) }
  })
}

const openCopy = (record: AiWorkflow) => {
  if (record.id == null) return
  void router.push({
    name: 'AdminWorkflowDefinitionBuilder',
    query: { cloneId: String(record.id) }
  })
}

const handleDeleteOne = async (id?: number | string) => {
  if (id == null) return
  const msg = await aiWorkflowApi.delete([id])
  message.success(msg || '删除成功')
  void fetchList()
}

const handlePublish = async (record: AiWorkflow) => {
  if (record.id == null) return
  const msg = await aiWorkflowRuntimeApi.publish({ id: record.id })
  message.success(msg || '发布成功')
  void fetchList()
  if (historyModalOpen.value && historyWorkflowId.value === record.id) {
    void fetchHistoryList()
  }
}

const fetchHistoryList = async () => {
  if (historyWorkflowId.value == null) return
  historyLoading.value = true
  try {
    const resp = await aiWorkflowRuntimeApi.nodePublishHistoryQueryPage({
      pageNo: historyPage.pageNum,
      pageSize: historyPage.pageSize,
      param: {
        flowDefinitionId: historyWorkflowId.value,
        historyType: 'PUBLISH'
      }
    })
    historyList.value = resp.list || []
    historyPage.total = resp.total || 0
  } finally {
    historyLoading.value = false
  }
}

const openHistory = (record: AiWorkflow) => {
  if (record.id == null) return
  historyWorkflowId.value = record.id
  historyWorkflowName.value = record.workflowName || String(record.id)
  historyPage.pageNum = 1
  historyModalOpen.value = true
  void fetchHistoryList()
}

const onHistoryPageChange = (pageNum: number, pageSize: number) => {
  historyPage.pageNum = pageNum
  historyPage.pageSize = pageSize
  void fetchHistoryList()
}

const closeHistoryModal = () => {
  historyModalOpen.value = false
  historyWorkflowId.value = undefined
  historyWorkflowName.value = ''
  historyList.value = []
}

const segmentedButtons = [
  {
    label: '查询',
    icon: SearchOutlined,
    type: 'primary' as const,
    ghost: true,
    onClick: () => {
      void fetchList()
    }
  },
  {
    label: '重置',
    icon: FilterOutlined,
    onClick: resetFilters
  },
  {
    label: '新建流程',
    icon: PlusOutlined,
    type: 'primary' as const,
    onClick: openCreate
  }
]

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

.history-pagination {
  margin-top: 12px;
}
</style>
