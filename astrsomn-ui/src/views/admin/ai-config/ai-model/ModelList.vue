<template>
  <AdminPageShell
    title="模型配置"
    description="统一管理 AI 模型供应商、版本及路由策略，支持快捷开关状态。"
  >
    <div class="model-page-container">
      <div class="glass-toolbar">
        <div class="search-group">
          <a-input
            v-model:value="query.modelName"
            placeholder="搜索模型名称"
            class="search-input"
            allow-clear
            @pressEnter="fetchList"
          >
            <template #prefix><search-outlined /></template>
          </a-input>
          
          <a-select
            v-model:value="query.provider"
            :options="providerOptions"
            placeholder="所有供应商"
            class="filter-select"
            allow-clear
            @change="fetchList"
          />
          
          <a-select
            v-model:value="query.status"
            :options="statusOptions"
            placeholder="状态过滤"
            class="filter-select"
            allow-clear
            @change="fetchList"
          />

          <a-button type="primary" @click="fetchList">
            <template #icon><filter-outlined /></template>
            筛选
          </a-button>
        </div>

        <div class="action-group">
          <a-popconfirm
            v-if="selectedRowKeys.length > 0"
            :title="`确定删除选中的 ${selectedRowKeys.length} 个模型吗？`"
            @confirm="handleBatchDelete"
          >
            <a-button danger ghost>批量删除</a-button>
          </a-popconfirm>
          
          <a-button type="primary" class="add-btn" @click="openCreate">
            <template #icon><plus-outlined /></template>
            新增模型
          </a-button>
        </div>
      </div>

      <div class="table-card">
        <a-table
          :columns="columns"
          :data-source="list"
          :pagination="false"
          row-key="id"
          :row-selection="rowSelection"
          :scroll="{ x: 1000 }"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'modelName'">
              <div class="model-info">
                <span class="model-title">{{ record.modelName }}</span>
                <span class="model-type-tag">{{ record.modelType }}</span>
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
  SearchOutlined, PlusOutlined, FilterOutlined, 
  EditOutlined, DeleteOutlined 
} from '@ant-design/icons-vue'
import AdminPageShell from '@/views/admin/components/admin/AdminPageShell.vue'
import ModelFormModal from './ModelFormModal.vue'
import { aiModelApi, type AiModel, type PageResponse } from '@/api/aiModel.ts'
import { useDictionary } from '@/locales/dictionary'

// ... (逻辑部分基本保持与原代码一致，新增工具函数)

const providerDict = useDictionary('ai-model.provider')
const statusDict = useDictionary('ai-model.status')

const providerOptions = computed(() => providerDict.value.options())
const statusOptions = computed(() => statusDict.value.options())
const isDefaultOptions = [{ label: '否', value: 0 }, { label: '是', value: 1 }]

const columns = [
  { title: '模型信息', key: 'modelName', fixed: 'left', width: 220 },
  { title: '标识 Key', key: 'modelKey', width: 180 },
  { title: '供应商', key: 'provider', width: 120 },
  { title: '状态', key: 'status', width: 100 },
  { title: '属性', key: 'isDefault', width: 100 },
  { title: '限流(tokens)', dataIndex: 'responseLimit', key: 'responseLimit', width: 120 },
  { title: '操作', key: 'actions', fixed: 'right', width: 160 }
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

// ... 逻辑部分保持原样 ...
const query = reactive<any>({})
const list = ref<AiModel[]>([])
const page = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const selectedRowKeys = ref<Array<number | string>>([])
const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys: any) => { selectedRowKeys.value = keys }
}))
const modal = reactive({ open: false, mode: 'create' as any, submitting: false })
const modalInitialData = ref<AiModel | null>(null)

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
  gap: 16px;
}

/* 毛玻璃质感的工具栏 */
.glass-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.search-group {
  display: flex;
  gap: 8px;
}

.search-input { width: 220px; }
.filter-select { width: 140px; }

.action-group {
  display: flex;
  gap: 12px;
}

/* 表格卡片化 */
.table-card {
  background: #fff;
  border-radius: 12px;
  padding: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
}

.model-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.model-title {
  font-weight: 600;
  color: #1a1a1a;
}

.model-type-tag {
  font-size: 11px;
  color: #8c8c8c;
  background: #f5f5f5;
  padding: 0 4px;
  width: fit-content;
  border-radius: 4px;
}

.code-text {
  font-family: monospace;
  background: #f8f9fa;
  padding: 2px 6px;
  border-radius: 4px;
  color: #d4380d;
  font-size: 13px;
}

.table-actions :deep(.ant-btn-link) {
  padding: 0 4px;
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 8px;
}

.total-text {
  color: #8c8c8c;
  font-size: 13px;
}

.text-secondary { color: #bfbfbf; }

:deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-weight: 600;
}
</style>