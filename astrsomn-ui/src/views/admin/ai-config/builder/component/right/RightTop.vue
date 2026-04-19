<template>
  <div class="right-top">
    <div class="status-info">
      <div class="status-dot"></div>
      <span class="status-text">实时预览测试</span>
    </div>
    <div class="action-buttons">
      <button class="action-btn" :class="{ active: mode === 'code' }" @click="$emit('toggle')">
        <code-outlined />
        {{ mode === 'code' ? '对话模式' : '代码模式' }}
      </button>
      <button class="action-btn reset-btn">
        <delete-outlined />
        重置
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
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-default);
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--bg-card);
}

.status-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-dot {
  width: 10px;
  height: 10px;
  background: #10b981;
  border-radius: 50%;
  box-shadow: 0 0 8px rgba(16, 185, 129, 0.5);
}

.status-text {
  font-weight: bold;
  font-size: 13px;
  color: var(--text-primary);
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-secondary);
  padding: 5px 12px;
  border-radius: var(--radius-md);
  background: transparent;
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 4px;
}

.action-btn:hover {
  color: var(--primary);
  background: rgba(99, 102, 241, 0.1);
}

.action-btn.active {
  color: var(--primary);
  background: rgba(99, 102, 241, 0.15);
  border-color: var(--primary);
}

.reset-btn:hover {
  color: #ef4444;
  background: rgba(239, 68, 68, 0.1);
}
</style>
