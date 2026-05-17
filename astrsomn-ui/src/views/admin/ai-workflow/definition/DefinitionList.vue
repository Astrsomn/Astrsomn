<template>
  <AstPageShell empty-text="暂无流程定义。">
    <div class="definition-list-page">
      <AstDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstSearchInput v-model="query.workflowName" placeholder="搜索流程名称" @search="fetchList"/>

            </div>
            <div class="toolbar-right">
              <AstegmentedButton :buttons="segmentedButtons"/>
            </div>
          </div>

        </template>

        <template #overview>
          <AstOverview
              :all-current-selected="false"
              :list-length="list.length"
              :part-current-selected="false"
              :selected-count="0"
              :show-actions="false"
              :summary-text="`当前页 ${list.length} 条流程定义。`"
          />
        </template>

        <AstDataView :columns="columns" :data-source="list" :loading="loading" :pagination="false" :row-selection="rowSelection"
                          mode="table" row-key="id">
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
              <a-space>
                <a-button size="small" type="link" @click="openEdit(record)">
                  <EditOutlined/>
                </a-button>
                <a-button size="small" type="link" @click="openCopy(record)">
                  <CopyOutlined/>
                </a-button>
                <a-popconfirm cancel-text="取消" ok-text="确认" title="确定发布该流程吗？"
                              @confirm="() => handlePublish(record)">
                  <a-button size="small" type="link">
                    <RocketOutlined/>
                  </a-button>
                </a-popconfirm>
                <a-button size="small" type="link" @click="openHistory(record)">
                  <HistoryOutlined/>
                </a-button>
                <a-popconfirm cancel-text="取消" ok-text="确认" title="确定删除吗？"
                              @confirm="() => handleDeleteOne(record.id)">
                  <a-button danger size="small" type="link">
                    <DeleteOutlined/>
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </AstDataView>

        <template #pagination>
          <AstPagination :current="page.pageNum" :page-size="page.pageSize" :total="page.total"
                              @change="onPageChange"/>
        </template>
      </AstDataSection>
    </div>

    <a-modal
        v-model:open="historyModalOpen"
        :footer="null"
        :title="`发布历史：${historyWorkflowName || '-'}`"
        destroy-on-close
        width="900px"
        @cancel="closeHistoryModal"
    >
      <a-table
          :columns="historyColumns"
          :data-source="historyList"
          :loading="historyLoading"
          :pagination="false"
          :scroll="{ y: 420 }"
          row-key="id"
          size="small"
      />
      <div class="history-pagination">
        <AstPagination
            :current="historyPage.pageNum"
            :page-size="historyPage.pageSize"
            :total="historyPage.total"
            @change="onHistoryPageChange"
        />
      </div>
    </a-modal>

  </AstPageShell>
</template>

<script lang="ts" setup>
import {computed, onMounted, reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import {message, Modal} from 'ant-design-vue'
import {
  CopyOutlined,
  DeleteOutlined,
  EditOutlined,
  FilterOutlined,
  HistoryOutlined,
  PlusOutlined,
  RocketOutlined,
  SearchOutlined
} from '@ant-design/icons-vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import AstDataSection from '@/components/home/AstDataSection.vue'
import AstDataView from '@/components/home/AstDataView.vue'
import AstOverview from '@/components/home/AstOverview.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstegmentedButton from '@/components/home/AstegmentedButton.vue'
import {type AiWorkflow, aiWorkflowApi, type PageResponse} from '@/api/aiWorkflow'
import {aiWorkflowRuntimeApi, type WorkflowRuntimeRecord} from '@/api/aiWorkflowRuntime'

type WorkflowQuery = {
  workflowName?: string
  workflowKey?: string
  description?: string
}

const categoryOptions = [
  {label: '生产', value: '生产'},
  {label: '质检', value: '质检'},
  {label: '通用', value: '通用'}
]

const columns = [
  {title: '流程名称', dataIndex: 'workflowName', key: 'workflowName', width: 220},
  {title: 'Flow Key', dataIndex: 'workflowKey', key: 'workflowKey', width: 220},
  {title: '业务分类', dataIndex: 'description', key: 'description', width: 140},
  {title: '版本号', dataIndex: 'versionNo', key: 'versionNo', width: 100},
  {title: '状态', dataIndex: 'status', key: 'status', width: 120},
  {title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', width: 180},
  {title: '操作', key: 'actions', width: 280}
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
  {title: '版本号', dataIndex: 'version', key: 'version', width: 90},
  {title: '节点ID', dataIndex: 'nodeId', key: 'nodeId', width: 180, ellipsis: true},
  {title: '节点名称', dataIndex: 'nodeName', key: 'nodeName', width: 180, ellipsis: true},
  {title: '历史类型', dataIndex: 'historyType', key: 'historyType', width: 120},
  {title: '记录时间', dataIndex: 'updateTime', key: 'updateTime', width: 180}
]

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const selectedRowKeys = ref<Array<number | string>>([])
const selectedKeySet = computed(() => new Set(selectedRowKeys.value))

const currentPageIds = computed(() =>
    list.value
        .map((item) => item.id)
        .filter((id): id is number | string => id !== undefined && id !== null)
)

const allCurrentSelected = computed(() => {
  return (
      currentPageIds.value.length > 0 &&
      currentPageIds.value.every((id) => selectedRowKeys.value.includes(id))
  )
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
  void router.push({name: 'AdminWorkflowDefinitionBuilder'})
}

const openEdit = (record: AiWorkflow) => {
  if (record.id == null) return
  void router.push({
    name: 'AdminWorkflowDefinitionEditBuilder',
    params: {id: String(record.id)}
  })
}

const openCopy = (record: AiWorkflow) => {
  if (record.id == null) return
  void router.push({
    name: 'AdminWorkflowDefinitionBuilder',
    query: {cloneId: String(record.id)}
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
  const msg = await aiWorkflowRuntimeApi.publish({id: record.id})
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

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await aiWorkflowApi.delete(ids)
  message.success(msg || '删除成功')
  selectedRowKeys.value = []
  void fetchList()
}

const segmentedButtons = computed(() => [
  {
    label: '查询',
    icon: SearchOutlined,
    type: 'default' as const,
    ghost: true,
    onClick: () => {
      void fetchList()
    },
    plain: true
  },
  {
    label: '重置',
    icon: FilterOutlined,
    onClick: resetFilters,
    plain: true
  },
  {
    label: selectedRowKeys.value.length > 0 ? `删除 (${selectedRowKeys.value.length})` : '删除',
    icon: DeleteOutlined,
    disabled: selectedRowKeys.value.length === 0,
    onClick: () => {
      if (selectedRowKeys.value.length === 0) return
      Modal.confirm({
        title: `确定批量删除选中的 ${selectedRowKeys.value.length} 条流程定义吗？`,
        okText: '确认',
        cancelText: '取消',
        onOk: () => handleBatchDelete()
      })
    },
    plain: true
  },
  {
    label: '新建',
    icon: PlusOutlined,
    type: 'primary' as const,
    onClick: openCreate,
    plain: true
  }
])

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
