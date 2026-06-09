<template>
  <div class="config-center-layout">
    <!-- 左侧 Sidebar 已被上提至 Home.vue 的 AdminSidebar；本页面仅负责主内容 -->
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
import {computed, defineAsyncComponent} from 'vue'
import {useRoute} from 'vue-router'
import Main from './component/Main.vue'
import {useAiConfigCenterState} from '@/composables/useAiConfigCenterState'

const route = useRoute()

const globalComponents: Record<string, any> = {
  'ai-account': defineAsyncComponent(() => import('@/views/admin/ai-config/ai-account/AccountList.vue')),
  'prompts': defineAsyncComponent(() => import('@/views/admin/ai-config/ai-prompt/PromptList.vue')),
  'mcp': defineAsyncComponent(() => import('@/views/admin/ai-config/ai-mcp/McpList.vue')),
  'tools': defineAsyncComponent(() => import('@/views/admin/ai-config/ai-tool/ToolList.vue')),
  'ftl': defineAsyncComponent(() => import('@/views/admin/ai-config/ai-template/TemplateList.vue')),
  'conversations': defineAsyncComponent(() => import('@/views/admin/ai-config/ai-conversation/ConversationList.vue')),
}


const currentViewKey = computed(() => {
  const viewKey = route.query.view as string | undefined
  const provider = route.query.provider as string | undefined

  if (viewKey && Object.keys(globalComponents).includes(viewKey)) {
    return viewKey
  }

  return provider || 'all'
})


const currentViewType = computed(() => {
  const viewKey = route.query.view as string | undefined
  if (viewKey && Object.keys(globalComponents).includes(viewKey)) {
    return 'global'
  }
  return 'agent'
})


const currentProviderKey = computed(() => {
  const provider = route.query.provider as string | undefined
  return provider || 'all'
})


const currentGlobalComponent = computed(() => {
  const viewKey = route.query.view as string | undefined
  return viewKey && globalComponents[viewKey] ? globalComponents[viewKey] : null
})


const initialViewMode = computed<'grid' | 'list'>(() => {
  const vm = route.query.viewMode as string | undefined
  return vm === 'grid' ? 'grid' : 'list'
})

// selectedProvider 状态由 AdminSidebar 维护，本页通过 composable 读取
const {selectedProvider} = useAiConfigCenterState()
</script>

<style scoped>
.config-center-layout {
  flex: 1;
  min-height: 0;
  display: flex;
  overflow: hidden;
  background-color: var(--bg-surface);
}
</style>
