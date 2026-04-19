<template>
  <div class="bento-card main-card">
    <div class="card-header">
      <div>
        <h2 class="card-title">智能体大脑</h2>
        <p class="card-desc">封装推理逻辑与 Agent 运行时状态</p>
      </div>
      <button class="btn btn-primary" @click="navigateToAgents">
        <i class="fa-solid fa-plus"></i>新建
      </button>
    </div>

    <div v-if="loading" class="loading-wrap">
      <a-spin size="small" />
    </div>

    <div v-else-if="list.length > 0" class="agent-list">
      <div
        v-for="item in list"
        :key="item.id ?? item.agentKey ?? item.agentName ?? 'agent'"
        class="agent-item"
        @click="navigateToAgents"
      >
        <div class="agent-info">
          <img class="agent-icon" src="@/assets/dashboard-icons/agents-color.svg" alt="agent" />
          <div class="agent-details">
            <div class="agent-name-row">
              <h4 class="agent-name">{{ item.agentName }}</h4>
              <span v-if="item.envCode" class="env-tag" :class="item.envCode">
                {{ item.envCode || '默认环境' }}
              </span>
            </div>
            <p class="agent-meta">{{ getAgentMeta(item) }}</p>
            <p v-if="item.description" class="agent-desc">{{ item.description }}</p>
            <div v-if="item.agentKey" class="agent-key">
              <span class="key-label">AGENT KEY:</span>
              <span class="key-value">{{ item.agentKey }}</span>
            </div>
          </div>
        </div>
        <div class="agent-status">
          <span :class="['status-pill', item.status === 'enabled' ? 'status-active' : 'status-inactive']">
            {{ item.status === 'enabled' ? '在线' : '停用' }}
          </span>
          <p v-if="item.createTime" class="create-time">{{ formatTime(item.createTime) }}</p>
        </div>
      </div>
    </div>

    <div v-else class="empty-wrap">
      <span class="empty-text">暂无智能体</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { aiAgentApi, type AiAgent } from '@/api/aiAgent.ts'

const router = useRouter()
const loading = ref(false)
const list = ref<AiAgent[]>([])

const navigateToAgents = () => {
  router.push('/admin/agents')
}

const getAgentMeta = (item: AiAgent) => {
  if (item.modelName) {
    return `模型: ${item.modelName}`
  }
  if (item.chatInstanceName) {
    return `实例: ${item.chatInstanceName}`
  }
  return '未配置模型'
}

const formatTime = (raw?: string) => {
  if (!raw) return ''
  return raw.replace('T', ' ').slice(0, 16)
}

const fetchList = async () => {
  loading.value = true
  try {
    const resp = await aiAgentApi.queryPage({
      pageNo: 1,
      pageSize: 4,
      param: {}
    })
    list.value = resp.list || []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  void fetchList()
})
</script>

<style scoped>
.bento-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  height: 100%;
  display: flex;
  flex-direction: column;
}

.bento-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border-color: #3b82f6;
}

.main-card {
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
  color: #1f2937;
  margin: 0;
}

.card-desc {
  font-size: 11px;
  color: #9ca3af;
  margin: 2px 0 0;
}

.btn {
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: bold;
  cursor: pointer;
  border: none;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  transition: background 0.2s;
}

.btn-primary {
  background: #2563eb;
  color: white;
}

.btn-primary:hover {
  background: #1d4ed8;
}

.loading-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  flex: 1;
}

.agent-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
  overflow: hidden;
}

.agent-item {
  padding: 12px 14px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  cursor: pointer;
  transition: background 0.2s;
  gap: 12px;
}

.agent-item:hover {
  background: #f9fafb;
}

.agent-info {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  flex: 1;
}

.agent-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  flex-shrink: 0;
}

.agent-details {
  flex: 1;
  min-width: 0;
}

.agent-name-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.agent-name {
  font-size: 14px;
  font-weight: bold;
  color: #374151;
  margin: 0;
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.env-tag {
  padding: 2px 8px;
  border-radius: 8px;
  font-size: 9px;
  font-weight: 800;
  color: #6b7280;
  background: #f3f4f6;
  text-transform: uppercase;
  white-space: nowrap;
}

.env-tag.prod, .env-tag.production {
  background: #10b981;
  color: white;
}

.agent-meta {
  font-size: 11px;
  color: #9ca3af;
  margin: 0 0 4px;
}

.agent-desc {
  font-size: 11px;
  color: #6b7280;
  margin: 0 0 6px;
  line-height: 1.4;
  height: 28px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.agent-key {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 4px;
}

.key-label {
  font-size: 9px;
  font-weight: 700;
  color: #9ca3af;
}

.key-value {
  font-size: 10px;
  color: #3b82f6;
  font-family: ui-monospace, monospace;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.agent-status {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
  flex-shrink: 0;
}

.status-pill {
  padding: 3px 8px;
  border-radius: 9999px;
  font-size: 10px;
  font-weight: 600;
  white-space: nowrap;
}

.status-active {
  background: #d1fae5;
  color: #059669;
}

.status-inactive {
  background: #f3f4f6;
  color: #6b7280;
}

.create-time {
  font-size: 10px;
  color: #9ca3af;
  margin: 0;
  white-space: nowrap;
}

.empty-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px 0;
  flex: 1;
}

.empty-text {
  font-size: 12px;
  color: #9ca3af;
}
</style>