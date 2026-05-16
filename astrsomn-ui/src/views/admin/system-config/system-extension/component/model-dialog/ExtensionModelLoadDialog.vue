<template>
  <a-modal
      :closable="false"
      :footer="null"
      :open="open"
      :width="modalWidth"
      centered
      destroy-on-close
      wrap-class-name="extension-model-sync-wrap"
      @update:open="onUpdateOpen"
  >
    <div class="ems-shell">
      <header class="ems-modal-header">
        <div class="ems-header-left">
          <div class="ems-logo-box">
            <CloudUploadOutlined/>
          </div>
          <div class="ems-title-group">
            <span class="ems-main-title">确认加载模型</span>
            <span class="ems-sub-title">{{ extensionLabel || '扩展' }}</span>
          </div>
        </div>
        <div class="ems-header-actions">
          <div class="ems-header-action-pair">
            <a-button class="ems-header-action-btn ems-header-btn-cancel" @click="emit('cancel')">
              取消
            </a-button>
            <a-button
                :disabled="okDisabled"
                :loading="confirming"
                class="ems-header-action-btn ems-header-btn-ok"
                type="primary"
                @click="handleOk"
            >
              确认加载模型
            </a-button>
          </div>
        </div>
      </header>

      <div class="ems-body-scroll">
        <div v-if="previewError" class="ems-modal-alert">
          <a-alert :message="previewError" show-icon type="error"/>
        </div>
        <a-spin v-else :spinning="loadingPreview">
          <template v-if="loadPreview">
            <p v-if="emptyHint" class="ems-hint">{{ emptyHint }}</p>
            <p class="ems-summary">已选择 {{ selectedCount }} 个模型将写入本环境。</p>

            <div v-if="(loadPreview.skippedInvalidCount ?? 0) > 0" class="ems-hint"
                 style="color: var(--error); margin-bottom: 8px;">
              厂商返回条目中有 {{ loadPreview.skippedInvalidCount }} 条数据异常，将跳过。
            </div>

            <div class="ems-preview-section">
              <div class="ems-preview-section-title">
                将保存（新增）
              </div>
              <a-table
                  v-if="(loadPreview.toCreate?.length ?? 0) > 0"
                  :columns="createColumns"
                  :data-source="loadPreview.toCreate"
                  :pagination="false"
                  :row-key="'modelKey'"
                  :row-selection="rowSelection"
                  :scroll="{ y: 300 }"
                  class="ems-preview-table"
                  size="small"
              />
              <div v-else class="ems-preview-empty">无新模型可新增</div>
            </div>

            <div class="ems-preview-section">
              <div class="ems-preview-section-title">已存在将跳过</div>
              <a-table
                  v-if="(loadPreview.skippedExisting?.length ?? 0) > 0"
                  :columns="existingColumns"
                  :data-source="loadPreview.skippedExisting"
                  :pagination="false"
                  :row-key="'modelKey'"
                  :scroll="{ y: 200 }"
                  class="ems-preview-table"
                  size="small"
              />
              <div v-else class="ems-preview-empty">无</div>
            </div>
          </template>
        </a-spin>
      </div>
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
import {computed, h, ref, watch} from 'vue'
import {CloudUploadOutlined} from '@ant-design/icons-vue'
import type {ExtensionModelLoadPreview} from '@/api/systemExtension.ts'
import {formatExtensionModelPreviewRow} from '../../utils/extensionModelSyncPreview.ts'

const open = defineModel<boolean>('open', {required: true})

const props = defineProps<{
  extensionLabel: string
  loadingPreview: boolean
  previewError: string
  loadPreview: ExtensionModelLoadPreview | null
  confirm: (selectedModelKeys: string[]) => Promise<void>
}>()

const emit = defineEmits<{
  cancel: []
}>()

const modalWidth = 'min(92vw, 820px)'
const confirming = ref(false)

// 存储选中的 key 列表
const selectedRowKeys = ref<string[]>([])

// 当数据加载时，默认全选新增列表
watch(
    () => props.loadPreview,
    (newPreview) => {
      if (newPreview?.toCreate) {
        selectedRowKeys.value = newPreview.toCreate.map(m => m.modelKey)
      } else {
        selectedRowKeys.value = []
      }
    },
    {immediate: true}
)

// Table 选择功能配置
const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys: string[]) => {
    selectedRowKeys.value = keys
  },
}))

const selectedCount = computed(() => selectedRowKeys.value.length)

const okDisabled = computed(() => {
  if (props.loadingPreview || props.previewError) return true
  return selectedCount.value === 0
})

const emptyHint = computed(() => {
  if (props.loadingPreview || props.previewError) return ''
  if (!props.loadPreview) return ''
  const p = props.loadPreview
  const total = (p.toCreate?.length ?? 0) + (p.skippedExisting?.length ?? 0) + (p.skippedInvalidCount ?? 0)
  if (total === 0) return '厂商未返回可用模型条目。'
  return ''
})

function onUpdateOpen(v: boolean) {
  open.value = v
  if (!v) emit('cancel')
}

// 移除了手动渲染的 Checkbox 列，交给 row-selection 处理
const createColumns = [
  {
    title: '模型详细信息',
    dataIndex: 'modelKey',
    // 移除了固定 600 宽度，使用 flex 布局或自动宽度更灵活
    render: (_, record: any) => {
      return h('div', {
        style: {padding: '4px 0', fontSize: '13px', lineHeight: '1.5'}
      }, formatExtensionModelPreviewRow(record))
    }
  }
]

const existingColumns = [
  {
    title: '模型详细信息',
    dataIndex: 'modelKey',
    render: (_, record: any) => {
      return h('div', {
        style: {padding: '4px 0', fontSize: '13px', color: 'var(--text-muted)'}
      }, formatExtensionModelPreviewRow(record))
    }
  }
]

async function handleOk() {
  if (selectedRowKeys.value.length === 0) return
  confirming.value = true
  try {
    await props.confirm(selectedRowKeys.value)
  } finally {
    confirming.value = false
  }
}
</script>

<style scoped>
@import '../../utils/extensionModelSyncDialog.css';

/* 间距调整：增加单元格内边距，优化视觉间距 */
.ems-preview-table :deep(.ant-table-cell) {
  padding: 8px 12px !important;
}

/* 针对勾选框列的宽度微调 */
.ems-preview-table :deep(.ant-table-selection-column) {
  width: 50px;
  text-align: center;
}

.ems-preview-section {
  margin-bottom: 24px;
}

.ems-preview-section-title {
  font-weight: 600;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.ems-preview-empty {
  padding: 20px;
  text-align: center;
  color: var(--text-muted);
  border: 1px dashed var(--border-default);
  border-radius: 4px;
}

.ems-summary {
  margin-bottom: 16px;
  font-weight: 500;
  color: var(--primary);
}
</style>