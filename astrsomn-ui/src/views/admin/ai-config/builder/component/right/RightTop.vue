<template>
  <div class="right-top">
    <div class="status-info">
      <div class="status-dot"></div>
      <span class="status-text">实时预览测试</span>
    </div>
    <button class="reset-btn">
      <DeleteOutlined />
      重置对话
    </button>
  </div>
</template>

<script setup lang="ts">
import { DeleteOutlined } from '@ant-design/icons-vue';
</script>

<style scoped>
.right-top {
  padding: 20px;
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
  color: var(--text-primary);
}

.reset-btn {
  font-size: 11px;
  font-weight: bold;
  color: var(--text-secondary);
  padding: 4px 12px;
  border-radius: var(--radius-md);
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 4px;
}

.reset-btn:hover {
  color: var(--primary);
  background: rgba(99, 102, 241, 0.1);
}

.reset-btn .anticon {
  font-size: 12px;
}
</style>