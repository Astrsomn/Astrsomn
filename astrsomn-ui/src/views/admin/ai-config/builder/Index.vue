<template>
    <main class="builder-main">
      <section class="builder-left">
        <LeftTop :agent-name="agentName" :description="description" @update:agentName="agentName = $event" @update:description="description = $event" />
        <LeftCenter />
      </section>
      <Right />
    </main>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { aiAgentApi } from '@/api/aiAgent.ts'

import LeftTop from './component/LeftTop.vue'
import LeftCenter from './component/LeftCenter.vue'
import Right from './component/Right.vue'

const route = useRoute()
const agentName = ref('未命名的智能体')
const description = ref('')

const fetchAgent = async (id: string) => {
  try {
    const agent = await aiAgentApi.detail(id)
    if (agent) {
      agentName.value = agent.agentName || '未命名的智能体'
      description.value = agent.description || ''
    }
  } catch (e) {
    console.error('Failed to fetch agent:', e)
  }
}

onMounted(() => {
  const id = route.query.id as string
  if (id) {
    void fetchAgent(id)
  }
})
</script>

<style scoped>
.logo-badge .anticon {
  font-size: 18px;
}

.builder-main {
  box-sizing: border-box;
  display: flex;
  flex: 1 1 auto;
  width: 100%;
  height: calc(100vh - 60px);
  max-height: calc(100vh - 60px);
  min-height: 0;
  overflow: hidden;
}

.builder-left {
  flex: 2.5 1 0;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 24px;
  overflow: hidden;
}
</style>

<style>
/* 设计稿 builder.demo：玻璃态 / 页面底色 / 设计变量（子树继承） */
.builder-main {
  --ab-bg-page: #f8fafc;
  --ab-glass-bg: rgba(255, 255, 255, 0.8);
  --ab-glass-border: rgba(255, 255, 255, 0.6);
  --ab-glass-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  --ab-glass-radius: 16px;
  --ab-glass-haze: 10px;
  --ab-hover-line: #3b82f6;
  --ab-hover-shadow: 0 0 15px rgba(59, 130, 246, 0.15);
  --ab-blue-grad: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%);
  --ab-btn-glow: 0 4px 15px rgba(59, 130, 246, 0.3);
  --ab-dashed: #e2e8f0;
  --ab-aside-tint: rgba(255, 255, 255, 0.4);
  background: var(--ab-bg-page);
  padding: 16px;
  gap: 16px;
}

@keyframes agent-studio-pulse {
  0%,
  100% {
    transform: scale(0.95);
    opacity: 0.7;
  }
  50% {
    transform: scale(1.05);
    opacity: 1;
  }
}
</style>
