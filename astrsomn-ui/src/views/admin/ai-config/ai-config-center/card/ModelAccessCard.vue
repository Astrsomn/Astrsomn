<template>
  <div class="model-access-card clickable" @click="navigateTo('/admin/ai-config/models')">
    <div class="card-header">
      <div class="card-icon blue-icon">
        <DatabaseOutlined />
      </div>
      <div class="card-title">模型接入</div>
      <div class="card-stats">
        <div class="stat-item">
          <CheckCircleOutlined class="stat-icon enabled" />
          <span class="stat-value">{{ enabledCount }}</span>
        </div>
 
      </div>
    </div>
    <p class="card-desc">定义供应商 API 路径与能力映射</p>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { DatabaseOutlined, CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons-vue'

const router = useRouter()

// 模拟数据
const enabledCount = 12
const disabledCount = 3

const navigateTo = (path: string) => {
  router.push(path)
}
</script>

<style scoped>
.model-access-card {
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

.model-access-card:hover {
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
  background: var(--primary-hover);
  color: var(--primary);
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