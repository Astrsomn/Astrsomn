<template>
  <div
    class="conversation-item"
    :class="{ selected: isSelected, compact }"
    @click="emit('open', item.memoryKey)"
  >
    <a-checkbox
      v-if="selectable"
      :checked="isSelected"
      @change="emit('toggle-select', item.memoryKey)"
      @click.stop
      class="select-checkbox"
    />
    <message-outlined class="item-icon" />
    <div class="title-wrap">
      <p class="title">{{ item.title || '无标题会话' }}</p>
    </div>
    <button
      v-if="deletable"
      type="button"
      class="delete-btn"
      title="删除会话"
      @click.stop="emit('delete', item)"
    >
      <delete-outlined />
    </button>
  </div>
</template>

<script setup lang="ts">
import { DeleteOutlined, MessageOutlined } from '@ant-design/icons-vue'
import type { ChatSessionItem } from './types'

defineProps<{
  item: ChatSessionItem
  isSelected: boolean
  compact?: boolean
  selectable?: boolean
  deletable?: boolean
}>()

const emit = defineEmits<{
  (e: 'open', memoryKey: string): void
  (e: 'toggle-select', memoryKey: string): void
  (e: 'delete', item: ChatSessionItem): void
}>()
</script>

<style scoped>
.conversation-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 12px;
  cursor: pointer;
  color: var(--text-secondary);
  transition: all 0.2s ease;
}

.conversation-item:hover {
  background: var(--primary-hover);
  color: var(--text-primary);
}

.conversation-item.selected {
  background: var(--primary-hover);
  color: var(--primary-light);
}

.conversation-item.compact {
  justify-content: center;
  padding: 10px 8px;
}

.select-checkbox {
  flex-shrink: 0;
}

.item-icon {
  font-size: 14px;
  color: var(--text-muted);
  flex-shrink: 0;
}

.conversation-item.selected .item-icon {
  color: var(--primary-light);
}

.title-wrap {
  flex: 1;
  min-width: 0;
}

.title {
  margin: 0;
  font-size: 13px;
  line-height: 1.35;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.delete-btn {
  width: 24px;
  height: 24px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: var(--text-muted);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  opacity: 0;
  transition: all 0.2s ease;
}

.conversation-item:hover .delete-btn {
  opacity: 1;
}

.delete-btn:hover {
  color: var(--error);
  background: rgba(239, 68, 68, 0.1);
}

.conversation-item.compact .title-wrap,
.conversation-item.compact .delete-btn {
  display: none;
}
</style>
