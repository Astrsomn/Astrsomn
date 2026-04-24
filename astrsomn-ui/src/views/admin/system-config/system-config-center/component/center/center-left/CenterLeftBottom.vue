<template>
  <div class="online-systems-container">
    <div class="panel-header">
      <div class="header-left">
        <TeamOutlined />
        <h2 class="panel-title">在线业务系统</h2>
      </div>
      <span class="panel-subtitle">用户即业务系统，展示实时在线状态</span>
    </div>

    <!-- 在线系统卡片网格 -->
    <div class="online-systems-grid">
      <OnlineSystemItem
        v-for="system in pagedOnlineSystems"
        :key="system.name"
        :system="system"
      />
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <button class="pagination-btn" :disabled="currentPage === 1" @click="emit('prev-page')">Prev</button>
      <span class="pagination-text">Page {{ currentPage }} / {{ totalPages }}</span>
      <button class="pagination-btn" :disabled="currentPage === totalPages" @click="emit('next-page')">Next</button>
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
.online-systems-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 300px);
  overflow: hidden;
}

.panel-header {
  padding: 24px;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-left :deep(.anticon) {
  width: 20px;
  height: 20px;
  color: #94a3b8;
}

.panel-title {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.panel-subtitle {
  font-size: 12px;
  color: #94a3b8;
  font-style: italic;
}

.online-systems-grid {
  padding: 24px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  flex: 1;
  overflow-y: auto;
}

.pagination {
  padding: 24px;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
}

.pagination-btn {
  padding: 6px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: #ffffff;
  color: #64748b;
  font-size: 12px;
  font-style: italic;
  cursor: pointer;
  transition: all 0.2s ease;
}

.pagination-btn:hover:not(:disabled) {
  background: #f8fafc;
}

.pagination-btn:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.pagination-text {
  font-size: 12px;
  color: #94a3b8;
}

@media (max-width: 768px) {
  .online-systems-grid {
    grid-template-columns: 1fr;
  }
}
</style>
