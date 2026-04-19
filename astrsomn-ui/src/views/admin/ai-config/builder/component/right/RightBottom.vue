<template>
  <div class="right-bottom">
    <div class="input-wrapper">
      <textarea
        class="input-area custom-scrollbar"
        rows="2"
        placeholder="发送指令给 Agent..."
      ></textarea>
      <div class="input-actions">
        <div class="action-icons">
          <PaperClipOutlined class="action-icon" />
          <AudioOutlined class="action-icon" />
        </div>
        <button class="send-btn">
          <SendOutlined />
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { PaperClipOutlined, AudioOutlined, SendOutlined } from '@ant-design/icons-vue';
</script>

<style scoped>
.right-bottom {
  padding: 24px;
  background: var(--bg-card);
}

.input-wrapper {
  position: relative;
  background: var(--bg-elevated);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-default);
  padding: 8px;
  transition: all 0.3s;
}

.input-wrapper:focus-within {
  border-color: var(--primary);
  box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
}

.input-area {
  width: 100%;
  background: transparent;
  border: none;
  outline: none;
  font-size: 12px;
  padding: 12px;
  resize: none;
  font-weight: 500;
  color: var(--text-primary);
  font-family: inherit;
}

.input-area::placeholder {
  color: var(--text-tertiary);
}

.input-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 12px 8px;
}

.action-icons {
  display: flex;
  gap: 16px;
  color: var(--text-secondary);
}

.action-icon {
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}

.action-icon:hover {
  color: var(--primary);
}

.send-btn {
  background: var(--primary-gradient);
  color: white;
  width: 36px;
  height: 36px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  box-shadow: var(--shadow-card);
  border: none;
  cursor: pointer;
}

.send-btn:hover {
  opacity: 0.9;
}

.send-btn:active {
  transform: scale(0.9);
}

.send-btn .anticon {
  font-size: 14px;
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