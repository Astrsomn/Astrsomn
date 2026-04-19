<template>
  <div class="image-card">
    <div class="card-header">
      <div class="header-left">
        <div class="icon-badge">
          <PictureOutlined />
        </div>
        <h3 class="card-title">多模态增强</h3>
      </div>
      <button class="add-btn">
        <PlusCircleOutlined />
      </button>
    </div>
    <div class="model-grid">
      <div class="model-item">
        <div class="model-info">
          <PictureOutlined class="model-icon" />
          <span class="model-name">DALL-E 3</span>
        </div>
        <RightOutlined class="model-arrow" />
      </div>
      <div class="model-item">
        <div class="model-info">
          <AudioOutlined class="model-icon audio" />
          <span class="model-name">TTS-1</span>
        </div>
        <RightOutlined class="model-arrow" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { PictureOutlined, PlusCircleOutlined, RightOutlined, AudioOutlined } from '@ant-design/icons-vue';
</script>

<style scoped>
.image-card {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-xl);
  padding: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.image-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-card);
  border-color: var(--primary);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.icon-badge {
  width: 32px;
  height: 32px;
  background: rgba(236, 72, 153, 0.1);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ec4899;
  box-shadow: var(--shadow-sm);
}

.icon-badge .anticon {
  font-size: 14px;
}

.card-title {
  font-weight: bold;
  color: var(--text-primary);
  margin: 0;
}

.add-btn {
  color: #ec4899;
  padding: 4px;
  border-radius: var(--radius-md);
  background: none;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
}

.add-btn:hover {
  background: rgba(236, 72, 153, 0.1);
}

.add-btn .anticon {
  font-size: 16px;
}

.model-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.model-item {
  padding: 12px;
  background: var(--bg-elevated);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  transition: all 0.2s;
}

.model-item:hover {
  border-color: rgba(236, 72, 153, 0.5);
}

.model-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.model-icon {
  color: #ec4899;
  font-size: 14px;
}

.model-icon.audio {
  color: #f97316;
}

.model-name {
  font-size: 11px;
  font-weight: bold;
  color: var(--text-primary);
}

.model-arrow {
  font-size: 10px;
  color: var(--text-tertiary);
}
</style>