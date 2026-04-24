<template>
  <div class="ultra-card main-brain-card">
    <div class="card-header">
      <div class="header-content">
        <h2 class="card-title">智能体大脑</h2>
        <p class="card-desc">封装推理逻辑与 Agent 运行时状态</p>
      </div>
      <div class="header-actions">
        <button class="icon-btn" title="查看全部" @click="navigateToAgents">
          <SecurityScanOutlined />
        </button>
        <button class="icon-btn btn-primary-icon" title="新建智能体" @click="navigateToAgents">
          <PlusOutlined />
        </button>
      </div>
    </div>

    <div v-if="loading" class="loading-wrap">
      <a-spin size="small" />
    </div>

    <div v-else-if="list.length > 0" class="agent-list-wrapper custom-scrollbar">
      <div
        v-for="item in list"
        :key="item.id ?? item.agentKey ?? item.agentName"
        class="agent-item-premium"
        @click="navigateToAgents"
      >
        <div class="item-identity">
          <div class="icon-container">
            <img class="agent-avatar" src="../../../../../../../assets/dashboard-icons/agents-color.svg" alt="agent" />
            <div v-if="item.status === 'enabled'" class="alive-indicator"></div>
          </div>
          
          <div class="info-cluster">
            <div class="top-line">
              <span class="name">{{ item.agentName }}</span>
              <span v-if="item.envCode" :class="['premium-tag', item.envCode]">
                {{ item.envCode }}
              </span>
            </div>
            <div class="bottom-line">
              <span class="meta-info">{{ getAgentMeta(item) }}</span>
              <span class="dot-split"></span>
              <span class="key-display">{{ item.agentKey || 'NO_KEY' }}</span>
            </div>
          </div>
        </div>

        <div class="item-visual-density">
          <div class="mini-sparkline">
            <div class="spark-bar" style="height: 40%"></div>
            <div class="spark-bar" style="height: 70%"></div>
            <div class="spark-bar" style="height: 50%"></div>
            <div class="spark-bar active" style="height: 90%"></div>
            <div class="spark-bar" style="height: 60%"></div>
          </div>
        </div>

        <div class="item-meta-status">
          <div :class="['status-indicator', item.status === 'enabled' ? 'is-active' : 'is-inactive']">
            {{ item.status === 'enabled' ? 'ON' : 'OFF' }}
          </div>
          <span class="timestamp">{{ formatTime(item.createTime) }}</span>
        </div>
      </div>
    </div>

    <div v-else class="empty-placeholder">
      <div class="empty-icon"><TeamOutlined /></div>
      <p>暂无配置智能体</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { aiAgentApi, type AiAgent } from '@/api/aiAgent.ts'
import { 
  SecurityScanOutlined, 
  PlusOutlined, 
  TeamOutlined 
} from '@ant-design/icons-vue'

const router = useRouter()
const loading = ref(false)
const list = ref<AiAgent[]>([])

const navigateToAgents = () => {
  router.push('/admin/agents')
}

const getAgentMeta = (item: AiAgent) => {
  if (item.modelName) return item.modelName
  if (item.chatInstanceName) return item.chatInstanceName
  return '未配模型'
}

const formatTime = (raw?: string) => {
  if (!raw) return ''
  // 仅提取 HH:mm 增强极简感，如果需要日期可自行修改
  return raw.includes('T') ? raw.split('T')[1].slice(0, 5) : raw.slice(11, 16)
}

const fetchList = async () => {
  loading.value = true
  try {
    const resp = await aiAgentApi.queryPage({
      pageNo: 1,
      pageSize: 5, // 稍微增加信息密度
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
/* 容器：去掉了厚重的边框，使用极浅的背景偏移 */
.ultra-card {
  background: var(--bg-card);
  border-radius: 24px;
  padding: 24px 28px;
  display: flex;
  flex-direction: column;
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  border: 1px solid #0c1521;
  height: 100%;
}

.ultra-card:hover {
  box-shadow: var(--shadow-card);
}

/* Header: 纯图标化 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.card-title {
  font-size: 17px;
  font-weight: 800;
  color: var(--text-heading);
  margin: 0;
  letter-spacing: -0.01em;
}

.card-desc {
  font-size: 11px;
  color: var(--text-muted);
  margin: 2px 0 0;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.icon-btn {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  border: none;
  background: var(--bg-elevated);
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}

.icon-btn:hover {
  background: var(--bg-input);
  color: var(--text-primary);
}

.btn-primary-icon {
  background: var(--primary);
  color: #ffffff;
}

.btn-primary-icon:hover {
  background: var(--primary-light);
  color: #ffffff;
}

/* 列表区域 */
.agent-list-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
}

.agent-item-premium {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid #0f766e;
}

.agent-item-premium:hover {

  background: var(--bg-elevated);
  border-color: var(--border-default);
}

/* 身份信息 */
.item-identity {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 3;
  min-width: 0;
}

.icon-container {
  position: relative;
  width: 40px;
  height: 40px;
  background: var(--bg-elevated);
  border-radius: 12px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.agent-avatar {
  width: 24px;
  height: 24px;
}

.alive-indicator {
  position: absolute;
  bottom: -1px;
  right: -1px;
  width: 8px;
  height: 8px;
  background: #22c55e;
  border: 1.5px solid var(--bg-card);
  border-radius: 50%;
}

.info-cluster {
  min-width: 0;
}

.top-line {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 2px;
}

.name {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.premium-tag {
  font-size: 9px;
  font-weight: 800;
  padding: 1px 4px;
  border-radius: 5px;
  text-transform: uppercase;
  background: var(--bg-elevated);
  color: var(--text-secondary);
}

.premium-tag.prod { background: rgba(82, 196, 26, 0.15); color: #52c41a; }

.bottom-line {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  color: var(--text-muted);
}

.dot-split {
  width: 3px;
  height: 3px;
  background: var(--border-default);
  border-radius: 50%;
}

.key-display {
  font-family: ui-monospace, monospace;
  opacity: 0.6;
}

/* 视觉密度图 (Sparkline) */
.item-visual-density {
  flex: 1;
  display: flex;
  justify-content: center;
}

.mini-sparkline {
  display: flex;
  align-items: flex-end;
  gap: 2px;
  height: 16px;
}

.spark-bar {
  width: 2px;
  background: var(--border-default);
  border-radius: 4px;
}

.spark-bar.active {
  background: var(--primary);
}

/* 状态与时间 */
.item-meta-status {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.status-indicator {
  font-size: 10px;
  font-weight: 900;
  padding: 2px 6px;
  border-radius: 6px;
}

.status-indicator.is-active {
  background: rgba(82, 196, 26, 0.12);
  color: #52c41a;
}

.status-indicator.is-inactive {
  background: var(--bg-elevated);
  color: var(--text-muted);
}

.timestamp {
  font-size: 10px;
  color: var(--text-muted);
  font-weight: 500;
}

/* 滚动条优化 */
.custom-scrollbar::-webkit-scrollbar {
  width: 3px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: var(--border-default);
  border-radius: 10px;
}

/* 其他辅助 */
.loading-wrap, .empty-placeholder {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 200px;
}

.empty-icon {
  font-size: 24px;
  color: var(--text-muted);
  margin-bottom: 8px;
}

.empty-placeholder p {
  font-size: 12px;
  color: var(--text-muted);
}
</style>