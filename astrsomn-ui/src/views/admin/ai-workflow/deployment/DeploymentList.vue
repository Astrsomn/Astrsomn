<template>
  <AdminPageShell title="流程发布" description="查看发布快照与版本信息。" empty-text="暂无发布记录。">
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
            :list-length="pagedList.length"
            :selected-count="0"
            :all-current-selected="false"
            :part-current-selected="false"
            :show-actions="false"
            :summary-text="`当前页 ${pagedList.length} 条发布记录。`"
          />
        </template>

        <AstrsomnDataView :data-source="pagedList" :columns="columns" row-key="id" mode="table" :pagination="false">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'latest'">
              <a-tag :color="record.latest ? 'green' : 'default'">{{ record.latest ? '最新' : '历史' }}</a-tag>
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

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 120 },
  { title: '流程定义 ID', dataIndex: 'flowDefinitionId', key: 'flowDefinitionId', width: 140 },
  { title: '版本号', dataIndex: 'version', key: 'version', width: 120 },
  { title: '状态', key: 'latest', width: 120 },
  { title: '发布时间', dataIndex: 'createTime', key: 'createTime', width: 180 }
]

const list = [
  { id: 1001, flowDefinitionId: 1, version: 3, latest: true, createTime: '2026-04-25 20:12:02' },
  { id: 1000, flowDefinitionId: 1, version: 2, latest: false, createTime: '2026-04-24 17:08:21' }
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
    return String(item.flowDefinitionId).includes(keyword) || String(item.version).includes(keyword)
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
