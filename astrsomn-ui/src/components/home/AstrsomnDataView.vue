<template>
  <a-spin :spinning="loading">
    <transition mode="out-in" name="data-view-switch">
      <div
          v-if="dataSource.length > 0 && mode === 'card'"
          key="card"
          :style="gridStyle"
          class="data-view-grid"
      >
        <div
            v-for="record in dataSource"
            :key="resolveKey(record)"
            class="data-view-grid-item"
        >
          <slot :record="record" name="card"/>
        </div>
      </div>

      <a-table
          v-else-if="dataSource.length > 0"
          key="table"
          :bordered="bordered"
          :class="{ dense: dense }"
          :columns="processedColumns"
          :data-source="dataSource"
          :pagination="false"
          :row-key="rowKey"
          :row-selection="rowSelection"
          :scroll="scroll"
          :style="tableStyle"
          class="data-view-table"
      >
        <template #bodyCell="{ column, record, text, index }">
          <slot
              :column="column"
              :index="index"
              :record="record"
              :text="text"
              name="bodyCell"
          >
            <template v-if="column.copyable && text">
              <span class="copyable-cell" title="点击复制" @click="handleCopy(text)">
               
                <CopyOutlined class="copy-icon"/> {{ text }}
              </span>
            </template>
            <template v-else-if="column.tag || column.enum">
              <a-tag :color="resolveTagColor(column, text)">
                <component :is="column.icon" v-if="column.icon"/>
                {{ resolveTagText(column, text) }}
              </a-tag>
            </template>
            <template v-else-if="column.dateFormat && text">
              <span class="icon-cell">
                <component :is="column.icon" v-if="column.icon" class="cell-icon"/>
                {{ props.dateFormatter(text) }}
              </span>
            </template>
            <template v-else-if="column.image || column.enableBase64Render">
              <img
                  :alt="column.imageAlt || ''"
                  :class="column.imageClass || 'base64-image'"
                  :src="text"
                  :style="column.imageStyle"
              />
            </template>
            <template v-else-if="column.icon">
              <span class="icon-cell">
                <component :is="column.icon" class="cell-icon"/>
                {{ text }}
              </span>
            </template>
            <template v-else>
              {{ text }}
            </template>
          </slot>
        </template>
      </a-table>

      <div v-else key="empty" class="data-view-empty">
        <a-empty :description="emptyText"/>
      </div>
    </transition>
  </a-spin>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {message} from 'ant-design-vue'
import {CopyOutlined} from '@ant-design/icons-vue'

const props = withDefaults(defineProps<{
  mode: 'card' | 'table'
  dataSource: any[]
  columns?: any[]
  rowKey?: string | ((record: any) => string | number)
  rowSelection?: any
  scroll?: Record<string, any>
  loading?: boolean
  emptyText?: string
  bordered?: boolean
  cardMinWidth?: string
  cardGap?: string
  cardColumns?: number
  dense?: boolean
  tableRowHeight?: string
  tableHeaderHeight?: string
  dateFormatter?: (value: string) => string
  /** 是否支持 base64 图片渲染 */
  enableBase64Render?: boolean
}>(), {
  columns: () => [],
  rowKey: 'id',
  rowSelection: undefined,
  scroll: undefined,
  loading: false,
  emptyText: '暂无数据',
  bordered: true,
  cardMinWidth: '320px',
  cardGap: '12px',
  cardColumns: 3,
  dense: false,
  tableRowHeight: '',
  tableHeaderHeight: '',
  dateFormatter: (value: string) => {
    if (!value) return '--'
    return value.replace('T', ' ').slice(0, 16)
  },
  enableBase64Render: false
})

const gridStyle = computed(() => ({
  gridTemplateColumns: `repeat(${props.cardColumns}, minmax(0, 1fr))`,
  gap: props.cardGap
}))

const tableStyle = computed(() => {
  const style: Record<string, string> = {}
  if (props.dense) {
    style['--dense-header-height'] = props.tableHeaderHeight || '32px'
    style['--dense-row-height'] = props.tableRowHeight || '28px'
  }
  return style
})

const processedColumns = computed(() => {
  return props.columns.map(col => {
    if (col.copyable) {
      return {
        ...col,
        customRender: undefined
      }
    }
    return col
  })
})

const resolveKey = (record: any) => {
  if (typeof props.rowKey === 'function') return props.rowKey(record)
  const key = props.rowKey
  return record?.[key] ?? record?.id ?? record?.agentKey ?? record?.bizKey ?? record?.agentName ?? Math.random()
}

const defaultEnumColors: Record<string, string> = {
  success: 'green',
  success1: 'green',
  success2: 'cyan',
  warning: 'orange',
  warning1: 'orange',
  error: 'red',
  error1: 'red',
  info: 'blue',
  info1: 'blue',
  default: 'gray'
}

const resolveTagColor = (column: any, value: string | number): string => {
  if (column.tagColor) {
    if (typeof column.tagColor === 'function') {
      return column.tagColor(value)
    }
    return column.tagColor
  }

  if (column.enum) {
    const enumItem = column.enum.find((item: any) => String(item.value) === String(value))
    if (enumItem && enumItem.color) {
      return enumItem.color
    }
    if (enumItem && enumItem.status) {
      return defaultEnumColors[enumItem.status] || defaultEnumColors.default
    }
  }

  return defaultEnumColors.default
}

const resolveTagText = (column: any, value: string | number): string => {
  if (column.enum) {
    const enumItem = column.enum.find((item: any) => String(item.value) === String(value))
    if (enumItem) {
      return enumItem.label ?? String(value)
    }
  }

  if (column.tagText) {
    if (typeof column.tagText === 'function') {
      return column.tagText(value)
    }
    return column.tagText
  }

  return String(value)
}

const handleCopy = async (text: string) => {
  if (!text) return
  try {
    await navigator.clipboard.writeText(text)
    message.success('已复制到剪贴板')
  } catch {
    message.error('复制失败，请手动复制')
  }
}
</script>

<style scoped>
.data-view-grid {
  display: grid;
}

.data-view-grid-item {
  min-width: v-bind(cardMinWidth);
}

.data-view-table {
  margin-top: 6px;
  overflow: hidden;
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
}

.data-view-table :deep(.ant-table-container) {
  border-radius: 0;
  background: var(--bg-card);
  border: none;
}

.data-view-table :deep(.ant-table) {
  background: var(--bg-card);
  border: none;
}

.data-view-table :deep(.ant-table-body) {
  background: var(--bg-card);
}

.data-view-table :deep(.ant-table-thead) {
  background: var(--bg-card);
}

.data-view-table :deep(.ant-table-tbody > tr) {
  background: var(--bg-card);
}

.data-view-table :deep(.ant-table-tbody > tr:hover) {
  background: var(--primary-hover);
}

.data-view-table :deep(.ant-table-footer) {
  background: var(--bg-card);
  border-top: 1px solid var(--border-default);
}

.data-view-table :deep(.ant-table-thead > tr > th) {
  background: var(--bg-card) !important;
  color: var(--text-secondary);
  font-weight: 600;
  border-bottom: 1px solid var(--border-default);
  border-right: 1px solid var(--border-default);
}

.data-view-table :deep(.ant-table-thead > tr > th:last-child) {
  border-right: none;
}

.data-view-table :deep(.ant-table-tbody > tr > td) {
  border-bottom: 1px solid var(--border-default);
  border-right: 1px solid var(--border-default);
  color: var(--text-primary);
  background: var(--bg-card) !important;
}

.data-view-table :deep(.ant-table-tbody > tr > td:last-child) {
  border-right: none;
}

.data-view-table :deep(.ant-table-tbody > tr:hover > td) {
  background: var(--primary-hover) !important;
}

.data-view-table :deep(.ant-table-row-selected) {
  background: var(--primary-hover) !important;
}

.data-view-table :deep(.ant-table-row-selected > td) {
  background: var(--primary-hover) !important;
}

.data-view-table :deep(.ant-table-cell-fix-right),
.data-view-table :deep(.ant-table-cell-fix-left) {
  background: var(--bg-card) !important;
  z-index: 10;
}

.data-view-table :deep(.ant-table-tbody > tr:hover .ant-table-cell-fix-right),
.data-view-table :deep(.ant-table-tbody > tr:hover .ant-table-cell-fix-left) {
  background: var(--primary-hover) !important;
}

.data-view-table :deep(.ant-table-row-selected .ant-table-cell-fix-right),
.data-view-table :deep(.ant-table-row-selected .ant-table-cell-fix-left) {
  background: var(--primary-hover) !important;
}

.data-view-table :deep(.ant-table-fixed-right),
.data-view-table :deep(.ant-table-fixed-left) {
  z-index: 10;
}

.data-view-table :deep(.ant-table-fixed-right .ant-table-header),
.data-view-table :deep(.ant-table-fixed-left .ant-table-header),
.data-view-table :deep(.ant-table-fixed-right .ant-table-body),
.data-view-table :deep(.ant-table-fixed-left .ant-table-body) {
  background: var(--bg-card) !important;
}

.data-view-table :deep(.ant-table-fixed-right table),
.data-view-table :deep(.ant-table-fixed-left table) {
  background: var(--bg-card) !important;
}

.data-view-table :deep(.ant-table-fixed-right .ant-table-thead > tr > th),
.data-view-table :deep(.ant-table-fixed-left .ant-table-thead > tr > th) {
  background: var(--bg-card) !important;
  color: var(--text-secondary);
  border-bottom: 1px solid var(--border-default);
  border-right: 1px solid var(--border-default);
}

.data-view-table :deep(.ant-table-fixed-right .ant-table-tbody > tr > td),
.data-view-table :deep(.ant-table-fixed-left .ant-table-tbody > tr > td) {
  background: var(--bg-card) !important;
  color: var(--text-primary);
  border-bottom: 1px solid var(--border-default);
  border-right: 1px solid var(--border-default);
}

.data-view-table :deep(.ant-table-fixed-right) {
  right: 0;
}

.data-view-table :deep(.ant-table-fixed-left) {
  left: 0;
}

.data-view-table.dense :deep(.ant-table-thead > tr > th) {
  padding: 4px 12px;
  line-height: var(--dense-header-height);
  font-size: 12px;
}

.data-view-table.dense :deep(.ant-table-tbody > tr > td) {
  padding: 4px 12px;
  line-height: var(--dense-row-height);
  font-size: 12px;
}

.data-view-table :deep(.ant-table-scroll) {
  border-bottom: 1px solid var(--border-default);
}

.data-view-table :deep(.ant-table-body-inner) {
  border-bottom: 1px solid var(--border-default);
}

.copyable-cell {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: var(--primary);
  transition: color 0.2s;
}

.copyable-cell:hover {
  color: var(--primary-light);
}

.copy-icon {
  font-size: 12px;
  opacity: 0.5;
  transition: opacity 0.2s;
}

.copyable-cell:hover .copy-icon {
  opacity: 1;
}

.icon-cell {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.cell-icon {
  font-size: 12px;
  color: var(--text-muted);
}

.base64-image {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 0 auto;
}

.data-view-empty {
  display: flex;
  justify-content: center;
  padding: 32px 0 12px;
}

.data-view-switch-enter-active,
.data-view-switch-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.data-view-switch-enter-from,
.data-view-switch-leave-to {
  opacity: 0;
  transform: translateY(6px);
}

@media (max-width: 720px) {
  .data-view-grid {
    grid-template-columns: 1fr !important;
  }

  .data-view-grid-item {
    min-width: 0;
  }
}
</style>
