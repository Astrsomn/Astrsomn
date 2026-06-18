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
  padding: 10px 12px;

  margin: 2px 0;
  border-radius: 8px;
  background: transparent;
  border: none;
  border-left: 3px solid transparent;
  cursor: pointer;
  transition: background 0.18s ease, border-color 0.18s ease, transform 0.18s ease, box-shadow 0.18s ease;

  &:hover {
    background: var(--bg-elevated);
    border-left-color: var(--border-default);
    transform: translateX(3px);
    box-shadow: 0 1px 3px color-mix(in srgb, var(--shadow-color, #000) 8%, transparent);
  }

  &.active {
    background: var(--bg-elevated);
    border-left-color: var(--primary);
  }

  &:active {
    transform: translateX(1px);
    transition-duration: 0.05s;
  }

  .source-icon-box {
    width: 34px;
    height: 34px;
    border-radius: 0;
    background: var(--bg-input);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    transition: background 0.18s ease, box-shadow 0.18s ease;

    .source-icon {
      font-size: 15px;
      color: var(--text-secondary);
      transition: color 0.18s ease;
    }

    .source-avatar {
      width: 22px;
      height: 22px;
      object-fit: contain;
      border-radius: 0;
    }
  }

  &:hover .source-icon-box {
    background: var(--bg-card);
    box-shadow: 0 0 0 1px var(--border-default);
  }

  &.active .source-icon-box {
    background: var(--primary-hover);

    .source-icon {
      color: var(--primary);
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
        transition: color 0.18s ease;
      }

      .status-dot {
        width: 7px;
        height: 7px;
        border-radius: 5px;
        flex-shrink: 0;
        transition: background 0.3s ease, box-shadow 0.3s ease;

        &.dot-connected {
          background: #22c55e;
          box-shadow: 0 0 4px color-mix(in srgb, #22c55e 50%, transparent);
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
