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
          <div class="ems-logo-box ems-logo-unload">
            <DeleteOutlined/>
          </div>
          <div class="ems-title-group">
            <span class="ems-main-title">确认卸载模型</span>
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
                danger
                type="primary"
                @click="handleOk"
            >
              确认卸载模型
            </a-button>
          </div>
        </div>
      </header>

      <div class="ems-body-scroll">
        <div v-if="previewError" class="ems-modal-alert">
          <a-alert :message="previewError" show-icon type="error"/>
        </div>
        <a-spin v-else :spinning="loadingPreview">
          <template v-if="unloadPreview">
            <p v-if="emptyHint" class="ems-hint">{{ emptyHint }}</p>
            <p class="ems-summary ems-summary-danger">
              已选择 {{ selectedCount }} 个模型将从本环境删除。
            </p>

            <div class="ems-preview-section">
              <div class="ems-preview-section-title">将卸载（删除）</div>
              <a-table
                  v-if="(unloadPreview.toRemove?.length ?? 0) > 0"
                  :columns="commonColumns"
                  :data-source="unloadPreview.toRemove"
                  :pagination="false"
                  :row-key="'modelKey'"
                  :row-selection="rowSelection"
                  :scroll="{ y: 280 }"
                  class="ems-preview-table"
                  size="small"
              />
              <div v-else class="ems-preview-empty">无</div>
            </div>

            <div class="ems-preview-section">
              <div class="ems-preview-section-title">因实例引用将保留</div>
              <a-table
                  v-if="(unloadPreview.keptReferenced?.length ?? 0) > 0"
                  :columns="commonColumns"
                  :data-source="unloadPreview.keptReferenced"
                  :pagination="false"
                  :row-key="'modelKey'"
                  :scroll="{ y: 200 }"
                  class="ems-preview-table ems-table-disabled"
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
import {DeleteOutlined} from '@ant-design/icons-vue'
import type {ExtensionModelUnloadPreview} from '@/api/systemExtension'
import {formatExtensionModelPreviewRow} from './extensionModelSyncPreview'

const open = defineModel<boolean>('open', {required: true})

const props = defineProps<{
  extensionLabel: string
  loadingPreview: boolean
  previewError: string
  unloadPreview: ExtensionModelUnloadPreview | null
  confirm: (selectedModelKeys: string[]) => Promise<void>
}>()

const emit = defineEmits<{
  cancel: []
}>()

const modalWidth = 'min(92vw, 820px)'
const confirming = ref(false)

// 选中的 Key 列表
const selectedRowKeys = ref<string[]>([])

// 监听数据变化，默认全部勾选待删除项
watch(
    () => props.unloadPreview,
    (newPreview) => {
      if (newPreview?.toRemove) {
        selectedRowKeys.value = newPreview.toRemove.map(m => m.modelKey)
      } else {
        selectedRowKeys.value = []
      }
    },
    {immediate: true}
)

// 表格选择配置
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
  if (!props.unloadPreview) return ''
  const p = props.unloadPreview
  const total = (p.toRemove?.length ?? 0) + (p.keptReferenced?.length ?? 0)
  if (total === 0) return '当前环境下该厂商暂无模型记录。'
  return ''
})

function onUpdateOpen(v: boolean) {
  open.value = v
  if (!v) emit('cancel')
}

// 通用的列定义，移除了固定的 width: 600
const commonColumns = [
  {
    title: '模型详细信息',
    dataIndex: 'modelKey',
    render: (_, record: any) => {
      return h('div', {
        style: {padding: '2px 0', fontSize: '13px'}
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
@import './extensionModelSyncDialog.css';

/* 1. 解决间距问题：增加单元格内边距 */
.ems-preview-table :deep(.ant-table-cell) {
  padding: 10px 12px !important;
}

/* 2. 勾选框列宽度自适应调整 */
.ems-preview-table :deep(.ant-table-selection-column) {
  width: 46px;
  text-align: center;
}

/* 危险操作的汇总文案颜色 */
.ems-summary-danger {
  color: #ff4d4f;
  font-weight: 500;
  margin-bottom: 16px;
}

.ems-preview-section {
  margin-bottom: 24px;
}

.ems-preview-section-title {
  font-weight: 600;
  margin-bottom: 12px;
  color: #333;
}

/* 已保留列表的样式微调 */
.ems-table-disabled :deep(.ant-table-cell) {
  color: #999;
}

.ems-preview-empty {
  padding: 16px;
  text-align: center;
  color: #bfbfbf;
  border: 1px dashed #eee;
}
</style>