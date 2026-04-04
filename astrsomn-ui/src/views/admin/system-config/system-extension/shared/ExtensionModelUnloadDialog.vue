<template>
  <a-modal
    :open="open"
    :footer="null"
    :closable="false"
    centered
    destroy-on-close
    :width="modalWidth"
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
            <a-button class="ems-header-action-btn ems-header-btn-cancel" @click="emit('cancel')">
              取消
            </a-button>
            <a-button
              danger
              type="primary"
              class="ems-header-action-btn ems-header-btn-ok"
              :disabled="okDisabled"
              :loading="confirming"
              @click="handleOk"
            >
              确认卸载模型
            </a-button>
          </div>
        </div>
      </header>

      <div class="ems-body-scroll">
        <div v-if="previewError" class="ems-modal-alert">
          <a-alert type="error" :message="previewError" show-icon />
        </div>
        <a-spin v-else :spinning="loadingPreview">
          <template v-if="unloadPreview">
            <p v-if="emptyHint" class="ems-hint">{{ emptyHint }}</p>
            <p class="ems-summary">已选择 {{ selectedCount }} 个模型将从本环境删除。</p>
            <div class="ems-preview-section">
              <div class="ems-preview-section-title">
                将卸载（删除）
                <span v-if="(unloadPreview.toRemove?.length ?? 0) > 0" class="ems-select-all">
                  <a-checkbox :checked="selectAllToRemove" @update:checked="onSelectAllToRemove">
                    全选
                  </a-checkbox>
                </span>
              </div>
              <div v-if="(unloadPreview.toRemove?.length ?? 0) > 0" class="ems-preview-list">
                <div v-for="(r, i) in unloadPreview.toRemove" :key="'r' + i" class="ems-preview-line">
                  <a-checkbox
                    :checked="selectedRemoveModels[r.modelKey]"
                    @update:checked="(v) => setRemoveRowChecked(r.modelKey, v)"
                  />
                  <span class="ems-model-info">{{ formatExtensionModelPreviewRow(r) }}</span>
                </div>
              </div>
              <div v-else class="ems-preview-empty">无</div>
            </div>
            <div class="ems-preview-section">
              <div class="ems-preview-section-title">因实例引用将保留</div>
              <div v-if="(unloadPreview.keptReferenced?.length ?? 0) > 0" class="ems-preview-list">
                <div v-for="(r, i) in unloadPreview.keptReferenced" :key="'k' + i" class="ems-preview-line">
                  <span class="ems-model-info">{{ formatExtensionModelPreviewRow(r) }}</span>
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

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { DeleteOutlined } from '@ant-design/icons-vue'
import type { ExtensionModelUnloadPreview } from '@/api/systemExtension'
import { formatExtensionModelPreviewRow } from './extensionModelSyncPreview'

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

const selectedRemoveModels = ref<Record<string, boolean>>({})

watch(
  () => props.unloadPreview,
  (newPreview) => {
    if (newPreview?.toRemove) {
      const next: Record<string, boolean> = {}
      newPreview.toRemove.forEach((model) => {
        next[model.modelKey] = false
      })
      selectedRemoveModels.value = next
    } else {
      selectedRemoveModels.value = {}
    }
  },
  { deep: true }
)

const selectAllToRemove = computed(() => {
  if (!props.unloadPreview?.toRemove?.length) return false
  return props.unloadPreview.toRemove.every((m) => selectedRemoveModels.value[m.modelKey])
})

function onSelectAllToRemove(value: boolean) {
  if (!props.unloadPreview?.toRemove) return
  const next = { ...selectedRemoveModels.value }
  props.unloadPreview.toRemove.forEach((m) => {
    next[m.modelKey] = value
  })
  selectedRemoveModels.value = next
}

function setRemoveRowChecked(modelKey: string, value: boolean) {
  selectedRemoveModels.value = { ...selectedRemoveModels.value, [modelKey]: value }
}

const selectedCount = computed(
  () => Object.entries(selectedRemoveModels.value).filter(([, v]) => v).length
)

const okDisabled = computed(() => {
  if (props.loadingPreview || props.previewError) return true
  return selectedCount.value === 0
})

const emptyHint = computed(() => {
  if (props.loadingPreview || props.previewError) return ''
  if (!props.unloadPreview) return ''
  const p = props.unloadPreview
  const total = (p.toRemove?.length ?? 0) + (p.keptReferenced?.length ?? 0)
  if (total === 0) return '当前环境下该厂商暂无模型记录，确认后不会产生删除。'
  return ''
})

function onUpdateOpen(v: boolean) {
  open.value = v
  if (!v) emit('cancel')
}

async function handleOk() {
  const keys = Object.entries(selectedRemoveModels.value)
    .filter(([, sel]) => sel)
    .map(([k]) => k)
  if (keys.length === 0) return
  confirming.value = true
  try {
    await props.confirm(keys)
  } finally {
    confirming.value = false
  }
}
</script>

<style scoped>
@import './extensionModelSyncDialog.css';
</style>
