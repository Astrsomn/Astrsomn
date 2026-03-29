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
      >
        <template #prefix><search-outlined style="color: #bfbfbf" /></template>
      </a-input>
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
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-badge :status="record.status === 'enabled' ? 'success' : 'error'" :text="record.status === 'enabled' ? '已上线' : '下线'" />
        </template>
        <template v-else-if="column.key === 'modelKey' || column.key === 'instanceKey' || column.key === 'promptKey'">
          <code class="code-text">{{ record[column.key] }}</code>
        </template>
      </template>
    </a-table>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import { aiModelApi } from '@/api/aiModel.ts'
import { aiInstanceApi } from '@/api/aiInstance.ts'
import { aiPromptApi } from '@/api/aiPrompt.ts'
import { aiToolApi } from '@/api/aiTool.ts'
import { aiMcpApi } from '@/api/aiMcp.ts'

export type PickKind = 'model' | 'instance' | 'prompt' | 'tool' | 'mcp'
export type InstanceModelType = 'chat' | 'embedding' | 'image'

const visible = defineModel<boolean>('open', { required: true })

const props = defineProps<{
  kind: PickKind
  /** kind=instance 时必填：chat / embedding / image */
  instanceModelType?: InstanceModelType
  /** 初始选中的 Key (单选) */
  initialSingle?: number | string | null
  /** 初始选中的 Keys (多选) */
  initialKeys?: string[]
}>()

const emit = defineEmits<{
  confirm: [payload: any]
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

// 1. 语义化标题修改
const title = computed(() => {
  if (props.kind === 'instance') {
    const sub: Record<InstanceModelType, string> = {
      chat: '选择对话推理预设 (Profiles)',
      embedding: '选择向量推理预设 (Profiles)',
      image: '选择图像推理预设 (Profiles)'
    }
    return sub[props.instanceModelType || 'chat'] || '选择推理预设'
  }
  const m: Record<Exclude<PickKind, 'instance'>, string> = {
    model: '选择接入端点 (Endpoints)',
    prompt: '选择系统提示词 (Prompts)',
    tool: '选择外部工具 (Tools)',
    mcp: '选择 MCP 服务'
  }
  return m[props.kind as Exclude<PickKind, 'instance'>]
})

const keywordPlaceholder = computed(() => {
  const m: Record<PickKind, string> = {
    model: '端点名称 / Model Key',
    instance: '预设名称 / Instance Key',
    prompt: '模板标题 / Prompt Key',
    tool: '工具名 / Tool Key',
    mcp: '服务名 / MCP Key'
  }
  return `搜索${m[props.kind]}`
})

// 2. 统一使用 Key 作为 RowKey，不再使用自增 ID
const rowKeyField = computed(() => {
  const keyMap: Record<PickKind, string> = {
    model: 'modelKey',
    instance: 'instanceKey',
    prompt: 'promptKey',
    tool: 'toolKey',
    mcp: 'mcpKey'
  }
  return keyMap[props.kind]
})

const columns = computed(() => {
  if (props.kind === 'model') {
    return [
      { title: '端点名称', dataIndex: 'modelName', key: 'modelName', ellipsis: true },
      { title: 'Model Key', dataIndex: 'modelKey', key: 'modelKey', width: 180 },
      { title: '供应商', dataIndex: 'provider', key: 'provider', width: 120 },
      { title: '状态', dataIndex: 'status', key: 'status', width: 100 }
    ]
  }
  if (props.kind === 'instance') {
    return [
      { title: '预设名称', dataIndex: 'instanceName', key: 'instanceName', ellipsis: true },
      { title: 'Instance Key', dataIndex: 'instanceKey', key: 'instanceKey', width: 180 },
      { title: '绑定端点', dataIndex: 'modelKey', key: 'modelKey', width: 160 },
      { title: '状态', dataIndex: 'status', key: 'status', width: 100 }
    ]
  }
  // Prompt, Tool, MCP 列定义保持原样，仅微调 Title
  if (props.kind === 'prompt') {
    return [
      { title: '提示词标题', dataIndex: 'promptTitle', key: 'promptTitle', ellipsis: true },
      { title: 'Prompt Key', dataIndex: 'promptKey', key: 'promptKey', width: 200 },
      { title: '使用场景', dataIndex: 'scene', key: 'scene', width: 120 }
    ]
  }
  return [
    { title: '名称', dataIndex: props.kind === 'tool' ? 'toolName' : 'serverName', key: 'name', ellipsis: true },
    { title: '唯一标识 (Key)', dataIndex: props.kind === 'tool' ? 'toolKey' : 'mcpKey', key: 'key', width: 220 },
    { title: '类型', dataIndex: 'type', key: 'type', width: 120 }
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
  showTotal: (t: number) => `共 ${t} 条`
}))

async function fetchList() {
  loading.value = true
  try {
    const base = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {} as Record<string, any>
    }
    const kw = keyword.value.trim()

    if (props.kind === 'model') {
      base.param = { modelName: kw || undefined }
      const resp = await aiModelApi.queryPage(base)
      list.value = resp.list || []
      page.total = resp.total || 0
    } else if (props.kind === 'instance') {
      base.param = {
        instanceName: kw || undefined,
        modelType: props.instanceModelType,
        status: 'enabled' // 智能体只能挂载已启用的预设
      }
      const resp = await aiInstanceApi.queryPage(base)
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
  } catch (e: any) {
    message.error(e.message || '数据加载失败')
  } finally {
    loading.value = false
  }
}

function onTableChange(pag: any) {
  page.pageNum = pag.current
  page.pageSize = pag.pageSize
  void fetchList()
}

function reload() {
  page.pageNum = 1
  void fetchList()
}

// 3. 初始回显逻辑优化：基于 Key 进行匹配
function syncSelectionFromInitial() {
  if (isMulti.value) {
    selectedRowKeys.value = props.initialKeys ? [...props.initialKeys] : []
  } else {
    selectedRowKeys.value = props.initialSingle ? [String(props.initialSingle)] : []
  }
}

watch(() => visible.value, (v) => {
  if (v) {
    keyword.value = ''
    page.pageNum = 1
    syncSelectionFromInitial()
    void fetchList()
  }
})

// 4. 确认逻辑：返回完整的业务对象
function handleConfirm() {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请至少选择一项')
    return
  }

  const firstKey = selectedRowKeys.value[0]
  const row = list.value.find(r => r[rowKeyField.value] === firstKey)

  // 基础校验：必须包含业务 Key
  if (!isMulti.value && (!row || !row[rowKeyField.value])) {
    message.error('所选数据无效，缺失业务 Key')
    return
  }

  if (props.kind === 'model') {
    emit('confirm', {
      kind: 'model',
      modelName: row.modelName,
      modelKey: row.modelKey
    })
  } else if (props.kind === 'instance') {
    emit('confirm', {
      kind: 'instance',
      instanceModelType: props.instanceModelType!,
      instanceKey: row.instanceKey,
      instanceName: row.instanceName
    })
  } else if (props.kind === 'prompt') {
    emit('confirm', {
      kind: 'prompt',
      promptKey: row.promptKey,
      promptTitle: row.promptTitle
    })
  } else {
    // Tool / MCP 返回 Key 数组
    emit('confirm', {
      kind: props.kind,
      keys: selectedRowKeys.value.map(String)
    })
  }

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
}
.picker-search {
  flex: 1;
  max-width: 400px;
}
.code-text {
  font-family: monospace;
  background: #f5f5f5;
  padding: 2px 4px;
  border-radius: 4px;
  color: #c41d7f;
}
</style>