<template>
  <div class="right-top">
    <div class="status-info">
      <div class="status-dot" />
      <span class="status-text">Preview & Debug</span>
    </div>
    <div class="action-buttons">
      <button
        class="action-btn"
        type="button"
        :class="{ active: mode === 'code' }"
        @click="$emit('toggle')"
      >
        <CodeOutlined />
        Code
      </button>
      <button class="action-btn reset-btn" type="button">
        <DeleteOutlined />
        Reset
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { DeleteOutlined, CodeOutlined } from '@ant-design/icons-vue';

defineProps<{
  mode: 'chat' | 'code';
}>();

defineEmits<{
  toggle: [];
}>();
</script>

<style scoped>
.right-top {
  flex-shrink: 0;
  padding: 16px 20px;
  border-bottom: 1px solid color-mix(in srgb, #e2e8f0 50%, transparent);
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.3);
}

.status-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #10b981;
  border-radius: 50%;
  box-shadow: 0 0 8px rgba(16, 185, 129, 0.5);
  animation: agent-studio-pulse 2s infinite;
}

.status-text {
  font-weight: 700;
  font-size: 14px;
  letter-spacing: 0.02em;
  color: #334155;
  text-transform: uppercase;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  color: #94a3b8;
  padding: 4px 0;
  border-radius: 4px;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: color 0.2s;
  display: flex;
  align-items: center;
  gap: 4px;
}

.action-btn:hover {
  color: #2563eb;
}

.action-btn.active {
  color: #2563eb;
}

.reset-btn:hover {
  color: #ef4444;
}
</style>
