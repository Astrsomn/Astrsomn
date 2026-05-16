<template>
  <div class="right-bottom-container">
    <div class="list-header">
      <div class="header-info">
        <h2 class="list-title">切片列表</h2>
        <p class="list-subtitle">doc={{ docId || '-' }} / store={{ storeId || '-' }}</p>
      </div>
    </div>
    <div class="table-wrapper">
      <a-table
          :columns="segmentColumns"
          :data-source="segmentRows"
          :loading="loading"
          :pagination="pagination"
          row-key="id"
          size="small"
          @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'segmentContent'">
            <a-typography-paragraph :content="record.segmentContent || '-'"
                                    :ellipsis="{ rows: 2, expandable: true, symbol: '展开' }"/>
          </template>
          <template v-else-if="column.key === 'metadataJson'">
            <a-typography-paragraph :content="record.metadataJson || '-'"
                                    :ellipsis="{ rows: 2, expandable: true, symbol: '展开' }"/>
          </template>
          <template v-if="column.key === 'actions'">
            <a @click="removeSegment(record)">删除</a>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import {message, Modal} from 'ant-design-vue'
import {type AiVecSegment, aiVecSegmentApi} from '@/api/aiVecSegment'

const props = defineProps<{
  segments: AiVecSegment[]
  storeId?: number | string
  docId?: number | string
}>()

const emit = defineEmits<{
  changed: []
}>()

const segmentColumns = [
  {title: 'ID', dataIndex: 'id'},
  {title: 'chunk', dataIndex: 'chunkIndex'},
  {title: '内容', dataIndex: 'segmentContent', key: 'segmentContent'},
  {title: '词数', dataIndex: 'wordCount'},
  {title: 'vectorId', dataIndex: 'vectorId'},
  {title: '元数据', dataIndex: 'metadataJson', key: 'metadataJson'},
  {title: '操作', key: 'actions'}
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
    {immediate: true}
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
    okButtonProps: {danger: true},
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
.right-bottom-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px 30px;
  background: var(--bg-card);
}

/* 头部 */
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.list-title {
  font-size: 24px;
  font-weight: bold;
  color: var(--text-primary);
  margin: 0;
}

.list-subtitle {
  font-size: 12px;
  color: var(--text-muted);
  margin: 0;
}

/* 表格区域内部滚动 */
.table-wrapper {
  flex: 1;
  overflow-y: auto;
}
</style>