<template>
  <div class="config-center-layout">
    <!-- 左侧侧边栏 -->
    <div class="config-center-sider">
      <Sidebar
          @select="handleSidebarSelect"
          @select-provider="handleSelectProvider"
          @update:collapsed="sidebarCollapsed = $event"
      />
    </div>

    <!-- 右侧主内容区域 -->
    <Main
        :current-view-type="currentViewType"
        :current-view-key="currentViewKey"
        :current-global-component="currentGlobalComponent"
        :initial-view-mode="initialViewMode"
        :current-provider-key="currentProviderKey"
        :selected-provider="selectedProvider"
    />
  </div>
</template>

<script lang="ts" setup>
import {computed, defineAsyncComponent, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import Sidebar from './component/Sidebar.vue'
import Main from './component/Main.vue'

const route = useRoute()
const router = useRouter()

const globalComponents: Record<string, any> = {
  'ai-account': defineAsyncComponent(() => import('@/views/admin/ai-config/ai-account/AccountList.vue')),
  'prompts': defineAsyncComponent(() => import('@/views/admin/ai-config/ai-prompt/PromptList.vue')),
  'mcp': defineAsyncComponent(() => import('@/views/admin/ai-config/ai-mcp/McpList.vue')),
  'tools': defineAsyncComponent(() => import('@/views/admin/ai-config/ai-tool/ToolList.vue')),
  'ftl': defineAsyncComponent(() => import('@/views/admin/ai-safety/ai-template/TemplateList.vue')),
  'conversations': defineAsyncComponent(() => import('@/views/admin/ai-config/ai-conversation/ConversationList.vue')),
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

// 从路由 query 中读取视图模式，传给全局管理子组件
const initialViewMode = computed<'grid' | 'list'>(() => {
  const vm = route.query.viewMode as string | undefined
  return vm === 'grid' ? 'grid' : 'list'
})

const configCenterPath = '/admin/ai-config-center'

const sidebarCollapsed = ref(false)
const selectedProvider = ref<{ key: string; name: string; description: string; avatar: string } | null>(null)

const handleSelectProvider = (info: { key: string; name: string; description: string; avatar: string }) => {
  selectedProvider.value = info
}

const handleSidebarSelect = (key: string) => {
  const globalKeys = Object.keys(globalComponents)

  if (globalKeys.includes(key)) {
    selectedProvider.value = null
    router.push({path: configCenterPath, query: {view: key, viewMode: 'grid'}})
  } else {
    if (key === 'all') {
      selectedProvider.value = null
      router.push({path: configCenterPath, query: {}})
    } else {
      router.push({path: configCenterPath, query: {provider: key}})
    }
  }
}
</script>

<style scoped>
.config-center-layout {
  height: calc(100vh - 60px);
  display: flex;
  overflow: hidden;
  background-color: var(--bg-surface);
}

.config-center-sider {
  overflow: hidden;
  flex-shrink: 0;
}
</style>
