<template>
  <a-modal
    v-model:open="open"
    :title="modalTitle"
    width="640px"
    destroy-on-close
    :ok-text="modalOkText"
    :ok-button-props="modalOkButtonProps"
    @ok="handleOk"
    @cancel="emit('cancel')"
  >
    <div v-if="previewError" class="model-sync-alert">
      <a-alert type="error" :message="previewError" show-icon />
    </div>
    <a-spin v-else :spinning="loadingPreview">
      <template v-if="mode === 'load' && loadPreview">
        <p v-if="emptyHint" class="model-sync-hint">{{ emptyHint }}</p>
        <div v-if="(loadPreview.skippedInvalidCount ?? 0) > 0" class="model-sync-hint">
          厂商返回条目中有 {{ loadPreview.skippedInvalidCount }} 条缺少 modelKey，将跳过。
        </div>
        <div class="preview-section">
          <div class="preview-section-title">将保存（新增）</div>
          <div v-if="(loadPreview.toCreate?.length ?? 0) > 0" class="preview-list">
            <div v-for="(r, i) in loadPreview.toCreate" :key="'c' + i" class="preview-line">
              {{ formatPreviewRow(r) }}
            </div>
          </div>
          <div v-else class="preview-empty">无</div>
        </div>
        <div class="preview-section">
          <div class="preview-section-title">已存在将跳过</div>
          <div v-if="(loadPreview.skippedExisting?.length ?? 0) > 0" class="preview-list">
            <div v-for="(r, i) in loadPreview.skippedExisting" :key="'s' + i" class="preview-line">
              {{ formatPreviewRow(r) }}
            </div>
          </div>
          <div v-else class="preview-empty">无</div>
        </div>
      </template>
      <template v-else-if="mode === 'unload' && unloadPreview">
        <p v-if="emptyHint" class="model-sync-hint">{{ emptyHint }}</p>
        <div class="preview-section">
          <div class="preview-section-title">将卸载（删除）</div>
          <div v-if="(unloadPreview.toRemove?.length ?? 0) > 0" class="preview-list">
            <div v-for="(r, i) in unloadPreview.toRemove" :key="'r' + i" class="preview-line">
              {{ formatPreviewRow(r) }}
            </div>
          </div>
          <div v-else class="preview-empty">无</div>
        </div>
        <div class="preview-section">
          <div class="preview-section-title">因实例引用将保留</div>
          <div v-if="(unloadPreview.keptReferenced?.length ?? 0) > 0" class="preview-list">
            <div v-for="(r, i) in unloadPreview.keptReferenced" :key="'k' + i" class="preview-line">
              {{ formatPreviewRow(r) }}
            </div>
          </div>
          <div v-else class="preview-empty">无</div>
        </div>
      </template>
    </a-spin>
  </a-modal>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type {
  ExtensionModelLoadPreview,
  ExtensionModelSyncPreviewRow,
  ExtensionModelUnloadPreview
} from '@/api/systemExtension'

const open = defineModel<boolean>('open', { required: true })

const props = defineProps<{
  mode: 'load' | 'unload' | null
  extensionLabel: string
  loadingPreview: boolean
  previewError: string
  loadPreview: ExtensionModelLoadPreview | null
  unloadPreview: ExtensionModelUnloadPreview | null
  confirm: () => Promise<void>
}>()

const emit = defineEmits<{
  cancel: []
}>()

function formatPreviewRow(r: ExtensionModelSyncPreviewRow) {
  const parts = [r.modelKey, r.modelName, r.modelType, r.provider].filter(Boolean)
  return parts.length ? parts.join(' · ') : '—'
}

const modalTitle = computed(() => {
  const name = props.extensionLabel || '扩展'
  if (props.mode === 'load') return `确认加载模型 — ${name}`
  if (props.mode === 'unload') return `确认卸载模型 — ${name}`
  return '模型同步'
})

const modalOkText = computed(() =>
  props.mode === 'load' ? '确认加载模型' : props.mode === 'unload' ? '确认卸载模型' : '确认'
)

const modalOkDisabled = computed(
  () => props.loadingPreview || Boolean(props.previewError) || props.mode == null
)

const modalOkButtonProps = computed(() => ({
  disabled: modalOkDisabled.value,
  danger: props.mode === 'unload'
}))

const emptyHint = computed(() => {
  if (props.loadingPreview || props.previewError) return ''
  if (props.mode === 'load' && props.loadPreview) {
    const p = props.loadPreview
    const total = (p.toCreate?.length ?? 0) + (p.skippedExisting?.length ?? 0) + (p.skippedInvalidCount ?? 0)
    if (total === 0) return '厂商未返回可用模型条目，确认后不会产生新增。'
  }
  if (props.mode === 'unload' && props.unloadPreview) {
    const p = props.unloadPreview
    const total = (p.toRemove?.length ?? 0) + (p.keptReferenced?.length ?? 0)
    if (total === 0) return '当前环境下该厂商暂无模型记录，确认后不会产生删除。'
  }
  return ''
})

async function handleOk() {
  await props.confirm()
}
</script>

<style scoped>
.model-sync-alert {
  margin-bottom: 8px;
}

.model-sync-hint {
  margin: 0 0 12px;
  color: var(--text-muted);
  font-size: 13px;
}

.preview-section {
  margin-bottom: 16px;
}

.preview-section-title {
  font-weight: 600;
  margin-bottom: 8px;
  font-size: 13px;
  color: var(--text-primary);
}

.preview-list {
  max-height: 220px;
  overflow: auto;
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  padding: 8px 10px;
  background: var(--bg-surface);
}

.preview-line {
  font-size: 12px;
  line-height: 1.5;
  padding: 2px 0;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  color: var(--text-secondary);
}

.preview-empty {
  font-size: 12px;
  color: var(--text-muted);
}
</style>
