<template>
  <section class="stats-grid">
    <div class="stat-card stat-card--blue">
      <div class="stat-info">
        <p class="stat-label">当前在线服务</p>
        <h3 class="stat-value">24 <span class="stat-unit">个节点</span></h3>
      </div>
      <i class="fa-solid fa-satellite-dish stat-icon"></i>
    </div>
    <div class="stat-card stat-card--green">
      <div class="stat-info">
        <p class="stat-label">今日消耗 Token</p>
        <h3 class="stat-value">1.2M <span class="stat-unit">Tokens</span></h3>
      </div>
      <i class="fa-solid fa-chart-line stat-icon"></i>
    </div>
    <div class="stat-card stat-card--amber">
      <div class="stat-info">
        <p class="stat-label">平均响应延迟</p>
        <h3 class="stat-value">842 <span class="stat-unit">ms</span></h3>
      </div>
      <i class="fa-solid fa-bolt stat-icon"></i>
    </div>
    <div class="stat-card stat-card--rose">
      <div class="stat-info">
        <p class="stat-label">阻断异常请求</p>
        <h3 class="stat-value">12 <span class="stat-unit">次拦截</span></h3>
      </div>
      <i class="fa-solid fa-shield-virus stat-icon"></i>
    </div>
  </section>
</template>

<style scoped>
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border: 1px solid;
}

.stat-card--blue {
  background: #eff6ff;
  border-color: #dbeafe;
}

.stat-card--green {
  background: #ecfdf5;
  border-color: #d1fae5;
}

.stat-card--amber {
  background: #fffbeb;
  border-color: #fef3c7;
}

.stat-card--rose {
  background: #fff1f2;
  border-color: #ffe4e6;
}

.stat-label {
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0;
}

.stat-card--blue .stat-label { color: #2563eb; }
.stat-card--green .stat-label { color: #059669; }
.stat-card--amber .stat-label { color: #d97706; }
.stat-card--rose .stat-label { color: #e11d48; }

.stat-value {
  font-size: 24px;
  font-weight: 900;
  margin: 4px 0 0;
}

.stat-card--blue .stat-value { color: #1e3a8a; }
.stat-card--green .stat-value { color: #064e3b; }
.stat-card--amber .stat-value { color: #92400e; }
.stat-card--rose .stat-value { color: #881337; }

.stat-unit {
  font-size: 14px;
  font-weight: normal;
}

.stat-icon {
  font-size: 48px;
  opacity: 0.2;
}

.stat-card--blue .stat-icon { color: #3b82f6; }
.stat-card--green .stat-icon { color: #10b981; }
.stat-card--amber .stat-icon { color: #f59e0b; }
.stat-card--rose .stat-icon { color: #f43f5e; }
</style>
