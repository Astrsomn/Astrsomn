<template>
  <div
    class="agent-item-premium"
    @click="$emit('click')"
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
          <button class="edit-icon-btn" title="编辑智能体" @click.stop="$emit('edit', item)">
            <EditOutlined />
          </button>
        </div>
        <div class="bottom-line">
          <span class="meta-info">{{ metaInfo }}</span>
          <span class="dot-split"></span>
          <span class="key-display">{{ item.agentKey || 'NO_KEY' }}</span>
        </div>
      </div>
    </div>

    <div class="item-visual-density">
      <div class="mini-sparkline">
        <div
          v-for="(bar, index) in sparklineData"
          :key="index"
          :class="['spark-bar', { active: bar >= 80 }]"
          :style="{ height: bar + '%', backgroundColor: getBarColor(bar) }"
        ></div>
      </div>
    </div>

    <div class="item-meta-status">
      <div :class="['status-indicator', item.status === 'enabled' ? 'is-active' : 'is-inactive']">
        {{ item.status === 'enabled' ? 'ON' : 'OFF' }}
      </div>
      <div class="status-toggle" @click.stop>
        <a-switch
          size="small"
          :checked="item.status === 'enabled'"
          :loading="loading"
          @change="(checked: boolean) => $emit('status-change', item, checked)"
        />

      </div>
      <span class="timestamp">{{ formattedTime }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { EditOutlined } from '@ant-design/icons-vue'
import type { AiAgent } from '@/api/aiAgent.ts'

const props = defineProps<{
  item: AiAgent
  loading?: boolean
}>()

defineEmits<{
  click: []
  'status-change': [item: AiAgent, checked: boolean]
  edit: [item: AiAgent]
}>()

const metaInfo = computed(() => {
  if (props.item.modelName) return props.item.modelName
  if (props.item.chatInstanceName) return props.item.chatInstanceName
  return '未配模型'
})

const formattedTime = computed(() => {
  if (!props.item.createTime) return ''
  return props.item.createTime.includes('T')
    ? props.item.createTime.split('T')[1].slice(0, 5)
    : props.item.createTime.slice(11, 16)
})

const sparklineData = computed(() => {
  return Array.from({ length: 8 }, () => Math.floor(Math.random() * 100) + 10)
})

const getBarColor = (height: number) => {
  if (height >= 90) return '#ef4444'
  if (height >= 70) return '#f97316'
  if (height >= 50) return '#eab308'
  return 'var(--border-default)'
}
</script>

<style scoped>
.agent-item-premium {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid var(--border-default);

}

.agent-item-premium:hover {
  background: var(--bg-elevated);
  border-color: color-mix(in srgb, var(--primary) 40%, var(--border-default));
}

.item-identity {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 3;
  min-width: 0;
}

.icon-container {
  position: relative;
  width: 40px;
  height: 40px;
  background: var(--bg-elevated);
  border: 1px solid var(--border-default);
  border-radius: 10px;
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

.edit-icon-btn {
  width: 18px;
  height: 18px;
  border: none;
  background: transparent;
  color: var(--text-muted);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  transition: color 0.2s;
}

.edit-icon-btn:hover {
  color: var(--primary);
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
  border-radius: 10px;
  text-transform: uppercase;
  background: var(--bg-elevated);
  color: var(--text-secondary);
}

.premium-tag.prod {
  background: rgba(82, 196, 26, 0.15);
  color: #52c41a;
}

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
  border-radius: 10px;
  transition: background-color 0.2s;
}

.spark-bar.active {
  opacity: 1;
}

.item-meta-status {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.status-indicator {
  font-size: 10px;
  font-weight: 900;
  padding: 2px 6px;
  border-radius: 10px;
}

.status-indicator.is-active {
  background: rgba(82, 196, 26, 0.12);
  color: #52c41a;
}

.status-indicator.is-inactive {
  background: color-mix(in srgb, var(--bg-elevated) 82%, var(--border-default));
  color: var(--text-muted);
}

.timestamp {
  font-size: 10px;
  color: var(--text-muted);
  font-weight: 500;
}

.status-toggle {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-text {
  font-size: 10px;
  font-weight: 700;
  color: var(--text-secondary);
}

:global(.dark) .agent-item-premium {
  background: color-mix(in srgb, var(--bg-card) 88%, #000);
}
</style>
