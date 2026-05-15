<template>
  <AstrsomnDrawerShell
      :open="props.open"
      :width="640"
      root-class-name="model-select-drawer"
      @update:open="handleClose"
  >
    <template #icon>
      <AppstoreOutlined/>
    </template>
    <template #title>选择模型</template>
    <template #subtitle>为实例选择一个推理端点</template>

    <div class="select-drawer-content">
      <div class="search-bar">
        <ExtensionSelector
            :allow-clear="true"
            :value="providerFilter"
            placeholder="全部提供商"
            size="middle"
            @update:value="onProviderChange"
        />
        <a-input
            v-model:value="keyword"
            allow-clear
            placeholder="搜索模型名称或 Key"
            size="large"
            @pressEnter="handleSearch"
        >
          <template #prefix>
            <SearchOutlined/>
          </template>
        </a-input>
      </div>

      <a-spin :spinning="loading">
        <div class="model-list">
          <div
              v-for="model in list"
              :key="model.id"
              :class="{ selected: selectedId === model.id }"
              class="model-item"
              @click="handleSelect(model)"
          >
            <div class="model-item-left">
              <img
                  v-if="getAvatar(model)"
                  :src="getAvatar(model)"
                  :alt="model.extensionCode"
                  class="provider-avatar"
              />
              <div v-else :class="model.modelType" class="model-icon-fallback">
                <MessageOutlined v-if="model.modelType === 'chat'"/>
                <PartitionOutlined v-else-if="model.modelType === 'embedding'"/>
                <PictureOutlined v-else-if="model.modelType === 'image'"/>
                <AudioOutlined v-else-if="model.modelType === 'voice'"/>
                <AppstoreOutlined v-else/>
              </div>
              <div class="model-info">
                <div class="model-name">{{ model.modelName }}</div>
                <div class="model-key">
                  <KeyOutlined/>
                  {{ model.modelKey }}
                </div>
              </div>
            </div>
            <div class="model-meta">
              <span class="provider-tag">{{ model.extensionCode }}</span>
              <a-tag :color="model.status === 'enabled' ? 'green' : 'red'" class="status-tag">
                {{ model.status === 'enabled' ? '启用' : '禁用' }}
              </a-tag>
            </div>
          </div>

          <a-empty v-if="!loading && list.length === 0" description="暂无模型"/>
        </div>
      </a-spin>
    </div>

    <template #footer>
      <AstrsomnPagination
          :current="page.pageNum"
          :page-size="page.pageSize"
          :show-size-changer="false"
          :total="page.total"
          @change="onPageChange"
      />
    </template>
  </AstrsomnDrawerShell>
</template>

<script lang="ts" setup>
import {ref, reactive, watch} from 'vue'
import {
  AppstoreOutlined,
  AudioOutlined,
  KeyOutlined,
  MessageOutlined,
  PartitionOutlined,
  PictureOutlined,
  SearchOutlined,
} from '@ant-design/icons-vue'
import {type AiModel, aiModelApi, type PageResponse} from '@/api/aiModel.ts'
import {WORKSPACE_ENV_STORAGE_KEY} from '@/constants/workspaceEnv.ts'
import AstrsomnDrawerShell from '@/components/home/AstrsomnDrawerShell.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import ExtensionSelector from '@/views/admin/system-config/system-extension/selectors/ExtensionSelector.vue'

const props = defineProps<{
  open: boolean
  fixedModelType?: string
}>()

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void
  (e: 'select', model: AiModel): void
}>()

const keyword = ref('')
const providerFilter = ref<string | undefined>(undefined)
const loading = ref(false)
const list = ref<AiModel[]>([])
const selectedId = ref<number | string | undefined>()
const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
})

function getAvatar(model: AiModel): string {
  const raw = model?.providerAvatar
  return typeof raw === 'string' && raw.trim() ? raw.trim() : ''
}

async function fetchList() {
  loading.value = true
  try {
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        modelName: keyword.value || undefined,
        envCode: localStorage.getItem(WORKSPACE_ENV_STORAGE_KEY) || undefined,
        modelType: props.fixedModelType || undefined,
        extensionCode: providerFilter.value?.trim() || undefined,
      },
    }
    const resp: PageResponse<AiModel> = await aiModelApi.queryPage(payload)
    list.value = resp.list || []
    page.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.pageNum = 1
  void fetchList()
}

function onProviderChange(v: string | undefined) {
  providerFilter.value = v
  page.pageNum = 1
  void fetchList()
}

function onPageChange(p: number, ps: number) {
  page.pageNum = p
  page.pageSize = ps
  void fetchList()
}

function handleSelect(model: AiModel) {
  selectedId.value = model.id
  emit('select', model)
}

function handleClose() {
  emit('update:open', false)
}

watch(() => props.open, (val) => {
  if (val) {
    keyword.value = ''
    providerFilter.value = undefined
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
  gap: 16px;
  height: 100%;
  min-height: 0;
}

.search-bar {
  display: flex;
  gap: 12px;
  flex-shrink: 0;
}

.search-bar > * {
  flex: 1;
}

.model-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.model-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 14px 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.model-item:hover {
  border-color: #3b82f6;
  background: #f0f7ff;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.08);
}

.model-item.selected {
  border-color: #3b82f6;
  background: #eff6ff;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.12);
}

.model-item-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.provider-avatar {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  object-fit: contain;
  flex-shrink: 0;
  background: #fff;
  padding: 2px;
}

.model-icon-fallback {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.model-icon-fallback.chat {
  background: linear-gradient(135deg, #6366f1 0%, #a855f7 100%);
  color: white;
}

.model-icon-fallback.embedding {
  background: linear-gradient(135deg, #10b981 0%, #22c55e 100%);
  color: white;
}

.model-icon-fallback.image {
  background: linear-gradient(135deg, #f59e0b 0%, #f97316 100%);
  color: white;
}

.model-icon-fallback.voice {
  background: linear-gradient(135deg, #06b6d4 0%, #6366f1 100%);
  color: white;
}

.model-info {
  flex: 1;
  min-width: 0;
}

.model-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 3px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.model-key {
  font-size: 11px;
  color: #94a3b8;
  font-family: 'JetBrains Mono', monospace;
  display: flex;
  align-items: center;
  gap: 4px;
}

.model-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.provider-tag {
  font-size: 11px;
  padding: 2px 8px;
  background: #f1f5f9;
  border-radius: 6px;
  color: #64748b;
  font-weight: 500;
}

.status-tag {
  font-size: 11px;
  margin: 0;
}
</style>
