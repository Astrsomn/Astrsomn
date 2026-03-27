<template>
  <transition name="input-slide" appear>
    <div class="chat-input-section">
      <div class="input-panel">
        <div class="input-toolbar">
          <div class="toolbar-left">
            <a-select
              :value="selectedAgent"
              class="panel-select"
              placeholder="选择 Agent"
              :bordered="false"
              :loading="optionsLoading"
              @update:value="emit('update:selectedAgent', $event)"
            >
              <a-select-option
                v-for="agent in agentOptions"
                :key="agent.agentKey"
                :value="agent.agentKey"
              >
                {{ agent.agentName || agent.agentKey }}
              </a-select-option>
            </a-select>

            <div class="v-divider"></div>

            <a-select
              :value="selectedModel"
              class="panel-select"
              placeholder="选择模型"
              :bordered="false"
              dropdown-class-name="custom-dropdown"
              :loading="optionsLoading"
              @update:value="emit('update:selectedModel', $event)"
            >
              <a-select-option
                v-for="model in modelOptions"
                :key="model.modelKey"
                :value="model.modelKey"
              >
                {{ model.modelName || model.modelKey }}
              </a-select-option>
            </a-select>
          </div>
        </div>

        <div class="input-body">
          <a-textarea
            :value="userInput"
            :auto-size="{ minRows: 1, maxRows: 6 }"
            placeholder="问点什么吧..."
            class="main-textarea"
            :disabled="isStreaming"
            @update:value="emit('update:userInput', $event)"
            @pressEnter="handleEnter"
          />
        </div>

        <div class="input-footer">
          <div class="footer-left">
            <a-upload :show-upload-list="false" class="upload-trigger">
              <button class="icon-btn" title="上传文件">
                <PaperClipOutlined />
              </button>
            </a-upload>

            <div class="feature-switches">
              <div
                class="feature-tag"
                :class="{ active: isDeepThinking }"
                @click="emit('update:isDeepThinking', !isDeepThinking)"
              >
                <BulbOutlined /> 深度思考
              </div>
              <div
                class="feature-tag"
                :class="{ active: isWebSearch }"
                @click="emit('update:isWebSearch', !isWebSearch)"
              >
                <GlobalOutlined /> 联网搜索
              </div>
            </div>
          </div>

          <div class="footer-right">
            <div v-if="userInput.length > 0" class="char-count">
              {{ userInput.length }}
            </div>
            <div v-else-if="isStreaming" class="stream-status">流式回复中</div>
            <a-button
              type="primary"
              class="send-btn"
              :disabled="sendDisabled"
              @click="isStreaming ? emit('stop') : handleSend()"
            >
              <template #icon>
                <StopOutlined v-if="isStreaming" />
                <ArrowUpOutlined v-else />
              </template>
            </a-button>
          </div>
        </div>
      </div>
      <p class="input-hint">Astrsomn 可能产生错误信息，请核查重要内容。</p>
    </div>
  </transition>
</template>

<script setup lang="ts">
import {
  ArrowUpOutlined,
  BulbOutlined,
  GlobalOutlined,
  PaperClipOutlined,
  StopOutlined
} from '@ant-design/icons-vue'
import type { AiAgent } from '@/api/aiAgent'
import type { AiModel } from '@/api/aiModel'

const props = defineProps<{
  selectedAgent?: string
  selectedModel?: string
  userInput: string
  isDeepThinking: boolean
  isWebSearch: boolean
  isStreaming: boolean
  optionsLoading: boolean
  sendDisabled: boolean
  agentOptions: AiAgent[]
  modelOptions: AiModel[]
}>()

const emit = defineEmits<{
  'update:selectedAgent': [value?: string]
  'update:selectedModel': [value?: string]
  'update:userInput': [value: string]
  'update:isDeepThinking': [value: boolean]
  'update:isWebSearch': [value: boolean]
  submit: [text: string]
  stop: []
}>()

const handleSend = () => {
  if (props.sendDisabled) return
  const text = props.userInput.trim()
  if (!text) return
  emit('submit', text)
  emit('update:userInput', '')
}

const handleEnter = (e: KeyboardEvent) => {
  if (!e.shiftKey) {
    e.preventDefault()
    if (props.isStreaming) return
    handleSend()
  }
}
</script>

<style scoped>
.chat-input-section {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 2;
  pointer-events: none;
  padding: 20px 20px 30px;
  background: transparent;
}

.input-panel {
  max-width: 840px;
  margin: 0 auto;
  pointer-events: auto;
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: 24px;
  box-shadow: var(--shadow-card);
  transition: border-color 0.3s, box-shadow 0.3s;
  overflow: hidden;
}

.input-panel:focus-within {
  border-color: var(--primary);
  box-shadow: 0 0 0 4px var(--primary-hover);
}

.input-toolbar {
  padding: 12px 16px 4px;
  display: flex;
  justify-content: space-between;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.v-divider {
  width: 1px;
  height: 18px;
  background: var(--border-default);
}

.panel-select {
  min-width: 100px;
  font-size: 13px;
  font-weight: 500;
}

.panel-select :deep(.ant-select-selection-item) {
  color: var(--text-secondary) !important;
}

.input-body {
  padding: 4px 16px;
}

.main-textarea {
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
  font-size: 16px;
  color: var(--text-primary);
  padding: 8px 0;
  resize: none;
}

.input-footer {
  padding: 8px 16px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.icon-btn {
  background: var(--bg-input);
  border: 1px solid var(--border-default);
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--text-secondary);
  transition: all 0.2s;
}

.icon-btn:hover {
  background: var(--border-subtle);
  color: var(--primary);
}

.feature-switches {
  display: flex;
  gap: 8px;
}

.feature-tag {
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 12px;
  background: var(--bg-input);
  border: 1px solid var(--border-default);
  color: var(--text-muted);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;
  user-select: none;
}

.feature-tag:hover {
  border-color: var(--text-muted);
}

.feature-tag.active {
  background: var(--primary-hover);
  border-color: var(--primary);
  color: var(--primary);
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.char-count {
  font-size: 12px;
  color: var(--text-muted);
}

.stream-status {
  font-size: 12px;
  color: var(--primary);
}

.send-btn {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}

.input-hint {
  pointer-events: auto;
  text-align: center;
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 12px;
}

.input-slide-enter-active {
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.input-slide-enter-from {
  opacity: 0;
  transform: translateY(100%);
}

@media (max-width: 640px) {
  .feature-tag span {
    display: none;
  }
}
</style>
