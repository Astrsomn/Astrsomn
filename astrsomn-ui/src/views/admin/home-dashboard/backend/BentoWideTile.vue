<template>
  <button
    type="button"
    class="glass-card glass-card--interactive wide-tile"
    :class="
      embedded
        ? ['wide-tile--embedded', 'wide-tile--narrow']
        : [wideSpanClass, { 'wide-tile--narrow': gridSpan === 3 }]
    "
    :disabled="disabled"
    @click="go"
  >
    <div class="wide-tile-icon">
      <api-outlined class="wide-tile-glyph" />
    </div>
    <div class="wide-tile-text">
      <h3 class="wide-tile-title">{{ title }}</h3>
      <p class="wide-tile-desc">{{ description }}</p>
    </div>
    <span class="wide-tile-badge">{{ badge }}</span>
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ApiOutlined } from '@ant-design/icons-vue'

const props = withDefaults(
  defineProps<{
    title: string
    description: string
    to: string
    badge?: string
    disabled?: boolean
    /** 3 / 4 / 8 / 12 列；3 为窄竖卡（与同排小格对齐） */
    gridSpan?: 3 | 4 | 8 | 12
    /** 嵌入 GridLayout 内时不挂 bento-span-*，避免污染外层 12 列栅格 */
    embedded?: boolean
  }>(),
  {
    badge: '已就绪',
    disabled: false,
    gridSpan: 4,
    embedded: false,
  },
)

const wideSpanClass = computed(() => {
  if (props.gridSpan === 12) return 'bento-span-12-1'
  if (props.gridSpan === 8) return 'bento-span-8-1'
  if (props.gridSpan === 3) return 'bento-span-3-1'
  return 'bento-span-4-1'
})

const router = useRouter()

const go = () => {
  if (props.disabled) return
  void router.push(props.to)
}
</script>

<style scoped>
.wide-tile--embedded {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
}

.wide-tile {
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  border: none;
  font: inherit;
  color: inherit;
  text-align: left;
}

.wide-tile:disabled {
  cursor: not-allowed;
  opacity: 0.55;
}

.wide-tile-icon {
  width: 48px;
  height: 48px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: linear-gradient(135deg, #10b981, #0d9488);
  color: #fff;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.35);
}

.wide-tile-glyph {
  font-size: 22px;
}

.wide-tile-text {
  flex: 1;
  min-width: 0;
}

.wide-tile-title {
  margin: 0 0 4px;
  font-size: 0.875rem;
  font-weight: 800;
  color: var(--text-heading);
}

.wide-tile-desc {
  margin: 0;
  font-size: 11px;
  color: var(--text-muted);
  line-height: 1.45;
}

.wide-tile-badge {
  flex-shrink: 0;
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 10px;
  font-weight: 800;
  background: color-mix(in srgb, var(--success) 14%, transparent);
  color: var(--success);
  border: 1px solid color-mix(in srgb, var(--success) 22%, transparent);
}

.wide-tile--narrow {
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 16px 10px;
  gap: 10px;
}

.wide-tile--narrow .wide-tile-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
}

.wide-tile--narrow .wide-tile-glyph {
  font-size: 18px;
}

.wide-tile--narrow .wide-tile-text {
  width: 100%;
}

.wide-tile--narrow .wide-tile-title {
  font-size: 0.8125rem;
  margin-bottom: 2px;
}

.wide-tile--narrow .wide-tile-desc {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  font-size: 0.65rem;
  line-height: 1.35;
}
</style>
