<template>
  <div class="small-cards col-span-2">
    <div class="bento-card clickable" @click="navigateTo('/admin/models')">
      <div class="card-icon blue-icon"><DatabaseOutlined /></div>
      <h3 class="card-title-small">模型接入</h3>
      <p class="card-desc-small">定义供应商 API 路径与能力映射</p>
    </div>

    <div class="bento-card clickable" @click="navigateTo('/admin/ai-instance')">
      <div class="card-icon purple-icon"><SettingOutlined /></div>
      <h3 class="card-title-small">推理配置</h3>
      <p class="card-desc-small">定义模型运行参数与 Token 限制</p>
    </div>

    <div class="bento-card clickable" @click="navigateTo('/admin/mcp')">
      <div class="card-icon indigo-icon"><ApiOutlined /></div>
      <h3 class="card-title-small">AI MCP</h3>
      <p class="card-desc-small">管理 Model Context Protocol 服务</p>
    </div>

    <div class="bento-card clickable" @click="navigateTo('/admin/tools')">
      <div class="card-icon teal-icon"><ToolOutlined /></div>
      <h3 class="card-title-small">工具插件</h3>
      <p class="card-desc-small">维护函数调用与 API 工具</p>
    </div>

    <div class="bento-card clickable" @click="navigateTo('/admin/ai-account')">
      <div class="card-icon sky-icon"><KeyOutlined /></div>
      <h3 class="card-title-small">凭证管理</h3>
      <p class="card-desc-small">维护 API_KEY 与账户额度</p>
    </div>

    <div class="bento-card clickable" @click="navigateTo('/admin/security')">
      <div class="card-icon red-icon"><SecurityScanOutlined /></div>
      <h3 class="card-title-small">安全治理</h3>
      <p class="card-desc-small">管理安全策略与访问控制</p>
    </div>

    <div class="discovery-hub-container">
      <DiscoveryHub />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import DiscoveryHub from '@/components/dashboard/DiscoveryHub.vue'
import { 
  DatabaseOutlined, 
  SettingOutlined, 
  ApiOutlined, 
  ToolOutlined, 
  KeyOutlined, 
  SecurityScanOutlined,
} from '@ant-design/icons-vue'

const router = useRouter()

const navigateTo = (path: string) => {
  router.push(path)
}
</script>

<style scoped>
.small-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  height: 100%;
}

.bento-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  min-height: 100px;
}

.bento-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border-color: #3b82f6;
}

.discovery-hub-container {
  grid-column: span 2;
  height: 100%;
  min-height: 180px;
  width: 100%;
  box-sizing: border-box;
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
  background: #e6f7ff;
  color: #1890ff;
}

.purple-icon {
  background: #f9f0ff;
  color: #722ed1;
}

.indigo-icon {
  background: #f0f5ff;
  color: #409eff;
}

.teal-icon {
  background: #e6fffb;
  color: #13c2c2;
}

.sky-icon {
  background: #e6f7ff;
  color: #1890ff;
}

.red-icon {
  background: #fff2f0;
  color: #ff4d4f;
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