<template>
  <div
    class="db-node"
    :class="{ selected: isSelected, 'has-pulse': active }"
    @click="$emit('select')"
  >
    <div class="db-info">
      <div class="db-title-row">
        <NodeIndexOutlined class="db-type-icon" />
        <span class="db-name">{{ dbName }}</span>
      </div>
      <div class="db-model-row">
        <span class="model-name">{{ modelName }}</span>
        <span class="dim-divider"></span>
        <span class="model-dim">{{ dim }}Dim</span>
      </div>
    </div>
    <div v-if="active" class="status-pulse"></div>
  </div>
</template>

<script lang="ts" setup>
import { NodeIndexOutlined } from '@ant-design/icons-vue'

defineProps<{
  dbName: string
  modelName: string
  dim: number
  active: boolean
  isSelected: boolean
}>()

defineEmits<{
  select: []
}>()
</script>

<style lang="less" scoped>
.db-node {
  position: relative;
  padding: 10px 12px;
  margin: 4px 0;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  justify-content: space-between;
  align-items: center;

  &:hover {
    background: var(--bg-input);
  }

  &.selected {
    background: var(--primary-hover);

    .db-name {
      color: var(--primary);
      font-weight: 600;
    }

    .db-type-icon {
      color: var(--primary);
    }
  }

  .db-info {
    .db-title-row {
      display: flex;
      align-items: center;
      gap: 6px;
      margin-bottom: 4px;

      .db-type-icon {
        font-size: 12px;
        color: var(--text-muted);
      }

      .db-name {
        font-size: 13px;
        font-weight: 600;
        color: var(--text-primary);
      }
    }

    .db-model-row {
      display: flex;
      align-items: center;
      gap: 6px;
      padding-left: 18px;

      .model-name {
        font-size: 11px;
        color: var(--text-muted);
      }

      .dim-divider {
        width: 3px;
        height: 3px;
        border-radius: 50%;
        background: var(--text-muted);
      }

      .model-dim {
        font-size: 11px;
        font-weight: 700;
        color: var(--primary);
        letter-spacing: 0.02em;
      }
    }
  }

  .status-pulse {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: var(--success);
    box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.4);
    animation: pulse 2s infinite;
  }
}

@keyframes pulse {
  0% {
    transform: scale(0.95);
    box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7);
  }
  70% {
    transform: scale(1);
    box-shadow: 0 0 0 6px rgba(16, 185, 129, 0);
  }
  100% {
    transform: scale(0.95);
    box-shadow: 0 0 0 0 rgba(16, 185, 129, 0);
  }
}
</style>
