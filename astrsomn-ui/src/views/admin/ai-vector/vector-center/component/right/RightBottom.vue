<template>
  <div class="right-bottom-container">
    <div class="list-header">
      <div class="header-info">
        <h2 class="list-title">{{ t.vectorCenter.rightBottom.segmentList }}</h2>
        <p class="list-subtitle">doc={{ docId || '-' }} / store={{ storeId || '-' }}</p>
      </div>
      <AstegmentedButton :buttons="toolbarButtons"/>
    </div>

    <AstSearchInput
        v-model="keyword"
        layout="fluid"
        :placeholder="t.vectorCenter.rightBottom.searchPlaceholder"
        class="segment-search"
    />

    <AstDataView
        :columns="columns"
        :data-source="filteredSegments"
        :loading="loading"
        :row-selection="rowSelection"
        :scroll="{ x: 1000 }"
        :empty-text="t.vectorCenter.rightBottom.emptyText"
        mode="table"
        row-key="id"
        dense
        table-row-height="22px"
        table-header-height="28px"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'segmentContent'">
          <a-typography-paragraph :content="record.segmentContent || '-'"
                                  :ellipsis="{ rows: 2, expandable: true, symbol: t.vectorCenter.rightBottom.expand }"/>
        </template>
        <template v-else-if="column.key === 'status'">
          <a-tag v-if="record.vectorId" color="green">{{ t.vectorCenter.rightBottom.vectorized }}</a-tag>
          <a-tag v-else color="default">{{ t.vectorCenter.rightBottom.pendingVectorize }}</a-tag>
        </template>
        <template v-else-if="column.key === 'metadataJson'">
          <a-typography-paragraph :content="record.metadataJson || '-'"
                                  :ellipsis="{ rows: 2, expandable: true, symbol: t.vectorCenter.rightBottom.expand }"/>
        </template>
        <template v-else-if="column.key === 'actions'">
          <a-space>
            <a-button size="small" type="link" @click="handleVectorizeSegment(record)">
              <template #icon>
                <experiment-outlined/>
              </template>
              {{ record.vectorId ? t.vectorCenter.rightBottom.reVectorize : t.vectorCenter.rightBottom.vectorize }}
            </a-button>
            <a-popconfirm :title="t.vectorCenter.rightBottom.confirmDelete" @confirm="removeSegment(record)">
              <a-button danger size="small" type="link">{{ t.vectorCenter.rightBottom.deleteBtn }}</a-button>
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
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import {type AiVecSegment, aiVecSegmentApi} from '@/api/aiVecSegment'
import {aiVecDocApi} from '@/api/aiVecDoc'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-vector')

const props = defineProps<{
  storeId?: number | string
  docId?: number | string
}>()

const columns = computed(() => [
  {title: t.value.vectorCenter.rightBottom.column.content, dataIndex: 'segmentContent', key: 'segmentContent'},
  {title: t.value.vectorCenter.rightBottom.column.status, key: 'status', width: 100},
  {title: t.value.vectorCenter.rightBottom.column.wordCount, dataIndex: 'wordCount', width: 80},
  {title: t.value.vectorCenter.rightBottom.column.metadata, dataIndex: 'metadataJson', key: 'metadataJson', width: 200},
  {title: t.value.vectorCenter.rightBottom.column.actions, key: 'actions', width: 160, fixed: 'right' as const}
])

const loading = ref(false)
const keyword = ref('')
const segmentRows = ref<AiVecSegment[]>([])
const selectedRowKeys = ref<Array<number | string>>([])
const pager = reactive({pageNum: 1, pageSize: 10, total: 0})

const filteredSegments = computed(() => {
  const kw = keyword.value.trim().toLowerCase()
  if (!kw) return segmentRows.value
  return segmentRows.value.filter(row =>
      (row.segmentContent || '').toLowerCase().includes(kw)
  )
})

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
    label: selectedRowKeys.value.length > 0 ? t.value.vectorCenter.rightBottom.deleteCount.replace('{count}', String(selectedRowKeys.value.length)) : t.value.vectorCenter.rightBottom.deleteBtn,
    icon: DeleteOutlined,
    type: 'danger',
    plain: true,
    disabled: selectedRowKeys.value.length === 0,
    onClick: handleBatchDelete
  },
  {
    label: t.value.vectorCenter.rightBottom.vectorize,
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
      keyword.value = ''
      selectedRowKeys.value = []
      fetchSegmentRows()
    },
    {immediate: true}
)

const removeSegment = async (record: AiVecSegment) => {
  if (record.id == null) return
  await aiVecSegmentApi.delete([record.id])
  message.success(t.value.vectorCenter.rightBottom.deleteSegmentSuccess)
  selectedRowKeys.value = selectedRowKeys.value.filter(k => k !== record.id)
  await fetchSegmentRows()
}

const handleBatchDelete = () => {
  const n = selectedRowKeys.value.length
  if (n === 0) return
  Modal.confirm({
    title: t.value.vectorCenter.rightBottom.confirmBatchDelete.replace('{n}', String(n)),
    okButtonProps: {danger: true},
    async onOk() {
      await aiVecSegmentApi.delete([...selectedRowKeys.value])
      message.success(t.value.vectorCenter.rightBottom.batchDeleted.replace('{n}', String(n)))
      selectedRowKeys.value = []
      await fetchSegmentRows()
    }
  })
}

const handleVectorizeSegment = async (record: AiVecSegment) => {
  if (record.id == null) {
    message.warning(t.value.vectorCenter.rightBottom.cannotGetSegmentId)
    return
  }
  const isRe = !!record.vectorId
  Modal.confirm({
    title: isRe ? t.value.vectorCenter.rightBottom.confirmReVectorize : t.value.vectorCenter.rightBottom.confirmVectorize,
    content: isRe ? t.value.vectorCenter.rightBottom.reVectorizeContent : t.value.vectorCenter.rightBottom.vectorizeContent,
    async onOk() {
      await aiVecSegmentApi.vectorize(record.id!)
      message.success(t.value.vectorCenter.rightBottom.vectorizeComplete)
      await fetchSegmentRows()
    }
  })
}

const handleVectorizeAll = () => {
  if (!props.docId) return
  Modal.confirm({
    title: t.value.vectorCenter.rightBottom.confirmBatchVectorize,
    content: t.value.vectorCenter.rightBottom.batchVectorizeContent,
    async onOk() {
      await aiVecDocApi.vectorize(props.docId!)
      message.success(t.value.vectorCenter.rightBottom.vectorizeSubmitted)
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

.segment-search {
  margin-bottom: 16px;
}

/* ---- table beautify ---- */
:deep(.data-view-table) {
  border-radius: var(--radius-md);
  overflow: hidden;
  border: 1px solid var(--border-default);
}

:deep(.ant-table-thead > tr > th) {
  background: var(--bg-elevated) !important;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.03em;
  border-color: var(--border-default) !important;
}

:deep(.ant-table-tbody > tr > td) {
  border-color: var(--border-subtle) !important;
  color: var(--text-primary);
}

:deep(.ant-table-tbody > tr:hover > td) {
  background: var(--primary-hover) !important;
}

:deep(.ant-tag) {
  border-radius: var(--radius-sm);
  font-size: 11px;
  line-height: 18px;
  padding: 0 6px;
}

:deep(.ant-btn-link) {
  padding: 0 6px;
}

:deep(.ant-typography-expand) {
  font-size: 11px;
}
</style>
