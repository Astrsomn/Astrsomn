<template>
  <div class="model-section">
    <div class="model-section__toolbar">
      <div class="model-section__search">
        <AstrsomnSearchPill
          v-model="keyword"
          layout="pane"
          placeholder="搜索模型名称..."
          @search="handleSearch"
        />
      </div>
      <span class="model-section__count">共 {{ total }} 个模型</span>
      <a-button size="large" type="primary" class="model-section__create-btn" @click="handleCreate">
        <template #icon><PlusOutlined /></template>
        新增模型
      </a-button>
    </div>

    <div class="model-section__body">
      <div v-if="loading" class="model-section__loading">
        <a-spin size="small" />
      </div>

      <template v-else>
        <div class="model-section__grid">
          <div
            v-for="model in models"
            :key="model.id"
            class="model-card"
            @click="handleEditItem(model)"
          >
            <div class="model-card__head">
              <div class="model-card__icon" :class="model.modelType">
                <img v-if="model.providerAvatar" :src="model.providerAvatar" class="model-card__avatar" :alt="model.modelName" />
                <MessageOutlined v-else-if="model.modelType === 'chat'" />
                <PartitionOutlined v-else-if="model.modelType === 'embedding'" />
                <PictureOutlined v-else-if="model.modelType === 'image'" />
                <CloudServerOutlined v-else />
              </div>
              <div class="model-card__meta">
                <span class="model-card__name">{{ model.modelName }}</span>
                <span class="model-card__key">{{ model.modelKey }}</span>
              </div>
              <div class="model-card__status">
                <CheckCircleOutlined v-if="model.status === 'enabled'" class="status-enabled" />
                <CloseCircleOutlined v-else class="status-disabled" />
              </div>
            </div>

            <div class="model-card__divider" />

            <div class="model-card__caps" v-if="getModelCaps(model).length">
              <span
                v-for="cap in getModelCaps(model)"
                :key="cap"
                class="cap-tag"
              >
                {{ capLabel(cap) }}
              </span>
            </div>

            <div class="model-card__foot">
              <span
                v-for="p in getModelParams(model)"
                :key="p"
                class="param-chip"
              >
                {{ capLabel(p) }}
              </span>
              <span v-if="!getModelParams(model).length" class="param-empty">暂无参数配置</span>
            </div>
          </div>
        </div>

        <div v-if="!models.length" class="model-section__empty">
          <a-empty description="暂无模型" :image-style="{ height: '48px' }" />
        </div>
      </template>
    </div>

    <AstrsomnPagination
      v-if="total > 0"
      :current="pageNo"
      :page-size="pageSize"
      :total="total"
      :show-size-changer="false"
      @change="handlePageChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  CloudServerOutlined,
  MessageOutlined,
  PartitionOutlined,
  PictureOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  PlusOutlined,
} from '@ant-design/icons-vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import AstrsomnPagination from '@/components/home/AstrsomnPagination.vue'
import { aiModelApi, type AiModel } from '@/api/aiModel.ts'
import { ensureWorkspaceEnvInStorage } from '@/utils/workspaceHelper.ts'
import { aiModelCapabilitiesDictionary } from '@/locales/zh-CN/dictionary/ai-config/ai-model.ts'
import {
  CHAT_CAPABILITIES_SET,
  CHAT_PARAM_CODES,
  EMBEDDING_CAPABILITIES_SET,
  EMBEDDING_PARAM_CODES,
  IMAGE_CAPABILITIES_SET,
  IMAGE_PARAM_CODES,
} from '@/constants/aiModelEnums'

const props = defineProps<{
  providerKey?: string
}>()

const router = useRouter()
const keyword = ref('')
const loading = ref(false)
const models = ref<AiModel[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(12)

const handleSearch = () => {
  pageNo.value = 1
  void fetchModels()
}

const handlePageChange = (page: number, size: number) => {
  pageNo.value = page
  pageSize.value = size
  void fetchModels()
}

function parseCapabilities(model: AiModel): string[] {
  try {
    const parsed = JSON.parse(model.capabilities || '[]')
    return Array.isArray(parsed) ? parsed.map(String) : []
  } catch {
    return []
  }
}

function parseParams(model: AiModel): any[] {
  try {
    const parsed = JSON.parse(model.param || '[]')
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

function getModelCaps(model: AiModel): string[] {
  const allCaps = parseCapabilities(model)
  let capSet: Set<string>
  if (model.modelType === 'embedding') capSet = EMBEDDING_CAPABILITIES_SET
  else if (model.modelType === 'image') capSet = IMAGE_CAPABILITIES_SET
  else capSet = CHAT_CAPABILITIES_SET
  return allCaps.filter((c) => capSet.has(c))
}

function getModelParams(model: AiModel): string[] {
  const params = parseParams(model)
  let paramCodes: readonly string[]
  if (model.modelType === 'embedding') paramCodes = EMBEDDING_PARAM_CODES
  else if (model.modelType === 'image') paramCodes = IMAGE_PARAM_CODES
  else paramCodes = CHAT_PARAM_CODES
  const activeIds = params.filter((p: any) => p.active !== false).map((p: any) => String(p.id))
  if (activeIds.length) {
    return activeIds.filter((id: string) => (paramCodes as readonly string[]).includes(id))
  }
  return paramCodes as unknown as string[]
}

function capLabel(code: string): string {
  return aiModelCapabilitiesDictionary.getLabel(code) ?? code
}

const fetchModels = async () => {
  if (!props.providerKey) {
    models.value = []
    total.value = 0
    return
  }
  loading.value = true
  try {
    await ensureWorkspaceEnvInStorage()
    const resp = await aiModelApi.queryPage({
      pageNo: pageNo.value,
      pageSize: pageSize.value,
      param: {
        extensionCode: props.providerKey !== 'all' ? props.providerKey : undefined,
        modelName: keyword.value.trim() || undefined,
      },
    })
    models.value = resp.list || []
    total.value = resp.total || 0
  } catch {
    models.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  router.push('/admin/ai-config/models')
}

const handleEditItem = (model: AiModel) => {
  router.push({ path: '/admin/ai-config/models', query: { edit: model.id } })
}

watch(() => props.providerKey, () => {
  keyword.value = ''
  pageNo.value = 1
  void fetchModels()
})

onMounted(() => {
  void fetchModels()
})
</script>

<style scoped>
.model-section {
  display: flex;
  flex-direction: column;
  padding: 24px 32px 32px;
  height: calc(100vh - 60px);
}

.model-section__toolbar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  flex-shrink: 0;
  margin-bottom: 24px;
}

.model-section__search {
  width: 360px;
  flex-shrink: 0;
}

.model-section__search :deep(.toolbar-search-pill) {
  height: 40px;
  border-radius: 20px;
  padding: 0 6px 0 16px;
  background: var(--bg-card);
}

.model-section__search :deep(.toolbar-search-pill__input) {
  font-size: 13px;
}

.model-section__search :deep(.toolbar-search-pill__btn) {
  width: 30px;
  height: 30px;
  border-radius: 50%;
}

.model-section__count {
  font-size: 13px;
  color: var(--text-muted);
  flex-shrink: 0;
}

.model-section__create-btn {
  border-radius: 20px;
  font-weight: 600;
  padding: 0 24px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.2);
}

.model-section__body {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
}

.model-section__loading {
  display: flex;
  justify-content: center;
  padding: 60px 0;
}

.model-section__grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.model-card {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 20px;
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-default);
  background: var(--bg-card);
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.model-card:hover {
  border-color: var(--primary);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.1);
  transform: translateY(-2px);
}

.model-card__head {
  display: flex;
  align-items: center;
  gap: 12px;
}

.model-card__icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 16px;
  background: rgba(59, 130, 246, 0.08);
  color: #60a5fa;
  overflow: hidden;
}

.model-card__icon.embedding {
  background: rgba(16, 185, 129, 0.08);
  color: #34d399;
}

.model-card__icon.image {
  background: rgba(245, 158, 11, 0.08);
  color: #f59e0b;
}

.model-card__avatar {
  width: 22px;
  height: 22px;
  object-fit: contain;
  border-radius: 4px;
}

.model-card__meta {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.model-card__name {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.model-card__key {
  font-size: 11px;
  color: var(--text-muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}

.model-card__status {
  flex-shrink: 0;
  font-size: 16px;
}

.status-enabled {
  color: #52c41a;
}

.status-disabled {
  color: #ff4d4f;
}

.model-card__divider {
  height: 1px;
  background: var(--border-default);
  opacity: 0.5;
}

.model-card__caps {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.cap-tag {
  display: inline-flex;
  align-items: center;
  height: 22px;
  padding: 0 10px;
  border-radius: 11px;
  font-size: 11px;
  font-weight: 500;
  background: rgba(59, 130, 246, 0.06);
  color: #3b82f6;
  line-height: 1;
  white-space: nowrap;
}

.model-card__foot {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.param-chip {
  display: inline-flex;
  align-items: center;
  height: 20px;
  padding: 0 8px;
  border-radius: 4px;
  font-size: 10px;
  color: var(--text-muted);
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  white-space: nowrap;
  opacity: 0.7;
}

.param-empty {
  font-size: 11px;
  color: var(--text-muted);
  opacity: 0.5;
}

.model-section__empty {
  display: flex;
  justify-content: center;
  padding: 60px 0;
}
</style>
