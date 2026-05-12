<template>
  <AstrsomnPageShell
    title="推理参数配置"
    description="管理 AI 运行预设：定义采样温度、长度限制及生成策略，供智能体直接引用。"
    empty-text="暂无推理预设实例。"
    :breadcrumbs="breadcrumbs"
    :show-view-toggle="true"
    :view-mode="viewMode"
    :view-toggle-handler="handleViewToggle"
  >
    <div ref="pageRef" class="instance-page">
      <AstrsomnDataSection>
        <template #toolbar>
          <div class="toolbar">
            <div class="toolbar-left">
              <AstrsomnSearchPill
                v-model="query.instanceName"
                layout="toolbar"
                placeholder="搜索预设名称或标识..."
                button-label="查询"
                @search="fetchList"
              />
              <ExtensionSelector
                :value="query.extensionCode"
                class="toolbar-provider-select"
                allow-clear
                @update:value="handleProviderChange"
              />
            </div>
            <div class="toolbar-right">
              <AstrsomnSegmentedButton :buttons="toolbarSegmentButtons" />
            </div>
          </div>
        </template>

        <a-tabs
                :active-key="modelTypeTab"
                class="toolbar-model-type-tabs"
                size="small"
                @change="handleModelTypeTabChange"
              >
                <a-tab-pane key="all" tab="全部" />
                <a-tab-pane key="chat" tab="对话" />
                <a-tab-pane key="embedding" tab="向量" />
                <a-tab-pane key="image" tab="图片" />
              </a-tabs>

        <AstrsomnDataView
          :mode="dataViewMode"
          :data-source="list"
          :loading="loading"
          :columns="columns"
          :row-selection="rowSelection"
          row-key="id"
          :scroll="{ x: 1080 }"
          empty-text="暂无匹配的推理配置"
          :card-columns="currentGridColumns"
          :card-min-width="instanceCardMinWidth"
          :card-gap="instanceCardGap"
        >
          <template #card="{ record }">
            <InstanceCard
              :record="record"
              :index="list.findIndex((item) => item.id === record.id)"
              :selected="record.id != null && selectedKeySet.has(record.id)"
              @edit="goEdit(record)"
              @delete="handleDeleteOne(record.id)"
            />
          </template>

          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'providerAvatar'">
              <img
                v-if="providerAvatarCell(record)"
                class="provider-avatar-cell"
                :src="providerAvatarCell(record)"
                :alt="record.instanceName || 'provider'"
                aria-hidden="true"
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
                {{ record.status === 'enabled' ? '启用' : '禁用' }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'isDefault'">
              <a-tag v-if="record.isDefault === 'Y'" color="blue">默认预设</a-tag>
              <span v-else class="text-secondary">-</span>
            </template>

            <template v-else-if="column.key === 'actions'">
              <a-space>
                <a-button type="link" size="small" @click="goEdit(record)">
                  <EditOutlined />
                </a-button>
                <a-popconfirm title="确定删除该配置吗？" @confirm="() => handleDeleteOne(record.id)">
                  <a-button type="link" danger size="small">
                    <DeleteOutlined />
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

      <InstanceForm
        v-model:visible="formVisible"
        :record="currentRecord"
        @success="handleFormSuccess"
      />
    </div>
  </AstrsomnPageShell>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons-vue'
import AstrsomnPageShell from '@/components/home/AstrsomnPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'
import ExtensionSelector from '../../system-config/system-extension/selectors/ExtensionSelector.vue'
import InstanceForm from './InstanceForm.vue'
import InstanceCard from './component/InstanceCard.vue'
import { aiInstanceApi, type AiInstance, type PageResponse } from '@/api/aiInstance'

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
const page = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const selectedRowKeys = ref<Array<number | string>>([])
const selectedKeySet = computed(() => new Set(selectedRowKeys.value))
const viewMode = ref<'grid' | 'list'>('list')
const dataViewMode = computed<'card' | 'table'>(() => (viewMode.value === 'grid' ? 'card' : 'table'))
const currentGridColumns = ref(3)
const modelTypeTab = computed(() => query.modelType ?? 'all')

const breadcrumbs = [
  { title: 'AI 配置', href: '/admin/ai-config' },
  { title: '推理参数配置' },
]

const handleViewToggle = () => {
  viewMode.value = viewMode.value === 'grid' ? 'list' : 'grid'
}

const columns = [
  { title: '供应商', key: 'providerAvatar', width: 80, align: 'center' as const },
  { title: '模型类型', dataIndex: 'modelType', key: 'modelType', width: 110 },
  { 
    title: '实例 Key', 
    dataIndex: 'instanceKey', 
    key: 'instanceKey', 
    width: 180,
    ellipsis: true,
    copyable: true
  },
  { title: '名称', dataIndex: 'instanceName', key: 'instanceName', width: 180, ellipsis: true },

  { title: '关联模型 Key', dataIndex: 'modelKey', key: 'modelKey', width: 180, ellipsis: true},
  { title: '关联账号', dataIndex: 'accountName', key: 'accountName', width: 150, ellipsis: true },
  { title: '默认', key: 'isDefault', width: 90, align: 'center' },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  {title: '环境', dataIndex: 'envCode', key: 'envCode', width: 80, ellipsis: true, tag: true, tagColor: 'blue'},
  {title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 150, dateFormat: true},
  {title: '创建人', dataIndex: 'createUser', key: 'createUser', width: 150},
  { title: '操作', key: 'actions', width: 140, fixed: 'right' as const }
]

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
  if (type === 'embedding') return '向量'
  if (type === 'image') return '图像'
  if (type === 'chat') return '对话'
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
  message.success('批量删除成功')
  selectedRowKeys.value = []
  void fetchList()
}

const toolbarSegmentButtons = computed<SegmentedButton[]>(() => [
  {
    label: selectedRowKeys.value.length > 0 ? `批量删除 (${selectedRowKeys.value.length})` : '删除',
    icon: DeleteOutlined,
    disabled: selectedRowKeys.value.length === 0,
    onClick: () => {
      if (selectedRowKeys.value.length === 0) return
      Modal.confirm({
        title: '确定批量删除选中的配置吗？',
        okText: '确认',
        cancelText: '取消',
        onOk: () => handleBatchDelete()
      })
    },
    type: 'danger',
    plain: true
  },
  {
    label: '新增',
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
  color: var(--text-muted, #bfbfbf);
}

@media (max-width: 720px) {
  .toolbar-left,
  .toolbar-right {
    width: 100%;
  }
}
</style>