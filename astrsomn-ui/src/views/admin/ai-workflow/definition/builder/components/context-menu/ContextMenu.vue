<template>
  <teleport to="body">
    <div v-if="visible" class="ctx-mask" @mousedown="onMaskDown">
      <div :style="{ left: `${position.x}px`, top: `${position.y}px` }" class="ctx-menu" @mousedown.stop>
        <button
            v-for="item in items"
            :key="item.key"
            :class="{ danger: item.danger }"
            :disabled="item.disabled"
            class="ctx-item"
            @click="onAction(item)"
        >
          <span class="ctx-left">
            <span v-if="item.icon" class="ctx-icon">{{ item.icon }}</span>
            <span>{{ item.label }}</span>
          </span>
          <span v-if="item.children?.length" class="ctx-arrow">></span>
        </button>
      </div>
    </div>
  </teleport>
</template>

<script lang="ts" setup>
import type {ContextMenuAction, ContextMenuPosition} from './types'

defineProps<{
  visible: boolean
  position: ContextMenuPosition
  items: ContextMenuAction[]
}>()

const emit = defineEmits<{
  action: [key: string]
  close: []
}>()

const onMaskDown = () => emit('close')

const onAction = (item: ContextMenuAction) => {
  if (item.disabled || item.children?.length) return
  emit('action', item.key)
}
</script>

<style scoped>
.ctx-mask {
  position: fixed;
  inset: 0;
  z-index: 9999;
}

.ctx-menu {
  position: fixed;
  min-width: 210px;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  box-shadow: 0 12px 28px -10px rgba(15, 23, 42, 0.3);
  padding: 6px;
}

.ctx-item {
  width: 100%;
  border: 0;
  background: transparent;
  border-radius: 8px;
  padding: 8px 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #1e293b;
  cursor: pointer;
}

.ctx-item:disabled {
  color: #94a3b8;
  cursor: not-allowed;
}

.ctx-item:not(:disabled):hover {
  background: #f1f5f9;
}

.ctx-item.danger {
  color: #dc2626;
}

.ctx-left {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.ctx-icon {
  width: 16px;
  text-align: center;
  color: #64748b;
}

.ctx-arrow {
  color: #94a3b8;
  font-size: 12px;
}
</style>

