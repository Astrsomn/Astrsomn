<template>
  <a-layout-footer class="bg-white border-t px-6 py-2 right-bottom">
    <a-row :gutter="16">
      <a-col :span="24">
        <div class="panel-head">
          <span>切片列表</span>
          <span class="meta">doc={{ docId || '-' }} / store={{ storeId || '-' }}</span>
        </div>
        <a-table
          :data-source="segmentRows"
          :loading="loading"
          :pagination="pagination"
          :columns="segmentColumns"
          row-key="id"
          size="small"
          @change="handleTableChange"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'segmentContent'">
              <a-typography-paragraph :ellipsis="{ rows: 2, expandable: true, symbol: '展开' }" :content="record.segmentContent || '-'" />
            </template>
            <template v-else-if="column.key === 'metadataJson'">
              <a-typography-paragraph :ellipsis="{ rows: 2, expandable: true, symbol: '展开' }" :content="record.metadataJson || '-'" />
            </template>
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
import { computed, reactive, ref, watch } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { aiVecSegmentApi, type AiVecSegment } from '@/api/aiVecSegment'

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
  { title: '内容', dataIndex: 'segmentContent', key: 'segmentContent' },
  { title: '词数', dataIndex: 'wordCount' },
  { title: 'vectorId', dataIndex: 'vectorId' },
  { title: '元数据', dataIndex: 'metadataJson', key: 'metadataJson' },
  { title: '操作', key: 'actions' }
]

const loading = ref(false)
const segmentRows = ref<AiVecSegment[]>([])
const pager = reactive({
  pageNo: 1,
  pageSize: 10,
  total: 0
})

const pagination = computed(() => ({
  current: pager.pageNo,
  pageSize: pager.pageSize,
  total: pager.total,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条`
}))

const fetchSegmentRows = async () => {
  if (!props.storeId || !props.docId) {
    segmentRows.value = []
    pager.total = 0
    return
  }
  loading.value = true
  try {
    const resp = await aiVecSegmentApi.queryPage({
      pageNo: pager.pageNo,
      pageSize: pager.pageSize,
      param: {
        collectionId: Number(props.storeId),
        docId: Number(props.docId)
      }
    })
    segmentRows.value = resp.list || []
    pager.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

watch(
  () => [props.storeId, props.docId],
  () => {
    pager.pageNo = 1
    fetchSegmentRows()
  },
  { immediate: true }
)

const handleTableChange = (page: { current?: number; pageSize?: number }) => {
  pager.pageNo = page.current || 1
  pager.pageSize = page.pageSize || 10
  fetchSegmentRows()
}

const removeSegment = async (record: AiVecSegment) => {
  if (record.id == null) return
  Modal.confirm({
    title: '确认删除切片',
    content: `将删除切片 ${record.id} 并同步清理向量实体。`,
    okButtonProps: { danger: true },
    async onOk() {
      const segmentId = record.id
      if (segmentId == null) return
      await aiVecSegmentApi.delete([segmentId])
      message.success('切片删除成功')
      emit('changed')
      await fetchSegmentRows()
    }
  })
}
</script>

<style scoped>
.ant-layout-footer {
  padding: 12px 24px;
  min-height: 260px;
  background: var(--bg-card);
  border-top: 1px solid var(--border-default);
}
.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.meta {
  color: var(--text-muted);
  font-size: 12px;
}
</style>