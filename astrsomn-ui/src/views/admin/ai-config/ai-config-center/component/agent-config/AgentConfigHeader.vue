<template>
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
          <span class="header-title">Agent 配置</span>
          <p class="header-subtitle">正在配置 Agent 运行环境与逻辑底座</p>
        </div>
      </div>
    </div>
    <div class="header-right">
      <a-button type="text" class="cancel-btn" @click="emit('back')">取消</a-button>
      <a-button type="primary" class="save-btn" :loading="submitting" @click="emit('save')">
        保存更改
      </a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ArrowLeftOutlined, LayoutOutlined } from '@ant-design/icons-vue'

defineProps<{
  submitting: boolean
}>()

const emit = defineEmits<{
  back: []
  save: []
}>()
</script>

<style scoped>
.config-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-default);
  margin-bottom: 20px;
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

.header-title {
  font-size: 20px;
  font-weight: bold;
  color: var(--text-primary);
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

.save-btn {
  font-size: 12px;
  font-weight: 600;
  padding: 6px 20px;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.25);
}
</style>
