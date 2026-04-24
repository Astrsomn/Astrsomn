<template>
  <div class="todo-panel">
    <div class="panel-header">
      <div class="header-left">
        <AlertOutlined />
        <h3 class="panel-title">待处理事项</h3>
      </div>
    </div>
    <ul class="todo-list">
      <li v-for="task in todoItems" :key="task" class="todo-item">
        <div class="task-dot"></div>
        <span class="task-text">{{ task }}</span>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { AlertOutlined } from '@ant-design/icons-vue'

defineProps<{
  todoItems: string[]
}>()
</script>

<style scoped>
.todo-panel {
  background: white;
  border-radius: 24px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  padding: 24px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-left :deep(.anticon) {
  width: 20px;
  height: 20px;
  color: #94a3b8;
}

.panel-title {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.todo-list {
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.todo-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.task-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #3b82f6;
  margin-top: 6px;
  flex-shrink: 0;
}

.task-text {
  font-size: 12px;
  color: #64748b;
  line-height: 1.5;
  flex: 1;
}
</style>
