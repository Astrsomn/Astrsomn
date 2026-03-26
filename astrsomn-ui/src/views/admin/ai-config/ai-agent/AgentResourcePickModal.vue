<template>
  <a-modal
    v-model:open="visible"
    :title="title"
    width="880px"
    :body-style="{ paddingTop: '8px' }"
    @ok="handleConfirm"
    @cancel="handleCancel"
  >
    <div class="picker-toolbar">
      <a-input
        v-model:value="keyword"
        :placeholder="keywordPlaceholder"
        allow-clear
        class="picker-search"
        @pressEnter="reload"
      />
      <a-button type="primary" @click="reload">查询</a-button>
    </div>
    <a-table
      size="small"
      :columns="columns"
      :data-source="list"
      :loading="loading"
      :pagination="pagination"
      :row-selection="rowSelection"
      :row-key="rowKeyField"
      :scroll="{ x: 'max-content' }"
      @change="onTableChange"
    />
  </a-modal>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import { aiModelApi, type AiModel } from '@/api/aiModel.ts'
import { aiPromptApi, type AiPrompt } from '@/api/aiPrompt.ts'
import { aiToolApi } from '@/api/aiTool.ts'
import { aiMcpApi } from '@/api/aiMcp.ts'

export type PickKind = 'model' | 'prompt' | 'tool' | 'mcp'

const visible = defineModel<boolean>('open', { required: true })

const props = defineProps<{
  kind: PickKind
  /** 单选：模型为行 id；提示词为 promptKey */
  initialSingle?: number | string | null
  /** 多选：已选 toolKey / mcpKey */
  initialKeys?: string[]
}>()

const emit = defineEmits<{
  confirm: [
    payload:
      | { kind: 'model'; id: number; modelName?: string; modelKey?: string }
      | { kind: 'prompt'; promptKey: string; promptTitle?: string }
      | { kind: 'tool'; keys: string[] }
      | { kind: 'mcp'; keys: string[] }
  ]
}>()

const keyword = ref('')
const loading = ref(false)
const list = ref<any[]>([])
const selectedRowKeys = ref<Array<number | string>>([])

const page = reactive({
  pageNum: 1,
  pageSize: 8,
  total: 0
})

const title = computed(() => {
  const m: Record<PickKind, string> = {
    model: '选择模型',
    prompt: '选择提示词',
    tool: '选择工具',
    mcp: '选择 MCP'
  }
  return m[props.kind]
})

const keywordPlaceholder = computed(() => {
  const m: Record<PickKind, string> = {
    model: '模型名称',
    prompt: '标题 / Key',
    tool: '工具名称 / Key',
    mcp: '服务名 / Key'
  }
  return `搜索${m[props.kind]}`
})

const rowKeyField = computed(() => {
  if (props.kind === 'model') return 'id'
  if (props.kind === 'prompt') return 'promptKey'
  if (props.kind === 'tool') return 'toolKey'
  return 'mcpKey'
})

const columns = computed(() => {
  if (props.kind === 'model') {
    return [
      { title: '模型名称', dataIndex: 'modelName', key: 'modelName', ellipsis: true },
      { title: 'Model Key', dataIndex: 'modelKey', key: 'modelKey', width: 140, ellipsis: true },
      { title: '供应商', dataIndex: 'provider', key: 'provider', width: 100 },
      { title: '状态', dataIndex: 'status', key: 'status', width: 90 }
    ]
  }
  if (props.kind === 'prompt') {
    return [
      { title: '标题', dataIndex: 'promptTitle', key: 'promptTitle', ellipsis: true },
      { title: 'Prompt Key', dataIndex: 'promptKey', key: 'promptKey', width: 200, ellipsis: true },
      { title: '场景', dataIndex: 'scene', key: 'scene', width: 120, ellipsis: true }
    ]
  }
  if (props.kind === 'tool') {
    return [
      { title: '工具名', dataIndex: 'toolName', key: 'toolName', ellipsis: true },
      { title: 'Tool Key', dataIndex: 'toolKey', key: 'toolKey', width: 180, ellipsis: true },
      { title: '类型', dataIndex: 'type', key: 'type', width: 100 }
    ]
  }
  return [
    { title: '服务名', dataIndex: 'serverName', key: 'serverName', ellipsis: true },
    { title: 'MCP Key', dataIndex: 'mcpKey', key: 'mcpKey', width: 180, ellipsis: true },
    { title: '类型', dataIndex: 'type', key: 'type', width: 100 }
  ]
})

const isMulti = computed(() => props.kind === 'tool' || props.kind === 'mcp')

const rowSelection = computed(() => ({
  type: isMulti.value ? ('checkbox' as const) : ('radio' as const),
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys: Array<number | string>) => {
    selectedRowKeys.value = keys
  }
}))

const pagination = computed(() => ({
  current: page.pageNum,
  pageSize: page.pageSize,
  total: page.total,
  showSizeChanger: true,
  pageSizeOptions: ['8', '10', '20', '50'],
  showTotal: (t: number) => `共 ${t} 条`
}))

function onTableChange(pag: { current?: number; pageSize?: number }) {
  if (pag.current != null) page.pageNum = pag.current
  if (pag.pageSize != null) {
    page.pageSize = pag.pageSize
    page.pageNum = 1
  }
  void fetchList()
}

async function fetchList() {
  loading.value = true
  try {
    const base = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {} as Record<string, unknown>
    }
    const kw = keyword.value.trim()
    if (props.kind === 'model') {
      base.param = { modelName: kw || undefined }
      const resp = await aiModelApi.queryPage(base)
      list.value = resp.list || []
      page.total = resp.total || 0
    } else if (props.kind === 'prompt') {
      base.param = { promptTitle: kw || undefined }
      const resp = await aiPromptApi.queryPage(base)
      list.value = resp.list || []
      page.total = resp.total || 0
    } else if (props.kind === 'tool') {
      base.param = { toolName: kw || undefined }
      const resp = await aiToolApi.queryPage(base)
      list.value = resp.list || []
      page.total = resp.total || 0
    } else {
      base.param = { mcpKey: kw || undefined }
      const resp = await aiMcpApi.queryPage(base)
      list.value = resp.list || []
      page.total = resp.total || 0
    }
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '加载失败')
    list.value = []
    page.total = 0
  } finally {
    loading.value = false
  }
}

function reload() {
  page.pageNum = 1
  void fetchList()
}

function syncSelectionFromInitial() {
  if (props.kind === 'model') {
    const id = props.initialSingle
    selectedRowKeys.value = id != null && id !== '' ? [id as number | string] : []
  } else if (props.kind === 'prompt') {
    const pk = props.initialSingle
    selectedRowKeys.value = pk != null && pk !== '' ? [String(pk)] : []
  } else {
    selectedRowKeys.value = [...(props.initialKeys || [])]
  }
}

watch(
  () => visible.value,
  (v) => {
    if (!v) return
    keyword.value = ''
    page.pageNum = 1
    syncSelectionFromInitial()
    void fetchList()
  }
)

function handleConfirm() {
  if (props.kind === 'model') {
    const id = selectedRowKeys.value[0]
    if (id == null) {
      message.warning('请选择一个模型')
      return
    }
    const row = list.value.find((r) => String(r.id) === String(id)) as AiModel | undefined
    emit('confirm', {
      kind: 'model',
      id: Number(id),
      modelName: row?.modelName,
      modelKey: row?.modelKey != null ? String(row.modelKey) : undefined
    })
    visible.value = false
    return
  }
  if (props.kind === 'prompt') {
    const pk = selectedRowKeys.value[0]
    if (pk == null || pk === '') {
      message.warning('请选择一条提示词')
      return
    }
    const row = list.value.find((r) => String(r.promptKey) === String(pk)) as AiPrompt | undefined
    emit('confirm', {
      kind: 'prompt',
      promptKey: String(pk),
      promptTitle: row?.promptTitle
    })
    visible.value = false
    return
  }
  if (props.kind === 'tool') {
    const keys = selectedRowKeys.value.map((k) => String(k)).filter(Boolean)
    emit('confirm', { kind: 'tool', keys })
    visible.value = false
    return
  }
  const keys = selectedRowKeys.value.map((k) => String(k)).filter(Boolean)
  emit('confirm', { kind: 'mcp', keys })
  visible.value = false
}

function handleCancel() {
  visible.value = false
}
</script>

<style scoped>
.picker-toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
  align-items: center;
}

.picker-search {
  max-width: 320px;
}
</style>
