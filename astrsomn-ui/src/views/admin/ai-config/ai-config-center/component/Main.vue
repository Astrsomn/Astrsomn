<template>
  <a-layout-content>
    <transition mode="out-in" name="fade">
      <!-- 全局管理页面 -->
      <component
          :is="currentGlobalComponent"
          v-if="currentViewType === 'global'"
          :key="currentViewKey"
          :initial-view-mode="initialViewMode"
      />
      <!-- Agent 配置页 -->
      <AgentForm
          v-else-if="showConfig"
          :agent-id="configAgentId"
          :agent-name="configAgentName"
          @back="handleConfigBack"
      />
      <!-- 指定提供商：Agent + Model 磁吸翻页 -->
      <div
          v-else-if="currentProviderKey !== 'all'"
          :key="currentProviderKey"
          ref="snapContainerRef"
          :class="{ 'is-peeking': isPeeking }"
          class="provider-detail-view"
      >
        <div class="snap-page">
          <AgentSection
              :provider-key="currentProviderKey"
              :provider-name="selectedProvider?.name"
              :provider-description="selectedProvider?.description"
              :provider-avatar="selectedProvider?.avatar"
              @create="handleCreateAgent"
              @select="handleSelectAgent"
          />
          <div class="scroll-hint">
            <DownOutlined/>
            <span>{{ t.main.scrollHint }}</span>
            <DownOutlined/>
          </div>
        </div>
        <div class="snap-page">
          <ModelSection :provider-key="currentProviderKey"/>
        </div>
      </div>
      <!-- 全部 Agent + Model 磁吸翻页 -->
      <div
          v-else
          key="all"
          ref="snapContainerRef"
          :class="{ 'is-peeking': isPeeking }"
          class="provider-detail-view"
      >
        <div class="snap-page">
          <AgentSection provider-key="all" @create="handleCreateAgent" @select="handleSelectAgent"/>
        </div>
        <div class="snap-page">
          <ModelSection provider-key="all"/>
        </div>
      </div>
    </transition>
  </a-layout-content>
</template>

<script lang="ts" setup>
import {nextTick, ref, watch} from 'vue'
import {DownOutlined} from '@ant-design/icons-vue'
import AgentSection from './right/AgentSection.vue'
import ModelSection from './right/ModelSection.vue'
import AgentForm from './right/AgentForm.vue'
import type {AiAgent} from '@/api/aiAgent'
import {usePageTranslation} from '@/locales/pages.ts'

const props = defineProps<{
  currentViewType: string
  currentViewKey: string
  currentGlobalComponent: any
  initialViewMode: 'grid' | 'list'
  currentProviderKey: string
  selectedProvider: { key: string; name: string; description: string; avatar: string } | null
}>()

const emit = defineEmits<{
  'create-agent': []
  'select-agent': [agent: AiAgent]
  'config-back': []
}>()

const snapContainerRef = ref<HTMLDivElement | null>(null)
const isPeeking = ref(false)
const showConfig = ref(false)
const configAgentName = ref('')
const configAgentId = ref<string | number | undefined>(undefined)

const t = usePageTranslation('ai-config-center')

const handleCreateAgent = () => {
  configAgentId.value = undefined
  configAgentName.value = t.value.main.newAgent
  showConfig.value = true
  emit('create-agent')
}

const handleSelectAgent = (agent: AiAgent) => {
  configAgentId.value = agent.id
  configAgentName.value = agent.agentName || 'Agent'
  showConfig.value = true
  emit('select-agent', agent)
}

const handleConfigBack = () => {
  showConfig.value = false
  configAgentName.value = ''
  configAgentId.value = undefined
  emit('config-back')
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
    () => props.currentProviderKey,
    (val) => {
      if (val && val !== 'all') {
        playPeekScroll()
      }
    },
    {immediate: true}
)
</script>

<style scoped>
.ant-layout-content {
  flex: 1;
  min-height: 0;
  height: 100%;
  overflow: visible;
  background-color: var(--bg-surface);
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


.provider-detail-view {
  height: 100%;
  overflow-y: auto;
  scroll-snap-type: y mandatory;
}

.provider-detail-view.is-peeking {
  scroll-snap-type: none;
}


.snap-page {
  min-height: calc(100vh - 60px);
  scroll-snap-align: start;
  display: flex;
  flex-direction: column;
}


.scroll-hint {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 6px;
  padding: 16px 30px 24px;
  font-size: 12px;
  color: var(--text-muted);
  animation: hint-bounce 2s ease-in-out infinite;
}

@keyframes hint-bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(6px);
  }
}
</style>
