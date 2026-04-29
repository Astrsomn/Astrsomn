<template>
  <AdminPageShell
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
            </div>
            <div class="toolbar-right">
              <AstrsomnSegmentedButton :buttons="toolbarSegmentButtons" />
            </div>
          </div>
        </template>

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
            <template v-if="column.key === 'modelType'">
              <a-tag>
                {{ modelTypeLabel(record.modelType) }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'status'">
              <a-tag :color="record.status === 'enabled' ? 'green' : 'default'">
                {{ record.status === 'enabled' ? '启用' : '禁用' }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'instanceKey'">
              <span class="mono-text">{{ record.instanceKey || '-' }}</span>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-button type="link" size="small" @click="goEdit(record)">编辑</a-button>
              <a-divider type="vertical" />
              <a-popconfirm title="确定删除该配置吗？" @confirm="() => handleDeleteOne(record.id)">
                <a-button type="link" size="small" danger>删除</a-button>
              </a-popconfirm>
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
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { DeleteOutlined, PlusOutlined } from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AstrsomnDataSection from '@/components/home/AstrsomnDataSection.vue'
import AstrsomnDataView from '@/components/home/AstrsomnDataView.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'
import InstanceForm from './InstanceForm.vue'
import InstanceCard from './InstanceCard.vue'
import { aiInstanceApi, type AiInstance, type PageResponse } from '@/api/aiInstance'

const INSTANCE_CARD_MIN_WIDTH_PX = 360
const INSTANCE_CARD_GAP_PX = 12
const instanceCardMinWidth = `${INSTANCE_CARD_MIN_WIDTH_PX}px`
const instanceCardGap = `${INSTANCE_CARD_GAP_PX}px`

const pageRef = ref<HTMLElement | null>(null)
const formVisible = ref(false)
const currentRecord = ref<AiInstance | undefined>(undefined)
const query = reactive<{ instanceName?: string; status?: string }>({})
const list = ref<AiInstance[]>([])
const loading = ref(false)
const page = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const selectedRowKeys = ref<Array<number | string>>([])
const selectedKeySet = computed(() => new Set(selectedRowKeys.value))
const viewMode = ref<'grid' | 'list'>('list')
const dataViewMode = computed<'card' | 'table'>(() => (viewMode.value === 'grid' ? 'card' : 'table'))
const currentGridColumns = ref(3)

const breadcrumbs = [
  { title: 'AI 配置', href: '/admin/ai-config' },
  { title: '推理参数配置' },
]

const handleViewToggle = () => {
  viewMode.value = viewMode.value === 'grid' ? 'list' : 'grid'
}

const columns = [
  { 
    title: '实例 Key', 
    dataIndex: 'instanceKey', 
    key: 'instanceKey', 
    width: 120, 
    ellipsis: true,
    copyable: true
  },
  { title: '名称', dataIndex: 'instanceName', key: 'instanceName', width: 180, ellipsis: true },
  { title: '模型类型', dataIndex: 'modelType', key: 'modelType', width: 110 },
  { title: '关联模型 Key', dataIndex: 'modelKey', key: 'modelKey', width: 180, ellipsis: true, copyable: true },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', width: 170, ellipsis: true },
  { title: '操作', key: 'actions', width: 140, fixed: 'right' as const }
]

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

const fetchList = async () => {
  loading.value = true
  try {
    const resp: PageResponse<AiInstance> = await aiInstanceApi.queryPage({
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: { instanceName: query.instanceName || undefined, status: query.status || undefined }
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
    label: selectedRowKeys.value.length > 0 ? `批量删除 (${selectedRowKeys.value.length})` : '批量删除',
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
    }
  },
  {
    label: '新增配置',
    type: 'primary',
    icon: PlusOutlined,
    onClick: goCreate
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

.mono-text {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}

@media (max-width: 720px) {
  .toolbar-left,
  .toolbar-right {
    width: 100%;
  }
}
</style>