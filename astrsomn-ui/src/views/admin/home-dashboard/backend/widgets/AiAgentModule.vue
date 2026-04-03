<template>
  <section
    class="glass-card glass-card--interactive dash-mod dash-mod--ai-agent agent-panel"
    :data-col-tier="gridColTier(gridW)"
    :data-row-tier="gridRowTier(gridH)"
    role="button"
    tabindex="0"
    @click="goAgentsList"
    @keydown.enter.prevent="goAgentsList"
    @keydown.space.prevent="goAgentsList"
  >
    <div class="agent-panel-head">
      <div class="agent-panel-head-left">
        <div class="agent-panel-icon-wrap">
          <robot-outlined class="agent-panel-icon" />
        </div>
        <div>
          <div class="agent-panel-title-row">
            <h2 class="agent-panel-title">智能体大脑</h2>
            <span class="status-badge">{{ runningLabel }}</span>
          </div>
          <p class="agent-panel-desc">封装复杂推理逻辑与多模态感知能力，核心编排入口。</p>
        </div>
      </div>
      <button type="button" class="btn-new-agent" @click.stop="goCreate">
        <plus-outlined />
        新建智能体
      </button>
    </div>

    <div class="agent-list">
      <button
        v-for="row in displayRows"
        :key="row.key"
        type="button"
        class="agent-row"
        :class="{ 'agent-row--muted': row.muted }"
        @click.stop="row.onClick"
      >
        <div class="agent-row-left">
          <div class="agent-avatar" :class="`agent-avatar--${row.tone}`">
            <component :is="row.icon" class="agent-avatar-icon" />
          </div>
          <div class="agent-row-text">
            <p class="agent-row-name">{{ row.name }}</p>
            <p class="agent-row-sub">{{ row.sub }}</p>
          </div>
        </div>
        <div class="agent-row-right">
          <template v-if="row.calls != null">
            <div class="agent-calls">
              <p class="agent-calls-val">{{ row.calls }}</p>
              <p class="agent-calls-label">今日调用</p>
            </div>
          </template>
          <span class="agent-status-pill" :class="`agent-status-pill--${row.state}`">{{ row.stateLabel }}</span>
        </div>
      </button>
    </div>
  </section>
</template>

<script setup lang="ts">
import type { Component } from 'vue'
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  RobotOutlined,
  PlusOutlined,
  CustomerServiceOutlined,
  FileTextOutlined,
  CodeOutlined,
} from '@ant-design/icons-vue'
import { aiAgentApi, type AiAgent } from '@/api/aiAgent.ts'
import { gridColTier, gridRowTier } from '../dashboardSizeTier'

defineProps<{ gridW: number; gridH: number }>()

type RowTone = 'indigo' | 'purple' | 'slate'
type RowState = 'active' | 'idle'

type DisplayRow = {
  key: string
  name: string
  sub: string
  icon: Component
  tone: RowTone
  state: RowState
  stateLabel: string
  calls?: string
  muted?: boolean
  onClick: () => void
}

const router = useRouter()
const agents = ref<AiAgent[]>([])

const runningCount = computed(() => agents.value.filter((a) => a.status === 'enabled').length)

const runningLabel = computed(() =>
  agents.value.length === 0 ? '5个运行中' : `${runningCount.value}个运行中`,
)

const fallbackRows = computed<DisplayRow[]>(() => [
  {
    key: 'fb1',
    name: '客服智能体 v2',
    sub: '处理售后咨询与自动化工单',
    icon: CustomerServiceOutlined,
    tone: 'indigo',
    state: 'active',
    stateLabel: 'Active',
    calls: '12k+',
    onClick: () => void router.push('/admin/agents'),
  },
  {
    key: 'fb2',
    name: '文档分析助手',
    sub: 'PDF/Excel 数据结构化提取',
    icon: FileTextOutlined,
    tone: 'purple',
    state: 'active',
    stateLabel: 'Active',
    calls: '850',
    onClick: () => void router.push('/admin/agents'),
  },
  {
    key: 'fb3',
    name: '代码生成插件',
    sub: '自动化代码评审与补全',
    icon: CodeOutlined,
    tone: 'slate',
    state: 'idle',
    stateLabel: 'Idle',
    muted: true,
    onClick: () => void router.push('/admin/agents'),
  },
])

const displayRows = computed<DisplayRow[]>(() => {
  if (agents.value.length > 0) return mapAgents(agents.value)
  return fallbackRows.value
})

function mapAgents(list: AiAgent[]): DisplayRow[] {
  const icons = [CustomerServiceOutlined, FileTextOutlined, CodeOutlined]
  const tones: RowTone[] = ['indigo', 'purple', 'slate']
  return list.slice(0, 3).map((a, i) => {
    const id = a.id
    const enabled = a.status === 'enabled'
    return {
      key: String(id ?? a.agentKey ?? i),
      name: a.agentName || a.agentKey || '未命名智能体',
      sub: a.description || '智能体编排与推理',
      icon: icons[i % icons.length]!,
      tone: tones[i % tones.length]!,
      state: enabled ? 'active' : 'idle',
      stateLabel: enabled ? 'Active' : 'Idle',
      calls: enabled ? '—' : undefined,
      muted: !enabled,
      onClick: () => {
        if (id != null) void router.push(`/admin/agents/model-assembly?id=${id}`)
        else void router.push('/admin/agents')
      },
    }
  })
}

onMounted(async () => {
  try {
    const resp = await aiAgentApi.queryPage({
      pageNo: 1,
      pageSize: 5,
      param: {},
    })
    agents.value = resp.list || []
  } catch {
    agents.value = []
  }
})

const goCreate = () => {
  void router.push('/admin/agents/model-assembly')
}

const goAgentsList = () => {
  void router.push('/admin/agents')
}
</script>

<style scoped>
.agent-panel {
  padding: 24px;
  display: flex;
  flex-direction: column;
  min-height: 0;
  height: 100%;
  box-sizing: border-box;
  outline: none;
}

.agent-panel:focus-visible {
  box-shadow:
    0 0 0 2px var(--bg-base),
    0 0 0 4px color-mix(in srgb, var(--primary) 45%, transparent);
}

.dash-mod--ai-agent[data-col-tier='1'] .agent-panel-desc,
.dash-mod--ai-agent[data-col-tier='1'] .btn-new-agent {
  display: none;
}

.dash-mod--ai-agent[data-col-tier='1'] .agent-calls {
  display: none;
}

.dash-mod--ai-agent[data-row-tier='1'] .agent-list .agent-row:nth-child(n + 2) {
  display: none;
}

.dash-mod--ai-agent[data-row-tier='2'] .agent-list .agent-row:nth-child(n + 3) {
  display: none;
}

.dash-mod--ai-agent[data-row-tier='1'] .agent-panel-head {
  margin-bottom: 12px;
}

.agent-panel-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.agent-panel-head-left {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  min-width: 0;
}

.agent-panel-icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(
    135deg,
    color-mix(in srgb, var(--primary) 55%, #2563eb),
    color-mix(in srgb, var(--primary) 35%, #4f46e5)
  );
  color: #fff;
  box-shadow: 0 4px 12px color-mix(in srgb, var(--primary) 25%, transparent);
  flex-shrink: 0;
}

.agent-panel-icon {
  font-size: 22px;
}

.agent-panel-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 4px;
}

.agent-panel-title {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 800;
  letter-spacing: -0.02em;
  color: var(--text-heading);
}

.status-badge {
  font-size: 0.7rem;
  padding: 2px 8px;
  border-radius: 99px;
  background: color-mix(in srgb, var(--primary) 12%, transparent);
  color: color-mix(in srgb, var(--primary) 85%, #3b82f6);
  font-weight: 700;
}

.agent-panel-desc {
  margin: 0;
  font-size: 0.875rem;
  color: var(--text-muted);
  line-height: 1.5;
}

.btn-new-agent {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: none;
  border-radius: 10px;
  padding: 10px 16px;
  font-size: 0.875rem;
  font-weight: 700;
  cursor: pointer;
  color: color-mix(in srgb, var(--primary) 85%, #2563eb);
  background: color-mix(in srgb, var(--primary) 10%, var(--bg-card));
  transition: background 0.2s ease;
}

.btn-new-agent:hover {
  background: color-mix(in srgb, var(--primary) 16%, var(--bg-card));
}

.agent-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: auto;
  min-height: 0;
  overflow: auto;
}

.agent-row {
  width: 100%;
  text-align: left;
  border: 1px solid var(--border-subtle);
  border-radius: 12px;
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  cursor: pointer;
  background: color-mix(in srgb, var(--text-muted) 6%, var(--bg-card));
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease,
    border-color 0.2s ease,
    background 0.2s ease;
}

.agent-row:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px -2px rgba(0, 0, 0, 0.08);
  border-color: color-mix(in srgb, var(--primary) 28%, var(--border-subtle));
  background: var(--bg-card);
}

.agent-row--muted {
  opacity: 0.85;
}

.agent-row-left {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 0;
}

.agent-avatar {
  width: 40px;
  height: 40px;
  border-radius: 999px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.agent-avatar--indigo {
  background: linear-gradient(135deg, color-mix(in srgb, #6366f1 35%, var(--bg-card)), #e9d5ff);
  color: #4f46e5;
}

.agent-avatar--purple {
  background: linear-gradient(135deg, #fae8ff, #fce7f3);
  color: #9333ea;
}

.agent-avatar--slate {
  background: linear-gradient(135deg, #e2e8f0, #cbd5e1);
  color: #475569;
}

.agent-avatar-icon {
  font-size: 16px;
}

.agent-row-text {
  min-width: 0;
}

.agent-row-name {
  margin: 0;
  font-size: 0.875rem;
  font-weight: 700;
  color: var(--text-heading);
}

.agent-row-sub {
  margin: 4px 0 0;
  font-size: 10px;
  color: var(--text-muted);
}

.agent-row-right {
  display: flex;
  align-items: center;
  gap: 24px;
  flex-shrink: 0;
}

.agent-calls {
  text-align: right;
}

.agent-calls-val {
  margin: 0;
  font-size: 0.75rem;
  font-weight: 800;
  color: var(--text-heading);
}

.agent-calls-label {
  margin: 2px 0 0;
  font-size: 10px;
  color: var(--text-muted);
}

.agent-status-pill {
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 10px;
  font-weight: 800;
  text-transform: uppercase;
}

.agent-status-pill--active {
  background: color-mix(in srgb, var(--success) 18%, transparent);
  color: var(--success);
}

.agent-status-pill--idle {
  background: color-mix(in srgb, var(--text-muted) 14%, transparent);
  color: var(--text-muted);
}
</style>
