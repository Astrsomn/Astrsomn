<template>
  <div class="palette-dock">
    <div
      v-for="item in items"
      :key="item.key"
      class="dock-group"
      @mouseenter="openGroup(item.key)"
      @mouseleave="scheduleClose"
    >
      <button type="button" class="dock-item" :class="`dock-item-${item.key}`" :title="item.description">
        <component :is="resolveIcon(item.key)" class="dock-icon" :class="`dock-icon-${item.key}`" />
      </button>

      <div
        v-if="expandedGroup === item.key"
        class="dock-children"
        @mouseenter="cancelClose"
        @mouseleave="scheduleClose"
      >
        <button
          v-for="child in item.children"
          :key="child.type"
          type="button"
          class="child-item"
          draggable="true"
          :title="child.description"
          @dragstart="onDragStart($event, child.type)"
        >
          {{ child.label }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import {
  ApartmentOutlined,
  RobotOutlined,
  BranchesOutlined,
  ToolOutlined,
  MessageOutlined
} from '@ant-design/icons-vue'
import { useNodeDnD } from '@/views/admin/ai-workflow/definition/builder/composables/useNodeDnD.ts'
import type { CanvasPaletteIconItem, WorkflowNodeType } from '../../../domain/types.ts'

defineProps<{
  items: CanvasPaletteIconItem[]
}>()

const { startDrag } = useNodeDnD()
const expandedGroup = ref<string | null>(null)
let closeTimer: number | null = null

const iconMap = {
  control: ApartmentOutlined,
  ai: RobotOutlined,
  logic: BranchesOutlined,
  tool: ToolOutlined,
  interaction: MessageOutlined
}

const resolveIcon = (key: string) => {
  return iconMap[key as keyof typeof iconMap] || ToolOutlined
}

const onDragStart = (ev: DragEvent, type: WorkflowNodeType) => {
  startDrag(ev, type)
}

const openGroup = (key: string) => {
  cancelClose()
  expandedGroup.value = key
}

const closeGroup = () => {
  expandedGroup.value = null
}

const cancelClose = () => {
  if (closeTimer != null) {
    window.clearTimeout(closeTimer)
    closeTimer = null
  }
}

const scheduleClose = () => {
  cancelClose()
  closeTimer = window.setTimeout(() => {
    closeGroup()
  }, 220)
}
</script>

<style scoped>
.palette-dock {
  position: absolute;
  top: 50%;
  left: 12px;
  transform: translateY(-50%);
  z-index: 8;
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 6px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.08);
}

.dock-group {
  position: relative;
}

.dock-item {
  width: 42px;
  height: 42px;
  border: 1px solid #d9e1ec;
  border-radius: 8px;
  background: #f8fafc;
  color: #334155;
  padding: 0;
  cursor: pointer;
  display: grid;
  justify-items: center;
  align-items: center;
  transition: all 0.2s ease;
}

.dock-item:hover {
  transform: translateY(-1px) scale(1.14);
  box-shadow: 0 10px 18px rgba(15, 23, 42, 0.18);
}

.dock-item-control {
  border-color: #bfdbfe;
  background: #eff6ff;
}

.dock-item-ai {
  border-color: #e9d5ff;
  background: #faf5ff;
}

.dock-item-logic {
  border-color: #99f6e4;
  background: #f0fdfa;
}

.dock-item-tool {
  border-color: #fed7aa;
  background: #fff7ed;
}

.dock-item-interaction {
  border-color: #fecdd3;
  background: #fff1f2;
}

.dock-icon {
  font-size: 16px;
}

.dock-icon-control {
  color: #2563eb;
}

.dock-icon-ai {
  color: #9333ea;
}

.dock-icon-logic {
  color: #0f766e;
}

.dock-icon-tool {
  color: #ea580c;
}

.dock-icon-interaction {
  color: #e11d48;
}

.dock-children {
  position: absolute;
  top: 0;
  left: 62px;
  z-index: 10;
  min-width: 176px;
  border: 1px solid #d9e1ec;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.08);
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.dock-children::before {
  content: '';
  position: absolute;
  left: -14px;
  top: 0;
  width: 14px;
  height: 100%;
}

.child-item {
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background: #fff;
  color: #334155;
  padding: 8px 10px;
  font-size: 12px;
  text-align: left;
  cursor: grab;
}

.child-item:hover {
  border-color: #91caff;
  background: #f8fbff;
}
</style>
