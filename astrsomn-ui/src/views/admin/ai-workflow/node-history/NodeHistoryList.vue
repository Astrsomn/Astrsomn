<template>
  <AdminPageShell title="节点历史" description="查看节点执行输入输出与耗时。" empty-text="暂无节点历史。">
    <div class="page-wrap">
      <AstrsomnDataSection>
        <AstrsomnDataView :data-source="list" :columns="columns" row-key="id" mode="table" :pagination="false" />
      </AstrsomnDataSection>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'

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
</script>

<style scoped>
.page-wrap {
  padding: 20px;
}
</style>
