<template>
  <AdminPageShell
    title="推理参数配置"
    description="管理 AI 运行预设：定义采样温度、长度限制及生成策略，供智能体直接引用。"
    empty-text="暂无推理预设实例。"
  >
    <div class="astrsomn-config-container">
      <header class="list-toolbar-glass">
        <div class="toolbar-left">
          <div class="search-input-wrapper">
            <search-outlined class="search-icon" />
            <input 
              v-model="query.instanceName" 
              placeholder="搜索预设名称或标识..." 
              @keyup.enter="fetchList"
            />
            <button class="search-btn" @click="fetchList">搜索</button>
          </div>
          
   
        </div>

        <div class="toolbar-right">
          <Transition name="fade">
            <a-popconfirm
              v-if="selectedRowKeys.length > 0"
              title="确定批量移除选中的配置吗？"
              @confirm="handleBatchDelete"
            >
              <a-button danger class="toc-batch-btn">
                <delete-outlined /> 批量删除 ({{ selectedRowKeys.length }})
              </a-button>
            </a-popconfirm>
          </Transition>
          <a-button type="primary" class="toc-add-btn" @click="goCreate">
            <plus-outlined /> 新增推理配置
          </a-button>
        </div>
      </header>

      <BaseOverview
        :list-length="list.length"
        :selected-count="selectedRowKeys.length"
        :all-current-selected="allCurrentSelected"
        :part-current-selected="partCurrentSelected"
        @toggle-select-all="toggleSelectAllCurrentPage"
      />

      <TransitionGroup name="list-stagger" tag="div" class="card-layout-grid">
        <div
          v-for="(record, index) in list"
          :key="record.id || index"
          :class="['toc-card', { 'is-selected': selectedRowKeys.includes(record.id!) }]"
          :style="{ '--delay': index }"
        >
          <div class="toc-checkbox-wrapper" @click="toggleSelect(record.id)">
            <div :class="['custom-check', { active: selectedRowKeys.includes(record.id!) }]">
              <check-outlined v-if="selectedRowKeys.includes(record.id!)" />
            </div>
          </div>

          <div class="toc-card-body" @click="goEdit(record)">
            <div class="toc-card-header">
              <div :class="['type-icon-box', getModelTypeClass(record)]">
                <component :is="getModelIcon(record)" />
              </div>
              <div class="title-area">
                <div class="top-row">
                  <h4 class="name">{{ record.instanceName || '未命名配置' }}</h4>
                  <div :class="['status-glow', record.status]">
                    <span class="dot"></span> {{ record.status === 'enabled' ? '运行中' : '已停用' }}
                  </div>
                </div>
                <p class="sub-key">{{ record.instanceKey || 'No ID' }}</p>
              </div>
            </div>

            <div class="dynamic-params-preview">
              <template v-if="record.modelType === 'chat' || !record.modelType">
                <div class="mini-progress-item">
                  <div class="p-labels"><span>温度 (Temp)</span> <b>{{ record.temperature ?? 0.7 }}</b></div>
                  <div class="p-track"><div class="p-thumb temp" :style="{ width: `${(record.temperature || 0) / 2 * 100}%` }"></div></div>
                </div>
                <div class="mini-progress-item">
                  <div class="p-labels"><span>核采样 (TopP)</span> <b>{{ record.topP ?? 1.0 }}</b></div>
                  <div class="p-track"><div class="p-thumb topp" :style="{ width: `${(record.topP || 0) * 100}%` }"></div></div>
                </div>
              </template>

              <template v-else-if="record.modelType === 'embedding'">
                <div class="vector-spec">
                  <div class="spec-tag">维度: {{ record.dimensions || 1536 }}</div>
                  <div class="spec-tag">环境: {{ record.envCode || 'Default' }}</div>
                </div>
              </template>
            </div>

            <div class="toc-card-meta">
              <div class="meta-left">
                <span class="provider-label">{{ record.modelKey || '未关联端点' }}</span>
              </div>
              <div class="meta-right">
                <span class="time-ago">{{ formatUpdateTime(record.updateTime) }}</span>
              </div>
            </div>
          </div>

          <footer class="toc-card-footer">
            <a-button type="text" class="f-btn" @click.stop="goEdit(record)"><edit-outlined /> 编辑</a-button>
            <a-divider type="vertical" />
            <a-popconfirm title="确定删除此配置？" @confirm="handleDeleteOne(record.id)">
              <a-button type="text" danger class="f-btn"><delete-outlined /> 删除</a-button>
            </a-popconfirm>
          </footer>
        </div>
      </TransitionGroup>

      <div v-if="list.length === 0" class="toc-empty">
        <a-empty description="暂时没有发现任何配置项" :image="Empty.PRESENTED_IMAGE_SIMPLE">
          <a-button type="primary" class="toc-add-btn" size="large" @click="goCreate">
            现在去创建一个
          </a-button>
        </a-empty>
      </div>

      <footer class="toc-pagination-area">
        <span class="count-tip">Total <b>{{ page.total }}</b> Results</span>
        <a-pagination
          v-model:current="page.pageNum"
          :page-size="page.pageSize"
          :total="page.total"
          show-less-items
          @change="onPageChange"
        />
      </footer>

      <InstanceForm
        v-model:visible="formVisible"
        :record="currentRecord"
        @success="handleFormSuccess"
      />
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { message, Empty } from 'ant-design-vue'
import {
  DeleteOutlined, PlusOutlined, SearchOutlined, EditOutlined,
  MessageOutlined, ThunderboltFilled, CheckOutlined, PartitionOutlined
} from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import BaseOverview from '@/components/home/BaseOverview.vue'
import InstanceForm from './InstanceForm.vue'
import { aiInstanceApi, type AiInstance, type PageResponse } from '@/api/aiInstance'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'

dayjs.extend(relativeTime)

const formVisible = ref(false)
const currentRecord = ref<AiInstance | undefined>(undefined)
const query = reactive({ instanceName: undefined, status: undefined })
const list = ref<AiInstance[]>([])
const page = reactive({ pageNum: 1, pageSize: 12, total: 0 })
const selectedRowKeys = ref<Array<number | string>>([])

const currentPageIds = computed(() => list.value.map(i => i.id).filter((id): id is number | string => id != null))
const allCurrentSelected = computed(() => currentPageIds.value.length > 0 && currentPageIds.value.every(id => selectedRowKeys.value.includes(id)))
const partCurrentSelected = computed(() => {
  const count = currentPageIds.value.filter(id => selectedRowKeys.value.includes(id)).length
  return count > 0 && count < currentPageIds.value.length
})

const toggleSelectAllCurrentPage = (checked: boolean) => {
  if (checked) selectedRowKeys.value = Array.from(new Set([...selectedRowKeys.value, ...currentPageIds.value]))
  else selectedRowKeys.value = selectedRowKeys.value.filter(id => !currentPageIds.value.includes(id))
}

const toggleSelect = (id: any) => {
  const idx = selectedRowKeys.value.indexOf(id)
  if (idx > -1) selectedRowKeys.value.splice(idx, 1)
  else selectedRowKeys.value.push(id)
}

const fetchList = async () => {
  const resp: PageResponse<AiInstance> = await aiInstanceApi.queryPage({
    pageNo: page.pageNum, pageSize: page.pageSize,
    param: { instanceName: query.instanceName || undefined, status: query.status || undefined }
  })
  list.value = resp.list || []
  page.total = resp.total || 0
}

const resetFilters = () => { query.instanceName = undefined; query.status = undefined; fetchList(); }
const onPageChange = (p: number) => { page.pageNum = p; fetchList(); }
const goCreate = () => { currentRecord.value = undefined; formVisible.value = true; }
const goEdit = (record: AiInstance) => { currentRecord.value = record; formVisible.value = true; }
const handleFormSuccess = () => fetchList()
const handleDeleteOne = async (id: any) => {
  const msg = await aiInstanceApi.delete([id])
  message.success(msg); fetchList()
}
const handleBatchDelete = async () => {
  await aiInstanceApi.delete(selectedRowKeys.value)
  message.success('批量删除成功'); selectedRowKeys.value = []; fetchList()
}

const formatUpdateTime = (time?: string) => time ? dayjs(time).fromNow() : '—'
const getModelIcon = (r: AiInstance) => r.modelType === 'embedding' ? PartitionOutlined : MessageOutlined
const getModelTypeClass = (r: AiInstance) => r.modelType || 'chat'

fetchList()
</script>

<style scoped>
.astrsomn-config-container {
  padding: 10px;
  background: transparent;
}

/* --- ToC 风格大搜索工具栏 --- */
.list-toolbar-glass {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  gap: 20px;
}

.search-input-wrapper {
  flex: 1;
  max-width: 460px;
  height: 52px;
  background: #fff;
  border-radius: 26px;
  padding: 0 8px 0 20px;
  display: flex;
  align-items: center;
  box-shadow:
    0 1px 2px rgba(15, 23, 42, 0.06),
    0 4px 12px rgba(15, 23, 42, 0.08),
    0 12px 28px rgba(15, 23, 42, 0.06);
  border: 1px solid #e2e8f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-input-wrapper:focus-within {
  border-color: #3b82f6;
  box-shadow:
    0 0 0 3px rgba(59, 130, 246, 0.22),
    0 4px 14px rgba(37, 99, 235, 0.2),
    0 14px 32px rgba(15, 23, 42, 0.12);
}

.search-icon {
  color: #3b82f6;
  font-size: 18px;
}

.search-input-wrapper input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 15px;
  margin-left: 10px;
  background: transparent;
}

.search-btn {
  background: linear-gradient(180deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 20px;
  font-weight: 600;
  cursor: pointer;
  box-shadow:
    0 1px 0 rgba(255, 255, 255, 0.2) inset,
    0 2px 4px rgba(29, 78, 216, 0.35),
    0 6px 14px rgba(37, 99, 235, 0.28);
}

.toc-select {
  width: 140px;
  :deep(.ant-select-selector) {
    border-radius: 12px !important;
    height: 40px !important;
    padding-top: 4px !important;
  }
}

.toc-add-btn {
  height: 48px;
  padding: 0 28px;
  border-radius: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border: none;
  box-shadow:
    0 1px 0 rgba(255, 255, 255, 0.18) inset,
    0 3px 6px rgba(29, 78, 216, 0.4),
    0 10px 24px rgba(37, 99, 235, 0.32),
    0 20px 40px rgba(15, 23, 42, 0.1);
}

/* --- 卡片网格布局 --- */
.card-layout-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 24px;
  margin-top: 20px;
}

.toc-card {
  background: #ffffff;
  border-radius: 24px;
  border: 1px solid #e2e8f0;
  position: relative;
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow:
    0 1px 2px rgba(15, 23, 42, 0.05),
    0 4px 10px rgba(15, 23, 42, 0.07),
    0 10px 28px rgba(15, 23, 42, 0.08);
}

.toc-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow:
    0 2px 4px rgba(15, 23, 42, 0.06),
    0 8px 18px rgba(15, 23, 42, 0.1),
    0 18px 44px rgba(15, 23, 42, 0.14),
    0 0 0 1px rgba(59, 130, 246, 0.12);
  border-color: #3b82f6;
}

.toc-card.is-selected {
  background: #eff6ff;
  border-color: #60a5fa;
  box-shadow:
    0 1px 2px rgba(37, 99, 235, 0.08),
    0 6px 16px rgba(37, 99, 235, 0.14),
    0 14px 36px rgba(37, 99, 235, 0.12);
}

/* 自定义 Checkbox */
.toc-checkbox-wrapper {
  position: absolute;
  top: 16px;
  right: 16px;
  z-index: 5;
  cursor: pointer;
}

.custom-check {
  width: 24px;
  height: 24px;
  border-radius: 8px;
  border: 2px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  background: white;
}

.custom-check.active {
  background: #2563eb;
  border-color: #1d4ed8;
  color: white;
  box-shadow: 0 2px 6px rgba(37, 99, 235, 0.45);
}

.toc-card-body {
  padding: 24px;
  cursor: pointer;
  flex: 1;
}

/* 卡片头部 */
.toc-card-header {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.type-icon-box {
  width: 52px;
  height: 52px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  background: #64748b;
}

.type-icon-box.chat { background: linear-gradient(135deg, #2563eb, #3b82f6); }
.type-icon-box.embedding { background: linear-gradient(135deg, #10b981, #34d399); }

.title-area .name {
  font-size: 17px;
  font-weight: 800;
  color: #1e293b;
  margin: 0 0 4px 0;
}

.status-glow {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  font-weight: 700;
  padding: 2px 10px;
  border-radius: 20px;
  background: #f1f5f9;
}

.status-glow.enabled { background: #dcfce7; color: #16a34a; }
.status-glow .dot { width: 6px; height: 6px; border-radius: 50%; background: currentColor; }

/* 动态预览条 */
.dynamic-params-preview {
  background: #f8fafc;
  border-radius: 16px;
  padding: 16px;
  margin-bottom: 20px;
}

.mini-progress-item {
  margin-bottom: 12px;
}
.mini-progress-item:last-child { margin-bottom: 0; }

.p-labels {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  margin-bottom: 6px;
  color: #64748b;
}

.p-track {
  height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  overflow: hidden;
}

.p-thumb {
  height: 100%;
  border-radius: 3px;
}

.p-thumb.temp { background: #f59e0b; }
.p-thumb.topp { background: #2563eb; }

/* 底部操作 */
.toc-card-footer {
  padding: 12px 20px;
  background: #fafafa;
  display: flex;
  justify-content: space-around;
  align-items: center;
  border-top: 1px solid #f1f5f9;
}

.f-btn { font-weight: 600; color: #64748b; }

/* --- 列表入场动画 --- */
.list-stagger-enter-active {
  transition: all 0.5s ease;
  transition-delay: calc(var(--delay) * 0.05s);
}

.list-stagger-enter-from {
  opacity: 0;
  transform: translateY(30px);
}

.toc-pagination-area {
  margin-top: 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;
}
</style>