<template>
  <div class="config-container">
    <div class="config-header">
      <div class="header-left">
        <a-button type="text" class="back-btn" @click="emit('back')">
          <component :is="ArrowLeftOutlined" />
        </a-button>
        <div class="header-info">
          <div class="icon-wrapper">
            <component :is="LayoutOutlined" class="config-icon" />
          </div>
          <div class="header-text">
            <a-input
              :value="agentName"
              class="name-input"
              :disabled="true"
            />
            <p class="header-subtitle">正在配置 Agent 运行环境与逻辑底座</p>
          </div>
        </div>
      </div>
      <div class="header-right">
        <a-button type="text" class="cancel-btn" @click="emit('back')">取消</a-button>
        <a-button type="primary" class="save-btn">保存更改</a-button>
      </div>
    </div>

    <div class="config-content">
      <!-- 1. 业务逻辑与集成 -->
      <section class="config-section">
        <div class="section-header">
          <div class="step-number">1</div>
          <h3 class="section-title">业务逻辑与集成</h3>
        </div>
        <div class="section-grid">
          <a-card class="config-card">
            <label class="card-label">挂载 Prompt 模板</label>
            <a-select class="config-select" :disabled="true">
              <a-select-option value="academic">Academic_Translation_v2</a-select-option>
              <a-select-option value="standard">Standard_Refactor_v1</a-select-option>
            </a-select>
            <div class="tag-list">
              <a-tag class="prompt-tag">Sys: Translator</a-tag>
              <a-tag class="prompt-tag">Var: {lang}</a-tag>
            </div>
          </a-card>
          <a-card class="config-card">
            <label class="card-label">MCP & 工具挂载</label>
            <div class="tool-list">
              <div class="tool-item">
                <span class="tool-name">
                  <component :is="LinkOutlined" class="tool-icon" />
                  FileSystem
                </span>
                <a-switch default-checked />
              </div>
              <div class="tool-item">
                <span class="tool-name">
                  <component :is="SearchOutlined" class="tool-icon" />
                  Search-API
                </span>
                <a-switch default-checked />
              </div>
            </div>
          </a-card>
        </div>
      </section>

      <!-- 2. 运行时实例与参数 -->
      <section class="config-section">
        <div class="section-header">
          <div class="step-number">2</div>
          <h3 class="section-title">运行时实例与参数 (Instance)</h3>
        </div>
        <a-card class="config-card large-card">
          <div class="instance-grid">
            <div class="instance-item">
              <label class="item-label">选择预设参数实例</label>
              <a-select class="config-select" :disabled="true">
                <a-select-option value="default">Default-Max-Instance</a-select-option>
                <a-select-option value="creative">Creative-Turbo-Preset</a-select-option>
              </a-select>
            </div>
            <div class="instance-item">
              <label class="item-label">计费账号</label>
              <div class="account-info">
                <component :is="CreditCardOutlined" class="account-icon" />
                <span>Aliyun-Main-01</span>
              </div>
            </div>
          </div>
          <div class="params-grid">
            <div class="param-item">
              <div class="param-header">
                <span class="param-label">Temperature</span>
                <span class="param-value">0.7</span>
              </div>
              <a-slider :disabled="true" :default-value="70" :max="100" />
            </div>
            <div class="param-item">
              <label class="param-label">Max Tokens</label>
              <a-input-number class="param-input" :disabled="true" :default-value="4096" />
            </div>
            <div class="param-item">
              <label class="param-label">Top-P</label>
              <a-input-number class="param-input" :disabled="true" :default-value="0.9" :step="0.1" />
            </div>
          </div>
        </a-card>
      </section>

      <!-- 3. 底层模型能力限制 -->
      <section class="config-section">
        <div class="section-header">
          <div class="step-number">3</div>
          <h3 class="section-title">底层模型能力限制 (Model)</h3>
        </div>
        <a-card class="model-card">
          <div class="model-info">
            <div class="model-icon-wrapper">
              <component :is="RobotOutlined" class="model-icon" />
            </div>
            <span class="model-name">qwen-max</span>
          </div>
          <div class="model-stats">
            <div class="stat-item">
              <span class="stat-label">Context</span>
              <span class="stat-value">128k</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">Func Call</span>
              <span class="stat-value yes">YES</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">Base Cost</span>
              <span class="stat-value">¥0.012</span>
            </div>
          </div>
        </a-card>
      </section>

      <div class="bottom-spacing"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  ArrowLeftOutlined,
  LayoutOutlined,
  LinkOutlined,
  SearchOutlined,
  CreditCardOutlined,
  RobotOutlined,
} from '@ant-design/icons-vue'

defineProps<{ agentName: string }>()
const emit = defineEmits(['back'])
</script>

<style scoped>
.config-container {
  padding: 32px;
  max-width: 1000px;
  margin: 0 auto;
  overflow-y: auto;
  height: 100%;
}

/* 头部 */
.config-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border-default);
  margin-bottom: 32px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.back-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: var(--bg-elevated);
  color: var(--text-muted);
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-btn:hover {
  background: var(--primary-hover);
  color: var(--primary);
}

.header-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.icon-wrapper {
  width: 48px;
  height: 48px;
  background: rgba(59, 130, 246, 0.08);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(59, 130, 246, 0.15);
}

.config-icon {
  font-size: 24px;
  color: var(--primary);
}

.header-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.name-input {
  font-size: 20px;
  font-weight: bold;
  color: var(--text-primary);
  background: transparent;
  border: none;
  padding: 0;
  width: auto;
}

.header-subtitle {
  font-size: 12px;
  color: var(--text-muted);
  margin: 0;
}

.header-right {
  display: flex;
  gap: 12px;
}

.cancel-btn {
  font-size: 12px;
  color: var(--text-muted);
}

.cancel-btn:hover {
  color: var(--text-primary);
}

.save-btn {
  font-size: 12px;
  font-weight: 600;
  padding: 6px 20px;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.25);
}

/* 配置内容 */
.config-content {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.config-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.step-number {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--bg-elevated);
  color: var(--text-muted);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: bold;
}

.section-title {
  font-size: 12px;
  font-weight: bold;
  color: var(--text-primary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* 卡片布局 */
.section-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.config-card {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  padding: 16px;
}

.config-card.large-card {
  grid-column: span 2;
}

.card-label {
  font-size: 10px;
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  margin-bottom: 12px;
  display: block;
}

.config-select {
  border-radius: 6px;
  font-size: 12px;
  font-weight: bold;
}

/* Prompt 标签 */
.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
}

.prompt-tag {
  font-size: 9px;
  padding: 2px 8px;
  background: rgba(251, 191, 36, 0.08);
  color: #eab308;
  border-color: rgba(251, 191, 36, 0.2);
  border-radius: 4px;
}

/* 工具列表 */
.tool-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.tool-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--bg-surface);
  padding: 8px;
  border-radius: 6px;
  border: 1px solid var(--border-default);
}

.tool-name {
  font-size: 11px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 8px;
}

.tool-icon {
  font-size: 12px;
  color: var(--text-muted);
}

/* 实例配置 */
.instance-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.instance-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.item-label {
  font-size: 10px;
  color: var(--text-muted);
}

.account-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: var(--text-secondary);
  padding: 6px 10px;
  background: var(--bg-surface);
  border-radius: 6px;
  border: 1px solid var(--border-default);
}

.account-icon {
  font-size: 12px;
  color: var(--primary);
}

/* 参数配置 */
.params-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
}

.param-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.param-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.param-label {
  font-size: 10px;
  color: var(--text-muted);
}

.param-value {
  font-size: 12px;
  color: var(--primary);
  font-weight: bold;
}

.param-input {
  border-radius: 6px;
  font-size: 12px;
  color: var(--text-secondary);
}

/* 模型卡片 */
.model-card {
  background: rgba(59, 130, 246, 0.04);
  border: 1px solid rgba(59, 130, 246, 0.12);
  border-radius: var(--radius-lg);
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.model-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.model-icon-wrapper {
  width: 32px;
  height: 32px;
  background: rgba(147, 51, 234, 0.08);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(147, 51, 234, 0.15);
}

.model-icon {
  font-size: 14px;
  color: #a78bfa;
}

.model-name {
  font-size: 14px;
  font-weight: bold;
  color: var(--text-primary);
}

.model-stats {
  display: flex;
  gap: 48px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-label {
  font-size: 10px;
  color: var(--text-muted);
  text-transform: uppercase;
}

.stat-value {
  font-size: 12px;
  font-weight: bold;
  color: var(--text-primary);
}

.stat-value.yes {
  color: #10b981;
}

/* 底部间距 */
.bottom-spacing {
  height: 40px;
}
</style>
