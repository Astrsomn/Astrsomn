<template>
  <div class="zoom-controls">
    <span class="zoom-percent">{{ zoomPercent }}%</span>
    <a-tooltip title="适配视图">
      <a-button class="tool-btn" shape="circle" @click="$emit('fit-view')">
        <template #icon><AimOutlined /></template>
      </a-button>
    </a-tooltip>
    <a-tooltip title="放大">
      <button type="button" class="zoom-btn" @click="$emit('zoom-in')">
        <PlusOutlined />
      </button>
    </a-tooltip>
    <a-tooltip title="缩小">
      <button type="button" class="zoom-btn" @click="$emit('zoom-out')">
        <MinusOutlined />
      </button>
    </a-tooltip>
  </div>
</template>

<script setup lang="ts">
import { AimOutlined, MinusOutlined, PlusOutlined } from '@ant-design/icons-vue'

defineProps<{
  zoomPercent: number
}>()

defineEmits<{
  'fit-view': []
  'zoom-in': []
  'zoom-out': []
}>()
</script>

<style scoped>
.zoom-controls {
  position: absolute;
  right: 16px;
  bottom: 16px;
  z-index: 7;
  display: flex;
  flex-direction: row;
  gap: 10px;
  padding: 6px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.08);
}

.zoom-percent {
  min-width: 52px;
  height: 38px;
  line-height: 38px;
  text-align: center;
  border-radius: 999px;
  background: #f8fafc;
  border: 1px solid #d9e1ec;
  color: #475569;
  font-size: 12px;
  font-weight: 600;
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

.zoom-btn {
  width: 38px;
  height: 38px;
  border-radius: 999px;
  border: 1px solid #d9e1ec;
  background: #fff;
  color: #1e293b;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.zoom-btn:hover {
  transform: translateY(-2px) scale(1.04);
  border-color: #60a5fa;
  box-shadow: 0 10px 20px rgba(59, 130, 246, 0.24);
}
</style>
