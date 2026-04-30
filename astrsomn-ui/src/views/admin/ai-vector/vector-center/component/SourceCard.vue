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
        <UpOutlined class="arrow-icon" :class="{ rotated: isOpen }" />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ClusterOutlined, UpOutlined } from '@ant-design/icons-vue'

defineProps<{
  sourceName: string
  sourceType: string
  providerAvatar?: string
  ip: string
  port: string
  isConnected: boolean
  isOpen: boolean
}>()

defineEmits<{
  toggle: []
}>()
</script>

<style lang="less" scoped>
@primary-blue: #2563eb;
@text-main: #1e293b;
@text-sub: #64748b;
@text-muted: #94a3b8;

.source-card {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  margin: 4px 0;
  border-radius: 12px;
  background: #fff;
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    border-color: #e2e8f0;
    background: #f8fafc;
    transform: translateY(-1px);
  }

  &.active {
    background: #f8fafc;
    border-color: #e2e8f0;
  }

  .source-icon-box {
    width: 36px;
    height: 36px;
    border-radius: 10px;
    background: #f1f5f9;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    .source-icon {
      font-size: 14px;
      color: @text-sub;
    }

    .source-avatar {
      width: 22px;
      height: 22px;
      object-fit: contain;
      border-radius: 6px;
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
        color: @text-main;
      }

      .status-badge {
        font-size: 10px;
        font-weight: 600;
        padding: 2px 8px;
        border-radius: 6px;
        letter-spacing: 0.02em;

        &.badge-up {
          background: #ecfdf3;
          color: #16a34a;
          border: 1px solid #d1fadf;
        }

        &.badge-down {
          background: #fef2f2;
          color: #ef4444;
          border: 1px solid #fee2e2;
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
        color: @text-muted;
      }

      .arrow-icon {
        font-size: 10px;
        color: @text-muted;
        transition: transform 0.3s ease;

        &.rotated {
          transform: rotate(180deg);
        }
      }
    }
  }
}
</style>
