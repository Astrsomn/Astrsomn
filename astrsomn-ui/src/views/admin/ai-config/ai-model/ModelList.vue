<template>
  <AdminPageShell
    title="模型配置"
    description="统一管理 AI 模型供应商、版本及路由策略，支持快捷开关状态。"
  >
    <div class="model-page-container">
      <div class="toolbar">
        <div class="toolbar-left">
          <div class="search-cluster">
            <a-input
              v-model:value="query.modelName"
              placeholder="搜索模型名称"
              class="toolbar-input search-main-input"
              allow-clear
              @pressEnter="fetchList"
            >
              <template #prefix><search-outlined /></template>
            </a-input>

            <a-select
              v-model:value="query.provider"
              :options="providerOptions"
              placeholder="所有供应商"
              class="toolbar-select provider-select"
              allow-clear
            />
          </div>

          <div class="status-switch" role="group" aria-label="状态筛选">
            <a-button
              class="status-btn"
              :class="{ active: query.status === 'enabled' }"
              @click="toggleStatusFilter('enabled')"
            >
              启用
            </a-button>
            <a-button
              class="status-btn"
              :class="{ active: query.status === 'disabled' }"
              @click="toggleStatusFilter('disabled')"
            >
              禁用
            </a-button>
          </div>
        </div>

        <div class="toolbar-right">
          <a-button type="primary" class="primary-btn" @click="fetchList">
            <template #icon><search-outlined /></template>
            查询
          </a-button>
          <a-popconfirm
            v-if="selectedRowKeys.length > 0"
            :title="`确定删除选中的 ${selectedRowKeys.length} 个模型吗？`"
            @confirm="handleBatchDelete"
          >
            <a-button danger class="ghost-btn danger-btn">
              <template #icon><delete-outlined /></template>
              批量删除
            </a-button>
          </a-popconfirm>
          <a-button class="ghost-btn" @click="resetFilters">重置</a-button>
          <a-button type="primary" class="ghost-btn add-btn" @click="openCreate">
            <template #icon><plus-outlined /></template>
            新增模型
          </a-button>
        </div>
      </div>

      <BaseOverview
        :list-length="list.length"
        :selected-count="selectedRowKeys.length"
        :all-current-selected="allCurrentSelected"
        :part-current-selected="partCurrentSelected"
        :show-actions="list.length > 0"
        :summary-text="`当前页 ${list.length} 条模型记录，已选 ${selectedRowKeys.length} 条。`"
        @toggle-select-all="toggleSelectAllCurrentPage"
      />

      <div class="table-card">
        <a-table
          :columns="columns"
          :data-source="list"
          :pagination="false"
          row-key="id"
          :row-selection="rowSelection"
          :scroll="{ x: 1680 }"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'modelName'">
              <div class="model-info">
                <span class="model-title">{{ record.modelName }}</span>
                <span class="model-type-tag">{{ getModelTypeLabel(record.modelType) }}</span>
              </div>
            </template>

            <template v-else-if="column.key === 'provider'">
              <a-tag :color="getProviderColor(record.provider)">
                {{ providerDict.getLabel(String(record.provider || '')) ?? record.provider }}
              </a-tag>
            </template>

            <template v-else-if="column.key === 'status'">
              <a-badge 
                :status="record.status === 'enabled' ? 'success' : 'default'" 
                :text="statusDict.getLabel(String(record.status || ''))" 
              />
            </template>

            <template v-else-if="column.key === 'isDefault'">
              <a-tag v-if="record.isDefault === 1" color="blue">默认模型</a-tag>
              <span v-else class="text-secondary">-</span>
            </template>

            <template v-else-if="column.key === 'modelKey'">
              <code class="code-text">{{ record.modelKey }}</code>
            </template>

            <template v-else-if="column.key === 'apiUrl'">
              <div class="api-url-cell" :title="record.apiUrl || '-'">
                <span class="created-line">
                  <global-outlined class="cell-icon subtle" />
                  <span class="api-url-text">{{ record.apiUrl || '-' }}</span>
                </span>
              </div>
            </template>

            <template v-else-if="column.key === 'capabilities'">
              <div class="capability-list">
                <a-tag
                  v-for="capability in parseCapabilities(record.capabilities).slice(0, 3)"
                  :key="capability"
                  class="capability-tag"
                  color="processing"
                >
                  {{ formatCapabilityLabel(String(capability)) }}
                </a-tag>
                <span v-if="parseCapabilities(record.capabilities).length === 0" class="text-secondary">-</span>
                <span
                  v-else-if="parseCapabilities(record.capabilities).length > 3"
                  class="capability-more"
                >
                  +{{ parseCapabilities(record.capabilities).length - 3 }}
                </span>
              </div>
            </template>

            <template v-else-if="column.key === 'runtime'">
              <div class="runtime-meta">
                <span class="runtime-chip">
                  <thunderbolt-outlined class="cell-icon" />
                  {{ record.responseLimit ?? 0 }} tokens
                </span>
                <span class="runtime-chip">权重 {{ record.randomIndex ?? 0 }}</span>
                <span class="runtime-chip">Top {{ record.topVariance ?? 0 }}</span>
              </div>
            </template>

            <template v-else-if="column.key === 'createdMeta'">
              <div class="created-meta">
                <span class="created-line">
                  <user-outlined class="cell-icon subtle" />
                  {{ record.createUser || '-' }}
                </span>
                <span class="created-line">
                  <calendar-outlined class="cell-icon subtle" />
                  {{ formatTime(record.createTime) }}
                </span>
              </div>
            </template>

            <template v-else-if="column.key === 'actions'">
              <div class="table-actions">
                <a-button type="link" size="small" @click="openEdit(record)">
                  <template #icon><edit-outlined /></template>
                  编辑
                </a-button>
                <a-divider type="vertical" />
                <a-popconfirm title="删除后不可恢复，确定吗？" @confirm="() => handleDeleteOne(record.id)">
                  <a-button type="link" size="small" danger>
                    <template #icon><delete-outlined /></template>
                    删除
                  </a-button>
                </a-popconfirm>
              </div>
            </template>
          </template>
        </a-table>

        <div class="pagination-container">
          <span class="total-text">共 {{ page.total }} 条记录</span>
          <a-pagination
            v-model:current="page.pageNum"
            :page-size="page.pageSize"
            :total="page.total"
            size="small"
            show-less-items
            @change="onPageChange"
          />
        </div>
      </div>

      <ModelFormModal
        v-model:open="modal.open"
        :mode="modal.mode"
        :confirm-loading="modal.submitting"
        :initial-data="modalInitialData"
        :provider-options="providerOptions"
        :status-options="statusOptions"
        :is-default-options="isDefaultOptions"
        :submit-handler="handleFormSubmit"
      />
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { 
  CalendarOutlined,
  DeleteOutlined,
  EditOutlined,
  GlobalOutlined,
  PlusOutlined,
  SearchOutlined,
  ThunderboltOutlined,
  UserOutlined
} from '@ant-design/icons-vue'
import AdminPageShell from '@/views/admin/components/admin/AdminPageShell.vue'
import BaseOverview from '@/views/admin/components/admin/BaseOverview.vue'
import ModelFormModal from './ModelFormModal.vue'
import { aiModelApi, type AiModel } from '@/api/aiModel.ts'
import { useDictionary } from '@/locales/dictionary'

// ... (逻辑部分基本保持与原代码一致，新增工具函数)

const providerDict = useDictionary('ai-model.provider')
const statusDict = useDictionary('ai-model.status')
const capabilitiesDict = useDictionary('ai-model.capabilities')

const providerOptions = computed(() => providerDict.value.options())
const statusOptions = computed(() => statusDict.value.options())
const isDefaultOptions = [{ label: '否', value: 0 }, { label: '是', value: 1 }]

const columns = [
  { title: '模型信息', key: 'modelName', fixed: 'left', width: 240 },
  { title: '标识 Key', key: 'modelKey', width: 190 },
  { title: '供应商', key: 'provider', width: 120 },
  { title: '状态', key: 'status', width: 100 },
  { title: '属性', key: 'isDefault', width: 100 },
  { title: '接口地址', key: 'apiUrl', width: 240 },
  { title: '能力标签', key: 'capabilities', width: 220 },
  { title: '运行参数', key: 'runtime', width: 220 },
  { title: '创建信息', key: 'createdMeta', width: 190 },
  { title: '操作', key: 'actions', fixed: 'right', width: 170 }
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

const parseCapabilities = (raw?: string) => {
  if (!raw) return []
  try {
    const parsed = JSON.parse(raw)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

const formatCapabilityLabel = (capability: string) => {
  return capabilitiesDict.value.getLabel(capability) ?? capability
}

const formatTime = (raw?: string) => {
  if (!raw) return '-'
  return raw.replace('T', ' ').slice(0, 19)
}

// ... 逻辑部分保持原样 ...
const query = reactive<any>({})
const list = ref<AiModel[]>([])
const page = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const selectedRowKeys = ref<Array<number | string>>([])
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
}

const onPageChange = (p: number) => {
  page.pageNum = p
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

fetchList()
</script>

<style scoped>
.model-page-container {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 6px;
  flex-wrap: wrap;
  padding: 16px;
  border-radius: 20px;
  background: var(--bg-card);
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

.search-cluster {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
  padding: 6px;
  border-radius: 16px;
  border: 1px solid var(--border-default);
  background: var(--bg-surface);
}

.search-cluster :deep(.ant-input-affix-wrapper),
.search-cluster :deep(.ant-select-selector) {
  border: none !important;
  box-shadow: none !important;
  background: transparent !important;
}

.search-cluster :deep(.ant-input-affix-wrapper:hover),
.search-cluster :deep(.ant-input-affix-wrapper-focused),
.search-cluster :deep(.ant-select-focused .ant-select-selector),
.search-cluster :deep(.ant-select-selector:hover) {
  background: color-mix(in srgb, var(--bg-card) 85%, var(--bg-surface)) !important;
}

.toolbar-input {
  width: 200px;
}

.search-main-input {
  width: 300px;
}

.toolbar-select {
  width: 160px;
}

.provider-select {
  width: 160px;
}

.primary-btn,
.ghost-btn {
  height: 40px;
  border-radius: 12px;
}

.danger-btn {
  color: var(--error);
  border-color: color-mix(in srgb, var(--error) 28%, var(--border-default));
  background: color-mix(in srgb, var(--error) 7%, var(--bg-card));
}

.danger-btn:hover,
.danger-btn:focus {
  color: var(--error) !important;
  border-color: color-mix(in srgb, var(--error) 42%, var(--border-default)) !important;
  background: color-mix(in srgb, var(--error) 12%, var(--bg-card)) !important;
}

.status-switch {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 4px;
  border-radius: 14px;
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
}

.status-btn {
  height: 36px;
  border: none;
  border-radius: 10px;
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
  background: var(--bg-card);
  border-radius: 20px;
  padding: 10px;
  border: 1px solid var(--border-default);
  box-shadow: 0 16px 32px rgba(15, 23, 42, 0.04);
}

.model-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.model-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-heading);
}

.model-type-tag {
  font-size: 11px;
  color: var(--text-secondary);
  background: var(--bg-surface);
  padding: 2px 8px;
  width: fit-content;
  border-radius: 999px;
  border: 1px solid var(--border-default);
}

.code-text {
  font-family: monospace;
  background: color-mix(in srgb, var(--primary) 6%, var(--bg-card));
  padding: 4px 8px;
  border-radius: 8px;
  color: var(--primary);
  font-size: 13px;
}

.api-url-cell,
.created-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.api-url-text {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--text-secondary);
}

.capability-list,
.runtime-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.capability-tag {
  margin-inline-end: 0;
  border-radius: 999px;
}

.capability-more {
  font-size: 12px;
  color: var(--text-secondary);
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

.table-actions :deep(.ant-btn-link) {
  padding: 0 4px;
  height: 28px;
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 8px 8px;
}

.total-text {
  color: #8c8c8c;
  font-size: 13px;
}

.text-secondary { color: #bfbfbf; }

:deep(.ant-table-thead > tr > th) {
  background: color-mix(in srgb, var(--bg-surface) 82%, white);
  font-weight: 600;
  color: var(--text-secondary);
  border-bottom: 1px solid var(--border-default);
  padding-top: 14px;
  padding-bottom: 14px;
}

:deep(.ant-table-tbody > tr > td) {
  padding-top: 14px;
  padding-bottom: 14px;
  border-bottom: 1px solid color-mix(in srgb, var(--border-default) 82%, white);
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