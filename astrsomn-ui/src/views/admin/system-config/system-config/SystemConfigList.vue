<template>
  <AdminPageShell
    title="系统配置"
    description="管理系统配置项（SYSTEM_CONFIG），支持按分组维护运行时参数。"
    empty-text="暂无系统配置。"
  >
    <div class="config-page">
      <AstrsomnDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
        
            <AstrsomnSearchPill
              v-model="query.configGroup"
              placeholder="配置分组"
              button-label="搜索"
              layout="toolbar"
              @search="fetchList"
            />
        

          <AstrsomnStateSwitch
            v-model="query.status"
            :options="statusOptions"
            @change="handleStatusChange"
          />
            </div>

            <div class="toolbar-right">
          <AstrsomnSegmentedButton :buttons="actionButtons" />
            </div>
          </div>
        </template>

        <template #overview>
          <AstrsomnOverview
        :list-length="list.length"
        :selected-count="selectedRowKeys.length"
        :all-current-selected="allCurrentSelected"
        :part-current-selected="partCurrentSelected"
        :show-actions="list.length > 0"
        :summary-text="`当前页 ${list.length} 条系统配置，已选 ${selectedRowKeys.length} 条。`"
        @toggle-select-all="toggleSelectAllCurrentPage"
      />
        </template>

        <AstrsomnDataView
          mode="table"
          :data-source="list"
          :columns="columns"
          :row-selection="rowSelection"
          :scroll="{ x: 1360 }"
          row-key="id"
          empty-text="暂无匹配的系统配置"
        >
          <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'configKey'">
            <code class="config-key">{{ record.configKey }}</code>
          </template>
          <template v-else-if="column.key === 'configValue'">
            <span class="config-value">{{ preview(record.configValue) }}</span>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.status === 'ENABLED' ? 'green' : 'default'">
              {{ statusLabel(record.status) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'isSystem'">
            <a-tag :color="record.isSystem ? 'blue' : 'default'">
              {{ record.isSystem ? '系统内置' : '自定义' }}
            </a-tag>
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
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref, onMounted } from 'vue'
import {
  AppstoreOutlined,
  CheckCircleOutlined,
  StopOutlined
} from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'
import AstrsomnOverview from '@/components/home/AstrsomnOverview.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnStateSwitch from '@/components/home/AstrsomnStateSwitch.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'

// 系统配置类型定义
type SystemConfig = {
  id: number | string
  configKey: string
  configGroup: string
  configValue: string
  status: 'ENABLED' | 'DISABLED'
  isSystem: boolean
  description: string
}

type PageResponse<T> = {
  list: T[]
  total: number
}

type QueryState = {
  configKey?: string
  configGroup?: string
  status?: string
}

const columns = [
  { title: '配置 Key', key: 'configKey', width: 260, ellipsis: true },
  { title: '配置分组', dataIndex: 'configGroup', key: 'configGroup', width: 160, ellipsis: true },
  { title: '配置值', key: 'configValue', width: 320, ellipsis: true },
  { title: '状态', key: 'status', width: 100 },
  { title: '属性', key: 'isSystem', width: 110 },
  { title: '描述', dataIndex: 'description', key: 'description', width: 220, ellipsis: true }
]

const query = reactive<QueryState>({})
const list = ref<SystemConfig[]>([])

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const statusOptions = [
  { label: '全部', value: undefined, color: '#3b82f6', icon: AppstoreOutlined },
  { label: '启用', value: 'ENABLED', color: '#10b981', icon: CheckCircleOutlined },
  { label: '禁用', value: 'DISABLED', color: '#f43f5e', icon: StopOutlined }
]

const selectedRowKeys = ref<Array<number | string>>([])

const actionButtons = computed<SegmentedButton[]>(() => [
  {
    label: '重置',
    type: 'default',
    onClick: resetFilters
  }
])

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

const handleStatusChange = (value: string | undefined) => {
  query.status = value
  void fetchList()
}

const resetFilters = () => {
  query.configKey = undefined
  query.configGroup = undefined
  query.status = undefined
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

const preview = (raw: string | undefined) => {
  if (!raw) return '—'
  const text = raw.replace(/\s+/g, ' ').trim()
  return text.length > 84 ? `${text.slice(0, 84)}…` : text
}

const statusLabel = (value: string | undefined) => {
  if (value === 'ENABLED') return '启用'
  if (value === 'DISABLED') return '禁用'
  return value ?? '—'
}

// 初始化默认配置数据
const initDefaultConfig = (): SystemConfig[] => {
  const defaultConfigs: SystemConfig[] = [
    // AI 配置域
    { id: 1, configKey: 'ai.model.default', configGroup: 'AI', configValue: 'gpt-4', status: 'ENABLED', isSystem: true, description: '默认 AI 模型' },
    { id: 2, configKey: 'ai.temperature', configGroup: 'AI', configValue: '0.7', status: 'ENABLED', isSystem: true, description: 'AI 生成温度参数' },
    // 向量中心域
    { id: 3, configKey: 'vec.index.default', configGroup: 'VECTOR', configValue: 'default-index', status: 'ENABLED', isSystem: true, description: '默认向量索引' },
    { id: 4, configKey: 'vec.dimensions', configGroup: 'VECTOR', configValue: '1536', status: 'ENABLED', isSystem: true, description: '向量维度' },
    // 系统管理域
    { id: 5, configKey: 'system.log.level', configGroup: 'SYSTEM', configValue: 'INFO', status: 'ENABLED', isSystem: true, description: '系统日志级别' },
    { id: 6, configKey: 'system.timezone', configGroup: 'SYSTEM', configValue: 'Asia/Shanghai', status: 'ENABLED', isSystem: true, description: '系统时区' },
    // 安全治理域
    { id: 7, configKey: 'security.cors.enabled', configGroup: 'SECURITY', configValue: 'true', status: 'ENABLED', isSystem: true, description: '是否启用 CORS' },
    { id: 8, configKey: 'security.rate.limit', configGroup: 'SECURITY', configValue: '100', status: 'ENABLED', isSystem: true, description: '速率限制' }
  ]
  localStorage.setItem('systemConfigs', JSON.stringify(defaultConfigs))
  return defaultConfigs
}

const fetchList = () => {
  // 从 localStorage 获取数据
  const storedConfigs = localStorage.getItem('systemConfigs')
  let allConfigs: SystemConfig[] = []
  
  if (storedConfigs) {
    try {
      allConfigs = JSON.parse(storedConfigs)
    } catch {
      allConfigs = initDefaultConfig()
    }
  } else {
    allConfigs = initDefaultConfig()
  }
  
  // 应用筛选条件
  let filteredConfigs = [...allConfigs]
  
  if (query.configGroup) {
    filteredConfigs = filteredConfigs.filter(config => 
      config.configGroup.toLowerCase().includes(query.configGroup!.toLowerCase())
    )
  }
  
  if (query.status) {
    filteredConfigs = filteredConfigs.filter(config => config.status === query.status)
  }
  
  // 计算分页
  const start = (page.pageNum - 1) * page.pageSize
  const end = start + page.pageSize
  
  list.value = filteredConfigs.slice(start, end)
  page.total = filteredConfigs.length
}

const onPageChange = (p: number, size: number) => {
  page.pageNum = p
  page.pageSize = size
  fetchList()
}

// 页面加载时获取数据
onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.config-page {
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
  border: none;
  box-shadow: none;
  background: transparent;
}

.search-cluster :deep(.ant-input-affix-wrapper:hover),
.search-cluster :deep(.ant-input-affix-wrapper-focused) {
  border: none;
  box-shadow: none;
  background: color-mix(in srgb, var(--bg-card) 85%, var(--bg-surface));
}

.toolbar-input {
  width: 200px;
}

.search-main-input {
  width: 320px;
}

.search-sub-input {
  width: 220px;
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

.config-key {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}

.config-value {
  color: rgba(0, 0, 0, 0.65);
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

  .search-cluster {
    padding: 8px;
  }

  .status-switch {
    justify-content: space-between;
  }

  .status-btn {
    flex: 1;
  }

  .toolbar-left,
  .toolbar-right {
    width: 100%;
  }
}
</style>
