<template>
  <a-layout-footer class="bg-white border-t px-6 py-2 right-bottom">
    <a-row :gutter="16">
      <a-col :span="24">
        <div class="panel-head">
          <span>切片列表</span>
          <span class="meta">doc={{ docId || '-' }} / store={{ storeId || '-' }}</span>
        </div>
        <a-table :data-source="segments" :pagination="false" :columns="segmentColumns" row-key="id" size="small">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'actions'">
              <a @click="removeSegment(record)">删除</a>
            </template>
          </template>
        </a-table>
      </a-col>
    </a-row>
  </a-layout-footer>
</template>

<script lang="ts" setup>
import { message } from 'ant-design-vue'
import { Layout } from 'ant-design-vue';
import { aiVecSegmentApi, type AiVecSegment } from '@/api/aiVecSegment.ts'

const { Footer } = Layout;

const props = defineProps<{
  segments: AiVecSegment[]
  storeId?: number | string
  docId?: number | string
}>()

const emit = defineEmits<{
  changed: []
}>()

const segmentColumns = [
  { title: 'ID', dataIndex: 'id' },
  { title: 'chunk', dataIndex: 'chunkIndex' },
  { title: 'vectorId', dataIndex: 'vectorId' },
  { title: '操作', key: 'actions' }
]

const removeSegment = async (record: AiVecSegment) => {
  if (record.id == null) return
  await aiVecSegmentApi.delete([record.id])
  emit('changed')
}
</script>

<style scoped>
.ant-layout-footer {
  padding: 12px 24px;
  min-height: 260px;
}
.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.meta {
  color: #999;
  font-size: 12px;
}
</style>