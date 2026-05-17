<template>
  <template v-if="!collapsed">
    <div class="sf-footer-row">
      <div class="sf-driver-info">
        <div class="sf-avatars">
          <template v-if="enabledExtensions.length">
            <span
                v-for="item in enabledExtensions.slice(0, 4)"
                :key="item.key"
                :title="item.name"
                class="sf-av sf-av-real"
            >
              <img v-if="item.avatar" :alt="item.name" :src="item.avatar"/>
              <span v-else>{{ item.initial }}</span>
            </span>
          </template>
          <span v-else class="sf-av">-</span>
        </div>
        <span class="sf-text">
          {{ enabledExtensions.length ? `已启用扩展 ${enabledExtensions.length}` : '暂无已启用扩展' }}
        </span>
      </div>
      <div class="sf-marketplace-btn" @click="emit('open-marketplace')">
        <AppstoreOutlined class="sf-marketplace-icon"/>
        <span>{{ marketplaceLabel }}</span>
        <span v-if="showDot" class="sf-dot"></span>
      </div>
    </div>
  </template>
  <template v-else>
    <a-tooltip placement="right">
      <template #title>{{ marketplaceLabel }}</template>
      <div class="sf-collapsed-btn" @click="emit('open-marketplace')">
        <AppstoreOutlined class="sf-collapsed-icon"/>
      </div>
    </a-tooltip>
  </template>
</template>

<script lang="ts" setup>
import {AppstoreOutlined} from '@ant-design/icons-vue'

withDefaults(defineProps<{
  enabledExtensions: Array<{ key: string; name: string; avatar: string; initial: string }>
  collapsed?: boolean
  showDot?: boolean
  marketplaceLabel?: string
}>(), {
  collapsed: false,
  showDot: false,
  marketplaceLabel: '插件市场',
})

const emit = defineEmits<{
  'open-marketplace': []
}>()
</script>

<style scoped>
.sf-footer-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sf-driver-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sf-avatars {
  display: flex;
}

.sf-av {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: var(--primary-hover);
  color: var(--primary);
  font-size: 9px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--bg-card);
  margin-right: -4px;
}

.sf-av.sf-av-real {
  overflow: hidden;
  padding: 0;
}

.sf-av.sf-av-real img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.sf-text {
  font-size: 11px;
  color: var(--text-muted);
}

.sf-marketplace-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  flex-shrink: 0;
  padding: 6px 12px;
  border-radius: var(--radius-md);
  background: var(--primary-hover);
  color: var(--primary);
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
  border: 1px solid transparent;
}

.sf-marketplace-btn:hover {
  border-color: var(--primary);
  background: var(--primary);
  color: #fff;
}

.sf-marketplace-icon {
  font-size: 13px;
}

.sf-dot {
  position: absolute;
  top: 4px;
  right: 6px;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #ff4d4f;
  box-shadow: 0 0 0 2px var(--primary-hover);
  animation: sf-dot-pulse 1.5s ease-in-out infinite;
}

@keyframes sf-dot-pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(1.3); }
}

.sf-collapsed-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  color: var(--text-muted);
  transition: all 0.2s;
}

.sf-collapsed-btn:hover {
  color: var(--primary);
  background: var(--primary-hover);
}

.sf-collapsed-icon {
  font-size: 16px;
}
</style>
