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
          <h2 class="list-title">{{ props.providerName || t.agent.allAgents }}</h2>
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
            <span class="stat-label">{{ t.agent.stats.todayCalls }}</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon-wrap stat-icon-purple">
            <ThunderboltOutlined/>
          </div>
          <div class="stat-body">
            <span class="stat-value">2.4M</span>
            <span class="stat-label">{{ t.agent.stats.tokenUsage }}</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon-wrap stat-icon-green">
            <DashboardOutlined/>
          </div>
          <div class="stat-body">
            <span class="stat-value">320<span class="stat-unit">ms</span></span>
            <span class="stat-label">{{ t.agent.stats.avgLatency }}</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon-wrap stat-icon-orange">
            <FundOutlined/>
          </div>
          <div class="stat-body">
            <span class="stat-value">99.2<span class="stat-unit">%</span></span>
            <span class="stat-label">{{ t.agent.stats.successRate }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <a-spin size="large"/>
    </div>

    <div v-if="!loading" class="agent-grid-section">
      <div v-if="selectedCount > 0" class="batch-action-bar">
        <span class="batch-count">{{ t.agent.batchActions.selected.replace('{n}', String(selectedCount)) }}</span>
        <a-button danger size="small" @click="handleBatchDelete">{{ t.agent.batchActions.delete }}</a-button>
        <a-button size="small" @click="clearSelection">{{ t.agent.batchActions.cancel }}</a-button>
      </div>

      <div class="agent-grid">
      <!-- Agent 卡片 -->
      <div
          v-for="agent in agents"
          :key="agent.id"
          class="agent-card"
          :class="{ 'card-selected': selectedKeys.has(agent.id!) }"
          @click="handleSelect(agent)"
      >
        <a-checkbox
          :checked="selectedKeys.has(agent.id!)"
          class="card-checkbox"
          @click.stop="toggleSelect(agent.id!)"
        />

        <div class="card-header">
          <div class="card-avatar">
            <img v-if="isImageAvatar(agent.agentAvatar)" :src="agent.agentAvatar" alt="avatar" class="card-avatar-img"/>
            <component v-else-if="isIconAvatar(agent.agentAvatar)" :is="getAvatarComponent(agent.agentAvatar)" class="card-avatar-icon"/>
            <span v-else class="card-avatar-fallback">{{ (agent.agentName || '?').slice(0, 1).toUpperCase() }}</span>
          </div>
          <div class="card-header-text">
            <h3 class="card-title">{{ agent.agentName }}</h3>
            <a-tag :class="['status-dot-tag', agent.status]" size="small">
              {{ agent.status === 'enabled' ? t.agent.status.enabled : t.agent.status.disabled }}
            </a-tag>
          </div>
        </div>

        <p class="card-prompt">{{ agent.promptContent || agent.promptTitle || t.agent.noPrompt }}</p>

        <div v-if="agent.modelName" class="card-model-row">
          <CloudServerOutlined class="card-model-icon"/>
          <span class="card-model-name">{{ agent.modelName }}</span>
        </div>

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
          <a-popconfirm
            :title="t.agent.deleteConfirm"
            :ok-text="t.agent.confirm"
            :cancel-text="t.agent.cancel"
            @confirm="handleDeleteOne(agent.id!)"
          >
            <button class="action-delete-btn" @click.stop><DeleteOutlined /></button>
          </a-popconfirm>
          <span class="edit-link">{{ t.agent.edit }} <RightOutlined/></span>
        </div>
      </div>

      <!-- 添加卡片 -->
      <div class="add-card" @click="handleCreate">
        <PlusOutlined class="add-icon"/>
        <span class="add-text">{{ t.agent.create }}</span>
      </div>
    </div>
  </div>
  </div>
</template>

<script lang="ts" setup>
import {computed, ref, watch} from 'vue'
import {
  ApiOutlined,
  CloudServerOutlined,
  CodeOutlined,
  CrownOutlined,
  DashboardOutlined,
  DeleteOutlined,
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
import {Modal, message} from 'ant-design-vue'
import {type AiAgent, aiAgentApi, type PageResponse} from '@/api/aiAgent.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const props = defineProps<{
  providerKey?: string
  providerName?: string
  providerDescription?: string
  providerAvatar?: string
}>()

const emit = defineEmits(['select', 'create'])

const loading = ref(false)
const agents = ref<AiAgent[]>([])
const t = usePageTranslation('ai-config-center')


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

const selectedKeys = ref<Set<string | number>>(new Set())
const selectedCount = computed(() => selectedKeys.value.size)

function toggleSelect(id: string | number) {
  const next = new Set(selectedKeys.value)
  if (next.has(id)) {
    next.delete(id)
  } else {
    next.add(id)
  }
  selectedKeys.value = next
}

function clearSelection() {
  selectedKeys.value = new Set()
}

const handleSelect = (agent: AiAgent) => emit('select', agent)
const handleCreate = () => emit('create')

async function handleDeleteOne(id: string | number) {
  try {
    await aiAgentApi.delete([id])
    message.success(t.value.agent.deleteSuccess)
    selectedKeys.value.delete(id)
    selectedKeys.value = new Set(selectedKeys.value)
    await fetchAgents()
  } catch (e: any) {
    message.error(e?.message || t.value.agent.deleteFailed)
  }
}

async function handleBatchDelete() {
  const ids = Array.from(selectedKeys.value)
  if (ids.length === 0) return
  Modal.confirm({
    title: t.value.agent.batchDeleteConfirm.replace('{n}', String(ids.length)),
    okText: t.value.agent.confirm,
    cancelText: t.value.agent.cancel,
    onOk: async () => {
      try {
        await aiAgentApi.delete(ids)
        message.success(t.value.agent.deleteSuccess)
        selectedKeys.value = new Set()
        await fetchAgents()
      } catch (e: any) {
        message.error(e?.message || t.value.agent.deleteFailed)
      }
    },
  })
}

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
  background: color-mix(in srgb, var(--primary) 10%, transparent);
  color: var(--primary);
}

.stat-icon-purple {
  background: color-mix(in srgb, #a855f7 10%, transparent);
  color: #a855f7;
}

.stat-icon-green {
  background: color-mix(in srgb, var(--success) 10%, transparent);
  color: var(--success);
}

.stat-icon-orange {
  background: color-mix(in srgb, var(--warning) 10%, transparent);
  color: var(--warning);
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
  position: relative;
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
  box-shadow: 0 4px 20px -4px color-mix(in srgb, var(--primary) 15%, transparent);
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
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
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
  background: color-mix(in srgb, var(--success) 10%, transparent);
  color: var(--success);
}

.status-dot-tag.disabled {
  background: color-mix(in srgb, var(--text-muted) 10%, transparent);
  color: var(--text-muted);
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
  background: color-mix(in srgb, var(--primary) 2%, transparent);
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


.agent-grid-section {
  display: flex;
  flex-direction: column;
  gap: 14px;
}


.batch-action-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 16px;
  background: var(--bg-card);
  border: 1px solid var(--primary);
  border-radius: 10px;
}

.batch-count {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-primary);
  margin-right: auto;
}
.card-checkbox {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 2;
  opacity: 0;
  transition: opacity 0.15s;
}

.agent-card:hover .card-checkbox,
.agent-card.card-selected .card-checkbox {
  opacity: 1;
}

.card-selected {
  border-color: var(--primary);
  box-shadow: 0 0 0 1px var(--primary);
}


.card-model-row {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: -4px;
}

.card-model-icon {
  font-size: 10px;
  color: var(--text-muted);
  flex-shrink: 0;
}

.card-model-name {
  font-size: 10px;
  color: var(--text-muted);
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}


.action-delete-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border: 1px solid var(--border-subtle);
  border-radius: 6px;
  background: var(--bg-elevated);
  color: var(--text-muted);
  cursor: pointer;
  font-size: 12px;
  transition: all 0.15s;
  flex-shrink: 0;
  padding: 0;
  opacity: 0;
}

.agent-card:hover .action-delete-btn {
  opacity: 1;
}

.action-delete-btn:hover {
  color: var(--error);
  border-color: var(--error);
  background: color-mix(in srgb, var(--error) 8%, transparent);
}
</style>
