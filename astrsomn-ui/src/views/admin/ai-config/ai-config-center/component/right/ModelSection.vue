<template>
  <div class="model-section">
    <!-- Ambient background -->
    <div class="model-section__ambient">
      <div class="ambient-orb ambient-orb--top" />
      <div class="ambient-orb ambient-orb--bottom" />
    </div>

    <!-- Glass toolbar -->
    <div class="model-section__toolbar">
      <div class="model-section__search">
        <AstSearchInput
            v-model="keyword"
            layout="pane"
            placeholder="搜索模型名称..."
            @search="handleSearch"
        />
      </div>
      <span class="model-section__count">
        <span class="count-dot" />
        共 <strong>{{ total }}</strong> 个模型
      </span>
      <a-tooltip title="新增模型">
        <a-button class="model-section__create-btn" shape="circle" size="large" type="primary" @click="handleCreate">
          <template #icon>
            <PlusOutlined/>
          </template>
        </a-button>
      </a-tooltip>
    </div>

    <!-- Body -->
    <div class="model-section__body">
      <div v-if="loading" class="model-section__loading">
        <a-spin size="small"/>
      </div>

      <template v-else>
        <div v-if="models.length" class="model-section__grid">
          <div
              v-for="(model, index) in models"
              :key="model.id"
              class="model-card"
              :style="{ '--stagger': `${Math.min(index * 50, 500)}ms` }"
              @click="handleEditItem(model)"
          >
            <!-- Card inner — clipped to fixed height -->
            <div class="model-card__inner">
              <!-- Card head -->
              <div class="model-card__head">
                <div :class="['model-card__icon', model.modelType]">
                  <img v-if="model.providerAvatar" :alt="model.modelName" :src="model.providerAvatar"
                       class="model-card__avatar"/>
                  <MessageOutlined v-else-if="model.modelType === 'chat'"/>
                  <PartitionOutlined v-else-if="model.modelType === 'embedding'"/>
                  <PictureOutlined v-else-if="model.modelType === 'image'"/>
                  <CloudServerOutlined v-else/>
                </div>
                <div class="model-card__meta">
                  <span class="model-card__name">{{ model.modelName }}</span>
                  <span class="model-card__key">{{ model.modelKey }}</span>
                </div>
                <div class="model-card__status">
                  <span v-if="model.status === 'enabled'" class="status-indicator status-indicator--enabled">
                    <span class="status-pulse" />
                    <span class="status-dot" />
                  </span>
                  <span v-else class="status-indicator status-indicator--disabled">
                    <CloseCircleOutlined class="status-disabled-icon"/>
                  </span>
                </div>
              </div>

              <!-- Divider -->
              <div class="model-card__divider"/>

              <!-- Capabilities -->
              <div v-if="getModelCaps(model).length" class="model-card__caps">
                <span
                    v-for="cap in visibleCaps(model)"
                    :key="cap"
                    class="cap-tag"
                >
                  {{ capLabel(cap) }}
                </span>
                <span
                    v-if="hiddenCapsCount(model) > 0"
                    class="cap-tag cap-tag--overflow"
                >
                  +{{ hiddenCapsCount(model) }}
                </span>
              </div>

              <!-- Params footer -->
              <div class="model-card__foot">
                <template v-if="getModelParams(model).length">
                  <span
                      v-for="p in visibleParams(model)"
                      :key="p"
                      class="param-chip"
                  >
                    {{ capLabel(p) }}
                  </span>
                  <span
                      v-if="hiddenParamsCount(model) > 0"
                      class="param-chip param-chip--overflow"
                  >
                    +{{ hiddenParamsCount(model) }}
                  </span>
                </template>
                <span v-else class="param-empty">暂无参数配置</span>
              </div>
            </div>

            <!-- Overflow content — hidden until hover, flows outside card inner -->
            <div v-if="hiddenCapsCount(model) > 0" class="model-card__overflow model-card__overflow--caps">
              <span
                  v-for="cap in hiddenCaps(model)"
                  :key="cap"
                  class="cap-tag"
              >
                {{ capLabel(cap) }}
              </span>
            </div>
            <div v-if="hiddenParamsCount(model) > 0" class="model-card__overflow model-card__overflow--params">
              <span
                  v-for="p in hiddenParams(model)"
                  :key="p"
                  class="param-chip"
              >
                {{ capLabel(p) }}
              </span>
            </div>
          </div>
        </div>

        <div v-else class="model-section__empty">
          <div class="empty-state">
            <div class="empty-state__icon">
              <CloudServerOutlined />
            </div>
            <p class="empty-state__text">暂无模型</p>
            <p class="empty-state__hint">点击上方 + 按钮新增一个模型</p>
          </div>
        </div>
      </template>
    </div>

    <!-- Pagination -->
    <div v-if="total > 0" class="model-section__pagination">
      <AstPagination
          :current="pageNo"
          :page-size="pageSize"
          :show-size-changer="false"
          :total="total"
          @change="handlePageChange"
      />
    </div>

    <!-- Create form -->
    <ModelForm
        v-model:open="formOpen"
        :initial-data="null"
        :status-options="statusOptions"
        :submit-handler="handleFormSubmit"
        mode="create"
    />

    <!-- Edit form (always mounted so open watch fires correctly) -->
    <ModelForm
        :key="editingModel?.id ?? 'edit'"
        v-model:open="editFormOpen"
        :initial-data="editingModel"
        :status-options="statusOptions"
        :submit-handler="handleEditSubmit"
        mode="edit"
    />
  </div>
</template>

<script lang="ts" setup>
import {ref, watch} from 'vue'
import {
  CloseCircleOutlined,
  CloudServerOutlined,
  MessageOutlined,
  PartitionOutlined,
  PictureOutlined,
  PlusOutlined,
} from '@ant-design/icons-vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import ModelForm from '@/views/admin/ai-config/ai-model/component/ModelForm.vue'
import {type AiModel, aiModelApi} from '@/api/aiModel.ts'
import {ensureWorkspaceEnvInStorage} from '@/utils/workspaceHelper.ts'
import {aiModelCapabilitiesDictionary} from '@/locales/zh-CN/dictionary/ai-config/ai-model.ts'
import {
  CHAT_CAPABILITIES_SET,
  CHAT_PARAM_CODES,
  EMBEDDING_CAPABILITIES_SET,
  EMBEDDING_PARAM_CODES,
  IMAGE_CAPABILITIES_SET,
  IMAGE_PARAM_CODES,
} from '@/constants/aiModelEnums.ts'

const VISIBLE_LIMIT = 3

const props = defineProps<{
  providerKey?: string
}>()

const keyword = ref('')
const loading = ref(false)
const models = ref<AiModel[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(18)

const formOpen = ref(false)
const editFormOpen = ref(false)
const editingModel = ref<AiModel | null>(null)
const statusOptions = [
  {label: '启用', value: 'enabled'},
  {label: '禁用', value: 'disabled'},
]

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

function visibleCaps(model: AiModel): string[] {
  return getModelCaps(model).slice(0, VISIBLE_LIMIT)
}

function hiddenCaps(model: AiModel): string[] {
  return getModelCaps(model).slice(VISIBLE_LIMIT)
}

function hiddenCapsCount(model: AiModel): number {
  return Math.max(0, getModelCaps(model).length - VISIBLE_LIMIT)
}

function visibleParams(model: AiModel): string[] {
  return getModelParams(model).slice(0, VISIBLE_LIMIT)
}

function hiddenParams(model: AiModel): string[] {
  return getModelParams(model).slice(VISIBLE_LIMIT)
}

function hiddenParamsCount(model: AiModel): number {
  return Math.max(0, getModelParams(model).length - VISIBLE_LIMIT)
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
  formOpen.value = true
}

const handleFormSubmit = async (payload: AiModel) => {
  await aiModelApi.create(payload)
  formOpen.value = false
  pageNo.value = 1
  await fetchModels()
}

const handleEditItem = async (model: AiModel) => {
  if (editFormOpen.value) {
    // Switching from one edit to another: close first so the watch fires on re-open
    editFormOpen.value = false
    await new Promise(r => setTimeout(r, 50))
  }
  editingModel.value = {...model}
  editFormOpen.value = true
}

const handleEditSubmit = async (payload: AiModel) => {
  await aiModelApi.update(payload)
  editFormOpen.value = false
  await fetchModels()
}

watch(() => props.providerKey, () => {
  keyword.value = ''
  pageNo.value = 1
  void fetchModels()
})

void fetchModels()
</script>

<style scoped>
/* =====================================================================
   ModelSection — Glassmorphism Card Grid
   Fixed cards · Scale hover · Overflow reveal · GPU smooth
   ===================================================================== */

/* ----- Glass tokens (theme-adaptive via color-mix) ----- */
.model-section {
  --ms-glass-bg: color-mix(in srgb, var(--bg-card) 50%, transparent);
  --ms-glass-bg-hover: color-mix(in srgb, var(--bg-card) 76%, transparent);
  --ms-glass-border: color-mix(in srgb, var(--border-default) 55%, transparent);
  --ms-glass-border-hover: color-mix(in srgb, var(--primary) 38%, transparent);
  --ms-glow-ring: color-mix(in srgb, var(--primary) 16%, transparent);
  --ms-card-radius: 14px;
  --ms-card-height: 196px;
  --ms-ease-out: cubic-bezier(0.22, 0.61, 0.36, 1);
  --ms-ease-spring: cubic-bezier(0.34, 1.56, 0.64, 1);
  --ms-scale: 1.0;

  display: flex;
  flex-direction: column;
  padding: 28px 36px 36px;
  flex: 1;
  position: relative;
  isolation: isolate;
  min-height: 0;
}

/* ----- Ambient orbs ----- */
.model-section__ambient {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: -1;
  overflow: hidden;
}

.ambient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(120px);
  opacity: 0.14;
}

.ambient-orb--top {
  width: 420px;
  height: 420px;
  top: -180px;
  right: -80px;
  background: var(--primary);
}

.ambient-orb--bottom {
  width: 320px;
  height: 320px;
  bottom: -120px;
  left: -60px;
  background: var(--primary-light);
  opacity: 0.08;
}

/* =====================================================================
   TOOLBAR
   ===================================================================== */
.model-section__toolbar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  flex-shrink: 0;
  margin-bottom: 28px;
  padding: 8px 8px 8px 18px;
  background: var(--ms-glass-bg);
  backdrop-filter: blur(18px) saturate(180%);
  -webkit-backdrop-filter: blur(18px) saturate(180%);
  border: 1px solid var(--ms-glass-border);
  border-radius: 16px;
  box-shadow:
    0 4px 24px rgba(0, 0, 0, 0.10),
    inset 0 1px 0 rgba(255, 255, 255, 0.03);
  position: sticky;
  top: 0;
  z-index: 10;
}

.model-section__search {
  flex: 1;
  max-width: 400px;
  flex-shrink: 0;
}

.model-section__search :deep(.toolbar-search-pill) {
  height: 36px;
  border-radius: 18px;
  padding: 0 2px 0 12px;
  background: color-mix(in srgb, var(--bg-input) 38%, transparent);
  border: 1px solid color-mix(in srgb, var(--border-default) 28%, transparent);
  transition: all 0.3s ease;
}

.model-section__search :deep(.toolbar-search-pill:hover) {
  background: color-mix(in srgb, var(--bg-input) 58%, transparent);
  border-color: color-mix(in srgb, var(--border-default) 48%, transparent);
}

.model-section__search :deep(.toolbar-search-pill:focus-within) {
  background: color-mix(in srgb, var(--bg-input) 68%, transparent);
  border-color: color-mix(in srgb, var(--primary) 48%, transparent);
  box-shadow:
    0 0 0 3px var(--ms-glow-ring),
    0 0 24px color-mix(in srgb, var(--primary) 8%, transparent);
}

.model-section__search :deep(.toolbar-search-pill__input) {
  font-size: 13px;
  color: var(--text-primary);
}

.model-section__search :deep(.toolbar-search-pill__input::placeholder) {
  color: var(--text-muted);
  opacity: 0.5;
}

.model-section__search :deep(.toolbar-search-pill__btn) {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: color-mix(in srgb, var(--primary) 10%, transparent);
  color: var(--primary-light);
  transition: all 0.25s ease;
}

.model-section__search :deep(.toolbar-search-pill__btn:hover) {
  background: var(--primary);
  color: #fff;
}

/* Count */
.model-section__count {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--text-muted);
  flex-shrink: 0;
  font-variant-numeric: tabular-nums;
}

.count-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--primary-light);
  box-shadow: 0 0 8px color-mix(in srgb, var(--primary) 60%, transparent);
}

.model-section__count strong {
  color: var(--text-primary);
  font-weight: 600;
}

/* Create button */
.model-section__create-btn {
  flex-shrink: 0;
  box-shadow: 0 2px 12px rgba(59, 130, 246, 0.25);
  transition: transform 0.25s var(--ms-ease-spring), box-shadow 0.25s ease;
}

.model-section__create-btn:hover {
  transform: scale(1.08);
  box-shadow: 0 4px 20px rgba(59, 130, 246, 0.4);
}

.model-section__create-btn:active {
  transform: scale(0.94);
}

/* =====================================================================
   BODY
   ===================================================================== */
.model-section__body {
  flex: 1;
  min-height: 0;
}

.model-section__loading {
  display: flex;
  justify-content: center;
  padding: 80px 0;
}

/* =====================================================================
   GRID
   ===================================================================== */
.model-section__grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(284px, 1fr));
  gap: 18px;
  align-items: start;
}

/* =====================================================================
   CARD — Fixed height, scale-on-hover, overflow reveal
   ===================================================================== */
.model-card {
  --stagger-delay: var(--stagger, 0ms);

  display: flex;
  flex-direction: column;
  height: var(--ms-card-height);
  border-radius: var(--ms-card-radius);
  background: var(--ms-glass-bg);
  backdrop-filter: blur(16px) saturate(180%);
  -webkit-backdrop-filter: blur(16px) saturate(180%);
  border: 1px solid var(--ms-glass-border);
  cursor: pointer;
  position: relative;
  isolation: isolate;
  overflow: visible;
  will-change: transform;
  transition:
    transform 0.45s var(--ms-ease-spring),
    border-color 0.3s ease,
    box-shadow 0.35s ease,
    background 0.3s ease;
  z-index: 1;

  animation: card-rise-in 0.55s var(--ms-ease-out) backwards;
  animation-delay: var(--stagger-delay);
}

@keyframes card-rise-in {
  from {
    opacity: 0;
    transform: translateY(28px) scale(0.94);
    filter: blur(2px);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
    filter: blur(0);
  }
}

/* ---- Hover: scale up individually, no row shift ---- */
.model-card:hover {
  transform: scale(1.045);
  background: var(--ms-glass-bg-hover);
  border-color: var(--ms-glass-border-hover);
  box-shadow:
    0 12px 48px rgba(0, 0, 0, 0.26),
    0 0 0 1px var(--ms-glow-ring);
  z-index: 5;
}

/* ---- Shine sweep ---- */
.model-card::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background: linear-gradient(
    135deg,
    transparent 30%,
    rgba(255, 255, 255, 0.04) 50%,
    transparent 70%
  );
  opacity: 0;
  transition: opacity 0.4s ease;
  pointer-events: none;
  z-index: 1;
}

.model-card:hover::after {
  opacity: 1;
}

/* ---- Active press ---- */
.model-card:active {
  transform: scale(1.02);
  transition: transform 0.1s ease;
}

/* =====================================================================
   CARD INNER — clipped fixed-height zone
   ===================================================================== */
.model-card__inner {
  display: flex;
  flex-direction: column;
  gap: 10px;
  height: 100%;
  padding: 18px 20px 14px;
  overflow: hidden;
  border-radius: inherit;
  position: relative;
  z-index: 0;
}

/* =====================================================================
   CARD HEAD
   ===================================================================== */
.model-card__head {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.model-card__icon {
  width: 38px;
  height: 38px;
  border-radius: 11px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 17px;
  position: relative;
  overflow: hidden;
}

.model-card__icon::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  opacity: 0.12;
}

.model-card__icon.chat {
  background: rgba(59, 130, 246, 0.10);
  color: #60a5fa;
}
.model-card__icon.chat::after {
  background: linear-gradient(135deg, #3b82f6, #6366f1);
}

.model-card__icon.embedding {
  background: rgba(16, 185, 129, 0.10);
  color: #34d399;
}
.model-card__icon.embedding::after {
  background: linear-gradient(135deg, #10b981, #06b6d4);
}

.model-card__icon.image {
  background: rgba(245, 158, 11, 0.10);
  color: #f59e0b;
}
.model-card__icon.image::after {
  background: linear-gradient(135deg, #f59e0b, #ef4444);
}

.model-card__icon:not(.chat):not(.embedding):not(.image) {
  background: rgba(148, 163, 184, 0.10);
  color: #94a3b8;
}
.model-card__icon:not(.chat):not(.embedding):not(.image)::after {
  background: linear-gradient(135deg, #64748b, #475569);
}

.model-card__avatar {
  width: 22px;
  height: 22px;
  object-fit: contain;
  border-radius: 4px;
  position: relative;
  z-index: 1;
}

/* ----- Meta ----- */
.model-card__meta {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.model-card__name {
  font-size: 13.5px;
  color: var(--text-primary);
  font-weight: 600;
  letter-spacing: -0.01em;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.model-card__key {
  font-size: 10.5px;
  color: var(--text-muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-family: 'SF Mono', 'Cascadia Code', 'Fira Code', ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  letter-spacing: 0.02em;
}

/* ----- Status ----- */
.model-card__status {
  flex-shrink: 0;
}

.status-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.status-indicator--enabled {
  width: 22px;
  height: 22px;
}

.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #22c55e;
  position: relative;
  z-index: 1;
  box-shadow: 0 0 8px rgba(34, 197, 94, 0.5);
}

.status-pulse {
  position: absolute;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #22c55e;
  animation: status-pulse 2s ease-out infinite;
}

@keyframes status-pulse {
  0% { transform: scale(1); opacity: 0.6; }
  100% { transform: scale(2.8); opacity: 0; }
}

.status-disabled-icon {
  font-size: 16px;
  color: #ef4444;
  opacity: 0.7;
}

/* ----- Divider ----- */
.model-card__divider {
  height: 1px;
  flex-shrink: 0;
  background: linear-gradient(
    to right,
    color-mix(in srgb, var(--border-default) 45%, transparent),
    color-mix(in srgb, var(--border-default) 25%, transparent),
    transparent
  );
}

/* =====================================================================
   CAPS + PARAMS inside inner
   ===================================================================== */
.model-card__caps {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 5px;
  min-height: 0;
}

.cap-tag {
  display: inline-flex;
  align-items: center;
  height: 23px;
  padding: 0 11px;
  border-radius: 11.5px;
  font-size: 11px;
  font-weight: 500;
  background: color-mix(in srgb, var(--primary) 8%, transparent);
  color: var(--primary-light);
  border: 1px solid color-mix(in srgb, var(--primary) 16%, transparent);
  white-space: nowrap;
  line-height: 1;
  transition: all 0.2s ease;
}

.cap-tag:hover {
  background: color-mix(in srgb, var(--primary) 14%, transparent);
  border-color: color-mix(in srgb, var(--primary) 32%, transparent);
}

.cap-tag--overflow {
  background: color-mix(in srgb, var(--bg-surface) 45%, transparent);
  color: var(--text-muted);
  border: 1px dashed color-mix(in srgb, var(--border-default) 48%, transparent);
  font-family: 'SF Mono', 'Cascadia Code', 'Fira Code', ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 10.5px;
  letter-spacing: 0.03em;
  cursor: default;
}
.cap-tag--overflow:hover {
  background: color-mix(in srgb, var(--bg-surface) 45%, transparent);
  transform: none;
}

.model-card:hover .cap-tag--overflow,
.model-card:hover .param-chip--overflow {
  opacity: 0;
}

.model-card__foot {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 5px;
  margin-top: auto;
}

.param-chip {
  display: inline-flex;
  align-items: center;
  height: 21px;
  padding: 0 8px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 500;
  color: var(--text-secondary);
  background: color-mix(in srgb, var(--bg-surface) 55%, transparent);
  border: 1px solid color-mix(in srgb, var(--border-default) 38%, transparent);
  white-space: nowrap;
  font-family: 'SF Mono', 'Cascadia Code', 'Fira Code', ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  letter-spacing: 0.02em;
  transition: all 0.2s ease;
}

.param-chip:hover {
  color: var(--text-primary);
  border-color: color-mix(in srgb, var(--border-default) 65%, transparent);
  background: color-mix(in srgb, var(--bg-elevated) 55%, transparent);
}

.param-chip--overflow {
  background: transparent;
  border: 1px dashed color-mix(in srgb, var(--border-default) 35%, transparent);
  color: var(--text-muted);
  cursor: default;
}
.param-chip--overflow:hover {
  color: var(--text-muted);
  background: transparent;
}

.param-empty {
  font-size: 10.5px;
  color: var(--text-muted);
  opacity: 0.4;
  font-style: italic;
}

/* =====================================================================
   OVERFLOW — hidden items that reveal on hover (outside inner)
   ===================================================================== */
.model-card__overflow {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  padding: 0 20px;
  max-height: 0;
  opacity: 0;
  overflow: hidden;
  transition:
    max-height 0.4s var(--ms-ease-out),
    opacity 0.3s ease,
    padding-top 0.4s var(--ms-ease-out),
    padding-bottom 0.4s var(--ms-ease-out);
}

.model-card__overflow--caps {
  padding-top: 0;
}

.model-card__overflow--params {
  padding-bottom: 0;
}

.model-card:hover .model-card__overflow {
  max-height: 100px;
  opacity: 1;
}

.model-card:hover .model-card__overflow--caps {
  padding-top: 8px;
}

.model-card:hover .model-card__overflow--params {
  padding-bottom: 14px;
}

/* =====================================================================
   EMPTY STATE
   ===================================================================== */
.model-section__empty {
  display: flex;
  justify-content: center;
  padding: 80px 0;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.empty-state__icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: var(--text-muted);
  background: var(--ms-glass-bg);
  border: 1px solid var(--ms-glass-border);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
}

.empty-state__text {
  font-size: 15px;
  color: var(--text-secondary);
  font-weight: 500;
  margin: 0;
}

.empty-state__hint {
  font-size: 12px;
  color: var(--text-muted);
  opacity: 0.6;
  margin: 0;
}

/* =====================================================================
   PAGINATION
   ===================================================================== */
.model-section__pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
