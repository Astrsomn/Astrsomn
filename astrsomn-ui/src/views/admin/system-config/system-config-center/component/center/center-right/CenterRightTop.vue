<template>
  <div class="panel-card">
    <div class="panel-head">
      <h3>
        <AlertOutlined />
        待处理事项
      </h3>
      <span>建议优先处理项</span>
    </div>
    <ul class="todo-list">
      <li v-for="task in todoItems" :key="task">{{ task }}</li>
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
.panel-card {
  border: 1px solid var(--border-default);
  border-radius: 14px;
  background: var(--bg-card);
  padding: 20px;
  min-height: 0;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 14px;
}

.panel-head h3 {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 16px;
  color: var(--text-heading);
}

.panel-head span {
  font-size: 12px;
  color: var(--text-secondary);
}

.todo-list {
  margin: 0;
  padding: 0 0 0 16px;
  display: grid;
  gap: 8px;
  color: var(--text-secondary);
  line-height: 1.5;
  font-size: 13px;
}

.todo-list li::marker {
  color: var(--primary);
}
</style>
