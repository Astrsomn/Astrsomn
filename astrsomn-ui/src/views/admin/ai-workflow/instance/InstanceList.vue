<template>
  <AstPageShell description="跟踪流程执行状态与当前节点。" empty-text="暂无实例。" title="流程实例">
    <div class="page-wrap">
      <AstDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstSearchInput v-model="query.keyword" placeholder="搜索业务主键 / 当前节点" @search="onSearch"/>
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
              :summary-text="`当前页 ${list.length} 条流程实例。`"
          />
        </template>

        <AstDataView :columns="columns" :data-source="list" :loading="loading" :pagination="false" mode="table"
                          row-key="id">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'executionStatus'">
              <a-tag :color="statusColor[record.executionStatus] || 'default'">{{ record.executionStatus }}</a-tag>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-button size="small" type="link" @click="goDetail(record.id)">详情</a-button>
            </template>
          </template>
        </AstDataView>

        <template #pagination>
          <AstPagination :current="page.pageNum" :page-size="page.pageSize" :total="page.total"
                              @change="onPageChange"/>
        </template>
      </AstDataSection>
    </div>
  </AstPageShell>
</template>

<script lang="ts" setup>
import {computed, reactive, ref} from 'vue'
import {FilterOutlined} from '@ant-design/icons-vue'
import {useRouter} from 'vue-router'
import AstPageShell from '@/components/home/AstPageShell.vue'
import AstDataSection from '@/components/home/AstDataSection.vue'
import AstDataView from '@/components/home/AstDataView.vue'
import AstOverview from '@/components/home/AstOverview.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstegmentedButton from '@/components/home/AstegmentedButton.vue'
import {aiWorkflowRuntimeApi, type WorkflowRuntimeRecord} from '@/api/aiWorkflowRuntime'

const statusColor: Record<string, string> = {
  RUNNING: 'processing',
  SUSPENDED: 'gold',
  COMPLETED: 'green',
  FAILED: 'red'
}

const columns = [
  {title: '实例 ID', dataIndex: 'id', key: 'id', width: 120},
  {title: '发布 ID', dataIndex: 'deploymentId', key: 'deploymentId', width: 120},
  {title: '业务主键', dataIndex: 'businessKey', key: 'businessKey', width: 180},
  {title: '执行状态', key: 'executionStatus', width: 130},
  {title: '当前节点', dataIndex: 'currentNodeId', key: 'currentNodeId', width: 160},
  {title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', width: 180},
  {title: '操作', key: 'actions', width: 100, fixed: 'right' as const}
]

const router = useRouter()
const list = ref<WorkflowRuntimeRecord[]>([])
const loading = ref(false)

const query = reactive({
  keyword: ''
})

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const fetchList = async () => {
  loading.value = true
  try {
    const resp = await aiWorkflowRuntimeApi.instanceQueryPage({
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {keyword: query.keyword || undefined}
    })
    list.value = resp.list || []
    page.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

const onSearch = () => {
  page.pageNum = 1
  void fetchList()
}

const resetFilters = () => {
  query.keyword = ''
  page.pageNum = 1
  void fetchList()
}

const onPageChange = (pageNum: number, pageSize: number) => {
  page.pageNum = pageNum
  page.pageSize = pageSize
  void fetchList()
}

const goDetail = (id: unknown) => {
  if (id == null) return
  void router.push({name: 'AdminWorkflowInstanceDetail', params: {id: String(id)}})
}

const segmentedButtons = computed(() => [
  {
    label: '重置',
    icon: FilterOutlined,
    onClick: resetFilters
  }
])

void fetchList()
</script>

<style scoped>
.page-wrap {
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
</style>
