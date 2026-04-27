<template>
  <a-spin :spinning="loading" class="session-list-spin">
    <div class="session-list">
      <SessionListItem
        v-for="item in items"
        :key="item.memoryKey"
        :item="item"
        :is-selected="selectedKeys.includes(item.memoryKey)"
        :compact="compact"
        :selectable="selectable"
        :deletable="deletable"
        @open="(memoryKey) => emit('open', memoryKey)"
        @toggle-select="(memoryKey) => emit('toggle-select', memoryKey)"
        @delete="(sessionItem) => emit('delete', sessionItem)"
      />
      <div v-if="items.length === 0" class="empty-wrap">
        <div class="empty-box">
          <div class="empty-icon-wrap">
            <div class="empty-icon-main"></div>
            <div class="empty-icon-badge">
              <div class="empty-icon-line"></div>
            </div>
          </div>
          <p class="empty-text">暂无会话记录，点击“新会话”开始对话</p>
        </div>
      </div>
    </div>
  </a-spin>
</template>

<script setup lang="ts">
import SessionListItem from './SessionListItem.vue'
import type { ChatSessionItem } from './types'

withDefaults(defineProps<{
  loading?: boolean
  items: ChatSessionItem[]
  selectedKeys?: string[]
  compact?: boolean
  selectable?: boolean
  deletable?: boolean
}>(), {
  loading: false,
  selectedKeys: () => [],
  compact: false,
  selectable: false,
  deletable: false
})

const emit = defineEmits<{
  (e: 'open', memoryKey: string): void
  (e: 'toggle-select', memoryKey: string): void
  (e: 'delete', item: ChatSessionItem): void
}>()
</script>

<style scoped>
.session-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-height: 100%;
}

.session-list-spin {
  display: block;
  height: 100%;
}

.session-list-spin:deep(.ant-spin-container) {
  min-height: 100%;
}

.empty-wrap {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100%;
  padding: 6px;
}

.empty-box {
  width: 100%;
  padding: 32px 12px;
  border: 2px dashed #f3f4f6;
  border-radius: 16px;
  background: rgba(249, 250, 251, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.empty-icon-wrap {
  width: 64px;
  height: 64px;
  margin-bottom: 14px;
  position: relative;
  opacity: 0.45;
}

.empty-icon-main {
  width: 48px;
  height: 48px;
  background: #e5e7eb;
  border-radius: 10px;
  margin: 8px auto 0;
}

.empty-icon-badge {
  position: absolute;
  top: 0;
  right: 0;
  width: 24px;
  height: 24px;
  border-radius: 9999px;
  border: 2px solid #f3f4f6;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-icon-line {
  width: 8px;
  height: 2px;
  border-radius: 9999px;
  background: #d1d5db;
}

.empty-text {
  margin: 0;
  font-size: 12px;
  color: #9ca3af;
  line-height: 1.6;
}

.session-list:deep(.conversation-card:last-child) {
  margin-bottom: 0;
}
</style>
