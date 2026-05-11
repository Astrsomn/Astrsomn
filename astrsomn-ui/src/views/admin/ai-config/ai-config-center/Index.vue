<template>
  <a-layout>
    <!-- 左侧侧边栏 -->
    <a-layout-sider width="320" class="bg-white border-r">
      <Sidebar @select="handleSidebarSelect" />
    </a-layout-sider>

    <!-- 右侧主内容区域 -->
    <a-layout-content class="bg-gray-50">
      <transition name="fade" mode="out-in">
        <!-- 全局管理页面 -->
        <component
          v-if="currentViewType === 'global'"
          :is="currentGlobalComponent"
          :key="currentViewKey"
        />
        <!-- Agent列表页面 -->
        <AgentList
          v-else
          :key="currentProviderKey"
          :provider-key="currentProviderKey"
        />
      </transition>
    </a-layout-content>
  </a-layout>
</template>

<script setup lang="ts">
import { computed, defineAsyncComponent } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Sidebar from './component/Sidebar.vue'
import AgentList from './component/AgentList.vue'

const route = useRoute()
const router = useRouter()

const globalComponents: Record<string, any> = {
  'ai-account': defineAsyncComponent(() => import('@/views/admin/ai-config/ai-account/AccountList.vue')),
  'prompts': defineAsyncComponent(() => import('@/views/admin/ai-config/ai-prompt/PromptList.vue')),
  'mcp': defineAsyncComponent(() => import('@/views/admin/ai-config/ai-mcp/McpList.vue')),
  'tools': defineAsyncComponent(() => import('@/views/admin/ai-config/ai-tool/ToolList.vue')),
}

// 当前选中的视图类型
const currentViewKey = computed(() => {
  const viewKey = route.query.view as string | undefined
  const provider = route.query.provider as string | undefined
  
  if (viewKey && Object.keys(globalComponents).includes(viewKey)) {
    return viewKey
  }
  
  return provider || 'all'
})

// 当前视图类型
const currentViewType = computed(() => {
  const viewKey = route.query.view as string | undefined
  if (viewKey && Object.keys(globalComponents).includes(viewKey)) {
    return 'global'
  }
  return 'agent'
})

// 当前提供商
const currentProviderKey = computed(() => {
  const provider = route.query.provider as string | undefined
  return provider || 'all'
})

// 当前全局组件
const currentGlobalComponent = computed(() => {
  const viewKey = route.query.view as string | undefined
  return viewKey && globalComponents[viewKey] ? globalComponents[viewKey] : null
})

const configCenterPath = '/admin/ai-config-center'

const handleSidebarSelect = (key: string) => {
  const globalKeys = Object.keys(globalComponents)
  
  if (globalKeys.includes(key)) {
    router.push({ path: configCenterPath, query: { view: key } })
  } else {
    if (key === 'all') {
      router.push({ path: configCenterPath, query: {} })
    } else {
      router.push({ path: configCenterPath, query: { provider: key } })
    }
  }
}
</script>

<style scoped>
/* 整体布局样式 */
.ant-layout {
  background-color: var(--bg-surface);
}

/* 主内容区样式 */
.ant-layout-content {
  padding: 0;
  overflow-y: auto;
  background-color: var(--bg-surface);
}

/* 侧边栏样式 */
.ant-layout-sider {
  background-color: var(--bg-card);
  border-right: 1px solid var(--border-default);
}

/* 视图切换动画 */
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
</style>
