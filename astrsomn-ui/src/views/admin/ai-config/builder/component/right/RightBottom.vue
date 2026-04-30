<template>
  <div class="right-bottom">
    <div class="input-wrapper">
      <textarea
        class="input-area custom-scrollbar"
        rows="2"
        placeholder="发送指令测试 Agent..."
      />
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
  flex-shrink: 0;
  padding: 16px;
  border-top: 1px solid color-mix(in srgb, #e2e8f0 50%, transparent);
  background: transparent;
}

.input-wrapper {
  position: relative;
  background: color-mix(in srgb, #fff 60%, transparent);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid color-mix(in srgb, #e2e8f0 80%, transparent);
  padding: 10px 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  transition: border-color 0.2s;
}

.input-wrapper:focus-within {
  border-color: #93c5fd;
}

.input-area {
  width: 100%;
  background: transparent;
  border: none;
  outline: none;
  font-size: 14px;
  padding: 4px 8px;
  resize: none;
  font-weight: 400;
  color: #1e293b;
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
  gap: 12px;
  color: #94a3b8;
}

.action-icon {
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}

.action-icon:hover {
  color: #2563eb;
}

.send-btn {
  background: var(--ab-blue-grad, linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%));
  color: white;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition:
    transform 0.15s,
    box-shadow 0.2s;
  box-shadow: var(--ab-btn-glow, 0 4px 15px rgba(59, 130, 246, 0.3));
  border: none;
  cursor: pointer;
}

.send-btn:hover {
  box-shadow: 0 6px 18px rgba(59, 130, 246, 0.4);
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