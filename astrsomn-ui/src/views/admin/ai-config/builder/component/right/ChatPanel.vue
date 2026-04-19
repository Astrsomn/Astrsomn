<template>
  <div class="chat-panel">
    <div class="chat-messages custom-scrollbar">
      <div class="message-item">
        <div class="message-avatar">
          <robot-outlined />
        </div>
        <div class="message-bubble">
          主链路已就绪！✨ 所有的配置改动都会在这里即时生效，试着跟我打个招呼吧。
        </div>
      </div>
    </div>
    <RightBottom />
  </div>
</template>

<script setup lang="ts">
import { RobotOutlined } from '@ant-design/icons-vue';
import RightBottom from './RightBottom.vue';
</script>

<style scoped>
.chat-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  background: linear-gradient(to bottom, rgba(99, 102, 241, 0.05), var(--bg-card));
}

.message-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.message-avatar {
  width: 36px;
  height: 36px;
  background: var(--primary-gradient);
  border-radius: var(--radius-lg);
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  box-shadow: var(--shadow-card);
}

.message-bubble {
  background: var(--bg-elevated);
  padding: 16px;
  border-radius: var(--radius-xl);
  border-top-left-radius: 0;
  font-size: 12px;
  line-height: 1.5;
  color: var(--text-primary);
  border: 1px solid rgba(99, 102, 241, 0.1);
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
