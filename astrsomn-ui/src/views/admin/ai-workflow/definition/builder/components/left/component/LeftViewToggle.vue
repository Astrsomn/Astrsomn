<template>
  <div class="left-view-toggle">
    <button
      v-for="item in viewModes"
      :key="item.key"
      type="button"
      class="toggle-btn"
      :class="{ active: item.key === modelValue }"
      @click="$emit('update:modelValue', item.key)"
    >
      {{ item.label }}
    </button>
  </div>
</template>

<script setup lang="ts">
import type { LeftViewMode } from '../../../domain/types.ts'

defineProps<{
  modelValue: LeftViewMode
}>()

defineEmits<{
  'update:modelValue': [value: LeftViewMode]
}>()

const viewModes: Array<{ key: LeftViewMode; label: string }> = [
  { key: 'workflow-list', label: '流程列表' },
  { key: 'model-chat', label: '模型对话' }
]
</script>

<style scoped>
.left-view-toggle {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}

.toggle-btn {
  border: 1px solid #d9e1ec;
  border-radius: 8px;
  background: #fff;
  height: 34px;
  font-size: 13px;
  color: #334155;
  cursor: pointer;
  transition: all 0.2s;
}

.toggle-btn.active {
  border-color: #1677ff;
  color: #1677ff;
  background: #f0f6ff;
}
</style>
