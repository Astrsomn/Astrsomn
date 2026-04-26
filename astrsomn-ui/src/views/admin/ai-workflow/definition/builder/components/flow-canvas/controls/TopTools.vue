<template>
  <div class="canvas-tools">
    <a-tooltip title="框选模式">
      <a-button class="tool-btn" :class="{ active: interactionMode === 'box' }" shape="circle" @click="$emit('set-mode', 'box')">
        <template #icon><BorderOutlined /></template>
      </a-button>
    </a-tooltip>
    <a-tooltip title="平移模式">
      <a-button class="tool-btn" :class="{ active: interactionMode === 'pan' }" shape="circle" @click="$emit('set-mode', 'pan')">
        <template #icon><DragOutlined /></template>
      </a-button>
    </a-tooltip>
    <a-tooltip title="清空选择">
      <a-button class="tool-btn" shape="circle" @click="$emit('clear-selection')">
        <template #icon><CloseCircleOutlined /></template>
      </a-button>
    </a-tooltip>
    <a-tooltip title="撤销（占位）">
      <a-button class="tool-btn" shape="circle" @click="$emit('placeholder', '撤销')">
        <template #icon><UndoOutlined /></template>
      </a-button>
    </a-tooltip>
    <a-tooltip title="重做（占位）">
      <a-button class="tool-btn" shape="circle" @click="$emit('placeholder', '重做')">
        <template #icon><RedoOutlined /></template>
      </a-button>
    </a-tooltip>
  </div>
</template>

<script setup lang="ts">
import { BorderOutlined, CloseCircleOutlined, DragOutlined, RedoOutlined, UndoOutlined } from '@ant-design/icons-vue'

defineProps<{
  interactionMode: 'box' | 'pan'
}>()

defineEmits<{
  'set-mode': [mode: 'box' | 'pan']
  'clear-selection': []
  placeholder: [action: string]
}>()
</script>

<style scoped>
.canvas-tools {
  position: absolute;
  top: 12px;
  left: 12px;
  z-index: 6;
  display: flex;
  flex-direction: row;
  gap: 10px;
  padding: 6px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.08);
}

.tool-btn {
  width: 38px;
  height: 38px;
  border: 1px solid #d9e1ec;
  color: #334155;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease, color 0.2s ease;
}

.tool-btn:hover {
  transform: translateY(-2px);
  border-color: #91caff;
  color: #1677ff;
  box-shadow: 0 8px 16px rgba(59, 130, 246, 0.2);
}

.tool-btn.active {
  border-color: #1677ff;
  color: #1677ff;
  background: #eff6ff;
}
</style>
