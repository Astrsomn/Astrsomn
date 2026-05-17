<template>
  <div class="right-bottom-container">
    <div class="list-header">
      <div class="header-info">
        <h2 class="list-title">切片列表</h2>
        <p class="list-subtitle">doc={{ docId || '-' }} / store={{ storeId || '-' }}</p>
      </div>
      <AstegmentedButton :buttons="toolbarButtons"/>
    </div>

    <AstDataView
        :columns="segmentColumns"
        :data-source="segmentRows"
        :loading="loading"
        :row-selection="rowSelection"
        :scroll="{ x: 1000 }"
        empty-text="暂无切片数据"
        mode="table"
        row-key="id"
        dense
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
        <template v-else-if="column.key === 'actions'">
          <a-space>
            <a-button size="small" type="link" @click="handleVectorizeDoc(record)">
              <template #icon>
                <experiment-outlined/>
              </template>
              向量化
            </a-button>
            <a-popconfirm title="确定删除该切片？" @confirm="removeSegment(record)">
              <a-button danger size="small" type="link">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </AstDataView>

    <AstPagination
        :current="pager.pageNum"
        :page-size="pager.pageSize"
        :total="pager.total"
        @change="onPageChange"
    />
  </div>
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import {message, Modal} from 'ant-design-vue'
import {DeleteOutlined, ExperimentOutlined} from '@ant-design/icons-vue'
import AstDataView from '@/components/home/AstDataView.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstegmentedButton, {type SegmentedButton} from '@/components/home/AstegmentedButton.vue'
import {type AiVecSegment, aiVecSegmentApi} from '@/api/aiVecSegment'
import {aiVecDocApi} from '@/api/aiVecDoc'

const props = defineProps<{
  storeId?: number | string
  docId?: number | string
}>()

const segmentColumns = [
  {title: 'chunk', dataIndex: 'chunkIndex', width: 70},
  {title: '内容', dataIndex: 'segmentContent', key: 'segmentContent'},
  {title: '词数', dataIndex: 'wordCount', width: 80},
  {title: 'vectorId', dataIndex: 'vectorId', width: 120, copyable: true},
  {title: '元数据', dataIndex: 'metadataJson', key: 'metadataJson', width: 200},
  {title: '操作', key: 'actions', width: 160, fixed: 'right'}
]

const loading = ref(false)
const segmentRows = ref<AiVecSegment[]>([])
const selectedRowKeys = ref<Array<number | string>>([])
const pager = reactive({pageNum: 1, pageSize: 10, total: 0})

const rowSelection = computed(() => ({
  fixed: true,
  columnWidth: 48,
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys: Array<number | string>) => {
    selectedRowKeys.value = keys
  }
}))

const toolbarButtons = computed<SegmentedButton[]>(() => [
  {
    label: selectedRowKeys.value.length > 0 ? `删除 (${selectedRowKeys.value.length})` : '删除',
    icon: DeleteOutlined,
    type: 'danger',
    plain: true,
    disabled: selectedRowKeys.value.length === 0,
    onClick: handleBatchDelete
  },
  {
    label: '向量化',
    icon: ExperimentOutlined,
    type: 'primary',
    plain: true,
    disabled: !props.docId,
    onClick: handleVectorizeAll
  }
])

const fetchSegmentRows = async () => {
  if (!props.storeId || !props.docId) {
    segmentRows.value = []
    pager.total = 0
    return
  }
  loading.value = true
  try {
    const resp = await aiVecSegmentApi.queryPage({
      pageNo: pager.pageNum,
      pageSize: pager.pageSize,
      param: {
        collectionId: props.storeId,
        docId: props.docId
      }
    })
    segmentRows.value = resp.list || []
    pager.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

const onPageChange = (page: number, size: number) => {
  pager.pageNum = page
  pager.pageSize = size
  fetchSegmentRows()
}

watch(
    () => [props.storeId, props.docId],
    () => {
      pager.pageNum = 1
      selectedRowKeys.value = []
      fetchSegmentRows()
    },
    {immediate: true}
)

const removeSegment = async (record: AiVecSegment) => {
  if (record.id == null) return
  await aiVecSegmentApi.delete([record.id])
  message.success('切片删除成功')
  selectedRowKeys.value = selectedRowKeys.value.filter(k => k !== record.id)
  await fetchSegmentRows()
}

const handleBatchDelete = () => {
  const n = selectedRowKeys.value.length
  if (n === 0) return
  Modal.confirm({
    title: `确定删除选中的 ${n} 个切片吗？`,
    okButtonProps: {danger: true},
    async onOk() {
      await aiVecSegmentApi.delete([...selectedRowKeys.value])
      message.success(`已删除 ${n} 个切片`)
      selectedRowKeys.value = []
      await fetchSegmentRows()
    }
  })
}

const handleVectorizeDoc = async (record: AiVecSegment) => {
  const docId = record.docId
  if (!docId) {
    message.warning('无法获取文档ID')
    return
  }
  Modal.confirm({
    title: '确认执行向量化',
    content: `将对文档 ${docId} 的切片执行向量化并写入向量库。`,
    async onOk() {
      await aiVecDocApi.vectorize(docId)
      message.success('向量化任务已提交')
    }
  })
}

const handleVectorizeAll = () => {
  if (!props.docId) return
  Modal.confirm({
    title: '确认执行向量化',
    content: `将对当前文档的所有切片执行向量化并写入向量库。`,
    async onOk() {
      await aiVecDocApi.vectorize(props.docId!)
      message.success('向量化任务已提交')
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

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
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
</style>
