<template>
  <div class="node-connector">
    <div class="instance-card" :class="{ 'has-instance': instance }">
      <div class="card-header">
        <div class="header-left">
          <div class="icon-badge">
            <ControlOutlined />
          </div>
          <div class="header-info">
            <span class="card-label">实例</span>
            <span v-if="instance?.instanceKey" class="instance-key-display">{{ instance.instanceKey }}</span>
          </div>
        </div>
      </div>
      <template v-if="instance">
        <div class="card-title">{{ instance.instanceName || '创意生成参数' }}</div>
        <div class="card-subtitle" v-if="instance.temperature !== undefined">
          Temp: {{ instance.temperature }}
        </div>
      </template>
      <template v-else>
        <div class="card-placeholder">请选择或创建实例</div>
      </template>
      <div class="card-icon">
        <DoubleRightOutlined />
      </div>
      <div class="card-overlay">
        <button class="overlay-btn" title="选择实例" @click.stop="emit('select')">
          <UnorderedListOutlined />
        </button>
        <button class="overlay-btn" title="创建实例" @click.stop="emit('create')">
          <PlusOutlined />
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ControlOutlined, UnorderedListOutlined, PlusOutlined, DoubleRightOutlined } from '@ant-design/icons-vue'
import type { AiInstance } from '@/api/aiInstance'

interface Props {
  instance?: AiInstance
}

defineProps<Props>()

const emit = defineEmits<{
  (e: 'select'): void
  (e: 'create'): void
}>()
</script>

<style scoped>
.node-connector {
  flex: 1;
  position: relative;
}

.instance-card {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-left: 4px solid #10b981;
  border-radius: var(--radius-xl);
  padding: 16px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  min-height: 100px;
  display: flex;
  flex-direction: column;
}

.instance-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-card);

}

.instance-card.has-instance {
  border-left-color: #10b981;
}

.instance-card:not(.has-instance) {
  border-left-color: var(--border-default);
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
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #10b981 0%, #34d399 100%);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.25);
}

.icon-badge .anticon {
  font-size: 16px;
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.card-label {
  font-size: 10px;
  font-weight: bold;
  color: #10b981;
  text-transform: uppercase;
}

.instance-key-display {
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
  color: rgba(16, 185, 129, 0.7);
  margin-top: 4px;
}

.card-placeholder {
  font-size: 13px;
  color: var(--text-hint);
  padding: 8px 0;
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

.instance-card:hover .card-overlay {
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
