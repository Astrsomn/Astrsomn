<template>
  <AdminPageShell
    title="提示词管理"
    description="维护 AI_PROMPT 系统提示词与版本，对接 AiPromptController（/v1/astro/ai-promopt）。"
    empty-text="暂无提示词，请先创建。"
  >
    <div class="prompt-page">
      <div class="toolbar">
        <div class="toolbar-left">
          <a-input
            v-model:value="query.promptTitle"
            placeholder="标题（模糊）"
            class="toolbar-input"
            allow-clear
          />
          <a-input
            v-model:value="query.promptKey"
            placeholder="Prompt Key（精确）"
            class="toolbar-input"
            allow-clear
          />
          <a-input
            v-model:value="query.scene"
            placeholder="场景"
            class="toolbar-input narrow"
            allow-clear
          />
          <a-select
            v-model:value="query.enabledFlag"
            :options="enabledFilterOptions"
            placeholder="启用状态"
            class="toolbar-select"
            allow-clear
          />
        </div>
        <div class="toolbar-right">
          <a-button type="primary" @click="fetchList">查询</a-button>
          <a-button @click="openCreate">新增</a-button>

          <a-popconfirm
            v-if="selectedRowKeys.length > 0"
            title="确定批量删除选中的提示词吗？"
            ok-text="确认"
            cancel-text="取消"
            @confirm="handleBatchDelete"
          >
            <a-button danger>批量删除</a-button>
          </a-popconfirm>
        </div>
      </div>

      <a-table
        :columns="columns"
        :data-source="list"
        :pagination="false"
        row-key="id"
        :row-selection="rowSelection"
        :scroll="{ x: 1080 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'enabledFlag'">
            <span>{{ renderEnabled(String(record.enabledFlag || '')) }}</span>
          </template>
          <template v-else-if="column.key === 'promptContent'">
            <span class="content-preview">{{ previewContent(record.promptContent) }}</span>
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-button type="link" @click="openEdit(record)">编辑</a-button>
            <a-divider type="vertical" />
            <a-popconfirm
              title="确定删除吗？"
              ok-text="确认"
              cancel-text="取消"
              @confirm="() => handleDeleteOne(record.id)"
            >
              <a-button type="link" danger>删除</a-button>
            </a-popconfirm>
          </template>
        </template>
      </a-table>

      <div class="pagination-wrap">
        <a-pagination
          :current="page.pageNum"
          :page-size="page.pageSize"
          :total="page.total"
          :show-size-changer="false"
          @change="onPageChange"
        />
      </div>

      <PromptFormModal
        v-model:open="modal.open"
        :mode="modal.mode"
        :confirm-loading="modal.submitting"
        :initial="modalInitial"
        @submit="handleFormSubmit"
      />
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import AdminPageShell from '@/views/admin/components/AdminPageShell.vue'
import PromptFormModal from './PromptFormModal.vue'
import { aiPromptApi, type AiPrompt, type PageResponse } from '@/api/aiPrompt.ts'

type QueryState = {
  promptTitle?: string
  promptKey?: string
  scene?: string
  enabledFlag?: string
}

const enabledFilterOptions = [
  { label: '启用', value: 'enabled' },
  { label: '停用', value: 'disabled' }
]

const renderEnabled = (f: string) => {
  return enabledFilterOptions.find((x) => x.value === f)?.label ?? f
}

const previewContent = (raw: string | undefined) => {
  if (!raw) return '—'
  const one = raw.replace(/\s+/g, ' ').trim()
  return one.length > 80 ? `${one.slice(0, 80)}…` : one
}

const columns = [
  { title: 'Prompt Key', dataIndex: 'promptKey', key: 'promptKey', width: 200, ellipsis: true },
  { title: '标题', dataIndex: 'promptTitle', key: 'promptTitle', width: 180, ellipsis: true },
  { title: '场景', dataIndex: 'scene', key: 'scene', width: 120, ellipsis: true },
  { title: '版本', dataIndex: 'version', key: 'version', width: 72 },
  { title: '内容预览', key: 'promptContent', width: 260, ellipsis: true },
  { title: '状态', key: 'enabledFlag', width: 90 },
  { title: '操作', key: 'actions', width: 160, fixed: 'right' as const }
]

const query = reactive<QueryState>({})
const list = ref<AiPrompt[]>([])

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const selectedRowKeys = ref<Array<number | string>>([])

const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys: Array<number | string>) => {
    selectedRowKeys.value = keys
  }
}))

const modal = reactive({
  open: false,
  mode: 'create' as 'create' | 'edit',
  submitting: false
})

const modalInitial = ref<AiPrompt | null>(null)

const fetchList = async () => {
  const payload = {
    pageNo: page.pageNum,
    pageSize: page.pageSize,
    param: {
      promptTitle: query.promptTitle || undefined,
      promptKey: query.promptKey || undefined,
      scene: query.scene || undefined,
      enabledFlag: query.enabledFlag || undefined
    }
  }

  const resp: PageResponse<AiPrompt> = await aiPromptApi.queryPage(payload)
  list.value = resp.list || []
  page.total = resp.total || 0
}

const onPageChange = (p: number) => {
  page.pageNum = p
  void fetchList()
}

const openCreate = () => {
  modal.mode = 'create'
  modalInitial.value = null
  modal.open = true
}

const openEdit = async (record: AiPrompt) => {
  modal.mode = 'edit'
  const id = record.id
  if (id == null) return

  const detail = await aiPromptApi.detail(id)
  modalInitial.value = detail
  modal.open = true
}

const handleDeleteOne = async (id: number | string) => {
  if (id == null) return
  const msg = await aiPromptApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return
  const msg = await aiPromptApi.delete(ids)
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleFormSubmit = async (form: AiPrompt) => {
  modal.submitting = true
  try {
    const payload: AiPrompt = { ...form }
    const v = payload.version
    if (v !== undefined && v !== null && v !== '') {
      const n = Number(v)
      payload.version = Number.isFinite(n) ? n : undefined
    }

    let msg: string
    if (modal.mode === 'create') {
      delete (payload as { id?: unknown }).id
      msg = await aiPromptApi.create(payload)
    } else {
      msg = await aiPromptApi.update(payload)
    }

    message.success(msg)
    modal.open = false
    void fetchList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '保存失败')
  } finally {
    modal.submitting = false
  }
}

void fetchList()
</script>

<style scoped>
.prompt-page {
  padding: 0 4px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.toolbar-left {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.toolbar-right {
  display: flex;
  gap: 12px;
  align-items: center;
}

.toolbar-input {
  width: 200px;
}

.toolbar-input.narrow {
  width: 140px;
}

.toolbar-select {
  width: 160px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.content-preview {
  color: rgba(0, 0, 0, 0.45);
  font-size: 12px;
}
</style>
