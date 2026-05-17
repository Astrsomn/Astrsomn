<template>
  <a-modal
      v-model:open="open"
      :body-style="{ maxHeight: '72vh', overflowY: 'auto' }"
      :footer="null"
      destroy-on-close
      title="历史版本"
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
          <a-button size="small" type="link" @click="openDetail(record)">查看内容</a-button>
        </template>
      </template>
    </a-table>

    <a-modal
        v-model:open="detailOpen"
        :footer="null"
        :title="`版本 ${detailRow?.version ?? '—'} 内容`"
        destroy-on-close
        width="720px"
    >
      <a-descriptions :column="1" bordered class="mb-3" size="small">
        <a-descriptions-item label="标题">{{ detailRow?.promptTitle || '—' }}</a-descriptions-item>
        <a-descriptions-item label="场景">{{ detailRow?.scene || '—' }}</a-descriptions-item>
        <a-descriptions-item label="状态">{{ renderEnabled(String(detailRow?.status || '')) }}</a-descriptions-item>
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
import {ref, watch} from 'vue'
import {message} from 'ant-design-vue'
import {type AiPrompt, aiPromptApi} from '@/api/aiPrompt.ts'

const props = defineProps<{
  promptKey?: string
  envCode?: string
}>()

const open = defineModel<boolean>('open', {required: true})

const loading = ref(false)
const rows = ref<AiPrompt[]>([])
const detailOpen = ref(false)
const detailRow = ref<AiPrompt | null>(null)

const enabledFilterOptions = [
  {label: '启用', value: 'enabled'},
  {label: '停用', value: 'disabled'}
]

const renderEnabled = (f: string) => {
  return enabledFilterOptions.find((x) => x.value === f)?.label ?? f
}

const formatTime = (v: unknown) => {
  if (v == null || v === '') return '—'
  if (typeof v === 'string') return v
  return String(v)
}

const columns = [
  {title: '版本', dataIndex: 'version', key: 'version', width: 72},
  {title: '标题', dataIndex: 'promptTitle', key: 'promptTitle', ellipsis: true},
  {title: '场景', dataIndex: 'scene', key: 'scene', width: 120, ellipsis: true},
  {title: '状态', key: 'status', width: 90},
  {title: '创建时间', key: 'createTime', width: 180},
  {title: '操作', key: 'actions', width: 100, fixed: 'right' as const}
]

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
    message.error(err?.message || '加载历史失败')
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
