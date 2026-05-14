<template>
  <div class="right-top">
    <div class="status-info">
      <div class="status-dot"/>
      <span class="status-text">Preview & Debug</span>
    </div>
    <div class="action-buttons">
      <button
          :class="{ active: mode === 'code' }"
          class="action-btn"
          type="button"
          @click="$emit('toggle')"
      >
        <CodeOutlined/>
        Code
      </button>
      <button class="action-btn reset-btn" type="button">
        <DeleteOutlined/>
        Reset
      </button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {CodeOutlined, DeleteOutlined} from '@ant-design/icons-vue';

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
  border-bottom: 1px solid var(--builder-right-header-border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--builder-right-header-bg);
}

.status-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: var(--success);
  border-radius: 50%;
  box-shadow: 0 0 8px rgba(16, 185, 129, 0.5);
  animation: agent-studio-pulse 2s infinite;
}

.status-text {
  font-weight: 700;
  font-size: 14px;
  letter-spacing: 0.02em;
  color: var(--builder-right-status-text);
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
  color: var(--builder-right-action-color);
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
  color: var(--builder-right-action-hover-color);
}

.action-btn.active {
  color: var(--builder-right-action-hover-color);
}

.reset-btn:hover {
  color: var(--builder-right-reset-hover-color);
}
</style>
