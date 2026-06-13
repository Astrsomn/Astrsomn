<template>
  <div :class="{ 'is-collapsed': activeCenter === 'chat' && chatState.sidebarCollapsed.value }" class="admin-sidebar-wrapper">
    <!-- Chat 会话中心 -->
    <AstSidebar
        v-if="activeCenter === 'chat'"
        :collapsed="chatState.sidebarCollapsed.value"
        :items="chatState.sessionItems.value"
        :loading="chatState.sessionLoading.value"
        :selected-memory-key="chatState.currentMemoryKey.value"
        @create="chatState.handleCreate()"
        @delete="(item) => chatState.handleDelete(item)"
        @open="(memoryKey) => chatState.handleOpen(memoryKey)"
        @update:collapsed="chatState.sidebarCollapsed.value = $event"
    />

    <!-- AI 配置中心 -->
    <AiConfigCenterSidebar
        v-else-if="activeCenter === 'ai-config'"
        @select="handleAiConfigSelect"
        @select-provider="handleAiConfigSelectProvider"
    />

    <!-- 向量知识库中心 -->
    <VectorCenterSidebar
        v-else-if="activeCenter === 'vector'"
        :sources="vectorState.sources.value"
        :stores="vectorState.stores.value"
        :selected-store-id="vectorState.selectedStoreId.value"
        @select-source="handleVectorSelectSource"
        @select-store="handleVectorSelectStore"
        @changed="handleVectorChanged"
    />

    <!-- 系统配置中心 -->
    <SystemConfigCenterSidebar
        v-else-if="activeCenter === 'system'"
        @select="handleSystemSelect"
    />
  </div>
</template>

<script lang="ts" setup>
import {computed, watch} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import AiConfigCenterSidebar from '@/views/admin/ai-config/ai-config-center/component/Sidebar.vue'
import VectorCenterSidebar from '@/views/admin/ai-vector/vector-center/component/Sidebar.vue'
import SystemConfigCenterSidebar from '@/views/admin/system-config/system-config-center/component/Sidebar.vue'
import AstSidebar from '@/views/chat-index/component/AstSidebar.vue'
import {useAiConfigCenterState, type AiConfigProvider} from '@/composables/useAiConfigCenterState'
import {useVectorCenterState} from '@/views/admin/ai-vector/vector-center/hooks/useVectorCenterState'
import {useChatSidebarState} from '@/composables/useChatSidebarState'

type CenterKey = 'chat' | 'ai-config' | 'vector' | 'system'

const route = useRoute()
const router = useRouter()

// Chat 状态
const chatState = useChatSidebarState()

// AI 配置中心状态
const aiConfigState = useAiConfigCenterState()

// 向量中心状态
const vectorState = useVectorCenterState()

/**
 * 解析当前路由对应的中心
 */
const activeCenter = computed<CenterKey | null>(() => {
  const path = route.path
  if (path === '/admin/ai-config-center' || path.startsWith('/admin/ai-config-center/')) return 'ai-config'
  if (path === '/admin/vec-center' || path.startsWith('/admin/vec-center/')) return 'vector'
  if (path === '/admin/system') return 'system'
  if (path === '/' || path === '' || path === '/chat') return 'chat'
  return null
})

const visible = computed(() => activeCenter.value !== null)

// ============ AI 配置中心 handlers ============
const aiConfigGlobalKeys = ['ai-account', 'prompts', 'mcp', 'tools', 'ftl', 'conversations']
const aiConfigCenterPath = '/admin/ai-config-center'

const handleAiConfigSelect = (key: string) => {
  if (aiConfigGlobalKeys.includes(key)) {
    aiConfigState.selectedProvider.value = null
    router.push({path: aiConfigCenterPath, query: {view: key, viewMode: 'grid'}})
    return
  }
  if (key === 'all') {
    aiConfigState.selectedProvider.value = null
    router.push({path: aiConfigCenterPath, query: {}})
    return
  }
  router.push({path: aiConfigCenterPath, query: {provider: key}})
}

const handleAiConfigSelectProvider = (info: AiConfigProvider) => {
  aiConfigState.selectedProvider.value = info
}

// ============ 向量中心 handlers ============
const handleVectorSelectSource = async (sourceId: number | string) => {
  const sourceChanged = vectorState.selectedSourceId.value !== sourceId
  vectorState.selectedSourceId.value = sourceId
  vectorState.selectedStoreId.value = undefined
  vectorState.selectedDocId.value = undefined
  vectorState.docs.value = []
  if (sourceChanged) {
    await vectorState.fetchStores()
  }
}

const handleVectorSelectStore = async (storeId: number | string) => {
  vectorState.selectedStoreId.value = storeId
  vectorState.selectedDocId.value = undefined
  await vectorState.fetchDocs()
}

const handleVectorChanged = async () => {
  await vectorState.fetchSources()
}

// 当切换到向量中心时，自动加载可用向量源
watch(activeCenter, (center) => {
  if (center === 'vector' && vectorState.sources.value.length === 0) {
    vectorState.bootstrap()
  }
}, {immediate: true})

// ============ 系统配置中心 handlers ============
const systemCenterPath = '/admin/system'

const handleSystemSelect = (key: string) => {
  if (key === 'all') {
    router.push({path: systemCenterPath, query: {}})
  } else {
    router.push({path: systemCenterPath, query: {view: key}})
  }
}
</script>

<style scoped>
.admin-sidebar-wrapper {
  width: 288px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  height: 100%;
  transition: width 0.28s ease;
}

.admin-sidebar-wrapper.is-collapsed {
  width: 56px;
}
</style>
