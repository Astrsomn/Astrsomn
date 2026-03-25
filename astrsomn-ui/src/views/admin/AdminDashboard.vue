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
              <span class="panel-subtitle">AI 配置 · AI 知识库 · 系统管理</span>
            </div>
          </div>
        </header>
        <div class="panel-body">
          <div class="card-grid">
            <button
              v-for="item in managementItems"
              :key="item.key"
              type="button"
              class="entry-card"
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
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
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
  CloudServerOutlined
} from '@ant-design/icons-vue'
import TrafficStats from './components/TrafficStats.vue'
import ModelUsageChart from './components/ModelUsageChart.vue'

const router = useRouter()

const managementItems = [
  { key: 'agents', label: '智能体管理', description: '配置智能体策略与执行参数', icon: TeamOutlined, route: '/admin/agents' },
  { key: 'mcp', label: 'AI MCP', description: '管理 MCP 服务连接与健康状态', icon: ContainerOutlined, route: '/admin/mcp' },
  { key: 'tools', label: 'AI Tools', description: '维护工具定义与调用权限', icon: ToolOutlined, route: '/admin/tools' },
  { key: 'models', label: '模型配置', description: '管理模型供应商与路由策略', icon: SettingOutlined, route: '/admin/models' },
  { key: 'prompts', label: '提示词管理', description: '维护提示词模板与版本', icon: SafetyCertificateOutlined, route: '/admin/prompts' },
  { key: 'kb-mgr', label: '知识库管理', description: '管理知识库空间与索引配置', icon: BookOutlined, route: '/admin/knowledge-bases' },
  { key: 'doc-mgr', label: '文档管理', description: '管理文档处理与入库任务', icon: FileTextOutlined, route: '/admin/documents' },
  { key: 'users', label: '用户管理', description: '管理系统用户、角色与权限分配', icon: UserOutlined, route: '/admin/users' },
  { key: 'env', label: '环境管理', description: '管理运行环境、服务实例与部署配置', icon: CloudServerOutlined, route: '/admin/env' }
]

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

/* 子卡片网格 - 重点修改 */
.card-grid {
  display: grid;
  /* 增加最小宽度，使卡片看起来更大气 */
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.entry-card {
  position: relative;
  border: 1px solid var(--border-subtle);
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

/* 悬浮效果：ToC 常用的轻微放大和投影 */
.entry-card:hover {
  border-color: var(--primary);
  background: var(--bg-card);
  transform: translateY(-4px);
  box-shadow: 0 12px 20px -8px rgba(59, 130, 246, 0.15);
}

/* 图标容器装饰 */
.icon-wrapper {
  flex-shrink: 0;
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-input);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.entry-card:hover .icon-wrapper {
  background: var(--primary-hover);
}

.entry-icon {
  font-size: 26px;
  color: var(--text-secondary);
  transition: color 0.3s ease;
}

.entry-card:hover .entry-icon {
  color: var(--accent-blue);
}

.entry-content {
  flex: 1;
  min-width: 0;
}

.entry-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-heading);
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
  color: var(--accent-blue);
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