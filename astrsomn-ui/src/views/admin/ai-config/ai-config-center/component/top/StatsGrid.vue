<template>
  <section class="stats-grid">
    <div class="compact-stat-card blue">
      <div class="card-inner">
        <div class="info-side">
          <p class="label">实时在线节点</p>
          <div class="value-row">
            <span class="value">24</span>
            <span class="unit">Nodes</span>
            <div class="mini-badge positive">+12.5%</div>
          </div>
        </div>
        <div class="icon-side">
          <div class="icon-box">
            <ClusterOutlined />
          </div>
        </div>
      </div>
      <div class="wave-decor"></div>
    </div>

    <div class="compact-stat-card purple">
      <div class="card-inner">
        <div class="info-side">
          <p class="label">今日算力消耗</p>
          <div class="value-row">
            <span class="value">1.2</span>
            <span class="unit">M</span>
            <div class="mini-badge stable">Stable</div>
          </div>
        </div>
        <div class="icon-side">
          <div class="icon-box">
            <AppstoreOutlined />
          </div>
        </div>
      </div>
      <div class="progress-decor">
        <div class="progress-fill" style="width: 65%"></div>
      </div>
    </div>

    <div class="compact-stat-card amber">
      <div class="card-inner">
        <div class="info-side">
          <p class="label">任务成功率</p>
          <div class="value-row">
            <span class="value font-mono">80</span>
            <span class="unit">%</span>
          </div>
          <div class="status-text">
            <span class="dot green"></span> Optimal
          </div>
        </div>
        <div class="icon-side">
          <div class="icon-box">
            <ThunderboltOutlined />
          </div>
        </div>
      </div>
    </div>

    <div class="compact-stat-card dark">
      <div class="card-inner">
        <div class="info-side">
          <p class="label">安全策略拦截</p>
          <div class="value-row">
            <span class="value">12</span>
          </div>
          <div class="status-text">
            <span class="dot pulse"></span> ACTIVE GUARD
          </div>
        </div>
        <div class="icon-side">
          <div class="icon-box">
            <SecurityScanOutlined />
          </div>
        </div>
      </div>
      <CheckCircleOutlined class="bg-watermark" />
    </div>
  </section>
</template>

<script setup lang="ts">
import { 
  ClusterOutlined, 
  AppstoreOutlined,
  ThunderboltOutlined, 
  SecurityScanOutlined,
  CheckCircleOutlined,
} from '@ant-design/icons-vue'
</script>

<style scoped lang="less">
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  margin: 6px 0;
}

.compact-stat-card {
  position: relative;
  box-sizing: border-box;
  min-height: 88px;
  background: var(--bg-card);
  border-radius: var(--radius-md);
  padding: 14px 16px;
  border: 1px solid var(--border-default);
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  overflow: hidden;
  transition: all 0.2s ease;

  &:hover {
    transform: translateY(-1px);
    box-shadow: var(--shadow-card);
  }

  .card-inner {
    display: flex;
    justify-content: space-between;
    align-items: center;
    min-height: 100%;
    position: relative;
    z-index: 2;
  }

  .info-side {
    .label {
      font-size: 12px;
      color: var(--text-muted);
      margin-bottom: 4px;
      font-weight: 500;
    }
    
    .value-row {
      display: flex;
      align-items: baseline;
      gap: 4px;
    }

    .value {
      font-size: 22px;
      font-weight: 800;
      color: var(--text-heading);
      line-height: 1.15;
    }

    .unit {
      font-size: 12px;
      color: var(--text-muted);
      font-weight: 600;
    }

    .font-mono {
      font-family: 'JetBrains Mono', monospace;
    }
  }

  .icon-side {
    .icon-box {
      flex-shrink: 0;
      width: 48px;
      height: 48px;
      border-radius: var(--radius-md);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 22px;
    }
  }

  &.blue .icon-box { background: rgba(59, 130, 246, 0.15); color: var(--primary); }
  &.purple .icon-box { background: rgba(139, 92, 246, 0.15); color: #a78bfa; }
  &.amber .icon-box { background: rgba(245, 158, 11, 0.15); color: #fb923c; }
  
  &.dark {
    background: var(--bg-input);
    border: 1px solid var(--border-default);
    .label { color: var(--text-muted); }
    .value { color: var(--text-heading); }
    .icon-box { background: var(--bg-elevated); color: var(--text-secondary); }
  }
}

.mini-badge {
  font-size: 10px;
  padding: 0 6px;
  border-radius: 10px;
  margin-left: 8px;
  font-weight: 700;
  
  &.positive { background: rgba(16, 185, 129, 0.12); color: var(--success); border: 1px solid rgba(16, 185, 129, 0.3); }
  &.stable { background: var(--bg-input); color: var(--text-muted); border: 1px solid var(--border-default); }
}

.status-text {
  font-size: 10px;
  color: var(--text-muted);
  font-weight: 700;
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 4px;

  .dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    &.green { background: var(--success); }
    &.pulse { 
      background: var(--success); 
      box-shadow: 0 0 0 rgba(16, 185, 129, 0.4);
      animation: pulse 2s infinite;
    }
  }
}

.progress-decor {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: var(--border-default);
  .progress-fill { height: 100%; background: #a78bfa; border-radius: 10px; }
}

.bg-watermark {
  position: absolute;
  right: -10px;
  bottom: -10px;
  font-size: 64px;
  color: var(--border-subtle);
  z-index: 1;
}

@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7); }
  70% { box-shadow: 0 0 0 6px rgba(16, 185, 129, 0); }
  100% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0); }
}

@media (max-width: 1200px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>