<template>
  <section
      class="glass-card glass-card--interactive agent-panel"
      :class="[
      `tier-w-${gridColTier(gridW)}`,
      `tier-h-${gridRowTier(gridH)}`,
      { 'is-wide': gridW >= 4, 'is-tiny': gridW === 1 && gridH === 1 }
    ]"
      :data-w="gridW"
      :data-h="gridH"
      role="button"
      tabindex="0"
      @click="goAgentsList"
  >
    <div class="agent-panel-head">
      <div class="agent-panel-head-left">
        <div class="agent-panel-icon-wrap">
          <robot-outlined class="agent-panel-icon" />
        </div>
        <div class="agent-panel-info">
          <div class="agent-panel-title-row">
            <h2 class="agent-panel-title">智能体大脑</h2>
            <span class="status-badge">{{ runningLabel }}</span>
          </div>
          <p class="agent-panel-desc">封装复杂推理逻辑，核心编排入口。</p>
        </div>
      </div>

      <button v-if="gridW >= 2" type="button" class="btn-new-agent" @click.stop="goCreate">
        <plus-outlined />
        <span class="btn-text" v-if="gridW >= 3">新建智能体</span>
      </button>
    </div>

    <div v-if="gridH >= 2" class="agent-list" :class="{ 'grid-cols-2': gridW >= 4 }">
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
            <p class="agent-row-sub" v-if="gridW >= 2">{{ row.sub }}</p>
          </div>
        </div>

        <div class="agent-row-right" v-if="gridW >= 3">
          <div v-if="row.calls" class="agent-calls">
            <p class="agent-calls-val">{{ row.calls }}</p>
            <p class="agent-calls-label">今日</p>
          </div>
          <span class="agent-status-pill" :class="`agent-status-pill--${row.state}`">
            {{ row.stateLabel }}
          </span>
        </div>
        <div v-else class="agent-status-dot" :class="`is-${row.state}`"></div>
      </button>

      <div v-if="displayRows.length === 0" class="agent-empty">
        暂无运行中的智能体
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  RobotOutlined, PlusOutlined, CustomerServiceOutlined,
  FileTextOutlined, CodeOutlined
} from '@ant-design/icons-vue'
import { aiAgentApi, type AiAgent } from '@/api/aiAgent.ts'
import { gridColTier, gridRowTier } from '../utils/dashboardSizeTier'

const props = defineProps<{ gridW: number; gridH: number; editMode?: boolean }>()
const router = useRouter()
const agents = ref<AiAgent[]>([])

// 根据高度动态决定显示行数 (H=2显4条，H=3显6条，若宽度大则翻倍)
const pageSize = computed(() => {
  const base = props.gridH === 2 ? 4 : 6
  return props.gridW >= 4 ? base * 2 : base
})

const runningLabel = computed(() => {
  const count = agents.value.filter(a => a.status === 'enabled').length
  return props.gridW === 1 ? `${count}` : `${count}个运行中`
})

const displayRows = computed(() => {
  const icons = [CustomerServiceOutlined, FileTextOutlined, CodeOutlined]
  const tones = ['indigo', 'purple', 'slate'] as const

  return agents.value.slice(0, pageSize.value).map((a, i) => {
    const enabled = a.status === 'enabled'
    return {
      key: String(a.id || i),
      name: a.agentName || '未命名智能体',
      sub: a.description || a.modelName || 'AI智能体',
      icon: icons[i % icons.length],
      tone: tones[i % tones.length],
      state: enabled ? 'active' : 'idle',
      stateLabel: enabled ? 'Active' : 'Idle',
      calls: enabled ? '128' : null,
      muted: !enabled,
      onClick: () => id != null && router.push(`/admin/agents/model-assembly?id=${a.id}`)
    }
  })
})

onMounted(async () => {
  try {
    const resp = await aiAgentApi.queryPage({ pageNo: 1, pageSize: 20, param: {} })
    agents.value = resp.list || []
  } catch {
    agents.value = []
  }
})

const goCreate = () => !props.editMode && router.push('/admin/agents/model-assembly')
const goAgentsList = () => !props.editMode && router.push('/admin/agents')
</script>

<style scoped>
.agent-panel {
  padding: 20px;
  display: flex;
  flex-direction: column;
  height: 100%;
  box-sizing: border-box;
  gap: 16px;
  overflow: hidden;
}

/* --- 尺寸适配策略 --- */

/* 1x1: 极简图标模式 */
.is-tiny {
  padding: 0;
  align-items: center;
  justify-content: center;
}
.is-tiny .agent-panel-info,
.is-tiny .btn-new-agent,
.is-tiny .agent-list { display: none; }
.is-tiny .agent-panel-icon-wrap { width: 44px; height: 44px; margin: 0; }

/* Tier W1: 隐藏文字描述 */
.tier-w-1 .agent-panel-desc,
.tier-w-1 .btn-text { display: none; }

/* Tier H1: 隐藏列表，强化头部布局 */
.tier-h-1 .agent-panel { justify-content: center; }
.tier-h-1 .agent-list { display: none; }

/* --- 基础样式 --- */
.agent-panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.agent-panel-head-left {
  display: flex;
  gap: 12px;
  align-items: center;
  min-width: 0;
}

.agent-panel-icon-wrap {
  width: 42px;
  height: 42px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--primary), #4f46e5);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.agent-panel-title {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 800;
  color: var(--text-heading);
}

.status-badge {
  font-size: 0.7rem;
  padding: 2px 8px;
  border-radius: 99px;
  background: color-mix(in srgb, var(--primary) 10%, transparent);
  color: var(--primary);
  font-weight: 700;
}

.agent-panel-desc {
  margin: 2px 0 0;
  font-size: 0.8rem;
  color: var(--text-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.btn-new-agent {
  border: none;
  background: var(--primary);
  color: white;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.85rem;
  font-weight: 600;
}

/* --- 列表与双列布局 --- */
.agent-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow-y: auto;
}

.agent-list.grid-cols-2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  align-content: start;
}

.agent-row {
  background: color-mix(in srgb, var(--text-muted) 4%, var(--bg-card));
  border: 1px solid var(--border-subtle);
  border-radius: 10px;
  padding: 10px 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  transition: all 0.2s;
}

.agent-row:hover {
  border-color: var(--primary);
  background: var(--bg-card);
  transform: translateX(2px);
}

.agent-row-left {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.agent-avatar {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.agent-avatar--indigo { background: #eef2ff; color: #4f46e5; }
.agent-avatar--purple { background: #faf5ff; color: #9333ea; }
.agent-avatar--slate { background: #f1f5f9; color: #475569; }

.agent-row-name {
  margin: 0;
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--text-heading);
}

.agent-row-sub {
  margin: 0;
  font-size: 0.75rem;
  color: var(--text-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 120px;
}

/* 状态展示 */
.agent-status-pill {
  font-size: 0.7rem;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 700;
}

.agent-status-pill--active { background: #dcfce7; color: #15803d; }
.agent-status-pill--idle { background: #f1f5f9; color: #64748b; }

.agent-status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}
.agent-status-dot.is-active { background: #22c55e; box-shadow: 0 0 8px #22c55e; }
.agent-status-dot.is-idle { background: #cbd5e1; }

.agent-calls {
  text-align: right;
  line-height: 1;
}
.agent-calls-val { margin: 0; font-size: 0.8rem; font-weight: 700; }
.agent-calls-label { margin: 2px 0 0; font-size: 0.6rem; color: var(--text-muted); }
</style>