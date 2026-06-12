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
  background: #e9ecef; /* 调整为稍深的灰色，作为底色 */
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
  background: #007bff; /* 启用状态（左侧）：高饱和度的蓝色滑块，极其醒目 */
  /* border: 1.5px solid #007bff; */ /* 移除边框，改用纯色滑块以增强对比 */
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 0;
}

.status-toggle-slider.right {
  transform: translateX(100%);
  background: #d6dadc; /* 禁用状态（右侧）：浅灰色滑块，代表“关闭” */
  /* border-color: #64748b; */
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
  color: #495057; /* 未激活状态的文字颜色 */
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

/* 关键对比度修改：启用和禁用状态下的文字颜色 */
.status-btn.active.left ~ .status-toggle-slider { /* 选中左侧（启用）时的特殊处理 */
}
.status-btn.active {
}

/* 根据滑块位置反转文字颜色 */
.status-btn:nth-of-type(1).active {
    color: #ffffff; /* 启用状态：滑块为蓝色，文字改为纯白以增强对比 */
}

.status-btn:nth-of-type(2).active {
    color: #000000; /* 禁用状态：滑块为浅灰色，文字改为纯黑 */
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

/* 圆点颜色配合滑块颜色 */
.status-btn.active:nth-of-type(1) .status-dot.enabled {
    background: #ffffff; /* 启用圆点：在蓝色背景上显示为白色 */
}
.status-btn:nth-of-type(1):not(.active) .status-dot.enabled {
    background: #007bff; /* 未启用时圆点：保持蓝色 */
}

.status-btn.active:nth-of-type(2) .status-dot.disabled {
    background: #000000; /* 禁用圆点：在浅灰色背景上显示为黑色 */
}
.status-btn:nth-of-type(2):not(.active) .status-dot.disabled {
    background: #64748b; /* 未禁用时圆点：深灰蓝 */
}
</style>