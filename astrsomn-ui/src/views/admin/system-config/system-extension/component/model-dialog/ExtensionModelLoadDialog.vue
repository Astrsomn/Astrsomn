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
          <div class="ems-logo-box">
            <CloudUploadOutlined />
          </div>
          <div class="ems-title-group">
            <span class="ems-main-title">{{ t.loadDialog.title }}</span>
            <span class="ems-sub-title">{{ extensionLabel || t.loadDialog.extension }}</span>
          </div>
        </div>
        <div class="ems-header-actions">
          <div class="ems-header-action-pair">
            <a-button class="ems-header-action-btn ems-header-btn-cancel" @click="emit('cancel')">{{ t.loadDialog.cancel }}</a-button>
            <a-button
              :disabled="okDisabled"
              :loading="confirming"
              class="ems-header-action-btn ems-header-btn-ok"
              type="primary"
              @click="handleOk"
            >
              {{ t.loadDialog.confirm }}
            </a-button>
          </div>
        </div>
      </header>

      <div class="ems-body-scroll">
        <div v-if="previewError" class="ems-modal-alert">
          <a-alert :message="previewError" show-icon type="error" />
        </div>
        <a-spin v-else :spinning="loadingPreview">
          <template v-if="loadPreview">
            <p v-if="emptyHint" class="ems-hint">{{ emptyHint }}</p>

            <div class="ems-summary-row">
              <p class="ems-summary" v-html="selectedCountHtml"></p>
              <a-button size="small" type="link" @click="toggleSelectAll">
                {{ allSelected ? t.loadDialog.deselectAll : t.loadDialog.selectAll }}
              </a-button>
            </div>

            <div v-if="(loadPreview.skippedInvalidCount ?? 0) > 0" class="ems-hint" style="color: var(--error); margin-bottom: 12px;" v-html="invalidCountHtml"></div>

            <div class="ems-preview-section">
              <div class="ems-preview-section-title">
                <span>{{ t.loadDialog.toCreate }}</span>
                <span class="ems-count-badge">{{ loadPreview.toCreate?.length ?? 0 }}</span>
              </div>
              <div v-if="(loadPreview.toCreate?.length ?? 0) > 0" class="ems-card-grid">
                <div
                  v-for="model in loadPreview.toCreate"
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
              <div v-else class="ems-preview-empty">{{ t.loadDialog.noNewModels }}</div>
            </div>

            <div class="ems-preview-section">
              <div class="ems-preview-section-title">
                <span>{{ t.loadDialog.skippedExisting }}</span>
                <span class="ems-count-badge muted">{{ loadPreview.skippedExisting?.length ?? 0 }}</span>
              </div>
              <div v-if="(loadPreview.skippedExisting?.length ?? 0) > 0" class="ems-card-grid">
                <div
                  v-for="model in loadPreview.skippedExisting"
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
              <div v-else class="ems-preview-empty">{{ t.loadDialog.none }}</div>
            </div>
          </template>
        </a-spin>
      </div>
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
import {computed, ref, watch} from 'vue'
import {CloudUploadOutlined} from '@ant-design/icons-vue'
import type {ExtensionModelLoadPreview} from '@/api/systemExtension.ts'
import {modelTypeColor, modelTypeLabel} from '../../utils/extensionDisplay.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('system-extension')

const open = defineModel<boolean>('open', { required: true })

const props = defineProps<{
  extensionLabel: string
  loadingPreview: boolean
  previewError: string
  loadPreview: ExtensionModelLoadPreview | null
  confirm: (selectedModelKeys: string[]) => Promise<void>
}>()

const emit = defineEmits<{
  cancel: []
}>()

const modalWidth = 'min(92vw, 820px)'
const confirming = ref(false)

const selectedRowKeys = ref<string[]>([])

watch(
  () => props.loadPreview,
  (newPreview) => {
    if (newPreview?.toCreate) {
      selectedRowKeys.value = newPreview.toCreate.map((m) => m.modelKey!)
    } else {
      selectedRowKeys.value = []
    }
  },
  { immediate: true },
)

const selectedCount = computed(() => selectedRowKeys.value.length)

const selectedCountHtml = computed(() => {
  return t.value.loadDialog.selectedCount.replace('{count}', `<strong>${selectedCount.value}</strong>`)
})

const invalidCountHtml = computed(() => {
  if (!props.loadPreview?.skippedInvalidCount) return ''
  return t.value.loadDialog.invalidCount.replace('{count}', String(props.loadPreview!.skippedInvalidCount))
})

const allSelected = computed(() => {
  const list = props.loadPreview?.toCreate ?? []
  return list.length > 0 && list.every((m) => selectedRowKeys.value.includes(m.modelKey!))
})

const okDisabled = computed(() => {
  if (props.loadingPreview || props.previewError) return true
  return selectedCount.value === 0
})

const emptyHint = computed(() => {
  if (props.loadingPreview || props.previewError) return ''
  if (!props.loadPreview) return ''
  const p = props.loadPreview
  const total = (p.toCreate?.length ?? 0) + (p.skippedExisting?.length ?? 0) + (p.skippedInvalidCount ?? 0)
  if (total === 0) return t.value.loadDialog.noModelsFromProvider
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
  const list = props.loadPreview?.toCreate ?? []
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
  color: var(--primary);
  font-weight: 500;
}

.ems-summary strong {
  font-weight: 700;
  font-size: 15px;
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
  background: color-mix(in srgb, var(--primary) 15%, transparent);
  color: var(--primary);
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
  border-color: color-mix(in srgb, var(--primary) 35%, transparent);
  background: var(--bg-elevated);
}

.ems-model-card.selected {
  border-color: var(--primary);
  background: color-mix(in srgb, var(--primary) 8%, transparent);
  box-shadow: 0 0 0 1px color-mix(in srgb, var(--primary) 20%, transparent);
}

.ems-model-card.skipped {
  cursor: default;
  opacity: 0.55;
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
