<template>
  <a-modal
    v-model:open="open"
    :title="title"
    width="880px"
    :confirm-loading="false"
    ok-text="确定"
    cancel-text="取消"
    @ok="handleConfirm"
    @cancel="handleCancel"
  >
    <div class="picker-toolbar">
      <a-input
        v-model:value="keyword"
        :placeholder="keywordPlaceholder"
        allow-clear
        class="picker-search"
        @press-enter="onSearch"
      />
      <a-button type="primary" @click="onSearch">查询</a-button>
    </div>

    <a-table
      size="small"
      :columns="columns"
      :data-source="list"
      :loading="loading"
      :pagination="false"
      :row-key="rowKey"
      :row-selection="rowSelection"
      :scroll="{ x: 'max-content' }"
    />

    <div class="picker-pagination">
      <a-pagination
        v-model:current="page.pageNum"
        :page-size="page.pageSize"
        :total="page.total"
        :show-size-changer="true"
        :page-size-options="['10', '20', '50']"
        @change="onPageChange"
        @show-size-change="onPageSizeChange"
      />
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import type { TableColumnsType } from 'ant-design-vue'
import { aiModelApi } from '@/api/aiModel.ts'
import { aiPromptApi } from '@/api/aiPrompt.ts'
import { aiToolApi } from '@/api/aiTool.ts'
import { aiMcpApi } from '@/api/aiMcp.ts'

export type ResourceKind = 'model' | 'prompt' | 'tool' | 'mcp'

const props = defineProps<{
  resourceType: ResourceKind
  /** 单选：模型为 AI_MODEL.id；提示词为 promptKey 字符串；多选：tool/mcp 为当前字段 JSON 字符串（用于回显勾选） */
  preset?: string | number | null
  multiple?: boolean
}>()

const emit = defineEmits<{
  confirm: [payload: { kind: ResourceKind; modelId?: number; promptKey?: string; keys?: string[] }]
}>()

const open = defineModel<boolean>('open', { required: true })

const keyword = ref('')
const loading = ref(false)
const list = ref<any[]>([])
const selectedRowKeys = ref<(string | number)[]>([])
const selectedRows = ref<any[]>([])

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const title = computed(() => {
  const m: Record<ResourceKind, string> = {
    model: '选择模型（AI_MODEL）',
    prompt: '选择系统提示词（AI_PROMPT）',
    tool: '选择工具（可多选）',
    mcp: '选择 MCP（可多选）'
  }
  return m[props.resourceType]
})

const keywordPlaceholder = computed(() => {
  const m: Record<ResourceKind, string> = {
    model: '模型名称',
    prompt: '标题 / Prompt Key',
    tool: '工具名称 / Tool Key',
    mcp: '服务名 / MCP Key'
  }
  return m[props.resourceType]
})

const rowKey = (record: any) => record.id as string | number

const columns = computed<TableColumnsType>(() => {
  if (props.resourceType === 'model') {
    return [
      { title: 'ID', dataIndex: 'id', key: 'id', width: 88 },
      { title: '模型名称', dataIndex: 'modelName', key: 'modelName', ellipsis: true },
      { title: 'Model Key', dataIndex: 'modelKey', key: 'modelKey', width: 140, ellipsis: true },
      { title: '供应商', dataIndex: 'provider', key: 'provider', width: 100 },
      { title: '状态', dataIndex: 'status', key: 'status', width: 88 }
    ]
  }
  if (props.resourceType === 'prompt') {
    return [
      { title: 'ID', dataIndex: 'id', key: 'id', width: 88 },
      { title: 'Prompt Key', dataIndex: 'promptKey', key: 'promptKey', width: 200, ellipsis: true },
      { title: '标题', dataIndex: 'promptTitle', key: 'promptTitle', ellipsis: true },
      { title: '场景', dataIndex: 'scene', key: 'scene', width: 100 }
    ]
  }
  if (props.resourceType === 'tool') {
    return [
      { title: 'ID', dataIndex: 'id', key: 'id', width: 88 },
      { title: 'Tool Key', dataIndex: 'toolKey', key: 'toolKey', width: 160, ellipsis: true },
      { title: '名称', dataIndex: 'toolName', key: 'toolName', ellipsis: true },
      { title: '类型', dataIndex: 'type', key: 'type', width: 100 }
    ]
  }
  return [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 88 },
    { title: 'MCP Key', dataIndex: 'mcpKey', key: 'mcpKey', width: 160, ellipsis: true },
    { title: '服务名', dataIndex: 'serverName', key: 'serverName', ellipsis: true },
    { title: '类型', dataIndex: 'type', key: 'type', width: 100 }
  ]
})

const rowSelection = computed(() => ({
  type: props.multiple ? ('checkbox' as const) : ('radio' as const),
  selectedRowKeys: selectedRowKeys.value,
  preserveSelectedRowKeys: props.multiple,
  onChange: (keys: (string | number)[], rows: any[]) => {
    selectedRowKeys.value = keys
    selectedRows.value = rows
  }
}))

function buildParam() {
  const k = keyword.value?.trim()
  if (props.resourceType === 'model') {
    return { modelName: k || undefined }
  }
  if (props.resourceType === 'prompt') {
    if (!k) return {}
    if (k.length >= 8 && /[0-9a-fA-F-]{8,}/.test(k)) {
      return { promptKey: k }
    }
    return { promptTitle: k }
  }
  if (props.resourceType === 'tool') {
    if (!k) return {}
    return { toolName: k }
  }
  if (props.resourceType === 'mcp') {
    if (!k) return {}
    return { mcpKey: k }
  }
  return {}
}

function parseKeyArray(raw?: string | null): string[] {
  if (!raw?.trim()) return []
  try {
    const j = JSON.parse(raw) as unknown
    if (Array.isArray(j)) return j.map((x) => String(x))
  } catch {
    /* fallthrough */
  }
  return raw
    .split(/[,\n]/)
    .map((s) => s.trim().replace(/^["']|["']$/g, ''))
    .filter(Boolean)
}

function applyPreset() {
  const preset = props.preset
  if (preset == null || preset === '') {
    selectedRowKeys.value = []
    selectedRows.value = []
    return
  }
  if (props.resourceType === 'model') {
    const id = Number(preset)
    if (Number.isFinite(id)) {
      const hit = list.value.find((r) => Number(r.id) === id)
      if (hit) {
        selectedRowKeys.value = [hit.id]
        selectedRows.value = [hit]
        return
      }
    }
    selectedRowKeys.value = []
    selectedRows.value = []
    return
  }
  if (props.resourceType === 'prompt') {
    const pk = String(preset)
    const hit = list.value.find((r) => String(r.promptKey) === pk)
    if (hit) {
      selectedRowKeys.value = [hit.id]
      selectedRows.value = [hit]
      return
    }
    selectedRowKeys.value = []
    selectedRows.value = []
    return
  }
  const keys = parseKeyArray(String(preset))
  if (keys.length === 0) {
    selectedRowKeys.value = []
    selectedRows.value = []
    return
  }
  const keyField = props.resourceType === 'tool' ? 'toolKey' : 'mcpKey'
  const hits = list.value.filter((r) => keys.includes(String(r[keyField])))
  selectedRowKeys.value = hits.map((r) => r.id)
  selectedRows.value = hits
}

async function fetchList() {
  loading.value = true
  try {
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: buildParam()
    }
    let resp: { list?: any[]; total?: number }
    if (props.resourceType === 'model') {
      resp = await aiModelApi.queryPage(payload)
    } else if (props.resourceType === 'prompt') {
      resp = await aiPromptApi.queryPage(payload)
    } else if (props.resourceType === 'tool') {
      resp = await aiToolApi.queryPage(payload)
    } else {
      resp = await aiMcpApi.queryPage(payload)
    }
    list.value = resp.list || []
    page.total = resp.total || 0
    applyPreset()
  } finally {
    loading.value = false
  }
}

function onSearch() {
  page.pageNum = 1
  void fetchList()
}

function onPageChange(p: number) {
  page.pageNum = p
  void fetchList()
}

function onPageSizeChange(_current: number, size: number) {
  page.pageSize = size
  page.pageNum = 1
  void fetchList()
}

function handleCancel() {
  open.value = false
}

function handleConfirm() {
  const rt = props.resourceType
  if (rt === 'model') {
    const row = selectedRows.value[0] || list.value.find((r) => r.id === selectedRowKeys.value[0])
    if (!row?.id) {
      message.warning('请选择一个模型')
      return Promise.reject(new Error('noop'))
    }
    emit('confirm', { kind: 'model', modelId: Number(row.id) })
    open.value = false
    return
  }
  if (rt === 'prompt') {
    const row = selectedRows.value[0] || list.value.find((r) => r.id === selectedRowKeys.value[0])
    if (!row?.promptKey) {
      message.warning('请选择一条提示词')
      return Promise.reject(new Error('noop'))
    }
    emit('confirm', { kind: 'prompt', promptKey: String(row.promptKey) })
    open.value = false
    return
  }
  const keyField = rt === 'tool' ? 'toolKey' : 'mcpKey'
  const rows =
    selectedRows.value.length > 0
      ? selectedRows.value
      : list.value.filter((r) => selectedRowKeys.value.includes(r.id))
  const keys = rows.map((r) => String(r[keyField])).filter(Boolean)
  if (keys.length === 0) {
    message.warning(rt === 'tool' ? '请至少选择一个工具' : '请至少选择一个 MCP')
    return Promise.reject(new Error('noop'))
  }
  emit('confirm', { kind: rt, keys })
  open.value = false
}

watch(
  () => open.value,
  (v) => {
    if (!v) return
    keyword.value = ''
    page.pageNum = 1
    selectedRowKeys.value = []
    selectedRows.value = []
    void fetchList()
  }
)

watch(
  () => props.preset,
  () => {
    if (open.value) applyPreset()
  }
)
</script>

<style scoped>
.picker-toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  align-items: center;
}

.picker-search {
  flex: 1;
  max-width: 360px;
}

.picker-pagination {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
</style>
