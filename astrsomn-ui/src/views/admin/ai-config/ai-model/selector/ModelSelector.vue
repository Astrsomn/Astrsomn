<template>
  <a-drawer
    :open="props.open"
    placement="right"
    :width="600"
    :maskClosable="false"
    :closable="true"
    title="选择模型"
    @close="handleClose"
    root-class-name="model-select-drawer"
  >
    <div class="select-drawer-content">
      <div class="search-bar">
        <a-input
          v-model:value="keyword"
          placeholder="搜索模型名称"
          allow-clear
          @pressEnter="handleSearch"
        >
          <template #prefix>
            <SearchOutlined />
          </template>
        </a-input>
        <a-select
          v-model:value="queryStatus"
          placeholder="状态筛选"
          allow-clear
          style="width: 120px"
          @change="handleSearch"
        >
          <a-select-option value="enabled">启用</a-select-option>
          <a-select-option value="disabled">禁用</a-select-option>
        </a-select>
        <a-button type="primary" @click="handleSearch">查询</a-button>
      </div>

      <a-spin :spinning="loading">
        <div class="model-list">
          <div
            v-for="model in list"
            :key="model.id"
            class="model-item"
            :class="{ selected: selectedId === model.id }"
            @click="handleSelect(model)"
          >
            <div class="model-icon" :class="model.modelType">
              <MessageOutlined v-if="model.modelType === 'chat'" />
              <PartitionOutlined v-else-if="model.modelType === 'embedding'" />
              <PictureOutlined v-else />
            </div>
            <div class="model-info">
              <div class="model-name">{{ model.modelName }}</div>
              <div class="model-key">
                <KeyOutlined /> {{ model.modelKey }}
              </div>
            </div>
            <div class="model-meta">
              <span class="provider-tag">{{ model.extensionCode }}</span>
              <span class="status-badge" :class="model.status">
                {{ model.status === 'enabled' ? '启用' : '禁用' }}
              </span>
            </div>
          </div>

          <a-empty v-if="!loading && list.length === 0" description="暂无模型" />
        </div>
      </a-spin>

      <div class="drawer-footer">
        <a-pagination
          v-model:current="page.pageNum"
          :page-size="page.pageSize"
          :total="page.total"
          :show-size-changer="false"
          @change="fetchList"
        />
      </div>
    </div>
  </a-drawer>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { SearchOutlined, KeyOutlined, MessageOutlined, PartitionOutlined, PictureOutlined } from '@ant-design/icons-vue'
import { aiModelApi, type AiModel, type PageResponse } from '@/api/aiModel.ts'
import { WORKSPACE_ENV_STORAGE_KEY } from '@/constants/workspaceEnv.ts'

const props = defineProps<{
  open: boolean
}>()

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void
  (e: 'select', model: AiModel): void
}>()

const keyword = ref('')
const queryStatus = ref<string | undefined>()
const loading = ref(false)
const list = ref<AiModel[]>([])
const selectedId = ref<number | string | undefined>()
const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const fetchList = async () => {
  loading.value = true
  try {
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        modelName: keyword.value || undefined,
        status: queryStatus.value || undefined,
        envCode: localStorage.getItem(WORKSPACE_ENV_STORAGE_KEY) || undefined
      }
    }
    const resp: PageResponse<AiModel> = await aiModelApi.queryPage(payload)
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

const handleSelect = (model: AiModel) => {
  selectedId.value = model.id
  emit('select', model)
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

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.model-list {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.model-item {
  display: flex;
  align-items: center;
  gap: 12px;
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.model-item:hover {
  border-color: var(--primary);
  box-shadow: var(--shadow-card);
}

.model-item.selected {
  border-color: var(--primary);
  background: var(--primary-hover);
}

.model-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.model-icon.chat {
  background: linear-gradient(135deg, #6366f1 0%, #a855f7 100%);
  color: white;
}

.model-icon.embedding {
  background: linear-gradient(135deg, #10b981 0%, #22c55e 100%);
  color: white;
}

.model-icon.image {
  background: linear-gradient(135deg, #f59e0b 0%, #f97316 100%);
  color: white;
}

.model-info {
  flex: 1;
  min-width: 0;
}

.model-name {
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.model-key {
  font-size: 12px;
  color: var(--text-secondary);
  font-family: 'JetBrains Mono', monospace;
  display: flex;
  align-items: center;
  gap: 4px;
}

.model-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.provider-tag {
  font-size: 11px;
  padding: 2px 8px;
  background: var(--bg-secondary);
  border-radius: 4px;
  color: var(--text-secondary);
}

.status-badge {
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 600;
}

.status-badge.enabled {
  background: #dcfce7;
  color: #16a34a;
}

.status-badge.disabled {
  background: #fee2e2;
  color: #ef4444;
}

.drawer-footer {
  flex-shrink: 0;
  padding-top: 16px;
  border-top: 1px solid var(--border-default);
  display: flex;
  justify-content: center;
}
</style>
