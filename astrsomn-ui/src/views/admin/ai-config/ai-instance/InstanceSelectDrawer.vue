<template>
  <a-drawer
    :open="props.open"
    placement="right"
    :width="560"
    :maskClosable="true"
    :closable="true"
    title="选择推理配置"
    @close="handleClose"
    root-class-name="instance-select-drawer"
  >
    <div class="select-drawer-content">
      <div class="toolbar">
        <a-input
          v-model:value="keyword"
          class="toolbar-search"
          placeholder="搜索预设名称"
          allow-clear
          @pressEnter="handleSearch"
        >
          <template #prefix>
            <SearchOutlined />
          </template>
        </a-input>
        <a-select
          v-model:value="queryStatus"
          class="toolbar-status"
          placeholder="状态筛选"
          allow-clear
          @change="handleSearch"
        >
          <a-select-option value="enabled">启用</a-select-option>
          <a-select-option value="disabled">禁用</a-select-option>
        </a-select>
        <a-button type="primary" @click="handleSearch">查询</a-button>
      </div>

      <a-table
        class="instance-table"
        :columns="columns"
        :data-source="list"
        :loading="loading"
        :pagination="false"
        row-key="id"
        :row-selection="rowSelection"
        :scroll="{ x: 620 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'instanceName'">
            <a-button type="link" class="instance-name-link" @click="handleSelect(record)">
              {{ record.instanceName || '未命名配置' }}
            </a-button>
          </template>
          <template v-else-if="column.key === 'instanceKey'">
            <span class="mono-text">{{ record.instanceKey || '-' }}</span>
          </template>
          <template v-else-if="column.key === 'modelType'">
            <span>{{ record.modelType || '-' }}</span>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.status === 'enabled' ? 'green' : 'default'">
              {{ record.status === 'enabled' ? '启用' : '禁用' }}
            </a-tag>
          </template>
        </template>
      </a-table>

      <div class="drawer-footer">
        <a-pagination
          :current="page.pageNum"
          :page-size="page.pageSize"
          :total="page.total"
          :show-size-changer="false"
          @change="onPageChange"
        />
      </div>
    </div>
  </a-drawer>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { SearchOutlined } from '@ant-design/icons-vue'
import { aiInstanceApi, type AiInstance, type PageResponse } from '@/api/aiInstance'

const props = defineProps<{
  open: boolean
  disableTtlEdit?: boolean
}>()

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void
  (e: 'select', instance: AiInstance): void
}>()

const keyword = ref('')
const queryStatus = ref<string | undefined>()
const loading = ref(false)
const list = ref<AiInstance[]>([])
const selectedId = ref<number | string | undefined>()
const columns = [
  { title: '名称', dataIndex: 'instanceName', key: 'instanceName', width: 190, ellipsis: true },
  { title: '实例 Key', dataIndex: 'instanceKey', key: 'instanceKey', width: 170, ellipsis: true },
  { title: '模型类型', dataIndex: 'modelType', key: 'modelType', width: 110 },
  { title: '模型 Key', dataIndex: 'modelKey', key: 'modelKey', width: 120, ellipsis: true },
  { title: '状态', dataIndex: 'status', key: 'status', width: 90 }
]
const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})
const rowSelection = computed(() => ({
  type: 'radio' as const,
  selectedRowKeys: selectedId.value != null ? [selectedId.value] : [],
  onChange: (keys: Array<number | string>, rows: AiInstance[]) => {
    selectedId.value = keys[0]
    if (rows[0]) {
      emit('select', rows[0])
    }
  }
}))

const fetchList = async () => {
  loading.value = true
  try {
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        instanceName: keyword.value || undefined,
        status: queryStatus.value || undefined
      }
    }
    const resp: PageResponse<AiInstance> = await aiInstanceApi.queryPage(payload)
    list.value = resp.list || []
    page.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.pageNum = 1
  void fetchList()
}

const handleSelect = (instance: AiInstance) => {
  selectedId.value = instance.id
  emit('select', instance)
}

const onPageChange = (p: number) => {
  page.pageNum = p
  void fetchList()
}

const handleClose = () => {
  emit('update:open', false)
}

watch(() => props.open, (val) => {
  if (val) {
    keyword.value = ''
    queryStatus.value = undefined
    selectedId.value = undefined
    page.pageNum = 1
    void fetchList()
  }
})
</script>

<style scoped>
.select-drawer-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.toolbar-search {
  flex: 1;
  min-width: 220px;
}

.toolbar-status {
  width: 120px;
}

.instance-table {
  flex: 1;
  min-height: 0;
}

.instance-name-link {
  padding: 0;
}

.mono-text {
  font-family: 'JetBrains Mono', monospace;
}

.drawer-footer {
  flex-shrink: 0;
  padding-top: 16px;
  border-top: 1px solid var(--border-default);
  display: flex;
  justify-content: center;
}
</style>
