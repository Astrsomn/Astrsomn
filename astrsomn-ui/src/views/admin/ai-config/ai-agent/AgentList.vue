<template>
  <AdminPageShell
    title="智能体管理"
    description="管理 Agent 配置、执行策略与发布状态。"
  >
    <div class="agent-page-wrapper">
      <div class="glass-toolbar">
        <div class="search-group">
          <div class="input-capsule">
            <search-outlined class="prefix-icon" />
            <input 
              v-model="query.agentName" 
              placeholder="搜索名称..." 
              @keyup.enter="fetchList"
            />
          </div>
          
          <a-select
            v-model:value="query.status"
            placeholder="所有状态"
            class="minimal-select"
            allow-clear
            @change="fetchList"
          >
            <a-select-option value="enabled">启用</a-select-option>
            <a-select-option value="disabled">停用</a-select-option>
          </a-select>

          <button class="icon-btn search-trigger" @click="fetchList" title="执行搜索">
            <search-outlined />
          </button>
        </div>

        <div class="action-group">
          <button class="primary-circle-btn" @click="openCreate" title="新增智能体">
            <plus-outlined />
          </button>
        </div>
      </div>

      <a-list
        :grid="{ gutter: 20, xs: 1, sm: 1, md: 2, lg: 2, xl: 2, xxl: 3 }"
        :data-source="list"
        :loading="loading"
        class="agent-grid"
      >
        <template #renderItem="{ item }">
          <a-list-item style="padding: 0; margin-bottom: 20px;">
            <AgentCard :record="item" @edit="openEdit" @delete="handleDeleteOne" />
          </a-list-item>
        </template>
      </a-list>

      <div class="pagination-footer">
        <a-pagination
          v-model:current="page.pageNum"
          :total="page.total"
          :page-size="page.pageSize"
          @change="onPageChange"
          simple
        />
      </div>

      <AgentFormModal
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
import { reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined, SearchOutlined } from '@ant-design/icons-vue'
import AdminPageShell from '@/views/admin/components/admin/AdminPageShell.vue'
import AgentFormModal from './AgentFormModal.vue'
import AgentCard from './AgentCard.vue'
import { aiAgentApi, type AiAgent, type PageResponse } from '@/api/aiAgent.ts'

// 状态管理
const loading = ref(false)
const query = reactive({ agentName: '', status: undefined })
const list = ref<AiAgent[]>([])
const page = reactive({ pageNum: 1, pageSize: 12, total: 0 })
const modal = reactive({ open: false, mode: 'create' as 'create' | 'edit', submitting: false })
const modalInitial = ref<AiAgent | null>(null)

// 数据交互逻辑
const fetchList = async () => {
  loading.value = true
  try {
    const resp: PageResponse<AiAgent> = await aiAgentApi.queryPage({
      pageNo: page.pageNum, pageSize: page.pageSize,
      param: { name: query.agentName || undefined, status: query.status || undefined }
    })
    list.value = resp.list || []
    page.total = resp.total || 0
  } finally { loading.value = false }
}

const onPageChange = (p: number) => { page.pageNum = p; fetchList() }
const openCreate = () => { modal.mode = 'create'; modalInitial.value = null; modal.open = true }
const openEdit = async (record: AiAgent) => {
  modal.mode = 'edit'
  const detail = await aiAgentApi.detail(record.id!)
  modalInitial.value = detail
  modal.open = true
}
const handleDeleteOne = async (id: number | string) => {
  await aiAgentApi.delete([id]); message.success('已删除'); fetchList()
}
const handleFormSubmit = async (form: AiAgent) => {
  modal.submitting = true
  try {
    if (modal.mode === 'create') await aiAgentApi.create(form); else await aiAgentApi.update(form)
    message.success('保存成功'); modal.open = false; fetchList()
  } catch (e: any) { message.error(e.message) } finally { modal.submitting = false }
}

fetchList()
</script>

<style scoped>
.agent-page-wrapper {
  padding: 8px 0;
}

/* --- 工具栏布局 --- */
.glass-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.search-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* --- 输入组件统一对齐 (高度 40px) --- */
.input-capsule {
  display: flex;
  align-items: center;
  background: var(--bg-input);
  border-radius: 10px;
  padding: 0 14px;
  height: 40px;
  border: 1px solid transparent;
  transition: all 0.2s;
}

.input-capsule:focus-within {
  background: var(--bg-surface);
  border-color: color-mix(in srgb, var(--primary) 35%, var(--border-default));
  box-shadow: 0 2px 8px color-mix(in srgb, var(--primary) 12%, transparent);
}

.input-capsule input {
  border: none;
  background: transparent;
  outline: none;
  margin-left: 8px;
  font-size: 14px;
  width: 180px;
  color: var(--text-primary);
}

.prefix-icon { color: var(--text-muted); }

/* 调整 Ant Design Select 样式以匹配胶囊 */
.minimal-select { 
  width: 130px; 
}
.minimal-select :deep(.ant-select-selector) {
  height: 40px !important;
  border-radius: 10px !important;
  border: none !important;
  background: var(--bg-input) !important;
  display: flex !important;
  align-items: center !important;
}

/* --- 按钮系列 --- */
.icon-btn {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.search-trigger {
  background: var(--primary-gradient);
  color: #fff;
  font-size: 16px;
  box-shadow: 0 4px 14px color-mix(in srgb, var(--primary) 35%, transparent);
}

.search-trigger:hover {
  filter: brightness(1.08);
  box-shadow: 0 6px 18px color-mix(in srgb, var(--primary) 45%, transparent);
}

.primary-circle-btn {
  width: 46px;
  height: 46px;
  border-radius: 23px;
  border: none;
  background: var(--primary-gradient);
  color: #fff;
  cursor: pointer;
  font-size: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px color-mix(in srgb, var(--primary) 40%, transparent);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.primary-circle-btn:hover {
  transform: rotate(90deg) scale(1.05);
  filter: brightness(1.1);
  box-shadow: 0 8px 22px color-mix(in srgb, var(--primary) 50%, transparent);
}

/* --- 分页 --- */
.pagination-footer {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}

/* 响应式：窄屏下工具栏堆叠 */
@media (max-width: 640px) {
  .glass-toolbar { flex-direction: column; gap: 16px; align-items: flex-start; }
  .search-group { width: 100%; flex-wrap: wrap; }
  .input-capsule { flex: 1; min-width: 150px; }
}
</style>