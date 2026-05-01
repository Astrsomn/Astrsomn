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
  background: #ffffff;
  border-radius: 10px;
  padding: 14px 16px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  overflow: hidden;
  transition: all 0.2s ease;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  }

  .card-inner {
    display: flex;
    justify-content: space-between;
    align-items: center;
    min-height: 100%;
    position: relative;
    z-index: 2;
  }

  /* 文字侧 */
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
      color: #1e293b;
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

  /* 图标侧 */
  .icon-side {
    .icon-box {
      flex-shrink: 0;
      width: 48px;
      height: 48px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 22px;
    }
  }

  /* 与 system-config 快捷入口 icon-1 ~ 4 色板一致 */
  &.blue .icon-box { background: #dbeafe; color: #3b82f6; }
  &.purple .icon-box { background: #ede9fe; color: #8b5cf6; }
  &.amber .icon-box { background: #fef3c7; color: #f59e0b; }
  
  &.dark {
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    .label { color: var(--text-muted); }
    .value { color: #1e293b; }
    .icon-box { background: #f1f5f9; color: #1e293b; }
  }
}

/* 徽章与状态 */
.mini-badge {
  font-size: 10px;
  padding: 0 6px;
  border-radius: 10px;
  margin-left: 8px;
  font-weight: 700;
  
  &.positive { background: rgba(82, 196, 26, 0.12); color: #52c41a; border: 1px solid rgba(82, 196, 26, 0.3); }
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
    &.green { background: #52c41a; }
    &.pulse { 
      background: #52c41a; 
      box-shadow: 0 0 0 rgba(82, 196, 26, 0.4);
      animation: pulse 2s infinite;
    }
  }
}

/* 装饰元素 */
.progress-decor {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: var(--border-default);
  .progress-fill { height: 100%; background: #a855f7; border-radius: 10px; }
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
  0% { box-shadow: 0 0 0 0 rgba(82, 196, 26, 0.7); }
  70% { box-shadow: 0 0 0 6px rgba(82, 196, 26, 0); }
  100% { box-shadow: 0 0 0 0 rgba(82, 196, 26, 0); }
}

@media (max-width: 1200px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>