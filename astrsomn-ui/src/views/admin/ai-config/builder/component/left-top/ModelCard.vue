<template>
  <div class="node-connector">
    <div class="model-card" :class="{ 'has-model': model }">
      <div class="card-header">
        <div class="header-left">
          <div class="icon-badge">
            <RobotOutlined />
          </div>
          <div class="header-info">
            <span class="card-label">模型</span>
            <span v-if="model?.modelKey" class="model-key-display">{{ model.modelKey }}</span>
          </div>
        </div>
      </div>
      <template v-if="model">
        <div class="card-title">{{ model.modelName }}</div>
        <div class="card-subtitle">
          <template v-if="model.modelType">{{ model.modelType }}</template>
          <template v-else>128k context</template>
        </div>
      </template>
      <template v-else>
        <div class="card-placeholder">{{ readonly ? '随实例自动关联' : '请选择模型' }}</div>
      </template>
      <div v-if="!readonly" class="card-icon">
        <DoubleRightOutlined />
      </div>
      <div v-if="!readonly" class="card-overlay">
        <button class="overlay-btn" title="选择模型" @click.stop="emit('select')">
          <UnorderedListOutlined />
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { RobotOutlined, UnorderedListOutlined, DoubleRightOutlined } from '@ant-design/icons-vue'
import type { AiModel } from '@/api/aiModel'

interface Props {
  model?: AiModel
  readonly?: boolean
}

withDefaults(defineProps<Props>(), {
  readonly: false
})

const emit = defineEmits<{
  (e: 'select'): void
}>()
</script>

<style scoped>
.node-connector {
  flex: 1;
  position: relative;
  width: 100%;
  min-height: 0;
}

.model-card {
  width: 100%;
  height: 100%;
  min-height: 100px;
  background: var(--ab-glass-bg, rgba(255, 255, 255, 0.8));
  backdrop-filter: blur(var(--ab-glass-haze, 10px));
  -webkit-backdrop-filter: blur(var(--ab-glass-haze, 10px));
  border: 1px solid var(--ab-glass-border, rgba(255, 255, 255, 0.6));
  border-radius: var(--ab-glass-radius, 16px);
  box-shadow: var(--ab-glass-shadow, 0 4px 20px rgba(0, 0, 0, 0.03));
  padding: 16px;
  position: relative;
  overflow: hidden;
  transition:
    border-color 0.2s,
    box-shadow 0.2s,
    transform 0.2s;
  display: flex;
  flex-direction: column;
}

.model-card:hover {
  border-color: var(--ab-hover-line, #3b82f6);
  box-shadow: var(--ab-hover-shadow, 0 0 15px rgba(59, 130, 246, 0.15));
  transform: translateY(-1px);
}

.model-card.has-model {
}

.model-card:not(.has-model) {
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.icon-badge {
  width: 40px;
  height: 40px;
  flex-shrink: 0;
  background: #e0e7ff;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6366f1;
  font-size: 18px;
}

.icon-badge .anticon {
  font-size: 18px;
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.card-label {
  font-size: 10px;
  font-weight: 700;
  color: #6366f1;
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.model-key-display {
  font-size: 11px;
  color: var(--text-secondary);
  font-family: 'JetBrains Mono', monospace;
}

.card-icon {
  color: var(--text-hint);
  font-size: 16px;
  position: absolute;
  top: 50%;
  right: 16px;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-title {
  font-weight: bold;
  color: var(--text-primary);
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-subtitle {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 4px;
}

.card-placeholder {
  font-size: 12px;
  line-height: 1.4;
  color: #94a3b8;
  padding: 0;
}

.card-overlay {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  opacity: 0;
  transition: opacity 0.2s;
  pointer-events: none;
}

.model-card:hover .card-overlay {
  opacity: 1;
  pointer-events: auto;
}

.overlay-btn {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-elevated);
  border: 1px solid var(--border-default);
  color: var(--primary);
  cursor: pointer;
  transition: all 0.2s;
}

.overlay-btn:hover {
  transform: scale(1.1);
  background: var(--primary);
  color: white;
}

.overlay-btn .anticon {
  font-size: 16px;
}
</style>
