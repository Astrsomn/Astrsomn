<template>
  <div class="panel-card online-panel">
    <div class="panel-head">
      <h3>
        <TeamOutlined />
        在线业务系统
      </h3>
      <span>用户即业务系统，展示实时在线状态</span>
    </div>
    <div class="online-grid">
      <OnlineSystemItem
        v-for="system in pagedOnlineSystems"
        :key="system.name"
        :system="system"
      />
    </div>
    <div class="pager-wrap">
      <button type="button" class="pager-btn" :disabled="currentPage === 1" @click="emit('prev-page')">
        上一页
      </button>
      <span class="pager-text">第 {{ currentPage }} / {{ totalPages }} 页，共 {{ totalSystems }} 个系统</span>
      <button type="button" class="pager-btn" :disabled="currentPage === totalPages" @click="emit('next-page')">
        下一页
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { TeamOutlined } from '@ant-design/icons-vue'
import OnlineSystemItem from './OnlineSystemItem.vue'

type OnlineStatus = 'online' | 'degraded' | 'offline'

type OnlineSystem = {
  name: string
  status: OnlineStatus
  statusText: string
  sessions: number
  lastHeartbeat: string
  icon: any
  extraInfo?: Record<string, string>
}

defineProps<{
  pagedOnlineSystems: OnlineSystem[]
  currentPage: number
  totalPages: number
  totalSystems: number
}>()

const emit = defineEmits<{
  (e: 'prev-page'): void
  (e: 'next-page'): void
}>()
</script>

<style scoped>
.panel-card {
  border: 1px solid var(--border-default);
  border-radius: 14px;
  background: var(--bg-card);
  padding: 16px;
}

.online-panel {
  display: flex;
  flex-direction: column;
  height: calc(100% - 65px);
  overflow: hidden;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 10px;
}

.panel-head h3 {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 16px;
  color: var(--text-heading);
}

.panel-head span {
  font-size: 12px;
  color: var(--text-secondary);
}

.online-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  flex: 1;
  overflow-y: auto;
  padding-right: 4px;
  min-height: 0;
}

.pager-wrap {
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.pager-btn {
  border: 1px solid var(--border-default);
  border-radius: 8px;
  background: var(--bg-card);
  color: var(--text-primary);
  font-size: 12px;
  padding: 4px 10px;
  cursor: pointer;
}

.pager-btn:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.pager-text {
  color: var(--text-secondary);
  font-size: 12px;
}

@media (max-width: 900px) {
  .pager-wrap {
    flex-direction: column;
    align-items: stretch;
  }

  .pager-text {
    text-align: center;
  }
}
</style>
