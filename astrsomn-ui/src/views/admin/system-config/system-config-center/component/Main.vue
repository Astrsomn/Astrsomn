<template>
  <a-layout-content class="main-content-area">
    <transition mode="out-in" name="fade">
      <component
          :is="currentGlobalComponent"
          v-if="currentViewKey !== 'all'"
          :key="currentViewKey"
      />
      <div v-else key="dashboard" class="dashboard-view">
        <Top :module-cards="moduleCards" @go-to="handleModuleGoTo"/>
        <a-spin :spinning="loading" class="center-spin">
          <Center
              :current-page="currentPage"
              :env-distribution="envDistribution"
              :filtered-total="filteredTotal"
              :loading="loading"
              :page-size="pageSize"
              :paged-online-systems="pagedOnlineSystems"
              :recent-changes="recentChanges"
              :resource-usage="resourceUsage"
              :status-counts="statusCounts"
              :status-filter="statusFilter"
              @export="onExport"
              @refresh="onRefresh"
              @update:status-filter="setStatusFilter"
              @update:page="setPage"
              @update:page-size="setPageSize"
              @view-all-changes="onViewAllChanges"
          />
        </a-spin>
      </div>
    </transition>
  </a-layout-content>
</template>

<script lang="ts" setup>
import {computed, defineAsyncComponent} from 'vue'
import {useRouter} from 'vue-router'
import {AlertOutlined, ApiOutlined, ClusterOutlined, SettingOutlined, UserOutlined} from '@ant-design/icons-vue'
import {message} from 'ant-design-vue'
import type {Component} from 'vue'
import Top from './Top.vue'
import Center from './Center.vue'
import {useSystemConfigCenter} from '../useSystemConfigCenter'

type ModuleCard = {
  title: string
  desc: string
  routeName: string
  icon: Component
}

const props = defineProps<{
  currentViewKey: string
}>()

const router = useRouter()

const globalComponents: Record<string, any> = {
  users: defineAsyncComponent(() => import('@/views/admin/system-config/system-user/SystemUserList.vue')),
  env: defineAsyncComponent(() => import('@/views/admin/system-config/system-env/SystemEnvList.vue')),
  config: defineAsyncComponent(() => import('@/views/admin/system-config/system-config/SystemConfigList.vue')),
  messages: defineAsyncComponent(() => import('@/views/admin/system-config/system-message/SystemMessageList.vue')),
  extensions: defineAsyncComponent(() => import('@/views/admin/system-config/system-extension/SystemExtensionList.vue')),
}

const currentGlobalComponent = computed(() => globalComponents[props.currentViewKey] || null)

const routeNameToViewKey: Record<string, string> = {
  AdminUsers: 'users',
  AdminEnv: 'env',
  AdminSystemConfig: 'config',
  AdminSystemMessage: 'messages',
  AdminSystemExtension: 'extensions',
}

const moduleCards: ModuleCard[] = [
  {title: '用户管理', desc: '维护系统用户与权限角色', routeName: 'AdminUsers', icon: UserOutlined},
  {title: '环境管理', desc: '维护系统运行环境与配置', routeName: 'AdminEnv', icon: ClusterOutlined},
  {title: '系统配置', desc: '管理系统参数与配置项', routeName: 'AdminSystemConfig', icon: SettingOutlined},
  {title: '系统消息', desc: '查看并维护系统通知记录', routeName: 'AdminSystemMessage', icon: AlertOutlined},
  {title: '系统扩展', desc: '管理扩展安装与市场模块', routeName: 'AdminSystemExtension', icon: ApiOutlined},
]

const handleModuleGoTo = (routeName: string) => {
  const viewKey = routeNameToViewKey[routeName]
  if (viewKey) {
    router.push({path: '/admin/system', query: {view: viewKey}})
  } else {
    router.push({name: routeName})
  }
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
  exportBusinessSystemsCsv,
} = useSystemConfigCenter()

const onExport = () => {
  exportBusinessSystemsCsv()
}

const onRefresh = async () => {
  const ok = await refresh()
  if (ok) message.success('已刷新')
}

const onViewAllChanges = () => {
  router.push({path: '/admin/system', query: {view: 'messages'}})
}
</script>

<style scoped>
.main-content-area {
  flex: 1;
  min-height: 0;
  height: calc(100vh - 60px);
  overflow-y: auto;
  background-color: var(--bg-surface);
}

.dashboard-view {
  width: 100%;
  max-width: 1600px;
  margin: 0 auto;
  padding: 30px 30px 100px 30px;
  height: 100%;
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

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

@media (max-width: 900px) {
  .dashboard-view {
    gap: 12px;
  }
}
</style>
