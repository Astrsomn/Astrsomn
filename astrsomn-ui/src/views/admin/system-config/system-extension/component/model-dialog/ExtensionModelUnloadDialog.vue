<template>
  <a-modal
    :closable="false"
    :footer="null"
    :open="open"
    :width="modalWidth"
    centered
    destroy-on-close
    wrap-class-name="extension-model-sync-wrap"
    @update:open="onUpdateOpen"
  >
    <div class="ems-shell">
      <header class="ems-modal-header">
        <div class="ems-header-left">
          <div class="ems-logo-box ems-logo-unload">
            <DeleteOutlined />
          </div>
          <div class="ems-title-group">
            <span class="ems-main-title">确认卸载模型</span>
            <span class="ems-sub-title">{{ extensionLabel || '扩展' }}</span>
          </div>
        </div>
        <div class="ems-header-actions">
          <div class="ems-header-action-pair">
            <a-button class="ems-header-action-btn ems-header-btn-cancel" @click="emit('cancel')">取消</a-button>
            <a-button
              :disabled="okDisabled"
              :loading="confirming"
              class="ems-header-action-btn ems-header-btn-ok"
              danger
              type="primary"
              @click="handleOk"
            >
              确认卸载模型
            </a-button>
          </div>
        </div>
      </header>

      <div class="ems-body-scroll">
        <div v-if="previewError" class="ems-modal-alert">
          <a-alert :message="previewError" show-icon type="error" />
        </div>
        <a-spin v-else :spinning="loadingPreview">
          <template v-if="unloadPreview">
            <p v-if="emptyHint" class="ems-hint">{{ emptyHint }}</p>

            <div class="ems-summary-row">
              <p class="ems-summary ems-summary-danger">
                已选择 <strong>{{ selectedCount }}</strong> 个模型将从本环境删除。
              </p>
              <a-button size="small" type="link" danger @click="toggleSelectAll">
                {{ allSelected ? '取消全选' : '全选' }}
              </a-button>
            </div>

            <div class="ems-preview-section">
              <div class="ems-preview-section-title">
                <span>将卸载（删除）</span>
                <span class="ems-count-badge danger">{{ unloadPreview.toRemove?.length ?? 0 }}</span>
              </div>
              <div v-if="(unloadPreview.toRemove?.length ?? 0) > 0" class="ems-card-grid">
                <div
                  v-for="model in unloadPreview.toRemove"
                  :key="model.modelKey"
                  :class="{ selected: selectedRowKeys.includes(model.modelKey!) }"
                  class="ems-model-card"
                  @click="toggleModel(model.modelKey!)"
                >
                  <div class="ems-card-accent" :style="{ background: modelTypeColor(model.modelType) }"></div>
                  <div class="ems-card-body">
                    <div class="ems-card-header">
                      <span class="ems-card-type" :style="{ color: modelTypeColor(model.modelType) }">
                        {{ modelTypeLabel(model.modelType) }}
                      </span>
                      <a-checkbox :checked="selectedRowKeys.includes(model.modelKey!)" @click.stop="toggleModel(model.modelKey!)" />
                    </div>
                    <div class="ems-card-name">{{ model.modelName || model.modelKey }}</div>
                    <div class="ems-card-key">{{ model.modelKey }}</div>
                    <div v-if="model.provider" class="ems-card-provider">{{ model.provider }}</div>
                  </div>
                </div>
              </div>
              <div v-else class="ems-preview-empty">无</div>
            </div>

            <div class="ems-preview-section">
              <div class="ems-preview-section-title">
                <span>因实例引用将保留</span>
                <span class="ems-count-badge muted">{{ unloadPreview.keptReferenced?.length ?? 0 }}</span>
              </div>
              <div v-if="(unloadPreview.keptReferenced?.length ?? 0) > 0" class="ems-card-grid">
                <div
                  v-for="model in unloadPreview.keptReferenced"
                  :key="model.modelKey"
                  class="ems-model-card skipped"
                >
                  <div class="ems-card-accent" :style="{ background: modelTypeColor(model.modelType), opacity: 0.35 }"></div>
                  <div class="ems-card-body">
                    <span class="ems-card-type" :style="{ color: modelTypeColor(model.modelType), opacity: 0.5 }">
                      {{ modelTypeLabel(model.modelType) }}
                    </span>
                    <div class="ems-card-name">{{ model.modelName || model.modelKey }}</div>
                    <div class="ems-card-key">{{ model.modelKey }}</div>
                    <div v-if="model.provider" class="ems-card-provider">{{ model.provider }}</div>
                  </div>
                </div>
              </div>
              <div v-else class="ems-preview-empty">无</div>
            </div>
          </template>
        </a-spin>
      </div>
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
import {computed, ref, watch} from 'vue'
import {DeleteOutlined} from '@ant-design/icons-vue'
import type {ExtensionModelUnloadPreview} from '@/api/systemExtension.ts'
import {modelTypeColor, modelTypeLabel} from '../../utils/extensionDisplay.ts'

const open = defineModel<boolean>('open', { required: true })

const props = defineProps<{
  extensionLabel: string
  loadingPreview: boolean
  previewError: string
  unloadPreview: ExtensionModelUnloadPreview | null
  confirm: (selectedModelKeys: string[]) => Promise<void>
}>()

const emit = defineEmits<{
  cancel: []
}>()

const modalWidth = 'min(92vw, 820px)'
const confirming = ref(false)

const selectedRowKeys = ref<string[]>([])

watch(
  () => props.unloadPreview,
  (newPreview) => {
    if (newPreview?.toRemove) {
      selectedRowKeys.value = newPreview.toRemove.map((m) => m.modelKey!)
    } else {
      selectedRowKeys.value = []
    }
  },
  { immediate: true },
)

const selectedCount = computed(() => selectedRowKeys.value.length)

const allSelected = computed(() => {
  const list = props.unloadPreview?.toRemove ?? []
  return list.length > 0 && list.every((m) => selectedRowKeys.value.includes(m.modelKey!))
})

const okDisabled = computed(() => {
  if (props.loadingPreview || props.previewError) return true
  return selectedCount.value === 0
})

const emptyHint = computed(() => {
  if (props.loadingPreview || props.previewError) return ''
  if (!props.unloadPreview) return ''
  const p = props.unloadPreview
  const total = (p.toRemove?.length ?? 0) + (p.keptReferenced?.length ?? 0)
  if (total === 0) return '当前环境下该厂商暂无模型记录。'
  return ''
})

function toggleModel(key: string) {
  const idx = selectedRowKeys.value.indexOf(key)
  if (idx === -1) {
    selectedRowKeys.value = [...selectedRowKeys.value, key]
  } else {
    selectedRowKeys.value = selectedRowKeys.value.filter((k) => k !== key)
  }
}

function toggleSelectAll() {
  const list = props.unloadPreview?.toRemove ?? []
  if (allSelected.value) {
    selectedRowKeys.value = []
  } else {
    selectedRowKeys.value = list.map((m) => m.modelKey!)
  }
}

function onUpdateOpen(v: boolean) {
  open.value = v
  if (!v) emit('cancel')
}

async function handleOk() {
  if (selectedRowKeys.value.length === 0) return
  confirming.value = true
  try {
    await props.confirm(selectedRowKeys.value)
  } finally {
    confirming.value = false
  }
}
</script>

<style scoped>
@import '../../utils/extensionModelSyncDialog.css';

.ems-summary-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.ems-summary {
  margin: 0;
  font-size: 13px;
  font-weight: 500;
}

.ems-summary strong {
  font-weight: 700;
  font-size: 15px;
}

.ems-summary-danger {
  color: var(--error);
}

.ems-preview-section {
  margin-bottom: 24px;
}

.ems-preview-section-title {
  font-weight: 600;
  font-size: 13px;
  color: var(--text-primary);
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.ems-count-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 22px;
  height: 20px;
  padding: 0 6px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 600;
  background: rgba(239, 68, 68, 0.15);
  color: var(--error);
}

.ems-count-badge.muted {
  background: var(--bg-surface);
  color: var(--text-muted);
}


.ems-card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(210px, 1fr));
  gap: 8px;
}


.ems-model-card {
  display: flex;
  border: 1px solid var(--border-default);
  border-radius: 10px;
  overflow: hidden;
  background: var(--bg-card);
  cursor: pointer;
  transition: all 0.18s ease;
  user-select: none;
}

.ems-model-card:hover {
  border-color: rgba(239, 68, 68, 0.3);
  background: var(--bg-elevated);
}

.ems-model-card.selected {
  border-color: var(--error);
  background: rgba(239, 68, 68, 0.06);
  box-shadow: 0 0 0 1px rgba(239, 68, 68, 0.18);
}

.ems-model-card.skipped {
  cursor: default;
  opacity: 0.5;
}

.ems-model-card.skipped:hover {
  border-color: var(--border-default);
  background: var(--bg-card);
}

.ems-card-accent {
  width: 3px;
  flex-shrink: 0;
  border-radius: 0;
}

.ems-card-body {
  flex: 1;
  min-width: 0;
  padding: 10px 12px;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.ems-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.ems-card-type {
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.3px;
}

.ems-card-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ems-card-key {
  font-size: 11px;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  color: var(--text-muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ems-card-provider {
  font-size: 10px;
  color: var(--text-muted);
  margin-top: 2px;
  padding: 1px 6px;
  background: var(--bg-surface);
  border-radius: 4px;
  align-self: flex-start;
}

.ems-preview-empty {
  padding: 24px;
  text-align: center;
  color: var(--text-muted);
  border: 1px dashed var(--border-default);
  border-radius: 8px;
  font-size: 13px;
}
</style>
