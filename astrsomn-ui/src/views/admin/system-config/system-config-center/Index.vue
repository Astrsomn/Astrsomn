<template>
  <AdminPageShell
    title="系统管理中心"
    description="统一管理系统用户、环境、配置与扩展模块，提升配置效率与运维可视化能力。"
    empty-text="暂无可管理模块。"
  >
    <div class="center-page">
      <section class="stats-grid">
        <article v-for="item in overviewStats" :key="item.label" class="stat-card">
          <div class="stat-head">
            <component :is="item.icon" class="stat-icon" />
            <div class="stat-label">{{ item.label }}</div>
          </div>
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-trend" :class="`trend-${item.trend}`">
            {{ item.hint }}
          </div>
        </article>
      </section>

      <section class="main-grid">
        <div class="left-column">
          <div class="panel-card">
            <div class="panel-head">
              <h3>
                <AppstoreOutlined />
                核心模块
              </h3>
              <span>快速进入系统管理能力</span>
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
              <h3>
                <ApiOutlined />
                系统扩展能力
              </h3>
              <span>支持多类型扩展：插件、向量库、模型等</span>
            </div>
            <div class="extension-grid">
              <article v-for="item in extensionCards" :key="item.name" class="extension-item">
                <div class="extension-top">
                  <div class="extension-name">
                    <component :is="item.icon" />
                    <span>{{ item.name }}</span>
                  </div>
                  <strong>{{ item.count }}</strong>
                </div>
                <p class="extension-desc">{{ item.desc }}</p>
                <div class="extension-tags">
                  <span v-for="tag in item.tags" :key="tag" class="extension-tag">{{ tag }}</span>
                </div>
              </article>
            </div>
          </div>
        </div>

        <div class="right-column">
          <div class="panel-card">
            <div class="panel-head">
              <h3>
                <TeamOutlined />
                在线业务系统
              </h3>
              <span>用户即业务系统，展示实时在线状态</span>
            </div>
            <div class="online-grid">
              <div v-for="system in pagedOnlineSystems" :key="system.name" class="online-item">
                <div class="online-top">
                  <div class="online-name">
                    <component :is="system.icon" />
                    <span>{{ system.name }}</span>
                  </div>
                  <span class="online-status" :class="`status-${system.status}`">
                    <span class="status-dot"></span>
                    {{ system.statusText }}
                  </span>
                </div>
                <div class="online-meta">
                  <span><ClockCircleOutlined /> 最后心跳 {{ system.lastHeartbeat }}</span>
                  <span><DatabaseOutlined /> 活跃会话 {{ system.sessions }}</span>
                </div>
              </div>
            </div>
            <div class="pager-wrap">
              <button type="button" class="pager-btn" :disabled="currentPage === 1" @click="prevPage">
                上一页
              </button>
              <span class="pager-text">第 {{ currentPage }} / {{ totalPages }} 页，共 {{ onlineSystems.length }} 个系统</span>
              <button
                type="button"
                class="pager-btn"
                :disabled="currentPage === totalPages"
                @click="nextPage"
              >
                下一页
              </button>
            </div>
          </div>

          <div class="panel-card">
            <div class="panel-head">
              <h3>
                <AlertOutlined />
                待处理事项
              </h3>
              <span>建议优先处理项</span>
            </div>
            <ul class="todo-list">
              <li v-for="task in todoItems" :key="task">{{ task }}</li>
            </ul>
          </div>
        </div>
      </section>
    </div>
  
  </AdminPageShell>
</template>

<script setup lang="ts">
import {
  AlertOutlined,
  ApiOutlined,
  AppstoreOutlined,
  ClockCircleOutlined,
  ClusterOutlined,
  DatabaseOutlined,
  DeploymentUnitOutlined,
  ExperimentOutlined,
  HddOutlined,
  SafetyCertificateOutlined,
  SettingOutlined,
  TeamOutlined,
  UserOutlined,
  RobotOutlined
} from '@ant-design/icons-vue'
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import type { Component } from 'vue'

type ModuleCard = {
  title: string
  desc: string
  routeName: string
  icon: Component
}

type Trend = 'up' | 'down' | 'flat'

type OverviewStat = {
  label: string
  value: string
  hint: string
  trend: Trend
  icon: Component
}

type OnlineStatus = 'online' | 'degraded' | 'offline'

type OnlineSystem = {
  name: string
  status: OnlineStatus
  statusText: string
  sessions: number
  lastHeartbeat: string
  icon: Component
}

const router = useRouter()

const moduleCards: ModuleCard[] = [
  { title: '用户管理', desc: '维护系统用户与权限角色。', routeName: 'AdminUsers', icon: UserOutlined },
  { title: '环境管理', desc: '维护系统运行环境与配置隔离。', routeName: 'AdminEnv', icon: ClusterOutlined },
  { title: '系统配置', desc: '管理系统参数与配置项。', routeName: 'AdminSystemConfig', icon: SettingOutlined },
  { title: '系统扩展', desc: '管理扩展安装与市场模块。', routeName: 'AdminSystemExtension', icon: ApiOutlined }
]

const overviewStats: OverviewStat[] = [
  { label: '在线业务系统', value: '18', hint: '较昨日 +2', trend: 'up', icon: TeamOutlined },
  { label: '活跃会话', value: '356', hint: '峰值时段 420', trend: 'up', icon: DatabaseOutlined },
  { label: '配置项总数', value: '246', hint: '本周新增 8 项', trend: 'flat', icon: SettingOutlined },
  { label: '健康告警', value: '2', hint: '较昨日 -3', trend: 'down', icon: AlertOutlined }
]

const extensionCards = [
  {
    name: '平台插件',
    count: 24,
    desc: '用于业务能力增强、流程编排与第三方集成。',
    icon: DeploymentUnitOutlined,
    tags: ['工作流', '通知', '审计', '数据同步']
  },
  {
    name: '向量库接入',
    count: 6,
    desc: '支持多种向量引擎，满足检索增强与知识库场景。',
    icon: HddOutlined,
    tags: ['Milvus', 'pgvector', 'Elastic', 'Chroma']
  },
  {
    name: '模型供应商',
    count: 14,
    desc: '统一管理推理模型、Embedding 模型和多模态模型。',
    icon: RobotOutlined,
    tags: ['OpenAI', 'Qwen', 'DeepSeek', 'Claude']
  },
  {
    name: '实验与灰度',
    count: 8,
    desc: '支持按业务系统做模型路由、版本对比与灰度放量。',
    icon: ExperimentOutlined,
    tags: ['AB 实验', '灰度发布', '版本回滚']
  }
]

const onlineSystems: OnlineSystem[] = [
  { name: 'OMS 订单中心', status: 'online', statusText: '在线', sessions: 86, lastHeartbeat: '10 秒前', icon: AppstoreOutlined },
  { name: 'WMS 仓储系统', status: 'online', statusText: '在线', sessions: 64, lastHeartbeat: '18 秒前', icon: DatabaseOutlined },
  { name: 'CRM 客户中心', status: 'degraded', statusText: '高延迟', sessions: 39, lastHeartbeat: '35 秒前', icon: TeamOutlined },
  { name: '风控引擎', status: 'online', statusText: '在线', sessions: 52, lastHeartbeat: '12 秒前', icon: SafetyCertificateOutlined },
  { name: '支付网关', status: 'offline', statusText: '离线', sessions: 0, lastHeartbeat: '4 分钟前', icon: ApiOutlined },
  { name: '会员中心', status: 'online', statusText: '在线', sessions: 47, lastHeartbeat: '9 秒前', icon: UserOutlined },
  { name: '营销自动化', status: 'degraded', statusText: '高延迟', sessions: 23, lastHeartbeat: '42 秒前', icon: AppstoreOutlined },
  { name: '主数据平台', status: 'online', statusText: '在线', sessions: 31, lastHeartbeat: '15 秒前', icon: DatabaseOutlined },
  { name: '智能客服', status: 'online', statusText: '在线', sessions: 72, lastHeartbeat: '11 秒前', icon: RobotOutlined },
  { name: '报表分析', status: 'offline', statusText: '离线', sessions: 0, lastHeartbeat: '7 分钟前', icon: ClusterOutlined }
]

const pageSize = 4
const currentPage = ref(1)

const totalPages = computed(() => Math.max(1, Math.ceil(onlineSystems.length / pageSize)))

const pagedOnlineSystems = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return onlineSystems.slice(start, start + pageSize)
})

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value -= 1
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value += 1
  }
}

const todoItems = [
  '校验生产环境关键参数，避免配置漂移',
  '清理 30 天未登录账号并复核权限',
  '升级过期扩展模块并执行兼容性测试',
  '完成本周系统配置变更审计归档'
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
  padding: 16px 18px;
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
  color: var(--text-secondary);
  font-size: 13px;
}

.stat-value {
  margin-top: 8px;
  font-size: 28px;
  line-height: 1;
  font-weight: 700;
  color: var(--text-heading);
}

.stat-trend {
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

.extension-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.extension-item {
  border: 1px solid var(--border-default);
  border-radius: 10px;
  padding: 12px;
  background: color-mix(in srgb, var(--bg-card) 88%, var(--bg-base));
}

.extension-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: var(--text-heading);
}

.extension-name {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
}

.extension-top strong {
  font-size: 18px;
  color: var(--text-heading);
}

.extension-desc {
  margin: 8px 0;
  color: var(--text-secondary);
  font-size: 12px;
  line-height: 1.5;
}

.extension-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.extension-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 11px;
  color: var(--text-secondary);
  border: 1px solid color-mix(in srgb, var(--border-default) 80%, transparent);
  background: color-mix(in srgb, var(--bg-card) 90%, var(--bg-base));
}

.online-grid {
  display: grid;
  gap: 10px;
}

.online-item {
  border: 1px solid var(--border-default);
  border-radius: 10px;
  background: var(--bg-card);
  padding: 12px 14px;
}

.online-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.online-name {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--text-heading);
  font-size: 14px;
  font-weight: 600;
}

.online-status {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 12px;
  border: 1px solid transparent;
}

.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 999px;
  background: currentColor;
}

.status-online {
  color: #2e9f5d;
  background: color-mix(in srgb, #2e9f5d 12%, transparent);
  border-color: color-mix(in srgb, #2e9f5d 26%, transparent);
}

.status-degraded {
  color: #f39c12;
  background: color-mix(in srgb, #f39c12 12%, transparent);
  border-color: color-mix(in srgb, #f39c12 24%, transparent);
}

.status-offline {
  color: #dd4b39;
  background: color-mix(in srgb, #dd4b39 12%, transparent);
  border-color: color-mix(in srgb, #dd4b39 24%, transparent);
}

.online-meta {
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
  gap: 8px;
  color: var(--text-secondary);
  font-size: 12px;
}

.online-meta span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.pager-wrap {
  margin-top: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.pager-btn {
  border: 1px solid var(--border-default);
  border-radius: 8px;
  background: var(--bg-card);
  color: var(--text-primary);
  font-size: 12px;
  padding: 4px 10px;
  cursor: pointer;
}

.pager-btn:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.pager-text {
  color: var(--text-secondary);
  font-size: 12px;
}

.todo-list {
  margin: 0;
  padding: 0 0 0 16px;
  display: grid;
  gap: 8px;
  color: var(--text-secondary);
  line-height: 1.5;
  font-size: 13px;
}

.todo-list li::marker {
  color: var(--primary);
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .main-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .card-grid {
    grid-template-columns: 1fr;
  }

  .extension-grid {
    grid-template-columns: 1fr;
  }

  .pager-wrap {
    flex-direction: column;
    align-items: stretch;
  }

  .pager-text {
    text-align: center;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
