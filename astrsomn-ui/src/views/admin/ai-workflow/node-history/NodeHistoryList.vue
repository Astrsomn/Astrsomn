<template>
  <AdminPageShell title="节点历史" description="查看节点执行输入输出与耗时。" empty-text="暂无节点历史。">
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
            :summary-text="`当前页 ${pagedList.length} 条节点历史。`"
          />
        </template>

        <AstrsomnDataView :data-source="pagedList" :columns="columns" row-key="id" mode="table" :pagination="false" />

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

const columns = [
  { title: '记录 ID', dataIndex: 'id', key: 'id', width: 120 },
  { title: '实例 ID', dataIndex: 'instanceId', key: 'instanceId', width: 120 },
  { title: '节点 ID', dataIndex: 'nodeId', key: 'nodeId', width: 180 },
  { title: '输入摘要', dataIndex: 'inputPreview', key: 'inputPreview', width: 260, ellipsis: true },
  { title: '输出摘要', dataIndex: 'outputPreview', key: 'outputPreview', width: 260, ellipsis: true },
  { title: '耗时(ms)', dataIndex: 'executionMs', key: 'executionMs', width: 120 }
]

const list = [
  {
    id: 20001,
    instanceId: 9001,
    nodeId: 'NODE_LLM_SCORE',
    inputPreview: '{"orderAmount":1820}',
    outputPreview: '{"score":0.81}',
    executionMs: 312
  },
  {
    id: 20000,
    instanceId: 9000,
    nodeId: 'NODE_TOOL_PUSH',
    inputPreview: '{"approved":true}',
    outputPreview: '{"status":"ok"}',
    executionMs: 109
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
