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
            <span class="value font-mono">80%</span>
            <span class="unit">ms</span>
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
  gap: 20px;
  margin-bottom: 24px;
}

.compact-stat-card {
  position: relative;
  background: var(--bg-card);
  border-radius: 16px;
  padding: 20px 24px;
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-card);
  overflow: hidden;
  transition: all 0.3s ease;
  min-height: 120px;

  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-card);
  }

  .card-inner {
    display: flex;
    justify-content: space-between;
    align-items: center;
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
      font-size: 24px;
      font-weight: 800;
      color: var(--text-heading);
      line-height: 1.2;
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
      width: 44px;
      height: 44px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 22px;
    }
  }

  /* 各色卡片细节 */
  &.blue .icon-box { background: var(--primary-hover); color: var(--primary); }
  &.purple .icon-box { background: rgba(114, 46, 209, 0.15); color: #a855f7; }
  &.amber .icon-box { background: rgba(250, 204, 21, 0.15); color: #f59e0b; }
  
  &.dark {
    background: var(--bg-base);
    border: 1px solid var(--border-default);
    .label { color: var(--text-muted); }
    .value { color: var(--text-heading); }
    .icon-box { background: var(--bg-elevated); color: var(--text-primary); }
  }
}

/* 徽章与状态 */
.mini-badge {
  font-size: 10px;
  padding: 0 6px;
  border-radius: 4px;
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
  .progress-fill { height: 100%; background: #a855f7; border-radius: 0 2px 2px 0; }
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