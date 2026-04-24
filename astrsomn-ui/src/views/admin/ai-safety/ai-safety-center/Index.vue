<template>
  <AdminPageShell
    title="AI 安全治理中心"
    description="统一管理提示词安全、敏感词治理、审计追踪与风险响应流程。"
    empty-text="暂无可配置的安全能力。"
  >
    <div class="center-page">
      <Top :safety-stats="safetyStats" />
      <Center :trace-stats="traceStats" :trace-cases="traceCases" :latest-events="latestEvents" />
      <Bottom :module-cards="moduleCards" @go-to="goTo" />
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import {
  AlertOutlined,
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
import Top from './component/top.vue'
import Center from './component/center.vue'
import Bottom from './component/bottom.vue'

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

@media (max-width: 1100px) {
  .center-page {
    gap: 14px;
  }
}

@media (max-width: 900px) {
  .center-page {
    gap: 12px;
  }
}
</style>
