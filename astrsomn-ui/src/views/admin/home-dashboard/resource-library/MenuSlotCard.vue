<template>
  <div class="slot-wrapper">
    <button
      type="button"
      class="slot-card"
      :class="{ 'slot-card--compact': variant === 'compact' }"
      :data-accent="accent"
      :disabled="buttonDisabled"
      @click="handleClick"
    >
      <div class="slot-icon-container" aria-hidden="true">
        <div class="slot-icon-bg"></div>
        <component v-if="entry" :is="entry.icon" class="slot-icon-actual" />
        <span v-else class="slot-icon-plus">+</span>
      </div>

      <div class="slot-content">
        <div class="slot-title">{{ title }}</div>
        <div class="slot-desc">{{ description || '暂无接入内容' }}</div>
      </div>
    </button>

    <button
      v-if="showPinToDashboard && entry"
      type="button"
      class="slot-pin"
      title="固定到控制台首页"
      @click.stop="emit('pinToDashboard', entry.route)"
    >
      <pushpin-outlined class="slot-pin-icon" />
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { PushpinOutlined } from '@ant-design/icons-vue'
// 注意：请确保路径与您的项目实际路径一致
import type { EntryAccent, ManagementEntry } from './management.ts'

const props = withDefaults(
  defineProps<{
    entry?: ManagementEntry
    title: string
    description?: string
    accent?: EntryAccent
    variant?: 'default' | 'compact'
    disabled?: boolean
    showPinToDashboard?: boolean
  }>(),
  {
    accent: 'primary',
    variant: 'default',
    disabled: false,
    showPinToDashboard: false,
  },
)

const emit = defineEmits<{
  navigate: [route: string]
  pinToDashboard: [route: string]
}>()

const buttonDisabled = computed(() => props.disabled || !props.entry)

const handleClick = () => {
  if (buttonDisabled.value || !props.entry) return
  emit('navigate', props.entry.route)
}
</script>

<style scoped>
/* 核心变量定义 - 建议在全局主题文件中配置，此处为局部回退 */
.slot-wrapper {
  --primary: #3b82f6;
  --bg-card: #ffffff;
  --text-heading: #1e293b;
  --text-muted: #64748b;
  --border-subtle: #f1f5f9;
  
  min-width: 0;
  position: relative;
  display: block;
}

/* 1. 卡片主体 */
.slot-card {
  /* 动态配色逻辑 */
  --accent-color: var(--primary);
  
  width: 100%;
  padding: 24px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  text-align: left;
  background: var(--bg-card);
  border: 1px solid color-mix(in srgb, var(--accent-color) 8%, var(--border-subtle));
  border-radius: 24px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

/* 2. 图标设计 */
.slot-icon-container {
  position: relative;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  color: var(--accent-color);
  transition: all 0.3s ease;
  z-index: 1;
}

.slot-icon-bg {
  position: absolute;
  inset: 0;
  background: color-mix(in srgb, var(--accent-color) 10%, transparent);
  border-radius: 16px;
  transition: all 0.3s ease;
  z-index: -1;
}

.slot-icon-actual {
  font-size: 22px;
  width: 22px;
  height: 22px;
}

/* 3. 文字内容 */
.slot-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-heading);
  margin-bottom: 6px;
  letter-spacing: -0.01em;
}

.slot-desc {
  font-size: 13px;
  color: var(--text-muted);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 4. 悬浮状态 (Hover) */
.slot-card:not(:disabled):hover {
  transform: translateY(-4px);
  border-color: color-mix(in srgb, var(--accent-color) 30%, transparent);
  box-shadow: 
    0 20px 25px -5px rgba(0, 0, 0, 0.05),
    0 8px 10px -6px color-mix(in srgb, var(--accent-color) 12%, transparent);
}

.slot-card:not(:disabled):hover .slot-icon-bg {
  background: var(--accent-color);
  transform: scale(1.05);
}

.slot-card:not(:disabled):hover .slot-icon-container {
  color: #ffffff;
}

/* 5. 固定按钮 (Pin) */
.slot-pin {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 32px;
  height: 32px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  border: 1px solid var(--border-subtle);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  opacity: 0;
  transform: scale(0.9);
  transition: all 0.2s ease;
  z-index: 10;
}

.slot-wrapper:hover .slot-pin {
  opacity: 1;
  transform: scale(1);
}

.slot-pin:hover {
  background: var(--accent-color);
  color: white;
  border-color: var(--accent-color);
}

/* 6. 紧凑模式 */
.slot-card--compact {
  padding: 16px;
  border-radius: 20px;
}
.slot-card--compact .slot-icon-container {
  width: 40px;
  height: 40px;
  margin-bottom: 12px;
}
.slot-card--compact .slot-icon-bg { border-radius: 12px; }
.slot-card--compact .slot-title { font-size: 14px; }

/* 7. 配色方案映射 */
.slot-card[data-accent='cyan'] { --accent-color: #06b6d4; }
.slot-card[data-accent='blue'] { --accent-color: #3b82f6; }
.slot-card[data-accent='mint'] { --accent-color: #10b981; }
.slot-card[data-accent='coral'] { --accent-color: #f43f5e; }
.slot-card[data-accent='indigo'] { --accent-color: #6366f1; }
/* ... 其他配色依此类推 */

.slot-card:disabled {
  cursor: not-allowed;
  opacity: 0.6;
  filter: grayscale(0.5);
}
</style>