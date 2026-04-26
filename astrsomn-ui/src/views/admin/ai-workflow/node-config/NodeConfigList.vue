<template>
  <AdminPageShell title="节点配置" description="查看流程节点配置。" empty-text="暂无节点配置。">
    <div class="page-wrap">
      <AstrsomnDataSection>
        <template #toolbar>
          <div class="toolbar">
            <AstrsomnSearchPill v-model="query.keyword" placeholder="搜索流程定义 ID / 节点 ID" @search="onSearch" />
            <AstrsomnSegmentedButton :buttons="segmentedButtons" />
          </div>
        </template>

        <AstrsomnDataView :data-source="list" :columns="columns" row-key="id" mode="table" :pagination="false" :loading="loading">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'actions'">
              <a-button type="link" size="small" @click="goDetail(record.id)">详情</a-button>
            </template>
          </template>
        </AstrsomnDataView>

        <template #pagination>
          <AstrsomnPagination :current="page.pageNum" :page-size="page.pageSize" :total="page.total" @change="onPageChange" />
        </template>
      </AstrsomnDataSection>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { FilterOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnSegmentedButton from '@/components/home/AstrsomnSegmentedButton.vue'
import { aiWorkflowRuntimeApi, type WorkflowRuntimeRecord } from '@/api/aiWorkflowRuntime'

const router = useRouter()
const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 120 },
  { title: '流程定义 ID', dataIndex: 'flowDefinitionId', key: 'flowDefinitionId', width: 160 },
  { title: '节点 ID', dataIndex: 'nodeId', key: 'nodeId', width: 180 },
  { title: '节点类型', dataIndex: 'nodeType', key: 'nodeType', width: 140 },
  { title: '启用状态', dataIndex: 'enabled', key: 'enabled', width: 120 },
  { title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', width: 180 },
  { title: '操作', key: 'actions', width: 100, fixed: 'right' as const }
]

const list = ref<WorkflowRuntimeRecord[]>([])
const loading = ref(false)
const query = reactive({ keyword: '' })
const page = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const fetchList = async () => {
  loading.value = true
  try {
    const resp = await aiWorkflowRuntimeApi.nodeConfigQueryPage({
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

const onPageChange = (pageNum: number, pageSize: number) => {
  page.pageNum = pageNum
  page.pageSize = pageSize
  void fetchList()
}

const resetFilters = () => {
  query.keyword = ''
  page.pageNum = 1
  void fetchList()
}

const goDetail = (id: unknown) => {
  if (id == null) return
  void router.push({ name: 'AdminWorkflowNodeConfigDetail', params: { id: String(id) } })
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
</style>
