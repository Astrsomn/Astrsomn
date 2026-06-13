<template>
  <div
      :class="{ selected: isSelected }"
      class="db-node"
      @click="$emit('select')"
  >
    <NodeIndexOutlined class="db-type-icon"/>
    <span class="db-name">{{ dbName }}</span>
  </div>
</template>

<script lang="ts" setup>
import {NodeIndexOutlined} from '@ant-design/icons-vue'

defineProps<{
  dbName: string
  isSelected: boolean
}>()

defineEmits<{
  select: []
}>()
</script>

<style lang="less" scoped>
.db-node {
  position: relative;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  margin: 2px 0;
  border-radius: 0;
  cursor: pointer;
  transition: all 0.18s ease;
  background: transparent;
  border-left: 2px solid transparent;

  &:hover {
    background: var(--bg-elevated);
    border-left-color: var(--border-default);
    transform: translateX(2px);
  }

  &.selected {
    background: var(--primary-hover);
    border-left-color: var(--primary);

    .db-name {
      color: var(--primary);
      font-weight: 600;
    }

    .db-type-icon {
      color: var(--primary);
    }
  }

  .db-type-icon {
    font-size: 12px;
    color: var(--text-muted);
    transition: color 0.18s ease;
    flex-shrink: 0;
  }

  .db-name {
    font-size: 12px;
    font-weight: 600;
    color: var(--text-primary);
    transition: color 0.18s ease;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
