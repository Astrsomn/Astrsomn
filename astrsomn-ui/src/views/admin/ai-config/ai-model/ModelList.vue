<template>
  <AstPageShell
      :breadcrumbs="breadcrumbs"
      :description="t.list.description"
      :title="t.list.title"
  >
    <div class="model-page-container">
      <AstDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstSearchInput
                  v-model="query.modelName"
                  layout="toolbar"
                  :placeholder="t.list.searchPlaceholder"
                  @search="fetchList"
              />
              <ExtensionSelector
                  v-model:value="query.extensionCode"
                  allow-clear
                  class="toolbar-provider-select"
                  :placeholder="t.list.providerFilterPlaceholder"
                  @update:value="handleProviderChange"
              />

              <AstStatusSwitch v-model="query.status" @change="fetchList"/>
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
          <a-tab-pane key="all" :tab="t.list.tabAll"/>
          <a-tab-pane key="chat" :tab="t.list.tabChat"/>
          <a-tab-pane key="embedding" :tab="t.list.tabEmbedding"/>
          <a-tab-pane key="image" :tab="t.list.tabImage"/>
        </a-tabs>

        <AstDataView
            :columns="columns"
            :data-source="list"
            :loading="loading"
            :row-selection="rowSelection"
            :scroll="{ x: 1200 }"
            :empty-text="t.list.emptyText"
            mode="table"
            row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'modelType'">
              <div :class="record.modelType" class="model-icon">
                <template v-if="record.modelType === 'chat'">
                  <MessageOutlined/>
                </template>
                <template v-else-if="record.modelType === 'embedding'">
                  <PartitionOutlined/>
                </template>
                <template v-else-if="record.modelType === 'image'">
                  <PictureOutlined/>
                </template>
              </div>
            </template>

            <template v-else-if="column.key === 'providerAvatar'">
              <img
                  v-if="providerAvatarCell(record)"
                  :alt="record.provider"
                  :src="providerAvatarCell(record)"
                  aria-hidden="true"
                  class="provider-avatar-cell"
              />
              <span v-else class="text-secondary">—</span>
            </template>

            <template v-else-if="column.key === 'modelName'">
              <div class="model-info">
                <div class="model-header">
                  <span class="model-title">{{ record.modelName }}</span>
                </div>
              </div>
            </template>

            <template v-else-if="column.key === 'status'">
              <div class="status-cell">
                <a-button
                    class="status-indicator"
                    size="small"
                    type="text"
                    @click="handleStatusChange(record.id, record.status !== 'enabled')"
                >
                  <template #icon>
                    <check-circle-outlined v-if="record.status === 'enabled'" style="color: #52c41a"/>
                    <close-circle-outlined v-else style="color: #ff4d4f"/>
                  </template>
                  <span :style="{ color: record.status === 'enabled' ? '#52c41a' : '#ff4d4f' }">
                        {{ record.status === 'enabled' ? t.list.statusEnabled : t.list.statusDisabled }}
                      </span>
                </a-button>

              </div>
            </template>


            <template v-else-if="column.key === 'runtime'">
              <div class="runtime-meta">
                    <span class="runtime-chip">
                      <thunderbolt-outlined class="cell-icon"/>
                      {{ t.list.runtimeQuota }} {{ record.maxQuotaTokens ?? 0 }}
                    </span>
                <span class="runtime-chip">{{ t.list.runtimeWeight }} {{ record.randomIndex ?? 0 }}</span>
                <span class="runtime-chip">{{ t.list.runtimeVariance }} {{ record.topVariance ?? 0 }}</span>
              </div>
            </template>


            <template v-else-if="column.key === 'actions'">
              <a-space>
                <a-popconfirm :title="t.list.confirmGenerateInstance" @confirm="() => handleGenerateInstances([record.id])">
                  <a-button size="small" type="link">
                    <swap-outlined/>
                  </a-button>
                </a-popconfirm>
                <a-button size="small" type="link" @click="openEdit(record)">
                  <edit-outlined/>
                </a-button>
                <a-popconfirm
                    :title="t.list.confirmRemoveModel"
                    @confirm="() => handleDeleteOne(record)"
                >
                  <a-button danger size="small" type="link">
                    <delete-outlined/>
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

      <ModelForm
          v-model:open="modal.open"
          :confirm-loading="modal.submitting"
          :initial-data="modalInitialData"
          :mode="modal.mode"
          :status-options="statusOptions"
          :submit-handler="handleFormSubmit"
      />


    </div>
  </AstPageShell>
</template>
<script lang="ts" setup>
import {computed, onMounted, reactive, ref} from 'vue'
import {message, Modal} from 'ant-design-vue'
import {
  CheckCircleOutlined,
  CloseCircleOutlined,
  DeleteOutlined,
  EditOutlined,
  MessageOutlined,
  PartitionOutlined,
  PictureOutlined,
  PlusOutlined,
  SearchOutlined,
  SwapOutlined
} from '@ant-design/icons-vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import AstDataSection from '@/components/home/AstDataSection.vue'
import AstDataView from '@/components/home/AstDataView.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstegmentedButton, {type SegmentedButton} from '@/components/home/AstegmentedButton.vue'
import AstStatusSwitch from '@/components/home/AstStatusSwitch.vue'
import ModelForm from './component/ModelForm.vue'
import ExtensionSelector from '@/views/admin/system-config/system-extension/selector/ExtensionSelector.vue'
import {type AiModel, aiModelApi} from '@/api/aiModel.ts'
import {useDictionary} from '@/locales/dictionary'
import {ensureWorkspaceEnvInStorage} from '@/utils/workspaceHelper.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-model')

const breadcrumbs = computed(() => [
  {title: t.value.list.breadcrumbAiConfig, href: '/admin/ai-config'},
  {title: t.value.list.breadcrumbCurrent},
])

const providerDict = useDictionary('ai-model.provider')
const statusDict = useDictionary('ai-model.status')
const sourceTypeDict = useDictionary('ai-model.sourceType')

const statusOptions = computed(() => statusDict.value.options())
const isDefaultOptions = computed(() => [{label: t.value.list.isDefaultNo, value: 'N'}, {label: t.value.list.isDefaultYes, value: 'Y'}])

const columns = computed(() => [
  {title: t.value.list.columnType, key: 'modelType', width: 60},
  {title: t.value.list.columnProvider, key: 'providerAvatar', width: 80, align: 'center'},
  {title: t.value.list.columnModelInfo, key: 'modelName', width: 180},
  {title: t.value.list.columnModelKey, dataIndex: 'modelKey', key: 'modelKey', width: 150, copyable: true},
  {
    title: t.value.list.columnSource,
    dataIndex: 'sourceType',
    key: 'sourceType',
    width: 100,
    enum: [
      {value: 'plugin', label: t.value.list.sourcePlugin, color: 'purple'},
      {value: 'api', label: t.value.list.sourceApi, color: 'blue'}
    ]
  },
  {title: t.value.list.columnStatus, key: 'status', width: 100},

  {title: t.value.list.columnEnv, dataIndex: 'envCode', key: 'envCode', width: 80, ellipsis: true, tag: true, tagColor: 'blue'},
  {title: t.value.list.columnCreateTime, dataIndex: 'createTime', key: 'createTime', width: 150, dateFormat: true},
  {title: t.value.list.columnCreateUser, dataIndex: 'createUser', key: 'createUser', width: 150},
  {title: t.value.list.columnActions, key: 'actions', width: 140, fixed: 'right'}
])


const getProviderColor = (provider: string) => {
  const colors: Record<string, string> = {
    'openai': 'green',
    'anthropic': 'purple',
    'google': 'orange',
    'deepseek': 'cyan'
  }
  return colors[provider?.toLowerCase()] || 'blue'
}

const getModelTypeLabel = (modelType?: string) => {
  if (modelType === 'embedding') return t.value.list.modelTypeEmbedding
  return t.value.list.modelTypeChat
}


const formatTime = (raw?: string) => {
  if (!raw) return '-'
  return raw.replace('T', ' ').slice(0, 19)
}

const providerAvatarCell = (record: AiModel) => {
  const raw = record?.providerAvatar
  return typeof raw === 'string' && raw.trim() ? raw.trim() : ''
}

const query = reactive<{
  modelName?: string
  extensionCode?: string
  modelType?: 'chat' | 'embedding' | 'image'
  status?: string
}>({})
const list = ref<AiModel[]>([])
const loading = ref(false)
const page = reactive({pageNum: 1, pageSize: 10, total: 0})
const selectedRowKeys = ref<Array<number | string>>([])
const statusUpdatingId = ref<number | string | null>(null)

const handleProviderChange = () => {
  page.pageNum = 1
  void fetchList()
}

const modelTypeTab = computed(() => query.modelType ?? 'all')

const handleModelTypeTabChange = (key: string) => {
  query.modelType = key === 'all' ? undefined : (key as 'chat' | 'embedding' | 'image')
  page.pageNum = 1
  void fetchList()
}

const rowSelection = computed(() => ({
  fixed: true,
  columnWidth: 54,
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys: any) => {
    selectedRowKeys.value = keys
  }
}))
const modal = reactive({open: false, mode: 'create' as any, submitting: false})
const modalInitialData = ref<AiModel | null>(null)

const currentPageIds = computed(() =>
    list.value
        .map((item) => item.id)
        .filter((id): id is number | string => id !== undefined && id !== null)
)

const allCurrentSelected = computed(() => {
  return currentPageIds.value.length > 0 && currentPageIds.value.every((id) => selectedRowKeys.value.includes(id))
})

const partCurrentSelected = computed(() => {
  if (currentPageIds.value.length === 0) return false
  const count = currentPageIds.value.filter((id) => selectedRowKeys.value.includes(id)).length
  return count > 0 && count < currentPageIds.value.length
})

const fetchList = async () => {
  loading.value = true
  try {
    await ensureWorkspaceEnvInStorage()
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        extensionCode: query.extensionCode || undefined,
        modelName: query.modelName || undefined,
        modelType: query.modelType || undefined,
        status: query.status || undefined
      }
    }
    const resp: any = await aiModelApi.queryPage(payload)
    list.value = resp.list || []
    page.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

const onPageChange = (p: number, size: number) => {
  page.pageNum = p
  page.pageSize = size
  fetchList()
}

const toggleStatusFilter = (value: 'enabled' | 'disabled') => {
  query.status = query.status === value ? undefined : value
}

const toggleSelectAllCurrentPage = (checked: boolean) => {
  if (checked) {
    selectedRowKeys.value = Array.from(new Set([...selectedRowKeys.value, ...currentPageIds.value]))
    return
  }
  selectedRowKeys.value = selectedRowKeys.value.filter((id) => !currentPageIds.value.includes(id))
}

const resetFilters = () => {
  query.extensionCode = undefined
  query.modelName = undefined
  query.modelType = undefined
  query.status = undefined
  page.pageNum = 1
  fetchList()
}

const openCreate = () => {
  modal.mode = 'create'
  modalInitialData.value = null
  modal.open = true
}

const toolbarSegmentButtons = computed<SegmentedButton[]>(() => [
  {
    label: t.value.list.btnSearch,
    type: 'primary',
    icon: SearchOutlined,
    plain: true,
    onClick: () => void fetchList()
  },
  {
    label: selectedRowKeys.value.length > 0 ? t.value.list.btnDeleteCount.replace('{count}', String(selectedRowKeys.value.length)) : t.value.list.btnDelete,
    icon: DeleteOutlined,
    type: 'danger',
    plain: true,
    disabled: selectedRowKeys.value.length === 0,
    onClick: () => {
      const n = selectedRowKeys.value.length
      if (n === 0) return
      Modal.confirm({
        title: t.value.list.confirmBatchDeleteTitle.replace('{count}', String(n)),
        onOk: () => handleBatchDelete()
      })
    }
  },
  {
    label: selectedRowKeys.value.length > 0 ? t.value.list.btnGenerateInstanceCount.replace('{count}', String(selectedRowKeys.value.length)) : t.value.list.btnGenerateInstance,
    type: 'primary',
    icon: SwapOutlined,
    disabled: selectedRowKeys.value.length === 0,
    onClick: () => {
      const n = selectedRowKeys.value.length
      if (n === 0) return
      Modal.confirm({
        title: t.value.list.confirmBatchGenerateTitle.replace('{count}', String(n)),
        onOk: () => handleBatchGenerateInstances()
      })
    },
    plain: true
  },
  {
    label: t.value.list.btnCreate,
    type: 'primary',
    icon: PlusOutlined,
    onClick: openCreate,
    plain: true
  }
])

const openView = async (record: AiModel) => {
  modal.mode = 'view'
  if (record.id) {
    const detail = await aiModelApi.detail(record.id)
    modalInitialData.value = detail
    modal.open = true
  }
}

const openEdit = async (record: AiModel) => {
  modal.mode = 'edit'
  if (record.id) {
    const detail = await aiModelApi.detail(record.id)
    modalInitialData.value = detail
    modal.open = true
  }
}

const handleDeleteOne = async (record: AiModel) => {

  const msg = await aiModelApi.delete([record.id as number | string])
  message.success(msg)
  selectedRowKeys.value = selectedRowKeys.value.filter((key) => key !== record.id)
  fetchList()
}

const handleBatchDelete = async () => {
  const selectedModels = list.value.filter((item) => selectedRowKeys.value.includes(item.id as number | string))
  await aiModelApi.delete([...selectedRowKeys.value])
  message.success(t.value.list.msgDeleteSuccess)
  selectedRowKeys.value = []
  fetchList()
}

const handleGenerateInstances = async (ids: Array<number | string | undefined>) => {
  const validIds = ids.filter((id): id is number | string => id !== undefined && id !== null)
  if (validIds.length === 0) return
  const msg = await aiModelApi.generateInstances(validIds)
  message.success(msg || t.value.list.msgInstanceGenerated)
  fetchList()
}

const handleBatchGenerateInstances = async () => {
  await handleGenerateInstances([...selectedRowKeys.value])
}

const handleStatusChange = async (id: number | string, checked: boolean) => {
  statusUpdatingId.value = id
  try {
    await aiModelApi.update({id, status: checked ? 'enabled' : 'disabled'})
    message.success(t.value.list.msgStatusUpdateSuccess)
    fetchList()
  } catch (e) {
    message.error(t.value.list.msgStatusUpdateFailed)
  } finally {
    statusUpdatingId.value = null
  }
}

const handleStatusSwitchChange = (id: number | string, checked: boolean) => {
  void handleStatusChange(id, checked)
}

const handleFormSubmit = async (payload: AiModel) => {
  modal.submitting = true
  try {
    modal.mode === 'create' ? await aiModelApi.create(payload) : await aiModelApi.update(payload)
    message.success(t.value.list.msgOperationSuccess)
    modal.open = false
    fetchList()
  } finally {
    modal.submitting = false
  }
}

onMounted(() => {
  void fetchList()
})
</script>

<style scoped>
.model-page-container {
  display: flex;
  flex-direction: column;
  gap: 0;
  padding: 20px;
  margin-top: -8px;
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

.status-switch {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 4px;
  border-radius: var(--radius-max);
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
}

.status-btn {
  height: 36px;
  border: none;
  border-radius: var(--radius-max);
  color: var(--text-secondary);
  background: transparent;
  box-shadow: none;
}

.status-btn.active {
  color: var(--primary);
  background: color-mix(in srgb, var(--primary) 10%, var(--bg-card));
}

.table-card {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  min-height: var(--model-list-panel-max-height);
  max-height: var(--model-list-panel-max-height);
  overflow: hidden;
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: 10px;
  border: 1px solid var(--border-default);
  box-shadow: 0 16px 32px color-mix(in srgb, var(--text-primary) 4%, transparent);
}

.table-card-scroll {
  flex: 1;
  min-height: 0;
  overflow: auto;
}

.table-card .pagination-container {
  flex-shrink: 0;
}

.model-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.model-header {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
}

.model-icon {
  width: 24px;
  height: 24px;
  border-radius: var(--radius-max);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: white;
  flex-shrink: 0;
  margin: 0 auto;
}

.model-icon.chat {
  background: linear-gradient(135deg, #0061ff, #60efff);
}

.model-icon.embedding {
  background: linear-gradient(135deg, #7c4dff, #f94dff);
}

.model-icon.image {
  background: linear-gradient(135deg, #ff6b6b, #ffd93d);
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

.model-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-heading);
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.model-meta {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-wrap: wrap;
}


.provider-tag {
  font-size: 11px;
  height: 20px;
  line-height: 20px;
  margin: 0;
}

.code-text {
  font-family: monospace;
  background: color-mix(in srgb, var(--primary) 6%, var(--bg-card));
  padding: 4px 8px;
  border-radius: var(--radius-max);
  color: var(--primary);
  font-size: 13px;
}

.api-url-cell,
.created-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.api-url-text {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--text-secondary);
}


.capability-tag {
  margin-inline-end: 0;
  border-radius: 999px;
}


.runtime-chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 3px 8px;
  border-radius: 999px;
  border: 1px solid var(--border-default);
  background: var(--bg-surface);
  color: var(--text-secondary);
  font-size: 12px;
}

.created-line {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--text-secondary);
}

.cell-icon {
  color: var(--primary);
}

.cell-icon.subtle {
  color: color-mix(in srgb, var(--primary) 72%, var(--text-secondary));
}

.table-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.status-cell {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.status-indicator {
  padding-inline: 0;
}

.table-actions :deep(.ant-btn-link) {
  padding: 0 4px;
  height: 28px;
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 8px 8px;
}


:deep(.ant-table-tbody > tr > td) {
  padding: 8px 12px;
}

:deep(.ant-table-thead > tr > th) {
  padding: 10px 12px;
  font-size: 12px;
}

.total-text {
  color: var(--text-muted);
  font-size: 13px;
}

.text-secondary {
  color: var(--text-muted);
}

:deep(.ant-table-thead > tr > th) {
  background: color-mix(in srgb, var(--bg-surface) 82%, var(--bg-card));
  font-weight: 600;
  color: var(--text-secondary);
  border-bottom: 1px solid var(--border-default);
  padding-top: 14px;
  padding-bottom: 14px;
}

:deep(.ant-table-tbody > tr > td) {
  padding-top: 14px;
  padding-bottom: 14px;
  border-bottom: 1px solid color-mix(in srgb, var(--border-default) 82%, var(--bg-card));
  vertical-align: middle;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background: color-mix(in srgb, var(--primary) 3%, var(--bg-card)) !important;
}

:deep(.ant-table-cell-fix-left),
:deep(.ant-table-cell-fix-right) {
  background: var(--bg-card);
}

:deep(.ant-table-selection-column) {
  text-align: center;
}

:deep(.ant-table-container) {
  border-radius: 14px;
  overflow: hidden;
}

:deep(.ant-table-tbody > tr.ant-table-row-selected > td) {
  background: color-mix(in srgb, var(--primary) 6%, var(--bg-card));
}

@media (max-width: 720px) {
  .toolbar {
    padding: 14px;
  }

  .toolbar-input,
  .search-main-input,
  .toolbar-select,
  .provider-select {
    width: 100%;
  }

  .toolbar-left,
  .toolbar-right,
  .search-cluster {
    width: 100%;
  }

  .status-switch {
    width: 100%;
    justify-content: space-between;
  }

  .status-btn {
    flex: 1;
  }
}
</style>
