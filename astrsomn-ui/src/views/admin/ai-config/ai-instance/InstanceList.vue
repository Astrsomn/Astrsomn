<template>
  <AdminPageShell
      title="推理参数配置"
      description="管理 AI 运行预设：定义采样温度、长度限制及生成策略，供智能体（Agents）直接引用。"
      empty-text="暂无推理预设实例。"
  >
    <div class="config-page">
      <AdminListToolbar>
        <template #left>
          <div class="search-cluster">
            <a-input
                v-model:value="query.instanceKey"
                placeholder="配置唯一标识 (Key)"
                class="toolbar-input search-main-input"
                allow-clear
                @pressEnter="fetchList"
            >
              <template #prefix><search-outlined /></template>
            </a-input>
            <a-input
                v-model:value="query.instanceName"
                placeholder="预设名称"
                class="toolbar-input search-sub-input"
                allow-clear
                @pressEnter="fetchList"
            >
              <template #prefix><search-outlined /></template>
            </a-input>
            <a-input
                v-model:value="query.modelKey"
                placeholder="关联端点 Key"
                class="toolbar-input search-sub-input"
                allow-clear
                @pressEnter="fetchList"
            >
              <template #prefix><search-outlined /></template>
            </a-input>
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
        </template>

        <template #right>
          <a-button type="primary" class="primary-btn" @click="fetchList">
            <template #icon><search-outlined /></template>
            查询
          </a-button>
          <a-popconfirm
              v-if="selectedRowKeys.length > 0"
              title="确定批量移除选中的推理配置吗？"
              ok-text="确认"
              cancel-text="取消"
              @confirm="handleBatchDelete"
          >
            <a-button danger class="ghost-btn danger-btn">
              <template #icon><delete-outlined /></template>
              批量删除
            </a-button>
          </a-popconfirm>
          <a-button class="ghost-btn" @click="resetFilters">重置</a-button>
          <a-button class="ghost-btn add-btn" @click="goCreate">
            <template #icon><plus-outlined /></template>
            新增配置
          </a-button>
        </template>
      </AdminListToolbar>

      <BaseOverview
          :list-length="list.length"
          :selected-count="selectedRowKeys.length"
          :all-current-selected="allCurrentSelected"
          :part-current-selected="partCurrentSelected"
          :show-actions="list.length > 0"
          :summary-text="`当前共有 ${list.length} 条推理预设，已选 ${selectedRowKeys.length} 条。`"
          @toggle-select-all="toggleSelectAllCurrentPage"
      />

      <a-table
          :columns="columns"
          :data-source="list"
          :pagination="false"
          row-key="id"
          :row-selection="rowSelection"
          :scroll="{ x: 1560 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'instanceKey'">
            <code class="code-text">{{ record.instanceKey || '—' }}</code>
          </template>
          <template v-else-if="column.key === 'instanceName'">
            <span>{{ record.instanceName || '—' }}</span>
          </template>
          <template v-else-if="column.key === 'modelKey'">
            <a-tooltip title="关联的接入端点标识">
              <code class="code-text">{{ record.modelKey || '—' }}</code>
            </a-tooltip>
          </template>
          <template v-if="column.key === 'params'">
            <span class="param-summary">
              温度 {{ record.temperature ?? '—' }} · 长度限制 {{ record.maxTokens ?? '—' }} · 核采样
              {{ record.topP ?? '—' }}
            </span>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.status === 'enabled' ? 'green' : 'default'">
              {{ record.status === 'enabled' ? '已激活' : '已停用' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'envCode'">
            <a-tag color="blue">{{ record.envCode || 'DEFAULT' }}</a-tag>
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-button type="link" @click="goEdit(record)">编辑参数</a-button>
            <a-divider type="vertical" />
            <a-popconfirm
                title="确定删除此配置吗？关联的智能体可能受影响。"
                ok-text="确认"
                cancel-text="取消"
                @confirm="() => handleDeleteOne(record.id)"
            >
              <a-button type="link" danger>删除</a-button>
            </a-popconfirm>
          </template>
        </template>
      </a-table>

      <div class="pagination-wrap">
        <a-pagination
            :current="page.pageNum"
            :page-size="page.pageSize"
            :total="page.total"
            :show-size-changer="false"
            @change="onPageChange"
        />
      </div>

      <InstanceForm
          v-model:visible="formVisible"
          :record="currentRecord"
          @success="handleFormSuccess"
      />
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { DeleteOutlined, PlusOutlined, SearchOutlined } from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AdminListToolbar from '@/components/home/AdminListToolbar.vue'
import BaseOverview from '@/components/home/BaseOverview.vue'
import InstanceForm from './InstanceForm.vue'
import { aiInstanceApi, type AiInstance, type PageResponse } from '@/api/aiInstance'

const formVisible = ref(false)
const currentRecord = ref<AiInstance | undefined>(undefined)

type QueryState = {
  instanceKey?: string
  instanceName?: string
  modelKey?: string
  status?: string
}

const columns = [
  { title: 'Instance Key', key: 'instanceKey', width: 200, ellipsis: true },
  { title: '实例名称', key: 'instanceName', width: 140, ellipsis: true },
  { title: 'Model Key', key: 'modelKey', width: 200, ellipsis: true },
  { title: '核心参数', key: 'params', width: 260, ellipsis: true },
  { title: '状态', key: 'status', width: 100 },
  { title: '环境', key: 'envCode', width: 100 },
  { title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', width: 180, ellipsis: true },
  { title: '操作', key: 'actions', width: 160, fixed: 'right' as const }
]

const query = reactive<QueryState>({})
const list = ref<AiInstance[]>([])
const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})
const selectedRowKeys = ref<Array<number | string>>([])

const currentPageIds = computed(() =>
  list.value
    .map((item) => item.id)
    .filter((id): id is number | string => id !== undefined && id !== null)
)

const allCurrentSelected = computed(() => {
  return (
    currentPageIds.value.length > 0 &&
    currentPageIds.value.every((id) => selectedRowKeys.value.includes(id))
  )
})

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

const toggleStatusFilter = (value: string) => {
  query.status = query.status === value ? undefined : value
}

const resetFilters = () => {
  query.instanceKey = undefined
  query.instanceName = undefined
  query.modelKey = undefined
  query.status = undefined
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

const fetchList = async () => {
  const payload = {
    pageNo: page.pageNum,
    pageSize: page.pageSize,
    param: {
      instanceKey: query.instanceKey || undefined,
      instanceName: query.instanceName || undefined,
      modelKey: query.modelKey || undefined,
      status: query.status || undefined
    }
  }
  const resp: PageResponse<AiInstance> = await aiInstanceApi.queryPage(payload)
  list.value = resp.list || []
  page.total = resp.total || 0
}

const onPageChange = (p: number) => {
  page.pageNum = p
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
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await aiInstanceApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

void fetchList()
</script>

<style scoped>
.config-page {
  padding: 0 4px;
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

.search-cluster :deep(.ant-input-affix-wrapper) {
  border: none !important;
  box-shadow: none !important;
  background: transparent !important;
}

.search-cluster :deep(.ant-input-affix-wrapper:hover),
.search-cluster :deep(.ant-input-affix-wrapper-focused) {
  background: color-mix(in srgb, var(--bg-card) 85%, var(--bg-surface)) !important;
}

.toolbar-input {
  width: 200px;
}

.search-main-input {
  width: 200px;
}

.search-sub-input {
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

.pagination-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.code-text {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 12px;
}

.param-summary {
  font-size: 12px;
  color: var(--text-secondary);
}

@media (max-width: 720px) {
  .toolbar-input,
  .search-main-input,
  .search-sub-input {
    width: 100%;
  }

  .search-cluster,
  .status-switch {
    width: 100%;
  }

  .status-switch {
    justify-content: space-between;
  }

  .status-btn {
    flex: 1;
  }

  .pagination-wrap {
    justify-content: center;
  }
}
</style>
