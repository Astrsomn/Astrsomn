<template>
  <button
    type="button"
    class="glass-card glass-card--interactive bento-span-4-1 wide-tile"
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
import { useRouter } from 'vue-router'
import { ApiOutlined } from '@ant-design/icons-vue'

const props = withDefaults(
  defineProps<{
    title: string
    description: string
    to: string
    badge?: string
    disabled?: boolean
  }>(),
  {
    badge: '已就绪',
    disabled: false,
  },
)

const router = useRouter()

const go = () => {
  if (props.disabled) return
  void router.push(props.to)
}
</script>

<style scoped>
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
</style>
