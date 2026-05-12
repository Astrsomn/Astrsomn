<template>
  <a-layout>
    <!-- 左侧侧边栏 -->
    <a-layout-sider width="320" class="bg-white">
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
          :initial-view-mode="initialViewMode"
        />
        <!-- Agent 配置页 -->
        <AgentConfig
          v-else-if="showConfig"
          :agent-id="configAgentId"
          :agent-name="configAgentName"
          @back="handleConfigBack"
        />
        <!-- 指定提供商：Agent + Model 磁吸翻页 -->
        <div
          v-else-if="currentProviderKey !== 'all'"
          ref="snapContainerRef"
          class="provider-detail-view"
          :class="{ 'is-peeking': isPeeking }"
          :key="currentProviderKey"
        >
          <div class="snap-page">
            <AgentSection :provider-key="currentProviderKey" @create="handleCreateAgent" @select="handleSelectAgent" />
            <div class="scroll-hint">
              <DownOutlined />
              <span>滚动查看接入模型</span>
              <DownOutlined />
            </div>
          </div>
          <div class="snap-page">
            <ModelSection :provider-key="currentProviderKey" />
          </div>
        </div>
        <!-- 全部 Agent + Model 磁吸翻页 -->
        <div
          v-else
          ref="snapContainerRef"
          class="provider-detail-view"
          :class="{ 'is-peeking': isPeeking }"
          key="all"
        >
          <div class="snap-page">
            <AgentSection provider-key="all" @create="handleCreateAgent" @select="handleSelectAgent" />
          </div>
          <div class="snap-page">
            <ModelSection provider-key="all" />
          </div>
        </div>
      </transition>
    </a-layout-content>
  </a-layout>
</template>

<script setup lang="ts">
import { computed, defineAsyncComponent, ref, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { DownOutlined } from '@ant-design/icons-vue'
import Sidebar from './component/Sidebar.vue'
import AgentSection from './component/AgentSection.vue'
import ModelSection from './component/ModelSection.vue'
import AgentConfig from './component/AgentConfig.vue'
import type { AiAgent } from '@/api/aiAgent'

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

// 从路由 query 中读取视图模式，传给全局管理子组件
const initialViewMode = computed<'grid' | 'list'>(() => {
  const vm = route.query.viewMode as string | undefined
  return vm === 'grid' ? 'grid' : 'list'
})

const configCenterPath = '/admin/ai-config-center'

const snapContainerRef = ref<HTMLDivElement | null>(null)
const isPeeking = ref(false)
const showConfig = ref(false)
const configAgentName = ref('')
const configAgentId = ref<string | number | undefined>(undefined)

const handleCreateAgent = () => {
  configAgentId.value = undefined
  configAgentName.value = '新 Agent'
  showConfig.value = true
}

const handleSelectAgent = (agent: AiAgent) => {
  configAgentId.value = agent.id
  configAgentName.value = agent.agentName || 'Agent'
  showConfig.value = true
}

const handleConfigBack = () => {
  showConfig.value = false
  configAgentName.value = ''
  configAgentId.value = undefined
}

const playPeekScroll = async () => {
  await nextTick()
  await new Promise((r) => setTimeout(r, 300))
  const el = snapContainerRef.value
  if (!el) return

  isPeeking.value = true

  const target = el.scrollHeight
  const duration = 600
  const stay = 300
  const start = performance.now()
  const easeInOut = (t: number) => (t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2)

  const animate = (now: number) => {
    const progress = Math.min((now - start) / duration, 1)
    el.scrollTop = target * easeInOut(progress)
    if (progress < 1) {
      requestAnimationFrame(animate)
    } else {
      setTimeout(() => {
        const backStart = performance.now()
        const animateBack = (now2: number) => {
          const p = Math.min((now2 - backStart) / duration, 1)
          el.scrollTop = target * (1 - easeInOut(p))
          if (p < 1) {
            requestAnimationFrame(animateBack)
          } else {
            isPeeking.value = false
          }
        }
        requestAnimationFrame(animateBack)
      }, stay)
    }
  }
  requestAnimationFrame(animate)
}

watch(
  () => currentProviderKey.value,
  (val) => {
    if (val && val !== 'all') {
      playPeekScroll()
    }
  },
  { immediate: true }
)

const handleSidebarSelect = (key: string) => {
  showConfig.value = false
  const globalKeys = Object.keys(globalComponents)
  
  if (globalKeys.includes(key)) {
    router.push({ path: configCenterPath, query: { view: key, viewMode: 'grid' } })
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
.ant-layout {
  height: calc(100vh - 60px);
  background-color: var(--bg-surface);
}

.ant-layout-sider {
  height: 100%;
  overflow: hidden;
  background-color: var(--bg-card);
  flex-shrink: 0;
}

.ant-layout-content {
  height: 100%;
  overflow-y: auto;
  background-color: var(--bg-surface);
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

/* 磁吸翻页容器 */
.provider-detail-view {
  height: 100%;
  overflow-y: auto;
  scroll-snap-type: y mandatory;
}

.provider-detail-view.is-peeking {
  scroll-snap-type: none;
}

/* 每一页占满视口高度 */
.snap-page {
  height: calc(100vh - 60px);
  scroll-snap-align: start;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

/* 底部滚动提示 */
.scroll-hint {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 16px 0 24px;
  font-size: 12px;
  color: var(--text-muted);
  animation: hint-bounce 2s ease-in-out infinite;
}

@keyframes hint-bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(6px); }
}
</style>
