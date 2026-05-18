<template>
  <div class="agent-list-container">
    <!-- 顶部统计头 -->
    <div class="list-header">
      <div class="header-left">
        <div v-if="props.providerAvatar" class="header-avatar">
          <img :src="props.providerAvatar" :alt="props.providerName" class="header-avatar-img"/>
        </div>
        <div v-else-if="props.providerName" class="header-avatar header-avatar-placeholder">
          <span>{{ props.providerName.slice(0, 1).toUpperCase() }}</span>
        </div>
        <div class="header-info">
          <h2 class="list-title">{{ props.providerName || '所有 Agents' }}</h2>
          <p v-if="props.providerDescription" class="list-subtitle">{{ props.providerDescription }}</p>
        </div>
      </div>
      <div class="header-stats">
        <div class="stat-card">
          <div class="stat-icon-wrap stat-icon-blue">
            <LineChartOutlined/>
          </div>
          <div class="stat-body">
            <span class="stat-value">12.8K</span>
            <span class="stat-label">今日调用量</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon-wrap stat-icon-purple">
            <ThunderboltOutlined/>
          </div>
          <div class="stat-body">
            <span class="stat-value">2.4M</span>
            <span class="stat-label">Token 消耗</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon-wrap stat-icon-green">
            <DashboardOutlined/>
          </div>
          <div class="stat-body">
            <span class="stat-value">320<span class="stat-unit">ms</span></span>
            <span class="stat-label">平均延迟</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon-wrap stat-icon-orange">
            <FundOutlined/>
          </div>
          <div class="stat-body">
            <span class="stat-value">99.2<span class="stat-unit">%</span></span>
            <span class="stat-label">成功率</span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <a-spin size="large"/>
    </div>

    <div v-else class="agent-grid">
      <!-- Agent 卡片 -->
      <div
          v-for="agent in agents"
          :key="agent.id"
          class="agent-card"
          @click="handleSelect(agent)"
      >
        <div class="card-header">
          <div class="card-avatar">
            <img v-if="isImageAvatar(agent.agentAvatar)" :src="agent.agentAvatar" alt="avatar" class="card-avatar-img"/>
            <component v-else-if="isIconAvatar(agent.agentAvatar)" :is="getAvatarComponent(agent.agentAvatar)" class="card-avatar-icon"/>
            <span v-else class="card-avatar-fallback">{{ (agent.agentName || '?').slice(0, 1).toUpperCase() }}</span>
          </div>
          <div class="card-header-text">
            <h3 class="card-title">{{ agent.agentName }}</h3>
            <a-tag :class="['status-dot-tag', agent.status]" size="small">
              {{ agent.status === 'enabled' ? '运行中' : '草稿' }}
            </a-tag>
          </div>
        </div>

        <p class="card-prompt">{{ agent.promptContent || agent.promptTitle || '暂无系统指令' }}</p>

        <div class="card-meta">
          <span v-if="agent.instanceList?.length" class="meta-chip">
            <CloudServerOutlined/> {{ agent.instanceList.length }}
          </span>
          <span v-if="agent.toolKeys" class="meta-chip">
            <ToolOutlined/> {{ agent.toolKeys.split(',').filter(Boolean).length }}
          </span>
          <span v-if="agent.mcpKeys" class="meta-chip">
            <ApiOutlined/> {{ agent.mcpKeys.split(',').filter(Boolean).length }}
          </span>
          <span class="meta-spacer"></span>
          <span class="edit-link">编辑 <RightOutlined/></span>
        </div>
      </div>

      <!-- 添加卡片 -->
      <div class="add-card" @click="handleCreate">
        <PlusOutlined class="add-icon"/>
        <span class="add-text">新建 Agent</span>
      </div>
    </div>

    <div v-if="!loading && agents.length === 0" class="empty-container">
      <a-empty description="暂无 Agent"/>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {ref, watch} from 'vue'
import {
  ApiOutlined,
  CloudServerOutlined,
  CodeOutlined,
  CrownOutlined,
  DashboardOutlined,
  FireOutlined,
  FundOutlined,
  GlobalOutlined,
  HeartOutlined,
  LineChartOutlined,
  PlusOutlined,
  RightOutlined,
  RobotOutlined,
  RocketOutlined,
  StarOutlined,
  ThunderboltOutlined,
  ToolOutlined,
} from '@ant-design/icons-vue'
import {type AiAgent, aiAgentApi, type PageResponse} from '@/api/aiAgent.ts'

const props = defineProps<{
  providerKey?: string
  providerName?: string
  providerDescription?: string
  providerAvatar?: string
}>()

const emit = defineEmits(['select', 'create'])

const loading = ref(false)
const agents = ref<AiAgent[]>([])


const avatarIconMap: Record<string, any> = {
  RobotOutlined, RocketOutlined, ThunderboltOutlined, StarOutlined,
  HeartOutlined, FireOutlined, CrownOutlined, GlobalOutlined,
  CodeOutlined, CloudServerOutlined,
}

function isImageAvatar(val?: string): boolean {
  return !!val && val.startsWith('data:image')
}

function isIconAvatar(val?: string): boolean {
  return !!val && !!avatarIconMap[val]
}

function getAvatarComponent(val?: string) {
  return avatarIconMap[val || ''] || RobotOutlined
}


const fetchAgents = async () => {
  loading.value = true
  try {
    const resp: PageResponse<AiAgent> = await aiAgentApi.queryPage({
      pageNo: 1,
      pageSize: 50,
      param: {
        extensionCode: props.providerKey && props.providerKey !== 'all' ? props.providerKey : undefined,
      },
    })
    agents.value = resp.list || []
  } catch (error) {
    console.error('Failed to fetch agents:', error)
    agents.value = []
  } finally {
    loading.value = false
  }
}

const handleSelect = (agent: AiAgent) => emit('select', agent)
const handleCreate = () => emit('create')

watch(() => props.providerKey, () => void fetchAgents())
void fetchAgents()
</script>

<style scoped>
.agent-list-container {
  padding: 20px 24px;
  flex: 1;
  overflow-y: auto;
}


.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.header-avatar {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.header-avatar-placeholder {
  background: linear-gradient(135deg, var(--primary) 0%, #7c3aed 100%);
  color: #fff;
  font-size: 18px;
  font-weight: 700;
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.list-title {
  font-size: 18px;
  font-weight: 800;
  color: var(--text-primary);
  margin: 0;
  letter-spacing: -0.02em;
}

.list-subtitle {
  font-size: 12px;
  color: var(--text-muted);
  margin: 0;
}


.header-stats {
  display: flex;
  gap: 10px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: 10px;
  min-width: 130px;
}

.stat-icon-wrap {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  flex-shrink: 0;
}

.stat-icon-blue {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.stat-icon-purple {
  background: rgba(168, 85, 247, 0.1);
  color: #a855f7;
}

.stat-icon-green {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.stat-icon-orange {
  background: rgba(249, 115, 22, 0.1);
  color: #f97316;
}

.stat-body {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.stat-value {
  font-size: 16px;
  font-weight: 800;
  color: var(--text-primary);
  font-family: 'SF Mono', 'Fira Code', monospace;
  line-height: 1.1;
}

.stat-unit {
  font-size: 11px;
  font-weight: 600;
  opacity: 0.6;
}

.stat-label {
  font-size: 10px;
  color: var(--text-muted);
  font-weight: 500;
}


.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 60px;
}

.empty-container {
  padding: 60px;
}


.agent-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 14px;
}


.agent-card {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.agent-card:hover {
  border-color: var(--primary);
  box-shadow: 0 4px 20px -4px rgba(59, 130, 246, 0.15);
  transform: translateY(-1px);
}


.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-avatar {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  overflow: hidden;
}

.card-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-avatar-icon {
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-avatar-fallback {
  font-size: 16px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.card-header-text {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.card-title {
  font-size: 13px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-dot-tag {
  font-size: 9px;
  font-weight: 600;
  padding: 1px 7px;
  border-radius: 10px;
  border: none;
  flex-shrink: 0;
  line-height: 18px;
}

.status-dot-tag.enabled {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.status-dot-tag.disabled {
  background: rgba(107, 114, 128, 0.1);
  color: #9ca3af;
}


.card-prompt {
  font-size: 11px;
  color: var(--text-muted);
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-clamp: 2;
  overflow: hidden;
  min-height: 35px;
}


.card-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: auto;
}

.meta-chip {
  font-size: 10px;
  color: var(--text-muted);
  display: inline-flex;
  align-items: center;
  gap: 3px;
  padding: 2px 7px;
  background: var(--bg-elevated);
  border-radius: 6px;
  font-weight: 500;
}

.meta-chip :deep(svg) {
  font-size: 10px;
}

.meta-spacer {
  flex: 1;
}

.edit-link {
  font-size: 10px;
  color: var(--primary);
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 3px;
  opacity: 0;
  transition: opacity 0.15s;
}

.agent-card:hover .edit-link {
  opacity: 1;
}

.edit-link :deep(svg) {
  font-size: 8px;
}


.add-card {
  border: 2px dashed var(--border-subtle);
  background: transparent;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 140px;
  color: var(--text-muted);
}

.add-card:hover {
  border-color: var(--primary);
  color: var(--primary);
  background: rgba(59, 130, 246, 0.02);
}

.add-icon {
  font-size: 22px;
  opacity: 0.4;
  transition: opacity 0.2s;
}

.add-card:hover .add-icon {
  opacity: 0.8;
}

.add-text {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.5px;
}
</style>
