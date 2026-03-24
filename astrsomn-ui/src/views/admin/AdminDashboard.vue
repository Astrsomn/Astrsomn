<template>
  <div class="dashboard">
    <div class="dashboard-toolbar">
      <span class="toolbar-hint">拖动面板底部调整大小 · 点击 ↔ 切换宽度</span>
      <button class="reset-btn" @click="resetLayout">
        <ReloadOutlined /> 重置布局
      </button>
    </div>

    <div class="widget-grid">
      <ResizableWidget widget-id="traffic-chart" title="流量统计">
        <template #badge><span class="chart-badge">近 7 天</span></template>
        <TrafficStats />
      </ResizableWidget>

      <ResizableWidget widget-id="model-chart" title="模型调用分布">
        <template #badge><span class="chart-badge">本周</span></template>
        <ModelUsageChart />
      </ResizableWidget>

      <ResizableWidget
        v-for="group in cardGroups"
        :key="group.key"
        :widget-id="group.key"
        :title="group.title"
      >
        <div class="card-grid">
          <button
            v-for="item in group.items"
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
      </ResizableWidget>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import {
  ReloadOutlined,
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
import { useDashboardLayout } from '@/composables/useDashboardLayout'
import ResizableWidget from './components/ResizableWidget.vue'
import TrafficStats from './components/TrafficStats.vue'
import ModelUsageChart from './components/ModelUsageChart.vue'

const router = useRouter()
const { reset: resetLayout } = useDashboardLayout()

const cardGroups = [
  {
    key: 'ai-config',
    title: 'AI 配置',
    items: [
      { key: 'agents', label: '智能体管理', description: '配置智能体策略与执行参数', icon: TeamOutlined, route: '/admin/agents' },
      { key: 'mcp', label: 'AI MCP', description: '管理 MCP 服务连接与健康状态', icon: ContainerOutlined, route: '/admin/mcp' },
      { key: 'tools', label: 'AI Tools', description: '维护工具定义与调用权限', icon: ToolOutlined, route: '/admin/tools' },
      { key: 'models', label: '模型配置', description: '管理模型供应商与路由策略', icon: SettingOutlined, route: '/admin/models' },
      { key: 'prompts', label: '提示词管理', description: '维护提示词模板与版本', icon: SafetyCertificateOutlined, route: '/admin/prompts' }
    ]
  },
  {
    key: 'ai-kb',
    title: 'AI 知识库',
    items: [
      { key: 'kb-mgr', label: '知识库管理', description: '管理知识库空间与索引配置', icon: BookOutlined, route: '/admin/knowledge-bases' },
      { key: 'doc-mgr', label: '文档管理', description: '管理文档处理与入库任务', icon: FileTextOutlined, route: '/admin/documents' }
    ]
  },
  {
    key: 'system',
    title: '系统管理',
    items: [
      { key: 'users', label: '用户管理', description: '管理系统用户、角色与权限分配', icon: UserOutlined, route: '/admin/users' },
      { key: 'env', label: '环境管理', description: '管理运行环境、服务实例与部署配置', icon: CloudServerOutlined, route: '/admin/env' }
    ]
  }
]

const navigateTo = (path: string) => {
  void router.push(path)
}
</script>

<style scoped>
.dashboard-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}

.toolbar-hint {
  font-size: 12px;
  color: var(--text-muted);
}

.reset-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  background: transparent;
  color: var(--text-secondary);
  font-size: 12px;
  cursor: pointer;
  transition: background 0.15s, color 0.15s, border-color 0.15s;
}

.reset-btn:hover {
  background: var(--primary-hover);
  color: var(--accent-blue);
  border-color: var(--accent-blue);
}

.widget-grid {
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
</style>
