<template>
  <AdminPageShell title="流程定义" description="维护工作流主定义与草稿图配置。" empty-text="暂无流程定义。">
    <div class="definition-list-page">
      <AstrsomnDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <a-input v-model:value="query.keyword" allow-clear class="toolbar-input" placeholder="搜索流程名称/Flow Key" />
              <a-select
                v-model:value="query.category"
                allow-clear
                class="toolbar-select"
                placeholder="业务分类"
                :options="categoryOptions"
              />
            </div>
            <div class="toolbar-right">
              <a-button @click="resetFilters">重置</a-button>
              <a-button type="primary">新建流程</a-button>
            </div>
          </div>
        </template>

        <AstrsomnDataView :data-source="filteredList" :columns="columns" row-key="id" mode="table" :pagination="false">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'actions'">
              <a-button type="link">编辑</a-button>
              <a-divider type="vertical" />
              <a-button type="link">发布</a-button>
            </template>
          </template>
        </AstrsomnDataView>
      </AstrsomnDataSection>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'

type FlowDefinition = {
  id: number
  name: string
  flowKey: string
  category: string
  latestVersion: number
  updateTime: string
}

const categoryOptions = [
  { label: '生产', value: '生产' },
  { label: '质检', value: '质检' },
  { label: '通用', value: '通用' }
]

const columns = [
  { title: '流程名称', dataIndex: 'name', key: 'name', width: 220 },
  { title: 'Flow Key', dataIndex: 'flowKey', key: 'flowKey', width: 220 },
  { title: '业务分类', dataIndex: 'category', key: 'category', width: 120 },
  { title: '最新版本', dataIndex: 'latestVersion', key: 'latestVersion', width: 120 },
  { title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', width: 180 },
  { title: '操作', key: 'actions', width: 160 }
]

const query = reactive({
  keyword: '',
  category: undefined as string | undefined
})

const list: FlowDefinition[] = [
  {
    id: 1,
    name: '订单审核流程',
    flowKey: 'order_audit_flow',
    category: '生产',
    latestVersion: 3,
    updateTime: '2026-04-25 20:32:10'
  },
  {
    id: 2,
    name: '来料质检流程',
    flowKey: 'incoming_qc_flow',
    category: '质检',
    latestVersion: 2,
    updateTime: '2026-04-25 19:13:44'
  }
]

const filteredList = computed(() => {
  return list.filter((item) => {
    const hitKeyword =
      !query.keyword ||
      item.name.toLowerCase().includes(query.keyword.toLowerCase()) ||
      item.flowKey.toLowerCase().includes(query.keyword.toLowerCase())
    const hitCategory = !query.category || item.category === query.category
    return hitKeyword && hitCategory
  })
})

const resetFilters = () => {
  query.keyword = ''
  query.category = undefined
}
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
  width: 280px;
}

.toolbar-select {
  width: 160px;
}
</style>
