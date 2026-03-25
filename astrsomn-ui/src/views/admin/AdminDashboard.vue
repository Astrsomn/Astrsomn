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
            <span class="panel-title">管理中心</span>
            <span class="panel-subtitle">AI 配置 · AI 知识库 · 系统管理</span>
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
              <component :is="item.icon" class="entry-icon" />
              <div class="entry-content">
                <div class="entry-title">{{ item.label }}</div>
                <div class="entry-desc">{{ item.description }}</div>
              </div>
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
.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.chart-badge {
  font-size: 10px;
  font-weight: 600;
  color: var(--accent-blue);
  background: var(--primary-hover);
  padding: 2px 8px;
  border-radius: 10px;
}

.panel {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 260px;
}

.panel-full {
  grid-column: span 2;
}

.panel-header {
  padding: 14px 18px 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.panel-title-area {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.panel-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-heading);
}

.panel-subtitle {
  font-size: 12px;
  color: var(--text-muted);
  white-space: nowrap;
}

.panel-body {
  flex: 1;
  min-height: 0;
  padding: 0 18px 16px;
  overflow: auto;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 14px;
}

.entry-card {
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  background: var(--bg-elevated);
  color: var(--text-primary);
  padding: 20px 18px;
  display: flex;
  gap: 14px;
  align-items: flex-start;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.2s ease, transform 0.2s ease, background-color 0.2s ease;
}

.entry-card:hover {
  border-color: var(--accent-blue);
  background: var(--primary-hover);
  transform: translateY(-2px);
}

.entry-icon {
  font-size: 24px;
  color: var(--accent-blue);
  margin-top: 2px;
}

.entry-content {
  min-width: 0;
}

.entry-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-heading);
  margin-bottom: 6px;
}

.entry-desc {
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.5;
}

@media (max-width: 1024px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
  .panel-full {
    grid-column: auto;
  }
}
</style>
