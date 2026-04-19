<template>
  <div class="small-cards col-span-2">
    <div class="bento-card">
      <img class="card-icon" src="@/assets/dashboard-icons/gateway-color.svg" alt="gateway" />
      <h3 class="card-title-small">负载均衡与网关</h3>
      <p class="card-desc-small">全局多 Key 调度与平滑轮询策略</p>
      
      <div class="progress-section">
        <div class="progress-header">
          <span>当前主策略</span>
          <span class="text-primary">最小并发优先</span>
        </div>
        <div class="progress-bar">
          <div class="progress-fill" style="width: 65%"></div>
        </div>
        <p class="progress-label">DeepSeek-V3 节点负荷 65%</p>
      </div>
    </div>

    <div class="bento-card">
      <img class="card-icon" src="@/assets/dashboard-icons/knowledge-color.svg" alt="knowledge" />
      <h3 class="card-title-small">知识库 (RAG)</h3>
      <p class="card-desc-small">向量空间索引与片段管理</p>
      <div class="chart-section">
        <div>
          <p class="chart-value">12</p>
          <p class="chart-label">已挂载空间</p>
        </div>
        <div class="chart-bars">
          <div class="bar" style="height: 16px"></div>
          <div class="bar" style="height: 24px"></div>
          <div class="bar" style="height: 12px"></div>
          <div class="bar" style="height: 20px"></div>
        </div>
      </div>
    </div>

    <div class="bento-card col-span-2 tool-bus-card">
      <div class="tool-bus-header">
        <img class="card-icon card-icon--inline" src="@/assets/dashboard-icons/mcp-color.svg" alt="mcp" />
        <div class="tool-bus-info">
          <div>
            <h3 class="card-title-small">跨服务工具总线</h3>
            <p class="card-desc-small">分布式 Tool 注册、发现与 RBAC 鉴权</p>
          </div>
        </div>
        <span class="badge">全局可用</span>
      </div>
      
      <div class="tool-grid">
        <div class="tool-item">
          <img src="@/assets/dashboard-icons/tools-color.svg" alt="tool" class="tool-icon" />
          <div>
            <p class="tool-name">querySalary</p>
            <p class="tool-source">来源: 财务系统 (Service-A)</p>
          </div>
        </div>
        <div class="tool-item">
          <img src="@/assets/dashboard-icons/tools-color.svg" alt="tool" class="tool-icon" />
          <div>
            <p class="tool-name">checkStock</p>
            <p class="tool-source">来源: 仓储系统 (Service-B)</p>
          </div>
        </div>
      </div>
    </div>

    <div class="bento-card">
      <img class="card-icon" src="@/assets/dashboard-icons/users-color.svg" alt="users" />
      <h3 class="card-title-small">用户与角色</h3>
      <p class="card-desc-small">业务系统/环境管理员权限矩阵</p>
    </div>

    <div class="bento-card clickable" @click="navigateToResourceLibrary">
      <img class="card-icon" src="@/assets/dashboard-icons/extension-color.svg" alt="apps" />
      <h3 class="card-title-small">全部应用</h3>
      <p class="card-desc-small">查看所有可用应用和资源入口</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const navigateToResourceLibrary = () => {
  router.push('/admin/resource-library')
}
</script>

<style scoped>
.small-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.bento-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 24px;
  padding: 24px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.bento-card:hover {
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05);
  border-color: #3b82f6;
}

.col-span-2 {
  grid-column: span 2;
}

.card-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.card-icon--inline {
  margin-bottom: 0;
}

.tool-icon {
  width: 32px;
  height: 32px;
  flex-shrink: 0;
}

.clickable {
  cursor: pointer;
}

.clickable:hover {
  border-color: #3b82f6;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
}

.card-title-small {
  font-size: 16px;
  font-weight: bold;
  color: #1f2937;
  margin: 0;
}

.card-desc-small {
  font-size: 12px;
  color: #9ca3af;
  margin: 4px 0 0;
}

.progress-section {
  margin-top: 16px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 12px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  font-size: 10px;
  font-weight: bold;
  color: #6b7280;
  text-transform: uppercase;
}

.text-primary {
  color: #2563eb;
}

.progress-bar {
  height: 6px;
  background: #e5e7eb;
  border-radius: 9999px;
  overflow: hidden;
  margin-top: 8px;
}

.progress-fill {
  height: 100%;
  background: #3b82f6;
}

.progress-label {
  font-size: 10px;
  color: #9ca3af;
  margin: 8px 0 0;
}

.chart-section {
  margin-top: 24px;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.chart-value {
  font-size: 24px;
  font-weight: 900;
  color: #1f2937;
  margin: 0;
}

.chart-label {
  font-size: 10px;
  font-weight: bold;
  color: #9ca3af;
  text-transform: uppercase;
  margin: 4px 0 0;
}

.chart-bars {
  display: flex;
  gap: 4px;
  align-items: flex-end;
}

.bar {
  width: 4px;
  background: #c4b5fd;
  border-radius: 2px;
}

.bar:nth-child(2) { background: #a78bfa; }
.bar:nth-child(4) { background: #8b5cf6; }

.tool-bus-card {
  border-left: 4px solid #3b82f6;
}

.tool-bus-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.tool-bus-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.badge {
  background: #ffedd5;
  color: #ea580c;
  font-size: 10px;
  font-weight: bold;
  padding: 4px 8px;
  border-radius: 4px;
}

.tool-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-top: 16px;
}

.tool-item {
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: border-color 0.2s;
}

.tool-item:hover {
  border-color: #fdba74;
}

.tool-name {
  font-size: 14px;
  font-weight: bold;
  color: #374151;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.tool-source {
  font-size: 10px;
  color: #9ca3af;
  margin: 4px 0 0;
}

.text-orange-400 {
  color: #fb923c;
}
</style>
