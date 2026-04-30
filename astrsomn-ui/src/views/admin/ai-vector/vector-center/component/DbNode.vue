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
@primary-blue: #2563eb;
@text-main: #1e293b;
@text-muted: #94a3b8;

.db-node {
  position: relative;
  padding: 10px 12px;
  margin: 4px 0;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  justify-content: space-between;
  align-items: center;

  &:hover {
    background: #f1f5f9;
  }

  &.selected {
    background: #eff6ff;

    .db-name {
      color: #1e40af;
      font-weight: 600;
    }

    .db-type-icon {
      color: #3b82f6;
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
        color: @text-muted;
      }

      .db-name {
        font-size: 13px;
        font-weight: 600;
        color: @text-main;
      }
    }

    .db-model-row {
      display: flex;
      align-items: center;
      gap: 6px;
      padding-left: 18px;

      .model-name {
        font-size: 11px;
        color: @text-muted;
      }

      .dim-divider {
        width: 3px;
        height: 3px;
        border-radius: 50%;
        background: @text-muted;
      }

      .model-dim {
        font-size: 11px;
        font-weight: 700;
        color: @primary-blue;
        letter-spacing: 0.02em;
      }
    }
  }

  .status-pulse {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: #22c55e;
    box-shadow: 0 0 0 0 rgba(34, 197, 94, 0.4);
    animation: pulse 2s infinite;
  }
}

@keyframes pulse {
  0% {
    transform: scale(0.95);
    box-shadow: 0 0 0 0 rgba(34, 197, 94, 0.7);
  }
  70% {
    transform: scale(1);
    box-shadow: 0 0 0 6px rgba(34, 197, 94, 0);
  }
  100% {
    transform: scale(0.95);
    box-shadow: 0 0 0 0 rgba(34, 197, 94, 0);
  }
}
</style>
