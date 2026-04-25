<template>
  <AdminPageShell title="流程实例" description="跟踪流程执行状态与当前节点。" empty-text="暂无实例。">
    <div class="page-wrap">
      <AstrsomnDataSection>
        <AstrsomnDataView :data-source="list" :columns="columns" row-key="id" mode="table" :pagination="false">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'executionStatus'">
              <a-tag :color="statusColor[record.executionStatus] || 'default'">{{ record.executionStatus }}</a-tag>
            </template>
          </template>
        </AstrsomnDataView>
      </AstrsomnDataSection>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'

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
</script>

<style scoped>
.page-wrap {
  padding: 20px;
}
</style>
