<template>
  <button type="button" class="glass-card glass-card--interactive bento-span-4-2 inst-panel" @click="go">
    <div class="inst-top">
      <div class="inst-icon-wrap">
        <control-outlined class="inst-icon" />
      </div>
      <span class="status-badge">多预设可用</span>
    </div>
    <h2 class="inst-title">推理实例配置</h2>
    <p class="inst-desc">管理对话 / 向量 / 图像等推理实例与采样、Token 等运行参数。</p>

    <div class="inst-bottom">
      <div class="progress-bar">
        <div class="progress-fill progress-fill--inst" :style="{ width: '62%' }" />
      </div>
      <div class="inst-capacity">
        <span>实例池占用</span>
        <span>62% Capacity</span>
      </div>

      <div class="inst-stats">
        <div class="inst-stat">
          <p class="inst-stat-label">对话实例</p>
          <p class="inst-stat-val">8</p>
        </div>
        <div class="inst-stat">
          <p class="inst-stat-label">向量实例</p>
          <p class="inst-stat-val inst-stat-val--amber">4</p>
        </div>
      </div>
    </div>
  </button>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ControlOutlined } from '@ant-design/icons-vue'

const router = useRouter()

const go = () => {
  void router.push('/admin/ai-instance')
}
</script>

<style scoped>
.inst-panel {
  padding: 24px;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  text-align: left;
  border: none;
  font: inherit;
  color: inherit;
}

.inst-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.inst-icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0d9488, #0891b2);
  color: #fff;
  box-shadow: 0 4px 12px rgba(13, 148, 136, 0.35);
}

.inst-icon {
  font-size: 22px;
}

.status-badge {
  font-size: 0.7rem;
  padding: 2px 8px;
  border-radius: 99px;
  background: color-mix(in srgb, var(--primary) 12%, transparent);
  color: color-mix(in srgb, var(--primary) 85%, #3b82f6);
  font-weight: 700;
}

.inst-title {
  margin: 0 0 4px;
  font-size: 1.25rem;
  font-weight: 800;
  color: var(--text-heading);
}

.inst-desc {
  margin: 0 0 24px;
  font-size: 0.875rem;
  color: var(--text-muted);
  line-height: 1.5;
}

.inst-bottom {
  margin-top: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.progress-bar {
  height: 8px;
  border-radius: 4px;
  overflow: hidden;
  background: color-mix(in srgb, var(--text-muted) 14%, transparent);
}

.progress-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.5s ease;
}

.progress-fill--inst {
  background: linear-gradient(90deg, #0d9488, #06b6d4);
  box-shadow: 0 0 12px rgba(13, 148, 136, 0.4);
}

.inst-capacity {
  display: flex;
  justify-content: space-between;
  font-size: 10px;
  font-weight: 800;
  color: var(--text-muted);
}

.inst-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-top: 8px;
}

.inst-stat {
  padding: 12px;
  border-radius: 12px;
  border: 1px solid var(--border-subtle);
  background: color-mix(in srgb, var(--text-muted) 8%, var(--bg-card));
}

.inst-stat-label {
  margin: 0 0 6px;
  font-size: 10px;
  font-weight: 700;
  color: var(--text-muted);
}

.inst-stat-val {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 800;
  color: var(--text-heading);
}

.inst-stat-val--amber {
  color: #d97706;
}
</style>
