<template>
  <div class="node-connector">
    <div class="agent-card">
      <div class="card-header">
        <span class="card-label">Agent Name</span>
        <RobotOutlined class="card-icon" />
      </div>
      <div class="card-title">Astrsomn_v2</div>
      <div class="card-subtitle">Ready to sync</div>
      <div class="card-overlay">
        <button class="overlay-btn" title="修改基本信息">
          <EditOutlined />
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { RobotOutlined, EditOutlined } from '@ant-design/icons-vue';
</script>

<style scoped>
.node-connector {
  flex: 1;
  position: relative;
}

.agent-card {
  background: var(--primary-gradient);
  border-radius: var(--radius-xl);
  padding: 16px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: var(--shadow-overview);
  color: white;
  cursor: pointer;
}

.agent-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-card);
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
  color: rgba(255, 255, 255, 0.8);
  text-transform: uppercase;
}

.card-icon {
  color: rgba(255, 255, 255, 0.5);
  font-size: 16px;
}

.card-title {
  font-weight: bold;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-subtitle {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.7);
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

.agent-card:hover .card-overlay {
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
  background: white;
  color: var(--primary);
  border: none;
  cursor: pointer;
  transition: all 0.2s;
}

.overlay-btn:hover {
  transform: scale(1.1);
}

.overlay-btn .anticon {
  font-size: 14px;
}
</style>