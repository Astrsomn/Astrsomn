<template>
  <AdminPageShell title="流程发布" description="查看发布快照与版本信息。" empty-text="暂无发布记录。">
    <div class="page-wrap">
      <AstrsomnDataSection>
        <AstrsomnDataView :data-source="list" :columns="columns" row-key="id" mode="table" :pagination="false">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'latest'">
              <a-tag :color="record.latest ? 'green' : 'default'">{{ record.latest ? '最新' : '历史' }}</a-tag>
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
</script>

<style scoped>
.page-wrap {
  padding: 20px;
}
</style>
