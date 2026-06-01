<template>
  <AstDrawer
      :open="props.open"
      :width="640"
      :root-class-name="mergedRootClass"
      @update:open="handleClose"
  >
    <template #icon>
      <AppstoreOutlined/>
    </template>
    <template #title>{{ t.selector.title }}</template>
    <template #subtitle>{{ t.selector.subtitle }}</template>

    <div class="select-drawer-content">
      <div class="search-bar">
        <ExtensionSelector
            :allow-clear="true"
            :value="providerFilter"
            :placeholder="t.selector.providerPlaceholder"
            size="middle"
            @update:value="onProviderChange"
        />
        <AstSearchInput
            v-model="keyword"
            layout="fluid"
            :placeholder="t.selector.searchPlaceholder"
            @search="handleSearch"
        />
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
                <div v-if="getModelCaps(model).length" class="model-caps">
                  <span
                      v-for="cap in getModelCaps(model)"
                      :key="cap"
                      class="cap-tag"
                  >
                    <component :is="CAP_ICON_MAP[cap]" v-if="CAP_ICON_MAP[cap]" class="cap-icon"/>
                    {{ capLabel(cap) }}
                  </span>
                </div>
              </div>
            </div>
            <div class="model-meta">
              <span class="provider-tag">{{ model.extensionCode }}</span>
              <a-tag :color="model.status === 'enabled' ? 'green' : 'red'" class="status-tag">
                {{ model.status === 'enabled' ? t.selector.statusEnabled : t.selector.statusDisabled }}
              </a-tag>
            </div>
          </div>

          <a-empty v-if="!loading && list.length === 0" :description="t.selector.emptyText"/>
        </div>
      </a-spin>
    </div>

    <template #footer>
      <AstPagination
          :current="page.pageNum"
          :page-size="page.pageSize"
          :show-size-changer="false"
          :total="page.total"
          @change="onPageChange"
      />
    </template>
  </AstDrawer>
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import {
  AppstoreOutlined,
  AudioOutlined,
  BlockOutlined,
  BulbOutlined,
  CaretRightOutlined,
  CompressOutlined,
  EyeOutlined,
  FileTextOutlined,
  KeyOutlined,
  MessageOutlined,
  PartitionOutlined,
  PictureOutlined,
  ToolOutlined,
} from '@ant-design/icons-vue'
import {type AiModel, aiModelApi, type PageResponse} from '@/api/aiModel.ts'
import {CHAT_CAPABILITIES_SET, EMBEDDING_CAPABILITIES_SET, IMAGE_CAPABILITIES_SET} from '@/constants/aiModelEnums.ts'
import {aiModelCapabilitiesDictionary} from '@/locales/zh-CN/dictionary/ai-config/ai-model.ts'
import {WORKSPACE_ENV_STORAGE_KEY} from '@/constants/workspaceEnv.ts'
import AstDrawer from '@/components/home/AstDrawer.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import ExtensionSelector from '@/views/admin/system-config/system-extension/selector/ExtensionSelector.vue'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-model')

const CAP_ICON_MAP: Record<string, any> = {
  streaming: CaretRightOutlined,
  tools: ToolOutlined,
  vision: EyeOutlined,
  json_mode: BlockOutlined,
  deep_reasoning: BulbOutlined,
  context_caching: CompressOutlined,
  text_embedding: FileTextOutlined,
  image_embedding: PictureOutlined,
  text_to_image: PictureOutlined,
  image_to_image: PictureOutlined,
  image_editing: PictureOutlined,
}

const props = defineProps<{
  open: boolean
  fixedModelType?: string
  rootClassName?: string
}>()

const mergedRootClass = computed(() =>
    ['model-select-drawer', props.rootClassName].filter(Boolean).join(' ')
)

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

function parseCapabilities(model: AiModel): string[] {
  try {
    const parsed = JSON.parse(model.capabilities || '[]')
    return Array.isArray(parsed) ? parsed.map(String) : []
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

function capLabel(code: string): string {
  return aiModelCapabilitiesDictionary.getLabel(code) ?? code
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
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  padding: 14px 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.model-item:hover {
  border-color: var(--primary);
  background: var(--primary-hover);
  box-shadow: 0 2px 8px color-mix(in srgb, var(--primary) 12%, transparent);
}

.model-item.selected {
  border-color: var(--primary);
  background: var(--primary-hover);
  box-shadow: 0 0 0 3px color-mix(in srgb, var(--primary) 18%, transparent);
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
  background: var(--bg-card);
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
  color: var(--text-primary);
  margin-bottom: 3px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.model-key {
  font-size: 11px;
  color: var(--text-muted);
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
  background: var(--bg-surface);
  border-radius: 6px;
  color: var(--text-secondary);
  font-weight: 500;
}

.status-tag {
  font-size: 11px;
  margin: 0;
}

.model-caps {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-top: 5px;
}

.cap-tag {
  display: inline-flex;
  align-items: center;
  height: 20px;
  padding: 0 7px;
  border-radius: 10px;
  font-size: 10px;
  font-weight: 500;
  background: var(--primary-hover);
  color: var(--primary);
  line-height: 1;
  white-space: nowrap;
  gap: 3px;
}

.cap-icon {
  font-size: 10px;
}
</style>
