<template>
  <a-modal
      v-model:open="open"
      :body-style="{ maxHeight: '72vh', overflowY: 'auto' }"
      :footer="null"
      destroy-on-close
      :title="t.history.title"
      width="920px"
      @cancel="open = false"
  >
    <a-table
        :columns="columns"
        :data-source="rows"
        :loading="loading"
        :pagination="false"
        :scroll="{ x: 820 }"
        row-key="id"
        size="small"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <span>{{ renderEnabled(String(record.status || '')) }}</span>
        </template>
        <template v-else-if="column.key === 'createTime'">
          <span>{{ formatTime(record.createTime) }}</span>
        </template>
        <template v-else-if="column.key === 'actions'">
          <a-button size="small" type="link" @click="openDetail(record)">{{ t.history.viewContent }}</a-button>
        </template>
      </template>
    </a-table>

    <a-modal
        v-model:open="detailOpen"
        :footer="null"
        :title="t.history.versionContentTitle.replace('{version}', String(detailRow?.version ?? '—'))"
        destroy-on-close
        width="720px"
    >
      <a-descriptions :column="1" bordered class="mb-3" size="small">
        <a-descriptions-item :label="t.history.detailLabels.title">{{ detailRow?.promptTitle || '—' }}</a-descriptions-item>
        <a-descriptions-item :label="t.history.detailLabels.scene">{{ detailRow?.scene || '—' }}</a-descriptions-item>
        <a-descriptions-item :label="t.history.detailLabels.status">{{ renderEnabled(String(detailRow?.status || '')) }}</a-descriptions-item>
      </a-descriptions>
      <a-textarea
          :auto-size="{ minRows: 14, maxRows: 28 }"
          :value="detailRow?.promptContent || ''"
          class="history-content"
          readonly
      />
    </a-modal>
  </a-modal>
</template>

<script lang="ts" setup>
import {computed, ref, watch} from 'vue'
import {message} from 'ant-design-vue'
import {type AiPrompt, aiPromptApi} from '@/api/aiPrompt.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-prompt')

const props = defineProps<{
  promptKey?: string
  envCode?: string
}>()

const open = defineModel<boolean>('open', {required: true})

const loading = ref(false)
const rows = ref<AiPrompt[]>([])
const detailOpen = ref(false)
const detailRow = ref<AiPrompt | null>(null)

const enabledFilterOptions = computed(() => [
  {label: t.value.history.statusOptions.enabled, value: 'enabled'},
  {label: t.value.history.statusOptions.disabled, value: 'disabled'}
])

const renderEnabled = (f: string) => {
  return enabledFilterOptions.value.find((x) => x.value === f)?.label ?? f
}

const formatTime = (v: unknown) => {
  if (v == null || v === '') return '—'
  if (typeof v === 'string') return v
  return String(v)
}

const columns = computed(() => [
  {title: t.value.history.columns.version, dataIndex: 'version', key: 'version', width: 72},
  {title: t.value.history.columns.title, dataIndex: 'promptTitle', key: 'promptTitle', ellipsis: true},
  {title: t.value.history.columns.scene, dataIndex: 'scene', key: 'scene', width: 120, ellipsis: true},
  {title: t.value.history.columns.status, key: 'status', width: 90},
  {title: t.value.history.columns.createTime, key: 'createTime', width: 180},
  {title: t.value.history.columns.actions, key: 'actions', width: 100, fixed: 'right' as const}
])

function openDetail(record: AiPrompt) {
  detailRow.value = record
  detailOpen.value = true
}

async function load() {
  const pk = props.promptKey
  if (!pk) {
    rows.value = []
    return
  }
  loading.value = true
  try {
    rows.value = await aiPromptApi.history(pk, props.envCode)
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || t.value.history.loadFailed)
    rows.value = []
  } finally {
    loading.value = false
  }
}

watch(
    () => [open.value, props.promptKey, props.envCode] as const,
    ([isOpen]) => {
      if (isOpen) {
        void load()
      }
    }
)
</script>

<style scoped>
.history-content {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 13px;
}

.mb-3 {
  margin-bottom: 12px;
}
</style>
