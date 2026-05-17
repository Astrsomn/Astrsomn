<template>
  <div class="sidebar-chat-panel">
    <div class="chat-viewport">
      <div v-if="messages.length === 0" class="welcome-mini">
        <div class="brand-logo-s">
          <rocket-filled/>
        </div>
        <h4>AI 配置助手</h4>
        <p>模拟流程交互预览</p>
      </div>

      <div v-for="item in messages" :key="item.id" :class="item.role" class="msg-group">
        <div class="msg-avatar">
          <component :is="item.role === 'assistant' ? RobotOutlined : UserOutlined"/>
        </div>
        <div class="msg-content-area">
          <div v-if="item.role === 'assistant' && isDeepThink" class="thought-box-s">
            <div class="thought-tag">
              <bulb-outlined class="rotating-icon"/>
              <span>Thinking...</span>
            </div>
          </div>

          <div class="msg-bubble">
            {{ item.content }}
          </div>
        </div>
      </div>
    </div>

    <footer class="console-footer-s">
      <div class="console-card-s">
        <div class="feature-bar-s">
          <div
              :class="{ active: isDeepThink }"
              class="feature-item-s"
              @click="isDeepThink = !isDeepThink"
          >
            <thunderbolt-outlined/>
            <span>深度思考</span>
          </div>
          <div class="feature-item-s">
            <global-outlined/>
          </div>
        </div>

        <a-textarea
            :auto-size="{ minRows: 1, maxRows: 4 }"
            :value="draft"
            class="sidebar-input"
            placeholder="发送消息..."
            @update:value="$emit('update:draft', $event)"
            @press-enter="handleSend"
        />

        <div class="console-bottom-s">
          <div class="left-tools">
            <paper-clip-outlined class="tool-icon"/>
          </div>

          <div class="right-actions">
            <a-button
                :disabled="!draft"
                class="send-btn-s"
                size="small"
                type="primary"
                @click="$emit('send')"
            >
              <template #icon>
                <send-outlined/>
              </template>
            </a-button>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script lang="ts" setup>
import {ref} from 'vue'
import {
  BulbOutlined,
  GlobalOutlined,
  PaperClipOutlined,
  RobotOutlined,
  RocketFilled,
  SendOutlined,
  ThunderboltOutlined,
  UserOutlined
} from '@ant-design/icons-vue'

defineProps<{
  draft: string
  messages: Array<{ id: string; role: 'user' | 'assistant'; content: string }>
}>()

const emit = defineEmits<{
  'update:draft': [value: string]
  send: []
}>()

const isDeepThink = ref(true)

const handleSend = (e: KeyboardEvent) => {
  if (!e.shiftKey) {
    e.preventDefault()
    emit('send')
  }
}
</script>

<style scoped>
/* 容器：针对侧边栏宽度优化 */
.sidebar-chat-panel {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  border-left: 1px solid #f0f0f0;
}

/* 消息流：移除 15% 的 Padding */
.chat-viewport {
  flex: 1;
  overflow-y: auto;
  padding: 20px 16px; /* 窄屏改用固定内边距 */
}

.welcome-mini {
  text-align: center;
  margin-top: 40px;
  color: #1d2129;
}

.brand-logo-s {
  font-size: 24px;
  color: #1677ff;
  background: #f0f7ff;
  width: 48px;
  height: 48px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  margin-bottom: 12px;
}

.welcome-mini h4 {
  margin-bottom: 4px;
  font-weight: 600;
}

.welcome-mini p {
  font-size: 12px;
  color: #86909c;
}

/* 消息组：适配窄屏 */
.msg-group {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  width: 100%; /* 撑满侧边栏 */
}

.msg-avatar {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  background: #f4f5f8;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  flex-shrink: 0;
}

.user .msg-avatar {
  background: #1d2129;
  color: #fff;
}

.msg-content-area {
  flex: 1;
  min-width: 0;
}

.msg-bubble {
  font-size: 14px;
  line-height: 1.6;
  color: #1d2129;
  word-wrap: break-word;
}

/* 紧凑思考态 */
.thought-box-s {
  background: #f7f8fa;
  padding: 4px 8px;
  border-radius: 6px;
  margin-bottom: 6px;
  display: inline-block;
}

.thought-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: #86909c;
}

/* 输入框区域：侧边栏核心适配 */
.console-footer-s {
  padding: 12px 12px 24px;
  border-top: 1px solid #f0f0f0;
}

.console-card-s {
  background: #fff;
  border: 1px solid #e5e6eb;
  border-radius: 12px;
  padding: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
}

.feature-bar-s {
  display: flex;
  gap: 8px;
  margin-bottom: 4px;
}

.feature-item-s {
  font-size: 11px;
  color: #4e5969;
  padding: 2px 6px;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
}

.feature-item-s.active {
  background: #e8f3ff;
  color: #1677ff;
}

/* 深度覆盖文本框 */
:deep(.sidebar-input) {
  border: none !important;
  box-shadow: none !important;
  font-size: 14px !important;
  padding: 4px !important;
  background: transparent !important;
}

.console-bottom-s {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 4px;
}

.tool-icon {
  color: #86909c;
  cursor: pointer;
  padding: 4px;
  font-size: 16px;
}

.send-btn-s {
  width: 28px;
  height: 28px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
}

.rotating-icon {
  animation: rotate 2s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>