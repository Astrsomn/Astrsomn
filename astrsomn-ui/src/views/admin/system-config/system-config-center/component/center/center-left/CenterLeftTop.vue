<template>
  <div class="panel-card module-panel">
    <div class="card-grid">
      <button
        v-for="(item, index) in moduleCards"
        :key="item.routeName"
        :class="['route-card', `theme-${index % 4 + 1}`]"
        type="button"
        @click="emit('go-to', item.routeName)"
      >
        <div class="card-content">
          <div class="icon-wrapper">
            <component :is="item.icon" />
          </div>
          <div class="text-group">
            <span class="title-text">{{ item.title }}</span>
            <span v-if="item.desc" class="desc-text">{{ item.desc }}</span>
          </div>
        </div>
        <div class="hover-glow"></div>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Component } from 'vue'

type ModuleCard = {
  title: string
  desc: string
  routeName: string
  icon: Component
}

defineProps<{
  moduleCards: ModuleCard[]
}>()

const emit = defineEmits<{
  (e: 'go-to', routeName: string): void
}>()
</script>

<style scoped>
/* 保持原始容器布局 */
.panel-card {
  border-radius: 14px;
  background: var(--bg-card, #ffffff);
  padding: 14px 16px;
}

.module-panel {
  width: 100%;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px; /* 保持你的原始间距 */
}

/* 核心卡片重构 */
.route-card {
  position: relative;
  min-height: 80px; /* 严格遵守原始高度 */
  padding: 12px 16px;
  background: #f8fafc; /* 极浅灰底色，干净利落 */
  border: 1px solid transparent; /* 默认隐藏边框 */
  border-radius: 12px;
  text-align: left;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  display: flex;
  align-items: center;
  outline: none;
}

.card-content {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

/* 图标容器：改用现代面性色块 */
.icon-wrapper {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: #ffffff;
  color: var(--theme-color);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.icon-wrapper :deep(.anticon) {
  font-size: 20px;
}

/* 文字排版 */
.text-group {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.title-text {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.desc-text {
  margin-top: 2px;
  font-size: 12px;
  color: #64748b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 现代配色方案 - 纯色点缀 */
.theme-1 { --theme-color: #3b82f6; --hover-bg: rgba(59, 130, 246, 0.05); }
.theme-2 { --theme-color: #10b981; --hover-bg: rgba(16, 185, 129, 0.05); }
.theme-3 { --theme-color: #f59e0b; --hover-bg: rgba(245, 158, 11, 0.05); }
.theme-4 { --theme-color: #8b5cf6; --hover-bg: rgba(139, 92, 246, 0.05); }

/* 交互动效 */
.route-card:hover {
  transform: translateY(-2px);
  background: var(--hover-bg);
  border-color: var(--theme-color);
}

.route-card:hover .icon-wrapper {
  background: var(--theme-color);
  color: #ffffff;
  transform: scale(1.05);
}

/* 背景光斑：鼠标移入时淡淡的品牌色漫反射 */
.hover-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 100px;
  height: 100px;
  background: var(--theme-color);
  filter: blur(40px);
  opacity: 0;
  transform: translate(-50%, -50%);
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.route-card:hover .hover-glow {
  opacity: 0.1;
}

/* 响应式适配（保持原始逻辑） */
@media (max-width: 1200px) {
  .card-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
}

@media (max-width: 900px) {
  .card-grid { grid-template-columns: 1fr; }
}
</style>