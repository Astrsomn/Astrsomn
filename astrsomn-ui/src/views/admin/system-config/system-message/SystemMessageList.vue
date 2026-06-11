<template>
  <AstPageShell
      :description="t.list.description"
      :empty-text="t.list.emptyText"
      :title="t.list.title"
  >
    <div class="message-page">
      <AstDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstSearchInput
                  v-model="query.title"
                  layout="toolbar"
                  :placeholder="t.list.searchPlaceholder"
                  @search="fetchList"
              />

              <a-select
                  v-model:value="query.messageType"
                  :options="messageTypeOptions"
                  allow-clear
                  class="toolbar-select"
                  :placeholder="t.list.filterMessageType"
                  @change="onFilterChanged"
              />

              <a-select
                  v-model:value="query.messageLevel"
                  :options="messageLevelOptions"
                  allow-clear
                  class="toolbar-select"
                  :placeholder="t.list.filterMessageLevel"
                  @change="onFilterChanged"
              />

              <a-select
                  v-model:value="query.readStatus"
                  :options="readStatusOptions"
                  allow-clear
                  class="toolbar-select"
                  :placeholder="t.list.filterReadStatus"
                  @change="onFilterChanged"
              />
            </div>

            <div class="toolbar-right">
              <AstegmentedButton :buttons="actionButtons"/>
            </div>
          </div>
        </template>


        <AstDataView
            :columns="columns"
            :data-source="list"
            :loading="loading"
            :row-selection="rowSelection"
            :scroll="{ x: 1320 }"
            :empty-text="t.list.emptyMatch"
            mode="table"
            row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'messageType'">
              <a-tag>{{ messageTypeLabel(record.messageType) }}</a-tag>
            </template>
            <template v-else-if="column.key === 'messageLevel'">
              <a-tag :color="levelColor(record.messageLevel)">{{ messageLevelLabel(record.messageLevel) }}</a-tag>
            </template>
            <template v-else-if="column.key === 'readStatus'">
              <a-tag :color="record.readStatus === 'READ' ? 'green' : 'orange'">
                {{ readStatusDict.getLabel(record.readStatus) }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'title'">
              <span class="title-cell">{{ record.title || '—' }}</span>
            </template>
            <template v-else-if="column.key === 'source'">
              <span>{{ record.source || 'SYSTEM' }}</span>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-button type="link" @click="goEdit(record)">{{ t.list.btnEdit }}</a-button>
              <a-divider type="vertical"/>
              <a-popconfirm :title="t.list.confirmDelete" @confirm="() => handleDeleteOne(record.id)">
                <a-button danger type="link">{{ t.list.btnDelete }}</a-button>
              </a-popconfirm>
            </template>
          </template>
        </AstDataView>

        <template #pagination>
          <AstPagination
              :current="page.pageNum"
              :page-size="page.pageSize"
              :total="page.total"
              @change="onPageChange"
          />
        </template>
      </AstDataSection>
    </div>
  </AstPageShell>
</template>

<script lang="ts" setup>
import {computed, reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import {message, Modal} from 'ant-design-vue'
import {DeleteOutlined, PlusOutlined, ReloadOutlined} from '@ant-design/icons-vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import AstDataSection from '@/components/home/AstDataSection.vue'
import AstDataView from '@/components/home/AstDataView.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstegmentedButton, {type SegmentedButton} from '@/components/home/AstegmentedButton.vue'
import {type PageResponse, type SystemMessage, systemMessageApi} from '@/api/systemMessage'
import {usePageTranslation} from '@/locales/pages.ts'
import {getDictionary} from '@/locales/dictionary/registry.ts'

const router = useRouter()
const t = usePageTranslation('system-message')
const messageTypeDict = getDictionary('system.message.type')
const messageLevelDict = getDictionary('system.message.level')
const readStatusDict = getDictionary('system.message.readStatus')

type QueryState = {
  title?: string
  messageType?: string
  messageLevel?: string
  readStatus?: string
}

const messageTypeOptions = computed(() => messageTypeDict.order.map(key => ({
  label: messageTypeDict.getLabel(key) ?? key,
  value: key
})))

const messageLevelOptions = computed(() => messageLevelDict.order.map(key => ({
  label: messageLevelDict.getLabel(key) ?? key,
  value: key
})))

const readStatusOptions = computed(() => readStatusDict.order.map(key => ({
  label: readStatusDict.getLabel(key) ?? key,
  value: key
})))

const messageTypeLabel = (v?: string) => messageTypeDict.getLabel(v) ?? v ?? '—'
const messageLevelLabel = (v?: string) => messageLevelDict.getLabel(v) ?? v ?? '—'

const levelColor = (v?: string) => {
  if (v === 'SUCCESS') return 'green'
  if (v === 'WARN') return 'orange'
  if (v === 'ERROR') return 'red'
  return 'blue'
}

const columns = computed(() => [
  {title: t.value.list.columnTitle, key: 'title', width: 260, ellipsis: true},
  {title: t.value.list.columnType, key: 'messageType', width: 180},
  {title: t.value.list.columnLevel, key: 'messageLevel', width: 100},
  {title: t.value.list.columnStatus, key: 'readStatus', width: 100},
  {title: t.value.list.columnSource, key: 'source', width: 140, ellipsis: true},
  {title: t.value.list.columnCreateTime, dataIndex: 'createTime', key: 'createTime', width: 180, ellipsis: true},
  {title: t.value.list.columnActions, key: 'actions', width: 160, fixed: 'right' as const}
])

const query = reactive<QueryState>({})
const list = ref<SystemMessage[]>([])
const loading = ref(false)
const page = reactive({pageNum: 1, pageSize: 10, total: 0})
const selectedRowKeys = ref<Array<number | string>>([])

const actionButtons = computed<SegmentedButton[]>(() => [
  {
    label: t.value.list.btnReset,
    type: 'primary',
    icon: ReloadOutlined,
    plain: true,
    onClick: resetFilters
  },
  {
    label: selectedRowKeys.value.length > 0 ? t.value.list.btnBatchDeleteCount.replace('{count}', String(selectedRowKeys.value.length)) : t.value.list.btnBatchDelete,
    type: 'danger',
    icon: DeleteOutlined,
    disabled: selectedRowKeys.value.length === 0,
    plain: true,
    onClick: handleBatchDelete
  },
  {
    label: t.value.list.btnCreate,
    type: 'primary',
    icon: PlusOutlined,
    onClick: goCreate
  }
])

const currentPageIds = computed(() => list.value.map((item) => item.id).filter((id): id is number | string => !!id))
const allCurrentSelected = computed(() => currentPageIds.value.length > 0 && currentPageIds.value.every((id) => selectedRowKeys.value.includes(id)))
const partCurrentSelected = computed(() => {
  if (currentPageIds.value.length === 0) return false
  const count = currentPageIds.value.filter((id) => selectedRowKeys.value.includes(id)).length
  return count > 0 && count < currentPageIds.value.length
})

const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys: Array<number | string>) => {
    selectedRowKeys.value = keys
  }
}))

const toggleSelectAllCurrentPage = (checked: boolean) => {
  if (checked) {
    selectedRowKeys.value = Array.from(new Set([...selectedRowKeys.value, ...currentPageIds.value]))
    return
  }
  selectedRowKeys.value = selectedRowKeys.value.filter((id) => !currentPageIds.value.includes(id))
}

const onFilterChanged = () => {
  page.pageNum = 1
  void fetchList()
}

const resetFilters = () => {
  query.title = undefined
  query.messageType = undefined
  query.messageLevel = undefined
  query.readStatus = undefined
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

const fetchList = async () => {
  loading.value = true
  try {
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        title: query.title || undefined,
        messageType: query.messageType || undefined,
        messageLevel: query.messageLevel || undefined,
        readStatus: query.readStatus || undefined
      }
    }
    const resp: PageResponse<SystemMessage> = await systemMessageApi.queryPage(payload)
    list.value = resp.list || []
    page.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

const onPageChange = (p: number, size: number) => {
  page.pageNum = p
  page.pageSize = size
  void fetchList()
}

const goCreate = () => {
  void router.push({name: 'AdminSystemMessageNew'})
}

const goEdit = (row: SystemMessage) => {
  if (row.id == null) return
  void router.push({name: 'AdminSystemMessageEdit', params: {id: String(row.id)}})
}

const handleDeleteOne = async (id: number | string | undefined) => {
  if (id == null) return
  const msg = await systemMessageApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  Modal.confirm({
    title: t.value.list.confirmBatchDelete,
    okText: t.value.list.btnConfirm,
    cancelText: t.value.list.btnCancel,
    onOk: async () => {
      const msg = await systemMessageApi.delete(ids)
      message.success(msg)
      selectedRowKeys.value = []
      void fetchList()
    }
  })
}

void fetchList()
</script>

<style scoped>
.message-page {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  flex-wrap: wrap;
}

.toolbar-left {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
  flex: 1;
}

.toolbar-right {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.toolbar-select {
  width: 160px;
}

.title-cell {
  font-weight: 500;
}

@media (max-width: 720px) {
  .toolbar-left,
  .toolbar-right,
  .toolbar-select {
    width: 100%;
  }
}
</style>
