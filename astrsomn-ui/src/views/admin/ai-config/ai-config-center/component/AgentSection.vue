<template>
  <div class="agent-list-container">
    <div class="list-header">
      <div v-if="props.providerAvatar" class="header-avatar">
        <img :src="props.providerAvatar" :alt="props.providerName" class="header-avatar-img"/>
      </div>
      <div v-else-if="props.providerName" class="header-avatar header-avatar-placeholder">
        <span>{{ props.providerName.slice(0, 1).toUpperCase() }}</span>
      </div>
      <div class="header-info">
        <h2 class="list-title">{{ props.providerName || '所有 Agents' }}</h2>
        <p v-if="props.providerDescription" class="list-subtitle">{{ props.providerDescription }}</p>
        <p v-else class="list-subtitle">当前 Provider: {{ currentProviderName }}</p>
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <a-spin size="large"/>
    </div>

    <div v-else class="agent-grid">
      <!-- Agent 卡片 -->
      <a-card
          v-for="agent in agents"
          :key="agent.id"
          class="agent-card"
          hoverable
          @click="handleSelect(agent)"
      >
        <div class="card-header">
          <div :class="['icon-wrapper', getIconBg(agent)]">
            <component :is="getIcon(agent)" class="card-icon"/>
          </div>
          <a-tag :class="['status-tag', agent.status]">{{ agent.status === 'enabled' ? '已上线' : '草稿' }}</a-tag>
        </div>
        <h3 class="card-title">{{ agent.agentName }}</h3>
        <p class="card-description">{{ agent.description }}</p>
        <div class="card-footer">
          <div class="instance-tags">
            <a-tag
                v-for="inst in (agent.instanceList || []).slice(0, 3)"
                :key="inst.instanceKey"
                :color="inst.modelType === 'chat' ? 'blue' : 'orange'"
                class="instance-tag"
            >
              {{ inst.instanceName || inst.modelKey }}
            </a-tag>
            <a-tag v-if="(agent.instanceList || []).length > 3" class="instance-tag">
              +{{ agent.instanceList.length - 3 }}
            </a-tag>
            <span v-if="!agent.instanceList || agent.instanceList.length === 0" class="no-instance">暂无实例</span>
          </div>
          <span class="edit-link">
            点击编辑
            <component :is="RightOutlined" class="arrow-icon"/>
          </span>
        </div>
      </a-card>

      <!-- 添加卡片 -->
      <a-card class="add-card" hoverable @click="handleCreate">
        <div class="add-content">
          <component :is="PlusCircleOutlined" class="add-icon"/>
          <span class="add-text">构建新业务</span>
        </div>
      </a-card>
    </div>

    <div v-if="!loading && agents.length === 0" class="empty-container">
      <a-empty description="暂无 Agent"/>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed, ref, watch} from 'vue'
import {
  CloudServerOutlined,
  CodeOutlined,
  GlobalOutlined,
  PlusCircleOutlined,
  RightOutlined,
} from '@ant-design/icons-vue'
import {type AiAgent, aiAgentApi, type PageResponse} from '@/api/aiAgent'

const props = defineProps<{
  providerKey?: string
  providerName?: string
  providerDescription?: string
  providerAvatar?: string
}>()

const emit = defineEmits(['select', 'create'])

const loading = ref(false)
const agents = ref<AiAgent[]>([])

const currentProviderName = computed(() => {
  if (!props.providerKey || props.providerKey === 'all') {
    return '全部'
  }
  return props.providerKey
})

const iconMap: Record<string, any> = {
  translate: GlobalOutlined,
  code: CodeOutlined,
  default: CloudServerOutlined,
}

const getIcon = (agent: AiAgent) => {
  const key = agent.agentKey?.toLowerCase() || 'default'
  if (key.includes('translate')) return GlobalOutlined
  if (key.includes('code') || key.includes('audit')) return CodeOutlined
  return CloudServerOutlined
}

const getIconBg = (agent: AiAgent) => {
  const key = agent.agentKey?.toLowerCase() || 'default'
  if (key.includes('translate')) return 'icon-blue'
  if (key.includes('code') || key.includes('audit')) return 'icon-purple'
  return 'icon-gray'
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

const handleSelect = (agent: AiAgent) => {
  emit('select', agent)
}

const handleCreate = () => {
  emit('create')
}

watch(
    () => props.providerKey,
    () => {
      void fetchAgents()
    }
)

void fetchAgents()
</script>

<style scoped>
.agent-list-container {
  padding: 20px 30px;
  flex: 1;
}

/* 头部 */
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
}

.header-avatar {
  width: 56px;
  height: 56px;
  border-radius: 16px;
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
  background: var(--primary-hover);
  color: var(--primary);
  font-size: 22px;
  font-weight: 700;
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.list-title {
  font-size: 24px;
  font-weight: bold;
  color: var(--text-primary);
  margin: 0;
}

.list-subtitle {
  font-size: 12px;
  color: var(--text-muted);
  margin: 0;
}

.create-btn {
  font-size: 12px;
  font-weight: 600;
}

/* 加载状态 */
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 60px;
}

/* 空状态 */
.empty-container {
  padding: 60px;
}

/* 卡片网格 */
.agent-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

/* Agent 卡片 */
.agent-card {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.agent-card:hover {
  border-color: var(--primary);
  transform: translateY(-4px);
  box-shadow: 0 10px 20px -10px rgba(59, 130, 246, 0.3);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.icon-wrapper {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid;
}

.icon-blue {
  background: rgba(37, 99, 235, 0.1);
  border-color: rgba(59, 130, 246, 0.2);
}

.icon-blue .card-icon {
  color: #60a5fa;
}

.icon-purple {
  background: rgba(147, 51, 234, 0.1);
  border-color: rgba(147, 51, 234, 0.2);
}

.icon-purple .card-icon {
  color: #a78bfa;
}

.icon-gray {
  background: rgba(107, 114, 128, 0.1);
  border-color: rgba(107, 114, 128, 0.2);
}

.icon-gray .card-icon {
  color: #9ca3af;
}

.card-icon {
  font-size: 20px;
}

.status-tag {
  font-size: 9px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 20px;
  border: 1px solid;
}

.status-tag.enabled {
  background: rgba(16, 185, 129, 0.1);
  color: #34d399;
  border-color: rgba(16, 185, 129, 0.2);
}

.status-tag.disabled {
  background: rgba(75, 85, 99, 0.2);
  color: #9ca3af;
  border-color: rgba(75, 85, 99, 0.3);
}

.card-title {
  font-size: 16px;
  font-weight: bold;
  color: var(--text-primary);
  margin: 0 0 8px;
}

.card-description {
  font-size: 12px;
  color: var(--text-muted);
  line-height: 1.6;
  margin: 0 0 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid var(--border-default);
}

.instance-tags {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-wrap: wrap;
}

.instance-tag {
  font-size: 11px;
}

.no-instance {
  font-size: 11px;
  color: var(--text-muted);
}

.edit-link {
  font-size: 11px;
  color: var(--primary);
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.arrow-icon {
  font-size: 8px;
}

/* 添加卡片 */
.add-card {
  border: 2px dashed var(--border-default);
  background: transparent;
  border-radius: var(--radius-lg);
  padding: 20px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 192px;
}

.add-card:hover {
  border-color: var(--primary);
}

.add-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: var(--text-muted);
}

.add-icon {
  font-size: 32px;
  opacity: 0.5;
}

.add-text {
  font-size: 12px;
  font-weight: bold;
  letter-spacing: 1px;
}

.add-card:hover .add-content {
  color: var(--primary);
}

.add-card:hover .add-icon {
  opacity: 1;
}
</style>
