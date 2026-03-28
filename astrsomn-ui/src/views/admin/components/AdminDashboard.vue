<template>
  <div class="dashboard">
    <div class="dashboard-grid">
      <section class="panel">
        <header class="panel-header">
          <div class="panel-title-area">
            <span class="panel-title">流量统计</span>
            <span class="chart-badge">近 7 天</span>
          </div>
        </header>
        <div class="panel-body">
          <TrafficStats />
        </div>
      </section>

      <section class="panel">
        <header class="panel-header">
          <div class="panel-title-area">
            <span class="panel-title">模型调用分布</span>
            <span class="chart-badge">本周</span>
          </div>
        </header>
        <div class="panel-body">
          <ModelUsageChart />
        </div>
      </section>

      <section class="panel panel-full">
        <header class="panel-header">
          <div class="panel-title-area">
            <div class="title-main">
              <span class="panel-title">管理中心</span>
              <span class="panel-subtitle">按分类浏览，快速进入对应模块</span>
            </div>
          </div>
        </header>
        <div class="panel-body">
          <div class="mgmt-sections">
            <section
              v-for="group in managementGroups"
              :key="group.id"
              class="mgmt-section"
            >
              <header class="mgmt-section-head">
                <h3 class="mgmt-section-title">{{ group.title }}</h3>
                <p v-if="group.subtitle" class="mgmt-section-sub">{{ group.subtitle }}</p>
              </header>
              <div class="card-grid">
                <button
                  v-for="item in group.items"
                  :key="item.key"
                  type="button"
                  class="entry-card"
                  :class="{ 'entry-card--highlight': item.highlight }"
                  :data-accent="item.accent"
                  @click="navigateTo(item.route)"
                >
                  <div class="icon-wrapper">
                    <component :is="item.icon" class="entry-icon" />
                  </div>
                  <div class="entry-content">
                    <div class="entry-title">{{ item.label }}</div>
                    <div class="entry-desc">{{ item.description }}</div>
                  </div>
                  <div class="arrow-hint">→</div>
                </button>
              </div>
            </section>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { Component } from 'vue'
import { useRouter } from 'vue-router'
import {
  TeamOutlined,
  ContainerOutlined,
  ToolOutlined,
  SettingOutlined,
  SafetyCertificateOutlined,
  BookOutlined,
  FileTextOutlined,
  UserOutlined,
  CloudServerOutlined,
  CodeOutlined,
  SecurityScanOutlined,
  LineChartOutlined,
  NodeIndexOutlined,
} from '@ant-design/icons-vue'
import TrafficStats from './echarts/TrafficStats.vue'
import ModelUsageChart from './echarts/ModelUsageChart.vue'

const router = useRouter()

function currentUserRole(): string | undefined {
  try {
    const raw = localStorage.getItem('userInfo')
    if (!raw) return undefined
    return (JSON.parse(raw) as { userRole?: string }).userRole
  } catch {
    return undefined
  }
}

/** 主题内 accent：仅用于标题字色、边框与图标；highlight 为重要入口加浅底色 */
type EntryAccent =
  | 'primary'
  | 'cyan'
  | 'blue'
  | 'sky'
  | 'mint'
  | 'coral'
  | 'primary-light'
  | 'indigo'
  | 'ocean'
  | 'violet'
  | 'teal'
  | 'frost'
  | 'brand'

type ManagementEntry = {
  key: string
  label: string
  description: string
  icon: Component
  route: string
  accent: EntryAccent
  highlight?: boolean
}

type ManagementGroup = {
  id: string
  title: string
  subtitle?: string
  items: ManagementEntry[]
}

const managementGroupsAll: ManagementGroup[] = [
  {
    id: 'ai-core',
    title: 'AI 模型与编排',
    subtitle: '智能体、MCP、工具、模型与提示词——搭建推理与执行链路',
    items: [
      {
        key: 'agents',
        label: '智能体管理',
        description: '配置智能体策略与执行参数',
        icon: TeamOutlined,
        route: '/admin/agents',
        accent: 'primary',
        highlight: true,
      },
      {
        key: 'mcp',
        label: 'AI MCP',
        description: '管理 MCP 服务连接与健康状态',
        icon: ContainerOutlined,
        route: '/admin/mcp',
        accent: 'ocean',
      },
      {
        key: 'tools',
        label: 'AI Tools',
        description: '维护工具定义与调用权限',
        icon: ToolOutlined,
        route: '/admin/tools',
        accent: 'blue',
      },
      {
        key: 'models',
        label: '模型配置',
        description: '管理模型供应商与路由策略',
        icon: SettingOutlined,
        route: '/admin/models',
        accent: 'sky',
        highlight: true,
      },
      {
        key: 'prompts',
        label: '提示词管理',
        description: '维护 AI_PROMPT 系统提示词与版本',
        icon: SafetyCertificateOutlined,
        route: '/admin/prompts',
        accent: 'mint',
      },
    ],
  },
  {
    id: 'content',
    title: '模板与知识',
    subtitle: '内容模板、知识库与文档入库',
    items: [
      {
        key: 'templates',
        label: 'FTL 模板',
        description: 'Freemarker / StringTemplate 模板（AI_TEMPLATE）',
        icon: CodeOutlined,
        route: '/admin/templates',
        accent: 'primary-light',
      },
      {
        key: 'kb-mgr',
        label: '知识库管理',
        description: '管理知识库空间与索引配置',
        icon: BookOutlined,
        route: '/admin/knowledge-bases',
        accent: 'teal',
      },
      {
        key: 'doc-mgr',
        label: '文档管理',
        description: '管理文档处理与入库任务',
        icon: FileTextOutlined,
        route: '/admin/documents',
        accent: 'cyan',
      },
    ],
  },
  {
    id: 'ops',
    title: '安全与流程',
    subtitle: '风控、可观测与自动化编排',
    items: [
      {
        key: 'security',
        label: '安全治理',
        description: '配置敏感词、注入检测与风控策略',
        icon: SecurityScanOutlined,
        route: '/admin/security',
        accent: 'coral',
      },
      {
        key: 'tracing',
        label: '链路追踪',
        description: '监控大模型调用链路与日志输出',
        icon: LineChartOutlined,
        route: '/admin/tracing',
        accent: 'violet',
      },
      {
        key: 'workflows',
        label: '工作流',
        description: '配置 DAG 节点流程、触发条件与执行链路',
        icon: NodeIndexOutlined,
        route: '/admin/workflows',
        accent: 'indigo',
      },
      {
        key: 'workflow-simple',
        label: '工作流编排测试',
        description: '按 Workflow Key 快速调用后端测试运行（已发布版本）',
        icon: NodeIndexOutlined,
        route: '/admin/workflows/simple',
        accent: 'sky',
      },
    ],
  },
  {
    id: 'system',
    title: '系统管理',
    subtitle: '账号、角色与运行环境（仅超级管理员）',
    items: [
      {
        key: 'users',
        label: '用户管理',
        description: '管理系统用户、角色与权限分配',
        icon: UserOutlined,
        route: '/admin/users',
        accent: 'brand',
        highlight: true,
      },
      {
        key: 'env',
        label: '环境管理',
        description: '管理运行环境、服务实例与部署配置',
        icon: CloudServerOutlined,
        route: '/admin/env',
        accent: 'frost',
      },
    ],
  },
]

function entryVisibleForRole(entry: ManagementEntry, isSuper: boolean): boolean {
  if (isSuper) return true
  return entry.route !== '/admin/users' && entry.route !== '/admin/env'
}

/** 按角色过滤条目；空分类不展示 */
const managementGroups = computed(() =>
  managementGroupsAll
    .map((g) => {
      const isSuper = currentUserRole() === 'SUPER_ADMIN'
      const items = g.items.filter((i) => entryVisibleForRole(i, isSuper))
      return { ...g, items }
    })
    .filter((g) => g.items.length > 0),
)

const navigateTo = (path: string) => {
  void router.push(path)
}
</script>

<style scoped>
/* 基础布局 */
.dashboard {
  padding: 24px;
  background-color: var(--bg-base);
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  max-width: 1600px;
  margin: 0 auto;
}

/* 面板样式 */
.panel {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: 16px;
  box-shadow: var(--shadow-card);
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
}

.panel-full {
  grid-column: span 2;
}

.panel-header {
  padding: 24px 24px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.panel-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-heading);
  letter-spacing: -0.02em;
}

.panel-subtitle {
  font-size: 13px;
  color: var(--text-muted);
  margin-top: 4px;
  display: block;
}

.chart-badge {
  font-size: 12px;
  font-weight: 600;
  color: var(--accent-blue);
  background: var(--primary-hover);
  padding: 4px 12px;
  border-radius: 20px;
  margin-left: 12px;
}

.panel-body {
  padding: 0 24px 24px;
}

/* 管理入口分区 */
.mgmt-sections {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.mgmt-section {
  margin: 0;
}

.mgmt-section-head {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-subtle);
}

.mgmt-section-title {
  margin: 0;
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 0.02em;
  color: var(--section-title);
}

.mgmt-section-sub {
  margin: 6px 0 0;
  font-size: 12px;
  line-height: 1.45;
  color: var(--text-muted);
  max-width: 720px;
}

/* 子卡片网格 - 重点修改 */
.card-grid {
  display: grid;
  /* 增加最小宽度，使卡片看起来更大气 */
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.entry-card {
  --entry-color: var(--accent-blue);

  position: relative;
  border: 1px solid color-mix(in srgb, var(--entry-color) 30%, var(--border-default));
  border-radius: 14px;
  background: var(--bg-card);
  padding: 24px;
  display: flex;
  gap: 18px;
  align-items: center;
  text-align: left;
  cursor: pointer;
  outline: none;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.entry-card[data-accent='primary'] {
  --entry-color: var(--primary);
}
.entry-card[data-accent='cyan'] {
  --entry-color: var(--accent-cyan);
}
.entry-card[data-accent='blue'] {
  --entry-color: var(--accent-blue);
}
.entry-card[data-accent='sky'] {
  --entry-color: var(--section-title);
}
.entry-card[data-accent='mint'] {
  --entry-color: var(--success);
}
.entry-card[data-accent='coral'] {
  --entry-color: var(--error);
}
.entry-card[data-accent='primary-light'] {
  --entry-color: var(--primary-light);
}
.entry-card[data-accent='indigo'] {
  --entry-color: color-mix(in srgb, var(--primary) 45%, var(--accent-blue) 55%);
}
.entry-card[data-accent='ocean'] {
  --entry-color: color-mix(in srgb, var(--primary) 35%, var(--accent-cyan) 65%);
}
.entry-card[data-accent='violet'] {
  --entry-color: color-mix(in srgb, var(--section-title) 88%, var(--accent-blue) 12%);
}
.entry-card[data-accent='teal'] {
  --entry-color: color-mix(in srgb, var(--success) 48%, var(--accent-cyan) 52%);
}
.entry-card[data-accent='frost'] {
  --entry-color: color-mix(in srgb, var(--section-title) 52%, var(--accent-cyan) 48%);
}
.entry-card[data-accent='brand'] {
  --entry-color: color-mix(in srgb, var(--primary) 52%, var(--accent-blue) 48%);
}

.entry-card--highlight {
  background: color-mix(in srgb, var(--entry-color) 9%, var(--bg-card));
}

.entry-card:hover {
  border-color: color-mix(in srgb, var(--entry-color) 58%, var(--border-default));
  transform: translateY(-4px);
  box-shadow:
    0 12px 24px -10px color-mix(in srgb, var(--entry-color) 22%, transparent),
    0 4px 12px -4px color-mix(in srgb, var(--entry-color) 12%, transparent);
}

/* 图标区：仅边框 + 字色随条目；重要入口略加深底色 */
.icon-wrapper {
  flex-shrink: 0;
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: 1px solid color-mix(in srgb, var(--entry-color) 38%, var(--border-subtle));
  border-radius: 12px;
  transition: all 0.3s ease;
}

.entry-card--highlight .icon-wrapper {
  background: color-mix(in srgb, var(--entry-color) 11%, var(--bg-card));
}

.entry-card:hover .icon-wrapper {
  border-color: color-mix(in srgb, var(--entry-color) 52%, var(--border-default));
  background: color-mix(in srgb, var(--entry-color) 14%, var(--bg-card));
}

.entry-icon {
  font-size: 26px;
  color: var(--entry-color);
  transition: color 0.3s ease;
}

.entry-content {
  flex: 1;
  min-width: 0;
}

.entry-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--entry-color);
  margin-bottom: 4px;
}

.entry-desc {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.5;
}

/* 装饰性箭头 */
.arrow-hint {
  font-size: 18px;
  color: var(--text-muted);
  opacity: 0;
  transform: translateX(-10px);
  transition: all 0.3s ease;
}

.entry-card:hover .arrow-hint {
  opacity: 1;
  transform: translateX(0);
  color: var(--entry-color);
}

/* 响应式调整 */
@media (max-width: 1024px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  .panel-full {
    grid-column: auto;
  }
  .card-grid {
    grid-template-columns: 1fr;
  }
}
</style>