<template>
  <a-spin :spinning="loading">
    <transition name="data-view-switch" mode="out-in">
      <div
        v-if="dataSource.length > 0 && mode === 'card'"
        key="card"
        class="data-view-grid"
        :style="gridStyle"
      >
        <div
          v-for="record in dataSource"
          :key="resolveKey(record)"
          class="data-view-grid-item"
        >
          <slot name="card" :record="record" />
        </div>
      </div>

      <a-table
        v-else-if="dataSource.length > 0"
        key="table"
        class="data-view-table"
        :class="{ dense: dense }"
        :style="tableStyle"
        :row-key="rowKey"
        :data-source="dataSource"
        :columns="processedColumns"
        :pagination="false"
        :row-selection="rowSelection"
        :scroll="scroll"
        :bordered="bordered"
      >
        <template #bodyCell="{ column, record, text, index }">
          <slot
            name="bodyCell"
            :column="column"
            :record="record"
            :text="text"
            :index="index"
          >
            <template v-if="column.copyable && text">
              <span class="copyable-cell" @click="handleCopy(text)" title="点击复制">
                {{ text }}
                <CopyOutlined class="copy-icon" />
              </span>
            </template>
            <template v-else-if="column.tag || column.enum">
              <a-tag :color="resolveTagColor(column, text)">
                <component v-if="column.icon" :is="column.icon"  />
                {{ resolveTagText(column, text) }}
              </a-tag>
            </template>
            <template v-else-if="column.dateFormat && text">
              <span class="icon-cell">
                <component v-if="column.icon" :is="column.icon" class="cell-icon" />
                {{ props.dateFormatter(text) }}
              </span>
            </template>
            <template v-else-if="column.image || column.enableBase64Render">
              <img 
                :src="text" 
                :alt="column.imageAlt || ''"
                :class="column.imageClass || 'base64-image'"
                :style="column.imageStyle"
              />
            </template>
            <template v-else-if="column.icon">
              <span class="icon-cell">
                <component :is="column.icon" class="cell-icon" />
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
        <a-empty :description="emptyText" />
      </div>
    </transition>
  </a-spin>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { message } from 'ant-design-vue'
import { CopyOutlined } from '@ant-design/icons-vue'

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
  return record?.[key] ?? record?.id ?? record?.agentKey ?? record?.agentName ?? Math.random()
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
}

.data-view-table :deep(.ant-table-container) {
  border-start-start-radius: 12px;
  border-start-end-radius: 12px;
}

.data-view-table :deep(.ant-table-thead > tr > th) {
  background: #f8fafc;
  color: #334155;
  font-weight: 700;
  border-bottom: 1px solid #e2e8f0;
}

.data-view-table :deep(.ant-table-tbody > tr > td) {
  border-bottom: 1px solid #e2e8f0;
  color: #1f2937;
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

.data-view-table :deep(.ant-table-tbody > tr:hover > td) {
  background: #f8fbff;
}

.data-view-table :deep(.ant-table-cell-fix-right),
.data-view-table :deep(.ant-table-cell-fix-left) {
  background: #ffffff;
  z-index: 1;
}

.data-view-table :deep(.ant-table-fixed-right),
.data-view-table :deep(.ant-table-fixed-left) {
  position: sticky;
}

.data-view-table :deep(.ant-table-fixed-right) {
  right: 0;
}

.data-view-table :deep(.ant-table-fixed-left) {
  left: 0;
}

.data-view-table :deep(.ant-table-tbody > tr:hover .ant-table-cell-fix-right),
.data-view-table :deep(.ant-table-tbody > tr:hover .ant-table-cell-fix-left) {
  background: #f8fbff;
}

.data-view-table :deep(.ant-table-row-selected .ant-table-cell-fix-right),
.data-view-table :deep(.ant-table-row-selected .ant-table-cell-fix-left) {
  background: #e8f0fe;
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
  color: var(--primary-hover);
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
  color: #9ca3af;
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
