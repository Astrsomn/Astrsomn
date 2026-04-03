<template>
  <section
    class="glass-card dash-mod dash-mod--console-load load-panel"
    :data-col-tier="gridColTier(gridW)"
    :data-row-tier="gridRowTier(gridH)"
  >
    <div class="load-head">
      <h3 class="load-title">系统负载</h3>
      <span class="load-stable">
        <span class="load-dot" />
        稳定运行
      </span>
    </div>

    <div class="load-throughput">
      <div>
        <p class="load-big">
          1.2k <span class="load-big-unit">req/s</span>
        </p>
        <p class="load-sub">当前吞吐量</p>
      </div>
      <div class="load-bars" aria-hidden="true">
        <div class="load-bar load-bar--1" />
        <div class="load-bar load-bar--2" />
        <div class="load-bar load-bar--3" />
        <div class="load-bar load-bar--4" />
        <div class="load-bar load-bar--5" />
        <div class="load-bar load-bar--6" />
        <div class="load-bar load-bar--7" />
      </div>
    </div>

    <div class="load-mini-grid">
      <div class="load-mini">
        <p class="load-mini-label">平均延迟</p>
        <p class="load-mini-val">124ms</p>
      </div>
      <div class="load-mini">
        <p class="load-mini-label">错误率</p>
        <p class="load-mini-val load-mini-val--ok">0.02%</p>
      </div>
    </div>

    <div class="load-cpu">
      <p class="load-cpu-label">CPU 使用率</p>
      <div class="progress-bar">
        <div class="progress-fill progress-fill--cpu" :style="{ width: '35%' }" />
      </div>
      <p class="load-cpu-pct">35%</p>
    </div>
  </section>
</template>

<script setup lang="ts">
import { gridColTier, gridRowTier } from '../dashboardSizeTier'

defineProps<{ gridW: number; gridH: number; editMode?: boolean }>()
</script>

<style scoped>
.load-panel {
  padding: 20px;
  height: 100%;
  box-sizing: border-box;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.dash-mod--console-load[data-row-tier='1'] .load-mini-grid,
.dash-mod--console-load[data-row-tier='1'] .load-cpu,
.dash-mod--console-load[data-row-tier='1'] .load-bars {
  display: none;
}

.dash-mod--console-load[data-row-tier='2'] .load-cpu {
  display: none;
}

.dash-mod--console-load[data-col-tier='1'] .load-big {
  font-size: 1.125rem;
}

.dash-mod--console-load[data-col-tier='1'] .load-head {
  margin-bottom: 12px;
}

.load-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.load-title {
  margin: 0;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--text-muted);
}

.load-stable {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 10px;
  font-weight: 800;
  color: var(--success);
}

.load-dot {
  width: 6px;
  height: 6px;
  border-radius: 999px;
  background: var(--success);
}

.load-throughput {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.load-big {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--text-heading);
}

.load-big-unit {
  font-size: 0.75rem;
  font-weight: 500;
  color: var(--text-muted);
}

.load-sub {
  margin: 4px 0 0;
  font-size: 10px;
  color: var(--text-muted);
}

.load-bars {
  display: flex;
  align-items: flex-end;
  gap: 3px;
  height: 40px;
}

.load-bar {
  width: 6px;
  border-radius: 3px 3px 0 0;
  background: color-mix(in srgb, var(--primary) 65%, #3b82f6);
}

.load-bar--1 {
  height: 40%;
  opacity: 0.35;
}
.load-bar--2 {
  height: 60%;
  opacity: 0.45;
}
.load-bar--3 {
  height: 50%;
  opacity: 0.55;
}
.load-bar--4 {
  height: 80%;
  opacity: 0.7;
}
.load-bar--5 {
  height: 70%;
  opacity: 0.8;
}
.load-bar--6 {
  height: 90%;
  opacity: 0.95;
}
.load-bar--7 {
  height: 60%;
  opacity: 0.65;
}

.load-mini-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.load-mini {
  padding: 12px;
  border-radius: 12px;
  border: 1px solid var(--border-subtle);
  background: color-mix(in srgb, var(--text-muted) 8%, var(--bg-card));
}

.load-mini-label {
  margin: 0 0 6px;
  font-size: 10px;
  font-weight: 700;
  color: var(--text-muted);
}

.load-mini-val {
  margin: 0;
  font-size: 0.875rem;
  font-weight: 800;
  color: var(--text-heading);
}

.load-mini-val--ok {
  color: var(--success);
}

.load-cpu {
  margin-top: 16px;
}

.load-cpu-label {
  margin: 0 0 8px;
  font-size: 10px;
  font-weight: 700;
  color: var(--text-muted);
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

.progress-fill--cpu {
  background: linear-gradient(90deg, #22c55e, #059669);
}

.load-cpu-pct {
  margin: 4px 0 0;
  font-size: 10px;
  color: var(--text-muted);
  text-align: right;
}
</style>
