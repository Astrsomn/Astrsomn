<template>
  <AstPageShell
      :show-view-toggle="activeTab === 'list'"
      :view-mode="activeTab === 'list' ? viewMode : undefined"
      :view-toggle-handler="activeTab === 'list' ? handleViewToggle : undefined"
      :empty-text="activeTab === 'list' ? t.list.emptyText : undefined"
  >
    <div class="template-page">
      <a-tabs v-model:activeKey="activeTab" class="template-tabs" size="small">
        <a-tab-pane key="list" :tab="t.list.tab">
          <AstDataSection>
            <template #toolbar>
              <div class="toolbar">
                <AstSearchInput
                    v-model="query.templateTitle"
                    :placeholder="t.list.searchPlaceholder"
                    @search="fetchList"
                />
                <AstStatusSwitch v-model="query.status" @change="fetchList"/>
              </div>
            </template>

            <template v-if="showAdvanced" #toolbar-extra>
              <a-input
                  v-model:value="query.category"
                  allow-clear
                  class="toolbar-input narrow"
                  :placeholder="t.list.categoryPlaceholder"
                  @pressEnter="fetchList"
              >
                <template #prefix>
                  <tags-outlined/>
                </template>
              </a-input>
              <a-select
                  v-model:value="query.templateType"
                  :options="templateTypeFilterOptions"
                  allow-clear
                  class="toolbar-select narrow-select"
                  :placeholder="t.list.templateTypePlaceholder"
              />
            </template>


            <!-- Card Grid Mode -->
            <div v-if="dataViewMode === 'card'" class="template-grid-section">
              <a-spin :spinning="loading">
                <div class="template-grid">
                  <div class="add-card" @click="openCreate">
                    <PlusOutlined class="add-icon"/>
                    <span class="add-text">{{ t.list.create }}</span>
                  </div>
                  <TemplateCard
                      v-for="record in list"
                      :key="record.id"
                      :record="record"
                      :selected="record.id != null && selectedKeySet.has(record.id)"
                      @delete="handleDeleteOne"
                      @edit="openEdit"
                      @toggle="onTemplateCardToggle"
                  />
                </div>
              </a-spin>
            </div>

            <AstDataView
                v-else
                :columns="columns"
                :data-source="list"
                :loading="loading"
                :row-selection="rowSelection"
                :scroll="{ x: 1100 }"
                :empty-text="t.list.emptyMatchText"
                mode="table"
                row-key="id"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'templateType'">
                  <span>{{ renderTemplateType(String(record.templateType || '')) }}</span>
                </template>
                <template v-else-if="column.key === 'status'">
                  <span>{{ renderStatus(String(record.status || '')) }}</span>
                </template>
                <template v-else-if="column.key === 'content'">
                  <span class="content-preview">{{ previewContent(record.content) }}</span>
                </template>
                <template v-else-if="column.key === 'actions'">
                  <a-button type="link" @click="openEdit(record)">{{ t.list.edit }}</a-button>
                  <a-divider type="vertical"/>
                  <a-popconfirm
                      :cancel-text="t.list.cancel"
                      :ok-text="t.list.confirm"
                      :title="t.list.deleteConfirm"
                      @confirm="() => handleDeleteOne(record.id)"
                  >
                    <a-button danger type="link">{{ t.list.delete }}</a-button>
                  </a-popconfirm>
                </template>
              </template>
            </AstDataView>

            <template #pagination>
              <AstPagination
                  :current="page.pageNum"
                  :page-size="page.pageSize"
                  :total="page.total"
                  @change="onPageChange"
              />
            </template>
          </AstDataSection>
        </a-tab-pane>

        <a-tab-pane key="debugger" :tab="t.debugger.tab">
          <TemplateDebugger/>
        </a-tab-pane>
      </a-tabs>

      <TemplateFormModal
          v-model:open="modal.open"
          :confirm-loading="modal.submitting"
          :initial="modalInitial"
          :mode="modal.mode"
          @submit="handleFormSubmit"
      />
    </div>
  </AstPageShell>
</template>

<script lang="ts" setup>
import {computed, reactive, ref} from 'vue'
import {message} from 'ant-design-vue'
import {PlusOutlined, TagsOutlined} from '@ant-design/icons-vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import AstDataSection from '@/components/home/AstDataSection.vue'
import AstDataView from '@/components/home/AstDataView.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstStatusSwitch from '@/components/home/AstStatusSwitch.vue'
import TemplateFormModal from './TemplateFormModal.vue'
import TemplateCard from './component/TemplateCard.vue'
import TemplateDebugger from './component/TemplateDebugger.vue'
import {type AiTemplate, aiTemplateApi, type PageResponse} from '@/api/aiTemplate.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-template')

const activeTab = ref('list')


type QueryState = {
  templateTitle?: string
  templateKey?: string
  category?: string
  templateType?: string
  status?: string
}

const statusOptions = computed(() => [
  {label: t.value.list.status.enabled, value: 'enabled'},
  {label: t.value.list.status.disabled, value: 'disabled'}
])

const templateTypeFilterOptions = computed(() => [
  {label: t.value.list.templateType.freemarker, value: 'FREEMARKER'},
  {label: t.value.list.templateType.stringTemplate, value: 'STRING_TEMPLATE'}
])

const renderStatus = (status: string) => {
  return statusOptions.value.find((x) => x.value === status)?.label ?? status
}

const renderTemplateType = (tp: string) => {
  return templateTypeFilterOptions.value.find((x) => x.value === tp)?.label ?? tp
}

const previewContent = (raw: string | undefined) => {
  if (!raw) return '—'
  const one = raw.replace(/\s+/g, ' ').trim()
  return one.length > 80 ? `${one.slice(0, 80)}…` : one
}

const columns = computed(() => [
  {title: t.value.list.column.templateKey, dataIndex: 'templateKey', key: 'templateKey', width: 160, ellipsis: true},
  {title: t.value.list.column.templateTitle, dataIndex: 'templateTitle', key: 'templateTitle', width: 180, ellipsis: true},
  {title: t.value.list.column.category, dataIndex: 'category', key: 'category', width: 100, ellipsis: true},
  {title: t.value.list.column.templateType, key: 'templateType', width: 130},
  {title: t.value.list.column.version, dataIndex: 'version', key: 'version', width: 72},
  {title: t.value.list.column.content, key: 'content', width: 260, ellipsis: true},
  {title: t.value.list.column.status, key: 'status', width: 90},
  {title: t.value.list.column.actions, key: 'actions', width: 160, fixed: 'right' as const}
])

const query = reactive<QueryState>({})
const showAdvanced = ref(false)
const list = ref<AiTemplate[]>([])
const loading = ref(false)

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const selectedRowKeys = ref<Array<number | string>>([])
const selectedKeySet = computed(() => new Set(selectedRowKeys.value))

const viewMode = ref<'grid' | 'list'>('grid')
const dataViewMode = computed<'card' | 'table'>(() => (viewMode.value === 'grid' ? 'card' : 'table'))

const handleViewToggle = () => {
  viewMode.value = viewMode.value === 'grid' ? 'list' : 'grid'
}

const onTemplateCardToggle = (id: number | string, checked: boolean) => {
  if (checked) {
    if (!selectedRowKeys.value.includes(id)) {
      selectedRowKeys.value = [...selectedRowKeys.value, id]
    }
    return
  }
  selectedRowKeys.value = selectedRowKeys.value.filter((k) => k !== id)
}

const currentPageIds = computed(() =>
    list.value
        .map((item) => item.id)
        .filter((id): id is number | string => id !== undefined && id !== null)
)

const allCurrentSelected = computed(() => {
  return currentPageIds.value.length > 0 && currentPageIds.value.every((id) => selectedRowKeys.value.includes(id))
})

const partCurrentSelected = computed(() => {
  if (currentPageIds.value.length === 0) return false
  const count = currentPageIds.value.filter((id) => selectedRowKeys.value.includes(id)).length
  return count > 0 && count < currentPageIds.value.length
})

const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys: Array<number | string>) => {
    selectedRowKeys.value = keys
  }
}))

const toggleSelectAllCurrentPage = (checked: boolean) => {
  if (checked) {
    selectedRowKeys.value = Array.from(new Set([...selectedRowKeys.value, ...currentPageIds.value]))
    return
  }
  selectedRowKeys.value = selectedRowKeys.value.filter((id) => !currentPageIds.value.includes(id))
}

const toggleStatusFilter = (value: 'enabled' | 'disabled') => {
  query.status = query.status === value ? undefined : value
}

const resetFilters = () => {
  query.templateTitle = undefined
  query.templateKey = undefined
  query.category = undefined
  query.templateType = undefined
  query.status = undefined
  showAdvanced.value = false
  page.pageNum = 1
  selectedRowKeys.value = []
  void fetchList()
}

const modal = reactive({
  open: false,
  mode: 'create' as 'create' | 'edit',
  submitting: false
})

const modalInitial = ref<AiTemplate | null>(null)

const fetchList = async () => {
  loading.value = true
  try {
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        templateTitle: query.templateTitle || undefined,
        templateKey: query.templateKey || undefined,
        category: query.category || undefined,
        templateType: query.templateType || undefined,
        status: query.status || undefined
      }
    }
    const resp: PageResponse<AiTemplate> = await aiTemplateApi.queryPage(payload)
    list.value = resp.list || []
    page.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

const onPageChange = (p: number, size: number) => {
  page.pageNum = p
  page.pageSize = size
  void fetchList()
}

const openCreate = () => {
  modal.mode = 'create'
  modalInitial.value = null
  modal.open = true
}

const openEdit = async (record: AiTemplate) => {
  modal.mode = 'edit'
  const id = record.id
  if (id == null) return

  try {
    const detail = await aiTemplateApi.detail(id)
    modalInitial.value = detail
    modal.open = true
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || t.value.list.saveFailed)
  }
}

const handleDeleteOne = async (id: number | string) => {
  if (id == null) return
  const msg = await aiTemplateApi.delete([id])
  message.success(msg)
  selectedRowKeys.value = []
  void fetchList()
}

const handleBatchDelete = async () => {
  const ids = [...selectedRowKeys.value]
  if (ids.length === 0) return

  if (confirm(t.value.list.batchDeleteConfirm)) {
    const msg = await aiTemplateApi.delete(ids)
    message.success(msg)
    selectedRowKeys.value = []
    void fetchList()
  }
}

const handleFormSubmit = async (form: AiTemplate) => {
  modal.submitting = true
  try {
    const payload: AiTemplate = {...form}
    const v = payload.version
    if (v !== undefined && v !== null) {
      const n = Number(v)
      payload.version = Number.isFinite(n) ? n : undefined
    }

    let msg: string
    if (modal.mode === 'create') {
      delete (payload as { id?: unknown }).id
      msg = await aiTemplateApi.create(payload)
    } else {
      msg = await aiTemplateApi.update(payload)
    }

    message.success(msg)
    modal.open = false
    void fetchList()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || t.value.list.saveFailed)
  } finally {
    modal.submitting = false
  }
}

void fetchList()
</script>

<style scoped>
.template-page {
  padding: 20px;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 14px;
  flex-wrap: wrap;
}

.search-cluster {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
  padding: 6px;
  border-radius: 16px;
  border: 1px solid var(--border-default);
  background: var(--bg-surface);
}

.search-cluster :deep(.ant-input-affix-wrapper) {
  border: none;
  box-shadow: none;
  background: transparent;
}

.search-cluster :deep(.ant-input-affix-wrapper:hover),
.search-cluster :deep(.ant-input-affix-wrapper-focused) {
  border: none;
  box-shadow: none;
  background: color-mix(in srgb, var(--bg-card) 85%, var(--bg-surface));
}

.toolbar-input {
  width: 200px;
}

.search-main-input {
  width: 320px;
}

.search-sub-input {
  width: 220px;
}

.toolbar-input.narrow {
  width: 160px;
}

.toolbar-select {
  width: 180px;
}

.toolbar-select.narrow-select {
  width: 180px;
}

.primary-btn,
.ghost-btn {
  height: 40px;
  border-radius: 12px;
}

.danger-btn {
  color: var(--error);
  border-color: color-mix(in srgb, var(--error) 28%, var(--border-default));
  background: color-mix(in srgb, var(--error) 7%, var(--bg-card));
}

.danger-btn:hover,
.danger-btn:focus {
  color: var(--error) !important;
  border-color: color-mix(in srgb, var(--error) 42%, var(--border-default)) !important;
  background: color-mix(in srgb, var(--error) 12%, var(--bg-card)) !important;
}

.status-switch {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 4px;
  border-radius: 14px;
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
}

.status-btn {
  height: 36px;
  border: none;
  border-radius: 10px;
  color: var(--text-secondary);
  background: transparent;
  box-shadow: none;
}

.status-btn.active {
  color: var(--primary);
  background: color-mix(in srgb, var(--primary) 10%, var(--bg-card));
}

.filter-toggle-btn {
  height: 40px;
  border-radius: 12px;
  color: var(--text-secondary);
}

.content-preview {
  color: var(--text-tertiary);
  font-size: 12px;
}

/* ── Card Grid (matching unified style) ── */
.template-grid-section {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 14px;
}

.template-grid > * {
  animation: cardEnter 0.35s ease both;
}

.template-grid > *:nth-child(1) { animation-delay: 0ms; }
.template-grid > *:nth-child(2) { animation-delay: 40ms; }
.template-grid > *:nth-child(3) { animation-delay: 80ms; }
.template-grid > *:nth-child(4) { animation-delay: 120ms; }
.template-grid > *:nth-child(5) { animation-delay: 160ms; }
.template-grid > *:nth-child(6) { animation-delay: 200ms; }
.template-grid > *:nth-child(7) { animation-delay: 240ms; }
.template-grid > *:nth-child(8) { animation-delay: 280ms; }
.template-grid > *:nth-child(9) { animation-delay: 320ms; }
.template-grid > *:nth-child(10) { animation-delay: 360ms; }
.template-grid > *:nth-child(n+11) { animation-delay: 400ms; }

@keyframes cardEnter {
  from {
    opacity: 0;
    transform: translateY(16px) scale(0.97);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* ── Add Card ── */
.add-card {
  border: 2px dashed var(--border-subtle);
  background: transparent;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 140px;
  color: var(--text-muted);
}

.add-card:hover {
  border-color: var(--primary);
  color: var(--primary);
  background: color-mix(in srgb, var(--primary) 2%, transparent);
}

.add-icon {
  font-size: 22px;
  opacity: 0.4;
  transition: opacity 0.2s;
}

.add-card:hover .add-icon {
  opacity: 0.8;
}

.add-text {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

@media (max-width: 720px) {
  .toolbar-input,
  .toolbar-input.narrow,
  .search-main-input,
  .search-sub-input,
  .toolbar-select,
  .toolbar-select.narrow-select {
    width: 100%;
  }

  .search-cluster,
  .status-switch {
    width: 100%;
  }

  .search-cluster {
    padding: 8px;
  }

  .status-switch {
    justify-content: space-between;
  }

  .status-btn {
    flex: 1;
  }

  .pagination-wrap {
    justify-content: center;
  }
}

/* ── Tabs ── */
.template-tabs {
  margin-bottom: 0;
}

.template-tabs :deep(.ant-tabs-nav) {
  margin-bottom: 0;
  padding-left: 4px;
}

.template-tabs :deep(.ant-tabs-content-holder) {
  padding-top: 16px;
}

.template-tabs :deep(.ant-tabs-tab) {
  font-size: 13px;
  font-weight: 600;
}
</style>
