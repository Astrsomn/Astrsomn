<template>
  <AdminPageShell
      title="接入端点管理"
      description="统一管理 AI 模型供应商、接入地址及路由策略，为上层实例提供底座支持。"
      :breadcrumbs="breadcrumbs"
  >
    <div class="model-page-container">
      <AstrsomnDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstrsomnSearchPill
                v-model="query.modelName"
                layout="toolbar"
                placeholder="搜索端点名称"
                @search="fetchList"
              />
              <ExtensionSelector
                v-model:value="query.provider"
                class="toolbar-provider-select"
                allow-clear
                @update:value="handleProviderChange"
              />
              <AstrsomnStateSwitch v-model="query.status" @change="fetchList" />
            </div>
            <div class="toolbar-right">
              <AstrsomnSegmentedButton :buttons="toolbarSegmentButtons" />
            </div>
          </div>
        </template>



        <AstrsomnDataView
          mode="table"
          :data-source="list"
          :loading="loading"
          :columns="columns"
          :row-selection="rowSelection"
          :scroll="{ x: 1200 }"
          row-key="id"
          empty-text="暂无匹配的接入端点"
        >
          <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'modelType'">
                  <div class="model-icon" :class="record.modelType">
                    <template v-if="record.modelType === 'chat'"><MessageOutlined /></template>
                    <template v-else-if="record.modelType === 'embedding'"><PartitionOutlined /></template>
                    <template v-else-if="record.modelType === 'image'"><PictureOutlined /></template>
                  </div>
                </template>

                <template v-else-if="column.key === 'providerAvatar'">
                  <img
                    v-if="providerAvatarCell(record)"
                    class="provider-avatar-cell"
                    :src="providerAvatarCell(record)"
                    :alt="record.provider"
                    aria-hidden="true"
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
                        type="text"
                        size="small"
                        class="status-indicator"
                        @click="handleStatusChange(record.id, record.status !== 'enabled')"
                    >
                      <template #icon>
                        <check-circle-outlined v-if="record.status === 'enabled'" style="color: #52c41a" />
                        <close-circle-outlined v-else style="color: #ff4d4f" />
                      </template>
                      <span :style="{ color: record.status === 'enabled' ? '#52c41a' : '#ff4d4f' }">
                        {{ record.status === 'enabled' ? '启用' : '禁用' }}
                      </span>
                    </a-button>
                
                  </div>
                </template>

                <template v-else-if="column.key === 'isDefault'">
                  <a-tag v-if="record.isDefault === 1" color="blue">默认端点</a-tag>
                  <span v-else class="text-secondary">-</span>
                </template>





                <template v-else-if="column.key === 'runtime'">
                  <div class="runtime-meta">
                    <span class="runtime-chip">
                      <thunderbolt-outlined class="cell-icon" />
                      限额 {{ record.maxQuotaTokens ?? 0 }}
                    </span>
                    <span class="runtime-chip">权重 {{ record.randomIndex ?? 0 }}</span>
                    <span class="runtime-chip">离散度 {{ record.topVariance ?? 0 }}</span>
                  </div>
                </template>

       

                <template v-else-if="column.key === 'actions'">
                  <a-space>
                    <a-button type="link" size="small" @click="openView(record)">
                      <eye-outlined />
                    </a-button>
                    <a-button type="link" size="small" @click="openEdit(record)">
                      <edit-outlined />
                    </a-button>
                    <a-popconfirm title="移除端点将影响下游关联实例，确定吗？" @confirm="() => handleDeleteOne(record.id)">
                      <a-button type="link" size="small" danger>
                        <delete-outlined />
                      </a-button>
                    </a-popconfirm>
                  </a-space>
                </template>
          </template>
        </AstrsomnDataView>

        <template #pagination>
          <AstrsomnPagination
            :current="page.pageNum"
            :page-size="page.pageSize"
            :total="page.total"
            @change="onPageChange"
          />
        </template>
      </AstrsomnDataSection>

      <ModelFormModal
          v-model:open="modal.open"
          :mode="modal.mode"
          :confirm-loading="modal.submitting"
          :initial-data="modalInitialData"
          :status-options="statusOptions"
          :is-default-options="isDefaultOptions"
          :submit-handler="handleFormSubmit"
      />


    </div>
  </AdminPageShell>
</template>
<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { 
  CalendarOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  DeleteOutlined,
  EditOutlined,
  GlobalOutlined,
  MessageOutlined,
  PartitionOutlined,
  PictureOutlined,
  PlusOutlined,
  SearchOutlined,
  UserOutlined
} from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'
import AstrsomnStateSwitch from '@/components/home/AstrsomnStateSwitch.vue'
import ModelFormModal from './ModelFormModal.vue'
import ExtensionSelector from '../../system-config/system-extension/selectors/ExtensionSelector.vue'
import { aiModelApi, type AiModel } from '@/api/aiModel.ts'
import { useDictionary } from '@/locales/dictionary'
import { ensureWorkspaceEnvInStorage } from '@/utils/ensureWorkspaceEnvStorage'

// ... (逻辑部分基本保持与原代码一致，新增工具函数)

const breadcrumbs = [
  { title: 'AI 配置', href: '/admin/ai-config' },
  { title: '接入端点管理' },
]

const providerDict = useDictionary('ai-model.provider')
const statusDict = useDictionary('ai-model.status')
const sourceTypeDict = useDictionary('ai-model.sourceType')

const statusOptions = computed(() => statusDict.value.options())
const isDefaultOptions = [{ label: '否', value: 0 }, { label: '是', value: 1 }]

const columns = [
  { title: '类型', key: 'modelType', width: 60 },
  { title: '供应商', key: 'providerAvatar', width: 80, align: 'center' },
  { title: '模型信息', key: 'modelName', width: 180 },
  { title: '模型Key', dataIndex: 'modelKey', key: 'modelKey', width: 150, copyable: true },
  { 
    title: '来源', 
    dataIndex: 'sourceType', 
    key: 'sourceType', 
    width: 100, 
    enum: [
      { value: 'plugin', label: '插件', color: 'purple' },
      { value: 'api', label: 'API', color: 'blue' }
    ] 
  },
  { title: '状态', key: 'status', width: 100 },
  { 
    title: '接口地址', 
    dataIndex: 'apiUrl', 
    key: 'apiUrl', 
    width: 100, 
    tag: true,
    tagColor: (value: string) => value ? 'green' : 'default',
    tagText: (value: string) => value ? '已配置' : '未配置'
  },
  {title: '环境', dataIndex: 'envCode', key: 'envCode', width: 80, ellipsis: true, tag: true, tagColor: 'blue'},
  {title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 150, dateFormat: true},
  {title: '创建人', dataIndex: 'createUser', key: 'createUser', width: 150},
  { title: '操作', key: 'actions', width: 140, fixed: 'right' }
]

// 简单的颜色映射逻辑
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
  if (modelType === 'embedding') return '向量模型'
  return '对话模型'
}



const formatTime = (raw?: string) => {
  if (!raw) return '-'
  return raw.replace('T', ' ').slice(0, 19)
}

const providerAvatarCell = (record: AiModel) => {
  const raw = record?.providerAvatar
  return typeof raw === 'string' && raw.trim() ? raw.trim() : ''
}

const query = reactive<{ modelName?: string; provider?: string; status?: string }>({})
const list = ref<AiModel[]>([])
const loading = ref(false)
const page = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const selectedRowKeys = ref<Array<number | string>>([])
const statusUpdatingId = ref<number | string | null>(null)

const handleProviderChange = () => {
  page.pageNum = 1
  void fetchList()
}

const rowSelection = computed(() => ({
  fixed: true,
  columnWidth: 54,
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys: any) => { selectedRowKeys.value = keys }
}))
const modal = reactive({ open: false, mode: 'create' as any, submitting: false })
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
        supplier: query.provider || undefined,
        modelName: query.modelName || undefined,
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
  query.provider = undefined
  query.modelName = undefined
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
    label: '搜索',
    type: 'primary',
    icon: SearchOutlined,
    plain: true,
    onClick: () => void fetchList()
  },
  {
    label: selectedRowKeys.value.length > 0 ? `删除 (${selectedRowKeys.value.length})` : '删除',
    icon: DeleteOutlined,
    disabled: selectedRowKeys.value.length === 0,
    onClick: () => {
      const n = selectedRowKeys.value.length
      if (n === 0) return
      Modal.confirm({
        title: `确定删除选中的 ${n} 个接入端点吗？`,
        onOk: () => handleBatchDelete()
      })
    }
  },
  {
    label: '创建',
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

const handleDeleteOne = async (id: any) => {
  const msg = await aiModelApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = selectedRowKeys.value.filter((key) => key !== id)
  fetchList()
}

const handleBatchDelete = async () => {
  await aiModelApi.delete([...selectedRowKeys.value])
  message.success('删除成功')
  selectedRowKeys.value = []
  fetchList()
}

const handleStatusChange = async (id: number | string, checked: boolean) => {
  statusUpdatingId.value = id
  try {
    await aiModelApi.update({ id, status: checked ? 'enabled' : 'disabled' })
    message.success('状态更新成功')
    fetchList()
  } catch (e) {
    message.error('状态更新失败')
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
    message.success('操作成功')
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
  min-height:  var(--model-list-panel-max-height);
  max-height: var(--model-list-panel-max-height);
  overflow: hidden;
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: 10px;
  border: 1px solid var(--border-default);
  box-shadow: 0 16px 32px rgba(15, 23, 42, 0.04);
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

/* 压缩表格行高 */
:deep(.ant-table-tbody > tr > td) {
  padding: 8px 12px;
}

:deep(.ant-table-thead > tr > th) {
  padding: 10px 12px;
  font-size: 12px;
}

.total-text {
  color: var(--text-muted, #8c8c8c);
  font-size: 13px;
}

.text-secondary { color: var(--text-muted, #bfbfbf); }

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