<template>
  <div class="prompt-card">
    <div class="card-header">
      <div class="header-left">
        <div class="icon-badge">
          <EditOutlined />
        </div>
        <h3 class="card-title">人设与指令 (Prompt)</h3>
      </div>
      <div class="header-actions">
        <button class="action-btn primary">
          <ThunderboltOutlined />
        </button>
        <button class="action-btn">
          <FullscreenOutlined />
        </button>
      </div>
    </div>
    <textarea
      class="prompt-textarea custom-scrollbar"
      rows="4"
      placeholder="给你的 Agent 一个酷炫的人设..."
    ></textarea>
  </div>
</template>

<script setup lang="ts">
import { EditOutlined, ThunderboltOutlined, FullscreenOutlined } from '@ant-design/icons-vue';
</script>

<style scoped>
.prompt-card {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-xl);
  padding: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.prompt-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-card);
  border-color: var(--primary);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.icon-badge {
  width: 32px;
  height: 32px;
  background: rgba(99, 102, 241, 0.1);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary);
  box-shadow: var(--shadow-sm);
}

.icon-badge .anticon {
  font-size: 14px;
}

.card-title {
  font-weight: bold;
  color: var(--text-primary);
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 4px;
}

.action-btn {
  width: 28px;
  height: 28px;
  background: var(--bg-elevated);
  color: var(--text-secondary);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  background: var(--bg-secondary);
}

.action-btn.primary {
  background: rgba(99, 102, 241, 0.1);
  color: var(--primary);
}

.action-btn.primary:hover {
  background: var(--primary);
  color: white;
}

.action-btn .anticon {
  font-size: 12px;
}

.prompt-textarea {
  width: 100%;
  background: var(--bg-elevated);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  padding: 16px;
  font-size: 12px;
  font-weight: 500;
  color: var(--text-primary);
  outline: none;
  transition: all 0.3s;
  resize: none;
  font-family: inherit;
}

.prompt-textarea::placeholder {
  color: var(--text-tertiary);
}

.prompt-textarea:focus {
  border-color: var(--primary);
  box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.1);
}

.custom-scrollbar::-webkit-scrollbar {
  width: 5px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: var(--border-default);
  border-radius: 10px;
}
</style>