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
.builder-container {
  min-height: 100vh;
  background: var(--bg-default);
  display: flex;
  flex-direction: column;
}

.builder-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  height: 72px;
  border-bottom: 1px solid var(--border-default);
  background: var(--bg-card);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  position: relative;
  z-index: 30;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.logo-badge {
  width: 40px;
  height: 40px;
  background: var(--primary-gradient);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.25);
}

.logo-badge .anticon {
  font-size: 18px;
}

.header-title {
  font-size: 18px;
  font-weight: bold;
  color: var(--text-heading);
  margin: 0;
}

.header-divider {
  width: 1px;
  height: 24px;
  background: var(--border-default);
}

.header-status {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--text-secondary);
}

.agent-name-input {
  padding: 6px 12px;
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  font-size: 14px;
  color: var(--text-primary);
  background: var(--bg-card);
  min-width: 200px;
  max-width: 300px;
  transition: all 0.3s;
}

.agent-name-input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
  background: white;
}

.header-right {
  display: flex;
  gap: 12px;
}

.btn-ghost {
  padding: 8px 16px;
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  background: var(--bg-card);
  color: var(--text-primary);
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}

.btn-ghost:hover {
  background: var(--bg-elevated);
  border-color: var(--primary);
  color: var(--primary);
}

.btn-primary {
  padding: 8px 16px;
  border: 1px solid var(--primary);
  border-radius: var(--radius-md);
  background: var(--primary);
  color: white;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}

.btn-primary:hover {
  background: var(--primary-light);
  border-color: var(--primary-light);
}

.builder-main {
  flex: 1;
  display: flex;
  gap: 32px;
  border-top: 1px solid var(--border-default);
  overflow: hidden;
}

.builder-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 32px;
  overflow: hidden;
}

.float-toolbar {
  position: fixed;
  bottom: 32px;
  left: 32px;
  z-index: 100;
}

.toolbar-group {
  display: flex;
  align-items: center;
  gap: 8px;
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-xl);
  padding: 8px;
  box-shadow: var(--shadow-card);
}

.toolbar-btn {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: var(--radius-md);
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.toolbar-btn:hover {
  background: var(--bg-elevated);
  color: var(--primary);
}

.toolbar-divider {
  width: 1px;
  height: 24px;
  background: var(--border-default);
}
</style>
