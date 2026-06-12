<template>
  <AstModal
      :open="open"
      width="90vw"
      max-width="min(90vw, 1400px)"
      body-height="80vh"
      main-padding="0"
      @cancel="handleClose"
      @update:open="emit('update:open', $event)"
  >
    <template #header-logo>
      <BlockOutlined/>
    </template>
    <template #header-title>
      {{ t.vectorCenter.rightBottom.segmentList }}
    </template>
    <template #header-subtitle>
      doc={{ docId || '-' }} / store={{ storeId || '-' }}
    </template>

    <div class="segment-modal-body">
      <div class="segment-modal-toolbar">
        <AstSearchInput
            v-model="keyword"
            layout="fluid"
            :placeholder="t.vectorCenter.rightBottom.searchPlaceholder"
            class="segment-search"
        />
        <div class="segment-modal-actions">
          <a-button
              :disabled="selectedRowKeys.length === 0"
              danger
              type="primary"
              ghost
              @click="handleBatchDelete"
          >
            <template #icon><DeleteOutlined/></template>
            {{ selectedRowKeys.length > 0 ? t.vectorCenter.rightBottom.deleteCount.replace('{count}', String(selectedRowKeys.length)) : t.vectorCenter.rightBottom.deleteBtn }}
          </a-button>
          <a-button
              :disabled="!docId"
              type="primary"
              ghost
              @click="handleVectorizeAll"
          >
            <template #icon><ExperimentOutlined/></template>
            {{ t.vectorCenter.rightBottom.vectorize }}
          </a-button>
        </div>
      </div>

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
                  <ExperimentOutlined/>
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
  </AstModal>
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import {message, Modal} from 'ant-design-vue'
import {BlockOutlined, DeleteOutlined, ExperimentOutlined} from '@ant-design/icons-vue'
import AstModal from '@/components/home/AstModal.vue'
import AstDataView from '@/components/home/AstDataView.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import {type AiVecSegment, aiVecSegmentApi} from '@/api/aiVecSegment'
import {aiVecDocApi} from '@/api/aiVecDoc'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-vector')

const props = defineProps<{
  open: boolean
  storeId?: number | string
  docId?: number | string
}>()

const emit = defineEmits<{
  'update:open': [value: boolean]
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
    () => [props.storeId, props.docId, props.open],
    () => {
      if (!props.open) return
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

const handleClose = () => {
  emit('update:open', false)
}
</script>

<style scoped>
.segment-modal-body {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 20px 30px;
  background: var(--bg-card);
}

.segment-modal-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.segment-search {
  max-width: 320px;
}

.segment-modal-actions {
  display: flex;
  gap: 8px;
}

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
