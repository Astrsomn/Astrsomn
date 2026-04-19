<template>
  <div class="node-connector">
    <div class="model-card">
      <div class="card-header">
        <span class="card-label">Model</span>
        <ApartmentOutlined class="card-icon" />
      </div>
      <div class="card-title">GPT-4o</div>
      <div class="card-subtitle">128k context</div>
      <div class="card-overlay">
        <button class="overlay-btn" title="选择模型库">
          <SearchOutlined />
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ApartmentOutlined, SearchOutlined } from '@ant-design/icons-vue';
</script>

<style scoped>
.node-connector {
  flex: 1;
  position: relative;
}

.model-card {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-left: 4px solid var(--primary);
  border-radius: var(--radius-xl);
  padding: 16px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.model-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-card);
  border-color: var(--primary);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.card-label {
  font-size: 10px;
  font-weight: bold;
  color: var(--primary);
  text-transform: uppercase;
}

.card-icon {
  color: rgba(59, 130, 246, 0.3);
  font-size: 16px;
}

.card-title {
  font-weight: bold;
  color: var(--text-primary);
}

.card-subtitle {
  font-size: 10px;
  color: var(--text-secondary);
  margin-top: 4px;
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
  width: 32px;
  height: 32px;
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
  color: var(--primary-light);
}

.overlay-btn .anticon {
  font-size: 14px;
}
</style>