<template>
  <div
    class="source-card"
    :class="{ active: isOpen }"
    @click="$emit('toggle')"
  >
    <div class="source-icon-box">
      <img v-if="providerAvatar" :src="providerAvatar" alt="provider-avatar" class="source-avatar" />
      <ClusterOutlined v-else class="source-icon" />
    </div>
    <div class="source-info">
      <div class="source-header-row">
        <span class="source-title">{{ sourceName || '数据源' }}</span>
        <span class="status-badge" :class="isConnected ? 'badge-up' : 'badge-down'">
          {{ isConnected ? '已连接' : '未连接' }}
        </span>
      </div>
      <div class="source-conn-row">
        <span class="source-conn">{{ sourceType }} · {{ ip }}:{{ port }}</span>
        <LoadingOutlined v-if="isChecking" class="arrow-icon spinning" />
        <UpOutlined v-else class="arrow-icon" :class="{ rotated: isOpen }" />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ClusterOutlined, LoadingOutlined, UpOutlined } from '@ant-design/icons-vue'

defineProps<{
  sourceName: string
  sourceType: string
  providerAvatar?: string
  ip: string
  port: string
  isConnected: boolean
  isOpen: boolean
  isChecking?: boolean
}>()

defineEmits<{
  toggle: []
}>()
</script>

<style lang="less" scoped>
.source-card {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  margin: 4px 0;
  border-radius: var(--radius-md);
  background: var(--bg-card);
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    border-color: var(--border-default);
    background: var(--bg-input);
    transform: translateY(-1px);
  }

  &.active {
    background: var(--bg-input);
    border-color: var(--border-default);
  }

  .source-icon-box {
    width: 36px;
    height: 36px;
    border-radius: var(--radius-md);
    background: var(--bg-input);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    .source-icon {
      font-size: 14px;
      color: var(--text-secondary);
    }

    .source-avatar {
      width: 22px;
      height: 22px;
      object-fit: contain;
      border-radius: var(--radius-lg);
    }
  }

  .source-info {
    flex: 1;
    min-width: 0;

    .source-header-row {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 4px;

      .source-title {
        font-size: 14px;
        font-weight: 700;
        color: var(--text-heading);
      }

      .status-badge {
        font-size: 10px;
        font-weight: 600;
        padding: 2px 8px;
        border-radius: var(--radius-sm);
        letter-spacing: 0.02em;

        &.badge-up {
          background: rgba(16, 185, 129, 0.1);
          color: var(--success);
          border: 1px solid rgba(16, 185, 129, 0.2);
        }

        &.badge-down {
          background: rgba(239, 68, 68, 0.1);
          color: var(--error);
          border: 1px solid rgba(239, 68, 68, 0.2);
        }
      }
    }

    .source-conn-row {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .source-conn {
        font-size: 12px;
        font-family: 'SF Mono', Consolas, monospace;
        color: var(--text-muted);
      }

      .arrow-icon {
        font-size: 10px;
        color: var(--text-muted);
        transition: transform 0.3s ease;

        &.rotated {
          transform: rotate(180deg);
        }

        &.spinning {
          animation: icon-spin 0.9s linear infinite;
        }
      }
    }
  }
}

@keyframes icon-spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
