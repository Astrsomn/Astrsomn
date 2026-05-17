<template>
  <AstPageShell description="查看流程节点配置。" empty-text="暂无节点配置。" title="节点配置">
    <div class="page-wrap">
      <AstDataSection>
        <template #toolbar>
          <div class="toolbar">
            <AstSearchInput v-model="query.keyword" placeholder="搜索流程定义 ID / 节点 ID" @search="onSearch"/>
            <AstegmentedButton :buttons="segmentedButtons"/>
          </div>
        </template>

        <AstDataView :columns="columns" :data-source="list" :loading="loading" :pagination="false" mode="table"
                          row-key="id">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'actions'">
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
import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstegmentedButton from '@/components/home/AstegmentedButton.vue'
import {aiWorkflowRuntimeApi, type WorkflowRuntimeRecord} from '@/api/aiWorkflowRuntime'

const router = useRouter()
const columns = [
  {title: 'ID', dataIndex: 'id', key: 'id', width: 120},
  {title: '流程定义 ID', dataIndex: 'flowDefinitionId', key: 'flowDefinitionId', width: 160},
  {title: '节点 ID', dataIndex: 'nodeId', key: 'nodeId', width: 180},
  {title: '节点类型', dataIndex: 'nodeType', key: 'nodeType', width: 140},
  {title: '启用状态', dataIndex: 'enabled', key: 'enabled', width: 120},
  {title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', width: 180},
  {title: '操作', key: 'actions', width: 100, fixed: 'right' as const}
]

const list = ref<WorkflowRuntimeRecord[]>([])
const loading = ref(false)
const query = reactive({keyword: ''})
const page = reactive({pageNum: 1, pageSize: 10, total: 0})

const fetchList = async () => {
  loading.value = true
  try {
    const resp = await aiWorkflowRuntimeApi.nodeConfigQueryPage({
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
  void router.push({name: 'AdminWorkflowNodeConfigDetail', params: {id: String(id)}})
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
