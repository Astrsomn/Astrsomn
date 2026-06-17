<template>
  <AstModal
    :open="open"
    :width="520"
    :body-height="420"
    :max-width="520"
    :footer="null"
    wrap-class-name="delete-source-modal"
    @update:open="emit('update:open', $event)"
  >
    <template #header-logo>
      <WarningOutlined />
    </template>
    <template #header-title>
      {{ t.vectorCenter.sidebar.deleteSourceConfirm }}
    </template>

    <div class="delete-content">
      <div class="delete-warning">
        <WarningOutlined class="warning-icon" />
        <span>{{ t.vectorCenter.sidebar.deleteSourceWarning }}</span>
      </div>

      <div class="delete-detail">
        <div class="detail-row">
          <span class="detail-label">{{ t.vectorCenter.sidebar.sourceName }}:</span>
          <span class="detail-value">{{ source?.name || '-' }}</span>
        </div>
        <div v-if="source?.dbs?.length" class="detail-row">
          <span class="detail-label">{{ t.vectorCenter.sidebar.associatedDatabases }}:</span>
          <span class="detail-value">{{ source.dbs.length }} {{ t.vectorCenter.sidebar.items }}</span>
        </div>
      </div>

      <div class="delete-input-group">
        <label class="delete-input-label">{{ t.vectorCenter.sidebar.enterSourceName }}</label>
        <a-input
          v-model:value="confirmInput"
          :placeholder="t.vectorCenter.sidebar.enterSourceNamePlaceholder"
          size="large"
        />
      </div>
    </div>

    <template #footer>
      <div class="delete-footer">
        <a-button @click="handleCancel">{{ t.vectorCenter.sidebar.cancel }}</a-button>
        <a-button
          type="primary"
          danger
          :disabled="!canConfirm"
          :loading="loading"
          @click="handleConfirm"
        >
          {{ t.vectorCenter.sidebar.confirmDelete }}
        </a-button>
      </div>
    </template>
  </AstModal>
</template>

<script lang="ts" setup>
import { computed, ref, watch } from 'vue'
import { WarningOutlined } from '@ant-design/icons-vue'
import AstModal from '@/components/home/AstModal.vue'
import { usePageTranslation } from '@/locales/pages.ts'

const t = usePageTranslation('ai-vector')

interface Source {
  id: number | string
  name: string
  dbs: Array<{ id: number | string; dbName: string }>
}

const props = defineProps<{
  source: Source | null
  loading?: boolean
}>()

const emit = defineEmits<{
  'update:open': [value: boolean]
  confirm: [source: Source]
}>()

const open = defineModel<boolean>('open', { required: true })

const confirmInput = ref('')

const canConfirm = computed(() => {
  if (!props.source) return false
  return confirmInput.value.trim() === props.source.name
})

watch(open, (val) => {
  if (val) {
    confirmInput.value = ''
  }
})

function handleCancel() {
  open.value = false
}

function handleConfirm() {
  if (!canConfirm.value || !props.source) return
  emit('confirm', props.source)
}
</script>

<style scoped>
.delete-content {
  display: flex;
  flex-direction: column;
  padding: 0 24px;
}

.delete-warning {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: color-mix(in srgb, var(--warning) 10%, transparent);
  border-radius: var(--radius-md);
  margin-bottom: 20px;
  border-left: 3px solid var(--warning);
}

.warning-icon {
  font-size: 20px;
  color: var(--warning);
  flex-shrink: 0;
  margin-top: 2px;
}

.delete-warning span {
  color: var(--text-primary);
  font-size: 13px;
  line-height: 1.6;
}

.delete-detail {
  background: var(--bg-input);
  border-radius: var(--radius-md);
  padding: 12px 16px;
  margin-bottom: 20px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
}

.detail-row:not(:last-child) {
  border-bottom: 1px solid var(--border-default);
}

.detail-label {
  color: var(--text-muted);
  font-size: 13px;
}

.detail-value {
  color: var(--text-primary);
  font-size: 13px;
  font-weight: 500;
}

.delete-input-group {
  margin-bottom: 8px;
}

.delete-input-label {
  display: block;
  color: var(--text-secondary);
  font-size: 13px;
  margin-bottom: 8px;
}

.delete-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
