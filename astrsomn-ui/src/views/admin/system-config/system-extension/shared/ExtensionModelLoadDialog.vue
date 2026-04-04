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
          <div class="ems-logo-box">
            <CloudUploadOutlined />
          </div>
          <div class="ems-title-group">
            <span class="ems-main-title">确认加载模型</span>
            <span class="ems-sub-title">{{ extensionLabel || '扩展' }}</span>
          </div>
        </div>
        <div class="ems-header-actions">
          <div class="ems-header-action-pair">
            <a-button class="ems-header-action-btn ems-header-btn-cancel" @click="emit('cancel')">
              取消
            </a-button>
            <a-button
              type="primary"
              class="ems-header-action-btn ems-header-btn-ok"
              :disabled="okDisabled"
              :loading="confirming"
              @click="handleOk"
            >
              确认加载模型
            </a-button>
          </div>
        </div>
      </header>

      <div class="ems-body-scroll">
        <div v-if="previewError" class="ems-modal-alert">
          <a-alert type="error" :message="previewError" show-icon />
        </div>
        <a-spin v-else :spinning="loadingPreview">
          <template v-if="loadPreview">
            <p v-if="emptyHint" class="ems-hint">{{ emptyHint }}</p>
            <p class="ems-summary">已选择 {{ selectedCount }} 个模型将写入本环境。</p>
            <div v-if="(loadPreview.skippedInvalidCount ?? 0) > 0" class="ems-hint">
              厂商返回条目中有 {{ loadPreview.skippedInvalidCount }} 条缺少 modelKey，将跳过。
            </div>
            <div class="ems-preview-section">
              <div class="ems-preview-section-title">
                将保存（新增）
                <span v-if="(loadPreview.toCreate?.length ?? 0) > 0" class="ems-select-all">
                  <a-checkbox :checked="selectAllToCreate" @update:checked="onSelectAllToCreate">
                    全选
                  </a-checkbox>
                </span>
              </div>
              <div v-if="(loadPreview.toCreate?.length ?? 0) > 0" class="ems-preview-list">
                <div v-for="(r, i) in loadPreview.toCreate" :key="'c' + i" class="ems-preview-line">
                  <a-checkbox
                    :checked="selectedCreateModels[r.modelKey]"
                    @update:checked="(v) => setCreateRowChecked(r.modelKey, v)"
                  />
                  <span class="ems-model-info">{{ formatExtensionModelPreviewRow(r) }}</span>
                </div>
              </div>
              <div v-else class="ems-preview-empty">无</div>
            </div>
            <div class="ems-preview-section">
              <div class="ems-preview-section-title">已存在将跳过</div>
              <div v-if="(loadPreview.skippedExisting?.length ?? 0) > 0" class="ems-preview-list">
                <div v-for="(r, i) in loadPreview.skippedExisting" :key="'s' + i" class="ems-preview-line">
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
import { CloudUploadOutlined } from '@ant-design/icons-vue'
import type { ExtensionModelLoadPreview } from '@/api/systemExtension'
import { formatExtensionModelPreviewRow } from './extensionModelSyncPreview'

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

const selectedCreateModels = ref<Record<string, boolean>>({})

watch(
  () => props.loadPreview,
  (newPreview) => {
    if (newPreview?.toCreate) {
      const next: Record<string, boolean> = {}
      newPreview.toCreate.forEach((model) => {
        next[model.modelKey] = false
      })
      selectedCreateModels.value = next
    } else {
      selectedCreateModels.value = {}
    }
  },
  { deep: true }
)

const selectAllToCreate = computed(() => {
  if (!props.loadPreview?.toCreate?.length) return false
  return props.loadPreview.toCreate.every((m) => selectedCreateModels.value[m.modelKey])
})

function onSelectAllToCreate(value: boolean) {
  if (!props.loadPreview?.toCreate) return
  const next = { ...selectedCreateModels.value }
  props.loadPreview.toCreate.forEach((m) => {
    next[m.modelKey] = value
  })
  selectedCreateModels.value = next
}

function setCreateRowChecked(modelKey: string, value: boolean) {
  selectedCreateModels.value = { ...selectedCreateModels.value, [modelKey]: value }
}

const selectedCount = computed(
  () => Object.entries(selectedCreateModels.value).filter(([, v]) => v).length
)

const okDisabled = computed(() => {
  if (props.loadingPreview || props.previewError) return true
  return selectedCount.value === 0
})

const emptyHint = computed(() => {
  if (props.loadingPreview || props.previewError) return ''
  if (!props.loadPreview) return ''
  const p = props.loadPreview
  const total = (p.toCreate?.length ?? 0) + (p.skippedExisting?.length ?? 0) + (p.skippedInvalidCount ?? 0)
  if (total === 0) return '厂商未返回可用模型条目，确认后不会产生新增。'
  return ''
})

function onUpdateOpen(v: boolean) {
  open.value = v
  if (!v) emit('cancel')
}

async function handleOk() {
  const keys = Object.entries(selectedCreateModels.value)
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
