<template>
  <div class="node-connector">
    <div :class="{ 'has-instance': instance }" class="instance-card">
      <div class="card-header">
        <div class="header-left">
          <div class="icon-badge">
            <ControlOutlined/>
          </div>
          <div class="header-info">
            <span class="card-label">{{ t.instanceCard.label }}</span>
            <span v-if="instance?.instanceKey" class="instance-key-display">{{ instance.instanceKey }}</span>
          </div>
        </div>
      </div>
      <template v-if="instance">
        <div class="card-title">{{ instance.instanceName || t.instanceCard.creativeParams }}</div>
        <div v-if="instance.temperature !== undefined" class="card-subtitle">
          Temp: {{ instance.temperature }}
        </div>
      </template>
      <template v-else>
        <div class="card-placeholder">{{ t.instanceCard.selectOrCreate }}</div>
      </template>
      <div class="card-icon">
        <DoubleRightOutlined/>
      </div>
      <div class="card-overlay">
        <button class="overlay-btn" :title="t.instanceCard.selectInstance" @click.stop="emit('select')">
          <UnorderedListOutlined/>
        </button>
        <button class="overlay-btn" :title="t.instanceCard.createInstance" @click.stop="emit('create')">
          <PlusOutlined/>
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {ControlOutlined, DoubleRightOutlined, PlusOutlined, UnorderedListOutlined} from '@ant-design/icons-vue'
import type {AiInstance} from '@/api/aiInstance'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-builder')

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
  width: 100%;
  min-height: 0;
}

.instance-card {
  width: 100%;
  height: 100%;
  min-height: 100px;
  background: var(--ab-glass-bg);
  backdrop-filter: blur(var(--ab-glass-haze, 10px));
  -webkit-backdrop-filter: blur(var(--ab-glass-haze, 10px));
  border: 1px solid var(--ab-glass-border);
  border-radius: var(--ab-glass-radius, 16px);
  box-shadow: var(--ab-glass-shadow);
  padding: 16px;
  position: relative;
  overflow: hidden;
  transition: border-color 0.2s,
  box-shadow 0.2s,
  transform 0.2s;
  display: flex;
  flex-direction: column;
}

.instance-card:hover {
  border-color: var(--ab-hover-line);
  box-shadow: var(--ab-hover-shadow, 0 0 15px color-mix(in srgb, var(--primary) 15%, transparent));
  transform: translateY(-1px);
}

.instance-card.has-instance {
}

.instance-card:not(.has-instance) {
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
  background: #d1fae5;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #10b981;
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
  color: color-mix(in srgb, var(--primary) 70%, transparent);
  margin-top: 4px;
}

.card-placeholder {
  font-size: 12px;
  line-height: 1.4;
  color: var(--text-tertiary);
  padding: 0;
}

.card-overlay {
  position: absolute;
  inset: 0;
  background: color-mix(in srgb, var(--bg-elevated) 10%, transparent);
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
