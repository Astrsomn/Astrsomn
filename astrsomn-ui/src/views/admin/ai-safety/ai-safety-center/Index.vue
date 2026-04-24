<template>
  <AdminPageShell
    title="AI 安全治理中心"
    description="统一管理提示词安全、敏感词治理、审计追踪与风险响应流程。"
    empty-text="暂无可配置的安全能力。"
  >
    <div class="center-page">
      <section class="stats-grid">
        <article v-for="item in safetyStats" :key="item.label" class="stat-card">
          <div class="stat-head">
            <component :is="item.icon" class="stat-icon" />
            <span class="stat-label">{{ item.label }}</span>
          </div>
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-hint" :class="`trend-${item.trend}`">{{ item.hint }}</div>
        </article>
      </section>

      <section class="main-grid">
        <div class="left-column">
          <div class="panel-card">
            <div class="panel-head">
              <h3><SafetyCertificateOutlined /> 安全治理模块</h3>
              <span>进入核心治理能力</span>
            </div>
            <div class="card-grid">
              <button
                v-for="item in moduleCards"
                :key="item.routeName"
                class="route-card"
                type="button"
                @click="goTo(item.routeName)"
              >
                <div class="card-title">
                  <component :is="item.icon" />
                  {{ item.title }}
                </div>
                <div class="card-desc">{{ item.desc }}</div>
              </button>
            </div>
          </div>

          <div class="panel-card">
            <div class="panel-head">
              <h3><ApartmentOutlined /> 调用链路状态看板</h3>
              <span>成功 / 失败 / 进行中链路实时分布</span>
            </div>
            <div class="trace-overview-grid">
              <article v-for="item in traceStats" :key="item.label" class="trace-overview-item">
                <div class="trace-overview-head">
                  <component :is="item.icon" />
                  <span>{{ item.label }}</span>
                </div>
                <strong>{{ item.value }}</strong>
                <div class="trace-overview-hint">{{ item.hint }}</div>
              </article>
            </div>
            <div class="trace-detail-list">
              <article v-for="trace in traceCases" :key="trace.name" class="trace-detail-item">
                <div class="trace-detail-top">
                  <span class="trace-detail-name">{{ trace.name }}</span>
                  <span class="trace-detail-status" :class="`status-${trace.status}`">
                    <span class="status-dot"></span>
                    {{ trace.statusText }}
                  </span>
                </div>
                <div class="trace-detail-meta">
                  <span>平均耗时 {{ trace.cost }}</span>
                  <span>最近更新 {{ trace.updatedAt }}</span>
                </div>
              </article>
            </div>
          </div>
        </div>

        <div class="right-column">
          <div class="panel-card">
            <div class="panel-head">
              <h3><AlertOutlined /> 最新风险事件</h3>
              <span>按优先级快速响应</span>
            </div>
            <div class="event-list">
              <article v-for="event in latestEvents" :key="event.title" class="event-item">
                <div class="event-title-row">
                  <span class="event-title">{{ event.title }}</span>
                  <span class="event-level" :class="`level-${event.level}`">{{ event.levelText }}</span>
                </div>
                <div class="event-meta">{{ event.source }} · {{ event.time }}</div>
              </article>
            </div>
          </div>

        </div>
      </section>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import {
  AlertOutlined,
  ApartmentOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  CloseCircleOutlined,
  DatabaseOutlined,
  FileSearchOutlined,
  SafetyCertificateOutlined,
  SecurityScanOutlined,
  WarningOutlined
} from '@ant-design/icons-vue'
import type { Component } from 'vue'
import { useRouter } from 'vue-router'
import AdminPageShell from '@/components/home/AdminPageShell.vue'

type ModuleCard = {
  title: string
  desc: string
  routeName: string
  icon: Component
}

type Trend = 'up' | 'down' | 'flat'

type SafetyStat = {
  label: string
  value: string
  hint: string
  trend: Trend
  icon: Component
}

const router = useRouter()

const moduleCards: ModuleCard[] = [
  {
    title: 'FTL 模板管理',
    desc: '管理提示词模板与输出模版。',
    routeName: 'AdminTemplates',
    icon: FileSearchOutlined
  },
  {
    title: '敏感词治理',
    desc: '维护敏感词策略与拦截规则。',
    routeName: 'AdminSecurity',
    icon: WarningOutlined
  },
  {
    title: '链路追踪',
    desc: '查看请求链路与审计日志。',
    routeName: 'AdminTracing',
    icon: DatabaseOutlined
  }
]

const safetyStats: SafetyStat[] = [
  { label: '策略总数', value: '72', hint: '本周新增 6 条', trend: 'up', icon: SecurityScanOutlined },
  { label: '今日拦截', value: '1,284', hint: '较昨日 +11%', trend: 'up', icon: AlertOutlined },
  { label: '误拦截率', value: '0.8%', hint: '较昨日 -0.2%', trend: 'down', icon: CheckCircleOutlined },
  { label: '待复核事件', value: '9', hint: '较昨日持平', trend: 'flat', icon: SafetyCertificateOutlined }
]

const traceStats = [
  { label: '已成功链路', value: '2,148', hint: '成功率 96.1%', icon: CheckCircleOutlined },
  { label: '失败链路', value: '87', hint: '失败率 3.9%', icon: CloseCircleOutlined },
  { label: '进行中链路', value: '46', hint: '峰值并发 71', icon: ClockCircleOutlined }
]

const latestEvents = [
  { title: '检测到疑似 Prompt 注入请求', level: 'high', levelText: '高危', source: '客服助手', time: '2 分钟前' },
  { title: '命中敏感词策略并完成拦截', level: 'medium', levelText: '中危', source: '营销生成', time: '8 分钟前' },
  { title: '模型输出触发合规复检规则', level: 'medium', levelText: '中危', source: '知识问答', time: '14 分钟前' },
  { title: '审计链路追踪任务执行成功', level: 'low', levelText: '低危', source: '系统任务', time: '26 分钟前' }
]

const traceCases = [
  { name: '客服问答安全审查链路', status: 'success', statusText: '成功', cost: '312ms', updatedAt: '1 分钟前' },
  { name: '营销文案合规过滤链路', status: 'running', statusText: '进行中', cost: '428ms', updatedAt: '10 秒前' },
  { name: '订单风控二次校验链路', status: 'failed', statusText: '失败', cost: '1.2s', updatedAt: '3 分钟前' },
  { name: '知识库检索结果脱敏链路', status: 'success', statusText: '成功', cost: '276ms', updatedAt: '2 分钟前' }
]

const goTo = (routeName: string) => {
  void router.push({ name: routeName })
}
</script>

<style scoped>
.center-page {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.stat-card {
  border: 1px solid var(--border-default);
  border-radius: 14px;
  background: var(--bg-card);
  padding: 16px;
}

.stat-head {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stat-icon {
  color: var(--primary);
  font-size: 16px;
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
}

.stat-value {
  margin-top: 10px;
  font-size: 28px;
  line-height: 1;
  font-weight: 700;
  color: var(--text-heading);
}

.stat-hint {
  margin-top: 10px;
  font-size: 12px;
}

.trend-up {
  color: var(--success, #2e9f5d);
}

.trend-down {
  color: var(--warning, #dd4b39);
}

.trend-flat {
  color: var(--text-secondary);
}

.main-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
}

.left-column,
.right-column {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.panel-card {
  border: 1px solid var(--border-default);
  border-radius: 14px;
  background: var(--bg-card);
  padding: 20px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 14px;
}

.panel-head h3 {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 16px;
  color: var(--text-heading);
}

.panel-head span {
  font-size: 12px;
  color: var(--text-secondary);
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.route-card {
  min-height: 124px;
  border: 1px solid var(--border-default);
  border-radius: 12px;
  background: var(--bg-card);
  padding: 16px;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
}

.route-card:hover {
  border-color: var(--primary);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-heading);
}

.card-desc {
  margin-top: 8px;
  color: var(--text-secondary);
  line-height: 1.5;
  font-size: 13px;
}

.trace-overview-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.trace-overview-item {
  border: 1px solid var(--border-default);
  border-radius: 10px;
  padding: 12px;
  background: color-mix(in srgb, var(--bg-card) 88%, var(--bg-base));
}

.trace-overview-head {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--text-secondary);
  font-size: 12px;
}

.trace-overview-item strong {
  display: block;
  margin-top: 8px;
  font-size: 24px;
  line-height: 1;
  color: var(--text-heading);
}

.trace-overview-hint {
  margin-top: 8px;
  font-size: 12px;
  color: var(--text-secondary);
}

.trace-detail-list {
  margin-top: 12px;
  display: grid;
  gap: 10px;
}

.trace-detail-item {
  border: 1px solid var(--border-default);
  border-radius: 10px;
  background: var(--bg-card);
  padding: 12px;
}

.trace-detail-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.trace-detail-name {
  font-size: 13px;
  color: var(--text-heading);
}

.trace-detail-status {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  border-radius: 999px;
  border: 1px solid transparent;
  padding: 2px 8px;
  font-size: 12px;
}

.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 999px;
  background: currentColor;
}

.status-success {
  color: #2e9f5d;
  background: color-mix(in srgb, #2e9f5d 12%, transparent);
  border-color: color-mix(in srgb, #2e9f5d 24%, transparent);
}

.status-failed {
  color: #dd4b39;
  background: color-mix(in srgb, #dd4b39 12%, transparent);
  border-color: color-mix(in srgb, #dd4b39 24%, transparent);
}

.status-running {
  color: #f39c12;
  background: color-mix(in srgb, #f39c12 12%, transparent);
  border-color: color-mix(in srgb, #f39c12 24%, transparent);
}

.trace-detail-meta {
  margin-top: 6px;
  display: flex;
  justify-content: space-between;
  gap: 10px;
  font-size: 12px;
  color: var(--text-secondary);
}

.event-list {
  display: grid;
  gap: 10px;
}

.event-item {
  border: 1px solid var(--border-default);
  border-radius: 10px;
  background: var(--bg-card);
  padding: 12px;
}

.event-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.event-title {
  font-size: 13px;
  color: var(--text-heading);
}

.event-level {
  font-size: 12px;
  border-radius: 999px;
  padding: 2px 8px;
  border: 1px solid transparent;
}

.level-high {
  color: #dd4b39;
  background: color-mix(in srgb, #dd4b39 12%, transparent);
  border-color: color-mix(in srgb, #dd4b39 24%, transparent);
}

.level-medium {
  color: #f39c12;
  background: color-mix(in srgb, #f39c12 12%, transparent);
  border-color: color-mix(in srgb, #f39c12 24%, transparent);
}

.level-low {
  color: #2e9f5d;
  background: color-mix(in srgb, #2e9f5d 12%, transparent);
  border-color: color-mix(in srgb, #2e9f5d 24%, transparent);
}

.event-meta {
  margin-top: 6px;
  color: var(--text-secondary);
  font-size: 12px;
}

@media (max-width: 1100px) {
  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .main-grid {
    grid-template-columns: 1fr;
  }

  .card-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .trace-overview-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .card-grid {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
