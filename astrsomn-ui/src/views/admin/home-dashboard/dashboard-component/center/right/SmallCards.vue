<template>
  <div class="small-cards col-span-2">
    <div class="bento-card clickable" @click="navigateTo('/admin/models')">
      <img class="card-icon" src="@/assets/dashboard-icons/models-color.svg" alt="models" />
      <h3 class="card-title-small">模型接入</h3>
      <p class="card-desc-small">定义供应商 API 路径与能力映射</p>
    </div>

    <div class="bento-card clickable" @click="navigateTo('/admin/ai-instance')">
      <img class="card-icon" src="@/assets/dashboard-icons/extension-color.svg" alt="instance" />
      <h3 class="card-title-small">推理配置</h3>
      <p class="card-desc-small">定义模型运行参数与 Token 限制</p>
    </div>

    <div class="bento-card clickable" @click="navigateTo('/admin/mcp')">
      <img class="card-icon" src="@/assets/dashboard-icons/mcp-color.svg" alt="mcp" />
      <h3 class="card-title-small">AI MCP</h3>
      <p class="card-desc-small">管理 Model Context Protocol 服务</p>
    </div>

    <div class="bento-card clickable" @click="navigateTo('/admin/tools')">
      <img class="card-icon" src="@/assets/dashboard-icons/tools-color.svg" alt="tools" />
      <h3 class="card-title-small">工具插件</h3>
      <p class="card-desc-small">维护函数调用与 API 工具</p>
    </div>

    <div class="bento-card clickable" @click="navigateTo('/admin/ai-account')">
      <img class="card-icon" src="@/assets/dashboard-icons/credentials-color.svg" alt="account" />
      <h3 class="card-title-small">凭证管理</h3>
      <p class="card-desc-small">维护 API_KEY 与账户额度</p>
    </div>

    <div class="discovery-hub-container">
      <DiscoveryHub />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import DiscoveryHub from '@/components/dashboard/DiscoveryHub.vue'

const router = useRouter()

const navigateTo = (path: string) => {
  router.push(path)
}
</script>

<style scoped>
.small-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  height: 100%;
}

.bento-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.bento-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border-color: #3b82f6;
}

.discovery-hub-container {
  grid-column: span 3;
  height: 100%;
  min-height: 180px;
}

.card-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  margin-bottom: 8px;
}

.card-title-small {
  font-size: 13px;
  font-weight: bold;
  color: #1f2937;
  margin: 0;
}

.card-desc-small {
  font-size: 10px;
  color: #9ca3af;
  margin: 2px 0 0;
}

.clickable {
  cursor: pointer;
}

.clickable:hover {
  border-color: #3b82f6;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
}
</style>