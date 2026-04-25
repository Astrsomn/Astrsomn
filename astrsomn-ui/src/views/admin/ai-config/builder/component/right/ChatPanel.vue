<template>
  <div class="chat-panel">
    <div class="chat-messages custom-scrollbar">
      <div class="message-item">
        <div class="message-avatar">
          <RobotOutlined />
        </div>
        <div class="message-bubble">
          主链路已就绪！✨ 所有的配置改动都会在这里实时生效。框架已自动启用 <strong>Fallback</strong> 策略。
        </div>
      </div>
      <div class="trace-block">
        <div class="trace-line trace-item">
          <SearchOutlined class="trace-ico blue" />
          <span>检索知识库: 命中 2 个分片 (Score: 0.92)</span>
        </div>
        <div class="trace-line trace-item">
          <SafetyOutlined class="trace-ico green" />
          <span>安全规则校验: 无敏感词冲突</span>
        </div>
        <div class="trace-line trace-item">
          <CodeOutlined class="trace-ico purple" />
          <span>Tool Call: search_engine.v1</span>
        </div>
      </div>
    </div>
    <RightBottom />
  </div>
</template>

<script setup lang="ts">
import { RobotOutlined, SearchOutlined, SafetyOutlined, CodeOutlined } from '@ant-design/icons-vue'
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
  min-height: 0;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  background: transparent;
}

.message-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.message-avatar {
  width: 32px;
  height: 32px;
  background: #2563eb;
  border-radius: 8px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
  box-shadow: 0 4px 6px -1px rgba(37, 99, 235, 0.2);
}

.message-bubble {
  background: color-mix(in srgb, #eff6ff 80%, transparent);
  padding: 12px 14px;
  border-radius: 16px;
  border-top-left-radius: 0;
  font-size: 12px;
  line-height: 1.625;
  color: #1e40af;
  border: 1px solid color-mix(in srgb, #bfdbfe 50%, transparent);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
  max-width: 100%;
}

.message-bubble strong {
  font-weight: 700;
}

.trace-block {
  margin-left: 44px;
  padding-left: 16px;
  border-left: 2px dashed #e2e8f0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.trace-line {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 10px;
  color: #94a3b8;
}

.trace-line::before {
  content: '';
  position: absolute;
  left: -22px;
  top: 4px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #cbd5e1;
}

.trace-ico {
  font-size: 12px;
  flex-shrink: 0;
}

.trace-ico.blue {
  color: #3b82f6;
}

.trace-ico.green {
  color: #10b981;
}

.trace-ico.purple {
  color: #a855f7;
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
