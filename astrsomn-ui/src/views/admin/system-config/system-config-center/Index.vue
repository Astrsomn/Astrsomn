<template>
  <AstrsomnDashboardWrapper>
    <div class="system-config-center-page">
      <Top :module-cards="moduleCards" @go-to="goTo" />
      <a-spin :spinning="loading" class="center-spin">
        <Center
          :loading="loading"
          :paged-online-systems="pagedOnlineSystems"
          :current-page="currentPage"
          :filtered-total="filteredTotal"
          :status-filter="statusFilter"
          :status-counts="statusCounts"
          :page-size="pageSize"
          :env-distribution="envDistribution"
          :resource-usage="resourceUsage"
          :recent-changes="recentChanges"
          @update:status-filter="setStatusFilter"
          @update:page="setPage"
          @update:page-size="setPageSize"
          @export="onExport"
          @refresh="onRefresh"
          @view-all-changes="onViewAllChanges"
        />
      </a-spin>
    </div>
  </AstrsomnDashboardWrapper>
</template>

<script setup lang="ts">
import {
  ApiOutlined,
  ClusterOutlined,
  SettingOutlined,
  UserOutlined,
  AlertOutlined
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import AstrsomnDashboardWrapper from '@/components/home/AstrsomnDashboardWrapper.vue'
import type { Component } from 'vue'
import Top from './component/Top.vue'
import Center from './component/Center.vue'
import { useSystemConfigCenter } from './useSystemConfigCenter'

type ModuleCard = {
  title: string
  desc: string
  routeName: string
  icon: Component
}

const {
  loading,
  pagedOnlineSystems,
  currentPage,
  filteredTotal,
  statusFilter,
  statusCounts,
  pageSize,
  envDistribution,
  resourceUsage,
  recentChanges,
  setStatusFilter,
  setPage,
  setPageSize,
  refresh,
  exportBusinessSystemsCsv
} = useSystemConfigCenter()

const router = useRouter()

const moduleCards: ModuleCard[] = [
  { title: '用户管理', desc: '维护系统用户与权限角色', routeName: 'AdminUsers', icon: UserOutlined },
  { title: '环境管理', desc: '维护系统运行环境与配置', routeName: 'AdminEnv', icon: ClusterOutlined },
  { title: '系统配置', desc: '管理系统参数与配置项', routeName: 'AdminSystemConfig', icon: SettingOutlined },
  { title: '系统消息', desc: '查看并维护系统通知记录', routeName: 'AdminSystemMessage', icon: AlertOutlined },
  { title: '系统扩展', desc: '管理扩展安装与市场模块', routeName: 'AdminSystemExtension', icon: ApiOutlined }
]

const onExport = () => {
  exportBusinessSystemsCsv()
}

const onRefresh = async () => {
  const ok = await refresh()
  if (ok) message.success('已刷新')
}

const onViewAllChanges = () => {
  void router.push({ name: 'AdminSystemMessage' })
}

const goTo = (routeName: string) => {
  void router.push({ name: routeName })
}
</script>

<style scoped>
.system-config-center-page {
  width: 100%;
  max-width: 1600px;
  margin: 0 auto;
  height: calc(100vh - 180px);
  display: flex;
  flex-direction: column;

  min-height: 0;
  box-sizing: border-box;
}

.center-spin {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.center-spin :deep(.ant-spin-container) {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

@media (max-width: 900px) {
  .system-config-center-page {
    gap: 12px;
  }
}
</style>
