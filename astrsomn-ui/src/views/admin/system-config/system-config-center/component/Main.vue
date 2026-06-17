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
            @refresh="onRefresh"
            @update:page="setPage"
            @update:status-filter="setStatusFilter"
            @view-all-changes="onViewAllChanges"
          />
        </a-spin>
      </div>
    </transition>

    <!-- 消息中心弹窗 -->
    <SystemMessageDialog
      :open="messageDialogOpen"
      @update:open="messageDialogOpen = $event"
    />
  </a-layout-content>
</template>

<script lang="ts" setup>
import {computed, defineAsyncComponent, ref} from 'vue'
import {message} from 'ant-design-vue'
import DashboardView from './DashboardView.vue'
import SystemMessageDialog from '@/views/admin/system-config/system-message/component/SystemMessageDialog.vue'
import {useSystemConfigCenter} from '../useSystemConfigCenter'

const props = defineProps<{
  currentViewKey: string
}>()

const globalComponents: Record<string, any> = {
  users: defineAsyncComponent(() => import('@/views/admin/system-config/system-user/SystemUserList.vue')),
  env: defineAsyncComponent(() => import('@/views/admin/system-config/system-env/SystemEnvList.vue')),
  config: defineAsyncComponent(() => import('@/views/admin/system-config/system-config/SystemConfigList.vue')),
  messages: defineAsyncComponent(() => import('@/views/admin/system-config/system-message/SystemMessageList.vue')),
  extensions: defineAsyncComponent(() => import('@/views/admin/system-config/system-extension/SystemExtensionList.vue')),
}

const currentGlobalComponent = computed(() => globalComponents[props.currentViewKey] || null)

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
  refresh,
  exportBusinessSystemsCsv,
} = useSystemConfigCenter()

const messageDialogOpen = ref(false)

const onExport = () => {
  exportBusinessSystemsCsv()
}

const onRefresh = async () => {
  const ok = await refresh()
  if (ok) message.success('已刷新')
}

const onViewAllChanges = () => {
  messageDialogOpen.value = true
}
</script>

<style scoped>
.main-content-area {
  flex: 1;
  min-height: 0;
  height: 100%;
  overflow: visible;
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
</style>
