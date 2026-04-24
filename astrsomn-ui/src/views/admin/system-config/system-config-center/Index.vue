<template>
  <DashboardWrapper>
      <Center
        :paged-online-systems="pagedOnlineSystems"
        :current-page="currentPage"
        :total-pages="totalPages"
        :total-systems="onlineSystems.length"
        :todo-items="todoItems"
        :extension-cards="extensionCards"
        :module-cards="moduleCards"
        @prev-page="prevPage"
        @next-page="nextPage"
        @go-to="goTo"
      />
  </DashboardWrapper>
</template>

<script setup lang="ts">
import {
  ApiOutlined,
  AppstoreOutlined,
  ClusterOutlined,
  DatabaseOutlined,
  DeploymentUnitOutlined,
  ExperimentOutlined,
  HddOutlined,
  SafetyCertificateOutlined,
  SettingOutlined,
  UserOutlined,
  TeamOutlined,
  AlertOutlined,
  RobotOutlined
} from '@ant-design/icons-vue'
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import DashboardWrapper from '@/components/home/DashboardWrapper.vue'
import type { Component } from 'vue'
import Center from './component/center/Center.vue'

type ModuleCard = {
  title: string
  desc: string
  routeName: string
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
  { title: '用户管理', desc: '维护系统用户与权限角色', routeName: 'AdminUsers', icon: UserOutlined },
  { title: '环境管理', desc: '维护系统运行环境与配置', routeName: 'AdminEnv', icon: ClusterOutlined },
  { title: '系统配置', desc: '管理系统参数与配置项', routeName: 'AdminSystemConfig', icon: SettingOutlined },
  { title: '系统扩展', desc: '管理扩展安装与市场模块', routeName: 'AdminSystemExtension', icon: ApiOutlined }
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


@media (max-width: 900px) {
  .center-page {
    gap: 12px;
  }
}
</style>
