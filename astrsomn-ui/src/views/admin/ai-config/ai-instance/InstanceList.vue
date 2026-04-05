<template>
  <AdminPageShell
    title="推理参数配置"
    description="管理 AI 运行预设：定义采样温度、长度限制及生成策略，供智能体直接引用。"
    empty-text="暂无推理预设实例。"
  >
    <div class="astrsomn-config-container">
      <header class="list-toolbar-glass">
        <div class="toolbar-left">
          <AstrsomnSearchPill
            v-model="query.instanceName"
            layout="toolbar"
            placeholder="搜索预设名称或标识..."
            @search="fetchList"
          />
          
   
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

      <AstrsomnOverview
        :list-length="list.length"
        :selected-count="selectedRowKeys.length"
        :all-current-selected="allCurrentSelected"
        :part-current-selected="partCurrentSelected"
        @toggle-select-all="toggleSelectAllCurrentPage"
      />

      <TransitionGroup name="list-stagger" tag="div" class="card-layout-grid">
        <InstanceCard
          v-for="(record, index) in list"
          :key="record.id || index"
          :record="record"
          :index="index"
          :selected="selectedRowKeys.includes(record.id!)"
          @toggle-select="toggleSelect(record.id)"
          @edit="goEdit(record)"
          @delete="handleDeleteOne(record.id)"
        />
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
import { DeleteOutlined, PlusOutlined } from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import AstrsomnOverview from '@/components/home/AstrsomnOverview.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import InstanceForm from './InstanceForm.vue'
import InstanceCard from './InstanceCard.vue'
import { aiInstanceApi, type AiInstance, type PageResponse } from '@/api/aiInstance'

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

fetchList()
</script>

<style scoped>
.astrsomn-config-container {
  padding: 0 20px;
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

/* --- 列表入场动画（--delay 由 InstanceCard 根节点提供）--- */
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