<template>
  <div
    class="resizable-widget"
    :class="{ 'span-full': layout.colSpan === 2 }"
    :style="{ height: height + 'px' }"
  >
    <div class="widget-header">
      <div class="widget-title-area">
        <span class="widget-title">{{ title }}</span>
        <slot name="badge" />
      </div>
      <div class="widget-actions">
        <button
          class="action-btn"
          :title="layout.colSpan === 2 ? '半宽显示' : '全宽显示'"
          @click="toggleSpan"
        >
          <ColumnWidthOutlined />
        </button>
      </div>
    </div>
    <div class="widget-body">
      <slot />
    </div>
    <div class="resize-handle" @mousedown.prevent="startResize">
      <span class="resize-dots"></span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onUnmounted } from 'vue'
import { ColumnWidthOutlined } from '@ant-design/icons-vue'
import { useDashboardLayout } from '@/composables/useDashboardLayout'

const props = defineProps<{ widgetId: string; title: string }>()

const { get, update } = useDashboardLayout()
const layout = computed(() => get(props.widgetId))
const height = ref(layout.value.height)
const resizing = ref(false)

watch(() => layout.value.height, v => {
  if (!resizing.value) height.value = v
})

function toggleSpan() {
  update(props.widgetId, { colSpan: layout.value.colSpan === 2 ? 1 : 2 })
}

let startY = 0
let startH = 0

function startResize(e: MouseEvent) {
  resizing.value = true
  startY = e.clientY
  startH = height.value
  document.addEventListener('mousemove', onMove)
  document.addEventListener('mouseup', onEnd)
  document.body.style.cursor = 'ns-resize'
  document.body.style.userSelect = 'none'
}

function onMove(e: MouseEvent) {
  height.value = Math.max(160, startH + e.clientY - startY)
}

function onEnd() {
  resizing.value = false
  document.removeEventListener('mousemove', onMove)
  document.removeEventListener('mouseup', onEnd)
  document.body.style.cursor = ''
  document.body.style.userSelect = ''
  update(props.widgetId, { height: height.value })
}

onUnmounted(() => {
  document.removeEventListener('mousemove', onMove)
  document.removeEventListener('mouseup', onEnd)
})
</script>

<style scoped>
.resizable-widget {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;
  transition: border-color 0.2s;
}

.resizable-widget:hover {
  border-color: var(--border-default);
}

.span-full {
  grid-column: span 2;
}

.widget-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px 8px;
  flex-shrink: 0;
}

.widget-title-area {
  display: flex;
  align-items: center;
  gap: 8px;
}

.widget-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-heading);
}

.widget-actions {
  display: flex;
  gap: 4px;
}

.action-btn {
  width: 28px;
  height: 28px;
  border: none;
  border-radius: var(--radius-sm);
  background: transparent;
  color: var(--text-muted);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  transition: background 0.15s, color 0.15s;
}

.action-btn:hover {
  background: var(--primary-hover);
  color: var(--accent-blue);
}

.widget-body {
  flex: 1;
  overflow: auto;
  padding: 0 18px 12px;
  min-height: 0;
}

.resize-handle {
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: ns-resize;
  flex-shrink: 0;
  user-select: none;
  opacity: 0;
  transition: opacity 0.2s;
}

.resizable-widget:hover .resize-handle {
  opacity: 1;
}

.resize-dots::before {
  content: '···';
  font-size: 16px;
  color: var(--text-muted);
  letter-spacing: 2px;
  line-height: 1;
}
</style>
