<template>
  <div class="tool-card clickable" @click="navigateTo('/admin/tools')">
    <div class="card-header">
      <div class="card-icon teal-icon">
        <ToolOutlined />
      </div>
      <div class="card-title">工具插件</div>
      <div class="card-stats">
        <div class="stat-item">
          <CheckCircleOutlined class="stat-icon enabled" />
          <span class="stat-value">{{ enabledCount }}</span>
        </div>
  
      </div>
    </div>
    <p class="card-desc">维护函数调用与 API 工具</p>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ToolOutlined, CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons-vue'

const router = useRouter()

// 模拟数据
const enabledCount = 20
const disabledCount = 5

const navigateTo = (path: string) => {
  router.push(path)
}
</script>

<style scoped>
.tool-card {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: 12px;
  padding: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0.2, 0.2, 1);
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
  min-height: 80px;
}

.tool-card:hover {
  box-shadow: var(--shadow-card);
  border-color: var(--primary);
  transform: translateY(-1px);
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  position: relative;
  flex: 0 0 auto;
}

.card-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  margin-right: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  background: rgba(20, 184, 166, 0.15);
  color: #14b8a6;
  flex-shrink: 0;
}

.card-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  flex: 1;
}

.card-desc {
  font-size: 10px;
  color: var(--text-muted);
  margin: 0;
  line-height: 1.3;
  flex: 1;
  padding-right: 80px;
}

.card-stats {
  position: absolute;
  top: 0;
  right: 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-icon {
  font-size: 14px;
}

.stat-icon.enabled {
  color: #10b981;
}

.stat-icon.disabled {
  color: #ef4444;
}

.stat-value {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1;
}

.clickable {
  cursor: pointer;
}
</style>