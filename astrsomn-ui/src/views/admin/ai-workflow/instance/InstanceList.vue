<template>
  <AdminPageShell title="流程实例" description="跟踪流程执行状态与当前节点。" empty-text="暂无实例。">
    <div class="page-wrap">
      <AstrsomnDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstrsomnSearchPill v-model="query.keyword" placeholder="搜索业务主键 / 当前节点" @search="onSearch" />
            </div>
            <div class="toolbar-right">
              <AstrsomnSegmentedButton :buttons="segmentedButtons" />
            </div>
          </div>
        </template>

        <template #overview>
          <AstrsomnOverview
            :list-length="pagedList.length"
            :selected-count="0"
            :all-current-selected="false"
            :part-current-selected="false"
            :show-actions="false"
            :summary-text="`当前页 ${pagedList.length} 条流程实例。`"
          />
        </template>

        <AstrsomnDataView :data-source="pagedList" :columns="columns" row-key="id" mode="table" :pagination="false">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'executionStatus'">
              <a-tag :color="statusColor[record.executionStatus] || 'default'">{{ record.executionStatus }}</a-tag>
            </template>
          </template>
        </AstrsomnDataView>

        <template #pagination>
          <AstrsomnPagination :current="page.pageNum" :page-size="page.pageSize" :total="filteredList.length" @change="onPageChange" />
        </template>
      </AstrsomnDataSection>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue'
import { FilterOutlined } from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'
import AstrsomnOverview from '@/components/home/AstrsomnOverview.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnSegmentedButton from '@/components/home/AstrsomnSegmentedButton.vue'

const statusColor: Record<string, string> = {
  RUNNING: 'processing',
  SUSPENDED: 'gold',
  COMPLETED: 'green',
  FAILED: 'red'
}

const columns = [
  { title: '实例 ID', dataIndex: 'id', key: 'id', width: 120 },
  { title: '发布 ID', dataIndex: 'deploymentId', key: 'deploymentId', width: 120 },
  { title: '业务主键', dataIndex: 'businessKey', key: 'businessKey', width: 180 },
  { title: '执行状态', key: 'executionStatus', width: 130 },
  { title: '当前节点', dataIndex: 'currentNodeId', key: 'currentNodeId', width: 160 },
  { title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', width: 180 }
]

const list = [
  {
    id: 9001,
    deploymentId: 1001,
    businessKey: 'ORDER_001',
    executionStatus: 'RUNNING',
    currentNodeId: 'NODE_APPROVAL',
    updateTime: '2026-04-25 20:56:10'
  },
  {
    id: 9000,
    deploymentId: 1000,
    businessKey: 'ORDER_000',
    executionStatus: 'COMPLETED',
    currentNodeId: 'NODE_END',
    updateTime: '2026-04-24 16:42:35'
  }
]

const query = reactive({
  keyword: ''
})

const page = reactive({
  pageNum: 1,
  pageSize: 10
})

const filteredList = computed(() => {
  const keyword = query.keyword.trim().toLowerCase()
  if (!keyword) return list
  return list.filter((item) => {
    return item.businessKey.toLowerCase().includes(keyword) || item.currentNodeId.toLowerCase().includes(keyword)
  })
})

const pagedList = computed(() => {
  const start = (page.pageNum - 1) * page.pageSize
  return filteredList.value.slice(start, start + page.pageSize)
})

const onSearch = () => {
  page.pageNum = 1
}

const resetFilters = () => {
  query.keyword = ''
  page.pageNum = 1
}

const onPageChange = (pageNum: number, pageSize: number) => {
  page.pageNum = pageNum
  page.pageSize = pageSize
}

const segmentedButtons = computed(() => [
  {
    label: '重置',
    icon: FilterOutlined,
    onClick: resetFilters
  }
])
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
