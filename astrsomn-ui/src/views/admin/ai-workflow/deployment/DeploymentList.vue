<template>
  <AstrsomnPageShell title="流程发布" description="查看发布快照与版本信息。" empty-text="暂无发布记录。">
    <div class="page-wrap">
      <AstrsomnDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstrsomnSearchPill v-model="query.keyword" placeholder="搜索流程定义 ID / 版本号" @search="onSearch" />
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
            :summary-text="`当前页 ${list.length} 条发布记录。`"
          />
        </template>

        <AstrsomnDataView :data-source="list" :columns="columns" row-key="id" mode="table" :pagination="false" :loading="loading">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'latest'">
              <a-tag :color="record.latest ? 'green' : 'default'">{{ record.latest ? '最新' : '历史' }}</a-tag>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-button type="link" size="small" @click="goDetail(record.id)">详情</a-button>
            </template>
          </template>
        </AstrsomnDataView>

        <template #pagination>
          <AstrsomnPagination :current="page.pageNum" :page-size="page.pageSize" :total="page.total" @change="onPageChange" />
        </template>
      </AstrsomnDataSection>
    </div>
  </AstrsomnPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { FilterOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import AstrsomnPageShell from '@/components/home/AstrsomnPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'
import AstrsomnOverview from '@/components/home/AstrsomnOverview.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnSegmentedButton from '@/components/home/AstrsomnSegmentedButton.vue'
import { aiWorkflowRuntimeApi, type WorkflowRuntimeRecord } from '@/api/aiWorkflowRuntime'

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 120 },
  { title: '流程定义 ID', dataIndex: 'flowDefinitionId', key: 'flowDefinitionId', width: 140 },
  { title: '版本号', dataIndex: 'version', key: 'version', width: 120 },
  { title: '状态', key: 'latest', width: 120 },
  { title: '发布时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
  { title: '操作', key: 'actions', width: 100, fixed: 'right' as const }
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
    const resp = await aiWorkflowRuntimeApi.deploymentQueryPage({
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: { keyword: query.keyword || undefined }
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
  void router.push({ name: 'AdminWorkflowDeploymentDetail', params: { id: String(id) } })
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
