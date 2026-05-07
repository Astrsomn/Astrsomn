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
      <AgentListItem
        v-for="item in list"
        :key="item.id ?? item.agentKey ?? item.agentName"
        :item="item"
        :loading="item.id ? switchingIds.has(item.id) : false"
        @click="navigateToAgents"
        @status-change="onToggleStatus"
        @edit="onEditAgent"
      />
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
import { message } from 'ant-design-vue'
import { aiAgentApi, type AiAgent } from '@/api/aiAgent.ts'
import AgentListItem from './AgentListItem.vue'
import {
  SecurityScanOutlined,
  PlusOutlined,
  TeamOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const loading = ref(false)
const list = ref<AiAgent[]>([])
const switchingIds = ref<Set<string | number>>(new Set())

const navigateToAgents = () => {
  router.push('/admin/ai-config/agents')
}

const onEditAgent = (item: AiAgent) => {
  if (!item.id) {
    message.warning('该智能体缺少 ID，无法编辑')
    return
  }
  router.push({ path: '/admin/ai-config/builder', query: { id: item.id.toString() } })
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

const onToggleStatus = async (item: AiAgent, checked: boolean) => {
  if (!item.id) {
    message.warning('该智能体缺少 ID，无法更新状态')
    return
  }
  if (switchingIds.value.has(item.id)) return

  const nextStatus = checked ? 'enabled' : 'disabled'
  if (item.status === nextStatus) return

  switchingIds.value.add(item.id)
  try {
    await aiAgentApi.update({
      ...item,
      status: nextStatus
    })
    item.status = nextStatus
    message.success(checked ? '已启用' : '已禁用')
  } catch {
    message.error('状态更新失败，请稍后重试')
  } finally {
    switchingIds.value.delete(item.id)
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
  border-radius: 10px;
  padding: 14px 16px;
  display: flex;
  flex-direction: column;
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  border: 1px solid var(--border-default);

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
  margin-bottom: 12px;
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
  border: 1px solid var(--border-default);
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
  border-color: var(--primary);
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
  gap: 10px;
  flex: 1;
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

:global(.dark) .icon-btn {
  background: color-mix(in srgb, var(--bg-elevated) 85%, #000);
}
</style>