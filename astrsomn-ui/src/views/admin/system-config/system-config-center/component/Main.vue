<template>
  <a-layout-content class="main-content-area">
    <transition mode="out-in" name="fade">
      <component
        :is="currentGlobalComponent"
        v-if="currentViewKey !== 'all'"
        :key="currentViewKey"
      />
      <div v-else key="dashboard" class="dashboard-wrapper">
        <a-spin :spinning="loading" class="center-spin">
          <DashboardView
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
            @go-to="handleModuleGoTo"
            @refresh="onRefresh"
            @update:page="setPage"
            @update:page-size="setPageSize"
            @update:status-filter="setStatusFilter"
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
import type {Component} from 'vue'
import {
  AlertOutlined,
  ApiOutlined,
  ClusterOutlined,
  SettingOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'
import {message} from 'ant-design-vue'
import DashboardView from './DashboardView.vue'
import {useSystemConfigCenter} from '../useSystemConfigCenter'

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

const handleModuleGoTo = (routeName: string) => {
  const viewKey = routeNameToViewKey[routeName]
  if (viewKey) {
    router.push({ path: '/admin/system', query: { view: viewKey } })
  } else {
    router.push({ name: routeName })
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
  router.push({ path: '/admin/system', query: { view: 'messages' } })
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

.dashboard-wrapper {
  width: 100%;
  height: 100%;
}

.center-spin {
  height: 100%;
}

.center-spin :deep(.ant-spin-container) {
  height: 100%;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(8px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
