<template>
  <AstPageShell
      :breadcrumbs="breadcrumbs"
      :show-view-toggle="true"
      :view-mode="viewMode"
      :view-toggle-handler="handleViewToggle"
      :description="t.list.description"
      :empty-text="t.list.emptyText"
      :title="t.list.title"
  >
    <div ref="pageRef" class="instance-page">
      <AstDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstSearchInput
                  v-model="query.instanceName"
                  :button-label="t.list.searchButton"
                  layout="toolbar"
                  :placeholder="t.list.searchPlaceholder"
                  @search="fetchList"
              />
              <ExtensionSelector
                  :value="query.extensionCode"
                  allow-clear
                  class="toolbar-provider-select"
                  @update:value="handleProviderChange"
              />
            </div>
            <div class="toolbar-right">
              <AstegmentedButton :buttons="toolbarSegmentButtons"/>
            </div>
          </div>
        </template>

        <a-tabs
            :active-key="modelTypeTab"
            class="toolbar-model-type-tabs"
            size="small"
            @change="handleModelTypeTabChange"
        >
          <a-tab-pane key="all" :tab="t.list.tab.all"/>
          <a-tab-pane key="chat" :tab="t.list.tab.chat"/>
          <a-tab-pane key="embedding" :tab="t.list.tab.embedding"/>
          <a-tab-pane key="image" :tab="t.list.tab.image"/>
        </a-tabs>

        <AstDataView
            :card-columns="currentGridColumns"
            :card-gap="instanceCardGap"
            :card-min-width="instanceCardMinWidth"
            :columns="columns"
            :data-source="list"
            :loading="loading"
            :mode="dataViewMode"
            :row-selection="rowSelection"
            :scroll="{ x: 1010 }"
            :empty-text="t.list.emptyMatchText"
            row-key="id"
        >
          <template #card="{ record }">
            <InstanceCard
                :index="list.findIndex((item) => item.id === record.id)"
                :record="record"
                :selected="record.id != null && selectedKeySet.has(record.id)"
                @delete="handleDeleteOne(record.id)"
                @edit="goEdit(record)"
            />
          </template>

          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'providerAvatar'">
              <img
                  v-if="providerAvatarCell(record)"
                  :alt="record.instanceName || 'provider'"
                  :src="providerAvatarCell(record)"
                  aria-hidden="true"
                  class="provider-avatar-cell"
              />
              <span v-else class="text-secondary">—</span>
            </template>
            <template v-else-if="column.key === 'modelType'">
              <a-tag>
                {{ modelTypeLabel(record.modelType) }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'status'">
              <a-tag :color="record.status === 'enabled' ? 'green' : 'default'">
                {{ record.status === 'enabled' ? t.list.status.enabled : t.list.status.disabled }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'isDefault'">
              <a-tag v-if="record.isDefault === 'Y'" color="blue">{{ t.list.defaultPreset }}</a-tag>
              <span v-else class="text-secondary">-</span>
            </template>

            <template v-else-if="column.key === 'actions'">
              <a-space>
                <a-button size="small" type="link" @click="goEdit(record)">
                  <EditOutlined/>
                </a-button>
                <a-popconfirm :title="t.list.deleteConfirm" @confirm="() => handleDeleteOne(record.id)">
                  <a-button danger size="small" type="link">
                    <DeleteOutlined/>
                  </a-button>
                </a-popconfirm>
              </a-space>
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

      <InstanceForm
          v-model:visible="formVisible"
          :record="currentRecord"
          @success="handleFormSuccess"
      />
    </div>
  </AstPageShell>
</template>

<script lang="ts" setup>
import {computed, onBeforeUnmount, onMounted, reactive, ref} from 'vue'
import {message, Modal} from 'ant-design-vue'
import {DeleteOutlined, EditOutlined, PlusOutlined} from '@ant-design/icons-vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import AstDataSection from '@/components/home/AstDataSection.vue'
import AstDataView from '@/components/home/AstDataView.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstegmentedButton, {type SegmentedButton} from '@/components/home/AstegmentedButton.vue'
import ExtensionSelector from '@/views/admin/system-config/system-extension/selector/ExtensionSelector.vue'
import InstanceForm from './InstanceForm.vue'
import InstanceCard from './component/InstanceCard.vue'
import {type AiInstance, aiInstanceApi, type PageResponse} from '@/api/aiInstance'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-instance')

const INSTANCE_CARD_MIN_WIDTH_PX = 360
const INSTANCE_CARD_GAP_PX = 12
const instanceCardMinWidth = `${INSTANCE_CARD_MIN_WIDTH_PX}px`
const instanceCardGap = `${INSTANCE_CARD_GAP_PX}px`

const pageRef = ref<HTMLElement | null>(null)
const formVisible = ref(false)
const currentRecord = ref<AiInstance | undefined>(undefined)
const query = reactive<{
  instanceName?: string
  status?: string
  extensionCode?: string
  modelType?: 'chat' | 'embedding' | 'image'
}>({})
const list = ref<AiInstance[]>([])
const loading = ref(false)
const page = reactive({pageNum: 1, pageSize: 10, total: 0})
const selectedRowKeys = ref<Array<number | string>>([])
const selectedKeySet = computed(() => new Set(selectedRowKeys.value))
const viewMode = ref<'grid' | 'list'>('list')
const dataViewMode = computed<'card' | 'table'>(() => (viewMode.value === 'grid' ? 'card' : 'table'))
const currentGridColumns = ref(3)
const modelTypeTab = computed(() => query.modelType ?? 'all')

const breadcrumbs = computed(() => [
  {title: t.value.list.breadcrumb.aiConfig, href: '/admin/ai-config'},
  {title: t.value.list.breadcrumb.instanceConfig},
])

const handleViewToggle = () => {
  viewMode.value = viewMode.value === 'grid' ? 'list' : 'grid'
}

const columns = computed(() => [
  {title: t.value.list.column.provider, key: 'providerAvatar', width: 60, align: 'center' as const},
  {title: t.value.list.column.modelType, dataIndex: 'modelType', key: 'modelType', width: 90},
  {
    title: t.value.list.column.instanceKey,
    dataIndex: 'instanceKey',
    key: 'instanceKey',
    width: 150,
    ellipsis: true,
    copyable: true
  },
  {title: t.value.list.column.name, dataIndex: 'instanceName', key: 'instanceName', width: 140, ellipsis: true},
  {title: t.value.list.column.modelKey, dataIndex: 'modelKey', key: 'modelKey', width: 150, ellipsis: true},
  {title: t.value.list.column.accountName, dataIndex: 'accountName', key: 'accountName', width: 120, ellipsis: true},
  {title: t.value.list.column.isDefault, key: 'isDefault', width: 80, align: 'center'},
  {title: t.value.list.column.status, dataIndex: 'status', key: 'status', width: 80},
  {title: t.value.list.column.createTime, dataIndex: 'createTime', key: 'createTime', width: 140, dateFormat: true},
  {title: t.value.list.column.actions, key: 'actions', width: 100, fixed: 'right' as const}
])

const providerAvatarCell = (record: AiInstance) => {
  const raw = record?.providerAvatar
  return typeof raw === 'string' && raw.trim() ? raw.trim() : ''
}

const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys: Array<number | string>) => {
    selectedRowKeys.value = keys
  }
}))

const modelTypeLabel = (type?: string) => {
  if (type === 'embedding') return t.value.modelType.embedding
  if (type === 'image') return t.value.modelType.image
  if (type === 'chat') return t.value.modelType.chat
  return type || '-'
}

const handleProviderChange = (value?: string) => {
  query.extensionCode = value || undefined
  page.pageNum = 1
  void fetchList()
}

const handleModelTypeTabChange = (key: string) => {
  query.modelType = key === 'all' ? undefined : (key as 'chat' | 'embedding' | 'image')
  page.pageNum = 1
  void fetchList()
}

const fetchList = async () => {
  loading.value = true
  try {
    const resp: PageResponse<AiInstance> = await aiInstanceApi.queryPage({
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        instanceName: query.instanceName || undefined,
        status: query.status || undefined,
        extensionCode: query.extensionCode || undefined,
        modelType: query.modelType || undefined
      }
    })
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
  currentRecord.value = undefined
  formVisible.value = true
}

const goEdit = (record: AiInstance) => {
  currentRecord.value = record
  formVisible.value = true
}

const handleFormSuccess = () => {
  void fetchList()
}

const handleDeleteOne = async (id: number | string | undefined) => {
  if (id == null) return
  const msg = await aiInstanceApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = selectedRowKeys.value.filter((key) => key !== id)
  void fetchList()
}

const handleBatchDelete = async () => {
  if (selectedRowKeys.value.length === 0) return
  await aiInstanceApi.delete(selectedRowKeys.value)
  message.success(t.value.list.batchDeleteSuccess)
  selectedRowKeys.value = []
  void fetchList()
}

const toolbarSegmentButtons = computed<SegmentedButton[]>(() => [
  {
    label: selectedRowKeys.value.length > 0 ? t.value.list.batchDeleteCount.replace('{n}', String(selectedRowKeys.value.length)) : t.value.list.batchDelete,
    icon: DeleteOutlined,
    disabled: selectedRowKeys.value.length === 0,
    onClick: () => {
      if (selectedRowKeys.value.length === 0) return
      Modal.confirm({
        title: t.value.list.batchDeleteConfirm,
        okText: t.value.list.confirm,
        cancelText: t.value.list.cancel,
        onOk: () => handleBatchDelete()
      })
    },
    type: 'danger',
    plain: true
  },
  {
    label: t.value.list.create,
    type: 'primary',
    icon: PlusOutlined,
    onClick: goCreate,
    plain: true
  }
])

const resolveGridColumns = () => {
  if (typeof window === 'undefined') return 3
  const width = pageRef.value?.clientWidth ?? window.innerWidth
  const columnsCount = Math.floor((width + INSTANCE_CARD_GAP_PX) / (INSTANCE_CARD_MIN_WIDTH_PX + INSTANCE_CARD_GAP_PX))
  return Math.max(1, Math.min(3, columnsCount))
}

const syncGridColumns = () => {
  currentGridColumns.value = resolveGridColumns()
}

let resizeObserver: ResizeObserver | null = null

onMounted(() => {
  syncGridColumns()
  if (typeof ResizeObserver !== 'undefined' && pageRef.value) {
    resizeObserver = new ResizeObserver(syncGridColumns)
    resizeObserver.observe(pageRef.value)
  } else {
    window.addEventListener('resize', syncGridColumns)
  }
  void fetchList()
})

onBeforeUnmount(() => {
  resizeObserver?.disconnect()
  window.removeEventListener('resize', syncGridColumns)
})
</script>

<style scoped>
.instance-page {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.toolbar-provider-select {
  width: 280px;
  min-width: 220px;
}

.toolbar-model-type-tabs {
  min-width: 240px;
}

.toolbar-model-type-tabs :deep(.ant-tabs-nav) {
  margin: 0;
}

.toolbar-model-type-tabs :deep(.ant-tabs-tab) {
  padding-top: 6px;
  padding-bottom: 6px;
}

.mono-text {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}

.provider-avatar-cell {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  vertical-align: middle;
}

.provider-avatar-cell :deep(svg) {
  width: 22px;
  height: 22px;
  display: block;
}

.text-secondary {
  color: var(--text-muted);
}

@media (max-width: 720px) {
  .toolbar-left,
  .toolbar-right {
    width: 100%;
  }
}
</style>
