<template>
  <div class="status-toggle">
    <div
        class="status-toggle-slider"
        :class="{ 'right': modelValue === disabledValue, 'left': modelValue === enabledValue }"
    />
    <button
        type="button"
        class="status-btn"
        :class="{ active: modelValue === enabledValue }"
        @click="emit('update:modelValue', enabledValue)"
    >
      <span class="status-dot enabled"/>
      {{ enabledLabel }}
    </button>
    <button
        type="button"
        class="status-btn"
        :class="{ active: modelValue === disabledValue }"
        @click="emit('update:modelValue', disabledValue)"
    >
      <span class="status-dot disabled"/>
      {{ disabledLabel }}
    </button>
  </div>
</template>

<script lang="ts" setup>
const props = withDefaults(defineProps<{
  modelValue: string | number
  enabledValue?: string | number
  disabledValue?: string | number
  enabledLabel?: string
  disabledLabel?: string
}>(), {
  enabledValue: 'enabled',
  disabledValue: 'disabled',
  enabledLabel: '启用',
  disabledLabel: '禁用'
})

const emit = defineEmits<{
  'update:modelValue': [value: string | number]
}>()
</script>

<style scoped>
.status-toggle {
  display: flex;
  align-items: center;
  background: #f0f0f0;
  border-radius: 10px;
  padding: 3px;
  position: relative;
  height: 44px;
}

.status-toggle-slider {
  position: absolute;
  top: 3px;
  left: 3px;
  width: calc(50% - 3px);
  height: calc(100% - 6px);
  border-radius: 8px;
  background: #ecfdf5;
  border: 1.5px solid #0379d0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 0;
}

.status-toggle-slider.right {
  transform: translateX(100%);
  background: #fff1f2;
  border-color: #f43f5e;
}

.status-toggle-slider.left {
  transform: translateX(0);
}

.status-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 38px;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-muted);
  transition: color 0.3s ease;
  z-index: 1;
  position: relative;
  background: transparent;
  border: none;
  box-shadow: none;
  cursor: pointer;
  padding: 0;
  margin: 0;
  outline: none;
}

.status-btn:hover,
.status-btn:focus {
  background: transparent;
  border: none;
  box-shadow: none;
}

.status-btn.active {
  color: var(--text-heading);
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.status-dot.enabled {
  background: #0379d0;
}

.status-dot.disabled {
  background: #f43f5e;
}
</style>
