<template>
  <div class="chat-home" :class="{ 'is-dark': isDark }">
    <header class="top-bar">
      <div class="brand">
        <div class="logo-dot">
          <img :src="logoUrl" class="logo-img" alt="Astrsomn" />
        </div>
        <div class="brand-info">
          <span class="brand-name">Astrsomn</span>
          <span class="brand-status">AI Assistant</span>
        </div>
      </div>
      
      <div class="top-actions">
        <a-select
          :value="currentLang"
          size="small"
          class="glass-select lang-select"
          :options="languageOptions"
          @change="changeLang"
          :bordered="false"
        />
        <div class="v-divider"></div>
        <button class="theme-btn" @click="toggleTheme(!isDark)">
          <template v-if="isDark">☀️</template>
          <template v-else>🌙</template>
        </button>
        <a-button type="primary" shape="round" class="login-btn" @click="goLogin">
          登录 / 注册
        </a-button>
      </div>
    </header>

    <main class="chat-main">
      <div class="chat-messages-container">
        <div class="message-scroll-area">
          <div class="message ai">
            <div class="avatar-mini">A</div>
            <div class="content">
              你好，我是 Astrsomn AI 助手。今天有什么我可以帮你的？
            </div>
          </div>
          
          <div v-if="userInputDisplay" class="message user">
            <div class="content">{{ userInputDisplay }}</div>
          </div>

          <div v-if="userInputDisplay" class="message ai">
            <div class="avatar-mini">A</div>
            <div class="content">
              <div class="typing-placeholder">这是预览模式下的回复。我已经准备好为您提供 Langchain4j 的深度支持。</div>
            </div>
          </div>
        </div>
      </div>

      <div class="chat-input-section">
        <div class="input-panel">
          <div class="input-toolbar">
            <div class="toolbar-left">
              <a-select
                v-model:value="selectedModel"
                class="panel-select"
                placeholder="选择模型"
                :bordered="false"
                dropdown-class-name="custom-dropdown"
              >
                <a-select-option value="gpt-4o">GPT-4o (Premium)</a-select-option>
                <a-select-option value="claude-3-5">Claude 3.5 Sonnet</a-select-option>
                <a-select-option value="deepseek-r1">DeepSeek R1</a-select-option>
              </a-select>
              
              <div class="v-divider"></div>
              
              <a-select
                v-model:value="selectedAgent"
                class="panel-select"
                placeholder="选择 Agent"
                :bordered="false"
              >
                <a-select-option value="general">通用助手</a-select-option>
                <a-select-option value="coder">专家级编程</a-select-option>
                <a-select-option value="writer">创意写作</a-select-option>
              </a-select>
            </div>
          </div>

          <div class="input-body">
            <a-textarea
              v-model:value="userInput"
              :auto-size="{ minRows: 1, maxRows: 6 }"
              placeholder="问点什么吧..."
              class="main-textarea"
              @pressEnter="handleEnter"
            />
          </div>

          <div class="input-footer">
            <div class="footer-left">
              <a-upload :show-upload-list="false" class="upload-trigger">
                <button class="icon-btn" title="上传文件">
                  <paper-clip-outlined />
                </button>
              </a-upload>
              
              <div class="feature-switches">
                <div 
                  class="feature-tag" 
                  :class="{ active: isDeepThinking }"
                  @click="isDeepThinking = !isDeepThinking"
                >
                  <bulb-outlined /> 深度思考
                </div>
                <div 
                  class="feature-tag" 
                  :class="{ active: isWebSearch }"
                  @click="isWebSearch = !isWebSearch"
                >
                  <global-outlined /> 联网搜索
                </div>
              </div>
            </div>
            
            <div class="footer-right">
              <div class="char-count" v-if="userInput.length > 0">
                {{ userInput.length }}
              </div>
              <a-button 
                type="primary" 
                class="send-btn" 
                :disabled="!userInput.trim()"
                @click="submitQuestion"
              >
                <template #icon><arrow-up-outlined /></template>
              </a-button>
            </div>
          </div>
        </div>
        <p class="input-hint">Astrsomn 可能产生错误信息，请核查重要内容。</p>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { 
  PaperClipOutlined, 
  BulbOutlined, 
  GlobalOutlined, 
  ArrowUpOutlined 
} from '@ant-design/icons-vue'
import { useTheme } from '@/composables/useTheme'
import { useLanguage } from '@/composables/useLanguage'
import logoUrl from '@/assets/Astrsomn-logo.png'

const router = useRouter()
const userInput = ref('')
const userInputDisplay = ref('')

// 新增功能状态
const selectedModel = ref('gpt-4o')
const selectedAgent = ref('general')
const isDeepThinking = ref(false)
const isWebSearch = ref(false)

const { isDark, toggleTheme } = useTheme()
const { currentLang, changeLang, languageOptions } = useLanguage()

const goLogin = () => {
  localStorage.removeItem('token')
  router.push('/login')
}

const handleEnter = (e: KeyboardEvent) => {
  if (!e.shiftKey) {
    e.preventDefault()
    submitQuestion()
  }
}

const submitQuestion = () => {
  if (!userInput.value.trim()) return
  
  userInputDisplay.value = userInput.value
  userInput.value = ''
  
  const features = []
  if (isDeepThinking.value) features.push('深度思考')
  if (isWebSearch.value) features.push('联网搜索')
  
  message.loading({ 
    content: `正在通过 ${selectedModel.value} ${features.length ? `(${features.join('+')})` : ''} 处理...`, 
    duration: 1 
  })
}
</script>

<style scoped>
/* 容器与背景 */
.chat-home {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-base);
  background-image: 
    radial-gradient(circle at 50% -20%, rgba(59, 130, 246, 0.08), transparent 50%),
    radial-gradient(circle at 0% 100%, rgba(16, 185, 129, 0.05), transparent 40%);
  color: var(--text-primary);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 顶部导航美化 */
.top-bar {
  height: 64px;
  padding: 0 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(var(--bg-surface), 0.7);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--border-subtle);
  z-index: 100;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-dot {
  width: 32px;
  height: 32px;
  background: transparent;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: none;
  overflow: hidden;
}

.logo-img {
  width: 64px;
  height: 64px;
  object-fit: cover;
  object-position: 50% 0%;
}

.brand-name {
  font-size: 18px;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.brand-status {
  display: block;
  font-size: 11px;
  color: var(--text-muted);
  margin-top: -2px;
}

.top-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.v-divider {
  width: 1px;
  height: 16px;
  background: var(--border-default);
}

.theme-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 18px;
  padding: 4px;
  border-radius: 6px;
  transition: background 0.2s;
}

.theme-btn:hover {
  background: var(--border-subtle);
}

/* 聊天主体 */
.chat-main {
  flex: 1;
  position: relative;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 40px 20px;
}

.message-scroll-area {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.message {
  display: flex;
  gap: 16px;
  max-width: 85%;
}

.message.ai {
  align-self: flex-start;
}

.message.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.avatar-mini {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: var(--bg-elevated);
  border: 1px solid var(--border-default);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  flex-shrink: 0;
}

.message .content {
  padding: 12px 18px;
  border-radius: 18px;
  line-height: 1.6;
  font-size: 15px;
}

.message.ai .content {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  color: var(--text-primary);
  border-top-left-radius: 4px;
}

.message.user .content {
  background: var(--primary);
  color: white;
  border-top-right-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.2);
}

/* 高级输入框面板 */
.chat-input-section {
  padding: 20px 20px 30px;
  background: linear-gradient(to top, var(--bg-base) 60%, transparent);
}

.input-panel {
  max-width: 840px;
  margin: 0 auto;
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

/* 输入框内部工具栏 */
.input-toolbar {
  padding: 12px 16px 4px;
  display: flex;
  justify-content: space-between;
}

.panel-select {
  min-width: 100px;
  font-size: 13px;
  font-weight: 500;
}

.panel-select :deep(.ant-select-selection-item) {
  color: var(--text-secondary) !important;
}

/* 输入主体 */
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

/* 输入框底部 */
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
  text-align: center;
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 12px;
}

/* 响应式调整 */
@media (max-width: 640px) {
  .feature-tag span { display: none; }
  .top-bar { padding: 0 16px; }
  .brand-name { display: none; }
}
</style>