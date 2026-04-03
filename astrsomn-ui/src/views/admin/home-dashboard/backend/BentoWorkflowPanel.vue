<template>
  <button type="button" class="glass-card glass-card--interactive bento-span-4-2 wf-panel" @click="go">
    <div class="wf-top">
      <div class="wf-icon-wrap">
        <node-index-outlined class="wf-icon" />
      </div>
      <span class="status-badge">12个流正在运行</span>
    </div>
    <h2 class="wf-title">工作流引擎</h2>
    <p class="wf-desc">基于 DAG 的可视化任务编排系统。</p>

    <div class="wf-bottom">
      <div class="progress-bar">
        <div class="progress-fill progress-fill--wf" :style="{ width: '70%' }" />
      </div>
      <div class="wf-capacity">
        <span>系统吞吐量</span>
        <span>70% Capacity</span>
      </div>

      <div class="wf-stats">
        <div class="wf-stat">
          <p class="wf-stat-label">活跃工作流</p>
          <p class="wf-stat-val">12</p>
        </div>
        <div class="wf-stat">
          <p class="wf-stat-label">待执行</p>
          <p class="wf-stat-val wf-stat-val--amber">3</p>
        </div>
      </div>
    </div>
  </button>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { NodeIndexOutlined } from '@ant-design/icons-vue'

const router = useRouter()

const go = () => {
  void router.push('/admin/workflows')
}
</script>

<style scoped>
.wf-panel {
  padding: 24px;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  text-align: left;
  border: none;
  font: inherit;
  color: inherit;
}

.wf-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.wf-icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #6366f1, #9333ea);
  color: #fff;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.35);
}

.wf-icon {
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

.wf-title {
  margin: 0 0 4px;
  font-size: 1.25rem;
  font-weight: 800;
  color: var(--text-heading);
}

.wf-desc {
  margin: 0 0 24px;
  font-size: 0.875rem;
  color: var(--text-muted);
  line-height: 1.5;
}

.wf-bottom {
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

.progress-fill--wf {
  background: linear-gradient(90deg, #6366f1, #9333ea);
  box-shadow: 0 0 12px rgba(99, 102, 241, 0.4);
}

.wf-capacity {
  display: flex;
  justify-content: space-between;
  font-size: 10px;
  font-weight: 800;
  color: var(--text-muted);
}

.wf-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-top: 8px;
}

.wf-stat {
  padding: 12px;
  border-radius: 12px;
  border: 1px solid var(--border-subtle);
  background: color-mix(in srgb, var(--text-muted) 8%, var(--bg-card));
}

.wf-stat-label {
  margin: 0 0 6px;
  font-size: 10px;
  font-weight: 700;
  color: var(--text-muted);
}

.wf-stat-val {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 800;
  color: var(--text-heading);
}

.wf-stat-val--amber {
  color: #d97706;
}
</style>
