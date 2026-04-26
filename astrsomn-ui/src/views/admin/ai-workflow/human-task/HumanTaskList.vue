<template>
  <AdminPageShell title="人工任务" description="处理人工审批节点与反馈数据。" empty-text="暂无人工任务。">
    <div class="page-wrap">
      <AstrsomnDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstrsomnSearchPill v-model="query.keyword" placeholder="搜索实例 ID / 节点 ID" @search="onSearch" />
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
            :summary-text="`当前页 ${pagedList.length} 条人工任务。`"
          />
        </template>

        <AstrsomnDataView :data-source="pagedList" :columns="columns" row-key="id" mode="table" :pagination="false">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'taskStatus'">
              <a-tag :color="statusColor[record.taskStatus] || 'default'">{{ record.taskStatus }}</a-tag>
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
  PENDING: 'gold',
  APPROVED: 'green',
  REJECTED: 'red'
}

const columns = [
  { title: '任务 ID', dataIndex: 'id', key: 'id', width: 120 },
  { title: '实例 ID', dataIndex: 'instanceId', key: 'instanceId', width: 120 },
  { title: '节点 ID', dataIndex: 'nodeId', key: 'nodeId', width: 160 },
  { title: '任务状态', key: 'taskStatus', width: 120 },
  { title: '上下文摘要', dataIndex: 'payloadPreview', key: 'payloadPreview', width: 280, ellipsis: true },
  { title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', width: 180 }
]

const list = [
  {
    id: 3001,
    instanceId: 9001,
    nodeId: 'NODE_APPROVAL',
    taskStatus: 'PENDING',
    payloadPreview: '{"applicant":"张三","amount":1820}',
    updateTime: '2026-04-25 21:03:24'
  },
  {
    id: 3000,
    instanceId: 9000,
    nodeId: 'NODE_APPROVAL',
    taskStatus: 'APPROVED',
    payloadPreview: '{"applicant":"李四","amount":500}',
    updateTime: '2026-04-24 18:40:11'
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
    return String(item.instanceId).includes(keyword) || item.nodeId.toLowerCase().includes(keyword)
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
