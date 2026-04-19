<template>
  <div class="node-connector">
    <div class="account-card">
      <div class="card-header">
        <span class="card-label">Account</span>
        <KeyOutlined class="card-icon" />
      </div>
      <div class="card-title">OpenAI_Personal</div>
      <div class="card-subtitle">余额充足</div>
      <div class="card-overlay">
        <button class="overlay-btn" title="切换账号">
          <UnorderedListOutlined />
        </button>
        <button class="overlay-btn" title="添加新账号">
          <PlusOutlined />
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { KeyOutlined, UnorderedListOutlined, PlusOutlined } from '@ant-design/icons-vue';
</script>

<style scoped>
.node-connector {
  flex: 1;
  position: relative;
}

.account-card {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-left: 4px solid #f97316;
  border-radius: var(--radius-xl);
  padding: 16px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.account-card:hover {
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
  color: #f97316;
  text-transform: uppercase;
}

.card-icon {
  color: rgba(249, 115, 22, 0.3);
  font-size: 16px;
}

.card-title {
  font-weight: bold;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
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

.account-card:hover .card-overlay {
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