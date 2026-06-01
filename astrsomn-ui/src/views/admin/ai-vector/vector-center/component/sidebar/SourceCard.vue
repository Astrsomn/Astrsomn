<template>
  <div
      :class="{ active: isOpen }"
      class="source-card"
      @click="$emit('toggle')"
  >
    <div class="source-icon-box">
      <img v-if="providerAvatar" :src="providerAvatar" alt="provider-avatar" class="source-avatar"/>
      <ClusterOutlined v-else class="source-icon"/>
    </div>
    <div class="source-info">
      <div class="source-header-row">
        <span class="source-title">{{ sourceName || t.vectorCenter.sourceCard.defaultName }}</span>
        <span :class="isConnected ? 'dot-connected' : 'dot-disconnected'" class="status-dot"></span>
      </div>
      <div class="source-conn-row">
        <span class="source-conn">{{ sourceType }} · {{ ip }}:{{ port }}</span>
        <LoadingOutlined v-if="isChecking" class="arrow-icon spinning"/>
        <UpOutlined v-else :class="{ rotated: isOpen }" class="arrow-icon"/>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {ClusterOutlined, LoadingOutlined, UpOutlined} from '@ant-design/icons-vue'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-vector')

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
  gap: 10px;
  padding: 10px;
  margin: 2px 0;
  border-radius: var(--radius-md);
  background: transparent;
  border: none;
  cursor: pointer;
  transition: background 0.15s;

  &:hover {
    background: var(--bg-input);
  }

  &.active {
    background: var(--bg-input);
  }

  .source-icon-box {
    width: 32px;
    height: 32px;
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
        font-size: 13px;
        font-weight: 600;
        color: var(--text-heading);
      }

      .status-dot {
        width: 8px;
        height: 8px;
        border-radius: 50%;
        flex-shrink: 0;

        &.dot-connected {
          background: #22c55e;
        }

        &.dot-disconnected {
          background: var(--text-muted, #94a3b8);
        }
      }
    }

    .source-conn-row {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .source-conn {
        font-size: 11px;
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
