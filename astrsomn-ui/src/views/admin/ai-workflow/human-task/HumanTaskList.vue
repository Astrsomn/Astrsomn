<template>
  <AdminPageShell title="人工任务" description="处理人工审批节点与反馈数据。" empty-text="暂无人工任务。">
    <div class="page-wrap">
      <AstrsomnDataSection>
        <AstrsomnDataView :data-source="list" :columns="columns" row-key="id" mode="table" :pagination="false">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'taskStatus'">
              <a-tag :color="statusColor[record.taskStatus] || 'default'">{{ record.taskStatus }}</a-tag>
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
</script>

<style scoped>
.page-wrap {
  padding: 20px;
}
</style>
