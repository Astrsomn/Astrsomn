<template>
  <div class="small-cards col-span-2">
    <!-- 第一行：全部应用入口 -->
    <div class="discovery-hub-container">
      <DiscoveryHub />
    </div>
    
    <!-- 第一行：凭证管理 -->
    <div class="credential-card-container">
      <CredentialManagementCard />
    </div>
    
    <!-- 第二行：模型接入和 MCP -->
    <div class="cards-row">
      <!-- 模型接入 -->
      <div class="models-card">
        <ModelAccessCard />
      </div>

      <!-- AI MCP -->
      <div class="mcp-card">
        <McpCard />
      </div>
    </div>

    <!-- 第三行：推理配置和工具插件 -->
    <div class="cards-row">
      <!-- 推理配置 -->
      <div class="inference-card">
        <InferenceConfigCard />
      </div>

      <!-- 工具插件 -->
      <div class="tools-card">
        <ToolPluginCard />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import DiscoveryHub from '@/components/dashboard/DiscoveryHub.vue'
import CredentialManagementCard from '@/components/dashboard/CredentialManagementCard.vue'
import ModelAccessCard from '@/components/dashboard/ModelAccessCard.vue'
import McpCard from '@/components/dashboard/McpCard.vue'
import InferenceConfigCard from '@/components/dashboard/InferenceConfigCard.vue'
import ToolPluginCard from '@/components/dashboard/ToolPluginCard.vue'

const router = useRouter()

const navigateTo = (path: string) => {
  router.push(path)
}
</script>

<style scoped>
.small-cards {
  display: grid;
  grid-template-columns: 2fr 1fr;
  grid-template-rows: auto auto auto;
  gap: 16px;
  height: 100%;
}

.bento-card {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: 16px;
  padding: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0.2, 0.2, 1);
  min-height: 100px;
}

.bento-card:hover {
  box-shadow: var(--shadow-card);
  border-color: var(--primary);
}

.discovery-hub-container {
  grid-row: 1;
  grid-column: 1;
  height: 100%;
  width: 100%;
  box-sizing: border-box;
}

.credential-card-container {
  grid-row: 1;
  grid-column: 2;
  height: 100%;
  box-sizing: border-box;
}

/* 卡片行容器 */
.cards-row {
  grid-column: 1 / span 2;
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
  height: 100%;
}

/* 第二行：模型接入和 MCP */
.cards-row:nth-child(3) {
  grid-row: 2;
}

/* 第三行：推理配置和工具插件 */
.cards-row:nth-child(4) {
  grid-row: 3;
}

/* 占两列的卡片 */
.models-card,
.inference-card {
  grid-column: 1;
}

/* 占一列的卡片 */
.mcp-card,
.tools-card {
  grid-column: 2;
}

.card-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.blue-icon {
  background: var(--primary-hover);
  color: var(--primary);
}

.purple-icon {
  background: rgba(114, 46, 209, 0.15);
  color: #a855f7;
}

.indigo-icon {
  background: rgba(99, 102, 241, 0.15);
  color: #6366f1;
}

.teal-icon {
  background: rgba(20, 184, 166, 0.15);
  color: #14b8a6;
}

.card-title-small {
  font-size: 13px;
  font-weight: bold;
  color: var(--text-primary);
  margin: 0;
}

.card-desc-small {
  font-size: 10px;
  color: var(--text-muted);
  margin: 2px 0 0;
}

.clickable {
  cursor: pointer;
}

.clickable:hover {
  border-color: var(--primary);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
}
</style>